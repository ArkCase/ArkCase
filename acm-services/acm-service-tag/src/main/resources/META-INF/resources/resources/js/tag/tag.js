/**
 * Tag is namespace component for Tag plugin
 *
 * @author jwu
 */
var Tag = Tag || {
    create: function() {
        if (Tag.Controller.create) {Tag.Controller.create();}
        if (Tag.Model.create)      {Tag.Model.create();}
        if (Tag.View.create)       {Tag.View.create();}

        if (SearchBase.create) {
            SearchBase.create({name: Tag.Model.MicroData.searchName
                ,filters    : Tag.Model.MicroData.searchFilters
                ,topFacets  : Tag.Model.MicroData.searchTopFacets
            });
        }
    }

    ,onInitialized: function() {
        if (Tag.Controller.onInitialized) {Tag.Controller.onInitialized();}
        if (Tag.Model.onInitialized)      {Tag.Model.onInitialized();}
        if (Tag.View.onInitialized)       {Tag.View.onInitialized();}

        if (SearchBase.onInitialized)     {SearchBase.onInitialized();}
    }
};
