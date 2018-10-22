package gov.foia.model;

import javax.persistence.*;

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
                @ColumnResult(name = "exemption_statute", type = String.class)
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

    public ExemptionCodeDto()
    {
    }

    public ExemptionCodeDto(Long requestId, Long fileId, String fileVersion, String fileName, String exemptionCode, String creator,
            String exemptionStatute)
    {
        this.requestId = requestId;
        this.fileId = fileId;
        this.fileVersion = fileVersion;
        this.fileName = fileName;
        this.exemptionCode = exemptionCode;
        this.creator = creator;
        this.exemptionStatute = exemptionStatute;
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
}
