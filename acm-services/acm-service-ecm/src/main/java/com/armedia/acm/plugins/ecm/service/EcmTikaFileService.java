package com.armedia.acm.plugins.ecm.service;

import com.armedia.acm.plugins.ecm.service.impl.EcmTikaFile;

import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

public interface EcmTikaFileService
{
    EcmTikaFile detectFileUsingTika(byte[] fileBytes, String fileName) throws IOException, SAXException, TikaException;

    EcmTikaFile detectFileUsingTika(File file, String fileName) throws IOException, SAXException, TikaException;
}
