'use strict';

angular.module('cases').controller('Cases.HistoryController', ['$scope', '$stateParams', '$q', 'UtilService', 'ValidationService', 'LookupService', 'CasesService',
    function ($scope, $stateParams, $q, Util, Validator, LookupService, CasesService) {
        $scope.$emit('req-component-config', 'history');

        var promiseUsers = Util.AcmGrid.getUsers($scope);

        $scope.$on('component-config', function (e, componentId, config) {
            if (componentId == 'history') {
                Util.AcmGrid.setColumnDefs($scope, config);
                Util.AcmGrid.setBasicOptions($scope, config);
                Util.AcmGrid.setExternalPaging($scope, config, $scope.retrieveGridData);
                Util.AcmGrid.setUserNameFilter($scope, promiseUsers);

                $scope.retrieveGridData();
            }
        });


        $scope.currentId = $stateParams.id;
        $scope.retrieveGridData = function () {
            if ($scope.currentId) {
                CasesService.queryAudit(Util.AcmGrid.withPagingParams($scope, {
                    id: $scope.currentId
                }), function (data) {
                    if (Validator.validateHistory(data)) {
                        promiseUsers.then(function () {
                            $scope.gridOptions.data = data.resultPage;
                            $scope.gridOptions.totalItems = data.totalCount;
                        });
                    }
                });
            }
        };
    }
]);