'use strict';

angular.module('cases').controller('Cases.RequestDenyDispositionCategoriesModalController', ['$scope', '$q', '$modal', '$modalInstance', 'params', 'Object.LookupService', 'Case.ExemptionService', 'ObjectService', 'UtilService', '$translate', function ($scope, $q, $modal, $modalInstance, params, ObjectLookupService, CaseExemptionService, ObjectService, Util, $translate) {

    $scope.objectId = params.objectId;
    $scope.customReason = '';

    ObjectLookupService.getLookupByLookupName('requestDispositionSubType').then(function (requestDispositionType) {
        $scope.requestDispositionCategories = requestDispositionType;
        $scope.requestDispositionCategory = $scope.requestDispositionCategories[0].key;
    });
    
    ObjectLookupService.getLookupByLookupName('requestOtherReason').then(function (data) {
        $scope.requestOtherReasons = data;
        $scope.requestOtherReason = $scope.requestOtherReasons[0].key;
    });

    var hasExemptionsOnRequest = CaseExemptionService.hasExemptionOnAnyDocumentsOnRequest($scope.objectId, ObjectService.ObjectTypes.CASE_FILE);

    $q.all([hasExemptionsOnRequest]).then(function (value) {
        $scope.hasExemptionOnAnyDocumentsOnRequest = value[0].data;
    });

    $scope.changeDispositionCategory = function(dispositionCategory){
        if(dispositionCategory == 'full-denial-exemption' && !$scope.hasExemptionOnAnyDocumentsOnRequest) {
            $scope.showErrorMessageNoExemptions = true;
        } else if((dispositionCategory == 'no-record' || dispositionCategory == 'records-referred' || dispositionCategory == 'no-agency-record') && $scope.hasExemptionOnAnyDocumentsOnRequest) {
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