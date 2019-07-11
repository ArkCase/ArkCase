package com.armedia.acm.correspondence.service;

/*-
 * #%L
 * ACM Service: Correspondence Library
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

import com.armedia.acm.correspondence.model.CorrespondenceMergeField;
import com.armedia.acm.correspondence.model.CorrespondenceTemplate;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jan 30, 2017
 *
 */
public class CorrespondenceMapper
{

    public static CorrespondenceTemplate mapTemplateFromConfiguration(CorrespondenceTemplate configuration)
    {
        CorrespondenceTemplate template = new CorrespondenceTemplate();

        template.setTemplateId(configuration.getTemplateId());
        template.setTemplateVersion(configuration.getTemplateVersion());
        template.setTemplateVersionActive(configuration.isTemplateVersionActive());
        template.setLabel(configuration.getLabel());
        template.setDocumentType(configuration.getDocumentType());
        template.setTemplateFilename(configuration.getTemplateFilename());
        template.setObjectType(configuration.getObjectType());
        template.setDateFormatString(configuration.getDateFormatString());
        template.setNumberFormatString(configuration.getNumberFormatString());
        template.setActivated(configuration.isActivated());
        template.setModifier(configuration.getModifier());
        template.setModified(configuration.getModified());
        template.setTemplateModelProvider(configuration.getTemplateModelProvider());

        return template;
    }

    public static CorrespondenceMergeField generateCorrespodencenMergeField(CorrespondenceMergeField configuration)
    {
        CorrespondenceMergeField mergeField = new CorrespondenceMergeField();

        mergeField.setFieldId(configuration.getFieldId());
        mergeField.setFieldValue(configuration.getFieldValue());
        mergeField.setFieldDescription(configuration.getFieldDescription());
        mergeField.setFieldObjectType(configuration.getFieldObjectType());

        return mergeField;
    }

}
