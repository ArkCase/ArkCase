'use strict';

angular.module('cases').controller('Cases.ParticipantsController', ['$scope', '$translate', 'Case.InfoService', 'ObjectService'
    , function ($scope, $translate, CaseInfoService, ObjectService) {

        $scope.participantsInit = {
            moduleId: 'cases',
            componentId: 'participants',
            showReplaceChildrenParticipants: true,
            retrieveObjectInfo: CaseInfoService.getCaseInfo,
            validateObjectInfo: CaseInfoService.validateCaseInfo,
            saveObjectInfo: CaseInfoService.saveCaseInfo,
            objectType: ObjectService.ObjectTypes.CASE_FILE,
            participantsTitle: $translate.instant("cases.comp.participants.title")
        }
    }
]);

