/**
 * AdminAccess.Service
 *
 * manages all service call to application server
 *
 * @author jwu
 */
AdminAccess.Service = {
    create : function() {
    }

    ,API_RETRIEVE_ACCESS_CONTROL       : "/api/latest/plugin/dataaccess/accessControlDefaults"

    ,API_UPDATE_ACCESS_CONTROL         : "/api/latest/plugin/dataaccess/default/"

    ,updateAdminAccess : function(data) {
        Acm.Ajax.asyncPost(App.getContextPath() + this.API_UPDATE_ACCESS_CONTROL + data.id
            ,JSON.stringify(data)
            ,AdminAccess.Callback.EVENT_ADMIN_ACCESS_UPDATED
        );
    }
};

