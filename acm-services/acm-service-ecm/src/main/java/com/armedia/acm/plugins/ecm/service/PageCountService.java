package com.armedia.acm.plugins.ecm.service;

import java.io.IOException;

public interface PageCountService
{
    int getNumberOfPages(String mimeType, byte[] data) throws IOException;
}
