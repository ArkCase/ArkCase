package com.armedia.acm.crypto;


import com.armedia.acm.core.exceptions.AcmEncryptionBadKeyOrDataException;
import com.armedia.acm.core.exceptions.AcmEncryptionException;
import org.bouncycastle.openpgp.PGPException;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchProviderException;

/**
 * Created by nebojsha on 09.05.2015.
 */
public interface AcmCryptoUtils {
    /**
     * @param passPhrase key for the encryption
     * @param data       data to be encrypted
     * @param addNonce   whether to add random data before encryption
     * @return encrypted data
     * @throws AcmEncryptionException
     */
    byte[] encryptData(byte[] passPhrase, byte[] data, boolean addNonce) throws AcmEncryptionException;

    /**
     * @param passPhrase key for the decryption
     * @param data       data to be decrypted
     * @param hasNonce   if on encryption is added nounce than you must set true this argument
     * @return decrypted data
     * @throws AcmEncryptionException
     */
    byte[] decryptData(byte[] passPhrase, byte[] data, boolean hasNonce) throws AcmEncryptionException;

    /**
     *
     * @param in InputStream which is encrypted with PGP symmetric encryption using passPhrase
     * @param passPhrase passPhrase
     * @return decrypted bytes
     */
    byte[] decryptInputStreamWithPGP(InputStream in,
                                     char[] passPhrase) throws AcmEncryptionBadKeyOrDataException;

    byte[] encryptWithPGP(
            byte[] clearData,
            char[] passPhrase,
            String fileName,
            int algorithm,
            boolean armor)
            throws AcmEncryptionBadKeyOrDataException;
}
