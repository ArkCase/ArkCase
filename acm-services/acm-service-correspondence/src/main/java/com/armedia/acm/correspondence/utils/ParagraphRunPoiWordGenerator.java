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

import com.armedia.acm.core.model.ApplicationConfig;
import com.armedia.acm.correspondence.model.CorrespondenceMergeField;
import com.armedia.acm.correspondence.service.CorrespondenceMergeFieldManager;
import com.armedia.acm.correspondence.service.CorrespondenceService;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.data.AcmEntity;
import com.armedia.acm.objectonverter.DateFormats;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.core.io.Resource;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Created by armdev on 12/11/14.
 */
public class ParagraphRunPoiWordGenerator implements SpELWordEvaluator, WordGenerator
{
    public static final String DATE_TYPE = "Date";
    public static final String CURRENT_DATE = "currentDate";
    public static final String BASE_URL = "baseUrl";
    public static final String FILES = "files";

    public static final String SUBSTITUTION_PREFIX = "${";
    public static final String SUBSTITUTION_SUFFIX = "}";
    private transient final Logger log = LogManager.getLogger(getClass());

    private CorrespondenceService correspondenceService;
    private EcmFileDao ecmFileDao;
    private ObjectConverter objectConverter;
    private CorrespondenceMergeFieldManager mergeFieldManager;
    private ApplicationConfig appConfig;


