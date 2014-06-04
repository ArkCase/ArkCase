/**
 * Dashboard.Event
 *
 * event handlers for objects
 *
 * @author jwu
 */
Dashboard.Event = {
    initialize : function() {
    }


    ,onPostInit: function() {
        var user = Acm.Object.getUserName();
        Dashboard.Service.retrieveMyTasks(user);
    }
};
