/**
 * Case File Split
 *
 * @author md
 */
var CaseFileSplit = CaseFileSplit || {
    create: function() {
        if (CaseFileSplit.Controller.create) {CaseFileSplit.Controller.create();}
        if (CaseFileSplit.Model.create)      {CaseFileSplit.Model.create();}
        if (CaseFileSplit.View.create)       {CaseFileSplit.View.create();}

        if (DocTree.create) {
            DocTree.create({name: "caseFile"
                ,fileTypes     : CaseFileSplit.View.MicroData.fileTypes
                ,uploadForm    : CaseFileSplit.View.Documents.uploadForm
                ,arkcaseUrl    : CaseFileSplit.View.MicroData.arkcaseUrl
                ,arkcasePort    : CaseFileSplit.View.MicroData.arkcasePort
            });
        }

    }
    ,onInitialized: function() {
        if (CaseFileSplit.Controller.onInitialized) {CaseFileSplit.Controller.onInitialized();}
        if (CaseFileSplit.Model.onInitialized)      {CaseFileSplit.Model.onInitialized();}
        if (CaseFileSplit.View.onInitialized)       {CaseFileSplit.View.onInitialized();}
    }
};


