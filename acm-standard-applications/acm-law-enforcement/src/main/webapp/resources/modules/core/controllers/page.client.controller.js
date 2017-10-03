'use strict';

angular.module('core').controller('PageController', ['$scope', '$modal', '$sce', '$q', 'UtilService', 'Acm.LoginService'
    , 'LoginWarningService', 'Authentication'
    , function ($scope, $modal, $sce, $q, Util, AcmLoginService, LoginWarningService, Authentication) {
        $scope.isLeftMenuCollapsed = false;

        $scope.$on('isLeftMenuCollapsed', function (e, isLeftMenuCollapsed) {
            $scope.isLeftMenuCollapsed = isLeftMenuCollapsed;
        });

        var promiseLoginWarning = LoginWarningService.queryLoginWarning();
        var promiseUserInfo = Authentication.queryUserInfo();

        var notificationMessage = '';

        $q.all([promiseLoginWarning, promiseUserInfo]).then(function (data) {
                var loginWarning = data[0];
                var userInfo = data[1];

                var isLoginGlobalWarning = false;
                if (Util.goodMapValue(loginWarning, "enabled", false)) {
                    if (!LoginWarningService.getWarningAccepted()) {
                        notificationMessage = loginWarning.message;
                        isLoginGlobalWarning = true;
                    }
                }
                if (!LoginWarningService.getPasswordWarningAccepted()) {
                    if (userInfo.notificationMessage) {
                        if (notificationMessage) {
                            notificationMessage += "<br/>";
                            notificationMessage += userInfo.notificationMessage;
                        }
                        else {
                            notificationMessage = userInfo.notificationMessage;
                        }
                    }
                }
                if (notificationMessage) {
                    showModalWarning(notificationMessage, isLoginGlobalWarning);
                }
            }
        );

        function onLoginGlobalWarning(data) {
            if (Util.goodMapValue(data, "accepted", false)) {
                LoginWarningService.setWarningAccepted(true);
                LoginWarningService.setPasswordWarningAccepted(true);
            }
            else {
                //redirect to logout
                AcmLoginService.logout();
            }
        }

        function onLoginPasswordWarning(data) {
            LoginWarningService.setPasswordWarningAccepted(true);
        }

        function showModalWarning(message, isLoginGlobalWarning) {
            var params = {
                message: "<strong>" + message + "</strong>",
                isLoginGlobalWarning: isLoginGlobalWarning
            };

            var modalInstance = $modal.open({
                animation: true,
                templateUrl: 'modules/core/views/warning-modal.client.view.html',
                controller: ['$scope', '$modalInstance', 'params', '$sce', function ($scope, $modalInstance, params, $sce) {
                    $scope.isLoginGlobalWarning = params.isLoginGlobalWarning;
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

            function onModalClosed(data) {
                if (isLoginGlobalWarning) return onLoginGlobalWarning(data);
                else return onLoginPasswordWarning(data);
            }

            modalInstance.result.then(function (data) {
                onModalClosed(data);
            });
        }
    }
]);
