/**
 * Complaint is namespace component for Complaint plugin
 *
 * @author jwu
 */
var Complaint = Complaint || {
    initialize: function() {
        Complaint.Object.initialize();
        Complaint.Event.initialize();
        Complaint.Page.initialize();
        Complaint.Rule.initialize();
        Complaint.Service.initialize();
        Complaint.Callback.initialize();

        Complaint.Event.onPostInit();
    }

    ,Object: {}
    ,Event:{}
    ,Page: {}
    ,Rule: {}
    ,Service: {}
    ,Callback: {}


    ,_complaintId : undefined
    ,getComplaintId : function() {
        return this._complaintId;
    }
    ,setComplaintId : function(id) {
        this._complaintId = id;
    }

    ,_complaint : {}
    ,getComplaint : function() {
        return this._complaint;
    }
    ,setComplaint : function(c) {
        this._complaint = c;
    }

    ,_personTypes : ["Witness", "Subject", "Spouse"]
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

    ,PERSON_SUBTABLE_TITLE_DEVICES:       "Communication Devices"
    ,PERSON_SUBTABLE_TITLE_ORGANIZATIONS: "Organizations"
    ,PERSON_SUBTABLE_TITLE_LOCATIONS:     "Locations"
    ,PERSON_SUBTABLE_TITLE_ALIASES:       "Aliases"
};

