/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.accounts.internal;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.validation.Valid;

import org.mayocat.accounts.AccountsService;
import org.mayocat.accounts.AccountsSettings;
import org.mayocat.accounts.IncompatibleConnectedUserException;
import org.mayocat.accounts.NoSuchPasswordResetKeyException;
import org.mayocat.accounts.NoSuchValidationKeyException;
import org.mayocat.accounts.PasswordDoesNotMeetRequirementsException;
import org.mayocat.accounts.UserAlreadyValidatedException;
import org.mayocat.accounts.UserDataSupplier;
import org.mayocat.accounts.UserNotFoundException;
import org.mayocat.accounts.WrongPasswordException;
import org.mayocat.accounts.model.Role;
import org.mayocat.accounts.model.Tenant;
import org.mayocat.accounts.model.TenantConfiguration;
import org.mayocat.accounts.model.User;
import org.mayocat.accounts.passwords.PasswordStrengthChecker;
import org.mayocat.accounts.store.TenantStore;
import org.mayocat.accounts.store.UserStore;
import org.mayocat.configuration.ConfigurationService;
import org.mayocat.configuration.MultitenancySettings;
import org.mayocat.configuration.SiteSettings;
import org.mayocat.configuration.general.GeneralSettings;
import org.mayocat.context.WebContext;
import org.mayocat.mail.MailException;
import org.mayocat.mail.MailTemplate;
import org.mayocat.mail.MailTemplateService;
import org.mayocat.security.PasswordManager;
import org.mayocat.shop.front.WebDataSupplier;
import org.mayocat.store.EntityAlreadyExistsException;
import org.mayocat.store.EntityDoesNotExistException;
import org.mayocat.store.InvalidEntityException;
import org.mayocat.url.URLHelper;
import org.mayocat.views.Template;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.io.BaseEncoding;

import groovy.text.SimpleTemplateEngine;

@Component
public class DefaultAccountsService implements AccountsService
{
    //
    // Injections
    // -----------------------------------------------------------------------------------------------------------------

    @Inject
    private Provider<TenantStore> tenantStore;

    @Inject
    private Provider<UserStore> userStore;

    @Inject
    private PasswordManager passwordManager;

    @Inject
    private PasswordStrengthChecker passwordStrengthChecker;

    @Inject
    private MultitenancySettings multitenancySettings;

    @Inject
    private MailTemplateService mailTemplateService;

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private GeneralSettings generalSettings;

    @Inject
    private SiteSettings siteSettings;

    @Inject
    private URLHelper urlHelper;

    @Inject
    private Map<String, UserDataSupplier> userDataSuppliers;

    @Inject
    private Logger logger;

    @Inject
    private WebContext context;

    //
    // Implemented methods
    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public void createInitialUser(User user) throws EntityAlreadyExistsException, InvalidEntityException,
            PasswordDoesNotMeetRequirementsException
    {
        if (this.hasUsers()) {
            throw new RuntimeException("Illegal attempt at create the initial user");
        }

        // Initial user does not have to validate, whatever the settings
        user.setActive(true);

        this.create(user, Role.ADMIN, Maps.<String, Object>newHashMap());
    }

    @Override
    public void createUser(@Valid User user) throws EntityAlreadyExistsException, InvalidEntityException,
            PasswordDoesNotMeetRequirementsException
    {
        this.create(user, Role.NONE, Maps.<String, Object>newHashMap());
    }

    @Override
    public void createUser(@Valid User user, Map<String, Object> additionalContext)
            throws EntityAlreadyExistsException, InvalidEntityException, PasswordDoesNotMeetRequirementsException
    {
        this.create(user, Role.NONE, additionalContext);
    }

    @Override
    public Tenant findTenant(String slug)
    {
        return this.tenantStore.get().findBySlug(slug);
    }

    @Override
    public Tenant findTenantByDefaultHost(String host)
    {
        return this.tenantStore.get().findByDefaultHost(host);
    }

    @Override
    public Tenant createDefaultTenant() throws EntityAlreadyExistsException
    {
        if (this.tenantStore.get().findAll(1, 0).size() != 0) {
            throw new EntityAlreadyExistsException("Cannot create default tenant : a tenant already exists");
        }
        String slug = multitenancySettings.getDefaultTenantSlug();
        TenantConfiguration configuration = new TenantConfiguration();
        Tenant tenant = new Tenant(slug, configuration);
        try {
            this.tenantStore.get().create(tenant);
        } catch (InvalidEntityException e) {
        }
        return this.tenantStore.get().findBySlug(slug);
    }

