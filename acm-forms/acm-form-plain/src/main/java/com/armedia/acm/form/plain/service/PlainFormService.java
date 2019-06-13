/**
 * 
 */
package com.armedia.acm.form.plain.service;

/*-
 * #%L
 * ACM Forms: Plain
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

import com.armedia.acm.form.plain.model.PlainForm;
import com.armedia.acm.form.plain.model.PlainFormCreatedEvent;
import com.armedia.acm.frevvo.config.FrevvoFormAbstractService;
import com.armedia.acm.frevvo.model.UploadedFiles;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author riste.tutureski
 *
 */
public class PlainFormService extends FrevvoFormAbstractService
{

    private Logger LOG = LogManager.getLogger(getClass());

    private String formName;
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public Object get(String action)
    {
        // No implementation is needed so far
        return null;
    }

    @Override
    public boolean save(String xml, MultiValueMap<String, MultipartFile> attachments) throws Exception
    {
        Long folderId = getFolderAndFilesUtils().convertToLong((String) getRequest().getParameter("folderId"));
        String cmisFolderId = null;
        PlainForm form = (PlainForm) convertFromXMLToObject(cleanXML(xml), PlainForm.class);

        if (form == null)
        {
            LOG.warn("Cannot umarshall " + getFormName() + " form.");
            return false;
        }

        try
        {
            cmisFolderId = findCmisFolderId(folderId, null, form.getObjectType(), form.getId());
        }
        catch (Exception e)
        {
            LOG.error("Cannot take CMIS folder id to be able to save attachments.", e);
            return false;
        }

        UploadedFiles uploaded = saveAttachments(attachments, cmisFolderId, form.getObjectType(), form.getObjectId());

        PlainFormCreatedEvent event = new PlainFormCreatedEvent(
                form, getFormName(), folderId, cmisFolderId, getAuthentication().getName(), getUserIpAddress(),
                uploaded.getPdfRendition().getFileId(), uploaded.getFormXml().getFileId());
        getApplicationEventPublisher().publishEvent(event);

        return true;
    }

    @Override
    public String getFormName()
    {
        return formName;
    }

    @Override
    public void setFormName(String formName)
    {
        this.formName = formName;
    }

    @Override
    public Class<?> getFormClass()
    {
        return PlainForm.class;
    }

    public ApplicationEventPublisher getApplicationEventPublisher()
    {
        return applicationEventPublisher;
    }

    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public Object convertToFrevvoForm(Object obj, Object form)
    {
        // Implementation no needed so far
        return null;
    }

}
