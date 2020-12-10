'use strict';

angular.module('admin').controller('Admin.CMEmailTemplatePreviewModalController',
    [ '$rootScope', '$scope', '$modal', '$modalInstance', '$translate', 'params', function($rootScope, $scope, $modal, $modalInstance, $translate, params) {

        $scope.templateContent = params.templateContent.replace("${baseURL}", window.location.href.split('/home.html#!')[0]);

        $rootScope.$on('add-template-content', function () {
            document.getElementById("content").innerHTML=$scope.templateContent;
        });

        $scope.onClickClose = function() {
            $modalInstance.dismiss();
        };

    } ]);