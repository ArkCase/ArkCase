"use strict";

var Parser = require("./Parser");

module.exports = function parse(str) {
    var parser = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : new Parser();

    var seqs = [];
    parser.on("data", function (data) {
        return seqs.push(data);
    });
    parser.write(str);
    parser.end();
    return seqs;
};