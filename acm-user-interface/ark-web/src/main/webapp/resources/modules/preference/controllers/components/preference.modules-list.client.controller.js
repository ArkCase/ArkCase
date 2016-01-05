'use strict';

angular.module('preference').controller('Preference.ModulesListController', ['$scope', '$state', '$stateParams', 'ConfigService', 'dashboard',
  function($scope, $state, $stateParams, ConfigService, dashboard) {
    $scope.modules = ConfigService.queryModules();
    $scope.selectModule = selectModule;

    function selectModule(newActive) {
      var prevActive = _.find($scope.modules, {
        active: true
      });
      if (prevActive) {
        prevActive.active = false;
      }
      newActive.active = true;
      $scope.$emit('req-module-selected', newActive);
    }
  }
]);
