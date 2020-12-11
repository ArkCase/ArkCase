package gov.foia.service;

/*-
 * #%L
 * ACM Default Plugin: Case File
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

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.casefile.exceptions.SplitCaseFileException;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.SplitCaseOptions;
import com.armedia.acm.plugins.casefile.service.SplitCaseServiceImpl;
import com.armedia.acm.plugins.ecm.exception.AcmFolderException;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import com.armedia.acm.plugins.task.exception.AcmTaskException;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import gov.foia.dao.FOIARequestDao;
import gov.foia.model.FOIARequest;

/**
 * Created by Aleksandar Acevski <aleksandar.acevski@armedia.com> on December, 2020
 */
public class SplitRequestServiceImpl extends SplitCaseServiceImpl
{
    private final Logger log = LogManager.getLogger(getClass());

    private FOIARequestDao foiaRequestDao;

    @Override
    @Transactional
    public CaseFile splitCase(Authentication auth,
            String ipAddress,
            SplitCaseOptions splitCaseOptions) throws PipelineProcessException, SplitCaseFileException, AcmUserActionFailedException,
            AcmCreateObjectFailedException, AcmFolderException, AcmObjectNotFoundException
    {
        FOIARequest originalRequest = foiaRequestDao.find(splitCaseOptions.getCaseFileId());

        if (originalRequest == null)
        {
            throw new SplitCaseFileException("Request with id = (" + splitCaseOptions.getCaseFileId() + ") not found");
        }

        FOIARequest copyRequest = new FOIARequest();

        Map<String, CaseFile> requests = new HashMap<>();
        requests.put("source", originalRequest);
        requests.put("copy", copyRequest);
        getSplitCaseFileBusinessRule().applyRules(requests);

        if (getTypesToCopy().contains("participants"))
        {
            copyParticipants(originalRequest, copyRequest, auth);
        }
        if (getTypesToCopy().contains("people"))
        {
            copyPeople(originalRequest, copyRequest);
        }

        ObjectAssociation childObjectCopy = createObjectAssociation(originalRequest, ASSOCIATION_CATEGORY_COPY_FROM);
        copyRequest.addChildObject(childObjectCopy);

        copyRequest.getParticipants().forEach(copyCaseFileParticipant -> copyCaseFileParticipant.setReplaceChildrenParticipant(true));
        copyRequest = (FOIARequest) getSaveCaseService().saveCase(copyRequest, auth, ipAddress);

        ObjectAssociation childObjectOriginal = createObjectAssociation(copyRequest, ASSOCIATION_CATEGORY_COPY_TO);
        originalRequest.addChildObject(childObjectOriginal);

        getSaveCaseService().saveCase(originalRequest, auth, ipAddress);

        if (getTypesToCopy().contains("tasks"))
        {
            try
            {
                copyTasks(originalRequest, copyRequest, auth, ipAddress);
            }
            catch (AcmTaskException e)
            {
                log.error("Couldn't copy tasks.", e);
            }
        }
        copyDocumentsAndFolders(copyRequest, splitCaseOptions);
        return copyRequest;
    }

    public FOIARequestDao getFoiaRequestDao()
    {
        return foiaRequestDao;
    }

    public void setFoiaRequestDao(FOIARequestDao foiaRequestDao)
    {
        this.foiaRequestDao = foiaRequestDao;
    }
}
