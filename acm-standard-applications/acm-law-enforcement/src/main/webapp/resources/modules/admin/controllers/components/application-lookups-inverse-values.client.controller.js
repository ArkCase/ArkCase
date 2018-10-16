'use strict';

angular.module('admin').controller('Admin.InverseValuesLookupController', [ '$scope', '$translate', '$modal', 'Object.LookupService', 'Helper.UiGridService', 'UtilService', 'MessageService', function($scope, $translate, $modal, ObjectLookupService, HelperUiGridService, Util, MessageService) {

    var gridHelper = new HelperUiGridService.Grid({
        scope: $scope
    });
    $scope.lookup = [];

    //get config and init grid settings
    $scope.config.$promise.then(function(config) {
        var componentConfig = _.find(config.components, {
            id: 'inverseValuesLookup'
        });
        var columnDefs = componentConfig.columnDefs;
        var rowTemplate = componentConfig.rowTemplate;

        // TODO: This should be checked in the HelperUiGridService (ignore addButton with same name)
        if (!_.findWhere(columnDefs, {
            name: 'act'
        })) {
            gridHelper.addButton(componentConfig, 'edit');
            gridHelper.addButton(componentConfig, 'delete');
        }

        $scope.gridOptions = {
            enableColumnResizing: true,
            enableRowSelection: true,
            enableRowHeaderSelection: false,
            multiSelect: false,
            noUnselect: false,
            columnDefs: columnDefs,
            totalItems: 0,
            data: [],
            rowTemplate: rowTemplate,
            onRegisterApi: function(gridApi) {
                gridApi.draggableRows.on.rowDropped($scope, function(info, dropTarget) {
                    saveLookup();
                });
            }

        };

        $scope.gridOptions.data = $scope.lookup;
    });

    $scope.addNew = function() {
        var entry = {};

        //put entry to scope, we will need it when we return from popup
        $scope.entry = entry;
        var item = {
            key: '',
            value: '',
            inverseKey: '',
            inverseValue: '',
        };
        showModal(item, false);
    };

    $scope.editRow = function(rowEntity) {
        $scope.entry = rowEntity;
        var item = {
            key: rowEntity.key,
            value: rowEntity.value,
            inverseKey: rowEntity.inverseKey,
            inverseValue: rowEntity.inverseValue
        };
        showModal(item, true);

    };

    $scope.deleteRow = function(rowEntity) {
        //change for AFDP-6803 change ok button content
        bootbox.confirm({
            message: $translate.instant("admin.application.lookups.config.deleteEntryMsg"),
            buttons: {
                confirm:{
                    label:  $translate.instant("admin.application.lookups.config.dialog.deleteEntryConfirm")
                },
                cancel: {
                    label:  $translate.instant("admin.application.lookups.config.dialog.cancel")
                }
            },
            callback: function(result){
                if (result) {
                    var idx;
                    _.find($scope.lookup, function(entry, entryIdx) {
                        if (entry.key == rowEntity.key) {
                            idx = entryIdx;
                            return true;
                        }
                    });
                    $scope.lookup.splice(idx, 1);
                    saveLookup();
                }
            }
        });
    };

    function showModal(entry, isEdit) {
        var params = {};
        params.entry = entry || {};
        params.isEdit = isEdit || false;
        params.config = $scope.config;

        var modalInstance = $modal.open({
            animation: true,
            templateUrl: 'modules/admin/views/components/application-lookups-inverse-values-modal.client.view.html',
            controller: 'Admin.InverseValuesLookupModalController',
            size: 'md',
            backdrop: 'static',
            resolve: {
                params: function() {
                    return params;
                }
            }
        });
        modalInstance.result.then(function(data) {
            $scope.entry.key = data.entry.key;
            $scope.entry.value = data.entry.value;
            $scope.entry.inverseKey = data.entry.inverseKey;
            $scope.entry.inverseValue = data.entry.inverseValue;
            if (!data.isEdit) {
                $scope.lookup.push($scope.entry);
            }

            saveLookup();
        });
    }

    $scope.$on('lookup-def-selected', lookupDefSelected);

    function lookupDefSelected(e, selectedLookupDef) {
        if (selectedLookupDef.lookupType === 'inverseValuesLookup') {
            $scope.selectedLookupDef = selectedLookupDef;
            fetchLookup();
        }
    }

    function saveLookup() {
        var promiseSaveInfo = ObjectLookupService.saveLookup($scope.selectedLookupDef, $scope.lookup);
        promiseSaveInfo.then(function(lookup) {
            MessageService.succsessAction();
            fetchLookup();
            return lookup;
        }, function(error) {
            MessageService.error(error.data ? error.data : error);
            fetchLookup();
            return error;
        });

        return promiseSaveInfo;
    }

    function fetchLookup() {
        ObjectLookupService.getLookup($scope.selectedLookupDef).then(function(lookup) {
            // if we change the reference of $scope.lookup variable the UI is not updated, so we change the elements in the array
            $scope.lookup.splice(0, $scope.lookup.length);
            $scope.lookup.push.apply($scope.lookup, lookup);
        });
    }

    $scope.$emit('lookup-controller-loaded');
} ]);