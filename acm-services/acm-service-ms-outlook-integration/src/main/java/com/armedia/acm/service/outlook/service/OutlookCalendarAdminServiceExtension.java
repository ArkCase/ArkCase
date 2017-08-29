package com.armedia.acm.service.outlook.service;

import com.armedia.acm.calendar.config.service.CalendarAdminService;
import com.armedia.acm.core.exceptions.AcmOutlookItemNotFoundException;
import com.armedia.acm.service.outlook.dao.AcmOutlookFolderCreatorDaoException;
import com.armedia.acm.service.outlook.model.AcmOutlookFolderCreator;
import com.armedia.acm.service.outlook.model.AcmOutlookUser;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;

import java.io.IOException;
import java.util.Optional;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Aug 1, 2017
 *
 */
public interface OutlookCalendarAdminServiceExtension extends CalendarAdminService
{

    Optional<AcmOutlookUser> getEventListenerOutlookUser(String objectType) throws AcmOutlookItemNotFoundException;

    Optional<AcmOutlookUser> getHandlerOutlookUser(String userName, String objectType) throws PipelineProcessException;

    /**
     * @param updatedCreator
     */
    void recreateFolders(AcmOutlookFolderCreator updatedCreator);

    /**
     *
     * @param updatedCreator
     * @param totalToProcess
     * @param updateStatus
     * @param failStatus TODO
     * @param finished
     * @param noRecreation
     * @throws AcmOutlookFolderCreatorDaoException
     */
    void recreateFolders(AcmOutlookFolderCreator updatedCreator, RecreateFoldersTotalToProcessCallback totalToProcess,
            RecreateFoldersUpdateStatusCallback updateStatus, RecreateFoldersFailStatusCallback failStatus,
            RecreateFoldersFinishedCallback finished, RecreateFoldersNoRecreationCallback noRecreation) throws AcmOutlookFolderCreatorDaoException;

    @FunctionalInterface
    public static interface RecreateFoldersTotalToProcessCallback
    {
        void total(int totalToProcess) throws IOException;
    }

    @FunctionalInterface
    public static interface RecreateFoldersUpdateStatusCallback
    {
        void updated(int updated, String folderName) throws IOException;
    }

    @FunctionalInterface
    public static interface RecreateFoldersFailStatusCallback
    {
        void failed(int failed, String objectType, String objectId) throws IOException;
    }

    @FunctionalInterface
    public static interface RecreateFoldersFinishedCallback
    {
        void finished(int totalToProcess, int success, int failed) throws IOException;
    }

    @FunctionalInterface
    public static interface RecreateFoldersNoRecreationCallback
    {
        void noRecreationRequired() throws IOException;
    }

    public static class RecreateFoldersCallbackPayload
    {

        private String message;

        private String data;

        public RecreateFoldersCallbackPayload()
        {
        }

        public RecreateFoldersCallbackPayload(String message, String data)
        {
            this.message = message;
            this.data = data;
        }

        /**
         * @return the message
         */
        public String getMessage()
        {
            return message;
        }

        /**
         * @param message
         *            the message to set
         */
        public void setMessage(String message)
        {
            this.message = message;
        }

        /**
         * @return the data
         */
        public String getData()
        {
            return data;
        }

        /**
         * @param data
         *            the data to set
         */
        public void setData(String data)
        {
            this.data = data;
        }

    }

}
