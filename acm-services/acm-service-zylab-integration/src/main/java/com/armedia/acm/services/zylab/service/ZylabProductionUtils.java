package com.armedia.acm.services.zylab.service;

/*-
 * #%L
 * ACM Service: Arkcase ZyLAB Integration
 * %%
 * Copyright (C) 2014 - 2021 ArkCase LLC
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

import com.armedia.acm.services.zylab.model.ZylabFile;
import com.armedia.acm.services.zylab.model.ZylabFileMetadata;
import com.armedia.acm.services.zylab.model.ZylabLoadFileColumns;
import com.armedia.acm.tool.zylab.exception.ZylabProductionSyncException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.LinkedCaseInsensitiveMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Aleksandar Acevski <aleksandar.acevski@armedia.com> on March, 2021
 */
public class ZylabProductionUtils
{
    private final static Logger log = LogManager.getLogger(ZylabProductionUtils.class);

    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public static File getLoadFile(long matterId, String productionKey, List<File> productionFiles) throws ZylabProductionSyncException
    {
        return productionFiles.stream()
                .filter(file -> file.getParentFile().getName().equals("DATA") && file.getName().toLowerCase().endsWith(".csv"))
                .findFirst()
                .orElseThrow(() -> new ZylabProductionSyncException(
                        String.format("No CSV load file found in production %s,for ZyLAB Matter %d", productionKey, matterId)));
    }

    public static List<ZylabFileMetadata> getFileMetadataFromLoadFile(File loadFile, Long matterId, String productionKey)
            throws ZylabProductionSyncException
    {
        List<ZylabFileMetadata> zylabFileMetadataList = new ArrayList<>();

        try (CSVParser parser = new CSVParser(new InputStreamReader(new BOMInputStream(new FileInputStream(loadFile)),
                StandardCharsets.UTF_8), CSVFormat.DEFAULT.withFirstRecordAsHeader()))
        {
            List<CSVRecord> records = parser.getRecords();
            List<String> headers = parser.getHeaderNames();

            for (CSVRecord record : records)
            {
                Map<String, String> map = new LinkedCaseInsensitiveMap<>();
                for (int j = 0; j < headers.size(); j++)
                {
                    map.put(headers.get(j), record.get(j));
                }
                ZylabFileMetadata zylabFileMetadata = mapZylabFileMetadata(map, matterId, productionKey);
                zylabFileMetadataList.add(zylabFileMetadata);
            }
        }
        catch (IOException e)
        {
            throw new ZylabProductionSyncException(
                    String.format("Error processing load file data in production %s,for ZyLAB Matter %d", productionKey, matterId), e);
        }
        return zylabFileMetadataList;
    }

