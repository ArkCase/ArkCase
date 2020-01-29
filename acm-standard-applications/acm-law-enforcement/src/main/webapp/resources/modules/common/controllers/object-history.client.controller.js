'use strict';

angular.module('common').controller('Common.HistoryController',
        [ '$scope', '$stateParams', '$q', 'UtilService', 'ConfigService', 'ObjectService', 'Object.AuditService', 'Object.LookupService', 'Helper.UiGridService', '$modal', function($scope, $stateParams, $q, Util, ConfigService, ObjectService, ObjectAuditService, ObjectLookupService, HelperUiGridService, $modal) {

            var onConfigRetrieved = function(config) {
                $scope.config = config;
                //first the filter is set, and after that everything else,
                //so that the data loads with the new filter applied
                gridHelper.setUserNameFilterToConfig(promiseUsers).then(function(updatedConfig) {
                    $scope.config = updatedConfig;
                    if ($scope.gridApi != undefined)
                        $scope.gridApi.core.refresh();
                    gridHelper.setColumnDefs(updatedConfig);
                    gridHelper.setBasicOptions(updatedConfig);
                    gridHelper.disableGridScrolling(updatedConfig);
                    gridHelper.setExternalPaging(updatedConfig, retrieveGridData);
                });
                retrieveGridData();
            };

            $scope.ObjectTypes = ObjectService.ObjectTypes;

            $scope.selectedEventType =
                ConfigService.getModuleConfig("common").then(function(moduleConfig) {
                $scope.config = moduleConfig;
                onConfigRetrieved(moduleConfig.objectHistoryClientGrid);
                return moduleConfig;
            });

            var gridHelper = new HelperUiGridService.Grid({
                scope: $scope
            });
            var promiseUsers = gridHelper.getUsers();

            function retrieveGridData() {
                var objectType = $stateParams.type == "ADHOC" ? "TASK" : $stateParams.type;
                gridHelper.retrieveAuditData(objectType, $stateParams.id);
            }

            $scope.onClickObjLink = function(event, rowEntity) {
                event.preventDefault();

                var targetType = Util.goodMapValue(rowEntity, "objectType");
                var targetId = Util.goodMapValue(rowEntity, "objectId");

                if(targetType === "NOTE") {
                    var parentTargetType = Util.goodMapValue(rowEntity, "parentObjectType");
                    var parentTargetId = Util.goodMapValue(rowEntity, "parentObjectId");
                    ObjectLookupService.getObjectTypes().then(function(objectTypes) {
                        var found = _.find(objectTypes, {
                            key: parentTargetType
                        });
                        var state = found.state;
                        state = state.replace(".main", ".notes");
                        gridHelper.transitionToState(parentTargetType, parentTargetId, state);
                    });
                }
                else {
                    gridHelper.showObject(targetType, targetId);
                }
            };

            $scope.showDetails = function(objectHistoryDetails) {
                var params = {};
                params.details = objectHistoryDetails;

                var modalInstance = $modal.open({
                    animation: true,
                    templateUrl: 'modules/common/views/object-history-details-modal.client.view.html',
                    controller: 'Common.ObjectHistoryDetailsController',
                    size: 'md',
                    backdrop: 'static',
                    resolve: {
                        params: function() {
                            return params;
                        }
                    }
                });

                modalInstance.result.then(function(data) {

                });
            };

        } ]);