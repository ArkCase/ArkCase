'use strict';
/**
 * @ngdoc service
 * @name CreateNewItemDialogService
 *
 * @description
 *
 * Provide methods for displaying dialog windows for creating new objects
 */
angular.module('core').service('CreateNewItemDialogService', [
    '$document', '$compile', '$rootScope', '$translate', '$modal', '$q',
    function ($document, $compile, $rootScope, $translate, $modal, $q) {

        return {
            /**
             * @ngdoc method
             * @name CreateNewItemDialogService#createNewTask
             *
             * @description
             * Display "Create New Task" dialog window
             *
             * @param {Object} params - metadata for the dialog
             *
             * @returns {Promise} Returns promise that will be resolved to response object when user will close window
             *
             */
            createNewTask: function (params) {
                var scope = $rootScope.$new();
                var modalDefer = $q.defer();

                scope.onModalSave = function () {
                    modalInstance.close();
                    modalDefer.resolve({
                        action: 'SAVE'
                    });
                };

                scope.onModalCancel = function () {
                    modalInstance.close();
                    modalDefer.resolve({
                        action: 'CANCEL'
                    });
                };

                var modalInstance = $modal.open({
                    animation: true,
                    templateUrl: 'modules/tasks/views/components/task-new-task.client.view.html',
                    controller: 'Tasks.NewTaskController',
                    size: 'lg',
                    backdrop: 'static',
                    scope: scope,
                    resolve: {
                        translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                            $translatePartialLoader.addPart('common');
                            $translatePartialLoader.addPart('tasks');
                            return $translate.refresh();
                        }],

                        modalParams: function () {
                            return params || {};
                        }
                    }
                });

                return modalDefer.promise;
            },

            /**
             * @ngdoc method
             * @name CreateNewItemDialogService#createNewPerson
             *
             * @description
             * Display "Create New Person" dialog window
             *
             * @param {Object} params - metadata for the dialog
             *
             * @returns {Promise} Returns promise that will be resolved to response object when user will close window
             *
             */
            createNewPerson: function (params) {
                var scope = $rootScope.$new();
                var modalDefer = $q.defer();

                scope.onModalSave = function () {
                    modalInstance.close();
                    modalDefer.resolve({
                        action: 'SAVE'
                    });
                };

                scope.onModalCancel = function () {
                    modalInstance.close();
                    modalDefer.resolve({
                        action: 'CANCEL'
                    });
                };

                var modalInstance = $modal.open({
                    animation: true,
                    templateUrl: 'modules/people/views/components/person-new-person.client.view.html',
                    controller: 'People.NewPersonController',
                    size: 'lg',
                    backdrop: 'static',
                    scope: scope,
                    resolve: {
                        translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                            $translatePartialLoader.addPart('common');
                            $translatePartialLoader.addPart('people');
                            return $translate.refresh();
                        }],

                        modalParams: function () {
                            return params || {};
                        }
                    }
                });

                return modalDefer.promise;
            },

            /**
             * @ngdoc method
             * @name CreateNewItemDialogService#createNewOrganization
             *
             * @description
             * Display "Create New Organization" dialog window
             *
             * @param {Object} params - metadata for the dialog
             *
             * @returns {Promise} Returns promise that will be resolved to response object when user will close window
             *
             */
            createNewOrganization: function (params) {
                var scope = $rootScope.$new();
                var modalDefer = $q.defer();

                scope.onModalSave = function () {
                    modalInstance.close();
                    modalDefer.resolve({
                        action: 'SAVE'
                    });
                };

                scope.onModalCancel = function () {
                    modalInstance.close();
                    modalDefer.resolve({
                        action: 'CANCEL'
                    });
                };

                var modalInstance = $modal.open({
                    animation: true,
                    templateUrl: 'modules/organizations/views/components/organization-new-organization.client.view.html',
                    controller: 'Organizations.NewOrganizationController',
                    size: 'lg',
                    backdrop: 'static',
                    scope: scope,
                    resolve: {
                        translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                            $translatePartialLoader.addPart('common');
                            $translatePartialLoader.addPart('organizations');
                            return $translate.refresh();
                        }],

                        modalParams: function () {
                            return params || {};
                        }
                    }
                });

                return modalDefer.promise;
            },

            /**
             * @ngdoc method
             * @name CreateNewItemDialogService#createNewDocumentRepository
             *
             * @description
             * Display "Create New Document Repository" dialog window
             *
             * @param {Object} params - metadata for the dialog
             *
             * @returns {Promise} Returns promise that will be resolved to response object when user will close window
             *
             */
            createNewDocumentRepository: function (params) {
                var scope = $rootScope.$new();
                var modalDefer = $q.defer();

                scope.onModalSave = function () {
                    modalInstance.close();
                    modalDefer.resolve({
                        action: 'SAVE'
                    });
                };

                scope.onModalCancel = function () {
                    modalInstance.close();
                    modalDefer.resolve({
                        action: 'CANCEL'
                    });
                };

                var modalInstance = $modal.open({
                    animation: true,
                    templateUrl: 'modules/document-repository/views/components/document-repository-new-document-repository.client.view.html',
                    controller: 'DocumentRepository.NewDocumentRepositoryController',
                    size: 'lg',
                    backdrop: 'static',
                    scope: scope,
                    resolve: {
                        translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                            $translatePartialLoader.addPart('common');
                            $translatePartialLoader.addPart('document-repository');
                            return $translate.refresh();
                        }],

                        modalParams: function () {
                            return params || {};
                        }
                    }
                });

                return modalDefer.promise;
            }
        };
    }
]);
