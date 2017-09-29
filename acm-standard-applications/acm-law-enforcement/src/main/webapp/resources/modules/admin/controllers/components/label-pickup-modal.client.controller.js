'use strict'

angular.module('admin').controller('Admin.LabelPickupClientController', ['$scope', '$q', '$modal', '$timeout', 'Helper.UiGridService',
    'UtilService', 'Admin.LabelsConfigService', 'MessageService', 'Config.LocaleService', '$modalInstance', 'params',
    function($scope, $q, $modal, $timeout, HelperUiGridService, Util, LabelsConfigService, messageService, LocaleService, $modalInstance, params)
    {
        $scope.config = params.config;
        var gridHelper = new HelperUiGridService.Grid({scope: $scope});

        $scope.onClickCancel = function () {
            $modalInstance.dismiss('Cancel');
        };

        $scope.entry = angular.copy(params.entry);
        $scope.isEdit = angular.copy(params.isEdit);

        $scope.onClickOk = function () {
            $scope.selectedRow = $scope.gridApi.selection.getSelectedRows();
            $scope.entry.value = $scope.selectedRow[0].id;
            $scope.entry.inverseValue = $scope.selectedRow[0].id;
            $modalInstance.close(
                {
                    entry: $scope.entry,
                    isEdit: $scope.isEdit,

                }
            );
        };

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
            paginationPageSizes: [],
            paginationPageSize: 0,
            data: [],
            onRegisterApi: function (gridApi) {
                $scope.gridApi = gridApi;
            }
        };


        var nsPromise = LabelsConfigService.retrieveNamespaces().$promise;
        var settingsPromise = LabelsConfigService.retrieveSettings().$promise;
        var localSettingsPromise = LocaleService.getSettings();

        $q.all([$scope.config.$promise, nsPromise, settingsPromise, localSettingsPromise]).then(function (result) {
            var config = result[0];
            var labelsConfig = _.find(config.components, {id: 'labelsChooser'});
            $scope.gridOptions.columnDefs = labelsConfig.columnDefs;

            $scope.gridOptions.paginationPageSizes = labelsConfig.paginationPageSizes;
            $scope.gridOptions.paginationPageSize = labelsConfig.paginationPageSize;

            var namespaces = result[1];
            var coreNamespace = _.find(namespaces, {id: 'core'});
            $scope.selectedNamespace = coreNamespace;

            $scope.settings = result[2];
            var localeCode = Util.goodMapValue($scope.settings, "localeCode", LocaleService.DEFAULT_CODE);

            $scope.localSettings = result[3];
            var locales = Util.goodMapValue($scope.localSettings, "locales", LocaleService.DEFAULT_LOCALES);

            $scope.languagesDropdownOptions = locales;
            $scope.selectedLocale = _.find(locales, {code: localeCode});

            reloadGrid();
        });


        function reloadGrid() {
            if ($scope.selectedNamespace && $scope.selectedLocale.code) {
                $scope.disabledInputs = true;
                LabelsConfigService.retrieveResource({
                        lang: $scope.selectedLocale.code,
                        ns: $scope.selectedNamespace.id
                    },
                    function (data) {
                        //success
                        $scope.gridOptions.data = data;

                        // Ideally, the flag should be set when ui-grid data is completed. Since this event is not
                        // given, estimated 5 second timeout for grid data to load is the best we can think of for now
                        $timeout(function () {
                            $scope.disabledInputs = false;
                        }, 5000);
                    },
                    function () {
                        //error
                        $scope.disabledInputs = false;
                    });
            }
        }

    }

]);