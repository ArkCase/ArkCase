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
        for (XWPFParagraph graph : graphs)
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
                        String value = StringUtils.isNotEmpty(sub.getValue()) ? sub.getValue() : "";
                        log.debug("replacing '" + placeholder + "' with '" + value + "'");
                        newText = newText.replace(placeholder, value);
                        replaced = true;
                    }
                }

                if (replaced)
                {
                    // the paragraph is made up of runs. The text to be replaced may be split across contiguous runs.
                    // So we need to remove all the runs, then add one new run with the replacement text. This will
                    // lose formatting.
                    int origCount = graph.getRuns().size();
                    for (int a = 0; a < origCount; a++)
                    {
                        // each time we remove a run, the graph is updated, and now has one less run. So we remove the
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
