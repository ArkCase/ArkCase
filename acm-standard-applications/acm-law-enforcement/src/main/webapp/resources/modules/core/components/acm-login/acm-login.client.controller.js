'use strict';

var AcmLoginController = [ "$q", "$scope", "$document", "$state", "$translate", "UtilService", "Util.TimerService", "Authentication", "Acm.LoginService", "Dialog.BootboxService", "AuditService", "ConfigService", 'Admin.ApplicationSettingsService',
        function($q, $scope, $document, $state, $translate, Util, UtilTimerService, Authentication, AcmLoginService, Dialog, AuditService, ConfigService, ApplicationSettingsService) {
            var ctrl = this;

            var promiseAppSetting = ApplicationSettingsService.getApplicationPropertiesConfig().then(function (response) {
                ctrl.idleLimit = Util.goodValue(response.data[ApplicationSettingsService.PROPERTIES.IDLE_LIMIT], 600000);
                ctrl.idlePull = Util.goodValue(response.data[ApplicationSettingsService.PROPERTIES.IDLE_PULL], 5000);
                ctrl.idleConfirm = Util.goodValue(response.data[ApplicationSettingsService.PROPERTIES.IDLE_CONFIRM], 15000);
                return response;
            });
            //var promiseConfig = ConfigService.getComponentConfig("core", "acmLogin").then(function (config) {
            //    ctrl.idleLimit = Util.goodValue(config.idleLimit, 600000);     //600000 - limit of 10 minutes
            //    ctrl.idlePull = Util.goodValue(config.idlePull, 5000);         //5000   - every 5 seconds
            //    ctrl.idleConfirm = Util.goodValue(config.idleConfirm, 15000);   //15000 - limit of 15 seconds
            //    return config;
            //});

            Authentication.queryUserInfo().then(function(userInfo) {
                AcmLoginService.setUserId(Util.goodMapValue(userInfo, "userId"));
                return userInfo;
            });

            var promiseSetLogin = AcmLoginService.getSetLoginPromise();
            $q.all([ promiseAppSetting, promiseSetLogin ]).then(function(data) {
                ctrl.waitConfirm = false;
                UtilTimerService.useTimer("AutoLogout", ctrl.idlePull, function() {
                    var isLogin = AcmLoginService.isLogin();
                    if (!isLogin) {
                        AcmLoginService.logout();
                        return false;
                    }

                    if (ctrl.waitConfirm) {
                        removeCanceledConfirmDialog();

                    } else { //if (!ctrl.waitConfirm) {
                        var sinceIdle = AcmLoginService.getSinceIdle();
                        if (ctrl.idleLimit < sinceIdle) {
                            ctrl.onIdleDetected();
                        }
                    }

                    return true;
                });

            });

            var removeCanceledConfirmDialog = function() {
                if (AcmLoginService.isConfirmCanceled()) {
                    UtilTimerService.removeListener("AboutToLogout");
                    Dialog.hideAll();
                    ctrl.waitConfirm = false;
                }
            };

            ctrl.onIdleDetected = function() {
                AuditService.genericAudit('com.armedia.acm.session.timeout');
                UtilTimerService.useTimer("AboutToLogout", ctrl.idleConfirm, function() {
                    removeCanceledConfirmDialog();
                    AcmLoginService.logout();
                    return false;
                });

                ctrl.waitConfirm = true;
                AcmLoginService.setConfirmCanceled(false);
                bootbox.confirm({
                    message: $translate.instant("common.comp.acmLogin.confirmLogout"),
                    buttons: {
                        confirm:{
                            label:$translate.instant("common.comp.acmLogin.logoutBtn")
                        },
                        cancel: {
                            label:$translate.instant("common.comp.acmLogin.cancelBtn")
                        }
                    },
                    callback: function(result){
                        UtilTimerService.removeListener("AboutToLogout");
                        if (result) {
                            UtilTimerService.removeListener("AutoLogout");
                            AcmLoginService.logout();
                        } else {
                            AcmLoginService.setConfirmCanceled(true);
                            AcmLoginService.setLastIdle();
                        }
                        ctrl.waitConfirm = false;
                    }
                })
            };

            $document.on("mousemove", function(e) {
                ctrl.onUserActivity(e);
            });
            $document.on("keypress", function(e) {
                ctrl.onUserActivity(e);
            });

            ctrl.onUserActivity = function() {
                AcmLoginService.setLastIdle();
            };

        } ];
