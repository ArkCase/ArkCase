package com.armedia.acm.plugins.casefile.web.api;

import com.armedia.acm.plugins.casefile.service.CaseFileFolderStructureConfigService;
import org.json.JSONArray;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping({ "/api/v1/plugin/casefile/folderstructure/config", "/api/latest/plugin/casefile/folderstructure/config" })
public class CaseFileFolderStructureAPIController {

    private CaseFileFolderStructureConfigService caseFileFolderStructureConfigService;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<String> CaseFileFolderStructure(HttpSession session, Authentication auth) {


    return getCaseFileFolderStructureConfigService().getFolderStructure();
    }

    public CaseFileFolderStructureConfigService getCaseFileFolderStructureConfigService() {
        return caseFileFolderStructureConfigService;
    }

    public void setCaseFileFolderStructureConfigService(CaseFileFolderStructureConfigService caseFileFolderStructureConfigService) {
        this.caseFileFolderStructureConfigService = caseFileFolderStructureConfigService;
    }
}
