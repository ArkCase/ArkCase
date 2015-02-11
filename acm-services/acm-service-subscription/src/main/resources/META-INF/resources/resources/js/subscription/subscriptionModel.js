/**
 * Subscription.Model
 *
 * @author jwu
 */
Subscription.Model = {
    create : function() {
        if (Subscription.Service.create)    {Subscription.Service.create();}
    }
    ,onInitialized: function() {
        if (Subscription.Service.onInitialized)    {Subscription.Service.onInitialized();}
    }

};

