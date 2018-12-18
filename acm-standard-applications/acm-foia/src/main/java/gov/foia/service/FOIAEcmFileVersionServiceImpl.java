package gov.foia.service;

import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import gov.foia.model.FOIAEcmFileVersion;

import java.util.List;
import java.util.Objects;

public class FOIAEcmFileVersionServiceImpl implements FOIAEcmFileVersionService
{
    private EcmFileDao ecmFileDao;

    @Override
    public void setReviewStatus(Long fileId, String fileVersion, String reviewStatus)
    {
        setReviewRedactionStatus(fileId, fileVersion, reviewStatus, "review");
    }

    @Override
    public void setRedactionStatus(Long fileId, String fileVersion, String redactionStatus)
    {
        setReviewRedactionStatus(fileId, fileVersion, redactionStatus, "redaction");
    }

    private void setReviewRedactionStatus(Long fileId, String fileVersion, String status, String statusType)
    {
        EcmFile file = getEcmFileDao().find(fileId);
        List<EcmFileVersion> fileVersions = file.getVersions();

        EcmFileVersion fileVersionToUpdate = fileVersions
                .stream()
                .filter(ecmFileVersion -> ecmFileVersion.getVersionTag().equals(fileVersion))
                .findFirst()
                .orElse(null);

        if(Objects.nonNull(fileVersionToUpdate) && fileVersionToUpdate instanceof FOIAEcmFileVersion)
        {
            if("review".equals(statusType))
            {
                ((FOIAEcmFileVersion)fileVersionToUpdate).setReviewStatus(status);
            }
            else if("redaction".equals(statusType))
            {
                ((FOIAEcmFileVersion)fileVersionToUpdate).setRedactionStatus(status);
            }
            getEcmFileDao().save(file);
        }
    }

    public EcmFileDao getEcmFileDao()
    {
        return ecmFileDao;
    }

    public void setEcmFileDao(EcmFileDao ecmFileDao)
    {
        this.ecmFileDao = ecmFileDao;
    }
}
