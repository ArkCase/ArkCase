/**
 * Timesheet.Controller
 *
 * @author md
 */
Timesheet.Controller = {
    create : function() {
    }
    ,onInitialized: function() {
    }
    ,VIEW_SAVED_DETAIL                   : "timesheet-view-saved-detail"
    ,viewSavedDetail: function(timesheet, details) {
        Acm.Dispatcher.fireEvent(this.VIEW_SAVED_DETAIL, timesheet, details);
    }
    ,MODEL_SAVED_DETAIL                   : "timesheet-model-saved-detail"
    ,modelSavedDetail: function(timesheet, details) {
        Acm.Dispatcher.fireEvent(this.MODEL_SAVED_DETAIL, timesheet, details);
    }
    ,VIEW_EDITTED_TIMESHEET               : "timesheet-view-editted-timesheet"
    ,viewEdittedTimesheet: function(timesheet) {
        Acm.Dispatcher.fireEvent(this.VIEW_EDITTED_TIMESHEET, timesheet);
    }
    ,VIEW_ADDED_TIMESHEET                 : "timesheet-view-added-timesheet"
    ,viewAddedTimesheet: function(timesheet) {
        Acm.Dispatcher.fireEvent(this.VIEW_ADDED_TIMESHEET, timesheet);
    }
};





