package com.armedia.acm.services.transcribe.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

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

}
