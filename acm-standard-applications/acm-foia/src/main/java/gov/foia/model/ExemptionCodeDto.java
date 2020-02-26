package gov.foia.model;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
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

import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.SqlResultSetMapping;
import java.util.Date;

/**
 * Created by dragan.simonovski on 09/27/2016.
 */

@SqlResultSetMapping(name = "ExemptionCodeResults", classes = {
        @ConstructorResult(targetClass = ExemptionCodeDto.class, columns = {
                @ColumnResult(name = "object_id", type = Long.class),
                @ColumnResult(name = "file_id", type = Long.class),
                @ColumnResult(name = "file_version"),
                @ColumnResult(name = "file_name"),
                @ColumnResult(name = "exemption_code"),
                @ColumnResult(name = "creator"),
                @ColumnResult(name = "exemption_statute", type = String.class),
                @ColumnResult(name = "status", type = String.class),
                @ColumnResult(name = "created", type = Date.class)
        })
})
@Entity
// workaround for mapping to be scanned by JPA
class SQLMappingCfgEntity
{
    @Id
    int id;
}

public class ExemptionCodeDto
{
    private Long requestId;
    private Long fileId;
    private String fileVersion;
    private String fileName;
    private String exemptionCode;
    private String creator;
    private String exemptionStatute;
    private String status;
    private Date created;

    public ExemptionCodeDto()
    {
    }

    public ExemptionCodeDto(Long requestId, Long fileId, String fileVersion, String fileName, String exemptionCode, String creator,
            String exemptionStatute, String status, Date created)
    {
        this.requestId = requestId;
        this.fileId = fileId;
        this.fileVersion = fileVersion;
        this.fileName = fileName;
        this.exemptionCode = exemptionCode;
        this.creator = creator;
        this.exemptionStatute = exemptionStatute;
        this.status = status;
        this.created = created;
    }

    public Long getRequestId()
    {
        return requestId;
    }

    public void setRequestId(Long requestId)
    {
        this.requestId = requestId;
    }

    public Long getFileId()
    {
        return fileId;
    }

    public void setFileId(Long fileId)
    {
        this.fileId = fileId;
    }

    public String getFileName()
    {
        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public String getExemptionCode()
    {
        return exemptionCode;
    }

    public void setExemptionCode(String exemptionCode)
    {
        this.exemptionCode = exemptionCode;
    }

    public String getCreator()
    {
        return creator;
    }

    public void setCreator(String creator)
    {
        this.creator = creator;
    }

    public String getExemptionStatute()
    {
        return exemptionStatute;
    }

    public void setExemptionStatute(String exemptionStatute)
    {
        this.exemptionStatute = exemptionStatute;
    }

    public String getFileVersion()
    {
        return fileVersion;
    }

    public void setFileVersion(String fileVersion)
    {
        this.fileVersion = fileVersion;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public Date getCreated()
    {
        return created;
    }

    public void setCreated(Date created)
    {
        this.created = created;
    }

}
