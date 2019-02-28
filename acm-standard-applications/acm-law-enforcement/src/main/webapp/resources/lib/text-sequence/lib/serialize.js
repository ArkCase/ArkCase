"use strict";

var Serializer = require("./Serializer");

module.exports = function serialize(array) {
    var serializer = new Serializer();
    var str = "";
    serializer.on("data", function (data) {
        return str += data;
    });
    for (var i = 0; i < array.length; i++) {
        serializer.write(array[i]);
    }
    return str;
};