package com.armedia.acm.services.transcribe.service;

import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.armedia.acm.services.transcribe.exception.CompileTranscribeException;
import com.armedia.acm.services.transcribe.exception.CreateTranscribeException;
import com.armedia.acm.services.transcribe.exception.GetConfigurationException;
import com.armedia.acm.services.transcribe.exception.GetTranscribeException;
import com.armedia.acm.services.transcribe.exception.SaveConfigurationException;
import com.armedia.acm.services.transcribe.exception.SaveTranscribeException;
import com.armedia.acm.services.transcribe.factory.TranscribeServiceFactory;
import com.armedia.acm.services.transcribe.model.Transcribe;
import com.armedia.acm.services.transcribe.model.TranscribeConfiguration;
import com.armedia.acm.services.transcribe.model.TranscribeType;

import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 02/27/2018
 */
public interface ArkCaseTranscribeService extends TranscribeService
{
    /**
     * This method will create Transcribe for given media file version ID
     *
     * @param mediaVersionId
     *            - ID of the media file version
     * @param type
     *            - AUTOMATIC or MANUAL
     * @return Transcribe object
     * @throws CreateTranscribeException
     */
    @Transactional
    public Transcribe create(Long mediaVersionId, TranscribeType type) throws CreateTranscribeException;

    /**
     * This method will create Transcribe for given media file version
     *
     * @param mediaVersion
     *            - Media File Version
     * @param type
     *            - AUTOMATIC or MANUAL
     * @return Transcribe object
     * @throws CreateTranscribeException
     */
    @Transactional
    public Transcribe create(EcmFileVersion mediaVersion, TranscribeType type) throws CreateTranscribeException;

    /**
     * This method will get Transcribe object for given ID
     *
     * @param id
     *            - ID of the Transcribe object
     * @return Transcribe
     * @throws GetTranscribeException
     */
    public Transcribe get(Long id) throws GetTranscribeException;

    /**
     * This method will return Transcribe object for given media file version ID
     *
     * @param mediaVersionId
     *            - ID of the media file version
     * @return Transcribe object
     * @throws GetTranscribeException
     */
    public Transcribe getByMediaVersionId(Long mediaVersionId) throws GetTranscribeException;

    /**
     * This method will save given Transcribe object in database. The method should be used only for Transcribe
     * objects that are already in the database. Otherwise, should throw SaveTranscribeException
     *
     * @param transcribe
     *            - Transcribe object that is already in database
     * @return Saved Transcribe object
     * @throws SaveTranscribeException
     */
    public Transcribe save(Transcribe transcribe) throws SaveTranscribeException;

    /**
     * This method will create copy a of given Transcribe object. Every fields will have the same value except the ID
     *
     * @param transcribe
     *            - Transcribe object that is already in database that we want to make a copy
     * @return Copied Transcribe object
     * @throws CreateTranscribeException
     */
    public Transcribe copy(Transcribe transcribe, EcmFileVersion ecmFileVersion) throws CreateTranscribeException;

    /**
     * This method will complete the process and set the status to COMPLETED
     *
     * @param id
     *            - ID of Transcribe object
     * @return Updated Transcribe object
     * @throws SaveTranscribeException
     */
    public Transcribe complete(Long id) throws SaveTranscribeException;

    /**
     * This method will cancel the process and set the status to DRAFT
     *
     * @param id
     *            - ID of Transcribe object
     * @return Updated Transcribe object
     * @throws SaveTranscribeException
     */
    public Transcribe cancel(Long id) throws SaveTranscribeException;

    /**
     * This method will fail the process and set the status to FAILED
     *
     * @param id
     *            - ID of Transcribe object
     * @return Updated Transcribe object
     * @throws SaveTranscribeException
     */
    public Transcribe fail(Long id) throws SaveTranscribeException;

