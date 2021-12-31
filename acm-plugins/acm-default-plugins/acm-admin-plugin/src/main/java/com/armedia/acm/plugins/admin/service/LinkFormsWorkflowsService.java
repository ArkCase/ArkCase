package com.armedia.acm.plugins.admin.service;

/*-
 * #%L
 * ACM Default Plugin: admin
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

import com.armedia.acm.activiti.model.AcmProcessDefinition;
import com.armedia.acm.activiti.services.AcmBpmnService;
import com.armedia.acm.configuration.model.ConfigurationClientConfig;
import com.armedia.acm.configuration.service.FileConfigurationService;
import com.armedia.acm.plugins.admin.exception.AcmLinkFormsWorkflowException;
import com.armedia.acm.plugins.admin.model.LinkFormsWorkflowsConstants;
import com.armedia.acm.plugins.ecm.service.AcmFileTypesService;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.SheetUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCellType;
import org.springframework.core.io.InputStreamResource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by admin on 6/12/15.
 */
public class LinkFormsWorkflowsService implements LinkFormsWorkflowsConstants
{
    private final String[] START_PROCESS_VALUES = new String[] { "", "true", "false" };
    private Logger log = LogManager.getLogger(LdapConfigurationService.class);
    private String configurationFile;
    private String configurationFileBackupTemplate;
    private FileConfigurationService fileConfigurationService;
    private ConfigurationClientConfig configurationClientConfig;
    private AcmBpmnService acmBpmnService;
    private AcmFileTypesService acmFileTypesService;

    /**
     * Return Excel workflow configuration as JSON
     *
     * @return
     * @throws AcmLinkFormsWorkflowException
     */
    public JSONObject retrieveConfigurationAsJson() throws AcmLinkFormsWorkflowException
    {
        String rulesLocation = configurationClientConfig.getRulesPath();

        try (InputStream stream = fileConfigurationService.getInputStreamFromConfiguration(rulesLocation + "/"
                + configurationFile))
        {

            XSSFWorkbook workbook = new XSSFWorkbook(stream);

            // Get Sheet number 0;
            XSSFSheet sheet = workbook.getSheetAt(0);

            // Find longest row
            int lastCell = getLastRowCell(sheet);

            // Get columns widths
            List<Double> columnsWidths = getColumnsWidths(sheet);

            List<List<Map<String, Object>>> cellsMatrix = new ArrayList();
            Map<Integer, String> columnsTypes = new HashMap();

            // Process rows and cells
            for (int rowNum = 0; rowNum < sheet.getLastRowNum(); rowNum++)
            {
                List<Map<String, Object>> cellsRow = new ArrayList();
                XSSFRow row = sheet.getRow(rowNum);
                if (row != null)
                {
                    for (int colNum = 0; colNum < row.getLastCellNum(); colNum++)
                    {

                        XSSFCell cell = row.getCell(colNum);

                        // Get type if available
                        String type = "";
                        if (columnsTypes.containsKey(colNum))
                        {
                            type = columnsTypes.get(colNum);
                        }

                        if (cell.getCellComment() != null)
                        {
                            String colType = getColumnType(cell.getCellComment().getString().getString());
                            if (colType != null)
                            {
                                columnsTypes.put(colNum, colType);
                            }
                        }
                        Map<String, Object> cellObj = new HashMap();

                        cellObj.putAll(getCellValueAndType(cell, type));
                        cellObj.putAll(getCellStyle(cell));
                        cellsRow.add(cellObj);
                    }
                }

                // Add tail cells if required
                if (cellsRow.size() < (lastCell))
                {
                    int tailSize = lastCell - cellsRow.size();
                    for (int i = 0; i < tailSize; i++)
                    {
                        cellsRow.add(getEmptyCell());
                    }
                }

                cellsMatrix.add(cellsRow);
            }

            // Add Tail rows
            // Get Process Names. There are no ways to get all processes
            List<AcmProcessDefinition> processDefinitions = acmBpmnService.list("name", true);
            List<String> processNames = new ArrayList();
            processNames.add("");
            for (AcmProcessDefinition processDefinitionIter : processDefinitions)
            {
                processNames.add(processDefinitionIter.getKey());
            }

            Set<String> fileTypes = new HashSet();
            fileTypes.add("");
            // Add file types
            fileTypes.addAll(acmFileTypesService.getFileTypes());

            // Add forms list
            fileTypes.addAll(acmFileTypesService.getForms());

            List<String> sortedFileTypes = new LinkedList();
            sortedFileTypes.addAll(fileTypes);
            Collections.sort(sortedFileTypes);

            // Add metadata (available values or some columns)
            JSONObject metaObject = new JSONObject();
            metaObject.put(COL_TYPE_FILE_TYPE, sortedFileTypes);
            metaObject.put(COL_TYPE_PROCESS_NAME, processNames);
            metaObject.put(COL_TYPE_START_PROCESS, START_PROCESS_VALUES);

            JSONObject configObject = new JSONObject();
            configObject.put(PROP_CELLS, cellsMatrix);
            configObject.put(PROP_COLUMNS_WIDTH, columnsWidths);
            configObject.put(PROP_META, metaObject);

            return configObject;

        }
        catch (Exception e)
        {

            log.error("Can't retrieve Link Forms Workflows configuration file", e);

            throw new AcmLinkFormsWorkflowException("Can't retrieve Link Forms Workflows configuration file", e);
        }
    }

