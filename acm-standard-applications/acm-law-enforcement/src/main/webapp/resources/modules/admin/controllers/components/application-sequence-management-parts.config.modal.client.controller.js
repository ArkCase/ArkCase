'use strict';

angular.module('admin').controller('Admin.SequenceManagementPartsModalController',
    [ '$scope', '$modal', 'params', 'Helper.UiGridService', 'MessageService', 'UtilService', 'Dialog.BootboxService', '$translate',
        '$modalInstance','Object.LookupService'
        , function($scope, $modal, params, HelperUiGridService, MessageService, Util, DialogService, $translate, $modalInstance, ObjectLookupService) {

        var gridHelper = new HelperUiGridService.Grid({
            scope: $scope
        });

        $scope.config = angular.copy(params.config);

        console.log($scope.config);

        gridHelper.addButton($scope.config, "edit");
        gridHelper.addButton($scope.config, "delete");
        gridHelper.setColumnDefs($scope.config);
        gridHelper.setBasicOptions($scope.config);
        gridHelper.disableGridScrolling($scope.config);

        $scope.gridOptions = {
            enableColumnResizing: true,
            enableRowSelection: true,
            enableRowHeaderSelection: false,
            multiSelect: false,
            noUnselect: false,
            columnDefs: $scope.config.columnDefs,
            paginationPageSizes: $scope.config.paginationPageSizes,
            paginationPageSize: $scope.config.paginationPageSize,
            totalItems: 0,
            data: params.sequenceParts
        };

        /*   var reloadGrid = function(data) {
               $scope.holidaySchedule.includeWeekends = data.includeWeekends;
               $scope.gridOptions.data = data.holidays;
           };
   */
        /*   $scope.loadPage = function() {
               AdminSequenceManagementService.getHolidays().then(function(response) {
                   if (!Util.isEmpty(response.data)) {

                       reloadGrid(response.data);
                   }
               });
           };*/
        //     $scope.loadPage();
        //  var deleteHoliday = function(holidayConf) {
        //      var holidayConfiguration = {
        //          "includeWeekends": $scope.holidaySchedule.includeWeekends,
        //          "holidays": holidayConf
        //      };
        //      saveConfig(holidayConfiguration);
        //  };

        $scope.save = function() {

            //saveConfig(holidayConfig);

        };
        /*   var saveConfig = function(holidayConfiguration) {
               AdminSequenceManagementService.saveHolidays(holidayConfiguration).then(function(data) {
                   MessageService.succsessAction();
                   reloadGrid(data.config.data);
               }, function() {
                   MessageService.errorAction();
               });
           };*/

        function showModal(sequencePart, isEdit) {
            var params = {};
            params.sequencePart = sequencePart;
            params.isEdit = isEdit;
            params.config = $scope.config;

            var modalInstance = $modal.open({
                animation: true,
                templateUrl: 'modules/admin/views/components/application-sequence-management-parts-modification.config.modal.client.view.html',
                controller: 'Admin.SequenceManagementPartsModalController',
                size: 'md',
                backdrop: 'static',
                resolve: {
                    params: function() {
                        return params;
                    }
                }
            });
            return modalInstance.result;
        }

        $scope.addNew = function() {
            var x = {};
            showModal(x, false).then(function(data) {
                console.log(data);

            });
        };

        $scope.editRow = function(rowEntity) {

            var entity = angular.copy(rowEntity);
            showModal(entity, true).then(function(data) {
                var element = data;

                var itemExist = _.find($scope.gridOptions.data, function(sequence) {
                    return (element.sequencePartName === sequence.sequencePartName && element.sequencePartType === sequence.sequencePartType);
                });

                if (!itemExist) {
                    rowEntity.sequencePartName = data.sequencePartName;
                    rowEntity.sequencePartType = data.sequencePartType;
                    $scope.save();
                } else {
                    DialogService.alert($translate.instant('admin.application.sequenceManagementParts.config.message'));
                }
            });
        };

        $scope.onClickCancel = function() {
            $modalInstance.dismiss('Cancel');
        };

        ObjectLookupService.getSequenceParts().then(function(sequenceParts) {
            $scope.sequenceParts = sequenceParts;
        });

        $scope.test = {};
        $scope.test["AUTOINCREMENT"] = true;

        /*
            $scope.deleteRow = function(rowEntity) {
                var sequenceConfig = angular.copy($scope.gridOptions.data);
                _.remove(sequenceConfig, function(item) {
                    return item.sequenceName === rowEntity.sequenceName;
                });
                deleteSequence(sequenceConfig);
            };*/

    } ]);