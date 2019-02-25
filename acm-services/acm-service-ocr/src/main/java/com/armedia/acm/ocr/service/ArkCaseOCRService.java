package com.armedia.acm.ocr.service;

/*-
 * #%L
 * acm-ocr
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

import com.armedia.acm.core.model.AcmEvent;
import com.armedia.acm.ocr.exception.CreateOCRException;
import com.armedia.acm.ocr.exception.GetConfigurationException;
import com.armedia.acm.ocr.exception.GetOCRException;
import com.armedia.acm.ocr.exception.SaveConfigurationException;
import com.armedia.acm.ocr.exception.SaveOCRException;
import com.armedia.acm.ocr.factory.OCRServiceFactory;
import com.armedia.acm.ocr.model.OCR;
import com.armedia.acm.ocr.model.OCRConfiguration;
import com.armedia.acm.ocr.model.OCRStatusType;
import com.armedia.acm.ocr.model.OCRType;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;

import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Vladimir Cherepnalkovski
 */
public interface ArkCaseOCRService extends OCRService
{

    /**
     * This method will start process to create searchable PDF for given event
     *
     * @param event
     *            - Added or Replaced event
     */
    @Transactional
    public void create(AcmEvent event) throws CreateOCRException;

    /**
     * This method will create searchable PDF for given file version ID
     *
     * @param ecmFileVersionId
     *            - ID of the file version
     * @param type
     *            - AUTOMATIC or MANUAL
     * @return OCR object
     */
    @Transactional
    public OCR create(Long ecmFileVersionId, OCRType type) throws CreateOCRException;

    /**
     * This method will create OCR for given file version
     *
     * @param ecmFileVersion
     *            - Media File Version
     * @param type
     *            - AUTOMATIC or MANUAL
     * @return OCR object
     */
    @Transactional
    public OCR create(EcmFileVersion ecmFileVersion, OCRType type) throws CreateOCRException;

    /**
     * This method will get OCR object for given ID
     *
     * @param id
     *            - ID of the OCR object
     * @return OCR
     */
    public OCR get(Long id) throws GetOCRException;

    /**
     * This method will return OCR object for given media file version ID
     *
     * @param mediaVersionId
     *            - ID of the media file version
     * @return OCR object
     */
    public OCR getByMediaVersionId(Long mediaVersionId) throws GetOCRException;

    /**
     * This method will return OCR object for given fileId
     * *
     *
     * @param fileId
     *            - ID of the file
     * @return OCR object
     */
    public OCR getByFileId(Long fileId) throws GetOCRException;

    /**
     * This method will return OCR object from QUEUE for given fileId
     * *
     *
     * @param fileId
     *            - ID of the file
     * @param statusType
     *            - OCR status type
     * @return OCR object
     */
    public OCR getByFileIdAndStatus(Long fileId, OCRStatusType statusType) throws GetOCRException;

    /**
     * This method will save given OCR object in database. The method should be used only for OCR
     * objects that are already in the database. Otherwise, should throw SaveOCRException
     *
     * @param ocr
     *            - OCR object that is already in database
     * @return Saved OCR object
     */
    public OCR save(OCR ocr) throws SaveOCRException;

    /**
     * This method will create copy a of given OCR object. Every fields will have the same value except the ID
     *
     * @param ocr
     *            - OCR object that is already in database that we want to make a copy
     * @return Copied OCR object
     */
    public OCR copy(OCR ocr, EcmFileVersion ecmFileVersion) throws CreateOCRException;

    /**
     * This method will complete the process and set the status to COMPLETED
     *
     * @param id
     *            - ID of OCR object
     * @return Updated OCR object
     */
    public OCR complete(Long id) throws SaveOCRException;

    /**
     * This method will cancel the process and set the status to DRAFT
     *
     * @param id
     *            - ID of OCR object
     * @return Updated OCR object
     */
    public OCR cancel(Long id) throws SaveOCRException;

