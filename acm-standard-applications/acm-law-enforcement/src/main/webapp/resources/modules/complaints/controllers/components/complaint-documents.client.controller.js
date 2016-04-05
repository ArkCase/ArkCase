'use strict';

angular.module('complaints').controller('Complaints.DocumentsController', ['$scope', '$stateParams', '$modal'
    , 'UtilService', 'ConfigService', 'ObjectService', 'Object.LookupService', 'Complaint.InfoService'
    , 'Helper.ObjectBrowserService', 'DocTreeService', 'Authentication'
    , function ($scope, $stateParams, $modal
        , Util, ConfigService, ObjectService, ObjectLookupService, ComplaintInfoService
        , HelperObjectBrowserService, DocTreeService, Authentication) {

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
            , moduleId: "complaints"
            , componentId: "documents"
            , retrieveObjectInfo: ComplaintInfoService.getComplaintInfo
            , validateObjectInfo: ComplaintInfoService.validateComplaintInfo
            , onConfigRetrieved: function (componentConfig) {
                return onConfigRetrieved(componentConfig);
            }
            , onObjectInfoRetrieved: function (objectInfo) {
                onObjectInfoRetrieved(objectInfo);
            }
        });

        var onConfigRetrieved = function (config) {
            $scope.config = config;
            $scope.treeConfig = config.docTree;
        };

        ObjectLookupService.getFormTypes(ObjectService.ObjectTypes.COMPLAINT).then(
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


        $scope.objectType = ObjectService.ObjectTypes.COMPLAINT;
        $scope.objectId = componentHelper.currentObjectId; //$stateParams.id;
        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;
            $scope.objectId = objectInfo.complaintId;
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
                    if (!nodes[0].data.lock) {
                        return "disable";
                    }
                    else if (nodes[0].data.lock && odes[0].data.lock.creator !== $scope.user || !adminRole) {
                        return "disable";
                    }
                }
                else if ("checkout" == cmd) {
                    if (nodes[0].data.lock) {
                        return "disable";
                    }
                }
            }
        };

        $scope.onPreCmd = function (cmd, nodes) {
        };

        $scope.onPostCmd = function (cmd, nodes) {
        };
    }
]);