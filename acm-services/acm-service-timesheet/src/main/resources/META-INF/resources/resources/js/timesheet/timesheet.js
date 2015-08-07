/**
 * Timesheet is namespace component for Timesheet plugin
 *
 * @author md
 */
var Timesheet = Timesheet || {
    create: function() {
        if (Timesheet.Controller.create) {Timesheet.Controller.create();}
        if (Timesheet.Model.create)      {Timesheet.Model.create();}
        if (Timesheet.View.create)       {Timesheet.View.create();}

        if (ObjNav.create) {
            ObjNav.create({name: "timesheet"
                ,$tree             : Timesheet.View.Navigator.$tree
                ,treeArgs          : Timesheet.View.Navigator.getTreeArgs()
                ,$ulFilter         : Timesheet.View.Navigator.$ulFilter
                ,treeFilter        : Timesheet.View.MicroData.treeFilter
                ,$ulSort           : Timesheet.View.Navigator.$ulSort
                ,treeSort          : Timesheet.View.MicroData.treeSort
                ,modelInterface    : Timesheet.Model.interface
                ,viewInterface    : Timesheet.View.interface
            });
        }
    }

    ,onInitialized: function() {
        if (Timesheet.Controller.onInitialized) {Timesheet.Controller.onInitialized();}
        if (Timesheet.Model.onInitialized)      {Timesheet.Model.onInitialized();}
        if (Timesheet.View.onInitialized)       {Timesheet.View.onInitialized();}
        if (ObjNav.onInitialized)              {ObjNav.onInitialized();}

    }
};
