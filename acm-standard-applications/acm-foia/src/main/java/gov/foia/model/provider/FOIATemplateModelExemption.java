package gov.foia.model.provider;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
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

    public String exemptionCodesAndSummary (List<ExemptionCode> exemptionCodes)
    {
        String exemptionCodesNames = exemptionCodes.stream()
                .map(ExemptionCode::getExemptionCode)
                .collect(Collectors.joining("and"));

        return exemptionCodesNames;
    }

    public FormattedMergeTerm exemptionCodesAndDescription (List<ExemptionCode> exemptionCodes)
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
