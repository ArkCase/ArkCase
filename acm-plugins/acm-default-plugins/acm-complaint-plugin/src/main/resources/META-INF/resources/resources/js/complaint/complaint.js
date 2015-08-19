/**
 * Complaint is namespace component for Complaint
 *
 * @author jwu
 */
var Complaint = Complaint || {
    create: function() {
        if (Complaint.Controller.create) {Complaint.Controller.create();}
        if (Complaint.Model.create)      {Complaint.Model.create();}
        if (Complaint.View.create)       {Complaint.View.create();}

        if (ObjNav.create) {
            ObjNav.create({name: "complaint"
                ,$tree            : Complaint.View.Navigator.$tree
                ,treeArgs         : Complaint.View.Navigator.getTreeArgs()
                ,$ulFilter        : Complaint.View.Navigator.$ulFilter
                ,treeFilter       : Complaint.View.MicroData.treeFilter
                ,$ulSort          : Complaint.View.Navigator.$ulSort
                ,treeSort         : Complaint.View.MicroData.treeSort
                ,modelInterface   : Complaint.Model.interfaceNavObj
                ,viewInterface    : Complaint.View.interfaceNavObj
            });
        }

        if (DocTree.create) {
            DocTree.create({name: "complaint"
                ,fileTypes     : Complaint.View.Documents.getFileTypes()
                ,uploadForm    : Complaint.View.Documents.uploadForm
                ,arkcaseUrl    : Complaint.View.MicroData.arkcaseUrl
                ,arkcasePort    : Complaint.View.MicroData.arkcasePort
                ,allowMailFilesAsAttachments : Complaint.View.MicroData.allowMailFilesAsAttachments
                ,allowMailFilesToExternalAddresses : Complaint.View.MicroData.allowMailFilesToExternalAddresses
            });
        }

        if (Calendar.create) {
            Calendar.create({name: "complaint",
                objectType: Complaint.Model.DOC_TYPE_COMPLAINT
                ,displayError: Complaint.View.Calendar.displayError
            });
        }

        if (SubscriptionOp.create) {
            SubscriptionOp.create({
                getSubscriptionInfo: function() {
                    return {userId: App.getUserName()
                        ,objectType: Complaint.Model.DOC_TYPE_COMPLAINT
                        ,objectId: Complaint.Model.getComplaintId()
                    };
                }
            });
        }
    }

    ,onInitialized: function() {
        if (Complaint.Controller.onInitialized) {Complaint.Controller.onInitialized();}
        if (Complaint.Model.onInitialized)      {Complaint.Model.onInitialized();}
        if (Complaint.View.onInitialized)       {Complaint.View.onInitialized();}

        if (ObjNav.onInitialized)               {ObjNav.onInitialized();}
        if (DocTree.onInitialized)              {DocTree.onInitialized();}
        if (SubscriptionOp.onInitialized)       {SubscriptionOp.onInitialized();}
    }

};

