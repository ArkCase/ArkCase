package com.armedia.acm.services.timesheet.service;

/*-
 * #%L
 * ACM Service: Timesheet
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
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.pipeline.AbstractPipelineContext;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.timesheet.model.AcmTime;
import com.armedia.acm.services.timesheet.model.AcmTimesheet;
import com.armedia.acm.services.timesheet.model.TimesheetConstants;
import com.armedia.acm.services.timesheet.pipeline.TimesheetPipelineContext;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PDFTimesheetDocumentGenerator<D extends AcmAbstractDao, T extends AcmTimesheet> extends PDFDocumentGenerator {
    private D dao;
    private final DateTimeFormatter datePattern1 = DateTimeFormatter.ofPattern("MMMM dd");
    private final DateTimeFormatter datePattern2 = DateTimeFormatter.ofPattern("MMMM dd yyyy");

    public void generatePdf(Long timesheetId, TimesheetPipelineContext ctx) throws ParserConfigurationException, PipelineProcessException {
        String objectType = TimesheetConstants.OBJECT_TYPE;
        if (getDao().getSupportedObjectType().equals(objectType)) {
            T costsheet = (T) getDao().find(timesheetId);
            generatePdf(objectType, timesheetId, ctx, ctx.getAuthentication(), costsheet, costsheet.getContainer(),
                    TimesheetConstants.TIMESHEET_STYLESHEET, TimesheetConstants.TIMESHEET_DOCUMENT,
                    TimesheetConstants.TIMESHEET_FILENAMEFORMAT);
        }
    }

    @Override
    public Document buildXmlForPdfDocument(Object businessObject, AbstractPipelineContext ctx) throws ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbf.newDocumentBuilder();
        Document document = docBuilder.newDocument();

        // create <timesheet>, the root of the document
        Element rootElem = document.createElement("timesheet");
        document.appendChild(rootElem);

        AcmTimesheet timesheet = (AcmTimesheet) businessObject;

        addElement(document, rootElem, "user", timesheet.getUser().getFullName(), true);
        addElement(document, rootElem, "status", StringUtils.capitalize(timesheet.getStatus().toLowerCase()), true);
        addElement(document, rootElem, "periodWeek",
                datePattern1.format(timesheet.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()) + " - "
                        + datePattern2.format(timesheet.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()), true);
        addElement(document, rootElem, "dateCreated", timesheet.getCreated().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString(), true);

        addElement(document, rootElem, "details", timesheet.getDetails() != null ? Jsoup.parse(timesheet.getDetails()).text() : "N/A", false);

        if (!timesheet.getTimes().isEmpty()) {
            List<AcmTime> times = timesheet.getTimes();
            addElement(document, rootElem, "totalCost", getSumOfCostsForAllTimes(times), false);
            addElement(document, rootElem, "totalHours", getSumOfHoursForAllTimes(times), false);

            Element costsElement = document.createElement("times");
            rootElem.appendChild(costsElement);

            for (AcmTime time : times) {
                Element timeElement = document.createElement("time");
                costsElement.appendChild(timeElement);
                addElement(document, timeElement, "type", time.getType(), true);
                addElement(document, timeElement, "chargeCode", time.getCode(), true);
                addElement(document, timeElement, "chargeRole", time.getChargeRole(), true);
                addElement(document, timeElement, "date", time.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString(), true);
                addElement(document, timeElement, "hours", time.getValue().toString(), true);
                addElement(document, timeElement, "cost", time.getTotalCost().toString(), true);
            }
        }

        Element participantsElement = document.createElement("participants");
        rootElem.appendChild(participantsElement);

        if (!timesheet.getParticipants().isEmpty()) {
            List<AcmParticipant> participants = timesheet.getParticipants();
            for (AcmParticipant participant : participants) {
                Element participantElement = document.createElement("participant");
                participantsElement.appendChild(participantElement);
                addElement(document, participantElement, "participantName", participant.getParticipantLdapId(), false);
            }
        } else {
            Element participantElement = document.createElement("participant");
            participantsElement.appendChild(participantElement);
            addElement(document, participantElement, "participantName", "N/A", false);
        }
        return document;
    }

    private String getSumOfCostsForAllTimes(List<AcmTime> times) {
        Double amount = 0.0;
        for (AcmTime time : times) {
            amount += time.getTotalCost();
        }
        return amount.toString();
    }

    private String getSumOfHoursForAllTimes(List<AcmTime> times) {
        Double amount = 0.0;
        for (AcmTime time : times) {
            amount += time.getValue();
        }
        return amount.toString();
    }

    public D getDao() {
        return dao;
    }

    public void setDao(D dao) {
        this.dao = dao;
    }
}
