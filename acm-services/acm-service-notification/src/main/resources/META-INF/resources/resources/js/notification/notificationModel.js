/**
 * AcmNotification.Model
 *
 * @author jwu
 */
AcmNotification.Model = {
    create : function() {
        if (AcmNotification.Service.create){AcmNotification.Service.create();}
        if (AcmNotification.Model.MicroData.create)       {AcmNotification.Model.MicroData.create();}
    }
    ,onInitialized: function() {
        if (AcmNotification.Service.onInitialized){AcmNotification.Service.onInitialized();}
        if (AcmNotification.Model.MicroData.onInitialized)      {AcmNotification.Model.MicroData.onInitialized();}
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
