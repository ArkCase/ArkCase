'use strict';
/**
 * @ngdoc service
 * @name ModalDialogService
 *
 * @description
 *
 * Provide method for displaying ui-bootstrap modal dialogs
 */
angular.module('services').service('ModalDialogService', [
    '$rootScope', '$translate', '$modal', '$q',
    function ($rootScope, $translate, $modal, $q) {

        return {
            /**
             * @ngdoc method
             * @name ModalDialogService#showModal
             *
             * @description
             * Display ui-bootstrap modal dialog
             *
             * @param {Object} modalMetaData
             * @param {String} modalMetaData.moduleName - the name of the module in which the controller for the modal is defined
             * @param {String} modalMetaData.templateUrl - the url for the template of the modal
             * @param {String} modalMetaData.controllerName - the name of the controller for the modal dialog
             * @param {String} modalMetaData.modalSize - the size of the modal dialog. Possible values: 'sm', 'md', 'lg'
             * @param {Object} modalMetaData.params - data passed to the modal dialog controller
             *
             * @returns {Promise} Returns promise that will be resolved to response object when user will close window
             *
             */
            showModal: function (modalMetaData) {
                var scope = $rootScope.$new();
                var modalDefer = $q.defer();

                scope.onModalClose = function (result) {
                    modalInstance.close();
                    modalDefer.resolve(result);
                };

                scope.onModalDismiss = function (result) {
                    modalInstance.dismiss();
                    modalDefer.resolve(result);
                };

                var modalInstance = $modal.open({
                    animation: true,
                    templateUrl: modalMetaData.templateUrl,
                    controller: modalMetaData.controllerName,
                    size: modalMetaData.modalSize || 'lg',
                    backdrop: 'static',
                    scope: scope,
                    resolve: {
                        translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                            $translatePartialLoader.addPart('common');
                            $translatePartialLoader.addPart(modalMetaData.moduleName);
                            return $translate.refresh();
                        }],

                        modalParams: function () {
                            return modalMetaData.params || {};
                        }
                    }
                });

                return modalDefer.promise;
            }
        };
    }
]);
