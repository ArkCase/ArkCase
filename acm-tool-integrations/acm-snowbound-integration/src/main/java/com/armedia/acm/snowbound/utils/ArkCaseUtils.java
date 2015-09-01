package com.armedia.acm.snowbound.utils;

import com.armedia.acm.snowbound.model.ArkCaseConstants;

/**
 * Created by joseph.mcgrady on 8/30/2015.
 */
public class ArkCaseUtils {

    /**
     * Returns the full url to the ArkCase replace file web call including the
     * url arguments for the authentication ticket and the acm id of the document to replace
     * @param baseURL - host, port, and application context parts of the url to ArkCase
     * @param sendFileService - url path to the ArkCase file replacement api
     * @param docIdString - id of the document in ArkCase whose contents will be replaced
     * @param acmTicket - acm authentication token (without this the call will result in an HTTP 401 unauthorized error)
     * @return url which can be used to call the ArkCase file replacement api
     */
    public static String buildReplaceFileUrl(String baseURL, String sendFileService, String docIdString, String acmTicket)
    {
        return baseURL + sendFileService + docIdString + "?" + ArkCaseConstants.ACM_TICKET_PARAM + "=" + acmTicket;
    }

    /**
     * Returns the full url to the ArkCase upload new file web call including the
     * url arguments for the authentication ticket and the id and type of the parent object in acm
     * @param baseURL - host, port, and application context parts of the url to ArkCase
     * @param uploadNewFileService - url path to the ArkCase web call for uploading new files
     * @param parentType - acm type of the parent object for the new upload
     * @param parentId - acm id of the parent object to which the new uploaded file will be attached
     * @param acmTicket - acm authentication token (without this the call will result in an HTTP 401 unauthorized error)
     * @return url which can be used to call the ArkCase new file upload api
     */
    public static String buildUploadNewFileUrl(String baseURL, String uploadNewFileService, String parentType, String parentId, String acmTicket)
    {
        return baseURL + uploadNewFileService + "?" +
                      ArkCaseConstants.ACM_PARENT_TYPE_PARAM + "=" + parentType +
                "&" + ArkCaseConstants.ACM_PARENT_ID_PARAM + "=" + parentId +
                "&" + ArkCaseConstants.ACM_TICKET_PARAM + "=" + acmTicket;
    }
}