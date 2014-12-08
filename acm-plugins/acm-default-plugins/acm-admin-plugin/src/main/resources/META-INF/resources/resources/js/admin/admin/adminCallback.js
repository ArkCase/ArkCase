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
        Acm.Dispatcher.addEventListener(this.EVENT_TEMPLATES_RETRIEVED, this.onTemplatesRetrieved);

    }


    //Admin Access Control Policy related Callbacks

    ,EVENT_ADMIN_ACCESS_UPDATED		: "admin-access-default-admin-access-updated"
    ,EVENT_TEMPLATES_RETRIEVED : "correspondence-templates-retrieved"


    ,onAdminAccessUpdate : function(Callback, response) {
        if (response.hasError) {
            Acm.Dialog.error("Failed to update ACL:" + response.errorMsg);
        } else {
            if (Acm.isNotEmpty(response.id)) {
                //no callback necessary at this moment
            }
        }
    }
    ,onTemplatesRetrieved : function(Callback, response) {
        if (response.hasError) {
            Acm.Dialog.error("Failed to retrieve correspondence templates:" + response.errorMsg);
        } else {
            if (response != null) {
                Admin.cacheTemplates.put(0, response);
                Admin.Object.refreshJTableTemplates();
            }
        }
    }
};


