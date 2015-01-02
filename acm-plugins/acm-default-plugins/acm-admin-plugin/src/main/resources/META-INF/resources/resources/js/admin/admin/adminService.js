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
        ,API_RETRIEVE_GROUPS        : "/api/latest/users/groups/get"

        ,createAdHocGroup: function(group){
            var url = App.getContextPath() + Admin.Service.Organization.API_GROUP + "/save";
            Acm.Service.asyncPost(
                function(response) {
                    if (response.hasError) {
                        Admin.Controller.modelCreatedAdHocGroup(response);

                    } else {
                        if (Admin.Model.Organization.validateGroup(response)) {
                            Admin.Controller.modelCreatedAdHocGroup(group);
                        }
                    }
                }
                ,url
                ,JSON.stringify(group)
            )

        }
        ,retrieveGroup : function(groupId){
            var url = App.getContextPath() + Admin.Service.Organization.API_GROUP + groupId + "/get";
            Acm.Service.asyncGet(
                function(response) {
                    if (response.hasError) {
                        var group = response.response.docs[0];
                        Admin.Controller.modelRetrievedGroup(group);

                    } else {
                        if (Admin.Model.Organization.validateGroup(response)) {
                            var group = response.response.docs[0];
                            Admin.Model.Organization.cacheGroup.put(group.name, group);
                            Admin.Controller.modelRetrievedGroup(group);
                        }
                    }
                }
                ,url
            )
        }

        ,retrieveGroups : function(groupId){
            var url = App.getContextPath() + Admin.Service.Organization.API_RETRIEVE_GROUPS;
            Acm.Service.asyncGet(
                function(response) {
                    if (response.hasError) {
                        var allGroups = response.response.docs;
                        Admin.Controller.modelRetrievedGroups(group);

                    } else {
                        if (Admin.Model.Organization.validateGroup(response)) {
                            var allGroups = response.response.docs;
                            Admin.Model.Organization.cacheAllGroups.put("allGroups", allGroups);
                            var subgroups = [];
                            var groups = [];
                            for(var i = 0; i<allGroups.length; i++){
                                var group = allGroups[i];
                                if(group.parent_type_s != null || group.parent_type_s == "GROUP"){
                                    subgroups.push(group);
                                }
                                else{
                                    groups.push(group);
                                }
                            }
                            if(subgroups !=null){
                                Admin.Model.Organization.cacheSubgroups.put("subgroups", subgroups);
                            }
                            if(groups !=null){
                                Admin.Model.Organization.cacheGroups.put("groups", groups);
                            }
                            //Admin.Model.Organization.cacheGroups.put("groups", groups);

                            Admin.Controller.modelRetrievedGroups(groups);
                        }
                    }
                }
                ,url
            )
        }

        ,retrieveGroupMembers : function(groupId){
            var url = App.getContextPath()+ Admin.Service.Organization.API_GROUP + groupId + "/get/members" ;
            Acm.Service.asyncGet(
                function(response) {
                    if (response.hasError) {
                        var groupMembers = response;
                        Admin.Controller.modelRetrievedGroup(groupMembers);

                    } else {
                        if (Admin.Model.Organization.validateGroup(response)) {
                            var groupMembers = response;
                            Admin.Model.Organization.cacheGroupMembers.put(groupId, groupMembers);
                            Admin.Controller.modelRetrievedGroupMembers(groupMembers);
                        }
                    }
                }
                ,url
            )
        }

        ,removeGroupMember : function(groupMember,parentGroupId){
            var url = App.getContextPath()+ Admin.Service.Organization.API_GROUP + parentGroupId + "/members/remove" ;
            Acm.Service.asyncPost(
                function(response) {
                    if (response.hasError) {
                        var removedMember = response;
                        Admin.Controller.modelRemovedGroupMember(removedMember);

                    } else {
                            var removedMember = response;
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
                        var removedGroup = response;
                        Admin.Controller.modelRemovedGroup(removedGroup);

                    } else {
                        if (Admin.Model.Organization.validateGroup(response)) {
                            var removedGroup = response;
                            var groups = Admin.Model.Organization.cacheGroups.get("groups");
                            var subGroups = Admin.Model.Organization.cacheSubgroups.get("subgroups");
                            var foundInGroup = false;
                            //first check in groups to remove the object manually from cache
                            for(var i = 0; i < groups.length; i++){
                                if(removedGroup.id == groups[i].object_id_s){
                                    groups.splice(i,1);
                                    foundInGroup = true;
                                    break;
                                }
                            }
                            //then check in subgroups to remove the object manually from cache
                            if(foundInGroup == false){
                                for(var j = 0; j < subGroups.length; j++){
                                    if(removedGroup.id == subGroups[i].object_id_s){
                                        subGroups.splice(i,1);
                                    }
                                }
                            }
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
};

