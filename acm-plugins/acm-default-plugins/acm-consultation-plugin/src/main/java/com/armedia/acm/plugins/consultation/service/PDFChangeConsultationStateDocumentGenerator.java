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

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on May, 2020
 */
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
