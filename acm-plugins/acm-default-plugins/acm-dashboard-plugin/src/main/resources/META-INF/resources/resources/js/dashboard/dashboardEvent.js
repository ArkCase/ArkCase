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
        Dashboard.Service.retrieveMyTasks();
    }
};
