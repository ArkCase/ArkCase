package com.armedia.acm.plugins.onlyoffice.service;

/*-
 * #%L
 * ACM Extra Plugin: OnlyOffice Integration
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

import com.armedia.acm.plugins.onlyoffice.exceptions.OnlyOfficeException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;

public class JWTSigningServiceImpl implements JWTSigningService {
    private String jwtOutboundAlgorithm;
    private String jwtOutboundKey;
    private String jwtOutboundKeyStore;
    private String jwtOutboundCertificateAlias;

    private String jwtInboundKey;
    private String jwtInboundTrustStore;

    @Override
    public boolean verifyToken(String token)
    {
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
