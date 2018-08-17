package com.armedia.acm.services.costsheet.service;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.ecm.service.PDFDocumentGenerator;
import com.armedia.acm.services.costsheet.model.AcmCost;
import com.armedia.acm.services.costsheet.model.AcmCostsheet;
import com.armedia.acm.services.costsheet.model.CostsheetConstants;
import com.armedia.acm.services.costsheet.pipeline.CostsheetPipelineContext;
import com.armedia.acm.services.participants.model.AcmParticipant;

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

    public void generatePdf(Long costsheetId, CostsheetPipelineContext ctx) throws ParserConfigurationException
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
    public Document buildXmlForPdfDocument(Object businessObject) throws ParserConfigurationException
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbf.newDocumentBuilder();
        Document document = docBuilder.newDocument();

        // create <costsheet>, the root of the document
        Element rootElem = document.createElement("costsheet");
        document.appendChild(rootElem);

        AcmCostsheet costsheet = (AcmCostsheet) businessObject;

        addElement(document, rootElem, "user", costsheet.getUser().getFullName(), true);
        addElement(document, rootElem, "status", costsheet.getStatus(), true);
        addElement(document, rootElem, "type", costsheet.getParentType(), true);
        addElement(document, rootElem, "code", costsheet.getParentNumber(), true);

        addElement(document, rootElem, "details", costsheet.getDetails() != null ? costsheet.getDetails() : "N/A", false);

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
                        cost.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString(), false);
                addElement(document, costElement, "title", cost.getTitle(), true);
                addElement(document, costElement, "description", cost.getDescription(), false);
                addElement(document, costElement, "amount", cost.getValue().toString(), false);
            }
        }

        if (!costsheet.getParticipants().isEmpty())
        {
            Element participantsElement = document.createElement("participants");
            rootElem.appendChild(participantsElement);

            List<AcmParticipant> participants = costsheet.getParticipants();
            for (AcmParticipant participant : participants)
            {
                Element participantElement = document.createElement("participant");
                participantsElement.appendChild(participantElement);
                addElement(document, participantElement, "participantName", participant.getParticipantLdapId(), false);
            }
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