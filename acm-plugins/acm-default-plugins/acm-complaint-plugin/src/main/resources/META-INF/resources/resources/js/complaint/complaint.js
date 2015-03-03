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
                ,modelInterface   : Complaint.Model.interface
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
        if (SubscriptionOp.onInitialized)       {SubscriptionOp.onInitialized();}
    }

};

