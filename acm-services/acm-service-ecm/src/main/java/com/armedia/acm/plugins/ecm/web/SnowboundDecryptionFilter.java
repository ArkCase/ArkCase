package com.armedia.acm.plugins.ecm.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

/**
 * URL Decryption filter
 */
public class SnowboundDecryptionFilter implements Filter
{
    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException
    {
        filterChain.doFilter(new DecryptRequest(servletRequest), servletResponse);
    }

    @Override
    public void destroy()
    {

    }

    static class DecryptRequest extends HttpServletRequestWrapper
    {

        private static final String SECRET_KEY = "armediaaeskey123"; // use 16 bytes string
        private static Logger log = LoggerFactory.getLogger(DecryptRequest.class);

        public DecryptRequest(ServletRequest request)
        {
            super((HttpServletRequest) request);
        }

        public static byte[] deriveKeyIV(byte[] password, int keySize, int ivSize, byte[] salt, byte[] resultKey, byte[] resultIv) throws NoSuchAlgorithmException
        {
            return deriveKeyIV(password, keySize, ivSize, salt, 1, "MD5", resultKey, resultIv);
        }

        public static byte[] deriveKeyIV(byte[] password, int keySize, int ivSize, byte[] salt, int iterations,
                                         String hashAlgorithm, byte[] resultKey, byte[] resultIv) throws NoSuchAlgorithmException
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
                hasher.update(password);
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

        static String decrypt(String encrypted)
        {
            try
            {
                log.debug("Decrypting AES encrypted string: {}", encrypted);
                byte[] decodeBase64 = Base64.getDecoder().decode(encrypted.getBytes("UTF-8"));

                byte[] saltBytes = Arrays.copyOfRange(decodeBase64, 8, 16);
                byte[] ciphertextBytes = Arrays.copyOfRange(decodeBase64, 16, decodeBase64.length);

                int keySize = 256;
                int ivSize = 128;

                byte[] key = new byte[keySize / 8];
                byte[] iv = new byte[ivSize / 8];

                deriveKeyIV(SECRET_KEY.getBytes("UTF-8"), keySize, ivSize, saltBytes, key, iv);

                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));

                byte[] recoveredPlaintextBytes = cipher.doFinal(ciphertextBytes);
                String recoveredPlaintext = new String(recoveredPlaintextBytes);
                return recoveredPlaintext;
            } catch (NoSuchPaddingException e)
            {
                log.warn("Bad padding", e);
            } catch (InvalidKeyException e)
            {
                log.warn("Invalid key", e);
            } catch (IllegalBlockSizeException e)
            {
                log.warn("Illegal block size", e);
            } catch (BadPaddingException e)
            {
                log.warn("Bad padding", e);
            } catch (Exception e)
            {
                log.warn("Invalid key", e);
            }
            log.debug("Decryption failed. Returning original string: {}", encrypted);
            return encrypted;
        }

        @Override
        public String getQueryString()
        {
            String url = super.getQueryString();
            return decrypt(url.substring(0, url.indexOf("&noCache")));
        }
    }
}
