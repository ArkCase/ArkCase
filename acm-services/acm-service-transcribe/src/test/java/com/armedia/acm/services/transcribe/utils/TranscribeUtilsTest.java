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
}
