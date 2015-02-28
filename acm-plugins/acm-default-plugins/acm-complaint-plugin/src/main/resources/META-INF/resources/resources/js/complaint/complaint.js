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
                        ,objectType: Complaint.Model.getObjectType()
                        ,objectId: Complaint.Model.getObjectId()
                    };
                }
            });
        }

//        this.create_old();
    }

    ,onInitialized: function() {
        if (Complaint.Controller.onInitialized) {Complaint.Controller.onInitialized();}
        if (Complaint.Model.onInitialized)      {Complaint.Model.onInitialized();}
        if (Complaint.View.onInitialized)       {Complaint.View.onInitialized();}

        if (ObjNav.onInitialized)               {ObjNav.onInitialized();}
        if (SubscriptionOp.onInitialized)       {SubscriptionOp.onInitialized();}
    }


    //------------------------------
    ,create_old: function() {
        Complaint.cachePage = new Acm.Model.CacheFifo(2);
        Complaint.cacheComplaint = new Acm.Model.CacheFifo(3);
        Complaint.cachePersonList = new Acm.Model.CacheFifo(3);
        Complaint.cacheNoteList = new Acm.Model.CacheFifo(3);


        Complaint.Object.create();
        Complaint.Event.create();
        Complaint.Page.create();
        Complaint.Rule.create();
        Complaint.Service.create();
        Complaint.Callback.create();

        Acm.deferred(Complaint.Event.onPostInit);
    }

//    ,Object: {}
//    ,Event:{}
//    ,Page: {}
//    ,Rule: {}
//    ,Service: {}
//    ,Callback: {}
    ,cachePersonList: null
    ,cachePage: null
    ,cacheComplaint: null
    ,cacheNoteList : null
    ,_complaintId: 0
    ,getComplaintId : function() {
        return this._complaintId;
    }
    ,setComplaintId : function(id) {
        this._complaintId = id;
    }
    ,getComplaint: function() {
        if (0 >= this._complaintId) {
            return null;
        }
        return this.cacheComplaint.get(this._complaintId);
    }


    ,PERSON_SUBTABLE_TITLE_DEVICES:       "Communication Devices"
    ,PERSON_SUBTABLE_TITLE_ORGANIZATIONS: "Organizations"
    ,PERSON_SUBTABLE_TITLE_LOCATIONS:     "Locations"
    ,PERSON_SUBTABLE_TITLE_ALIASES:       "Aliases"



    ,_personTypes : ['Initiator', 'Complaintant','Subject','Witness','Wrongdoer','Other']
    ,getPersonTypes : function() {
        return this._personTypes;
    }
    ,_personTypesModifiable : ['Complaintant','Subject','Witness','Wrongdoer','Other']
    ,getPersonTypesModifiable : function() {
        return this._personTypesModifiable;
    }

    ,_personTitles : ['Mr', 'Mrs', 'Ms', 'Miss']
    ,getPersonTitles : function() {
        return this._personTitles;
    }

    ,_deviceTypes : ['Home phone', 'Office phone', 'Cell phone', 'Pager',
                'Email','Instant messenger', 'Social media','Website','Blog']
    ,getDeviceTypes : function() {
        return this._deviceTypes;
    }

    ,_organizationTypes : ['Non-profit','Government','Corporation']
    ,getOrganizationTypes : function() {
        return this._organizationTypes;
    }

    ,_locationTypes : ['Business' , 'Home']
    ,getLocationTypes : function() {
        return this._locationTypes;
    }

    ,_aliasTypes : ['FKA' , 'Married']
    ,getAliasTypes : function() {
        return this._aliasTypes;
    }

};


