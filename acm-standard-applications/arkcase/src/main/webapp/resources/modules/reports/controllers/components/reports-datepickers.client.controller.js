'use strict';

angular.module('reports').controller('Reports.DatepickersController', [ '$scope', 'UtilService', 'Util.DateService', 'LookupService', function($scope, Util, UtilDateService, LookupService) {

    $scope.maxDate = moment(new Date());
    $scope.minDate = moment.utc($scope.data.startDate).local();

    $scope.startDateChanged = function(data) {
        if (data) {
            if (data && moment($scope.data.startDate).isAfter($scope.data.endDate)) {
                $scope.data.endDate = data.dateInPicker;
                $scope.dateChangedManually = true;
            }
            $scope.minDate = moment.utc($scope.data.startDate).local();
        }
    };

    $scope.opened = {};
    $scope.opened.openedStart = false;
    $scope.opened.openedEnd = false;

    // Retrieve the json data from acm-reports-parameters.json file
    LookupService.getConfig("reportsParameters").then(function(payload) {
        $scope.reportsParameters = payload['dateSearchTypes'];    
    });

    $scope.$watchCollection('data.reportSelected', onReportSelected);

    function onReportSelected() {
        if (!$scope.data.reports || !$scope.data.reportSelected) {
            $scope.showReportParameters = false;
            return;
        }
        
        var dateSearchTypeValue = _.get($scope.reportsParameters, $scope.data.reportSelected);
        
        // reset start and end date
        $scope.data.startDate = new Date();
        $scope.data.endDate = new Date();
        
        if (Util.isEmpty(dateSearchTypeValue)) {
            $scope.data.dateSearchType = 'DATE_RANGE'; // by default DATE_RANGE is used
        } else {
            $scope.data.dateSearchType = dateSearchTypeValue;
        }
    };
} ]);
