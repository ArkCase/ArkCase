'use strict';

angular.module('organizations').controller('Organizations.ParticipantsController', ['$scope', '$translate', 'Organization.InfoService', 'ObjectService'
    , function ($scope, $translate, OrganizationInfoService, ObjectService) {

        $scope.participantsInit = {
            moduleId: 'organizations',
            componentId: 'participants',
            retrieveObjectInfo: OrganizationInfoService.getOrganizationInfo,
            validateObjectInfo: OrganizationInfoService.validateOrganizationInfo,
            saveObjectInfo: OrganizationInfoService.saveOrganizationInfo,
            objectType: ObjectService.ObjectTypes.ORGANIZATION,
            participantsTitle: $translate.instant("organizations.comp.participants.title")
        }
    }
]);