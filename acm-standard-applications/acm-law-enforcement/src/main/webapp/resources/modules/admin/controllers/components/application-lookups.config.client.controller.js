'use strict';

angular.module('admin').controller('Admin.LookupsConfigController',
        [ '$scope', '$q', '$templateCache', '$modal', '$http', 'Object.LookupService', 'LookupService', 'MessageService', 'UtilService', '$translate', function($scope, $q, $templateCache, $modal, $http, ObjectLookupService, LookupService, MessageService, Util, $translate) {

            $scope.selectLookupDef = function(selectedLookupDef) {
                if (Util.isEmpty(this.selectedLookupDef)) {
                    var found = _.find($scope.lookupsDefs, {
                        name: $scope.selectedLookupDef.name
                    });
                    if (Util.isEmpty(found)) {
                        this.selectedLookupDef = $scope.lookupsDefs[0];
                    } else {
                        this.selectedLookupDef = $scope.selectedLookupDef;
                    }
                }
                $scope.selectedLookupDef = this.selectedLookupDef;
                switch (this.selectedLookupDef.lookupType) {
                case 'standardLookup':
                    $scope.view = "modules/admin/views/components/application-lookups-standard.client.view.html";
                    break;
                case 'nestedLookup':
                    $scope.view = "modules/admin/views/components/application-lookups-nested-lookup-parent.client.view.html";
                    break;
                case 'inverseValuesLookup':
                    $scope.view = "modules/admin/views/components/application-lookups-inverse-values.client.view.html";
                    break;
                default:
                    console.error("Unknown lookup type!");
                    break;
                }
                $scope.$broadcast('lookup-def-selected', $scope.selectedLookupDef);
            };

            $scope.getLookups = function() {
                ObjectLookupService.getLookupsDefs().then(function(data) {
                    //AFDP-8833, AFDP-8838 - Request and Appeal disposition type and subType lookups should be removed from UI 
                    _.remove(data, function(lookup) {
                        return lookup.name === 'appealDispositionType' || lookup.name === 'requestDispositionType' || lookup.name === 'requestDispositionSubType';
                    });
                    $scope.lookupsDefs = data
                    var index = 0;
                    if (!Util.isEmpty($scope.selectedLookupDef)) {
                        var _index = _.findIndex($scope.lookupsDefs, {
                            name: $scope.selectedLookupDef.name
                        })
                        if (!Util.isEmpty(_index) && _index > -1) {
                            index = _index;
                        }
                    }
                    $scope.selectedLookupDef = $scope.lookupsDefs[index];
                    $scope.selectLookupDef($scope.selectedLookupDef);
                });
            };

            $scope.getLookups();

            // workaround for the first load of child controllers
            $scope.$on('lookup-controller-loaded', function() {
                $scope.$broadcast('lookup-def-selected', $scope.selectedLookupDef);
            });

            $scope.lookup = [];
            $scope.addLookup = function() {
                var entry = {}; // json object for store lookupName and lookupType

                //entry to $scope
                $scope.entry = entry;
                var item = {
                    name: '',
                    lookupType: '',
                    entries: [],
                    readonly: false
                };
                showModal(item);

            };

            function deleteLookupFromExtLookups() {
                //delete function
                var promise = LookupService.deleteLookup($scope.selectedLookupDef.name);
                promise.then(function(success) {
                    MessageService.info($translate.instant('admin.application.lookups.config.delete.success'));
                    $scope.getLookups();
                    return success;
                }, function(error) {
                    MessageService.error(error.data ? error.data : error);
                    return error;
                });
                return promise;
            };

            function showModal(entry) {
                var params = {};
                params.entry = entry;

                var modalInstance = $modal.open({
                    animation: true,
                    templateUrl: 'modules/admin/views/components/application-lookups-add-lookup-modal.client.view.html',
                    controller: 'Admin.AddLookupModalController',
                    size: 'md',
                    backdrop: 'static',
                    resolve: {
                        params: function() {
                            return params;
                        }
                    }
                });
                modalInstance.result.then(function(data) {
                    $scope.entry.name = data.entry.name;
                    $scope.entry.lookupType = data.entry.lookupType;
                    $scope.entry.readonly = data.entry.readonly;

                    var found = _.find($scope.lookupsDefs, {
                        name: $scope.entry.name
                    });
                    if (!Util.isEmpty(found)) {
                        MessageService.error($translate.instant('admin.application.lookups.config.lookupExists.error'));
                    } else {
                        saveLookup();
                    }
                });
            }

            function saveLookup() {
                var promiseSaveInfo = ObjectLookupService.saveLookup($scope.entry, $scope.lookup);
                promiseSaveInfo.then(function(success) {
                    MessageService.succsessAction();
                    $scope.getLookups();
                    return success;
                }, function(error) {
                    MessageService.error(error.data ? error.data : error);
                    return error;
                });

                return promiseSaveInfo;
            }

            // Add method for AFDP-6803 to customize ok button
            $scope.deleteLookUpsConfirm = function deleteLookUpsConfirm() {
                bootbox.confirm({
                    message: "<i class='fa fa-fw fa-exclamation-triangle text-danger'></i>" + $translate.instant("admin.application.lookups.config.deleteLookupMsg"),
                    buttons: {
                        confirm:{
                            label: $translate.instant("admin.application.lookups.config.dialog.deleteLookupConfirm"),
                            className :"btn btn-primary"
                        },
                        cancel: {
                            label: $translate.instant("admin.application.lookups.config.dialog.cancel"),
                        }
                    },
                    callback: function(result){
                        if (result) {
                            deleteLookupFromExtLookups();
                        }
                    }
                })
            }
        } ]);