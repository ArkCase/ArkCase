/**
 * Subscription is namespace component for Subscription plugin
 *
 * @author jwu
 */
var Subscription = Subscription || {
    create: function() {
        if (Subscription.Model.create)      {Subscription.Model.create();}
        if (Subscription.Service.create)    {Subscription.Service.create();}
        if (Subscription.View.create)       {Subscription.View.create();}
        if (Subscription.Controller.create) {Subscription.Controller.create();}
    }
    ,onInitialized: function() {
        if (Subscription.Model.onInitialized)      {Subscription.Model.onInitialized();}
        if (Subscription.Service.onInitialized)    {Subscription.Service.onInitialized();}
        if (Subscription.View.onInitialized)       {Subscription.View.onInitialized();}
        if (Subscription.Controller.onInitialized) {Subscription.Controller.onInitialized();}
    }
};