    /**
     * This method will fail the process and set the status to FAILED
     *
     * @param id
     *            - ID of OCR object
     * @return Updated OCR object
     */
    public OCR fail(Long id) throws SaveOCRException;

    /**
     * This method will change status of the OCR object for given ID
     *
     * @param id
     *            - ID of the OCR object
     * @param status
     *            - The status of the OCR object that needed to be stored
     * @return OCR object with the new status
     */
    public OCR changeStatus(Long id, String status) throws SaveOCRException;

    /**
     * This methot will change statuses of the OCR objects for given list of IDs
     *
     * @param ids
     *            - IDs of the OCR objects
     * @param status
     *            - The status of the OCR objects that needed to be stored
     * @return List of OCR objects with the new status
     */
    public List<OCR> changeStatusMultiple(List<Long> ids, String status) throws SaveOCRException;

    /**
     * This method will notify user for the action performed under OCR object
     *
     * @param id
     *            - ID of the OCR object
     * @param action
     *            - Action that is performed
     */
    public void notify(Long id, String action);

    /**
     * This method will notify user for the action performed under list of OCR objects
     *
     * @param ids
     *            - List of IDs of the OCR objects
     * @param action
     *            - Action that is performed
     */
    public void notifyMultiple(List<Long> ids, String action);

    /**
     * This method will audit performed action for OCR object
     *
     * @param id
     *            - ID of the OCR object
     * @param action
     *            - Action that is performed
     */
    public void audit(Long id, String action);

    /**
     * This method will audit performed action for list of OCR objects
     *
     * @param ids
     *            - List of IDs of the OCR objects
     * @param action
     *            - Action that is performed
     */
    public void auditMultiple(List<Long> ids, String action);

    /**
     * This method will create word document for given OCR object ID
     *
     * @param id
     *            - ID of the OCR object
     * @return EcmFile object
     */
    public EcmFile compile(Long id);

    /**
     * This method will start business process defined for OCR object
     *
     * @param ocr
     * @return
     */
    public ProcessInstance startBusinessProcess(OCR ocr);

    /**
     * This method will remove OCR object from the waiting state
     *
     * @param processInstance
     * @param status
     *            The next status after signal
     * @param action
     *            The action that is performing
     */
    public void signal(ProcessInstance processInstance, String status, String action);

    /**
     * This method will return configuration for OCR service
     *
     * @return OCR Configuration object
     */
    public OCRConfiguration getConfiguration() throws GetConfigurationException;

    /**
     * This method will save configuration for OCR service
     *
     * @param configuration
     *            - Configuration object that should be saved
     * @return Saved OCRConfiguration object
     */
    public void saveConfiguration(OCRConfiguration configuration) throws SaveConfigurationException;

    /**
     * This method will verify that all required libraries are installed in order to enable OCR.
     *
     * @param configuration
     *            - OCR Configuration object
     * @throws SaveConfigurationException
     */
    public void verifyOCR(OCRConfiguration configuration) throws SaveConfigurationException;

    /**
     * This method will return true if all conditions are reached for proceeding with automatic transcription
     *
     * @param ecmFileVersion
     *            - File version
     * @return true/false
     */
    public boolean allow(EcmFileVersion ecmFileVersion);

    /**
     * This method will return if ocr is enabled
     *
     * @return true/false
     */
    public boolean isOCREnabled();

    /**
     * This method will return if provided file is image or image only PDF
     *
     * @param ecmFileVersion
     *            - File version
     * @return true/false
     */
    public boolean isFileVersionOCRable(EcmFileVersion ecmFileVersion);

    /**
     * This method will return factory that provides correct provider service
     *
     * @return OCRServiceFactory object
     */
    public OCRServiceFactory getOCRServiceFactory();
    /**
     * This method will return true if file type contains in ocr.excludedFileTypes
     * in .arkcase/acm/ecmFileService.properties
     *
     * @return OCRServiceFactory object
     */
    public boolean isExcludedFileTypes(String fileType);
}
