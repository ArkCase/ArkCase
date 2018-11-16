package gov.foia.web.api;

import gov.foia.model.FoiaConfiguration;
import gov.foia.service.FoiaConfigurationService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping({"api/v1/service/foia/configuration", "api/latest/service/foia/configuration"})
public class FoiaConfigurationAPIController {

    private FoiaConfigurationService foiaConfigurationService;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public void updateFoiaConfigurationFile(@RequestBody FoiaConfiguration foiaConfiguration)
    {
        foiaConfigurationService.writeConfiguration(foiaConfiguration);
    }


    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public FoiaConfiguration getFoiaConfigurationFile()
    {
        return getFoiaConfigurationService().readConfiguration();
    }

    public FoiaConfigurationService getFoiaConfigurationService()
    {
        return foiaConfigurationService;
    }

    public void setFoiaConfigurationService(FoiaConfigurationService foiaConfigurationService)
    {
        this.foiaConfigurationService = foiaConfigurationService;
    }

}
