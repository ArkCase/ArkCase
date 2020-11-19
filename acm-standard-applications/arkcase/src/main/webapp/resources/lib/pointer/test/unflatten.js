"use strict";

var assert = require("assert");

var _require = require(".."),
    flatten = _require.flatten,
    unflatten = _require.unflatten;

var _require2 = require("json8"),
    clone = _require2.clone;

var tests = [{
    foo: ["bar", "baz"],
    "": 0,
    "a/b": 1,
    "c%d": 2,
    "e^f": 3,
    "g|h": 4,
    "i\\j": 5,
    'k"l': 6,
    " ": 7,
    "m~n": 8
}, "foo", true, null, ["foo", "bar"], new Set(["foo", "bar"])];

var map = new Map();
map.set("object", { foo: "bar" });
map.set("array", ["foo", "bar"]);
tests.push(map);

describe("unflatten", function () {
    it("returns an equal value of the original", function () {
        tests.forEach(function (test) {
            var json = clone(test);
            assert.deepEqual(unflatten(flatten(json)), test);
        });
    });
});