    @Override
    public void createTenant(@Valid Tenant tenant) throws EntityAlreadyExistsException, InvalidEntityException
    {
        this.tenantStore.get().create(tenant);
    }

    @Override
    public void updateTenant(@Valid Tenant tenant) throws EntityDoesNotExistException, InvalidEntityException
    {
        this.tenantStore.get().update(tenant);
    }

    @Override
    public List<Tenant> findAllTenants(Integer limit, Integer offset)
    {
        return this.tenantStore.get().findAll(limit, offset);
    }

    @Override
    public Integer countAllTenants()
    {
        return this.tenantStore.get().countAll();
    }

    @Override
    public boolean hasUsers()
    {
        return this.userStore.get().findAll(1, 0).size() > 0;
    }

    @Override
    public User findUserByEmailOrUserName(String userNameOrEmail)
    {
        return this.userStore.get().findUserByEmailOrUserName(userNameOrEmail);
    }

    @Override
    public List<Role> findRolesForUser(User user)
    {
        return this.userStore.get().findRolesForUser(user);
    }

    @Override
    public User validateAccount(String validationKey)
            throws NoSuchValidationKeyException, IncompatibleConnectedUserException, UserAlreadyValidatedException
    {
        User user = this.userStore.get().findByValidationKey(validationKey);

        if (user == null) {
            throw new NoSuchValidationKeyException("Validation key does not exist");
        }

        if (context.getUser() != null && !context.getUser().getId().equals(user.getId())) {
            throw new IncompatibleConnectedUserException("Refusing to validate user not matching connected user");
        }

        if (user.isActive()) {
            throw new UserAlreadyValidatedException("User already validated");
        }

        user.setActive(true);

        try {
            this.userStore.get().update(user);
        } catch (InvalidEntityException | EntityDoesNotExistException e) {
            this.logger.error("Failed to validate user", e);
            throw new RuntimeException(e);
        }

        return user;
    }

    @Override
    public void changePassword(User user, String currentPassword, String newPassword) throws WrongPasswordException,
            PasswordDoesNotMeetRequirementsException
    {
        Preconditions.checkNotNull(user);
        Preconditions.checkNotNull(user.getId());

        User stored = this.userStore.get().findById(user.getId());

        if (!this.passwordManager.verifyPassword(currentPassword, stored.getPassword())) {
            throw new WrongPasswordException("Refusing to change password : given current password is incorrect");
        }

        if (!passwordStrengthChecker.checkLength(newPassword)) {
            throw new PasswordDoesNotMeetRequirementsException(
                    "Provided password does not meet requirements : length too short");
        }

        if (!passwordStrengthChecker.checkEntropy(newPassword)) {
            throw new PasswordDoesNotMeetRequirementsException(
                    "Provided password does not meet requirements : not enough bits of entropy");
        }

        this.userStore.get().updatePassword(user, passwordManager.hashPassword(newPassword));
    }

    @Override
    public void createPasswordResetRequest(String emailOrUsername) throws UserNotFoundException
    {
        Preconditions.checkNotNull(emailOrUsername);

        User user = this.userStore.get().findUserByEmailOrUserName(emailOrUsername);

        if (user == null) {
            throw new UserNotFoundException("No user found with this email or usernmae");
        }

        AccountsSettings settings = getSettings();
        final String secret = generateSecret();
        this.userStore.get().createPasswordResetRequest(user, secret);

        sendPasswordResetMail(user, secret, settings);
    }

    @Override
    public void resetPassword(String resetKey, String password)
            throws NoSuchPasswordResetKeyException, PasswordDoesNotMeetRequirementsException
    {
        Preconditions.checkNotNull(resetKey);
        Preconditions.checkNotNull(password);

        User user = this.userStore.get().findUserByPasswordResetRequest(resetKey);

        if (user == null) {
            throw new NoSuchPasswordResetKeyException();
        }

        AccountsSettings settings = getSettings();
        if (password.length() < settings.getPasswordRequirements().getMinimalLength()) {
            throw new PasswordDoesNotMeetRequirementsException("Provided password does not meet requirements");
        }

        this.userStore.get().updatePassword(user, passwordManager.hashPassword(password));
        this.userStore.get().deletePasswordResetRequest(resetKey);
    }

