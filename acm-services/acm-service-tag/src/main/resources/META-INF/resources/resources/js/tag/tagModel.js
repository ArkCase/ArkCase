/**
 * Tag.Model
 *
 * @author jwu
 */
Tag.Model = {
    create : function() {
        if (Tag.Service.create)    {Tag.Service.create();}
    }
    ,onInitialized: function() {
        if (Tag.Service.onInitialized)    {Tag.Service.onInitialized();}
    }

};

