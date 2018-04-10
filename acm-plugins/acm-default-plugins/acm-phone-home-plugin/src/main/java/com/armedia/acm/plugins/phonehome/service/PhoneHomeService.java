package com.armedia.acm.plugins.phonehome.service;

import com.armedia.acm.plugins.stateofarkcaseplugin.service.AcmStateOfArkcaseService;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

public class PhoneHomeService
{

    private MultipartRequestGateway gateway;
    private AcmStateOfArkcaseService stateOfArkcaseService;

    public void uploadFile(File file)
    {
        Resource streamResource = new FileSystemResource(file);
        Map<String, Object> params = new HashMap<>();
        params.put("report", streamResource);
        gateway.postMultipartRequest(params);
    }

    public void generateAndUploadReportFile()
    {
        uploadFile(stateOfArkcaseService.generateReportForDay(LocalDate.now().minus(1, ChronoUnit.DAYS)));
    }

    public void setStateOfArkcaseService(AcmStateOfArkcaseService stateOfArkcaseService)
    {
        this.stateOfArkcaseService = stateOfArkcaseService;
    }

    public void setGateway(MultipartRequestGateway gateway)
    {
        this.gateway = gateway;
    }
}
