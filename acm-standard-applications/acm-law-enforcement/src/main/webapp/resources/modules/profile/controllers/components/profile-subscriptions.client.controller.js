'use strict';

angular.module('profile').controller('Profile.SubscriptionController',
        [ '$http', '$scope', 'Object.SubscriptionService', 'ConfigService', 'ObjectService', 'UtilService', 'Helper.UiGridService', function($http, $scope, ObjectSubscriptionService, ConfigService, ObjectService, Util, HelperUiGridService) {

            $scope.config = null;
            $scope.subscriptionGridOptions = {};
            ConfigService.getComponentConfig("profile", "subscription").then(function(config) {
                $scope.config = config;
                $scope.subscriptionGridOptions = {
                    data: [],
                    columnDefs: config.columnDefs,
                    paginationPageSizes: config.paginationPageSizes,
                    paginationPageSize: config.paginationPageSize,
                    onRegisterApi: function(gridApi) {
                        $scope.gridApi = gridApi;
                    }
                };
            });

            var gridHelper = new HelperUiGridService.Grid({
                scope: $scope
            });

            $scope.onClickObjLink = function(event, rowEntity) {
                event.preventDefault();

                var targetType = Util.goodMapValue(rowEntity, "targetType");
                var targetId = Util.goodMapValue(rowEntity, "targetId");

                gridHelper.showObject(targetType, targetId);

                if (targetType == ObjectService.ObjectTypes.COMPLAINT) {
                    $scope.$emit('request-show-object', {
                        objectId: targetId,
                        objectType: targetType
                    });
                }
            };

            $scope.unsubscribe = function(rowEntity) {
                var index = $scope.subscriptionGridOptions.data.indexOf(rowEntity);
                var userID = rowEntity.userId;
                var parentID = rowEntity.parentId;
                var type = rowEntity.targetType;
                ObjectSubscriptionService.unsubscribe(userID, type, parentID);
                $scope.subscriptionGridOptions.data.splice(index, 1);
            };

            $scope.unsubscriptSelected = function() {
                var rowSelected = $scope.gridApi.selection.getSelectedRows();
                for (var i = 0; i < rowSelected.length; i++) {
                    var index = $scope.subscriptionGridOptions.data.indexOf(rowSelected[i]);
                    var userID = rowSelected[i].userId;
                    var parentID = rowSelected[i].parentId;
                    var type = rowSelected[i].targetType;
                    ObjectSubscriptionService.unsubscribe(userID, type, parentID);
                    $scope.subscriptionGridOptions.data.splice(index, 1);
                }
            };

            ObjectSubscriptionService.getListOfSubscriptionsByUser().then(function(data) {
                for (var i = 0; i < data.length; i++) {
                    $scope.subscriptionGridOptions.data.push({
                        "title": data[i].objectTitle,
                        "targetType": data[i].subscriptionObjectType,
                        "targetId": data[i].objectId,
                        "created": moment(data[i].created).format('MM-DD-YYYY'),
                        "parentId": data[i].objectId,
                        "userId": data[i].userId
                    });
                }
            });

        } ]);
