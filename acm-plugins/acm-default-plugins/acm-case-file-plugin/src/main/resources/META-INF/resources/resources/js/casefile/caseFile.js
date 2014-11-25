/**
 * Case File module
 *
 * @author jwu
 */
var CaseFile = CaseFile || {
    create: function() {
        if (CaseFile.Model.create)      {CaseFile.Model.create();}
        if (CaseFile.View.create)       {CaseFile.View.create();}
        if (CaseFile.Controller.create) {CaseFile.Controller.create();}
    }
    ,initialize: function() {
        if (CaseFile.Model.initialize)      {CaseFile.Model.initialize();}
        if (CaseFile.View.initialize)       {CaseFile.View.initialize();}
        if (CaseFile.Controller.initialize) {CaseFile.Controller.initialize();}
    }
};


