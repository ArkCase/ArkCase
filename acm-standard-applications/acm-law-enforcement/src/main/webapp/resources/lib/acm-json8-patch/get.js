"use strict";

var walk = require("../pointer/lib/walk");
var decode = require("../pointer/lib/decode");

/**
 * @typedef OperationResult
 * @type Object
 * @property {Any}   doc       - The patched document
 * @property {Array} previous  - The previous/replaced value if any
 */

/**
 * Get the value at the JSON Pointer location
 *
 * @param  {Object|Array} doc   - JSON document
 * @param  {String|Array} path  - JSON Pointer string or tokens path
 * @return {Any}                - value at the JSON Pointer location
 */
module.exports = function get(doc, path) {
  var tokens = decode(path);

  // returns the document
  if (tokens.length === 0) return doc;

  var r = walk(doc, tokens);
  var token = r[0];
  var parent = r[1];

  return parent[token];
};
