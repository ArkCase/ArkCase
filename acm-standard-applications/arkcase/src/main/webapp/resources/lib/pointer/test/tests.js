"use strict";

var assert = require("assert");
var find = require("../lib/find");

describe("trailing slash", function () {
    describe("/ with object", function () {
        it("returns the value with an empty key", function () {
            var doc = { "": "bar" };
            assert.equal(find(doc, "/"), "bar");
        });

        it("returns undefined if the target does not have an empty key", function () {
            var doc = { foo: "bar" };
            assert.equal(find(doc, "/"), undefined);
        });
    });

    describe("/ with array", function () {
        it("returns undefined", function () {
            var doc = [0, 1];
            assert.equal(find(doc, "/"), undefined);
        });
    });

    describe("/foo/ with object", function () {
        it("returns the value with an empty key", function () {
            var doc = { foo: { "": "bar" } };
            assert.equal(find(doc, "/foo/"), "bar");
        });

        it("returns undefined if the target does not have an empty key", function () {
            var doc = { foo: { foo: "bar" } };
            assert.equal(find(doc, "/foo/"), undefined);
        });
    });

    describe("/foo/ with array", function () {
        it("returns undefined", function () {
            var doc = { foo: [0, 1] };
            assert.equal(find(doc, "/foo/"), undefined);
        });
    });
});