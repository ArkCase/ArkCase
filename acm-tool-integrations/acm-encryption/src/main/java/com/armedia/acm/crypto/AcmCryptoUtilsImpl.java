package com.armedia.acm.crypto;

import com.armedia.acm.core.exceptions.AcmEncryptionBadKeyOrDataException;
import com.armedia.acm.core.exceptions.AcmEncryptionException;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.CompressionAlgorithmTags;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPCompressedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedDataList;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPLiteralDataGenerator;
import org.bouncycastle.openpgp.PGPPBEEncryptedData;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.jcajce.JcaPGPObjectFactory;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPDigestCalculatorProviderBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePBEDataDecryptorFactoryBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePBEKeyEncryptionMethodGenerator;
import org.bouncycastle.openpgp.operator.jcajce.JcePGPDataEncryptorBuilder;
import org.bouncycastle.util.io.Streams;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

/**
 * Created by nebojsha on 01.05.2015.
 */
public class AcmCryptoUtilsImpl implements AcmCryptoUtils
{
    public AcmCryptoUtilsImpl()
    {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * @inheritDoc
     */
    @Override
    public byte[] encryptData(byte[] passPhrase, byte[] data, boolean addNonce) throws AcmEncryptionException
    {
        if (addNonce)
        {
            data = addNonceToData(data);
        }
        byte[] encryptedData = null;
        try
        {
            Cipher c = Cipher.getInstance(CryptoConstants.ENCRYPTION_ALGORITHM);
            SecretKeySpec k = new SecretKeySpec(passPhrase, CryptoConstants.ENCRYPTION_ALGORITHM);
            c.init(Cipher.ENCRYPT_MODE, k);
            encryptedData = c.doFinal(data);

        }
        catch (NoSuchAlgorithmException e)
        {
            throw new AcmEncryptionException("No such algorithm ", e);
        }
        catch (NoSuchPaddingException e)
        {
            throw new AcmEncryptionException("No such padding ", e);
        }
        catch (InvalidKeyException e)
        {
            throw new AcmEncryptionException("Invalid key ", e);
        }
        catch (IllegalBlockSizeException e)
        {
            throw new AcmEncryptionException("illegal block size ", e);
        }
        catch (BadPaddingException e)
        {
            throw new AcmEncryptionException("Bad padding ", e);
        }
        return encryptedData;
    }

    private byte[] addNonceToData(byte[] data)
    {
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
    public byte[] decryptData(byte[] passPhrase, byte[] data, boolean hasNonce) throws AcmEncryptionException
    {
        byte[] decryptedData = null;

        try
        {
            Cipher c = Cipher.getInstance(CryptoConstants.ENCRYPTION_ALGORITHM);
            SecretKeySpec k = new SecretKeySpec(passPhrase, CryptoConstants.ENCRYPTION_ALGORITHM);
            c.init(Cipher.DECRYPT_MODE, k);
            decryptedData = c.doFinal(data);
            if (hasNonce)
            {
                decryptedData = extractDataAndVerifyNounce(decryptedData);
            }
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new AcmEncryptionException("Internal error. No such algorithm " + CryptoConstants.ENCRYPTION_ALGORITHM, e);
        }
        catch (NoSuchPaddingException e)
        {
            throw new AcmEncryptionException("No such padding ", e);
        }
        catch (InvalidKeyException e)
        {
            throw new AcmEncryptionBadKeyOrDataException("Invalid key ", e);
        }
        catch (IllegalBlockSizeException e)
        {
            throw new AcmEncryptionBadKeyOrDataException("illegal block size ", e);
        }
        catch (BadPaddingException e)
        {
            throw new AcmEncryptionBadKeyOrDataException("Bad padding ", e);
        }
        return decryptedData;
    }

    /**
     * @inheritDoc
     */
    @Override
    public byte[] decryptData(PrivateKey key, byte[] data, boolean hasNonce, String encryptionAlgorithm) throws AcmEncryptionException
    {
        byte[] decryptedData = null;

        try
        {
            Cipher c = Cipher.getInstance(encryptionAlgorithm);
            c.init(Cipher.DECRYPT_MODE, key);
            decryptedData = c.doFinal(data);
            if (hasNonce)
            {
                decryptedData = extractDataAndVerifyNounce(decryptedData);
            }
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new AcmEncryptionException("Internal error. No such algorithm " + encryptionAlgorithm, e);
        }
        catch (NoSuchPaddingException e)
        {
            throw new AcmEncryptionException("No such padding ", e);
        }
        catch (InvalidKeyException e)
        {
            throw new AcmEncryptionBadKeyOrDataException("Invalid key ", e);
        }
        catch (IllegalBlockSizeException e)
        {
            throw new AcmEncryptionBadKeyOrDataException("illegal block size ", e);
        }
        catch (BadPaddingException e)
        {
            throw new AcmEncryptionBadKeyOrDataException("Bad padding ", e);
        }
        return decryptedData;
    }

    private byte[] extractDataAndVerifyNounce(byte[] decryptedData) throws AcmEncryptionBadKeyOrDataException
    {
        String decryptedDataString = new String(decryptedData);
        if (!decryptedDataString.contains("-"))
        {
            throw new AcmEncryptionBadKeyOrDataException("Bad key or data");
        }
        String[] splitted = decryptedDataString.split("-");
        if (splitted.length != 6)
        {
            throw new AcmEncryptionBadKeyOrDataException("Bad key or data");
        }

        if (splitted[1].length() != 8 || splitted[2].length() != 4 || splitted[3].length() != 4 || splitted[4].length() != 4
                || splitted[5].length() != 12)
        {
            throw new AcmEncryptionBadKeyOrDataException("Bad key or data");
        }

        return splitted[0].getBytes();
    }

    /**
     * @inheritDoc
     */
    @Override
    public byte[] decryptInputStreamWithPGP(InputStream in, char[] passPhrase) throws AcmEncryptionBadKeyOrDataException
    {
        // this source is copied from org.bouncycastle.openpgp.examples.ByteArrayHandler.java

        try
        {
            in = PGPUtil.getDecoderStream(in);

            JcaPGPObjectFactory pgpF = new JcaPGPObjectFactory(in);
            PGPEncryptedDataList enc;
            Object o = pgpF.nextObject();

            //
            // the first object might be a PGP marker packet.
            //
            if (o instanceof PGPEncryptedDataList)
            {
                enc = (PGPEncryptedDataList) o;
            }
            else
            {
                enc = (PGPEncryptedDataList) pgpF.nextObject();
            }

            PGPPBEEncryptedData pbe = (PGPPBEEncryptedData) enc.get(0);

            InputStream clear = pbe.getDataStream(
                    new JcePBEDataDecryptorFactoryBuilder(new JcaPGPDigestCalculatorProviderBuilder().setProvider("BC").build())
                            .setProvider("BC").build(passPhrase));

            JcaPGPObjectFactory pgpFact = new JcaPGPObjectFactory(clear);

            PGPCompressedData cData = (PGPCompressedData) pgpFact.nextObject();

            pgpFact = new JcaPGPObjectFactory(cData.getDataStream());

            PGPLiteralData ld = (PGPLiteralData) pgpFact.nextObject();

            return Streams.readAll(ld.getInputStream());
        }
        catch (IOException e)
        {
            throw new AcmEncryptionBadKeyOrDataException("Bad key or data", e);
        }
        catch (PGPException e)
        {
            throw new AcmEncryptionBadKeyOrDataException("Bad key or data", e);
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public byte[] encryptWithPGP(byte[] clearData, char[] passPhrase, String fileName, int algorithm, boolean armor)
            throws AcmEncryptionBadKeyOrDataException
    {
        try
        {
            if (fileName == null)
            {
                fileName = PGPLiteralData.CONSOLE;
            }

            byte[] compressedData = compress(clearData, fileName, CompressionAlgorithmTags.ZIP);

            ByteArrayOutputStream bOut = new ByteArrayOutputStream();

            OutputStream out = bOut;
            if (armor)
            {
                out = new ArmoredOutputStream(out);
            }

            PGPEncryptedDataGenerator encGen = new PGPEncryptedDataGenerator(
                    new JcePGPDataEncryptorBuilder(algorithm).setSecureRandom(new SecureRandom()).setProvider("BC"));
            encGen.addMethod(new JcePBEKeyEncryptionMethodGenerator(passPhrase).setProvider("BC"));

            OutputStream encOut = encGen.open(out, compressedData.length);

            encOut.write(compressedData);
            encOut.close();

            if (armor)
            {
                out.close();
            }

            return bOut.toByteArray();
        }
        catch (PGPException e)
        {
            throw new AcmEncryptionBadKeyOrDataException("Error encrypting data with PGP.", e);
        }
        catch (IOException e)
        {
            throw new AcmEncryptionBadKeyOrDataException("Error encrypting data with PGP.", e);
        }
    }

    private byte[] compress(byte[] clearData, String fileName, int algorithm) throws IOException
    {
        // this source is copied from org.bouncycastle.openpgp.examples.ByteArrayHandler.java

        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        PGPCompressedDataGenerator comData = new PGPCompressedDataGenerator(algorithm);
        OutputStream cos = comData.open(bOut); // open it with the final destination

        PGPLiteralDataGenerator lData = new PGPLiteralDataGenerator();

        // we want to generate compressed data. This might be a user option later,
        // in which case we would pass in bOut.
        OutputStream pOut = lData.open(cos, // the compressed output stream
                PGPLiteralData.BINARY, fileName, // "filename" to store
                clearData.length, // length of clear data
                new Date() // current time
        );

        pOut.write(clearData);
        pOut.close();

        comData.close();

        return bOut.toByteArray();
    }

    /**
     * @inheritDoc
     */
    @Override
    public byte[] decryptData(byte[] passPhrase, byte[] data, int keySize, int ivSize, int magicSize, int saltSize,
            int passPhraseIterations, String passPhraseHashAlgorithm, String encryptionAlgorithm, String blockCipherMode, String padding)
            throws AcmEncryptionBadKeyOrDataException
    {
        byte[] decryptedData = null;

        try
        {
            byte[] saltBytes = Arrays.copyOfRange(data, magicSize, magicSize + saltSize);
            byte[] ciphertextBytes = Arrays.copyOfRange(data, magicSize + saltSize, data.length);

            byte[] key = new byte[keySize / 8];
            byte[] iv = new byte[ivSize / 8];
            deriveKeyIV(passPhrase, keySize, ivSize, saltBytes, passPhraseIterations, passPhraseHashAlgorithm, key, iv);

            Cipher cipher = Cipher.getInstance(encryptionAlgorithm + "/" + blockCipherMode + "/" + padding);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, encryptionAlgorithm), new IvParameterSpec(iv));
            decryptedData = cipher.doFinal(ciphertextBytes);
        }
        catch (Exception e)
        {
            throw new AcmEncryptionBadKeyOrDataException("Error decrypting data.", e);
        }

        return decryptedData;
    }

    private byte[] deriveKeyIV(byte[] passPhrase, int keySize, int ivSize, byte[] salt, int iterations, String hashAlgorithm,
            byte[] resultKey, byte[] resultIv) throws NoSuchAlgorithmException
    {
        keySize = keySize / 32;
        ivSize = ivSize / 32;
        int targetKeySize = keySize + ivSize;
        byte[] derivedBytes = new byte[targetKeySize * 4];
        int numberOfDerivedWords = 0;
        byte[] block = null;
        MessageDigest hasher = MessageDigest.getInstance(hashAlgorithm);
        while (numberOfDerivedWords < targetKeySize)
        {
            if (block != null)
            {
                hasher.update(block);
            }
            hasher.update(passPhrase);
            block = hasher.digest(salt);
            hasher.reset();

            // Iterations
            for (int i = 1; i < iterations; i++)
            {
                block = hasher.digest(block);
                hasher.reset();
            }

            System.arraycopy(block, 0, derivedBytes, numberOfDerivedWords * 4,
                    Math.min(block.length, (targetKeySize - numberOfDerivedWords) * 4));

            numberOfDerivedWords += block.length / 4;
        }

        System.arraycopy(derivedBytes, 0, resultKey, 0, keySize * 4);
        System.arraycopy(derivedBytes, keySize * 4, resultIv, 0, ivSize * 4);

        return derivedBytes; // key + iv
    }

    /**
     * @inheritDoc
     */
    @Override
    public byte[] encryptData(byte[] passPhrase, byte[] data, int keySize, int ivSize, int magicSize, int saltSize,
            int passPhraseIterations, String passPhraseHashAlgorithm, String encryptionAlgorithm, String blockCipherMode, String padding)
            throws AcmEncryptionException
    {

        byte[] encryptedData = null;
        try
        {
            byte[] magic = getRandomBytes(magicSize);

            byte[] saltBytes = getRandomBytes(saltSize);

            byte[] key = new byte[keySize / 8];
            byte[] iv = new byte[ivSize / 8];
            deriveKeyIV(passPhrase, keySize, ivSize, saltBytes, passPhraseIterations, passPhraseHashAlgorithm, key, iv);
            // KeySpec keySpec = new PBEKeySpec("AcMd3v$".toCharArray(), saltBytes, passPhraseIterations);
            // SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndAES").generateSecret(keySpec);
            // AlgorithmParameterSpec paramSpec = new PBEParameterSpec(saltBytes, passPhraseIterations, new IvParameterSpec(iv));

            Cipher cipher = Cipher.getInstance(encryptionAlgorithm + "/" + blockCipherMode + "/" + padding);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, encryptionAlgorithm), new IvParameterSpec(iv));
            byte[] ciphertextBytes = cipher.doFinal(data);

            // concat salt bytes
            encryptedData = new byte[magic.length + saltBytes.length + ciphertextBytes.length];
            System.arraycopy(magic, 0, encryptedData, 0, magic.length);
            System.arraycopy(saltBytes, 0, encryptedData, magic.length, saltBytes.length);
            System.arraycopy(ciphertextBytes, 0, encryptedData, magic.length + saltBytes.length, ciphertextBytes.length);
        }
        catch (Exception e)
        {
            throw new AcmEncryptionBadKeyOrDataException("Error decrypting data.", e);
        }

        return encryptedData;
    }

    private byte[] getRandomBytes(int size)
    {
        if (size == 0)
        {
            return new byte[0];
        }
        byte[] bytes = new byte[size];
        new SecureRandom().nextBytes(bytes);
        return bytes;
    }
}