    /**
     * Return name of column if found, else null
     *
     * @param draftColumnType
     * @return
     */
    private String getColumnType(String draftColumnType)
    {
        String colType = null;

        switch (draftColumnType)
        {
        case COL_TYPE_RULE_NAME:
            colType = COL_TYPE_RULE_NAME;
            break;

        case COL_TYPE_FILE_TYPE:
            colType = COL_TYPE_FILE_TYPE;
            break;

        case COL_TYPE_START_PROCESS:
            colType = COL_TYPE_START_PROCESS;
            break;

        case COL_TYPE_PROCESS_NAME:
            colType = COL_TYPE_PROCESS_NAME;
            break;

        case COL_TYPE_PRIORITY:
            colType = COL_TYPE_PRIORITY;
            break;

        case COL_TYPE_DUE_DATE:
            colType = COL_TYPE_DUE_DATE;
            break;
        }

        return colType;
    }

    /**
     * Return empty cell hash map
     *
     * @return
     */
    private Map<String, Object> getEmptyCell()
    {
        Map<String, Object> emptyCell = new HashMap();
        emptyCell.put(PROP_BG_COLOR, DEFAULT_BG_COLOR);
        emptyCell.put(PROP_READONLY, true);
        return emptyCell;
    }

    /**
     * Return number of last cell in the row
     *
     * @param sheet
     * @return
     */
    private int getLastRowCell(XSSFSheet sheet)
    {
        int lastCell = 0;
        for (int i = 0; i <= sheet.getLastRowNum(); i++)
        {
            if (sheet.getRow(i) != null)
            {
                if (sheet.getRow(i).getLastCellNum() > lastCell)
                {
                    lastCell = sheet.getRow(i).getLastCellNum();
                }
            }
        }
        return lastCell;
    }

    /**
     * Return list of columns' widths
     *
     * @param sheet
     * @return
     */
    private List<Double> getColumnsWidths(XSSFSheet sheet)
    {
        List<Double> columnsWidths = new ArrayList();
        int lastCell = getLastRowCell(sheet);
        for (int i = 0; i < lastCell; i++)
        {
            columnsWidths.add(SheetUtil.getColumnWidth(sheet, i, false, 0, sheet.getLastRowNum()));
        }
        return columnsWidths;
    }

    /**
     * Return hash map with cell's value and type
     *
     * @param cell
     * @param type
     * @return
     */
    private Map<String, Object> getCellValueAndType(XSSFCell cell, String type)
    {
        Map<String, Object> cellObj = new HashMap<>();
        Object value = "";
        if (cell.getCellType() == CellType.STRING)
        {
            value = cell.getStringCellValue();
        }
        else if (cell.getCellType() == CellType.BOOLEAN)
        {
            value = String.valueOf(cell.getBooleanCellValue());
        }
        else if (cell.getCellType() == CellType.NUMERIC)
        {
            value = cell.getNumericCellValue();
        }

        cellObj.put(PROP_VALUE, value);
        cellObj.put(PROP_TYPE, type);
        return cellObj;
    }

