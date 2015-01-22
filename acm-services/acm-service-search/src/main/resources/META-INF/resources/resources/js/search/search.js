/**
 * Search is namespace component for Search plugin
 *
 * @author jwu
 */
var Search = Search || {
    create: function() {
        if (Search.Model.create)      {Search.Model.create();}
        if (Search.Service.create)    {Search.Service.create();}
        if (Search.View.create)       {Search.View.create();}
        if (Search.Controller.create) {Search.Controller.create();}
    }
    ,onInitialized: function() {
        if (Search.Model.onInitialized)      {Search.Model.onInitialized();}
        if (Search.Service.onInitialized)    {Search.Service.onInitialized();}
        if (Search.View.onInitialized)       {Search.View.onInitialized();}
        if (Search.Controller.onInitialized) {Search.Controller.onInitialized();}
    }
};

