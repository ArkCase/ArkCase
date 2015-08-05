package com.armedia.acm.frevvo.model;

import com.armedia.acm.plugins.ecm.model.EcmFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by armdev on 11/4/14.
 */
public class FrevvoUploadedFiles
{
    private EcmFile pdfRendition;
    private EcmFile formXml;
    private List<EcmFile> uploadedFiles = new ArrayList<>();

    public EcmFile getPdfRendition()
    {
        return pdfRendition;
    }

    public void setPdfRendition(EcmFile pdfRendition)
    {
        this.pdfRendition = pdfRendition;
    }

    public EcmFile getFormXml()
    {
        return formXml;
    }

    public void setFormXml(EcmFile formXml)
    {
        this.formXml = formXml;
    }

    public List<EcmFile> getUploadedFiles()
    {
        return uploadedFiles;
    }

    public void setUploadedFiles(List<EcmFile> uploadedFiles)
    {
        this.uploadedFiles = uploadedFiles;
    }
}
