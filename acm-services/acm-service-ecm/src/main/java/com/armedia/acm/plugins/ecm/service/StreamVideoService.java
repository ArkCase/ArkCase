package com.armedia.acm.plugins.ecm.service;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.mule.api.MuleException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by riste.tutureski on 6/6/2017.
 */
public interface StreamVideoService
{
    public void stream(String cmisId, HttpServletRequest request, HttpServletResponse response, EcmFile ecmFile, String version) throws AcmUserActionFailedException, MuleException, AcmObjectNotFoundException, IOException;

    public void stream(HttpServletRequest request, HttpServletResponse response, ContentStream payload, EcmFile ecmFile, String version) throws IOException;
}
