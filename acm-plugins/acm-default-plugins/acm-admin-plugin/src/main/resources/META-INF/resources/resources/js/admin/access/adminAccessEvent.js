/**
 * AdminAccess.Event
 *
 * event handlers for objects
 *
 * @author jwu
 */
AdminAccess.Event = {
    initialize : function() {
    }

    ,onClickBtnTest: function(e) {
        alert("test clicked");
        App.Object.setApprovers(null);
        App.Object.setComplaintTypes(null);
        App.Object.setPriorities(null);
    }
    ,onPostInit: function() {
//        AdminAccess.Service.retrieveMyTasks(App.getUserName());
    }
};
