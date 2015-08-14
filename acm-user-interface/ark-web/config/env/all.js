'use strict';
var appRoot = require('app-root-path');
var path = require('path');


module.exports = {

    appPath: '/arkcase/',

    config: {
        modulesConfigFolder: 'src/main/webapp/resources/modules_config/config/',
        modulesConfigFile: 'src/main/webapp/resources/modules_config/config/modules.json',
        modulesPermissionsFolder: 'src/main/webapp/resources/modules_config/permission/',
        modulesSchemasFolder: 'src/main/webapp/resources/modules_config/schemas/'
    },

    modules: {
        defaultModulesFolder: 'src/main/webapp/resources/modules/',
        customModulesFolder: 'src/main/webapp/resources/custom_modules/'
    },


    homePage: {
        title: "ArkCase Application",
        template: 'templates/home.tpl.html',
        target: 'src/main/webapp/home.html'
    },

    assets: {
        lib: {
            css: [
                'src/main/webapp/resources/lib/bootstrap/dist/css/bootstrap.css',
                'src/main/webapp/resources/lib/bootstrap/dist/css/bootstrap-theme.css',
                'src/main/webapp/resources/lib/fancytree/dist/skin-win8/ui.fancytree.css',
                'src/main/webapp/resources/lib/angular-ui-grid/ui-grid.css'
            ],
            js: [
                'src/main/webapp/resources/lib/jquery/dist/jquery.js',
                'src/main/webapp/resources/lib/jquery-ui/jquery-ui.js',
                'src/main/webapp/resources/lib/fancytree/dist/jquery.fancytree.js',
                'src/main/webapp/resources/lib/tv4/tv4.js',
                'src/main/webapp/resources/lib/objectpath/lib/ObjectPath.js',
                'src/main/webapp/resources/lib/lodash/lodash.js',
                'src/main/webapp/resources/lib/angular/angular.js',
                'src/main/webapp/resources/lib/angular-resource/angular-resource.js',
                'src/main/webapp/resources/lib/angular-cookies/angular-cookies.js',
                'src/main/webapp/resources/lib/angular-animate/angular-animate.js',
                'src/main/webapp/resources/lib/angular-touch/angular-touch.js',
                'src/main/webapp/resources/lib/angular-sanitize/angular-sanitize.js',
                'src/main/webapp/resources/lib/angular-ui-router/release/angular-ui-router.js',
                'src/main/webapp/resources/lib/angular-bootstrap/ui-bootstrap-tpls.js',
                'src/main/webapp/resources/lib/angular-ui-grid/ui-grid.js',
                'src/main/webapp/resources/lib/angular-schema-form/dist/schema-form.js',
                'src/main/webapp/resources/lib/angular-schema-form/dist/bootstrap-decorator.js',
                'src/main/webapp/resources/lib/angular-translate/angular-translate.js',
                'src/main/webapp/resources/lib/angular-translate-loader-partial/angular-translate-loader-partial.js'
            ]
        },
        css: [
            'src/main/webapp/resources/assets/css/application.css',
            'src/main/webapp/resources/modules/**/css/*.css',
            'src/main/webapp/resources/custom_modules/**/css/*.css'
        ],
        scss: [
            'src/main/webapp/resources/modules/**/scss/*.scss',
            'src/main/webapp/resources/custom_modules/**/scss/*.scss'
        ],
        js: [
            'src/main/webapp/resources/config.js',
            'src/main/webapp/resources/application.js',
            'src/main/webapp/resources/services/*.js',
            'src/main/webapp/resources/services/*/*.js',
            'src/main/webapp/resources/directives/*.js',
            'src/main/webapp/resources/directives/*/*.js'
        ],
        jsModules: [
            'src/main/webapp/resources/modules/*/*.js',
            'src/main/webapp/resources/modules/*/**/*.js'
        ],
        jsCustomModules: [
            'src/main/webapp/resources/custom_modules/*/*.js',
            'src/main/webapp/resources/custom_modules/*/**/*.js'
        ]
    }
}