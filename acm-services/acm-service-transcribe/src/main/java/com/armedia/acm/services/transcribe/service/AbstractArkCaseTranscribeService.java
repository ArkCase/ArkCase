package com.armedia.acm.services.transcribe.service;

import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.services.transcribe.exception.*;
import com.armedia.acm.services.transcribe.factory.TranscribeServiceFactory;
import com.armedia.acm.services.transcribe.model.*;

import java.util.List;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 02/27/2018
 */
public abstract class AbstractArkCaseTranscribeService implements TranscribeService
{
    /**
     * This method will create Transcribe for given media file ID and media file version ID
     *
     * @param mediaId - ID of the media file
     * @param versionId - ID of the media file version
     * @return Transcribe
     * @throws CreateTranscribeException
     */
    public abstract Transcribe create(Long mediaId, Long versionId) throws CreateTranscribeException;

    /**
     * This method will get Transcribe object for given ID
     *
     * @param id - ID of the Transcribe object
     * @return Transcribe
     * @throws GetTranscribeException
     */
    public abstract Transcribe get(Long id) throws GetTranscribeException;

    /**
     * This method will return Transcribe object for given media file ID and media file version ID
     *
     * @param mediaId - ID of the media file
     * @param versionId - ID of the media file version
     * @return Transcribe object
     * @throws GetTranscribeException
     */
    public abstract Transcribe getByMediaIdAndVersionId(Long mediaId, Long versionId) throws GetTranscribeException;

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
    public abstract List<Transcribe> changeStatus(List<Long> ids, String status) throws SaveTranscribeException;

    /**
     * This method will notify user for the action performed under Transcribe object
     *
     * @param id - ID of the Transcribe object
     * @param userType - Type of the user (owner of the media file, or owner of the parent object)
     * @param action - Action that is performed
     */
    public abstract void notify(Long id, UserType userType, ActionType action);

    /**
     * This method will notify user for the action performed under list of Transcribe objects
     *
     * @param ids - List of IDs of the Transcribe objects
     * @param userType - Type of the user (owner of the media file, or owner of the parent object)
     * @param action - Action that is performed
     */
    public abstract void notify(List<Long> ids, UserType userType, ActionType action);

    /**
     * This method will audit performed action for Transcribe object
     *
     * @param id - ID of the Transcribe object
     * @param action - Action that is performed
     */
    public abstract void audit(Long id, ActionType action);

    /**
     * This method will audit performed action for list of Transcribe objects
     *
     * @param ids - List of IDs of the Transcribe objects
     * @param action - Action that is performed
     */
    public abstract void audit(List<Long> ids, ActionType action);

    /**
     * This method will create word document for given Transcribe object ID
     *
     * @param id - ID of the Transcribe object
     * @return EcmFile object
     * @throws CompileTranscribeException
     */
    public abstract EcmFile compile(Long id) throws CompileTranscribeException;

    /**
     * This method will return configuration for Transcribe service
     *
     * @return TranscribeConfiguratoin object
     * @throws GetTranscribeConfigurationException
     */
    public abstract TranscribeConfiguration getConfiguration() throws GetTranscribeConfigurationException;

    /**
     * This method will save configuration for Transcribe service
     *
     * @param configuration - TranscribeConfiguration object that should be saved
     * @return Saved TranscribeConfiguration object
     * @throws SaveTranscribeConfigurationException
     */
    public abstract TranscribeConfiguration saveConfiguration(TranscribeConfiguration configuration) throws SaveTranscribeConfigurationException;

    /**
     * This method will return factory that provides correct provider service
     *
     * @return TranscribeServiceFactory object
     */
    public abstract TranscribeServiceFactory getFactory();
}
