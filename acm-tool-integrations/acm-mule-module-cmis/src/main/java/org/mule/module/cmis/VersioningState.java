/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.cmis;

/**
 * Enum holding all the possible versioning states
 */
public enum VersioningState {
    /**
     * The document MUST be created as a non-versionable document.
     */
    NONE("none"),

    /**
     * The document MUST be created as a major version
     */
    MAJOR("major"),

    /**
     * The document MUST be created as a minor version.
     */
    MINOR("minor"),

    /**
     * The document MUST be created in the checked-out state.
     */
    CHECKEDOUT("checkedout");


    private final String value;

    VersioningState(String v) {
        value = v;
    }

    public String value() {
        return value;
    }
}