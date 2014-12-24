/**
 * Created by manoj.dhungana on 12/4/2014.
 */

Admin.Service = {
    create : function() {
    }
    ,onInitialized: function() {
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

