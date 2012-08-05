/*
 * 
 */
package org.smartly.packages.mongo.impl.db.service;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import org.smartly.IConstants;
import org.smartly.commons.cryptograph.MD5;
import org.smartly.commons.logging.Level;
import org.smartly.commons.util.*;
import org.smartly.packages.mongo.impl.AbstractMongoService;
import org.smartly.packages.mongo.impl.StandardCodedException;
import org.smartly.packages.mongo.impl.db.entity.MongoUser;
import org.smartly.packages.mongo.impl.util.MongoUtils;

import java.util.Collection;
import java.util.regex.Pattern;

/**
 * @author angelo.geminiani
 */
public class MongoUserService
        extends AbstractMongoService {

    // ------------------------------------------------------------------------
    //                      Constructor
    // ------------------------------------------------------------------------
    public MongoUserService(final DB db, final String[] langCodes) {
        super(db, MongoUser.COLLECTION, langCodes);
    }

    // ------------------------------------------------------------------------
    //                      public
    // ------------------------------------------------------------------------
    public DBObject getById(final Object id, final boolean onlyEnabled) {
        final DBObject user = super.findById(id);
        if (null != user) {
            final boolean isenabled = MongoUser.getEnabled(user);
            if (onlyEnabled) {
                // only enabled users
                return isenabled
                        ? user
                        : null;
            }
            return user;
        }
        return null;
    }

    public MongoUser getFirst(final DBObject filter) {
        final DBObject object = super.findOne(filter);
        return null != object
                ? new MongoUser(object)
                : null;
    }

    /**
     * Try to signin
     *
     * @param id_user
     * @param password
     * @return
     */
    public DBObject signin(final String id_user, final String password) {
        // try with username
        DBObject user = this.getByUsernameAndPassword(id_user, password);
        if (null == user) {
            // try with email
            user = this.getByEmailAndPassword(id_user, password);
        }

        // return user
        if (null != user && !MongoUser.getRemoved(user)) {
            // return user
            return user;
        } else {
            return null;
        }
    }

    public DBObject getByUsernameAndPassword(final String username,
                                             final String password) {
        return this.lookupByUsernameAndPassword(username, password);
    }

    public DBObject getByEmailAndPassword(final String email, final String password) {
        return this.lookupByEmailAndPassword(email, password);
    }

    public boolean exists(final String iduser) {
        return !CollectionUtils.isEmpty(this.lookup(iduser));
    }

    public Collection<DBObject> lookup(final String iduser) {
        if (StringUtils.hasText(iduser)) {
            try {
                Collection<DBObject> users;
                //-- tryID --//
                users = this.lookupID(iduser);
                if (!CollectionUtils.isEmpty(users)) {
                    return users;
                }

                //-- try with username --//
                users = this.lookupUserName(iduser);
                if (!CollectionUtils.isEmpty(users)) {
                    return users;
                }

                //-- try with email --//
                users = this.lookupEmail(iduser);
                if (!CollectionUtils.isEmpty(users)) {
                    return users;
                }
            } catch (Exception ex) {
                super.getLogger().log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    public Collection<DBObject> lookupID(final String userId) {
        final DBObject query = new BasicDBObject();
        query.put(MongoUser.ID, userId);
        return super.find(query);
    }

    public Collection<DBObject> lookupUserName(final String username) {
        final DBObject query = new BasicDBObject();
        final Pattern equal = Pattern.compile("\\A" + username + "\\z", MongoUtils.CASE_INSENSITIVE);
        query.put(MongoUser.USERNAME, equal);
        return super.find(query);
    }

    public Collection<DBObject> lookupEmail(final String email) {
        final DBObject query = new BasicDBObject();
        final Pattern equal = Pattern.compile("\\A" + email + "\\z", MongoUtils.CASE_INSENSITIVE);
        query.put(MongoUser.EMAIL, equal);
        return super.find(query);
    }

    public MongoUser createNew(final String idphone,
                               final String email,
                               final String password,
                               final String langCode,
                               final String country)
            throws Exception {
        // have valid input data?
        if (StringUtils.hasText(idphone) || StringUtils.hasText(email)) {
            // does user exists?
            if (!this.exists(email)) {
                final MongoUser user = new MongoUser();
                MongoUser.setEnabled(user, false);
                user.setPhonehome("");
                user.setPhonemobile(StringUtils.leftStr(idphone, 6, true));
                user.setPhoneoffice(StringUtils.leftStr(idphone, 6, true));
                user.setLang(StringUtils.hasText(langCode)
                        ? langCode
                        : IConstants.DEF_LANG);
                user.setCountryId(null != country ? country : "IT");
                // initial username and password
                user.setUsername(StringUtils.hasText(email)
                        ? email : idphone);
                user.setPassword(StringUtils.hasText(password)
                        ? password : RandomUtils.randomNumeric(6));
                // init email
                if (RegExUtils.isValidEmail(email)) {
                    user.setEmail(email);
                }
                // save user
                super.upsert(user);

                // return saved user
                return user;
            }
        }
        return null;
    }

    public MongoUser save(final DBObject item,
                          final String... excludeProperties) throws StandardCodedException {
        final DBObject checkeduser = this.validateUser(item);
        final DBObject result = super.merge(checkeduser, excludeProperties);
        return null != result
                ? new MongoUser(result)
                : null;
    }

    public DBObject update(final DBObject user) throws Exception {
        if (null != user) {
            final Object id = MongoUtils.getId(user);
            if (null != id) {
                // save user
                return this.save(user, MongoUser.PASSWORD,
                        MongoUser.CREATIONDATE);
            } else {
                throw new Exception(
                        FormatUtils.format("User not found on database: {0}", user));
            }
        }
        return null;
    }

    public boolean changePassword(final Object id,
                                  final String password) throws Exception {
        final DBObject user = this.getById(id, true);
        if (null != user) {
            MongoUser.setPassword(user, password);
            super.upsert(user);
            return true;
        }
        return false;
    }

    public void initCountry(final DBObject user) {
        if (null != user) {
            final String countryId = MongoUser.getCountryId(user);
            if (StringUtils.hasText(countryId)) {
                final DBObject country = this.getCountry(countryId);
                MongoUser.setCountry(user, country);
            }
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    private DBObject validateUser(final DBObject item) {
        if (null != item) {
            try {
            } catch (Throwable t) {
                super.getLogger().log(Level.SEVERE, null, t);
            }
        }
        return item;
    }

    private DBObject lookupByUsernameAndPassword(final String username,
                                                 final String password) {
        DBObject user = null;
        try {
            final DBObject query = new BasicDBObject();
            final Pattern equal = MongoUtils.patternEquals(username); //Pattern.compile("\\A" + username + "\\z", BeeMongoUtils.CASE_INSENSITIVE);
            query.put(MongoUser.USERNAME, equal);
            query.put(MongoUser.PASSWORD, password);
            // get user
            user = CollectionUtils.getFirst(super.find(query));
            if (null == user) {
                query.put(MongoUser.PASSWORD, MD5.encode(password));
                user = CollectionUtils.getFirst(super.find(query));
            }
        } catch (Exception ex) {
            super.getLogger().log(Level.SEVERE, null, ex);
        }
        return user;
    }

    private DBObject lookupByEmailAndPassword(final String email, final String password) {
        DBObject user = null;
        try {
            final DBObject query = new BasicDBObject();
            final Pattern equal = MongoUtils.patternEquals(email); //Pattern.compile("\\A" + email + "\\z", BeeMongoUtils.CASE_INSENSITIVE);
            query.put(MongoUser.EMAIL, equal);
            query.put(MongoUser.PASSWORD, password);
            user = CollectionUtils.getFirst(super.find(query));
            if (null == user) {
                query.put(MongoUser.PASSWORD, MD5.encode(password));
                user = CollectionUtils.getFirst(super.find(query));
            }
        } catch (Exception ex) {
            super.getLogger().log(Level.SEVERE, null, ex);
        }
        return user;
    }

    private DBObject getCountry(final String countryId) {
        final MongoCountryService srvc = new MongoCountryService(super.getDb(),
                super.getLanguages());
        return srvc.findById(countryId);
    }
}
