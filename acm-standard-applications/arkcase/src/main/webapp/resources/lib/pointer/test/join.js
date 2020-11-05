"use strict";

var assert = require("assert");
var join = require("../lib/join");

describe("join", function () {
    it("joins base path and tokens", function () {
        assert.strictEqual(join("/foo", ["bar", "foo"]), "/foo/bar/foo");
        assert.strictEqual(join("/foo", ["bar"]), "/foo/bar");
        assert.strictEqual(join(["foo"], ["bar"]), "/foo/bar");
        assert.strictEqual(join(["foo"], "bar"), "/foo/bar");
        assert.strictEqual(join("/foo", "bar"), "/foo/bar");
        assert.strictEqual(join("/foo", ["bar"]), "/foo/bar");
        assert.strictEqual(join("/foo", []), "/foo");
        assert.strictEqual(join("", ["foo"]), "/foo");

        assert.strictEqual(join("", ["0"]), "/0");
        assert.strictEqual(join("/0", ["foo"]), "/0/foo");

        assert.strictEqual(join([], []), "");
        assert.strictEqual(join("", []), "");
        assert.strictEqual(join("", ""), "/");
        assert.strictEqual(join([], ""), "/");
    });
});