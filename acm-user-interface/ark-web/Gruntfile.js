'use strict';

var nunjucks = require('nunjucks');
var fs = require('fs');

module.exports = function(grunt){
    var config = require('./config/env/all');

    grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),

        csslint: {
            options: {
                csslintrc: '.csslintrc'
            },
            all: {
                src: 'src/main/webapp/resources/modules/**/*.css'
            }
        },
        uglify: {
            production: {
                options: {
                    mangle: false
                },
                files: {
                    'src/main/webapp/resources/dist/application.min.js': 'src/main/webapp/resources/dist/application.js'
                }
            }
        },
        cssmin: {
            combine: {
                files: {
                    'src/main/webapp/resources/dist/application.min.css': '<%= applicationCSSFiles %>'
                }
            }
        },
        ngAnnotate: {
            production: {
                files: {
                    'src/main/webapp/resources/dist/application.js': '<%= applicationJavaScriptFiles %>'
                }
            }
        },

        sass: {
            dist: {
                options: {

                },
                files: [
                    {
                        'src/main/webapp/resources/assets/css/login.css': 'src/main/scss/login.scss'
                    },
                    {
                        'src/main/webapp/resources/assets/css/application.css': 'src/main/scss/application.scss'
                    },
                    {
                        expand: true,
                        src: config.assets.scss,
                        ext: '.css',
                        rename: function (base, src) {
                            return src.replace('/scss/', '/css/');
                        }
                    }
                ]
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
        var config = require('./config/config');
        grunt.config.set('applicationJavaScriptFiles', config.assets.js);
        grunt.config.set('applicationCSSFiles', config.assets.css);
    });


    // Render home page bases on hom.tpl.html template
    grunt.registerTask('renderHome', 'Render HTML page',  function() {
        var configUtil = require('./config/config');

        var title = config.homePage.title;

        var jsFiles = configUtil.getJavaScriptAssets();
        var cssFiles = configUtil.getCSSAssets();


        var html = nunjucks.render(config.homePage.template, {
            title: title,
            jsFiles: jsFiles,
            cssFiles: cssFiles
        });

        fs.writeFileSync(config.homePage.target, html);
    });


    // Lint task.
    grunt.registerTask('lint', ['jshint', 'csslint']);

    // Build task.
    //grunt.registerTask('build', ['renderHome', 'sass', 'lint', 'loadConfig', 'ngAnnotate', 'uglify', 'cssmin']);
    grunt.registerTask('build', ['sass', 'renderHome']);
};