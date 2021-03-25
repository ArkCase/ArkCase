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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.armedia.acm.tool.zylab.service.ZylabProductionFileExtractor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.armedia.acm.services.zylab.model.ZylabFile;
import com.armedia.acm.services.zylab.model.ZylabFileMetadata;

/**
 * Created by Aleksandar Acevski <aleksandar.acevski@armedia.com> on March, 2021
 */
public class ZylabProductionUtils
{
    private final static Logger log = LogManager.getLogger(ZylabProductionUtils.class);

    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public static List<ZylabFileMetadata> getFileMetadataFromLoadFile(File loadFile, Long matterId, String productionKey)
            throws IOException
    {
        List<ZylabFileMetadata> zylabFileMetadataList = new ArrayList<>();

        try (CSVParser parser = new CSVParser(new InputStreamReader(new BOMInputStream(new FileInputStream(loadFile)),
                StandardCharsets.UTF_8), CSVFormat.DEFAULT.withFirstRecordAsHeader()))
        {
            List<CSVRecord> records = parser.getRecords();
            List<String> headers = parser.getHeaderNames();

            for (CSVRecord record : records)
            {
                Map<String, String> map = new LinkedHashMap<>();
                for (int j = 0; j < headers.size(); j++)
                {
                    map.put(headers.get(j), record.get(j));
                }
                ZylabFileMetadata zylabFileMetadata = mapZylabFileMetadata(map, matterId, productionKey);
                zylabFileMetadataList.add(zylabFileMetadata);
            }
        }
        return zylabFileMetadataList;
    }

    public static ZylabFileMetadata mapZylabFileMetadata(Map<String, String> dataMap, Long matterId, String productionKey)
    {
        ZylabFileMetadata zylabFileMetadata = new ZylabFileMetadata();

        zylabFileMetadata.setMatterId(matterId);
        zylabFileMetadata.setProductionKey(productionKey);

        if (NumberUtils.isParsable(dataMap.get("ZyLAB_ID")))
        {
            zylabFileMetadata.setZylabId(Long.valueOf(dataMap.get("ZyLAB_ID")));
        }
        zylabFileMetadata.setBegBatesNumber(dataMap.get("Beg_BatesNumber"));
        zylabFileMetadata.setEndBatesNumber(dataMap.get("End_BatesNumber"));
        if (NumberUtils.isParsable(dataMap.get("Produced_Pages")))
        {
            zylabFileMetadata.setProducedPages(Integer.valueOf(dataMap.get("Produced_Pages")));
        }
        zylabFileMetadata.setProductionCreateDate(parseDate(dataMap.get("Production_CreateDate")));
        zylabFileMetadata.setContainsRedaction(Boolean.valueOf(dataMap.get("Contains_Redaction")));
        zylabFileMetadata.setRedactionCode1(dataMap.get("RedactionCode1"));
        zylabFileMetadata.setRedactionCode2(dataMap.get("RedactionCode2"));
        zylabFileMetadata.setRedactionJustification(dataMap.get("RedactionJustification"));
        zylabFileMetadata.setCustodian(dataMap.get("Custodian"));
        zylabFileMetadata.setDocName(dataMap.get("Doc_Name"));
        if (NumberUtils.isParsable(dataMap.get("Doc_PageCount")))
        {
            zylabFileMetadata.setDocPageCount(Integer.valueOf(dataMap.get("Doc_PageCount")));
        }
        zylabFileMetadata.setDocDate(parseDate(dataMap.get("Doc_Date")));
        zylabFileMetadata.setDocExt(dataMap.get("Doc_Ext"));
        if (NumberUtils.isParsable(dataMap.get("Doc_Size")))
        {
            zylabFileMetadata.setDocSize(Long.valueOf(dataMap.get("Doc_Size")));
        }
        zylabFileMetadata.setHasAttachment(Boolean.valueOf(dataMap.get("Has_Attachment")));
        zylabFileMetadata.setAttachment(Boolean.valueOf(dataMap.get("Is_Attachmentt")));
        zylabFileMetadata.setEmailFrom(dataMap.get("Email_From"));
        zylabFileMetadata.setEmailRecipient(dataMap.get("Email_Recipient"));
        if (NumberUtils.isParsable(dataMap.get("Multimedia_Duration(Sec)")))
        {
            zylabFileMetadata.setMultimediaDurationSec(Integer.valueOf(dataMap.get("Multimedia_Duration(Sec)")));
        }
        zylabFileMetadata.setMultimediaProperties(dataMap.get("Multimedia_properties"));
        zylabFileMetadata.setReviewedAnalysis(dataMap.get("Reviewed_Analysis"));
        zylabFileMetadata.setLastReviewedBy(dataMap.get("LastRevieweBy"));
        zylabFileMetadata.setSource(dataMap.get("Source"));
        zylabFileMetadata.setExemptWithheldReason(dataMap.get("Exempt_Withheld_Reason"));

        return zylabFileMetadata;
    }

    public static List<ZylabFile> linkMetadataToZylabFiles(List<File> productionFiles, List<ZylabFileMetadata> zylabFileMetadataList)
    {

        List<ZylabFile> zylabFiles = new ArrayList<>();

        for (ZylabFileMetadata zylabFileMetadata : zylabFileMetadataList)
        {
            String fileNameInSentFolder = String.valueOf(zylabFileMetadata.getZylabId());

            productionFiles.stream()
                    .filter(file -> file.getName().startsWith(fileNameInSentFolder))
                    .map(file -> new ZylabFile(file, zylabFileMetadata))
                    .forEach(zylabFiles::add);
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
