package com.armedia.acm.plugins.casefile.service;

/*-
 * #%L
 * ACM Default Plugin: Case File
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

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.ChangeCaseStateContants;
import com.armedia.acm.plugins.casefile.model.ChangeCaseStatus;
import com.armedia.acm.plugins.casefile.pipeline.CaseFilePipelineContext;
import com.armedia.acm.plugins.ecm.service.PDFDocumentGenerator;
import com.armedia.acm.services.pipeline.AbstractPipelineContext;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;

import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class PDFChangeCaseFileStateDocumentGenerator<D extends AcmAbstractDao, T extends CaseFile> extends PDFDocumentGenerator
{
    private D dao;

    public void generatePdf(String objectType, Long caseFileId, CaseFilePipelineContext ctx)
            throws PipelineProcessException
    {
        try
        {
            CaseFile caseFile = (CaseFile) getDao().find(caseFileId);
            generatePdf(objectType, caseFileId, ctx, ctx.getAuthentication(), caseFile, caseFile.getContainer(),
                    ChangeCaseStateContants.CHANGE_CASE_STATUS_STYLESHEET,
                    ChangeCaseStateContants.CHANGE_CASE_STATUS, ChangeCaseStateContants.CHANGE_CASE_STATUS_FILENAMEFORMAT);
        }
        catch (ParserConfigurationException e)
        {
            throw new PipelineProcessException(e);
        }
    }

    @Override
    public Document buildXmlForPdfDocument(Object businessObject, AbstractPipelineContext ctx) throws ParserConfigurationException
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbf.newDocumentBuilder();
        Document document = docBuilder.newDocument();

        Element rootElem = document.createElement("changeCaseFileState");
        document.appendChild(rootElem);

        ChangeCaseStatus changeCaseStatus = ((CaseFilePipelineContext) ctx).getChangeCaseStatus();

        addElement(document, rootElem, "changeDate", ctx.getPropertyValue("changeDate").toString(),
                true);

        addElement(document, rootElem, "caseNumber", changeCaseStatus.getCaseId().toString(),
                true);

        addElement(document, rootElem, "status", changeCaseStatus.getStatus(),
                true);

        if (!changeCaseStatus.getStatus().isEmpty())
        {
            addElement(document, rootElem, "caseResolution", ctx.getPropertyValue("caseResolution").toString(),
                    true);
        }

        if(!changeCaseStatus.getParticipants().isEmpty()){
            addElement(document, rootElem, "participant", changeCaseStatus.getParticipants().size() > 0 ?  changeCaseStatus.getParticipants().get(0).getParticipantLdapId() : "N/A", false);
        }

        return document;
    }

    public D getDao()
    {
        return dao;
    }

    public void setDao(D dao)
    {
        this.dao = dao;
    }
}
