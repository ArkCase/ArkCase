'use strict';
angular.module('document-details').controller(
        'Document.ParticipantsController',
        [ '$scope', '$stateParams', '$q', '$modal', 'UtilService', 'ConfigService', 'Helper.UiGridService', 'ObjectService', 'Object.ParticipantService', 'Authentication', 'MessageService', '$translate', 'Object.LookupService', 'Object.ModelService', 'EcmService',
                function($scope, $stateParams, $q, $modal, Util, ConfigService, HelperUiGridService, ObjectService, ObjectParticipantService, Authentication, MessageService, $translate, ObjectLookupService, ObjectModelService, EcmService) {

                    $scope.participantsInit = {
                        moduleId: 'document-details',
                        componentId: 'participants',
                        objectId: $stateParams['id'],
                        showReplaceChildrenParticipants: false,
                        retrieveObjectInfo: ObjectParticipantService.getFileParticipantsAsObjectInfo,
                        validateObjectInfo: ObjectParticipantService.validateObjectParticipants,
                        saveObjectInfo: ObjectParticipantService.saveFileParticipants,
                        objectType: ObjectService.ObjectTypes.FILE,
                        participantsTitle: $translate.instant("common.directive.docTree.participantsDialog.title")
                    }
                } ]);