    //
    // Private helpers
    // -----------------------------------------------------------------------------------------------------------------

    private void create(User user, Role initialRole, Map<String, Object> additionalContext)
            throws InvalidEntityException, EntityAlreadyExistsException, PasswordDoesNotMeetRequirementsException
    {
        AccountsSettings settings = getSettings();
        Boolean validationIsRequired = settings.getUserValidation().getValue();

        if (user.getPassword().length() < settings.getPasswordRequirements().getMinimalLength()) {
            throw new PasswordDoesNotMeetRequirementsException("Provided password does not meet requirements");
        }

        if (validationIsRequired) {
            if (context.getTenant() != null) {
                throw new RuntimeException("Validation is not supported for local users");
            }

            user.setActive(false);
            user.setValidationKey(generateSecret());
        } else {
            user.setActive(true);
        }

        user.setPassword(this.passwordManager.hashPassword(user.getPassword()));
        final User createdUser = this.userStore.get().create(user, initialRole);

        if (validationIsRequired) {
            sendValidationMail(createdUser, settings, additionalContext);
        }
    }

    private void sendValidationMail(final User createdUser, AccountsSettings settings,
            Map<String, Object> additionalContext)
    {
        MailTemplate mailTemplate = new MailTemplate().template("account-validation").to(createdUser.getEmail())
                .from(generalSettings.getNotificationsEmail());

        try {
            Map<String, Object> context = Maps.newHashMap();
            context.putAll(additionalContext);
            String validationUriTemplate;
            if (!Strings.isNullOrEmpty(settings.getUserValidationUriTemplate().getValue())) {
                validationUriTemplate = settings.getUserValidationUriTemplate().getValue();
            } else {
                validationUriTemplate =
                        urlHelper.getContextWebURL("/account/validation/${validationKey}").toString();
            }
            SimpleTemplateEngine templateEngine = new SimpleTemplateEngine();
            groovy.text.Template uriTemplate = templateEngine.createTemplate(validationUriTemplate);
            context.put("validationLink", uriTemplate.make(new HashMap()
            {
                {
                    put("validationKey", createdUser.getValidationKey());
                }
            }).toString());
            context.put("siteName", siteSettings.getName());

            mailTemplateService.sendTemplateMail(mailTemplate, context);
        } catch (MailException | ClassNotFoundException | IOException e) {
            logger.error("Failed to send validation email", e);
        }
    }

    private void sendPasswordResetMail(User user, final String secret, AccountsSettings settings)
    {
        MailTemplate mailTemplate = new MailTemplate().template("password-reset").to(user.getEmail())
                .from(generalSettings.getNotificationsEmail());

        try {
            Map<String, Object> context = Maps.newHashMap();
            for (UserDataSupplier supplier : userDataSuppliers.values()) {
                supplier.supply(user, context);
            }
            String passwordResetUriLink;
            if (!Strings.isNullOrEmpty(settings.getUserPasswordResetUriTemplate().getValue())) {
                passwordResetUriLink = settings.getUserPasswordResetUriTemplate().getValue();
            } else {
                passwordResetUriLink = urlHelper.getContextWebURL("/login/reset-password/${resetKey}").toString();
            }
            SimpleTemplateEngine templateEngine = new SimpleTemplateEngine();
            groovy.text.Template uriTemplate = templateEngine.createTemplate(passwordResetUriLink);
            context.put("resetLink", uriTemplate.make(new HashMap()
            {
                {
                    put("resetKey", secret);
                }
            }).toString());
            context.put("siteName", siteSettings.getName());

            mailTemplateService.sendTemplateMail(mailTemplate, context);
        } catch (MailException | ClassNotFoundException | IOException e) {
            logger.error("Failed to send validation email", e);
        }
    }

    /**
     * Generates a secret string. Used typically as a key for password resets, for account validation.
     *
     * @return the generated key;
     */
    private String generateSecret()
    {
        byte[] buffer = new byte[32];
        new SecureRandom().nextBytes(buffer);
        return BaseEncoding.base32().omitPadding().lowerCase().encode(buffer);
    }

    private AccountsSettings getSettings()
    {
        return configurationService.getSettings(AccountsSettings.class);
    }
}
