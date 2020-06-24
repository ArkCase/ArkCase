package gov.privacy.service;

/*-
 * #%L
 * ACM Privacy: Subject Access Request
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

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.correspondence.model.CorrespondenceTemplate;
import com.armedia.acm.correspondence.service.CorrespondenceService;
import com.armedia.acm.plugins.ecm.model.EcmFile;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import gov.privacy.model.SARDocumentDescriptor;
import gov.privacy.model.SARObject;

/**
 * @author Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on Jun, 2020
 *
 */
public class CorrespondenceDocumentGenerator implements DocumentGenerator
{

    private CorrespondenceService correspondenceService;

    @Override
    public EcmFile generateAndUpload(SARDocumentDescriptor documentDescriptor, SARObject acmObject, String targetCmisFolderId,
                                     String targetFilename, Map<String, String> substitutions)
            throws DocumentGeneratorException
    {
        try
        {
            Collection<CorrespondenceTemplate> templates = getCorrespondenceService().getActiveVersionTemplates();
            Optional<CorrespondenceTemplate> optionalCorrespondenceTemplate = templates.stream()
                    .filter(t -> t.getLabel().equals(documentDescriptor.getTemplate())).findFirst();
            if (optionalCorrespondenceTemplate.isPresent())
            {

                CorrespondenceTemplate template = optionalCorrespondenceTemplate.get();
                return getCorrespondenceService().generate(template.getTemplateFilename(), acmObject.getObjectType(),
                        acmObject.getId(),
                        targetCmisFolderId);
            }
            else
            {
                throw new DocumentGeneratorException(
                        "Failed to generate correspondence document for objectId: [" + acmObject.getId() + "], objectType: ["
                                + acmObject.getObjectType() + "] and template:[" + documentDescriptor.getTemplate() + "]");
            }

        }
        catch (IllegalArgumentException | IOException | AcmCreateObjectFailedException | AcmUserActionFailedException e)
        {
            throw new DocumentGeneratorException(
                    "Failed to generate correspondence document for objectId: [" + acmObject.getId() + "], objectType: ["
                            + acmObject.getObjectType() + "] and template:[" + documentDescriptor.getTemplate() + "]",
                    e);
        }
    }

    public CorrespondenceService getCorrespondenceService()
    {
        return correspondenceService;
    }

    public void setCorrespondenceService(CorrespondenceService correspondenceService)
    {
        this.correspondenceService = correspondenceService;
    }
}
