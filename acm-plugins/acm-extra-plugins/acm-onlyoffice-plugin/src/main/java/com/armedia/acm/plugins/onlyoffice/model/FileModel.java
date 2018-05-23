/*
 * (c) Copyright Ascensio System Limited 2010-2017
 * The MIT License (MIT)
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.armedia.acm.plugins.onlyoffice.model;

/*-
 * #%L
 * ACM Extra Plugin: OnlyOffice Integration
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

import com.armedia.acm.plugins.onlyoffice.helpers.DocumentManager;
import com.armedia.acm.plugins.onlyoffice.helpers.FileUtility;
import com.armedia.acm.plugins.onlyoffice.helpers.ServiceConverter;

public class FileModel
{
    private String fileName;
    private Boolean typeDesktop;

    public String getFileName()
    {
        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public Boolean getTypeDesktop()
    {
        return typeDesktop;
    }

    public void setTypeDesktop(Boolean typeDesktop)
    {
        this.typeDesktop = typeDesktop;
    }

    public String getFileUri() throws Exception
    {
        return DocumentManager.getFileUri(fileName);
    }

    public String curUserHostAddress()
    {
        return DocumentManager.curUserHostAddress(null);
    }

    public String getDocumentType()
    {
        return FileUtility.getFileType(fileName).toString().toLowerCase();
    }

    public String getKey()
    {
        return ServiceConverter.generateRevisionId(DocumentManager.curUserHostAddress(null) + "/" + fileName);
    }

    public String getCallbackUrl()
    {
        return DocumentManager.getCallback(fileName);
    }

    public String getServerUrl()
    {
        return DocumentManager.getServerUrl();
    }
}
