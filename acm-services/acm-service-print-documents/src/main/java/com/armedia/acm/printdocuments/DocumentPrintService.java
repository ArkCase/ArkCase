package com.armedia.acm.printdocuments;

import java.io.IOException;
import java.util.List;

/**
 * Produces an instance of <code>PrintDocument</code> for instances of <code>CaseFile</code> or subclasses. It is up to
 * the implementation to decide which pages associated with a case file will be included for printing.
 *
 * @see com.armedia.acm.plugins.casefile.model.CaseFile
 * @see PrintDocument
 *
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Nov 16, 2016
 *
 */
public interface DocumentPrintService<CF, PD extends PrintDocument<CF>>
{

    /**
     * @param caseFileIds IDs of the instances of <codeCaseFile</code> whose documents should be printed.
     * @return an instance of <code>PrintDocument</code> containing the binary data of the documents associated with
     *         case files, that are included for printing.
     * @throws IOException if there was an error while processing the case files documents.
     */
    PD createPrintDocument(List<Long> caseFileIds) throws IOException;

    /**
     * Allows for post processing of the print document before response is sent back. This might include raising events,
     * or adding additional pages to the resulting PDF.
     *
     * @param printDocument the print document that was produced by <code>createPrintDocument</code> method.
     * @see DocumentPrintService#createPrintDocument(List)
     */
    void postProcessPrintDocument(PD printDocument);

}
