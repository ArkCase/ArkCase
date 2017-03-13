package com.armedia.acm.plugins.admin.model;

import java.util.Date;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jan 27, 2017
 *
 */
public class CorrespondenceTemplateRequestResponse
{

    private String templateId;

    private String templateVersion;

    private boolean templateVersionActive;

    private String label;

    private String documentType;

    private String templateFilename;

    private String correspondenceQueryBeanId;

    private String objectType;

    private String dateFormatString;

    private String numberFormatString;

    private boolean activated;

    private String modifier;

    private Date modified;

    private String downloadFileName;

    /**
     * @return the templateId
     */
    public String getTemplateId()
    {
        return templateId;
    }

    /**
     * @param templateId
     *            the templateId to set
     */
    public void setTemplateId(String templateId)
    {
        this.templateId = templateId;
    }

    /**
     * @return the templateVersion
     */
    public String getTemplateVersion()
    {
        return templateVersion;
    }

    /**
     * @param templateVersion
     *            the templateVersion to set
     */
    public void setTemplateVersion(String templateVersion)
    {
        this.templateVersion = templateVersion;
    }

    /**
     * @return the templateVersionActive
     */
    public boolean isTemplateVersionActive()
    {
        return templateVersionActive;
    }

    /**
     * @param templateVersionActive
     *            the templateVersionActive to set
     */
    public void setTemplateVersionActive(boolean templateVersionActive)
    {
        this.templateVersionActive = templateVersionActive;
    }

    /**
     * @return the label
     */
    public String getLabel()
    {
        return label;
    }

    /**
     * @param label
     *            the label to set
     */
    public void setLabel(String label)
    {
        this.label = label;
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
     * @return the objectType
     */
    public String getObjectType()
    {
        return objectType;
    }

    /**
     * @param objectType
     *            the objectType to set
     */
    public void setObjectType(String objectType)
    {
        this.objectType = objectType;
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

    /**
     * @return the downloadFileName
     */
    public String getDownloadFileName()
    {
        return downloadFileName;
    }

    /**
     * @param downloadFileName
     *            the downloadFileName to set
     */
    public void setDownloadFileName(String downloadFileName)
    {
        this.downloadFileName = downloadFileName;
    }

}
