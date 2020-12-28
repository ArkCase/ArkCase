package gov.foia.model.provider;

import com.armedia.acm.core.provider.TemplateModelProvider;
import com.armedia.acm.plugins.task.model.AcmTask;
import gov.foia.dao.FOIARequestDao;
import gov.foia.model.FOIARequest;
import gov.foia.model.FOIATaskRequestModel;

public class FOIATaskRequestTemplateModelProvider implements TemplateModelProvider<FOIATaskRequestModel>
{

    private FOIARequestDao foiaRequestDao;

    @Override
    public FOIATaskRequestModel getModel(Object object)
    {
        AcmTask task = (AcmTask) object;
        FOIATaskRequestModel model = new FOIATaskRequestModel();

        if(task.getParentObjectId() != null)
        {
            FOIARequest request = foiaRequestDao.find(task.getParentObjectId());
            if(request != null)
            {
                model.setRequest(request);
            }
        }

        model.setTask(task);

        return model;
    }

    @Override
    public Class<FOIATaskRequestModel> getType()
    {
        return FOIATaskRequestModel.class;
    }

    public FOIARequestDao getFoiaRequestDao()
    {
        return foiaRequestDao;
    }

    public void setFoiaRequestDao(FOIARequestDao foiaRequestDao)
    {
        this.foiaRequestDao = foiaRequestDao;
    }
}
