package com.armedia.acm.plugins.ecm.utils;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
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

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by manoj.dhungana on 6/24/2015.
 */
public class FolderAndFilesUtilsTest extends EasyMockSupport
{

    private FolderAndFilesUtils unit;

    @Before
    public void setUp() throws Exception
    {
        unit = new FolderAndFilesUtils();
    }

    @Test
    public void testGetActiveVersionCmisId() throws Exception
    {
        List<EcmFileVersion> versions = new ArrayList();
        String firstCmisId = "workspace:\\/\\/SpacesStore\\/c26d9627-ce0f-494e-a57b-fdfe2f3afb5d;1.0";
        String secondCmisId = "workspace:\\/\\/SpacesStore\\/c26d9627-ce0f-494e-a57b-fdfe2f3afb5d;2.0";

        EcmFile ecmFile = new EcmFile();

        EcmFileVersion versionFirst = new EcmFileVersion();
        versionFirst.setVersionTag("versionTag1");
        versionFirst.setCmisObjectId(firstCmisId);
        versions.add(versionFirst);

        EcmFileVersion versionSecond = new EcmFileVersion();
        versionSecond.setVersionTag("versionTag2");
        versionSecond.setCmisObjectId(secondCmisId);
        versions.add(versionSecond);

        ecmFile.setVersions(versions);

        ecmFile.setActiveVersionTag("versionTag1");

        ecmFile.setVersionSeriesId("testVersionId");

        String cmisId = unit.getActiveVersionCmisId(ecmFile);

        assertEquals(cmisId, firstCmisId);

        ecmFile.setActiveVersionTag("versionTag2");

        cmisId = unit.getActiveVersionCmisId(ecmFile);

        assertEquals(cmisId, secondCmisId);

    }

    @Test
    public void createUniqueIdentificator_cutLastDot()
    {
        String input = "someFileNameWithLastDot.";

        String result = unit.createUniqueIdentificator(input);

        assertFalse(result.endsWith("."));
    }
}
