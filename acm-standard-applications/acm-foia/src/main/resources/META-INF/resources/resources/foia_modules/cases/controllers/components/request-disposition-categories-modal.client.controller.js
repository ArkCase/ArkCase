'use strict';

angular.module('cases').controller('Cases.RequestDispositionCategoriesModalController', ['$scope', '$q', '$modal', '$modalInstance', 'params', 'Object.LookupService', 'Case.ExemptionService', 'ObjectService', '$translate', function ($scope, $q, $modal, $modalInstance, params, ObjectLookupService, CaseExemptionService, ObjectService, $translate) {

    $scope.objectId = params.objectId;
    
    ObjectLookupService.getLookupByLookupName('requestDispositionType').then(function (requestDispositionType) {
        $scope.requestDispositionCategories = requestDispositionType;
        $scope.requestDispositionCategory = $scope.requestDispositionCategories[0].key;
    });

    var hasExemptionsOnRequest = CaseExemptionService.hasExemptionOnAnyDocumentsOnRequest($scope.objectId, ObjectService.ObjectTypes.CASE_FILE);
    
    $q.all([hasExemptionsOnRequest]).then(function (value) {
        $scope.hasExemptionOnAnyDocumentsOnRequest = value[0].data;
    });
    
    $scope.onClickSave = function () {
        var deferred = $q.defer();
        var disposition = _.find($scope.requestDispositionCategories, {
           key: $scope.requestDispositionCategory 
        });
        if($scope.requestDispositionCategory == 'grantedInFull' && $scope.hasExemptionOnAnyDocumentsOnRequest) {
            openConfirmationModal(deferred);
        } else {
            deferred.resolve();
            $modalInstance.close({
                    requestDispositionCategory: $scope.requestDispositionCategory, 
                    dispositionValue: $translate.instant(disposition.value)});
        }
        deferred.promise.then(function() {
            $modalInstance.close({
                requestDispositionCategory: $scope.requestDispositionCategory,
                dispositionValue: $translate.instant(disposition.value)});
        });
    };
    
    function openConfirmationModal(deferred){
        var modalInstance = $modal.open({
            animation: $scope.animationsEnabled,
            templateUrl: 'modules/cases/views/components/request-disposition-confirm-modal.client.view.html',
            controller: 'Cases.RequestDispositionConfirmModalController',
            size: 'md',
            backdrop: 'static'
        });

        modalInstance.result.then(function () {
            deferred.resolve();
        }, function () {
            deferred.reject();
            $scope.loading = false;
            $scope.loadingIcon = "fa fa-check";
        });
    }

    $scope.onClickCancel = function () {
        $modalInstance.dismiss('cancel');
    };
    
}]);