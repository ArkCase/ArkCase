'use strict';

angular.module('complaints').controller('Complaints.DocumentsController', ['$scope', '$stateParams', '$modal', '$q'
    , 'UtilService', 'ConfigService', 'ObjectService', 'Object.LookupService', 'Complaint.InfoService'
    , 'Helper.ObjectBrowserService', 'DocTreeService', 'Authentication', 'PermissionsService', 'Object.ModelService'
    , function ($scope, $stateParams, $modal, $q
        , Util, ConfigService, ObjectService, ObjectLookupService, ComplaintInfoService
        , HelperObjectBrowserService, DocTreeService, Authentication, PermissionsService, ObjectModelService) {


        Authentication.queryUserInfo().then(
            function (userInfo) {
                $scope.user = userInfo.userId;
                return userInfo;
            }
        );

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

        $scope.uploadForm = function (type, folderId, onCloseForm) {
            return DocTreeService.uploadFrevvoForm(type, folderId, onCloseForm, $scope.objectInfo, $scope.fileTypes);
        };

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
            $scope.allowParentOwnerToCancel = config.docTree.allowParentOwnerToCancel;
        };


        $scope.objectType = ObjectService.ObjectTypes.COMPLAINT;
        $scope.objectId = componentHelper.currentObjectId; //$stateParams.id;
        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;
            $scope.objectId = objectInfo.complaintId;
            $scope.assignee = ObjectModelService.getAssignee(objectInfo);
        };

        $scope.onInitTree = function(treeControl) {
            $scope.treeControl = treeControl;

            //Sample usage
            //
            //$scope.treeControl.addColumnRenderer({
            //    name: "mimeType"
            //    , model: ["mimeType"]
            //    , renderer: function(element, node, columnDef, isReadOnly) {
            //        //$(element).text(node.data.mimeType);
            //
            //        //var html = "<button onclick=\"alert('hello')\">Click me</button>";
            //        var html = "<div onclick=\"alert('hello')\">Click me</div>";
            //        $(element).html(html);
            //    }
            //});

        };


        $scope.onClickRefresh = function () {
            $scope.treeControl.refreshTree();
        };

        $scope.onAllowCmd = function (cmd, nodes) {
            if (1 == nodes.length) {
                var fileObject = nodes[0].data;
                var lock = fileObject.lock;
                if ("checkin" == cmd) {
                    if (!lock) {
                        //there is no lock so checkin should be disabled
                        return "disable";
                    }
                    else if (lock
                        && lock.creator !== $scope.user) {
                        //there is lock on object but it is not by the user so checkin is disabled
                        return "disable";
                    }
                    else if (lock.lockType !== ObjectService.LockTypes.CHECKOUT_LOCK) {
                        //object has lock and it is by the user but it isn't checkout
                        //it is probably edit in word so checkin should be disabled
                        return "disable";
                    }
                    else {
                        //object has lock, lockType is checkout and user is creator

                        //user should be able to unlock the file with checkin if he had permisison
                        //to checkout the file, there is not need to check for permission for unlock
                        //but because we will use unlock service which is checking for unlock permission
                        //we should make the check because otherwise server will return access denied

                        var df = $q.defer();

                        //check permission for unlock
                        PermissionsService.getActionPermission('unlock', fileObject)
                            .then(function success(hasPermission) {
                                    if (hasPermission)
                                        df.resolve("");
                                    else
                                        df.resolve("disable");
                                },
                                function error() {
                                    df.resolve("disable");
                                }
                            );
                        return df.promise;
                    }
                }
                else if ("cancelEditing" == cmd) {
                    if (!lock) {
                        //there is no lock so cancel is disabled
                        return "disable";
                    }
                    else if (lock.creator == $scope.user
                        || ($scope.allowParentOwnerToCancel && lock.creator == $scope.assignee)) {

                        //object has lock and user is creator or owner of parent object
                        //so they should be able to unlock
                        //because backend will expect unlock permission we should check for it
                        var df = $q.defer();

                        PermissionsService.getActionPermission('unlock', fileObject)
                            .then(function success(hasPermission) {
                                    if (hasPermission)
                                        df.resolve("");
                                    else
                                        df.resolve("disable");
                                },
                                function error() {
                                    df.resolve("disable");
                                }
                            );
                    }
                    else {
                        //object has lock, the user is not creator or owner of parent object

                        //we will check for permissions if it is user that has permission to cancelLock (admin users)
                        //and after that for unlock because backend will return access denied without unlock permission
                        var df = $q.defer();

                        PermissionsService.getActionPermission('cancelLock', fileObject)
                            .then(function success(hasCancelPermission) {
                                    if (hasCancelPermission) {
                                        //check permission for unlock
                                        PermissionsService.getActionPermission('unlock', fileObject)
                                            .then(function success(hasPermission) {
                                                    if (hasPermission)
                                                        df.resolve("");
                                                    else
                                                        df.resolve("disable");
                                                },
                                                function error() {
                                                    df.resolve("disable");
                                                }
                                            );
                                    }
                                    else
                                        df.resolve("disable");
                                },
                                function error() {
                                    df.resolve("disable");
                                }
                            );
                        return df.promise;
                    }
                }
                else if ("checkout" == cmd || "editWithWord") {
                    if (lock) {
                        return "disable";
                    } else {
                        var df = $q.defer();
                        //check permission for lock
                        PermissionsService.getActionPermission('lock', fileObject)
                            .then(function success(hasPermission) {
                                    if (hasPermission)
                                        df.resolve("");
                                    else
                                        df.resolve("disable");

                                },
                                function error() {
                                    df.resolve("disable");
                                }
                            );
                        return df.promise;
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