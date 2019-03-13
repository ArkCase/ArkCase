package com.armedia.acm.plugins.ecm.service.sync.impl;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.ecm.dao.AcmFolderDao;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.sync.EcmEvent;
import com.armedia.acm.plugins.ecm.model.sync.EcmEventType;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.ecm.utils.FolderAndFilesUtils;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.NoResultException;

import java.util.Arrays;

/**
 * @author ivana.shekerova on 2/25/2019.
 */
public class EcmNodeDeletedEventHandlerTest
{

    private EcmNodeDeletedEventHandler unit;

    private AuditPropertyEntityAdapter auditPropertyEntityAdapter = mock(AuditPropertyEntityAdapter.class);

    private AcmFolderDao acmFolderDao = mock(AcmFolderDao.class);
    private AcmFolderService acmFolderService = mock(AcmFolderService.class);
    private EcmFileDao ecmFileDao = mock(EcmFileDao.class);
    private EcmFileService ecmFileService = mock(EcmFileService.class);
    private FolderAndFilesUtils spyFolderAndFilesUtils = spy(FolderAndFilesUtils.class);

    private EcmEvent nodeDeletedEvent;

    @Before
    public void setUp() throws Exception
    {
        unit = new EcmNodeDeletedEventHandler();

        unit.setAuditPropertyEntityAdapter(auditPropertyEntityAdapter);
        unit.setFolderService(acmFolderService);
        unit.setFileService(ecmFileService);
        spyFolderAndFilesUtils.setFileDao(ecmFileDao);
        spyFolderAndFilesUtils.setFileService(ecmFileService);
        spyFolderAndFilesUtils.setFolderDao(acmFolderDao);
        spyFolderAndFilesUtils.setFolderService(acmFolderService);
        unit.setFolderAndFilesUtils(spyFolderAndFilesUtils);

        nodeDeletedEvent = new EcmEvent(new JSONObject());
        nodeDeletedEvent.setEcmEventType(EcmEventType.MOVE);
        nodeDeletedEvent.setUserId("userId");
        nodeDeletedEvent.setNodeId("workspace://SpacesStore/c77777c7-6fff-4444-a11a-777777d77d7d");

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void onEcmNodeDeleted_ifFileIsDeletedInContentRepository_thenDeleteItFromArkcase()
    {
        auditPropertyEntityAdapter.setUserId(nodeDeletedEvent.getUserId());
        EcmFile file = new EcmFile();
        file.setFileId(111L);

        when(ecmFileDao.findByCmisFileId(nodeDeletedEvent.getNodeId())).thenReturn(Arrays.asList(file));

        unit.onEcmNodeDeleted(nodeDeletedEvent);

        verify(ecmFileDao, times(1)).findByCmisFileId(nodeDeletedEvent.getNodeId());
        verify(acmFolderDao, times(0)).findByCmisFolderId(nodeDeletedEvent.getNodeId());
    }

    @Test
    public void onEcmNodeDeleted_ifFolderIsDeletedInContentRepository_thenDeleteItFromArkcase()
    {
        auditPropertyEntityAdapter.setUserId(nodeDeletedEvent.getUserId());
        AcmFolder folder = new AcmFolder();
        folder.setId(222L);

        when(ecmFileDao.findByCmisFileId(nodeDeletedEvent.getNodeId())).thenThrow(new NoResultException());
        when(acmFolderDao.findByCmisFolderId(nodeDeletedEvent.getNodeId())).thenReturn(folder);

        // I don't know why this is needed, but Mockito throws up without it
        Authentication auth = new UsernamePasswordAuthenticationToken("test", "test");
        SecurityContextHolder.getContext().setAuthentication(auth);

        unit.onEcmNodeDeleted(nodeDeletedEvent);


        verify(ecmFileDao, times(1)).findByCmisFileId(nodeDeletedEvent.getNodeId());
        verify(acmFolderDao, times(1)).findByCmisFolderId(nodeDeletedEvent.getNodeId());
    }
}
