package com.armedia.acm.crypto.properties;

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

import com.armedia.acm.crypto.exceptions.AcmEncryptionException;

import java.util.Map;

/**
 * Utility class used when encrypting/decrypting values in properties files.
 * <p>
 * A value is considered "encrypted" when it appears surrounded by <tt>ENC(...)</tt>, like:
 * <p>
 * <center><tt>my.value=ENC(!"DGAS24FaIO$)</tt></center>
 * <p>
 * Created by Bojan Milenkoski on 20.4.2016
 */
public interface AcmEncryptablePropertyUtils
{
    /**
     * Returns the decrypted value of the symmetric key as a byte array.
     *
     * @return the decrypted value of the symmetric key as a byte array.
     * @throws AcmEncryptionException
     */
    byte[] decryptSymmetricKey() throws AcmEncryptionException;

    /**
     * Returns a decrypted property value if the original value is surrounded by <tt>ENC(...)</tt>. Otherwise returns
     * the original value.
     *
     * @param originalValue
     *            the encrypted value to decrypt
     * @return decrypted value
     * @throws AcmEncryptionException
     */
    String decryptPropertyValue(final String originalValue) throws AcmEncryptionException;

    /**
     * Returns an encrypted property value for the given original value. The encrypted value is surrounded by
     * <tt>ENC(...)</tt>. If the
     * original value already is surrounded by <tt>ENC(...)</tt>, then the original value is returned.
     *
     * @param originalValue
     *            the value to encrypt
     * @return encrypted value surrounded by <tt>ENC(...)</tt>
     * @throws AcmEncryptionException
     */
    String encryptPropertyValue(String originalValue) throws AcmEncryptionException;

    void decryptProperties(Map<? extends Object, Object> toBeDecrypted);
}
