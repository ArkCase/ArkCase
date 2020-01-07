package com.armedia.acm.plugins.ecm.model;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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
 * ProgressbarDetails for File Upload functionality
 */
public class ProgressbarDetails
{

    private String uuid;
    private int stage;
    private int currentProgress;
    private boolean isProgressbar;
    private boolean success;
    private String objectType;
    private Long objectId;
    private String objectNumber;
    private String fileName;

    // status of the file. inprogress, finished, failed.
    private enum UploadFileStatus
    {
        READY,
        IN_PROGRESS,
        FAILED,
        FINISHED
    }

    /**
     * @return the uuid of the File
     */
    public String getUuid()
    {
        return uuid;
    }

    /**
     * @param uuid
     *            the uuid of the File to set
     */
    public void setUuid(String uuid)
    {
        this.uuid = uuid;
    }

    /**
     * @return the current stage of upload process(stage 2 writes file to file system, stage 3 writes file to Alfresco)
     */
    public int getStage()
    {
        return stage;
    }

    /**
     * @param stage
     *            the stage of the upload process to set
     */
    public void setStage(int stage)
    {
        this.stage = stage;
    }

    /**
     * @return the currentProgress percentage of the file upload
     */
    public int getCurrentProgress()
    {
        return currentProgress;
    }

    /**
     * @param currentProgress
     *            the currentProgress percentage of the file upload to set
     */
    public void setCurrentProgress(int currentProgress)
    {
        this.currentProgress = currentProgress;
    }

    /**
     * @return progressbar
     */
    public boolean isProgressbar()
    {
        return isProgressbar;
    }

    /**
     * @param progressbar
     *            the progressbar property to set
     */
    public void setProgressbar(boolean progressbar)
    {
        isProgressbar = progressbar;
    }

    /**
     * @return the success of the file upload
     */
    public boolean isSuccess()
    {
        return success;
    }

    /**
     * @param success
     *            the success property to set in file upload
     */
    public void setSuccess(boolean success)
    {
        this.success = success;
    }

    /**
     * @return the objectType of the File
     */
    public String getObjectType()
    {
        return objectType;
    }

    /**
     * @param objectType
     *            the objectType of the File to set
     */
    public void setObjectType(String objectType)
    {
        this.objectType = objectType;
    }

    /**
     * @return the objectId of the File
     */
    public Long getObjectId()
    {
        return objectId;
    }

    /**
     * @param objectId
     *            the objectId of the File to set
     */
    public void setObjectId(Long objectId)
    {
        this.objectId = objectId;
    }

    /**
     * @return the objectNumber is the containerObjectTitle
     */
    public String getObjectNumber()
    {
        return objectNumber;
    }

    /**
     * @param objectNumber
     *            the objectNumber is the containerObjectTitle to set
     */
    public void setObjectNumber(String objectNumber)
    {
        this.objectNumber = objectNumber;
    }

    /**
     * @return the fileName of the File
     */
    public String getFileName()
    {
        return fileName;
    }

    /**
     * @param fileName
     *            the fileName of the File to set
     */
    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }
}
