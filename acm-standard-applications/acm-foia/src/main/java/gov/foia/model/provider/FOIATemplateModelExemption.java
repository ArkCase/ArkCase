package gov.foia.model.provider;

import com.armedia.acm.correspondence.model.FormattedMergeTerm;
import com.armedia.acm.correspondence.model.FormattedRun;
import com.armedia.acm.services.config.lookups.model.StandardLookupEntry;
import com.armedia.acm.services.config.lookups.service.LookupDao;
import com.armedia.acm.services.exemption.model.ExemptionCode;
import gov.foia.service.FOIAExemptionService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FOIATemplateModelExemption
{
    private FOIAExemptionService foiaExemptionService;
    private LookupDao lookupDao;

    protected String exemptionCodesAndSummary (List<ExemptionCode> exemptionCodes)
    {
        String exemptionCodesNames = exemptionCodes.stream()
                .map(ExemptionCode::getExemptionCode)
                .collect(Collectors.joining("and"));

        return exemptionCodesNames;
    }

    protected FormattedMergeTerm exemptionCodesAndDescription (List<ExemptionCode> exemptionCodes)
    {
        FormattedMergeTerm exemptionCodesAndDescription = new FormattedMergeTerm();
        List<FormattedRun> runs = getExemptionCodesAndDescriptionRuns(exemptionCodes);
        exemptionCodesAndDescription.setRuns(runs);
        return exemptionCodesAndDescription;
    }

    private List<FormattedRun> getExemptionCodesAndDescriptionRuns(List<ExemptionCode> exemptionCodes)
    {
        List<StandardLookupEntry> lookupEntries = (List<StandardLookupEntry>) getLookupDao().getLookupByName("annotationTags").getEntries();
        Map<String, String> codeDescriptions = lookupEntries.stream()
                .collect(Collectors.toMap(StandardLookupEntry::getKey, StandardLookupEntry::getValue));
        List<FormattedRun> runs = new ArrayList<>();
        for (ExemptionCode exCode : exemptionCodes)
        {
            foiaExemptionService.createAndStyleRunsForCorrespondenceLetters(codeDescriptions, runs, exCode);
        }
        return runs;
    }

    public FOIAExemptionService getFoiaExemptionService()
    {
        return foiaExemptionService;
    }

    public void setFoiaExemptionService(FOIAExemptionService foiaExemptionService)
    {
        this.foiaExemptionService = foiaExemptionService;
    }

    public LookupDao getLookupDao()
    {
        return lookupDao;
    }

    public void setLookupDao(LookupDao lookupDao)
    {
        this.lookupDao = lookupDao;
    }
}
