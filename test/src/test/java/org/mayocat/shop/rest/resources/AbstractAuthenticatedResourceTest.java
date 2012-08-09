package org.mayocat.shop.rest.resources;

import javax.ws.rs.core.MediaType;

import junit.framework.Assert;

import org.eclipse.jetty.util.B64Code;
import org.junit.Before;
import org.mayocat.shop.service.UserService;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;

public abstract class AbstractAuthenticatedResourceTest extends AbstractResourceTest
{
    private UserResource userResource;
    
    private UserService userService;

    protected static final String USER_NAME = "jerome@mayocat.org";

    protected static final String PASSWORD = "password";

    public AbstractAuthenticatedResourceTest()
    {
        super();
    }

    @Before
    public void setUpUser() throws Exception
    {
        this.userService = this.componentManager.getInstance(UserService.class);
        if (!this.userService.hasUsers()) {
            ClientResponse cr = client().resource("/user/")
                .type(MediaType.APPLICATION_JSON)
                .entity("{\"email\":\"" + USER_NAME + "\", \"password\" : \"" + PASSWORD + "\"}")
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
        return "Basic " + B64Code.encode(USER_NAME + ":" + PASSWORD);
    }
}
