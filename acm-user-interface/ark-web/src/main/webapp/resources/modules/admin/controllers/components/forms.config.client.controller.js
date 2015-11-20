'use strict';

angular.module('admin').controller('Admin.FormsConfigController', ['$scope', '$state', 'Admin.FormConfigService', 'HelperService', 'MessageService', '$translate',
    function ($scope, $state, FormConfigService, Helper, messageService, $translate) {
        $scope.gridOptions = {
            enableColumnResizing: true,
            enableRowSelection: true,
            enableRowHeaderSelection: false,
            multiSelect: false,
            noUnselect: false,
            columnDefs: [],
            totalItems: 0,
            data: []
        };
        $scope.config.$promise.then(function (config) {
            var plainFormConfig = _.find(config.components, {id: 'plainFormsConfig'});
            var columnDefs = plainFormConfig.columnDefs;
            var columnDef = addEditColumn();
            columnDefs.push(columnDef);

            Helper.Grid.addDeleteButton(columnDefs, "grid.appScope.deleteRow(row.entity)");

            $scope.gridOptions.columnDefs = columnDefs;
            $scope.formsDropdownOptions = plainFormConfig.formsDropdown;

            reloadGrid();
        });

        function addEditColumn() {
            var columnDef = {
                name: "edit",
                cellEditableCondition: false,
                width: 40,
                headerCellTemplate: "<span></span>",
                cellTemplate: "<span><i class='fa fa-pencil fa-lg' style='cursor :pointer' ng-click='grid.appScope.editRow(row.entity)'></i></span>"
            };
            return columnDef;
        }

        function reloadGrid() {
            var tempFormPromise = FormConfigService.retrievePlainForms();
            tempFormPromise.then(function (plainForms) {
                $scope.gridOptions.data = plainForms.data;

            });
        }

        $scope.editRow = function (rowEntity) {
            $state.go('editplainform', {key: rowEntity.key, target: rowEntity.target});
        };

        $scope.deleteRow = function (rowEntity) {
            $scope.deletePlainForm = rowEntity;
            FormConfigService.deletePlainForm($scope.deletePlainForm.key, $scope.deletePlainForm.target).then(function () {
                Helper.Grid.deleteRow($scope, $scope.deletePlainForm);
                messageService.info($translate.instant('successfully deleted form'));
            }, function () {
                messageService.error($translate.instant('error while deleting form'));
            });
        };

        $scope.openNewFrevvoForm = function(){
                $state.go('newplainform',{target: $scope.selectedTarget});
        }

    }
]);