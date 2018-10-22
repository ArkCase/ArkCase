package gov.foia.service.dataupdate;

import com.armedia.acm.services.dataupdate.service.AcmDataUpdateExecutor;
import com.armedia.acm.services.dataupdate.service.ExtensionDataUpdateExecutors;

import java.util.List;

public class FoiaExtensionDataUpdateExecutor implements ExtensionDataUpdateExecutors
{

    private List<AcmDataUpdateExecutor> foiaDataUpdateExecutors;

    @Override
    public List<AcmDataUpdateExecutor> getExecutors()
    {
        return foiaDataUpdateExecutors;
    }

    public void setFoiaDataUpdateExecutors(List<AcmDataUpdateExecutor> foiaDataUpdateExecutors)
    {
        this.foiaDataUpdateExecutors = foiaDataUpdateExecutors;
    }
}
