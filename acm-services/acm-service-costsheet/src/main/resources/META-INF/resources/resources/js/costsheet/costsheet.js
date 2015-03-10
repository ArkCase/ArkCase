/**
 * Costsheet is namespace component for Costsheet plugin
 *
 * @author md
 */
var Costsheet = Costsheet || {
    create: function() {
        if (Costsheet.Controller.create) {Costsheet.Controller.create();}
        if (Costsheet.Model.create)      {Costsheet.Model.create();}
        if (Costsheet.View.create)       {Costsheet.View.create();}
    }

    ,onInitialized: function() {
        if (Costsheet.Controller.onInitialized) {Costsheet.Controller.onInitialized();}
        if (Costsheet.Model.onInitialized)      {Costsheet.Model.onInitialized();}
        if (Costsheet.View.onInitialized)       {Costsheet.View.onInitialized();}
    }
};
