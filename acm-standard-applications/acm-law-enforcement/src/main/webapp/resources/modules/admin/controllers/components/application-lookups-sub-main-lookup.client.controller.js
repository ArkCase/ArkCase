'use strict';

angular.module('admin').controller('Admin.SybMainLookupController', ['$scope', '$translate', '$modal', 'Object.LookupService', 'Helper.UiGridService', 'UtilService', 'MessageService',
    function ($scope, $translate, $modal, ObjectLookupService, HelperUiGridService, Util, MessageService) {
        
        var gridHelper = new HelperUiGridService.Grid({scope: $scope});
        $scope.lookup = [];
     
        //get config and init grid settings
        $scope.config.$promise.then(function (config) {
            var componentConfig = _.find(config.components, {id: 'standardLookup'});
            var columnDefs = componentConfig.columnDefs;
            
            // TODO: This should be checked in the HelperUiGridService (ignore addButton with same name)
            if (!_.findWhere(columnDefs, {name: 'act'})) {
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
                onRegisterApi:  function (gridApi) {
                                    gridApi.selection.on.rowSelectionChanged($scope, function (row) {
                                        if (row.isSelected) {
                                            $scope.mainLookupValueSelected(row.entity);
                                        } else {
                                            $scope.mainLookupValueSelected(null);
                                        }
                                    })
                                }
            }

            $scope.gridOptions.data = $scope.lookup;
        });

        $scope.addNew = function () {
            var entry = {'subLookup':[]};

            //put entry to scope, we will need it when we return from popup
            $scope.entry = entry;
            var item = {
                key: '',
                value: ''
            };
            $scope.mainLookupValueSelected(null);
            showModal(item, false);
        };

        $scope.editRow = function (rowEntity) {
            $scope.entry = rowEntity;
            var item = {
                key: rowEntity.key,
                value: rowEntity.value
            };
            showModal(item, true);
        };

        $scope.deleteRow = function (rowEntity) {
            var idx;
            _.find($scope.lookup, function(entry, entryIdx){ 
                if (entry.key == rowEntity.key) { 
                   idx = entryIdx;
                   return true;
                }
            });
            $scope.mainLookupValueSelected(null);
            $scope.lookup.splice(idx , 1);
            saveLookup();
        };

        function showModal(entry, isEdit) {
            var params = {};
            params.entry = entry || {};
            params.isEdit = isEdit || false;

            var modalInstance = $modal.open({
                animation: true,
                templateUrl: 'modules/admin/views/components/application-lookups-standard-modal.client.view.html',
                controller: 'Admin.StandardLookupModalController',
                size: 'md',
                backdrop: 'static',
                resolve: {
                    params: function () {
                        return params;
                    }
                }
            });
            modalInstance.result.then(function (data) {
                $scope.entry.key = data.entry.key;
                $scope.entry.value = data.entry.value;
                if (!data.isEdit) {
                    $scope.lookup.push($scope.entry);
                }

                saveLookup();
            });
        }

        $scope.$on('lookup-def-selected', lookupDefSelected);
        
        function lookupDefSelected(e, selectedLookupDef) {
            if (selectedLookupDef.lookupType === 'subLookup') {
                $scope.selectedLookupDef = selectedLookupDef;
                fetchLookup();
            }
        }
        
        $scope.mainLookupValueSelected = function(selectedMainLookupValue) {
            $scope.selectedMainLookupValue = selectedMainLookupValue;            
            $scope.$broadcast('main-lookup-selected', $scope.selectedLookupDef, $scope.lookup, $scope.selectedMainLookupValue);
        };
        
        // workaround for the first load of child controllers
        $scope.$on('sub-lookup-controller-loaded', function() {
             $scope.$broadcast('main-lookup-selected', $scope.selectedLookup);
        });
        
        function saveLookup() {
            var promiseSaveInfo = Util.errorPromise($translate.instant("common.service.error.invalidData"));

            promiseSaveInfo = ObjectLookupService.saveLookup($scope.selectedLookupDef, $scope.lookup);
            promiseSaveInfo.then(
                function (lookup) {
                    fetchLookup();
                    return lookup;
                }
                , function (error) {
                    MessageService.error(error);
                    fetchLookup();
                    return error;
                }
            );
            
            return promiseSaveInfo;
        }

        function fetchLookup() {
            var lookup = ObjectLookupService.getLookup($scope.selectedLookupDef);
            // if we change the reference of $scope.lookup variable the UI is not updated, so we change the elements in the array
            $scope.lookup.splice(0, $scope.lookup.length, ...lookup);
        }

        $scope.$emit('lookup-controller-loaded');
    }
]);