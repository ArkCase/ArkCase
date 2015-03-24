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
    ,VIEW_CLOSED_EDIT_TIMESHEET_WINDOW               : "timesheet-view-closed-edit-timesheet-window"
    ,viewClosedEditTimesheetWindow: function(timesheet) {
        Acm.Dispatcher.fireEvent(this.VIEW_CLOSED_EDIT_TIMESHEET_WINDOW, timesheet);
    }
    ,VIEW_CLOSED_ADD_TIMESHEET_WINDOW                 : "timesheet-view-closed-add-timesheet-window"
    ,viewClosedAddTimesheetWindow: function(timesheet) {
        Acm.Dispatcher.fireEvent(this.VIEW_CLOSED_ADD_TIMESHEET_WINDOW, timesheet);
    }
};





