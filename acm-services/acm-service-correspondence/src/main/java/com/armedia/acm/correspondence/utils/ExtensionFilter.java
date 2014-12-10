package com.armedia.acm.correspondence.utils;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by marjan.stefanoski on 06.12.2014.
 */
public class ExtensionFilter implements FilenameFilter {
    String ext;

    public ExtensionFilter(String ext) {
        this.ext = "." + ext;
    }

    public boolean accept(File dir, String name) {
        return name.endsWith(ext);
    }
}
