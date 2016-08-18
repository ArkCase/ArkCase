'use strict';

angular.module('core').controller('PageController', ['$scope', '$modal', '$sce', 'UtilService', 'Acm.LoginService', 'LoginWarningService',
    function ($scope, $modal, $sce, Util, AcmLoginService, LoginWarningService) {
        $scope.isLeftMenuCollapsed = false;

        $scope.$on('isLeftMenuCollapsed', function (e, isLeftMenuCollapsed) {
            $scope.isLeftMenuCollapsed = isLeftMenuCollapsed;
        });

        LoginWarningService.queryLoginWarning().then(
            function (data) {
                if (Util.goodMapValue(data, "enabled", false)) {
                    //if (! sessionStorage.getItem('warningAccepted'))
                    if (!LoginWarningService.getWarningAccepted()) {
                        showModalWarning(data);
                    }
                }
            });


        function showModalWarning(data) {
            var params = {
                message: data.message
            };

            var modalInstance = $modal.open({
                animation: true,
                templateUrl: 'modules/core/views/warning-modal.client.view.html',
                controller: ['$scope', '$modalInstance', 'params', '$sce', function ($scope, $modalInstance, params, $sce) {
                    $scope.message = $sce.getTrustedHtml($sce.trustAsHtml(params.message));
                    $scope.onClickOk = function () {
                        $modalInstance.close({accepted: true});
                    };
                    $scope.onClickCancel = function () {
                        $modalInstance.close({accepted: false});
                    }
                }],
                size: 'lg',
                backdrop: "static",
                resolve: {
                    params: function () {
                        return params;
                    }
                }
            });
            modalInstance.result.then(function (data) {
                if (Util.goodMapValue(data, "accepted", false)) {
                    //put in local/session storage that user has accepted warning
                    //sessionStorage.setItem('warningAccepted', true);
                    LoginWarningService.setWarningAccepted(true);
                }
                else {
                    //redirect to logout
                    AcmLoginService.logout();
                }
            });
        }
    }
]);
