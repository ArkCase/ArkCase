/**
 * AdminAccess.Callback
 *
 * Callback handlers for server responses
 *
 * @author jwu
 */
AdminAccess.Callback = {
    create : function() {
        Acm.Dispatcher.addEventListener(this.EVENT_ADMIN_ACCESS_UPDATED, this.onAdminAccessUpdate);
    }

    ,EVENT_ADMIN_ACCESS_UPDATED		: "admin-access-default-admin-access-updated"

    ,onAdminAccessUpdate : function(Callback, response) {
        if (response.hasError) {
            Acm.Dialog.error("Failed to update ACL:" + response.errorMsg);
        } else {
            if (Acm.isNotEmpty(response.id)) {
                //no callback necessary at this moment
            }
        }
    }
};


