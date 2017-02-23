package com.armedia.acm.correspondence.model;

import java.util.Date;

/**
 * This POJO stores the parameters to generate a new Word document based on a Word template.
 */
public class CorrespondenceTemplate
{

    private String templateId;

    private String templateVersion;

    private boolean templateVersionActive;
    /**
     * The display name for the template, by default set to the value of <code>documentType</code>.
     */
    private String displayName;

    /**
     * The document type of the generated correspondence, e.g. &quot;Report of Investigation&quot;
     */
    private String documentType;

    /**
     * Must match a filename in the $HOME/.acm/correspondenceTemplates folder. This file must be a Microsoft Word .docx
     * file, in the Office XML file format first introduced by Word 2007.
     */
    private String templateFilename;

    private String objectType;

    /**
     * The date format that should be applied to any date. All date columns will be displayed in the generated
     * correspondence in this format. Must be a valid date format according to Java DateFormat rules.
     */
    private String dateFormatString;

    /**
     * The number format that should be applied to any number. All number columns will be displayed in the generated
     * correspondence in this format. Must be a valid number format according to Java NumberFormat rules.
     */
    private String numberFormatString;

    private boolean activated;

    private String modifier;

    private Date modified;

    public String getTemplateId()
    {
        return templateId;
    }

    public void setTemplateId(String templateId)
    {
        this.templateId = templateId;
    }

    public String getTemplateVersion()
    {
        return templateVersion;
    }

    public void setTemplateVersion(String templateVersion)
    {
        this.templateVersion = templateVersion;
    }

    public boolean isTemplateVersionActive()
    {
        return templateVersionActive;
    }

    public void setTemplateVersionActive(boolean templateVersionActive)
    {
        this.templateVersionActive = templateVersionActive;
    }

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

    public String getDocumentType()
    {
        return documentType;
    }

    public void setDocumentType(String documentType)
    {
        this.documentType = documentType;
    }

    public String getTemplateFilename()
    {
        return templateFilename;
    }

    public void setTemplateFilename(String templateFilename)
    {
        this.templateFilename = templateFilename;
    }

    public String getObjectType()
    {
        return objectType;
    }

    public void setObjectType(String objectType)
    {
        this.objectType = objectType;
    }

    public String getDateFormatString()
    {
        return dateFormatString;
    }

    public void setDateFormatString(String dateFormatString)
    {
        this.dateFormatString = dateFormatString;
    }

    public String getNumberFormatString()
    {
        return numberFormatString;
    }

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

}
