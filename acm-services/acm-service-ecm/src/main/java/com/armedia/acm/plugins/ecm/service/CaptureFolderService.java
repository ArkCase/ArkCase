package com.armedia.acm.plugins.ecm.service;

import java.io.InputStream;

/**
 * Created by joseph.mcgrady on 9/14/2015.
 */
public interface CaptureFolderService {
    void copyToCaptureHotFolder(String fileName, InputStream fileInputStream);
}