/**
 * HelloWorld is namespace component for HelloWorld plugin
 *
 * @author jwu
 */
var HelloWorld = HelloWorld || {
    initialize: function() {
//        HelloWorld.Callbacks.initialize();
//        HelloWorld.ObjectManager.initialize();
//        HelloWorld.ServerManager.initialize();
//        HelloWorld.TableManager.initialize();
//
//        HelloWorld.Callbacks.onPostInit();
        console.log("HelloWorld initialize");
    }

    ,Callbacks:{}
    ,ObjectManager: {}
    ,ServerManager: {}
    ,TableManager: {}
};

//$(document).ready(
//    function() {
//        HelloWorld.initialize();
//    }
//);
