'use strict';

angular.module('cases').controller('Cases.RequestDenyDispositionCategoriesModalController', ['$scope', '$q', '$modal', '$modalInstance', 'params', 'Object.LookupService', 'Case.ExemptionService', 'ObjectService', 'UtilService', '$translate', function ($scope, $q, $modal, $modalInstance, params, ObjectLookupService, CaseExemptionService, ObjectService, Util, $translate) {

    $scope.objectId = params.objectId;
    $scope.customReason = '';

    ObjectLookupService.getLookupByLookupName('requestDispositionSubType').then(function (requestDispositionType) {
        $scope.requestDispositionCategories = requestDispositionType;
        var defaultRequestDispositionCategory = ObjectLookupService.getPrimaryLookup($scope.requestDispositionCategories);
        if (defaultRequestDispositionCategory) {
            $scope.requestDispositionCategory = defaultRequestDispositionCategory.key;
        }
    });

    ObjectLookupService.getLookupByLookupName('requestOtherReason').then(function (data) {
        $scope.requestOtherReasons = data;
        var defaultRequestOtherReason = ObjectLookupService.getPrimaryLookup($scope.requestOtherReasons);
        if (defaultRequestOtherReason) {
            $scope.requestOtherReason = defaultRequestOtherReason.key;
        } else {
            $scope.requestOtherReason = $scope.requestOtherReasons[0].key;
        }
    });

    var hasExemptionsOnRequestOnAnyDocuments = CaseExemptionService.hasExemptionOnAnyDocumentsOnRequest($scope.objectId, ObjectService.ObjectTypes.CASE_FILE);
    var hasExemptionsOnRequestManuallyAdded = CaseExemptionService.getExemptionCode($scope.objectId, 'CASE_FILE');

    $q.all([hasExemptionsOnRequestOnAnyDocuments, hasExemptionsOnRequestManuallyAdded]).then(function (value) {
        $scope.hasExemptionOnAnyDocumentsOnRequest = value[0].data;
        $scope.hasExemptionsOnRequestManuallyAdded = value[1].data;
    });

    $scope.changeDispositionCategory = function(dispositionCategory){
        if(dispositionCategory != 'other') {
            $scope.customReason = '';
            $scope.requestOtherReason = null;
        }
        if(dispositionCategory == 'full-denial-exemption' && !$scope.hasExemptionOnAnyDocumentsOnRequest && $scope.hasExemptionsOnRequestManuallyAdded.length <= 0) {
            $scope.showErrorMessageNoExemptions = true;
        } else if((dispositionCategory == 'no-record' || dispositionCategory == 'records-referred' || dispositionCategory == 'no-agency-record') && ($scope.hasExemptionOnAnyDocumentsOnRequest || $scope.hasExemptionsOnRequestManuallyAdded.length > 0)) {
            $scope.showErrorMessageHasExemptions = true;
        } else {
            $scope.showErrorMessageNoExemptions = false;
            $scope.showErrorMessageHasExemptions = false;
        }
    };

    $scope.onClickSave = function () {
        var disposition = _.find($scope.requestDispositionCategories, {
            key: $scope.requestDispositionCategory
        });
        if(!Util.isEmpty($scope.customReason) && $scope.requestDispositionCategory == 'other') {
            $scope.requestOtherReason = $scope.customReason;
        }
        $scope.requestOtherReason = $scope.requestDispositionCategory != 'other' ? null : $scope.requestOtherReason;
        $modalInstance.close({
            requestDispositionCategory:$scope.requestDispositionCategory,
            dispositionValue: $translate.instant(disposition.value),
            requestOtherReason: $scope.requestOtherReason });
    };

    $scope.onClickCancel = function () {
        $modalInstance.dismiss('cancel');
    };

}]);