package com.armedia.acm.frevvo.model;

/*-
 * #%L
 * ACM Service: Form Configuration
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.plugins.ecm.model.EcmFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by armdev on 11/4/14.
 *
 * OVA CLASS TREBA da se preimenuva so cel da mozi da se koristi od poke mesta
 *
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
