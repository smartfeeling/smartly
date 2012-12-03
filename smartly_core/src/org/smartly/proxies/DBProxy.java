package org.smartly.proxies;

/**
 *
 */
public class DBProxy {

    private IDBProxy _proxy;

    private DBProxy() {

    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private static DBProxy __instance;

    private static DBProxy getInstance(){
        if(null==__instance){
            __instance = new DBProxy();
        }
        return __instance;
    }

    public static <T> IDBProxy<T> get(){
        return getInstance()._proxy;
    }

    public static void register(final IDBProxy proxy){
        getInstance()._proxy = proxy;
    }

}
