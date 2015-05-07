package com.armedia.acm.plugins.profile.util;

import com.armedia.acm.plugins.profile.exception.AcmEncryptionException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by nebojsha on 01.05.2015.
 */
public class CryptoUtils {

    public static final String ENCRYPTION_ALGORITHM = "AES";

    public static byte[] encryptData(byte[] passPhrase, byte[] data) throws AcmEncryptionException {
        byte[] encryptedData = null;
        try {
            Cipher c = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            SecretKeySpec k =
                    new SecretKeySpec(passPhrase, ENCRYPTION_ALGORITHM);
            c.init(Cipher.ENCRYPT_MODE, k);
            encryptedData = c.doFinal(data);
        } catch (NoSuchAlgorithmException e) {
            throw new AcmEncryptionException("No such algorithm ", e);
        } catch (NoSuchPaddingException e) {
            throw new AcmEncryptionException("No such padding ", e);
        } catch (InvalidKeyException e) {
            throw new AcmEncryptionException("Invalid key ", e);
        } catch (IllegalBlockSizeException e) {
            throw new AcmEncryptionException("illegal block size ", e);
        } catch (BadPaddingException e) {
            throw new AcmEncryptionException("Bad padding ", e);
        }
        return encryptedData;
    }

    public static byte[] decryptData(byte[] passPhrase, byte[] data) throws AcmEncryptionException {
        byte[] decryptedData = null;
        try {
            Cipher c = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            SecretKeySpec k =
                    new SecretKeySpec(passPhrase, ENCRYPTION_ALGORITHM);
            c.init(Cipher.DECRYPT_MODE, k);
            decryptedData = c.doFinal(data);
        } catch (NoSuchAlgorithmException e) {
            throw new AcmEncryptionException("No such algorithm ", e);
        } catch (NoSuchPaddingException e) {
            throw new AcmEncryptionException("No such padding ", e);
        } catch (InvalidKeyException e) {
            throw new AcmEncryptionException("Invalid key ", e);
        } catch (IllegalBlockSizeException e) {
            throw new AcmEncryptionException("illegal block size ", e);
        } catch (BadPaddingException e) {
            throw new AcmEncryptionException("Bad padding ", e);
        }
        return decryptedData;
    }

}
