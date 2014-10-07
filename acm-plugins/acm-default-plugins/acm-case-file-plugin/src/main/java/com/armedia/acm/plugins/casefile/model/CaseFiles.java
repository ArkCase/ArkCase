package com.armedia.acm.plugins.casefile.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * To return lists of case files via JAXB marshalling.
 */
@XmlRootElement(name="caseFiles")
public class CaseFiles
{
    private List<CaseFile> caseFiles;

    @XmlElement(name="caseFile")
    public List<CaseFile> getCaseFiles()
    {
        return caseFiles;
    }

    public void setCaseFiles(List<CaseFile> caseFiles)
    {
        this.caseFiles = caseFiles;
    }
}
