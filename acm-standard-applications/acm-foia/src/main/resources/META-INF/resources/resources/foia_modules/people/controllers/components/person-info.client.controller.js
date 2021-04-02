'use strict';

angular.module('people').controller('People.InfoController', [ '$scope', '$stateParams', '$translate', 'Person.InfoService', 'Helper.ObjectBrowserService', 'UtilService', 'Object.LookupService', function($scope, $stateParams, $translate, PersonInfoService, HelperObjectBrowserService, Util, ObjectLookupService) {

    new HelperObjectBrowserService.Component({
        scope: $scope,
        stateParams: $stateParams,
        moduleId: "people",
        componentId: "info",
        retrieveObjectInfo: PersonInfoService.getPersonInfo,
        validateObjectInfo: PersonInfoService.validatePersonInfo,
        onObjectInfoRetrieved: function(objectInfo) {
            onObjectInfoRetrieved(objectInfo);
        }
    });

    var onObjectInfoRetrieved = function(objectInfo)
    {
        $scope.personTitle = _.find($scope.prefixes, function(prefix)
        {
            return prefix.key === objectInfo.title
        });
        if (!$scope.personTitle)
        {
            $scope.personTitle = objectInfo.title;
        }
        if(objectInfo.defaultOrganization){
            $scope.personPosition = _.find($scope.positions, function(personPosition)
            {
                return personPosition.key === objectInfo.defaultOrganization.personToOrganizationAssociationType;
            });
        }

        // if (!$scope.personPosition)
        // {
        //     $scope.personPosition = objectInfo.defaultOrganization.personToOrganizationAssociationType;
        // }
        $scope.objectInfo = objectInfo;
    };

    ObjectLookupService.getPersonTitles().then(function (prefixes) {
        $scope.prefixes = prefixes;
        return prefixes;
    });
    ObjectLookupService.getPersonOrganizationRelationTypes().then(function (positions) {
        $scope.positions = positions;
        return positions;
    });

    $scope.savePerson = function() {
        var promiseSaveInfo = Util.errorPromise($translate.instant("common.service.error.invalidData"));
        if (PersonInfoService.validatePersonInfo($scope.objectInfo)) {
            var objectInfo = Util.omitNg($scope.objectInfo);
            promiseSaveInfo = PersonInfoService.savePersonInfo(objectInfo);
            promiseSaveInfo.then(function(objectInfo) {
                $scope.$emit("report-object-updated", objectInfo);
                return objectInfo;
            }, function(error) {
                $scope.$emit("report-object-update-failed", error);
                return error;
            });
        }
        return promiseSaveInfo;
    };
} ]);