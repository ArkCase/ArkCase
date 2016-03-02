'use strict';

angular.module('admin').controller('Admin.LabelsConfigController', ['$scope', '$q', 'Admin.LabelsConfigService',
    function ($scope, $q, LabelsConfigService) {

        $scope.settings = {};
        $scope.disabledInputs = false;
        $scope.reloadGrid = reloadGrid;

        $scope.gridOptions = {
            enableColumnResizing: true,
            enableRowSelection: true,
            enableRowHeaderSelection: false,
            enableFiltering: true,
            multiSelect: false,
            noUnselect: false,
            columnDefs: [],
            totalItems: 0,
            data: [],
            onRegisterApi: function (gridApi) {
                $scope.gridApi = gridApi;
            }
        };

        $scope.config.$promise.then(function (config) {
            var labelsConfig = _.find(config.components, {id: 'labelsConfig'});
            var columnDefs = labelsConfig.columnDefs;

            $scope.gridOptions.columnDefs = columnDefs;
            var nsPromise = LabelsConfigService.retrieveNamespaces().$promise;
            var langPromise = LabelsConfigService.retrieveLanguages().$promise;
            var settingsPromise = LabelsConfigService.retrieveSettings().$promise;

            $q.all([nsPromise, langPromise, settingsPromise]).then(function(result){
                var namespaces = result[0];
                var langs = result[1];
                var settings = result[2];

                $scope.namespacesDropdownOptions = _.sortBy(namespaces, 'name');
                $scope.languagesDropdownOptions = langs;
                $scope.settings = settings;

                $scope.selectedNamespace = $scope.namespacesDropdownOptions[0];
                $scope.selectedLanguage = settings.defaultLang;
                $scope.selectedDefaultLanguage = settings.defaultLang;

                reloadGrid();

            });
        });

        function reloadGrid() {
            if ($scope.selectedNamespace && $scope.selectedLanguage) {
                $scope.disabledInputs = true;
                LabelsConfigService.retrieveResource({
                        lang: $scope.selectedLanguage,
                        ns: $scope.selectedNamespace.id
                    },
                    function (data) {
                        $scope.gridOptions.data = data;
                        $scope.disabledInputs = false;
                    },
                    function () {
                        $scope.disabledInputs = false;
                    });
            }
        }


        //changing default language
        $scope.changeDefaultLng = function (newLang) {
            $scope.settings.defaultLang = newLang;
            LabelsConfigService.updateSettings(
                angular.toJson($scope.settings)
            )
        };

        //reset all values to default for selected module from dropdown
        $scope.resetCurrentModuleResources = function () {
            $scope.disabledInputs = true;
            LabelsConfigService.resetResource({
                lng: [$scope.selectedLanguage],
                ns: [$scope.selectedNamespace.id]
            }, function () {
                reloadGrid();
            }, function(){
                $scope.disabledInputs = false;
            });
        };

        //reset all values to default for all modules
        $scope.resetAllResources = function () {
            var allNamespaces = _.pluck($scope.namespacesDropdownOptions, 'id');
            $scope.disabledInputs = true;
            LabelsConfigService.resetResource({
                lng: [$scope.selectedLanguage],
                ns: allNamespaces
            }, function () {
                reloadGrid();
            }, function(){
                $scope.disabledInputs = false;
            });
        };

        //updating value for Description for selected record in grid
        $scope.updateLabelDesc = function (desc, rowEntity) {
            LabelsConfigService.updateResource({
                lang: $scope.selectedLanguage,
                ns: $scope.selectedNamespace.id
            }, angular.toJson({
                id: rowEntity.id,
                value: rowEntity.value,
                description: desc
            }));
        };

        //updating value for Value for selected record in grid
        $scope.updateLabelValue = function (value, rowEntity) {
            LabelsConfigService.updateResource({
                lang: $scope.selectedLanguage,
                ns: $scope.selectedNamespace.id
            }, angular.toJson({
                id: rowEntity.id,
                value: value,
                description: rowEntity.description
            }));
        }
    }
]);