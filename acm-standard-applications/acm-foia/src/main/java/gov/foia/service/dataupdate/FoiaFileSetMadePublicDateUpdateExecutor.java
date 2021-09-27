package gov.foia.service.dataupdate;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
 * %%
 * Copyright (C) 2014 - 2021 ArkCase LLC
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
        return "foia-file-set-madePublicDate-v2";
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
