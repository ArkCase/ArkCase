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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Date;
import java.util.UUID;

/**
 * Created by nebojsha on 01.05.2015.
 */
public class AcmCryptoUtilsImpl implements AcmCryptoUtils {
    private final Logger log = LoggerFactory.getLogger(getClass());

    public AcmCryptoUtilsImpl() {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * @inheritDoc
     */
    @Override
    public byte[] encryptData(byte[] passPhrase, byte[] data, boolean addNonce) throws AcmEncryptionException {
        if (addNonce)
            data = addNonceToData(data);
        byte[] encryptedData = null;
        try {
            Cipher c = Cipher.getInstance(CryptoConstants.ENCRYPTION_ALGORITHM);
            SecretKeySpec k =
                    new SecretKeySpec(passPhrase, CryptoConstants.ENCRYPTION_ALGORITHM);
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

        try {
            Cipher c = Cipher.getInstance(CryptoConstants.ENCRYPTION_ALGORITHM);
            SecretKeySpec k =
                    new SecretKeySpec(passPhrase, CryptoConstants.ENCRYPTION_ALGORITHM);
            c.init(Cipher.DECRYPT_MODE, k);
            decryptedData = c.doFinal(data);
            if (hasNonce) {
                decryptedData = extractDataAndVerifyNounce(decryptedData);
            }
        } catch (NoSuchAlgorithmException e) {
            throw new AcmEncryptionException("Internal error. No such algorithm " + CryptoConstants.ENCRYPTION_ALGORITHM, e);
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

    @Override
    public byte[] decryptInputStreamWithPGP(InputStream in, char[] passPhrase) throws AcmEncryptionBadKeyOrDataException {
        //this source is copied from org.bouncycastle.openpgp.examples.ByteArrayHandler.java

        try {
            in = PGPUtil.getDecoderStream(in);

            JcaPGPObjectFactory pgpF = new JcaPGPObjectFactory(in);
            PGPEncryptedDataList enc;
            Object o = pgpF.nextObject();

            //
            // the first object might be a PGP marker packet.
            //
            if (o instanceof PGPEncryptedDataList) {
                enc = (PGPEncryptedDataList) o;
            } else {
                enc = (PGPEncryptedDataList) pgpF.nextObject();
            }

            PGPPBEEncryptedData pbe = (PGPPBEEncryptedData) enc.get(0);

            InputStream clear = pbe.getDataStream(new JcePBEDataDecryptorFactoryBuilder(new JcaPGPDigestCalculatorProviderBuilder().setProvider("BC").build()).setProvider("BC").build(passPhrase));

            JcaPGPObjectFactory pgpFact = new JcaPGPObjectFactory(clear);

            PGPCompressedData cData = (PGPCompressedData) pgpFact.nextObject();

            pgpFact = new JcaPGPObjectFactory(cData.getDataStream());

            PGPLiteralData ld = (PGPLiteralData) pgpFact.nextObject();

            return Streams.readAll(ld.getInputStream());
        } catch (IOException e) {
            throw new AcmEncryptionBadKeyOrDataException("Bad key or data", e);
        } catch (PGPException e) {
            throw new AcmEncryptionBadKeyOrDataException("Bad key or data", e);
        }
    }

    /**
     * Simple PGP encryptor between byte[].
     *
     * @param clearData  The test to be encrypted
     * @param passPhrase The pass phrase (key).  This method assumes that the
     *                   key is a simple pass phrase, and does not yet support
     *                   RSA or more sophisticated keying.
     * @param fileName   File name. This is used in the Literal Data Packet (tag 11)
     *                   which is really only important if the data is to be
     *                   related to a file to be recovered later.  Because this
     *                   routine does not know the source of the information, the
     *                   caller can set something here for file name use that
     *                   will be carried.  If this routine is being used to
     *                   encrypt SOAP MIME bodies, for example, use the file name from the
     *                   MIME type, if applicable. Or anything else appropriate.
     * @param armor
     * @return encrypted data.
     * @throws IOException
     * @throws PGPException
     * @throws java.security.NoSuchProviderException
     */
    @Override
    public byte[] encryptWithPGP(
            byte[] clearData,
            char[] passPhrase,
            String fileName,
            int algorithm,
            boolean armor) throws AcmEncryptionBadKeyOrDataException {
        //this source is copied from org.bouncycastle.openpgp.examples.ByteArrayHandler.java

        try {
            if (fileName == null) {
                fileName = PGPLiteralData.CONSOLE;
            }

            byte[] compressedData = compress(clearData, fileName, CompressionAlgorithmTags.ZIP);

            ByteArrayOutputStream bOut = new ByteArrayOutputStream();

            OutputStream out = bOut;
            if (armor) {
                out = new ArmoredOutputStream(out);
            }

            PGPEncryptedDataGenerator encGen = new PGPEncryptedDataGenerator(new JcePGPDataEncryptorBuilder(algorithm).setSecureRandom(new SecureRandom()).setProvider("BC"));
            encGen.addMethod(new JcePBEKeyEncryptionMethodGenerator(passPhrase).setProvider("BC"));

            OutputStream encOut = encGen.open(out, compressedData.length);

            encOut.write(compressedData);
            encOut.close();

            if (armor) {
                out.close();
            }

            return bOut.toByteArray();
        } catch (PGPException e) {
            throw new AcmEncryptionBadKeyOrDataException("Error encrypting data with PGP.", e);
        } catch (IOException e) {
            throw new AcmEncryptionBadKeyOrDataException("Error encrypting data with PGP.", e);
        }
    }

    private byte[] compress(byte[] clearData, String fileName, int algorithm) throws IOException {
        //this source is copied from org.bouncycastle.openpgp.examples.ByteArrayHandler.java

        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        PGPCompressedDataGenerator comData = new PGPCompressedDataGenerator(algorithm);
        OutputStream cos = comData.open(bOut); // open it with the final destination

        PGPLiteralDataGenerator lData = new PGPLiteralDataGenerator();

        // we want to generate compressed data. This might be a user option later,
        // in which case we would pass in bOut.
        OutputStream pOut = lData.open(cos, // the compressed output stream
                PGPLiteralData.BINARY,
                fileName,  // "filename" to store
                clearData.length, // length of clear data
                new Date()  // current time
        );

        pOut.write(clearData);
        pOut.close();

        comData.close();

        return bOut.toByteArray();
    }
}
