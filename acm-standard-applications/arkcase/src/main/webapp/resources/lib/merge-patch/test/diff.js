"use strict";

var assert = require("assert");
var apply = require("../lib/apply");
var diff = require("../lib/diff");
var tests = require("./diff.json");

var _require = require("json8"),
    clone = _require.clone;

describe("diff", function () {
    tests.forEach(function (test) {
        test = clone(test);
        it(test.description, function () {
            assert.deepEqual(diff(test.a, test.b), test.diff);
            assert.deepEqual(apply(test.a, test.diff), test.b);
        });
    });
});