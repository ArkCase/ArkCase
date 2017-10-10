package com.armedia.acm.calendar.service.integration.exchange.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.service.outlook.service.OutlookRecreateableCalendarService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Aug 31, 2017
 *
 */
@Controller
@RequestMapping({ "/api/v1/service/calendar/exchange", "/api/latest/service/calendar/exchange" })
public class AcmExchangeCalendarAPIController
{

    private OutlookRecreateableCalendarService recreatableCalendarService;

    @RequestMapping(path = "/folders/recreated", method = RequestMethod.PUT)
    public ResponseEntity<AcmContainer> clearFolderRecreatedFlag(@RequestBody RecreatedEntity recreatedEntity)
            throws AcmObjectNotFoundException
    {
        AcmContainer container = recreatableCalendarService.clearFolderRecreatedFlag(recreatedEntity.getObjectType(),
                recreatedEntity.getObjectId());
        return ResponseEntity.status(HttpStatus.OK).body(container);
    }

    /**
     * @param recreatableCalendarService
     *            the recreatableCalendarService to set
     */
    public void setRecreatableCalendarService(OutlookRecreateableCalendarService recreatableCalendarService)
    {
        this.recreatableCalendarService = recreatableCalendarService;
    }

}
