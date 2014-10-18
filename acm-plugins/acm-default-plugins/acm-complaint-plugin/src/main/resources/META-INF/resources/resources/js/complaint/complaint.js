/**
 * Complaint is namespace component for Complaint
 *
 * @author jwu
 */
var Complaint = Complaint || {
    create: function() {
        Complaint.cachePage = new Acm.Model.CacheFifo(2);
        Complaint.cacheComplaint = new Acm.Model.CacheFifo(3);
        Complaint.cachePersonList = new Acm.Model.CacheFifo(3);


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


