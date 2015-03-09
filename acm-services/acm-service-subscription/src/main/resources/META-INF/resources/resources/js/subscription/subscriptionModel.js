/**
 * Subscription.Model
 *
 * @author jwu
 */
Subscription.Model = {
    create : function() {
        if (Subscription.Service.create)               {Subscription.Service.create();}
        if (Subscription.Model.MicroData.create)       {Subscription.Model.MicroData.create();}
    }
    ,onInitialized: function() {
        if (Subscription.Service.onInitialized)              {Subscription.Service.onInitialized();}
        if (Subscription.Model.MicroData.onInitialized)      {Subscription.Model.MicroData.onInitialized();}
    }

    ,MicroData: {
        create : function() {
            this.searchName     = Acm.Object.MicroData.get("search.name");
            this.searchFilters  = Acm.Object.MicroData.getJson("search.filters");
        }
        ,onInitialized: function() {
        }
        ,getSearchFilters: function() {
            if (Acm.isArray(this.searchFilters)) {
                for (var i = 0; i < this.searchFilters.length; i++) {
                    if (Acm.equals(["$user"], this.searchFilters[i].values)) {
                        this.searchFilters[i].values = [App.getUserName()];
                    }
                }
            }
            return this.searchFilters;
        }
    }
};

