/**
 * Complaint is namespace component for Complaint plugin
 *
 * @author jwu
 */
var Complaint = Complaint || {
    initialize: function() {
    }

    ,PERSON_SUBTABLE_TITLE_DEVICES:       "Communication Devices"
    ,PERSON_SUBTABLE_TITLE_ORGANIZATIONS: "Organizations"
    ,PERSON_SUBTABLE_TITLE_LOCATIONS:     "Locations"
    ,PERSON_SUBTABLE_TITLE_ALIASES:       "Aliases"


    ,_complaint : {}
    ,getComplaint : function() {
        return this._complaint;
    }
    ,setComplaint : function(c) {
        this._complaint = c;
    }
    ,getComplaintId : function() {
        return this._complaint.complaintId;
    }
    ,setComplaintId : function(id) {
        this._complaint.complaintId = id;
    }
    ,constructComplaint : function() {
        var c = {};
        //c.initiatorFlags = [];                //????need to be defined in POJO


        c.originator = {};
        c.originator.id = 0;
        c.originator.title = "";
        c.originator.givenName = "";
        c.originator.familyName = "";
        c.originator.type = "";
        c.originator.description = "";
        c.originator.contactMethods = [];
        c.originator.personAliases = [];
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


        //c.originator.people = [];
        //c.attachments = [];


        //c.approvers = [];                     //????need to be defined in POJO
        //c.notifications = [];                 //????need to be defined in POJO
        //c.alertDevices = [];                  //????need to be defined in POJO

        //preserve existing complaintId if it has one
        c.complaintId = (Acm.isNotEmpty(this._complaint.complaintId))? this._complaint.complaintId : 0;

        this._complaint = c;
        return c;
    }

//    ,_tasks : []
//    ,getTasks : function() {
//        return this._tasks;
//    }
//    ,setTasks : function(tasks) {
//        this._tasks = tasks;
//    }


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

};

