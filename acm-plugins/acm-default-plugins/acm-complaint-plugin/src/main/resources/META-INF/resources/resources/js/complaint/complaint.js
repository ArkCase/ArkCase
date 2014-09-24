/**
 * Complaint is namespace component for Complaint
 *
 * @author jwu
 */
var Complaint = Complaint || {
    initialize: function() {
        Complaint.cachePage = new Acm.CacheFifo(2);
        Complaint.cacheComplaint = new Acm.CacheFifo(3);
        Complaint.cachePersonList = new Acm.CacheFifo(3);


        Complaint.Object.initialize();
        Complaint.Event.initialize();
        Complaint.Page.initialize();
        Complaint.Rule.initialize();
        Complaint.Service.initialize();
        Complaint.Callback.initialize();

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



    ,_personTypes : ["Initiator","Witness", "Subject", "Spouse"]
    ,getPersonTypes : function() {
        return this._personTypes;
    }

    ,_personTitles : ['Mr.', 'Mrs.', 'Ms.', 'Dr.']
    ,getPersonTitles : function() {
        return this._personTitles;
    }

    ,_deviceTypes : ['Phone', 'Email']
    ,getDeviceTypes : function() {
        return this._deviceTypes;
    }

    ,_organizationTypes : ['org', 'gov', 'com']
    ,getOrganizationTypes : function() {
        return this._organizationTypes;
    }

    ,_locationTypes : ['Home', 'Office', 'Hotel']
    ,getLocationTypes : function() {
        return this._locationTypes;
    }

    ,_aliasTypes : ['Nick Name', 'Other Name']
    ,getAliasTypes : function() {
        return this._aliasTypes;
    }

};


