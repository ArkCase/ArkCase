'use strict';

angular.module('admin').controller('Admin.SequenceManagementPartsModalConfigController', ['$scope', '$modalInstance', 'params', 'Util.DateService',
    '$filter', 'Object.LookupService', 'Admin.SequenceManagementService', 'UtilService', function ($scope, $modalInstance, params, UtilDateService,
                                                                                                   $filter, ObjectLookupService, AdminSequenceManagementService, Util) {


        $scope.sequence = params.sequence;
        $scope.sequencePart = {};
        $scope.isEdit = params.isEdit;
        if ($scope.isEdit) {
            $scope.sequencePart = params.selectedSequencePart;
            $scope.sequencePart.sequencePartName = params.selectedSequencePart.sequencePartName;
        }
        $scope.selectedItem = {};

        $scope.sequencePartsCurrentlySelected = {};
        $scope.sequencePartType = {};
        $scope.showRequiredFields = function () {
            $scope.sequencePartsCurrentlySelected = {};
            $scope.sequencePartType = $scope.selectedItem.key;
            $scope.sequencePartsCurrentlySelected[$scope.selectedItem.key] = true;
        };

        $scope.onClickCancel = function () {
            $modalInstance.dismiss('Cancel');
        };

        $scope.sequenceParts = [];
        var promiseSequenceParts = ObjectLookupService.getSequenceParts();
        promiseSequenceParts.then(function (sequenceParts) {
            $scope.sequenceParts = sequenceParts;
            if (params.isEdit) {
                $scope.selectedItem = _.find($scope.sequenceParts, function (part) {
                    return params.selectedSequencePart.sequencePartType === part.key;
                });
            }
            $scope.sequencePartsCurrentlySelected[$scope.selectedItem.key] = true;
        });

        ObjectLookupService.getSequenceObjectProperty().then(function(sequenceObjectProperty){
            $scope.sequenceObjectProperties = sequenceObjectProperty;
        });

        $scope.onClickOk = function () {
            var object = {};
            object.sequencePartType = $scope.selectedItem.key;
            object.sequencePartName = $scope.sequencePart.sequencePartName;
            object.sequenceCondition = $scope.sequencePart.sequenceCondition;
            object.sequenceStartNumber = $scope.sequencePart.sequenceStartNumber;
            object.sequenceIncrementSize = $scope.sequencePart.sequenceIncrementSize;
            object.sequenceNumberLength = $scope.sequencePart.sequenceNumberLength;
            object.sequenceFillBlanks = $scope.sequencePart.sequenceFillBlanks;
            object.sequenceArbitraryText = $scope.sequencePart.sequenceArbitraryText;
            object.sequenceObjectPropertyName = $scope.sequencePart.sequenceObjectPropertyName;
            object.sequenceDateFormat = $scope.sequencePart.sequenceDateFormat;
            $modalInstance.close(object);
        };

        var reloadGrid = function (data) {
            $scope.gridOptions.data = data;
        };

        $scope.loadPage = function () {
            AdminSequenceManagementService.getSequences().then(function (response) {
                if (!Util.isEmpty(response.data)) {

                    reloadGrid(response.data);
                }
            });
        };

        $scope.allNumbersExceptZero = '^(?!-?0)[-]?\\d{1,4}$';
        $scope.allNumbersLargerOrEqualZero = '\\d+';
        $scope.allNumbersLargerZero = '[1-9]\\d*';
    }]);