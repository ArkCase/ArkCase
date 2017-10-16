'use strict';

angular.module('people').controller('People.ParticipantsController', ['$scope', '$translate', 'Person.InfoService', 'ObjectService'
    , function ($scope, $translate, PersonInfoService, ObjectService) {

        $scope.participantsInit = {
            moduleId: 'people',
            componentId: 'participants',
            retrieveObjectInfo: PersonInfoService.getPersonInfo,
            validateObjectInfo: PersonInfoService.validatePersonInfo,
            saveObjectInfo: PersonInfoService.savePersonInfo,
            objectType: ObjectService.ObjectTypes.PERSON,
            participantsTitle: $translate.instant("people.comp.participants.title")
        }
    }
]);