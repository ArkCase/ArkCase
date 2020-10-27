package com.armedia.acm.services.exemption.service.impl;

/*-
 * #%L
 * ACM Service: Exemption
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

import com.armedia.acm.services.exemption.dao.ExemptionCodeDao;
import com.armedia.acm.services.exemption.exception.DeleteExemptionCodeException;
import com.armedia.acm.services.exemption.exception.SaveExemptionCodeException;
import com.armedia.acm.services.exemption.model.ExemptionCode;
import com.armedia.acm.services.exemption.model.ExemptionCodeAndStatuteEventPublisher;
import com.armedia.acm.services.exemption.model.ExemptionConstants;
import com.armedia.acm.services.exemption.service.ExemptionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ana.serafimoska
 */
public class ExemptionServiceImpl implements ExemptionService
{
    private Logger log = LogManager.getLogger(getClass());
    private ExemptionCodeDao exemptionCodeDao;
    private ExemptionCodeAndStatuteEventPublisher exemptionCodeAndStatuteEventPublisher;

    @Override
    @Transactional
    public List<ExemptionCode> saveExemptionCodes(ExemptionCode exemptionCodes, String user) throws SaveExemptionCodeException
    {

        log.info("Saving Exemption codes [{}]", exemptionCodes.getExemptionCodes());
        try
        {
            List<ExemptionCode> exemptionCodeList = new ArrayList<>();
            Date created = new Date();
            for (String code : exemptionCodes.getExemptionCodes())
            {
                ExemptionCode exemptionCodeObj = new ExemptionCode();
                exemptionCodeObj.setExemptionCode(code);
                exemptionCodeObj.setExemptionStatus(ExemptionConstants.EXEMPTION_STATUS_MANUAL);
                exemptionCodeObj.setManuallyFlag(true);
                exemptionCodeObj.setParentObjectId(exemptionCodes.getParentObjectId());
                exemptionCodeObj.setParentObjectType(exemptionCodes.getParentObjectType());
                exemptionCodeList.add(exemptionCodeObj);
                ExemptionCode saved = getExemptionCodeDao().save(exemptionCodeObj);
                getExemptionCodeAndStatuteEventPublisher().publishExemptionCodeCreatedEvent(saved);
            }
            return exemptionCodeList;
        }
        catch (Exception e)
        {
            log.error("Saving Exemption Codes [{}] failed", exemptionCodes.getExemptionCodes());
            throw new SaveExemptionCodeException("Unable to save exemption code [{}]" + exemptionCodes.getExemptionCodes(), e);
        }
    }

    @Override
    public void deleteExemptionCode(Long tagId) throws DeleteExemptionCodeException
    {
        log.info("Deleting exemption code with id: {}", tagId);
        try
        {
            ExemptionCode exemptionCode = getExemptionCodeDao().find(tagId);
            getExemptionCodeDao().deleteExemptionCode(tagId);
            getExemptionCodeAndStatuteEventPublisher().publishExemptionCodeDeletedEvent(exemptionCode);
        }
        catch (Exception e)
        {
            log.error("Delete failed for exemption code with id: {}", tagId);
            throw new DeleteExemptionCodeException("Unable to delete exemption code with id: {}" + tagId, e);
        }
    }

    public ExemptionCodeDao getExemptionCodeDao()
    {
        return exemptionCodeDao;
    }

    public void setExemptionCodeDao(ExemptionCodeDao exemptionCodeDao)
    {
        this.exemptionCodeDao = exemptionCodeDao;
    }

    public ExemptionCodeAndStatuteEventPublisher getExemptionCodeAndStatuteEventPublisher()
    {
        return exemptionCodeAndStatuteEventPublisher;
    }

    public void setExemptionCodeAndStatuteEventPublisher(ExemptionCodeAndStatuteEventPublisher exemptionCodeAndStatuteEventPublisher)
    {
        this.exemptionCodeAndStatuteEventPublisher = exemptionCodeAndStatuteEventPublisher;
    }
}
