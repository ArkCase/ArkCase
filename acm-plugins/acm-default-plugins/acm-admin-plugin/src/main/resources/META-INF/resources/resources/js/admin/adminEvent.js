/**
 * Admin.Event
 *
 * event handlers for objects
 *
 * @author jwu
 */
Admin.Event = {
    initialize : function() {
    }

    ,onClickBtnTest: function(e) {
        alert("test clicked");
        Acm.Object.setApprovers(null);
        Acm.Object.setComplaintTypes(null);
        Acm.Object.setPriorities(null);
    }
    ,onPostInit: function() {
//        Admin.Service.retrieveMyTasks(Acm.getUserName());
    }
};
