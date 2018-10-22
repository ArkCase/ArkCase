package gov.foia.model;

public class FOIADocumentDescriptor
{
    private String type;
    private String reqAck;
    private String template;
    private String doctype;
    private String filenameFormat;
    private String targetFileExtension;

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getReqAck()
    {
        return reqAck;
    }

    public void setReqAck(String reqAck)
    {
        this.reqAck = reqAck;
    }

    public String getTemplate()
    {
        return template;
    }

    public void setTemplate(String template)
    {
        this.template = template;
    }

    public String getDoctype()
    {
        return doctype;
    }

    public void setDoctype(String doctype)
    {
        this.doctype = doctype;
    }

    public String getFilenameFormat()
    {
        return filenameFormat;
    }

    public void setFilenameFormat(String filenameFormat)
    {
        this.filenameFormat = filenameFormat;
    }

    public String getTargetFileExtension()
    {
        return targetFileExtension;
    }

    public void setTargetFileExtension(String targetFileExtension)
    {
        this.targetFileExtension = targetFileExtension;
    }
}
