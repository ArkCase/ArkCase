package com.armedia.acm.plugins.casefile.pipeline.postsave;

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

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.form.config.FormsTypeCheckService;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.ChangeCaseStateContants;
import com.armedia.acm.plugins.casefile.model.ChangeCaseStatus;
import com.armedia.acm.plugins.casefile.pipeline.CaseFilePipelineContext;
import com.armedia.acm.plugins.casefile.service.PDFChangeCaseFileStateDocumentGenerator;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class PDFChangeCaseFileStateDocumentGeneratorHandler extends PDFChangeCaseFileStateDocumentGenerator<CaseFileDao, CaseFile>
        implements PipelineHandler<ChangeCaseStatus, CaseFilePipelineContext>
{
    private FormsTypeCheckService formsTypeCheckService;
    private transient final Logger log = LogManager.getLogger(getClass());
    private PDFChangeCaseFileStateDocumentGenerator pdfChangeCaseFileStateDocumentGenerator;

    @Override
    public void execute(ChangeCaseStatus form, CaseFilePipelineContext ctx) throws PipelineProcessException
    {
        if (!formsTypeCheckService.getTypeOfForm().equals("frevvo"))
        {
            log.debug("Entering pipeline handler forEntering pipeline handler for case file with id [{}]",
                    form.getId());

            // ensure the SQL of all prior handlers is visible to this handler
            getDao().getEm().flush();

            try
            {
                generatePdf("CASE_FILE", form.getCaseId(), ctx);
            }
            catch (Exception e)
            {
                log.warn("Unable to generate pdf document for the case file with id [{}]", form.getId());
                throw new PipelineProcessException(e);
            }

            log.debug("Exiting pipeline handler for object: [{}]", form.getId());
        }

    }

    @Override
    public void rollback(ChangeCaseStatus form, CaseFilePipelineContext ctx) throws PipelineProcessException
    {
        if (ctx.hasProperty(ChangeCaseStateContants.NEW_FILE))
        {
            boolean newFile = (boolean) ctx.getPropertyValue(ChangeCaseStateContants.NEW_FILE);
            if (newFile)
            {
                if (ctx.hasProperty(ChangeCaseStateContants.FILE_ID))
                {
                    Long fileId = (Long) ctx.getPropertyValue(ChangeCaseStateContants.FILE_ID);
                    try
                    {
                        getEcmFileService().deleteFile(fileId);
                    }
                    catch (AcmUserActionFailedException | AcmObjectNotFoundException e)
                    {
                        log.warn("Unable to delete ecm file with id [{}] for the case file with id [{}]", fileId,
                                form.getId());
                        throw new PipelineProcessException(e);
                    }
                }
            }
        }
    }

    public PDFChangeCaseFileStateDocumentGenerator getPdfChangeCaseFileStateDocumentGenerator()
    {
        return pdfChangeCaseFileStateDocumentGenerator;
    }

    public void setPdfChangeCaseFileStateDocumentGenerator(PDFChangeCaseFileStateDocumentGenerator pdfChangeCaseFileStateDocumentGenerator)
    {
        this.pdfChangeCaseFileStateDocumentGenerator = pdfChangeCaseFileStateDocumentGenerator;
    }

    public FormsTypeCheckService getFormsTypeCheckService()
    {
        return formsTypeCheckService;
    }

    public void setFormsTypeCheckService(FormsTypeCheckService formsTypeCheckService)
    {
        this.formsTypeCheckService = formsTypeCheckService;
    }
}
