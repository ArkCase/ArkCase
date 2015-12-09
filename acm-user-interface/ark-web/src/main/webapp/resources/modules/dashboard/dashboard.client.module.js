'use strict';

ApplicationConfiguration.registerModule('dashboard', [
    'adf',
    'adf.provider',
    'highcharts-ng',
    'dashboard.cases-by-queue',
    'dashboard.cases-by-status',
    'dashboard.my-cases',
    'dashboard.my-complaints',
    'dashboard.my-tasks',
    'dashboard.new-complaints',
    'dashboard.team-workload',
    'dashboard.weather',
    'dashboard.news'
]);