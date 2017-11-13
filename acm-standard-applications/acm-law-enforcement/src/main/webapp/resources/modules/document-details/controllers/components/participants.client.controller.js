'use strict';
angular.module('document-details').controller('Document.ParticipantsController', ['$scope', '$stateParams', '$q', '$modal'
    , 'UtilService', 'ConfigService', 'Helper.UiGridService', 'ObjectService', 'Object.ParticipantService', 'Authentication', 'MessageService', '$translate',
    'Object.LookupService', 'Object.ModelService', 'EcmService',
    function ($scope, $stateParams, $q, $modal, Util, ConfigService, HelperUiGridService, ObjectService, ObjectParticipantService, Authentication, MessageService, $translate
        , ObjectLookupService, ObjectModelService, EcmService) {

		$scope.$on('document-data', function (event, ecmFile) {
		    $scope.objectId = ecmFile.fileId;
		    $scope.objectName = ecmFile.fileName;
        });
        
        $scope.participantsInit = {
            moduleId: 'document-details',
            componentId: 'participants',
            objectId: $scope.objectId,
            showReplaceChildrenParticipants: false,
            retrieveObjectInfo: ObjectParticipantService.getFileParticipantsAsObjectInfo,
            validateObjectInfo: ObjectParticipantService.validateObjectParticipants,
            saveObjectInfo: ObjectParticipantService.saveFileParticipants,
            objectType: ObjectService.ObjectTypes.FILE,
            objectName: $scope.fileName,
            participantsTitle: $translate.instant("common.directive.docTree.participantsDialog.title")
        }
    }
]);