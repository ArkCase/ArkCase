package com.armedia.acm.plugins.admin.web.api;


import com.armedia.acm.activiti.model.AcmProcessDefinition;
import com.armedia.acm.activiti.services.AcmBpmnService;
import com.armedia.acm.plugins.admin.exception.AcmLinkFormsWorkflowException;
import com.armedia.acm.plugins.ecm.service.AcmFileTypesService;
import com.google.gson.JsonObject;
import org.activiti.engine.impl.util.json.JSONArray;
import org.apache.commons.codec.binary.Hex;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.SheetUtil;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.codehaus.plexus.util.FileUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;

/**
 * Created by admin on 6/12/15.
 */
public class LinkFormsWorkflowsService implements LinkFormsWorkflowsConstants{
    private Logger log = LoggerFactory.getLogger(LdapConfigurationService.class);

    private String configurationLocation;
    private String configurationFile;
    private String configurationFileBackupTemplate;
    private String configurationFileBackupRegex;

    private final String[] START_PROCESS_VALUES = new String[] {"", "true", "false"};

    private AcmBpmnService acmBpmnService;
    private AcmFileTypesService acmFileTypesService;

    /**
     * Return Excel workflow configuration as JSON
     * @return
     * @throws AcmLinkFormsWorkflowException
     */
    public JSONObject retrieveConfigurationAsJson() throws AcmLinkFormsWorkflowException {
        try {
            FileInputStream file = null;
            try {
                file = new FileInputStream(new File(configurationLocation + configurationFile));

                XSSFWorkbook workbook = new XSSFWorkbook(file);

                // Get Sheet number 0;
                XSSFSheet sheet = workbook.getSheetAt(0);

                // Find longest row
                int lastCell = getLastRowCell(sheet);

                // Get columns widths
                List<Double> columnsWidths = getColumnsWidths(sheet);

                List<List<Map<String, Object>>> cellsMatrix = new ArrayList();
                Map <Integer, String> columnsTypes = new HashMap();

                // Process rows and cells
                for (int rowNum = 0; rowNum < sheet.getLastRowNum(); rowNum++) {
                    List<Map<String, Object>> cellsRow = new ArrayList();
                    XSSFRow row = (XSSFRow)sheet.getRow(rowNum);
                    if (row != null) {
                        for (int colNum = 0; colNum < row.getLastCellNum(); colNum++) {

                            XSSFCell cell = (XSSFCell) row.getCell(colNum);

                            // Get type if available
                            String type = "";
                            if (columnsTypes.containsKey(colNum)) {
                                type = columnsTypes.get(colNum);
                            }

                            if (cell.getCellComment() != null) {
                                String colType = getColumnType(cell.getCellComment().getString().getString());
                                if (colType != null) {
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
                    if (cellsRow.size() < (lastCell) ) {
                        int tailSize = lastCell - cellsRow.size();
                        for (int i = 0; i < tailSize; i++) {
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
                for (AcmProcessDefinition processDefinitionIter: processDefinitions) {
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

            } finally {
                if (file != null) {
                    file.close();
                }
            }
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Can't retrieve Link Forms Workflows configuration file", e);
            }

            throw new AcmLinkFormsWorkflowException("Can't retrieve Link Forms Workflows configuration file", e);
        }
    }

    /**
     * Return name of column if found, else null
     * @param draftColumnType
     * @return
     */
    private String getColumnType(String draftColumnType) {
        String colType = null;

        switch (draftColumnType) {
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
     * @return
     */
    private Map <String, Object> getEmptyCell() {
        Map <String, Object> emptyCell = new HashMap();
        emptyCell.put(PROP_BG_COLOR, DEFAULT_BG_COLOR);
        emptyCell.put(PROP_READONLY, true);
        return emptyCell;
    }

    /**
     * Return number of last cell in the row
     * @param sheet
     * @return
     */
    private int getLastRowCell(XSSFSheet sheet) {
        int lastCell = 0;
        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            if (sheet.getRow(i) != null) {
                if (sheet.getRow(i).getLastCellNum() > lastCell) {
                    lastCell = sheet.getRow(i).getLastCellNum();
                }
            }
        }
        return lastCell;
    }

    /**
     * Return list of columns' widths
     * @param sheet
     * @return
     */
    private List<Double> getColumnsWidths(XSSFSheet sheet) {
        List<Double> columnsWidths =  new ArrayList();
        int lastCell = getLastRowCell(sheet);
        for (int i = 0; i < lastCell; i++) {
            columnsWidths.add(SheetUtil.getColumnWidth(sheet, i, false, 0, sheet.getLastRowNum()));
        }
        return columnsWidths;
    }

    /**
     * Return hash map with cell's value and type
     * @param cell
     * @param type
     * @return
     */
    private Map <String, Object> getCellValueAndType(XSSFCell cell, String type) {
        Map<String, Object> cellObj = new HashMap();
        Object value = "";
        if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
            value = cell.getStringCellValue();
        } else if (cell.getCellType() == XSSFCell.CELL_TYPE_BOOLEAN) {
            value = String.valueOf(cell.getBooleanCellValue());
        } else if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
            value = cell.getNumericCellValue();
        }

        cellObj.put(PROP_VALUE, value);
        cellObj.put(PROP_TYPE, type);
        return cellObj;
    }

    /**
     * Return hash map with style properties
     * @param cell
     * @return
     */
    private Map<String, Object> getCellStyle(XSSFCell cell) {
        Map<String, Object> cellObj = new HashMap();

        XSSFCellStyle cellStyle = cell.getCellStyle();
        XSSFFont cellFont = cellStyle.getFont();

        String color = "#" + Hex.encodeHexString(cellFont.getXSSFColor().getRGB());
        Boolean isLocked = false;
        String bgColor = "";
        if (cellStyle != null) {
            if (cellStyle.getFillForegroundColorColor() != null) {
                bgColor = "#" + Hex.encodeHexString(((XSSFColor) cellStyle.getFillForegroundColorColor()).getRGB());
            } else if (cellStyle.getFillBackgroundColorColor() != null) {
                bgColor = "#" + Hex.encodeHexString(((XSSFColor) cellStyle.getFillBackgroundColorColor()).getRGB());
            } else {
                bgColor = DEFAULT_BG_COLOR;
            }

            isLocked = cellStyle.getLocked();
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
     * @param newValues
     */
    public void updateConfiguration(List<List<String>> newValues) throws AcmLinkFormsWorkflowException {
        try {
            FileInputStream inputFile = null;
            FileOutputStream outputFile = null;
            try {
                inputFile = new FileInputStream(new File(configurationLocation + configurationFile));
                XSSFWorkbook workbook = new XSSFWorkbook(inputFile);

                // Get Sheet number 0;
                XSSFSheet sheet = workbook.getSheetAt(0);

                // Update all not locked cells' values
                for (int rowNum = 0; rowNum < newValues.size(); rowNum++) {
                    List<String> valuesRow = newValues.get(rowNum);
                    for (int colNum = 0; colNum < valuesRow.size(); colNum++) {
                        String value = valuesRow.get(colNum);
                        if (value != null) {
                            if (sheet.getRow(rowNum) != null) {
                                XSSFCell cell = sheet.getRow(rowNum).getCell(colNum);
                                if (cell!= null && cell.getCellStyle() != null) {
                                    if (!cell.getCellStyle().getLocked()) {
                                        cell.setCellValue(value);
                                    }
                                }
                            }
                        }
                    }
                }

                // Generate backup file name based on current time.
                String destFileName = String.format(configurationFileBackupTemplate, (new Date()).getTime());

                // Save current configuration file as backup
                FileUtils.copyFile(
                    new File(configurationLocation + configurationFile),
                    new File(configurationLocation + destFileName)
                );

                // Store updates
                outputFile = new FileOutputStream(configurationLocation + configurationFile);
                workbook.write(outputFile);

            } finally {
                if (inputFile != null) {
                    inputFile.close();
                }

                if (outputFile != null) {
                    outputFile.close();
                }
            }


        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Can't retrieve Link Forms Workflows configuration file", e);
            }

            throw new AcmLinkFormsWorkflowException("Can't retrieve Link Forms Workflows configuration file", e);
        }
    }

    public void setConfigurationFile(String configurationFile) {
        this.configurationFile = configurationFile;
    }

    public void setConfigurationLocation(String configurationLocation) {
        this.configurationLocation = configurationLocation;
    }

    public void setConfigurationFileBackupTemplate(String configurationFileBackupTemplate) {
        this.configurationFileBackupTemplate = configurationFileBackupTemplate;
    }

    public void setConfigurationFileBackupRegex(String configurationFileBackupRegex) {
        this.configurationFileBackupRegex = configurationFileBackupRegex;
    }

    public void setAcmBpmnService(AcmBpmnService acmBpmnService) {
        this.acmBpmnService = acmBpmnService;
    }

    public void setAcmFileTypesService(AcmFileTypesService acmFileTypesService) {
        this.acmFileTypesService = acmFileTypesService;
    }
}
