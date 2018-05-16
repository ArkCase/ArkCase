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

import com.armedia.acm.services.transcribe.exception.CreateTranscribeException;
import com.armedia.acm.services.transcribe.exception.GetTranscribeException;
import com.armedia.acm.services.transcribe.model.Transcribe;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 02/27/2018
 */
public interface TranscribeService
{
    /**
     * This method will create Transcribe
     *
     * @param transcribe
     *            - Transcribe object
     * @return Transcribe
     * @throws CreateTranscribeException
     */
    @Transactional
    public Transcribe create(Transcribe transcribe) throws CreateTranscribeException;

    /**
     * This method will get the Transcribe by given remote ID (ID that is stored on provider side)
     *
     * @param remoteId
     *            - ID stored on provider side
     * @return Transcribe
     * @throws GetTranscribeException
     */
    public Transcribe get(String remoteId) throws GetTranscribeException;

    /**
     * This method will get all Transcribe objects
     *
     * @return List of Transcribe objects or empty list
     * @throws GetTranscribeException
     */
    public List<Transcribe> getAll() throws GetTranscribeException;

    /**
     * This method will get all Transcribe objects by status
     *
     * @param status
     *            - Status of the Transcribe object
     * @return List of Transcribe objects or empty list
     * @throws GetTranscribeException
     */
    public List<Transcribe> getAllByStatus(String status) throws GetTranscribeException;

    /**
     * This method will get Transcribe objects page for given start index and number of objects that needed to be return
     *
     * @param start
     *            - Start index of the Transcribe object in the list
     * @param n
     *            - Number of objects that should be return
     * @return List of Transcribe objects or empty list
     * @throws GetTranscribeException
     */
    public List<Transcribe> getPage(int start, int n) throws GetTranscribeException;

    /**
     * This method will get Transcribe objects page for given start index, number of objects that needed to be return
     * and filtered by status
     *
     * @param start
     *            - Start index of the Transcribe object in the list
     * @param n
     *            - Number of objects that should be return
     * @param status
     *            - Status of the Transcribe object
     * @return List of Transcribe objects or empty list
     * @throws GetTranscribeException
     */
    public List<Transcribe> getPageByStatus(int start, int n, String status) throws GetTranscribeException;

    /**
     * This method will purge Transcribe information
     *
     * @param transcribe
     *            - Transcribe object
     * @return boolean - true/false
     */
    public boolean purge(Transcribe transcribe);
}
