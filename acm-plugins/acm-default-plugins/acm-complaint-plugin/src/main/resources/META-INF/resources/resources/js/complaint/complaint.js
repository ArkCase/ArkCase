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

        Acm.deferred(Complaint.Event.onPostInit);
    }

    ,Object: {}
    ,Event:{}
    ,Page: {}
    ,Rule: {}
    ,Service: {}
    ,Callback: {}


    ,PERSON_SUBTABLE_TITLE_DEVICES:       "Communication Devices"
    ,PERSON_SUBTABLE_TITLE_ORGANIZATIONS: "Organizations"
    ,PERSON_SUBTABLE_TITLE_LOCATIONS:     "Locations"
    ,PERSON_SUBTABLE_TITLE_ALIASES:       "Aliases"

//    ,_complaintId : undefined
//    ,getComplaintId : function() {
//        return this._complaintId;
//    }
//    ,setComplaintId : function(id) {
//        this._complaintId = id;
//    }
    ,getComplaintId : function() {
        return this._complaint.complaintId;
    }
    ,setComplaintId : function(id) {
        this._complaint.complaintId = id;
    }
    ,_complaint : {}
    ,getComplaint : function() {
        return this._complaint;
    }
    ,setComplaint : function(c) {
        this._complaint = c;
    }
    ,constructComplaint : function() {
        var c = {};
        c.complaintId = 0;
        //c.initiatorFlags = [];                //????need to be defined in POJO


        c.originator = {};
        c.originator.id = 0;
        c.originator.title = "";
        c.originator.givenName = "";
        c.originator.familyName = "";
        //c.originator.type = "";               //????need to be defined in POJO
        //c.originator.description = "";        //????need to be defined in POJO

        c.originator.contactMethods = [];
        //c.originator.contactMethods.id = 0;
        //c.originator.contactMethods.type = "";
        //c.originator.contactMethods.value = "";
        //c.originator.contactMethods.created = "";
        //c.originator.contactMethods.creator = "";

        //c.originator.organizations = [];            //????need to be defined in POJO

        c.originator.addresses = [];
        //c.originator.addresses.id = 0;
        //c.originator.addresses.type = "";
        //c.originator.addresses.streetAddress = "";  //what happends to streetAddress2 ?
        //c.originator.addresses.city = "";
        //c.originator.addresses.state = "";
        //c.originator.addresses.zip = "";
        //c.originator.addresses.country = "";
        //c.originator.addresses.created = "";
        //c.originator.addresses.creator = "";

        //c.originator.aliases = [];                  //????need to be defined in POJO

        c.incidentDate = "";
        //c.duration = "";
        c.complaintType = "";
        c.priority = "";
        c.complaintType = "";
        c.details = "";
        //c.complaintFlags = [];                //????need to be defined in POJO


        c.originator.people = [];


        c.attachments = [];


        //c.approvers = [];                     //????need to be defined in POJO
        //c.notifications = [];                 //????need to be defined in POJO
        //c.alertDevices = [];                  //????need to be defined in POJO

        this._complaint = c;
        return c;
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

    ,_participants : ["David Miller", "James Bailey", "Judy Hsu", "Ronda Ringo", "AJ McClary", "Jim Nasr"]
    ,getParticipants : function() {
        return this._participants;
    }

};

