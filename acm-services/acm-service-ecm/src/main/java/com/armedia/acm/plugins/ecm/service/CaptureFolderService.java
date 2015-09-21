package com.armedia.acm.plugins.ecm.service;

import com.armedia.acm.plugins.ecm.exception.EphesoftException;
import com.armedia.acm.plugins.ecm.model.EcmFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by joseph.mcgrady on 9/14/2015.
 */
public interface CaptureFolderService {
    void copyToCaptureHotFolder(EcmFile ephesoftFile, InputStream fileInputStream) throws EphesoftException, IOException;
}