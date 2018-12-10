package gov.foia.service;

import com.armedia.acm.convertfolder.ConversionException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.exception.AcmFolderException;
import gov.foia.broker.FOIARequestFileBrokerClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestResponseFolderService
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private ResponseFolderConverterService responseFolderConverterService;
    private ResponseFolderCompressorService responseFolderCompressorService;
    private FOIARequestFileBrokerClient foiaRequestFileBrokerClient;
    private ResponseFolderNotifyService responseFolderNotifyService;

    public void compressAndSendResponseFolderToPortal(Long requestId, String userName) throws ConversionException, AcmUserActionFailedException, AcmFolderException, AcmObjectNotFoundException
    {
        log.debug("Converting the Response folder for the request [{}]", requestId);
        getResponseFolderConverterService().convertResponseFolder(requestId, userName);

        log.debug("Compressing the Response folder for the request [{}]", requestId);
        getResponseFolderCompressorService().compressResponseFolder(requestId);

        log.debug("Sending the compressed Response folder file to outbound message queue the request [{}]", requestId);
        getFoiaRequestFileBrokerClient().sendReleaseFile(requestId);

        log.debug("Sending Email notification Response folder zip completed for the request [{}]", requestId);
        getResponseFolderNotifyService().sendEmailNotification(requestId);

    }

    public ResponseFolderConverterService getResponseFolderConverterService()
    {
        return responseFolderConverterService;
    }

    public void setResponseFolderConverterService(ResponseFolderConverterService responseFolderConverterService)
    {
        this.responseFolderConverterService = responseFolderConverterService;
    }

    public ResponseFolderCompressorService getResponseFolderCompressorService()
    {
        return responseFolderCompressorService;
    }

    public void setResponseFolderCompressorService(ResponseFolderCompressorService responseFolderCompressorService)
    {
        this.responseFolderCompressorService = responseFolderCompressorService;
    }

    public FOIARequestFileBrokerClient getFoiaRequestFileBrokerClient()
    {
        return foiaRequestFileBrokerClient;
    }

    public void setFoiaRequestFileBrokerClient(FOIARequestFileBrokerClient foiaRequestFileBrokerClient)
    {
        this.foiaRequestFileBrokerClient = foiaRequestFileBrokerClient;
    }

    public ResponseFolderNotifyService getResponseFolderNotifyService()
    {
        return responseFolderNotifyService;
    }

    public void setResponseFolderNotifyService(ResponseFolderNotifyService responseFolderNotifyService)
    {
        this.responseFolderNotifyService = responseFolderNotifyService;
    }
}
