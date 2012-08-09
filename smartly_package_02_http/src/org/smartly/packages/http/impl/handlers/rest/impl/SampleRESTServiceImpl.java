package org.smartly.packages.http.impl.handlers.rest.impl;


import org.json.JSONException;
import org.json.JSONObject;
import org.smartly.packages.http.impl.handlers.rest.impl.annotations.*;

import java.util.LinkedList;
import java.util.List;

@Path("/test")
public class SampleRESTServiceImpl extends RESTService {

    public SampleRESTServiceImpl() {
    }

    @GET
    @Path("/all")
    public List getAll() throws JSONException {
        final List result = new LinkedList();
        for(int i=0;i<10;i++){
            final JSONObject item = new JSONObject();
            item.putOpt("index", i);
            result.add(item);
        }
        return result;
    }

    @GET
    @Path("{id}")
    public Object get(@PathParam("id") String id){

        return "passed " + id;
    }

    @GET
    @Path("{token}/{id}")
    public Object get(@PathParam("token") String token, @PathParam("id")String id){

        return "passed " + token + "-" + id;
    }

    @GET
    @Path("/form")
    public Object formGET(@FormParam("param1") String param){

        return "passed: " + param;
    }

    @POST
    @Path("/form")
    public Object formPOST(@FormParam("param1") String param){

        return "passed: " + param;
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}
