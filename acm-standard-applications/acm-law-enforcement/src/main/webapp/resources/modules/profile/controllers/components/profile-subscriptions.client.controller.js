'use strict';

angular.module('profile').controller('Profile.SubscriptionController', ['$http', '$scope'
    , 'Object.SubscriptionService', 'ConfigService'
    , function ($http, $scope
        , ObjectSubscriptionService, ConfigService) {

        $scope.config = null;
        $scope.subscribptionGridOptions = {};
        ConfigService.getComponentConfig("profile", "subscription").then(function (config) {
            $scope.config = config;
            $scope.subscribptionGridOptions = {
                data: [],
                columnDefs: config.columnDefs,
                paginationPageSizes: config.paginationPageSizes,
                paginationPageSize: config.paginationPageSize,
                onRegisterApi: function (gridApi) {
                    $scope.gridApi = gridApi;
                }
            };
        });

        $scope.unsubscribe = function (rowEntity) {
            var index = $scope.subscribptionGridOptions.data.indexOf(rowEntity);
            var userID = rowEntity.userID;
            var parentID = rowEntity.parentID;
            var type = rowEntity.type;
            ObjectSubscriptionService.unsubscribe(userID, type, parentID);
            $scope.subscribptionGridOptions.data.splice(index, 1);
        };

        $scope.unsubscriptSelected = function () {
            var rowSelected = $scope.gridApi.selection.getSelectedRows();
            for (var i = 0; i < rowSelected.length; i++) {
                var index = $scope.subscribptionGridOptions.data.indexOf(rowSelected[i]);
                var userID = rowSelected[i].userID;
                var parentID = rowSelected[i].parentID;
                var type = rowSelected[i].type;
                ObjectSubscriptionService.unsubscribe(userID, type, parentID);
                $scope.subscribptionGridOptions.data.splice(index, 1);
            }
        };

        ObjectSubscriptionService.getListOfSubscriptionsByUser().then(function (data) {
            for (var i = 0; i < data.length; i++) {
                $scope.subscribptionGridOptions.data.push(
                    {
                        "title": data[i].objectTitle,
                        "type": data[i].subscriptionObjectType,
                        "created": moment(data[i].created).format('MM-DD-YYYY'),
                        "parentID": data[i].objectId,
                        "userID": data[i].userId
                    }
                );
            }
        });

    }
]);