    /**
     * This method will change status of the Transcribe object for given ID
     *
     * @param id
     *            - ID of the Transcribe object
     * @param status
     *            - The status of the Transcribe object that needed to be stored
     * @return Transcribe object with the new status
     * @throws SaveTranscribeException
     */
    public Transcribe changeStatus(Long id, String status) throws SaveTranscribeException;

    /**
     * This methot will change statuses of the Transcribe objects for given list of IDs
     *
     * @param ids
     *            - IDs of the Transcribe objects
     * @param status
     *            - The status of the Transcribe objects that needed to be stored
     * @return List of Transcribe objects with the new status
     * @throws SaveTranscribeException
     */
    public List<Transcribe> changeStatusMultiple(List<Long> ids, String status) throws SaveTranscribeException;

    /**
     * This method will notify user for the action performed under Transcribe object
     *
     * @param id
     *            - ID of the Transcribe object
     * @param action
     *            - Action that is performed
     */
    public void notify(Long id, String action);

    /**
     * This method will notify user for the action performed under list of Transcribe objects
     *
     * @param ids
     *            - List of IDs of the Transcribe objects
     * @param action
     *            - Action that is performed
     */
    public void notifyMultiple(List<Long> ids, String action);

    /**
     * This method will audit performed action for Transcribe object
     *
     * @param id
     *            - ID of the Transcribe object
     * @param action
     *            - Action that is performed
     */
    public void audit(Long id, String action);

    /**
     * This method will audit performed action for list of Transcribe objects
     *
     * @param ids
     *            - List of IDs of the Transcribe objects
     * @param action
     *            - Action that is performed
     */
    public void auditMultiple(List<Long> ids, String action);

    /**
     * This method will create word document for given Transcribe object ID
     *
     * @param id
     *            - ID of the Transcribe object
     * @return EcmFile object
     * @throws CompileTranscribeException
     */
    public EcmFile compile(Long id) throws CompileTranscribeException;

    /**
     * This method will start business process defined for Transcribe object
     *
     * @param transcribe
     * @return
     */
    public ProcessInstance startBusinessProcess(Transcribe transcribe);

    /**
     * This method will remove Transcribe object from the waiting state
     *
     * @param processInstance
     * @param status
     *            The next status after signal
     * @param action
     *            The action that is performing
     */
    public void signal(ProcessInstance processInstance, String status, String action);

    /**
     * This method will return factory that provides correct provider service
     *
     * @return TranscribeServiceFactory object
     */
    public TranscribeServiceFactory getTranscribeServiceFactory();

    /**
     * This method will return configuration for Transcribe service
     *
     * @return TranscribeConfiguration object
     * @throws GetConfigurationException
     */
    public TranscribeConfiguration getConfiguration() throws GetConfigurationException;

    /**
     * This method will save configuration for Transcribe service
     *
     * @param configuration
     *            - Configuration object that should be saved
     * @return Saved TranscribeConfiguration object
     * @throws SaveConfigurationException
     */
    public TranscribeConfiguration saveConfiguration(TranscribeConfiguration configuration) throws SaveConfigurationException;

    /**
     * This method will return true if all conditions are reached for proceeding with automatic transcription
     *
     * @param ecmFileVersion
     *            - File version
     * @return true/false
     */
    public boolean allow(EcmFileVersion ecmFileVersion);

    /**
     * This method will return if transcribe is enabled
     *
     * @return true/false
     */
    public boolean isTranscribeOn();

    /**
     * This method will return if automatic transcribe is enabled
     *
     * @return true/false
     */
    public boolean isAutomaticTranscribeOn();

    /**
     * This method will return if provided media duration is less than configured duration
     *
     * @param ecmFileVersion
     *            - Media file version
     * @return true/false
     */
    public boolean isMediaDurationAllowed(EcmFileVersion ecmFileVersion);

    /**
     * This method will return if provided file is audio or video
     *
     * @param ecmFileVersion
     *            - File version
     * @return true/false
     */
    public boolean isFileVersionTranscribable(EcmFileVersion ecmFileVersion);
}
