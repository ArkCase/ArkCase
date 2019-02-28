"use strict";

var assert = require("assert");
var types = require("..");

describe("types", function () {
    it("has an OBJECT property set to 'object'", function () {
        assert.strictEqual(types.OBJECT, "object");
    });

    it("has a STRING property set to 'string'", function () {
        assert.strictEqual(types.STRING, "string");
    });

    it("has a NUMBER property set to 'number'", function () {
        assert.strictEqual(types.NUMBER, "number");
    });

    it("has a NULL property set to 'null'", function () {
        assert.strictEqual(types.NULL, "null");
    });

    it("has an ARRAY property set to 'array'", function () {
        assert.strictEqual(types.ARRAY, "array");
    });

    it("has an BOOLEAN property set to 'boolean'", function () {
        assert.strictEqual(types.BOOLEAN, "boolean");
    });

    describe("STRUCTURES", function () {
        it("contains ARRAY", function () {
            assert(types.STRUCTURES.indexOf(types.ARRAY) !== -1);
        });
        it("contains OBJECT", function () {
            assert(types.STRUCTURES.indexOf(types.OBJECT) !== -1);
        });
        it("has a length of 2", function () {
            assert.equal(types.STRUCTURES.length, 2);
        });
    });

    describe("PRIMITIVES", function () {
        it("contains STRING", function () {
            assert(types.PRIMITIVES.indexOf(types.STRING) !== -1);
        });
        it("contains NUMBER", function () {
            assert(types.PRIMITIVES.indexOf(types.NUMBER) !== -1);
        });
        it("contains NULL", function () {
            assert(types.PRIMITIVES.indexOf(types.NULL) !== -1);
        });
        it("contains BOOLEAN", function () {
            assert(types.PRIMITIVES.indexOf(types.BOOLEAN) !== -1);
        });
        it("has a length of 4", function () {
            assert.equal(types.PRIMITIVES.length, 4);
        });
    });
});