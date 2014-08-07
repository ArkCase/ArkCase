/**
 * AdminAccess is namespace component for AdminAccess plugin
 *
 * @author jwu
 */
var AdminAccess = AdminAccess || {
    initialize: function() {
        AdminAccess.Object.initialize();
        AdminAccess.Event.initialize();
        AdminAccess.Page.initialize();
        AdminAccess.Rule.initialize();
        AdminAccess.Service.initialize();
        AdminAccess.Callback.initialize();

        Acm.deferred(AdminAccess.Event.onPostInit);
    }

    ,_adminAccessList : {}

    ,getUpdatedAdminAccessList : function(){
        return this._adminAccessList;
    }

    ,setUpdatedAdminAccessList : function(data){
        this._adminAccessList.id = data.record.id;
        this._adminAccessList.objectType = data.record.objectType;
        this._adminAccessList.objectState = data.record.objectState;
        this._adminAccessList.accessLevel = data.record.accessLevel;
        this._adminAccessList.accessorType = data.record.accessorType;
        this._adminAccessList.accessDecision = data.record.accessDecision;
        if ("true" == data.record.allowDiscretionaryUpdate) {
            this._adminAccessList.allowDiscretionaryUpdate = true;
        } else {
            this._adminAccessList.allowDiscretionaryUpdate = false;
        };
    }

    ,Object: {}
    ,Event:{}
    ,Page: {}
    ,Rule: {}
    ,Service: {}
    ,Callback: {}
};

