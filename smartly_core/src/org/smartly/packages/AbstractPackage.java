package org.smartly.packages;


import org.json.JSONArray;
import org.json.JSONObject;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.util.JsonWrapper;

import java.util.Arrays;

public abstract class AbstractPackage
        implements Comparable<AbstractPackage> {

    private final String _id;
    private final int _priority;
    private final JSONObject _dependencies;
    private String _version;
    private String _description;
    private String _maintainer_name;
    private String _maintainer_mail;
    private String _maintainer_url;

    /**
     * Creates Package instance.
     *
     * @param id       Unique id for package.
     * @param priority Priority is important in order of execution. System packages has priority from 0 to 100.
     *                 For standard packages use priority grater than 100.
     */
    public AbstractPackage(final String id, final int priority) {
        _id = id;
        _priority = (this instanceof ISmartlySystemPackage) ? priority : (priority < 100 ? priority + 100 : priority);
        _version = "0.0.1";
        _dependencies = new JSONObject();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + (this.getId() != null ? this.getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AbstractPackage other = (AbstractPackage) obj;
        return !((this.getId() == null) ? (other.getId() != null) : !this.getId().equals(other.getId()));
    }

    @Override
    public int compareTo(final AbstractPackage o) {
        if (_priority == o.getPriority()) {
            return 0;
        } else if (_priority > o.getPriority()) {
            return +1;
        } else {
            return -1;
        }
    }

    public final String getId() {
        return _id;
    }

    public int getPriority() {
        return _priority;
    }

    public String getVersion() {
        return _version;
    }

    public void setVersion(String _version) {
        this._version = _version;
    }

    public String getDescription() {
        return _description;
    }

    public void setDescription(String _description) {
        this._description = _description;
    }

    public String getMaintainerName() {
        return _maintainer_name;
    }

    public void setMaintainerName(String _mantainer_name) {
        this._maintainer_name = _mantainer_name;
    }

    public String getMaintainerMail() {
        return _maintainer_mail;
    }

    public void setMaintainerMail(String _mantainer_mail) {
        this._maintainer_mail = _mantainer_mail;
    }

    public String getMaintainerUrl() {
        return _maintainer_url;
    }

    public void setMaintainerUrl(String _mantainer_url) {
        this._maintainer_url = _mantainer_url;
    }

    /**
     * Hash of prerequisite packages on which this package depends in order to install and run.
     * Each dependency defines the lowest compatible MAJOR[.MINOR[.PATCH]] dependency versions
     * (only one per MAJOR version) with which the package has been tested and is assured to work.
     * The version may be a simple version string (see the version property for acceptable forms),
     * or it may be a hash group of dependencies which define a set of options, any one of which satisfies
     * the dependency. The ordering of the group is significant and earlier entries have higher priority.<br>
     * For example:
     * dependencies": {
     * webkit": "1.2",
     * ssl": {
     * gnutls": ["1.0", "2.0"],
     * openssl": "0.9.8",
     * },
     * }
     *
     * @return JSONObject
     */
    public JSONObject getDependencies() {
        return _dependencies;
    }

    /**
     * Add dependency as single version.
     *
     * @param moduleName Name of Module
     * @param version    module version
     */
    public void addDependency(final String moduleName, final String version) {
        JsonWrapper.put(_dependencies, moduleName, version);
    }

    /**
     * Add dependency from a list of versions.
     *
     * @param moduleName Name of Module.
     * @param versions   List of versions.
     */
    public void addDependency(final String moduleName, final String[] versions) {
        final JSONArray array = new JSONArray(Arrays.asList(versions));
        JsonWrapper.put(_dependencies, moduleName, array);
    }

    /**
     * Add hash group of dependencies.
     *
     * @param moduleName Name of Module.
     * @param modules    Hash group of dependencies
     */
    public void addDependency(final String moduleName, final JSONObject modules) {
        JsonWrapper.put(_dependencies, moduleName, modules);
    }

    public Logger getLogger() {
        return LoggingUtils.getLogger(this);
    }

    /**
     * Enter point for a package
     *
     * @throws Exception
     */
    public abstract void load() throws Exception;

    /**
     * Called when Smartly is ready.
     */
    public abstract void ready();


}
