/**
 * Case File module
 *
 * @author jwu
 */
var CaseFile = CaseFile || {
    create: function() {
        if (CaseFile.Model.create)      {CaseFile.Model.create();}
        if (CaseFile.View.create)       {CaseFile.View.create();}
        if (CaseFile.Controller.create) {CaseFile.Controller.create();}

        if (SubscriptionOp.create)           {
            SubscriptionOp.create({
                getSubscriptionInfo: function() {
                    return {userId: App.getUserName()
                        ,objectType: CaseFile.Model.getObjectType()
                        ,objectId: CaseFile.Model.getCaseFileId()
                    };
                }
            });
        }
    }
    ,onInitialized: function() {
        if (CaseFile.Model.onInitialized)      {CaseFile.Model.onInitialized();}
        if (CaseFile.View.onInitialized)       {CaseFile.View.onInitialized();}
        if (CaseFile.Controller.onInitialized) {CaseFile.Controller.onInitialized();}
        if (SubscriptionOp.onInitialized)      {SubscriptionOp.onInitialized();}
    }
};


