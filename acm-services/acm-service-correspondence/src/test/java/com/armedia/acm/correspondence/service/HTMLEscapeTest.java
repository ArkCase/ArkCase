package com.armedia.acm.correspondence.service;

import org.jsoup.Jsoup;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by riste.tutureski on 2/28/2017.
 */
public class HTMLEscapeTest
{
    @Test
    public void escapeHTML() throws Exception
    {
        String htmlText = "<p style=\"color: red;\">This is <strong>HTML</strong> example. <i>After escaping HTML characters, it should be text with no any HTML tags.</i><p>";
        String noHtmlText = Jsoup.parse(htmlText).text();

        assertEquals("This is HTML example. After escaping HTML characters, it should be text with no any HTML tags.", noHtmlText);
    }
}
