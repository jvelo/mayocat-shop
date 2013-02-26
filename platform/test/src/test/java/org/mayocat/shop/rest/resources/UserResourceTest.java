package org.mayocat.shop.rest.resources;

import javax.ws.rs.core.MediaType;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mayocat.accounts.model.User;
import org.mayocat.shop.rest.api.v1.resources.UserResource;
import org.mayocat.shop.rest.api.v1.resources.UserResource;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.yammer.dropwizard.validation.InvalidEntityException;

public class UserResourceTest extends AbstractAuthenticatedResourceTest
{
    private UserResource userResource;

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    @Override
    protected void setUpResources() throws Exception
    {
        super.setUpResources();

        this.userResource = this.componentManager.getInstance(Resource.class, "UserResource");
        addResource(this.userResource);
    }

    @Test
    public void testGetCurrentUserWhenAuthenticated() throws Exception
    {
        User user = client().resource("/user/_me")
                            .header("Authorization", this.getBasicAuthenticationHeader())
                            .get(User.class);

        Assert.assertEquals(user.getEmail(), EMAIL);
        Assert.assertEquals(user.getPassword(), "********");

    }

    @Test
    public void testGetCurrentUserWhenNotAuthenticated() throws Exception
    {
        ClientResponse cr = client().resource("/user/_me").get(ClientResponse.class);

        Assert.assertEquals(Status.UNAUTHORIZED, cr.getClientResponseStatus());
    }
    
    @Test
    public void testRegisterUserValidation() throws Exception
    {
        // FIXME I don't know why this exception prevents from actually testing the response code.
        thrown.expect(InvalidEntityException.class);
        
        ClientResponse cr = client().resource("/user/")
                                    .type(MediaType.APPLICATION_JSON)
                                    .entity("{\"email\":\"_me\", \"slug\": \"lol\", \"password\" : \"lol\"}")
                                    .header("Authorization", this.getBasicAuthenticationHeader())
                                    .post(ClientResponse.class);

        
        // Direct code comparison as 422 is a WebDAV extension which Jersey client has no mapping for.
        // Assert.assertEquals(422, cr.getStatus());
    }

}
