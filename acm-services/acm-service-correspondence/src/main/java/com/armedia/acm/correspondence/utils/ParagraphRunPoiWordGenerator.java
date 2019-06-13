package com.armedia.acm.correspondence.utils;

/*-
 * #%L
 * ACM Service: Correspondence Library
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
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
    private transient final Logger log = LogManager.getLogger(getClass());

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
                String texts[] = { "" };
                if (replacement != null)
                {
                    texts = replacement.toString().split("\n");
                }

                // Snowbound throws error on runs with empty text (see AFDP-6414). So we just delete these runs
                if (texts[0].equals(""))
                {
                    for (int i = lastRunNum; i >= runNum; i--)
                    {
                        paragraph.removeRun(i);
                    }
                    break;
                }

                // set the run text to the first line of the replacement; this existing run maintains its formatting
                // so no formatting code is needed.
                run.setText(texts[0], 0);
                XWPFRun newRun = run;

                // for each subsequent line of the replacement text, add a new run with the new line; since we are
                // adding a new run, we need to set the formatting.
                for (int i = 1; i < texts.length; i++)
                {
                    newRun.addCarriageReturn();
                    if (texts[i] != null && !texts[i].equals(""))
                    {
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
