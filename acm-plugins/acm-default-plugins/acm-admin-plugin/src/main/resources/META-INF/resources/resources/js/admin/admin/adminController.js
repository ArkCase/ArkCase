/**
 * Created by manoj.dhungana on 12/4/2014.
 */

Admin.Controller = Admin.Controller || {
    create: function() {
    }
    ,onInitialized: function() {
    }
    ,VIEW_ORG_HIERARCHY_CREATED_AD_HOC_GROUP                                : "organization-hierarchy-view-created-adhoc-group"                              //param : group,parentId

    ,VIEW_ORG_HIERARCHY_REMOVED_MEMBERS                                     : "organization-hierarchy-view-removed-group-member"                             //param : removedMembers, parentGroupId

    ,VIEW_ORG_HIERARCHY_REMOVED_GROUP                                       : "organization-hierarchy-view-removed-group"                                    //param : groupId

    ,VIEW_ORG_HIERARCHY_ADDED_MEMBERS                                       : "organization-hierarchy-view-added-members"                                    //param : addedMembers,parentGroupId

    ,VIEW_ORG_HIERARCHY_ADDED_SUPERVISOR                                    : "organization-hierarchy-view-added-supervisor"                                 //param : addedSupervisor,parentGroupId

    ,VIEW_ORG_HIERARCHY_SEARCHED_MEMBERS                                    : "organization-hierarchy-view-searched--members"                                //param : term

    ,VIEW_ORG_HIERARCHY_CHANGED_FACET                                       : "organization-hierarchy-search-view-changed-facet-selection"                   //param: selected

    ,VIEW_ORG_HIERARCHY_SUBMITTED_QUERY                                     : "organization-hierarchy-search-view-submitted-query"                           //param: term

    ,MODEL_ORG_HIERARCHY_RETRIEVED_ERROR                                    : "organization-hierarchy-model-general-error"                                   //param: errorMsg

    ,MODEL_ORG_HIERARCHY_CHANGED_RESULT                                     : "organization-hierarchy-search-model-changed-result"                           //param: result

    ,MODEL_ORG_HIERARCHY_CHANGED_FACET                                      : "organization-hierarchy-search-model-changed-facet"                            //param: facet

    ,MODEL_CORRESPONDENCE_TEMPLATES_RETRIEVED_TEMPLATES                     : "admin-model-retrieved-correspondence-templates"                              //param : templatesList

    ,MODEL_ORG_HIERARCHY_CREATED_ADHOC_GROUP                                : "organization-hierarchy-model-created-group"                                  //param : group

    ,MODEL_ORG_HIERARCHY_REMOVED_ADHOC_GROUP                                : "organization-hierarchy-model-removed-group"                                  //param : removedGroup

    ,MODEL_ORG_HIERARCHY_RETRIEVED_GROUP                                    : "organization-hierarchy-model-retrieved-group"                                //param : group

    ,MODEL_ORG_HIERARCHY_RETRIEVED_GROUPS                                   : "organization-hierarchy-retrieved-all-groups"                                 //param : groups

    ,MODEL_ORG_HIERARCHY_RETRIEVED_MEMBERS                                  : "organization-hierarchy-retrieved-group-members"                              //param : groupMembers

    ,MODEL_ORG_HIERARCHY_ADDED_MEMBERS                                      : "organization-hierarchy-added-group-members"                                  //param : addedMember

    ,MODEL_ORG_HIERARCHY_ADDED_SUPERVISOR                                   : "organization-hierarchy-added-group-supervisor"                               //param : addedMember

    ,MODEL_ORG_HIERARCHY_REMOVED_MEMBER                                     : "organization-hierarchy-removed-group-member"                                 //param : removedMember

    ,MODEL_ORG_HIERARCHY_RETRIEVED_USERS                                    : "organization-hierarchy-retrieved-all-users"                                  //param : allUsers

    ,MODEL_FAC_RETRIEVED_APPLICATION_ROLES                                  : "functional-access-control-retrieved-application-roles" 			            // param : roles
    	
    ,MODEL_FAC_RETRIEVED_APPLICATION_ROLES_ERROR                            : "functional-access-control-retrieved-application-roles-error"                 // errorMsg
    	
    ,MODEL_FAC_RETRIEVED_GROUPS 		                                    : "functional-access-control-retrieved-groups" 			                        // param : groups
    	
    ,MODEL_FAC_RETRIEVED_ACCESS_CONTROL_GROUPS_ERROR 		                : "functional-access-control-retrieved-groups-error" 			                // param : errorMsg
    	
    ,MODEL_FAC_RETRIEVED_APPLICATION_ROLES_TO_GROUPS_MAP                    : "functional-access-control-retrieved-application-roles-to-groups"             // param : rolesToGroups
    	
    ,MODEL_FAC_RETRIEVED_APPLICATION_ROLES_TO_GROUPS_MAP_ERROR              : "functional-access-control-retrieved-error-application-roles-to-groups"       // param : rolesToGroups
    	
    ,MODEL_FAC_SAVED_APPLICATION_ROLES_TO_GROUPS                            : "functional-access-control-saved-application-roles-to-groups"                 // param: errorMsg
    	
    ,MODEL_FAC_RETRIEVED_SAVE_APPLICATION_ROLES_TO_GROUPS_ERROR             : "functional-access-control-retrieved-error-save-application-roles-to-groups"  // param: errorMsg

    ,VIEW_REPORT_CONFIGURATION_SAVED_REPORT_TO_GROUPS_MAP                   : "reports-configuration-view-saved-reports-to-groups-map"		                // param : reportToGroupsMap

    ,MODEL_REPORT_CONFIGURATION_RETRIEVED_REPORTS                           : "reports-configuration-model-retrieved-reports" 			                    // param : reports

    ,MODEL_REPORT_CONFIGURATION_RETRIEVED_GROUPS                            : "reports-configuration-model-retrieved-groups" 			                    // param : groups

    ,MODEL_REPORT_CONFIGURATION_RETRIEVED_REPORT_TO_GROUPS_MAP              : "reports-configuration-model-retrieved-reports-to-groups-map"		            // param : reportToGroupsMap

    ,MODEL_REPORT_CONFIGURATION_SAVED_REPORT_TO_GROUPS_MAP                  : "reports-configuration-model-saved-reports-to-groups-map"		                // param : success

    ,MODEL_REPORT_CONFIGURATION_RETRIEVED_ERROR                             : "reports-configuration-model-error"		                                    // param : errorMsg

    ,viewCreatedAdHocGroup: function(group,parentId){
        Acm.Dispatcher.fireEvent(this.VIEW_ORG_HIERARCHY_CREATED_AD_HOC_GROUP, group,parentId);
    }

    ,viewMemberSearch: function(term){
        Acm.Dispatcher.fireEvent(this.VIEW_ORG_HIERARCHY_SEARCHED_MEMBERS, term);
    }

    ,viewRemovedGroupMember: function(removedMembers, parentGroupId){
        Acm.Dispatcher.fireEvent(this.VIEW_ORG_HIERARCHY_REMOVED_MEMBERS, removedMembers, parentGroupId);
    }

    ,viewRemovedGroup: function(groupId){
        Acm.Dispatcher.fireEvent(this.VIEW_ORG_HIERARCHY_REMOVED_GROUP, groupId);
    }

    ,viewChangedFacetSelection: function(selected) {
        Acm.Dispatcher.fireEvent(this.VIEW_ORG_HIERARCHY_CHANGED_FACET, selected);
    }

    ,viewSubmittedQuery: function(term) {
        Acm.Dispatcher.fireEvent(this.VIEW_ORG_HIERARCHY_SUBMITTED_QUERY, term);
    }

    ,viewAddedMembers: function(addedMembers,parentGroupId){
        Acm.Dispatcher.fireEvent(this.VIEW_ORG_HIERARCHY_ADDED_MEMBERS, addedMembers,parentGroupId);
    }

    ,viewAddedSupervisor: function(addedSupervisor,parentGroupId){
        Acm.Dispatcher.fireEvent(this.VIEW_ORG_HIERARCHY_ADDED_SUPERVISOR, addedSupervisor,parentGroupId);
    }

    ,modelChangedResult: function(result) {
        Acm.Dispatcher.fireEvent(this.MODEL_ORG_HIERARCHY_CHANGED_RESULT, result);
    }
    ,modelChangedFacet: function(facet) {
        Acm.Dispatcher.fireEvent(this.MODEL_ORG_HIERARCHY_CHANGED_FACET, facet);
    }
    ,modelRetrievedError: function(errorMsg) {
        Acm.Dispatcher.fireEvent(this.MODEL_ORG_HIERARCHY_RETRIEVED_ERROR, errorMsg);
    }
    ,modelRetrievedCorrespondenceTemplates : function(templatesList) {
        Acm.Dispatcher.fireEvent(this.MODEL_CORRESPONDENCE_TEMPLATES_RETRIEVED_TEMPLATES, templatesList);
    }

    ,modelCreatedAdHocGroup: function(group){
        Acm.Dispatcher.fireEvent(this.MODEL_ORG_HIERARCHY_CREATED_ADHOC_GROUP, group);
    }

    ,modelRemovedGroup: function(removedGroup){
        Acm.Dispatcher.fireEvent(this.MODEL_ORG_HIERARCHY_REMOVED_ADHOC_GROUP, removedGroup);
    }

    ,modelRetrievedGroup: function(group){
        Acm.Dispatcher.fireEvent(this.MODEL_ORG_HIERARCHY_RETRIEVED_GROUP, group);
    }

    ,modelRetrievedGroups: function(groups){
        Acm.Dispatcher.fireEvent(this.MODEL_ORG_HIERARCHY_RETRIEVED_GROUPS, groups);
    }

    ,modelRetrievedGroupMembers: function(groupMembers){
        Acm.Dispatcher.fireEvent(this.MODEL_ORG_HIERARCHY_RETRIEVED_MEMBERS, groupMembers);
    }

    ,modelRemovedGroupMember: function(removedMember){
        Acm.Dispatcher.fireEvent(this.MODEL_ORG_HIERARCHY_REMOVED_MEMBER , removedMember);
    }

    ,modelAddedGroupMember: function(addedMembers){
        Acm.Dispatcher.fireEvent(this.MODEL_ORG_HIERARCHY_ADDED_MEMBERS, addedMembers);
    }

    ,modelAddedGroupSupervisor: function(addedSupervisor){
        Acm.Dispatcher.fireEvent(this.MODEL_ORG_HIERARCHY_ADDED_SUPERVISOR, addedSupervisor);
    }

    ,modelRetrievedUsers : function(allUsers){
        Acm.Dispatcher.fireEvent(this.MODEL_ORG_HIERARCHY_RETRIEVED_USERS, allUsers);
    }
    
    ,modelRetrievedFunctionalAccessControlApplicationRoles : function(roles) {
        Acm.Dispatcher.fireEvent(this.MODEL_FAC_RETRIEVED_APPLICATION_ROLES, roles);
    }
    
    ,modelErrorRetrievingFunctionalAccessControlApplicationRoles : function(errorMsg) {
        Acm.Dispatcher.fireEvent(this.MODEL_FAC_RETRIEVED_APPLICATION_ROLES_ERROR, errorMsg);
    }
    
    ,modelRetrievedFunctionalAccessControlGroups : function(groups) {
        Acm.Dispatcher.fireEvent(this.MODEL_FAC_RETRIEVED_GROUPS, groups);
    }
    
    ,modelErrorRetrievingFunctionalAccessControlGroups : function(errorMsg) {
        Acm.Dispatcher.fireEvent(this.MODEL_FAC_RETRIEVED_ACCESS_CONTROL_GROUPS_ERROR, errorMsg);
    }
    
    ,modelRetrievedFunctionalAccessControlApplicationRolesToGroups : function(rolesToGroups) {
        Acm.Dispatcher.fireEvent(this.MODEL_FAC_RETRIEVED_APPLICATION_ROLES_TO_GROUPS_MAP, rolesToGroups);
    }
    
    ,modelErrorRetrievingFunctionalAccessControlApplicationRolesToGroups : function(errorMsg) {
        Acm.Dispatcher.fireEvent(this.MODEL_FAC_RETRIEVED_APPLICATION_ROLES_TO_GROUPS_MAP_ERROR, errorMsg);
    }
    
    ,modelSaveFunctionalAccessControlApplicationRolesToGroups : function(applicationRolesToGroups) {
    	Acm.Dispatcher.fireEvent(this.MODEL_FAC_SAVED_APPLICATION_ROLES_TO_GROUPS, applicationRolesToGroups);
    }
    
    ,modelErrorSavingFunctionalAccessControlApplicationRolesToGroups : function(errorMsg) {
    	Acm.Dispatcher.fireEvent(this.MODEL_FAC_RETRIEVED_SAVE_APPLICATION_ROLES_TO_GROUPS_ERROR, errorMsg);
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
        Acm.Dispatcher.fireEvent(this.MODEL_REPORT_CONFIGURATION_RETRIEVED_ERROR, errorMsg);
    }
    ,modelReportConfigSavedReportToGroupsMap: function(success){
        Acm.Dispatcher.fireEvent(this.MODEL_REPORT_CONFIGURATION_SAVED_REPORT_TO_GROUPS_MAP, success);
    }
}