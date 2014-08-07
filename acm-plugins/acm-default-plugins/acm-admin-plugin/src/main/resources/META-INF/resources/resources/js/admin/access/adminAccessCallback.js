/**
 * AdminAccess.Callback
 *
 * Callback handlers for server responses
 *
 * @author jwu
 */
AdminAccess.Callback = {
    initialize : function() {
        Acm.Dispatcher.addEventListener(this.EVENT_ADMIN_ACCESS_UPDATED, this.onAdminAccessUpdate);
    }

    ,EVENT_ADMIN_ACCESS_UPDATED		: "admin-access-default-admin-access-updated"

    ,onAdminAccessUpdate : function(Callback, response) {
//        var success = false;
//        if (response) {
//            AdminAccess.Page.fillMyTasks(response);
//            success = true;
//        }
//
//        if (!success) {
//            Acm.Dialog.error("Failed to retrieve my tasks");
//        }
        if (response.hasError) {
            Acm.Dialog.error("Failed to update ACL:" + response.errorMsg);
        } else {
            if (Acm.isNotEmpty(response.id)) {
                //AdminAccess.setUpdatedAdminAccessList(response);
                //AdminAccess.Object.updateAdminAccess(response);
            }
        }
    }
};


