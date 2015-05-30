/**
 * Calendar.Service
 *
 * manages all service call to application server
 *
 * @author md
 */
Calendar.Service = {
    create : function(args) {
    }
    ,onInitialized: function() {
    }

    ,OutlookCalendar: {
        create: function () {
        }
        , onInitialized: function () {
        }
        , API_CALENDAR_ITEMS: "/api/v1/plugin/outlook/calendar"

        ,retrieveOutlookOutlookCalendarItems : function(calendarFolderId, parentId) {
            var url = App.getContextPath() + this.API_CALENDAR_ITEMS;
            url+= "?folderId=" + encodeURIComponent(calendarFolderId);
            return Acm.Service.call({type: "GET"
                    ,url: url
                    ,callback:function(response) {
                        if (response.hasError) {
                            Calendar.Controller.modelRetrievedOutlookCalendarItems(response);

                        } else {
                            if (Calendar.Model.OutlookCalendar.validateOutlookCalendarItems(response)) {
                                var outlookCalendarItems = response;
                                Calendar.Model.OutlookCalendar.cacheOutlookCalendarItems.put(parentId, outlookCalendarItems);
                                Calendar.Controller.modelRetrievedOutlookCalendarItems(outlookCalendarItems);
                                return true;
                            }
                        }
                    }
                }
            )
        }
        ,createOutlookOutlookCalendarItems: function(parentId, outlookCalendarItem) {
            var url = App.getContextPath() + this.API_CALENDAR_ITEMS;
            return Acm.Service.call({type: "POST"
                ,url: url
                ,callback: function(response) {
                    if (response.hasError) {
                        Calendar.Controller.modelRetrievedOutlookCalendarItems(outlookCalendarItem);

                    } else {
                        if (Calendar.Model.OutlookCalendar.validateOutlookCalendarItems(response)) {
                            var outlookCalendarItems = response;
                            Calendar.Model.OutlookCalendar.cacheOutlookCalendarItems.put(parentId, outlookCalendarItems);
                            Calendar.Controller.modelRetrievedOutlookCalendarItems(outlookCalendarItems);
                            return true;
                        }
                    } //end else
                }
            });
        }
    }
};

