package com.armedia.acm.crypto;

import com.armedia.acm.crypto.exceptions.AcmEncryptionBadKeyOrDataException;
import com.armedia.acm.crypto.exceptions.AcmEncryptionException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.UUID;

/**
 * Created by nebojsha on 01.05.2015.
 */
public class AcmCryptoUtilsImpl implements AcmCryptoUtils {

    public static final String ENCRYPTION_ALGORITHM = "AES";

    /**
     * @inheritDoc
     */
    @Override
    public byte[] encryptData(byte[] passPhrase, byte[] data, boolean addNonce) throws AcmEncryptionException {

        //we must use only 16 bytes for the key or we need to install "Cryptography Extension (JCE) Unlimited Strength."
        passPhrase = Arrays.copyOfRange(passPhrase, 0, 16);
        if (addNonce)
            data = addNonceToData(data);
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

    private byte[] addNonceToData(byte[] data) {
        byte[] nounce = ("-" + UUID.randomUUID().toString()).getBytes();
        byte[] newData = new byte[data.length + nounce.length];
        System.arraycopy(data, 0, newData, 0, data.length);
        System.arraycopy(nounce, 0, newData, data.length, nounce.length);
        return newData;
    }

    /**
     * @inheritDoc
     */
    @Override
    public byte[] decryptData(byte[] passPhrase, byte[] data, boolean hasNonce) throws AcmEncryptionException {
        byte[] decryptedData = null;
        //we must use only 16 bytes for the key or we need to install "Cryptography Extension (JCE) Unlimited Strength."
        passPhrase = Arrays.copyOfRange(passPhrase, 0, 16);
        try {
            Cipher c = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            SecretKeySpec k =
                    new SecretKeySpec(passPhrase, ENCRYPTION_ALGORITHM);
            c.init(Cipher.DECRYPT_MODE, k);
            decryptedData = c.doFinal(data);
            if (hasNonce) {
                decryptedData = extractDataAndVerifyNounce(decryptedData);
            }
        } catch (NoSuchAlgorithmException e) {
            throw new AcmEncryptionException("Internal error. No such algorithm " + ENCRYPTION_ALGORITHM, e);
        } catch (NoSuchPaddingException e) {
            throw new AcmEncryptionException("No such padding ", e);
        } catch (InvalidKeyException e) {
            throw new AcmEncryptionBadKeyOrDataException("Invalid key ", e);
        } catch (IllegalBlockSizeException e) {
            throw new AcmEncryptionBadKeyOrDataException("illegal block size ", e);
        } catch (BadPaddingException e) {
            throw new AcmEncryptionBadKeyOrDataException("Bad padding ", e);
        }
        return decryptedData;
    }

    private byte[] extractDataAndVerifyNounce(byte[] decryptedData) throws AcmEncryptionBadKeyOrDataException {
        String decryptedDataString = new String(decryptedData);
        if (!decryptedDataString.contains("-"))
            throw new AcmEncryptionBadKeyOrDataException("Bad key or data");
        String[] splitted = decryptedDataString.split("-");
        if (splitted.length != 6)
            throw new AcmEncryptionBadKeyOrDataException("Bad key or data");

        if (splitted[1].length() != 8
                || splitted[2].length() != 4
                || splitted[3].length() != 4
                || splitted[4].length() != 4
                || splitted[5].length() != 12
                )
            throw new AcmEncryptionBadKeyOrDataException("Bad key or data");


        return splitted[0].getBytes();
    }

}
