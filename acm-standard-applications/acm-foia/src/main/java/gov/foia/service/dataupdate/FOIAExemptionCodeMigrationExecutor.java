package gov.foia.service.dataupdate;

import com.armedia.acm.services.dataupdate.service.AcmDataUpdateExecutor;

import gov.foia.service.FOIAExemptionService;

/**
 * Created by ana.serafimoska
 */
public class FOIAExemptionCodeMigrationExecutor implements AcmDataUpdateExecutor
{

    private FOIAExemptionService foiaExemptionService;

    @Override
    public String getUpdateId()
    {
        return "foia-exemption-code-migration-executor";
    }

    @Override
    public void execute()
    {
        getFoiaExemptionService().migrateExemptionCodes();
    }

    public FOIAExemptionService getFoiaExemptionService()
    {
        return foiaExemptionService;
    }

    public void setFoiaExemptionService(FOIAExemptionService foiaExemptionService)
    {
        this.foiaExemptionService = foiaExemptionService;
    }
}
