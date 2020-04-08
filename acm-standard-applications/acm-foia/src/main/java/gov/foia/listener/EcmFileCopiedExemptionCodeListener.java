package gov.foia.listener;

import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.event.EcmFileCopiedEvent;

import org.springframework.context.ApplicationListener;

import gov.foia.model.FOIAFile;
import gov.foia.service.FOIAExemptionService;

public class EcmFileCopiedExemptionCodeListener implements ApplicationListener<EcmFileCopiedEvent>
{
    private FOIAExemptionService foiaExemptionService;

    @Override
    public void onApplicationEvent(EcmFileCopiedEvent event)
    {
        if (event != null && event.isSucceeded() && event.getOriginal() != null)
        {
            System.out.printf("EcmFileCopiedExemptionCodeListener called");
            EcmFile copy = (EcmFile) event.getSource();
            FOIAFile original = (FOIAFile) event.getOriginal();
            if (original != null && original.getExemptionCodes().size() > 0)
            {
                getFoiaExemptionService().copyFileWithExemptionCodes(original, copy);
            }

        }
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
