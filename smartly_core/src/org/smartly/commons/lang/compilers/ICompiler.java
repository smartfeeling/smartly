package org.smartly.commons.lang.compilers;

import java.util.Map;

/**
 * Generic compiler interface.
 * Use it in a compiler wrapper to encapsulate external compilers
 */
public interface ICompiler {

    byte[] compile(final byte[] data) throws Exception;

    byte[] compile(final byte[] data, final Map<String, Object> args) throws Exception;

}
