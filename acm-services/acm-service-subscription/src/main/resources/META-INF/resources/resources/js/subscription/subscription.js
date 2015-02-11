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

        SearchBase.create("subscription"
            ,Subscription.View.$edtSearch
            ,Subscription.View.$btnSearch
            ,Subscription.View.$divFacet
            ,Subscription.View.$divResults
            ,Subscription.View.args
            ,Subscription.View.jtDataMaker
        );
    }
    ,onInitialized: function() {
        if (Subscription.Controller.onInitialized) {Subscription.Controller.onInitialized();}
        if (Subscription.Model.onInitialized)      {Subscription.Model.onInitialized();}
        if (Subscription.View.onInitialized)       {Subscription.View.onInitialized();}
    }
};
