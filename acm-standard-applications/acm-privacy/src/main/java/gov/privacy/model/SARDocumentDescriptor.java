package gov.privacy.model;

/*-
 * #%L
 * ACM Privacy: Subject Access Request
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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
 * @author Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on Jun, 2020
 *
 */
public class SARDocumentDescriptor
{
    private String type;
    private String reqAck;
    private String template;
    private String doctype;
    private String filenameFormat;
    private String targetFileExtension;

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getReqAck()
    {
        return reqAck;
    }

    public void setReqAck(String reqAck)
    {
        this.reqAck = reqAck;
    }

    public String getTemplate()
    {
        return template;
    }

    public void setTemplate(String template)
    {
        this.template = template;
    }

    public String getDoctype()
    {
        return doctype;
    }

    public void setDoctype(String doctype)
    {
        this.doctype = doctype;
    }

    public String getFilenameFormat()
    {
        return filenameFormat;
    }

    public void setFilenameFormat(String filenameFormat)
    {
        this.filenameFormat = filenameFormat;
    }

    public String getTargetFileExtension()
    {
        return targetFileExtension;
    }

    public void setTargetFileExtension(String targetFileExtension)
    {
        this.targetFileExtension = targetFileExtension;
    }
}
