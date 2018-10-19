package gov.foia.web.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * An exception thrown by <code>PrintDocumentsAPIController</code> if there was an error while processing the requested
 * FOIA requests.
 *
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Nov 18, 2016
 */
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Error while printing documents.")
public class DocumentPrintingException extends RuntimeException
{

    private static final long serialVersionUID = -2628029472658230302L;

    public DocumentPrintingException()
    {
    }

    public DocumentPrintingException(Throwable t)
    {
        super(t);
    }

    /**
     * @param message
     */
    public DocumentPrintingException(String message)
    {
        super(message);
    }

}
