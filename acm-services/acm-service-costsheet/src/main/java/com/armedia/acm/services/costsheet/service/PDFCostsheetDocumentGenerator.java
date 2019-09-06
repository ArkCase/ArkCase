package com.armedia.acm.services.costsheet.service;

/*-
 * #%L
 * ACM Service: Costsheet
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
import com.armedia.acm.plugins.ecm.service.PDFDocumentGenerator;
import com.armedia.acm.services.costsheet.model.AcmCost;
import com.armedia.acm.services.costsheet.model.AcmCostsheet;
import com.armedia.acm.services.costsheet.model.CostsheetConstants;
import com.armedia.acm.services.costsheet.pipeline.CostsheetPipelineContext;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.pipeline.AbstractPipelineContext;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.time.ZoneId;
import java.util.List;

public class PDFCostsheetDocumentGenerator<D extends AcmAbstractDao, T extends AcmCostsheet> extends PDFDocumentGenerator
{
    private D dao;

    public void generatePdf(Long costsheetId, CostsheetPipelineContext ctx) throws ParserConfigurationException, PipelineProcessException
    {
        String objectType = CostsheetConstants.OBJECT_TYPE;
        if (getDao().getSupportedObjectType().equals(objectType))
        {
            T costsheet = (T) getDao().find(costsheetId);
            generatePdf(objectType, costsheetId, ctx, ctx.getAuthentication(), costsheet, costsheet.getContainer(),
                    CostsheetConstants.COSTSHEET_STYLESHEET, CostsheetConstants.COSTSHEET_DOCUMENT,
                    CostsheetConstants.COSTSHEET_FILENAMEFORMAT);
        }
    }

    @Override
    public Document buildXmlForPdfDocument(Object businessObject, AbstractPipelineContext ctx) throws ParserConfigurationException
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbf.newDocumentBuilder();
        Document document = docBuilder.newDocument();

        // create <costsheet>, the root of the document
        Element rootElem = document.createElement("costsheet");
        document.appendChild(rootElem);

        AcmCostsheet costsheet = (AcmCostsheet) businessObject;

        addElement(document, rootElem, "user", costsheet.getUser().getFullName(), true);
        addElement(document, rootElem, "status", StringUtils.capitalize(costsheet.getStatus().toLowerCase()), true);
        addElement(document, rootElem, "type", StringUtils.capitalize(costsheet.getParentType().toLowerCase()), true);
        addElement(document, rootElem, "code", costsheet.getParentNumber(), true);

        addElement(document, rootElem, "details", costsheet.getDetails() != null ? Jsoup.parse(costsheet.getDetails()).text() : "N/A", false);

        if (!costsheet.getCosts().isEmpty())
        {
            List<AcmCost> costs = costsheet.getCosts();
            addElement(document, rootElem, "balance", getAmountBalanceForCosts(costs), false);

            Element costsElement = document.createElement("costs");
            rootElem.appendChild(costsElement);

            for (AcmCost cost : costs)
            {
                Element costElement = document.createElement("cost");
                costsElement.appendChild(costElement);
                addElement(document, costElement, "date",
                        cost.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString(), true);
                addElement(document, costElement, "title", cost.getTitle(), true);
                addElement(document, costElement, "description", cost.getDescription() != null ? cost.getDescription() : "N/A", false);
                addElement(document, costElement, "amount", cost.getValue().toString(), true);
            }
        }

        Element participantsElement = document.createElement("participants");
        rootElem.appendChild(participantsElement);

        if (!costsheet.getParticipants().isEmpty())
        {
            List<AcmParticipant> participants = costsheet.getParticipants();
            for (AcmParticipant participant : participants)
            {
                Element participantElement = document.createElement("participant");
                participantsElement.appendChild(participantElement);
                addElement(document, participantElement, "participantName", participant.getParticipantLdapId(), false);
            }
        }
        else
        {
            Element participantElement = document.createElement("participant");
            participantsElement.appendChild(participantElement);
            addElement(document, participantElement, "participantName", "N/A", false);
        }
        return document;
    }

    private String getAmountBalanceForCosts(List<AcmCost> costs)
    {
        Double amount = 0.0;
        for (AcmCost cost : costs)
        {
            amount += cost.getValue();
        }
        return amount.toString();
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
