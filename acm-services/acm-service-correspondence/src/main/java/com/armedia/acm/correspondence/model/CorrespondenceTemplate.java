package com.armedia.acm.correspondence.model;

import java.util.Map;

/**
 * This POJO stores the parameters to generate a new Word document based on a Word template.
 */
public class CorrespondenceTemplate
{

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

    /**
     * Text of the JPA query to retrieve the data needed by the template. This query must return a column for each
     * template substitution variable. It must be a valid JPA query.
     */
    private CorrespondenceQuery query;

    /**
     * Each entry must exactly match a field names form the query (key in this map) and substitution variable in the
     * Word template (value in this map);
     * <p/>
     * Suppose the Word template includes the substitution variable text &quot;${Create Date}&quot; and &quot;${Subject
     * Name}&quot;. In this case, the jpaQuery should return both the Create Date and the Subject Name. If &quot;Subject
     * Name&quot; is the first column returned, and &quot;Create Date&quot; is the second column, then this list
     * <strong>must</strong> include ${Subject Name} and ${Create Date} in that order.
     *
     */
    private Map<String, String> templateSubstitutionVariables;

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

    /**
     * @return the dislpayName
     */
    public String getDisplayName()
    {
        return displayName;
    }

    /**
     * @param displayName the dislpayName to set
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

    public CorrespondenceQuery getQuery()
    {
        return query;
    }

    public void setQuery(CorrespondenceQuery query)
    {
        this.query = query;
    }

    public Map<String, String> getTemplateSubstitutionVariables()
    {
        return templateSubstitutionVariables;
    }

    public void setTemplateSubstitutionVariables(Map<String, String> templateSubstitutionVariables)
    {
        this.templateSubstitutionVariables = templateSubstitutionVariables;
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
     * @param activated the activated to set
     */
    public void setActivated(boolean activated)
    {
        this.activated = activated;
    }
}
