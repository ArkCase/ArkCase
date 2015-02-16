/**
 * Search.Model
 *
 * @author jwu
 */
Search.Model = {
    create : function() {
        if (Search.Service.create)    {Search.Service.create();}
    }
    ,onInitialized: function() {
        if (Search.Service.onInitialized)    {Search.Service.onInitialized();}
    }

};

