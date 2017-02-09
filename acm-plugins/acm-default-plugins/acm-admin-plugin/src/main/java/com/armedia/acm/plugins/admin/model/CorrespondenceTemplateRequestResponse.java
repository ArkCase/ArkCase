package com.armedia.acm.plugins.admin.model;

import java.util.Date;
import java.util.Map;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jan 27, 2017
 *
 */
public class CorrespondenceTemplateRequestResponse
{

    private String displayName;

    private String documentType;

    private String templateFilename;

    private String correspondenceQueryBeanId;

    private String queryType;

    private Map<String, String> templateSubstitutionVariables;

    private String dateFormatString;

    private String numberFormatString;

    private boolean activated;

    private String modifier;

    private Date modified;

    private String downloadFileName;

    /**
     * @return the dislpayName
     */
    public String getDisplayName()
    {
        return displayName;
    }

    /**
     * @param displayName
     *            the dislpayName to set
     */
    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }

    /**
     * @return the documentType
     */
    public String getDocumentType()
    {
        return documentType;
    }

    /**
     * @param documentType
     *            the documentType to set
     */
    public void setDocumentType(String documentType)
    {
        this.documentType = documentType;
    }

    /**
     * @return the templateFilename
     */
    public String getTemplateFilename()
    {
        return templateFilename;
    }

    /**
     * @param templateFilename
     *            the templateFilename to set
     */
    public void setTemplateFilename(String templateFilename)
    {
        this.templateFilename = templateFilename;
    }

    /**
     * @return the correspondenceQueryBeanId
     */
    public String getCorrespondenceQueryBeanId()
    {
        return correspondenceQueryBeanId;
    }

    /**
     * @param correspondenceQueryBeanId
     *            the correspondenceQueryBeanId to set
     */
    public void setCorrespondenceQueryBeanId(String correspondenceQueryBeanId)
    {
        this.correspondenceQueryBeanId = correspondenceQueryBeanId;
    }

    /**
     * @return the queryTpe
     */
    public String getQueryType()
    {
        return queryType;
    }

    /**
     * @param queryType
     *            the queryTpe to set
     */
    public void setQueryType(String queryType)
    {
        this.queryType = queryType;
    }

    /**
     * @return the templateSubstitutionVariables
     */
    public Map<String, String> getTemplateSubstitutionVariables()
    {
        return templateSubstitutionVariables;
    }

    /**
     * @param templateSubstitutionVariables
     *            the templateSubstitutionVariables to set
     */
    public void setTemplateSubstitutionVariables(Map<String, String> templateSubstitutionVariables)
    {
        this.templateSubstitutionVariables = templateSubstitutionVariables;
    }

    /**
     * @return the dateFormatString
     */
    public String getDateFormatString()
    {
        return dateFormatString;
    }

    /**
     * @param dateFormatString
     *            the dateFormatString to set
     */
    public void setDateFormatString(String dateFormatString)
    {
        this.dateFormatString = dateFormatString;
    }

    /**
     * @return the numberFormatString
     */
    public String getNumberFormatString()
    {
        return numberFormatString;
    }

    /**
     * @param numberFormatString
     *            the numberFormatString to set
     */
    public void setNumberFormatString(String numberFormatString)
    {
        this.numberFormatString = numberFormatString;
    }

    /**
     * @return the activated
     */
    public boolean isActivated()
    {
        return activated;
    }

    /**
     * @param activated
     *            the activated to set
     */
    public void setActivated(boolean activated)
    {
        this.activated = activated;
    }

    /**
     * @return the modifier
     */
    public String getModifier()
    {
        return modifier;
    }

    /**
     * @param modifier
     *            the modifier to set
     */
    public void setModifier(String modifier)
    {
        this.modifier = modifier;
    }

    /**
     * @return the modified
     */
    public Date getModified()
    {
        return modified;
    }

    /**
     * @param modified
     *            the modified to set
     */
    public void setModified(Date modified)
    {
        this.modified = modified;
    }

    public String getDownloadFileName()
    {
        return downloadFileName;
    }

    public void setDownloadFileName(String downloadFileName)
    {
        this.downloadFileName = downloadFileName;
    }

}
