'use strict';

angular.module('request-info').controller('RequestInfo.AnnotationTagsManuallyModalController',
    ['$scope', '$q', '$modalInstance', 'UtilService', 'params', 'ExemptionService', '$stateParams', 'MessageService', '$translate', function ($scope, $q, $modalInstance, Utils, params, ExemptionService, $stateParams, MessageService, $translate) {

        $scope.allAnnotationTags = convertToKeyName(params.annotationTags);
        $scope.existingAnnotationTags = params.existingAnnotationTags;
        $scope.annotationTags = [];
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
            $scope.annotationTags = convertToFlatArray($scope.data.selectedAuthorized);
            var existingTags = $scope.existingAnnotationTags.map(function (code) {
                return code.exemptionCode;
            });
            var duplicateCodesArr = [];
            var copyAnnotationTags = _.cloneDeep($scope.annotationTags);
            copyAnnotationTags.forEach(function (selected, index) {
                if (_.includes(existingTags, selected)) {
                    duplicateCodesArr.push(selected);
                    $scope.annotationTags.splice($scope.annotationTags.indexOf(selected), 1);
                }
            });

            if ($scope.annotationTags.length > 0) {
                ExemptionService.saveDocumentExemptionCode($stateParams.fileId, $scope.annotationTags)
                    .then(function () {
                            if (duplicateCodesArr.length > 0) {
                                generateDuplicateCodeMessages(duplicateCodesArr);
                            } else {
                                MessageService.succsessAction();
                            }
                            $modalInstance.close('done');
                        }, function () {
                            MessageService.errorAction();
                            $modalInstance.close('done');
                        }
                    );
            } else {
                generateDuplicateCodeMessages(duplicateCodesArr);
                $modalInstance.close('done');
            }

        };

        function generateDuplicateCodeMessages(duplicateCodesArr) {
            if (duplicateCodesArr.length == 1) {
                MessageService.info($translate.instant("requests.comp.exemption.determiner") + " " + duplicateCodesArr + " " + $translate.instant("requests.comp.exemption.existingExemptionCodeError"));
            } else if (duplicateCodesArr.length > 1) {
                MessageService.info($translate.instant("requests.comp.exemption.determiner") + " " + duplicateCodesArr + " " + $translate.instant("requests.comp.exemption.existingExemptionCodesError"));
            }
        }

        $scope.onClickCancel = function () {
            $modalInstance.dismiss('cancel');
        };

        function convertToKeyName(array) {
            if (!Utils.isArrayEmpty(array)) {
                return array.map(function (item) {
                    return {"key": item["key"], "name": item["key"], "value": item["value"]};
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
