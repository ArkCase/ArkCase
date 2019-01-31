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

import com.armedia.acm.ocr.exception.CreateOCRException;
import com.armedia.acm.ocr.exception.GetOCRException;
import com.armedia.acm.ocr.model.OCR;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Vladimir Cherepnalkovski
 */
public interface OCRService
{

    @Transactional
    public OCR create(OCR ocr) throws CreateOCRException;

    public OCR get(String remoteId) throws GetOCRException;

    public List<OCR> getAll() throws GetOCRException;

    /**
     * This method will get all OCR objects by status
     *
     * @param status
     *            - Status of the OCR object
     * @return List of OCR objects or empty list
     * @throws GetOCRException
     */
    public List<OCR> getAllByStatus(String status) throws GetOCRException;

    /**
     * This method will get OCR objects page for given start index and number of objects that needed to be return
     *
     * @param start
     *            - Start index of the OCR object in the list
     * @param n
     *            - Number of objects that should be return
     * @return List of OCR objects or empty list
     * @throws GetOCRException
     */
    public List<OCR> getPage(int start, int n) throws GetOCRException;

    /**
     * This method will get OCR objects page for given start index, number of objects that needed to be return
     * and filtered by status
     *
     * @param start
     *            - Start index of the OCR object in the list
     * @param n
     *            - Number of objects that should be return
     * @param status
     *            - Status of the OCR object
     * @return List of OCR objects or empty list
     * @throws GetOCRException
     */
    public List<OCR> getPageByStatus(int start, int n, String status) throws GetOCRException;

    /**
     * This method will purge OCR information
     *
     * @param ocr
     *            - OCR object
     * @return boolean - true/false
     */
    public boolean purge(OCR ocr);

}
