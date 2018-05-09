'use strict';

angular.module('core').controller('PageController',
        [ '$scope', '$modal', '$sce', '$q', '$location', 'UtilService', 'Acm.LoginService', 'LoginWarningService', 'Authentication', 'ConfigService', function($scope, $modal, $sce, $q, $location, Util, AcmLoginService, LoginWarningService, Authentication, ConfigService) {

            $scope.fullScreenMode = false;
            $scope.isLeftMenuCollapsed = false;

            $scope.$on('isLeftMenuCollapsed', function(e, isLeftMenuCollapsed) {
                $scope.isLeftMenuCollapsed = isLeftMenuCollapsed;
            });

            var promiseLoginWarning = LoginWarningService.queryLoginWarning();
            var promiseUserInfo = Authentication.queryUserInfo();
            var promiseConfig = ConfigService.getModuleConfig("core");

            var notificationMessage = '';

            $q.all([ promiseLoginWarning, promiseUserInfo, promiseConfig ]).then(function(data) {
                var loginWarning = data[0];
                var userInfo = data[1];
                var config = data[2];

                var fullScreenPathExpressions = Util.goodMapValue(config, 'fullScreenPathExpressions', []);
                _.each(fullScreenPathExpressions, function(element) {
                    var expression = Util.goodMapValue(element, 'expression', '');
                    if (_.startsWith($location.path(), expression)) {
                        $scope.fullScreenMode = true;
                    }
                });

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
                        } else {
                            notificationMessage = userInfo.notificationMessage;
                        }
                    }
                }
                if (notificationMessage) {
                    showModalWarning(notificationMessage, isLoginGlobalWarning);
                }
            });

            function onLoginGlobalWarning(data) {
                if (Util.goodMapValue(data, "accepted", false)) {
                    LoginWarningService.setWarningAccepted(true);
                    LoginWarningService.setPasswordWarningAccepted(true);
                } else {
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
                    controller: [ '$scope', '$modalInstance', 'params', '$sce', function($scope, $modalInstance, params, $sce) {
                        $scope.isLoginGlobalWarning = params.isLoginGlobalWarning;
                        $scope.message = $sce.getTrustedHtml($sce.trustAsHtml(params.message));
                        $scope.onClickOk = function() {
                            $modalInstance.close({
                                accepted: true
                            });
                        };
                        $scope.onClickCancel = function() {
                            $modalInstance.close({
                                accepted: false
                            });
                        }
                    } ],
                    size: 'lg',
                    backdrop: "static",
                    resolve: {
                        params: function() {
                            return params;
                        }
                    }
                });

                function onModalClosed(data) {
                    if (isLoginGlobalWarning)
                        return onLoginGlobalWarning(data);
                    else
                        return onLoginPasswordWarning(data);
                }

                modalInstance.result.then(function(data) {
                    onModalClosed(data);
                });
            }
        } ]);
