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

        SearchBase.create("search"
            ,Search.View.Query.$edtSearch
            ,Search.View.Query.$btnSearch
            ,Search.View.$divFacet
            ,Search.View.$divResults
//            ,Search.View.args
//            ,Search.View.jtDataMaker
        );
    }
    ,onInitialized: function() {
        if (Search.Controller.onInitialized) {Search.Controller.onInitialized();}
        if (Search.Model.onInitialized)      {Search.Model.onInitialized();}
        if (Search.View.onInitialized)       {Search.View.onInitialized();}
    }
};

