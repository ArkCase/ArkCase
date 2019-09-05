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
import com.armedia.acm.plugins.casefile.model.CaseFileConstants;
import com.armedia.acm.plugins.casefile.pipeline.CaseFilePipelineContext;
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

public class PDFCasefileDocumentGenerator<D extends AcmAbstractDao, T extends CaseFile> extends PDFDocumentGenerator
{

    private D dao;

    public void generatePdf(Long caseFileId, CaseFilePipelineContext ctx) throws ParserConfigurationException, PipelineProcessException
    {
        String objectType = CaseFileConstants.OBJECT_TYPE;
        if (getDao().getSupportedObjectType().equals(objectType))
        {
            T casefile = (T) getDao().find(caseFileId);
            generatePdf(objectType, caseFileId, ctx, ctx.getAuthentication(), casefile, casefile.getContainer(),
                    CaseFileConstants.CASEFILE_STYLESHEET, CaseFileConstants.CASEFILE_DOCUMENT,
                    CaseFileConstants.CASEFILE_FILENAMEFORMAT);
        }
    }

    @Override
    public Document buildXmlForPdfDocument(Object businessObject, AbstractPipelineContext ctx) throws ParserConfigurationException
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbf.newDocumentBuilder();
        Document document = docBuilder.newDocument();

        // create <casefile>, the root of the document
        Element rootElem = document.createElement("casefile");
        document.appendChild(rootElem);

        CaseFile caseFile = (CaseFile) businessObject;

        addElement(document, rootElem, "caseTitle", caseFile.getTitle(), true);
        addElement(document, rootElem, "caseType", caseFile.getCaseType(), true);
        addElement(document, rootElem, "caseDetails", caseFile.getDetails() != null ? Jsoup.parse(caseFile.getDetails()).text() : "N/A", false);

        String initiator = "Unknown";
        if ( caseFile.getOriginator() != null && caseFile.getOriginator().getPerson() != null && caseFile.getOriginator().getPerson().getFullName() != null )
        {
            initiator = caseFile.getOriginator().getPerson().getFullName();
        }
        addElement(document, rootElem, "initiator", initiator, true);

        if (!caseFile.getPersonAssociations().isEmpty())
        {
            Element peopleElement = document.createElement("people");
            rootElem.appendChild(peopleElement);

            List<PersonAssociation> people = caseFile.getPersonAssociations();
            for (PersonAssociation person : people)
            {
                Element personAssocElement = document.createElement("person");
                peopleElement.appendChild(personAssocElement);
                addElement(document, personAssocElement, "personType", person.getPersonType(), false);
                addElement(document, personAssocElement, "personName", person.getPerson().getFullName(), false);
            }
        }

        addParticipantsInXmlDocument(caseFile.getParticipants(), document, rootElem, "participantName", "participantType");

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
