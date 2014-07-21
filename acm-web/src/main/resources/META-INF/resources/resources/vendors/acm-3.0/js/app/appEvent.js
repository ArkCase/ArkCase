/**
 * App.Event
 *
 * event handlers for objects
 *
 * @author jwu
 */
App.Event = {
    initialize : function() {
    }

    ,onPostInit: function() {
        var data = App.Object.getApprovers();
        if (Acm.isEmpty(data)) {
            App.Service.getApprovers();
        }
        data = App.Object.getComplaintTypes();
        if (Acm.isEmpty(data)) {
            App.Service.getComplaintTypes();
        }
        data = App.Object.getPriorities();
        if (Acm.isEmpty(data)) {
            App.Service.getPriorities();
        }
    }

};
