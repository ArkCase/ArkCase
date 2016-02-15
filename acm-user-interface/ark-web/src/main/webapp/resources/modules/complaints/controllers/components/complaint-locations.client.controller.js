'use strict';

angular.module('complaints').controller('Complaints.LocationsController', ['$scope', '$stateParams', '$q'
    , 'UtilService', 'Helper.UiGridService', 'ConfigService', 'Complaint.InfoService'
    , 'Object.LookupService', 'Helper.ObjectBrowserService'
    , function ($scope, $stateParams, $q, Util, HelperUiGridService, ConfigService, ComplaintInfoService
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
                onConfigRetrieved(componentConfig);
            }
        });

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});

        var promiseAddressTypes = ObjectLookupService.getAddressTypes().then(
            function (addressTypes) {
                $scope.addressTypes = addressTypes;
                return addressTypes;
            }
        );

        var onConfigRetrieved = function (config) {
            $scope.config = config;
            gridHelper.addDeleteButton(config.columnDefs, "grid.appScope.deleteRow(row.entity)");
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
            gridHelper.setInPlaceEditing(config, $scope.updateRow);

            $q.all([promiseAddressTypes]).then(function (data) {
                gridHelper.setLookupDropDown(HelperUiGridService.Lookups.ADDRESS_TYPES, "type", "name", $scope.addressTypes);
            });
        };

        var onObjectInfoRetrieved = function (objectInfo) {
            $q.all([componentHelper.promiseConfig]).then(function () {
                $scope.objectInfo = objectInfo;
                var location = Util.goodMapValue($scope.objectInfo, "location", null);
                $scope.gridOptions.data = (location)? [location] : [];
                //gridHelper.hidePagingControlsIfAllDataShown($scope.gridOptions.data.length);
            });
        };


        $scope.addNew = function () {
            $scope.gridOptions.data.push({});
        };
        $scope.updateRow = function (rowEntity) {
            var complaintInfo = Util.omitNg($scope.objectInfo);
            complaintInfo.location = complaintInfo.location || {};
            complaintInfo.location.streetAddress = rowEntity.streetAddress;
            complaintInfo.location.type = rowEntity.type;
            complaintInfo.location.city = rowEntity.city;
            complaintInfo.location.state = rowEntity.state;
            complaintInfo.location.zip = rowEntity.zip;
            if (ComplaintInfoService.validateLocation(complaintInfo.location)) {
                ComplaintInfoService.saveComplaintInfo(complaintInfo).then(
                    function (complaintSaved) {
                        $scope.$emit("report-object-updated", complaintSaved);
                        return complaintSaved;
                    }
                );
            }
        };
        $scope.deleteRow = function (rowEntity) {
            gridHelper.deleteRow(rowEntity);

            var id = Util.goodMapValue(rowEntity, "id", 0);
            if (0 < id) {    //do not need to call service when deleting a new row
                var complaintInfo = Util.omitNg($scope.objectInfo);
                complaintInfo.location = null;
                ComplaintInfoService.saveComplaintInfo(complaintInfo).then(
                    function (complaintSaved) {
                        $scope.$emit("report-object-updated", complaintSaved);
                        return complaintSaved;
                    }
                );
            }
        };
    }
])

;


