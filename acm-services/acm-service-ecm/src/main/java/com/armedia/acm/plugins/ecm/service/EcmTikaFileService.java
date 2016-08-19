package com.armedia.acm.plugins.ecm.service;

import com.armedia.acm.plugins.ecm.service.impl.EcmTikaFile;

import org.apache.tika.mime.MimeTypeException;

import java.io.IOException;
import java.io.InputStream;

public interface EcmTikaFileService
{
    public EcmTikaFile detectFileUsingTika(InputStream inputStream, String fileName) throws IOException, MimeTypeException;
}
