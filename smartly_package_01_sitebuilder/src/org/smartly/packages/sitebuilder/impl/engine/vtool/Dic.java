package org.smartly.packages.sitebuilder.impl.engine.vtool;

import org.smartly.commons.io.jsonrepository.JsonRepository;
import org.smartly.packages.velocity.impl.vtools.IVLCTool;

public class Dic implements IVLCTool {

    public static final String NAME = "dic";

    private final JsonRepository _repo;

    public Dic(final String root) throws Exception {
         _repo = new JsonRepository(root);
    }

    @Override
    public String getName() {
        return NAME;
    }

    public String get(final String lang, final String key){
        final String path = lang.concat(".").concat(key);
        return _repo.getString(path, "'".concat(key).concat("' not found") );
    }

    public String get(final String path){
        return _repo.getString(path, "'".concat(path).concat("' not found") );
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


}
