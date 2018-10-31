'use strict';

angular.module('reports').controller('Reports.DatepickersController', [ '$scope', 'UtilService', 'Util.DateService', 'LookupService', function($scope, Util, UtilDateService, LookupService) {

    $scope.startDateChanged = function() {
        var validateDate = UtilDateService.validateFromDate($scope.data.startDate, $scope.data.endDate);
        $scope.data.startDate = validateDate.from;
        $scope.data.endDate = validateDate.to;
    };

    $scope.endDateChanged = function() {
        var validateDate = UtilDateService.validateToDate($scope.data.startDate, $scope.data.endDate);
        $scope.data.startDate = validateDate.from;
        $scope.data.endDate = validateDate.to;
    };

    $scope.opened = {};
    $scope.opened.openedStart = false;
    $scope.opened.openedEnd = false;

    // Retrieve the json data from acm-reports-parameters.json file
    LookupService.getConfig("reportsParameters").then(function(payload) {
        $scope.reportsParameters = payload['reports-parameters'];    
    });

    $scope.$watchCollection('data.reportSelected', onReportSelected);

    function onReportSelected() {
        if (!$scope.data.reports || !$scope.data.reportSelected) {
            $scope.showReportParameters = false;
            return;
        }
        
        var reportParameters = _.find($scope.reportsParameters, {
            "reportName": $scope.data.reportSelected
        });
        
        // reset start and end date
        $scope.data.startDate = new Date();
        $scope.data.endDate = new Date();
        
        if (!reportParameters) {
            $scope.data.dateSearchType = 'DATE_RANGE'; // by default DATE_RANGE is used
        } else {
            $scope.data.dateSearchType = reportParameters.dateSearchType;
        }
    };
} ]);