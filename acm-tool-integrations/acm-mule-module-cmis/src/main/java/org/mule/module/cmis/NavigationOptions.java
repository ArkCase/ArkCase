/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.cmis;

/**
 * Enum holding all the possible navigation options
 */
public enum NavigationOptions {
    /**
     * Returns the parent Folder
     */
    PARENT,
    /**
     * Returns a list of objects contained in the current folder
     */
    CHILDREN,
    /**
     * Returns the whole descentants tree of the current folder
     */
    DESCENDANTS,
    /**
     * Returns the folder tree starting with this folder.
     */
    TREE;
}