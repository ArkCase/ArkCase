/**
 * Tag.Model
 *
 * @author jwu
 */
Tag.Model = {
    create : function() {
        if (Tag.Service.create)               {Tag.Service.create();}
        if (Tag.Model.MicroData.create)       {Tag.Model.MicroData.create();}
    }
    ,onInitialized: function() {
        if (Tag.Service.onInitialized)              {Tag.Service.onInitialized();}
        if (Tag.Model.MicroData.onInitialized)      {Tag.Model.MicroData.onInitialized();}
    }

    ,MicroData: {
        create : function() {
            this.searchName        = Acm.Object.MicroData.get("search.name");
            this.searchFilters     = Acm.Object.MicroData.getJson("search.filters");
            this.searchTopFacets   = Acm.Object.MicroData.getJson("search.topFacets");
        }
        ,onInitialized: function() {
        }

    }

};

