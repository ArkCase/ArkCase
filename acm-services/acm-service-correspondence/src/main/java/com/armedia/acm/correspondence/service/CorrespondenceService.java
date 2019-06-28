package com.armedia.acm.correspondence.service;

/*-
 * #%L
 * ACM Service: Correspondence Library
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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
import com.armedia.acm.correspondence.model.CorrespondenceMergeField;
import com.armedia.acm.correspondence.model.CorrespondenceTemplate;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.data.AcmEntity;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author darko.dimitrievski
 */
public interface CorrespondenceService
{
    /**
     * For use from MVC controllers and any other client with an Authentication object.
     *
     * @param authentication
     * @param templateName
     * @param parentObjectType
     * @param parentObjectId
     * @param targetCmisFolderId
     * @return EcmFile
     * @throws IOException
     * @throws IllegalArgumentException
     * @throws AcmCreateObjectFailedException
     */
    EcmFile generate(Authentication authentication, String templateName, String parentObjectType, Long parentObjectId,
            String targetCmisFolderId)
            throws IOException, IllegalArgumentException, AcmCreateObjectFailedException, AcmUserActionFailedException;

    /**
     * For use from MVC controllers and any other client with an Authentication object.
     *
     * @param authentication
     * @param templates
     * @param parentObjectType
     * @param parentObjectId
     * @param targetCmisFolderId
     * @param documentName
     * @return EcmFile
     * @throws Exception
     */
    EcmFile generateMultiTemplate(Authentication authentication, List<CorrespondenceTemplate> templates, String parentObjectType,
            Long parentObjectId,
            String targetCmisFolderId, String documentName) throws Exception;

    /**
     * For use from MVC controllers and any other client with an Authentication object.
     *
     * @param templateName
     * @return EcmFile
     * @throws Exception
     */
    CorrespondenceTemplate findTemplate(String templateName);

    /**
     * Helper method for use from Activiti and other clients with no direct access to an Authentication, but in the call
     * stack of a Spring MVC authentication... so there is an Authentication in the Spring Security context holder.
     */
    EcmFile generate(String templateName, String parentObjectType, Long parentObjectId, String targetCmisFolderId)
            throws IOException, IllegalArgumentException, AcmCreateObjectFailedException, AcmUserActionFailedException;

    List<CorrespondenceTemplate> getAllTemplates();

    List<CorrespondenceTemplate> getActiveVersionTemplates();

    List<CorrespondenceTemplate> getActivatedActiveVersionTemplatesByObjectType(String objectType);

    /**
     * @param templateId
     * @return
     */
    List<CorrespondenceTemplate> getTemplateVersionsById(String templateId);

    /**
     * @param templateId
     * @return
     */
    Optional<CorrespondenceTemplate> getActiveTemplateById(String templateId);

    /**
     * @param templateId
     * @param templateVersion
     * @return
     */
    Optional<CorrespondenceTemplate> getTemplateByIdAndVersion(String templateId, String templateVersion);

    /**
     * @param templateId
     * @param templateFilename
     * @return
     */

    Optional<CorrespondenceTemplate> getTemplateByIdAndFilename(String templateId, String templateFilename);

    /**
     * @param templateId
     * @param templateVersion
     * @return
     * @throws IOException
     */
    Optional<CorrespondenceTemplate> deleteTemplateByIdAndVersion(String templateId, String templateVersion) throws IOException;

    List<CorrespondenceMergeField> getMergeFields();

    /**
     * @param objectType
     * @return
     * @throws IOException, CorrespondenceMergeFieldVersionException
     */
    List<CorrespondenceMergeField> getMergeFieldsByType(String objectType);

    /**
     * @param mergeFieldId
     * @return
     * @throws IOException, CorrespondenceMergeFieldVersionException
     */
    List<CorrespondenceMergeField> getMergeFieldByMergeFieldId(String mergeFieldId);

    /**
     * @param mergeField
     * @return
     * @throws IOException, CorrespondenceMergeFieldVersionException
     */
    void deleteMergeFields (String mergeFieldId) throws IOException;

    /**
     * @param newMergeField
     * @return
     */
    void addMergeField (CorrespondenceMergeField newMergeField) throws IOException;
    /**
     * @param mergeFields
     * @param auth
     * @return
     * @throws IOException
     */
    List<CorrespondenceMergeField> saveMergeFieldsData(List<CorrespondenceMergeField> mergeFields, Authentication auth)
            throws IOException;

    /**
     * @param template
     * @throws IOException
     */
    Optional<CorrespondenceTemplate> updateTemplate(CorrespondenceTemplate template) throws IOException;

    /**
     * Generating Dao objects depending from the object type
     *
     * @param objectType
     */
    AcmAbstractDao<AcmEntity> getAcmAbstractDao(String objectType);

    /**
     * Listing all template model providers
     */
    Map<String, String> listTemplateModelProviders();

    /**
     * Listing all declared fields for given template model provider classpath
     */
    String getTemplateModelProviderDeclaredFields(String classPath);
}
