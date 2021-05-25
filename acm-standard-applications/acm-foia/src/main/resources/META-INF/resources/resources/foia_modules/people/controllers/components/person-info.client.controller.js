'use strict';

angular.module('people').controller('People.InfoController', ['$scope', '$stateParams', '$translate', '$q', 'Person.InfoService',
    'Helper.ObjectBrowserService', 'UtilService', 'Object.LookupService',
    function ($scope, $stateParams, $translate, $q, PersonInfoService, HelperObjectBrowserService, Util, ObjectLookupService) {

    new HelperObjectBrowserService.Component({
        scope: $scope,
        stateParams: $stateParams,
        moduleId: "people",
        componentId: "info",
        retrieveObjectInfo: PersonInfoService.getPersonInfo,
        validateObjectInfo: PersonInfoService.validatePersonInfo,
        onObjectInfoRetrieved: function (objectInfo) {
            onObjectInfoRetrieved(objectInfo);
        }
    });

    var prefixesPromise = ObjectLookupService.getPersonTitles();

    var positionsPromise = ObjectLookupService.getPersonOrganizationRelationTypes();

    var onObjectInfoRetrieved = function (objectInfo) {
        $q.all([prefixesPromise, positionsPromise]).then(function (data)
        {
            $scope.prefixes = data[0];
            $scope.personTitle = _.find($scope.prefixes, function (prefix) {
                return prefix.key === objectInfo.title
            });

            if (!$scope.personTitle) {
                $scope.personTitle = objectInfo.title;
            }

            var positions = data[1];
            if (objectInfo.defaultOrganization) {
                $scope.personPosition = _.find(positions, function (personPosition) {
                    return personPosition.key === objectInfo.defaultOrganization.personToOrganizationAssociationType;
                });
            } else $scope.personPosition = null;
        });
        $scope.objectInfo = objectInfo;
    };


    $scope.savePerson = function () {
        var promiseSaveInfo = Util.errorPromise($translate.instant("common.service.error.invalidData"));
        if (PersonInfoService.validatePersonInfo($scope.objectInfo)) {
            var objectInfo = Util.omitNg($scope.objectInfo);
            promiseSaveInfo = PersonInfoService.savePersonInfo(objectInfo);
            promiseSaveInfo.then(function (objectInfo) {
                $scope.$emit("report-object-updated", objectInfo);
                return objectInfo;
            }, function (error) {
                $scope.$emit("report-object-update-failed", error);
                return error;
            });
        }
        return promiseSaveInfo;
    };
}]);
