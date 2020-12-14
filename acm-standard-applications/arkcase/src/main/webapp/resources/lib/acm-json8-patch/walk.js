"use strict";

var validArrayToken = require("../pointer/lib/validArrayToken");
var OBJECT = "object";

/**
 * Walk a JSON document with a tokens array
 *
 * @param {Object|Array} doc     - JSON document
 * @param {Array}        tokens  - array of tokens
 * @return {Array}               - [token, target]
 */
module.exports = function walk(doc, tokens) {
    var length = tokens.length;
    var i = 0;
    var target = doc;
    var token;

    while (i < length - 1) {
        token = tokens[i++];

        if (Array.isArray(target)) validArrayToken(token, target.length);
        else if (typeof target !== OBJECT || Array.isArray(target) || target === null)
            throw new Error("Cannot be walked");

        if ((typeof token === "string") && (token.lastIndexOf("?@.", 0) === 0)) {
            var found = false;
            var propertyName = token.substring(3, token.indexOf("="));
            var propertyValue = token.substring(token.indexOf("'") + 1, token.length - 1);

            var arrayLength = target.length;
            for (var k = 0; k < arrayLength; k++) {
                if (target[k][propertyName] === propertyValue) {
                    token = k + '';
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new Error("Token: " + token + " not found!");
            }
        }

        target = target[token];
        if (target === undefined) {
            throw new Error("Token: " + token + " not found!");
        }
    }

    token = tokens[i];

    if ((typeof token === "string") && (token.lastIndexOf("?@.", 0) === 0)) {
        var found = false;
        var propertyName = token.substring(3, token.indexOf("="));
        var propertyValue = token.substring(token.indexOf("'") + 1, token.length - 1);

        var arrayLength = target.length;
        for (var k = 0; k < arrayLength; k++) {
            if (target[k][propertyName] === propertyValue) {
                token = k + '';
                found = true;
                break;
            }
        }
        if (!found) {
            throw new Error("Token: " + token + " not found!");
        }
    }

    if (Array.isArray(target)) validArrayToken(token, target.length);
    else if (typeof target !== OBJECT || target === null)
        throw new Error("Invalid target");

    return [token, target];
};
