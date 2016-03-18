'use strict';

angular.module('cases').controller('Cases.ParticipantsController', ['$scope', 'Case.InfoService', 'ObjectService'
    , function ($scope, CaseInfoService, ObjectService) {

        $scope.participantsInit = {
            moduleId: 'cases',
            componentId: 'participants',
            retrieveObjectInfo: CaseInfoService.getCaseInfo,
            validateObjectInfo: CaseInfoService.validateCaseInfo,
            saveObjectInfo: CaseInfoService.saveCaseInfo,
            objectType: ObjectService.ObjectTypes.CASE_FILE
        }
    }
]);

