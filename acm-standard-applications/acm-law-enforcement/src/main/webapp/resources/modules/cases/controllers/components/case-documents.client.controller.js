'use strict';

angular.module('cases').controller('Cases.DocumentsController', ['$scope', '$stateParams', '$modal'
    , 'UtilService', 'ConfigService', 'ObjectService', 'Object.LookupService', 'Case.InfoService', 'DocTreeService'
    , 'Helper.ObjectBrowserService', 'Authentication'
    , function ($scope, $stateParams, $modal
        , Util, ConfigService, ObjectService, ObjectLookupService, CaseInfoService, DocTreeService
        , HelperObjectBrowserService, Authentication) {

        var adminRole = false;

        Authentication.queryUserInfo().then(
            function(userInfo) {
                $scope.user = userInfo.userId;
                _.forEach(userInfo.authorities, function (authority) {
                    if (authority === 'ROLE_ADMINISTRATOR') {
                        adminRole = true;
                    }
                });
                return userInfo;
            }
        );

        var componentHelper = new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "cases"
            , componentId: "documents"
            , retrieveObjectInfo: CaseInfoService.getCaseInfo
            , validateObjectInfo: CaseInfoService.validateCaseInfo
            , onConfigRetrieved: function (componentConfig) {
                return onConfigRetrieved(componentConfig);
            }
            , onObjectInfoRetrieved: function (objectInfo) {
                onObjectInfoRetrieved(objectInfo);
            }
        });


        var onConfigRetrieved = function (config) {
            $scope.treeConfig = config.docTree;
        };

        ObjectLookupService.getFormTypes(ObjectService.ObjectTypes.CASE_FILE).then(
            function (formTypes) {
                $scope.fileTypes = $scope.fileTypes || [];
                $scope.fileTypes = $scope.fileTypes.concat(Util.goodArray(formTypes));
                return formTypes;
            }
        );
        ObjectLookupService.getFileTypes().then(
            function (fileTypes) {
                $scope.fileTypes = $scope.fileTypes || [];
                $scope.fileTypes = $scope.fileTypes.concat(Util.goodArray(fileTypes));
                return fileTypes;
            }
        );


        $scope.objectType = ObjectService.ObjectTypes.CASE_FILE;
        $scope.objectId = componentHelper.currentObjectId; //$stateParams.id;
        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectId = objectInfo.id;
        };


        $scope.uploadForm = function (type, folderId, onCloseForm) {
            return DocTreeService.uploadFrevvoForm(type, folderId, onCloseForm, $scope.objectInfo, $scope.fileTypes);
        };

        $scope.onClickRefresh = function () {
            $scope.treeControl.refreshTree();
        };

        $scope.onAllowCmd = function (cmd, nodes) {
            if (1 == nodes.length) {
                if ("checkin" == cmd || "cancelEditing" == cmd) {
                    if (!nodes[0].data.locked) {
                        return "disable";
                    }
                    else if (nodes[0].data.locked && nodes[0].data.creator !== $scope.user && nodes[0].data.modifier !== $scope.user || !adminRole) {
                        return "disable";
                    }
                }
                else if ("checkout" == cmd) {
                    if (nodes[0].data.locked) {
                        return "disable";
                    }
                }
            }
        };

        $scope.onPreCmd = function (cmd, nodes) {
            //console.log("onPreCmd:" + cmd);
            //if ("newFolder" == cmd) {
            //    return true;
            //}

        };

        $scope.onPostCmd = function (cmd, nodes) {
            //console.log("onPostCmd:" + cmd);
        };
    }
]);
