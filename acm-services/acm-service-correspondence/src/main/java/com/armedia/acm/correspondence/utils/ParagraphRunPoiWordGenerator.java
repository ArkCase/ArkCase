package com.armedia.acm.correspondence.utils;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by armdev on 12/11/14.
 */
public class ParagraphRunPoiWordGenerator implements PoiWordGenerator
{
    public static final String substitutionPrefix = "${";
    public static final String substitutionSuffix = "}";
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Generate the Word document via direct manipulation of Word paragraph texts. This works seamlessly (user sees
     * no prompt to update document fields) but loses all formatting in paragraphs with substitution variables.
     */
    @Override
    public void generate(Resource wordTemplate, OutputStream targetStream, Map<String, String> substitutions) throws IOException
    {
        Map<String, String> substitutionsWithPrefixSufix = new HashMap<>();
        substitutions.forEach((k, v) -> substitutionsWithPrefixSufix.putIfAbsent(substitutionPrefix + k + substitutionSuffix, v));

        try (XWPFDocument template = new XWPFDocument(wordTemplate.getInputStream()))
        {
            List<XWPFParagraph> graphs = template.getParagraphs();
            List<XWPFTable> tables = template.getTables();

            // Update all plain text in the word document who is outside any tables
            updateGraphs(graphs, substitutionsWithPrefixSufix);

            // Update all text in the word document who is inside tables/rows/cells
            updateTables(tables, substitutionsWithPrefixSufix);

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

    private List<XWPFParagraph> updateGraphs(List<XWPFParagraph> graphs, Map<String, String> substitutions)
    {
        for (XWPFParagraph graph : graphs)
        {
            replace(graph, substitutions);
        }

        return graphs;
    }

    private <V> void replace(XWPFParagraph paragraph, String searchText, V replacement)
    {
        boolean found = true;
        while (found)
        {
            found = false;
            int pos = paragraph.getText().indexOf(searchText);
            if (pos >= 0)
            {
                found = true;
                Map<Integer, XWPFRun> posToRuns = getPosToRuns(paragraph);
                XWPFRun run = posToRuns.get(pos);
                XWPFRun lastRun = posToRuns.get(pos + searchText.length() - 1);
                int runNum = paragraph.getRuns().indexOf(run);
                int lastRunNum = paragraph.getRuns().indexOf(lastRun);
                String texts[] = replacement.toString().split("\n");
                run.setText(texts[0], 0);
                XWPFRun newRun = run;
                for (int i = 1; i < texts.length; i++)
                {
                    newRun.addCarriageReturn();
                    newRun = paragraph.insertNewRun(runNum + i);
                    /*
                     * We should copy all style attributes to the newRun from run
                     * also from background color, ...
                     * Here we duplicate only the simple attributes...
                     */
                    newRun.setText(texts[i]);
                    newRun.setBold(run.isBold());
                    newRun.setCapitalized(run.isCapitalized());
                    // run.getCharacterSpacing() throws NullPointerException. Maybe in future version of the library
                    // this will be fixed.
                    // newRun.setCharacterSpacing(run.getCharacterSpacing());
                    newRun.setColor(run.getColor());
                    newRun.setDoubleStrikethrough(run.isDoubleStrikeThrough());
                    newRun.setEmbossed(run.isEmbossed());
                    newRun.setFontFamily(run.getFontFamily());
                    newRun.setFontSize(run.getFontSize());
                    newRun.setImprinted(run.isImprinted());
                    newRun.setItalic(run.isItalic());
                    newRun.setKerning(run.getKerning());
                    newRun.setShadow(run.isShadowed());
                    newRun.setSmallCaps(run.isSmallCaps());
                    newRun.setStrikeThrough(run.isStrikeThrough());
                    newRun.setSubscript(run.getSubscript());
                    newRun.setUnderline(run.getUnderline());
                }
                for (int i = lastRunNum + texts.length - 1; i > runNum + texts.length - 1; i--)
                {
                    paragraph.removeRun(i);
                }
            }
        }
    }

    private Map<Integer, XWPFRun> getPosToRuns(XWPFParagraph paragraph)
    {
        int pos = 0;
        Map<Integer, XWPFRun> map = new HashMap<Integer, XWPFRun>(10);
        for (XWPFRun run : paragraph.getRuns())
        {
            String runText = run.text();
            if (runText != null)
            {
                for (int i = 0; i < runText.length(); i++)
                {
                    map.put(pos + i, run);
                }
                pos += runText.length();
            }
        }
        return (map);
    }

    private <V> void replace(XWPFParagraph paragraph, Map<String, V> map)
    {
        for (Map.Entry<String, V> entry : map.entrySet())
        {
            replace(paragraph, entry.getKey(), entry.getValue());
        }
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
