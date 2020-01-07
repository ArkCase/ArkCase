package com.armedia.acm.plugins.complaint.pipeline.postsave;

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

import com.armedia.acm.form.config.FormsTypeCheckService;
import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.model.CloseComplaintRequest;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.pipeline.CloseComplaintPipelineContext;
import com.armedia.acm.plugins.complaint.service.PDFCloseComplaintDocumentGenerator;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class PDFCloseComplaintDocumentGeneratorHandler extends PDFCloseComplaintDocumentGenerator<ComplaintDao, Complaint>
        implements PipelineHandler<CloseComplaintRequest, CloseComplaintPipelineContext>
{
    private FormsTypeCheckService formsTypeCheckService;
    private transient final Logger log = LogManager.getLogger(getClass());

    @Override
    public void execute(CloseComplaintRequest form, CloseComplaintPipelineContext ctx) throws PipelineProcessException
    {
        if (!formsTypeCheckService.getTypeOfForm().equals("frevvo"))
        {
            log.debug("Entering pipeline handler forEntering pipeline handler for complaint with id [{}]",
                    form.getId());

            // ensure the SQL of all prior handlers is visible to this handler
            getDao().getEm().flush();

            try
            {
                generatePdf("COMPLAINT", form.getComplaintId(), ctx);
            }
            catch (Exception e)
            {
                log.warn("Unable to generate pdf document for the complaint with id [{}]", form.getId());
                throw new PipelineProcessException(e);
            }

            log.debug("Exiting pipeline handler for object: [{}]", form.getId());
        }
    }

    @Override
    public void rollback(CloseComplaintRequest form, CloseComplaintPipelineContext pipelineContext)
    {
        // nothing to do here, there is no rollback action to be executed
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
