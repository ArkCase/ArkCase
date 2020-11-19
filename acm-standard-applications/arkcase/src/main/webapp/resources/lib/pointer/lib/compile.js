"use strict";

var decode = require("./decode");

module.exports = function compile(pointer) {
    var tokens = Array.isArray(pointer) ? pointer : decode(pointer);

    var str = "return doc";
    var _iteratorNormalCompletion = true;
    var _didIteratorError = false;
    var _iteratorError = undefined;

    try {
        for (var _iterator = tokens[Symbol.iterator](), _step; !(_iteratorNormalCompletion = (_step = _iterator.next()).done); _iteratorNormalCompletion = true) {
            var token = _step.value;

            str += "['" + token.replace(/\\/, "\\\\").replace(/'/, "\\'") + "']";
        }
    } catch (err) {
        _didIteratorError = true;
        _iteratorError = err;
    } finally {
        try {
            if (!_iteratorNormalCompletion && _iterator.return) {
                _iterator.return();
            }
        } finally {
            if (_didIteratorError) {
                throw _iteratorError;
            }
        }
    }

    return Function("doc", str); // eslint-disable-line no-new-func
};