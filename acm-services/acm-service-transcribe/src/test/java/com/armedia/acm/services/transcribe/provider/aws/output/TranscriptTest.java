package com.armedia.acm.services.transcribe.provider.aws.output;

import com.armedia.acm.services.transcribe.model.TranscribeItem;
import com.armedia.acm.services.transcribe.provider.aws.model.transcript.AWSTranscript;
import com.armedia.acm.services.transcribe.provider.aws.model.transcript.AWSTranscriptAlternative;
import com.armedia.acm.services.transcribe.provider.aws.model.transcript.AWSTranscriptItem;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/16/2018
 */
public class TranscriptTest
{
    @Test
    public void createItemsFromAWSResponse() throws Exception
    {
        String jsonString = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("aws/output/asrOutput.json"));

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        AWSTranscript awsTranscript = objectMapper.readValue(jsonString, AWSTranscript.class);

        assertNotNull(awsTranscript);
        assertNotNull(awsTranscript.getResult());
        assertNotNull(awsTranscript.getResult().getItems());

        int numberOfWordsPerItem = 20;
        List<TranscribeItem> items = new ArrayList<>();

        int counter = 0;
        BigDecimal startTime = null;
        BigDecimal endTime = null;
        BigDecimal confidence = new BigDecimal("0");
        int confidenceCounter = 0;
        String text = "";
        List<AWSTranscriptItem> awsTranscriptItems = awsTranscript.getResult().getItems();
        int size = awsTranscriptItems.size();
        for (int i = 0; i < size; i++)
        {
            AWSTranscriptItem awsTranscriptItem = awsTranscriptItems.get(i);
            boolean punctuation = "punctuation".equalsIgnoreCase(awsTranscriptItem.getType());

            if (!punctuation)
            {
                if (startTime == null && awsTranscriptItem.getStartTime() != null)
                {
                    startTime = new BigDecimal(awsTranscriptItem.getStartTime());
                }

                if (awsTranscriptItem.getEndTime() != null)
                {
                    endTime = new BigDecimal(awsTranscriptItem.getEndTime());
                }

                counter++;
            }

            if (awsTranscriptItem.getAlternatives() != null && awsTranscriptItem.getAlternatives().size() > 0)
            {
                AWSTranscriptAlternative awsTranscriptAlternative = getBestAWSTranscriptAlternative(awsTranscriptItem.getAlternatives());

                if (awsTranscriptAlternative != null && awsTranscriptAlternative.getConfidence() != null)
                {
                    BigDecimal confidenceAsBigDecimal = new BigDecimal(awsTranscriptAlternative.getConfidence());
                    confidence = confidence.add(confidenceAsBigDecimal);
                    confidenceCounter++;
                }

                String textDelimiter = !punctuation ? " " : "";
                if (awsTranscriptAlternative != null && StringUtils.isNotEmpty(awsTranscriptAlternative.getContent()))
                {
                    text = (text + textDelimiter + awsTranscriptAlternative.getContent()).trim();
                }
            }

            if (counter >= numberOfWordsPerItem || i == size - 1)
            {
                if (!isNextPunctuation(i, size, awsTranscriptItems))
                {
                    TranscribeItem item = new TranscribeItem();
                    item.setStartTime(startTime);
                    item.setEndTime(endTime);
                    item.setText(text);

                    int conf = confidence.intValue() == 0 || confidenceCounter == 0 ? 0 : confidence.multiply(new BigDecimal(100)).intValue() / confidenceCounter;
                    item.setConfidence(conf);

                    items.add(item);

                    counter = 0;
                    startTime = null;
                    endTime = null;
                    confidence = new BigDecimal("0");
                    confidenceCounter = 0;
                    text = "";
                }
            }
        }

        assertNotNull(items);
        assertEquals(17, items.size());
        assertEquals(new BigDecimal("1.390"), items.get(0).getStartTime());
        assertEquals(new BigDecimal("7.720"), items.get(0).getEndTime());
        assertEquals(98, items.get(0).getConfidence());
        assertEquals("I've often said that i wish people could realize all their dreams and wealth fame and so that they could", items.get(0).getText());
        assertEquals(new BigDecimal("153.760"), items.get(16).getStartTime());
        assertEquals(new BigDecimal("177.730"), items.get(16).getEndTime());
        assertEquals(81, items.get(16).getConfidence());
        assertEquals("shows that he's given it a chance to give you the future? I don't need security. Wait", items.get(16).getText());

        for (int i = 0; i < 16; i++)
        {
            assertEquals(numberOfWordsPerItem, items.get(i).getText().split(" ").length);
        }

        assertEquals(17, items.get(16).getText().split(" ").length);
    }

    private boolean isNextPunctuation(int i, int size, List<AWSTranscriptItem> awsTranscriptItems)
    {
        if (i <= size - 2 && awsTranscriptItems.get(i+1) != null && "punctuation".equalsIgnoreCase(awsTranscriptItems.get(i+1).getType()))
        {
            return true;
        }
        return false;
    }

    private AWSTranscriptAlternative getBestAWSTranscriptAlternative(List<AWSTranscriptAlternative> alternatives)
    {
        if (alternatives == null || alternatives.size() == 0)
        {
            return null;
        }

        if (alternatives.size() == 1)
        {
            return alternatives.get(0);
        }

        alternatives.sort((AWSTranscriptAlternative a1, AWSTranscriptAlternative a2) -> (toIntWithWholePrecision(a2.getConfidence()) - toIntWithWholePrecision(a1.getConfidence())));

        return alternatives.get(0);
    }

    private int toIntWithWholePrecision(String confidence)
    {
        // Confidence is in the format with 4 digits after comma, for example 0.9878
        // Convert it to whole number with all digits to be able to use in lambda sort method
        if (StringUtils.isNotEmpty(confidence))
        {
            BigDecimal conf = new BigDecimal(confidence);
            return conf.multiply(new BigDecimal(10000)).intValue();
        }

        return 0;
    }
}
