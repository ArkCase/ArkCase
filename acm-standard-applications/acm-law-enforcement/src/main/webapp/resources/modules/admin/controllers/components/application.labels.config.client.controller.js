'use strict';

angular.module('admin').controller('Admin.LabelsConfigController', ['$scope', '$q'
    , 'UtilService', 'Admin.LabelsConfigService', 'MessageService', 'Config.LocaleService'
    , function ($scope, $q
        , Util, LabelsConfigService, messageService, LocaleService) {

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
            var settingsPromise = LabelsConfigService.retrieveSettings().$promise;
            var localSettingsPromise = LocaleService.getSettings();

            $q.all([nsPromise, settingsPromise, localSettingsPromise]).then(function (result) {
                var namespaces = result[0];
                $scope.namespacesDropdownOptions = _.sortBy(namespaces, 'name');
                $scope.selectedNamespace = $scope.namespacesDropdownOptions[0];

                var settings = result[1];
                $scope.settings = settings;
                var localeCode = Util.goodMapValue($scope.settings, "localeCode", LocaleService.DEFAULT_CODE);

                $scope.localSettings = result[2];
                var locales = Util.goodMapValue($scope.localSettings, "locales", LocaleService.DEFAULT_LOCALES);

                $scope.languagesDropdownOptions = locales;
                $scope.selectedLocale = _.find(locales, {code: localeCode});
                $scope.defaultLocale = _.find(locales, {code: localeCode});

                reloadGrid();
            });
        });

        function reloadGrid() {
            if ($scope.selectedNamespace && $scope.selectedLocale.locale) {
                $scope.disabledInputs = true;
                LabelsConfigService.retrieveResource({
                        lang: $scope.selectedLocale.code,
                        ns: $scope.selectedNamespace.id
                    },
                    function (data) {
                    	//success
                    	$scope.gridOptions.data = data;
                        $scope.disabledInputs = false;
                    },
                    function () {
                    	//error
                        $scope.disabledInputs = false;
                    });
            }
        }


        //changing default language
        $scope.changeDefaultLocale = function ($event, newLocale) {
            $event.preventDefault();
            var locales = Util.goodMapValue($scope.localSettings, "locales", LocaleService.DEFAULT_LOCALES);
            $scope.defaultLocale = _.find(locales, {code: newLocale});
            $scope.settings.localeCode = newLocale;
            LabelsConfigService.updateSettings(
                angular.toJson($scope.settings)
            )
        };

        //reset all values to default for selected module from dropdown
        $scope.resetCurrentModuleResources = function () {
            $scope.disabledInputs = true;
            LabelsConfigService.resetResource({
                lng: [$scope.selectedLocale.code],
                ns: [$scope.selectedNamespace.id]
            }, function () {
            	//success
            	reloadGrid();
                messageService.succsessAction();
            }, function () {
            	//error
            	$scope.disabledInputs = false;
            	messageService.errorAction();
            });
        };

        //reset all values to default for all modules
        $scope.resetAllResources = function () {
            var allNamespaces = _.pluck($scope.namespacesDropdownOptions, 'id');
            $scope.disabledInputs = true;
            LabelsConfigService.resetResource({
                lng: [$scope.selectedLocale.code],
                ns: allNamespaces
            }, function () {
            	//success
            	reloadGrid();
                messageService.succsessAction();
            }, function () {
            	//error
            	$scope.disabledInputs = false;
                messageService.errorAction();
            });
        };

        $scope.refreshAllResources = function () {
            var allNamespaces = _.pluck($scope.namespacesDropdownOptions, 'id');
            $scope.disabledInputs = true;
            LabelsConfigService.refreshResource({
                lng: [$scope.selectedLocale.code],
                ns: allNamespaces
            }, function () {
            	//success
            	reloadGrid();
                messageService.succsessAction();
            }, function () {
            	//error
            	$scope.disabledInputs = false;
                messageService.errorAction();
            });
        };

        //updating value for Description for selected record in grid
        $scope.updateLabelDesc = function (desc, rowEntity) {
            LabelsConfigService.updateResource({
                lang: $scope.selectedLocale.code,
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
                lang: $scope.selectedLocale.code,
                ns: $scope.selectedNamespace.id
            }, angular.toJson({
                id: rowEntity.id,
                value: value,
                description: rowEntity.description
            }));
        }
    }
]);