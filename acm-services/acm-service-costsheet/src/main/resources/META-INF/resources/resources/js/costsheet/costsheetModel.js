/**
 * Costsheet.Model
 *
 * @author md
 */
Costsheet.Model = {
    create : function() {
        if (Costsheet.Service.create)    {Costsheet.Service.create();}
    }
    ,onInitialized: function() {
        if (Costsheet.Service.onInitialized)    {Costsheet.Service.onInitialized();}
    }

};

