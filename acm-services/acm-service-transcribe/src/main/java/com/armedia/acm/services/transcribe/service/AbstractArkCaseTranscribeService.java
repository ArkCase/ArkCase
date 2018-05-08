package com.armedia.acm.services.transcribe.service;

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

import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.armedia.acm.services.transcribe.exception.CompileTranscribeException;
import com.armedia.acm.services.transcribe.exception.CreateTranscribeException;
import com.armedia.acm.services.transcribe.exception.CreateTranscribeItemException;
import com.armedia.acm.services.transcribe.exception.GetConfigurationException;
import com.armedia.acm.services.transcribe.exception.GetTranscribeException;
import com.armedia.acm.services.transcribe.exception.SaveConfigurationException;
import com.armedia.acm.services.transcribe.exception.SaveTranscribeException;
import com.armedia.acm.services.transcribe.exception.SaveTranscribeItemException;
import com.armedia.acm.services.transcribe.factory.TranscribeServiceFactory;
import com.armedia.acm.services.transcribe.model.Transcribe;
import com.armedia.acm.services.transcribe.model.TranscribeConfiguration;
import com.armedia.acm.services.transcribe.model.TranscribeItem;
import com.armedia.acm.services.transcribe.model.TranscribeType;

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
     * @param mediaVersionId
     *            - ID of the media file version
     * @param type
     *            - AUTOMATIC or MANUAL
     * @return Transcribe object
     * @throws CreateTranscribeException
     */
    @Transactional
    public abstract Transcribe create(Long mediaVersionId, TranscribeType type) throws CreateTranscribeException;

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
    public abstract Transcribe create(EcmFileVersion mediaVersion, TranscribeType type) throws CreateTranscribeException;

    /**
     * This method will get Transcribe object for given ID
     *
     * @param id
     *            - ID of the Transcribe object
     * @return Transcribe
     * @throws GetTranscribeException
     */
    public abstract Transcribe get(Long id) throws GetTranscribeException;

    /**
     * This method will return Transcribe object for given media file version ID
     *
     * @param mediaVersionId
     *            - ID of the media file version
     * @return Transcribe object
     * @throws GetTranscribeException
     */
    public abstract Transcribe getByMediaVersionId(Long mediaVersionId) throws GetTranscribeException;

    /**
     * This method will save given Transcribe object in database. The method should be used only for Transcribe
     * objects that are already in the database. Otherwise, should throw SaveTranscribeException
     *
     * @param transcribe
     *            - Transcribe object that is already in database
     * @return Saved Transcribe object
     * @throws SaveTranscribeException
     */
    public abstract Transcribe save(Transcribe transcribe) throws SaveTranscribeException;

    /**
     * This method will create copy a of given Transcribe object. Every fields will have the same value except the ID
     *
     * @param transcribe
     *            - Transcribe object that is already in database that we want to make a copy
     * @return Copied Transcribe object
     * @throws CreateTranscribeException
     */
    public abstract Transcribe copy(Transcribe transcribe, EcmFileVersion ecmFileVersion) throws CreateTranscribeException;

    /**
     * This method will complete the process and set the status to COMPLETED
     *
     * @param id
     *            - ID of Transcribe object
     * @return Updated Transcribe object
     * @throws SaveTranscribeException
     */
    public abstract Transcribe complete(Long id) throws SaveTranscribeException;

    /**
     * This method will cancel the process and set the status to DRAFT
     *
     * @param id
     *            - ID of Transcribe object
     * @return Updated Transcribe object
     * @throws SaveTranscribeException
     */
    public abstract Transcribe cancel(Long id) throws SaveTranscribeException;

    /**
     * This method will fail the process and set the status to FAILED
     *
     * @param id
     *            - ID of Transcribe object
     * @return Updated Transcribe object
     * @throws SaveTranscribeException
     */
    public abstract Transcribe fail(Long id) throws SaveTranscribeException;

    /**
     * This method will create TranscribeItem in the Transcribe with given ID
     *
     * @param id
     *            - ID of the Transcribe object
     * @param item
     *            - TranscribeItem object
     * @return Created TranscribeItem object
     * @throws CreateTranscribeItemException
     */
    public abstract TranscribeItem createItem(Long id, TranscribeItem item) throws CreateTranscribeItemException;

    /**
     * This method will save given TranscribeItem object in database. The method should be used only for TranscribeItem
     * objects that are already in the database. Otherwise, should throw SaveTranscribeItemException
     *
     * @param item
     *            - TranscribItem object that is already in the database
     * @return Saved TranscribeItem object
     * @throws SaveTranscribeItemException
     */
    public abstract TranscribeItem saveItem(TranscribeItem item) throws SaveTranscribeItemException;

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
    public abstract Transcribe changeStatus(Long id, String status) throws SaveTranscribeException;

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
    public abstract List<Transcribe> changeStatusMultiple(List<Long> ids, String status) throws SaveTranscribeException;

    /**
     * This method will notify user for the action performed under Transcribe object
     *
     * @param id
     *            - ID of the Transcribe object
     * @param action
     *            - Action that is performed
     */
    public abstract void notify(Long id, String action);

    /**
     * This method will notify user for the action performed under list of Transcribe objects
     *
     * @param ids
     *            - List of IDs of the Transcribe objects
     * @param action
     *            - Action that is performed
     */
    public abstract void notifyMultiple(List<Long> ids, String action);

    /**
     * This method will audit performed action for Transcribe object
     *
     * @param id
     *            - ID of the Transcribe object
     * @param action
     *            - Action that is performed
     */
    public abstract void audit(Long id, String action);

    /**
     * This method will audit performed action for list of Transcribe objects
     *
     * @param ids
     *            - List of IDs of the Transcribe objects
     * @param action
     *            - Action that is performed
     */
    public abstract void auditMultiple(List<Long> ids, String action);

    /**
     * This method will create word document for given Transcribe object ID
     *
     * @param id
     *            - ID of the Transcribe object
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
     * @param status
     *            The next status after signal
     * @param action
     *            The action that is performing
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
     * @param configuration
     *            - Configuration object that should be saved
     * @return Saved TranscribeConfiguration object
     * @throws SaveConfigurationException
     */
    public abstract TranscribeConfiguration saveConfiguration(TranscribeConfiguration configuration) throws SaveConfigurationException;
}
