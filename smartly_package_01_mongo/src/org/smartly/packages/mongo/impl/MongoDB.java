/*
 * 
 */
package org.smartly.packages.mongo.impl;

import com.mongodb.*;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.util.ExceptionUtils;
import org.smartly.commons.util.StringUtils;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * To start MongoDB with authentication you must run mongo with -auth parameter.
 * i.e. "mongod.exe --auth"
 *
 * @author angelo.geminiani
 */
public class MongoDB {

    // ------------------------------------------------------------------------
    //                      constants
    // ------------------------------------------------------------------------
    private static String DB_ADMIN = IMongoConstants.DB_ADMIN;
    private static String COLL_SYSTEMUSERS = IMongoConstants.COLL_SYSTEMUSERS;
    private static String HOST = "localhost";
    private static int PORT = 27017;
    private static boolean OPT_SAFE = true;
    // ------------------------------------------------------------------------
    //                      variables
    // ------------------------------------------------------------------------
    private com.mongodb.Mongo __mongo;
    // ------------------------------------------------------------------------
    //                      fields
    // ------------------------------------------------------------------------
    private String _host;
    private int _port;
    private boolean _safe;
    // ------------------------------------------------------------------------
    //                      constructor
    // ------------------------------------------------------------------------

    public MongoDB() {
        _host = HOST;
        _port = PORT;
        _safe = OPT_SAFE;
    }

    public MongoDB(final String host, final int port) {
        _host = host;
        _port = port;
    }

    public MongoDB(final String host, final int port,
                   final boolean safe) {
        _host = host;
        _port = port;
        _safe = safe;
    }

    // ------------------------------------------------------------------------
    //                      properties
    // ------------------------------------------------------------------------
    public String getHost() {
        return _host;
    }

    public void setHost(String value) {
        this._host = StringUtils.hasText(value) ? value : HOST;
        __mongo = null;
    }

    public int getPort() {
        return _port;
    }

    public void setPort(int value) {
        this._port = value > 0 ? value : PORT;
        __mongo = null;
    }
    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    /**
     * gets a list of all database names present on the server
     *
     * @return
     * @throws StandardCodedException
     */
    public final List<String> getDBNames() throws StandardCodedException {
        try {
            final com.mongodb.Mongo mongo = this.getMongo();
            if (null != mongo) {
                return mongo.getDatabaseNames();
            }
            return new ArrayList<String>();
        } catch (UnknownHostException t) {
            throw this.getError503(t.getMessage());
        } catch (Throwable t) {
            throw this.getError500(t.getMessage());
        }
    }

    /**
     * Drops the database if it exists.
     *
     * @param name name of database to drop
     * @throws StandardCodedException
     */
    public final void dropDB(final String name) throws StandardCodedException {
        try {
            final com.mongodb.Mongo mongo = this.getMongo();
            if (null != mongo) {
                mongo.dropDatabase(name);
            }
        } catch (UnknownHostException t) {
            throw this.getError503(t.getMessage());
        } catch (Throwable t) {
            throw this.getError500(t.getMessage());
        }
    }

    /**
     * Gets the maximum size for a BSON object supported by the current master server.
     * Note that this value may change over time depending on which server is master.
     * If the size is not known yet, a request may be sent to the master server
     *
     * @return the maximum size
     * @throws StandardCodedException
     */
    public final int getMaxBsonObjectSize() throws StandardCodedException {
        try {
            final com.mongodb.Mongo mongo = this.getMongo();
            if (null != mongo) {
                return mongo.getMaxBsonObjectSize();
            }
            return 0;
        } catch (UnknownHostException t) {
            throw this.getError503(t.getMessage());
        } catch (Throwable t) {
            throw this.getError500(t.getMessage());
        }
    }

