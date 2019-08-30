package com.armedia.acm.configuration.service;

import org.springframework.core.io.InputStreamResource;

import java.io.IOException;

public interface FileConfigurationService
{

    void moveFileToConfiguration(InputStreamResource file, String fileName) throws IOException;

    void getFileFromConfiguration(String fileName, String customFilesLocation) throws IOException;

}
