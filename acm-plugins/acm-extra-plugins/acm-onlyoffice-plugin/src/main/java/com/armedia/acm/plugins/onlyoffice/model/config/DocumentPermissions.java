package com.armedia.acm.plugins.onlyoffice.model.config;

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

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class DocumentPermissions
{
    /**
     * Defines if the document can be commented or not. In case the commenting permission is set to "true" the document
     * side bar will contain the Comment menu option; the document commenting will only be available for the document
     * editor if the mode parameter is set to edit. The default value coincides with the value of the edit parameter.
     */
    private boolean comment;
    /**
     * Defines if the document can be downloaded or only viewed or edited online. In case the downloading permission is
     * set to "false" the Download as... menu option will be absent from the File menu. The default value is true.
     */
    private boolean download;
    /**
     * Defines if the document can be edited or only viewed. In case the editing permission is set to "true" the File
     * menu will contain the Edit Document menu option; please note that if the editing permission is set to "false" the
     * document will be opened in viewer and you will not be able to switch it to the editor even if the mode parameter
     * is set to edit. The default value is true.
     */
    private boolean edit;
    /**
     * Defines if the document can be printed or not. In case the printing permission is set to "false" the Print menu
     * option will be absent from the File menu. The default value is true.
     */
    private boolean print;
    /**
     * Defines if the document can be reviewed or not. In case the reviewing permission is set to "true" the document
     * status bar will contain the Review menu option; the document review will only be available for the document
     * editor if the mode parameter is set to edit. The default value coincides with the value of the edit parameter.
     */
    private boolean review;

    public DocumentPermissions(boolean comment, boolean download, boolean edit, boolean print, boolean review)
    {
        this.comment = comment;
        this.download = download;
        this.edit = edit;
        this.print = print;
        this.review = review;
    }

    public boolean isComment()
    {
        return comment;
    }

    public void setComment(boolean comment)
    {
        this.comment = comment;
    }

    public boolean isDownload()
    {
        return download;
    }

    public void setDownload(boolean download)
    {
        this.download = download;
    }

    public boolean isEdit()
    {
        return edit;
    }

    public void setEdit(boolean edit)
    {
        this.edit = edit;
    }

    public boolean isPrint()
    {
        return print;
    }

    public void setPrint(boolean print)
    {
        this.print = print;
    }

    public boolean isReview()
    {
        return review;
    }

    public void setReview(boolean review)
    {
        this.review = review;
    }
}
