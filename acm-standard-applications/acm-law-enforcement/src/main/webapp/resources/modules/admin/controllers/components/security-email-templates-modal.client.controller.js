angular.module('admin').controller('Admin.EmailTemplatesModalController', [ '$scope', '$modalInstance', 'params', 'Admin.EmailTemplatesService', function($scope, $modalInstance, params, emailTemplatesService) {

    $scope.templateFile = null;
    $scope.isEdit = params.isEdit;
    $scope.template = angular.copy(params.template);
    $scope.objectTypeList = params.objectTypeList;
    $scope.actionList = params.actionList;
    $scope.sourceList = params.sourceList;

    $scope.onClickCancel = function() {
        $modalInstance.dismiss('Cancel');
    };

    $scope.onClickOk = function() {
        $modalInstance.close({
            file: $scope.templateFile,
            template: $scope.template,
            isEdit: $scope.isEdit
        });
    };

    $scope.downloadFile = function(templateName) {
        emailTemplatesService.getEmailTemplate(templateName).then(function(response) {
            var content = response.data.content;
            var url = URL.createObjectURL(new Blob([ content ]));
            var link = document.createElement('a');
            link.href = url;
            link.download = templateName;
            link.target = '_blank';
            link.click();
        });
    };

    $scope.toggleObjectTypes = function(objectType) {
        var idx = $scope.template.objectTypes.indexOf(objectType);

        if (idx > -1) {
            $scope.template.objectTypes.splice(idx, 1);
        } else {
            $scope.template.objectTypes.push(objectType);
        }
    };

    $scope.toggleActions = function(action) {
        var idx = $scope.template.actions.indexOf(action);
        if (idx > -1) {
            $scope.template.actions.splice(idx, 1);
        } else {
            $scope.template.actions.push(action);
        }
    };
} ]);