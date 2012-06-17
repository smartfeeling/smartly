package org.smartly.packages.http.impl.util.vtool;


public class App extends org.smartly.packages.velocity.impl.vtools.impl.App {

    public final String getAppToken(){
        try{
           return (String)super.getConfiguration().get("remoting.app_securetoken");
        } catch(Throwable ignored){
        }
        return "";
    }

}
