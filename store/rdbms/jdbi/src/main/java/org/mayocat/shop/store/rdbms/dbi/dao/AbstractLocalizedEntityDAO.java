package org.mayocat.shop.store.rdbms.dbi.dao;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.mayocat.shop.model.LocalizedEntity;
import org.mayocat.shop.model.Tenant;
import org.mayocat.shop.model.Translations;
import org.mayocat.shop.store.rdbms.dbi.jointype.EntityFullJoinRow;
import org.mayocat.shop.store.rdbms.dbi.mapper.EntityFullJoinRowMapper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.Define;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import com.google.common.base.Optional;
import com.yammer.dropwizard.util.Generics;

public abstract class AbstractLocalizedEntityDAO<E extends LocalizedEntity> implements TranslationDAO,
    EntityDAO<E>
{

    public void insertTranslations(Long entityId, Translations translations)
    {
        if (translations == null) {
            return;
        }
        List<Long> ids = new ArrayList<Long>();
        List<String> languages = new ArrayList<String>();
        List<String> texts = new ArrayList<String>();

        for (String field : translations.keySet()) {
            Map<Locale, String> fieldTranslations = translations.get(field);
            Long id = createTranslation(entityId, field);
            for (Locale locale : fieldTranslations.keySet()) {
                ids.add(id);
                languages.add(locale.toString());
                texts.add(fieldTranslations.get(locale));
            }
        }

        if (ids.size() > 0) {
            // FIXME check in the entity class the size of the translation
            insertTranslations("small", ids, languages, texts);
        }
    }

    public E findBySlugWithTranslations(String type, String slug, Tenant tenant) {
        List<EntityFullJoinRow> rows = this.findBySlugWithTranslationsRows(type, slug, tenant);

        E entity = null;
        Class< E > thisEntityType = Generics.getTypeParameter(getClass(), LocalizedEntity.class);
        Translations translations = new Translations();
        for (EntityFullJoinRow row : rows) {
            if (entity == null) {
                entity = this.getEntity(row.getEntityData(), thisEntityType);
            }
            String field = row.getField();
            if (field != null) {
                if (!translations.containsKey(field)) {
                    translations.put(field, new HashMap<Locale, String>());
                }
                Map<Locale, String> fieldTranslations = translations.get(field);
                fieldTranslations.put(row.getLocale(), row.getText());
            }
        }
        if (entity != null) {
            entity.setTranslations(translations);
        }
        return entity;
    }

    private E getEntity(Map<String, Object> entityData, Class< ? > type)
    {
        E entity;
        try {
            entity = (E) type.newInstance();
            String entityType = type.getSimpleName().toLowerCase();
            // TODO we will likely need to support custom table name mapping via annotation in the future


            for (Method method : entity.getClass().getMethods()) {
                if (method.getName().startsWith("set") && !method.getName().equals("setTranslations")
                    && Character.isUpperCase(method.getName().charAt(3))) {
                    // Found a setter.
                    String field = method.getName().substring(3);

                    Object value = Optional.absent();
                    if (entityData.containsKey("entity." + field)) {
                        value = entityData.get("entity." + field);
                    }
                    else if (entityData.containsKey(entityType + "." + field)) {
                        value = entityData.get(entityType + "." + field);
                    }

                    boolean setterAccessible = method.isAccessible();
                    method.setAccessible(true);
                    method.invoke(entity, value);
                    method.setAccessible(setterAccessible);
                }
            }
            return entity;

        } catch (InstantiationException e) {
            throw new IllegalArgumentException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        } catch (SecurityException e) {
            throw new IllegalArgumentException(e);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e);
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    @RegisterMapper(EntityFullJoinRowMapper.class)
    @SqlQuery
    (
        "SELECT * " 
      + "FROM   entity " 
      + "       INNER JOIN <type> " 
      + "               ON entity.id = <type>.entity_id " 
      + "       LEFT JOIN translation "
      + "              ON translation.entity_id = entity.id " 
      + "       LEFT JOIN translation_small "
      + "              ON translation_small.translation_id = translation.id " 
      + "       LEFT JOIN translation_medium "
      + "              ON translation_medium.translation_id = translation.id " 
      + "WHERE  entity.slug = :slug " 
      + "       AND entity.type = '<type>' " 
      + "       AND entity.tenant_id = :tenant.id "
    )
    abstract List<EntityFullJoinRow> findBySlugWithTranslationsRows(@Define("type") String type, @Bind("slug") String slug, @BindBean("tenant") Tenant tenant);
}
