'use strict';

angular.module('admin').controller('Admin.FormsConfigController', ['$scope', '$state', 'Admin.FormConfigService', 'HelperService', 'MessageService', '$translate',
    function ($scope, $state, FormConfigService, Helper, messageService, $translate) {
        $scope.tableConfig = {
            formsConfig: {
                "formsDropdown": [
                    {"id": "CASE_FILE", "name": "Case File"},
                    {"id": "COMPLAINT", "name": "Complaint"}
                ],
                "columnDefs": [
                    {
                        "name": "name",
                        "displayName": "admin.forms.data.name",
                        "visible": true,
                        "headerCellFilter": "translate"
                    }, {
                        "name": "applicationName",
                        "displayName": "admin.forms.data.applicationName",
                        "visible": true,
                        "headerCellFilter": "translate"
                    }, {
                        "name": "description",
                        "displayName": "admin.forms.data.description",
                        "visible": true,
                        "headerCellFilter": "translate"
                    }, {
                        "name": "target",
                        "displayName": "admin.forms.data.target",
                        "visible": true,
                        "headerCellFilter": "translate"
                    }]
            }
        };
       // console.log($scope.selectedTarget);
        // console.log($scope.tableConfig.formsConfig.formsDropdown[0]);
       // $scope.targets = tableConfig.formsConfig.formsDropdown;
      //  $scope.selectedTarget = $scope.targets[0].id;
        //$scope.disabledSelectButton = true;

        $scope.$watch('selectedTarget', function (){
         console.log($scope.selectedTarget);
        });

        /*$scope.onTargetSelect = function () {
            //$scope.selectedTarget = selectedTarget;
            console.log($scope.selectedTarget);
            if ($scope.selectedTarget !== $scope.targets[0].id) {
                $scope.disabledSelectButton = false;
            } else {
                $scope.disabledSelectButton = true;
            }
        };*/

        //get config and init grid settings

        var columnDefs = $scope.tableConfig.formsConfig.columnDefs;
        var columnDef = addEditColumn();
        columnDefs.push(columnDef);

        Helper.Grid.addDeleteButton(columnDefs, "grid.appScope.deleteRow(row.entity)");

        $scope.gridOptions = {
            enableColumnResizing: true,
            enableRowSelection: true,
            enableRowHeaderSelection: false,
            multiSelect: false,
            noUnselect: false,
            columnDefs: columnDefs,
            totalItems: 0,
            data: []
        };

        reloadGrid();


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