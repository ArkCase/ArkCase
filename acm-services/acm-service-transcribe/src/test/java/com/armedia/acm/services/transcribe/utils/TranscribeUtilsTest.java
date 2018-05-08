package com.armedia.acm.services.transcribe.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.armedia.acm.services.transcribe.model.TranscribeItem;

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
