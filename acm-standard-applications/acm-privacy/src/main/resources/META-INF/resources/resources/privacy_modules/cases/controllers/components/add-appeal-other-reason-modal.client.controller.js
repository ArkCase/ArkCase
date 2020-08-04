'use strict';

angular.module('cases').controller('Cases.AddAppealOtherReasonModalController',
    ['$scope', '$modal', '$modalInstance', 'params', '$q', 'UtilService', 'Object.LookupService',
        function ($scope, $modal, $modalInstance, params, $q, Util, ObjectLookupService) {
            $scope.objectInfo = {};
            $scope.objectInfo.dispositionReasons = params.dispositionReasons;

            ObjectLookupService.getAppealOtherReasons().then(function (appealOtherReasons) {
                $scope.otherReasonsLookup = appealOtherReasons;

                if (!Util.isEmpty(params.otherReason)) {
                    var found = _.find(appealOtherReasons, {
                        key: params.otherReason
                    });
                    if (found) {
                        $scope.objectInfo.otherReason = params.otherReason;
                    } else {
                        $scope.objectInfo.otherReason = 'custom';
                        $scope.customOtherReason = params.otherReason;
                    }

                    if ($scope.objectInfo.otherReason === 'custom') {
                        $scope.isCustomDisabled = false;
                    } else {
                        $scope.isCustomDisabled = true;
                    }
                } else {
                    $scope.objectInfo.otherReason = $scope.otherReasonsLookup[0].key;
                }

                if (Util.isEmpty($scope.objectInfo.otherReason)) {
                    var otherExistsInReason = _.some($scope.objectInfo.dispositionReasons, function (value) {
                        return value.reason === 'other';
                    });

                    if (otherExistsInReason) {
                        $scope.isOtherRequired = true;
                    } else {
                        $scope.isOtherRequired = false;
                    }
                }
            });

            $scope.changeOtherReason = function () {
                if (Util.isEmpty($scope.objectInfo.otherReason)) {
                    var otherExistsInReason = _.some($scope.objectInfo.dispositionReasons, function (value) {
                        return value.reason === 'other';
                    });

                    if (otherExistsInReason) {
                        $scope.isOtherRequired = true;
                    } else {
                        $scope.isOtherRequired = false;
                    }
                }

                if ($scope.objectInfo.otherReason === 'custom') {
                    $scope.isCustomDisabled = false;
                } else {
                    $scope.isCustomDisabled = true;
                    $scope.customOtherReason = null;
                }
            };

            $scope.onClickOk = function () {
                var data = {
                    otherReason: $scope.objectInfo.otherReason,
                    dispositionReasons: $scope.objectInfo.dispositionReasons
                };

                if ($scope.objectInfo.otherReason === 'custom') {
                    data.otherReason = $scope.customOtherReason;
                }

                $modalInstance.close(data);
            };

            $scope.onClickCancel = function () {
                _.forEach($scope.objectInfo.dispositionReasons, function (reason, i) {
                    if (!reason.hasOwnProperty("id")) {
                        $scope.objectInfo.dispositionReasons.splice(i, 1);
                    }
                });

                $modalInstance.dismiss('Cancel');
            };
        }]);
