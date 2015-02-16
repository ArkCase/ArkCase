/**
 * Created by manoj.dhungana on 12/4/2014.
 */

Admin.Controller = Admin.Controller || {
    create: function() {
    }
    ,onInitialized: function() {
    }
    ,VIEW_CREATED_AD_HOC_GROUP                                : "organization-hierarchy-view-created-adhoc-group"                              //param : group,parentId

    ,VIEW_REMOVED_GROUP_MEMBER                                : "organization-hierarchy-view-removed-group-member"                             //param : removedMembers, parentGroupId

    ,VIEW_REMOVED_GROUP                                       : "organization-hierarchy-view-removed-group"                                    //param : groupId

    ,VIEW_ADDED_GROUP_MEMBERS                                 : "organization-hierarchy-view-added-members"                                    //param : addedMembers,parentGroupId

    ,VIEW_ADDED_GROUP_SUPERVISOR                              : "organization-hierarchy-view-added-supervisor"                                  //param : addedSupervisor,parentGroupId

    ,VIEW_SEARCHED_MEMBERS                                    : "organization-hierarchy-view-searched--members"                                //param : term

    ,VIEW_CHANGED_FACET_SELECTION                             : "organization-hierarchy-search-view-changed-facet-selection"                         //param: selected

    ,VIEW_SUBMITTED_QUERY                                     : "organization-hierarchy-search-view-submitted-query"                                 //param: term

    ,MODEL_RETRIEVED_ERROR                                    : "organization-hierarchy-general-error"                                          //param: errorMsg

    ,MODEL_CHANGED_RESULT                                     : "organization-hierarchy-search-changed-result"                                     //param: result

    ,MODEL_CHANGED_FACET                                      : "organization-hierarchy-search-changed-facet"                                      //param: facet

    ,MODEL_RETRIEVED_CORRESPONDENCE_TEMPLATES                 : "admin-model-retrieved-correspondence-templates"              //param : templatesList

    ,MODEL_CREATED_ADHOC_GROUP                                : "organization-hierarchy-group-created"                        //param : group

    ,MODEL_REMOVED_GROUP                                      : "organization-hierarchy-group-removed"                        //param : removedGroup

    ,MODEL_RETRIEVED_GROUP                                    : "organization-hierarchy-group-retrieved"                      //param : group

    ,MODEL_RETRIEVED_GROUPS                                   : "organization-hierarchy-all-groups-retrieved"                 //param : groups

    ,MODEL_RETRIEVED_GROUP_MEMBERS                            : "organization-hierarchy-group-members-retrieved"              //param : groupMembers

    ,MODEL_ADDED_GROUP_MEMBER                                 : "organization-hierarchy-group-members-added"                  //param : addedMember

    ,MODEL_ADDED_GROUP_SUPERVISOR                             : "organization-hierarchy-group-supervisor-added"                  //param : addedMember

    ,MODEL_REMOVED_GROUP_MEMBER                               : "organization-hierarchy-group-member-removed"                 //param : removedMember

    ,MODEL_RETRIEVED_USERS                                    : "organization-hierarchy-all-users-retrieved"                  //param : allUsers

    ,MODEL_RETRIEVED_FUNCTIONAL_ACCESS_CONTROL_APPLICATION_ROLES        : "functional-access-control-application-roles" 			  // param : roles
    	
    ,MODEL_ERROR_RETRIEVING_FUNCTIONAL_ACCESS_CONTROL_APPLICATION_ROLES : "error-functional-access-control-application-roles" // errorMsg
    	
    ,MODEL_RETRIEVED_FUNCTIONAL_ACCESS_CONTROL_GROUPS 		            : "functional-access-control-groups" 			                  // param : groups
    	
    ,MODEL_ERROR_RETRIEVING_FUNCTIONAL_ACCESS_CONTROL_GROUPS 		    : "error-functional-access-control-groups" 			  // param : errorMsg
    	
    ,MODEL_RETRIEVED_FUNCTIONAL_ACCESS_CONTROL_APPLICATION_ROLES_TO_GROUPS          : "functional-access-control-application-roles-to-groups" // param : rolesToGroups
    	
    ,MODEL_ERROR_RETRIEVING_FUNCTIONAL_ACCESS_CONTROL_APPLICATION_ROLES_TO_GROUPS   : "error-functional-access-control-application-roles-to-groups" // param : rolesToGroups
    	
    ,MODEL_SAVE_FUNCTIONAL_ACCESS_CONTROL_APPLICATION_ROLES_TO_GROUPS               : "save-functional-access-control-application-roles-to-groups" // param: errorMsg
    	
    ,MODEL_ERROR_SAVING_FUNCTIONAL_ACCESS_CONTROL_APPLICATION_ROLES_TO_GROUPS       : "error-save-functional-access-control-application-roles-to-groups" // param: errorMsg

    ,VIEW_REPORT_CONFIGURATION_SAVED_REPORT_TO_GROUPS_MAP                      : "reports-configuration-view-saved-reports-to-groups-map"		  // param : reportToGroupsMap

    ,MODEL_REPORT_CONFIGURATION_RETRIEVED_REPORTS                                   : "reports-configuration-model-retrieved-reports" 			  // param : reports

    ,MODEL_REPORT_CONFIGURATION_RETRIEVED_GROUPS                                    : "reports-configuration-model-retrieved-groups" 			  // param : groups

    ,MODEL_REPORT_CONFIGURATION_RETRIEVED_REPORT_TO_GROUPS_MAP                 : "reports-configuration-model-retrieved-reports-to-groups-map"		  // param : reportToGroupsMap

    ,MODEL_REPORT_CONFIGURATION_SAVED_REPORT_TO_GROUPS_MAP                      : "reports-configuration-model-saved-reports-to-groups-map"		  // param : success

    ,MODEL_REPORT_CONFIGURATION_ERROR                                           : "reports-configuration-model-error"		                    // param : errorMsg

    ,viewCreatedAdHocGroup: function(group,parentId){
        Acm.Dispatcher.fireEvent(this.VIEW_CREATED_AD_HOC_GROUP, group,parentId);
    }

    ,viewMemberSearch: function(term){
        Acm.Dispatcher.fireEvent(this.VIEW_SEARCHED_MEMBERS, term);
    }

    ,viewRemovedGroupMember: function(removedMembers, parentGroupId){
        Acm.Dispatcher.fireEvent(this.VIEW_REMOVED_GROUP_MEMBER, removedMembers, parentGroupId);
    }

    ,viewRemovedGroup: function(groupId){
        Acm.Dispatcher.fireEvent(this.VIEW_REMOVED_GROUP, groupId);
    }

    ,viewChangedFacetSelection: function(selected) {
        Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_FACET_SELECTION, selected);
    }

    ,viewSubmittedQuery: function(term) {
        Acm.Dispatcher.fireEvent(this.VIEW_SUBMITTED_QUERY, term);
    }

    ,viewAddedMembers: function(addedMembers,parentGroupId){
        Acm.Dispatcher.fireEvent(this.VIEW_ADDED_GROUP_MEMBERS, addedMembers,parentGroupId);
    }

    ,viewAddedSupervisor: function(addedSupervisor,parentGroupId){
        Acm.Dispatcher.fireEvent(this.VIEW_ADDED_GROUP_SUPERVISOR, addedSupervisor,parentGroupId);
    }

    ,modelChangedResult: function(result) {
        Acm.Dispatcher.fireEvent(this.MODEL_CHANGED_RESULT, result);
    }
    ,modelChangedFacet: function(facet) {
        Acm.Dispatcher.fireEvent(this.MODEL_CHANGED_FACET, facet);
    }
    ,modelRetrievedError: function(errorMsg) {
        Acm.Dispatcher.fireEvent(this.MODEL_RETRIEVED_ERROR, errorMsg);
    }
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

    ,modelAddedGroupMember: function(addedMembers){
        Acm.Dispatcher.fireEvent(this.MODEL_ADDED_GROUP_MEMBER, addedMembers);
    }

    ,modelAddedGroupSupervisor: function(addedSupervisor){
        Acm.Dispatcher.fireEvent(this.MODEL_ADDED_GROUP_SUPERVISOR, addedSupervisor);
    }

    ,modelRetrievedUsers : function(allUsers){
        Acm.Dispatcher.fireEvent(this.MODEL_RETRIEVED_USERS, allUsers);
    }
    
    ,modelRetrievedFunctionalAccessControlApplicationRoles : function(roles) {
        Acm.Dispatcher.fireEvent(this.MODEL_RETRIEVED_FUNCTIONAL_ACCESS_CONTROL_APPLICATION_ROLES, roles);
    }
    
    ,modelErrorRetrievingFunctionalAccessControlApplicationRoles : function(errorMsg) {
        Acm.Dispatcher.fireEvent(this.MODEL_ERROR_RETRIEVING_FUNCTIONAL_ACCESS_CONTROL_APPLICATION_ROLES, errorMsg);
    }
    
    ,modelRetrievedFunctionalAccessControlGroups : function(groups) {
        Acm.Dispatcher.fireEvent(this.MODEL_RETRIEVED_FUNCTIONAL_ACCESS_CONTROL_GROUPS, groups);
    }
    
    ,modelErrorRetrievingFunctionalAccessControlGroups : function(errorMsg) {
        Acm.Dispatcher.fireEvent(this.MODEL_ERROR_RETRIEVING_FUNCTIONAL_ACCESS_CONTROL_GROUPS, errorMsg);
    }
    
    ,modelRetrievedFunctionalAccessControlApplicationRolesToGroups : function(rolesToGroups) {
        Acm.Dispatcher.fireEvent(this.MODEL_RETRIEVED_FUNCTIONAL_ACCESS_CONTROL_APPLICATION_ROLES_TO_GROUPS, rolesToGroups);
    }
    
    ,modelErrorRetrievingFunctionalAccessControlApplicationRolesToGroups : function(errorMsg) {
        Acm.Dispatcher.fireEvent(this.MODEL_ERROR_RETRIEVING_FUNCTIONAL_ACCESS_CONTROL_APPLICATION_ROLES_TO_GROUPS, errorMsg);
    }
    
    ,modelSaveFunctionalAccessControlApplicationRolesToGroups : function(applicationRolesToGroups) {
    	Acm.Dispatcher.fireEvent(this.MODEL_SAVE_FUNCTIONAL_ACCESS_CONTROL_APPLICATION_ROLES_TO_GROUPS, applicationRolesToGroups);
    }
    
    ,modelErrorSavingFunctionalAccessControlApplicationRolesToGroups : function(errorMsg) {
    	Acm.Dispatcher.fireEvent(this.MODEL_ERROR_SAVING_FUNCTIONAL_ACCESS_CONTROL_APPLICATION_ROLES_TO_GROUPS, errorMsg);
    }
    ,viewSavedReportToGroupsMap: function(reportToGroupsMap){
        Acm.Dispatcher.fireEvent(this.VIEW_REPORT_CONFIGURATION_SAVED_REPORT_TO_GROUPS_MAP, reportToGroupsMap);
    }
    ,modelReportConfigRetrievedReports: function(reports){
        Acm.Dispatcher.fireEvent(this.MODEL_REPORT_CONFIGURATION_RETRIEVED_REPORTS, reports);
    }
    ,modelReportConfigRetrievedGroups: function(groups){
        Acm.Dispatcher.fireEvent(this.MODEL_REPORT_CONFIGURATION_RETRIEVED_GROUPS, groups);
    }
    ,modelReportConfigRetrievedReportToGroupsMap: function(reportToGroupsMap){
        Acm.Dispatcher.fireEvent(this.MODEL_REPORT_CONFIGURATION_RETRIEVED_REPORT_TO_GROUPS_MAP, reportToGroupsMap);
    }

    ,modelReportConfigError: function(errorMsg){
        Acm.Dispatcher.fireEvent(this.MODEL_REPORT_CONFIGURATION_ERROR, errorMsg);
    }
    ,modelReportConfigSavedReportToGroupsMap: function(success){
        Acm.Dispatcher.fireEvent(this.MODEL_REPORT_CONFIGURATION_SAVED_REPORT_TO_GROUPS_MAP, success);
    }
}