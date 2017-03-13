package com.armedia.acm.plugins.ecm.web;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Decoder;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
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
import java.net.URLDecoder;
import java.security.InvalidKeyException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;

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

        private static final String SECRET_KEY = "YXJtZWRpYWFlc2tleTEyMw==";
        private static final String IV = "YXJtZWRpYWFlc2l2MTIzNA==";
        private static Logger log = LoggerFactory.getLogger(DecryptRequest.class);

        public DecryptRequest(ServletRequest request)
        {
            super((HttpServletRequest) request);
        }

        static String decrypt(String encrypted)
        {
            try
            {
                log.debug("Decrypting string: {}", encrypted);
                String decoded = URLDecoder.decode(encrypted, "UTF-8");
                SecretKey key = new SecretKeySpec(Base64.decodeBase64(SECRET_KEY), "AES");
                AlgorithmParameterSpec iv = new IvParameterSpec(Base64.decodeBase64(IV));
                byte[] decodeBase64 = new BASE64Decoder().decodeBuffer(decoded);
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, key, iv);
                return new String(cipher.doFinal(decodeBase64), "UTF-8");
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

        public String[] getParameterValues(String parameter)
        {
            if ("credentials".equals(parameter))
            {
                String[] values = super.getParameterValues(parameter);
                return Arrays.stream(values)
                        .map(DecryptRequest::decrypt)
                        .toArray(String[]::new);
            }
            return super.getParameterValues(parameter);
        }
    }
}
