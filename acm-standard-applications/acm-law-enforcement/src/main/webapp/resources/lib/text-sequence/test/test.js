"use strict";

var test = require("ava");
var parse = require("../lib/parse");

var _require = require("fs"),
    createReadStream = _require.createReadStream;

var ParseStream = require("../lib/ParseStream");

test.cb("ParseStream", function (t) {
    var counter = 0;

    createReadStream(__dirname + "/ParseStream.log", { encoding: "utf8" }).pipe(new ParseStream()).on("data", function (data) {
        t.is(data.count, counter);
        counter++;
    }).on("end", function () {
        t.is(counter, 10);
        t.end();
    });
});

var _require2 = require("fs"),
    createWriteStream = _require2.createWriteStream;

var SerializeStream = require("../lib/SerializeStream");

test.cb("SerializeStream", function (t) {
    var counter = 0;

    var serializer = new SerializeStream();

    serializer.pipe(createWriteStream(__dirname + "/SerializeStream.log", {
        encoding: "utf8",
        flags: "w"
    }));

    serializer.on("data", function (data) {
        data = parse(data)[0];
        t.is(data.count, counter);
        counter++;
    }).on("end", function () {
        t.is(counter, 4);
        t.end();
    });

    serializer.write({ count: 0 });
    serializer.write({ count: 1 });
    serializer.write({ count: 2 });
    serializer.write({ count: 3 });
    serializer.end();
});