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
import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.model.ComplaintConstants;
import com.armedia.acm.plugins.complaint.pipeline.ComplaintPipelineContext;
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

import java.time.ZoneId;
import java.util.List;

public class PDFComplaintDocumentGenerator<D extends AcmAbstractDao, T extends Complaint> extends PDFDocumentGenerator
{
    private D dao;

    public void generatePdf(Long complaintId, ComplaintPipelineContext ctx) throws ParserConfigurationException, PipelineProcessException
    {
        String objectType = ComplaintConstants.OBJECT_TYPE;
        if (getDao().getSupportedObjectType().equals(objectType))
        {
            T complaint = (T) getDao().find(complaintId);
            generatePdf(objectType, complaintId, ctx, ctx.getAuthentication(), complaint, complaint.getContainer(),
                    ComplaintConstants.COMPLAINT_STYLESHEET, ComplaintConstants.COMPLAINT_DOCUMENT,
                    ComplaintConstants.COMPLAINT_FILENAMEFORMAT);
        }
    }

    @Override
    public Document buildXmlForPdfDocument(Object businessObject, AbstractPipelineContext ctx) throws ParserConfigurationException
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbf.newDocumentBuilder();
        Document document = docBuilder.newDocument();

        // create <complaint>, the root of the document
        Element rootElem = document.createElement("complaint");
        document.appendChild(rootElem);

        Complaint complaint = (Complaint) businessObject;

        String incidentDateStr = "Unknown";
        if ( complaint.getIncidentDate() != null )
        {
            incidentDateStr = complaint.getIncidentDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString();
        }
        addElement(document, rootElem, "incidentDate",
                incidentDateStr,
                true);
        String initiator = "Unknown";
        if ( complaint.getOriginator() != null && complaint.getOriginator().getPerson() != null && complaint.getOriginator().getPerson().getFullName() != null )
        {
            initiator = complaint.getOriginator().getPerson().getFullName();
        }
        addElement(document, rootElem, "initiator", initiator, true);

        addElement(document, rootElem, "complaintType", complaint.getComplaintType(), true);
        addElement(document, rootElem, "complaintNumber", complaint.getComplaintNumber(), true);
        addElement(document, rootElem, "complaintTitle", complaint.getComplaintTitle(), true);

        addElement(document, rootElem, "priority", complaint.getPriority() != null ? complaint.getPriority() : "N/A", false);
        addElement(document, rootElem, "complaintTag", complaint.getTag() != null ? complaint.getTag() : "N/A", false);
        addElement(document, rootElem, "frequency", complaint.getFrequency() != null ? complaint.getFrequency() : "N/A", false);

        addElement(document, rootElem, "complaintDescription", complaint.getDetails() != null ? Jsoup.parse(complaint.getDetails()).text() : "N/A", false);

        if (complaint.getDefaultAddress() != null)
        {
            PostalAddress location = complaint.getDefaultAddress();
            addElement(document, rootElem, "locationType", location.getType(), false);
            addElement(document, rootElem, "locationAddress", location.getStreetAddress(), false);
            addElement(document, rootElem, "locationCity", location.getCity(), false);
            addElement(document, rootElem, "locationState", location.getState(), false);
            addElement(document, rootElem, "locationZip", location.getZip(), false);
            addElement(document, rootElem, "locationAddedBy", location.getCreator(), false);
        }
        else
        {
            addElement(document, rootElem, "locationType", "N/A", false);
            addElement(document, rootElem, "locationAddress", "N/A", false);
            addElement(document, rootElem, "locationCity", "N/A", false);
            addElement(document, rootElem, "locationState", "N/A", false);
            addElement(document, rootElem, "locationZip", "N/A", false);
            addElement(document, rootElem, "locationAddedBy", "N/A", false);
        }

        if (!complaint.getPersonAssociations().isEmpty())
        {
            Element peopleElement = document.createElement("people");
            rootElem.appendChild(peopleElement);

            List<PersonAssociation> people = complaint.getPersonAssociations();
            for (PersonAssociation person : people)
            {
                Element personAssocElement = document.createElement("person");
                peopleElement.appendChild(personAssocElement);
                addElement(document, personAssocElement, "personType", person.getPersonType(), false);
                addElement(document, personAssocElement, "personName", person.getPerson().getFullName(), false);
            }
        }

        addParticipantsInXmlDocument(complaint.getParticipants(), document, rootElem, "participantName", "participantType");

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
