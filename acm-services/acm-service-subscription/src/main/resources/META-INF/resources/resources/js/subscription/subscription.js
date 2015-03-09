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
            SearchBase.create({name: Subscription.Model.MicroData.searchName
                ,jtArgs     : Subscription.View.getJtArgs()
                ,jtDataMaker: Subscription.View.jtDataMaker
                ,filters    : Subscription.Model.MicroData.getSearchFilters()
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
