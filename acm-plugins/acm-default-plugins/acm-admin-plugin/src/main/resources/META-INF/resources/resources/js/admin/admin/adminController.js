/**
 * Created by manoj.dhungana on 12/4/2014.
 */

Admin.Controller = Admin.Controller || {
    create: function() {
    }
    ,onInitialized: function() {
    }

    ,MODEL_RETRIEVED_CORRESPONDENCE_TEMPLATES                 : "admin-model-retrieved-correspondence-templates"              //param : templatesList

    ,MODEL_UPDATED_ACCESS_CONTROL                             : "access-control-updated"                                      //param : accessControlList

    ,MODEL_RETRIEVED_GROUP                                    : "organization-hierarchy-group-retrieved"                      //param : group

    ,MODEL_RETRIEVED_GROUP_MEMBERS                            : "organization-hierarchy-group-members-retrieved"              //param : groupMembers

    ,modelRetrievedCorrespondenceTemplates : function(templatesList) {
        Acm.Dispatcher.fireEvent(this.MODEL_RETRIEVED_CORRESPONDENCE_TEMPLATES, templatesList);
    }

    ,modelRetrievedGroup: function(group){
        Acm.Dispatcher.fireEvent(this.MODEL_RETRIEVED_GROUP, group);
    }

    ,modelRetrievedGroupMembers: function(groupMembers){
        Acm.Dispatcher.fireEvent(this.MODEL_RETRIEVED_GROUP_MEMBERS, groupMembers);
    }

    ,modelUpdatedAccessControl : function(accessControlList){
        Acm.Dispatcher.fireEvent(this.MODEL_UPDATED_ACCESS_CONTROL, accessControlList);
    }
}