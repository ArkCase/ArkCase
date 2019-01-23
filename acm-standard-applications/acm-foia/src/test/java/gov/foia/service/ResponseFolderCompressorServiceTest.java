/**
 *
 */
package gov.foia.service;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
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

import com.armedia.acm.compressfolder.FolderCompressor;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;

import org.easymock.EasyMockRunner;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationEventPublisher;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Sep 20, 2016
 */
@RunWith(EasyMockRunner.class)
public class ResponseFolderCompressorServiceTest extends EasyMockSupport
{

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @Mock
    private CaseFileDao mockedCaseFileDao;
    @Mock
    private ResponseFolderService mockedResponseFolderService;
    @Mock
    private FolderCompressor mockedCompressor;
    @Mock
    private CaseFile mockedCaseFile;
    @Mock
    private AcmContainer mockedContainer;
    @Mock
    private AcmFolder mockedRootFolder;
    @Mock
    private AcmFolder mockedRequestFolder;
    @Mock
    private AcmFolder mockedWorkingFolder;
    @Mock
    private AcmFolder mockedResponseFolder;
    @Mock
    private ApplicationEventPublisher mockedApplicationEventPublisher;
    private ResponseFolderCompressorService compresssorService;

    @Before
    public void setUp() throws Exception
    {
        compresssorService = new ResponseFolderCompressorService();
        compresssorService.setCaseFileDao(mockedCaseFileDao);
        compresssorService.setCompressor(mockedCompressor);
        compresssorService.setResponseFolderService(mockedResponseFolderService);
        compresssorService.setApplicationEventPublisher(mockedApplicationEventPublisher);
    }

    /**
     * Test method for {@link ResponseFolderCompressorService#compressResponseFolder(Long)}.
     *
     * @throws Exception
     */
    @Test
    @Ignore
    public void testCompressResponseFolder() throws Exception
    {
        long requestId = 101l;
        long responseFolderId = 103l;
        String pathToCompressedFile = "path_to_compressed_file";

        expect(mockedCaseFileDao.find(requestId)).andReturn(mockedCaseFile);
        expect(mockedResponseFolderService.getResponseFolder(mockedCaseFile)).andReturn(mockedResponseFolder);
        expect(mockedResponseFolder.getId()).andReturn(responseFolderId);
        expect(mockedCompressor.compressFolder(responseFolderId)).andReturn(pathToCompressedFile);
        mockedApplicationEventPublisher.publishEvent(anyObject());
        expectLastCall().andVoid();

        replayAll();

        String compressedFilePath = compresssorService.compressResponseFolder(requestId);

        assertThat(compressedFilePath, equalTo(pathToCompressedFile));

    }

}
