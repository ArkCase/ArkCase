'use strict';

/**
 * @ngdoc service
 * @name services:DocTreeExt.Checkin
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/directives/doc-tree/doc-tree-ext.checkin.client.service.js directives/doc-tree/doc-tree-ext.checkin.client.service.js}
 *
 * DocTree extensions for check-in, check-out functions.
 */
angular.module('services').factory('DocTreeExt.Checkin', ['$q', '$modal', '$translate', 'UtilService', 'LookupService'
    , 'Authentication', 'ObjectService', 'PermissionsService', 'Object.LockingService', 'Helper.NoteService', 'Object.NoteService'
    , 'Profile.UserInfoService'
    , function ($q, $modal, $translate, Util, LookupService
        , Authentication, ObjectService, PermissionsService, LockingService, HelperNoteService, ObjectNoteService
        , UserInfoService
    ) {
        var userId = "";
        Authentication.queryUserInfo().then(
            function (userInfo) {
                userId = userInfo.userId;
                return userInfo;
            }
        );

        var Service = {
            /**
             * @ngdoc method
             * @name getColumnRenderers
             * @methodOf services:DocTreeExt.Checkin
             *
             * @description
             * Return list of renderers this extension provides. This function is required for a docTree extension
             *
             * @param {Object} DocTree  DocTree object defined in doc-tree directive
             *
             */
            getColumnRenderers: function(DocTree) {
                return [
                    {
                        name: "lock",
                        renderer: function (element, node, columnDef, isReadOnly) {
                            $(element).empty();
                            if (Util.goodMapValue(node, "data.lock")) {
                                //UserInfoService.getUserInfoByIdQuietly(node.data.lock.creator).then(function (userInfo) {
                                UserInfoService.getUserInfoById(node.data.lock.creator).then(function (userInfo) {
                                    var lockedTitle = $translate.instant("common.directive.docTree.lockedTitle") + userInfo.fullName;
                                    var jqSpan = $("<span class='ui-icon ui-icon-locked' title='" + lockedTitle + "'/>").appendTo($(element));
                                    jqSpan.hover(function () {
                                        $(this).tooltip('show');
                                    }, function () {
                                        $(this).tooltip('hide');
                                    });
                                });
                            }
                            $(element).addClass("");
                        }
                    }
                ];
            }

            /**
             * @ngdoc method
             * @name getCommandHandlers
             * @methodOf services:DocTreeExt.Checkin
             *
             * @description
             * Return list of command handlers this extension provides. This function is required for a docTree extension
             *
             * @param {Object} DocTree  DocTree object defined in doc-tree directive
             *
             */
            ,getCommandHandlers: function(DocTree) {
                return [
                    {
                        name: "checkout",
                        execute: function (nodes, args) {
                            var node = nodes[0];
                            var fileId = node.data.objectId;
                            LockingService.lockObject(fileId, ObjectService.ObjectTypes.FILE,
                                ObjectService.LockTypes.CHECKOUT_LOCK, true).then(
                                function (lockedFile) {
                                    if (lockedFile) {
                                        DocTree._doDownload(node);

                                        node.data.lock = lockedFile;
                                        var cacheKey = DocTree.getCacheKeyByNode(node.parent);
                                        var folderList = DocTree.cacheFolderList.get(cacheKey);
                                        if (DocTree.Validator.validateFolderList(folderList)) {
                                            var locked = DocTree.findFolderItemIdx(fileId, folderList);
                                            if (0 <= locked) {
                                                folderList.children[locked].lock = lockedFile;
                                                DocTree.cacheFolderList.put(cacheKey, folderList);
                                                DocTree.refreshNode(node);
                                                return lockedFile;
                                            }
                                        }
                                    }
                                }
                            );
                        }
                    }
                    , {
                        name: "checkin",
                        execute: function (nodes, args) {
                            var selectFiles = DocTree.Command.findHandler("selectReplacement/");
                            selectFiles.execute(nodes, args);

                            $q.when(DocTree.uploadSetting.deferSelectFile.promise).then(function (files) {
                                args = args || {};
                                args.files = files;
                                var checkinFiles = DocTree.Command.findHandler("checkinFiles/");
                                DocTree.Command.handleCommand(checkinFiles, nodes, args);
                            });
                        }
                    }
                    , {
                        name: "cancelEditing",
                        execute: function (nodes, args) {
                            var node = nodes[0];
                            var fileId = node.data.objectId;
                            LockingService.unlockObject(fileId, ObjectService.ObjectTypes.FILE,
                                ObjectService.LockTypes.CANCEL_LOCK).then(
                                function (unlockedFile) {
                                    node.data.lock = "";
                                    var cacheKey = DocTree.getCacheKeyByNode(node.parent);
                                    var folderList = DocTree.cacheFolderList.get(cacheKey);
                                    if (unlockedFile) {
                                        var unlocked = DocTree.findFolderItemIdx(fileId, folderList);
                                        if (0 <= unlocked) {
                                            folderList.children[unlocked].lock = "";
                                            DocTree.cacheFolderList.put(cacheKey, folderList);
                                            DocTree.refreshNode(node);
                                            return unlockedFile;
                                        }
                                    }
                                }
                            );
                        }
                    }
                    , {
                        name: "checkinFiles/",
                        execute: function (nodes, args) {
                            return DocTree.Command.executeSubmitFiles(nodes, args);
                        },
                        onPostCmd: function (nodes, args) {
                            var node = nodes[0];
                            var noteHelper = new HelperNoteService.Note();
                            var fileId = node.data.objectId;
                            var isNoteRequired = Util.goodMapValue(DocTree, "treeConfig.noteRequiredOnCheckin", true);
                            var note = noteHelper.createNote(fileId, ObjectService.ObjectTypes.FILE, userId);
                            var params = {
                                config: Util.goodMapValue(DocTree.treeConfig, "comment", {}),
                                note: note,
                                isNoteRequired: isNoteRequired
                            };

                            var modalInstance = $modal.open({
                                templateUrl: "directives/doc-tree/doc-tree-ext.checkin.dialog.html"
                                , controller: 'directives.DocTreeCheckinDialogController'
                                , animation: true
                                , size: 'lg'
                                , resolve: {
                                    params: function () {
                                        return params;
                                    }
                                }
                            });

                            modalInstance.result.then(function (data) {
                                if (data.note.note != null && data.note.note.length > 0) {
                                    //we have text in note so we will save the note
                                    data.note.tag = node.data.version;
                                    ObjectNoteService.saveNote(data.note);
                                }

                                var cancelEditing = DocTree.Command.findHandler("cancelEditing");
                                DocTree.Command.handleCommand(cancelEditing, nodes);
                            });

                        }
                    }
                ];
            }

            /**
             * @ngdoc method
             * @name handleCheckout
             * @methodOf services:DocTreeExt.Checkin
             *
             * @description
             * Add command handler for "checkout" to DocTree extension
             *
             * @param {Object} treeControl  Interface of functions to DocTree
             * @param {Object} scope  Angular scope
             *
             */
            , handleCheckout: function (treeControl, scope) {
                treeControl.addCommandHandler({
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
            }
            /**
             * @ngdoc method
             * @name handleCheckin
             * @methodOf services:DocTreeExt.Checkin
             *
             * @description
             * Add command handler for "checkin" to DocTree extension
             *
             * @param {Object} treeControl  Interface of functions to DocTree
             * @param {Object} scope  Angular scope
             *
             */
            , handleCheckin: function (treeControl, scope) {
                treeControl.addCommandHandler({
                    name: "checkin"
                    , onAllowCmd: function(nodes) {
                        var fileObject = nodes[0].data;
                        var lock = fileObject.lock;
                        if (!lock) {
                            //there is no lock so checkin should be disabled
                            return "disable";
                        }
                        else if (lock
                            && lock.creator !== scope.user) {
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
            }
            /**
             * @ngdoc method
             * @name handleCancelEditing
             * @methodOf services:DocTreeExt.Checkin
             *
             * @description
             * Add command handler for "cancelEditing" to DocTree extension
             *
             * @param {Object} treeControl  Interface of functions to DocTree
             * @param {Object} scope  Angular scope
             *
             */
            , handleCancelEditing: function (treeControl, scope) {
                treeControl.addCommandHandler({
                    name: "cancelEditing"
                    , onAllowCmd: function(nodes) {
                        var fileObject = nodes[0].data;
                        var lock = fileObject.lock;
                        if (!lock) {
                            //there is no lock so cancel is disabled
                            return "disable";
                        }
                        else if (lock.creator == scope.user
                            || (scope.allowParentOwnerToCancel && lock.creator == scope.assignee)) {

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
            }
        };

        return Service;
    }
]);


angular.module('directives').controller('directives.DocTreeCheckinDialogController', ['$scope', '$modalInstance'
        , 'UtilService', 'params'
        , function ($scope, $modalInstance, Util, params) {
            $scope.modalInstance = $modalInstance;

            $scope.config = params.config;
            $scope.note = params.note;
            $scope.isNoteRequired = params.isNoteRequired;

            $scope.onClickCancel = function () {
                $modalInstance.dismiss();
            };
            $scope.onClickOk = function () {
                $modalInstance.close({note: $scope.note});
            };
        }
    ]
);

