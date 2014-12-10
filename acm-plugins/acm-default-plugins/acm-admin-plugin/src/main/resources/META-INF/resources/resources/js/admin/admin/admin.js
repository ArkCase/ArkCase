/**
 * Admin is namespace component for Admin plugin
 *
 * @author jwu
 */
var Admin = Admin || {
    create: function() {

        Admin.cacheTemplates = new Acm.Model.CacheFifo(3);


        Admin.Object.create();
        Admin.Event.create();
        Admin.Page.create();
        Admin.Rule.create();
        Admin.Service.create();
        Admin.Callback.create();

        Acm.deferred(Admin.Event.onPostInit());
    }
    ,Object: {}
    ,Event:{}
    ,Page: {}
    ,Rule: {}
    ,Service: {}
    ,Callback: {}

    ,cacheTemplates: null


    ,_adminAccessList : {}

    ,getUpdatedAdminAccessList : function(){
        return this._adminAccessList;
    }

    ,setUpdatedAdminAccessList : function(data) {
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

    ,getTemplates: function() {
        return this.cacheTemplates.get(0);
    }
};

