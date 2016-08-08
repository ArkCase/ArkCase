package com.armedia.acm.plugins.ecm.utils;

import com.armedia.acm.plugins.ecm.service.impl.EcmTikaFile;

import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.Detector;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;

import java.io.IOException;
import java.io.InputStream;

public class EcmTikaUtils
{
    private TikaConfig tikaConfig;

    public EcmTikaFile detectFileUsingTika(InputStream inputStream, String fileName) throws IOException, MimeTypeException
    {
        tikaConfig = TikaConfig.getDefaultConfig();
        Detector detector = tikaConfig.getDetector();
        TikaInputStream stream = TikaInputStream.get(inputStream);
        Metadata metadata = new Metadata();
        metadata.add(Metadata.RESOURCE_NAME_KEY, fileName);
        MediaType mediaType = detector.detect(stream, metadata);
        MimeType mimeType = tikaConfig.getMimeRepository().forName(mediaType.toString());

        EcmTikaFile ecmTikaFile = new EcmTikaFile();
        ecmTikaFile.setContentType(mediaType.toString());
        ecmTikaFile.setNameExtension(mimeType.getExtension());
        return ecmTikaFile;
    }

}
