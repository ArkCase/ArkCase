/**
 * Subscription is namespace component for Subscription plugin
 *
 * @author jwu
 */
var Subscription = Subscription || {
    create: function() {
        if (Subscription.Controller.create) {Subscription.Controller.create();}
        if (Subscription.Model.create)      {Subscription.Model.create();}
        if (Subscription.View.create)       {Subscription.View.create();}

        if (SearchBase.create) {
            SearchBase.create({name: "subscription"
                ,$edtSearch : Subscription.View.$edtSearch
                ,$btnSearch : Subscription.View.$btnSearch
                ,$divFacets : Subscription.View.$divFacets
                ,$divResults: Subscription.View.$divResults
                ,jtArgs     : Subscription.View.getJtArgs
                ,jtDataMaker: Subscription.View.jtDataMaker
                ,filters    : [{key: "Object Type", values: ["CASE_FILE"]}]
            });
        }
    }

    ,onInitialized: function() {
        if (Subscription.Controller.onInitialized) {Subscription.Controller.onInitialized();}
        if (Subscription.Model.onInitialized)      {Subscription.Model.onInitialized();}
        if (Subscription.View.onInitialized)       {Subscription.View.onInitialized();}

        if (SearchBase.onInitialized)              {SearchBase.onInitialized();}
    }
};
