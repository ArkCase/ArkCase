package com.armedia.acm.plugins.complaint.service;

/*-
 * #%L
 * ACM Default Plugin: Complaints
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
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.complaint.model.CloseComplaintConstants;
import com.armedia.acm.plugins.complaint.model.CloseComplaintRequest;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.pipeline.CloseComplaintPipelineContext;
import com.armedia.acm.plugins.ecm.service.PDFDocumentGenerator;
import com.armedia.acm.services.pipeline.AbstractPipelineContext;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.time.format.DateTimeFormatter;

public class PDFCloseComplaintDocumentGenerator<D extends AcmAbstractDao, T extends Complaint> extends PDFDocumentGenerator
{
    private final DateTimeFormatter datePattern = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private D dao;

    private CaseFileDao caseFileDao;

    public void generatePdf(String objectType, Long complaintId, CloseComplaintPipelineContext ctx)
            throws PipelineProcessException
    {
        try
        {

            Complaint complaint = (Complaint) getDao().find(complaintId);
            generatePdf(objectType, complaintId, ctx, ctx.getAuthentication(), complaint, complaint.getContainer(),
                    CloseComplaintConstants.CLOSE_COMPLAINT_STYLESHEET,
                    CloseComplaintConstants.CLOSE_COMPLAINT_DOCUMENT, CloseComplaintConstants.CLOSE_COMPLAINT_FILENAMEFORMAT);
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

        Element rootElem = document.createElement("closeComplaint");
        document.appendChild(rootElem);

        CloseComplaintRequest closeComplaintRequest = ((CloseComplaintPipelineContext) ctx).getCloseComplaintRequest();
        Complaint complaint = (Complaint) businessObject;

        addElement(document, rootElem, "closeDate", closeComplaintRequest.getDisposition().getCloseDate().toString(),
                true);
        addElement(document, rootElem, "complaintNumber", complaint.getComplaintNumber(), true);
        addElement(document, rootElem, "complaintDisposition",
                closeComplaintRequest.getDisposition().getDispositionType(), true);

        String caseId = ((CloseComplaintPipelineContext) ctx).getCloseComplaintRequest().getDisposition().getExistingCaseNumber();
        CaseFile caseFile = caseFileDao.findByCaseNumber(
                ((CloseComplaintPipelineContext) ctx).getCloseComplaintRequest().getDisposition().getExistingCaseNumber());
        if (caseId != null)
        {
            addElement(document, rootElem, "existingCaseNumber", caseId, true);
            addElement(document, rootElem, "existingCaseSearchBtn", "Search", true);
            addElement(document, rootElem, "existingCaseTitle", caseFile.getTitle(),
                    true);
            addElement(document, rootElem, "existingCaseCreated",
                    caseFile.getCreated().toString(), true);
            addElement(document, rootElem, "existingCasePriority",
                    caseFile.getPriority(), true);
        }

        if (closeComplaintRequest.getDisposition().getReferExternalContactMethod() != null)
        {
            addElement(document, rootElem, "referExternalNameOfAgency",
                    closeComplaintRequest.getDisposition().getReferExternalOrganizationName(), true);
            addElement(document, rootElem, "referExternalDate",
                    closeComplaintRequest.getDisposition().getReferExternalDate().toString(),
                    true);
            addElement(document, rootElem, "referExternalContactName",
                    closeComplaintRequest.getDisposition().getReferExternalContactPersonName(), true);
            addElement(document, rootElem, "referExternalContactType",
                    closeComplaintRequest.getDisposition().getReferExternalContactMethod().getType(),
                    true);
            addElement(document, rootElem, "referExternalContactValue",
                    closeComplaintRequest.getDisposition().getReferExternalContactMethod().getValue(),
                    true);
        }

        addParticipantsInXmlDocument(closeComplaintRequest.getParticipants(), document, rootElem, "participantName", "");

        return document;
    }

    @Override
    public DateTimeFormatter getDatePattern()
    {
        return datePattern;
    }

    public D getDao()
    {
        return dao;
    }

    public void setDao(D dao)
    {
        this.dao = dao;
    }

    public CaseFileDao getCaseFileDao()
    {
        return caseFileDao;
    }

    public void setCaseFileDao(CaseFileDao caseFileDao)
    {
        this.caseFileDao = caseFileDao;
    }
}
