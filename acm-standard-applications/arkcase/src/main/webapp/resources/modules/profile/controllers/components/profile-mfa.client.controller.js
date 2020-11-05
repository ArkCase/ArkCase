'use strict';

angular.module('profile').controller('Profile.MfaController',
        [ '$scope', '$modal', '$translate', '$state', 'ConfigService', 'UtilService', 'MessageService', 'Profile.MfaService', 'i18nConstants', function($scope, $modal, $translate, $state, ConfigService, Util, MessageService, ProfileMfaService, i18nConstants) {
            /**
             * Force refresh labels on grid when changing languages
             */
            $scope.$on(i18nConstants.UPDATE_EVENT, updateLocale);

            $scope.gridOptions = {
                data: [],
                totalItems: 0,
                columnDefs: [],
                enableRowHeaderSelection: false,
                useExternalPagination: false,
                enableSorting: false,
                enableColumnMenus: false,
                multiSelect: false,
                noUnselect: false,
                enableColumnResizing: true
            };
            ConfigService.getComponentConfig('profile', 'mfa').then(function(config) {
                $scope.config = config;
                $scope.gridOptions.columnDefs = config.columnDefs;
                $scope.gridOptions.paginationPageSizes = config.paginationPageSizes;
                $scope.gridOptions.paginationPageSize = config.paginationPageSize;
                retrieveFactors();
            });

            function retrieveFactors() {
                ProfileMfaService.getEnrolledFactors().then(function(data) {
                    $scope.gridOptions.data = Util.omitNg(data);
                    $scope.gridOptions.totalItems = $scope.gridOptions.data.length;
                    $scope.noData = $scope.gridOptions.totalItems === 0;
                });
                ProfileMfaService.getAvailableFactors().then(function(data) {
                    $scope.availableFactors = [];
                    var factors = Util.omitNg(data);
                    _.forEach(factors, function(factor) {
                        $scope.availableFactors.push({
                            value: factor.factorType,
                            label: ProfileMfaService.getFactorType(factor)
                        });
                    });
                });
            }

            $scope.getFactorType = function(rowEntity) {
                return ProfileMfaService.getFactorType(rowEntity);
            };

            $scope.getFactorDetails = function(rowEntity) {
                return ProfileMfaService.getFactorDetails(rowEntity);
            };

            $scope.isFactorEditDisabled = function(rowEntity) {
                return Util.isEmpty(rowEntity) || Util.isEmpty(rowEntity.factorType) || Util.isEmpty(rowEntity.id) || Util.compare('PENDING_ACTIVATION', rowEntity.status) || Util.compare('token:software:totp', rowEntity.factorType);
            };

            $scope.isFactorActivateDisabled = function(rowEntity) {
                return Util.isEmpty(rowEntity) || Util.isEmpty(rowEntity.factorType) || Util.isEmpty(rowEntity.id) || !Util.compare('PENDING_ACTIVATION', rowEntity.status);
            };

            $scope.createFactor = function() {
                var factorInfo = {
                    provider: 'OKTA',
                    profile: {}
                };
                showCreateEditFactorModal(factorInfo, false);
            };

            $scope.activateFactor = function(rowEntity) {
                showActivateFactorModal(Util.omitNg(angular.copy(rowEntity)));
            };

            $scope.editFactor = function(rowEntity) {
                showCreateEditFactorModal(Util.omitNg(angular.copy(rowEntity)), true);
            };

            $scope.deleteFactor = function(rowEntity) {
                showDeleteConfirmModal(Util.omitNg(angular.copy(rowEntity)));
            };

            $scope.translateFactorStatus = function(status) {
                if (status && !Util.isEmpty(status.trim())) {
                    return $translate.instant("profile.mfa.table.factorStatus." + status.trim().toLowerCase());
                } else {
                    return status;
                }
            };

            function showCreateEditFactorModal(factorInfo, isEdit) {
                var factorDialog = $modal.open({
                    templateUrl: 'modules/profile/views/dialogs/profile-factor-modal.client.view.html',
                    controller: 'Profile.MfaFactorModalController',
                    size: 'md',
                    resolve: {
                        factorInfo: function() {
                            return factorInfo;
                        },
                        isEdit: function() {
                            return isEdit;
                        },
                        availableFactors: function() {
                            return $scope.availableFactors;
                        }
                    }
                });

                factorDialog.result.then(function(factorInfo) {
                    if (!Util.isEmpty(factorInfo)) {
                        if (isEdit) {
                            saveUpdatedFactor(factorInfo);
                        } else {
                            saveNewFactor(factorInfo);
                        }
                    }
                });
            }

            function saveNewFactor(factorInfo) {
                if (!Util.isEmpty(factorInfo)) {
                    ProfileMfaService.enrollFactor(factorInfo).then(function(data) {
                        retrieveFactors();
                        MessageService.info($translate.instant('profile.mfa.createSucceeded'));
                    }, function(error) {
                        MessageService.error($translate.instant('profile.mfa.createFailed') + ": " + error.data);
                    });
                }
            }

            function saveUpdatedFactor(factorInfo) {
                if (!Util.isEmpty(factorInfo) && !Util.isEmpty(factorInfo.id)) {
                    ProfileMfaService.deleteFactor(factorInfo.id).then(function(data) { // delete old factor
                        ProfileMfaService.enrollFactor(factorInfo).then(function(data) { // save replacement
                            retrieveFactors();
                            MessageService.info($translate.instant('profile.mfa.editSucceeded'));
                        }, function(error) {
                            retrieveFactors();
                            MessageService.error($translate.instant('profile.mfa.editFailed') + ": " + error.data);
                        });
                    }, function(error) {
                        MessageService.error($translate.instant('profile.mfa.editFailed') + ": " + error.data);
                    });
                }
            }

            function showActivateFactorModal(factorInfo) {
                var activateFactorDialog = $modal.open({
                    templateUrl: 'modules/profile/views/dialogs/profile-activate-factor-modal.client.view.html',
                    controller: 'Profile.MfaActivateFactorModalController',
                    size: 'md',
                    resolve: {
                        factorInfo: function() {
                            return factorInfo;
                        }
                    }
                });

                activateFactorDialog.result.then(function(activateInfo) {
                    if (!Util.isEmpty(activateInfo) && !Util.isEmpty(activateInfo.factorId) && !Util.isEmpty(activateInfo.activationCode)) {
                        performActivation(activateInfo);
                    }
                });
            }

            function performActivation(activateInfo) {
                if (!Util.isEmpty(activateInfo) && !Util.isEmpty(activateInfo.factorId) && !Util.isEmpty(activateInfo.activationCode)) {
                    ProfileMfaService.activateFactor(activateInfo).then(function(data) {
                        retrieveFactors();
                        MessageService.info($translate.instant('profile.mfa.activateSucceeded'));
                    }, function(error) {
                        MessageService.info($translate.instant('profile.mfa.activateFailed'));
                    });
                }
            }

            function showDeleteConfirmModal(factorInfo) {
                var deleteConfirmDialog = $modal.open({
                    templateUrl: 'modules/profile/views/dialogs/profile-delete-factor-modal.client.view.html',
                    controller: 'Profile.MfaDeleteFactorModalController',
                    size: 'sm',
                    resolve: {
                        factorInfo: function() {
                            return factorInfo;
                        }
                    }
                });

                deleteConfirmDialog.result.then(function(deleteInfo) {
                    if (!Util.isEmpty(deleteInfo) && deleteInfo.deleteFactorConfirmed) {
                        performDeletion(factorInfo);
                    }
                });
            }

            function performDeletion(factorInfo) {
                if (!Util.isEmpty(factorInfo) && !Util.isEmpty(factorInfo.id)) {
                    ProfileMfaService.deleteFactor(factorInfo.id).then(function(data) {
                        retrieveFactors();
                        MessageService.info($translate.instant('profile.mfa.deleteSucceeded'));
                    }, function(error) {
                        MessageService.error($translate.instant('profile.mfa.deleteFailed') + ": " + error.data);
                    });
                }
            }

            function updateLocale() {
                $state.reload();
            }
        } ]);