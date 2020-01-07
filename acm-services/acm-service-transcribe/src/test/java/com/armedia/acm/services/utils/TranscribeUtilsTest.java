package com.armedia.acm.services.utils;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.armedia.acm.services.transcribe.model.TranscribeItem;
import com.armedia.acm.services.transcribe.utils.TranscribeUtils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/06/2018
 */
public class TranscribeUtilsTest
{
    @Test
    public void getFirstWords_MoreThanRequired()
    {
        String sentence = "This is just some testing sentence with some words in it.";
        String expectedResult = "This is just some testing ...";

        String actualResult = TranscribeUtils.getFirstWords(sentence, 5);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void getFirstWords2_EqualThanRequired()
    {
        String sentence = "This is just some testing.";
        String expectedResult = "This is just some testing.";

        String actualResult = TranscribeUtils.getFirstWords(sentence, 5);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void getFirstWords3_LessThanRequired()
    {
        String sentence = "This is shorter.";
        String expectedResult = "This is shorter.";

        String actualResult = TranscribeUtils.getFirstWords(sentence, 5);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void getFirstWords4_EmptyString()
    {
        String sentence = "";
        String expectedResult = "";

        String actualResult = TranscribeUtils.getFirstWords(sentence, 5);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void getFirstWords5_Null()
    {
        String sentence = null;
        String expectedResult = "";

        String actualResult = TranscribeUtils.getFirstWords(sentence, 5);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void extractMediaType_video()
    {
        String mimeType = "video/mp4";
        String expectedResult = "mp4";

        String actualResult = TranscribeUtils.extractMediaType(mimeType);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void extractMediaType_audio()
    {
        String mimeType = "audio/mp3";
        String expectedResult = "mp3";

        String actualResult = TranscribeUtils.extractMediaType(mimeType);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void extractMediaType_More_Slashes()
    {
        String mimeType = "video/mp4/mistake";
        String expectedResult = "";

        String actualResult = TranscribeUtils.extractMediaType(mimeType);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void extractMediaType_Ends_With_Slash()
    {
        String mimeType = "video/";
        String expectedResult = "";

        String actualResult = TranscribeUtils.extractMediaType(mimeType);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void extractMediaType_No_Slash()
    {
        String mimeType = "video";
        String expectedResult = "";

        String actualResult = TranscribeUtils.extractMediaType(mimeType);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void extractMediaType_Empty_String()
    {
        String mimeType = "";
        String expectedResult = "";

        String actualResult = TranscribeUtils.extractMediaType(mimeType);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void extractMediaType_Null()
    {
        String mimeType = null;
        String expectedResult = "";

        String actualResult = TranscribeUtils.extractMediaType(mimeType);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void getText() throws Exception
    {
        TranscribeItem item1 = new TranscribeItem();
        item1.setText("This");

        TranscribeItem item2 = new TranscribeItem();
        item2.setText("is");

        TranscribeItem item3 = new TranscribeItem();
        item3.setText("a");

        TranscribeItem item4 = new TranscribeItem();
        item4.setText("test");

        List<TranscribeItem> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        items.add(item3);
        items.add(item4);

        String text = TranscribeUtils.getText(items);

        assertNotNull(text);
        assertEquals("This is a test", text);
    }
}
