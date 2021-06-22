package com.armedia.acm.correspondence.model;

/*-
 * #%L
 * ACM Service: Correspondence Library
 * %%
 * Copyright (C) 2014 - 2021 ArkCase LLC
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

import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.VerticalAlign;

public class FormattedRun
{
    String fontFamily;
    Integer fontSize;
    boolean bold;
    boolean capitalized;
    String color;
    boolean doubleStrikeThrough;
    boolean embossed;
    boolean imprinted;
    boolean italic;
    Integer kerning;
    boolean shadowed;
    boolean smallCaps;
    boolean strikeThrough;
    VerticalAlign subscript;
    UnderlinePatterns underline;
    String text;

    public FormattedRun(String text)
    {
        this.text = text;
    }

    public FormattedRun()
    {
    }

    public String getFontFamily()
    {
        return fontFamily;
    }

    public void setFontFamily(String fontFamily)
    {
        this.fontFamily = fontFamily;
    }

    public Integer getFontSize()
    {
        return fontSize;
    }

    public void setFontSize(Integer fontSize)
    {
        this.fontSize = fontSize;
    }

    public boolean isBold()
    {
        return bold;
    }

    public void setBold(boolean bold)
    {
        this.bold = bold;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public boolean isCapitalized()
    {
        return capitalized;
    }

    public void setCapitalized(boolean capitalized)
    {
        this.capitalized = capitalized;
    }

    public String getColor()
    {
        return color;
    }

    public void setColor(String color)
    {
        this.color = color;
    }

    public boolean isDoubleStrikeThrough()
    {
        return doubleStrikeThrough;
    }

    public void setDoubleStrikeThrough(boolean doubleStrikeThrough)
    {
        this.doubleStrikeThrough = doubleStrikeThrough;
    }

    public boolean isEmbossed()
    {
        return embossed;
    }

    public void setEmbossed(boolean embossed)
    {
        this.embossed = embossed;
    }

    public boolean isImprinted()
    {
        return imprinted;
    }

    public void setImprinted(boolean imprinted)
    {
        this.imprinted = imprinted;
    }

    public boolean isItalic()
    {
        return italic;
    }

    public void setItalic(boolean italic)
    {
        this.italic = italic;
    }

    public Integer getKerning()
    {
        return kerning;
    }

    public void setKerning(Integer kerning)
    {
        this.kerning = kerning;
    }

    public boolean isShadowed()
    {
        return shadowed;
    }

    public void setShadowed(boolean shadowed)
    {
        this.shadowed = shadowed;
    }

    public boolean isSmallCaps()
    {
        return smallCaps;
    }

    public void setSmallCaps(boolean smallCaps)
    {
        this.smallCaps = smallCaps;
    }

    public boolean isStrikeThrough()
    {
        return strikeThrough;
    }

    public void setStrikeThrough(boolean strikeThrough)
    {
        this.strikeThrough = strikeThrough;
    }

    public VerticalAlign getSubscript()
    {
        return subscript;
    }

    public void setSubscript(VerticalAlign subscript)
    {
        this.subscript = subscript;
    }

    public UnderlinePatterns getUnderline()
    {
        return underline;
    }

    public void setUnderline(UnderlinePatterns underline)
    {
        this.underline = underline;
    }
}
