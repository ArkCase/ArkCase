package com.armedia.acm.plugins.ecm.utils;

import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by manoj.dhungana on 6/24/2015.
 */
public class FolderAndFilesUtilsTest extends EasyMockSupport {

    private FolderAndFilesUtils unit;

    @Before
    public void setUp() throws Exception{
        unit = new FolderAndFilesUtils();
    }

    @Test
    public void testGetActiveVersionCmisId() throws Exception{
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
}
