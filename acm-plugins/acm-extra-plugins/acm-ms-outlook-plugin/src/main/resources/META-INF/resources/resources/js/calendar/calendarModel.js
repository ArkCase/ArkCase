/**
 * OutlookCalendar.Model
 *
 * @author md
 */
Calendar.Model = Calendar.Model || {
    create : function(args) {
        if (Calendar.Model.OutlookCalendar.create)       {Calendar.Model.OutlookCalendar.create(args);}
        if (Calendar.Service.create)                    {Calendar.Service.create(args);}
    }
    ,onInitialized: function(args) {
        if (Calendar.Model.OutlookCalendar.onInitialized)       {Calendar.Model.OutlookCalendar.onInitialized(args);}
        if (Calendar.Service.onInitialized)                    {Calendar.Service.onInitialized(args);}
    }

    ,OutlookCalendar: {
        create : function(args) {
            this.cacheParentObject = new Acm.Model.CacheFifo();
            this.cacheOutlookCalendarItems = new Acm.Model.CacheFifo();

            Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT   ,this.onModelRetrievedObject);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT          ,this.onViewSelectedObject);
            Acm.Dispatcher.addEventListener(Calendar.Controller.VIEW_REFRESHED_OUTLOOK_CALENDAR          ,this.onViewRefreshedOutlookCalendar);

        }
        ,onInitialized: function(args) {
        }
        ,getParentObject: function(nodeType,objId) {
            if(Acm.isNotEmpty(objId) && Acm.isNotEmpty(nodeType)){
                return ObjNav.Model.Detail.getCacheObject(Acm.goodValue(nodeType), Acm.goodValue(objId));
            }
        }
        ,onModelRetrievedObject: function(objData) {
            if(Acm.isNotEmpty(objData)){
                Calendar.Model.OutlookCalendar.cacheParentObject.put(objData.id,objData);
                if(Acm.isNotEmpty(objData.container) && Acm.isNotEmpty(objData.container.calendarFolderId)){
                    Calendar.Service.OutlookCalendar.retrieveOutlookOutlookCalendarItems(objData.container.calendarFolderId,objData.id);
                }
            }
        }
        ,onViewSelectedObject: function(nodeType,objId){
            var outlookCalendarItems = Calendar.Model.OutlookCalendar.cacheOutlookCalendarItems.get(objId);
            if(Calendar.Model.OutlookCalendar.validateOutlookCalendarItems(outlookCalendarItems)){
                return;
            }
            else{
                var parentObject = Calendar.Model.OutlookCalendar.getParentObject(nodeType,objId);
                if(Acm.isNotEmpty(parentObject)){
                    if(Acm.isNotEmpty(parentObject.container) && Acm.isNotEmpty(parentObject.container.calendarFolderId)){
                        Calendar.Service.OutlookCalendar.retrieveOutlookOutlookCalendarItems(parentObject.container.calendarFolderId,objId);
                    }
                }
            }
        }
        ,onViewRefreshedOutlookCalendar: function(parentId){
            var parentObject = Calendar.Model.OutlookCalendar.cacheParentObject.get(parentId);
            if(Acm.isNotEmpty(parentObject)){
                if(Acm.isNotEmpty(parentObject.container) && Acm.isNotEmpty(parentObject.container.calendarFolderId)){
                    Calendar.Service.OutlookCalendar.retrieveOutlookOutlookCalendarItems(parentObject.container.calendarFolderId,parentId);
                }
            }
        }
        ,validateOutlookCalendarItems: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isNotArray(data.items)) {
                return false;
            }
            if (Acm.isEmpty(data.totalItems)) {
                return false;
            }
            return true;
        }
        ,validateOutlookCalendarItem: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.id)) {
                return false;
            }
            if (Acm.isEmpty(data.size)) {
                return false;
            }
            if (Acm.isEmpty(data.sent)) {
                return false;
            }
            if (Acm.isEmpty(data.allDayEvent)) {
                return false;
            }
            if (Acm.isEmpty(data.cancelled)) {
                return false;
            }
            if (Acm.isEmpty(data.meeting)) {
                return false;
            }
            if (Acm.isEmpty(data.recurring)) {
                return false;
            }
            if (Acm.isEmpty(data.startDate)) {
                return false;
            }
            if (Acm.isEmpty(data.endDate)) {
                return false;
            }
            return true;
        }
    }
};

