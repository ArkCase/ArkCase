/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.cmis;

/**
 * Connection Type for CMIS
 */
public enum CMISConnectionType {
    ATOM("ATOM"),
    SOAP("SOAP");

    private String value;

    CMISConnectionType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return this.value();
    }
}
