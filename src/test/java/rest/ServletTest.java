package rest;

import dbStuff.AccountService;
import accountService.AccountServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.glassfish.jersey.test.JerseyTest;
import static org.junit.Assert.*;
import main.Context;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Application;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
/**
 * Created by KOPTE3 on 30.03.2016.
 */
public class ServletTest extends JerseyTest {

    @Override
    protected Application configure() {
        final Context ctx = new Context();
        ctx.put(AccountService.class, new AccountServiceImpl());
        final ResourceConfig resourceConfig = new ResourceConfig(Users.class, Session.class);
        final HttpServletRequest request = mock(HttpServletRequest.class);
        resourceConfig.register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(ctx);
                bind(request).to(HttpServletRequest.class);
            }
        });
        return resourceConfig;
    }

    @Test
    public void testSimple() {
//        System.out.println("fffffff");
//        final String response = target("api").path("user").request().get(String.class);
//        assertEquals("empty", response);
    }
}