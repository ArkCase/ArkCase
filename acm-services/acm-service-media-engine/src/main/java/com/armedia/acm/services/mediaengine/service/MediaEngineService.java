package com.armedia.acm.services.mediaengine.service;

/*-
 * #%L
 * ACM Service: Transcribe
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

import com.armedia.acm.camelcontext.exception.ArkCaseFileRepositoryException;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.armedia.acm.services.mediaengine.exception.CreateMediaEngineException;
import com.armedia.acm.services.mediaengine.exception.GetConfigurationException;
import com.armedia.acm.services.mediaengine.exception.GetMediaEngineException;
import com.armedia.acm.services.mediaengine.exception.MediaEngineProviderNotFound;
import com.armedia.acm.services.mediaengine.exception.MediaEngineServiceNotFoundException;
import com.armedia.acm.services.mediaengine.exception.SaveConfigurationException;
import com.armedia.acm.services.mediaengine.exception.SaveMediaEngineException;
import com.armedia.acm.services.mediaengine.factory.MediaEngineServiceFactory;
import com.armedia.acm.services.mediaengine.model.MediaEngine;
import com.armedia.acm.services.mediaengine.model.MediaEngineConfiguration;
import com.armedia.acm.services.mediaengine.model.MediaEngineType;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com>
 */
public interface MediaEngineService<T extends MediaEngine>
{
    /**
     * This method will create MediaEngine for given media file version ID
     *
     * @param mediaVersionId
     *            - ID of the media file version
     * @param type
     *            - AUTOMATIC or MANUAL
     * @return MediaEngine object
     * @throws CreateMediaEngineException
     */
    @Transactional
    MediaEngine create(Long mediaVersionId, MediaEngineType type) throws CreateMediaEngineException;

    /**
     * This method will create MediaEngine for given media file version
     *
     * @param ecmFileVersion
     *            - Media File Version
     * @param type
     *            - AUTOMATIC or MANUAL
     * @return MediaEngine object
     * @throws CreateMediaEngineException
     */
    @Transactional
    MediaEngine create(EcmFileVersion ecmFileVersion, MediaEngineType type) throws CreateMediaEngineException;

    /**
     * This method will create Transcribe
     *
     * @param mediaEngine
     *            - MediaEngine object
     * @return MediaEngine
     * @throws CreateMediaEngineException
     */
    @Transactional
    MediaEngine create(MediaEngine mediaEngine) throws CreateMediaEngineException;

    /**
     * This method will get the MediaEngine by given remote ID (ID that is stored on provider side)
     *
     * @param remoteId
     *            - ID stored on provider side
     * @return MediaEngine
     * @throws GetMediaEngineException
     */
    MediaEngine get(String remoteId) throws GetMediaEngineException;

    /**
     * This method will get MediaEngine object for given ID
     *
     * @param id
     *            - ID of the MediaEngine object
     * @return MediaEngine
     * @throws GetMediaEngineException
     */
    MediaEngine get(Long id) throws GetMediaEngineException;

    /**
     * This method will return MediaEngine object for given media file version ID
     *
     * @param mediaVersionId
     *            - ID of the media file version
     * @return MediaEngine object
     * @throws GetMediaEngineException
     */
    MediaEngine getByMediaVersionId(Long mediaVersionId) throws GetMediaEngineException;

    /**
     * This method will return the last MediaEngine object for given media fileId
     *
     * @param fileId
     *            - ID of the media file version
     * @return MediaEngine object
     * @throws GetMediaEngineException
     */
    MediaEngine getByFileId(Long fileId) throws GetMediaEngineException;

    /**
     * This method will get all MediaEngine objects
     *
     * @return List of MediaEngine objects or empty list
     * @throws GetMediaEngineException
     */
    List<MediaEngine> getAll() throws GetMediaEngineException;

    /**
     * This method will get all MediaEngine objects by status
     *
     * @param status
     *            - Status of the MediaEngine object
     * @return List of MediaEngine objects or empty list
     * @throws GetMediaEngineException
     */
    List<MediaEngine> getAllByStatus(String status) throws GetMediaEngineException;

