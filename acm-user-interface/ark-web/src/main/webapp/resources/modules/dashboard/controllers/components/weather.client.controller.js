'use strict';

angular.module("dashboard.weather", ["adf.provider"]).config(["dashboardProvider",
    function (dashboardProvider) {
        dashboardProvider.widget("weather", {
            title: 'Weather',
            description: 'Display the current temperature of a city',
            templateUrl: "modules/dashboard/views/components/weather.client.view.html",
            controller: 'Dashboard.WeatherController',
            reload: true,
            resolve: {
                location: function (config) {
                    return config.location ? config.location : void 0
                }
            },
            edit: {
                templateUrl: 'modules/dashboard/views/components/weather-edit.client.view.html'
            }
        })
    }
]).service("weatherService", ["$q", "$http",
    function ($q, $http) {
        return {
            get: function (location, appId) {

                var deferred = $q.defer();

                var url = "http://api.openweathermap.org/data/2.5/weather?units=metric&callback=JSON_CALLBACK&APPID=" + appId + "&q=" + location;

                var weather = {location: {}, temp: {}, clouds: null};

                return $http.jsonp(url).success(function (data) {
                    if (data) {
                        if (data.main) {
                            weather.temp.current = data.main.temp * 9 / 5 + 32;
                            weather.temp.min = data.main.temp_min * 9 / 5 + 32;
                            weather.temp.max = data.main.temp_max * 9 / 5 + 32;
                            weather.location.city = data.name;
                            weather.location.country = data.sys.country;
                        }
                        weather.clouds = data.clouds ? data.clouds.all : undefined;
                    }
                    data && 200 === data.cod ? deferred.resolve(weather) : deferred.reject()
                }).error(function () {
                    deferred.reject()
                }), deferred.promise

                return weather;
            }
        }
    }
]).filter('temp', function ($filter) {
    return function (input, precision) {
        if (!precision) {
            precision = 1;
        }
        var numberFilter = $filter('number');
        return numberFilter(input, precision) + '\u00B0F';
    };
}).controller("Dashboard.WeatherController", ["$scope", "location", "weatherService",
    function ($scope, location, weatherService) {

        $scope.$on('component-config', applyConfig);
        $scope.$emit('req-component-config', 'weather');

        function applyConfig(e, componentId, config) {
            if (componentId == 'weather') {
                $scope.config = config;

                var appid = config.APPID;

                weatherService.get(location, appid).then(function (weather) {
                    $scope.weather = weather;
                })
            }
        }
    }
]).directive('weatherIcon', function () {
    return {
        restrict: 'E',
        replace: true,
        scope: {
            cloudiness: '@'
        },
        controller: function ($scope) {
            $scope.imgurl = function () {
                var baseUrl = 'assets/img/dashboard/widgets/weather/';
                if ($scope.cloudiness < 20) {
                    return baseUrl + 'sunny.png';
                } else if ($scope.cloudiness < 90) {
                    return baseUrl + 'partly_cloudy.png';
                } else {
                    return baseUrl + 'cloudy.png';
                }
            };
        },
        template: '<div style="float:left"><img ng-src="{{ imgurl() }}"></div>'
    };
})

