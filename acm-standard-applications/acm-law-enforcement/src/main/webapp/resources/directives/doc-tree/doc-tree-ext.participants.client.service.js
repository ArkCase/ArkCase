'use strict';

/**
 * @ngdoc service
 * @name services:DocTreeExt.Participants
 *
 * @description
 *
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/directives/doc-tree/doc-tree-ext.paticipants.client.service.js directives/doc-tree/doc-tree-ext.email.participants.service.js}
 *
 * DocTree extensions for files and folders participants functions.
 */
angular.module('services').factory('DocTreeExt.Participants', ['$q', '$modal'
    , function ($q, $modal) {

        var Participants = {

            /**
             * @ngdoc method
             * @name getColumnRenderers
             * @methodOf services:DocTreeExt.Participants
             *
             * @description
             * No renderer is needed; return empty list of renderers.
             *
             * @param {Object} DocTree  DocTree object defined in doc-tree directive
             *
             */
            getColumnRenderers: function (DocTree) {
                return [];
            }

            /**
             * @ngdoc method
             * @name getCommandHandlers
             * @methodOf services:DocTreeExt.Participants
             *
             * @description
             * Return list of command handlers this extension provides. This function is required for a docTree extension
             *
             * @param {Object} DocTree  DocTree object defined in doc-tree directive
             *
             */
            , getCommandHandlers: function (DocTree) {
                return [
                    {
                        name: "showParticipants",
                        execute: function (nodes, args) {                            
                            Participants.openModal(nodes[0].data.objectId, nodes[0].data.name + (nodes[0].data.ext ? nodes[0].data.ext : ""), nodes[0].folder);
                        }
                    }
                ];
            }

            , openModal: function (objectId, objectName, isFolder) {
                var params = {
                    objectId: objectId,
                    objectName: objectName,
                    isFolder: isFolder
                };

                var modalInstance = $modal.open({
                    templateUrl: "directives/doc-tree/doc-tree-ext.participants.dialog.html"
                    , controller: 'directives.DocTreeParticipantsDialogController'
                    , animation: true
                    , size: 'lg'
                    , resolve: {
                        params: function () {
                            return params;
                        }
                    }
                });                
            }
        }; // end Participants

        return Participants;
    }
]);


angular.module('directives').controller('directives.DocTreeParticipantsDialogController', ['$scope', '$modalInstance', '$translate'
        , 'UtilService', 'params', '$modal', 'ObjectService', 'Object.ParticipantService'
        , function ($scope, $modalInstance, $translate, Util, params, $modal, ObjectService, ObjectParticipantService) {

            $scope.participantsInit = {
                moduleId: 'document-details',
                componentId: 'participants',
                objectId: params.objectId,
                showReplaceChildrenParticipants: params.isFolder ? true : false,
                retrieveObjectInfo: params.isFolder ? ObjectParticipantService.getFolderParticipantsAsObjectInfo : ObjectParticipantService.getFileParticipantsAsObjectInfo,
                validateObjectInfo: ObjectParticipantService.validateObjectParticipants,
                saveObjectInfo: params.isFolder ? ObjectParticipantService.saveFolderParticipants : ObjectParticipantService.saveFileParticipants,
                objectType: params.isFolder ? ObjectService.ObjectTypes.FOLDER : ObjectService.ObjectTypes.FILE,
                objectName: params.objectName,
                participantsTitle: $translate.instant("common.directive.docTree.participantsDialog.title")
            }
            
            $scope.closeDialog = function () {
                $modalInstance.close();
            }
        }
    ]
);