    /**
     * This method will save given MediaEngine object in database. The method should be used only for MediaEngine
     * objects that are already in the database. Otherwise, should throw SaveMediaEngineException
     *
     * @param mediaEngine
     *            - MediaEngine object that is already in database
     * @return Saved MediaEngine object
     * @throws SaveMediaEngineException
     */
    MediaEngine save(MediaEngine mediaEngine) throws SaveMediaEngineException;

    /**
     * This method will create copy a of given MediaEngine object. Every fields will have the same value except the ID
     *
     * @param mediaEngine
     *            - MediaEngine object that is already in database that we want to make a copy
     * @return Copied MediaEngine object
     * @throws CreateMediaEngineException
     */
    MediaEngine copy(MediaEngine mediaEngine, EcmFileVersion ecmFileVersion) throws CreateMediaEngineException;

    /**
     * This method will complete the process and set the status to COMPLETED
     *
     * @param id
     *            - ID of MediaEngine object
     * @return Updated MediaEngine object
     * @throws SaveMediaEngineException
     */
    MediaEngine complete(Long id) throws SaveMediaEngineException;

    /**
     * This method will cancel the process and set the status to DRAFT
     *
     * @param id
     *            - ID of MediaEngine object
     * @return Updated MediaEngine object
     * @throws SaveMediaEngineException
     */
    MediaEngine cancel(Long id) throws SaveMediaEngineException;

    /**
     * This method will fail the process and set the status to FAILED
     *
     * @param id
     *            - ID of MediaEngine object
     * @return Updated MediaEngine object
     * @throws SaveMediaEngineException
     */
    MediaEngine fail(Long id, String message) throws SaveMediaEngineException;

    /**
     * This method will purge MediaEngine information
     *
     * @param mediaEngine
     *            - MediaEngine object
     * @return boolean - true/false
     */
    boolean purge(MediaEngine mediaEngine) throws GetConfigurationException, MediaEngineProviderNotFound;

    /**
     * This method will change status of the MediaEngine object for given ID
     *
     * @param id
     *            - ID of the MediaEngine object
     * @param status
     *            - The status of the MediaEngine object that needed to be stored
     * @return MediaEngine object with the new status
     * @throws SaveMediaEngineException
     */
    MediaEngine changeStatus(Long id, String status) throws SaveMediaEngineException;

    /**
     * This methot will change statuses of the MediaEngine objects for given list of IDs
     *
     * @param ids
     *            - IDs of the MediaEngine objects
     * @param status
     *            - The status of the MediaEngine objects that needed to be stored
     * @return List of MediaEngine objects with the new status
     * @throws SaveMediaEngineException
     */
    List<MediaEngine> changeStatusMultiple(List<Long> ids, String status) throws SaveMediaEngineException;

    /**
     * This method will notify user for the action performed under MediaEngine object
     *
     * @param id
     *            - ID of the MediaEngine object
     * @param action
     *            - Action that is performed
     */
    void notify(Long id, String action);

    /**
     * This method will notify user for the action performed under list of MediaEngine objects
     *
     * @param ids
     *            - List of IDs of the MediaEngine objects
     * @param action
     *            - Action that is performed
     */
    void notifyMultiple(List<Long> ids, String action);

    /**
     * This method will audit performed action for MediaEngine object
     *
     * @param id
     *            - ID of the MediaEngine object
     * @param action
     *            - Action that is performed
     * @param message
     *            - Descriptive message for mediaEngine status
     */
    void audit(Long id, String action, String message);

    /**
     * This method will audit performed action for list of MediaEngine objects
     *
     * @param ids
     *            - List of IDs of the MediaEngine objects
     * @param action
     *            - Action that is performed
     * @param message
     *            - Descriptive message for mediaEngine status
     */
    void auditMultiple(List<Long> ids, String action, String message);

