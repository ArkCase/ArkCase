/**
 * Admin.Callback
 *
 * Callback handlers for server responses
 *
 * @author jwu
 */
Admin.Callback = {
    create : function() {
        Acm.Dispatcher.addEventListener(this.EVENT_ADMIN_ACCESS_UPDATED, this.onAdminAccessUpdate);
    }


    //Admin Access Control Policy related Callbacks

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


