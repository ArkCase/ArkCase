/**
 * Case File module
 *
 * @author jwu
 */
var CaseFile = CaseFile || {
    create: function() {
        if (CaseFile.Controller.create) {CaseFile.Controller.create();}
        if (CaseFile.Model.create)      {CaseFile.Model.create();}
        if (CaseFile.View.create)       {CaseFile.View.create();}

        if (ObjNav.create) {
            ObjNav.create({name: "casefile"
                ,$tree             : CaseFile.View.Navigator.$tree
                ,treeArgs          : CaseFile.View.Navigator.getTreeArgs()
                ,$ulFilter         : CaseFile.View.Navigator.$ulFilter
                ,treeFilter        : CaseFile.View.MicroData.treeFilter
                ,$ulSort           : CaseFile.View.Navigator.$ulSort
                ,treeSort          : CaseFile.View.MicroData.treeSort
                ,modelInterface    : CaseFile.Model.interface
            });
        }

        if (DocTree.create) {
            DocTree.create({name: "caseFile"
                ,fileTypes     : CaseFile.View.MicroData.fileTypes
                ,uploadForm    : CaseFile.View.Documents.uploadForm
                ,arkcaseUrl    : CaseFile.View.MicroData.arkcaseUrl
                ,arkcasePort    : CaseFile.View.MicroData.arkcasePort
            });
        }

        if (Calendar.create) {
            Calendar.create({name: "caseFile"
                ,getObjectInfo: function() {
                    return {
                        objectType: CaseFile.Model.DOC_TYPE_CASE_FILE
                    };
                }
            });
        }

        if (SubscriptionOp.create)           {
            SubscriptionOp.create({
                getSubscriptionInfo: function() {
                    return {userId: App.getUserName()
                        ,objectType: CaseFile.Model.DOC_TYPE_CASE_FILE
                        ,objectId: CaseFile.Model.getCaseFileId()
                    };
                }
            });
        }
    }
    ,onInitialized: function() {
        if (CaseFile.Controller.onInitialized) {CaseFile.Controller.onInitialized();}
        if (CaseFile.Model.onInitialized)      {CaseFile.Model.onInitialized();}
        if (CaseFile.View.onInitialized)       {CaseFile.View.onInitialized();}
        if (ObjNav.onInitialized)              {ObjNav.onInitialized();}
        if (SubscriptionOp.onInitialized)      {SubscriptionOp.onInitialized();}
    }

    ,config: function() {
        CaseFile.Model.Config.request();
    }
};


