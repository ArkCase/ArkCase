package com.armedia.acm.services.transcribe.service;

import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.armedia.acm.services.transcribe.exception.*;
import com.armedia.acm.services.transcribe.factory.TranscribeServiceFactory;
import com.armedia.acm.services.transcribe.model.*;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 02/27/2018
 */
public abstract class AbstractArkCaseTranscribeService implements TranscribeService
{
    /**
     * This method will create Transcribe for given media file version ID
     *
     * @param mediaVersionId - ID of the media file version
     * @param type - AUTOMATIC or MANUAL
     * @return Transcribe object
     * @throws CreateTranscribeException
     */
    @Transactional
    public abstract Transcribe create(Long mediaVersionId, TranscribeType type) throws CreateTranscribeException;

    /**
     * This method will create Transcribe for given media file version
     *
     * @param mediaVersion - Media File Version
     * @param type - AUTOMATIC or MANUAL
     * @return Transcribe object
     * @throws CreateTranscribeException
     */
    @Transactional
    public abstract Transcribe create(EcmFileVersion mediaVersion, TranscribeType type) throws CreateTranscribeException;

    /**
     * This method will get Transcribe object for given ID
     *
     * @param id - ID of the Transcribe object
     * @return Transcribe
     * @throws GetTranscribeException
     */
    public abstract Transcribe get(Long id) throws GetTranscribeException;

    /**
     * This method will return Transcribe object for given media file version ID
     *
     * @param mediaVersionId - ID of the media file version
     * @return Transcribe object
     * @throws GetTranscribeException
     */
    public abstract Transcribe getByMediaVersionId(Long mediaVersionId) throws GetTranscribeException;

    /**
     * This method will save given Transcribe object in database. The method should be used only for Transcribe
     * objects that are already in the database. Otherwise, should throw SaveTranscribeException
     *
     * @param transcribe - Transcribe object that is already in database
     * @return Saved Transcribe object
     * @throws SaveTranscribeException
     */
    public abstract Transcribe save(Transcribe transcribe) throws SaveTranscribeException;

    /**
     * This method will create TranscribeItem in the Transcribe with given ID
     *
     * @param id - ID of the Transcribe object
     * @param item - TranscribeItem object
     * @return Created TranscribeItem object
     * @throws CreateTranscribeItemException
     */
    public abstract TranscribeItem createItem(Long id, TranscribeItem item) throws CreateTranscribeItemException;

    /**
     * This method will save given TranscribeItem object in database. The method should be used only for TranscribeItem
     * objects that are already in the database. Otherwise, should throw SaveTranscribeItemException
     *
     * @param item - TranscribItem object that is already in the database
     * @return Saved TranscribeItem object
     * @throws SaveTranscribeItemException
     */
    public abstract TranscribeItem saveItem(TranscribeItem item) throws SaveTranscribeItemException;

    /**
     * This method will change status of the Transcribe object for given ID
     *
     * @param id - ID of the Transcribe object
     * @param status - The status of the Transcribe object that needed to be stored
     * @return Transcribe object with the new status
     * @throws SaveTranscribeException
     */
    public abstract Transcribe changeStatus(Long id, String status) throws SaveTranscribeException;

    /**
     * This methot will change statuses of the Transcribe objects for given list of IDs
     *
     * @param ids - IDs of the Transcribe objects
     * @param status - The status of the Transcribe objects that needed to be stored
     * @return List of Transcribe objects with the new status
     * @throws SaveTranscribeException
     */
    public abstract List<Transcribe> changeStatusMultiple(List<Long> ids, String status) throws SaveTranscribeException;

    /**
     * This method will notify user for the action performed under Transcribe object
     *
     * @param id - ID of the Transcribe object
     * @param userType - Type of the user (owner of the media file, or owner of the parent object)
     * @param action - Action that is performed
     */
    public abstract void notify(Long id, String userType, String action);

    /**
     * This method will notify user for the action performed under list of Transcribe objects
     *
     * @param ids - List of IDs of the Transcribe objects
     * @param userType - Type of the user (owner of the media file, or owner of the parent object)
     * @param action - Action that is performed
     */
    public abstract void notifyMultiple(List<Long> ids, String userType, String action);

    /**
     * This method will audit performed action for Transcribe object
     *
     * @param id - ID of the Transcribe object
     * @param action - Action that is performed
     */
    public abstract void audit(Long id, String action);

    /**
     * This method will audit performed action for list of Transcribe objects
     *
     * @param ids - List of IDs of the Transcribe objects
     * @param action - Action that is performed
     */
    public abstract void auditMultiple(List<Long> ids, String action);

    /**
     * This method will create word document for given Transcribe object ID
     *
     * @param id - ID of the Transcribe object
     * @return EcmFile object
     * @throws CompileTranscribeException
     */
    public abstract EcmFile compile(Long id) throws CompileTranscribeException;

    /**
     * This method will start business process defined for Transcribe object
     *
     * @param transcribe
     * @return
     */
    public abstract ProcessInstance startBusinessProcess(Transcribe transcribe);

    /**
     * This method will remove Transcribe object from the waiting state
     *
     * @param processInstance
     * @param status The next status after signal
     * @param action The action that is performing
     */
    public abstract void signal(ProcessInstance processInstance, String status, String action);

    /**
     * This method will return factory that provides correct provider service
     *
     * @return TranscribeServiceFactory object
     */
    public abstract TranscribeServiceFactory getTranscribeServiceFactory();

    /**
     * This method will return configuration for Transcribe service
     *
     * @return TranscribeConfiguration object
     * @throws GetConfigurationException
     */
    public abstract TranscribeConfiguration getConfiguration() throws GetConfigurationException;

    /**
     * This method will save configuration for Transcribe service
     *
     * @param configuration - Configuration object that should be saved
     * @return Saved TranscribeConfiguration object
     * @throws SaveConfigurationException
     */
    public abstract TranscribeConfiguration saveConfiguration(TranscribeConfiguration configuration) throws SaveConfigurationException;
}
