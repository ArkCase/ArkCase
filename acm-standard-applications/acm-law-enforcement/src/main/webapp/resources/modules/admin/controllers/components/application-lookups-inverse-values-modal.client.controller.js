'use strict';

angular.module('admin').controller('Admin.InverseValuesLookupModalController', ['$scope', '$modal', '$modalInstance', 'params',
        function ($scope, $modal, $modalInstance, params) {

            $scope.entry = angular.copy(params.entry);
            $scope.isEdit = angular.copy(params.isEdit);

            $scope.onClickCancel = function () {
                $modalInstance.dismiss('Cancel');
            };
            $scope.onClickOk = function () {
                $modalInstance.close(
                    {
                        entry: $scope.entry,
                        isEdit: $scope.isEdit
                    }
                );
            };

            $scope.pickLabel = function () {
                var modalInstance = $modal.open({
                    animation: true,
                    size: 'md',
                    backdrop: 'static',
                    resolve: {
                        params: function () {
                            return params;
                        }
                    },
                    templateUrl: "modules/admin/views/components/label-pickup-modal.client.view.html",
                    controller: 'Admin.LabelPickupClientController'
                });

                modalInstance.result.then(function (data) {
                    $scope.entry.key = data.entry.key;
                    $scope.entry.value = data.entry.value;

                });
            } //end pickLabel function


            $scope.pickInverseLabel = function () {
                var modalInstance = $modal.open({
                    animation: true,
                    size: 'md',
                    backdrop: 'static',
                    resolve: {
                        params: function () {
                            return params;
                        }
                    },
                    templateUrl: "modules/admin/views/components/label-pickup-modal.client.view.html",
                    controller: 'Admin.LabelPickupClientController'
                });

                modalInstance.result.then(function (data) {
                    $scope.entry.inverseKey = data.entry.key;
                    $scope.entry.inverseValue = data.entry.value;

                });
            } //end pickInverseLabel function
        }
    ]
);