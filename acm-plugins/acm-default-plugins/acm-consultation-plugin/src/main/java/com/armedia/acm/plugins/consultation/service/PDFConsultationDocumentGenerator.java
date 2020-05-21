package com.armedia.acm.plugins.consultation.service;

/*-
 * #%L
 * ACM Default Plugin: Consultation
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on May, 2020
 */
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
