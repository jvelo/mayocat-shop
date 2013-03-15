package org.mayocat.rest.resources;

import javax.ws.rs.core.MediaType;

import junit.framework.Assert;

import org.eclipse.jetty.util.B64Code;
import org.junit.Before;
import org.mayocat.rest.api.v1.resources.UserResource;
import org.mayocat.accounts.AccountsService;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;

public abstract class AbstractAuthenticatedResourceTest extends AbstractResourceTest
{
    private UserResource userResource;
    
    private AccountsService accountsService;

    protected static final String SLUG = "jerome";
    
    protected static final String EMAIL = "jerome@mayocat.org";

    protected static final String PASSWORD = "password";

    public AbstractAuthenticatedResourceTest()
    {
        super();
    }

    @Before
    public void setUpUser() throws Exception
    {
        this.accountsService = this.componentManager.getInstance(AccountsService.class);
        if (!this.accountsService.hasUsers()) {
            ClientResponse cr = client().resource("/user/")
                .type(MediaType.APPLICATION_JSON)
                .entity("{\"slug\":\"" + SLUG + "\", \"email\":\"" + EMAIL + "\", \"password\" : \"" + PASSWORD + "\"}")
                .post(ClientResponse.class);

            Assert.assertEquals(Status.OK, cr.getClientResponseStatus());
        }        
    }
    
    @Override
    protected void setUpResources() throws Exception
    {
        super.setUpResources();
        
        this.userResource = this.componentManager.getInstance(Resource.class, "UserResource");
        addResource(this.userResource);

    }

    protected String getBasicAuthenticationHeader()
    {
        return "Basic " + B64Code.encode(EMAIL + ":" + PASSWORD);
    }
}