    @Override
    public void generate(Resource wordTemplate, OutputStream targetStream, String objectType, Long parentObjectId) throws IOException
    {
        try (XWPFDocument template = new XWPFDocument(wordTemplate.getInputStream()))
        {
            List<XWPFParagraph> graphs = template.getParagraphs();
            List<XWPFTable> tables = template.getTables();

            // Update all plain text in the word document who is outside any tables
            updateGraphs(graphs, objectType, parentObjectId);

            // Update all text in the word document who is inside tables/rows/cells
            updateTables(tables, objectType, parentObjectId);

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

    /**
     * Generate the Word document via direct manipulation of Word paragraph texts. This works seamlessly (user sees
     * no prompt to update document fields) but loses all formatting in paragraphs with substitution variables.
     */
    @Override
    public void generate(Resource wordTemplate, OutputStream targetStream, Map<String, String> substitutions) throws IOException
    {
        Map<String, String> substitutionsWithPrefixSufix = new HashMap<>();
        substitutions.forEach((k, v) -> substitutionsWithPrefixSufix.putIfAbsent(SUBSTITUTION_PREFIX + k + SUBSTITUTION_SUFFIX, v));

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

    private List<XWPFParagraph> updateGraphs(List<XWPFParagraph> graphs, String objectType, Long parentObjectId)
    {
        for (XWPFParagraph graph : graphs)
        {
            replace(graph, objectType, parentObjectId);
        }

        return graphs;
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

    private <V> void replace(XWPFParagraph paragraph, Map<String, V> map)
    {
        for (Map.Entry<String, V> entry : map.entrySet())
        {
            replace(paragraph, entry.getKey(), entry.getValue());
        }
    }

    private <V> void replace(XWPFParagraph paragraph, String objectType, Long parentObjectId)
    {
        boolean found = true;
        while (found)
        {
            found = false;
            int pos = paragraph.getText().indexOf(SUBSTITUTION_PREFIX);
            if (pos >= 0)
            {
                found = true;
                Map<Integer, XWPFRun> posToRuns = getPosToRuns(paragraph);
                XWPFRun run = posToRuns.get(pos);
                XWPFRun lastRun = posToRuns.get(paragraph.getText().indexOf(SUBSTITUTION_SUFFIX));
                int runNum = paragraph.getRuns().indexOf(run);
                int lastRunNum = paragraph.getRuns().indexOf(lastRun);
                String texts[] = { "" };
                StringBuilder sb = new StringBuilder();

                //if the both suffix and prefix are in the same run
                if (runNum == lastRunNum) {
                    sb.append(paragraph.getRuns().get(runNum).getText(0));
                }
                else
                {
                    for (int runNums = runNum + 1; runNums < lastRunNum; runNums++)
                    {
                        sb.append(paragraph.getRuns().get(runNums).getText(0));
                    }
                }
                String spelExpressionToBeEvaluted = sb.toString();
                if (spelExpressionToBeEvaluted != null)
                {
                    texts = evaluateSpelExpression(objectType, parentObjectId, spelExpressionToBeEvaluted).split("\n");
                }

                // Snowbound throws error on runs with empty text (see AFDP-6414). So we just delete these runs
                if (texts[0].equals(""))
                {
                    for (int i = lastRunNum; i >= runNum; i--)
                    {
                        paragraph.removeRun(i);
                    }
                    continue;
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
        Map<Integer, XWPFRun> map = new HashMap<>(10);
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

    private List<XWPFTable> updateTables(List<XWPFTable> tables, String objectType, Long parentObjectId)
    {
        if (tables != null)
        {
            tables.stream().forEach(table -> {
                List<XWPFTableRow> rows = table.getRows();
                updateRows(rows, objectType, parentObjectId);
            });
        }

        return tables;
    }

    private List<XWPFTableRow> updateRows(List<XWPFTableRow> rows, String objectType, Long parentObjectId)
    {
        if (rows != null)
        {
            rows.stream().forEach(row -> {
                List<XWPFTableCell> cells = row.getTableCells();
                updateCells(cells, objectType, parentObjectId);
            });
        }

        return rows;
    }

    private List<XWPFTableCell> updateCells(List<XWPFTableCell> cells, String objectType, Long parentObjectId)
    {
        if (cells != null)
        {
            cells.stream().forEach(cell -> {
                List<XWPFParagraph> graphs = cell.getParagraphs();
                updateGraphs(graphs, objectType, parentObjectId);
            });
        }

        return cells;
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

    private String evaluateSpelExpression(String objectType, Long parentObjectId, String spelExpression)
    {
        String generatedExpression;
        boolean isExistingMergeField = false;
        SimpleDateFormat formatter = new SimpleDateFormat(DateFormats.WORKFLOW_DATE_FORMAT);

        AcmAbstractDao<AcmEntity> correspondedObjectDao = getCorrespondenceService().getAcmAbstractDao(objectType);
        Object correspondenedObject = correspondedObjectDao.find(parentObjectId);

        //Passing the object of Corresponded Object class to StandardEvaluationContext, which is going to evaluate the expressions in the context of this object.
        StandardEvaluationContext stContext = new StandardEvaluationContext(correspondenedObject);

        //Creating an object of SpelExpressionParser class, used to parse the SpEL expression
        SpelParserConfiguration config = new SpelParserConfiguration(true, true);
        SpelExpressionParser parser = new SpelExpressionParser(config);

        for (CorrespondenceMergeField mergeField : getMergeFieldManager().getMergeFields())
        {
            if (mergeField.getFieldObjectType().equalsIgnoreCase(objectType) && mergeField.getFieldId().equalsIgnoreCase(spelExpression))
            {
                spelExpression = mergeField.getFieldValue();
                isExistingMergeField = true;
            }
        }

        //Calling parseRaw() method of SpelExpressionParser, which parses the expression and returns an SpelEpression object
        SpelExpression expression = parser.parseRaw(spelExpression);

        if (isExistingMergeField)
        {
            try
            {
                if (expression.getValue(stContext) != null)
                {
                    if (expression.getValue(stContext).getClass().getSimpleName().equalsIgnoreCase(DATE_TYPE))
                    {
                        generatedExpression = formatter.format(expression.getValue(stContext));
                    }
                    else
                    {
                        generatedExpression = String.valueOf(expression.getValue(stContext));
                    }
                }
                else
                    generatedExpression = "";
            }
            catch (Exception e)
            {
                log.error("Error while retrieving some property from object {}, with parentObjectId {}", objectType, parentObjectId, e);
                generatedExpression = "";
            }
        }

        else
        {

            //check if the expression is currentDate, files or baseURL
            if (CURRENT_DATE.equalsIgnoreCase(spelExpression))
            {
                generatedExpression = formatter.format(new Date());
            }
            else if (BASE_URL.equalsIgnoreCase(spelExpression))
            {
                generatedExpression = appConfig.getBaseUrl();
            }
            else if (FILES.equalsIgnoreCase(spelExpression))
            {
                String spelExpressionForContainerId = "container.id";
                Long containerId = Long.valueOf(String.valueOf(parser.parseRaw(spelExpressionForContainerId).getValue(stContext)));

                List<EcmFile> allFiles = getEcmFileDao().findForContainer(containerId);
                StringJoiner joiner = new StringJoiner(",");
                for (EcmFile file : allFiles)
                {
                    joiner.add(file.getFileName());
                }
                generatedExpression = joiner.toString();
            }
            else
            {
                try
                {
                    if (String.valueOf(expression.getValue(stContext)) != null)
                    {
                        if (expression.getValue(stContext).getClass().getSimpleName().equalsIgnoreCase(DATE_TYPE))
                        {
                            generatedExpression = formatter.format(expression.getValue(stContext));
                        }
                        else
                        {

                            generatedExpression = String.valueOf(expression.getValue(stContext));

                        }
                    }
                    else
                    {
                        generatedExpression = "";
                    }
                }
                catch (SpelEvaluationException ex)
                {
                    log.error("Merge field with id " + expression.getExpressionString() + " does not exist");
                    generatedExpression = "";
                }


            }
        }
        return generatedExpression;
    }

    public CorrespondenceService getCorrespondenceService()
    {
        return correspondenceService;
    }

    /**
     * @param correspondenceService
     *            the correspondenceService to set
     */
    public void setCorrespondenceService(CorrespondenceService correspondenceService)
    {
        this.correspondenceService = correspondenceService;
    }

    public EcmFileDao getEcmFileDao()
    {
        return ecmFileDao;
    }

    public void setEcmFileDao(EcmFileDao ecmFileDao)
    {
        this.ecmFileDao = ecmFileDao;
    }

    public ObjectConverter getObjectConverter()
    {
        return objectConverter;
    }

    public void setObjectConverter(ObjectConverter objectConverter)
    {
        this.objectConverter = objectConverter;
    }

    public CorrespondenceMergeFieldManager getMergeFieldManager()
    {
        return mergeFieldManager;
    }

    public void setMergeFieldManager(CorrespondenceMergeFieldManager mergeFieldManager)
    {
        this.mergeFieldManager = mergeFieldManager;
    }

    public ApplicationConfig getAppConfig()
    {
        return appConfig;
    }

    public void setAppConfig(ApplicationConfig appConfig)
    {
        this.appConfig = appConfig;
    }
}
