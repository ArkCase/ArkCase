package com.armedia.acm.calendar.service.integration.exchange.web.api;

import com.armedia.acm.service.outlook.dao.AcmOutlookFolderCreatorDao;
import com.armedia.acm.service.outlook.dao.AcmOutlookFolderCreatorDaoException;
import com.armedia.acm.service.outlook.model.AcmOutlookFolderCreator;
import com.armedia.acm.service.outlook.service.OutlookCalendarAdminServiceExtension;

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

    @RequestMapping(path = "/credentials/invalid", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    List<AcmOutlookFolderCreator> findFolderCreatorsWithInvalidCredentials()
    {
        List<AcmOutlookFolderCreator> invalidUsers = calendarAdminService.findFolderCreatorsWithInvalidCredentials();
        return invalidUsers;
    }

    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<AcmOutlookFolderCreator> updateConfiguration(@RequestBody AcmOutlookFolderCreator updatedCreator)
            throws AcmOutlookFolderCreatorDaoException
    {
        boolean shouldRecreate = folderCreatorDao.updateFolderCreator(updatedCreator);
        if (shouldRecreate)
        {
            calendarAdminService.recreateFolders(updatedCreator);
        }
        updatedCreator.setSystemPassword(null);
        return ResponseEntity.status(HttpStatus.OK).body(updatedCreator);
    }

    /**
     * This is an interactive version for updating the folder creator for clients that support SSE (server sent events).
     *
     * The client should register event handlers for the 'total', 'update', 'fail' and 'finished' events sent by the
     * back-end.
     *
     * The following code shows an example how the client can invoke the controller and handle the events:
     *
     * <code>
     *  <!DOCTYPE html>
     *  <html>
     *      <head>
     *          <title>Recreate Outlook Folders SSEs</title>
     *          <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
     *          <script type="text/javascript">
     *              function setup() {
     *
     *                  const eventSource = new EventSource("https://acm-arkcase/arkcase/api/latest/service/calendar/exchange/configure/{creator_id}/{system_email}/{system_password}", {withCredentials: true});
     *
     *                  eventSource.addEventListener('total', function(e) {
     *                      const response = JSON.parse(e.data);
     *                      console.log(response);
     *                  }, false);
     *
     *                  eventSource.addEventListener('update', function(e) {
     *                      const response = JSON.parse(e.data);
     *                      console.log(response);
     *                  }, false);
     *
     *                  eventSource.addEventListener('fail', function(e) {
     *                      const response = JSON.parse(e.data);
     *                      console.log(response);
     *                  }, false);
     *
     *                  eventSource.addEventListener('finished', function(e) {
     *                      const response = JSON.parse(e.data);
     *                      console.log(response);
     *                      eventSource.close();
     *                  }, false);
     *
     *              }
     *
     *              window.onload = setup;
     *          </script>
     *      </head>
     *      <body>
     *          <h1>Hello Calendar Recreate Folders</h1>
     *  </html>
     * </code>
     *
     * where {@code creator_id} is the value of {@code acm_outlook_folder_creator.cm_outlook_folder_creator_id},
     * {@code system_email} is the new value to be stored in {@code acm_outlook_folder_creator.cm_system_email_address}
     * and {@code system_password} is the value to be stored in {@code acm_outlook_folder_creator.cm_system_password}.
     *
     * For clients that do not support SSE, the {@code updateConfiguration} API method should be invoked instead.
     *
     * @see #updateConfiguration(AcmOutlookFolderCreator)
     *
     *      Checking if the lient supports SSE or not, the following snippet can be used:
     *
     *      <code>
     *  if(typeof(EventSource) !== "undefined") {
     *      // Yes! Server-sent events support!
     *      // Some code.....
     *  } else {
     *      // Sorry! No server-sent events support..
     *  }
     * </code>
     *
     * @param id
     * @param systemEmail
     * @param systemPassword
     * @return
     * @throws AcmOutlookFolderCreatorDaoException
     */
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
