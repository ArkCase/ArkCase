package gov.foia.web.api;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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


import com.armedia.acm.services.exemption.exception.GetExemptionStatuteException;
import com.armedia.acm.services.exemption.model.ExemptionStatute;
import gov.foia.service.FOIAExemptionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping({ "/api/v1/service/exemption-statute", "/api/latest/service/exemption-statute" })
public class FOIAExemptionStatuteAPIController {

    private final Logger log = LogManager.getLogger(getClass());
    private FOIAExemptionService foiaExemptionService;

    @RequestMapping(value = "/{parentObjectId}/{parentObjectType}/statutes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<ExemptionStatute> getExemptionStatutes(@PathVariable Long parentObjectId, @PathVariable String parentObjectType)
            throws GetExemptionStatuteException {
        return getFoiaExemptionService().getExemptionStatutes(parentObjectId, parentObjectType);
    }

    public FOIAExemptionService getFoiaExemptionService() {
        return foiaExemptionService;
    }

    public void setFoiaExemptionService(FOIAExemptionService foiaExemptionService) {
        this.foiaExemptionService = foiaExemptionService;
    }
}
