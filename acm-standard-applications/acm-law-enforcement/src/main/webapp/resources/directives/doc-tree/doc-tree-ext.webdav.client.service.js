'use strict';

/**
 * @ngdoc service
 * @name services:DocTreeExt.WebDAV
 *
 * @description
 *
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/directives/doc-tree/doc-tree-ext.webdav.client.service.js directives/doc-tree/doc-tree-ext.webdav.client.service.js}
 *
 * DocTree extensions for WebDAV functions.
 */
angular.module('services').factory('DocTreeExt.WebDAV', ['$q', '$browser', '$location', 'UtilService'
    ,'ObjectService', 'PermissionsService', 'Object.LockingService', 'TicketService'
    , function ($q, $browser, $location, Util
        , ObjectService, PermissionsService, LockingService, TicketService
    ) {

        var Service = {
            /**
             * @ngdoc method
             * @name getColumnRenderers
             * @methodOf services:DocTreeExt.WebDAV
             *
             * @description
             * Return empty list of renderers because this extension does not customize any renderers. This function is required for a docTree extension
             *
             * @param {Object} DocTree  DocTree object defined in doc-tree directive
             *
             */
            getColumnRenderers: function(DocTree) {
                return [];
            }

            /**
             * @ngdoc method
             * @name getCommandHandlers
             * @methodOf services:DocTreeExt.WebDAV
             *
             * @description
             * Return list of command handlers this extension provides. This function is required for a docTree extension
             *
             * @param {Object} DocTree  DocTree object defined in doc-tree directive
             *
             */
            , getCommandHandlers: function(DocTree) {
                return [
                    {
                        name: "editWithWebDAV",
                        execute: function (nodes, args) {
                            var node = nodes[0];
                            var fileId = node.data.objectId;
                            var promiseTicket = TicketService.getArkCaseTicket();
                            promiseTicket.then(function (ticketData) {
                                var acmTicket = ticketData.data;
                                LockingService.lockObject(fileId, ObjectService.ObjectTypes.FILE,
                                    ObjectService.LockTypes.WORD_EDIT_LOCK, false).then(function (lockedFile) {
                                    var absUrl = $location.absUrl();
                                    var baseHref = $browser.baseHref();
                                    var appUrl = absUrl.substring(0, absUrl.indexOf(baseHref) + baseHref.length);

                                    ITHit.WebDAV.Client.DocManager.EditDocument(appUrl + "webdav/" + acmTicket + "/" + ObjectService.ObjectTypes.FILE + "/" + ObjectService.LockTypes.WORD_EDIT_LOCK + "/" +
                                        node.data.objectId + node.data.ext, appUrl + "webdav", protocolInstallMessage);
                                    function protocolInstallMessage(message) {
                                        var installerFilePath = appUrl + "assets/js/Plugins/" + ITHit.WebDAV.Client.DocManager.GetInstallFileName();

                                        var protocolMessage = "You must install a helper program to edit this file.  " +
                                            "Select 'OK' to download the helper program installer; and then install " +
                                            "the helper program by running the downloaded installer.  After " +
                                            "installing the helper program, edit this file again.";
                                        if (confirm(protocolMessage)) {
                                            window.open(installerFilePath);
                                        }
                                    }

                                    //refreshTree here won't work. It refreshes before the document is opened.
                                    //DocTree.refreshTree();

                                })
                            });
                        }
                    }
                ];
            }

            /**
             * @ngdoc method
             * @name handleEditWithWebDAV
             * @methodOf services:DocTreeExt.WebDAV
             *
             * @description
             * Add command handler for "editWithWebDAV" to DocTree extension
             *
             * @param {Object} treeControl  Interface of functions to DocTree
             * @param {Object} scope  Angular scope
             *
             */
            , handleEditWithWebDAV: function (treeControl, scope) {
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
            }

        };


        return Service;
    }
]);


