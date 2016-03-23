package com.armedia.acm.correspondence.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
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
        List<XWPFTable> tables = template.getTables();

        // Update all plain text in the word document who is outside any tables
        updateGraphs(graphs, substitutions);

        // Update all text in the word document who is inside tables/rows/cells
        updateTables(tables, substitutions);

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

    private List<XWPFParagraph> updateGraphs(List<XWPFParagraph> graphs, Map<String, String> substitutions)
    {
        for ( XWPFParagraph graph : graphs )
        {
            String graphText = graph.getText();
            String newText = graphText;

            if (newText != null)
            {
                boolean replaced = false;

                for (Map.Entry<String, String> sub : substitutions.entrySet())
                {
                    String placeholder = substitutionPrefix + sub.getKey() + substitutionSuffix;
                    if (newText.contains(placeholder))
                    {
                        log.debug("replacing '" + placeholder + "' with '" + sub.getValue() + "'");
                        newText = newText.replace(placeholder, sub.getValue());
                        replaced = true;
                    }
                }

                if (replaced)
                {
                    // the paragraph is made up of runs.  The text to be replaced may be split across contiguous runs.
                    // So we need to remove all the runs, then add one new run with the replacement text.  This will
                    // lose formatting.
                    int origCount = graph.getRuns().size();
                    for (int a = 0; a < origCount; a++)
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
        }

        return graphs;
    }

    private List<XWPFTable> updateTables(List<XWPFTable> tables, Map<String, String> substitutions)
    {
        if (tables != null)
        {
            tables.stream().forEach(table -> {
                List<XWPFTableRow> rows = table.getRows();
                updateRows(rows, substitutions);
            });
        }

        return tables;
    }

    private List<XWPFTableRow> updateRows(List<XWPFTableRow> rows, Map<String, String> substitutions)
    {
        if (rows != null)
        {
            rows.stream().forEach(row -> {
                List<XWPFTableCell> cells = row.getTableCells();
                updateCells(cells, substitutions);
            });
        }

        return rows;
    }

    private List<XWPFTableCell> updateCells(List<XWPFTableCell> cells, Map<String, String> substitutions)
    {
        if (cells != null)
        {
            cells.stream().forEach(cell -> {
                List<XWPFParagraph> graphs = cell.getParagraphs();
                updateGraphs(graphs, substitutions);
            });
        }

        return cells;
    }

}