    /**
     * Return hash map with style properties
     *
     * @param cell
     * @return
     */
    private Map<String, Object> getCellStyle(XSSFCell cell)
    {
        Map<String, Object> cellObj = new HashMap();

        XSSFCellStyle cellStyle = cell.getCellStyle();
        XSSFFont cellFont = cellStyle.getFont();

        String color = "#" + Hex.encodeHexString(cellFont.getXSSFColor().getRGB());
        Boolean isLocked = false;
        String bgColor = "";
        if (cellStyle != null)
        {
            if (cellStyle.getFillForegroundColorColor() != null)
            {
                bgColor = "#" + Hex.encodeHexString(cellStyle.getFillForegroundColorColor().getRGB());
            }
            else if (cellStyle.getFillBackgroundColorColor() != null)
            {
                bgColor = "#" + Hex.encodeHexString(cellStyle.getFillBackgroundColorColor().getRGB());
            }
            else
            {
                bgColor = DEFAULT_BG_COLOR;
            }

            isLocked = false;
        }
        int fontSize = cellFont.getFontHeightInPoints();

        cellObj.put(PROP_COLOR, color);
        cellObj.put(PROP_BG_COLOR, bgColor);
        cellObj.put(PROP_READONLY, isLocked);
        cellObj.put(PROP_FONT_SIZE, fontSize);

        return cellObj;
    }

    /**
     * Update Excel workflow configuration
     *
     * @param newValues
     */
    public void updateConfiguration(List<List<String>> newValues) throws AcmLinkFormsWorkflowException
    {
        String rulesLocation = configurationClientConfig.getRulesPath();

        try (InputStream inputStream = fileConfigurationService.getInputStreamFromConfiguration(rulesLocation + "/"
                + configurationFile))
        {

            byte[] receivedBinary = IOUtils.toByteArray(inputStream);

            try (ByteArrayInputStream receivedBytes = new ByteArrayInputStream(receivedBinary))
            {

                XSSFWorkbook workbook = new XSSFWorkbook(receivedBytes);

                // Get Sheet number 0;
                XSSFSheet sheet = workbook.getSheetAt(0);

                // Update all not locked cells' values
                for (int rowNum = 0; rowNum < newValues.size(); rowNum++)
                {
                    List<String> valuesRow = newValues.get(rowNum);
                    for (int colNum = 0; colNum < valuesRow.size(); colNum++)
                    {
                        String value = valuesRow.get(colNum);
                        if (value != null)
                        {
                            if (sheet.getRow(rowNum) != null)
                            {
                                XSSFCell cell = sheet.getRow(rowNum).getCell(colNum);
                                if (cell != null && cell.getCellStyle() != null)
                                {
                                    if (cell.getCTCell().getT() == STCellType.INLINE_STR)
                                    { // cell has inline string in it
                                        if (cell.getCTCell().isSetIs())
                                        { // inline string has is element
                                            cell.getCTCell().getIs().setT(value); // set t element in is element
                                        }
                                        else
                                        {
                                            cell.getCTCell().setV(value); // set v element of inline string
                                        }
                                    }
                                    else
                                    {
                                        cell.setCellValue(value); // set shared string cell value
                                    }

                                }
                            }
                        }
                    }
                }

                // Generate backup file name based on current time.
                String destFileName = String.format(configurationFileBackupTemplate, (new Date()).getTime());

                // Save current configuration file as backup
                try (ByteArrayInputStream inputBytes = new ByteArrayInputStream(receivedBinary))
                {
                    fileConfigurationService.moveFileToConfiguration(new InputStreamResource(inputBytes),
                            rulesLocation + "/" + destFileName);
                }

                // Store updates
                try (ByteArrayOutputStream outputBytes = new ByteArrayOutputStream())
                {
                    workbook.write(outputBytes);
                    try (ByteArrayInputStream inputBytes = new ByteArrayInputStream(outputBytes.toByteArray()))
                    {
                        fileConfigurationService.moveFileToConfiguration(new InputStreamResource(inputBytes),
                                rulesLocation + "/" + configurationFile);
                    }
                }
            }

        }
        catch (Exception e)
        {

            log.error("Can't update Link Forms Workflows configuration file", e);
            throw new AcmLinkFormsWorkflowException("Can't update Link Forms Workflows configuration file", e);
        }
    }

    public void setConfigurationFile(String configurationFile)
    {
        this.configurationFile = configurationFile;
    }

    public void setConfigurationFileBackupTemplate(String configurationFileBackupTemplate)
    {
        this.configurationFileBackupTemplate = configurationFileBackupTemplate;
    }

    public void setFileConfigurationService(FileConfigurationService fileConfigurationService)
    {
        this.fileConfigurationService = fileConfigurationService;
    }

    public void setConfigurationClientConfig(ConfigurationClientConfig configurationClientConfig)
    {
        this.configurationClientConfig = configurationClientConfig;
    }

    public void setAcmBpmnService(AcmBpmnService acmBpmnService)
    {
        this.acmBpmnService = acmBpmnService;
    }

    public void setAcmFileTypesService(AcmFileTypesService acmFileTypesService)
    {
        this.acmFileTypesService = acmFileTypesService;
    }
}
