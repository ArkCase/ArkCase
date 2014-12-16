package com.armedia.acm.correspondence.utils;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * Created by armdev on 12/11/14.
 */
public class ParagraphRunPoiWordGenerator implements PoiWordGenerator
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    public static final String substitutionPrefix = "${";
    public static final String substitutionSuffix = "}";

    /**
     * Generate the Word document via direct manipulation of Word paragraph texts.  This works seamlessly (user sees
     * no prompt to update document fields) but loses all formatting in paragraphs with substitution variables.
     */
    @Override
    public void generate(Resource wordTemplate, OutputStream targetStream, Map<String, String> substitutions) throws IOException
    {

        XWPFDocument template = new XWPFDocument(wordTemplate.getInputStream());
        List<XWPFParagraph> graphs = template.getParagraphs();

        for ( XWPFParagraph graph : graphs )
        {
            String graphText = graph.getText();

            String newText = graphText;

            boolean replaced = false;

            for ( Map.Entry<String, String> sub : substitutions.entrySet() )
            {
                String placeholder = substitutionPrefix + sub.getKey() + substitutionSuffix;
                if (newText.contains(placeholder))
                {
                    log.debug("replacing '" + placeholder + "' with '" + sub.getValue() + "'");
                    newText = newText.replace(placeholder, sub.getValue());
                    replaced = true;
                }
            }

            if ( replaced)
            {
                // the paragraph is made up of runs.  The text to be replaced may be split across contiguous runs.
                // So we need to remove all the runs, then add one new run with the replacement text.  This will
                // lose formatting.
                int origCount = graph.getRuns().size();
                for ( int a = 0; a < origCount; a++ )
                {
                    // each time we remove a run, the graph is updated, and now has one less run.  So we remove the
                    // run at index 0 until they are all gone.
                    graph.removeRun(0);
                }

                XWPFRun replacement = graph.createRun();
                replacement.setText(newText);

                graph.addRun(replacement);
            }
        }

        log.debug("writing correspondence to stream: " + targetStream);

        try
        {
            template.write(targetStream);
            targetStream.flush();
        }
        finally
        {
            try
            {
                targetStream.close();
            }
            catch (IOException e)
            {
                // could not close the file, not the end of the world
            }
        }


    }

}
