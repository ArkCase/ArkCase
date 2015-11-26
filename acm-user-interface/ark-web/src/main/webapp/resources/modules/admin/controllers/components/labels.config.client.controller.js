'use strict';

angular.module('admin').controller('Admin.LabelsConfigController', ['$scope', 'Admin.LabelsConfigService',
    function ($scope, labelsConfigService) {

        $scope.loaded = false;
        $scope.settings = {};
        $scope.takeAllNamespaces = takeAllNamespaces;
        $scope.allNamespaces = [];

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

            labelsConfigService.retrieveNamespaces(
                function (data) {
                    $scope.namespacesDropdownOptions = data;
                    $scope.selectedNamespace = $scope.namespacesDropdownOptions[0].id;
                    $scope.loaded = $scope.checkLoaded();
                });
            labelsConfigService.retrieveLanguages(
                function (data) {
                    $scope.languagesDropdownOptions = data;
                    $scope.selectedLanguage = $scope.languagesDropdownOptions[0];
                    $scope.selectedDefaultLanguage = $scope.languagesDropdownOptions[0];
                    $scope.loaded = $scope.checkLoaded();
                });

            reloadGrid();
        });

        $scope.$watch('loaded', function () {
            if ($scope.loaded == true) {
                reloadGrid();
            }
        });

        $scope.$watch('selectedDefaultLanguage', function () {
            $scope.settings.defaultLang = $scope.selectedDefaultLanguage;
        });

        $scope.$watchGroup(['selectedNamespace', 'selectedLanguage'], function () {
            reloadGrid();
        });

        function reloadGrid() {
            if ($scope.selectedNamespace && $scope.selectedLanguage) {
                var selectedNamespace = $scope.selectedNamespace;
                var selectedLanguage = $scope.selectedLanguage;
                labelsConfigService.retrieveResource({
                        lang: selectedLanguage,
                        ns: selectedNamespace},
                    function (data) {
                        $scope.gridOptions.data = data;
                    });
            }
        }

        $scope.checkLoaded = function () {
            if ($scope.selectedNamespace && $scope.selectedLanguage && $scope.loaded === false) {
                return true;
            }
            return false;
        };

        //changing default language
        $scope.changeDefaultLng = function () {
            labelsConfigService.updateSettings(
                    angular.toJson($scope.settings)
            )
        };

        //reset all values to default for selected module from dropdown
        $scope.resetCurrentModuleResources = function(){
            labelsConfigService.resetResource({
                lng: [$scope.selectedLanguage],
                ns: [$scope.selectedNamespace]
            });
            reloadGrid();
        };

        //reset all values to default for all modules
        $scope.resetAllResources = function(){
            takeAllNamespaces();
            labelsConfigService.resetResource({
                lng: [$scope.selectedLanguage],
                ns: $scope.allNamespaces
            });
            reloadGrid();
        };

        //retrieve all Namespaces from dropdown list and put them into allNamespaces array
       function takeAllNamespaces(){
            angular.forEach($scope.namespacesDropdownOptions, function(option){
                var exists = false;
                angular.forEach($scope.allNamespaces, function(avOption){
                    if(avOption == option){
                        exists = true;
                    }
                });
                if(exists == false){
                    $scope.allNamespaces.push(option.id);
                }
            });
        }

        //updating value for Description for selected record in grid
        $scope.updateLabelDesc = function(desc, rowEntity){
            labelsConfigService.updateResource({
                lang: $scope.selectedLanguage,
                ns: $scope.selectedNamespace
            }, angular.toJson({
                id: rowEntity.id,
                value: rowEntity.value,
                description: desc
            }));
        };

        //updating value for Value for selected record in grid
        $scope.updateLabelValue = function(value, rowEntity){
            labelsConfigService.updateResource({
                lang: $scope.selectedLanguage,
                ns: $scope.selectedNamespace
            }, angular.toJson({
                id: rowEntity.id,
                value: value,
                description: rowEntity.description
            }));
        }
    }
]);