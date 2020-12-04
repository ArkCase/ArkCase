"use strict";

var assert = require("assert");
var toJSONPatch = require("..");
var tests = require("./RFC.json");

var _require = require("json8-patch"),
    apply = _require.apply;

var _require2 = require("json8"),
    clone = _require2.clone;

describe("toJSONPatch", function () {
    it("converts path properties with null value as a remove operation", function () {
        var patch = toJSONPatch({ foo: null });
        assert.deepEqual(patch, [{ op: "remove", path: "/foo" }]);
    });

    it("converts nested path properties with null value as a remove operation", function () {
        var patch = toJSONPatch({ foo: { bar: null } });
        assert.deepEqual(patch, [{ op: "remove", path: "/foo/bar" }]);
    });

    it("converts path properties with non null value as an add operation", function () {
        var patch = toJSONPatch({ foo: "hello" });
        assert.deepEqual(patch, [{ op: "add", path: "/foo", value: "hello" }]);
    });

    it("converts nested path properties with non null value as an add operation", function () {
        var patch = toJSONPatch({ foo: { bar: "hello" } });
        assert.deepEqual(patch, [{ op: "add", path: "/foo/bar", value: "hello" }]);
    });

    it("returns a replace operation targeting the whole document if the patch is not an object", function () {
        [true, null, 42, [], "foo"].forEach(function (v) {
            var patch = toJSONPatch(v);
            assert.deepEqual(patch, [{ op: "replace", path: "", value: v }]);
        });
    });

    describe("RFC", function () {
        tests.forEach(function (test) {
            test = clone(test);

            if (test["json-patch"] === false) {
                it("throws an error for " + JSON.stringify(test.patch) + " apply on " + JSON.stringify(test.original), function () {
                    var patch = toJSONPatch(test.patch);
                    assert.throws(function () {
                        apply(test.original, patch);
                    });
                });
            } else {
                it("returns " + JSON.stringify(test.result), function () {
                    var patch = toJSONPatch(test.patch);
                    assert.deepEqual(apply(test.original, patch).doc, test.result);
                });
            }
        });
    });
});