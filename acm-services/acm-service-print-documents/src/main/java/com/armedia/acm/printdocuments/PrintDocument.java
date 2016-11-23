package com.armedia.acm.printdocuments;

import com.armedia.acm.plugins.casefile.model.CaseFile;

/**
 * Abstraction containing binary data of PDF versions of documents associated with an instance or instances of a
 * <code>CaseFile</code>. Instance of <code>PrintDocument</code> is produced by an implementation of
 * <code>DocumentPrintService</code>. It is up to the service implementation to decide which files will be included in
 * the PDF data delivered for printing, for example filtering files included in predifined folders, specific versions
 * and so on.
 *
 * @see DocumentPrintService
 *
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Nov 16, 2016
 *
 */
public interface PrintDocument<CF extends CaseFile>
{

    /**
     * @return generated file name to be included in the <code>Content-Disposition</code> response header.
     */
    String getFileName();

    /**
     * @return binary data from the generated PDF document.
     */
    byte[] getContent();

}