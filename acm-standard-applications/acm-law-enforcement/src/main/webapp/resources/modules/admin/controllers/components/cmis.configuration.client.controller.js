'use strict';

angular.module('admin').controller('Admin.CMISConfigurationController', ['$scope', '$modal', 'Helper.UiGridService',
    function ($scope, $modal, HelperUiGridService) {

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});

        //get config and init grid settings
        $scope.config.$promise.then(function (config) {
            var componentConfig = _.find(config.components, {id: 'cmisConfiguration'});
            $scope.config = config;

            gridHelper.addButton(componentConfig, 'edit');
            gridHelper.addButton(componentConfig, 'delete');

            $scope.gridOptions = {
                enableColumnResizing: true,
                enableRowSelection: true,
                enableRowHeaderSelection: false,
                multiSelect: false,
                noUnselect: false,
                columnDefs: componentConfig.columnDefs,
                totalItems: 0,
                data: []
            };

            reloadGrid();
        });

        $scope.showModal = function (cmisConfig, isEdit, originalConfig) {
            var modalScope = $scope.$new();
            modalScope.cmisConfig = cmisConfig || {};
            modalScope.isEdit = isEdit || false;

            var modalInstance = $modal.open({
                scope: modalScope,
                templateUrl: 'modules/admin/views/components/cmis.configuration.addconfig.modal.html',
                backdrop: 'static',
                controller: function ($scope, $modalInstance) {
                    $scope.ok = function () {
                        $modalInstance.close({cmisConfig: $scope.cmisConfig, isEdit: $scope.isEdit});
                    };
                    $scope.cancel = function () {
                        $modalInstance.dismiss('cancel');
                    }
                }
            });

            modalInstance.result.then(function (result) {
                //TODO Create new configuration file

                console.log(JSON.stringify(result.cmisConfig));
                if (result.isEdit) {
                    var toRemove = _.find($scope.gridOptions.data, originalConfig);
                    _.remove($scope.gridOptions.data, toRemove);
                    $scope.gridOptions.data.push(result.cmisConfig);
                } else {
                    $scope.gridOptions.data.push(result.cmisConfig);
                }
            })
        };

        $scope.deleteRow = function (rowEntity) {
            //TODO: Delete configuration

            console.log("You clicked DELETE. Here's a cookie (::)");
        };

        $scope.editRow = function (rowEntity) {
            //TODO: edit configuration

            console.log("You clicked EDIT. Here's Kirby (>'.')>");
            $scope.showModal(angular.copy(rowEntity), true, rowEntity);
        };

        function reloadGrid() {
            //TODO: Look up and format existing properties files.

            var exampleData = {
                "id": "id",
                "baseUrl": "baseUrl",
                "username": "username",
                "password": "password",
                "useAlfrescoExtension": "useAlfrescoExtension",
                "endpoint": "endpoint",
                "maxIdle": "maxIdle",
                "maxActive": "maxActive",
                "maxWait": "maxWait",
                "minEvictionMillis": "minEvictionMillis",
                "evictionCheckIntervalMillis": "evictionCheckIntervalMillis",
                "reconnectCount": "reconnectCount",
                "reconnectFrequency": "reconnectFrequency",
                "repositoryId": "repositoryId",
                "versioningState": "versioningState"
            };

            $scope.gridOptions.data.push(exampleData);
        }
    }]);
