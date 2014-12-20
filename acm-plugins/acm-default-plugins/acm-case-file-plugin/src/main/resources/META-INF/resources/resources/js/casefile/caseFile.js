/**
 * Case File module
 *
 * @author jwu
 */
var CaseFile = CaseFile || {
    create: function() {
        if (CaseFile.Model.create)      {CaseFile.Model.create();}
        if (CaseFile.Service.create)    {CaseFile.Service.create();}
        if (CaseFile.View.create)       {CaseFile.View.create();}
        if (CaseFile.Controller.create) {CaseFile.Controller.create();}
    }
    ,onInitialized: function() {
        if (CaseFile.Model.onInitialized)      {CaseFile.Model.onInitialized();}
        if (CaseFile.Service.onInitialized)    {CaseFile.Service.onInitialized();}
        if (CaseFile.View.onInitialized)       {CaseFile.View.onInitialized();}
        if (CaseFile.Controller.onInitialized) {CaseFile.Controller.onInitialized();}
    }
};


