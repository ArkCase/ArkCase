'use strict';

angular.module('admin').controller('Admin.StandardLookupModalController', ['$scope', '$modalInstance', 'params',
        function ($scope, $modalInstance, params) {

            $scope.entry = params.entry;
            $scope.isEdit = params.isEdit;

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
        }
    ]
);