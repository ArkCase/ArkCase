/**
 * Timesheet.Service
 *
 * manages all service call to application server
 *
 * @author md
 */
Timesheet.Service = {
    create : function() {
    }
    ,onInitialized: function() {
    }


    ,Detail:{
        saveTimesheet: function(timesheetId, timesheet, handler) {
            ObjNav.Service.Detail.saveObject(Timesheet.Model.DOC_TYPE_TIMESHEET, timesheetId, timesheet, handler);
        }
        ,saveDetail: function(timesheet,details) {
        if (Timesheet.Model.Detail.validateTimesheet(timesheet)) {
                //have to remove group property from the JSON
                //to prevent from error
                delete timesheet.user["group"];
                timesheet.details = details;
                Timesheet.Service.Detail.saveTimesheet(timesheet.id, timesheet
                    ,function(data) {
                        Timesheet.Controller.modelSavedDetail(timesheet, Acm.Service.responseWrapper(data, data.details));
                    }
                );
            }
        }
    }
};
