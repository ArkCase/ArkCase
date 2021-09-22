package gov.foia.model.provider;

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

import com.armedia.acm.core.provider.TemplateModelProvider;
import com.armedia.acm.correspondence.exception.CorrespondenceTemplateMissingAssigneeException;
import com.armedia.acm.services.billing.exception.GetBillingInvoiceException;
import com.armedia.acm.services.billing.model.BillingInvoice;
import com.armedia.acm.services.billing.service.BillingService;
import com.armedia.acm.services.exemption.exception.GetExemptionCodeException;
import com.armedia.acm.services.exemption.model.ExemptionCode;
import com.armedia.acm.services.participants.utils.ParticipantUtils;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import gov.foia.model.FOIADeterminationLetterCorrespondence;
import gov.foia.model.FOIARequest;
import gov.foia.service.FOIAExemptionService;

public class FOIADeterminationLetterModelProvider implements TemplateModelProvider<FOIADeterminationLetterCorrespondence>
{

    private FOIAExemptionService foiaExemptionService;
    private transient final Logger log = LogManager.getLogger(getClass());
    private BillingService billingService;
    private UserDao userDao;
    private FOIATemplateModelExemption foiaTemplateModelExemption;

    @Override
    public FOIADeterminationLetterCorrespondence getModel(Object foiaRequest)
    {
        FOIARequest request = (FOIARequest) foiaRequest;

        FOIADeterminationLetterCorrespondence determinationLetterCorrespondence = new FOIADeterminationLetterCorrespondence();
        determinationLetterCorrespondence.setPersonAssociations(request.getPersonAssociations());
        List<ExemptionCode> exemptionCodes;
        try
        {
            exemptionCodes = foiaExemptionService.getExemptionCodes(request.getId(), request.getObjectType());
        }
        catch (GetExemptionCodeException e)
        {
            log.warn("Failed to fetch exemption codes for object with type [{}] and id [{}]", request.getObjectType(), request.getId());
            exemptionCodes = new ArrayList<>();
        }

        determinationLetterCorrespondence.setExemptionCodeSummary(getFoiaTemplateModelExemption().exemptionCodesAndSummary(exemptionCodes));
        determinationLetterCorrespondence.setExemptionCodesAndDescription(getFoiaTemplateModelExemption().exemptionCodesAndDescription(exemptionCodes));

        String requestAssignee = ParticipantUtils.getAssigneeIdFromParticipants(request.getParticipants());
        if (requestAssignee == null)
        {
            throw new CorrespondenceTemplateMissingAssigneeException("Assignee is Required");
        }
        AcmUser acmUser = userDao.findByUserId(requestAssignee);
        String requestAssigneeName = null;
        String requestAssigneeTitle = null;
        String requestAssigneeEmail = null;
        if (acmUser != null)
        {
            requestAssigneeName = acmUser.getFullName();
            requestAssigneeTitle = acmUser.getTitle();
            requestAssigneeEmail = acmUser.getMail();
        }
        determinationLetterCorrespondence.setAssigneeName(requestAssigneeName);
        determinationLetterCorrespondence.setAssigneeTitle(requestAssigneeTitle);
        determinationLetterCorrespondence.setAssigneeEmail(requestAssigneeEmail);

        determinationLetterCorrespondence.setRequestCaseNumber(request.getCaseNumber());
        determinationLetterCorrespondence.setReceivedDate(request.getReceivedDate());
        determinationLetterCorrespondence.setPerfectedDate(request.getPerfectedDate());

        try
        {
            List<BillingInvoice> billingInvoices = billingService.getBillingInvoicesByParentObjectTypeAndId(request.getObjectType(),
                    request.getId());
            double sum = billingInvoices.stream().map(BillingInvoice::getBillingInvoiceAmount).mapToDouble(Double::doubleValue).sum();
            determinationLetterCorrespondence.setInvoiceAmount(sum);
        }
        catch (GetBillingInvoiceException e)
        {
            log.warn("Failed to get billing invoices for object with type [{}] and id [{}]", request.getObjectType(), request.getId());
            determinationLetterCorrespondence.setInvoiceAmount(0);
        }
        return determinationLetterCorrespondence;
    }

    @Override
    public Class<FOIADeterminationLetterCorrespondence> getType()
    {
        return FOIADeterminationLetterCorrespondence.class;
    }

    public FOIAExemptionService getFoiaExemptionService()
    {
        return foiaExemptionService;
    }

    public void setFoiaExemptionService(FOIAExemptionService foiaExemptionService)
    {
        this.foiaExemptionService = foiaExemptionService;
    }

    public BillingService getBillingService()
    {
        return billingService;
    }

    public void setBillingService(BillingService billingService)
    {
        this.billingService = billingService;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public FOIATemplateModelExemption getFoiaTemplateModelExemption()
    {
        return foiaTemplateModelExemption;
    }

    public void setFoiaTemplateModelExemption(FOIATemplateModelExemption foiaTemplateModelExemption)
    {
        this.foiaTemplateModelExemption = foiaTemplateModelExemption;
    }
}
