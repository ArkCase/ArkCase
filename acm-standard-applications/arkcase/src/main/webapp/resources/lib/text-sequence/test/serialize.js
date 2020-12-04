"use strict";

var test = require("ava");
var serialize = require("../lib/serialize");

test("serialize", function (t) {
    t.is(serialize(["hello"]), '\x1e"hello"\x0A');
    t.is(serialize([true]), "\x1etrue\x0A");
    t.is(serialize([false]), "\x1efalse\x0A");
    t.is(serialize([null]), "\x1enull\x0A");
    t.is(serialize([0]), "\x1e0\x0A");
    t.is(serialize([-1]), "\x1e-1\x0A");
    t.is(serialize([1]), "\x1e1\x0A");
    t.is(serialize([]), "");
});