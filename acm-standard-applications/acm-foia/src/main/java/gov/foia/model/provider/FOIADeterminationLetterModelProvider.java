package gov.foia.model.provider;

import com.armedia.acm.core.provider.TemplateModelProvider;
import com.armedia.acm.services.billing.exception.GetBillingInvoiceException;
import com.armedia.acm.services.billing.model.BillingInvoice;
import com.armedia.acm.services.billing.service.BillingService;
import com.armedia.acm.services.config.lookups.model.StandardLookupEntry;
import com.armedia.acm.services.config.lookups.service.LookupDao;
import com.armedia.acm.services.exemption.exception.GetExemptionCodeException;
import com.armedia.acm.services.exemption.model.ExemptionCode;
import com.armedia.acm.services.labels.service.TranslationService;
import com.armedia.acm.services.participants.utils.ParticipantUtils;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import gov.foia.model.FOIADeterminationLetterCorrespondence;
import gov.foia.model.FOIARequest;
import gov.foia.service.FOIAExemptionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FOIADeterminationLetterModelProvider implements TemplateModelProvider<FOIADeterminationLetterCorrespondence>
{

    private FOIAExemptionService foiaExemptionService;
    private transient final Logger log = LogManager.getLogger(getClass());
    private BillingService billingService;
    private UserDao userDao;
    private LookupDao lookupDao;
    private TranslationService translationService;

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
        String exemptionCodesNames = exemptionCodes.stream()
                .map(ExemptionCode::getExemptionCode)
                .collect(Collectors.joining("and"));
        determinationLetterCorrespondence.setExemptionCodeSummary(exemptionCodesNames);

        List<StandardLookupEntry> lookupEntries = (List<StandardLookupEntry>) getLookupDao().getLookupByName("annotationTags").getEntries();
        Map<String, String> codeDescriptions = lookupEntries.stream().collect(Collectors.toMap(StandardLookupEntry::getKey, StandardLookupEntry::getValue));
        String exemptionCodesAndDescription = exemptionCodes.stream()
                .map(code -> String.format("%s: %s.", code.getExemptionCode(), labelValue(codeDescriptions.get(code.getExemptionCode()))))
                .collect(Collectors.joining("\n"));
        determinationLetterCorrespondence.setExemptionCodesAndDescription(exemptionCodesAndDescription);

        String requestAssignee = ParticipantUtils.getAssigneeIdFromParticipants(request.getParticipants());
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
        determinationLetterCorrespondence.setRequestAssigneeName(requestAssigneeName);
        determinationLetterCorrespondence.setRequestAssigneeTitle(requestAssigneeTitle);
        determinationLetterCorrespondence.setRequestAssigneeEmail(requestAssigneeEmail);

        determinationLetterCorrespondence.setCaseNumber(request.getCaseNumber());
        determinationLetterCorrespondence.setReceivedDate(request.getReceivedDate());
        determinationLetterCorrespondence.setPerfectedDate(request.getPerfectedDate());

        try
        {
            List<BillingInvoice> billingInvoices = billingService.getBillingInvoicesByParentObjectTypeAndId(request.getObjectType(), request.getId());
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

    private String labelValue(String labelKey)
    {
        return translationService.translate(labelKey);
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

    public LookupDao getLookupDao()
    {
        return lookupDao;
    }

    public void setLookupDao(LookupDao lookupDao)
    {
        this.lookupDao = lookupDao;
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
