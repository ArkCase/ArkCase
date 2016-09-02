package com.armedia.acm.crypto.properties;

import com.armedia.acm.core.exceptions.AcmEncryptionException;

import java.util.Map;

/**
 * Utility class used when encrypting/decrypting values in properties files.
 * <p>
 * A value is considered "encrypted" when it appears surrounded by <tt>ENC(...)</tt>, like:
 * <p>
 * <center><tt>my.value=ENC(!"DGAS24FaIO$)</tt></center>
 * <p>
 * Created by Bojan Milenkoski on 20.4.2016
 */
public interface AcmEncryptablePropertyUtils
{
    /**
     * Returns the decrypted value of the symmetric key as a byte array.
     *
     * @return the decrypted value of the symmetric key as a byte array.
     * @throws AcmEncryptionException
     */
    byte[] decryptSymmetricKey() throws AcmEncryptionException;

    /**
     * Returns a decrypted property value if the original value is surrounded by <tt>ENC(...)</tt>. Otherwise returns the original value.
     *
     * @param originalValue the encrypted value to decrypt
     * @return decrypted value
     * @throws AcmEncryptionException
     */
    String decryptPropertyValue(final String originalValue) throws AcmEncryptionException;

    /**
     * Returns an encrypted property value for the given original value. The encrypted value is surrounded by <tt>ENC(...)</tt>. If the
     * original value already is surrounded by <tt>ENC(...)</tt>, then the original value is returned.
     *
     * @param originalValue the value to encrypt
     * @return encrypted value surrounded by <tt>ENC(...)</tt>
     * @throws AcmEncryptionException
     */
    String encryptPropertyValue(String originalValue) throws AcmEncryptionException;

    void decryptProperties(Map<? extends Object, Object> toBeDecrypted);
}
