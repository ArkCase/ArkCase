'use strict';

angular.module('complaints').controller('Complaints.LocationsController', ['$scope', '$stateParams', '$q'
    , 'UtilService', 'HelperService', 'Complaint.InfoService', 'Object.LookupService'
    , function ($scope, $stateParams, $q, Util, Helper, ComplaintInfoService, ObjectLookupService) {

        var promiseConfig = Helper.requestComponentConfig($scope, "locations", function (config) {
            Helper.Grid.addDeleteButton(config.columnDefs, "grid.appScope.deleteRow(row.entity)");
            Helper.Grid.setColumnDefs($scope, config);
            Helper.Grid.setBasicOptions($scope, config);
            Helper.Grid.setInPlaceEditing($scope, config, $scope.updateRow);


            $q.all([promiseAddressTypes]).then(function (data) {
                $scope.gridOptions.enableRowSelection = false;    //need to turn off for inline edit
                for (var i = 0; i < $scope.config.columnDefs.length; i++) {
                    if (Helper.Lookups.ADDRESS_TYPES == $scope.config.columnDefs[i].lookup) {
                        $scope.gridOptions.columnDefs[i].enableCellEdit = true;
                        $scope.gridOptions.columnDefs[i].editableCellTemplate = "ui-grid/dropdownEditor";
                        $scope.gridOptions.columnDefs[i].editDropdownIdLabel = "type";
                        $scope.gridOptions.columnDefs[i].editDropdownValueLabel = "name";
                        $scope.gridOptions.columnDefs[i].editDropdownOptionsArray = $scope.addressTypes;
                        $scope.gridOptions.columnDefs[i].cellFilter = "mapKeyValue: col.colDef.editDropdownOptionsArray:'type':'name'";
                    }
                }
            });
        });

        var promiseAddressTypes = ObjectLookupService.getAddressTypes().then(
            function (addressTypes) {
                var options = [];
                Util.forEachStripNg(addressTypes, function (v, k) {
                    options.push({type: k, name: v});
                });
                $scope.addressTypes = options;
                return addressTypes;
            }
        );

        $scope.$on('complaint-updated', function (e, data) {
            $scope.complaintInfo = data;
            $scope.gridOptions.data = [Util.goodValue($scope.complaintInfo.location, {})];
            Helper.Grid.hidePagingControlsIfAllDataShown($scope, $scope.gridOptions.data.length);
        });


        $scope.addNew = function () {
            $scope.gridOptions.data.push({});
            Helper.Grid.hidePagingControlsIfAllDataShown($scope, $scope.gridOptions.data.length);
        };
        $scope.updateRow = function (rowEntity) {
            var complaintInfo = Util.omitNg($scope.complaintInfo);
            complaintInfo.location = complaintInfo.location || {};
            complaintInfo.location.streetAddress = rowEntity.streetAddress;
            complaintInfo.location.type = rowEntity.type;
            complaintInfo.location.city = rowEntity.city;
            complaintInfo.location.state = rowEntity.state;
            complaintInfo.location.zip = rowEntity.zip;
            if (ComplaintInfoService.validateLocation(complaintInfo.location)) {
                ComplaintInfoService.saveComplaintInfo(complaintInfo).then(
                    function (complaintSaved) {
                        $scope.$emit("report-complaint-updated", complaintSaved);
                        return complaintSaved;
                    }
                );
            }
        };
        $scope.deleteRow = function (rowEntity) {
            Helper.Grid.deleteRow($scope, rowEntity);

            var id = Util.goodMapValue(rowEntity, "id", 0);
            if (0 < id) {    //do not need to call service when deleting a new row
                var complaintInfo = Util.omitNg($scope.complaintInfo);
                complaintInfo.location = null;
                ComplaintInfoService.saveComplaintInfo(complaintInfo).then(
                    function (complaintSaved) {
                        $scope.$emit("report-complaint-updated", complaintSaved);
                        return complaintSaved;
                    }
                );
            }
        };
    }
])

;


