package org.smartly.commons.lang.compilers;

/**
 * Generic compiler interface.
 * Use it in a compiler wrapper to encapsulate external compilers
 */
public interface ICompiler {

    byte[] compile(final byte[] data) throws Exception;


}
