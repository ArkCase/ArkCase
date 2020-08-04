'use strict';

angular.module('cases').controller('Cases.LimitedReleaseModalController', ['$scope', '$modalInstance', '$modal', 'params', function ($scope, $modalInstance, $modal, params) {

    $scope.limitedDeliveryFlag = false;
    $scope.pageCount = params.pageCount;
    $scope.disposition = params.disposition;
    $scope.dispositionReasons = params.dispositionReasons;
    $scope.otherReason = params.otherReason;
    $scope.caseId = params.caseId;
    $scope.queueName = params.queue;

    if ($scope.disposition == null && params.requestType === 'Appeal') {
        openAppealDispositionCategoryModal();
    }

    $scope.onClickOk = function () {

        var data = {}
        data.requestDispositionCategory = $scope.requestDispositionCategory;
        data.dispositionValue = $scope.dispositionValue;
        data.requestOtherReason = $scope.requestOtherReason;
        data.dispositionReasons = $scope.requestDispositionReasons;
        data.limitedDeliveryFlag = $scope.limitedDeliveryFlag;

        $modalInstance.close(data);
    };

    function openAppealDispositionCategoryModal() {
        var params = {
            disposition: $scope.disposition,
            dispositionReasons: $scope.dispositionReasons,
            otherReason: $scope.otherReason,
            caseId: $scope.caseId,
            queue: $scope.queueName,
            isDispositionRequired: true
        };

        var modalInstance = $modal.open({
            animation: true,
            templateUrl: "modules/cases/views/components/add-appeal-disposition-category-modal.client.view.html",
            controller: 'Cases.AddAppealDispositionCategoriesModalController',
            size: 'md',
            backdrop: 'static',
            resolve: {
                params: function () {
                    return params;
                }
            }
        });

        modalInstance.result.then(function (data) {
            $scope.requestDispositionCategory = data.disposition;
            $scope.dispositionValue = data.dispositionValue;
            $scope.requestOtherReason = data.otherReason;
            $scope.requestDispositionReasons = data.dispositionReasons;
        });
    }

    $scope.onClickCancel = function () {
        $modalInstance.dismiss('cancel');
    };
}]);