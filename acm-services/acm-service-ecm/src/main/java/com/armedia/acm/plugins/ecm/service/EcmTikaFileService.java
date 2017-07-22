package com.armedia.acm.plugins.ecm.service;

import com.armedia.acm.plugins.ecm.service.impl.EcmTikaFile;
import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;

public interface EcmTikaFileService
{
    EcmTikaFile detectFileUsingTika(InputStream inputStream, String fileName) throws IOException, SAXException, TikaException;
}
