package com.armedia.acm.services.sequence.model;

/*-
 * #%L
 * ACM Service: Sequence Manager
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

/**
 * @author sasko.tanaskoski
 *
 */
public class AcmSequencePart
{

    private String sequencePartName;
    private String sequencePartType;

    private String sequenceCondition;

    private String sequenceArbitraryText;

    private String sequenceObjectPropertyName;

    private String sequenceDateFormat;

    private Integer sequenceStartNumber;
    private Integer sequenceIncrementSize;
    private Integer sequenceNumberLength;
    private Boolean sequenceFillBlanks;

    /**
     * @return the sequencePartName
     */
    public String getSequencePartName()
    {
        return sequencePartName;
    }

    /**
     * @param sequencePartName
     *            the sequencePartName to set
     */
    public void setSequencePartName(String sequencePartName)
    {
        this.sequencePartName = sequencePartName;
    }

    /**
     * @return the sequencePartType
     */
    public String getSequencePartType()
    {
        return sequencePartType;
    }

    /**
     * @param sequencePartType
     *            the sequencePartType to set
     */
    public void setSequencePartType(String sequencePartType)
    {
        this.sequencePartType = sequencePartType;
    }

    /**
     * @return the sequenceCondition
     */
    public String getSequenceCondition()
    {
        return sequenceCondition;
    }

    /**
     * @param sequenceCondition
     *            the sequenceCondition to set
     */
    public void setSequenceCondition(String sequenceCondition)
    {
        this.sequenceCondition = sequenceCondition;
    }

    /**
     * @return the sequenceArbitraryText
     */
    public String getSequenceArbitraryText()
    {
        return sequenceArbitraryText;
    }

    /**
     * @param sequenceArbitraryText
     *            the sequenceArbitraryText to set
     */
    public void setSequenceArbitraryText(String sequenceArbitraryText)
    {
        this.sequenceArbitraryText = sequenceArbitraryText;
    }

    /**
     * @return the sequenceObjectPropertyName
     */
    public String getSequenceObjectPropertyName()
    {
        return sequenceObjectPropertyName;
    }

    /**
     * @param sequenceObjectPropertyName
     *            the sequenceObjectPropertyName to set
     */
    public void setSequenceObjectPropertyName(String sequenceObjectPropertyName)
    {
        this.sequenceObjectPropertyName = sequenceObjectPropertyName;
    }

    /**
     * @return the sequenceDateFormat
     */
    public String getSequenceDateFormat()
    {
        return sequenceDateFormat;
    }

    /**
     * @param sequenceDateFormat
     *            the sequenceDateFormat to set
     */
    public void setSequenceDateFormat(String sequenceDateFormat)
    {
        this.sequenceDateFormat = sequenceDateFormat;
    }

    /**
     * @return the sequenceStartNumber
     */
    public Integer getSequenceStartNumber()
    {
        return sequenceStartNumber;
    }

    /**
     * @param sequenceStartNumber
     *            the sequenceStartNumber to set
     */
    public void setSequenceStartNumber(Integer sequenceStartNumber)
    {
        this.sequenceStartNumber = sequenceStartNumber;
    }

    /**
     * @return the sequenceIncrementSize
     */
    public Integer getSequenceIncrementSize()
    {
        return sequenceIncrementSize;
    }

    /**
     * @param sequenceIncrementSize
     *            the sequenceIncrementSize to set
     */
    public void setSequenceIncrementSize(Integer sequenceIncrementSize)
    {
        this.sequenceIncrementSize = sequenceIncrementSize;
    }

    /**
     * @return the sequenceNumberLength
     */
    public Integer getSequenceNumberLength()
    {
        return sequenceNumberLength;
    }

    /**
     * @param sequenceNumberLength
     *            the sequenceNumberLength to set
     */
    public void setSequenceNumberLength(Integer sequenceNumberLength)
    {
        this.sequenceNumberLength = sequenceNumberLength;
    }

    /**
     * @return the sequenceFillBlanks
     */
    public Boolean getSequenceFillBlanks()
    {
        return sequenceFillBlanks;
    }

    /**
     * @param sequenceFillBlanks
     *            the sequenceFillBlanks to set
     */
    public void setSequenceFillBlanks(Boolean sequenceFillBlanks)
    {
        this.sequenceFillBlanks = sequenceFillBlanks;
    }

}
