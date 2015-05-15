/**
 * Created by marjan.stefanoski on 10/1/2014.
 */

var dashboardConfigServices = angular.module('dashboardConfigServices', ['ngResource']);

dashboardConfigServices.factory('RolesByWidgets', ['$resource',
    function($resource){
        var url = App.getContextPath() + "/api/latest/plugin/dashboard/widgets/rolesByWidget/all";
        return $resource(url, {}, {
            query: {method:'GET', params:{}, isArray:true}
        });
    }]);

dashboardConfigServices.factory('AuthorizeRolesForWidget', ['$resource',
    function($resource){
        var url = App.getContextPath() + "/api/latest/plugin/dashboard/widgets/set";
        return $resource(url, {}, {
            save: {method:'POST', params:{}}
        });
    }]);