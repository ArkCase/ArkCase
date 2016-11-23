package com.armedia.acm.printdocuments;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of <code>PdfConverter</code> that allows converters capable of converting from specific mime types to
 * be registered.
 *
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Nov 18, 2016
 *
 */
public class PluggablePdfConverter implements PdfConverter
{

    /**
     * Implementation used for throwing <code>UnconvertableSourceException</code> if there is no converter registered
     * for the mime type of the source input stream.
     *
     * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Nov 18, 2016
     *
     */
    private static class NonConverter implements PdfConverter
    {

        /*
         * (non-Javadoc)
         *
         * @see com.armedia.acm.printdocumentse.PdfConverter#convertToPdf(java.io.InputStream, java.lang.String)
         */
        @Override
        public InputStream convertToPdf(InputStream sourceInputStream, String mimeType) throws UnconvertableSourceException
        {
            throw new UnconvertableSourceException(String.format("No registered converter for mime type %s.", mimeType));
        }

    }

    private static final PdfConverter NON_CONVERTER = new NonConverter();

    /**
     * Converters registry. Converters are registered with a key that corresponds to the mime type they are capable of
     * converting to PDF.
     */
    private Map<String, PdfConverter> convertеrRegistry = new HashMap<>();

    /*
     * (non-Javadoc)
     *
     * @see com.armedia.acm.printdocuments.PdfConverter#convertToPdf(java.io.InputStream, java.lang.String)
     */
    @Override
    public InputStream convertToPdf(InputStream sourceStream, String mimeType) throws UnconvertableSourceException
    {
        if ("application/pdf".equalsIgnoreCase(mimeType))
        {
            return sourceStream;
        }
        return convertеrRegistry.getOrDefault(mimeType, NON_CONVERTER).convertToPdf(sourceStream, mimeType);
    }

    /**
     * @param convertеrRegistry the convertеrRegistry to set
     */
    public void setConvertеrRegistry(Map<String, PdfConverter> convertеrRegistry)
    {
        this.convertеrRegistry = convertеrRegistry;
    }

}
