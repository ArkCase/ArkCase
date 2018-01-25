angular.module('organizations').controller(
        'Organizations.DBAsModalController',
        [ '$scope', '$translate', '$modalInstance', 'Object.LookupService', 'params',
                function($scope, $translate, $modalInstance, ObjectLookupService, params) {
                    ObjectLookupService.getDBAsTypes().then(function(response) {
                        $scope.dbasTypes = response;
                    });

                    $scope.dba = params.dba;
                    $scope.isEdit = params.isEdit;
                    $scope.isDefault = params.isDefault;
                    $scope.hideNoField = params.isDefault;

                    $scope.onClickCancel = function() {
                        $modalInstance.dismiss('Cancel');
                    };
                    $scope.onClickOk = function() {
                        $modalInstance.close({
                            dba : $scope.dba,
                            isDefault : $scope.isDefault,
                            isEdit : $scope.isEdit
                        });
                    };
                } ]);