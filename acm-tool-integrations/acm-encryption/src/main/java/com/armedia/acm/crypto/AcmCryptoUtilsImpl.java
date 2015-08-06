package com.armedia.acm.crypto;

import com.armedia.acm.core.exceptions.AcmEncryptionBadKeyOrDataException;
import com.armedia.acm.core.exceptions.AcmEncryptionException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPEncryptedDataList;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPPBEEncryptedData;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.jcajce.JcaPGPObjectFactory;
import org.bouncycastle.openpgp.operator.PBEDataDecryptorFactory;
import org.bouncycastle.openpgp.operator.PGPDigestCalculatorProvider;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPDigestCalculatorProviderBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePBEDataDecryptorFactoryBuilder;
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
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Arrays;
import java.util.UUID;

/**
 * Created by nebojsha on 01.05.2015.
 */
public class AcmCryptoUtilsImpl implements AcmCryptoUtils {
    private final Logger log = LoggerFactory.getLogger(getClass());

    public static final String ENCRYPTION_ALGORITHM = "AES";

    public AcmCryptoUtilsImpl() {
        Security.addProvider(new BouncyCastleProvider());
    }

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

    @Override
    public byte[] decryptInputStreamWithPGP(InputStream in, char[] passPhrase) throws AcmEncryptionBadKeyOrDataException {
        //this source is copied from org.bouncycastle.openpgp.examples.PBEFileProcessor.java and slightly modified to return byte []

        ByteArrayOutputStream fOut = null;
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

            PGPDigestCalculatorProvider pgpDigestCalculatorProvider = new JcaPGPDigestCalculatorProviderBuilder().setProvider("BC").build();
            PBEDataDecryptorFactory pbeDataDecryptorFactory = new JcePBEDataDecryptorFactoryBuilder(pgpDigestCalculatorProvider).setProvider("BC").build(passPhrase);
            InputStream clear = pbe.getDataStream(pbeDataDecryptorFactory);

            JcaPGPObjectFactory pgpFact = new JcaPGPObjectFactory(clear);

            //
            // if we're trying to read a file generated by someone other than us
            // the data might not be compressed, so we check the return type from
            // the factory and behave accordingly.
            //
            o = pgpFact.nextObject();
            if (o instanceof PGPCompressedData) {
                PGPCompressedData cData = (PGPCompressedData) o;

                pgpFact = new JcaPGPObjectFactory(cData.getDataStream());

                o = pgpFact.nextObject();
            }

            PGPLiteralData ld = (PGPLiteralData) o;
            InputStream unc = ld.getInputStream();

            fOut = new ByteArrayOutputStream();

            Streams.pipeAll(unc, fOut);

            fOut.close();

            if (pbe.isIntegrityProtected()) {
                if (!pbe.verify()) {
                    System.err.println("message failed integrity check");
                } else {
                    System.err.println("message integrity check passed");
                }
            } else {
                System.err.println("no message integrity check");
            }
        } catch (IOException e) {
            throw new AcmEncryptionBadKeyOrDataException("Bad key or data", e);
        } catch (PGPException e) {
            throw new AcmEncryptionBadKeyOrDataException("Bad key or data", e);
        }

        return fOut.toByteArray();
    }
}
