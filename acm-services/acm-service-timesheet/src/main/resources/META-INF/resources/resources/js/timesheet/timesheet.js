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
    }

    ,onInitialized: function() {
        if (Timesheet.Controller.onInitialized) {Timesheet.Controller.onInitialized();}
        if (Timesheet.Model.onInitialized)      {Timesheet.Model.onInitialized();}
        if (Timesheet.View.onInitialized)       {Timesheet.View.onInitialized();}
    }
};
