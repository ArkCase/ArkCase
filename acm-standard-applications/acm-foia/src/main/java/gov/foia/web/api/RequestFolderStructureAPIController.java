package gov.foia.web.api;

import gov.foia.service.RequestFolderStructureService;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping({ "/api/v1/plugin/request/folderstructure", "/api/latest/plugin/request/folderstructure" })
public class RequestFolderStructureAPIController {
    private RequestFolderStructureService requestFolderStructureService;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<String> requestFolderStructure(HttpSession session, Authentication auth) {


        return getRequestFolderStructureService().getFolderStructure();
    }

    public RequestFolderStructureService getRequestFolderStructureService() {
        return requestFolderStructureService;
    }

    public void setRequestFolderStructureService(RequestFolderStructureService requestFolderStructureService) {
        this.requestFolderStructureService = requestFolderStructureService;
    }
}
