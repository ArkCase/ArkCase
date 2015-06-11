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
        // In this URL "n" was not provided. For that reason it always back the default value (which is 10).
        // If the application have more than 10 users, the rest of the users will not be shown in the organization hierarchy.
        ,API_RETRIEVE_USERS         : "/api/v1/plugin/search/advanced/USER?n=10000"
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

    , WorkflowConfiguration: {
        API_RETRIEVE_WORKFLOWS: "/api/latest/plugin/admin/workflowconfiguration/workflows"
        ,API_RETRIEVE_HISTORY: "/api/latest/plugin/admin/workflowconfiguration/workflows/{0}/versions/{1}/history"
        ,API_FILE: "/api/latest/plugin/admin/workflowconfiguration/workflows/{0}/versions/{1}/file"
        ,API_UPLOAD_FILE: "/api/latest/plugin/admin/workflowconfiguration/files"
        ,API_MAKE_ACTIVE: "/api/latest/plugin/admin/workflowconfiguration/workflows/{0}/versions/{1}/active"

        ,create: function(){

        }
        ,onInitialized: function() {

        }
        ,retrieveWorkflows : function(start, length, orderBy, isAsc) {
            var $dfd = jQuery.Deferred();
            var url = App.getContextPath() + Admin.Service.WorkflowConfiguration.API_RETRIEVE_WORKFLOWS;

            Acm.Service.asyncGet(
                function(response) {
                    if (response.hasError) {
                        $dfd.reject()
                    } else {
                        $dfd.resolve(response);
                    }
                }
                ,url
            );

            return $dfd.promise();
        }

        ,retrieveHistory : function(key, version) {
            var $dfd = jQuery.Deferred();
            var url = App.getContextPath() + Admin.Service.WorkflowConfiguration.API_RETRIEVE_HISTORY.format(key, version);

            Acm.Service.asyncGet(
                function(response) {
                    if (response.hasError) {
                        $dfd.reject();
                    } else {
                        $dfd.resolve(response);
                    }
                }
                ,url
            );

            return $dfd.promise();
        },

        makeActive: function(key, version) {
            var $dfd = jQuery.Deferred();
            var url = App.getContextPath() + Admin.Service.WorkflowConfiguration.API_MAKE_ACTIVE.format(key, version);

            Acm.Service.asyncPut(
                function(response) {
                    if (response.hasError) {
                        $dfd.reject()
                    } else {
                        $dfd.resolve(response);
                    }
                }
                ,url
            );

            return $dfd.promise();
        },

        getFileLink: function(key, version){
            return App.getContextPath() + Admin.Service.WorkflowConfiguration.API_FILE.format(key, version)
        },

        uploadWorkflowFile: function(fd) {
            var $dfd = jQuery.Deferred();
            var url = App.getContextPath() + Admin.Service.WorkflowConfiguration.API_UPLOAD_FILE;
            Acm.Service.asyncPostFormData(
                function(response) {
                    if (response.hasError) {
                        $dfd.reject();
                    } else {
                        $dfd.resolve(response);
                    }
                }
                ,url
                ,fd
            );

            return $dfd.promise();
        }

    }

    , LinkFormsWorkflows: {
        API_CONFIGURATION: "/api/latest/plugin/admin/linkformsworkflows/configuration"
        ,create: function(){

        }
        ,onInitialized: function() {

        }

        ,retrieveConfiguration: function(){
            var $dfd = jQuery.Deferred();
            var url = App.getContextPath() + Admin.Service.LinkFormsWorkflows.API_CONFIGURATION;
            Acm.Service.asyncGet(
                function(response) {
                    if (response.hasError) {
                        $dfd.reject()
                    } else {
                        $dfd.resolve(response);
                    }
                }
                , url
            );

            return $dfd.promise();
        }

        ,updateConfiguration: function(data) {
            var $dfd = jQuery.Deferred();
            var url = App.getContextPath() + Admin.Service.LinkFormsWorkflows.API_CONFIGURATION;
            Acm.Service.asyncPut(
                function(response) {
                    if (response.hasError) {
                        $dfd.reject()
                    } else {
                        $dfd.resolve(response);
                    }
                }
                , url
                ,JSON.stringify(data)
            );

            return $dfd.promise();
        }
    }

    , LDAPConfiguration: {
        API_RETRIEVE_LDAP_DIRECTORIES: "/api/latest/plugin/admin/ldapconfiguration/directories"
        ,API_CREATE_LDAP_DIRECTORY: "/api/latest/plugin/admin/ldapconfiguration/directories"
        ,API_LDAP_DIRECTORY: "/api/latest/plugin/admin/ldapconfiguration/directories/{0}"

        ,create: function(){

        }
        ,onInitialized: function() {

        }

        ,retrieveDirectories: function(){
            var $dfd = jQuery.Deferred();
            var url = App.getContextPath() + Admin.Service.LDAPConfiguration.API_RETRIEVE_LDAP_DIRECTORIES;

            Acm.Service.asyncGet(
                function(response) {
                    if (response.hasError) {
                        $dfd.reject()
                    } else {
                        $dfd.resolve(response);
                    }
                }
                , url
            );

            return $dfd.promise();
        }

        ,createDirectory: function (data) {
            var $dfd = jQuery.Deferred();
            var url = App.getContextPath() + Admin.Service.LDAPConfiguration.API_CREATE_LDAP_DIRECTORY;

            Acm.Service.asyncPost(
                function(response){
                    if (response.hasError) {
                        $dfd.reject(response)
                    } else {
                        $dfd.resolve(response);
                    }
                }
                ,url
                ,JSON.stringify(data, null, 4)
            )
            return $dfd.promise();
        }

        ,deleteDirectory: function (dirId) {
            var $dfd = jQuery.Deferred();
            var url = App.getContextPath() + Admin.Service.LDAPConfiguration.API_LDAP_DIRECTORY.format(dirId);
            Acm.Service.asyncDelete(
                function(response){
                    if (response.hasError) {
                        $dfd.reject(response)
                    } else {
                        $dfd.resolve(response);
                    }
                }
                ,url
            )
            return $dfd.promise();
        }

        ,updateDirectory: function (dirId, data) {
            var $dfd = jQuery.Deferred();
            var url = App.getContextPath() + Admin.Service.LDAPConfiguration.API_LDAP_DIRECTORY.format(dirId);
            Acm.Service.asyncPut(
                function(response){
                    if (response.hasError) {
                        $dfd.reject(response)
                    } else {
                        $dfd.resolve(response);
                    }
                }
                ,url
                ,JSON.stringify(data)
            )
            return $dfd.promise();
        }

    }


    ,LabelConfiguration: {
        API_SETTINGS: "/api/latest/plugin/admin/labelconfiguration/settings"
        ,API_RETRIEVE_NAMESPACES: "/api/latest/plugin/admin/labelconfiguration/namespaces"
        ,API_RETRIEVE_LANGUAGES: "/api/latest/plugin/admin/labelconfiguration/languages"
        ,API_RESOURCE:  "/api/latest/plugin/admin/labelconfiguration/admin-resource?lang={0}&ns={1}"
        ,API_RESET_RESOURCE:  "/api/latest/plugin/admin/labelconfiguration/admin-resource/reset"


        ,create: function(){

        }
        ,onInitialized: function() {
        },

        retrieveSettings: function(){
            var $dfd = jQuery.Deferred();
            var url = App.getContextPath() + Admin.Service.LabelConfiguration.API_SETTINGS;

            Acm.Service.asyncGet(
                function(response) {
                    if (response.hasError) {
                        var errorMsg = "Failed to retrieve resource settings:" + response.errorMsg;
                        $dfd.reject();
                    } else {
                        $dfd.resolve(response);
                    }
                }
                ,url
            );

            return $dfd.promise();
        },

        updateSettings: function(settings){
            var $dfd =jQuery.Deferred();
            var url = App.getContextPath() + Admin.Service.LabelConfiguration.API_SETTINGS;
            Acm.Service.asyncPut(
                function(response){
                    //if (response.hasError) {
                    //    var errorMsg = "Failed to save labels resource:" + response.errorMsg;
                    //    Admin.Controller.modelErrorRetrievingFunctionalAccessControlGroups(errorMsg);
                    //    $dfd.reject()
                    //} else {
                    //    $dfd.resolve(response);
                    //}
                    $dfd.resolve(response);
                }
                ,url
                ,JSON.stringify(settings, null, 4)
            );
            return $dfd.promise();
        },

        retrieveLanguages: function() {
            var $dfd = jQuery.Deferred();
            var url = App.getContextPath() + Admin.Service.LabelConfiguration.API_RETRIEVE_LANGUAGES;

            Acm.Service.asyncGet(
                function(response) {
                    if (response.hasError) {
                        var errorMsg = "Failed to retrieve resource languages:" + response.errorMsg;
                        Admin.Controller.modelErrorRetrievingFunctionalAccessControlGroups(errorMsg);
                        $dfd.reject()
                    } else {
                        $dfd.resolve(response);
                    }
                }
                ,url
            );

            return $dfd.promise();
        },

        retrieveNamespaces: function() {
            var $dfd = jQuery.Deferred();
            var url = App.getContextPath() + Admin.Service.LabelConfiguration.API_RETRIEVE_NAMESPACES;

            Acm.Service.asyncGet(
                function(response) {
                    if (response.hasError) {
                        var errorMsg = "Failed to retrieve resource namespaces:" + response.errorMsg;
                        Admin.Controller.modelErrorRetrievingFunctionalAccessControlGroups(errorMsg);
                        $dfd.reject()
                    } else {
                        $dfd.resolve(response);
                    }
                }
                ,url
            );

            return $dfd.promise();
        }

        ,retrieveResource: function(lang, namespace){
            var $dfd = jQuery.Deferred();

            var url = App.getContextPath() + Admin.Service.LabelConfiguration.API_RESOURCE.format(lang, namespace);

            Acm.Service.asyncGet(
                function(response) {
                    if (response.hasError) {
                        var errorMsg = "Failed to retrieve labels resource:" + response.errorMsg;
                        Admin.Controller.modelErrorRetrievingFunctionalAccessControlGroups(errorMsg);
                        $dfd.reject()
                    } else {
                        $dfd.resolve(response);
                    }
                }
                ,url
            )

            return $dfd.promise();
        }

        ,updateResource: function(lang, namespace, resource){
            var $dfd =jQuery.Deferred();
            var url = App.getContextPath() + Admin.Service.LabelConfiguration.API_RESOURCE.format(lang, namespace);
            Acm.Service.asyncPut(
                function(response){
                    //if (response.hasError) {
                    //    var errorMsg = "Failed to save labels resource:" + response.errorMsg;
                    //    Admin.Controller.modelErrorRetrievingFunctionalAccessControlGroups(errorMsg);
                    //    $dfd.reject()
                    //} else {
                    //    $dfd.resolve(response);
                    //}
                    $dfd.resolve(response);
                }
                ,url
                ,JSON.stringify(resource, null, 4)
            );
            return $dfd.promise();
        }

        ,resetResource: function(langs, namespaces){
            var $dfd =jQuery.Deferred();
            var lng = _.isArray(langs) ? langs: [langs];
            var ns = _.isArray(namespaces) ? namespaces: [namespaces];
            var url = App.getContextPath() + Admin.Service.LabelConfiguration.API_RESET_RESOURCE.format(lng, ns);
            var data = {
                lng: lng,
                ns: ns
            }
            Acm.Service.asyncPost(
                function(response){
                    //if (response.hasError) {
                    //    var errorMsg = "Failed to save labels resource:" + response.errorMsg;
                    //    Admin.Controller.modelErrorRetrievingFunctionalAccessControlGroups(errorMsg);
                    //    $dfd.reject()
                    //} else {
                    //    $dfd.resolve(response);
                    //}
                    $dfd.resolve(response);
                }
                ,url,
                JSON.stringify(data, null, 4)
            );
            return $dfd.promise();
        }
    }

    ,Logo: {
        create: function() {
        }

        ,onInitialized: function() {
        }

    }

    ,CustomCss: {
        API_CUSTOM_CSS: '/api/latest/plugin/admin/branding/customcss'

        ,create: function() {
        }

        ,onInitialized: function() {
        }

        ,retrieveCustomCss: function(){
            var url = App.getContextPath() + Admin.Service.CustomCss.API_CUSTOM_CSS;
            var $dfd = jQuery.Deferred();

            $.ajax({
                url: url,
                beforeSend: function(xhr) {
                    xhr.setRequestHeader('Accept', 'text/css');
                },
                type: 'GET'
            })
                .done(function(response) {
                    $dfd.resolve(response);
                })
                .fail(function(){
                    $dfd.reject();
                });

            return $dfd.promise();
        }

        ,updateCustomCss: function(customCss){
            var url = App.getContextPath() + Admin.Service.CustomCss.API_CUSTOM_CSS;
            var $dfd = jQuery.Deferred();

            Acm.Service.asyncPut(
                function(response) {
                    if (response.hasError) {
                        var errorMsg = "Failed to update custom CSS:" + response.errorMsg;
                        $dfd.reject();
                    } else {
                        $dfd.resolve(response);
                    }
                }
                ,url
                ,customCss
            );
            return $dfd.promise();
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

    ,RolesPrivileges: {
        create: function() {

        }

        ,onInitialized: function() {

        }

        ,API_ROLES: "/api/latest/plugin/admin/rolesprivileges/roles"
        ,API_RETRIEVE_PRIVILEGES: "/api/latest/plugin/admin/rolesprivileges/privileges"
        ,API_ROLE_PRIVILEGES: "/api/latest/plugin/admin/rolesprivileges/roles/{0}/privileges"

        ,retrieveApplicationRoles: function() {
            var $dfd = jQuery.Deferred();
            var url = App.getContextPath() + Admin.Service.RolesPrivileges.API_ROLES;
            Acm.Service.asyncGet(
                function(response) {
                    if (response.hasError) {
                        var errorMsg = "Failed to retrieve application roles: " + response.errorMsg;
                        $dfd.reject(errorMsg);
                        //Admin.Controller.modelErrorRetrievedRolesPrivilegesApplicationRoles(errorMsg);
                    } else {
                        if (Admin.Model.RolesPrivileges.validateApplicationRoles(response)) {
                            $dfd.resolve(response);
                            //var roles = response;
                            //Admin.Controller.modelRetrievedRolesPrivilegesApplicationRoles(roles);
                        }
                    }
                }
                ,url
            )

            return $dfd.promise();
        }

        ,createApplicationRole: function(roleName) {
            var $dfd = jQuery.Deferred();
            var url = App.getContextPath() + Admin.Service.RolesPrivileges.API_ROLES;
            var data = {
                roleName: roleName
            }
            Acm.Service.asyncPost(
                function(response) {
                    if (response.hasError) {
                        var errorMsg = "Failed to create role: " + response.errorMsg;
                        $dfd.reject(errorMsg);
                    } else {
                        $dfd.resolve(response);
                    }
                }
                ,url
                ,JSON.stringify(data)
            )

            return $dfd.promise();

        }

        ,retrieveApplicationPrivileges: function() {
            var $dfd = jQuery.Deferred();
            var url = App.getContextPath() + Admin.Service.RolesPrivileges.API_RETRIEVE_PRIVILEGES;
            Acm.Service.asyncGet(
                function(response) {
                    if (response.hasError) {
                        var errorMsg = "Failed to retrieve application privileges: " + response.errorMsg;
                        $dfd.reject(errorMsg);
                    } else {
                        if (Admin.Model.RolesPrivileges.validateApplicationPrivileges(response)) {
                            $dfd.resolve(response);
                        }
                    }
                }
                ,url
            )

            return $dfd.promise();
        }

        ,retrieveApplicationRolePrivileges: function(roleName) {
            var $dfd = jQuery.Deferred();
            var url = App.getContextPath() + Admin.Service.RolesPrivileges.API_ROLE_PRIVILEGES.format(roleName);
            Acm.Service.asyncGet(
                function(response) {
                    if (response.hasError) {
                        var errorMsg = "Failed to retrieve role privileges: " + response.errorMsg;
                        $dfd.reject(errorMsg);
                    } else {
                        if (Admin.Model.RolesPrivileges.validateApplicationRolePrivileges(response)) {
                            $dfd.resolve(response);
                        }
                    }
                }
                ,url
            )

            return $dfd.promise();
        }
        ,saveApplicationRolePrivileges: function(roleName, privileges) {
            var $dfd = jQuery.Deferred();
            var url = App.getContextPath() + Admin.Service.RolesPrivileges.API_ROLE_PRIVILEGES.format(roleName);
            var data = {
              privileges: privileges
            };
            Acm.Service.asyncPut(
                function(response) {
                    if (response.hasError) {
                        var errorMsg = "Failed to save role privileges: " + response.errorMsg;
                        $dfd.reject(errorMsg);
                    } else {
                        if (Admin.Model.RolesPrivileges.validateApplicationRolePrivileges(response)) {
                            $dfd.resolve(response);
                        }
                    }
                }
                ,url
                , JSON.stringify(data)
            )

            return $dfd.promise();
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
                        var errorMsg = "Failed to retrieve application roles to groups map:" + response.errorMsg;

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
                        var errorMsg = "Failed to save application roles to groups map.";

                        Admin.Controller.modelErrorSavingFunctionalAccessControlApplicationRolesToGroups(errorMsg);
                    }
                }
                ,url
                ,JSON.stringify(applicationRolesToGroups)
            )
        }
    }

    ,ReportsConfiguration : {

        create: function() {
        }

        ,onInitialized: function() {
        }

        ,API_RETRIEVE_REPORTS: 			  "/api/latest/plugin/report/get/pentaho"
        ,API_RETRIEVE_GROUPS: 			  "/api/latest/users/groups/get"
        ,API_RETRIEVE_REPORT_TO_GROUPS_MAP:  "/api/latest/plugin/report/reporttogroupsmap"
        ,API_SAVE_REPORT_TO_GROUPS_MAP:      "/api/latest/plugin/report/reporttogroupsmap"

        ,retrieveReports : function() {
            var url = App.getContextPath() + Admin.Service.ReportsConfiguration.API_RETRIEVE_REPORTS;
            Acm.Service.asyncGet(
                function(response) {
                    if (response.hasError) {
                        var errorMsg = "Failed to retrieve application roles:" + response.errorMsg;
                        Admin.Controller.modelReportConfigError(errorMsg);
                    } else {
                        if (Admin.Model.ReportsConfiguration.validateReports(response)) {
                            var reports = [];
                            for(var i = 0; i < response.length; i++){
                                reports.push(response[i].title);
                            }
                            Admin.Model.ReportsConfiguration.cacheReports.put("reports", reports);
                            Admin.Controller.modelReportConfigRetrievedReports(reports);
                        }
                    }
                }
                ,url
            )
        }

        ,retrieveGroups : function() {
            var url = App.getContextPath() + Admin.Service.ReportsConfiguration.API_RETRIEVE_GROUPS;
            Acm.Service.asyncGet(
                function(response) {
                    if (response.hasError) {
                        var errorMsg = "Failed to retrieve groups:" + response.errorMsg;
                        Admin.Controller.modelReportConfigError(errorMsg);
                    } else {
                        if (Admin.Model.ReportsConfiguration.validateGroup(response)) {
                            var groups = response.response.docs;
                            Admin.Model.ReportsConfiguration.cacheGroups.put("groups", groups);
                            Admin.Controller.modelReportConfigRetrievedGroups(groups);
                        }
                    }
                }
                ,url
            )
        }

        ,retrieveReportToGroupsMap : function() {
            var url = App.getContextPath() + Admin.Service.ReportsConfiguration.API_RETRIEVE_REPORT_TO_GROUPS_MAP;
            Acm.Service.asyncGet(
                function(response) {
                    if (response.hasError) {
                        var errorMsg = "Failed to retrieve reports to groups map:" + response.errorMsg;
                        Admin.Controller.modelReportConfigError(errorMsg);
                    } else {
                        if (Admin.Model.ReportsConfiguration.validateReportToGroupsMap(response)) {
                            var reportToGroupsMap = response;

                            Admin.Model.ReportsConfiguration.cacheReportToGroupsMap.put("reportToGroupsMap", reportToGroupsMap);
                            Admin.Controller.modelReportConfigRetrievedReportToGroupsMap(reportToGroupsMap);
                        }
                    }
                }
                ,url
            )
        }

        ,saveReportToGroupsMap : function(reportToGroupsMap){
            var url = App.getContextPath() + Admin.Service.ReportsConfiguration.API_SAVE_REPORT_TO_GROUPS_MAP;
            Acm.Service.asyncPost(
                function(response) {
                    if (false == response) {
                        var errorMsg = "Failed to save reports to groups map";
                        Admin.Controller.modelReportConfigError(errorMsg);
                    }
                    else if(true == response){
                        Admin.Controller.modelReportConfigSavedReportToGroupsMap(response);
                    }
                }
                ,url
                ,JSON.stringify(reportToGroupsMap)
            )
        }
    }

    ,Forms: {
    	create: function () {
    		if (Admin.Service.Forms.PlainForms.create)        	{Admin.Service.Forms.PlainForms.create();}
        }
        , onInitialized: function () {
        	if (Admin.Service.Forms.PlainForms.onInitialized)        	{Admin.Service.Forms.PlainForms.onInitialized();}
        }
        
        ,PlainForms:{
        	create: function () {
        		
            }
            , onInitialized: function () {
            	
            }
            
            ,API_RETRIEVE_PLAIN_FORMS: 			  "/api/latest/plugin/admin/plainforms"
            ,API_DELETE_PLAIN_FORM: 			  "/api/latest/plugin/admin/plainforms"
            ,API_RETRIEVE_PLAIN_FORM_TARGETS:	  "/api/latest/plugin/admin/plainform/targets"
            	
        	,retrievePlainForms: function() {
                var url = App.getContextPath() + Admin.Service.Forms.PlainForms.API_RETRIEVE_PLAIN_FORMS;
                Acm.Service.asyncGet(
                    function(response) {
                        if (response.hasError) {
                            var errorMsg = "Failed to retrieve plain forms:" + response.errorMsg;
                            Admin.Controller.modelReportConfigError(errorMsg);
                        } else {
                            if (Admin.Model.Forms.PlainForms.validatePlainForms(response)) {
                                Admin.Model.Forms.PlainForms.setPlainForms(response);
                                Admin.Controller.modelFormsConfigRetrievedPlainForms(response);
                            }
                        }
                    }
                    ,url
                )
            }
            
            ,deletePlainForm: function(key, target) {
                var url = App.getContextPath() + Admin.Service.Forms.PlainForms.API_DELETE_PLAIN_FORM + '/' + key + '/' + target;
                return Acm.Service.call({type: "DELETE"
                    ,url: url
                    ,callback: function(response) {
                        if (response.hasError) {
                        	var errorMsg = "Failed to delete plain form:" + response.errorMsg;
                            Admin.Controller.modelReportConfigError(errorMsg);
                        } else {
                        	Admin.Controller.modelFormsConfigDeletedPlainForm(response);
                            return true;
                        }
                    }
                });
            }
            
            ,retrievePlainFormTargets: function() {
                var url = App.getContextPath() + Admin.Service.Forms.PlainForms.API_RETRIEVE_PLAIN_FORM_TARGETS;
                Acm.Service.asyncGet(
                    function(response) {
                        if (response.hasError) {
                            var errorMsg = "Failed to retrieve plain form targets:" + response.errorMsg;
                            Admin.Controller.modelReportConfigError(errorMsg);
                        } else {
                            if (Admin.Model.Forms.PlainForms.validatePlainFormTargets(response)) {
                                Admin.Model.Forms.PlainForms.setPlainFormTargets(response);
                                Admin.Controller.modelFormsConfigRetrievedPlainFormTargets(response);
                            }
                        }
                    }
                    ,url
                )
            }
        }
    }
};

