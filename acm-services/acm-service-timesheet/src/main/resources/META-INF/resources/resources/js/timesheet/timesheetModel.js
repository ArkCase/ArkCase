/**
 * Timesheet.Model
 *
 * @author md
 */
Timesheet.Model = {
    create : function() {
        if (Timesheet.Service.create)    {Timesheet.Service.create();}
    }
    ,onInitialized: function() {
        if (Timesheet.Service.onInitialized)    {Timesheet.Service.onInitialized();}
    }

};

