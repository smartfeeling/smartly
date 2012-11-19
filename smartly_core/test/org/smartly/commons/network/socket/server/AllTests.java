package org.smartly.commons.network.socket.server;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

    public static Test suite() {

        TestSuite suite = new TestSuite("All Tests");

        suite.addTestSuite(VerySimpleTest.class);
        suite.addTestSuite(TestServer.class);
        suite.addTestSuite(TestObjects.class);

        return suite;

    }
}
