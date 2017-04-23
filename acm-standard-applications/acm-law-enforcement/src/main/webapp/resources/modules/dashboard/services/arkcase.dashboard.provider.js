'use strict';

/**
 *@ngdoc service
 *@name adf.provider:ArkCaseDashboard
 *
 *@description
 *
 *{@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/dashboard/services/dashboard.arkcase.provider.js modules/dashboard/services/dashboard.arkcase.provider.js}
 *
 *  The ArkCaseDashboard.provider is used to customize Angular Dashboard Framework provider with ArkCase's own templates.
 */

// angular.module('services').factory('Dashboard.ArkCaseProvider', ['$q'//, 'adf.provider', 'dashboardProvider'
//     , function ($q//, adfProvider, dashboardProvider

angular.module('adf.provider').provider('ArkCaseDashboard', function (dashboardProvider) {

    this.widget = function(name, widget){
        widget.titleTemplateUrl = widget.titleTemplateUrl || 'modules/dashboard/templates/widget-title.html';
        widget.editTemplateUrl = widget.editTemplateUrl || 'modules/dashboard/templates/widget-edit.html';
        widget.deleteTemplateUrl = widget.deleteTemplateUrl || 'modules/dashboard/templates/widget-delete.html';
        widget.fullscreenTemplateUrl = widget.titleTemplateUrl || 'modules/dashboard/templates/widget-fullscreen.html';
        dashboardProvider.widget(name, widget);
        return this;
    };
    this.structure = function(name, structure){
        dashboardProvider.structure(name, structure);
        return this;
    };
    this.setLocale = function(locale){
        dashboardProvider.setLocale(locale);
        return this;
    };
    this.addLocale = function(locale, translations){
        dashboardProvider.addLocale(locale, translations);
        return this;
    };

     // this.widgetsPath = function(path){
     // return this;
     // };
     // this.structure = function(name, structure){
     // return this;
     // };
     // this.messageTemplate = function(template){
     // return this;
     // };
     // this.loadingTemplate = function(template){
     // return this;
     // };
     // function getLocales() {
     // return locales;
     // }
     // function getActiveLocale() {
     // return activeLocale;
     // }
     // function translate(label) {
     // return translation ? translation : label;
     // }
     // this.customWidgetTemplatePath = function(templatePath) {
     // return this;
     // };

    this.$get = function(){
        console.log("=============================in ArkCaseDashboardProvider.$get=============================");
        return {
             // widgets: widgets,
             // widgetsPath: widgetsPath,
             // structures: structures,
             // messageTemplate: messageTemplate,
             // loadingTemplate: loadingTemplate,
             // setLocale: this.setLocale,
             // locales: getLocales,
             // activeLocale: getActiveLocale,
             // translate: translate,
             // customWidgetTemplatePath: customWidgetTemplatePath
        };
    };


});

