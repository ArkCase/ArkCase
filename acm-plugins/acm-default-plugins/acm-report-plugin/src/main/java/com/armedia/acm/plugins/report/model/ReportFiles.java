package com.armedia.acm.plugins.report.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by joseph.mcgrady on 6/19/2017.
 */
@XmlRootElement(name = "repositoryFileDtoes")
public class ReportFiles
{
    private List<FileProperties> filePropertiesList;

    @XmlElement(name = "repositoryFileDto", type = FileProperties.class)
    public List<FileProperties> getFilePropertiesList()
    {
        return filePropertiesList;
    }

    public void setFilePropertiesList(List<FileProperties> filePropertiesList)
    {
        this.filePropertiesList = filePropertiesList;
    }
}