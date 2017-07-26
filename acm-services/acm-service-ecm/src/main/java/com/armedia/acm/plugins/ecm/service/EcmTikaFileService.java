package com.armedia.acm.plugins.ecm.service;

import com.armedia.acm.plugins.ecm.service.impl.EcmTikaFile;
import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;

public interface EcmTikaFileService
{
    EcmTikaFile detectFileUsingTika(InputStream inputStream, byte[] fileBytes) throws IOException, SAXException, TikaException;
}
