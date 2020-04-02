package com.armedia.acm.plugins.admin.web.api;

/*-
 * #%L
 * ACM Default Plugin: admin
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
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

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.exception.AcmFolderException;
import com.armedia.acm.plugins.ecm.exception.LinkAlreadyExistException;
import com.armedia.acm.plugins.ecm.model.AcmFolderConstants;
import com.armedia.acm.plugins.ecm.model.RecycleBinDTO;
import com.armedia.acm.plugins.ecm.model.RecycleBinItemDTO;
import com.armedia.acm.plugins.ecm.service.RecycleBinItemService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;

/**
 * @author darko.dimitrievski
 */

@Controller
@RequestMapping({ "/api/v1/service/recycleBin", "/api/latest/service/recycleBin" })
public class RecycleBinItemController
{
    private transient final Logger log = LogManager.getLogger(getClass());
    private RecycleBinItemService recycleBinItemService;

    @RequestMapping(method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    public RecycleBinDTO findRecycleBinItems(Authentication authentication,
            @RequestParam(value = "start", required = false, defaultValue = "0") int start,
            @RequestParam(value = "n", required = false, defaultValue = "5") int maxRows,
            @RequestParam(value = "sortBy", required = false, defaultValue = "modified_date_tdt") String sortBy,
            @RequestParam(value = "sortDir", required = false, defaultValue = "desc") String sortDir)
    {
        try
        {
            return recycleBinItemService.findRecycleBinItems(authentication, sortBy, sortDir, start, maxRows);
        }
        catch (Exception e)
        {
            log.error("No recycle bin items found, reason {}", e.getMessage());
            return new RecycleBinDTO(0, new ArrayList<>());
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public void restoreItemsFromRecycleBin(@RequestBody List<RecycleBinItemDTO> filesToBeRestored, Authentication authentication)
            throws AcmUserActionFailedException,
            AcmObjectNotFoundException, AcmCreateObjectFailedException, AcmFolderException, LinkAlreadyExistException {
        recycleBinItemService.restoreItemsFromRecycleBin(filesToBeRestored, authentication);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    @ResponseBody
    public void removeItemsFromRecycleBin(@RequestBody List<RecycleBinItemDTO> filesToBeDeleted,
            Authentication authentication, HttpSession session) throws AcmUserActionFailedException, AcmObjectNotFoundException
    {
        String ipAddress = (String) session.getAttribute(AcmFolderConstants.IP_ADDRESS_ATTRIBUTE);

        for (RecycleBinItemDTO recycleBinItemDTO : filesToBeDeleted)
        {
            getRecycleBinItemService().deleteRecycleBinItemPermanently(recycleBinItemDTO, authentication, ipAddress);
        }
    }

    public RecycleBinItemService getRecycleBinItemService()
    {
        return recycleBinItemService;
    }

    public void setRecycleBinItemService(RecycleBinItemService recycleBinItemService)
    {
        this.recycleBinItemService = recycleBinItemService;
    }
}
