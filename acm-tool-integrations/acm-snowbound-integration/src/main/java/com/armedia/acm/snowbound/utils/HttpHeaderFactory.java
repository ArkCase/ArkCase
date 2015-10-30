package com.armedia.acm.snowbound.utils;

/**
 * Created by joseph.mcgrady on 8/30/2015.
 */
public class HttpHeaderFactory {

    public static String createMultipartFormContentTypeHeader(String multipartBoundary) {
        return "multipart/form-data; boundary=" + multipartBoundary;
    }

    public static String createFileContentDispositionHeader(String fileName) {
        return "Content-Disposition: form-data; name=\"files[]\"; filename=\"" + fileName + "\"";
    }

    public static String createContentTypeHeader(String contentMimeType) {
        return "Content-Type: " + contentMimeType;
    }

    public static String createContentTransferEncodingHeader(String encodingType) {
        return "Content-Transfer-Encoding: " + encodingType;
    }
}