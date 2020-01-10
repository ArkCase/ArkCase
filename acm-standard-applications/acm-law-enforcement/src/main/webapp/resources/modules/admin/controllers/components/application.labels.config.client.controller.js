'use strict';

angular.module('admin').controller('Admin.LabelsConfigController', [ '$scope', '$q', '$modal', '$timeout', 'UtilService', 'Admin.LabelsConfigService', 'MessageService', 'Config.LocaleService', function($scope, $q, $modal, $timeout, Util, LabelsConfigService, messageService, LocaleService) {

    $scope.settings = {};
    $scope.disabledInputs = true;
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
        paginationPageSize: 20,
        paginationPageSizes: [ 20, 50, 100, 250, 1000 ],
        onRegisterApi: function(gridApi) {
            $scope.gridApi = gridApi;
        }
    };

    var nsPromise = LabelsConfigService.retrieveNamespaces().$promise;
    var settingsPromise = LabelsConfigService.retrieveSettings().$promise;
    var localSettingsPromise = LocaleService.getSettings();

    $q.all([ $scope.config.$promise, nsPromise, settingsPromise, localSettingsPromise ]).then(function(result) {
        var config = result[0];
        var labelsConfig = _.find(config.components, {
            id: 'labelsConfig'
        });
        $scope.gridOptions.columnDefs = labelsConfig.columnDefs;

        var namespaces = result[1];
        $scope.namespacesDropdownOptions = _.sortBy(namespaces, 'name');
        $scope.selectedNamespace = $scope.namespacesDropdownOptions[0];

        $scope.settings = result[2];
        var localeCode = Util.goodMapValue($scope.settings, "localeCode", LocaleService.DEFAULT_CODE);

        $scope.localSettings = result[3];
        var locales = Util.goodMapValue($scope.localSettings, "locales", LocaleService.DEFAULT_LOCALES);

        $scope.languagesDropdownOptions = locales;
        $scope.selectedLocale = _.find(locales, {
            code: localeCode
        });

        reloadGrid();
    });

    function reloadGrid() {
        if ($scope.selectedNamespace && $scope.selectedLocale.code) {
            $scope.disabledInputs = true;
            LabelsConfigService.retrieveResource({
                lang: $scope.selectedLocale.code,
                ns: $scope.selectedNamespace.id
            }, function(data) {
                //success
                $scope.gridOptions.data = data;
                $scope.disabledInputs = false;
            }, function() {
                //error
                $scope.disabledInputs = false;
            });
        }
    }

    //reset all values to default
    var params = {};
    $scope.resetToDefault = function() {
        params.namespacesDropdownOptions = $scope.namespacesDropdownOptions;
        params.languagesDropdownOptions = $scope.languagesDropdownOptions;
        params.selectedNamespaces = [ $scope.selectedNamespace ];
        params.selectedLocales = [ $scope.selectedLocale ];

        var modalInstance = $modal.open({
            animation: true,
            size: 'md',
            backdrop: 'static',
            resolve: {
                params: function() {
                    return params;
                }
            },
            templateUrl: "modules/admin/views/components/application.labels.config.setToDefault.dialog.view.html",
            controller: [ '$scope', '$modal', '$modalInstance', '$q', 'params', function($scope, $modal, $modalInstance, $q, params) {

                $scope.namespacesDropdownOptions = params.namespacesDropdownOptions;
                $scope.languagesDropdownOptions = params.languagesDropdownOptions;
                $scope.selectedNamespaces = params.selectedNamespaces;
                $scope.selectedLocales = params.selectedLocales;

                $scope.onClickOk = function() {
                    $modalInstance.close({
                        selectedNamespaces: $scope.selectedNamespaces,
                        selectedLocales: $scope.selectedLocales
                    });
                };
                $scope.onClickCancel = function() {
                    $modalInstance.dismiss();
                };

            } ]
        });

        modalInstance.result.then(function(result) {
            $scope.disabledInputs = true;
            var languages = _.map(result.selectedLocales, 'code');
            var namespaces = _.map(result.selectedNamespaces, 'id');

            LabelsConfigService.resetResource({
                lng: languages,
                ns: namespaces
            }, function() {
                reloadGrid();
                messageService.succsessAction();
            }, function() {
                $scope.disabledInputs = false;
                messageService.errorAction();
            });

        }, function(error) {
        });

    };

    //updating value for Description for selected record in grid
    $scope.updateLabelDesc = function(desc, rowEntity) {
        LabelsConfigService.updateResource({
            lang: $scope.selectedLocale.code,
            ns: $scope.selectedNamespace.id
        }, {
            id: rowEntity.id,
            value: rowEntity.value,
            description: desc
        });
    };

    //updating value for Value for selected record in grid
    $scope.updateLabelValue = function(value, rowEntity) {
        LabelsConfigService.updateResource({
            lang: $scope.selectedLocale.code,
            ns: $scope.selectedNamespace.id
        }, {
            id: rowEntity.id,
            value: value,
            description: rowEntity.description
        });
    }
} ]);
