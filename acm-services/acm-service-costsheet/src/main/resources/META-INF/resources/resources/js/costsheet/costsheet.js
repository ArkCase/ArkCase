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
        if (ObjNav.create) {
            ObjNav.create({name: "costsheet"
                ,$tree             : Costsheet.View.Navigator.$tree
                ,treeArgs          : Costsheet.View.Navigator.getTreeArgs()
                ,$ulFilter         : Costsheet.View.Navigator.$ulFilter
                ,treeFilter        : Costsheet.View.MicroData.treeFilter
                ,$ulSort           : Costsheet.View.Navigator.$ulSort
                ,treeSort          : Costsheet.View.MicroData.treeSort
                ,modelInterface    : Costsheet.Model.interface
            });
        }
    }

    ,onInitialized: function() {
        if (Costsheet.Controller.onInitialized) {Costsheet.Controller.onInitialized();}
        if (Costsheet.Model.onInitialized)      {Costsheet.Model.onInitialized();}
        if (Costsheet.View.onInitialized)       {Costsheet.View.onInitialized();}
        if (ObjNav.onInitialized)               {ObjNav.onInitialized();}
    }
};
