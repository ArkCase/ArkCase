package com.armedia.acm.plugins.consultation.service;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.consultation.model.Consultation;
import com.armedia.acm.plugins.consultation.model.ConsultationConstants;
import com.armedia.acm.plugins.consultation.pipeline.ConsultationPipelineContext;
import com.armedia.acm.plugins.ecm.service.PDFDocumentGenerator;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.services.pipeline.AbstractPipelineContext;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;

import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.util.List;

public class PDFConsultationDocumentGenerator<D extends AcmAbstractDao, T extends Consultation> extends PDFDocumentGenerator
{

    private D dao;

    public void generatePdf(Long consultationId, ConsultationPipelineContext ctx) throws ParserConfigurationException, PipelineProcessException
    {
        String objectType = ConsultationConstants.OBJECT_TYPE;
        if (getDao().getSupportedObjectType().equals(objectType))
        {
            T consultation = (T) getDao().find(consultationId);
            generatePdf(objectType, consultationId, ctx, ctx.getAuthentication(), consultation, consultation.getContainer(),
                    ConsultationConstants.CONSULTATION_STYLESHEET, ConsultationConstants.CONSULTATION_DOCUMENT,
                    ConsultationConstants.CONSULTATION_NAME_FORMAT);
        }
    }

    @Override
    public Document buildXmlForPdfDocument(Object businessObject, AbstractPipelineContext ctx) throws ParserConfigurationException
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbf.newDocumentBuilder();
        Document document = docBuilder.newDocument();

        // create <consultation>, the root of the document
        Element rootElem = document.createElement("consultation");
        document.appendChild(rootElem);

        Consultation consultation = (Consultation) businessObject;

        addElement(document, rootElem, "consultationTitle", consultation.getTitle(), true);
        addElement(document, rootElem, "consultationDetails", consultation.getDetails() != null ? Jsoup.parse(consultation.getDetails()).text() : "N/A", false);

        String initiator = "Unknown";
        if ( consultation.getOriginator() != null && consultation.getOriginator().getPerson() != null && consultation.getOriginator().getPerson().getFullName() != null )
        {
            initiator = consultation.getOriginator().getPerson().getFullName();
        }
        addElement(document, rootElem, "initiator", initiator, true);

        if (!consultation.getPersonAssociations().isEmpty())
        {
            Element peopleElement = document.createElement("people");
            rootElem.appendChild(peopleElement);

            List<PersonAssociation> people = consultation.getPersonAssociations();
            for (PersonAssociation person : people)
            {
                Element personAssocElement = document.createElement("person");
                peopleElement.appendChild(personAssocElement);
                addElement(document, personAssocElement, "personType", person.getPersonType(), false);
                addElement(document, personAssocElement, "personName", person.getPerson().getFullName(), false);
            }
        }

        addParticipantsInXmlDocument(consultation.getParticipants(), document, rootElem, "participantName", "participantType");

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
