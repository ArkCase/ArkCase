'use strict';

angular.module('admin').controller('Admin.CMTemplatesController', ['$scope', 'Admin.CMTemplatesService',
    function ($scope, correspondenceService) {

        $scope.gridOptions = {
            enableColumnResizing: true,
            enableRowSelection: true,
            enableRowHeaderSelection: false,
            multiSelect: false,
            noUnselect: false,
            columnDefs: [],
            data: []
        };

        function AddFullPath(data) {
            angular.forEach(data, function (row, index) {
                row.fullPath = correspondenceService.fullDownloadPath(row.path);
            });
        }

        function ReloadGrid() {
            var templatesPromise = correspondenceService.retrieveTemplatesList();
            templatesPromise.then(function (templates) {
                AddFullPath(templates.data);
                $scope.gridOptions.data = templates.data;
            });
        }

        function upload(files) {
            $scope.selectedFiles = files;
            correspondenceService.uploadTemplate(files).then(
                function (result) {
                    ReloadGrid();
                }
            );
        }
        
        //get config and init grid settings
        $scope.config.$promise.then(function (config) {
            var componentConfig = _.find(config.components, {id: 'correspondenceManagementTemplates'});
            var columnDefs = componentConfig.columnDefs;

            $scope.gridOptions.columnDefs = columnDefs;

            $scope.upload = upload;

            ReloadGrid();
        });
    }
]);
