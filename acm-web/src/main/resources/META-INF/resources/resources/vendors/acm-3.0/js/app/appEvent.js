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
        // only call these functions if the Complaint module actually exists.
        // in reality these calls should be moved to the Complaint module
        if ( Complaint ) {
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
    }

};
