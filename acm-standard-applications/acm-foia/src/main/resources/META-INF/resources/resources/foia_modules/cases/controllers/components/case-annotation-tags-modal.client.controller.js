'use strict';

angular.module('cases').controller('Case.AnnotationTagsModalController',
    ['$scope', '$q', '$modalInstance', 'UtilService', 'params', 'Case.ExemptionService', '$stateParams', 'MessageService', '$translate', function ($scope, $q, $modalInstance, Utils, params, CaseExemptionService, $stateParams, MessageService, $translate) {

        $scope.allAnnotationTags = convertToKeyName(params.annotationTags);
        $scope.existingAnnotationTags = params.existingAnnotationTags;
        $scope.annotationTags = [];
        $scope.exemptionObjectModel = {};
        $scope.data = {
            "chooseObject": [],
            "selectedNotAuthorized": [],
            "selectedAuthorized": []
        };

        $scope.init = function () {
            $scope.data.selectedNotAuthorized = $scope.allAnnotationTags;
            $scope.data.selectedAuthorized = [];
            $scope.data.firstSelectHide = true;
            $scope.data.hideFilter = true;
        };
        $scope.onChange = function (selectedObject, authorized, notAuthorized) {
            var deferred = $q.defer();
            return deferred.promise;
        };

        $scope.init();

        $scope.onClickOk = function () {
            $scope.loading = true;
            $scope.annotationTags = convertToFlatArray($scope.data.selectedAuthorized);
            var arr = [];
            $scope.data.selectedAuthorized.forEach(function (selected) {
                if (!Utils.isEmpty($scope.existingAnnotationTags)) {
                    $scope.existingAnnotationTags.forEach(function (existing) {
                        if (selected.key === existing.exemptionCode) {
                            arr.push(selected.key);
                        }
                    })
                }
            });

            if (arr.length == 0) {
                $scope.exemptionObjectModel.exemptionCodes = $scope.annotationTags;
                $scope.exemptionObjectModel.parentObjectId = $stateParams.id;
                $scope.exemptionObjectModel.parentObjectType = "CASE_FILE";
                CaseExemptionService.saveExemptionCode($scope.exemptionObjectModel).then(function (value) {
                    $modalInstance.close(value.data);
                    MessageService.succsessAction();
                }, function () {
                    MessageService.errorAction();
                });
            } else if (arr.length == 1) {
                MessageService.error($translate.instant("requests.comp.exemption.determiner") + " " + arr + " " + $translate.instant("requests.comp.exemption.existingExemptionCodeError"));
            } else {
                MessageService.error($translate.instant("requests.comp.exemption.determiner") + " " + arr + " " + $translate.instant("requests.comp.exemption.existingExemptionCodesError"));
            }
            $modalInstance.close('done');
        };

        $scope.onClickCancel = function () {
            $modalInstance.dismiss('cancel');
        };

        function convertToKeyName(array) {
            if (!Utils.isArrayEmpty(array)) {
                return array.map(function (item) {
                    return {
                        "key": item["key"],
                        "name": item["key"],
                        "value": item["value"]
                    };
                });
            }

            return [];
        }

        function convertToFlatArray(array) {
            if (!Utils.isArrayEmpty(array)) {
                return array.map(function (item) {
                    return item["key"];
                });
            }

            return [];
        }
    }]);
