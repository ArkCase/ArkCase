'use strict';

angular.module('cases').controller('Cases.DocumentsController', ['$scope', '$stateParams', '$modal', '$q', '$timeout'
    , 'UtilService', 'ConfigService', 'ObjectService', 'Object.LookupService', 'Case.InfoService', 'DocTreeService'
    , 'Helper.ObjectBrowserService', 'Authentication', 'PermissionsService', 'Object.ModelService'
    , function ($scope, $stateParams, $modal, $q, $timeout
        , Util, ConfigService, ObjectService, ObjectLookupService, CaseInfoService, DocTreeService
        , HelperObjectBrowserService, Authentication, PermissionsService, ObjectModelService) {

        Authentication.queryUserInfo().then(
            function (userInfo) {
                $scope.user = userInfo.userId;
                return userInfo;
            }
        );

        $scope.treeControl = {};
        
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
            $scope.allowParentOwnerToCancel = config.docTree.allowParentOwnerToCancel;
        };

        ObjectLookupService.getFormTypes(ObjectService.ObjectTypes.CASE_FILE).then(
            function (formTypes) {
                $timeout(function() {
                    $scope.fileTypes = $scope.fileTypes || [];
                    $scope.fileTypes = $scope.fileTypes.concat(Util.goodArray(formTypes));
                }, 0);
                return formTypes;
            }
        );
        ObjectLookupService.getFileTypes().then(
            function (fileTypes) {
                $timeout(function() {
                    $scope.fileTypes = $scope.fileTypes || [];
                    $scope.fileTypes = $scope.fileTypes.concat(Util.goodArray(fileTypes));
                }, 0);
                return fileTypes;
            }
        );


        $scope.objectType = ObjectService.ObjectTypes.CASE_FILE;
        $scope.objectId = componentHelper.currentObjectId; //$stateParams.id;
        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;
            $scope.objectId = objectInfo.id;
            $scope.assignee = ObjectModelService.getAssignee(objectInfo);
        };


        $scope.uploadForm = function (type, folderId, onCloseForm) {
            return DocTreeService.uploadFrevvoForm(type, folderId, onCloseForm, $scope.objectInfo, $scope.fileTypes);
        };

        $scope.onInitTree = function(treeControl) {
            $scope.treeControl = treeControl;

            //$scope.treeControl.addCommandHandler({
            //    name: "sample"
            //    , onAllowCmd: function(nodes) {
            //        return "disable";
            //    }
            //    , onPreCmd: function(nodes, args) {
            //        console.log("onPreCmd of sample command");
            //        return false;
            //    }
            //    , onPostCmd: function(nodes, args) {
            //        console.log("onPostCmd of sample command");
            //    }
            //    , execute: function(nodes, args) {
            //        console.log("Possible to add onPreCmd code here");
            //        var promise = this.prevHandler.execute(nodes, args);
            //        $q.when(promise).then(function () {
            //            console.log("Possible to add onPostCmd code here, too");
            //        });
            //        console.log("Possible to add onPostCmd code here");
            //    }
            //});

            $scope.treeControl.addCommandHandler({
                name: "checkout"
                , onAllowCmd: function(nodes) {
                    var fileObject = nodes[0].data;
                    var lock = fileObject.lock;
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
            });

            $scope.treeControl.addCommandHandler({
                name: "checkin"
                , onAllowCmd: function(nodes) {
                    var fileObject = nodes[0].data;
                    var lock = fileObject.lock;
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
            });


            $scope.treeControl.addCommandHandler({
                name: "editWithWord"
                , onAllowCmd: function(nodes) {
                    var fileObject = nodes[0].data;
                    var lock = fileObject.lock;
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
            });


            $scope.treeControl.addCommandHandler({
                name: "cancelEditing"
                , onAllowCmd: function(nodes) {
                    var fileObject = nodes[0].data;
                    var lock = fileObject.lock;
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
            });

        };

        $scope.onClickRefresh = function () {
            $scope.treeControl.refreshTree();
        };

    }

]);
