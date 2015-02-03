/**
 * Created by manoj.dhungana on 12/4/2014.
 */

Admin.Service = {
    create : function() {
    }
    ,onInitialized: function() {
    }

    ,Organization: {
        create: function(){
        }
        ,onInitialized: function(){
        }
        ,API_GROUP                  : "/api/latest/users/group/"
        ,API_RETRIEVE_GROUPS        : "/api/latest/users/groups/get?n=50&s=create_date_tdt desc"
        ,API_RETRIEVE_USERS         : "/api/v1/plugin/search/advanced/USER"
        ,API_FACET_SEARCH_          : "/api/v1/plugin/search/facetedSearch?q="


        ,createAdHocGroup: function(group,parentId){
            var url = App.getContextPath() + Admin.Service.Organization.API_GROUP + "save";
            if(Acm.isNotEmpty(parentId)){
                url += "/" + parentId;
            }
            Acm.Service.asyncPost(
                function(response) {
                    if (response.hasError) {
                        Admin.Controller.modelRetrievedError(response.errorMsg);
                    } else {
                        if (Admin.Model.Organization.validateGroup(response)) {
                            if(Acm.isNotEmpty(parentId)){
                                var currentGroup = Admin.Model.Organization.Tree.getCurrentGroup();
                                if(currentGroup){
                                    if(currentGroup.children != null){
                                        var children = currentGroup.children;
                                        currentGroup.children.splice(0,children.length);
                                    }
                                    if(currentGroup.subgroups){
                                        currentGroup.subgroups.unshift(response.name);
                                    }
                                    else{
                                        currentGroup.subgroups = [];
                                        currentGroup.subgroups.unshift(response.name);
                                    }
                                }
                                var subGroups = Admin.Model.Organization.cacheSubgroups.get("subgroups");
                                var subgroup = Admin.Model.Organization.makeGroupData(response);
                                subgroup.isInGroupCache = false;
                                subGroups.unshift(subgroup);

                                Admin.Model.Organization.cacheSubgroups.put("subgroups",subGroups);
                                Admin.Model.Organization.Tree.sourceLoaded(false);
                                Admin.Controller.modelCreatedAdHocGroup(subgroup);
                            }
                            else{
                                var groups = Admin.Model.Organization.cacheGroups.get("groups");
                                var group = Admin.Model.Organization.makeGroupData(response);
                                group.isInGroupCache = true;
                                groups.unshift(group);
                                Admin.Model.Organization.cacheGroups.put("groups",groups);
                                Admin.Model.Organization.Tree.sourceLoaded(false);
                                Admin.Controller.modelCreatedAdHocGroup(group);

                            }
                        }
                    }
                }
                ,url
                ,JSON.stringify(group)
            )
        }
        ,addGroupMembers : function(groupMembers,parentGroupId){
            var url = App.getContextPath()+ Admin.Service.Organization.API_GROUP + parentGroupId + "/members/save" ;
            Acm.Service.asyncPost(
                function(response) {
                    if (response.hasError) {
                        Admin.Controller.modelRetrievedError(response.errorMsg);
                    }
                    else {
                        if (Admin.Model.Organization.validateUsers(response)) {
                            var currentGroup = Admin.Model.Organization.Tree.getCurrentGroup();
                            if (currentGroup) {
                                if (currentGroup.children != null) {
                                    var children = currentGroup.children;
                                    currentGroup.children.splice(0, children.length);
                                }
                                if (!currentGroup.members) {
                                    currentGroup.members = [];
                                }
                                else if (currentGroup.members) {
                                    //response contains all members, so have to clear the members from the cache to prevent duplicates
                                    currentGroup.members.splice(0, currentGroup.members.length);
                                }
                                for (var i = 0; i < response.members.length; i++) {
                                    var member = response.members[i];
                                    currentGroup.members.push(member.userId);
                                }
                                Admin.Model.Organization.Tree.sourceLoaded(false);
                                Admin.Controller.modelAddedGroupMember(currentGroup.members, parentGroupId);
                            }
                        }
                    }
                }
                ,url
                ,JSON.stringify(groupMembers)
            )
        }
        ,addGroupSupervisor : function(supervisor,parentGroupId){
            var url = App.getContextPath()+ Admin.Service.Organization.API_GROUP + parentGroupId + "/supervisor/save/false" ;
            Acm.Service.asyncPost(
                function(response) {
                    if (response.hasError) {
                        Admin.Controller.modelRetrievedError(response.errorMsg);
                    }
                    else {
                        if(Acm.isNotEmpty(response.supervisor)){
                            var currentGroup = Admin.Model.Organization.Tree.getCurrentGroup();
                            if(currentGroup){
                                if(currentGroup.children != null){
                                    var children = currentGroup.children;
                                    currentGroup.children.splice(0,children.length);
                                }
                                if(!currentGroup.supervisor) {
                                    currentGroup.supervisor = null;
                                }
                                currentGroup.supervisor = response.supervisor.userId;
                                Admin.Model.Organization.Tree.sourceLoaded(false);
                                Admin.Controller.modelAddedGroupSupervisor(currentGroup.supervisor,parentGroupId);
                            }
                        }
                    }
                }
                ,url
                ,JSON.stringify(supervisor)
            )
        }

        ,retrieveGroups : function(){
            var url = App.getContextPath() + Admin.Service.Organization.API_RETRIEVE_GROUPS;
            Acm.Service.asyncGet(
                function(response) {
                    if (response.hasError) {
                        Admin.Controller.modelRetrievedError(response.errorMsg);
                    }
                    else {
                        if (Admin.Model.Organization.validateSolrResponse(response)) {
                            var allGroups = response.response.docs;
                            //create arrays to make fancytree structure
                            var subgroups = [];
                            var groups = [];
                            for(var i = 0; i < allGroups.length; i++){
                                var selGroup = allGroups[i];
                                if(selGroup.parent_type_s != null || selGroup.parent_type_s == "GROUP"){
                                    var subgroup = Admin.Model.Organization.makeGroupDataFromSolrResponse(selGroup);
                                    subgroup.isInGroupCache = false;
                                    subgroups.push(subgroup);
                                }
                                else{
                                    var group = Admin.Model.Organization.makeGroupDataFromSolrResponse(selGroup);
                                    group.isInGroupCache = true;
                                    groups.push(group);
                                }
                            }
                            if(subgroups != null){
                                Admin.Model.Organization.cacheSubgroups.put("subgroups", subgroups);
                            }
                            if(groups != null){
                                Admin.Model.Organization.cacheGroups.put("groups", groups);
                            }
                            Admin.Model.Organization.Tree.sourceLoaded(false);
                            Admin.Controller.modelRetrievedGroups(groups);
                        }
                    }
                }
                ,url
            )
        }

        ,retrieveUsers : function(){
            var url = App.getContextPath() + Admin.Service.Organization.API_RETRIEVE_USERS;
            Acm.Service.asyncGet(
                function(response) {
                    if (response.hasError) {
                        Admin.Controller.modelRetrievedError(response.errorMsg);
                    }
                    else {
                        if (Admin.Model.Organization.validateSolrResponse(response)) {
                            var acmUsersFromSolr = response.response.docs;
                            var membersForTree = [];
                            for(var i = 0; i < acmUsersFromSolr.length; i++){
                                var user = {};
                                user.title = acmUsersFromSolr[i].name;
                                user.lastname = acmUsersFromSolr[i].last_name_lcs;
                                user.object_id_s = acmUsersFromSolr[i].object_id_s;
                                user.isMember = true;
                                membersForTree.push(user);
                            }
                            Admin.Model.Organization.cacheAcmUsersFromSolr.put("acmUsersFromSolr", acmUsersFromSolr);
                            Admin.Model.Organization.cacheMembersForTree.put("membersForTree", membersForTree);
                            Admin.Controller.modelRetrievedUsers(membersForTree);
                        }
                    }
                }
                ,url
            )
        }

        ,facetSearchDeferred : function(searchInfo, postData, jtParams, sortMap, callbackSuccess, callbackError) {
            return AcmEx.Service.JTable.deferredPagingListAction(postData, jtParams, sortMap
                ,function() {
                    var url;
                    url =  App.getContextPath() + Admin.Service.Organization.API_FACET_SEARCH_;
                    url += searchInfo.q + '&filters=fq="Object Type":USER';
                    //url += searchInfo.q ;

                    //for test
                    //url = App.getContextPath() + "/resources/facetSearch.json?q=xyz";


                    var filter = Admin.Model.Organization.ModalDialog.Members.Facets.makeFilterParam(searchInfo);
                    url += filter;

                    return url;
                }
                ,function(data) {
                    var jtData = null
                    if (Admin.Model.Organization.ModalDialog.Members.Facets.validateFacetSearchData(data)) {
                        if (0 == data.responseHeader.status) {
                            //response.start should match to jtParams.jtStartIndex
                            //response.docs.length should be <= jtParams.jtPageSize

                            searchInfo.total = data.response.numFound;


                            var result = data.response;
                            //var page = Acm.goodValue(jtParams.jtStartIndex, 0);
                            //Search.Model.cacheResult.put(page, result);
                            Admin.Model.Organization.ModalDialog.Members.Facets.putCachedResult(searchInfo, result);
                            jtData = callbackSuccess(result);
                            Admin.Controller.modelChangedResult(Acm.Service.responseWrapper(data, result));

                            if (!Admin.Model.Organization.ModalDialog.Members.Facets.isFacetUpToDate()) {
                                var facet = Admin.Model.Organization.ModalDialog.Members.Facets.makeFacet(data);
                                Admin.Controller.modelChangedFacet(Acm.Service.responseWrapper(data, facet));
                                Admin.Model.Organization.ModalDialog.Members.Facets.setFacetUpToDate(true);
                            }
                        } else {
                            if (Acm.isNotEmpty(data.error)) {
                                //todo: report error to controller. data.error.msg + "(" + data.error.code + ")";
                            }
                        }
                    }
                    return jtData;
                }
            );
        }

        ,removeGroupMember : function(groupMember,parentGroupId){
            var url = App.getContextPath()+ Admin.Service.Organization.API_GROUP + parentGroupId + "/members/remove" ;
            Acm.Service.asyncPost(
                function(response) {
                    if (response.hasError) {
                        Admin.Controller.modelRetrievedError(response.errorMsg);
                    }
                    else{
                        var removedMember = groupMember[0].userId;
                        var currentGroup = Admin.Model.Organization.Tree.getCurrentGroup();
                        if(currentGroup){
                            if(currentGroup.title == parentGroupId){
                                if(currentGroup.children != null){
                                    var children = currentGroup.children;
                                    currentGroup.children.splice(0,children.length);
                                }
                                if(currentGroup.members != null){
                                    var members = currentGroup.members;
                                    for(var j = 0; j < members.length; j++){
                                        if(members[j] == removedMember){
                                            currentGroup.members.splice(j,1);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        Admin.Model.Organization.Tree.sourceLoaded(false);
                        Admin.Controller.modelRemovedGroupMember(removedMember);
                    }
                }
                ,url
                ,JSON.stringify(groupMember)
            )
        }

        ,removeGroup : function(groupId){
            var url = App.getContextPath()+ Admin.Service.Organization.API_GROUP + groupId + "/remove" ;
            Acm.Service.asyncDelete(
                function(response) {
                    if (response.hasError) {
                        Admin.Controller.modelRetrievedError(response.errorMsg);
                    } else {
                        if (Admin.Model.Organization.validateGroup(response)) {
                            var removedGroup = response;
                            var groups = Admin.Model.Organization.cacheGroups.get("groups");
                            var subGroups = Admin.Model.Organization.cacheSubgroups.get("subgroups");
                            var foundInGroup = false;
                            //first check in groups to remove the object manually from cache
                            for(var i = 0; i < groups.length; i++){
                                if(removedGroup.name == groups[i].title){
                                    groups.splice(i,1);
                                    Admin.Model.Organization.cacheGroups.put("groups",groups);
                                    foundInGroup = true;
                                    break;
                                }
                            }
                            //then check in subgroups to remove the object manually from cache
                            if(foundInGroup == false){
                                for(var j = 0; j < subGroups.length; j++){
                                    if(removedGroup.name == subGroups[j].title){
                                        subGroups.splice(j,1);
                                        Admin.Model.Organization.cacheSubgroups.put("subgroups",subGroups);
                                        break;
                                    }
                                }
                            }
                            Admin.Model.Organization.Tree.sourceLoaded(false);
                            Admin.Controller.modelRemovedGroup(removedGroup);
                        }
                    }
                }
                ,url
            )
        }


    }

    ,Correspondence : {

        create: function() {
        }
        ,onInitialized: function() {
        }
        ,API_RETRIEVE_TEMPLATES_LIST             : "/api/latest/plugin/admin/template/list"

        ,API_DOWNLOAD_TEMPLATE             : "/api/latest/plugin/admin/template?filePath="

        ,API_UPLOAD_TEMPLATE             : "/api/latest/plugin/admin/template"

        ,retrieveTemplatesList : function() {
            var url = App.getContextPath()+ Admin.Service.Correspondence.API_RETRIEVE_TEMPLATES_LIST;
            Acm.Service.asyncGet(
                function(response) {
                    if (response.hasError) {
                        var templatesList = response;
                        Admin.Controller.modelRetrievedCorrespondenceTemplates(templatesList);

                    } else {
                        if (Admin.Model.Correspondence.validateTemplatesList(response)) {
                            var templatesList = response;
                            Admin.Model.Correspondence.cacheTemplatesList.put(0, templatesList);
                            Admin.Controller.modelRetrievedCorrespondenceTemplates(templatesList);
                        }
                    }
                }
                ,url
            )
        }
        ,uploadTemplateFile : function(formData){
            var url = App.getContextPath() + Admin.Service.Correspondence.API_UPLOAD_TEMPLATE;
            Acm.Service.ajax({
                url: url
                ,data: formData
                ,processData: false
                ,contentType: false
                ,type: 'POST'
                ,success: function(response){
                    if (response.hasError) {
                        Admin.Controller.modelRetrievedCorrespondenceTemplates(response);
                    }
                    else {
                        if(response!= null){
                            Admin.Model.Correspondence.cacheTemplatesList.put(0, response);
                            Admin.Controller.modelRetrievedCorrespondenceTemplates(response);
                        }
                    }
                }
            });
        }
    }


    ,AccessControl : {
        create: function () {
        }
        , onInitialized: function () {
        }
        , API_RETRIEVE_ACCESS_CONTROL: "/api/latest/plugin/dataaccess/accessControlDefaults"
        , API_UPDATE_ACCESS_CONTROL: "/api/latest/plugin/dataaccess/default/"

        , retrieveAccessControlListDeferred: function (postData, jtParams, sortMap, callbackSuccess, callbackError) {
            //var pageIndex = jtParams.jtStartIndex;
            var pageIndex = jtParams.jtPageSize.toString() + jtParams.jtStartIndex.toString();

            return AcmEx.Service.JTable.deferredPagingListAction(postData, jtParams, sortMap
                , function () {
                    return  App.getContextPath() + Admin.Service.AccessControl.API_RETRIEVE_ACCESS_CONTROL;
                }
                , function (data) {
                    var jtData = null
                    if (Admin.Model.AccessControl.validateAccessControlList(data)) {
                        var results = data.resultPage;
                        //response.start should match to jtParams.jtStartIndex
                        //response.resultPage.length should be <= jtParams.jtPageSize

                        var accessControlList = [];
                        for (var i = 0; i < results.length; i++) {
                            var accessControl = {};
                            accessControl.id = results[i].id;
                            accessControl.objectType = results[i].objectType;
                            accessControl.objectState = results[i].objectState;
                            accessControl.accessLevel = results[i].accessLevel;
                            accessControl.accessorType = results[i].accessorType;
                            accessControl.accessDecision = results[i].accessDecision;
                            accessControl.allowDiscretionaryUpdate = (results[i].allowDiscretionaryUpdate) ? "true" : "false";
                            accessControlList.push(accessControl);
                        }
                        Admin.Model.AccessControl.cacheAccessControlList.put(pageIndex, accessControlList);
                        Admin.Model.setTotalCount(data.totalCount);
                        jtData = callbackSuccess(accessControlList);
                    }
                    return jtData;
                }
            );
        }
        , updateAdminAccess: function (data) {
            var url = App.getContextPath() + Admin.Service.AccessControl.API_UPDATE_ACCESS_CONTROL + data.id;
            Acm.Service.asyncPost(
                function (response) {
                    if (response.hasError) {
                        var accessControlList = response;
                        Admin.Controller.modelUpdatedAccessControl(accessControlList);
                    }
                    else {
                        //no action required
                        //var accessControlList = response;
                        //Admin.Controller.modelUpdatedAccessControl(accessControlList);
                    }
                }
                , url
                , JSON.stringify(data)
            )
        }
    }

    ,FunctionalAccessControl : {

        create: function() {
        }

        ,onInitialized: function() {
        }

        ,API_RETRIEVE_APPLICATION_ROLES: 			"/api/latest/functionalaccess/roles"
        ,API_RETRIEVE_GROUPS: 						"/api/latest/users/groups/get"
        ,API_RETRIEVE_APPLICATION_ROLES_TO_GROUPS:  "/api/latest/functionalaccess/rolestogroups"
        ,API_SAVE_APPLICATION_ROLES_TO_GROUPS:      "/api/latest/functionalaccess/rolestogroups"

        ,retrieveApplicationRoles : function() {
            var url = App.getContextPath() + Admin.Service.FunctionalAccessControl.API_RETRIEVE_APPLICATION_ROLES;
            Acm.Service.asyncGet(
                function(response) {
                    if (response.hasError) {
                        var errorMsg = "Failed to retrieve application roles:" + response.errorMsg;

                        Admin.Controller.modelErrorRetrievingFunctionalAccessControlApplicationRoles(errorMsg);
                    } else {
                        if (Admin.Model.FunctionalAccessControl.validateApplicationRoles(response)) {
                            var roles = response;
                            Admin.Model.FunctionalAccessControl.cacheApplicationRoles.put(0, roles);
                            Admin.Controller.modelRetrievedFunctionalAccessControlApplicationRoles(roles);
                        }
                    }
                }
                ,url
            )
        }

        ,retrieveGroups : function() {
            var url = App.getContextPath() + Admin.Service.FunctionalAccessControl.API_RETRIEVE_GROUPS;
            Acm.Service.asyncGet(
                function(response) {
                    if (response.hasError) {
                        var errorMsg = "Failed to retrieve groups:" + response.errorMsg;

                        Admin.Controller.modelErrorRetrievingFunctionalAccessControlGroups(errorMsg);
                    } else {
                        if (Admin.Model.FunctionalAccessControl.validateGroups(response)) {
                            var groups = response.response.docs;

                            Admin.Model.FunctionalAccessControl.cacheGroups.put(0, groups);
                            Admin.Controller.modelRetrievedFunctionalAccessControlGroups(groups);
                        }
                    }
                }
                ,url
            )
        }

        ,retrieveApplicationRolesToGroups : function() {
            var url = App.getContextPath() + Admin.Service.FunctionalAccessControl.API_RETRIEVE_APPLICATION_ROLES_TO_GROUPS;
            Acm.Service.asyncGet(
                function(response) {
                    if (response.hasError) {
                        var errorMsg = "Failed to retrieve application roles to groups mapping:" + response.errorMsg;

                        Admin.Controller.modelErrorRetrievingFunctionalAccessControlApplicationRolesToGroups(errorMsg);
                    } else {
                        if (Admin.Model.FunctionalAccessControl.validateApplicationRolesToGroups(response)) {
                            var rolesToGroups = response;

                            Admin.Model.FunctionalAccessControl.cacheApplicationRolesToGroups.put(0, rolesToGroups);
                            Admin.Controller.modelRetrievedFunctionalAccessControlApplicationRolesToGroups(rolesToGroups);
                        }
                    }
                }
                ,url
            )
        }

        ,saveApplicationRolesToGroups : function(applicationRolesToGroups){
            var url = App.getContextPath() + Admin.Service.FunctionalAccessControl.API_SAVE_APPLICATION_ROLES_TO_GROUPS;
            Acm.Service.asyncPost(
                function(response) {
                    if (response !== true) {
                        var errorMsg = "Failed to save application roles to groups mapping.";

                        Admin.Controller.modelErrorSavingFunctionalAccessControlApplicationRolesToGroups(errorMsg);
                    }
                }
                ,url
                ,JSON.stringify(applicationRolesToGroups)
            )
        }
    }
};