    public static ZylabFileMetadata mapZylabFileMetadata(Map<String, String> dataMap, Long matterId, String productionKey)
    {
        ZylabFileMetadata zylabFileMetadata = new ZylabFileMetadata();

        zylabFileMetadata.setMatterId(matterId);
        zylabFileMetadata.setProductionKey(productionKey);
        zylabFileMetadata.setName(dataMap.get(ZylabLoadFileColumns.NAME));

        if (NumberUtils.isParsable(dataMap.get(ZylabLoadFileColumns.ZYLAB_ID)))
        {
            zylabFileMetadata.setZylabId(Long.valueOf(dataMap.get(ZylabLoadFileColumns.ZYLAB_ID)));
        }
        if (NumberUtils.isParsable(dataMap.get(ZylabLoadFileColumns.PRODUCED_PAGES)))
        {
            zylabFileMetadata.setProducedPages(Integer.valueOf(dataMap.get(ZylabLoadFileColumns.PRODUCED_PAGES)));
        }
        zylabFileMetadata.setProductionCreateDate(parseDate(dataMap.get(ZylabLoadFileColumns.PRODUCTION_CREATE_DATE)));
        zylabFileMetadata.setContainsRedaction(Boolean.valueOf(dataMap.get(ZylabLoadFileColumns.CONTAINS_REDACTION)));
        zylabFileMetadata.setRedactionCode1(dataMap.get(ZylabLoadFileColumns.REDACTION_CODE_1));
        zylabFileMetadata.setRedactionCode2(dataMap.get(ZylabLoadFileColumns.REDACTION_CODE_2));
        zylabFileMetadata.setRedactionJustification(dataMap.get(ZylabLoadFileColumns.REDACTION_JUSTIFICATION));
        zylabFileMetadata.setCustodian(dataMap.get(ZylabLoadFileColumns.CUSTODIAN));
        zylabFileMetadata.setDocName(dataMap.get(ZylabLoadFileColumns.DOC_NAME));
        if (NumberUtils.isParsable(dataMap.get(ZylabLoadFileColumns.DOC_PAGE_COUNT)))
        {
            zylabFileMetadata.setDocPageCount(Integer.valueOf(dataMap.get(ZylabLoadFileColumns.DOC_PAGE_COUNT)));
        }
        zylabFileMetadata.setDocDate(parseDate(dataMap.get(ZylabLoadFileColumns.DOC_DATE)));
        zylabFileMetadata.setDocExt(dataMap.get(ZylabLoadFileColumns.DOC_EXT));
        if (NumberUtils.isParsable(dataMap.get(ZylabLoadFileColumns.DOC_SIZE)))
        {
            zylabFileMetadata.setDocSize(Long.valueOf(dataMap.get(ZylabLoadFileColumns.DOC_SIZE)));
        }
        zylabFileMetadata.setHasAttachment(Boolean.valueOf(dataMap.get(ZylabLoadFileColumns.HAS_ATTACHMENT)));
        zylabFileMetadata.setAttachment(Boolean.valueOf(dataMap.get(ZylabLoadFileColumns.IS_ATTACHMENT)));
        zylabFileMetadata.setEmailFrom(dataMap.get(ZylabLoadFileColumns.EMAIL_FROM));
        zylabFileMetadata.setEmailRecipient(dataMap.get(ZylabLoadFileColumns.EMAIL_RECIPIENT));
        if (NumberUtils.isParsable(dataMap.get(ZylabLoadFileColumns.MULTIMEDIA_DURATION_SEC)))
        {
            zylabFileMetadata.setMultimediaDurationSec(Integer.valueOf(dataMap.get(ZylabLoadFileColumns.MULTIMEDIA_DURATION_SEC)));
        }
        zylabFileMetadata.setMultimediaProperties(dataMap.get(ZylabLoadFileColumns.MULTIMEDIA_PROPERTIES));
        zylabFileMetadata.setReviewedAnalysis(dataMap.get(ZylabLoadFileColumns.REVIEWED_ANALYSIS));
        zylabFileMetadata.setLastReviewedBy(dataMap.get(ZylabLoadFileColumns.LAST_REVIEWED_BY));
        zylabFileMetadata.setSource(dataMap.get(ZylabLoadFileColumns.SOURCE));
        zylabFileMetadata.setExemptWithheldReason(dataMap.get(ZylabLoadFileColumns.EXEMPT_WITHHELD_REASON));
        zylabFileMetadata.setExemptWithheld(Boolean.valueOf(dataMap.get(ZylabLoadFileColumns.EXEMPT_WITHHELD)));

        return zylabFileMetadata;
    }

    public static List<ZylabFile> linkMetadataToZylabFiles(List<File> productionFiles, List<ZylabFileMetadata> zylabFileMetadataList)
            throws ZylabProductionSyncException
    {
        List<ZylabFile> zylabFiles = new ArrayList<>();

        for (ZylabFileMetadata zylabFileMetadata : zylabFileMetadataList)
        {
            Long zylabId = zylabFileMetadata.getZylabId();
            String zylabName = zylabFileMetadata.getName();
            if (zylabId == null)
            {
                throw new ZylabProductionSyncException(
                        "ZyLAB_ID field not set in load file for entry " + zylabFileMetadata);
            }
            else if (zylabName == null)
            {
                throw new ZylabProductionSyncException(
                        "Name field not set in load file for entry " + zylabFileMetadata);
            }

            productionFiles.stream()
                    .filter(file -> file.getName().startsWith(zylabName))
                    .map(file -> new ZylabFile(file, zylabFileMetadata))
                    .forEach(zylabFiles::add);
        }
        if (zylabFiles.isEmpty())
        {
            throw new ZylabProductionSyncException(
                    "No files found in production. Files in production must be named with the name field (FamilyID_ZylabID)");
        }
        return zylabFiles;
    }

    private static LocalDateTime parseDate(String dateString)
    {
        try
        {
            Date date = dateFormatter.parse(dateString);
            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        }
        catch (ParseException e)
        {
            log.warn("Date field sent from ZyLAB couldn't be parsed");
            return null;
        }
    }

}
