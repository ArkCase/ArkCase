'use strict';

angular.module('admin').controller('Admin.FormWorkflowsLinkController', [ '$scope', '$element', '$interval', 'Admin.FormWorkflowsLinkService', 'hotRegisterer', 'MessageService', '$translate', function($scope, $element, $interval, formWorkflowsLinkService, hotRegisterer, messageService, $translate) {
    $scope.tableValues = [];
    $scope.data = {};
    $scope.columnsWidths = [];

    $scope.tableSettings = {
        height: 750
    };

    // We have to display whole table without vertical scroll.
    // This is HACK, because on some reason we can't get rows heights.
    // That's why we compare visible and total rows number every 100 msec and increase table's height if it is required
    // (skolomiets)
    var intervalId = $interval(function() {
        var instance = hotRegisterer.getInstance('formWorkflowsLink');
        if (!instance) {
            return;
        }
        var totalRows = instance.countRows();
        var visibleRows = instance.countVisibleRows();

        if (visibleRows < totalRows) {
            $scope.tableSettings.height += 100;
            instance.updateSettings($scope.tableSettings);
        }
        else {
            if(visibleRows > 0){
                $interval.cancel(intervalId);
            }
        }
    }, 100);

    //retrieve all data
    formWorkflowsLinkService.getFormWorkflowsData().then(function(payload) {

        $scope.data = angular.copy(payload.data);

        var cells = [];
        for (var i = 0; i < $scope.data.cells.length; i++) {
            var rowValues = _.pluck($scope.data.cells[i], 'value');
            cells.push(rowValues);
        }

        // Set Column Width
        for (var i = 0; i < $scope.data.columnsWidths.length; i++) {
            $scope.columnsWidths[i] = $scope.data.columnsWidths[i] * 2.5;
        }

        $scope.tableValues = cells;
    });

    $scope.cells = function(row, col, prop) {
        if ($scope.data && $scope.data.cells) {

            var cellProperties = {};
            var cellType = $scope.data.cells[row][col].type;

            // Add data for dropdown control if required
            if (cellType && $scope.data.meta[cellType]) {
                cellProperties.type = 'dropdown';
                cellProperties.source = $scope.data.meta[cellType];
            } else if (cellType == 'priority') {
                cellProperties.type = 'numeric'
                cellProperties.allowInvalid = false;
                cellProperties.validator = function(value, callback) {
                    callback((value >= 0) && (value <= 100) && (value % 1 === 0));
                }
            }

            // Apply styles to cells
            cellProperties.renderer = function(instance, td, row, col, prop, value, cellProperties) {
                Handsontable.renderers.TextRenderer.apply(this, arguments);

                var cellInfo = $scope.data.cells[row][col];
                if (cellInfo) {

                    td.style.wordWrap = 'break-word';

                    if (cellInfo.bgColor) {
                        td.style.background = cellInfo.bgColor;
                    }

                    if (cellInfo.color) {
                        td.style.color = cellInfo.color;
                    }

                    if (cellInfo.readonly) {
                        cellProperties.readOnly = true;
                    }

                    if (cellInfo.fontSize) {
                        td.style.fontSize = cellInfo.fontSize + "px";
                    }
                }
            };
            return cellProperties;
        }
    };

    $scope.undoChanges = function() {
        var handsontableInstance = hotRegisterer.getInstance('formWorkflowsLink');
        if (handsontableInstance.isUndoAvailable()) {
            handsontableInstance.undo();
        }
    };

    $scope.saveChanges = function() {
        var handsontableInstance = hotRegisterer.getInstance('formWorkflowsLink');
        formWorkflowsLinkService.saveData(handsontableInstance.getData()).then(function() {
            //success saved
            messageService.info($translate.instant('admin.formWorkflows.link.editRole.save.success'));
        }, function(payload) {
            //error saving
            messageService.error($translate.instant('admin.formWorkflows.link.editRole.save.error'));
        });
    };

    // Stop interval timer before component destroyed
    $scope.$on('$destroy', function() {
        $interval.cancel(intervalId);
    });
} ]);