package gov.foia.service;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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

import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.services.exemption.exception.GetExemptionCodeException;
import com.armedia.acm.services.exemption.model.ExemptionCode;
import com.armedia.acm.services.exemption.model.ExemptionConstants;
import com.armedia.acm.services.labels.service.TranslationService;
import gov.foia.dao.FOIAExemptionCodeDao;
import gov.foia.dao.FOIAExemptionStatuteDao;
import gov.foia.model.ExemptionStatute;
import gov.foia.model.FormattedRun;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import gov.foia.dao.FOIAExemptionCodeDao;
import gov.foia.dao.FOIAExemptionStatuteDao;
import gov.foia.model.ExemptionStatute;
import com.armedia.acm.correspondence.model.FormattedRun;

/**
 * Created by ana.serafimoska
 */
public class FOIAExemptionService
{

    private Logger log = LogManager.getLogger(getClass());
    private FOIAExemptionCodeDao foiaExemptionCodeDao;
    private FOIAExemptionStatuteDao foiaExemptionStatuteDao;
    private TranslationService translationService;

    public List<ExemptionCode> getExemptionCodes(Long parentObjectId, String parentObjectType) throws GetExemptionCodeException
    {
        log.info("Finding  exemption codes for objectId: {}", parentObjectId);
        try
        {
            List<ExemptionCode> combineResult = new ArrayList<>();
            List<ExemptionCode> listCodesOnDocuments = getFoiaExemptionCodeDao().getExemptionCodesByParentObjectIdAndType(parentObjectId,
                    parentObjectType);
            List<ExemptionCode> filterDocumentCodesList = filterExemptionCodes(listCodesOnDocuments);
            combineResult.addAll(filterDocumentCodesList);

            List<ExemptionCode> listCodesOnRequest = getFoiaExemptionCodeDao().getManuallyAddedCodesOnRequestLevel(parentObjectId,
                    parentObjectType);
            combineResult.addAll(listCodesOnRequest);
            List<ExemptionCode> finalList = filterExemptionCodes(combineResult);
            return finalList;
        }
        catch (Exception e)
        {
            log.error("Finding  exemption codes for objectId: {} failed", parentObjectId);
            throw new GetExemptionCodeException("Unable to get exemption codes for objectId: {}" + parentObjectId, e);
        }
    }

    @Transactional
    public void copyFileWithExemptionCodesAndStatutes(EcmFile originalFile, EcmFile copiedFile)
    {
        Long copiedFileId = copiedFile.getFileId();
        List<ExemptionCode> exemptionCodeList = getFoiaExemptionCodeDao().findExemptionCodesByFileId(originalFile.getFileId());
        List<ExemptionStatute> exemptionStatuteList = getFoiaExemptionStatuteDao().findExemptionStatutesByFileId(originalFile.getFileId());

        for (ExemptionCode exemptionCode : exemptionCodeList)
        {
            ExemptionCode exemptionCodeObj = new ExemptionCode();
            exemptionCodeObj.setParentObjectType(exemptionCode.getParentObjectType());
            exemptionCodeObj.setExemptionCode(exemptionCode.getExemptionCode());
            exemptionCodeObj.setExemptionCodeNumber(exemptionCode.getExemptionCodeNumber());
            exemptionCodeObj.setExemptionStatus(exemptionCode.getExemptionStatus());
            exemptionCodeObj.setFileId(copiedFileId);
            exemptionCodeObj.setFileVersion(exemptionCode.getFileVersion());
            exemptionCodeObj.setManuallyFlag(exemptionCode.getManuallyFlag());
            getFoiaExemptionCodeDao().save(exemptionCodeObj);
        }

        for (ExemptionStatute exemptionStatute : exemptionStatuteList)
        {
            ExemptionStatute exStatuteObj = new ExemptionStatute();
            exStatuteObj.setParentObjectType(exemptionStatute.getParentObjectType());
            exStatuteObj.setExemptionStatute(exemptionStatute.getExemptionStatute());
            exStatuteObj.setExemptionStatus(exemptionStatute.getExemptionStatus());
            exStatuteObj.setFileId(copiedFileId);
            exStatuteObj.setFileVersion(exemptionStatute.getFileVersion());
            getFoiaExemptionStatuteDao().save(exStatuteObj);
        }
    }

