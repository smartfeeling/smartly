/*
 * ICryptographConstants.java
 *
 */

package org.smartly.commons.cryptograph;

/**
 *
 * @author Angelo Geminiani ( angelo.geminiani@gmail.com )
 */
public interface ICryptographConstants {
    
    /**
     * Supported Algorithm for MessageDigest. Type: MessageDigest
     */
    public enum AlgorithmMessageDigest {
        MD2("MD2"),
        MD5("MD5"),
        SHA("SHA"),
        SHA_1("SHA-1");
        private final String _value;
        AlgorithmMessageDigest(String value){
            _value = value;
        }
        public String toString(){
            return _value;
        }
    } 
    
    /**
     * Supported Algorithm for Digital Signature. Type: Signature
     */
    public enum AlgorithmDigitalSignature {
        SHA1withDSA("SHA1withDSA"),
        MD2withRSA("MD2withRSA"),
        MD5withRSA ("MD5withRSA "),
        SHA1withRSA("SHA1withRSA-1");
        private final String _value;
        AlgorithmDigitalSignature(String value){
            _value = value;
        }
        public String toString(){
            return _value;
        }
    } 
    
    /**
     * Supported Algorithm for Key Pair. Type: KeyPairGenerator
     */
    public enum AlgorithmKeyPair {
        DSA("DSA"),
        RSA("RSA");
        private final String _value;
        AlgorithmKeyPair(String value){
            _value = value;
        }
        public String toString(){
            return _value;
        }
    } 
    
}
