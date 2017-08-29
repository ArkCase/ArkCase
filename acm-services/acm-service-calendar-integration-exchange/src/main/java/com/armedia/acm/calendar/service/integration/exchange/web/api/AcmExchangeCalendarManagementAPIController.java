package com.armedia.acm.calendar.service.integration.exchange.web.api;

import com.armedia.acm.service.outlook.dao.AcmOutlookFolderCreatorDao;
import com.armedia.acm.service.outlook.dao.AcmOutlookFolderCreatorDaoException;
import com.armedia.acm.service.outlook.model.AcmOutlookFolderCreator;
import com.armedia.acm.service.outlook.service.OutlookCalendarAdminServiceExtension;
import com.armedia.acm.service.outlook.service.OutlookCalendarAdminServiceExtension.RecreateFoldersCallbackPayload;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Aug 15, 2017
 *
 */
@Controller
@RequestMapping({ "/api/v1/service/calendar/exchange/configure", "/api/latest/service/calendar/exchange/configure" })
public class AcmExchangeCalendarManagementAPIController
{
    private AcmOutlookFolderCreatorDao folderCreatorDao;

    private OutlookCalendarAdminServiceExtension calendarAdminService;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AcmOutlookFolderCreator> checkFolderCreatorCredentials()
    {
        List<AcmOutlookFolderCreator> invalidUsers = folderCreatorDao.getFolderCreatorsWithInvalidCredentials();
        return invalidUsers;
    }

    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<?> updateConfiguration(@RequestBody AcmOutlookFolderCreator updatedCreator)
            throws AcmOutlookFolderCreatorDaoException
    {
        boolean shouldRecreate = folderCreatorDao.updateFolderCreator(updatedCreator);
        if (shouldRecreate)
        {
            calendarAdminService.recreateFolders(updatedCreator);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(path = "/{creatorId}/{systemEmail}/{systemPassword}", method = RequestMethod.GET, produces = "text/event-stream")
    public SseEmitter interactiveUpdateConfiguration(@PathVariable("creatorId") Long id, @PathVariable("systemEmail") String systemEmail,
            @PathVariable("systemPassword") String systemPassword) throws AcmOutlookFolderCreatorDaoException
    {

        SseEmitter emitter = new SseEmitter();

        AcmOutlookFolderCreator updatedCreator = new AcmOutlookFolderCreator(systemEmail, systemPassword);
        updatedCreator.setId(id);

        performRecreateFolder(emitter, updatedCreator);

        return emitter;
    }

    private void performRecreateFolder(SseEmitter emitter, AcmOutlookFolderCreator updatedCreator)
            throws AcmOutlookFolderCreatorDaoException
    {
        calendarAdminService.recreateFolders(updatedCreator, (totalToProcess) -> {
            emitter.send(SseEmitter.event().data(new RecreateFoldersCallbackPayload(String.format("%s folders to process.", totalToProcess),
                    Integer.toString(totalToProcess)), MediaType.APPLICATION_JSON).name("total"));

        }, (updated, folderName) -> {
            emitter.send(SseEmitter.event()
                    .data(new RecreateFoldersCallbackPayload(String.format("Folder %s updated.", folderName), Integer.toString(updated)),
                            MediaType.APPLICATION_JSON)
                    .name("update"));
        }, (failed, objectType, objectId) -> {
            emitter.send(SseEmitter.event()
                    .data(new RecreateFoldersCallbackPayload(
                            String.format("Failed to update folder for object of type %s with id %s.", objectType, objectId),
                            Integer.toString(failed)), MediaType.APPLICATION_JSON)
                    .name("fail"));
        }, (totalToProcess, success, failed) -> {
            emitter.send(SseEmitter.event()
                    .data(new RecreateFoldersCallbackPayload(
                            String.format("Of total %s folders, %s updated successfully, %s failed.", totalToProcess, success, failed),
                            String.format("%s:%s:%s", totalToProcess, success, failed)), MediaType.APPLICATION_JSON)
                    .name("finished"));
            emitter.complete();
        }, () -> {
            emitter.send(SseEmitter.event()
                    .data(new RecreateFoldersCallbackPayload("Folders do not need recreation.", ""), MediaType.APPLICATION_JSON)
                    .name("finished"));
            emitter.complete();
        });
    }

    @ExceptionHandler(AcmOutlookFolderCreatorDaoException.class)
    @ResponseBody
    public ResponseEntity<?> handleConfigurationException(AcmOutlookFolderCreatorDaoException ce)
    {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("error_cause", "INTERNAL_SERVER_ERROR");
        errorDetails.put("error_message", ce.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDetails);
    }

    /**
     * @param folderCreatorDao
     *            the folderCreatorDao to set
     */
    public void setFolderCreatorDao(AcmOutlookFolderCreatorDao folderCreatorDao)
    {
        this.folderCreatorDao = folderCreatorDao;
    }

    /**
     * @param calendarAdminService
     *            the calendarAdminService to set
     */
    public void setCalendarAdminService(OutlookCalendarAdminServiceExtension calendarAdminService)
    {
        this.calendarAdminService = calendarAdminService;
    }

}
