package com.armedia.acm.services.transcribe.service;

import com.armedia.acm.services.transcribe.exception.CreateTranscribeException;
import com.armedia.acm.services.transcribe.exception.GetTranscribeException;
import com.armedia.acm.services.transcribe.model.Transcribe;

import java.util.List;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 02/27/2018
 */
public interface TranscribeService
{
    /**
     * This method will create Transcribe
     *
     * @param transcribe - Transcribe object
     * @return Transcribe
     * @throws CreateTranscribeException
     */
    public Transcribe create(Transcribe transcribe) throws CreateTranscribeException;

    /**
     * This method will get the Transcribe by given remote ID (ID that is stored on provider side)
     *
     * @param remoteId - ID stored on provider side
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
     * @param status - Status of the Transcribe object
     * @return List of Transcribe objects or empty list
     * @throws GetTranscribeException
     */
    public List<Transcribe> getAllByStatus(String status) throws GetTranscribeException;

    /**
     * This method will get Transcribe objects page for given start index and number of objects that needed to be return
     *
     * @param start - Start index of the Transcribe object in the list
     * @param n - Number of objects that should be return
     * @return List of Transcribe objects or empty list
     * @throws GetTranscribeException
     */
    public List<Transcribe> getPage(int start, int n) throws GetTranscribeException;

    /**
     * This method will get Transcribe objects page for given start index, number of objects that needed to be return
     * and filtered by status
     *
     * @param start - Start index of the Transcribe object in the list
     * @param n - Number of objects that should be return
     * @param status - Status of the Transcribe object
     * @return List of Transcribe objects or empty list
     * @throws GetTranscribeException
     */
    public List<Transcribe> getPageByStatus(int start, int n, String status) throws GetTranscribeException;
}
