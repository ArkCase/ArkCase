'use strict';

angular.module('complaints').controller('Complaints.LocationsController', ['$scope', '$stateParams', '$q', '$modal'
    , 'UtilService', 'Helper.UiGridService', 'ConfigService', 'Complaint.InfoService'
    , 'Object.LookupService', 'Helper.ObjectBrowserService'
    , function ($scope, $stateParams, $q, $modal, Util, HelperUiGridService, ConfigService, ComplaintInfoService
        , ObjectLookupService, HelperObjectBrowserService) {

        var componentHelper = new HelperObjectBrowserService.Component({
            moduleId: "complaints"
            , componentId: "locations"
            , scope: $scope
            , stateParams: $stateParams
            , retrieveObjectInfo: ComplaintInfoService.getComplaintInfo
            , validateObjectInfo: ComplaintInfoService.validateComplaintInfo
            , onObjectInfoRetrieved: function (objectInfo) {
                onObjectInfoRetrieved(objectInfo);
            }
            , onConfigRetrieved: function (componentConfig) {
                return onConfigRetrieved(componentConfig);
            }
        });

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});

        ObjectLookupService.getAddressTypes().then(
            function (addressTypes) {
                $scope.addressTypes = addressTypes;
                return addressTypes;
            }
        );

        var onConfigRetrieved = function (config) {
            $scope.config = config;
            gridHelper.addEditButton(config.columnDefs, "grid.appScope.editRow(row.entity)");
            gridHelper.addDeleteButton(config.columnDefs, "grid.appScope.deleteRow(row.entity)");
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
        };

        var onObjectInfoRetrieved = function (objectInfo) {
            $q.all([componentHelper.promiseConfig]).then(function () {
                $scope.objectInfo = objectInfo;
                var location = Util.goodMapValue($scope.objectInfo, "location", null);
                $scope.gridOptions.data = (location) ? [location] : [];
            });
        };

        $scope.addNew = function () {
            var location = {};
            //put location to scope, we will need it when we return from popup
            $scope.location = location;
            var item = {
                id: '',
                streetAddress: '',
                type: '',
                city: '',
                state: '',
                zip: '',
                addressTypes: $scope.addressTypes
            };
            showModal(item, false);
        };

        $scope.editRow = function (rowEntity) {
            $scope.location = rowEntity;
            var item = {
                id: rowEntity.id,
                streetAddress: rowEntity.streetAddress,
                type: rowEntity.type,
                city: rowEntity.city,
                state: rowEntity.state,
                zip: rowEntity.zip,
                addressTypes: $scope.addressTypes
            };
            showModal(item, true);
        };

        $scope.deleteRow = function (rowEntity) {
            gridHelper.deleteRow(rowEntity);

            var id = Util.goodMapValue(rowEntity, "id", 0);
            if (0 < id) {    //do not need to call service when deleting a new row
                $scope.objectInfo.location = null;
                saveObjectInfoAndRefresh();
            }
        };

        var showModal = function (location, isEdit) {
            var modalScope = $scope.$new();
            modalScope.location = location || {};
            modalScope.isEdit = isEdit || false;

            var modalInstance = $modal.open({
                scope: modalScope,
                animation: true,
                templateUrl: 'modules/complaints/views/components/complaint-locations-modal.client.view.html',
                controller: ['$scope', '$modalInstance', function ($scope, $modalInstance) {
                    $scope.onClickOk = function () {
                        $modalInstance.close({
                            location: $scope.location,
                            isEdit: $scope.isEdit
                        });
                    };
                    $scope.onClickCancel = function () {
                        $modalInstance.dismiss('cancel');
                    }
                }],
                size: 'lg'
            });

            modalInstance.result.then(function (data) {
                var location;
                if (!data.isEdit)
                    location = $scope.location;
                else {
                    location = $scope.objectInfo.location;
                }
                location.type = data.location.type;
                location.streetAddress = data.location.streetAddress;
                location.city = data.location.city;
                location.state = data.location.state;
                location.zip = data.location.zip;
                location.country = data.location.country;
                if (!data.isEdit) {
                    $scope.objectInfo.location = location;
                }
                saveObjectInfoAndRefresh();
            });
        };

        var saveObjectInfoAndRefresh = function () {
            var saveObject = Util.omitNg($scope.objectInfo);
            ComplaintInfoService.saveComplaintInfo(saveObject).then(
                function (objectSaved) {
                    refresh();
                    return objectSaved;
                },
                function (error) {
                    return error;
                }
            );
        };

        var refresh = function () {
            $scope.$emit('report-object-refreshed', $stateParams.id);
        };
    }
]);


