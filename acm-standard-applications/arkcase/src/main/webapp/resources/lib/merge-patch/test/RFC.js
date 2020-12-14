"use strict";

var assert = require("assert");
var apply = require("../lib/apply");
var tests = require("./RFC.json");

var _require = require("json8"),
    clone = _require.clone;

describe("RFC", function () {
    tests.forEach(function (test) {
        test = clone(test);
        it("returns " + JSON.stringify(test.result), function () {
            assert.deepEqual(apply(test.original, test.patch), test.result);
        });
    });
});