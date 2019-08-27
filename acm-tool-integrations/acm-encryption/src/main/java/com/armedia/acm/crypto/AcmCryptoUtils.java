package com.armedia.acm.crypto;

/*-
 * #%L
 * Acm Encryption Tools
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.crypto.exceptions.AcmEncryptionBadKeyOrDataException;
import com.armedia.acm.crypto.exceptions.AcmEncryptionException;

import org.bouncycastle.openpgp.PGPException;

import java.io.IOException;
import java.io.InputStream;
import java.security.PrivateKey;

/**
 * Created by nebojsha on 09.05.2015.
 */
public interface AcmCryptoUtils
{
    /**
     * @param passPhrase
     *            key for the encryption
     * @param data
     *            data to be encrypted
     * @param addNonce
     *            whether to add random data before encryption
     * @return encrypted data
     * @throws AcmEncryptionException
     */
    byte[] encryptData(byte[] passPhrase, byte[] data, boolean addNonce) throws AcmEncryptionException;

    /**
     * @param passPhrase
     *            key for the decryption
     * @param data
     *            data to be decrypted
     * @param hasNonce
     *            if on encryption is added nounce than you must set true this argument
     * @return decrypted data
     * @throws AcmEncryptionException
     */
    byte[] decryptData(byte[] passPhrase, byte[] data, boolean hasNonce) throws AcmEncryptionException;

    /**
     * Decrypts given data with the given PrivateKey.
     *
     * @param key
     *            the {@link PrivateKey} to use to decrypt the data
     * @param data
     *            data to be decrypted
     * @param hasNonce
     *            if on encryption is added nounce than you must set true this argument
     * @param encryptionAlgorithm
     *            the encryption algorithm to use
     * @return decrypted data
     * @throws AcmEncryptionException
     *             when decrypting fails for some reaso
     */
    byte[] decryptData(PrivateKey key, byte[] data, boolean hasNonce, String encryptionAlgorithm) throws AcmEncryptionException;

    /**
     * Decrypts given data with the given passPhrase.
     *
     * @param passPhrase
     *            key for the encryption
     * @param data
     *            data to be decrypted
     * @param keySize
     *            the size of the KEY in bytes
     * @param ivSize
     *            the size of the IV in bytes
     * @param magicSize
     *            the size of the magic in bytes
     * @param saltSize
     *            the size of salt in bytes
     * @param passPhraseIterations
     *            number of hashing iterations used to generate the derived key+IV
     * @param passPhraseHashAlgorithm
     *            the hashing algorithm used to generate the derived key+IV
     * @param encryptionAlgorithm
     *            the encryption algorithm to use for decryption
     * @param blockCipherMode
     *            the block cipher mode used for decryption
     * @param padding
     *            the padding used for decryption
     * @return decrypted data
     * @throws AcmEncryptionException
     *             when decrypting fails for some reason
     */
    byte[] decryptData(byte[] passPhrase, byte[] data, int keySize, int ivSize, int magicSize, int saltSize, int passPhraseIterations,
            String passPhraseHashAlgorithm, String encryptionAlgorithm, String blockCipherMode, String padding)
            throws AcmEncryptionException;

    /**
     * Returns the encrypted data with the given passPhrase as a byte array.
     *
     * @param passPhrase
     *            key for the encryption
     * @param data
     *            data to be encrypted
     * @param keySize
     *            the size of the KEY in bytes
     * @param ivSize
     *            the size of the IV in bytes
     * @param magicSize
     *            the size of the magic in bytes
     * @param saltSize
     *            the size of salt in bytes
     * @param passPhraseIterations
     *            number of hashing iterations used to generate the derived key+IV
     * @param passPhraseHashAlgorithm
     *            the hashing algorithm used to generate the derived key+IV
     * @param encryptionAlgorithm
     *            the encryption algorithm to use for encryption
     * @param blockCipherMode
     *            the block cipher mode used for encryption
     * @param padding
     *            the padding used for encryption
     * @return encrypted data
     * @throws AcmEncryptionException
     *             when encrypting fails for some reason
     */
    byte[] encryptData(byte[] passPhrase, byte[] data, int keySize, int ivSize, int magicSize, int saltSize, int passPhraseIterations,
            String passPhraseHashAlgorithm, String encryptionAlgorithm, String blockCipherMode, String padding)
            throws AcmEncryptionException;

    /**
     * @param in
     *            InputStream which is encrypted with PGP symmetric encryption using passPhrase
     * @param passPhrase
     *            passPhrase
     * @return decrypted bytes
     */
    byte[] decryptInputStreamWithPGP(InputStream in, char[] passPhrase) throws AcmEncryptionBadKeyOrDataException;

    /**
     * Simple PGP encryptor of byte array.
     *
     * @param clearData
     *            The text to be encrypted
     * @param passPhrase
     *            The pass phrase (key). This method assumes that the key is a simple pass phrase, and does not yet
     *            support RSA or more
     *            sophisticated keying.
     * @param fileName
     *            File name. This is used in the Literal Data Packet (tag 11) which is really only important if the data
     *            is to be related to
     *            a file to be recovered later. Because this routine does not know the source of the information, the
     *            caller can set
     *            something here for file name use that will be carried. If this routine is being used to encrypt SOAP
     *            MIME bodies, for
     *            example, use the file name from the MIME type, if applicable. Or anything else appropriate.
     * @param armor
     *            true if the output stream that writes data should use ASCII Armored format.
     * @return encrypted data
     * @throws IOException
     * @throws PGPException
     * @throws java.security.NoSuchProviderException
     */
    byte[] encryptWithPGP(byte[] clearData, char[] passPhrase, String fileName, int algorithm, boolean armor)
            throws AcmEncryptionBadKeyOrDataException;
}