    /**
     * This method will start business process defined for MediaEngine object
     *
     * @param mediaEngine
     * @return
     */
    ProcessInstance startBusinessProcess(MediaEngine mediaEngine, String serviceName);

    /**
     * This method will remove MediaEngine object from the waiting state
     *
     * @param processInstance
     * @param status
     *            The next status after signal
     * @param action
     *            The action that is performing
     */
    void signal(ProcessInstance processInstance, String status, String action);

    /**
     * This method will return factory that provides correct provider service
     *
     * @return TranscribeServiceFactory object
     */
    MediaEngineServiceFactory getMediaEngineServiceFactory();

    /**
     * This method will return configuration for MediaEngine service
     *
     * @return Configuration object
     * @throws GetConfigurationException
     */
    MediaEngineConfiguration getConfiguration() throws GetConfigurationException;

    /**
     * This method will save configuration for MediaEngine service
     *
     * @param configuration
     *            - Configuration object that should be saved
     * @return Saved Configuration object
     * @throws SaveConfigurationException
     */
    void saveConfiguration(MediaEngineConfiguration configuration) throws SaveConfigurationException, MediaEngineServiceNotFoundException;

    /**
     * This method will return true if all conditions are reached for proceeding.
     *
     * @param ecmFileVersion
     *            - File version
     * @return true/false
     */
    boolean allow(EcmFileVersion ecmFileVersion);

    /**
     * This method is called from MediaEngine Workflow.
     * It will check process status, and it will update the status in DB
     *
     * @param delegateExecution
     */
    void checkStatus(DelegateExecution delegateExecution);

    /**
     * This method is called from MediaEngine Workflow.
     * It will create/execute process on provider side.
     *
     * @param delegateExecution
     */
    void process(DelegateExecution delegateExecution);

    /**
     * This method is called from MediaEngine Workflow.
     * Provider is called, to delete all temp files.
     *
     * @param delegateExecution
     */
    void purge(DelegateExecution delegateExecution);

    /**
     * This method is called from MediaEngine Workflow.
     * If the process is completed, this method will remove remoteProcessId from the DB.
     *
     * @param delegateExecution
     */
    void removeProcessId(DelegateExecution delegateExecution);

    /**
     * This method will return if automatic mediaEngine is enabled
     *
     * @return true/false
     */
    boolean isAutomaticOn();

    /**
     * This method will return if required service is enabled
     *
     * @return true/false
     */
    boolean isServiceEnabled();

    /**
     * This method will return if provided file is an adequate candidate for processing.
     *
     * @param ecmFileVersion
     *            - File version
     * @return true/false
     */
    boolean isProcessable(EcmFileVersion ecmFileVersion);

    /**
     * This method will return true if file type contains in mediaEngine.excludedFileTypes
     * in .arkcase/acm/ecmFileService.properties
     *
     * @return true/false
     */
    boolean isExcludedFileTypes(String fileType);

    /**
     * This method will return service name.
     *
     * @return service name
     */
    String getServiceName();

    /**
     * This method will return service system user.
     *
     * @return service system user
     */
    String getSystemUser();

    /**
     * This method will return mediaEngine object. Should be implemented in concrete service implementations.
     *
     * @param fileId
     * @param versionId
     * @return MediaEngine object
     * @throws GetMediaEngineException
     */
    MediaEngine getForCopiedFile(Long fileId, Long versionId) throws GetMediaEngineException;

    /**
     * This method will create temp file, so we can send it to tool integration instead of ArkCase EcmFile object.
     *
     * @param mediaEngine
     * @param tempPath
     *            shared folder used to store temp files.
     * @return File
     * @throws IOException
     * @throws ArkCaseFileRepositoryException
     */
    File createTempFile(MediaEngine mediaEngine, String tempPath) throws IOException, ArkCaseFileRepositoryException;

    /**
     *
     * @param mediaVersionId
     *            - ID of the media file version
     * @return Failure reason message
     */
    Map<String, String> getFailureReasonMessage(Long mediaVersionId);
}