    @Transactional
    public void migrateExemptionCodes()
    {
        Query query = getFoiaExemptionCodeDao().queryExistingCodes();

        List<Object[]> resultList = query.getResultList();
        for (Object[] record : resultList)
        {
            ExemptionCode exemptionCodeObj = new ExemptionCode();
            exemptionCodeObj.setParentObjectType("DOCUMENT");
            exemptionCodeObj.setExemptionCode((String) record[0]);
            exemptionCodeObj.setExemptionStatus((String) record[1]);
            exemptionCodeObj.setCreator((String) record[2]);
            exemptionCodeObj.setCreated((Date) record[3]);
            exemptionCodeObj.setFileId((Long) record[4]);
            exemptionCodeObj.setFileVersion((String) record[5]);
            exemptionCodeObj.setManuallyFlag((Boolean) record[6]);
            getFoiaExemptionCodeDao().save(exemptionCodeObj);
        }

    }

    public List<ExemptionCode> filterExemptionCodes(List<ExemptionCode> exemptionCodeList)
    {
        Map<String, List<ExemptionCode>> codes = new HashMap<>();
        exemptionCodeList.forEach(it -> {
            List<ExemptionCode> codesForCodeName = codes.getOrDefault(it.getExemptionCode(), new ArrayList<>());
            codesForCodeName.add(it);
            codes.put(it.getExemptionCode(), codesForCodeName);
        });

        List<ExemptionCode> resultList = codes.values()
                .stream()
                .map(codeList -> {
                    return codeList.stream()
                            .filter(it -> it.getExemptionStatus().equals(ExemptionConstants.EXEMPTION_STATUS_APPROVED))
                            .findFirst()
                            .orElse(codeList.get(0));
                }).collect(Collectors.toList());

        resultList.sort(Comparator.comparing(ExemptionCode::getExemptionCode));

        return resultList;
    }

    public void createAndStyleRunsForCorrespondenceLetters(Map<String, String> codeDescriptions, List<FormattedRun> runs, ExemptionCode exCode)
    {
        runs.add(new FormattedRun());
        FormattedRun exemptionCodeRun = new FormattedRun();
        exemptionCodeRun.setText(exCode.getExemptionCode());
        exemptionCodeRun.setBold(true);
        exemptionCodeRun.setCapitalized(true);
        runs.add(exemptionCodeRun);
        runs.add(new FormattedRun());
        FormattedRun exemptionDescriptionRun = new FormattedRun();
        exemptionDescriptionRun.setText(labelValue(codeDescriptions.get(exCode.getExemptionCode())));
        exemptionDescriptionRun.setFontSize(11);
        runs.add(exemptionDescriptionRun);
        FormattedRun exemptionLine = new FormattedRun();
        exemptionLine.setText("--------------------------------------------------------------------------------------------------------------------");
        exemptionLine.setFontSize(11);
        runs.add(exemptionLine);
    }

    private String labelValue(String labelKey)
    {
        return translationService.translate(labelKey);
    }


    public boolean hasExemptionOnAnyDocumentsOnRequest(Long objectId, String objectType)
    {
        return foiaExemptionCodeDao.hasExemptionOnAnyDocumentsOnRequest(objectId, objectType);
    }

    public FOIAExemptionCodeDao getFoiaExemptionCodeDao()
    {
        return foiaExemptionCodeDao;
    }

    public void setFoiaExemptionCodeDao(FOIAExemptionCodeDao foiaExemptionCodeDao)
    {
        this.foiaExemptionCodeDao = foiaExemptionCodeDao;
    }

    public FOIAExemptionStatuteDao getFoiaExemptionStatuteDao()
    {
        return foiaExemptionStatuteDao;
    }

    public void setFoiaExemptionStatuteDao(FOIAExemptionStatuteDao foiaExemptionStatuteDao)
    {
        this.foiaExemptionStatuteDao = foiaExemptionStatuteDao;
    }

    public TranslationService getTranslationService()
    {
        return translationService;
    }

    public void setTranslationService(TranslationService translationService)
    {
        this.translationService = translationService;
    }
}
