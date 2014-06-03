/**
 * Task is namespace component for Task plugin
 *
 * @author jwu
 */
var Task = Task || {
    initialize: function() {
        Task.Object.initialize();
        Task.Event.initialize();
        Task.Page.initialize();
        Task.Rule.initialize();
        Task.Service.initialize();
        Task.Callback.initialize();

        Acm.deferred(Task.Event.onPostInit);
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

//    ,_taskId : undefined
//    ,getTaskId : function() {
//        return this._taskId;
//    }
//    ,setTaskId : function(id) {
//        this._taskId = id;
//    }
    ,getTaskId : function() {
        return this._task.taskId;
    }
    ,setTaskId : function(id) {
        this._task.taskId = id;
    }
    ,_task : {}
    ,getTask : function() {
        return this._task;
    }
    ,setTask : function(c) {
        this._task = c;
    }
    ,constructTask : function() {
        var c = {};
        c.taskId = 0;
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
        c.taskType = "";
        c.priority = "";
        c.taskType = "";
        c.details = "";
        //c.taskFlags = [];                //????need to be defined in POJO


        c.originator.people = [];


        c.attachments = [];


        //c.approvers = [];                     //????need to be defined in POJO
        //c.notifications = [];                 //????need to be defined in POJO
        //c.alertDevices = [];                  //????need to be defined in POJO

        this._task = c;
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

