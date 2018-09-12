package com.armedia.acm.plugins.onlyoffice.service;

import com.armedia.acm.plugins.onlyoffice.exceptions.OnlyOfficeException;
import com.armedia.acm.plugins.onlyoffice.model.callback.CallBackData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import org.apache.commons.beanutils.BeanUtils;

public class JWTSigningServiceImpl implements JWTSigningService {
    private String jwtOutboundAlgorithm;
    private String jwtOutboundKey;
    private String jwtOutboundKeyStore;
    private String jwtOutboundCertificateAlias;

    private String jwtInboundKey;
    private String jwtInboundTrustStore;
    private ObjectMapper mapper;

    @Override
    public boolean verifyToken(String callBackData, String token) {
        try {
            JWSVerifier verifier = new MACVerifier(getKey(jwtInboundKey));

            JWSObject jwsObject = JWSObject.parse(token);
            System.out.println(jwsObject.getPayload().toString());

            CallBackData callBackDataFromToken = mapper.readValue(jwsObject.getPayload().toString(), CallBackData.class);
            CallBackData callBackDataOriginal = mapper.readValue(callBackData, CallBackData.class);
            callBackDataOriginal.
        } catch (Exception e) {
            throw new OnlyOfficeException("Can't verify payload. Reason: " + e.getMessage(), e);
        }
    }

    private boolean verifyToken(String token) {
        try {
            JWSVerifier verifier = new MACVerifier(getKey(jwtInboundKey));

            JWSObject jwsObject = JWSObject.parse(token);

            return jwsObject.verify(verifier);
        } catch (Exception e) {
            throw new OnlyOfficeException("Can't verify payload. Reason: " + e.getMessage(), e);
        }
    }


    @Override
    public String signJsonPayload(String jsonString) {
        try {
            JWSAlgorithm jwsAlgorithm = new JWSAlgorithm(jwtOutboundAlgorithm);

            JWSSigner signer = new MACSigner(getKey(jwtOutboundKey));

            JWSHeader header = new JWSHeader(jwsAlgorithm);
            JWSObject jwsObject = new JWSObject(header, new Payload(jsonString));

            jwsObject.sign(signer);

            String s = jwsObject.serialize();

            return s;
        } catch (Exception e) {
            throw new OnlyOfficeException("Can't sign payload. Reason: " + e.getMessage(), e);
        }
    }

    private byte[] getKey(String key) {
        try {
            if (jwtOutboundAlgorithm.startsWith("HS")) {
                //for HS algorithm key must be exact length as algorithm is chosen, i.e. if HS256 than key length must be 256 bits or 32 bytes
                int keyExpectedLength = Integer.valueOf(jwtOutboundAlgorithm.substring(2)) / 8;
                byte[] jwtOutboundKeyBytes = key.getBytes("UTF8");

                byte[] sharedSecret = new byte[keyExpectedLength];

                if (keyExpectedLength > jwtOutboundKeyBytes.length) {
                    //if key length is smaller that expected, fill with zeroes
                    System.arraycopy(jwtOutboundKeyBytes, 0, sharedSecret, 0, jwtOutboundKeyBytes.length);
                } else if (keyExpectedLength < jwtOutboundKeyBytes.length) {
                    //if key length is bigger that expected, remove after expected length
                    System.arraycopy(jwtOutboundKeyBytes, 0, sharedSecret, 0, keyExpectedLength);
                } else if (keyExpectedLength == jwtOutboundKeyBytes.length) {
                    sharedSecret = jwtOutboundKeyBytes;
                }

                return sharedSecret;
            } else {
                //TODO implement for other algorithms as needed
                throw new OnlyOfficeException("Algorithm [" + jwtOutboundAlgorithm + "] not supported.");
            }
        } catch (Exception e) {
            throw new OnlyOfficeException("Can't get key. Reason: " + e.getMessage());
        }
    }

    public void setJwtOutboundAlgorithm(String jwtOutboundAlgorithm) {
        this.jwtOutboundAlgorithm = jwtOutboundAlgorithm;
    }

    public void setJwtOutboundKey(String jwtOutboundKey) {
        this.jwtOutboundKey = jwtOutboundKey;
    }

    public void setJwtOutboundKeyStore(String jwtOutboundKeyStore) {
        this.jwtOutboundKeyStore = jwtOutboundKeyStore;
    }

    public void setJwtOutboundCertificateAlias(String jwtOutboundCertificateAlias) {
        this.jwtOutboundCertificateAlias = jwtOutboundCertificateAlias;
    }

    public void setJwtInboundKey(String jwtInboundKey) {
        this.jwtInboundKey = jwtInboundKey;
    }

    public void setJwtInboundTrustStore(String jwtInboundTrustStore) {
        this.jwtInboundTrustStore = jwtInboundTrustStore;
    }
}
