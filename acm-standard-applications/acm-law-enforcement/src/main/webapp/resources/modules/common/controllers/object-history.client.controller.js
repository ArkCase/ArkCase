'use strict';

angular.module('common').controller(
        'Common.HistoryController',
        [ '$scope', '$stateParams', '$q', 'UtilService', 'ConfigService', 'ObjectService', 'Object.AuditService', 'Helper.UiGridService',
                '$modal',
                function($scope, $stateParams, $q, Util, ConfigService, ObjectService, ObjectAuditService, HelperUiGridService, $modal) {

                    var onConfigRetrieved = function(config) {
                        gridHelper.setColumnDefs(config);
                        gridHelper.setBasicOptions(config);
                        gridHelper.disableGridScrolling(config);
                        gridHelper.setExternalPaging(config, retrieveGridData);
                        gridHelper.setUserNameFilter(promiseUsers);
                        retrieveGridData();
                    };

                    ConfigService.getModuleConfig("common").then(function(moduleConfig) {
                        $scope.config = moduleConfig;
                        onConfigRetrieved(moduleConfig.objectHistoryClientGrid);
                        return moduleConfig;
                    });

                    var gridHelper = new HelperUiGridService.Grid({
                        scope : $scope
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

                        gridHelper.showObject(targetType, targetId);
                    };

                    $scope.showDetails = function(objectHistoryDetails) {
                        var params = {};
                        params.details = objectHistoryDetails;

                        var modalInstance = $modal.open({
                            animation : true,
                            templateUrl : 'modules/common/views/object-history-details-modal.client.view.html',
                            controller : 'Common.ObjectHistoryDetailsController',
                            size : 'md',
                            backdrop : 'static',
                            resolve : {
                                params : function() {
                                    return params;
                                }
                            }
                        });

                        modalInstance.result.then(function(data) {

                        });
                    };

                } ]);