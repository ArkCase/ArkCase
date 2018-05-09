/**
 * 
 */
package com.armedia.acm.form.changecasestatus.model;

import com.armedia.acm.form.config.Item;
import com.armedia.acm.form.config.ResolveInformation;
import com.armedia.acm.form.config.xml.ApproverItem;
import com.armedia.acm.form.config.xml.CaseResolveInformation;

import javax.xml.bind.annotation.XmlElement;

import java.util.List;

/**
 * @author riste.tutureski
 *
 */
public class ChangeCaseStatusForm
{

    private ResolveInformation information;
    private List<Item> approvers;
    private List<String> resolutions;

    @XmlElement(name = "information", type = CaseResolveInformation.class)
    public ResolveInformation getInformation()
    {
        return information;
    }

    public void setInformation(ResolveInformation information)
    {
        this.information = information;
    }

    @XmlElement(name = "approverItem", type = ApproverItem.class)
    public List<Item> getApprovers()
    {
        return approvers;
    }

    public void setApprovers(List<Item> approvers)
    {
        this.approvers = approvers;
    }

    public List<String> getResolutions()
    {
        return resolutions;
    }

    public void setResolutions(List<String> resolutions)
    {
        this.resolutions = resolutions;
    }
}
