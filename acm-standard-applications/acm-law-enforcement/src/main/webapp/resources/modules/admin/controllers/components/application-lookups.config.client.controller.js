'use strict';

angular.module('admin').controller('Admin.LookupsConfigController', ['$scope', '$q', '$templateCache', '$modal', '$http', 'Object.LookupService', 'MessageService',
    function ($scope, $q, $templateCache, $modal, $http, ObjectLookupService, MessageService) {

        $scope.selectLookupDef = function (selectedLookupDef) {
            $scope.selectedLookupDef = this.selectedLookupDef;
            switch(this.selectedLookupDef.lookupType) {
                case 'standardLookup' :
                    $scope.view = "modules/admin/views/components/application-lookups-standard.client.view.html";
                    break;
                case 'nestedLookup' :
                    $scope.view = "modules/admin/views/components/application-lookups-nested-lookup-parent.client.view.html";
                    break;
                case 'inverseValuesLookup' :
                    $scope.view = "modules/admin/views/components/application-lookups-inverse-values.client.view.html";
                    break;
                default:
                    console.error("Unknown lookup type!");
                    break;
            }

            $scope.$broadcast('lookup-def-selected', $scope.selectedLookupDef);
        };

        ObjectLookupService.getLookupsDefs().then(function(data) {
            $scope.lookupsDefs = data;
            $scope.selectedLookupDef = $scope.lookupsDefs[0];
            $scope.selectLookupDef($scope.selectedLookupDef);
        });

        // workaround for the first load of child controllers
        $scope.$on('lookup-controller-loaded', function() {
            $scope.$broadcast('lookup-def-selected', $scope.selectedLookupDef);
        });

        $scope.lookup = [];
        $scope.addLookup = function () {
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

        $scope.deleteLookup = function () {
            //delete function
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
                    params: function () {
                        return params;
                    }
                }
            });
            modalInstance.result.then(function (data) {
                $scope.entry.name = data.entry.name;
                $scope.entry.lookupType = data.entry.lookupType;
                $scope.entry.readonly = data.entry.readonly;
                saveLookup();
            });
        }

        function saveLookup() {
            var promiseSaveInfo = ObjectLookupService.saveLookup($scope.entry, $scope.lookup);
            promiseSaveInfo.then(
                function (success) {
                    MessageService.succsessAction();
                    return success;
                }
                , function (error) {
                    MessageService.error(error.data ? error.data : error);
                    return error;
                }
            );

            return promiseSaveInfo;
        }
    }
]);