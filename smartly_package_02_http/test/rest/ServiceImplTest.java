package rest;


import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;
import org.smartly.commons.remoting.rest.RESTRegistry;
import org.smartly.commons.remoting.rest.SampleRESTServiceImpl;
import org.smartly.commons.remoting.rest.wrapper.MethodWrapper;
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

        String http_method = "GET";
        String path = "/test/A/b";

        MethodWrapper mw =  RESTRegistry.getMethod(http_method, path);
        assertNotNull("method not found", mw);

        System.out.println(mw.toString());

        Map<String, Object> formParams = new HashMap<String, Object>();
        byte[] bytes = mw.execute(path, formParams);
        String result = new String(bytes);
        System.out.println("RESPONSE: " + result);

        path = "/test/form";
        mw =  RESTRegistry.getMethod(http_method, path);
        formParams = new HashMap<String, Object>();
        formParams.put("param1", "hello");
        bytes = mw.execute(path, formParams);
        result = new String(bytes);
        System.out.println("RESPONSE: " + result);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}
