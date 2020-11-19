"use strict";

var assert = require("assert");

var _require = require(".."),
    index = _require.index;

var primitives = {
    null: null,
    string: "hello",
    boolean: true,
    number: -42
};

var structures = {
    array: [],
    object: {},
    map: new Map(),
    set: new Set()
};

function forEach(obj, fn) {
    for (var i in obj) {
        fn(i, obj[i]);
    }
}

describe("index", function () {
    describe("primitives", function () {
        forEach(primitives, function (name, value) {
            it("indexes " + name + " primitive correctly", function () {
                var expect = { "": value };
                assert.deepEqual(index(value), expect);
            });
        });
    });

    describe("empty structures", function () {
        forEach(structures, function (name, value) {
            it("indexes empty " + name + " structure correctly", function () {
                var expect = { "": value };
                assert.deepEqual(index(value), expect);
            });
        });
    });
});