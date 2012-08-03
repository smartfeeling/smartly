package rest;


import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;
import org.smartly.packages.http.impl.handlers.rest.impl.RESTRegistry;
import org.smartly.packages.http.impl.handlers.rest.impl.SampleRESTServiceImpl;
import org.smartly.packages.http.impl.handlers.rest.impl.wrapper.MethodWrapper;
import org.smartly.packages.http.launcher.Main;

import java.util.HashMap;
import java.util.Map;

public class ServiceImplTest {

    public ServiceImplTest() {

    }

    @BeforeClass
    public static void open() {
        Main.main(new String[]{"-w", "z:/_smartly_http/", "-t"});
    }

    @Test
    public void testRegister() throws Exception {

        System.out.println("register");

        RESTRegistry.register(SampleRESTServiceImpl.class);

        final String http_method = "GET";
        final String path = "/test/A/b";

        final MethodWrapper mw =  RESTRegistry.getMethod(http_method, path);
        assertNotNull("method not found", mw);

        System.out.println(mw.toString());

        final Map<String, String> formParams = new HashMap<String, String>();
        final byte[] bytes = mw.execute(path, formParams);
        final String result = new String(bytes);
        System.out.println("RESPONSE: " + result);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}
