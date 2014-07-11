/**
 * Acm.Event
 *
 * event handlers for objects
 *
 * @author jwu
 */
Acm.Event = {
    initialize : function() {
    }

    ,onPostInit: function() {
        var data = Acm.Object.getApprovers();
        if (Acm.isEmpty(data)) {
            Acm.Service.getApprovers();
        } else {
            //alert("Acm: data Not empty")
        }
        data = Acm.Object.getComplaintTypes();
        if (Acm.isEmpty(data)) {
            Acm.Service.getComplaintTypes();
        }
        data = Acm.Object.getPriorities();
        if (Acm.isEmpty(data)) {
            Acm.Service.getPriorities();
        }
    }

};
