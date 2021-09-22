package gov.foia.service.dataupdate;

import com.armedia.acm.services.dataupdate.service.AcmDataUpdateExecutor;
import gov.foia.dao.FOIAFileDao;
import gov.foia.model.FOIAFile;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

public class FoiaFileSetMadePublicDateUpdateExecutor implements AcmDataUpdateExecutor
{

    FOIAFileDao foiaFileDao;

    @Override
    public String getUpdateId()
    {
        return "foia-file-set-madePublicDate";
    }

    @Override
    public void execute()
    {
        List<FOIAFile> publicFiles = getFoiaFileDao().getPublicFiles();

        for (FOIAFile file : publicFiles)
        {
            LocalDateTime modified = file.getModified().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            file.setMadePublicDate(modified);
            getFoiaFileDao().save(file);
        }
    }

    public FOIAFileDao getFoiaFileDao()
    {
        return foiaFileDao;
    }

    public void setFoiaFileDao(FOIAFileDao foiaFileDao)
    {
        this.foiaFileDao = foiaFileDao;
    }
}
