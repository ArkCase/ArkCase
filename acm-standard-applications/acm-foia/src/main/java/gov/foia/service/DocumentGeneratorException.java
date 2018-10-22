package gov.foia.service;

/**
 * Document generator exception thrown when the generator fails to generate the document based on the template.
 * 
 * @author bojan.milenkoski
 */
public class DocumentGeneratorException extends Exception
{
    private static final long serialVersionUID = 1L;

    public DocumentGeneratorException(String message)
    {
        super(message);
    }

    public DocumentGeneratorException(Exception exception)
    {
        super(exception);
    }

    public DocumentGeneratorException(String message, Exception cause)
    {
        super(message, cause);
    }

}
