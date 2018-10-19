package gov.foia.listener;

import static com.armedia.acm.plugins.casefile.model.CaseFileConstants.OBJECT_TYPE;

import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFilePostUploadEvent;

import org.springframework.context.ApplicationListener;

import java.util.Optional;

import gov.foia.model.FOIARequest;
import gov.foia.service.ResponseFolderFileUpdateService;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Aug 21, 2018
 *
 */
public class ResponseFolderFileAddedListener implements ApplicationListener<EcmFilePostUploadEvent>
{

    private String responseFolderName;

    private String releaseQueueName;

    private CaseFileDao caseFileDao;

    private ResponseFolderFileUpdateService fileUpdateService;

    /*
     * (non-Javadoc)
     * @see
     * org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
     */
    @Override
    public void onApplicationEvent(EcmFilePostUploadEvent event)
    {
        EcmFile file = event.getFile();

        if (!OBJECT_TYPE.equals(file.getContainer().getContainerObjectType()))
        {
            return;
        }

        FOIARequest request = (FOIARequest) caseFileDao.find(file.getContainer().getContainerObjectId());
        if (!releaseQueueName.equals(request.getQueue().getName()))
        {
            return;
        }

        Optional<AcmFolder> folder = getResponseFolder(file);

        if (folder.isPresent())
        {
            fileUpdateService.updateFile(file, folder.get().getId(), event.getUsername());
        }

    }

    private Optional<AcmFolder> getResponseFolder(EcmFile file)
    {
        AcmFolder folder = file.getFolder();

        while (folder != null)
        {
            if (responseFolderName.equalsIgnoreCase(folder.getName()))
            {
                break;
            }
            folder = folder.getParentFolder();
        }

        return Optional.ofNullable(folder);
    }

    /**
     * @param responseFolderName
     *            the responseFolderName to set
     */
    public void setResponseFolderName(String responseFolderName)
    {
        this.responseFolderName = responseFolderName;
    }

    /**
     * @param releaseQueueName
     *            the releaseQueueName to set
     */
    public void setReleaseQueueName(String releaseQueueName)
    {
        this.releaseQueueName = releaseQueueName;
    }

    /**
     * @param caseFileDao
     *            the caseFileDao to set
     */
    public void setCaseFileDao(CaseFileDao caseFileDao)
    {
        this.caseFileDao = caseFileDao;
    }

    /**
     * @param fileUpdateService
     *            the fileUpdateService to set
     */
    public void setFileUpdateService(ResponseFolderFileUpdateService fileUpdateService)
    {
        this.fileUpdateService = fileUpdateService;
    }

}