    /**
     * gets a database object
     *
     * @param dbname   the database name
     * @param username
     * @param password
     * @return
     * @throws StandardCodedException
     */
    public final DB getDB(final String dbname,
                          final String username, final String password) throws StandardCodedException {
        try {
            final DB db = this.getMongo().getDB(dbname);
            if (StringUtils.hasText(username)
                    && StringUtils.hasText(password)
                    && !db.isAuthenticated()) {
                if (!db.authenticate(username, password.toCharArray())) {
                    throw this.getError403();
                }
            }
            return db;
        } catch (UnknownHostException t) {
            throw this.getError503(t.getMessage());
        } catch (Throwable t) {
            throw this.getError500(t.getMessage());
        }
    }

    public final boolean addUser(final String dbname, final String authusername,
                                 final String authpassword, final String username,
                                 final String password, final boolean readOnly) throws StandardCodedException {
        try {
            final DB db = this.getDB(dbname, authusername, authpassword);
            return this.addUser(db, username, password, readOnly);
        } catch (Throwable t) {
            throw this.getError500(t);
        }
    }

    public final boolean addUser(final DB db,
                                 final String username, final String password,
                                 final boolean readOnly) throws StandardCodedException {
        try {
            if (null != db) {
                final WriteResult result = db.addUser(username,
                        password.toCharArray(), readOnly);
                if (StringUtils.hasText(result.getError())) {
                    throw this.getError500(result.getError());
                }
                return true;
            }
            throw this.getError503("Database cannot be a NULL Object!");
        } catch (Throwable t) {
            throw this.getError500(t);
        }
    }

    public final boolean removeUser(final DB db,
                                    final String username) throws StandardCodedException {
        try {
            if (null != db) {
                final WriteResult result = db.removeUser(username);
                if (StringUtils.hasText(result.getError())) {
                    throw this.getError500(result.getError());
                }
                return true;
            }
            throw this.getError503("Database cannot be a NULL Object!");
        } catch (Throwable t) {
            throw this.getError500(t);
        }
    }

    public final boolean addAdminUser(final String authusername,
                                      final String authpassword, final String username,
                                      final String password) throws StandardCodedException {
        return this.addUser(DB_ADMIN,
                authusername, authpassword,
                username, password, true);
    }

    public final List<DBObject> getDBUsers(final String dbname, final String authusername,
                                           final String authpassword) throws StandardCodedException {
        try {
            final DB db = this.getDB(dbname, authusername, authpassword);
            if (null != db) {
                if (db.collectionExists(COLL_SYSTEMUSERS)) {
                    final DBCollection coll = db.getCollection(COLL_SYSTEMUSERS);
                    return coll.find().toArray();
                }
            }
        } catch (Throwable t) {
            throw this.getError500(t);
        }
        return new ArrayList<DBObject>();
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    private Logger getLogger() {
        return LoggingUtils.getLogger(this);
    }

    private StandardCodedException getError500(final Throwable t) {
        final String message = ExceptionUtils.getRealMessage(t);
        return this.getError500(message);
    }

    private StandardCodedException getError500(final String message) {
        return new StandardCodedException(
                StandardCodedException.ERROR_500_SERVERERROR,
                message);
    }

    private StandardCodedException getError503(final String message) {
        return new StandardCodedException(
                StandardCodedException.ERROR_503_SERVICEUNAVAILABLE,
                message);
    }

    private StandardCodedException getError403() {
        return new StandardCodedException(
                StandardCodedException.ERROR_403_FORBIDDEN,
                "Invalid Username or Password");
    }

    private com.mongodb.Mongo getMongo() throws UnknownHostException {
        if (null != __mongo) {
            return __mongo;
        }

        //-- creates new mongo --//
        final MongoClientOptions options = MongoClientOptions.builder()
                .writeConcern(WriteConcern.SAFE)
                .build();
        //options.setSafe(_safe);
        final ServerAddress address = new ServerAddress(_host, _port);
        __mongo = new com.mongodb.MongoClient(address, options);
        return __mongo;
    }
}
