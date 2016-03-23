'use strict';

angular.module('cases').controller('Cases.PeopleController', ['$scope', '$translate', 'ObjectService', 'Case.InfoService'
    , function ($scope, $translate, ObjectService, CaseInfoService) {

        $scope.peopleInit = {
            moduleId: 'cases',
            componentId: 'people',
            retrieveObjectInfo: CaseInfoService.getCaseInfo,
            validateObjectInfo: CaseInfoService.validateCaseInfo,
            saveObjectInfo: CaseInfoService.saveCaseInfo,
            objectType: ObjectService.ObjectTypes.CASE_FILE,
            objectInfoId: 'id',
            peopleTitle: $translate.instant("cases.comp.people.title")
        }
    }
]);
