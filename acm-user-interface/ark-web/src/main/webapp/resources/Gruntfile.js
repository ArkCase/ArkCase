'use strict';

var nunjucks = require('nunjucks'),
    glob = require('glob'),
    fs = require('fs-extra'),
    path = require('path'),
    _ = require('lodash');


var mergeConfigFiles = require('./config/config').mergeConfigFiles;

module.exports = function (grunt) {
    var config = require('./config/env/all');
    try{
        var customConfig = require('./config/env/customConfig');
        _.merge(config, customConfig);
    }
    catch(ex){
        console.log('Custom config does not exist..continuing..');
    }

    grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),

        csslint: {
            options: {
                csslintrc: '.csslintrc'
            },
            all: {
                src: 'modules/**/*.css'
            }
        },
        uglify: {
            production: {
                options: {
                    mangle: false
                },
                files: {
                    'assets/dist/application.min.js': 'assets/dist/application.js'
                }
            }
        },
        cssmin: {
            combine: {
                files: {
                    'assets/dist/application.min.css': '<%= applicationCSSFiles %>'
                }
            }
        },
        ngAnnotate: {
            production: {
                files: {
                    'assets/dist/application.js': '<%= applicationJavaScriptFiles %>'
                }
            }
        }
    });


    // Load NPM tasks
    require('load-grunt-tasks')(grunt);

    // Making grunt default to force in order not to break the project.
    grunt.option('force', true);

    // A Task for loading the configuration object
    grunt.task.registerTask('loadConfig', 'Task that loads the config into a grunt option.', function () {
        //var init = require('./config/init')();
        var configUtil = require('./config/config');
        grunt.config.set('applicationJavaScriptFiles', configUtil.assets.js.concat(configUtil.getModulesJavaScriptAssets()));
        grunt.config.set('applicationCSSFiles', configUtil.getCSSAssets());
    });


    // Render home page bases on hom.tpl.html template
    grunt.registerTask('renderHome', 'Render HTML page', function () {
        var configUtil = require('./config/config');

        var title = config.homePage.title;
        var jsFiles = [];
        var cssFiles = [];

        if (process.env.NODE_ENV == 'production') {
            jsFiles = config.assets.lib.js.concat(config.assets.distJs, config.assets.lib.customJs);
            cssFiles = configUtil.getCSSAssets();
            //cssFiles = config.assets.lib.css.concat(config.assets.distCss);
        } else {
            jsFiles = configUtil.getJavaScriptAssets().concat(configUtil.getModulesJavaScriptAssets());
            cssFiles = configUtil.getCSSAssets();
        }


        var html = nunjucks.render(config.homePage.template, {
            appPath: config.appPath,
            title: title,
            jsFiles: jsFiles,
            cssFiles: cssFiles
        });

        fs.writeFileSync(config.homePage.target, html);
    });

    /**
     * Synchronize modules configuration files
     */
    grunt.registerTask('updateModulesConfig', 'Update Modules config files', function () {
        var cfg = config.config;

        // Be sure that config folder is created
        fs.mkdirpSync(cfg.modulesConfigFolder);

        // Get config from modules and _config_modules diectrories
        var defaultConfigFolders = glob.sync(config.modules.defaultModulesFolder + '*/module_config/');
        var customConfigFolders = glob.sync(config.modules.customModulesDir + '*/module_config/');


        //// Add missed modules
        //_.forEach(modulesConfigFolders, function (folderName) {
        //    var fileName = path.join(folderName, 'config.json');
        //
        //    // This works only for modules that have config.json file
        //    // other modules ingnored
        //    if (fs.existsSync(fileName)) {
        //        var moduleData = fs.readFileSync(fileName);
        //        var moduleObj = JSON.parse(moduleData);
        //        var moduleId = moduleObj.id;
        //
        //        // Check if module is not present in modules.json. Add if required
        //        if (!_.find(modules, {id: moduleId})) {
        //            newModulesFolders.push({
        //                id: moduleId,
        //                folder: folderName
        //            });
        //            modules.push({
        //                'id': moduleId,
        //                'title': moduleObj.title
        //            });
        //            newModules.push(moduleObj);
        //        }
        //        allModules.push(moduleObj);
        //    }
        //});


        //var modulesConfigFolders = glob.sync(config.modules.defaultModulesFolder + '*/module_config/').concat(glob.sync(config.modules.customModulesDir + '*/module_config/'));

        // Copy new modules configuration to the modules folder
        _.forEach(defaultConfigFolders, function (module) {
            // Create module folder if required
            var moduleFolder = path.join(cfg.modulesConfigFolder, 'modules', module.id);
            fs.mkdirsSync(moduleFolder);
            console.log('Added new module: ' + module.id);

            // Copy module config  folder
            fs.copySync(module.folder, moduleFolder);

            // Merge config.json with additional config files located in config folder if required
            var additionalConfigFolder = path.join(moduleFolder, 'config');
            /// Get all config files from config folder if it present
            if (fs.existsSync(additionalConfigFolder)) {
                var configFiles = glob.sync(additionalConfigFolder + '/*.json');
                configFiles = _.sortBy(configFiles);
                // Load config.json and additional config files
                var configFileName = path.join(moduleFolder, 'config.json');
                var configContent = JSON.parse(fs.readFileSync(configFileName));
                try {
                    _.forEach(configFiles, function(additionalConfigFileName){
                        var additionalConfigContent = JSON.parse(fs.readFileSync(additionalConfigFileName));
                        mergeConfigFiles(configContent, additionalConfigContent);
                        // _.merge(configContent, additionalConfigContent);
                    });
                } catch (ex){
                    console.error('Error during merging config of module ' + module.id + ':  ' + ex);
                }

                //processProperties(configContent);
                fs.writeFileSync(configFileName, JSON.stringify(configContent, null, 2));
            }
        });

        // Remove excess modules files
        _.forEach(removedModules, function (module) {
            var moduleFolder = path.join(cfg.modulesConfigFolder, 'modules', module.id);
            fs.removeSync(moduleFolder);
            console.log('Removed module config folder: ' + moduleFolder);
        });


        //var cfg = config.config;
        //
        //// Be sure that config folder is created
        //fs.mkdirpSync(cfg.modulesConfigFolder);
        //
        //
        //var modules = [];
        //if (fs.existsSync(cfg.modulesConfigFile)) {
        //    modules = JSON.parse(fs.readFileSync(cfg.modulesConfigFile));
        //}
        //
        //
        //// Get config from modules and _config_modules diectrories
        //var modulesConfigFolders = glob.sync(config.modules.defaultModulesFolder + '*/module_config/').concat(glob.sync(config.modules.customModulesDir + '*/module_config/'));
        //
        //var allModules = [];
        //var newModules = [];
        //var newModulesFolders = [];
        //
        //// Add missed modules
        //_.forEach(modulesConfigFolders, function (folderName) {
        //    var fileName = path.join(folderName, 'config.json');
        //
        //    // This works only for modules that have config.json file
        //    // other modules ingnored
        //    if (fs.existsSync(fileName)) {
        //        var moduleData = fs.readFileSync(fileName);
        //        var moduleObj = JSON.parse(moduleData);
        //        var moduleId = moduleObj.id;
        //
        //        // Check if module is not present in modules.json. Add if required
        //        if (!_.find(modules, {id: moduleId})) {
        //            newModulesFolders.push({
        //                id: moduleId,
        //                folder: folderName
        //            });
        //            modules.push({
        //                'id': moduleId,
        //                'title': moduleObj.title
        //            });
        //            newModules.push(moduleObj);
        //        }
        //        allModules.push(moduleObj);
        //    }
        //});
        //
        //var removedModules = [];
        //// Remove missed modules info from config
        //_.forEach(modules, function (module) {
        //    if (!_.find(allModules, {id: module.id})) {
        //        // remove modules
        //        removedModules.push(module);
        //    }
        //});
        //
        //
        //modules = _.reject(modules, function (item) {
        //    return _.find(removedModules, {id: item.id});
        //});
        //// Save modules config file
        //fs.writeFileSync(cfg.modulesConfigFile, JSON.stringify(modules, null, 2));
        //
        //// Copy new modules configuration to the modules folder
        //_.forEach(newModulesFolders, function (module) {
        //    // Create module folder if required
        //    var moduleFolder = path.join(cfg.modulesConfigFolder, 'modules', module.id);
        //    fs.mkdirsSync(moduleFolder);
        //    console.log('Added new module: ' + module.id);
        //
        //    // Copy module config  folder
        //    fs.copySync(module.folder, moduleFolder);
        //
        //    // Merge config.json with additional config files located in config folder if required
        //    var additionalConfigFolder = path.join(moduleFolder, 'config');
        //    /// Get all config files from config folder if it present
        //    if (fs.existsSync(additionalConfigFolder)) {
        //        var configFiles = glob.sync(additionalConfigFolder + '/*.json');
        //        configFiles = _.sortBy(configFiles);
        //        // Load config.json and additional config files
        //        var configFileName = path.join(moduleFolder, 'config.json');
        //        var configContent = JSON.parse(fs.readFileSync(configFileName));
        //        try {
        //            _.forEach(configFiles, function(additionalConfigFileName){
        //                var additionalConfigContent = JSON.parse(fs.readFileSync(additionalConfigFileName));
        //                mergeConfigFiles(configContent, additionalConfigContent);
        //                // _.merge(configContent, additionalConfigContent);
        //            });
        //        } catch (ex){
        //            console.error('Error during merging config of module ' + module.id + ':  ' + ex);
        //        }
        //
        //        //processProperties(configContent);
        //        fs.writeFileSync(configFileName, JSON.stringify(configContent, null, 2));
        //    }
        //});
        //
        //// Remove excess modules files
        //_.forEach(removedModules, function (module) {
        //    var moduleFolder = path.join(cfg.modulesConfigFolder, 'modules', module.id);
        //    fs.removeSync(moduleFolder);
        //    console.log('Removed module config folder: ' + moduleFolder);
        //});
    });


    // Lint task.
    grunt.registerTask('lint', ['jshint', 'csslint']);

    // Build task.
    //grunt.registerTask('build', ['renderHome', 'sass', 'lint', 'loadConfig', 'ngAnnotate', 'uglify', 'cssmin']);
    grunt.registerTask('default', ['loadConfig', 'ngAnnotate', 'uglify', 'cssmin', 'renderHome', 'updateModulesConfig']);
};