'use strict';

angular.module('cases').controller('CasesListController', ['$scope', '$state', '$stateParams', 'CasesModelsService',
    function ($scope, $state, $stateParams, CasesModelsService) {
        $scope.casesData = CasesModelsService.queryCasesTree();

        $scope.onSelect = function (selectedCase) {
            $scope.$emit('req-select-case', selectedCase);
            $state.go('cases.' + selectedCase.type, {
                id: selectedCase.id
            });
        };
    }
]);