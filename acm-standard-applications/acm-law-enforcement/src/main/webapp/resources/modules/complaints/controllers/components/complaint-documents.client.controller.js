'use strict';

angular.module('complaints').controller('Complaints.DocumentsController', ['$scope', '$stateParams', '$modal', '$q'
    , 'UtilService', 'ConfigService', 'ObjectService', 'Object.LookupService', 'Complaint.InfoService'
    , 'Helper.ObjectBrowserService', 'DocTreeService', 'Authentication', 'PermissionsService'
    , function ($scope, $stateParams, $modal, $q
        , Util, ConfigService, ObjectService, ObjectLookupService, ComplaintInfoService
        , HelperObjectBrowserService, DocTreeService, Authentication, PermissionsService) {

        Authentication.queryUserInfo().then(
            function (userInfo) {
                $scope.user = userInfo.userId;
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
                if ("checkin" == cmd) {
                    if (!nodes[0].data.lock) {
                        return "disable";
                    }
                    else if (nodes[0].data.lock && nodes[0].data.lock.creator !== $scope.user) {
                        return "disable";
                    }
                    else {
                        var allowDeffered = $q.defer();
                        //check permission for unlock
                        PermissionsService.getActionPermission('unlock', nodes[0].data)
                            .then(function success(hasPermission) {
                                    if (hasPermission)
                                        allowDeffered.resolve("");
                                    else
                                        allowDeffered.resolve("disable");
                                },
                                function error() {
                                    allowDeffered.resolve("disable");
                                }
                            );
                        return allowDeffered.promise;
                    }
                }
                else if ("cancelEditing" == cmd) {
                    if (!nodes[0].data.lock) {
                        return "disable";
                    }
                    else if (nodes[0].data.lock
                        && !(nodes[0].data.lock.creator == $scope.user
                        || nodes[0].data.creator == $scope.user)) {
                        return "disable";
                    }
                    else {
                        var allowDeffered = $q.defer();
                        //check permission for unlock
                        PermissionsService.getActionPermission('unlock', nodes[0].data)
                            .then(function success(hasPermission) {
                                    if (hasPermission)
                                        allowDeffered.resolve("");
                                    else
                                        allowDeffered.resolve("disable");
                                },
                                function error() {
                                    allowDeffered.resolve("disable");
                                }
                            );
                        return allowDeffered.promise;
                    }
                }
                else if ("checkout" == cmd) {
                    if (nodes[0].data.lock) {
                        return "disable";
                    } else {
                        var allowDeffered = $q.defer();
                        //check permission for lock
                        PermissionsService.getActionPermission('lock', nodes[0].data)
                            .then(function success(hasPermission) {
                                    if (hasPermission)
                                        allowDeffered.resolve("");
                                    else
                                        allowDeffered.resolve("disable");

                                },
                                function error() {
                                    allowDeffered.resolve("disable");
                                }
                            );
                        return allowDeffered.promise;
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