'use strict';

angular.module('cases').controller('Cases.PeopleController', ['$scope', 'ObjectService', 'Case.InfoService'
    , function ($scope, ObjectService, CaseInfoService) {

        $scope.peopleInit = {
            moduleId: 'cases',
            componentId: 'people',
            retrieveObjectInfo: CaseInfoService.getCaseInfo,
            validateObjectInfo: CaseInfoService.validateCaseInfo,
            saveObjectInfo: CaseInfoService.saveCaseInfo,
            objectType: ObjectService.ObjectTypes.CASE_FILE,
            objectInfoId: 'id'
        }
    }
]);
