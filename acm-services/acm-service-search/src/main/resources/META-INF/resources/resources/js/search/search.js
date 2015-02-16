/**
 * Search is namespace component for Search plugin
 *
 * @author jwu
 */
var Search = Search || {
    create: function() {
        if (Search.Controller.create) {Search.Controller.create();}
        if (Search.Model.create)      {Search.Model.create();}
        if (Search.View.create)       {Search.View.create();}

        if (SearchBase.create) {
            SearchBase.create({name: "search"
                ,$edtSearch : Search.View.Query.$edtSearch
                ,$btnSearch : Search.View.Query.$btnSearch
                ,$divFacets : Search.View.$divFacets
                ,$divResults: Search.View.$divResults
            });
        }
    }
    ,onInitialized: function() {
        if (Search.Controller.onInitialized) {Search.Controller.onInitialized();}
        if (Search.Model.onInitialized)      {Search.Model.onInitialized();}
        if (Search.View.onInitialized)       {Search.View.onInitialized();}

        if (SearchBase.onInitialized)        {SearchBase.onInitialized();}
    }
};

