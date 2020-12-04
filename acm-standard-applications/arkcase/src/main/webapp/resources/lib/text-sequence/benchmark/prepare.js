"use strict";

function _asyncToGenerator(fn) { return function () { var gen = fn.apply(this, arguments); return new Promise(function (resolve, reject) { function step(key, arg) { try { var info = gen[key](arg); var value = info.value; } catch (error) { reject(error); return; } if (info.done) { resolve(value); } else { return Promise.resolve(value).then(function (value) { step("next", value); }, function (err) { step("throw", err); }); } } return step("next"); }); }; }

var _require = require("fs"),
    createWriteStream = _require.createWriteStream;

var _require2 = require(".."),
    SerializeStream = _require2.SerializeStream;

var request = require("request");
var JSONStream = require("JSONStream");

_asyncToGenerator( /*#__PURE__*/regeneratorRuntime.mark(function _callee() {
    var writeJSON, writeJST, stream, versions;
    return regeneratorRuntime.wrap(function _callee$(_context) {
        while (1) {
            switch (_context.prev = _context.next) {
                case 0:
                    writeJSON = createWriteStream(__dirname + "/data.json");
                    writeJST = createWriteStream(__dirname + "/data.jts");
                    stream = request("http://registry.npmjs.org/browserify");
                    versions = stream.pipe(JSONStream.parse("versions.*"));


                    versions.pipe(JSONStream.stringify()).pipe(writeJSON);
                    versions.pipe(new SerializeStream()).pipe(writeJST);

                case 6:
                case "end":
                    return _context.stop();
            }
        }
    }, _callee, undefined);
}))();