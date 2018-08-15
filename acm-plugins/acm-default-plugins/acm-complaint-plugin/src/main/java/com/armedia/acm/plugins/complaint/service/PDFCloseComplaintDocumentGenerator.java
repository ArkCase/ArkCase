package com.armedia.acm.plugins.complaint.service;


import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.complaint.model.CloseComplaintConstants;
import com.armedia.acm.plugins.complaint.model.CloseComplaintRequest;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.pipeline.CloseComplaintPipelineContext;
import com.armedia.acm.plugins.ecm.service.PDFDocumentGenerator;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.pipeline.AbstractPipelineContext;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class PDFCloseComplaintDocumentGenerator<D extends AcmAbstractDao, T extends Complaint> extends PDFDocumentGenerator
{
    private final DateTimeFormatter datePattern = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private D dao;

    private CaseFileDao caseFileDao;

    public void generatePdf(String objectType, Long complaintId, CloseComplaintPipelineContext ctx) throws ParserConfigurationException
    {
        Complaint complaint = (Complaint) getDao().find(complaintId);
        generatePdf(objectType, complaintId, ctx, ctx.getAuthentication(), complaint, complaint.getContainer(),
                CloseComplaintConstants.CLOSE_COMPLAINT_STYLESHEET,
                CloseComplaintConstants.CLOSE_COMPLAINT_DOCUMENT, CloseComplaintConstants.CLOSE_COMPLAINT_FILENAMEFORMAT);
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
        if (caseId != null)
        {
            CaseFile existingCase = caseFileDao.findByCaseNumber(caseId);
            addElement(document, rootElem, "existingCaseNumber", caseId, true);
            addElement(document, rootElem, "existingCaseSearchBtn", "Search", true);
            addElement(document, rootElem, "existingCaseTitle", ctx.getPropertyValue("existingCaseTitle").toString(),
                    true);
            addElement(document, rootElem, "existingCaseCreated",
                    ctx.getPropertyValue("existingCaseCreated").toString(), true);
            addElement(document, rootElem, "existingCasePriority",
                    ctx.getPropertyValue("existingCasePriority").toString(), true);
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
        if (!closeComplaintRequest.getParticipants().isEmpty())
        {
            Element participantsElement = document.createElement("participants");
            rootElem.appendChild(participantsElement);
            List<AcmParticipant> participants = closeComplaintRequest.getParticipants();
            for (AcmParticipant participant : participants)
            {
                Element participantElement = document.createElement("participant");
                participantsElement.appendChild(participantElement);
                addElement(document, participantElement, "participantName", participant.getParticipantLdapId(), false);
            }
        }

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
