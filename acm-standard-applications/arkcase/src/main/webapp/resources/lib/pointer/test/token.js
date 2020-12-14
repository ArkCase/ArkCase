"use strict";

var assert = require("assert");

var _require = require(".."),
    encode = _require.encode,
    decode = _require.decode;

describe("parse", function () {
    it("returns ['foo', 'bar']", function () {
        var r = decode("/foo/bar");
        assert.deepEqual(r, ["foo", "bar"]);
    });

    it('returns [""]', function () {
        var r = decode("/");
        assert.deepEqual(r, [""]);
    });

    it("returns []", function () {
        var r = decode("");
        assert.deepEqual(r, []);
    });
});

describe("serialize", function () {
    it("should return /foo/bar", function () {
        var s = encode(["foo", "bar"]);
        assert.deepEqual(s, "/foo/bar");
    });

    it('should return ""', function () {
        var s = encode([]);
        assert.deepEqual(s, "");
    });

    it("should return /", function () {
        var s = encode([""]);
        assert.deepEqual(s, "/");
    });
});