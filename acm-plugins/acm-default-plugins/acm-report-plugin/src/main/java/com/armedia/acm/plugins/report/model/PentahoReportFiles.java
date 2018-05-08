package com.armedia.acm.plugins.report.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.List;

/**
 * Created by joseph.mcgrady on 6/19/2017.
 */
@XmlRootElement(name = "repositoryFileDtoes")
public class PentahoReportFiles
{
    private List<PentahoFileProperties> pentahoFilePropertiesList;

    @XmlElement(name = "repositoryFileDto", type = PentahoFileProperties.class)
    public List<PentahoFileProperties> getPentahoFilePropertiesList()
    {
        return pentahoFilePropertiesList;
    }

    public void setPentahoFilePropertiesList(List<PentahoFileProperties> pentahoFilePropertiesList)
    {
        this.pentahoFilePropertiesList = pentahoFilePropertiesList;
    }
}