package com.armedia.acm.plugins.consultation.service;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.consultation.model.ChangeConsultationStateContants;
import com.armedia.acm.plugins.consultation.model.ChangeConsultationStatus;
import com.armedia.acm.plugins.consultation.model.Consultation;
import com.armedia.acm.plugins.consultation.pipeline.ConsultationPipelineContext;
import com.armedia.acm.plugins.ecm.service.PDFDocumentGenerator;
import com.armedia.acm.services.pipeline.AbstractPipelineContext;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class PDFChangeConsultationStateDocumentGenerator<D extends AcmAbstractDao, T extends Consultation> extends PDFDocumentGenerator
{
    private D dao;

    public void generatePdf(String objectType, Long consultationId, ConsultationPipelineContext ctx)
            throws PipelineProcessException
    {
        try
        {
            Consultation consultation = (Consultation) getDao().find(consultationId);
            generatePdf(objectType, consultationId, ctx, ctx.getAuthentication(), consultation, consultation.getContainer(),
                    ChangeConsultationStateContants.CHANGE_CONSULTATION_STATUS_STYLESHEET,
                    ChangeConsultationStateContants.CHANGE_CONSULTATION_STATUS,
                    ChangeConsultationStateContants.CHANGE_CONSULTATION_STATUS_FILENAMEFORMAT);
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

        Element rootElem = document.createElement("changeConsultationState");
        document.appendChild(rootElem);

        ChangeConsultationStatus changeConsultationStatus = ((ConsultationPipelineContext) ctx).getChangeConsultationStatus();

        addElement(document, rootElem, "changeDate", ctx.getPropertyValue("changeDate").toString(),
                true);

        addElement(document, rootElem, "consultationNumber", changeConsultationStatus.getConsultationId().toString(),
                true);

        addElement(document, rootElem, "status", changeConsultationStatus.getStatus(),
                true);

        if (!changeConsultationStatus.getStatus().isEmpty())
        {
            addElement(document, rootElem, "consultationResolution", ctx.getPropertyValue("consultationResolution").toString(),
                    true);
        }

        if (!changeConsultationStatus.getParticipants().isEmpty())
        {
            addElement(document, rootElem, "participant",
                    changeConsultationStatus.getParticipants().size() > 0
                            ? changeConsultationStatus.getParticipants().get(0).getParticipantLdapId()
                            : "N/A",
                    false);
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
