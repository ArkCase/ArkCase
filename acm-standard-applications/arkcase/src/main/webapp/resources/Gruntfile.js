'use strict';
/*
Bellow is the link for creating patch file from two config.json files ,and the link for viewing  newly created .json
file from existing json and patch.
https://json8.github.io/patch/demos/diff/
https://json8.github.io/patch/demos/apply/
*/

var nunjucks = require('nunjucks'), glob = require('glob'), fs = require('fs-extra'), os = require('os'), path = require('path'), _ = require('lodash'), homedir = require('homedir');

module.exports = function(grunt) {
    // Path to .arkcase folder, used for sync-dev task
    var destPath = null;
    try {
        destPath = path.join(homedir(), '.arkcase');
    } catch (ex) {
        console.log('Can\'t get HOME DIR for dev tasks ... continue')
    }

    var config = require('./config/env/all');
    try {
        var customConfig = require('./config/env/customConfig');
        _.merge(config, customConfig);
    } catch (ex) {
        console.log('Custom config does not exist..continuing..');
    }
    // AFDP-6364 allow custom config to be loaded from ~/.arkcase/custom-js
    try {
        var externalCustomConfig = require('../custom-js/customConfig');
        console.log('Found external custom config, merging it.');
        _.merge(config, externalCustomConfig);
    } catch (ex) {
        console.log('External custom config does not exist..continuing..');
    }


    grunt.initConfig({
        pkg : grunt.file.readJSON('package.json'),

        csslint : {
            options : {
                csslintrc : '.csslintrc'
            },
            all : {
                src : 'modules/**/*.css'
            }
        },
        uglify : {
            production : {
                options : {
                    mangle : false,
                    sourceMap : true
                },
                files : {
                    'assets/dist/application.min.js' : 'assets/dist/application.js'
                }
            }
        },
        cssmin : {
            combine : {
                files : {
                    'assets/dist/application.min.css' : '<%= applicationCSSFiles %>'
                }
            }
        },
        ngAnnotate : {
            production : {
                files : {
                    'assets/dist/application.js' : '<%= applicationJavaScriptFiles %>'
                }
            }
        },
        concat : {
            dist : {
                src : '<%= vendorsJavaScriptFiles %>',
                dest : 'assets/dist/vendors.min.js'
            }
        },
        cacheBust: {
            taskName: {
                options: {
                    assets: [ 'assets/dist/**' ]
                },
                src: [ 'home.html' ]
            }
        },
        /////////////////////////////////////
        // Development tasks
        /////////////////////////////////////
        concurrent : {
            default1 : {
                tasks : [ 'watch:resources' ],
                options : {
                    logConcurrentOutput : true,
                    limit : 10
                }
            }
        },
        sync : {
            resources : {
                files : [ {
                    cwd : 'assets/',
                    src : [ '**' ],
                    dest : path.join(destPath, 'custom/assets/')
                }, {
                    cwd : 'directives/',
                    src : [ '**' ],
                    dest : path.join(destPath, 'custom/directives/')
                }, {
                    cwd : 'filters/',
                    src : [ '**' ],
                    dest : path.join(destPath, 'custom/filters/')
                }, {
                    cwd : 'modules/',
                    src : [ '**' ],
                    dest : path.join(destPath, 'custom/modules/')
                }, {
                    cwd : 'modules_config/',
                    src : [ '**' ],
                    dest : path.join(destPath, 'custom/modules_config/')
                }, {
                    cwd : 'services/',
                    src : [ '**' ],
                    dest : path.join(destPath, 'custom/services/')
                } ],
                verbose : true,
                updateAndDelete : false,
                compareUsing : 'mtime'
            }
        },
        clean: [ 'assets/dist' ],
        watch: {
            resources: {
                files: [ 'assets/**', 'directives/**', 'filters/**', 'modules/**', 'modules_config/**', 'service/**' ],
                tasks: [ 'sync:resources' ]
            }
        }
    });

    // Load NPM tasks
    require('load-grunt-tasks')(grunt);

    // Making grunt default to force in order not to break the project.
    grunt.option('force', true);

    // A Task for loading the configuration object
    grunt.task.registerTask('loadConfig', 'Task that loads the config into a grunt option.', function() {
        //var init = require('./config/init')();
        var configUtil = require('./config/config');
        grunt.config.set('applicationJavaScriptFiles', configUtil.assets.js.concat(configUtil.getModulesJavaScriptAssets()));
        grunt.config.set('vendorsJavaScriptFiles', configUtil.assets.lib.js);
        grunt.config.set('applicationCSSFiles', configUtil.getCSSAssets());
    });

    // Render home page bases on hom.tpl.html template
    grunt.registerTask('renderHome', 'Render HTML page', function() {
        var configUtil = require('./config/config');

        var title = config.homePage.title;
        var jsFiles = [];
        var cssFiles = [];

        if (process.env.NODE_ENV == 'production') {
            jsFiles = config.assets.distJs.concat(config.assets.lib.customJs);
            cssFiles = configUtil.getCSSAssets();
        } else {
            jsFiles = configUtil.getJavaScriptAssets().concat(configUtil.getModulesJavaScriptAssets());
            cssFiles = configUtil.getCSSAssets();
        }

        var html = nunjucks.render(config.homePage.template, {
            appPath : config.appPath,
            title : title,
            jsFiles : jsFiles,
            cssFiles : cssFiles
        });
        fs.writeFileSync(config.homePage.target, html);
    });

    /**
     * Synchronize modules configuration files
     */
    grunt.registerTask('updateModulesConfig', 'Update Modules config files', function() {
        var cfg = config.config;

        // Be sure that config folder is created
        fs.mkdirpSync(cfg.modulesConfigFolder);

        var modules = [];
        if (fs.existsSync(cfg.modulesConfigFile)) {
            modules = JSON.parse(fs.readFileSync(cfg.modulesConfigFile));
        }

        // Get config from modules and _config_modules directories
        var modulesConfigFolders = glob.sync(config.modules.defaultModulesFolder + '*/module_config/').concat(
            glob.sync(config.modules.customModulesDir + '*/module_config/'));

        var allModules = [];
        var newModules = [];
        var newModulesFolders = [];
        // Add missed modules
        _.forEach(modulesConfigFolders, function(folderName) {
            var fileName = path.join(folderName, 'config.json');
            // This works only for modules that have config.json file
            // other modules ingnored
            if (fs.existsSync(fileName)) {
                var moduleData = fs.readFileSync(fileName);
                var moduleObj = JSON.parse(moduleData);
                var moduleId = moduleObj.id;

                // Check if module is not present in modules.json. Add if required
                if (!_.find(modules, {
                    id : moduleId
                })) {
                    newModulesFolders.push({
                        id : moduleId,
                        folder : folderName
                    });
                    modules.push({
                        'id' : moduleId,
                        'title' : moduleObj.title,
                        'configurable' : moduleObj.configurable
                    });
                    newModules.push(moduleObj);
                }
                allModules.push(moduleObj);
                var ooPatch = require('./lib/acm-json8-patch/apply');
                var clone = require("./lib/json8/lib/clone");
                var patchFile = fileName.replace("config.json", "config-patch.json");
                var locationTmp = "modules/" + moduleId + "/module_config/config.json";
                var inputData = fs.readFileSync(fileName);
                var inputObj = JSON.parse(inputData);
                if (fs.existsSync(patchFile)) {
                    var patchData = fs.readFileSync(patchFile);
                    var patchObj = JSON.parse(patchData);

                    var doc = clone(inputObj);
                    doc = ooPatch(doc, patchObj).doc;

                    fs.writeFileSync(locationTmp, JSON.stringify(doc, null, 2));
                    fs.unlinkSync(patchFile);
                }
                else {
                    fs.writeFileSync(locationTmp, JSON.stringify(inputObj, null, 2));
                }

            }
        });

        var removedModules = [];
        // Remove missed modules info from config
        _.forEach(modules, function(module) {
            if (!_.find(allModules, {
                id : module.id
            })) {
                // remove modules
                removedModules.push(module);
            }
        });

        modules = _.reject(modules, function(item) {
            return _.find(removedModules, {
                id : item.id
            });
        });
        // Save modules config file
        fs.writeFileSync(cfg.modulesConfigFile, JSON.stringify(modules, null, 2));

        // Copy new modules configuration to the modules folder
        _.forEach(newModulesFolders, function(module) {
            // Create module folder if required
            var moduleFolder = path.join(cfg.modulesConfigFolder, 'modules', module.id);
            fs.mkdirsSync(moduleFolder);
            // Copy module config  folder
            fs.copySync(module.folder, moduleFolder);
        });

        // Remove excess modules files
        _.forEach(removedModules, function(module) {
            var moduleFolder = path.join(cfg.modulesConfigFolder, 'modules', module.id);
            fs.removeSync(moduleFolder);
        });
    });


    grunt.registerTask('copyToModulesConfigFolder', 'Update Modules config files', function () {
        var cfg = config.config;

        var modules = [];
        if (fs.existsSync(cfg.modulesConfigFile)) {
            modules = JSON.parse(fs.readFileSync(cfg.modulesConfigFile));
        }
        // Get config from modules and _config_modules diectrories
        var modulesConfigFolders = glob.sync(config.modules.defaultModulesFolder + '*/module_config/').concat(
            glob.sync(config.modules.customModulesDir + '*/module_config/'));

        var allModules = [];
        var newModules = [];
        var newModulesFolders = [];
        // Add missed modules
        _.forEach(modulesConfigFolders, function (folderName) {
            var fileName = path.join(folderName, 'config.json');

            // This works only for modules that have config.json file
            // other modules ingnored
            if (fs.existsSync(fileName)) {
                var moduleData = fs.readFileSync(fileName);
                var moduleObj = JSON.parse(moduleData);
                var moduleId = moduleObj.id;

                // Check if module is not present in modules.json. Add if required
                if (!_.find(modules, {
                    id: moduleId
                })) {
                    newModulesFolders.push({
                        id: moduleId,
                        folder: folderName
                    });
                    modules.push({
                        'id': moduleId,
                        'title': moduleObj.title,
                        'configurable': moduleObj.configurable
                    });
                    newModules.push(moduleObj);
                }
                allModules.push(moduleObj);
            }
        });

        var removedModules = [];
        // Remove missed modules info from config
        _.forEach(modules, function (module) {
            if (!_.find(allModules, {
                id: module.id
            })) {
                // remove modules
                removedModules.push(module);
            }
        });

        modules = _.reject(modules, function (item) {
            return _.find(removedModules, {
                id: item.id
            });
        });


        // Save modules config file
        fs.writeFileSync(cfg.modulesConfigFile, JSON.stringify(modules, null, 2));

        // Copy new modules configuration to the modules folder
        _.forEach(newModulesFolders, function (module) {
            // Create module folder if required
            var moduleFolder = path.join(cfg.modulesConfigFolder, 'modules', module.id);
            fs.mkdirsSync(moduleFolder);
            console.log('Added new module: ' + module.id);

            // Copy module config  folder
            fs.copySync(module.folder, moduleFolder);
        });

        // Remove excess modules files
        _.forEach(removedModules, function (module) {
            var moduleFolder = path.join(cfg.modulesConfigFolder, 'modules', module.id);
            fs.removeSync(moduleFolder);
            console.log('Removed module config folder: ' + moduleFolder);
        });
    });


    // Lint task.
    grunt.registerTask('lint', ['jshint', 'csslint']);

    // Build task.
    //grunt.registerTask('build', ['renderHome', 'sass', 'lint', 'loadConfig', 'ngAnnotate', 'uglify', 'cssmin']);
    grunt.registerTask('default', [ 'loadConfig', 'clean', 'ngAnnotate', 'uglify', 'concat', 'cssmin', 'renderHome', 'cacheBust', 'copyToModulesConfigFolder' ]);

    // Task syncs current folder with $user/.arkcase/custom/ folder
    grunt.registerTask('sync-dev', ['concurrent:default'])
};
