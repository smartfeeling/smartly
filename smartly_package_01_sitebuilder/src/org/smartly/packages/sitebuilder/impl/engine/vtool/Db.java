package org.smartly.packages.sitebuilder.impl.engine.vtool;

import org.json.JSONObject;
import org.smartly.commons.jsonrepository.JsonRepository;
import org.smartly.packages.velocity.impl.vtools.IVLCTool;

import java.util.List;

public class Db implements IVLCTool {

    public static final String NAME = "db";

    private final JsonRepository _repo;

    public Db(final String root) throws Exception {
        _repo = new JsonRepository(root);
    }

    @Override
    public String getName() {
        return NAME;
    }

    public Object get(final String path) {
        return _repo.get(path);
    }

    public String getString(final String path){
        return _repo.getString(path);
    }

    public String getString(final String path, final String defVal){
        return _repo.getString(path, defVal);
    }

    public int getInt(final String path){
        return _repo.getInt(path);
    }

    public int getInt(final String path, final int defVal){
        return _repo.getInt(path, defVal);
    }

    public double getDouble(final String path){
        return _repo.getDouble(path);
    }

    public double getDouble(final String path, final double defVal){
        return _repo.getDouble(path, defVal);
    }

    public Object[] getList(final String path){
        final List<JSONObject> list = _repo.getList(path);
        return list.toArray(new Object[list.size()]);
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


}
