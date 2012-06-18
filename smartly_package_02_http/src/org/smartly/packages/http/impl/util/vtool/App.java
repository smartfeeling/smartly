package org.smartly.packages.http.impl.util.vtool;


import org.smartly.packages.http.SmartlyHttp;

public class App extends org.smartly.packages.velocity.impl.vtools.impl.App {

    public final String getAppToken(){
        try{
           return (String)super.getConfiguration().get("remoting.app_securetoken");
        } catch(Throwable ignored){
        }
        return "";
    }

    public String getHttpRoot() {
        try {
            return SmartlyHttp.getHTTPUrl("");
        } catch (Throwable ignored) {
        }
        return "";
    }

}
