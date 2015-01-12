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

    ,MODEL_CREATED_ADHOC_GROUP                                : "organization-hierarchy-group-created"                        //param : group

    ,MODEL_REMOVED_GROUP                                      : "organization-hierarchy-group-removed"                        //param : removedGroup

    ,MODEL_RETRIEVED_GROUP                                    : "organization-hierarchy-group-retrieved"                      //param : group

    ,MODEL_RETRIEVED_GROUPS                                   : "organization-hierarchy-all-groups-retrieved"                 //param : groups

    ,MODEL_RETRIEVED_GROUP_MEMBERS                            : "organization-hierarchy-group-members-retrieved"              //param : groupMembers

    ,MODEL_ADDED_GROUP_MEMBER                                 : "organization-hierarchy-group-members-added"                  //param : addedMember

    ,MODEL_REMOVED_GROUP_MEMBER                               : "organization-hierarchy-group-member-removed"                //param : removedMember

    ,MODEL_RETRIEVED_USERS                                    : "organization-hierarchy-all-users-retrieved"                //param : allUsers

    ,modelRetrievedCorrespondenceTemplates : function(templatesList) {
        Acm.Dispatcher.fireEvent(this.MODEL_RETRIEVED_CORRESPONDENCE_TEMPLATES, templatesList);
    }

    ,modelCreatedAdHocGroup: function(group){
        Acm.Dispatcher.fireEvent(this.MODEL_CREATED_ADHOC_GROUP, group);
    }

    ,modelRemovedGroup: function(removedGroup){
        Acm.Dispatcher.fireEvent(this.MODEL_REMOVED_GROUP, removedGroup);
    }

    ,modelRetrievedGroup: function(group){
        Acm.Dispatcher.fireEvent(this.MODEL_RETRIEVED_GROUP, group);
    }

    ,modelRetrievedGroups: function(groups){
        Acm.Dispatcher.fireEvent(this.MODEL_RETRIEVED_GROUPS, groups);
    }

    ,modelRetrievedGroupMembers: function(groupMembers){
        Acm.Dispatcher.fireEvent(this.MODEL_RETRIEVED_GROUP_MEMBERS, groupMembers);
    }

    ,modelRemovedGroupMember: function(removedMember){
        Acm.Dispatcher.fireEvent(this.MODEL_REMOVED_GROUP_MEMBER, removedMember);
    }

    ,modelAddedGroupMember: function(addedMember){
        Acm.Dispatcher.fireEvent(this.MODEL_ADDED_GROUP_MEMBER, addedMember);
    }

    ,modelUpdatedAccessControl : function(accessControlList){
        Acm.Dispatcher.fireEvent(this.MODEL_UPDATED_ACCESS_CONTROL, accessControlList);
    }

    ,modelRetrievedUsers : function(allUsers){
        Acm.Dispatcher.fireEvent(this.MODEL_RETRIEVED_USERS, allUsers);
    }
}