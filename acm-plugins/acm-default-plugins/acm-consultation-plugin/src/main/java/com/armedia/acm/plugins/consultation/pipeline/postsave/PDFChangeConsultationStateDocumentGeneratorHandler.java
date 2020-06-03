package com.armedia.acm.plugins.consultation.pipeline.postsave;

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

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.consultation.dao.ConsultationDao;
import com.armedia.acm.plugins.consultation.model.ChangeConsultationStateContants;
import com.armedia.acm.plugins.consultation.model.ChangeConsultationStatus;
import com.armedia.acm.plugins.consultation.model.Consultation;
import com.armedia.acm.plugins.consultation.model.ConsultationConstants;
import com.armedia.acm.plugins.consultation.pipeline.ConsultationPipelineContext;
import com.armedia.acm.plugins.consultation.service.PDFChangeConsultationStateDocumentGenerator;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on May, 2020
 */
public class PDFChangeConsultationStateDocumentGeneratorHandler
        extends PDFChangeConsultationStateDocumentGenerator<ConsultationDao, Consultation>
        implements PipelineHandler<ChangeConsultationStatus, ConsultationPipelineContext>
{
    private transient final Logger log = LogManager.getLogger(getClass());
    private PDFChangeConsultationStateDocumentGenerator pdfChangeConsultationStateDocumentGenerator;

    @Override
    public void execute(ChangeConsultationStatus form, ConsultationPipelineContext ctx) throws PipelineProcessException
    {
        log.debug("Entering pipeline handler forEntering pipeline handler for consultation with id [{}]",
                form.getId());

        // ensure the SQL of all prior handlers is visible to this handler
        getDao().getEm().flush();

        try
        {
            generatePdf(ConsultationConstants.OBJECT_TYPE, form.getConsultationId(), ctx);
        }
        catch (Exception e)
        {
            log.warn("Unable to generate pdf document for the consultation with id [{}]", form.getId());
            throw new PipelineProcessException(e);
        }

        log.debug("Exiting pipeline handler for object: [{}]", form.getId());
    }

    @Override
    public void rollback(ChangeConsultationStatus form, ConsultationPipelineContext ctx) throws PipelineProcessException
    {
        if (ctx.hasProperty(ChangeConsultationStateContants.NEW_FILE))
        {
            boolean newFile = (boolean) ctx.getPropertyValue(ChangeConsultationStateContants.NEW_FILE);
            if (newFile)
            {
                if (ctx.hasProperty(ChangeConsultationStateContants.FILE_ID))
                {
                    Long fileId = (Long) ctx.getPropertyValue(ChangeConsultationStateContants.FILE_ID);
                    try
                    {
                        getEcmFileService().deleteFile(fileId);
                    }
                    catch (AcmUserActionFailedException | AcmObjectNotFoundException e)
                    {
                        log.warn("Unable to delete ecm file with id [{}] for the consultation with id [{}]", fileId,
                                form.getId());
                        throw new PipelineProcessException(e);
                    }
                }
            }
        }
    }

    public PDFChangeConsultationStateDocumentGenerator getPdfChangeConsultationStateDocumentGenerator()
    {
        return pdfChangeConsultationStateDocumentGenerator;
    }

    public void setPdfChangeConsultationStateDocumentGenerator(
            PDFChangeConsultationStateDocumentGenerator pdfChangeConsultationStateDocumentGenerator)
    {
        this.pdfChangeConsultationStateDocumentGenerator = pdfChangeConsultationStateDocumentGenerator;
    }
}
