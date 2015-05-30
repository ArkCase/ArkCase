/**
 * Calendar.Controller
 *
 * @author jwu
 */
Calendar.Controller = Calendar.Controller || {
    create : function(args) {
        var name = Acm.goodValue(args.name, "calendar");
        this.MODEL_RETRIEVED_OUTLOOK_CALENDAR_ITEMS           = name + "-model-retrieved-outlook-calendar-items";
        this.VIEW_REFRESHED_OUTLOOK_CALENDAR                  = name + "-view-refreshed-outlook-calendar";
    }
    ,onInitialized: function() {
    }

    //outlook calendar items
    ,modelRetrievedOutlookCalendarItems: function(outlookCalendarItems){
        Acm.Dispatcher.fireEvent(Calendar.Controller.MODEL_RETRIEVED_OUTLOOK_CALENDAR_ITEMS, outlookCalendarItems);
    }
    ,viewRefreshedOutlookCalendar: function(caseFileId){
        Acm.Dispatcher.fireEvent(Calendar.Controller.VIEW_REFRESHED_OUTLOOK_CALENDAR, caseFileId);
    }
};

