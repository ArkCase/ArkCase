'use strict';

angular.module('dashboard.signature', [ 'adf.provider' ]).config(function(dashboardProvider) {
    dashboardProvider.widget('signature', {
        title : 'dashboard.widgets.signature.title',
        description : 'dashboard.widgets.signature.description',
        controller : 'Dashboard.SignatureController',
        reload : true,
        templateUrl : 'modules/dashboard/views/components/signature.client.view.html',
        commonName : 'signature'
    });
}).controller(
        'Dashboard.SignatureController',
        [
                '$scope',
                '$translate',
                '$stateParams',
                '$q',
                'UtilService',
                'Task.InfoService',
                'Authentication',
                'Dashboard.DashboardService',
                'Object.SignatureService',
                'ObjectService',
                'ConfigService',
                'Helper.ObjectBrowserService',
                'Helper.UiGridService',
                function($scope, $translate, $stateParams, $q, Util, TaskInfoService, Authentication, DashboardService,
                        ObjectSignatureService, ObjectService, ConfigService, HelperObjectBrowserService, HelperUiGridService) {

                    var promiseConfig;
                    var promiseInfo;
                    var modules = [ {
                        name : "TASK",
                        configName : "tasks",
                        getInfo : ObjectSignatureService.findSignatures,
                        objectType : ObjectService.ObjectTypes.TASK
                    }, {
                        name : "ADHOC",
                        configName : "tasks",
                        getInfo : ObjectSignatureService.findSignatures,
                        objectType : ObjectService.ObjectTypes.TASK
                    } ];

                    var module = _.find(modules, function(module) {
                        return module.name == $stateParams.type;
                    });

                    $scope.gridOptions = {
                        enableColumnResizing : true,
                        columnDefs : []
                    };

                    var gridHelper = new HelperUiGridService.Grid({
                        scope : $scope
                    });

                    var currentObjectId = HelperObjectBrowserService.getCurrentObjectId();
                    if (module && Util.goodPositive(currentObjectId, false)) {
                        promiseConfig = ConfigService.getModuleConfig(module.configName);
                        promiseInfo = module.getInfo(module.objectType, currentObjectId);

                        $q.all([ promiseConfig, promiseInfo ]).then(function(data) {
                            var config = _.find(data[0].components, {
                                id : "main"
                            });
                            var info = data[1];
                            var widgetInfo = _.find(config.widgets, function(widget) {
                                return widget.id === "signatures";
                            });
                            $scope.config = config;
                            gridHelper.setColumnDefs(widgetInfo);

                            var signatures = info;
                            gridHelper.setWidgetsGridData(signatures);
                        }, function(err) {

                        });
                    }
                } ]);