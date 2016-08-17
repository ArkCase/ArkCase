'use strict';

/**
 * @ngdoc service
 * @name services:DocTreeExt.Core
 *
 * @description
 *
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/directives/doc-tree/doc-tree-ext-core.client.service.js directives/doc-tree/doc-tree-ext-core.client.service.js}
 *
 * DocTree common extensions for core. Currently, a DocTree extension can add or customize tree table column or/and menu command.
 * We use name convention 'renderXxxx' to customize table column and 'handleXxxx' to customzie command
 */
angular.module('services').factory('DocTreeExt.Core', ['$q', 'UtilService', 'ObjectService', 'PermissionsService'
    , function ($q, Util, ObjectService, PermissionsService) {
        var Service = {};

        /**
         * @ngdoc method
         * @name handleCheckout
         * @methodOf services:DocTreeExt.Core
         *
         * @description
         * Add command handler for "checkout" to DocTree extension
         *
         * @param {Object} treeControl  Interface of functions to DocTree
         * @param {Object} scope  Angular scope
         *
         */
        Service.handleCheckout = function (treeControl, scope) {
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
        };


        /**
         * @ngdoc method
         * @name handleCheckin
         * @methodOf services:DocTreeExt.Core
         *
         * @description
         * Add command handler for "checkin" to DocTree extension
         *
         * @param {Object} treeControl  Interface of functions to DocTree
         * @param {Object} scope  Angular scope
         *
         */
        Service.handleCheckin = function (treeControl, scope) {
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
        };


        /**
         * @ngdoc method
         * @name handleEditWithWebDAV
         * @methodOf services:DocTreeExt.Core
         *
         * @description
         * Add command handler for "editWithWebDAV" to DocTree extension
         *
         * @param {Object} treeControl  Interface of functions to DocTree
         * @param {Object} scope  Angular scope
         *
         */
        Service.handleEditWithWebDAV = function (treeControl, scope) {
            treeControl.addCommandHandler({
                name: "editWithWebDAV"
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
        };


        /**
         * @ngdoc method
         * @name handleCancelEditing
         * @methodOf services:DocTreeExt.Core
         *
         * @description
         * Add command handler for "cancelEditing" to DocTree extension
         *
         * @param {Object} treeControl  Interface of functions to DocTree
         * @param {Object} scope  Angular scope
         *
         */
        Service.handleCancelEditing = function (treeControl, scope) {
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
        };

        return Service;
    }
]);
