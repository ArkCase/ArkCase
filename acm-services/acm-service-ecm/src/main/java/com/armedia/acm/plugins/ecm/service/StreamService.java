package com.armedia.acm.plugins.ecm.service;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import org.apache.chemistry.opencmis.client.api.Document;
import org.mule.api.MuleException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by riste.tutureski on 6/6/2017.
 */
public interface StreamService
{
    public void stream(Long id, String version, HttpServletRequest request, HttpServletResponse response) throws AcmUserActionFailedException, MuleException, AcmObjectNotFoundException, IOException;

    public void stream(Document payload, EcmFile file, String version, HttpServletRequest request, HttpServletResponse response) throws IOException;
}
