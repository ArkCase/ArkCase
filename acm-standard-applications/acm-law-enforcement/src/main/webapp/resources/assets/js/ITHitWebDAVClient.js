
// Declare ITHit core.
if ('undefined' === typeof ITHit) {
	
	(function () {
		
		this.ITHit = {
			
			_oComponents: {},
			_oNamespace: {},
			
			// Define new modules.
			Define: function (sComponentName) {
				this._oComponents[sComponentName] = true;
			},
			
			// Check whether module has already been defined.
			Defined: function (sComponentName) {
				return !!this._oComponents[sComponentName];
			},
			
			// Add new modules.
			Add: function (sLable, mValue) {
				
				var aLable  = sLable.split('.');
				var oObj    = this;
				var iLength = aLable.length;
				
				for (var i = 0; i < iLength; i++) {
					
					if ('undefined' === typeof oObj[aLable[i]]) {
						if (i < (iLength - 1)) {
							oObj[aLable[i]] = {};
						} else {
							oObj[aLable[i]] = mValue;
						}
					} else {
						if (!(oObj[aLable[i]] instanceof Object)) {
							return;
						}
					}
					
					oObj = oObj[aLable[i]];
				}
			},
			
			// Temporary object.
			Temp: {}
		};
	})();
	
}

// Declare ITHit Config.
ITHit.Config = {
	Global: window,
	ShowOriginalException: true,
	// Whether UNIX timestamp needs to be added to URI for XMLHttpRequest for prevanting caching.
	PreventCaching: false
};

/*
* Method for getting namespace.
* @class
*
* @param {String/Array mNamespace} Namespace for getting/creating
* @param {Boolean} bCreate If whether namespace need to be created
* @param {Object} oContext Context from witch will be created namespace
* @returns Namespace object if exists or "undefined" otherwise
*/
ITHit.Add('GetNamespace', 
	function(mNamespace, bCreate, oContext) {
		
		var utilsNs = ITHit.Utils;
		
		// Namespace type checking
		if ( !utilsNs.IsString(mNamespace)
			&& !utilsNs.IsArray(mNamespace)
		) {
			throw new ITHit.Exception('ITHit.GetNamespace() expected string as first parameter of method.');
		}
			
		var aNamespace = utilsNs.IsArray(mNamespace) ? mNamespace : mNamespace.split('.');
		
		// Context
		var oObj = oContext || ITHit.Config.Global;
		
		// Creating or selecting namespace
		for (var i = 0, sPart = ''; oObj && (sPart = aNamespace[i]); i++) {
			
			if (sPart in oObj) {
				oObj = oObj[sPart];
			} else {
				if (bCreate) {
					oObj[sPart] = {};
					oObj = oObj[sPart];
				} else {
					oObj = undefined;
				}
			}
		}
		
		// Return selected namespace
		return oObj;
	}
);

/*
*
* @method ITHit.Namespace
* 
* @param {String} sNamespace 
* @param {Object} oOntext 
* @returns Namespace object if exists or "undefined" otherwise
*/
ITHit.Add('Namespace',
	function (sNamespace, oContext) {
		return ITHit.GetNamespace(sNamespace, false, oContext);
	}
);

/*
* 
* @method ITHit.Declare
* 
* @param {String} sNamespace 
* @param {Object} oOntext 
* @returns Namespace object if exists or "undefined" otherwise
*/
ITHit.Add('Declare',
	function (sNamespace, oContext) {
		return ITHit.GetNamespace(sNamespace, true, oContext);
	}
);

ITHit.Add('DetectOS',
	function () {
	    var _plat = navigator.platform,
		    detectOS = {
		        Windows: (-1 != _plat.indexOf('Win')),
		        MacOS: (-1 != _plat.indexOf('Mac')),
		        Linux: (-1 != _plat.indexOf('Linux')),
		        UNIX: (-1 != _plat.indexOf('X11')),
		        OS: null
		    };

	    if (detectOS.Windows) {
	        detectOS.OS = 'Windows';
	    } else if (detectOS.Linux) {
	        detectOS.OS = 'Linux';
	    } else if (detectOS.MacOS) {
	        detectOS.OS = 'MacOS';
	    } else if (detectOS.UNIX) {
	        detectOS.OS = 'UNIX';
	    }

	    return detectOS;
	}()
);

ITHit.Add('DetectBrowser',
	function () {
	    var _nav = navigator.userAgent,
			detectBrowser = {
			    IE: false,
			    FF: false,
			    Chrome: false,
			    Safari: false,
			    Opera: false,
			    Browser: null,
			    Mac: false
			},
			browsers = {
			    /*IE 10 and earlier*/
			    IE: {
			        Search: 'MSIE',
			        Browser: 'IE'
			    },
			    IE11: {
			        Search: 'Trident/7',
			        Version: 'rv',
			        Browser: 'IE'
			    },
				Edge: {
					Search: 'Edge',
					Browser: 'Edge'
				},
			    FF: {
			        Search: 'Firefox',
			        Browser: 'FF'
			    },
			    Chrome: {
			        Search: 'Chrome',
			        Browser: 'Chrome'
			    },
			    Safari: {
			        Search: 'Safari',
			        Version: 'Version',
			        Browser: 'Safari',
			        Mac: 'Macintosh',
			        iPad: 'iPad',
					iPhone: 'iPhone'
			    },
			    Opera: {
			        Search: 'Opera',
			        Browser: 'Opera'
			    }
			};

	    for (var check in browsers) {
	        var pos = _nav.indexOf(browsers[check].Search);
	        if (-1 != pos) {
	            detectBrowser.Browser = browsers[check].Browser;
	            detectBrowser.Mac = navigator.platform.indexOf('Mac') == 0; //(browsers[check].Mac && _nav.indexOf(browsers[check].Mac) != -1);
	            detectBrowser.iPad = (browsers[check].iPad && _nav.indexOf(browsers[check].iPad) != -1);
				detectBrowser.iPhone = (browsers[check].iPhone && _nav.indexOf(browsers[check].iPhone) != -1);

	            var versionSearch = browsers[check].Version || browsers[check].Search,
					index = _nav.indexOf(versionSearch);

	            if (-1 == index) {
	                detectBrowser[browsers[check].Browser] = true;
	                break;
	            }

	            detectBrowser[browsers[check].Browser] = parseFloat(_nav.substring(index + versionSearch.length + 1));

	            break;
	        }
	    }

	    return detectBrowser;
	}()
);

ITHit.Add('DetectDevice',
	function() {
		var sUserAgent = navigator.userAgent;
		var resultDevices = {};
		var devices = {
			Android: {
				Search: 'Android'
			},
			BlackBerry: {
				Search: 'BlackBerry'
			},
			iOS: {
				Search: 'iPhone|iPad|iPod'
			},
			Opera: {
				Search: 'Opera Mini'
			},
			Windows: {
				Search: 'IEMobile'
			},
			Mobile: {
			}
		};

		for (var name in devices) {
			var oParams = devices[name];
			if (!oParams.Search) {
				continue;
			}

			// Detect device
			var oRegExp = new RegExp(oParams.Search, 'i');
			resultDevices[name] = oRegExp.test(sUserAgent);

			// Set any
			if (!resultDevices.Mobile && resultDevices[name]) {
				resultDevices.Mobile = true;
			}
		}

		return resultDevices;
	}()
);

ITHit.Add('HttpRequest',
	function(sHref, sMethod, oHeaders, sBody, sUser, sPass) {
		
		if (!ITHit.Utils.IsString(sHref)) {
			throw new ITHit.Exception('Expexted string href in ITHit.HttpRequest. Passed: "'+ sHref +'"', 'sHref');
		}
		
		if (!ITHit.Utils.IsObjectStrict(oHeaders) && !ITHit.Utils.IsNull(oHeaders) && !ITHit.Utils.IsUndefined(oHeaders)) {
			throw new ITHit.Exception('Expexted headers list as object in ITHit.HttpRequest.', 'oHeaders');
		}
		
		this.Href     = sHref;
		this.Method   = sMethod;
		this.Headers  = oHeaders;
		this.Body     = sBody;
		this.User     = sUser || null;
		this.Password = sPass || null;
	}
);

ITHit.Add('HttpResponse',
	function() {
		
		var HttpResponse = function(sHref, iStatus, sStatusDescription, oHeaders) {
			
			if (!ITHit.Utils.IsString(sHref)) {
				throw new ITHit.Exception('Expexted string href in ITHit.HttpResponse. Passed: "'+ sHref +'"', 'sHref');
			}
			
			if (!ITHit.Utils.IsInteger(iStatus)) {
				throw new ITHit.Exception('Expexted integer status in ITHit.HttpResponse.', 'iStatus');
			}
			
			if (!ITHit.Utils.IsString(sStatusDescription)) {
				throw new ITHit.Exception('Expected string status description in ITHit.HttpResponse.', 'sStatusDescription');
			}
			
			if (oHeaders && !ITHit.Utils.IsObjectStrict(oHeaders)) {
				throw new ITHit.Exception('Expected object headers in ITHit.HttpResponse.', 'oHeaders');
			}
			else if (!oHeaders) {
				oHeaders = {};
			}
			
			this.Href              = sHref;
			this.Status            = iStatus;
			this.StatusDescription = sStatusDescription;
			this.Headers           = oHeaders;
			this.BodyXml           = null;
			this.BodyText          = '';
		}
		
		HttpResponse.prototype._SetBody = function(oBodyXml, sBodyText) {
			this.BodyXml  = oBodyXml  || null;
			this.BodyText = sBodyText || '';
		}
		
		HttpResponse.prototype.SetBodyText = function(sBody) {
			this.BodyXml  = null;
			this.BodyText = sBody;
		}
		
		HttpResponse.prototype.SetBodyXml = function(oBody) {
			this.BodyXml  = oBody;
			this.BodyText = '';
		}
		
		HttpResponse.prototype.ParseXml = function(sXml) {
			
			if (!ITHit.Utils.IsString(sXml)) {
				throw new ITHit.Exception('Expected XML string in ITHit.HttpResponse.ParseXml', 'sXml');
			}
			
			var oXml = new ITHit.XMLDoc();
			oXml.load(sXml);
			
			this.BodyXml  = oXml._get();
			this.BodyText = sXml;
		}
		
		HttpResponse.prototype.GetResponseHeader = function(sHeader, bToLowerCase) {
			
			if (!bToLowerCase) {
				return this.Headers[sHeader];
			}
			else {
				var sHeader = String(sHeader).toLowerCase();
				
				for (var sHeaderName in this.Headers) {
					if (sHeader === String(sHeaderName).toLowerCase()) {
						return this.Headers[sHeaderName];
					}
				}
				
				return undefined;
			}
		}
		
		return HttpResponse;
	}()
);

ITHit.Add('XMLRequest',
	(function() {

	    var XMLObjectVersion;

	    /*
		* Get XMLHttpRequest method.
		* @method XMLRequest
		*
		* @throws {Object} Exception Whether object cannot be created.
		*/
	    var GetXMLObject = function () {
	        // Get XMLHttpRequest object in IE.
	        if (ITHit.DetectBrowser.IE && ITHit.DetectBrowser.IE < 10 && window.ActiveXObject) {

	            if (XMLObjectVersion) {
	                return new ActiveXObject(XMLObjectVersion)
	            }
	            else {
	                var aVers = ["MSXML2.XmlHttp.6.0", "MSXML2.XmlHttp.3.0"];
	                for (var i = 0; i < aVers.length; i++) {
	                    try {
	                        var oXmlObj = new ActiveXObject(aVers[i]);
	                        XMLObjectVersion = aVers[i];
	                        return oXmlObj;
	                    }
	                    catch (e) {
	                    }
	                }
	            }

	            // Get XMLHttpRequest object in W3C compatible browsers.
	        } else if ('undefined' != typeof XMLHttpRequest) {
	            return new XMLHttpRequest();
	        }

	        // Whether XMLHttpRequest object has not been created.
	        throw new ITHit.Exception('XMLHttpRequest (AJAX) not supported');
	    }

	    var parseResponseHeaders = function (sHeaders) {

	        var oHeaders = {};

	        if (!sHeaders) {
	            return oHeaders;
	        }

	        var aHeaders = sHeaders.split('\n');
	        for (var i = 0; i < aHeaders.length; i++) {

	            if (!ITHit.Trim(aHeaders[i])) {
	                continue;
	            }

	            var aParts = aHeaders[i].split(':');
	            var sHeaderName = aParts.shift();

	            oHeaders[sHeaderName] = ITHit.Trim(aParts.join(':'));
	        }

	        return oHeaders;
	    }

        var XMLRequest = function(oHttpRequest, bAsync) {

            // Set true, if need async request and register callback through XMLRequest.OnData() method
            this.bAsync = bAsync === true;

            // Listeners for async requests
			this.OnData = null;
			this.OnError = null;
			this.OnProgress = null;

            this.oHttpRequest = oHttpRequest;
            this.oError = null;

            // Check whether url is empty.
            if (!oHttpRequest.Href) {
                throw new ITHit.Exception('Server url had not been set.');
            }

            if (ITHit.Logger && ITHit.LogLevel) {
                ITHit.Logger.WriteMessage('[' + oHttpRequest.Href + ']');
            }

            this.oRequest = GetXMLObject();

            var sHref   = String(oHttpRequest.Href);
            var sMethod = oHttpRequest.Method || 'GET';

            try {
                this.oRequest.open(sMethod, ITHit.DecodeHost(sHref), this.bAsync, oHttpRequest.User || null, oHttpRequest.Password || null);

                if (ITHit.DetectBrowser.IE && ITHit.DetectBrowser.IE >= 10) {
                    try {
                        this.oRequest.responseType = 'msxml-document';
                    } catch (e) {
                    }
                }
                // Initialization failed.
            } catch(e) {

                // Get host of requested page.
                var aDestHost = sHref.match(/(?:\/\/)[^\/]+/);

                // Check whether host is found.
                if (aDestHost) {
                    var sDestHost = aDestHost[0].substr(2);

                    // Check whether root host and destination host is not the same.
                    if (XMLRequest.Host != sDestHost) {
                        // Cross-domain request, throw an exception.
                        throw new ITHit.Exception(ITHit.Phrases.CrossDomainRequestAttempt.Paste(window.location, sHref, String(sMethod)), e);

                        // Another reason.
                    } else {
                        throw e;
                    }
                }
            }

            // Set headers.
            for (var sHeader in oHttpRequest.Headers) {
                this.oRequest.setRequestHeader(sHeader, oHttpRequest.Headers[sHeader]);
            }

			if (this.bAsync) {
				try {
					this.oRequest.withCredentials = true;
				} catch(e) {}
			}

            if (this.bAsync) {
                var self = this;
                this.oRequest.onreadystatechange = function() {
                    if (self.oRequest.readyState != 4) {
                        return;
                    }

                    var oResponse = self.GetResponse();
					if (typeof self.OnData === 'function') {
						self.OnData.call(self, oResponse);
					}
                };
				if ('onprogress' in this.oRequest) {
					this.oRequest.onprogress = function(oEvent) {
						if (typeof self.OnProgress === 'function') {
							self.OnProgress.call(self, oEvent);
						}
					};
				}
            }
        }

        XMLRequest.prototype.Send = function() {
            // Get body as string.
            var sBody = this.oHttpRequest.Body;
            sBody = sBody || (ITHit.Utils.IsUndefined(sBody) || ITHit.Utils.IsNull(sBody) || ITHit.Utils.IsBoolean(sBody) ? '' : sBody);
            sBody = String(sBody);

            if (sBody === ''){
                sBody = null;
            }

            // Try to send request.
            try {
                this.oRequest.send(sBody);
            } catch (e) {
                this.oError = e;

				if (typeof this.OnError === 'function') {
					this.OnError.call(this, e);
				}
            }
        }

		XMLRequest.prototype.Abort = function() {
			if (this.oRequest) {
				try {
					this.oRequest.abort();
				} catch (e) {
					this.oError = e;

					if (typeof this.OnError === 'function') {
						this.OnError.call(this, e);
					}
				}
			}
		}

        XMLRequest.prototype.GetResponse = function() {
            var oHttpRequest = this.oHttpRequest;
            var oRequest = this.oRequest;
            var sHref   = String(oHttpRequest.Href);

            if (this.bAsync && oRequest.readyState != 4) {
                throw new ITHit.Exception('Request sended as asynchronous, please register callback through XMLRequest.OnData() method for get responce object.');
            }

            // Throw an exception whether module is not loaded. But ignore error if we are uploading JS file with PROPFIND method.
            if ((404 == oRequest.status) && (-1 != sHref.indexOf('.js') && (oHttpRequest.Method !== "PROPFIND"))) {
                ITHit.debug.loadTrace.failed(ITHit.debug.loadTrace.FAILED_LOAD);
                throw new ITHit.Exception('Failed to load script ("' + sHref + '"). Request returned status: '+ oRequest.status + (oRequest.statusText ? ' ('+ oRequest.statusText +')' :'') +'.', this.oError || undefined);
            }

            // Fix request status whether it is necessary.
            var oStatus   = this.FixResponseStatus(oRequest.status, oRequest.statusText);
            var oResponse = new ITHit.HttpResponse(sHref, oStatus.Status, oStatus.StatusDescription, parseResponseHeaders(oRequest.getAllResponseHeaders()));
            oResponse._SetBody(oRequest.responseXML, oRequest.responseText);

            return oResponse;
        }

        /*
         * Fix response status. Whether it is necessary and can be done.
         * @function {Object} ITHit.XMLRequest.fixResponseStatus
         *
         * @param {Integer} iStatus Response status.
         * @param {String} sStatusText Status text.
         */
        XMLRequest.prototype.FixResponseStatus = function(iStatus, sStatusText) {

            var oStatus = {
                Status: iStatus,
                StatusDescription: sStatusText
            };

            // Fix for IE7 browser for PUT method 204 status code.
            if (1223 == iStatus) {
                oStatus.Status            = 204;
                oStatus.StatusDescription = 'No Content';
            }

            return oStatus;
        }

        XMLRequest.Host = window.location.host;
		

		
		return XMLRequest;
	})()
);

ITHit.Add('Utils',
	{
		// Check variable type.
		
		IsString: function(mValue) {
			return ( ('string' == typeof mValue) || (mValue instanceof String) );
		},
		
		IsNumber: function(mValue) {
			return ('number' == typeof mValue);
		},
		
		IsBoolean: function(mValue) {
			return ( ('boolean' == typeof mValue) || (mValue instanceof Boolean) );
		},
		
		IsInteger: function(mValue) {
			return this.IsNumber(mValue) && (-1 == String(mValue).indexOf('.'));
		},
		
		IsArray: function(mValue) {
			return (mValue instanceof Array || ('array' == typeof mValue));
		},
		
		IsFunction: function(mValue) {
			return (mValue instanceof Function);
		},
		
		IsObject: function(mValue) {
			return ('object' == typeof mValue);
		},
		
		IsDate: function(mValue) {
			return (mValue instanceof Date)
		},
		
		IsRegExp: function(mValue) {
			return (mValue instanceof RegExp);
		},
		
		IsObjectStrict: function(mValue) {
			return this.IsObject(mValue) && !this.IsArray(mValue) && !this.IsString(mValue) && !this.IsNull(mValue) && !this.IsNumber(mValue) && !this.IsDate(mValue) && !this.IsRegExp(mValue) && !this.IsBoolean(mValue) && !this.IsFunction(mValue) && !this.IsNull(mValue);
		},
		
		IsUndefined: function(mValue) {
			return (undefined === mValue);
		},
		
		IsNull: function(mValue) {
			return (null === mValue);
		},
		
		IsDOMObject: function(mValue) {
			return mValue && this.IsObject(mValue) && !this.IsUndefined(mValue.nodeType);
		},

		HtmlEscape: function(text) {
			return String(text)
				.replace(/&/g, '&amp;')
				.replace(/"/g, '&quot;')
				.replace(/'/g, '&#39;')
				.replace(/</g, '&lt;')
				.replace(/>/g, '&gt;');
		},

		IndexOf: function(array, item, isSorted) {
			var i = 0, length = array && array.length;
			if (typeof isSorted == 'number') {
				i = isSorted < 0 ? Math.max(0, length + isSorted) : isSorted;
			}
			for (; i < length; i++) if (array[i] === item) return i;
			return -1;
		},
		
		/*
		 * Create DOM Element.
		 * @function CreateDOMElement
		 * 
		 * @paramset Syntax 1
		 * @param {string} elem  Node name.
		 * @param {String} props Node propertyes.
		 * @paramset Syntax 2
		 * @param {Object} elem Node object. Node name must be specified in nodeName property.
		 */
		CreateDOMElement: function(elem, props) {
	
			var utilsNs = ITHit.Utils;
			
			if (utilsNs.IsObject(elem)) {
				if (!elem.nodeName) {
					throw new ITHit.Exception('nodeName property does not specified.');
				}
				props = elem;
				elem = elem.nodeName;
				delete props.nodeName;
			}
			
			// Create new element.
			var newElem = document.createElement(elem);
			
			// Assign properties.
			if (props && utilsNs.IsObject(props)) {
				for (var propName in props) {
					if (!props.hasOwnProperty(propName)) 
						continue;
					
					switch (propName) {
					
						// Replace property class with className.
						case 'class':
							if (props[propName]) {
								newElem.className = props[propName];
							}
							break;
							
						// Style
						case 'style':
							var stylesList = props[propName];
							for (var style in stylesList) {
								if (!stylesList.hasOwnProperty(style)) 
									continue;
								
								newElem.style[style] = stylesList[style];
							}
							break;
							
						// Append child nodes.
						case 'childNodes':
							for (var i = 0, l = props[propName].length; i < l; i++) {
								var child = props[propName][i];
								
								// Create text node and continue.
								if (utilsNs.IsString(child) || utilsNs.IsNumber(child) || utilsNs.IsBoolean(child)) {
									child = document.createTextNode(child);
									
								// Continue loop whether value is not set.
								}
								else 
									if (!child) {
										continue;
									}
								
								if (!utilsNs.IsDOMObject(child)) {
									child = ITHit.Utils.CreateDOMElement(child);
								}
								
								// Add child node.
								newElem.appendChild(child);
							}
							break;
							
						default:
							newElem[propName] = props[propName];
					}
				}	
			}
			
			return newElem;
		},
		
		GetComputedStyle: function(node) {
			
			// First call binding.
			ITHit.Utils.GetComputedStyle = ITHit.Components.dojo.getComputedStyle;
			
			return ITHit.Utils.GetComputedStyle(node);
		},
		
		MakeScopeClosure: function(scope, method, params) {
			if (this.IsUndefined(params)) {
				return this._GetClosureFunction(scope, method);
			} else {
				if (!this.IsArray(params)) {
					params = [params];
				}
				return this._GetClosureParamsFunction(scope, method, params);
			} 
		},
		
		_GetClosureParamsFunction: function(scope, method, params) {
			return function() {
					
					var args = [];
					for (var i = 0, l = params.length; i < l; i++) {
						args.push(params[i]);
					}
					
					if (arguments.length) {
						for (var i = 0, l = arguments.length; i < l; i++) {
							args.push(arguments[i]);
						}
					}
					
					scope[method].apply(scope, args);
				};
		},
		
		_GetClosureFunction: function(scope, method) {
			return function() {
					return scope[method].apply(scope, arguments);
				};
		}
	}
);

/*
 * Removes initial and final space characters.
 * @function {static String} ITHit.Trim
 * 
 * @param {Object} sText Text to trim.
 * @param {Object} sType Trim type.
 * @param {Object} bHardCheck Whether first parameter must be string type. Default is false.
 * 
 * @throw {ITHit.Exception} If not string passed as first parameter and bHardCheck is set to true.
 */
ITHit.Add('Trim',
	function (sText, sType, bHardCheck) {
		
		if ( ('string' != typeof sText) && !(sText instanceof String) ) {
			if (!bHardCheck) {
				return sText;
			} else {
				throw new ITHit.Exception('ITHit.Trim() expected string as first prametr.');
			}
		}
		
		switch (sType) {
		
			case ITHit.Trim.Left:
				return sText.replace(/^\s+/, '');
				break;
				
			case ITHit.Trim.Right:
				return sText.replace(/\s+$/, '');
				break;
				
			default:
				return sText.replace(/(?:^\s+|\s+$)/g, '');
		}
	}
);

/*
 * Trim only initial space chars.
 * @property {String} ITHit.Trim.Left
 */
ITHit.Add('Trim.Left',  'Left');

/*
 * Trim only final space chars.
 * @property {String} ITHit.Trim.Right
 */
ITHit.Add('Trim.Right', 'Right');

/*
 * Trim initial and final space chars.
 * @property {String} ITHit.Trim.Both
 */
ITHit.Add('Trim.Both',  'Both');

/**
 * Base class for exceptions.
 * @class ITHit.Exception
 */
ITHit.Add('Exception',
	(function () {
		
		/*
		 * Create instance of Exception class.
		 * @constructor Exception
		 * 
		 * @param {String} sMessage Exception message.
		 * @param {optional Object} oInnerException Original exception.
		 */
		var Exception = function (sMessage, oInnerException) {
			
			/**
			 * Exception message.
			 * @property {String} ITHit.Exception.Message
			 */
			this.Message = sMessage;
			
			/**
			 * Original exception that caused this exception.
			 * @property {ITHit.Exception}  ITHit.Exception.InnerException
			 */
			this.InnerException = oInnerException;
			
			// Whether exception must be logged.
			if (ITHit.Logger.GetCount(ITHit.LogLevel.Error)) {
				
				var sException =
					'Exception: '+ this.Name +'\n'
					+ 'Message: '+ this.Message +'\n';
				
				if (oInnerException) {
					sException += ((!oInnerException instanceof Error) ? 'Inner exception: ' : '')
						+ this.GetExceptionsStack(oInnerException);
					
				}
				
				// Log exception.
				ITHit.Logger.WriteMessage(sException, ITHit.LogLevel.Error);
			}
		}
		
		/*
		 * Exception title.
		 * @property {private String} ITHit.Exception.Name
		 */
		Exception.prototype.Name = 'Exception';
		
		/*
		 * Returns oririginal exceptions.
		 * @function {private String} ITHit.Exception.GetExceptionsStack
		 * 
		 * @param {Object} oException 
		 * @param {Integer} iLevel 
		 * 
		 * @returns Stack of original exceptions.
		 */
		Exception.prototype.GetExceptionsStack = function (oException, iLevel) {
			
			if ('undefined' === typeof oException) {
				var oException = this;
			}
			
			var iLevel = iLevel ? iLevel : 0;
			var sStr = '';
			var sSingleMargin = '      ';
			var sMargin = '';
			for (var i = 0; i < iLevel; i++) {
				sMargin += sSingleMargin;
			}
			
			if (oException instanceof ITHit.Exception) {
				sStr += sMargin + (oException.Message ? oException.Message : oException) + '\n';
			} else {
				if (ITHit.Config.ShowOriginalException) {
					sStr += '\nOriginal exception:\n';
					if ( ('string' != typeof oException) && !(oException instanceof String) ) {
						for (var sProp in oException) {
							sStr += '\t' + sProp + ': "' + ITHit.Trim(oException[sProp]) + '"\n';
						}
					} else {
						sStr += '\t' + oException +'\n';
					}
				}
			}
			
			return sStr;
		}
		
		/*
		 * Convert object to string.
		 * @function {String} ITHit.Exception.toString
		 * 
		 * @returns Stack of original exceptions.
		 */
		Exception.prototype.toString = function () {
			return this.GetExceptionsStack();
		}
		
		return Exception;
	})()
);

/*
 * Method for emulating classical inharitance for javascript.
 * @function {static} ITHit.Extend
 * 
 * @param {Object} subClass Class to extend.
 * @param {Object} baseClass Base class.
 */
ITHit.Add('Extend',
	function (subClass, baseClass){
		
		function inheritance(){};
		
		inheritance.prototype = baseClass.prototype;
		
		// set prototype to new instance of baseClass
		// _without_ the constructor
		subClass.prototype = new inheritance();
		subClass.prototype.constructor = subClass;
		subClass.baseConstructor = baseClass;
		
		// enable multiple inheritance
		if (baseClass.base) {
			baseClass.prototype.base = baseClass.base;
		}
		
		subClass.base = baseClass.prototype;
	}
);

ITHit.Add('Events',
	function() {

		var Events = function() {
			this._Listeners      = this._NewObject();
			this._DispatchEvents = {};
			this._DelayedDelete  = {};
		}

		Events.prototype._NewObject = function() {

			var obj = {};
			for (var prop in obj) {
				delete obj[prop];
			}

			return obj;
		}

		Events.prototype.AddListener = function(oObject, sEventName, mHandler, mHandlerScope) {

			var sInstanceName = oObject.__instanceName;

			var oHandler;
			var oNsHandler = ITHit.EventHandler;

			if (!(mHandler instanceof ITHit.EventHandler)) {
				oHandler = new ITHit.EventHandler(mHandlerScope || null, mHandler);
			}
			else {
				oHandler = mHandler;
			}

			var oListeners = this._Listeners[sInstanceName] || (this._Listeners[sInstanceName] = this._NewObject());
			var oEventHandlers = oListeners[sEventName] || (oListeners[sEventName] = []);

			var bFounded = false;
			for (var i = 0, l = oEventHandlers.length; i < l; i++) {
				if (oEventHandlers[i].IsEqual(oHandler)) {
					bFounded = true;
					break;
				}
			}

			if (!bFounded) {
				oEventHandlers.push(oHandler);
			}
		}

		Events.prototype.DispatchEvent = function(oObject, sEventName, mData) {

			var sInstanceName = oObject.__instanceName;

			if (!this._Listeners[sInstanceName] || !this._Listeners[sInstanceName][sEventName] || !this._Listeners[sInstanceName][sEventName].length) {
				return undefined;
			}

			var oNsHandler = ITHit.EventHandler;
			var bRet;
			var oHandlers  = [];
			for (var i = 0, l = this._Listeners[sInstanceName][sEventName].length; i < l; i++) {
				oHandlers.push(this._Listeners[sInstanceName][sEventName][i]);
			}

			this._DispatchEvents[sInstanceName] = (this._DispatchEvents[sInstanceName] || 0) + 1;
			this._DispatchEvents[sInstanceName+':'+sEventName] = (this._DispatchEvents[sInstanceName+':'+sEventName] || 0) + 1;

			for (var i = 0; i < oHandlers.length; i++) {

				var bForRet;

				if (oHandlers[i] instanceof oNsHandler) {
					try {
						bForRet = oHandlers[i].CallHandler(oObject, sEventName, mData);
					} catch(e) {
						throw e;
					}
				}
				if (oHandlers[i] instanceof Function) {
					try {
						bForRet = oHandlers[i](oObject, sEventName, mData);
					} catch(e) {
						throw e;
					}
				}

				if (!ITHit.Utils.IsUndefined(bForRet)) {
					bRet = bForRet;
				}
			}

			this._DispatchEvents[sInstanceName]--;
			this._DispatchEvents[sInstanceName+':'+sEventName]--;

			this._CheckDelayedDelete(oObject, sEventName);

			return bRet;
		}

		Events.prototype.RemoveListener = function(oObject, sEventName, mHandler, mHandlerScope) {

			var sInstanceName = oObject.__instanceName;

			mHandlerScope = mHandlerScope || null;

			if (!this._Listeners[sInstanceName] || !this._Listeners[sInstanceName][sEventName] || !this._Listeners[sInstanceName][sEventName].length) {
				return true;
			}

			var aHandlers = this._Listeners[sInstanceName][sEventName];
			for (var i = 0, l = aHandlers.length; i < l; i++) {
				if (aHandlers[i].IsEqual(mHandlerScope, mHandler)) {
					this._Listeners[sInstanceName][sEventName].splice(i, 1);
					break;
				}
			}
		}

		Events.prototype.RemoveAllListeners = function(oObject, sEventName) {

			var sInstanceName = oObject.__instanceName;

			if (!ITHit.Utils.IsUndefined(sEventName)) {
				if (ITHit.Utils.IsUndefined(this._DispatchEvents[sInstanceName +':'+ sEventName])) {
					delete this._Listeners[sInstanceName][sEventName];
				} else {
					this._DelayedDelete[sInstanceName +':'+ sEventName] = true;
				}

			} else {
				if (ITHit.Utils.IsUndefined(this._DispatchEvents[sInstanceName])) {
					delete this._Listeners[sInstanceName];
				} else {
					this._DelayedDelete[sInstanceName] = true;
				}
			}

		}

		Events.prototype._CheckDelayedDelete = function(oObject, sEventName) {

			var sInstanceName = oObject.__instanceName;

			if (!this._DispatchEvents[sInstanceName+':'+sEventName]) {
				delete this._DispatchEvents[sInstanceName+':'+sEventName];
				if (!ITHit.Utils.IsUndefined(this._DelayedDelete[sInstanceName +':'+ sEventName])) {
					this.RemoveAllListeners(oObject, sEventName);
				}
			}
			if (!this._DispatchEvents[sInstanceName]) {
				delete this._DispatchEvents[sInstanceName];
				if (!ITHit.Utils.IsUndefined(this._DelayedDelete[sInstanceName])) {
					this.RemoveAllListeners(oObject);
				}
			}
		}

		Events.prototype.ListenersLength = function(oObject, sEventName) {

			var sInstanceName = oObject.__instanceName;

			if (!this._Listeners[sInstanceName] || !this._Listeners[sInstanceName][sEventName]) {
				return 0;
			}

			return this._Listeners[sInstanceName][sEventName].length;
		}

		Events.prototype.Fix = function(e) {

			// Get event.
			e = e || window.event;

			// Get target element.
			if (!e.target && e.srcElement) {
				e.target = e.srcElement;
			}

			// Calculate pageX, pageY if not defined.
			if ((null == e.pageX) && (null != e.clientX)) {
				var html = document.documentElement,
					body = document.body;
				e.pageX = e.clientX + (html && html.scrollLeft || body && body.scrollLeft || 0) - (html.clientLeft || 0);
				e.pageY = e.clientY + (html && html.scrollTop || body && body.scrollTop || 0) - (html.clientTop || 0);
			}

			// Fix mouse button definition.
			if (!e.which && e.button) {
				e.which = e.button & 1 ? 1 : ( e.button & 2 ? 3 : ( e.button & 4 ? 2 : 0 ) );
			}

			return e;
		}

		Events.prototype.AttachEvent = function(elem, eventName, handler) {

			eventName = eventName.replace(/^on/, '');

			if (elem.addEventListener) {
				elem.addEventListener(eventName, handler, false);
			} else if(elem.attachEvent) {
				elem.attachEvent('on'+ eventName, handler);
			} else {
				elem['on'+ eventName] = handler;
			}
		}

		Events.prototype.DettachEvent = function(elem, eventName, handler) {

			eventName = eventName.replace(/^on/, '');

			if (elem.removeEventListener) {
				elem.removeEventListener(eventName, handler, false);
			} else if(elem.detachEvent) {
				elem.detachEvent('on'+ eventName, handler);
			} else {
				elem['on'+ eventName] = null;
			}
		}

		Events.prototype.Stop = function(e) {
			e = e || window.event;

			if (e.stopPropagation) {
				e.stopPropagation();
			}

			if (e.preventDefault) {
				e.preventDefault();
			} else {
				e.returnValue  = false;
			}

			e.cancelBubble = true;

			return false;
		}

		return new Events();

	}()
);

ITHit.Add('EventHandler',
	function() {
		var EventHandler = function(oScope, mMethod) {

			var oNsUtils = ITHit.Utils;

			if (!oNsUtils.IsObjectStrict(oScope) && !oNsUtils.IsNull(oScope)) {
				throw new ITHit.Exception('Event handler scope expected to be an object.');
			}

			if (!oNsUtils.IsFunction(mMethod) && (oScope && !oNsUtils.IsString(mMethod))) {
				throw new ITHit.Exception('Method handler expected to be a string or function.');
			}

			if (oScope) {
				this.Scope = oScope;
				this.Name = oScope.__instanceName;
			} else {
				this.Scope = window;
				this.Name = 'window';
			}

			this.Method = mMethod;
		}

		EventHandler.prototype.IsEqual = function(oScopeOrEventHandler, mMethod) {

			if (oScopeOrEventHandler instanceof ITHit.EventHandler) {
				return this.GetCredentials() === oScopeOrEventHandler.GetCredentials();
			}
			else {
				return ((oScopeOrEventHandler || null) === this.Scope) && (mMethod === this.Method);
			}
		}

		EventHandler.prototype.GetCredentials = function() {
			return this.Name + '::' + this.Method;
		}
		
		EventHandler.prototype.CallHandler = function(oScope, sEvent, aParams) {
				
			if ( !(aParams instanceof Array) ) {
				aParams = [aParams];
			}
			
			if (this.Scope) {
				
				if (this.Method instanceof Function) {
					return this.Method.apply(this.Scope || window, aParams.concat([oScope]));
				}
				else {
					try {
						return this.Scope[this.Method].apply(this.Scope, aParams.concat([oScope]));
					} catch(e) {
						throw new ITHit.Exception(e);
					}
				}
				
			}
			else {
				return this.Method.apply({}, aParams.concat([oScope]));
			}
		}
		
		return EventHandler;
	}()
);

ITHit.Add('HtmlEncode',
	function (sText) {
		return sText.replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/'/g, '&amp;').replace(/"/g, '&quot;');
	}
);
	
ITHit.Add('HtmlDecode',
	function (sText) {
		return sText.replace(/&quot;/, '"').replace(/&amp;/g, '\'').replace(/&gt;/g, '>').replace(/&lt;/g, '<');
	}
);

ITHit.Add('Encode',
	function(sText) {
		
		if (!sText) {
			return sText;
		}
		
		return ITHit.EncodeURI(sText.replace(/%/g,  "%25"))
			.replace(/~/g,  "%7E")
			.replace(/!/g,  "%21")
			.replace(/@/g,  "%40")
			.replace(/#/g,  "%23")
			.replace(/\$/g, "%24")
			.replace(/&/g,  "%26")
			.replace(/\*/g, "%2A")
			.replace(/\(/g, "%28")
			.replace(/\)/g, "%29")
			.replace(/\-/g, "%2D")
			.replace(/_/g,  "%5F")
			.replace(/\+/g, "%2B")
			.replace(/\=/g, "%3D")
			.replace(/'/g,  "%27")
			.replace(/;/g,  "%3B")
			.replace(/\,/g, "%2C")
			.replace(/\?/g, "%3F");
	}
);

ITHit.Add('EncodeURI',
	function(sText) {
		
		if (!sText) {
			return sText;
		}
		
		return encodeURI(sText).replace(/%25/g, "%");
	}
);

ITHit.Add('Decode',
	function(sText) {
		
		if (!sText) {
			return sText;
		}
		
		var sText = sText
			.replace(/%7E/gi, "~")
			.replace(/%21/g,  "!")
			.replace(/%40/g,  "@")
			.replace(/%23/g,  "#")
			.replace(/%24/g,  "$")
			.replace(/%26/g,  "&")
			.replace(/%2A/gi, "*")
			.replace(/%28/g,  "(")
			.replace(/%29/g,  ")")
			.replace(/%2D/gi, "-")
			.replace(/%5F/gi, "_")
			.replace(/%2B/gi, "+")
			.replace(/%3D/gi, "=")
			.replace(/%27/g,  "'")
			.replace(/%3B/gi, ";")
			.replace(/%2E/gi, ".")
			.replace(/%2C/gi, ",")
			.replace(/%3F/gi, "?");
		
		return ITHit.DecodeURI(sText);
	}
);

ITHit.Add('DecodeURI',
	function(sText) {
		
		if (!sText) {
			return sText;
		}
		
		return decodeURI(sText.replace(/%([^0-9A-F]|.(?:[^0-9A-F]|$)|$)/gi, "%25$1"));
	}
);

ITHit.Add('DecodeHost',
	function(sHref) {
		
		// Check whether host contains encoded characters.
		if (/^(http|https):\/\/[^:\/]*?%/.test(sHref)) {
			
			var aMatchRes = sHref.match(/^(?:http|https):\/\/[^\/:]+/);
			if (aMatchRes && aMatchRes[0]) {
				var sMatch = aMatchRes[0].replace(/^(http|https):\/\//, '');
				
				// Decode characters to prevent crossdomain restriction.
				sHref = sHref.replace(sMatch, ITHit.Decode(sMatch));
			}
		}
		
		return sHref;
	}
);


(function() {

	var fNoop = function() {
	};

	var extendWithSuper = function (childClass, newProperties) {
		// Extend and setup virtual methods
		for (var key in newProperties) {
			if (!newProperties.hasOwnProperty(key)) {
				continue;
			}

			var value = newProperties[key];
			if (typeof value == 'function' && typeof childClass[key] == 'function' && childClass[key] !== fNoop) {
				childClass[key] = coverVirtual(value, childClass[key]);
			} else {
				childClass[key] = value;
			}
		}

		// Default state
		if (!childClass._super) {
			childClass._super = fNoop;
		}
	};

	var coverVirtual = function (childMethod, parentMethod) {
		return function () {
			var old = this._super;
			this._super = parentMethod;
			var r = childMethod.apply(this, arguments);
			this._super = old;
			return r;
		};
	};

	var instanceUniqueCounter = 0;

	/**
	 * @name ITHit.DefineClass
	 * @param {String} globalName
	 * @param {Function} parentClass
	 * @param {Object} [prototypeProperties]
	 * @param {Object} [staticProperties]
	 */
	ITHit.Add('DefineClass', function (globalName, parentClass, prototypeProperties, staticProperties) {
		parentClass = parentClass !== null ? parentClass : function() {};

		if (!parentClass) {
			throw new Error('Not found extended class for ' + globalName);
		}

		if (prototypeProperties.hasOwnProperty('__static')) {
			staticProperties = prototypeProperties.__static;
			delete prototypeProperties.__static;
		}

		var childClass;

		// The constructor function for the new subclass is either defined by you
		// (the "constructor" property in your `extend` definition), or defaulted
		// by us to simply call the parent's constructor.
		if (prototypeProperties && prototypeProperties.hasOwnProperty('constructor')) {
			childClass = function() {
				this.__instanceName = this.__className + instanceUniqueCounter++;
				return coverVirtual(prototypeProperties.constructor, parentClass).apply(this, arguments);
			};
		} else {
			childClass = function () {
				this.__instanceName = this.__className + instanceUniqueCounter++;
				return parentClass.apply(this, arguments);
			};
		}

		// Add static properties to the constructor function, if supplied.
		for (var prop in parentClass) {
			childClass[prop] = parentClass[prop];
		}
		extendWithSuper(childClass, staticProperties);

		// Set the prototype chain to inherit from `parent`, without calling
		// `parent`'s constructor function.
		var Surrogate = function () {
			this.constructor = childClass;
		};
		Surrogate.prototype = parentClass.prototype;
		childClass.prototype = new Surrogate;

		// Clone empty objects
		for (var key in Surrogate.prototype) {
			if (!Surrogate.prototype.hasOwnProperty(key)) {
				continue;
			}

			var value = Surrogate.prototype[key];
			if (!value) {
				continue;
			}

			if (value instanceof Array) {
				if (value.length === 0) {
					childClass.prototype[key] = [];
				}
			} else if (typeof value === 'object') {
				var isEmpty = true;
				for (var k in value) {
					isEmpty = isEmpty && value.hasOwnProperty(k);
				}
				if (isEmpty) {
					childClass.prototype[key] = {};
				}
			}
		}

		// Add prototype properties (instance properties) to the subclass,
		// if supplied.
		if (prototypeProperties) extendWithSuper(childClass.prototype, prototypeProperties);

		// Share class name
		childClass.__className = childClass.prototype.__className = globalName;

		// Split namespace
		var iPos = globalName.lastIndexOf('.'),
			sLocalName = globalName.substr(iPos + 1);
		return ITHit.Declare(globalName.substr(0, iPos))[sLocalName] = childClass;
	});

})();

/*
 * Base namespace for exceptions.
 * @namespace ITHit.Exceptions
 */

/**
 * This namespace provides classes for accessing WebDAV server items, file structure management, properties management and items locking.
 * @namespace ITHit.WebDAV.Client
 */

/**
 * The ITHit.WebDav.Client.Exceptions namespace provides classes that represent various WebDAV client library exceptions, erroneous server responses and HTTP errors.
 * @namespace ITHit.WebDAV.Client.Exceptions
 */


ITHit.Temp.WebDAV_Phrases={
	CrossDomainRequestAttempt: 'Attempting to make cross-domain request.\nRoot URL: {0}\nDestination URL: {1}\nMethod: {2}',
	
	// WebDavRequest
	Exceptions: {
		BadRequest:         'The request could not be understood by the server due to malformed syntax.',
		Conflict:           'The request could not be carried because of conflict on server.',
		DependencyFailed:   'The method could not be performed on the resource because the requested action depended on another action and that action failed.',
		InsufficientStorage: 'The request could not be carried because of insufficient storage.',
		Forbidden:          'The server refused to fulfill the request.',
		Http:               'Exception during the request occurred.',
		Locked:             'The item is locked.',
		MethodNotAllowed:   'The method is not allowed.',
		NotFound:           'The item doesn\'t exist on the server.',
		PreconditionFailed: 'Precondition failed.',
		PropertyFailed:     'Failed to get one or more properties.',
		PropertyForbidden:  'Not enough rights to obtain one of requested properties.',
		PropertyNotFound:   'One or more properties not found.',
		Unauthorized:       'Incorrect credentials provided or insufficient permissions to access the requested item.',
		LockWrongCountParametersPassed: 'Lock.{0}: Wrong count of parameters passed. (Passed {1})',
		UnableToParseLockInfoResponse:  'Unable to parse response: quantity of LockInfo elements isn\'t equal to 1.',
		ParsingPropertiesException:     'Exception while parsing properties.',
		InvalidDepthValue: 'Invalid Depth value.',
		FailedCreateFolder: 'Failed creating folder.',
		FailedCreateFile: 'Failed creating file.',
		FolderWasExpectedAsDestinationForMoving: 'Folder was expected as destination for moving folder.',
		AddOrUpdatePropertyDavProhibition: 'Add or update of property {0} ignored: properties from "DAV:" namespace could not be updated/added.',
		DeletePropertyDavProhibition: 'Delete of property {0} ignored: properties from "DAV:" namespace could not be deleted.',
		NoPropertiesToManipulateWith: 'Calling UpdateProperties ignored: no properties to update/add/delete.',
		ActiveLockDoesntContainLockscope: 'Activelock node doesn\'t contain lockscope node.',
		ActiveLockDoesntContainDepth: 'Activelock node doedn\'t contain depth node.',
		WrongCountPropertyInputParameters: 'Wrong count of input parameters passed for Property constructor. Expected 1-3, passed: {1}.',
		FailedToWriteContentToFile: 'Failed to write content to file.',
		PropertyUpdateTypeException: 'Property expected to be an Property class instance.',
		PropertyDeleteTypeException: 'Property name expected to be an PropertyName class instance.',
		UnknownResourceType:  'Unknown resource type.',
		NotAllPropertiesReceivedForUploadProgress: 'Not all properties received for upload progress. {0}',
		ReportOnResourceItemWithParameterCalled: 'For files the method should be called without parametres.',
		WrongHref: 'Href expected to be a string.',
		WrongUploadedBytesType: 'Count of uploaded bytes expected to be a integer.',
		WrongContentLengthType: 'File content length expected to be a integer.',
		BytesUploadedIsMoreThanTotalFileContentLength: 'Bytes uploaded is more than total file content length.',
		ExceptionWhileParsingProperties: 'Exception while parsing properties.'
	},
	ResourceNotFound:         'Resource not found. {0}',
	ResponseItemNotFound:     'The response doesn\'t have required item. {0}',
	ResponseFileWrongType: 'Server returned folder while file is expected. {0}',
	FolderNotFound:           'Folder not found. {0}',
	ResponseFolderWrongType:  'Server returned file while folder is expected. {0}',
	ItemIsMovedOrDeleted:     'Cannot perform operation because item "{0}" is moved or deleted.',
	FailedToCopy:             'Failed to copy item.',
	FailedToCopyWithStatus:   'Copy failed with status {0}: {1}.',
	FailedToDelete:           'Failed to delete item.',
	DeleteFailedWithStatus:   'Delete failed with status {0}: {1}.',
	PutUnderVersionControlFailed: 'Put under version control failed.',
	FailedToMove:             'Failed to move item.',
	MoveFailedWithStatus:     'Move failed with status {0}: {1}.',
	UnlockFailedWithStatus:   'Unlock failed with status {0}: {1}.',
	PropfindFailedWithStatus: 'PROPFIND method failed with status {0}.',
	FailedToUpdateProp:       'Failed to update or delete one or more properties.',
	FromTo:                   'The From parameter cannot be less than To.',
	NotToken:                 'The supplied string is not a valid HTTP token.',
	RangeTooSmall:            'The From or To parameter cannot be less than 0.',
	RangeType:                'A different range specifier has already been added to this request.',
	ServerReturned:           'Server returned:',
	UserAgent:                'IT Hit WebDAV AJAX Library v{0}',
	
	// WebDavResponse
	wdrs: {
		status: '\n{0} {1}',
		response: '{0}: {1}'
	}
};



ITHit.oNS = ITHit.Declare('ITHit.Exceptions');

/*
 * Exception for Logger class.
 * @class ITHit.Exceptions.LoggerException
 * @extends ITHit.Exception
 */
/*
 * Initializes a new instance of the LoggerException class with a specified error message and a reference to the inner exception that is the cause of this exception. 
 * @constructor LoggerException
 * 
 * @param {String} sMessage The error message string.
 * @param {optional ITHit.Exception} oInnerException The ITHit.Exception instance that caused the current exception.
 */
ITHit.oNS.LoggerException = function(sMessage, oInnerException) {
	
	// Inheritance definition.
	ITHit.Exceptions.LoggerException.baseConstructor.call(this, sMessage, oInnerException);
}

// Extend class.
ITHit.Extend(ITHit.oNS.LoggerException, ITHit.Exception);

// Exception name.
ITHit.oNS.LoggerException.prototype.Name = 'LoggerException';

/**
 * Type of information being logged.
 * @api
 * @enum {number}
 * @class ITHit.LogLevel
 */
ITHit.DefineClass('ITHit.LogLevel', null, {}, /** @lends ITHit.LogLevel */{

	/**
	 * All messages will be written to log.
	 * @type {number}
	 */
	All: 32,
	
	/**
	 * Messages with LogLevel.Debug level will be written to log.
	 * @type {number}
	 */
	Debug: 16,
	
	/**
	 * Messages with LogLevel.Info level will be written to log.
	 * @type {number}
	 */
	Info: 8,
	
	/**
	 * Messages with LogLevel.Warn level will be written to log.
	 * @type {number}
	 */
	Warn: 4,
	
	/**
	 * Messages with LogLevel.Error level will be written to log.
	 * @type {number}
	 */
	Error: 2,
	
	/**
	 * Messages with LogLevel.Fatal level will be written to log.
	 * @type {number}
	 */
	Fatal: 1,
	
	/**
	 * No messages will be written to log.
	 * @type {number}
	 */
	Off: 0
});


;
(function() {
	
	// Log listeners.
	var oListeners = {}
	
	// Declare counter for listeners
	var oListenersCount = {};
	
	// Declare all listeners listening each log level.
	var oListenersForLevels = {};
	
	// Initialize list of lesteners and counter.
	for (var sProp in ITHit.LogLevel) {
		oListeners[ITHit.LogLevel[sProp]]          = [];
		oListenersForLevels[ITHit.LogLevel[sProp]] = [];
	}

	var recheck = function(bIncrease, iFrom, iTo, fHandler) {

		for (var sProp in ITHit.LogLevel) {

			// Skip elements with higher log level.
			if (ITHit.LogLevel[sProp] > iTo) {
				continue;
			}

			// Skip elements with lower log level
			if (!ITHit.LogLevel[sProp]
				|| (iFrom >= ITHit.LogLevel[sProp])
			) {
				continue;
			}

			// Increase log level listeners list.
			if (bIncrease) {
				oListenersForLevels[ITHit.LogLevel[sProp]].push(fHandler);

				// Decrease log level listeners list.
			} else {
				for (var i = 0; i < oListenersForLevels[ITHit.LogLevel[sProp]].length; i++) {
					if (oListenersForLevels[ITHit.LogLevel[sProp]][i] == fHandler) {

						// Delete element from list.
						oListenersForLevels[ITHit.LogLevel[sProp]].splice(i, 1);
					}
				}
			}
		}

	};

	recheck.add = function(iTo, fHandler) {
		recheck.increase(ITHit.LogLevel.Off, iTo, fHandler);
	};

	recheck.del = function(iTo, fHandler) {
		recheck.decrease(ITHit.LogLevel.Off, iTo, fHandler);
	};

	recheck.increase = function(iFrom, iTo, fHandler) {
		recheck(true, iFrom, iTo, fHandler);
	};

	recheck.decrease = function(iFrom, iTo, fHandler) {
		recheck(false, iFrom, iTo, fHandler);
	};

	/**
	 * Provides static methods for logging.
	 * @api
	 * @class ITHit.Logger
	 */
	ITHit.DefineClass('ITHit.Logger', null, {}, /** @lends ITHit.Logger */{

		Level: ITHit.Config.LogLevel || ITHit.LogLevel.Debug,

		/**
		 * Handler function called when event is trigger.
		 * @callback ITHit.Logger~EventHandler
		 */

		/**
		 * Adds log listener.
		 * @api
		 * @param {ITHit.Logger~EventHandler} fHandler Handler function.
		 * @param {number} iLogLevel Log level messages capturing.
		 */
		AddListener: function (fHandler, iLogLevel) {

			// Delete listener from listeners list.
			if (iLogLevel == ITHit.LogLevel.Off) {
				this.RemoveListener();
			}

			// Initialize indexes.
			var iLevel = 0;
			var iIndex = 0;

			// Set outer loop exit lable.
			outer:
				// Loop through all log levels.
				for (var iProp in oListeners) {

					// Loop through all listeners for log level.
					for (var i = 0; i < oListeners[iProp].length; i++) {

						// If handler is found then save it's position for future comparison.
						if (oListeners[iProp][i] == fHandler) {

							// Save indexes for founded listener.
							iLevel = iProp;
							iIndex = i;

							// Break outer loop.
							break outer;
						}
					}
				}

			// Listener is not found.
			if (!iLevel) {

				// Add listener for specified log level.
				oListeners[iLogLevel].push(fHandler);

				recheck.add(iLogLevel, fHandler);

				// Listener has been found.
			} else {

				// If specified log level for listener is not the same.
				if (iLogLevel != iLevel) {

					// Delete listener for old log level.
					oListeners[iLevel].splice(iIndex, 1);

					// Declare listener for specified log level.
					oListeners[iLogLevel].push(fHandler);

					if (iLogLevel > iLevel) {
						recheck.increase(iLevel, iLogLevel, fHandler);
					} else {
						recheck.decrease(iLogLevel, iLevel, fHandler);
					}
				}
			}
		},

		/**
		 * Removes log listener.
		 * @api
		 * @param {ITHit.Logger~EventHandler} fHandler Handler function.
		 */
		RemoveListener: function (fHandler) {

			// Set lable for outer loop.
			outer:
				// Loop through all log levels.
				for (var iLogLevel in oListeners) {

					// Loop through all listeners for log level.
					for (var i = 0; i < oListeners[iLogLevel].length; i++) {

						// Listener is found.
						if (oListeners[iLogLevel][i] == fHandler) {

							// Delete specified listener.
							oListeners[iLogLevel].splice(i, 1);

							recheck.del(iLogLevel, fHandler);

							// Break outer loop.
							break outer;
						}
					}
				}

			return true;
		},

		/**
		 * Set log level for listener.
		 * @param fHandler
		 * @param iLogLevel
		 * @returns {*}
		 */
		SetLogLevel: function (fHandler, iLogLevel) {
			return this.AddListener(fHandler, iLogLevel, true);
		},

		/**
		 * Get log level for listener.
		 * @param fHandler
		 * @returns {*}
		 */
		GetLogLevel: function (fHandler) {

			// Loop through all log levels.
			for (var iLogLevel in oListeners) {

				// Loop through all listeners for log level.
				for (var i = 0; i < oListeners[iLogLevel].length; i++) {

					// Listener has been found.
					if (oListeners[iLogLevel][i] == fHandler) {

						// Return log level for specified listener..
						return iLogLevel;
					}
				}
			}

			// Listener has not been found in listeners list.
			return false;
		},

		/**
		 * Get listeners for specified log level.
		 * @param iLogLevel
		 * @returns {*}
		 */
		GetListenersForLogLevel: function (iLogLevel) {
			return oListenersForLevels[iLogLevel];
		},

		/**
		 * Get count of listeners for specified log level.
		 * @param iLogLevel
		 * @returns {*}
		 */
		GetCount: function (iLogLevel) {
			return oListenersForLevels[iLogLevel].length;
		},

		/**
		 * Writes response data to log if Level value is LogLevel.Info or higher.
		 *
		 * @param {Object} oResponse Response object.
		 */
		WriteResponse: function (oResponse) {

			// Check count of listeners for LogLevel.Info messages.
			if (Logger.GetCount(ITHit.LogLevel.Info)) {

				var sStr = '';

				// Add status and description data.
				if (oResponse instanceof HttpWebResponse) {
					sStr += '\n' + oResponse.StatusCode + ' ' + oResponse.StatusDescription + '\n';
				}

				// Add response URI.
				sStr += oResponse.ResponseUri + '\n';

				// Add all response headers.
				for (var sProp in oResponse.Headers) {
					sStr += sProp + ': ' + oResponse.Headers[sProp] + '\n';
				}

				// Add response body.
				sStr += oResponse.GetResponse();

				// Write response data to log.
				this.WriteMessage(sStr);
			}
		},

		/**
		 * Writs a message to log with a specified log level. Default log level is {@link ITHit.LogLevel#Info}
		 * @api
		 * @param {string} sMessage Message to be logged.
		 * @param {number} iLogLevel Logging level.
		 * @throws ITHit.Exceptions.LoggerException Function was expected as log listener.
		 */
		WriteMessage: function (sMessage, iLogLevel) {

			// Check log level.
			iLogLevel = ('undefined' == typeof iLogLevel) ? ITHit.LogLevel.Info : parseInt(iLogLevel);

			// Check whether there are listeners for current log level.
			if (ITHit.Logger.GetCount(iLogLevel)) {

				// Get listeners for current log level.
				var aListeners = this.GetListenersForLogLevel(iLogLevel);

				var sMessage = String(sMessage).replace(/([^\n])$/, '$1\n');

				// Loop through listeners.
				for (var i = 0; i < aListeners.length; i++) {
					try {
						// Pass message to lestener.
						aListeners[i](sMessage, ITHit.LogLevel.Info);
					} catch (e) {

						if (!aListeners[i] instanceof Function) {
							throw new ITHit.Exceptions.LoggerException('Log listener expected function, passed: "' + aListeners[i] + '"', e);
						} else {
							throw new ITHit.Exceptions.LoggerException('Message could\'not be logged.', e);
						}
					}
				}
			}
		},

		StartLogging: function () {
		},

		FinishLogging: function () {
		},

		StartRequest: function () {
			//this.WriteMessage('--- Request started ---', ITHit.LogLevel.Info);
		},

		FinishRequest: function () {
			//this.WriteMessage('--- Request finished ---', ITHit.LogLevel.Info);
		}
	});

})();

ITHit.oNS = ITHit.Declare('ITHit.Exceptions');

/*
 * Exception for Phrases class.
 * @class ITHit.Exceptions.PhraseException
 * @extends ITHit.Exception
 */
/*
 * Initializes a new instance of the PhraseException class with a specified error message and a reference to the inner exception that is the cause of this exception.
 * @constructor PhraseException
 * 
 * @param {String} sMessage The error message string.
 * @param {optional ITHit.Exception} oInnerException The ITHit.Exception instance that caused the current exception.
 */
ITHit.oNS.PhraseException = function(sMessage, oInnerException) {
	
	// Inheritance definition.
	ITHit.Exceptions.PhraseException.baseConstructor.call(this, sMessage, oInnerException);
}

// Extend class.
ITHit.Extend(ITHit.oNS.PhraseException, ITHit.Exception);

// Exception name.
ITHit.oNS.PhraseException.prototype.Name = 'PhraseException';

/*
 * Class for work with the text. Allows parse transferred JSON text and gives the convenient 
 *    mechanism for returning phrases with an opportunity of replacement placeholders.
 * @struct {static} ITHit.Phrases
 */
ITHit.Phrases = (function() {
	
	var PhrasesToEval = {};
	
	/*
	 * Class for replacing placeholders. Class for using with replace method.
	 * @class _callbackReplace
	 */
	/*
	 * Initializes a new instance of the _callbackReplace class.
	 * @constructor _callbackReplace
	 * 
	 * @param {Object} oArguments Phrases to replace.
	 */
	var _callbackReplace = function(oArguments) {
		this._arguments = oArguments;
	}
	/*
	 * Method for replacing placeholders.
	 * @function {private String} _callbackReplace.Replace
	 * 
	 * @param {String} sPlaceholder Placeholder for replacing.
	 * 
	 * @returns Sentence.
	 */
	_callbackReplace.prototype.Replace = function(sPlaceholder) {
		
		var iIndex = sPlaceholder.substr(1, sPlaceholder.length-2);
		return ('undefined' != typeof this._arguments[iIndex]) ? this._arguments[iIndex] : sPlaceholder;
	}
	
	var Phrase = function(sPhrase) {
		this._phrase = sPhrase;
	}
		
	/*
	 * Method for returning a phrase. It is implicitly caused at access to object in a context of a line.
	 * @function {String} ITHit.Phrases.Phrase.toString
	 *
	 * @returns Phrase.
	 */
	Phrase.prototype.toString = function() {
		return this._phrase;
	}

	/*
	 * A method for replacement placeholders. Accepts unlimited number of parameters for replacement.
	 * @function {String} ITHit.Phrases.Phrase.Paste
	 *
	 * @returns Phrase.
	 */
	Phrase.prototype.Paste = function() {
		
		var sPhrase = this._phrase;
		if (/\{\d+?\}/.test(sPhrase)) {
			var oReplace = new _callbackReplace(arguments);
			sPhrase = sPhrase.replace(/\{(\d+?)\}/g, function(args){return oReplace.Replace(args);});
		}
		
		return sPhrase;
	}
	
	var Phrases = function() {}
	
	/*
	 * A method for transformation JSON text into javascript object.
	 * @function {Boolean} ITHit.Phrases.loadJSON
	 * 
	 * @paramset Syntax 1
	 * @param {String} mPhrases A line containing JSON text of phrases.
	 * @param {optional String} sNamespace A way in which phrases will be stored. It is set in the form of: "name1.name2".
	 * @paramset Syntax 2
	 * @param {Object} mPhrases A line containing JSON object of phrases.
	 * @param {optional String} sNamespace A way in which phrases will be stored. It is set in the form of: "name1.name2".
	 * 
	 * @throws ITHit.Exceptions.PhraseException &nbsp;Wrong text structure if failed to eval passed JSON text or namespace expected to be a string.
	 */
	Phrases.prototype.LoadJSON = function(mPhrases, sNamespace) {
		
		var utilsNs = ITHit.Utils
		
		if ( sNamespace && !utilsNs.IsString(sNamespace) ) {
			throw new ITHit.Exceptions.PhraseException('Namespace expected to be a string.');
		}
		
		var _context = this;
		
		// Select or create context if specified.
		if (sNamespace) {
			_context = ITHit.Declare(sNamespace);
		}
		
		try {
			var oPhrases = mPhrases;
			
			// Eval JSON string to obtain an object.
			if (utilsNs.IsString(oPhrases)) {
				oPhrases = eval('('+ mPhrases +')');
			}
			
			this._AddPhrases(oPhrases, _context);
			
		} catch(e) {
			console.dir(e);
			throw new ITHit.Exceptions.PhraseException('Wrong text structure.', e);
		}
	}
	
	Phrases.prototype.LoadLocalizedJSON = function(defaultPhrases, localizedPhrases, namespace) {
		
		var utilsNs     = ITHit.Utils,
		    isUndefined = utilsNs.IsUndefined,
		    isObject    = utilsNs.IsObject;
		
		if (!defaultPhrases || !utilsNs.IsObjectStrict(defaultPhrases)) {
			throw new ITHit.Exceptions.PhraseException('Default phrases expected to be an JSON object.');
		}
		if (localizedPhrases && !utilsNs.IsObjectStrict(localizedPhrases)) {
			throw new ITHit.Exceptions.PhraseException('Default phrases expected to be an JSON object');
		}
		
		var mergedPhrases;
		if (localizedPhrases) {
			
			mergedPhrases = {};
			
			// Clone localized phrases.
			this._MergePhrases(mergedPhrases, localizedPhrases);
			
			// Add default phrases whether localized is not set.
			this._MergePhrases(mergedPhrases, defaultPhrases);
			
		} else {
			mergedPhrases = defaultPhrases;
		}
		
		this.LoadJSON(mergedPhrases, namespace);
	}
	
	Phrases.prototype._MergePhrases = function(dest, source) {
		
		var utilsNs     = ITHit.Utils,
		    isUndefined = utilsNs.IsUndefined,
		    isObject    = utilsNs.IsObject;
		
		for (var prop in source) {
			if (!source.hasOwnProperty(prop)) continue;
			
			if (isUndefined(dest[prop])) {
				dest[prop] = source[prop];
			} else if (isObject(dest[prop])) {
				this._MergePhrases(dest[prop], source[prop]);
			}
		}
	}
	
	/*
	 * A method for converting phrases into objects.
	 * @function {private} ITHit.Phrases._AddPhrases
	 * 
	 * @param {Object} oPhrases Phrase object.
	 * @param {Object} _context A way in which phrases will be stored.
	 *
	 * @throws ITHit.Exceptions.PhraseException &nbsp;Passed phrase is one of reserved words.
	 */
	Phrases.prototype._AddPhrases = function(oPhrases, _context) {
		
		// Get context.
		_context = _context || this;
		
		// Loop through phrases.
		for (var phrase in oPhrases) {
			if ( ('object' != typeof oPhrases[phrase]) || !(oPhrases[phrase] instanceof Object) ) {
				
				switch (phrase) {
					case '_AddPhrases':
					case 'LoadJSON':
					case 'LoadLocalizedJSON':
					case '_Merge':
					case 'prototype':
					case 'toString':
						throw new ITHit.Exceptions.PhraseException('"'+ phrase +'" is reserved word.');
						break;
				}
				
				_context[phrase] = new Phrase(oPhrases[phrase]);
			} else {
				this._AddPhrases(oPhrases[phrase], _context[phrase] ? _context[phrase] : (_context[phrase] = {}));
			}
		}
	}
	
	return new Phrases();
})();


ITHit.oNS = ITHit.Declare('ITHit.Exceptions');

/*
 * Exception for XPath class.
 * @class ITHit.Exceptions.XPathException
 * @extends ITHit.Exception
 */
/*
 * Initializes a new instance of the XPathException class with a specified error message and a reference to the inner exception that is the cause of this exception.
 * @constructor XMLDocException
 * 
 * @param {String} sMessage Variable name.
 * @param {optional ITHit.Exception} oInnerException The ITHit.Exception instance that caused the current exception.
 */
ITHit.oNS.XPathException = function(sMessage, oInnerException) {
	
	// Inheritance definition.
	ITHit.Exceptions.XPathException.baseConstructor.call(this, sMessage, oInnerException);
}

// Extend class.
ITHit.Extend(ITHit.oNS.XPathException, ITHit.Exception);

// Exception name.
ITHit.oNS.XPathException.prototype.Name = 'XPathException';

/*
 * XPath class.
 * @class ITHit.XPath
 */
ITHit.XPath = {
	_component: null,
	_version: null
};

/*
 * Static method for executing search.
 * @constructor XPath
 * 
 * @param {String} sExpression String expression for search.
 * @param {ITHit.XMLDoc} oXmlDom XML DOM object.
 * @param {optional ITHit.XPath.resolver} mResolver Namespace resolver object or null if namespace is not specified for search.
 * @param {optional ITHit.XPath.result} mResult Result object to reuse result object for search results or null for creating new result object.
 * 
 * @returns Result object or XML DOM element.
 * 
 * @throws ITHit.Exceptions.XPathException &nbsp;
 */
/*
 * @function {ITHit.XPath.result} ITHit.XPath
 */
ITHit.XPath.evaluate = function(sExpression, oXmlDom, oResolver, oResult, _bSelectSingle) {
	
	// Check whether variables has valid types.
	if ( ('string' != typeof sExpression) && !(sExpression instanceof String) ) {
		throw new ITHit.Exceptions.XPathException('Expression was expected to be a string in ITHit.XPath.eveluate.');
	}
	
	if ( !(oXmlDom instanceof ITHit.XMLDoc) ) {
		throw new ITHit.Exceptions.XPathException('Element was expected to be an ITHit.XMLDoc object in ITHit.XPath.evaluate.');
	}
	
	if ( oResolver && !(oResolver instanceof ITHit.XPath.resolver) ) {
		throw new ITHit.Exceptions.XPathException('Namespace resolver was expected to be an ITHit.XPath.resolver object in ITHit.XPath.evaluate.');
	}
	
	if ( oResult && !(oResult instanceof ITHit.XPath.result) ) {
		throw new ITHit.Exceptions.XPathException('Result expected to be an ITHit.XPath.result object in ITHit.XPath.evaluate.');
	}
	
	// Set default values if is not passed.
	oResolver = oResolver || null;
	oResult   = oResult   || null;
	
	if ( document.implementation.hasFeature('XPath', '3.0') && document.evaluate ) {
		
		// Get XML DOM Element.
		var oContext = oXmlDom._get();
		var oContextDocument = oContext.ownerDocument || oContext;
		
		// If specified then reuse result for search.
		if (oResult) {
			oContextDocument.evaluate(sExpression, oContext, oResolver, ITHit.XPath.result.UNORDERED_NODE_SNAPSHOT_TYPE, oResult._res);
			return;
		}
		
		// Return result set.
		var oRes = new ITHit.XPath.result(oContextDocument.evaluate(sExpression, oContext, oResolver, ITHit.XPath.result.UNORDERED_NODE_SNAPSHOT_TYPE, null));
		
		// Return result set.
		if (!_bSelectSingle) {
			return oRes;
			
		// Return single (first) result.
		} else {
			return oRes.iterateNext();
		}
	} else if (undefined !== window.ActiveXObject) {
			
		// Get XML DOM Element.
		var oContext = oXmlDom._get();
		
		// Check whether setProperty method is supported.
		var bIsSetProp = false;
		try {
			oContext.getProperty('SelectionNamespaces');
			bIsSetProp = true;
		} catch(e) {}
		
		var bChangedNs = false;
		if (3 == ITHit.XMLDoc._version) {
			
			var sXml = oContext.xml.replace(/^\s+|\s+$/g, '');
			
			// Data to replace.
			var sReplaceWhat = 'urn:uuid:c2f41010-65b3-11d1-a29f-00aa00c14882/';
			var sReplaceTo   = 'cutted';
			
			if ( -1 != sXml.indexOf(sReplaceWhat) || true) {
				
				// Make replace.
				var sXmlNew = sXml.replace(sReplaceWhat, sReplaceTo);
				
				// Create new XML DOM document.
				var oXmlDoc = new ITHit.XMLDoc();
				oXmlDoc.load(sXmlNew);
				
				// Make replace for namespace resolver.
				if (oResolver) {
					var oNs = oResolver.getAll();
					for (var sAlias in oNs) {
						if (sReplaceWhat == oNs[sAlias]) {
							oNs.add(sAlias, sReplaceTo); 
							break;
						}
					}
				}
				
				oContext   = oXmlDoc._get();
				bIsSetProp = true;
				bChangedNs = true;
			}
		}
		
		// Set namespaces.
		if ( bIsSetProp && oResolver && oResolver.length() ) {
			
			var oNsPairs = oResolver.getAll();
			var aNs = [];
			for (var sAlias in oNsPairs) {
				aNs.push("xmlns:"+ sAlias +"='"+ oNsPairs[sAlias] +"'");
			}
			
			oContext.setProperty("SelectionNamespaces", aNs.join(' '));
		}
		
		if (bChangedNs) {
			oContext = oContext.documentElement;
		}
		
		try {
			
			// Return result set.
			if (!_bSelectSingle) {
				
				// Return result.
				if (!oResult) {
					return new ITHit.XPath.result(oContext.selectNodes(sExpression));
					
				// Reuse result object.
				} else {
					oResult._res = oContext.selectNodes(sExpression);
					return;
				}
				
			// Return single result.
			} else {
				
				// Search single element.
				var mOut = oContext.selectSingleNode(sExpression);
				
				if (mOut) {
					return new ITHit.XMLDoc(mOut);
				} else {
					return mOut;
				}
			}
			
		} catch(e) {
			
			// Check whether XML Document needed to be created.
			if ( !bIsSetProp
				&& (-1 != e.message.indexOf('Reference to undeclared namespace prefix'))
				&& oResolver
				&& oResolver.length()
			) {
				
				// Create new XML Document with passed XML Dom nodes.
				var sEl = new ITHit.XMLDoc(oContext).toString();
				var oEl = new ITHit.XMLDoc();
				oEl.load(sEl);
				oContext = oEl._get();
				
				// Set namespace resolver.
				var oNsPairs = oResolver.getAll();
				var aNs = [];
				for (var sAlias in oNsPairs) {
					aNs.push("xmlns:"+ sAlias +"='"+ oNsPairs[sAlias] +"'");
				}
				oContext.setProperty("SelectionNamespaces", aNs.join(' '));
				
				// Get document element.
				oContext = oContext.documentElement;
				
				// Return result set.
				if (!_bSelectSingle) {
					
					// Return result.
					if (!oResult) {
						return new ITHit.XPath.result(oContext.selectNodes(sExpression));
						
					// Reuse result object.
					} else {
						oResult._res = oContext.selectNodes(sExpression);
						return;
					}
					
				// Return single result.
				} else {
					
					// Search single element.
					var mOut = oContext.selectSingleNode(sExpression);
					
					if (mOut) {
						return new ITHit.XMLDoc(mOut);
					} else {
						return mOut;
					}
				}
				
			// Throw exception otherwise.
			} else {
				throw new ITHit.Exceptions.XPathException('Evaluation failed for searching "'+ sExpression +'".', e);
			}
		}
	}
	
	throw new ITHit.Exceptions.XPathException('XPath support is not implemented for your browser.');
}

/*
 * Find single (first) node.
 * @function {ITHit.XMLDoc} ITHit.XPath.selectSingleNode
 * 
 * @param {String} sExpression String expression for search.
 * @param {ITHit.XMLDoc} oXmlDom XML DOM object.
 * @param {optional ITHit.XPath.resolver} mResolver Namespace resolver object or null if namespace is not specified for search.
 */
ITHit.XPath.selectSingleNode = function(sExpression, oXmlDom, oResolver) {
	return ITHit.XPath.evaluate(sExpression, oXmlDom, oResolver, false, true);
}

/*
 * Class for creating namespace resolver for XPath.
 * @class ITHit.XPath.resolver
 */
/*
 * Create new instance of resolver class.
 * @constructor resolver
 */
ITHit.XPath.resolver = function() {
	this._ns     = {};
	this._length = 0;
}

/*
 * Add alias and namespace for it.
 * @function ITHit.XPath.resolver.add
 * 
 * @param {String} sAlias Namespace alias.
 * @param {String} sNs Namespace
 */
ITHit.XPath.resolver.prototype.add = function(sAlias, sNs) {
	this._ns[sAlias] = sNs;
	this._length++;
}

/*
 * Removes alias and namespace corresponding to it.
 * @function ITHit.XPath.resolver.remove
 * 
 * @param {String} sAlias Alias for namespace to delete.
 */
ITHit.XPath.resolver.prototype.remove = function(sAlias) {
	delete this._ns[sAlias];
	this._length--;
}

/*
 * Get namespace corresponding to passed alias.
 * @function {String} ITHit.XPath.resolver.get
 *  
 * @param {String} sAlias Alias for namespace.
 * 
 * @returns Corresponding to alias namespace.
 */
ITHit.XPath.resolver.prototype.get = function(sAlias) {
	return this._ns[sAlias] || null;
}

/*
 * Alias for get method.
 * @function {String} ITHit.XPath.resolver.lookupNamespaceURI
 * 
 * @see ITHit.XPath.resolver.get
 */
ITHit.XPath.resolver.prototype.lookupNamespaceURI = ITHit.XPath.resolver.prototype.get;

/*
 * Get list of all namespaces.
 * @function {Object} ITHit.XPath.resolver.getAll
 * 
 * @returns List of aliases as keys and corresponding to it namespaces.
 */
ITHit.XPath.resolver.prototype.getAll = function() {
	
	var oOut = {};
	for (var sAlias in this._ns) {
		oOut[sAlias] = this._ns[sAlias];
	}
	
	return oOut;
}

/*
 * Get count of setted namespaces.
 * @function {Integer} ITHit.XPath.resolver.length
 * 
 * @returns Count of added alias and namespace pairs.
 */
ITHit.XPath.resolver.prototype.length = function() {
	return this._length;
}

/*
 * Class representing result of XPath query.
 * @class ITHit.XPath.resolver.result
 */
/*
 * Create new instance of result class.
 * @constructor result
 * 
 * @param {Object} oResult XPath result object.
 */
ITHit.XPath.result = function(oResult) {
	this._res = oResult;
	this._i   = 0;
	this.length = oResult.length ? oResult.length : oResult.snapshotLength;
}

/*
 * Constants representing result type.
 */
ITHit.XPath.result.ANY_TYPE                     = 0;
ITHit.XPath.result.NUMBER_TYPE                  = 1;
ITHit.XPath.result.STRING_TYPE                  = 2;
ITHit.XPath.result.BOOLEAN_TYPE                 = 3;
ITHit.XPath.result.UNORDERED_NODE_ITERATOR_TYPE = 4;
ITHit.XPath.result.ORDERED_NODE_ITERATOR_TYPE   = 5;
ITHit.XPath.result.UNORDERED_NODE_SNAPSHOT_TYPE = 6;
ITHit.XPath.result.ORDERED_NODE_SNAPSHOT_TYPE   = 7;
ITHit.XPath.result.ANY_UNORDERED_NODE_TYPE      = 8;
ITHit.XPath.result.FIRST_ORDERED_NODE_TYPE      = 9;

/*
 * Iterate through founded nodes.
 * @function ITHit.XPath.resolver.iterateNext
 * 
 * @returns Result value.
 */
ITHit.XPath.result.prototype.iterateNext = function(iIndex) {
	
	var mOut;
	if (!iIndex) {
		if (!this._res.snapshotItem) {
			try {
				mOut = this._res[this._i++];
			} catch(e) {
				return null;
			}
		} else {
			mOut = this._res.snapshotItem(this._i++);
		}
	} else {
		mOut = this._res[iIndex];
	}
	
	if (mOut) {
		return new ITHit.XMLDoc(mOut);
	} else {
		return mOut;
	}
}

/*
 * Iterate through founded nodes.
 * @function ITHit.XPath.resolver.snapshotItem
 * 
 * @param {Integer} iIndex for result node for snapshot result type.
 * 
 * @returns Result value.
 */
ITHit.XPath.result.prototype.snapshotItem = ITHit.XPath.result.prototype.iterateNext;

/*
 * Return type for result.
 * @function {Integer} ITHit.XPath.resolver.type
 * 
 * @returns Result type.
 */
ITHit.XPath.result.prototype.type = function() {
	return this._res.resultType;
}

ITHit.XPath.result.prototype._get = function() {
	return this._res;
}


ITHit.oNS = ITHit.Declare('ITHit.Exceptions');

/*
 * Exception for XMLDoc class.
 * @class ITHit.Exceptions.XMLDocException
 * @extends ITHit.Exception
 */
/*
 * Initializes a new instance of the XMLDocException class with a specified error message and a reference to the inner exception that is the cause of this exception.
 * @constructor XMLDocException
 * 
 * @param {String} sMessage Variable name.
 * @param {optional ITHit.Exception} oInnerException The ITHit.Exception instance that caused the current exception.
 */
ITHit.oNS.XMLDocException = function(sMessage, oInnerException) {
	
	// Inheritance definition.
	ITHit.Exceptions.XMLDocException.baseConstructor.call(this, sMessage, oInnerException);
}

// Extend class.
ITHit.Extend(ITHit.oNS.XMLDocException, ITHit.Exception);

// Exception name.
ITHit.oNS.XMLDocException.prototype.Name = 'XMLDocException';

/*
 * Class for manipulating XML document.
 * @class ITHit.XMLDoc
 */
ITHit.XMLDoc = (function() {
	
	/*
	 * Holds AxtiveX Object GUID.
	 * @property {private String} ITHit.XMLDoc._actXObj
	 */
	var _actXObj;
	
	// Node types
	var NODE_ELEMENT = 1;
	var NODE_ATTRIBUTE = 2;
	var NODE_TEXT = 3;
	var NODE_CDATA_SECTION = 4;
	var NODE_ENTITY_REFERENCE = 5;
	var NODE_ENTITY = 6;
	var NODE_PROCESSING_INSTRUCTION = 7;
	var NODE_COMMENT = 8;
	var NODE_DOCUMENT = 9;
	var NODE_DOCUMENT_TYPE = 10;
	var NODE_DOCUMENT_FRAGMENT = 11;
	var NODE_NOTATION = 12;
	
	/*
	 * Creates class for working with XML documents.
	 * @constructor XMLDoc
	 * 
	 * @param {optional Object} oDomElem XML DOM element.
	 */
	var XMLDoc = function(oDomElem) {
		
		this._xml      = null;
		this._encoding = null;
		
		// Whether oDomElem is not null.
		if (null !== oDomElem) {
			
		// If oXmlObj is not passed then create XML document.
		if ( !oDomElem || ('object' != typeof oDomElem) ) {
			
			// Create document in IE.
		    if (undefined !== window.ActiveXObject) {
			
				// Whether activeX object is already have been initialized.
				if (_actXObj) {
					
					// Create instance of the same guid.
					this._xml = new window.ActiveXObject(_actXObj);
					
				// First initialization.
				} else {
					
					// Array of GUIDs for creating XML DOM document.
					var aComponents = ['Msxml2.DOMDocument.6.0', 'Msxml2.DOMDocument.4.0', 'Msxml2.DOMDocument.3.0'];
					var aVers       = [6, 4, 3]
					for (var i = 0; i < aComponents.length; i++) {
						try {
							this._xml = new window.ActiveXObject(aComponents[i]);
							
							// Save component's version.
							XMLDoc._version = aVers[i];
							
							// Save curent GUID for future instances.
							_actXObj = aComponents[i];
							
							break;
						} catch(e) {
							if (3 == aVers[i]) {
								throw new ITHit.Exception('XML component is not supported.');
							}
						}
					}
				}
			
			// Create XML document in Gecko based brousers.
			} else if (document.implementation && document.implementation.createDocument) {
				this._xml = document.implementation.createDocument('', '', null);
			}
			
			// Check whether XML document is not created.
			if (undefined === this._xml) {
				throw new ITHit.Exceptions.XMLDocException('XML support for current browser is not implemented.');
			}
			
			// Set XML document load to asyncronous mode.
			this._xml.async = false;
		
		// Assign passed XML element.
		} else {
			this._xml = oDomElem;
		}
			
		} else {
			this._xml = null;
			return null;
		}
	};
	
	/*
	 * Holds version of XML DOM GUID.
	 * @param {Inreger} ITHit.XMLDoc._version
	 */
	XMLDoc._version = 0;
	
	XMLDoc.prototype.contentEncoding = function(sEncoding) {
		if (undefined !== sEncoding) {
			this._encoding = sEncoding;
		}
		
		return this._encoding;
	}
	
	/*
	 * Load XML structure from string to specified object.
	 *  Previous structure will be deleted.
	 * @function ITHit.XMLDoc.load
	 * 
	 * @param {String} sXmlText XML string structure.
	 * 
	 * @throws ITHit.Exceptions.XMLDocException &nbsp;Srting was expected as parameter for method or wrong xml structure.
	 */
	XMLDoc.prototype.load = function(sXmlText) {
		
		if ( !ITHit.Utils.IsString(sXmlText) ) {
			throw new ITHit.Exceptions.XMLDocException('String was expected for xml parsing.');
		}
		
		if (!sXmlText) {
			return new XMLDoc();
		}
		
		var oDoc;
		
		// MSIE.
		if (undefined !== window.ActiveXObject) {
			try {
				
				// Replace unsupported in Msxml2.DOMDocument.3.0 namespace. 
				if (3 == XMLDoc._version) {
					sXmlText = sXmlText.replace(/(?:urn\:uuid\:c2f41010\-65b3\-11d1\-a29f\-00aa00c14882\/)/g, 'cutted');
				}
				
				if (XMLDoc._version) {
					this._xml.loadXML(sXmlText);
				} else {
					
					// Create new XML DOM document.
					var oXmlDoc = new XMLDoc();
					
					// Replace unsupported in Msxml2.DOMDocument.3.0 namespace.
					if (3 == XMLDoc._version) {
						sXmlText = sXmlText.replace(/(?:urn\:uuid\:c2f41010\-65b3\-11d1\-a29f\-00aa00c14882\/)/g, 'cutted');
					}
					
					// Load XML string.
					oXmlDoc.load(sXmlText);
					
					// Assign XML DOM to current object.
					this._xml = oXmlDoc._get();
				}
				
			} catch (e) {
				var oError = e;
			}
			
		// Mozilla and Netscape browsers.
		} else if (document.implementation.createDocument) {
			try {
				var oParser = new DOMParser();
				oDoc = oParser.parseFromString(sXmlText, "text/xml");
				this._xml = oDoc;
			} catch (e) {
				var oError = e;
			}
			
		} else {
			throw new ITHit.Exceptions.XMLDocException('Cannot create XML parser object. Support for current browser is not implemented.');
		}
		
		// If error while parsing happend.
		if (undefined !== oError) {
			throw new ITHit.Exceptions.XMLDocException('ITHit.XMLDoc.load() method failed.\nPossible reason: syntax error in passed XML string.', oError);
		}
	};
	
	/*
	 * Append child element.
	 * @function ITHit.XMLDoc.appendChild
	 * 
	 * @param {Object} oNode XML Dom element witch will be assigned to the current node.
	 * 
	 * @returns ITHit.Exceptions.XMLDocException Instance of XMLDoc class was expexted as parametr.
	 */
	XMLDoc.prototype.appendChild = function(oNode) {
		
		if (!oNode instanceof ITHit.XMLDoc) {
			throw ITHit.Exceptions.XMLDocException('Instance of XMLDoc was expected in appendChild method.');
		}
		
		this._xml.appendChild(oNode._get());
	};
	
	/*
	 * Create element method.
	 * @function {ITHit.XMLDoc} ITHit.XMLDoc.createElement
	 * 
	 * @param {String} sElementName Element name.
	 * 
	 * @returns XML DOM element.
	 */
	XMLDoc.prototype.createElement = function(sElementName) {
		return new XMLDoc(this._xml.createElement(sElementName));
	};
	
	/*
	 * Create element with namespace.
	 * @function {ITHit.XMLDoc} ITHit.XMLDoc.createElementNS
	 * 
	 * @param {String} sNS The URI of the namespace.
	 * @param {String} sElementName The qualified name of the element, as prefix:tagname.
	 * 
	 * @returns XML DOM element.
	 * 
	 * @throws ITHit.Exceptions.XMLDocException &nbsp;Node is not created.
	 */
	XMLDoc.prototype.createElementNS = function(sNS, sElementName) {
		
		// For Gecko based browsers
		if (this._xml.createElementNS) {
			var oElement = this._xml.createElementNS(sNS, sElementName, '');
						
			// Return new XML DOM element
			return new ITHit.XMLDoc(oElement);
		
		// For IE
		} else {
			try {
				return new XMLDoc(this._xml.createNode(NODE_ELEMENT, sElementName, sNS));
			} catch(e) {
				throw new ITHit.Exceptions.XMLDocException('Node is not created.', e);
			}
		}
		
		throw new ITHit.Exceptions.XMLDocException('createElementNS for current browser is not implemented.');
	};
	
	/*
	 * Create text node.
	 * @function {ITHit.XMLDoc} ITHit.XMLDoc.createTextNode
	 * 
	 * @param {String} sText Text.
	 * 
	 * @returns XML DOM text element.
	 */
	XMLDoc.prototype.createTextNode = function(sText) {
		return new XMLDoc(this._xml.createTextNode(sText));
	};
	
	/*
	 * Get element by it's id.
	 * @function {ITHit.XMLDoc} ITHit.XMLDoc.getElementById
	 * 
	 * @param {String} sId ID of the element.
	 * 
	 * @returns Selected element.
	 */
	XMLDoc.prototype.getElementById = function(sId) {
		return new XMLDoc(this._xml.getElementById(sId));
	};
	
	/*
	 * Get elements by it's tag names
	 * @function {ITHit.XMLDoc[]} ITHit.XMLDoc.getElementsByTagName
	 * 
	 * @param {String} sTagName Tag name of the elements.
	 * 
	 * @returns List of XML DOM elements.
	 */
	XMLDoc.prototype.getElementsByTagName = function(sTagName) {
		return new XMLDoc(this._xml.getElementsByTagName(sTagName));
	};
	
	/*
	 * Get element's child nodes.
	 * @function {ITHit.XMLDoc[]} ITHit.XMLDoc.childNodes
	 * 
	 * @returns List of child nodes.
	 */
	XMLDoc.prototype.childNodes = function() {
		
		var oNodes    = this._xml.childNodes;
		var oRetNodes = [];
		
		for ( var i = 0; i < oNodes.length; i++ ) {
			oRetNodes.push(new ITHit.XMLDoc(oNodes[i]));
		}
		
		return oRetNodes;
	}
	
	/*
	 * Get elements by it's tag names with namespace.
	 * @function {ITHit.XMLDoc[]} ITHit.XMLDoc.getElementsByTagNameNS
	 * 
	 * @param {String} sNamespace Element's namespace.
	 * @param {String} sTagName Tag name.
	 * 
	 * @returns List of selected nodes.
	 */
	XMLDoc.prototype.getElementsByTagNameNS = function(sNamespace, sTagName) {
		
		if (this._xml.getElementsByTagNameNS) {
			var oNode = this._xml.getElementsByTagNameNS(sNamespace, sTagName);
			
		} else {
			// Recreate XML DOM document.
			var sElem = this.toString();
			var oXmlDoc = new ITHit.XMLDoc();
			oXmlDoc.load(sElem);
			
			// Add namespace resolver.
			var oResolver = new ITHit.XPath.resolver();
			oResolver.add('a', sNamespace);
			
			// Search corresponding nodes.
			var oRes = ITHit.XPath.evaluate(('//a:'+ sTagName), oXmlDoc, oResolver);
			var oNode = oRes._get();
		}
		
		var aRet = [];
		for (var i = 0; i < oNode.length; i++) {
			var oNodeI = new ITHit.XMLDoc(oNode[i]);
			aRet.push(oNodeI);
		}
		
		return aRet;
	};
	
	/*
	 * Set attribute.
	 * @function ITHit.XMLDoc.setAttribute
	 * 
	 * @paramset Syntax 1
	 * @param {String} sName Attribute's name.
	 * @param {String} mValue Atribute's value.
	 * @paramset Syntax 2
	 * @param {String} sName Attribute's name.
	 * @param {Number} mValue Atribute's value.
	 */
	XMLDoc.prototype.setAttribute = function(sName, mValue) {
		this._xml.setAttribute(sName, mValue);
	};
	
	/*
	 * Check whether attribute with specified name is present in element's attributes list.
	 * @function {Boolean} ITHit.XMLDoc.hasAttribute
	 * 
	 * @param {String} sName Attribute's name.
	 * 
	 * @returns true if present, false otherwise.
	 */
	XMLDoc.prototype.hasAttribute = function(sName) {
		
		return this._xml.hasAttribute(sName);
	};
	
	/*
	 * Get attribute represented by name.
	 * @function {String} ITHit.XMLDoc.getAttribute
	 * 
	 * @param {String} sName Attribute's name.
	 * 
	 * @returns Value of the attribute or 'undefined' if attribute is not set.
	 */
	XMLDoc.prototype.getAttribute = function(sName) {
		
		return this._xml.getAttribute(sName);
	};
	
	/*
	 * Remove specified attribute.
	 * @function ITHit.XMLDoc.removeAttribute
	 * 
	 * @param {String} sName Attribute's name.
	 */
	XMLDoc.prototype.removeAttribute = function(sName) {
		
		this._xml.removeAttribute(sName);
	};
	
	/*
	 * Check whether attribute with specified name is present in element's attributes list.
	 * @function {Boolean} ITHit.XMLDoc.hasAttributeNS
	 * 
	 * @param {String} sName Attribute's name.
	 * 
	 * @returns true if present, false otherwise.
	 */
	XMLDoc.prototype.hasAttributeNS = function(sName) {
		
		return this._xml.hasAttribute(sName);
	};
	
	/*
	 * Get attribute represented by name.
	 * @function {String} ITHit.XMLDoc.getAttributeNS
	 * 
	 * @param {String} sName Attribute's name.
	 * 
	 * @returna Value of the attribute or 'undefined' if attribute is not set.
	 */
	XMLDoc.prototype.getAttributeNS = function(sName) {
		
		return this._xml.getAttribute(sName);
	};
	
	/*
	 * Remove specified attribute.
	 * @function ITHit.XMLDoc.removeAttributeNS
	 * 
	 * @param {String} sName Attribute's name.
	 */
	XMLDoc.prototype.removeAttributeNS = function(sName) {
		this._xml.removeAttribute(sName);
	};
	
	/*
	 * Remove specified child node with all it's subnodes from current XML DOM tree.
	 * @function {ITHit.XMLDoc} ITHit.XMLDoc.removeChild
	 * 
	 * @param {ITHit.XMLDoc} oNode Node for deleting.
	 * 
	 * @returns Deleted element.
	 * 
	 * @throws ITHit.Exceptions.XMLDocException &nbsp;Instance of XMLDoc was expected as method parameter.
	 */
	XMLDoc.prototype.removeChild = function(oNode) {
		
		if (!oNode instanceof ITHit.XMLDoc) {
			throw ITHit.Exceptions.XMLDocException('Instance of XMLDoc was expected in ITHit.XMLDoc.removeChild() method.');
		}
		
		this._xml.removeChild(oNode);
		
		return new ITHit.XMLDoc(oNode);
	};
	
	/*
	 * Remove specified node with all it's subnodes from current XML DOM tree.
	 * @function {ITHit.XMLDoc} ITHit.XMLDoc.removeNode
	 * 
	 * @param {ITHit.XMLDoc} oNode Node for deleting.
	 * 
	 * @returns Deleted element.
	 * 
	 * @throws ITHit.Exceptions.XMLDocException &nbsp;Instance of XMLDoc was expected as method parameter.
	 */
	XMLDoc.prototype.removeNode = function(oNode) {
		
		if (!oNode instanceof ITHit.XMLDoc) {
			throw ITHit.Exceptions.XMLDocException('Instance of XMLDoc was expected in ITHit.XMLDoc.removeNode() method.');
		}
		
		oNode = oNode._get();
		
		if (oNode.removeNode) {
			return new XMLDoc(oNode.removeNode(true));
		} else {
			return new XMLDoc(oNode.parentNode.removeChild(oNode));
		}
	};
	
	/*
	 * Clone specified node.
	 * @function {ITHit.XMLDoc} ITHit.XMLDoc.cloneNode
	 * 
	 * @param {ITHit.XMLDoc} oNode Node for cloning.
	 * @param {Boolean} bWithChildren Whether node be cloned with all it's subnodes.
	 * 
	 * @returns Cloned element.
	 * 
	 * @throws ITHit.Exceptions.XMLDocException &nbsp;Instance of XMLDoc was expected as method parameter.
	 */
	XMLDoc.prototype.cloneNode = function(bWithChildren) {
		
		if (undefined === bWithChildren) {
			bWithChildren = true;
		}
		
		return new ITHit.XMLDoc(this._xml.cloneNode(bWithChildren));
	};
	
	/*
	 * Get specified property.
	 * @function {String} ITHit.XMLDoc.getProperty
	 * 
	 * @param {String} sPropName Property name.
	 * 
	 * @returns Property value.
	 */
	XMLDoc.prototype.getProperty = function(sPropName) {
		return this._xml[sPropName];
	};
	
	/*
	 * Set specified property.
	 * @function {String} ITHit.XMLDoc.setProperty
	 * 
	 * @param {String} sPropName Property name.
	 * @param {String, Number} mValue Property value.
	 */
	XMLDoc.prototype.setProperty = function(sPropName, mValue) {
		this._xml[sPropName] = mValue;
	};
	
	/*
	 * Get node name with nprefix if specified.
	 * @function {String} ITHit.XMLDoc.nodeName
	 * 
	 * @returns Node name.
	 */
	XMLDoc.prototype.nodeName = function() {
		return this._xml.nodeName;
	};
	
	/*
	 * Get next sibling.
	 * @function {ITHit.XMLDoc} ITHit.XMLDoc.nextSibling
	 * 
	 * @returns Next sibling.
	 */
	XMLDoc.prototype.nextSibling = function() {
		return new ITHit.XMLDoc(this._xml.nextSibling);
	};
	
	/*
	 * Get element's namespace.
	 * @function {String} ITHit.XMLDoc.namespaceURI
	 * 
	 * @returns Element's namespace.
	 */
	XMLDoc.prototype.namespaceURI = function() {
		return this._xml.namespaceURI;
	};
	
	/*
	 * Whether element has child nodes.
	 *  @function {Boolean} ITHit.XMLDoc.hasChildNodes
	 *  
	 *  @returns true if element has child nodes, false otherwise.
	 */
	XMLDoc.prototype.hasChildNodes = function() {
		return (this._xml && this._xml.hasChildNodes());
	}
	
	/*
	 * Get first child node.
	 * @function {ITHit.XMLDoc} ITHit.XMLDoc.firstChild
	 * 
	 * @returns First child node.
	 */
	XMLDoc.prototype.firstChild = function() {
		return new XMLDoc(this._xml.firstChild);
	}
	
	/*
	 * Get local name (without prefix).
	 * @function {String} ITHit.XMLDoc.localName
	 * 
	 * @returns Element's name.
	 */
	XMLDoc.prototype.localName = function() {
		return this._xml.localName || this._xml.baseName;
	};
	
	/*
	 * Get node value.
	 * @function ITHit.XMLDoc.nodeValue
	 * 
	 * @returns Node's value.
	 */
	XMLDoc.prototype.nodeValue = function() {
		
		var mValue = '';
			
		if (this._xml) {
			mValue = this._xml.nodeValue;
		}
		
		if ('object' != typeof mValue) {
			return mValue;
		} else {
			return new ITHit.XMLDoc(mValue);
		}
	};
	
	/*
	 * Get node type.
	 * @function ITHit.XMLDoc.nodeType
	 * 
	 * @return Node's type.
	 */
	XMLDoc.prototype.nodeType = function() {
		return this._xml.nodeType;
	}
	
	XMLDoc.prototype._get = function() {
		return this._xml;
	};
	
	/*
	 * Returns XML DOM document as string. 
	 * @function {String} ITHit.XMLDoc.toString
	 * 
	 * @returns String representation of XML DOM element.
	 */
	XMLDoc.prototype.toString = function(bWithoutDeclaration) {
		return XMLDoc.toString(this._xml, this._encoding, bWithoutDeclaration);
	};
	
	/*
	 * Returns XML DOM document as string. Static method.
	 * @function {static String} ITHit.XMLDoc.toString
	 * 
	 * @returns String representation of XML DOM element.
	 */
	XMLDoc.toString = function(oXmlObj, sEncoding, bWithoutDeclaration) {
		
		if (!oXmlObj) {
			throw new ITHit.Exceptions.XMLDocException('ITHit.XMLDoc: XML object expected.');
		}
		
		var sOutput = '';
		var bRaiseException = true;
		
		// Check whether IE conversion method.
		if (undefined !== oXmlObj.xml) {
			sOutput = oXmlObj.xml.replace(/^\s+|\s+$/g, '');
			bRaiseException = false;
			
		// Check for XMLSerializer support by browser.
		} else if (document.implementation.createDocument && (undefined !== XMLSerializer)) {
			sOutput = new XMLSerializer().serializeToString(oXmlObj);
			bRaiseException = false;
		}
		
		// Check for output data.
		if (sOutput) {
			
			// Whether encoding is specified.
			if (sEncoding) {
				// Add encoding data.
				sEncoding = ' encoding="'+ this._encoding +'"'
			} else {
				// Clear encoding.
				sEncoding = '';
			}
			
			// Return string XML document.
			var sOut = ( (!bWithoutDeclaration) ? '<?xml version="1.0"'+ sEncoding +'?>' : '' )+ sOutput.replace(/^<\?xml[^?]+\?>/, ''); // Replace xml declaration if is set.
			
			return sOut;
		}
		
		if (bRaiseException) {
			throw new ITHit.Exceptions.XMLDocException('XML parser object is not created.');
		}
		
		return sOutput;
	};
	
	// Return reference of the constructor for the XMLDoc class.
	return XMLDoc;
})();

/*
 * XML DOM node types
 */
ITHit.XMLDoc.nodeTypes = {
	NODE_ELEMENT: 1,
	NODE_ATTRIBUTE: 2,
	NODE_TEXT: 3,
	NODE_CDATA_SECTION: 4,
	NODE_ENTITY_REFERENCE: 5,
	NODE_ENTITY: 6,
	NODE_PROCESSING_INSTRUCTION: 7,
	NODE_COMMENT: 8,
	NODE_DOCUMENT: 9,
	NODE_DOCUMENT_TYPE: 10,
	NODE_DOCUMENT_FRAGMENT: 11,
	NODE_NOTATION: 12
};

ITHit.oNS = ITHit.Declare('ITHit.Exceptions');

/*
 * Wrong argument value.
 * @class ITHit.Exceptions.ArgumentException
 * @extends ITHit.Exception
 */
/*
 * Initializes a new instance of the ArgumentException class with a specified error message and variable name wrong value of which is caused an exception.
 * @constructor ArgumentException
 * 
 * @param {String} sMessage  The error message that explains the reason for the exception.
 * @param {String} sVariable The name of the parameter that caused the current exception.
 */
ITHit.oNS.ArgumentException = function(sMessage, sVariable) {
	
	sMessage += ' Variable name: "'+ sVariable +'"';
	
	// Inheritance definition.
	ITHit.Exceptions.ArgumentException.baseConstructor.call(this, sMessage);
}

// Extend class.
ITHit.Extend(ITHit.oNS.ArgumentException, ITHit.Exception);

// Exception name.
ITHit.oNS.ArgumentException.prototype.Name = 'ArgumentException';

;
(function () {

	/**
	 * @class ITHit.WebDAV.Client.Depth
	 */
	var self = ITHit.DefineClass('ITHit.WebDAV.Client.Depth', null, /** @lends ITHit.WebDAV.Client.Depth.prototype */{

		__static: /** @lends ITHit.WebDAV.Client.Depth */{

			/**
			 * @type {ITHit.WebDAV.Client.Depth}
			 */
			Zero: null,

			/**
			 * @type {ITHit.WebDAV.Client.Depth}
			 */
			One: null,

			/**
			 * @type {ITHit.WebDAV.Client.Depth}
			 */
			Infinity: null,

			Parse: function (sValue) {

				// Switch depth variant.
				switch (sValue.toLowerCase()) {

					// Depth 0.
					case '0':
						return ITHit.WebDAV.Client.Depth.Zero;
						break;

					// Get one level.
					case '1':
						return ITHit.WebDAV.Client.Depth.One;
						break;

					// Get all.
					case 'infinity':
						return ITHit.WebDAV.Client.Depth.Infinity;
						break;

					default:
						throw new ITHit.Exceptions.ArgumentException(ITHit.Phrases.Exceptions.InvalidDepthValue, 'sValue');

				}

			}
		},

		/**
		 *
		 * @param {string|number} mValue
		 */
		constructor: function (mValue) {
			this.Value = mValue;
		}

	});

	self.Zero = new self(0);
	self.One = new self(1);
	self.Infinity = new self('Infinity');

})();
ITHit.DefineClass('ITHit.WebDAV.Client.Methods.HttpMethod', null, /** @lends ITHit.WebDAV.Client.Methods.HttpMethod.prototype */{

	__static: /** @lends ITHit.WebDAV.Client.Methods.HttpMethod */{

		/**
		 * @param {ITHit.WebDAV.Client.Request} oRequest
		 * @param {string} sHref
		 * @param {...*} otherParam
		 * @returns {ITHit.WebDAV.Client.WebDavRequest}
		 */
		Go: function (oRequest, sHref, otherParam) {
			// Create request.
			var oWebDavRequest = this._CreateRequest.apply(this, arguments);
			var oResponse = oWebDavRequest.GetResponse();

			return this._ProcessResponse(oResponse, sHref);
		},

		/**
		 * @param {ITHit.WebDAV.Client.Request} oRequest
		 * @param {string} sHref
		 * @param {...*} otherParam
		 * @returns {ITHit.WebDAV.Client.WebDavRequest}
		 */
		GoAsync: function (oRequest, sHref, otherParam) {
			// Create request.
			var fCallback = arguments[arguments.length - 1];
			var oWebDavRequest = this._CreateRequest.apply(this, arguments);

			var that = this;
			oWebDavRequest.GetResponse(function (oAsyncResult) {
				if (oAsyncResult.IsSuccess) {
					oAsyncResult.Result = that._ProcessResponse(oAsyncResult.Result, sHref);
				}
				fCallback(oAsyncResult);
			});

			return oWebDavRequest;
		},

		/**
		 *
		 * @protected
		 * @returns {ITHit.WebDAV.Client.WebDavRequest}
		 */
		_CreateRequest: function () {
			// @todo throw not implement
		},

		_ProcessResponse: function (oResponse, sHref) {
			return new this(oResponse, sHref);
		}

	},

	/**
	 * @type {ITHit.WebDAV.Client.WebDavResponse}
	 */
	Response: null,

	/**
	 * @type {string}
	 */
	Href: null,

	/**
	 * Base class for all Http methods. Provides logging functionality.
	 * @constructs
	 * @param {ITHit.WebDAV.Client.WebDavResponse} oResponse Response object.
	 * @param {string} sHref
	 */
	constructor: function (oResponse, sHref) {
		this.Response = oResponse;
		this.Href = sHref;

		this._Init();
	},

	_Init: function () {
	}
});


ITHit.oNS = ITHit.Declare('ITHit.Exceptions');

/*
 * Wrong argument value.
 * @class ITHit.Exceptions.ArgumentNullException
 * @extends ITHit.Exception
 */
/*
 * Initializes a new instance of the ArgumentNullException class with a variable name caused an exception.
 * @constructor ArgumentNullException
 * 
 * @param {String} sMessage Variable name.
 */
ITHit.oNS.ArgumentNullException = function(sVariable) {
	
	var sMessage = 'Variable "'+ sVariable +'" nas null value.';
	
	// Inheritance definition.
	ITHit.Exceptions.ArgumentNullException.baseConstructor.call(this, sMessage);
}

// Extend class.
ITHit.Extend(ITHit.oNS.ArgumentNullException, ITHit.Exception);

// Exception name.
ITHit.oNS.ArgumentNullException.prototype.Name = 'ArgumentNullException';

/**
 * Utilities container.
 * @class ITHit.WebDAV.Client.WebDavUtil
 */
ITHit.DefineClass('ITHit.WebDAV.Client.WebDavUtil', null, {
	__static: /** @lends ITHit.WebDAV.Client.WebDavUtil */{

		/**
		 * Check for non null value.
		 * @throws ITHit.Exceptions.ArgumentNullException Argument is null.
		 */
		VerifyArgumentNotNull: function (oArgument, sArgumentName) {
			if (oArgument === null) {
				throw new ITHit.Exceptions.ArgumentNullException(sArgumentName);
			}
		},

		/**
		 * Check for non empty and non null value.
		 * @throws ITHit.Exceptions.ArgumentNullException Argument is null or empty.
		 */
		VerifyArgumentNotNullOrEmpty: function (oArgument, sArgumentName) {
			if (oArgument === null || oArgument === '') {
				throw new ITHit.Exceptions.ArgumentNullException(sArgumentName);
			}
		}

	}
});


ITHit.DefineClass('ITHit.WebDAV.Client.PropertyName', null, /** @lends ITHit.WebDAV.Client.PropertyName.prototype */{

	/**
	 * Name of the property.
	 * @api
	 * @type {string}
	 */
	Name: null,

	/**
	 * Namespace of the property.
	 * @api
	 * @type {string}
	 */
	NamespaceUri: null,

	/**
	 * Initializes new instance of PropertyName.
	 * @classdesc WebDAV property name.
	 * @constructs
	 * @param {string} sName Name of the property.
	 * @param {string} sNamespaceUri Namespace of the property.
	 * @throws ITHit.Exceptions.ArgumentNullException
	 */
	constructor: function(sName, sNamespaceUri) {

		// Check passed arguments type
		ITHit.WebDAV.Client.WebDavUtil.VerifyArgumentNotNullOrEmpty(sName, "sName");

		this.Name         = sName;
		this.NamespaceUri = sNamespaceUri;
	},

	/**
	 * Checks whether objects are equal.
	 * @param {ITHit.WebDAV.Client.PropertyName} oObj An object to compare with the PropertyName object.
	 * @returns {boolean} True if the PropertyName and oObj are both PropertyName objects, and every component
	 * of the PropertyName object matches the corresponding component of oObj; otherwise, false.
	 */
	Equals: function(oObj) {

		if (this == oObj) {
			return true;
		}

		if ( !oObj instanceof ITHit.WebDAV.Client.PropertyName ) {
			return false;
		}

		return (this.Name === oObj.Name) && (this.NamespaceUri === oObj.NamespaceUri);
	},

	/**
	 * Check whether property is standard.
	 * @returns {boolean} Whether property is standard.
	 */
	IsStandardProperty: function () {

		// Declare standard properties.
		if (!ITHit.WebDAV.Client.PropertyName.StandardNames) {
			ITHit.WebDAV.Client.PropertyName.StandardNames = [
				ITHit.WebDAV.Client.DavConstants.ResourceType,
				ITHit.WebDAV.Client.DavConstants.DisplayName,
				ITHit.WebDAV.Client.DavConstants.CreationDate,
				ITHit.WebDAV.Client.DavConstants.GetLastModified,
				ITHit.WebDAV.Client.DavConstants.GetContentLength,
				ITHit.WebDAV.Client.DavConstants.GetContentType,
				ITHit.WebDAV.Client.DavConstants.GetETag,
				ITHit.WebDAV.Client.DavConstants.IsCollection,
				ITHit.WebDAV.Client.DavConstants.IsFolder,
				ITHit.WebDAV.Client.DavConstants.IsHidden,
				ITHit.WebDAV.Client.DavConstants.SupportedLock,
				ITHit.WebDAV.Client.DavConstants.LockDiscovery,
				ITHit.WebDAV.Client.DavConstants.GetContentLanguage,
				ITHit.WebDAV.Client.DavConstants.Source,
				ITHit.WebDAV.Client.DavConstants.QuotaAvailableBytes,
				ITHit.WebDAV.Client.DavConstants.QuotaUsedBytes,
				new ITHit.WebDAV.Client.PropertyName("Win32FileAttributes", 'urn:schemas-microsoft-com:')
			];
		}

		// Search whether property is standard.
		for (var i = 0; i < ITHit.WebDAV.Client.PropertyName.StandardNames.length; i++) {
			if (this.Equals(ITHit.WebDAV.Client.PropertyName.StandardNames[i])) {
				return true;
			}
		}

		return false;
	},

	/**
	 * Check exists "DAV:" namespace
	 * @returns {boolean} Whether property is standard.
	 */
	HasDavNamespace: function () {
		return this.NamespaceUri === ITHit.WebDAV.Client.DavConstants.NamespaceUri;
	},

	/**
	 * Returns string representation of current property name.
	 * @api
	 * @returns {string} String representation of current property name.
	 */
	toString: function() {
		return this.NamespaceUri + ':' + this.Name;
	}

});



;
(function () {

	var sNamespaceUri = 'DAV:';

	/**
	 * WebDAV properties and constants enumeration
	 * @enum {ITHit.WebDAV.Client.PropertyName}
	 * @class ITHit.WebDAV.Client.DavConstants
	 */
	ITHit.DefineClass('ITHit.WebDAV.Client.DavConstants', null, {
		__static: /** @lends ITHit.WebDAV.Client.DavConstants */{

			/**
			 * WebDAV default namespace uri
			 * @readonly
			 * @type {string}
			 */
			NamespaceUri: sNamespaceUri,

			/**
			 * WebDAV property `comment`
			 * @readonly
			 * @type {ITHit.WebDAV.Client.PropertyName}
			 */
			Comment: new ITHit.WebDAV.Client.PropertyName("comment", sNamespaceUri),

			/**
			 * WebDAV property `creationdate`
			 * @readonly
			 * @type {ITHit.WebDAV.Client.PropertyName}
			 */
			CreationDate: new ITHit.WebDAV.Client.PropertyName("creationdate", sNamespaceUri),

			/**
			 * WebDAV property `creator-displayname`
			 * @readonly
			 * @type {ITHit.WebDAV.Client.PropertyName}
			 */
			CreatorDisplayName: new ITHit.WebDAV.Client.PropertyName("creator-displayname", sNamespaceUri),

			/**
			 * WebDAV property `displayname`
			 * @readonly
			 * @type {ITHit.WebDAV.Client.PropertyName}
			 */
			DisplayName: new ITHit.WebDAV.Client.PropertyName("displayname", sNamespaceUri),

			/**
			 * WebDAV property `getcontentlength`
			 * @readonly
			 * @type {ITHit.WebDAV.Client.PropertyName}
			 */
			GetContentLength: new ITHit.WebDAV.Client.PropertyName("getcontentlength", sNamespaceUri),

			/**
			 * WebDAV property `getcontenttype`
			 * @readonly
			 * @type {ITHit.WebDAV.Client.PropertyName}
			 */
			GetContentType: new ITHit.WebDAV.Client.PropertyName("getcontenttype", sNamespaceUri),

			/**
			 * WebDAV property `getetag`
			 * @readonly
			 * @type {ITHit.WebDAV.Client.PropertyName}
			 */
			GetETag: new ITHit.WebDAV.Client.PropertyName("getetag", sNamespaceUri),

			/**
			 * WebDAV property `getlastmodified`
			 * @readonly
			 * @type {ITHit.WebDAV.Client.PropertyName}
			 */
			GetLastModified: new ITHit.WebDAV.Client.PropertyName("getlastmodified", sNamespaceUri),

			/**
			 * WebDAV property `iscollection`
			 * @readonly
			 * @type {ITHit.WebDAV.Client.PropertyName}
			 */
			IsCollection: new ITHit.WebDAV.Client.PropertyName("iscollection", sNamespaceUri),

			/**
			 * WebDAV property `isFolder`
			 * @readonly
			 * @type {ITHit.WebDAV.Client.PropertyName}
			 */
			IsFolder: new ITHit.WebDAV.Client.PropertyName("isFolder", sNamespaceUri),

			/**
			 * WebDAV property `ishidden`
			 * @readonly
			 * @type {ITHit.WebDAV.Client.PropertyName}
			 */
			IsHidden: new ITHit.WebDAV.Client.PropertyName("ishidden", sNamespaceUri),

			/**
			 * WebDAV property `resourcetype`
			 * @readonly
			 * @type {ITHit.WebDAV.Client.PropertyName}
			 */
			ResourceType: new ITHit.WebDAV.Client.PropertyName("resourcetype", sNamespaceUri),

			/**
			 * WebDAV property `supportedlock`
			 * @readonly
			 * @type {ITHit.WebDAV.Client.PropertyName}
			 */
			SupportedLock: new ITHit.WebDAV.Client.PropertyName("supportedlock", sNamespaceUri),

			/**
			 * WebDAV property `lockdiscovery`
			 * @readonly
			 * @type {ITHit.WebDAV.Client.PropertyName}
			 */
			LockDiscovery: new ITHit.WebDAV.Client.PropertyName("lockdiscovery", sNamespaceUri),

			/**
			 * WebDAV property `getcontentlanguage`
			 * @readonly
			 * @type {ITHit.WebDAV.Client.PropertyName}
			 */
			GetContentLanguage: new ITHit.WebDAV.Client.PropertyName("getcontentlanguage", sNamespaceUri),

			/**
			 * WebDAV property `source`
			 * @readonly
			 * @type {ITHit.WebDAV.Client.PropertyName}
			 */
			Source: new ITHit.WebDAV.Client.PropertyName("source", sNamespaceUri),

			/**
			 * WebDAV property `quota-available-bytes`
			 * @readonly
			 * @type {ITHit.WebDAV.Client.PropertyName}
			 */
			QuotaAvailableBytes: new ITHit.WebDAV.Client.PropertyName("quota-available-bytes", sNamespaceUri),

			/**
			 * WebDAV property `quota-used-bytes`
			 * @readonly
			 * @type {ITHit.WebDAV.Client.PropertyName}
			 */
			QuotaUsedBytes: new ITHit.WebDAV.Client.PropertyName("quota-used-bytes", sNamespaceUri),

			/**
			 * WebDAV property `version-name`
			 * @readonly
			 * @type {ITHit.WebDAV.Client.PropertyName}
			 */
			VersionName: new ITHit.WebDAV.Client.PropertyName("version-name", sNamespaceUri),

			/**
			 * WebDAV property `version-history`
			 * @readonly
			 * @type {ITHit.WebDAV.Client.PropertyName}
			 */
			VersionHistory: new ITHit.WebDAV.Client.PropertyName("version-history", sNamespaceUri),

			/**
			 * WebDAV property `checked-in`
			 * @readonly
			 * @type {ITHit.WebDAV.Client.PropertyName}
			 */
			CheckedIn: new ITHit.WebDAV.Client.PropertyName("checked-in", sNamespaceUri),

			/**
			 * WebDAV property `checked-out`
			 * @readonly
			 * @type {ITHit.WebDAV.Client.PropertyName}
			 */
			CheckedOut: new ITHit.WebDAV.Client.PropertyName("checked-out", sNamespaceUri),

			/**
			 * WebDAV constant `src`
			 * @readonly
			 * @type {string}
			 */
			Src: 'src',

			/**
			 * WebDAV constant `dst`
			 * @readonly
			 * @type {string}
			 */
			Dst: 'dst',

			/**
			 * WebDAV constant `link`
			 * @readonly
			 * @type {string}
			 */
			Link: 'link',

			/**
			 * WebDAV slash constant
			 * @readonly
			 * @type {string}
			 */
			Slash: '/',

			/**
			 * WebDAV depndency failed error code
			 * @readonly
			 * @type {number}
			 */
			DepndencyFailedCode: 424,

			/**
			 * WebDAV locked error code
			 * @readonly
			 * @type {number}
			 */
			LockedCode: 423,

			/**
			 * WebDAV opaque lock token constant
			 * @readonly
			 * @type {string}
			 */
			OpaqueLockToken: 'opaquelocktoken:',

			/**
			 * WebDAV error property `quota-not-exceeded`
			 * @readonly
			 * @type {ITHit.WebDAV.Client.PropertyName}
			 */
			QuotaNotExceeded: new ITHit.WebDAV.Client.PropertyName("quota-not-exceeded", sNamespaceUri),

			/**
			 * WebDAV error property `sufficient-disk-space`
			 * @readonly
			 * @type {ITHit.WebDAV.Client.PropertyName}
			 */
			SufficientDiskSpace: new ITHit.WebDAV.Client.PropertyName("sufficient-disk-space", sNamespaceUri)

		}
	});

})();


;
(function() {

	/**
	 * Structure that describes HTTP response's status.
	 * @api
	 * @class ITHit.WebDAV.Client.HttpStatus
	 */
	ITHit.DefineClass('ITHit.WebDAV.Client.HttpStatus', null, /** @lends ITHit.WebDAV.Client.HttpStatus.prototype */{

		__static: /** @lends ITHit.WebDAV.Client.HttpStatus */{

			/**
			 * No status defined.
			 * @api
			 * @type {ITHit.WebDAV.Client.HttpStatus}
			 */
			None: null,

			/**
			 * The request requires user authentication.
			 * @api
			 * @type {ITHit.WebDAV.Client.HttpStatus}
			 */
			Unauthorized: null,

			/**
			 * The request has succeeded.
			 * @api
			 * @type {ITHit.WebDAV.Client.HttpStatus}
			 */
			OK: null,

			/**
			 * The request has been fulfilled and resulted in a new resource being created.
			 * @api
			 * @type {ITHit.WebDAV.Client.HttpStatus}
			 */
			Created: null,

			/**
			 * The server has fulfilled the request but does not need to return an entity-body, and might want to
			 * return updated meta information.
			 * @api
			 * @type {ITHit.WebDAV.Client.HttpStatus}
			 */
			NoContent: null,

			/**
			 * The server has fulfilled the partial GET request for the resource.
			 * @api
			 * @type {ITHit.WebDAV.Client.HttpStatus}
			 */
			PartialContent: null,

			/**
			 * This status code provides status for multiple independent operations.
			 * @api
			 * @type {ITHit.WebDAV.Client.HttpStatus}
			 */
			MultiStatus: null,

			/**
			 * This status code is used instead if 302 redirect. This is because 302 code is processed automatically
			 * and there is no way to process redirect to login page.
			 * @api
			 * @type {ITHit.WebDAV.Client.HttpStatus}
			 */
			Redirect: null,

			/**
			 * The request could not be understood by the server due to malformed syntax. The client SHOULD NOT repeat
			 * the request without modifications.
			 * @api
			 * @type {ITHit.WebDAV.Client.HttpStatus}
			 */
			BadRequest: null,

			/**
			 * The server has not found anything matching the Request-URI.
			 * @api
			 * @type {ITHit.WebDAV.Client.HttpStatus}
			 */
			NotFound: null,

			/**
			 * The method specified in the Request-Line is not allowed for the resource identified by the Request-URI.
			 * @api
			 * @type {ITHit.WebDAV.Client.HttpStatus}
			 */
			MethodNotAllowed: null,

			/**
			 * The precondition given in one or more of the request-header fields evaluated to false when it was tested
			 * on the server.
			 * @api
			 * @type {ITHit.WebDAV.Client.HttpStatus}
			 */
			PreconditionFailed: null,

			/**
			 * The source or destination resource of a method is locked.
			 * @api
			 * @type {ITHit.WebDAV.Client.HttpStatus}
			 */
			Locked: null,

			/**
			 * The method could not be performed on the resource because the requested action depended on another
			 * action and that action failed.
			 * @api
			 * @type {ITHit.WebDAV.Client.HttpStatus}
			 */
			DependencyFailed: null,

			/**
			 * The server understood the request, but is refusing to fulfill it.
			 * @api
			 * @type {ITHit.WebDAV.Client.HttpStatus}
			 */
			Forbidden: null,

			/**
			 * The request could not be completed due to a conflict with the current state of the resource.
			 * @api
			 * @type {ITHit.WebDAV.Client.HttpStatus}
			 */
			Conflict: null,

			/**
			 * The server does not support the functionality required to fulfill the request.
			 * @api
			 * @type {ITHit.WebDAV.Client.HttpStatus}
			 */
			NotImplemented: null,

			/**
			 * The server, while acting as a gateway or proxy, received an invalid response from the upstream
			 * server it accessed in attempting to fulfill the request.
			 * @api
			 * @type {ITHit.WebDAV.Client.HttpStatus}
			 */
			BadGateway: null,

			/**
			 * The method could not be performed on the resource because the server is unable to store the
			 * representation needed to successfully complete the request.
			 * @api
			 * @type {ITHit.WebDAV.Client.HttpStatus}
			 */
			InsufficientStorage: null,

			/**
			 * Parses HttpStatus structure from string containing status information.
			 * @api
			 * @param {string} sStatus String containing status information.
			 * @returns {ITHit.WebDAV.Client.HttpStatus} HttpStatus structure that describes current status.
			 */
			Parse: function(sStatus) {
				var aParts  = sStatus.split(' ');
				var iStatus = parseInt(aParts[1]);

				aParts.splice(0, 2);

				return new ITHit.WebDAV.Client.HttpStatus(iStatus, aParts.join(' '));
			}

		},

		/**
		 * @type {number}
		 */
		Code: null,

		/**
		 * @type {string}
		 */
		Description: null,

		/**
		 * Represents response status.
		 * Initializes a new instance of t,he HttpStatus structure with code and description specified.
		 * @param {number} iCode Code of the status.
		 * @param {string} sDescription Description of the status.
		 */
		constructor: function(iCode, sDescription) {
			this.Code = iCode;
			this.Description = sDescription;
		},

		/**
		 * Indicates whether the current HttpStatus structure is equal to another HttpStatus structure.
		 * @api
		 * @param {ITHit.WebDAV.Client.HttpStatus} oHttpStatus HttpStatus object to compare.
		 * @returns {boolean} True if the current object is equal to the other parameter; otherwise, false.
		 */
		Equals: function(oHttpStatus) {
			if ( !oHttpStatus || !(oHttpStatus instanceof ITHit.WebDAV.Client.HttpStatus) ) {
				return false;
			}

			return this.Code === oHttpStatus.Code;
		},

		/**
		 * Returns true if status is successful for Create operation.
		 * @api
		 * @returns {boolean} Returns true if status is successful for Create operation.
		 */
		IsCreateOk: function() {
			return this.Equals(ITHit.WebDAV.Client.HttpStatus.Created);
		},

		/**
		 * Returns true if status is successful for Delete operation.
		 * @api
		 * @returns {boolean} Returns true if status is successful for Delete operation.
		 */
		IsDeleteOk: function() {
			return this.Equals(ITHit.WebDAV.Client.HttpStatus.OK) || this.Equals(ITHit.WebDAV.Client.HttpStatus.NoContent);
		},

		/**
		 * Returns true if status is successful.
		 * @api
		 * @returns {boolean} Returns true if status is successful.
		 */
		IsOk: function() {
			return this.Equals(ITHit.WebDAV.Client.HttpStatus.OK);
		},

		/**
		 * Returns true if status is successful for Copy or Move operation.
		 * @api
		 * @returns {boolean} Returns true if status is successful for Copy or Move operation.
		 */
		IsCopyMoveOk: function() {
			return this.Equals(ITHit.WebDAV.Client.HttpStatus.NoContent) || this.Equals(ITHit.WebDAV.Client.HttpStatus.Created);
		},

		/**
		 * Returns true if status is successful for Get operation.
		 * @api
		 * @returns {boolean} Returns true if status is successful for Get operation.
		 */
		IsGetOk: function() {
			return this.Equals(ITHit.WebDAV.Client.HttpStatus.OK) || this.Equals(ITHit.WebDAV.Client.HttpStatus.PartialContent);
		},

		/**
		 * Returns true if status is successful for Put operation.
		 * @api
		 * @returns {boolean} Returns true if status is successful for Put operation.
		 */
		IsPutOk: function() {
			return this.Equals(ITHit.WebDAV.Client.HttpStatus.OK) || this.Equals(ITHit.WebDAV.Client.HttpStatus.Created) || this.Equals(ITHit.WebDAV.Client.HttpStatus.NoContent);
		},

		/**
		 * Returns true if status is successful for Unlock operation.
		 * @api
		 * @returns {boolean} Returns true if status is successful for Unlock operation.
		 */
		IsUnlockOk: function() {
			return this.Equals(ITHit.WebDAV.Client.HttpStatus.OK) || this.Equals(ITHit.WebDAV.Client.HttpStatus.NoContent);
		},

		/**
		 * Returns true if status is successful for Head operation.
		 * @api
		 * @returns {boolean} Returns true if status is successful for Head operation.
		 */
		IsHeadOk: function() {
			return this.Equals(ITHit.WebDAV.Client.HttpStatus.OK) || this.Equals(ITHit.WebDAV.Client.HttpStatus.NotFound);
		},


		/**
		 * Returns true if status is successful for Proppatch operation.
		 * @api
		 * @returns {boolean} Returns true if status is successful for Proppatch operation.
		 */
		IsUpdateOk: function() {
			return this.Equals(ITHit.WebDAV.Client.HttpStatus.OK) || this.Equals(ITHit.WebDAV.Client.HttpStatus.None);
		},

		/**
		 * Returns true if status is successful.
		 * @api
		 * @returns {boolean} Returns true if status is successful.
		 */
		IsSuccess: function() {
			return (parseInt(this.Code / 100) == 2);
		}

	});

})();

ITHit.WebDAV.Client.HttpStatus.None =                new ITHit.WebDAV.Client.HttpStatus(0, '');
ITHit.WebDAV.Client.HttpStatus.Unauthorized =        new ITHit.WebDAV.Client.HttpStatus(401, 'Unauthorized');
ITHit.WebDAV.Client.HttpStatus.OK =                  new ITHit.WebDAV.Client.HttpStatus(200, 'OK');
ITHit.WebDAV.Client.HttpStatus.Created =             new ITHit.WebDAV.Client.HttpStatus(201, 'Created');
ITHit.WebDAV.Client.HttpStatus.NoContent =           new ITHit.WebDAV.Client.HttpStatus(204, 'No Content');
ITHit.WebDAV.Client.HttpStatus.PartialContent =      new ITHit.WebDAV.Client.HttpStatus(206, 'Partial Content');
ITHit.WebDAV.Client.HttpStatus.MultiStatus =         new ITHit.WebDAV.Client.HttpStatus(207, 'Multi-Status');
ITHit.WebDAV.Client.HttpStatus.Redirect =            new ITHit.WebDAV.Client.HttpStatus(278, 'Redirect');
ITHit.WebDAV.Client.HttpStatus.BadRequest =          new ITHit.WebDAV.Client.HttpStatus(400, 'Bad Request');
ITHit.WebDAV.Client.HttpStatus.NotFound =            new ITHit.WebDAV.Client.HttpStatus(404, 'Not Found');
ITHit.WebDAV.Client.HttpStatus.MethodNotAllowed =    new ITHit.WebDAV.Client.HttpStatus(405, 'Method Not Allowed');
ITHit.WebDAV.Client.HttpStatus.PreconditionFailed =  new ITHit.WebDAV.Client.HttpStatus(412, 'Precondition Failed');
ITHit.WebDAV.Client.HttpStatus.Locked =              new ITHit.WebDAV.Client.HttpStatus(423, 'Locked');
ITHit.WebDAV.Client.HttpStatus.DependencyFailed =    new ITHit.WebDAV.Client.HttpStatus(424, 'Dependency Failed');
ITHit.WebDAV.Client.HttpStatus.Forbidden =           new ITHit.WebDAV.Client.HttpStatus(403, 'Forbidden');
ITHit.WebDAV.Client.HttpStatus.Conflict =            new ITHit.WebDAV.Client.HttpStatus(409, 'Conflict');
ITHit.WebDAV.Client.HttpStatus.NotImplemented =      new ITHit.WebDAV.Client.HttpStatus(501, 'Not Implemented');
ITHit.WebDAV.Client.HttpStatus.BadGateway =          new ITHit.WebDAV.Client.HttpStatus(502, 'Bad gateway');
ITHit.WebDAV.Client.HttpStatus.InsufficientStorage = new ITHit.WebDAV.Client.HttpStatus(507, 'Insufficient Storage');


ITHit.DefineClass('ITHit.WebDAV.Client.Property', null, /** @lends ITHit.WebDAV.Client.Property.prototype */{

	/**
	 * Property Name.
	 * @api
	 * @type {string|ITHit.WebDAV.Client.PropertyName}
	 */
	Name: null,

	/**
	 * Property value.
	 * @api
	 * @type {*}
	 */
	Value: null,

	/**
	 * Initializes new string valued property.
	 * @api
	 * @classdesc Represents custom property exposed by WebDAV hierarchy items.
	 * @constructs
	 * @param {string|ITHit.WebDAV.Client.PropertyName} sName Name of the property.
	 * @param {string} [sValue] Property value.
	 * @param {string} [sNamespace] Namespace of the property.
	 * @throws ITHit.Exception
	 */
	constructor: function(sName, sValue, sNamespace) {
		switch (arguments.length) {

			case 1:
				// Declare variable.
				var oElement = sName;

				// Check variable.
				ITHit.WebDAV.Client.WebDavUtil.VerifyArgumentNotNull(oElement, 'oElement');

				this.Name    = new ITHit.WebDAV.Client.PropertyName(oElement.localName(), oElement.namespaceURI());
				this.Value   = oElement;

				break;

			case 2:
				// Declare variables.
				var oName        = sName,
					sStringValue = sValue;

				// Check variables.
				ITHit.WebDAV.Client.WebDavUtil.VerifyArgumentNotNull(oName, 'oName');
				ITHit.WebDAV.Client.WebDavUtil.VerifyArgumentNotNull(sStringValue, 'sStringValue');

				this.Name   = oName;

				// Create element.
				var oXmlDoc = new ITHit.XMLDoc(),
					oElem   = oXmlDoc.createElementNS(oName.NamespaceUri, oName.Name);
				oElem.appendChild(oXmlDoc.createTextNode(sStringValue));
				this.Value  = oElem;

				break;

			case 3:
				// Declare variables.
				var sName      = sName,
					sValue     = sValue,
					sNameSpace = sNamespace;

				// Check variables.
				ITHit.WebDAV.Client.WebDavUtil.VerifyArgumentNotNullOrEmpty(sName, "sName");
				ITHit.WebDAV.Client.WebDavUtil.VerifyArgumentNotNull(sValue, "sValue");
				ITHit.WebDAV.Client.WebDavUtil.VerifyArgumentNotNullOrEmpty(sNameSpace, "sNameSpace");

				this.Name   = new ITHit.WebDAV.Client.PropertyName(sName, sNameSpace);

				// Create element.
				var oXmlDoc = new ITHit.XMLDoc(),
					oElem   = oXmlDoc.createElementNS(sNameSpace, sName);
				oElem.appendChild(oXmlDoc.createTextNode(sValue));
				this.Value  = oElem;

				break;

			default:
				throw ITHit.Exception(ITHit.Phrases.Exceptions.WrongCountPropertyInputParameters.Paste(arguments.length));
		}
	},

	/**
	 * String value of the custom property.
	 * @api
	 * @returns {string} String value of the custom property.
	 */
	StringValue: function() {
		return this.Value.firstChild().nodeValue();
	},

	/**
	 * Returns string representation of current property.
	 * @returns {string} String representation of PropertyName.
	 */
	toString: function() {
		return this.Name.toString();
	}

});


ITHit.DefineClass('ITHit.WebDAV.Client.Methods.Propstat', null, /** @lends ITHit.WebDAV.Client.Methods.Propstat.prototype */{

	PropertiesByNames: null,
	Properties: null,
	ResponseDescription: '',
	Status: '',

	/**
	 * @constructs
	 * @param oElement
	 */
	constructor: function(oElement) {

		// Declare class variables.
		this.PropertiesByNames   = {};
		this.Properties          = [];

		var oNode;

		// Create namespace resolver.
		var oResolver = new ITHit.XPath.resolver();
		oResolver.add('d', ITHit.WebDAV.Client.DavConstants.NamespaceUri);

		// Get response description.
		if ( oNode = ITHit.XPath.selectSingleNode('d:responsedescription', oElement, oResolver) ) {
			this.ResponseDescription = oNode.firstChild().nodeValue();
		}

		// Get status.
		oNode = ITHit.XPath.selectSingleNode('d:status', oElement, oResolver);
		this.Status = ITHit.WebDAV.Client.HttpStatus.Parse(oNode.firstChild().nodeValue());

		// Get properties.
		var oRes = ITHit.XPath.evaluate('d:prop/*', oElement, oResolver);
		while ( oNode = oRes.iterateNext() ) {

			var oProperty = new ITHit.WebDAV.Client.Property(oNode.cloneNode());
			var sPropName = oProperty.Name;

			if ('undefined' == typeof this.PropertiesByNames[sPropName]) {
				this.PropertiesByNames[sPropName] = oProperty;
			} else {
				var aChildNodes = oNode.childNodes();

				for ( var i = 0; i < aChildNodes.length; i++ ) {
					this.PropertiesByNames[sPropName].Value.appendChild(aChildNodes[i]);
				}
			}
			this.Properties.push(oProperty);
		}
	}

});


ITHit.DefineClass('ITHit.WebDAV.Client.Methods.Response', null, /** @lends ITHit.WebDAV.Client.Methods.Response.prototype */{

	Href: '',
	ResponseDescription: '',
	Status: '',
	Propstats: null,

	/**
	 * @constructs
	 * @param oResponseItem
	 * @param sOriginalUri
	 */
	constructor: function(oResponseItem, sOriginalUri) {

		// Declare class properties.
		this.Propstats = [];

		// Declare variables.
		var oNode;

		// Create namespace resolver.
		var oResolver = new ITHit.XPath.resolver();
		oResolver.add('d', ITHit.WebDAV.Client.DavConstants.NamespaceUri);

		// Get response href.
		this.Href = ITHit.XPath.selectSingleNode('d:href', oResponseItem, oResolver).firstChild().nodeValue();

		// Get description if specified.
		if ( oNode = ITHit.XPath.selectSingleNode('d:responsedescription', oResponseItem, oResolver) ) {
			this.ResponseDescription = oNode.firstChild().nodeValue();
		}

		// Get response status if specified.
		if ( oNode = ITHit.XPath.selectSingleNode('d:status', oResponseItem, oResolver) ) {
			this.Status = ITHit.WebDAV.Client.HttpStatus.Parse(oNode.firstChild().nodeValue());
		}

		// Get propstat.
		var oRes = ITHit.XPath.evaluate('d:propstat', oResponseItem, oResolver);
		while ( oNode = oRes.iterateNext() ) {
			this.Propstats.push(new ITHit.WebDAV.Client.Methods.Propstat(oNode.cloneNode()));
		}
	}

});


ITHit.DefineClass('ITHit.WebDAV.Client.Methods.MultiResponse', null, /** @lends ITHit.WebDAV.Client.Methods.MultiResponse.prototype */{

	ResponseDescription: '',
	Responses: null,

	/**
	 *
	 * @param oXmlDoc
	 * @param sOriginalUri
	 * @constructs
	 */
	constructor: function(oXmlDoc, sOriginalUri) {

		// Declare properties.
		this.ResponseDescription = '';
		this.Responses = [];

		// Create namespace resolver.
		var oResolver = new ITHit.XPath.resolver();
		oResolver.add('d', ITHit.WebDAV.Client.DavConstants.NamespaceUri);

		// Select nodes.
		var oRes = ITHit.XPath.evaluate('/d:multistatus/d:response', oXmlDoc, oResolver);

		// Loop through selected nodes.
		var oNode;
		while( (oNode = oRes.iterateNext())) {
			this.Responses.push(new ITHit.WebDAV.Client.Methods.Response(oNode.cloneNode(), sOriginalUri));
		}

		// Get response description if specified.
		ITHit.XPath.evaluate('/d:multistatus/d:responsedescription', oXmlDoc, oResolver, oRes);

		if ( (oNode = oRes.iterateNext()) ) {
			this.ResponseDescription = oNode.firstChild().nodeValue();
		}
	}

});


/**
 * Instance of this class is passed to callback function. It provides information about success or failure of
 * the operation as well as you will use it to get the results of the asynchronous call.
 * @api
 * @class ITHit.WebDAV.Client.AsyncResult
 */
ITHit.DefineClass('ITHit.WebDAV.Client.AsyncResult', null, /** @lends ITHit.WebDAV.Client.AsyncResult.prototype */{

	/**
	 * Result value. Can be any type, each method may put there appropriate object which before was returned directly.
	 * Null if request was unsuccessful.
	 * @api
	 * @type {*}
	 */
	Result: null,

	/**
	 * Flag of either async request result was successful or not.
	 * @api
	 * @type {boolean}
	 */
	IsSuccess: null,

	/**
	 * Error (Exception) object. Describes the type of error that occurred. Null if request was successful.
	 * @api
	 * @type {ITHit.WebDAV.Client.Exceptions.WebDavException|Error|null}
	 */
	Error: null,

	/**
	 *
	 * @param {*} oResult
	 * @param {boolean} bSuccess
	 * @param {ITHit.WebDAV.Client.Exceptions.WebDavException|Error|null} oError
	 */
	constructor: function(oResult, bSuccess, oError) {
		this.Result = oResult;
		this.IsSuccess = bSuccess;
		this.Error = oError;
	}

});


/**
 * Method to perform Propfind request to a server.
 * Create new instance of Propfind class.
 * @class ITHit.WebDAV.Client.Methods.Propfind
 * @extends ITHit.WebDAV.Client.Methods.HttpMethod
 */
ITHit.DefineClass('ITHit.WebDAV.Client.Methods.Propfind', ITHit.WebDAV.Client.Methods.HttpMethod, /** @lends ITHit.WebDAV.Client.Methods.Propfind.prototype */{

	__static: /** @lends ITHit.WebDAV.Client.Methods.Propfind */{

		/**
		 * Propfind modes.
		 */
		PropfindMode: {
			SelectedProperties: 'SelectedProperties',
			PropertyNames: 'PropertyNames'
		},

		Go: function (oRequest, sUri, iMode, aProperties, oDepth, sHost) {
			return this.GoAsync(oRequest, sUri, iMode, aProperties, oDepth, sHost);
		},

		GoAsync: function (oRequest, sUri, iMode, aProperties, oDepth, sHost, fCallback) {

			// Create request object.
			var oWebDavRequest = ITHit.WebDAV.Client.Methods.Propfind.createRequest(oRequest, sUri, iMode, aProperties, oDepth, sHost);

			var self = this;
			var fOnResponse = typeof fCallback === 'function'
				? function (oResult) {
				self._GoCallback(oRequest, sUri, oResult, fCallback)
			}
				: null;

			// Make request.
			var oResponse = oWebDavRequest.GetResponse(fOnResponse);

			if (typeof fCallback !== 'function') {
				var oResult = new ITHit.WebDAV.Client.AsyncResult(oResponse, oResponse != null, null);
				return this._GoCallback(oRequest, sUri, oResult, fCallback);
			} else {
				return oWebDavRequest;
			}
		},

		_GoCallback: function (oRequest, sUri, oResult, fCallback) {

			var oResponse = oResult;
			var bSuccess = true;
			var oError = null;

			if (oResult instanceof ITHit.WebDAV.Client.AsyncResult) {
				oResponse = oResult.Result;
				bSuccess = oResult.IsSuccess;
				oError = oResult.Error;
			}

			var oPropfind = null;
			if (bSuccess) {
				// Receive data.
				var oResponseData = oResponse.GetResponseStream();

				var oMultiResponse = new ITHit.WebDAV.Client.Methods.MultiResponse(oResponseData, sUri);

				oPropfind = new ITHit.WebDAV.Client.Methods.Propfind(oMultiResponse);
			}
			// Return response.
			if (typeof fCallback === 'function') {
				var oPropfindResult = new ITHit.WebDAV.Client.AsyncResult(oPropfind, bSuccess, oError);
				fCallback.call(this, oPropfindResult);
			} else {
				return oPropfind;
			}
		},

		createRequest: function (oRequest, sUri, iMode, aProperties, oDepth, sHost) {

			// Create request object.
			var oWebDavRequest = oRequest.CreateWebDavRequest(sHost, sUri);

			// Set method.
			oWebDavRequest.Method('PROPFIND');

			// Add headers.
			oWebDavRequest.Headers.Add('Depth', oDepth.Value);
			oWebDavRequest.Headers.Add('Content-Type', 'text/xml; charset="utf-8"');

			// Create XML document.
			var oWriter = new ITHit.XMLDoc();

			// Create root element.
			var propfind = oWriter.createElementNS(ITHit.WebDAV.Client.DavConstants.NamespaceUri, 'propfind');

			// Switch property mode.
			switch (iMode) {

				// Namespace mode.
				case ITHit.WebDAV.Client.Methods.Propfind.PropfindMode.SelectedProperties:

					// All properties.
					if (!aProperties || !aProperties.length) {
						var propEl = oWriter.createElementNS(ITHit.WebDAV.Client.DavConstants.NamespaceUri, 'allprop');

						// Selected properties.
					} else {
						var propEl = oWriter.createElementNS(ITHit.WebDAV.Client.DavConstants.NamespaceUri, 'prop');
						for (var i = 0; i < aProperties.length; i++) {
							var prop = oWriter.createElementNS(aProperties[i].NamespaceUri, aProperties[i].Name);
							propEl.appendChild(prop);
						}
					}
					break;

				// Property mode.
				case ITHit.WebDAV.Client.Methods.Propfind.PropfindMode.PropertyNames:
					var propEl = oWriter.createElementNS(ITHit.WebDAV.Client.DavConstants.NamespaceUri, 'propname');
					break;
			}

			// Append created child nodes.
			propfind.appendChild(propEl);
			oWriter.appendChild(propfind);

			// Assign created document as body for request.
			oWebDavRequest.Body(oWriter);

			return oWebDavRequest;
		}

	}
});


ITHit.DefineClass('ITHit.WebDAV.Client.Methods.SingleResponse', null, /** @lends ITHit.WebDAV.Client.Methods.SingleResponse.prototype */{

	Status: null,
	ResponseDescription: null,

	/**
	 * Contains information about server's simple response with no XML content.
	 * Create new instance of SingleResponse class.
	 * @constructs
	 * @param {ITHit.WebDAV.Client.WebDavResponse} oResponse Response object.
	 */
	constructor: function(oResponse) {

		this.Status              = oResponse.Status;
		this.ResponseDescription = oResponse.Status.Description;
	}

});


/**
 * Factory class for different inheritors.
 * @class ITHit.WebDAV.Client.Methods.ResponseFactory
 */
ITHit.DefineClass('ITHit.WebDAV.Client.Methods.ResponseFactory', null, /** @lends ITHit.WebDAV.Client.Methods.ResponseFactory.prototype */{

	__static: /** @lends ITHit.WebDAV.Client.Methods.ResponseFactory */{

		/**
		 * Returns suitable object of IResponse's inheritor: SingleResponse or MultiResponse.
		 * @param {ITHit.WebDAV.Client.WebDavResponse} oResponse Response object.
		 * @param {string} sOriginalUri Request URI.
		 * @returns {object} SingleResponse or MultiResponse object corresponding to request.
		 */
		GetResponse: function (oResponse, sOriginalUri) {

			// Get response body.
			var oResponseData = oResponse.GetResponseStream(oResponse);

			// Check whether there is single or multi-response.
			if (!oResponseData || !oResponse.Status.Equals(ITHit.WebDAV.Client.HttpStatus.MultiStatus)) {

				// Single response.
				return new ITHit.WebDAV.Client.Methods.SingleResponse(oResponse);
			} else {

				// Multi-response.
				return new ITHit.WebDAV.Client.Methods.MultiResponse(oResponseData, sOriginalUri);
			}
		}

	}
});


/**
 * @class ITHit.WebDAV.Client.Methods.VersionControl
 * @extends ITHit.WebDAV.Client.Methods.HttpMethod
 */
ITHit.DefineClass('ITHit.WebDAV.Client.Methods.VersionControl', ITHit.WebDAV.Client.Methods.HttpMethod, /** @lends ITHit.WebDAV.Client.Methods.VersionControl.prototype */{

	__static: /** @lends ITHit.WebDAV.Client.Methods.VersionControl */{

		/**
		 *
		 * @param oRequest
		 * @param sHref
		 * @param oLockTokens
		 * @param sHost
		 * @returns {*}
		 */
		Go: function (oRequest, sHref, oLockTokens, sHost) {
			return this._super.apply(this, arguments);
		},

		/**
		 *
		 * @param oRequest
		 * @param sHref
		 * @param oLockTokens
		 * @param sHost
		 * @param fCallback
		 * @returns {*}
		 */
		GoAsync: function (oRequest, sHref, oLockTokens, sHost, fCallback) {
			return this._super.apply(this, arguments);
		},

		_CreateRequest: function (oRequest, sHref, oLockTokens, sHost) {

			// Create request.
			var oWebDavRequest = oRequest.CreateWebDavRequest(sHost, sHref, oLockTokens);

			// Set method.
			oWebDavRequest.Method('VERSION-CONTROL');

			// Return request object.
			return oWebDavRequest;
		},

		_ProcessResponse: function (oResponse, sHref) {
			// Get appropriate response object.
			var oResp = ITHit.WebDAV.Client.Methods.ResponseFactory.GetResponse(oResponse, sHref);

			return this._super(oResp);
		}

	}
});

/**
 * Enumeration of the item (Resource or Folder).
 * @api
 * @enum {string}
 * @class ITHit.WebDAV.Client.ResourceType
 */
ITHit.DefineClass('ITHit.WebDAV.Client.ResourceType', null, {
	__static: /** @lends ITHit.WebDAV.Client.ResourceType */{

		/**
		 * Item is folder.
		 * @api
		 * @readonly
		 * @type {string}
		 */
		Folder: 'Folder',

		/**
		 * Item is file.
		 * @api
		 * @readonly
		 * @type {string}
		 */
		File: 'Resource',

		Resource: 'Resource'

	}
});


/**
 * Base exception for all exceptions thrown by WebDAV client library.
 * Initializes a new instance of the WebDavException class with a specified error message.
 * @api
 * @class ITHit.WebDAV.Client.Exceptions.WebDavException
 */
ITHit.DefineClass('ITHit.WebDAV.Client.Exceptions.WebDavException', ITHit.Exception, /** @lends ITHit.WebDAV.Client.Exceptions.WebDavException.prototype */{

	/**
	 * Exception name
	 * @type {string}
	 */
	Name: 'WebDavException',

	/**
	 * @param {string} sMessage The error message string.
	 * @param {ITHit.Exception} [oInnerException] The ITHit.Exception instance that caused the current exception.
	 */
	constructor: function(sMessage, oInnerException) {
		this._super(sMessage, oInnerException);
	}

});


/**
 * Represents information about errors occurred in different elements.
 * @api
 * @class ITHit.WebDAV.Client.Multistatus
 */
ITHit.DefineClass('ITHit.WebDAV.Client.Multistatus', null, /** @lends ITHit.WebDAV.Client.Multistatus.prototype */{

    /**
     * Gets the generic description, if available.
     * @api
     * @type {string}
     */
    Description: null,

    /**
     * Array of the errors returned by server.
     * @api
     * @type {ITHit.WebDAV.Client.MultistatusResponse[]}
     */
    Responses: null

});

/**
 * Represents error occurred in one element.
 * @api
 * @class ITHit.WebDAV.Client.MultistatusResponse
 */
ITHit.DefineClass('ITHit.WebDAV.Client.MultistatusResponse', null, /** @lends ITHit.WebDAV.Client.MultistatusResponse.prototype */{

    /**
     * Request href
     * @api
     * @type {string}
     */
    Href: null,

    /**
     * Array of the errors returned by server.
     * @api
     * @type {string}
     */
    Description: null,

    /**
     * HTTP Status of the operation.
     * @api
     * @type {ITHit.WebDAV.Client.HttpStatus}
     */
    Status: null

});

ITHit.DefineClass('ITHit.WebDAV.Client.Exceptions.Info.MultistatusResponse', ITHit.WebDAV.Client.MultistatusResponse, /** @lends ITHit.WebDAV.Client.Exceptions.Info.MultistatusResponse.prototype */{

	/**
	 * Url of the item.
	 * @type {ITHit.WebDAV.Client.MultistatusResponse.Href}
	 */
	Href: null,

	/**
	 * Description of error, if available.
	 * @type {ITHit.WebDAV.Client.MultistatusResponse.Description}
	 */
	Description: null,

	/**
	 * HTTP Status of the operation.
	 * @type {ITHit.WebDAV.Client.MultistatusResponse.Status}
	 */
	Status: null,

	/**
	 * Represents error occurred in one element.
	 * @constructs
	 * @extends ITHit.WebDAV.Client.MultistatusResponse
	 */
	constructor: function(oResponse) {

		// Define object properties.
		this.Href = oResponse.Href;
		this.Description = oResponse.ResponseDescription;
		this.Status = oResponse.Status;

		// Loop through response propstats, look for first not OK status.
		for ( var i = 0; i < oResponse.Propstats.length; i++ ) {
			if (oResponse.Propstats[i] != ITHit.WebDAV.Client.HttpStatus.OK) {
				this.Status = oResponse.Propstats[i];
				break;
			}
		}
	}

});


ITHit.DefineClass('ITHit.WebDAV.Client.Exceptions.Info.Multistatus', ITHit.WebDAV.Client.Multistatus, /** @lends ITHit.WebDAV.Client.Exceptions.Info.Multistatus.prototype */{

	/**
	 * Gets the generic description, if available. 11
	 * @type {string}
	 */
	Description: '',

	/**
	 * Array of the errors returned by server.
	 * @type {ITHit.WebDAV.Client.MultistatusResponse[]}
	 */
	Responses: null,

	/**
	 * Represents information about errors occurred in different elements.
	 * @constructs
	 * @extends ITHit.WebDAV.Client.Multistatus
	 */
	constructor: function(oMultiResponse) {
		this.Responses = [];

		// Whether multistatus response object passed.
		if (oMultiResponse) {

			this.Description = oMultiResponse.ResponseDescription;

			// Loop through all received responses and add it to class' property.
			for ( var i = 0; i < oMultiResponse.Responses.length; i++ ) {
				this.Responses.push(new ITHit.WebDAV.Client.Exceptions.Info.MultistatusResponse(oMultiResponse.Responses[i]));
			}
		}
	}

});


/**
 * Is thrown whenever and erroneous http response is received. Initializes a new instance of the WebDavHttpException
 * class with a specified error message, a reference to the inner exception that is the cause of this exception,
 * href of the item, multistatus response and status of the response caused the error.
 * @api
 * @class ITHit.WebDAV.Client.Exceptions.WebDavHttpException
 * @extends ITHit.WebDAV.Client.Exceptions.WebDavException
 */
ITHit.DefineClass('ITHit.WebDAV.Client.Exceptions.WebDavHttpException', ITHit.WebDAV.Client.Exceptions.WebDavException, /** @lends ITHit.WebDAV.Client.Exceptions.WebDavHttpException.prototype */{

	/**
	 * Exception name
	 * @type {string}
	 */
	Name: 'WebDavHttpException',

	/**
	 * Multistatus Contains {@link ITHit.WebDAV.Client.Multistatus} with elements that had errors, if multistatus information was available in response.
	 * @api
	 * @type {ITHit.WebDAV.Client.Multistatus}
	 */
	Multistatus: null,

	/**
	 * Http status with wich request failed.
	 * @api
	 * @type {ITHit.WebDAV.Client.HttpStatus}
	 */
	Status: null,

	/**
	 * Uri for which request failed.
	 * @api
	 * @type {string}
	 */
	Uri: null,

	/**
	 * Error contains IError with additional info, if error information was available in response.
	 * @api
	 * @type {ITHit.WebDAV.Client.Error}
	 */
	Error: null,

	/**
	 * @param {string} sMessage The error message string.
	 * @param {string} sHref The href of an item caused the current exception.
	 * @param {ITHit.WebDAV.Client.Multistatus} oMultistatus Multistatus response containing error information.
	 * @param {ITHit.WebDAV.Client.HttpStatus} oStatus Status of response that caused error.
	 * @param {ITHit.Exception} [oInnerException] The ITHit.Exception instance that caused the current exception.
	 * @param {ITHit.WebDAV.Client.Error} [oError] Error response containing additional error information.
	 */
	constructor: function(sMessage, sHref, oMultistatus, oStatus, oInnerException, oError) {
		this._super(sMessage, oInnerException);

		this.Multistatus = oMultistatus || new ITHit.WebDAV.Client.Exceptions.Info.Multistatus();
		this.Status = oStatus;
		this.Uri = sHref;
		this.Error = oError;
	}

});


/**
 * Is raised whenever property processing was unsuccessfull. Initializes a new instance of the PropertyException
 * class with a specified error message, a reference to the inner exception that is the cause of this exception,
 * href of the item and multistatus response caused the error.
 * @api
 * @class ITHit.WebDAV.Client.Exceptions.PropertyException
 * @extends ITHit.WebDAV.Client.Exceptions.WebDavHttpException
 */
ITHit.DefineClass('ITHit.WebDAV.Client.Exceptions.PropertyException', ITHit.WebDAV.Client.Exceptions.WebDavHttpException, /** @lends ITHit.WebDAV.Client.Exceptions.PropertyException.prototype */{

	/**
	 * Exception name
	 * @type {string}
	 */
	Name: 'PropertyException',

	/**
	 * Name of the property processing of which caused the exception.
	 * @api
	 * @type {ITHit.WebDAV.Client.PropertyName}
	 */
	PropertyName: null,

	/**
	 * @param {string} sMessage The error message string.
	 * @param {string} sHref The href of an item caused the current exception.
	 * @param {ITHit.WebDAV.Client.PropertyName} oPropertyName Name of the property processing of which caused the exception.
	 * @param {ITHit.WebDAV.Client.Multistatus} oMultistatus Multistatus response containing error information.
	 * @param {ITHit.WebDAV.Client.HttpStatus} oStatus Status of response that caused error.
	 * @param {ITHit.Exception} [oInnerException] The ITHit.Exception instance that caused the current exception.
	 */
	constructor: function(sMessage, sHref, oPropertyName, oMultistatus, oStatus, oInnerException) {
		this.PropertyName = oPropertyName;
		this._super(sMessage, sHref, oMultistatus, oStatus, oInnerException);
	}

});


/**
 * Thrown when server responded with Property Not Found http response. Initializes a new instance of the
 * PropertyNotFoundException class with a specified error message, a reference to the inner exception that
 * is the cause of this exception, href of the item and multistatus response caused the error.
 * @api
 * @class ITHit.WebDAV.Client.Exceptions.PropertyNotFoundException
 * @extends ITHit.WebDAV.Client.Exceptions.PropertyException
 */
ITHit.DefineClass('ITHit.WebDAV.Client.Exceptions.PropertyNotFoundException', ITHit.WebDAV.Client.Exceptions.PropertyException, /** @lends ITHit.WebDAV.Client.Exceptions.PropertyNotFoundException.prototype */{

	/**
	 * Exception name
	 * @type {string}
	 */
	Name: 'PropertyForbiddenException',

	/**
	 * @param {string} sMessage The error message string.
	 * @param {string} sHref The href of an item caused the current exception.
	 * @param {ITHit.WebDAV.Client.PropertyName} oPropertyName Name of the property processing of which caused the exception.
	 * @param {ITHit.WebDAV.Client.Multistatus} oMultistatus Multistatus response containing error information.
	 * @param {ITHit.Exception} [oInnerException] The ITHit.Exception instance that caused the current exception.
	 */
	constructor: function(sMessage, sHref, oPropertyName, oMultistatus, oInnerException) {
		this._super(sMessage, sHref, oPropertyName, oMultistatus, ITHit.WebDAV.Client.HttpStatus.NotFound, oInnerException);
	}

});


/**
 * Thrown when server responded with Property forbidden http response. Initializes a new instance of the
 * PropertyForbiddenException class with a specified error message, a reference to the inner exception
 * that is the cause of this exception, href of the item and multistatus response caused the error.
 * @api
 * @class ITHit.WebDAV.Client.Exceptions.PropertyForbiddenException
 * @extends ITHit.WebDAV.Client.Exceptions.PropertyException
 */
ITHit.DefineClass('ITHit.WebDAV.Client.Exceptions.PropertyForbiddenException', ITHit.WebDAV.Client.Exceptions.PropertyException, /** @lends ITHit.WebDAV.Client.Exceptions.PropertyForbiddenException.prototype */{

	/**
	 * Exception name
	 * @type {string}
	 */
	Name: 'PropertyForbiddenException',

	/**
	 * @param {string} sMessage The error message string.
	 * @param {string} sHref The href of an item caused the current exception.
	 * @param {ITHit.WebDAV.Client.PropertyName} oPropertyName Name of the property processing of which caused the exception.
	 * @param {ITHit.WebDAV.Client.Multistatus} oMultistatus Multistatus response containing error information.
	 * @param {ITHit.Exception} [oInnerException] The ITHit.Exception instance that caused the current exception.
	 */
	constructor: function(sMessage, sHref, oPropertyName, oMultistatus, oInnerException) {
		this._super(sMessage, sHref, oPropertyName, oMultistatus, ITHit.WebDAV.Client.HttpStatus.Forbidden, oInnerException);
	}

});


/**
 * Provides means for finding which properties failed to update.
 * @api
 * @class ITHit.WebDAV.Client.PropertyMultistatusResponse
 * @extends ITHit.WebDAV.Client.MultistatusResponse
 */
ITHit.DefineClass('ITHit.WebDAV.Client.PropertyMultistatusResponse', ITHit.WebDAV.Client.MultistatusResponse, /** @lends ITHit.WebDAV.Client.PropertyMultistatusResponse.prototype */{

	/**
	 * Name of the property, if element is property. Otherwise null.
	 * @api
	 * @type {ITHit.WebDAV.Client.PropertyName}
	 */
	PropertyName: null

});

ITHit.DefineClass('ITHit.WebDAV.Client.Exceptions.Info.PropertyMultistatusResponse', ITHit.WebDAV.Client.PropertyMultistatusResponse, /** @lends ITHit.WebDAV.Client.Exceptions.Info.PropertyMultistatusResponse.prototype */{

	/**
	 * Url of the item.
	 * @type {string}
	 */
	Href: null,

	/**
	 * Description of error, if available.
	 * @type {string}
	 */
	Description: null,

	/**
	 * HTTP Status of the operation.
	 * @type {ITHit.WebDAV.Client.HttpStatus}
	 */
	Status: null,

	/**
	 * Name of the property, if element is property. Otherwise null.
	 * @type {ITHit.WebDAV.Client.PropertyMultistatusResponse}
	 */
	PropertyName: null,

	/**
	 * Provides means for finding which properties failed to update.
	 * @constructs
	 * @extends ITHit.WebDAV.Client.PropertyMultistatusResponse
	 */
	constructor: function(sHref, sDescription, oStatus, oPropertyName) {
		this._super();

		this.Href = sHref;
		this.Description = sDescription;
		this.Status = oStatus;
		this.PropertyName = oPropertyName;
	}

});



ITHit.DefineClass('ITHit.WebDAV.Client.Exceptions.Info.PropertyMultistatus', ITHit.WebDAV.Client.Multistatus, /** @lends ITHit.WebDAV.Client.Exceptions.Info.PropertyMultistatus.prototype */{

	/**
	 * Gets the generic description, if available.
	 * @type {string}
	 */
	Description: '',

	/**
	 * Array of the errors returned by server.
	 * @type {ITHit.WebDAV.Client.MultistatusResponse[]}
	 */
	Responses: null,

	/**
	 * Provides means for finding which properties failed to update.
	 * Create new instance of PropertyMultistatus class.
	 * @param {ITHit.WebDAV.Client.Exceptions.Info.MultiResponse} [oMultiResponse] MultiResponse object.
	 * @constructs
	 * @extends ITHit.WebDAV.Client.Multistatus
	 */
	constructor: function(oMultiResponse) {
		this.Responses = [];

		if (oMultiResponse) {
			this.Description = oMultiResponse.ResponseDescription;

			for ( var i = 0; i < oMultiResponse.Responses.length; i++ ) {
				var oResponse = oMultiResponse.Responses[i];
				for ( var j = 0; j < oResponse.Propstats.length; j++ ) {
					var oPropstat = oResponse.Propstats[j];
					for ( var k = 0; k < oPropstat.Properties.length; k++ ) {
						this.Responses.push(new ITHit.WebDAV.Client.Exceptions.Info.PropertyMultistatusResponse(oResponse.Href, oPropstat.ResponseDescription, oPropstat.Status, oPropstat.Properties[k].Name));
					}
				}
			}
		}
	}

});

/**
 * Provides functionality for encoding paths and URLs.
 * @class ITHit.WebDAV.Client.Encoder
 */
ITHit.DefineClass('ITHit.WebDAV.Client.Encoder', null, {
	__static: /** @lends ITHit.WebDAV.Client.Encoder */{

		/**
		 * Encodes path presented by string.
		 * @param {string} sText Path to encode.
		 * @returns {string} Encoded path.
		 */
		Encode: ITHit.Encode,

		/**
		 * Decodes path presented by string.
		 * @param {string} sText Path to decode.
		 * @returns {string} Decoded path.
		 */
		Decode: ITHit.Decode,

		EncodeURI: ITHit.EncodeURI,

		DecodeURI: ITHit.DecodeURI

	}
});


/**
 * Method to perform Copy or Move request to a server.
 * @class ITHit.WebDAV.Client.Methods.CopyMove
 * @extends ITHit.WebDAV.Client.Methods.HttpMethod
 */
ITHit.DefineClass('ITHit.WebDAV.Client.Methods.CopyMove', ITHit.WebDAV.Client.Methods.HttpMethod, /** @lends ITHit.WebDAV.Client.Methods.CopyMove.prototype */{

	__static: /** @lends ITHit.WebDAV.Client.Methods.CopyMove */{

		/**
		 * Modes for CopyMove methods.
		 */
		Mode: {
			Copy: 'Copy',
			Move: 'Move'
		},


		Go: function (oRequest, sMode, sSource, sDestination, bIsCollection, bDeep, bOverwrite, aLockTokens, sHost) {
			// Create request.
			var oWebDavRequest = this.createRequest(oRequest, sMode, sSource, sDestination, bIsCollection, bDeep, bOverwrite, aLockTokens, sHost);
			var oResponse = oWebDavRequest.GetResponse();
			return this._ProcessResponse(oResponse, sSource);
		},

		GoAsync: function (oRequest, sMode, sSource, sDestination, bIsCollection, bDeep, bOverwrite, aLockTokens, sHost, fCallback) {
			// Create request.
			var oWebDavRequest = this.createRequest(oRequest, sMode, sSource, sDestination, bIsCollection, bDeep, bOverwrite, aLockTokens, sHost);

			var that = this;
			oWebDavRequest.GetResponse(function (oAsyncResult) {
				if (!oAsyncResult.IsSuccess) {
					fCallback(new ITHit.WebDAV.Client.AsyncResult(null, false, oAsyncResult.Error));
					return;
				}

				var oResult = that._ProcessResponse(oAsyncResult.Result, sSource);
				fCallback(new ITHit.WebDAV.Client.AsyncResult(oResult, true, null));
			});

			return oWebDavRequest;
		},

		_ProcessResponse: function (oResponse, sSource) {
			// Get appropriate response object.
			var oResp = ITHit.WebDAV.Client.Methods.ResponseFactory.GetResponse(oResponse, sSource);

			// Return result.
			return new ITHit.WebDAV.Client.Methods.CopyMove(oResp);
		},

		createRequest: function (oRequest, sMode, sSource, sDestination, bIsCollection, bDeep, bOverwrite, aLockTokens, sHost) {

			// Create request.
			var oWebDavRequest = oRequest.CreateWebDavRequest(sHost, sSource, aLockTokens);

			// TODO: Remove after when encoding special characters on server will be fixed.
			sDestination = ITHit.WebDAV.Client.Encoder.EncodeURI(sDestination).replace(/#/g, '%23').replace(/'/g, '%27');

			if (/^\//.test(sDestination)) {
				sDestination = sHost + sDestination.substr(1);
			}

			// Add headers
			oWebDavRequest.Method((sMode == ITHit.WebDAV.Client.Methods.CopyMove.Mode.Copy) ? 'COPY' : 'MOVE');
			oWebDavRequest.Headers.Add('Content-Type', 'text/xml; charset="utf-8"');
			oWebDavRequest.Headers.Add('Destination', ITHit.DecodeHost(sDestination));
			oWebDavRequest.Headers.Add('Overwrite', bOverwrite ? "T" : "F");

			// Set depth property if specified by input parameters.
			if (bIsCollection && (sMode == ITHit.WebDAV.Client.Methods.CopyMove.Mode.Copy)) {
				// Built-in IIS 8.0 WebDAV does not support Depth: Infinity
				if (!bDeep) {
					oWebDavRequest.Headers.Add("Depth", ITHit.WebDAV.Client.Depth.Zero.Value);
				}
			}

			// Create XML DOM document.
			var oWriter = new ITHit.XMLDoc();

			// Create XML document.
			var propBehav = oWriter.createElementNS(ITHit.WebDAV.Client.DavConstants.NamespaceUri, 'propertybehavior');
			var keepAlive = oWriter.createElementNS(ITHit.WebDAV.Client.DavConstants.NamespaceUri, 'keepalive')
			keepAlive.appendChild(oWriter.createTextNode('*'));
			propBehav.appendChild(keepAlive);
			oWriter.appendChild(propBehav);

			// Add XML document as request body.
			oWebDavRequest.Body(oWriter);

			// Return request object.
			return oWebDavRequest;
		}

	}
});


/**
 * @class ITHit.WebDAV.Client.Methods.Delete
 * @extends ITHit.WebDAV.Client.Methods.HttpMethod
 */
ITHit.DefineClass('ITHit.WebDAV.Client.Methods.Delete', ITHit.WebDAV.Client.Methods.HttpMethod, /** @lends ITHit.WebDAV.Client.Methods.Delete.prototype */{

	__static: /** @lends ITHit.WebDAV.Client.Methods.Delete */{

		/**
		 *
		 * @param oRequest
		 * @param sHref
		 * @param oLockTokens
		 * @param sHost
		 * @returns {*}
		 */
		Go: function (oRequest, sHref, oLockTokens, sHost) {
			return this._super.apply(this, arguments);
		},

		/**
		 *
		 * @param oRequest
		 * @param sHref
		 * @param oLockTokens
		 * @param sHost
		 * @param fCallback
		 * @returns {*}
		 */
		GoAsync: function (oRequest, sHref, oLockTokens, sHost, fCallback) {
			return this._super.apply(this, arguments);
		},

		_CreateRequest: function (oRequest, sHref, oLockTokens, sHost) {

			// Create request.
			var oWebDavRequest = oRequest.CreateWebDavRequest(sHost, sHref, oLockTokens);

			// Set method.
			oWebDavRequest.Method('DELETE');

			// Return request object.
			return oWebDavRequest;
		},

		_ProcessResponse: function (oResponse, sHref) {
			// Get appropriate response object.
			var oResp = ITHit.WebDAV.Client.Methods.ResponseFactory.GetResponse(oResponse, sHref);

			return this._super(oResp);
		}

	}
});


/**
 * @class ITHit.WebDAV.Client.Methods.Proppatch
 * @extends ITHit.WebDAV.Client.Methods.HttpMethod
 */
ITHit.DefineClass('ITHit.WebDAV.Client.Methods.Proppatch', ITHit.WebDAV.Client.Methods.HttpMethod, /** @lends ITHit.WebDAV.Client.Methods.Proppatch.prototype */{

	__static: /** @lends ITHit.WebDAV.Client.Methods.Proppatch */{

		Go: function (oRequest, sHref, aPropsToAddOrUpdate, aPropsToDelete, sLockToken, sHost) {

			// Create request.
			var oWebDavRequest = ITHit.WebDAV.Client.Methods.Proppatch.createRequest(
				oRequest,
				sHref,
				aPropsToAddOrUpdate,
				aPropsToDelete,
				sLockToken,
				sHost
			);

			// Get response.
			var oResult = oWebDavRequest.GetResponse();

			// Return response object.
			return this._ProcessResponse(oResult);
		},

		GoAsync: function (oRequest, sHref, aPropsToAddOrUpdate, aPropsToDelete, sLockToken, sHost, fCallback) {
			// Create request.
			var oWebDavRequest = ITHit.WebDAV.Client.Methods.Proppatch.createRequest(
				oRequest,
				sHref,
				aPropsToAddOrUpdate,
				aPropsToDelete,
				sLockToken,
				sHost
			);

			// Get response.
			var that = this;
			oWebDavRequest.GetResponse(function (oAsyncResult) {

				if (!oAsyncResult.IsSuccess) {
					fCallback(new ITHit.WebDAV.Client.AsyncResult(null, false, oAsyncResult.Error));
					return;
				}

				var oResult = that._ProcessResponse(oAsyncResult.Result, sHref);
				fCallback(new ITHit.WebDAV.Client.AsyncResult(oResult, true, null));
			});

		},

		_ProcessResponse: function (oResponse, sHref) {
			// Get response data.
			var oResponseData = oResponse.GetResponseStream();

			// Return response object.
			return new ITHit.WebDAV.Client.Methods.Proppatch(new ITHit.WebDAV.Client.Methods.MultiResponse(oResponseData, sHref));
		},

		ItemExists: function (aArr) {

			if (aArr && aArr.length) {
				for (var i = 0; i < aArr.length; i++) {
					if (aArr[i]) {
						return true;
					}
				}
			}

			return false;
		},

		createRequest: function (oRequest, sHref, aPropsToAddOrUpdate, aPropsToDelete, sLockToken, sHost) {

			// Assign default value if needed.
			sLockToken = sLockToken || null;

			// Create request.
			var oWebDavRequest = oRequest.CreateWebDavRequest(sHost, sHref, sLockToken);
			oWebDavRequest.Method('PROPPATCH');
			oWebDavRequest.Headers.Add('Content-Type', 'text/xml; charset="utf-8"');

			// Create XML DOM document.
			var oWriter = new ITHit.XMLDoc();

			// Create XML request.
			var propUpd = oWriter.createElementNS(ITHit.WebDAV.Client.DavConstants.NamespaceUri, 'propertyupdate');

			// Check whether properties to add or update are specified.
			if (ITHit.WebDAV.Client.Methods.Proppatch.ItemExists(aPropsToAddOrUpdate)) {
				var set = oWriter.createElementNS(ITHit.WebDAV.Client.DavConstants.NamespaceUri, 'set');

				for (var i = 0; i < aPropsToAddOrUpdate.length; i++) {
					if (aPropsToAddOrUpdate[i]) {
						var prop = oWriter.createElementNS(ITHit.WebDAV.Client.DavConstants.NamespaceUri, 'prop');
						prop.appendChild(aPropsToAddOrUpdate[i].Value);
						set.appendChild(prop);
					}
				}
				propUpd.appendChild(set);
			}

			// Check whether properties to delete are specified.
			if (ITHit.WebDAV.Client.Methods.Proppatch.ItemExists(aPropsToDelete)) {
				var remove = oWriter.createElementNS(ITHit.WebDAV.Client.DavConstants.NamespaceUri, 'remove');
				var prop = oWriter.createElementNS(ITHit.WebDAV.Client.DavConstants.NamespaceUri, 'prop');
				for (var i = 0; i < aPropsToDelete.length; i++) {
					if (aPropsToDelete[i]) {
						var elem = oWriter.createElementNS(aPropsToDelete[i].NamespaceUri, aPropsToDelete[i].Name);
						prop.appendChild(elem);
					}
				}
				remove.appendChild(prop);
				propUpd.appendChild(remove);
			}

			oWriter.appendChild(propUpd);
			oWebDavRequest.Body(oWriter);

			return oWebDavRequest;
		}

	}
});

/**
 * Scope of the lock.
 * Represents exclusive or shared lock.
 * @api
 * @enum {string}
 * @class ITHit.WebDAV.Client.LockScope
 */
ITHit.DefineClass('ITHit.WebDAV.Client.LockScope', null, {
	__static: /** @lends ITHit.WebDAV.Client.LockScope */{

		/**
		 * Exclusive lock. No one else can obtain the lock.
		 * @api
		 * @type {string}
		 */
		Exclusive: 'Exclusive',

		/**
		 * Shared lock. It will be possible for another clients to get the shared locks.
		 * @api
		 * @property {string}
		 */
		Shared: 'Shared'

	}
});


/**
 * Represents pair of resource uri - lock token. Is used to access locked resources.
 * @api
 * @class ITHit.WebDAV.Client.LockUriTokenPair
 */
ITHit.DefineClass('ITHit.WebDAV.Client.LockUriTokenPair', null, /** @lends ITHit.WebDAV.Client.LockUriTokenPair.prototype */{

	/**
	 * Path to the locked resource.
	 * @api
	 * @type {string}
	 */
	Href: null,

	/**
	 * Lock token.
	 * @api
	 * @type {string}
	 */
	LockToken: null,

	/**
	 * Initializes new instance of LockUriTokenPair.
	 * @param {string} sHref Path to the locked resource.
	 * @param {string} sLockToken Lock token.
	 * @throws ITHit.Exceptions.ArgumentNullException Whether sHref is null or sLockScope is null or empty.
	 */
	constructor: function(sHref, sLockToken) {
		ITHit.WebDAV.Client.WebDavUtil.VerifyArgumentNotNull(sHref, "href");
		ITHit.WebDAV.Client.WebDavUtil.VerifyArgumentNotNullOrEmpty(sLockToken, "lockToken");

		this.Href = sHref;
		this.LockToken = sLockToken;
	},

	toString: function() {
		return this.LockToken;
	}

});


/**
 * Information about lock set on an item.
 * @api
 * @class ITHit.WebDAV.Client.LockInfo
 */
ITHit.DefineClass('ITHit.WebDAV.Client.LockInfo', null, /** @lends ITHit.WebDAV.Client.LockInfo.prototype */{

	__static: /** @lends ITHit.WebDAV.Client.LockInfo */{

		/**
		 * Parses activeLocks from lockNode.
		 * @param {ITHit.XMLDoc} oElement Node containing XML Element with activeLock node.
		 * @param {string} sHref Request's URI.
		 * @returns {ITHit.WebDAV.Client.LockInfo} Information about active locks.
		 */
		ParseLockInfo: function(oElement, sHref) {

			// Declare resolver for namespace.
			var oResolver = new ITHit.XPath.resolver();
			oResolver.add('d', ITHit.WebDAV.Client.DavConstants.NamespaceUri);

			// Declare node variable.
			var oNode;

			// Get lock scope.
			if (!(oNode = ITHit.XPath.selectSingleNode('d:lockscope', oElement, oResolver))) {
				throw new ITHit.WebDAV.Client.Exceptions.WebDavException(ITHit.Phrases.Exceptions.ActiveLockDoesntContainLockscope);
			}

			// Detect lock scope
			var oLockScope = null;
			var oLockScopeChilds = oNode.childNodes();
			for (var i = 0, l = oLockScopeChilds.length; i < l; i++) {
				if (oLockScopeChilds[i].nodeType() === 1) {
					oLockScope = oLockScopeChilds[i].localName();
					break;
				}
			}
			switch (oLockScope) {
				case 'shared':
					oLockScope = ITHit.WebDAV.Client.LockScope.Shared;
					break;

				case 'exclusive':
					oLockScope = ITHit.WebDAV.Client.LockScope.Exclusive;
					break;
			}

			// Get depth.
			if ( !(oNode = ITHit.XPath.selectSingleNode('d:depth', oElement, oResolver)) ) {
				throw new ITHit.WebDAV.Client.Exceptions.WebDavException(ITHit.Phrases.Exceptions.ActiveLockDoesntContainDepth);
			}

			var oDepthValue = ITHit.WebDAV.Client.Depth.Parse(oNode.firstChild().nodeValue());
			var bDeep = (oDepthValue == ITHit.WebDAV.Client.Depth.Infinity);

			// Get owner.
			var sOwner = null;
			if ( oNode = ITHit.XPath.selectSingleNode('d:owner', oElement, oResolver) ) {
				sOwner = oNode.firstChild().nodeValue();
			}

			// Get timeout.
			var iTimeOut = -1;
			if ( oNode = ITHit.XPath.selectSingleNode('d:timeout', oElement, oResolver) ) {
				var sTimeOut = oNode.firstChild().nodeValue();

				if ('infinite' != sTimeOut.toLowerCase()) {
					if (-1 != sTimeOut.toLowerCase().indexOf('second-')) {
						sTimeOut = sTimeOut.substr(7);
					}
					var iTimeOut = parseInt(sTimeOut);
				}
			}

			// Get lock token.

			var oLockToken = null;
			if ( oNode = ITHit.XPath.selectSingleNode('d:locktoken', oElement, oResolver) ) {
				var sLockTokenText = ITHit.XPath.selectSingleNode('d:href', oNode, oResolver).firstChild().nodeValue();
				sLockTokenText = sLockTokenText.replace(ITHit.WebDAV.Client.DavConstants.OpaqueLockToken, '');
				oLockToken = new ITHit.WebDAV.Client.LockUriTokenPair(sHref, sLockTokenText);
			}

			return new ITHit.WebDAV.Client.LockInfo(oLockScope, bDeep, sOwner, iTimeOut, oLockToken);
		},

		/**
		 * Parses activeLocks from lockNode.
		 * @param {ITHit.XMLDoc} oElement Node containing XML Element with activeLock node.
		 * @param {string} sHref Requests URI
		 * @returns {Array} Information about active locks.
		 */
		ParseLockDiscovery: function(oElement, sHref) {

			// Declare variable list of locks.
			var aLocks = [];

			// Get a list of active lockes nodes.
			var aSearchedLocks = oElement.getElementsByTagNameNS(ITHit.WebDAV.Client.DavConstants.NamespaceUri, 'activelock');

			for (var i = 0; i < aSearchedLocks.length; i++) {
				aLocks.push(ITHit.WebDAV.Client.LockInfo.ParseLockInfo(aSearchedLocks[i], sHref));
			}

			return aLocks;
		}
	},

	/**
	 * Scope of the lock.
	 * @api
	 * @type {ITHit.WebDAV.Client.LockScope}
	 */
	LockScope: null,

	/**
	 * Whether lock is set on item's children.
	 * @api
	 * @type {boolean}
	 */
	Deep: null,

	/**
	 * Timeout until lock expires.
	 * @api
	 * @type {number}
	 */
	TimeOut: null,

	/**
	 * Owner's name.
	 * @api
	 * @type {string}
	 */
	Owner: null,

	/**
	 * Lock token.
	 * @api
	 * @type {ITHit.WebDAV.Client.LockUriTokenPair}
	 */
	LockToken: null,

	/**
	 * Initializes new instance of LockInfo.
	 * @param {ITHit.WebDAV.Client.LockScope} oLockScope Scope of the lock.
	 * @param {boolean}   bDeep Whether lock is set on item's children.
	 * @param {string} sOwner Owner's name.
	 * @param {number} iTimeOut Timeout until lock expires.
	 * @param {ITHit.WebDAV.Client.LockUriTokenPair} oLockToken Lock token.
	 */
	constructor: function(oLockScope, bDeep, sOwner, iTimeOut, oLockToken) {
		this.LockScope = oLockScope;
		this.Deep = bDeep;
		this.TimeOut = iTimeOut;
		this.Owner = sOwner;
		this.LockToken = oLockToken;
	}

});


/**
 * @class ITHit.WebDAV.Client.Methods.Lock
 * @extends ITHit.WebDAV.Client.Methods.HttpMethod
 */
ITHit.DefineClass('ITHit.WebDAV.Client.Methods.Lock', ITHit.WebDAV.Client.Methods.HttpMethod, /** @lends ITHit.WebDAV.Client.Methods.Lock.prototype */{

	__static: /** @lends ITHit.WebDAV.Client.Methods.Lock */{

		/**
		 *
		 * @param oRequest
		 * @param sHref
		 * @param iTimeout
		 * @param sLockTokenOrScope
		 * @param sHost
		 * @param bDeep
		 * @param sOwner
		 * @returns {*}
		 * @constructor
		 */
		Go: function (oRequest, sHref, iTimeout, sLockTokenOrScope, sHost, bDeep, sOwner) {
			return this._super.apply(this, arguments);
		},

		/**
		 *
		 * @param oRequest
		 * @param sHref
		 * @param iTimeout
		 * @param sLockTokenOrScope
		 * @param sHost
		 * @param bDeep
		 * @param sOwner
		 * @param fCallback
		 * @returns {*}
		 * @constructor
		 */
		GoAsync: function (oRequest, sHref, iTimeout, sLockTokenOrScope, sHost, bDeep, sOwner, fCallback) {
			return this._super.apply(this, arguments);
		},

		_CreateRequest: function (oRequest, sHref, iTimeout, sLockTokenOrScope, sHost, bDeep, sOwner) {
			// Passed lock scope.
			var sLockScope = sLockTokenOrScope;

			// Create request.
			var oWebDavRequest = oRequest.CreateWebDavRequest(sHost, sHref);
			oWebDavRequest.Method('LOCK');

			// Add headers.
			oWebDavRequest.Headers.Add('Timeout',
				(-1 === iTimeout)
					? 'Infinite'
					: 'Second-' + parseInt(iTimeout)
			);
			oWebDavRequest.Headers.Add('Depth', bDeep ? ITHit.WebDAV.Client.Depth.Infinity.Value : ITHit.WebDAV.Client.Depth.Zero.Value);
			oWebDavRequest.Headers.Add('Content-Type', 'text/xml; charset="utf-8"');

			// Create XML DOM document object.
			var oWriter = new ITHit.XMLDoc();

			// Get namespace for XML elements.
			var sNamespaceUri = ITHit.WebDAV.Client.DavConstants.NamespaceUri;

			// Create root element.
			var lockInfo = oWriter.createElementNS(sNamespaceUri, 'lockinfo');

			// Create elements.
			var lockScope = oWriter.createElementNS(sNamespaceUri, 'lockscope');
			var lockScopeData = oWriter.createElementNS(sNamespaceUri, sLockScope.toLowerCase());
			lockScope.appendChild(lockScopeData);

			var lockType = oWriter.createElementNS(sNamespaceUri, 'locktype');
			var write = oWriter.createElementNS(sNamespaceUri, 'write');
			lockType.appendChild(write);

			var owner = oWriter.createElementNS(sNamespaceUri, 'owner');
			owner.appendChild(oWriter.createTextNode(sOwner));

			lockInfo.appendChild(lockScope);
			lockInfo.appendChild(lockType);
			lockInfo.appendChild(owner);

			oWriter.appendChild(lockInfo);

			// Add XML document as request body.
			oWebDavRequest.Body(oWriter);

			return oWebDavRequest;
		}

	},

	/**
	 * @type {ITHit.WebDAV.Client.LockInfo}
	 */
	LockInfo: null,

	_Init: function () {
		// Get response data as string.
		var oXmlDoc = this.Response.GetResponseStream();

		// Create namespace resolver.
		var oResolver = new ITHit.XPath.resolver();
		oResolver.add('d', ITHit.WebDAV.Client.DavConstants.NamespaceUri);

		// Select property element.
		var oProp = new ITHit.WebDAV.Client.Property(ITHit.XPath.selectSingleNode('/d:prop', oXmlDoc, oResolver));

		try {
			// Parse property element.
			var oInfoList = new ITHit.WebDAV.Client.LockInfo.ParseLockDiscovery(oProp.Value, this.Href);

			// Check length of selected elements.
			if (oInfoList.length !== 1) {
				throw new ITHit.WebDAV.Client.Exceptions.WebDavException(ITHit.Phrases.UnableToParseLockInfoResponse);
			}

			// Select property element.
			this.LockInfo = oInfoList[0];

			// Exception had happened.
		} catch (e) {
			throw new ITHit.WebDAV.Client.Exceptions.PropertyException(
				ITHit.Phrases.Exceptions.ParsingPropertiesException,
				this.Href,
				oProp.Name,
				null,
				ITHit.WebDAV.Client.HttpStatus.OK,
				e
			);
		}
	}
});


/**
 * @class ITHit.WebDAV.Client.Methods.LockRefresh
 * @extends ITHit.WebDAV.Client.Methods.Lock
 */
ITHit.DefineClass('ITHit.WebDAV.Client.Methods.LockRefresh', ITHit.WebDAV.Client.Methods.Lock, /** @lends ITHit.WebDAV.Client.Methods.LockRefresh.prototype */{

	__static: /** @lends ITHit.WebDAV.Client.Methods.LockRefresh */{

		/**
		 *
		 * @param oRequest
		 * @param sHref
		 * @param iTimeout
		 * @param sLockTokenOrScope
		 * @param sHost
		 * @param bDeep
		 * @param sOwner
		 * @returns {*}
		 * @constructor
		 */
		Go: function (oRequest, sHref, iTimeout, sLockTokenOrScope, sHost, bDeep, sOwner) {
			return this._super.apply(this, arguments);
		},

		/**
		 *
		 * @param oRequest
		 * @param sHref
		 * @param iTimeout
		 * @param sLockTokenOrScope
		 * @param sHost
		 * @param bDeep
		 * @param sOwner
		 * @param fCallback
		 * @returns {*}
		 * @constructor
		 */
		GoAsync: function (oRequest, sHref, iTimeout, sLockTokenOrScope, sHost, bDeep, sOwner, fCallback) {
			return this._super.apply(this, arguments);
		},

		_CreateRequest: function (oRequest, sHref, iTimeout, sLockTokenOrScope, sHost, bDeep, sOwner) {

			// Passed lock token.
			var sLockToken = sLockTokenOrScope;

			// Create request.
			var oWebDavRequest = oRequest.CreateWebDavRequest(sHost, sHref, sLockToken);
			oWebDavRequest.Method('LOCK');

			// Add header.
			oWebDavRequest.Headers.Add('Timeout',
				(-1 == iTimeout)
					? 'Infinite'
					: 'Second-' + parseInt(iTimeout)
			);

			oWebDavRequest.Body('');

			return oWebDavRequest;
		}

	}
});


/**
 * @class ITHit.WebDAV.Client.Methods.Unlock
 * @extends ITHit.WebDAV.Client.Methods.HttpMethod
 */
ITHit.DefineClass('ITHit.WebDAV.Client.Methods.Unlock', ITHit.WebDAV.Client.Methods.HttpMethod, /** @lends ITHit.WebDAV.Client.Methods.Unlock.prototype */{

	__static: /** @lends ITHit.WebDAV.Client.Methods.Unlock */{

		/**
		 *
		 * @param oRequest
		 * @param sHref
		 * @param sLockToken
		 * @param sHost
		 * @returns {ITHit.WebDAV.Client.Methods.Unlock}
		 */
		Go: function (oRequest, sHref, sLockToken, sHost) {
			return this._super.apply(this, arguments);
		},

		/**
		 *
		 * @param oRequest
		 * @param sHref
		 * @param sLockToken
		 * @param sHost
		 * @param fCallback
		 * @returns {*}
		 */
		GoAsync: function (oRequest, sHref, sLockToken, sHost, fCallback) {
			return this._super.apply(this, arguments);
		},

		_ProcessResponse: function (oResponse, sHref) {
			// Get appropriate response object.
			var oResp = new ITHit.WebDAV.Client.Methods.SingleResponse(oResponse);

			return this._super(oResp);
		},

		_CreateRequest: function (oRequest, sHref, sLockToken, sHost) {

			// Create request.
			var oWebDavRequest = oRequest.CreateWebDavRequest(sHost, sHref);
			oWebDavRequest.Method('UNLOCK');

			// Add header.
			oWebDavRequest.Headers.Add('Lock-Token', '<' + ITHit.WebDAV.Client.DavConstants.OpaqueLockToken + sLockToken + '>');

			return oWebDavRequest;
		}

	}
});


/**
 * Options of an item, described by supported HTTP extensions.
 * @api
 * @class ITHit.WebDAV.Client.OptionsInfo
 */
ITHit.DefineClass('ITHit.WebDAV.Client.OptionsInfo', null, /** @lends ITHit.WebDAV.Client.OptionsInfo.prototype */{

	/**
	 * Features supported by WebDAV server. See Features Enumeration {@link ITHit.WebDAV.Client.Features}.
	 * @api
	 * @type {number}
	 */
	Features: null,

	/**
	 * A nonstandard header meaning the server supports WebDAV protocol.
	 * @type {boolean}
	 */
	MsAuthorViaDav: null,

	/**
	 * DeltaV Version History compliant item.
	 * @type {number}
	 */
	VersionControl: null,

	/**
	 * The item supports search
	 * @type {boolean}
	 */
	Search: null,

	/**
	 * Server version (engine header)
	 * @type {string}
	 */
	ServerVersion: '',

	/**
	 * Create new instance of OptionsInfo class.
	 * @param {number} iFeatures Classes of WebDAV protocol supported by the item.
	 * @param {boolean} bMsAuthorViaDav A nonstandard header meaning the server supports WebDAV protocol.
	 * @param {number} iVersionControl
	 * @param {boolean} bSearchSupported
	 * @param {string} sServerVersion
	 */
	constructor: function(iFeatures, bMsAuthorViaDav, iVersionControl, bSearchSupported, sServerVersion) {
		this.Features = iFeatures;
		this.MsAuthorViaDav = bMsAuthorViaDav;
		this.VersionControl = iVersionControl;
		this.Search = bSearchSupported;
		this.ServerVersion = sServerVersion;
	}

});

/**
 * Represents features supported by WebDAV server.
 * @api
 * @enum {number}
 * @class ITHit.WebDAV.Client.Features
 */
ITHit.DefineClass('ITHit.WebDAV.Client.Features', null, {
	__static: /** @lends ITHit.WebDAV.Client.Features */{

		/**
		 * WebDAV Class 1 compliant item.
		 * Class 1 items does not support locking.
		 * @api
		 * @readonly
		 * @type {number}
		 */
		Class1: 1,

		/**
		 * WebDAV Class 2 compliant item.
		 * Class 2 items support locking.
		 * @api
		 * @readonly
		 * @type {number}
		 */
		Class2: 2,

		/**
		 * WebDAV Class 3 compliant item.
		 * @api
		 * @readonly
		 * @type {number}
		 */
		Class3: 3,

		/**
		 * DeltaV version-control compliant item.
		 * @api
		 * @readonly
		 * @type {number}
		 */
		VersionControl: 4,

		/**
		 * Checkout-in-place item support check out, check in and uncheckout operations.
		 * @api
		 * @readonly
		 * @type {number}
		 */
		CheckoutInPlace: 16,

		/**
		 * DeltaV Version History compliant item.
		 * @api
		 * @readonly
		 * @type {number}
		 */
		VersionHistory: 32,

		/**
		 * DeltaV Update compliant item.
		 * @api
		 * @readonly
		 * @type {number}
		 */
		Update: 64,

		/**
		 * The item supports resumable upload.
		 * @api
		 * @readonly
		 * @type {number}
		 */
		ResumableUpload: 128,

		/**
		 * The item supports resumable download.
		 * @api
		 * @readonly
		 * @type {number}
		 */
		ResumableDownload: 256

	}
});


ITHit.DefineClass('ITHit.WebDAV.Client.Methods.Options', ITHit.WebDAV.Client.Methods.HttpMethod, /** @lends ITHit.WebDAV.Client.Methods.Options.prototype */{

	__static: /** @lends ITHit.WebDAV.Client.Methods.Options */{

		Go: function (oRequest, sHref, sHost) {
			return this.GoAsync(oRequest, sHref, sHost);
		},

		GoAsync: function (oRequest, sHref, sHost, fCallback) {

			// Create request.
			var oWebDavRequest = ITHit.WebDAV.Client.Methods.Options.createRequest(oRequest, sHref, sHost);

			var self = this;
			var fOnResponse = typeof fCallback === 'function'
				? function (oResult) {
				self._GoCallback(oRequest, sHref, oResult, fCallback)
			}
				: null;

			// Make request.
			var oResponse = oWebDavRequest.GetResponse(fOnResponse);

			if (typeof fCallback !== 'function') {
				var oResult = new ITHit.WebDAV.Client.AsyncResult(oResponse, oResponse != null, null);
				return this._GoCallback(oRequest, sHref, oResult, fCallback);
			} else {
				return oWebDavRequest;
			}
		},

		_GoCallback: function (oRequest, sHref, oResult, fCallback) {

			var oResponse = oResult;
			var bSuccess = true;
			var oError = null;

			if (oResult instanceof ITHit.WebDAV.Client.AsyncResult) {
				oResponse = oResult.Result;
				bSuccess = oResult.IsSuccess;
				oError = oResult.Error;
			}

			var oOptions = null;
			if (bSuccess) {
				// Get options.
				var oOptions = new ITHit.WebDAV.Client.Methods.Options(oResponse);
			}

			// Return response.
			if (typeof fCallback === 'function') {
				var oOptionsResult = new ITHit.WebDAV.Client.AsyncResult(oOptions, bSuccess, oError);
				fCallback.call(this, oOptionsResult);
			} else {
				return oOptions;
			}
		},

		createRequest: function (oRequest, sHref, sHost) {

			// Create request.
			var oWebDavRequest = oRequest.CreateWebDavRequest(sHost, sHref);

			// Set method.
			oWebDavRequest.Method('OPTIONS');

			// Return request object.
			return oWebDavRequest;
		}

	},

	ItemOptions: null,

	/**
	 * Method to perform Options request to a server.
	 * Create new instance of Options class.
	 * @constructs
	 * @param {ITHit.WebDAV.Client.WebDavResponse} oResponse Response object.
	 * @extends ITHit.WebDAV.Client.Methods.HttpMethod
	 */
	constructor: function (oResponse) {
		this._super(oResponse);

		// Get DAV request header.
		var sDav = oResponse._Response.GetResponseHeader('dav', true);

		// Get version of WebDAV server.
		var iFeatures = 0;
		var iVersionControl = 0;
		if (sDav) {
			if (-1 != sDav.indexOf('2')) {
				iFeatures = ITHit.WebDAV.Client.Features.Class1 + ITHit.WebDAV.Client.Features.Class2;
			} else if (-1 != sDav.indexOf('1')) {
				iFeatures = ITHit.WebDAV.Client.Features.Class1;
			}

			if (-1 != sDav.indexOf('version-control')) {
				iVersionControl = ITHit.WebDAV.Client.Features.VersionControl;
			}

			// Whether server supports ITHit Resumable Upload.
			if (-1 != sDav.indexOf('resumable-upload')) {
				iFeatures += ITHit.WebDAV.Client.Features.ResumableUpload;
			}
		}

		var bMsAuthorViaDav = false;
		var sMsAuthorViaHeader = oResponse._Response.GetResponseHeader('ms-author-via', true);

		if (sMsAuthorViaHeader && (-1 != sMsAuthorViaHeader.toLowerCase().indexOf('dav'))) {
			bMsAuthorViaDav = true;
		}

		// Detect search support
		var iSearchSupported = false;
		var sAllowHeader = oResponse._Response.GetResponseHeader('allow', true) || '';
		var aAllowList = sAllowHeader.toLowerCase().split(/[^a-z-_]+/);
		for (var i = 0, l = aAllowList.length; i < l; i++) {
			if (aAllowList[i] === 'search') {
				iSearchSupported = true;
				break;
			}
		}

		// Get server version
		var sServerVersion = oResponse._Response.GetResponseHeader('x-engine', true);

		this.ItemOptions = new ITHit.WebDAV.Client.OptionsInfo(iFeatures, bMsAuthorViaDav, iVersionControl, iSearchSupported, sServerVersion);
	}
});


ITHit.oNS = ITHit.Declare('ITHit.Exceptions');

/*
 * Wrong expression.
 * @class ITHit.Exceptions.ExpressionException
 * @extends ITHit.Exception
 */
/*
 * Initializes a new instance of the ExpressionException class with a specified error message.
 * @constructor ExpressionException
 * 
 * @param {String} sMessage  The error message that explains the reason for the exception.
 */
ITHit.oNS.ExpressionException = function(sMessage) {
	
	// Inheritance definition.
	ITHit.Exceptions.ExpressionException.baseConstructor.call(this, sMessage);
}

// Extend class.
ITHit.Extend(ITHit.oNS.ExpressionException, ITHit.Exception);

// Exception name.
ITHit.oNS.ExpressionException.prototype.Name = 'ExpressionException';

/**
 * Information about file upload progress.
 * @api
 * @class ITHit.WebDAV.Client.UploadProgressInfo
 */
ITHit.DefineClass('ITHit.WebDAV.Client.UploadProgressInfo', null, /** @lends ITHit.WebDAV.Client.UploadProgressInfo.prototype */{

	__static: /** @lends ITHit.WebDAV.Client.UploadProgressInfo */{

		GetUploadProgress: function(oMultiResponse) {

			var aUploadInfo    = [];

			if (!ITHit.WebDAV.Client.UploadProgressInfo.PropNames) {
				ITHit.WebDAV.Client.UploadProgressInfo.PropNames = [
					new ITHit.WebDAV.Client.PropertyName('bytes-uploaded', 'ithit'),
					new ITHit.WebDAV.Client.PropertyName('last-chunk-saved', 'ithit'),
					new ITHit.WebDAV.Client.PropertyName('total-content-length', 'ithit')
				];
			}

			for (var i = 0, oResponse; oResponse = oMultiResponse.Responses[i]; i++) {
				for (var j = 0, oPropstat; oPropstat = oResponse.Propstats[j]; j++) {

					var oFoundedProps = [];

					for (var k = 0, oProp; oProp = oPropstat.Properties[k]; k++) {

						// Bytes uploaded.
						if (oProp.Name.Equals(ITHit.WebDAV.Client.UploadProgressInfo.PropNames[0])) {
							oFoundedProps[0] = oProp.Value;
						}
						// Last chunk saved.
						else if (oProp.Name.Equals(ITHit.WebDAV.Client.UploadProgressInfo.PropNames[1])) {
							oFoundedProps[1] = oProp.Value;
						}
						// Percent uploaded.
						else if (oProp.Name.Equals(ITHit.WebDAV.Client.UploadProgressInfo.PropNames[2])) {
							oFoundedProps[2] = oProp.Value;
						}
					}

					if (!oFoundedProps[0] || !oFoundedProps[1] || !oFoundedProps[2]) {
						throw new ITHit.Exception(ITHit.Phrases.Exceptions.NotAllPropertiesReceivedForUploadProgress.Paste(oResponse.Href));
					}

					aUploadInfo.push(new ITHit.WebDAV.Client.UploadProgressInfo(oResponse.Href, parseInt(oFoundedProps[0].firstChild().nodeValue()), parseInt(oFoundedProps[2].firstChild().nodeValue()), ITHit.WebDAV.Client.HierarchyItem.GetDate(oFoundedProps[1].firstChild().nodeValue())));
				}
			}

			return aUploadInfo;
		}

	},

	/**
	 * Item path on the server.
	 * @api
	 * @type {string}
	 */
	Href: null,

	/**
	 * Amount of bytes successfully uploaded to server.
	 * @api
	 * @type {number}
	 */
	BytesUploaded: null,

	/**
	 * Total file size.
	 * @api
	 * @type {number}
	 */
	TotalContentLength: null,

	/**
	 * The date and time when the last chunk of file was saved on server side.
	 * @api
	 * @type {Date}
	 */
	LastChunkSaved: null,

	/**
	 * @param {string} sHref Item's path.
	 * @param {number} iBytesUploaded Uploaded bytes.
	 * @param {number} iContentLength Total file size.
	 * @param {Date} [oLastChunkSaved] Last chunk save date.
	 */
	constructor: function(sHref, iBytesUploaded, iContentLength, oLastChunkSaved) {

		if (!ITHit.Utils.IsString(sHref) || !sHref) {
			throw new ITHit.Exceptions.ArgumentException(ITHit.Phrases.Exceptions.WrongHref.Paste(), sHref);
		}

		if (!ITHit.Utils.IsInteger(iBytesUploaded)) {
			throw new ITHit.Exceptions.ArgumentException(ITHit.Phrases.Exceptions.WrongUploadedBytesType, iBytesUploaded);
		}

		if (!ITHit.Utils.IsInteger(iContentLength)) {
			throw new ITHit.Exceptions.ArgumentException(ITHit.Phrases.Exceptions.WrongContentLengthType, iContentLength);
		}

		if (iBytesUploaded > iContentLength) {
			throw new ITHit.Exceptions.ExpressionException(ITHit.Phrases.Exceptions.BytesUploadedIsMoreThanTotalFileContentLength);
		}

		this.Href               = sHref;
		this.BytesUploaded      = iBytesUploaded;
		this.TotalContentLength = iContentLength;
		this.LastChunkSaved     = oLastChunkSaved;
	}

});


ITHit.DefineClass('ITHit.WebDAV.Client.Methods.Report', ITHit.WebDAV.Client.Methods.HttpMethod, /** @lends ITHit.WebDAV.Client.Methods.Report.prototype */{

	__static: /** @lends ITHit.WebDAV.Client.Methods.Report */{

		ReportType: {

			/**
			 * Report return upload progress info (default)
			 * @type {string}
			 */
			UploadProgress: 'UploadProgress',

			/**
			 * Report return versions tree
			 * @type {string}
			 */
			VersionsTree: 'VersionsTree'
		},

		Go: function (oRequest, sHref, sHost, reportType, aProperties) {
			return this.GoAsync(oRequest, sHref, sHost, reportType, aProperties);
		},

		GoAsync: function (oRequest, sHref, sHost, reportType, aProperties, fCallback) {

			// by default
			if (!reportType) {
				reportType = ITHit.WebDAV.Client.Methods.Report.ReportType.UploadProgress;
			}

			// Create request.
			var oWebDavRequest = ITHit.WebDAV.Client.Methods.Report.createRequest(oRequest, sHref, sHost, reportType, aProperties);


			var self = this;
			var fOnResponse = typeof fCallback === 'function'
				? function (oResult) {
				self._GoCallback(sHref, oResult, reportType, fCallback)
			}
				: null;

			// Make request.
			var oResponse = oWebDavRequest.GetResponse(fOnResponse);

			if (typeof fCallback !== 'function') {
				var oResult = new ITHit.WebDAV.Client.AsyncResult(oResponse, oResponse != null, null);
				return this._GoCallback(sHref, oResult, reportType, fCallback);
			} else {
				return oWebDavRequest;
			}
		},

		_GoCallback: function (sHref, oResult, reportType, fCallback) {

			var oResponse = oResult;
			var bSuccess = true;
			var oError = null;

			if (oResult instanceof ITHit.WebDAV.Client.AsyncResult) {
				oResponse = oResult.Result;
				bSuccess = oResult.IsSuccess;
				oError = oResult.Error;
			}

			var oReport = null;
			if (bSuccess) {
				// Receive data.
				var oResponseData = oResponse.GetResponseStream();

				oReport = new ITHit.WebDAV.Client.Methods.Report(new ITHit.WebDAV.Client.Methods.MultiResponse(oResponseData, sHref), reportType);
			}

			// Return response.
			if (typeof fCallback === 'function') {
				var oReportResult = new ITHit.WebDAV.Client.AsyncResult(oReport, bSuccess, oError);
				fCallback.call(this, oReportResult);
			} else {
				return oReport;
			}
		},

		createRequest: function (oRequest, sHref, sHost, reportType, aProperties) {

			var oWebDavRequest = oRequest.CreateWebDavRequest(sHost, sHref);

			oWebDavRequest.Method('REPORT');
			oWebDavRequest.Headers.Add('Content-Type', 'text/xml; charset="utf-8"');

			// Create XML DOM document.
			var oWriter = new ITHit.XMLDoc();

			switch (reportType) {
				case ITHit.WebDAV.Client.Methods.Report.ReportType.UploadProgress:
					var oElem = oWriter.createElementNS('ithit', 'upload-progress');
					oWriter.appendChild(oElem);
					break;

				case ITHit.WebDAV.Client.Methods.Report.ReportType.VersionsTree:
					// Create root element.
					var oVersionTreeElement = oWriter.createElementNS(ITHit.WebDAV.Client.DavConstants.NamespaceUri, 'version-tree');

					// All properties.
					if (!aProperties || !aProperties.length) {
						var propEl = oWriter.createElementNS(ITHit.WebDAV.Client.DavConstants.NamespaceUri, 'allprop');

						// Selected properties.
					} else {
						var propEl = oWriter.createElementNS(ITHit.WebDAV.Client.DavConstants.NamespaceUri, 'prop');
						for (var i = 0; i < aProperties.length; i++) {
							var prop = oWriter.createElementNS(aProperties[i].NamespaceUri, aProperties[i].Name);
							propEl.appendChild(prop);
						}
					}

					// Append created child nodes.
					oVersionTreeElement.appendChild(propEl);
					oWriter.appendChild(oVersionTreeElement);
					break;
			}

			oWebDavRequest.Body(oWriter);

			// Return request object.
			return oWebDavRequest;
		}

	},

	/**
	 * @constructs
	 * @param {ITHit.WebDAV.Client.WebDavResponse} oResponse Response object.
	 * @param {string} reportType
	 */
	constructor: function (oResponse, reportType) {
		this._super(oResponse);

		switch (reportType) {
			case ITHit.WebDAV.Client.Methods.Report.ReportType.UploadProgress:
				return ITHit.WebDAV.Client.UploadProgressInfo.GetUploadProgress(oResponse);
		}
	}
});


;
(function() {

	/**
	 * Represents one WebDAV item (file, folder or lock-null).
	 * @api
	 * @class ITHit.WebDAV.Client.HierarchyItem
	 */
	var self = ITHit.DefineClass('ITHit.WebDAV.Client.HierarchyItem', null, /** @lends ITHit.WebDAV.Client.HierarchyItem.prototype */{

		__static: /** @lends ITHit.WebDAV.Client.HierarchyItem */{

			GetRequestProperties: function() {
				return ITHit.WebDAV.Client.File.GetRequestProperties();
			},

			GetCustomRequestProperties: function(aCustomProperties) {
				// Set node properties for selection.
				var aProperties = this.GetRequestProperties();

				// Normalize additional properties (clean duplicates)
				var aNormalizedAdditionalProperties = [];
				for (var i = 0, l = aCustomProperties.length; i < l; i++) {
					var oProperty = aCustomProperties[i];
					var bFinedInDefaults = false;

					// Find in defaults
					for (var i2 = 0, l2 = aProperties.length; i2 < l2; i2++) {
						if (oProperty.Equals(aProperties[i2])) {
							bFinedInDefaults = true;
							break;
						}
					}

					if (!bFinedInDefaults) {
						aNormalizedAdditionalProperties.push(oProperty);
					}
				}

				// Append additional properties
				return aNormalizedAdditionalProperties;
			},

			ParseHref: function(sHref) {
				return {
					Href: sHref,
					Host: ITHit.WebDAV.Client.HierarchyItem.GetHost(sHref)
				};
			},

			/**
			 * Load item from server.
			 * @deprecated Use asynchronous method instead
			 * @param {ITHit.WebDAV.Client.Request} oRequest Current WebDAV session.
			 * @param {string} sHref This item path on the server.
			 * @param {ITHit.WebDAV.Client.PropertyName[]} [aProperties] Additional properties requested from server. Default is empty array.
			 * @returns {ITHit.WebDAV.Client.HierarchyItem} Loaded item.
			 * @throws ITHit.WebDAV.Client.Exceptions.WebDavException A Folder was expected or the response doesn't have required item.
			 */
			OpenItem: function(oRequest, sHref, aProperties) {
				aProperties = aProperties || [];

				// Normalize custom properties
				aProperties = this.GetCustomRequestProperties(aProperties);

				var oHrefObject = this.ParseHref(sHref);
				var oResult = ITHit.WebDAV.Client.Methods.Propfind.Go(
					oRequest,
					oHrefObject.Href,
					ITHit.WebDAV.Client.Methods.Propfind.PropfindMode.SelectedProperties,
					[].concat(this.GetRequestProperties()).concat(aProperties),
					ITHit.WebDAV.Client.Depth.Zero,
					oHrefObject.Host
				);

				return this.GetItemFromMultiResponse(oResult.Response, oRequest, sHref, aProperties);
			},

			/**
			 * Callback function to be called when folder loaded from server.
			 * @callback ITHit.WebDAV.Client.HierarchyItem~OpenItemAsyncCallback
			 * @param {ITHit.WebDAV.Client.AsyncResult} oResult Result object
			 * @param {ITHit.WebDAV.Client.HierarchyItem} oResult.Result Loaded item.
			 */

			/**
			 * Load item from server.
			 * @param {ITHit.WebDAV.Client.Request} oRequest Current WebDAV session.
			 * @param {string} sHref This item path on the server.
			 * @param {ITHit.WebDAV.Client.PropertyName[]} aProperties Additional properties requested from server. Default is empty array.
			 * @param {ITHit.WebDAV.Client.HierarchyItem~OpenItemAsyncCallback} fCallback Function to call when operation is completed.
			 * @returns {ITHit.WebDAV.Client.Request} Request object.
			 */
			OpenItemAsync: function(oRequest, sHref, aProperties, fCallback) {
				aProperties = aProperties || [];

				// Normalize custom properties
				aProperties = this.GetCustomRequestProperties(aProperties);

				var oHrefObject = this.ParseHref(sHref);
				ITHit.WebDAV.Client.Methods.Propfind.GoAsync(
					oRequest,
					oHrefObject.Href,
					ITHit.WebDAV.Client.Methods.Propfind.PropfindMode.SelectedProperties,
					[].concat(this.GetRequestProperties()).concat(aProperties),
					ITHit.WebDAV.Client.Depth.Zero,
					oHrefObject.Host,
					function(oAsyncResult) {
						if (oAsyncResult.IsSuccess) {
							try {
								oAsyncResult.Result = self.GetItemFromMultiResponse(oAsyncResult.Result.Response, oRequest, sHref, aProperties);
							} catch(oError) {
								oAsyncResult.Error = oError;
								oAsyncResult.IsSuccess = false;
							}
						}

						fCallback(oAsyncResult);
					}
				);

				return oRequest;
			},

			GetItemFromMultiResponse: function(oMultiResponse, oRequest, sHref, aCustomProperties) {
				aCustomProperties = aCustomProperties || [];

				// Loop through result items.
				for (var i = 0; i < oMultiResponse.Responses.length; i++) {
					var oResponse = oMultiResponse.Responses[i];

					// Whether item is found.
					if (!ITHit.WebDAV.Client.HierarchyItem.HrefEquals(oResponse.Href, sHref)) {
						continue;
					}

					return this.GetItemFromResponse(oResponse, oRequest, sHref, aCustomProperties);
				}

				throw new ITHit.WebDAV.Client.Exceptions.NotFoundException(ITHit.Phrases.FolderNotFound.Paste(sHref));
			},

			GetItemsFromMultiResponse: function (oMultiResponse, oRequest, sHref, aCustomProperties) {
				aCustomProperties = aCustomProperties || [];

				var aItems = [];

				// Loop through result items.
				for (var i = 0; i < oMultiResponse.Responses.length; i++) {
					var oResponse = oMultiResponse.Responses[i];

					// Do not include current node, get only it's child nodes.
					if (ITHit.WebDAV.Client.HierarchyItem.HrefEquals(oResponse.Href, sHref)) {
						continue;
					}

					// Ignore element whether it's status is set and not OK.
					if (oResponse.Status && !oResponse.Status.IsOk()) {
						continue;
					}

					aItems.push(this.GetItemFromResponse(oResponse, oRequest, sHref, aCustomProperties));
				}

				return aItems;
			},

			GetItemFromResponse: function(oResponse, oRequest, sHref, aCustomProperties) {
				var oHrefObject = this.ParseHref(sHref);

				// Append custom properties
				var aPropertyList = ITHit.WebDAV.Client.HierarchyItem.GetPropertiesFromResponse(oResponse);

				// Set null for not-exists properties
				for (var i2 = 0, l2 = aCustomProperties.length; i2 < l2; i2++) {
					if (!ITHit.WebDAV.Client.HierarchyItem.HasProperty(oResponse, aCustomProperties[i2])) {
						aPropertyList.push(new ITHit.WebDAV.Client.Property(aCustomProperties[i2], ''));
					}
				}

				switch (ITHit.WebDAV.Client.HierarchyItem.GetResourceType(oResponse)) {
					case ITHit.WebDAV.Client.ResourceType.File:
						return new ITHit.WebDAV.Client.File(
							oRequest.Session,
							oResponse.Href,
							ITHit.WebDAV.Client.HierarchyItem.GetLastModified(oResponse),
							ITHit.WebDAV.Client.HierarchyItem.GetDisplayName(oResponse),
							ITHit.WebDAV.Client.HierarchyItem.GetCreationDate(oResponse),
							ITHit.WebDAV.Client.HierarchyItem.GetContentType(oResponse),
							ITHit.WebDAV.Client.HierarchyItem.GetContentLength(oResponse),
							ITHit.WebDAV.Client.HierarchyItem.GetSupportedLock(oResponse),
							ITHit.WebDAV.Client.HierarchyItem.GetActiveLocks(oResponse, sHref),
							oHrefObject.Host,
							ITHit.WebDAV.Client.HierarchyItem.GetQuotaAvailableBytes(oResponse),
							ITHit.WebDAV.Client.HierarchyItem.GetQuotaUsedBytes(oResponse),
							ITHit.WebDAV.Client.HierarchyItem.GetCkeckedIn(oResponse),
							ITHit.WebDAV.Client.HierarchyItem.GetCheckedOut(oResponse),
							aPropertyList
						);
						break;

					case ITHit.WebDAV.Client.ResourceType.Folder:
						return new ITHit.WebDAV.Client.Folder(
							oRequest.Session,
							oResponse.Href,
							ITHit.WebDAV.Client.HierarchyItem.GetLastModified(oResponse),
							ITHit.WebDAV.Client.HierarchyItem.GetDisplayName(oResponse),
							ITHit.WebDAV.Client.HierarchyItem.GetCreationDate(oResponse),
							ITHit.WebDAV.Client.HierarchyItem.GetSupportedLock(oResponse),
							ITHit.WebDAV.Client.HierarchyItem.GetActiveLocks(oResponse, sHref),
							oHrefObject.Host,
							ITHit.WebDAV.Client.HierarchyItem.GetQuotaAvailableBytes(oResponse),
							ITHit.WebDAV.Client.HierarchyItem.GetQuotaUsedBytes(oResponse),
							ITHit.WebDAV.Client.HierarchyItem.GetCkeckedIn(oResponse),
							ITHit.WebDAV.Client.HierarchyItem.GetCheckedOut(oResponse),
							aPropertyList
						);

					default:
						throw new ITHit.WebDAV.Client.Exceptions.WebDavException(ITHit.Phrases.Exceptions.UnknownResourceType);
				}
			},

			/**
			 * Get item's full path.
			 * @param {string} sUri Base URI.
			 * @param {string} sDestinationName Item name.
			 * @returns {string} Item full path.
			 */
			AppendToUri: function(sUri, sDestinationName) {
				return ITHit.WebDAV.Client.HierarchyItem.GetAbsoluteUriPath(sUri) + ITHit.WebDAV.Client.Encoder.EncodeURI(sDestinationName);
			},

			/**
			 * Retrieves locks for this item.
			 * @returns {ITHit.WebDAV.Client.LockInfo[]} List of LockInfo objects.
			 * @throws ITHit.WebDAV.Client.Exceptions.NotFoundException This item doesn't exist on the server.
			 * @throws ITHit.WebDAV.Client.Exceptions.WebDavException Unexpected error occurred.
			 */
			GetActiveLocks: function(oResp, sHref){

				// Get lock name.
				var oLockDiscovery = ITHit.WebDAV.Client.DavConstants.LockDiscovery.toString();


				for (var i = 0; i < oResp.Propstats.length; i++) {
					var oPropstat = oResp.Propstats[i];

					if (!oPropstat.Status.IsOk()) {
						break;
					}

					if ('undefined' != typeof oPropstat.PropertiesByNames[oLockDiscovery]) {
						var oProp = oPropstat.PropertiesByNames[oLockDiscovery];

						try {
							// Return active locks.
							return ITHit.WebDAV.Client.LockInfo.ParseLockDiscovery(oProp.Value, sHref);
						} catch (e) {
							if (typeof window.console !== 'undefined') {
								console.error(e.stack || e.toString());
							}
							break;
						}

					} else {
						break;
					}
				}

				return [];
			},

			/**
			 * Retrieves locks for this item.
			 * @returns {Array} Supported locks.
			 */
			GetSupportedLock: function(oResp) {

				var oSupportedLock = ITHit.WebDAV.Client.DavConstants.SupportedLock;

				for (var i = 0; i < oResp.Propstats.length; i++ ) {
					var oPropstat = oResp.Propstats[i];

					if (!oPropstat.Status.IsOk()) {
						break;
					}

					var out = [];
					for (var p in oPropstat.PropertiesByNames) {
						out.push(p);
					}

					if ('undefined' != typeof oPropstat.PropertiesByNames[oSupportedLock]) {
						var oProp = oPropstat.PropertiesByNames[oSupportedLock];
						try {
							return ITHit.WebDAV.Client.HierarchyItem.ParseSupportedLock(oProp.Value);
						} catch (e) {
							break;
						}
					}
				}

				return [];
			},

			/**
			 * Parse supported locks.
			 * @param {ITHit.XMLDoc} oSupportedLockProp XML DOM lock property element.
			 * @returns {Array} Supported locks.
			 */
			ParseSupportedLock: function(oSupportedLockProp) {

				var aLocks = [];

				// Create namespace resolver.
				var oResolver = new ITHit.XPath.resolver();
				oResolver.add('d', ITHit.WebDAV.Client.DavConstants.NamespaceUri);

				var oNode       = null;
				var oNode1      = null;
				var iNodeElType = ITHit.XMLDoc.nodeTypes.NODE_ELEMENT;

				var oRes  = ITHit.XPath.evaluate('d:lockentry', oSupportedLockProp, oResolver);
				while ( oNode = oRes.iterateNext() ) {

					var oRes1     = ITHit.XPath.evaluate('d:*', oNode, oResolver);
					while (oNode1 = oRes1.iterateNext()) {

						if (oNode1.nodeType() == iNodeElType) {

							var sNodeName = '';

							if (oNode1.hasChildNodes()) {

								var oChildNode = oNode1.firstChild();
								while (oChildNode) {
									if (oChildNode.nodeType() == iNodeElType) {
										sNodeName = oChildNode.localName();
										break;
									}
									oChildNode = oChildNode.nextSibling();
								}
							}
							else {
								sNodeName = oNode1.localName();
							}

							switch (sNodeName.toLowerCase()) {

								case 'shared':
									aLocks.push(ITHit.WebDAV.Client.LockScope.Shared);
									break;

								case 'exclusive':
									aLocks.push(ITHit.WebDAV.Client.LockScope.Exclusive);
									break;
							}
						}
					}
				}

				return aLocks;
			},

			/**
			 * Retrieves information about quota available bytes.
			 * @returns {number} Available bytes.
			 */
			GetQuotaAvailableBytes: function(oResp) {

				var oAvailableBytes = ITHit.WebDAV.Client.DavConstants.QuotaAvailableBytes;

				for (var i = 0; i < oResp.Propstats.length; i++ ) {
					var oPropstat = oResp.Propstats[i];

					if (!oPropstat.Status.IsOk()) {
						break;
					}

					if ('undefined' != typeof oPropstat.PropertiesByNames[oAvailableBytes]) {
						var oProp = oPropstat.PropertiesByNames[oAvailableBytes];
						try {
							return parseInt(oProp.Value.firstChild().nodeValue());
						} catch (e) {
							break;
						}
					}
				}

				return -1;
			},

			/**
			 * Retrieves information about quota used bytes.
			 * @returns {number} Used bytes.
			 */
			GetQuotaUsedBytes: function(oResp) {

				var oUsedBytes = ITHit.WebDAV.Client.DavConstants.QuotaUsedBytes;

				for (var i = 0; i < oResp.Propstats.length; i++ ) {
					var oPropstat = oResp.Propstats[i];

					if (!oPropstat.Status.IsOk()) {
						break;
					}

					if ('undefined' != typeof oPropstat.PropertiesByNames[oUsedBytes]) {
						var oProp = oPropstat.PropertiesByNames[oUsedBytes];
						try {
							return parseInt(oProp.Value.firstChild().nodeValue());
						} catch (e) {
							break;
						}
					}
				}

				return -1;
			},

			/**
			 * Retrieves information about checked-in item.
			 * @returns {Array|boolean} Array checked-in files or false, if versions is not supported
			 */
			GetCkeckedIn: function(oResp) {

				var CheckedIn = ITHit.WebDAV.Client.DavConstants.CheckedIn;

				for (var i = 0; i < oResp.Propstats.length; i++ ) {
					var oPropstat = oResp.Propstats[i];

					if (!oPropstat.Status.IsOk()) {
						break;
					}

					if ('undefined' != typeof oPropstat.PropertiesByNames[CheckedIn]) {
						var oProp = oPropstat.PropertiesByNames[CheckedIn];
						try {
							return ITHit.WebDAV.Client.HierarchyItem.ParseChecked(oProp.Value);
						} catch (e) {
							break;
						}
					}
				}

				return false;
			},

			/**
			 * Retrieves information about checked-out item.
			 * @returns {number} Used bytes.
			 */
			GetCheckedOut: function(oResp) {

				var CheckedIn = ITHit.WebDAV.Client.DavConstants.CheckedOut;

				for (var i = 0; i < oResp.Propstats.length; i++ ) {
					var oPropstat = oResp.Propstats[i];

					if (!oPropstat.Status.IsOk()) {
						break;
					}

					if ('undefined' != typeof oPropstat.PropertiesByNames[CheckedIn]) {
						var oProp = oPropstat.PropertiesByNames[CheckedIn];
						try {
							return ITHit.WebDAV.Client.HierarchyItem.ParseChecked(oProp.Value);
						} catch (e) {
							break;
						}
					}
				}

				return false;
			},

			/**
			 * Parse checked-in/out files.
			 * @param {ITHit.XMLDoc} oCheckedProp XML DOM lock property element.
			 * @returns {Array} Checked files.
			 */
			ParseChecked: function(oCheckedProp) {

				var aCheckeds = [];

				// Create namespace resolver.
				var oResolver = new ITHit.XPath.resolver();
				oResolver.add('d', ITHit.WebDAV.Client.DavConstants.NamespaceUri);

				var oNode       = null;
				var iNodeElType = ITHit.XMLDoc.nodeTypes.NODE_ELEMENT;

				var oRes  = ITHit.XPath.evaluate('d:href', oCheckedProp, oResolver);
				while ( oNode = oRes.iterateNext() ) {
					if (oNode.nodeType() == iNodeElType) {
						aCheckeds.push(oNode.firstChild().nodeValue());
					}
				}

				return aCheckeds;
			},

			/**
			 * Get resource type.
			 * @param {ITHit.WebDAV.Client.WebDavResponse} oResponse Response object.
			 * @returns {string} Resource type.
			 */
			GetResourceType: function(oResponse) {

				var oProperty = ITHit.WebDAV.Client.HierarchyItem.GetProperty(oResponse, ITHit.WebDAV.Client.DavConstants.ResourceType);
				var sResourceType = ITHit.WebDAV.Client.ResourceType.File;

				if (oProperty.Value.getElementsByTagNameNS(ITHit.WebDAV.Client.DavConstants.NamespaceUri, 'collection').length > 0) {
					sResourceType = ITHit.WebDAV.Client.ResourceType.Folder;
				}

				return sResourceType;
			},

			/**
			 * Has property.
			 * @param {ITHit.WebDAV.Client.WebDavResponse} oResponse Response object.
			 * @param {ITHit.WebDAV.Client.PropertyName} oPropName Property name.
			 * @returns {boolean} Searched property exits.
			 */
			HasProperty: function(oResponse, oPropName) {

				for ( var i = 0; i < oResponse.Propstats.length; i++ ) {
					var oPropstat = oResponse.Propstats[i];
					for ( var j = 0; j < oPropstat.Properties.length; j++ ) {
						var oProperty = oPropstat.Properties[j];
						if (oProperty.Name.Equals(oPropName)) {
							return true;
						}
					}
				}

				return false;
			},

			/**
			 * Get property.
			 * @param {ITHit.WebDAV.Client.WebDavResponse} oResponse Response object.
			 * @param {ITHit.WebDAV.Client.PropertyName} oPropName Property name.
			 * @returns {ITHit.WebDAV.Client.Property} Searched property.
			 * @throws ITHit.WebDAV.Client.Exceptions.PropertyNotFoundException Property was not found.
			 */
			GetProperty: function(oResponse, oPropName) {

				for ( var i = 0; i < oResponse.Propstats.length; i++ ) {
					var oPropstat = oResponse.Propstats[i];
					for ( var j = 0; j < oPropstat.Properties.length; j++ ) {
						var oProperty = oPropstat.Properties[j];
						if (oProperty.Name.Equals(oPropName)) {
							return oProperty;
						}
					}
				}

				throw new ITHit.WebDAV.Client.Exceptions.PropertyNotFoundException(ITHit.Phrases.Exceptions.PropertyNotFound, oResponse.Href, oPropName, null, null);
			},

			/**
			 * Get custom properties key-value object.
			 * @param {ITHit.WebDAV.Client.WebDavResponse} oResponse Response object.
			 * @returns {ITHit.WebDAV.Client.Property[]} Custom properties.
			 */
			GetPropertiesFromResponse: function(oResponse) {
				var aProperties = [];

				for ( var i = 0; i < oResponse.Propstats.length; i++ ) {
					var oPropstat = oResponse.Propstats[i];
					for ( var i2 = 0; i2 < oPropstat.Properties.length; i2++ ) {
						aProperties.push(oPropstat.Properties[i2]);
					}
				}

				return aProperties;
			},

			/**
			 * Get item's name.
			 * @param {ITHit.WebDAV.Client.WebDavResponse} oResponse Response object.
			 * @returns {string} Item's name.
			 */
			GetDisplayName: function(oResponse) {

				var oElement = ITHit.WebDAV.Client.HierarchyItem.GetProperty(oResponse,  ITHit.WebDAV.Client.DavConstants.DisplayName).Value;
				var sName;

				if (oElement.hasChildNodes()) {
					sName = oElement.firstChild().nodeValue();
				} else {
					sName = ITHit.WebDAV.Client.Encoder.Decode(ITHit.WebDAV.Client.HierarchyItem.GetLastName(oResponse.Href));
				}

				return sName;
			},

			/**
			 * Get item's last modified date.
			 * @param {ITHit.WebDAV.Client.WebDavResponse} oResponse Response object.
			 * @returns {Date} Item's last modified date.
			 */
			GetLastModified: function(oResponse) {
                var oProp;
                try {
				    oProp = ITHit.WebDAV.Client.HierarchyItem.GetProperty(oResponse, ITHit.WebDAV.Client.DavConstants.GetLastModified);
                } catch (e) {
                    if (!(e instanceof ITHit.WebDAV.Client.Exceptions.PropertyNotFoundException)) {
                        throw e;
                    }
                    return null;
                }

				return ITHit.WebDAV.Client.HierarchyItem.GetDate(oProp.Value.firstChild().nodeValue(), 'rfc1123');
			},

			/**
			 * Get item's content type.
			 * @param {ITHit.WebDAV.Client.WebDavResponse} oResponse Response object.
			 * @returns {string} Item's content type.
			 */
			GetContentType: function(oResponse) {

				var sContentType = null;
				var oValue = ITHit.WebDAV.Client.HierarchyItem.GetProperty(oResponse, ITHit.WebDAV.Client.DavConstants.GetContentType).Value;
				if (oValue.hasChildNodes()) {
					sContentType = oValue.firstChild().nodeValue();
				}
				return sContentType;
			},

			/**
			 * Get file content length.
			 * @param {ITHit.WebDAV.Client.WebDavResponse} oResponse Response object.
			 * @returns {number|null} Content length.
			 */
			GetContentLength: function(oResponse) {

				var iContentLength = 0;
                try {
                    var oValue = ITHit.WebDAV.Client.HierarchyItem.GetProperty(oResponse, ITHit.WebDAV.Client.DavConstants.GetContentLength).Value;
                    if (oValue.hasChildNodes()) {
                        iContentLength = parseInt(oValue.firstChild().nodeValue());
                    }
                } catch (e) {
                    if (!(e instanceof ITHit.WebDAV.Client.Exceptions.PropertyNotFoundException)) {
                        throw e;
                    }
                    return null;
                }
				return iContentLength;
			},

			/**
			 * Get item's creating date.
			 * @param {ITHit.WebDAV.Client.WebDavResponse} oResponse Response object.
			 * @returns {Date} Item's creation date.
			 */
			GetCreationDate: function(oResponse) {
                var oProp;
                try {
                    oProp = ITHit.WebDAV.Client.HierarchyItem.GetProperty(oResponse, ITHit.WebDAV.Client.DavConstants.CreationDate);
                } catch (e) {
                    if (!(e instanceof ITHit.WebDAV.Client.Exceptions.PropertyNotFoundException)) {
                        throw e;
                    }
                    return null;
                }

				return ITHit.WebDAV.Client.HierarchyItem.GetDate(oProp.Value.firstChild().nodeValue(), 'tz');
			},

			GetDate: function(sDate, sDateFormat) {

				var oDate;
				var i = 0; // rfc1123

				if ('tz' == sDateFormat) {
					i++; // tz
				}

				if (!sDate) {
					return new Date(0);
				}

				for (var e = i + 1; i <= e; i++) {

					if (0 == i % 2) {

						// rfc1123
						var oDate = new Date(sDate);

						if (!isNaN(oDate)) {
							break;
						}
					}
					else {

						// tz
						var aTime = sDate.match(/([\d]{4})\-([\d]{2})\-([\d]{2})T([\d]{2}):([\d]{2}):([\d]{2})(\.[\d]+)?((?:Z)|(?:[\+\-][\d]{2}:[\d]{2}))/);

						if (aTime && aTime.length >= 7) {

							aTime.shift();

							var oDate = new Date(aTime[0], aTime[1] - 1, aTime[2], aTime[3], aTime[4], aTime[5]);

							var iCounter = 6;
							if (('undefined' != typeof aTime[iCounter]) && (-1 != aTime[iCounter].indexOf('.'))) {
								oDate.setMilliseconds(aTime[iCounter].replace(/[^\d]/g, ''));
							}
							iCounter++;

							if (('undefined' != typeof aTime[iCounter]) && ('-00:00' != aTime[iCounter]) && (-1 != aTime[iCounter].search(/(?:\+|-)/))) {

								var aParts = aTime[iCounter].slice(1).split(':');
								var iOffset = parseInt(aParts[1]) + (60 * aParts[0]);

								if ('+' == aTime[iCounter][0]) {
									oDate.setMinutes(oDate.getMinutes() - iOffset);
								}
								else {
									oDate.setMinutes(oDate.getMinutes() + iOffset);
								}

								iCounter++;
							}

							oDate.setMinutes(oDate.getMinutes() + (-1 * oDate.getTimezoneOffset()));

							break;
						}
					}
				}

				if (!oDate || isNaN(oDate)) {
					oDate = new Date(0);
				}

				return oDate;

			},

			/**
			 * Get folder's absolute path.
			 * @param {string} sHref Folder's URL.
			 * @returns {string} Folder's URL.
			 */
			GetAbsoluteUriPath: function(sHref) {
				return sHref.replace(/\/?$/, '/');
			},

			/**
			 * Get path without host.
			 * @param {string} sHref Folder's URL.
			 * @return {string} Folder's URL.
			 */
			GetRelativePath: function(sHref) {
				return sHref.replace(/^[a-z]+\:\/\/[^\/]+\//, '\/');
			},

			GetLastName: function(sHref) {

				// Get relative path.
				var sName = ITHit.WebDAV.Client.HierarchyItem.GetRelativePath(sHref).replace(/\/$/, '');

				return sName.match(/[^\/]*$/)[0];
			},

			/**
			 * Check whether hrefs are equals.
			 * @param {string} sHref1 URL 1.
			 * @param {string} sHref2 URL 2.
			 * @returns {boolean} True if URLs are equals, false otherwise.
			 */
			HrefEquals: function(sHref1, sHref2) {

				// TODO: Uncomment when encoding special characters on server will be fixed.
				//	var iPos         = sHref1.indexOf('?');
				//	if (-1 != iPos) {
				//		sHref1 = sHref1.substr(0, iPos);
				//	}
				//	var iPos         = sHref1.indexOf('#');
				//	if (-1 != iPos) {
				//		sHref1 = sHref1.substr(0, iPos);
				//	}

				//	var iPos         = sHref2.indexOf('?');
				var iPos         = sHref2.search(/\?[^\/]+$/);
				if (-1 != iPos) {
					sHref2 = sHref2.substr(0, iPos);
				}
				//	var iPos         = sHref2.indexOf('#');
				var iPos         = sHref2.search(/\?[^\/]+$/);
				if (-1 != iPos) {
					sHref2 = sHref2.substr(0, iPos);
				}

				return ITHit.WebDAV.Client.HierarchyItem.GetRelativePath(ITHit.WebDAV.Client.Encoder.Decode(sHref1)).replace(/\/$/, '') == ITHit.WebDAV.Client.HierarchyItem.GetRelativePath(ITHit.WebDAV.Client.Encoder.Decode(sHref2)).replace(/\/$/, '');
			},

			/**
			 * Get folder parent path.
			 * @param {string} sHref Folder URL.
			 * @returns {string} Folder parent URL.
			 */
			GetFolderParentUri: function(sHref) {

				// Get parent folder URI.
				var sHost = /^https?\:\/\//.test(sHref) ? sHref.match(/^https?\:\/\/[^\/]+/)[0] + '/' : '/';
				var sPath = ITHit.WebDAV.Client.HierarchyItem.GetRelativePath(sHref);
				sPath = sPath.replace(/\/?$/, '');

				if (sPath === '') {
					return null;
				}

				sPath = sPath.substr(0, sPath.lastIndexOf('/') + 1);
				sPath = sPath.substr(1);

				return sHost + sPath;
			},

			/**
			 * Get host from URL.
			 * @param {string} sHref Item's URL.
			 * @returns {string} Server host.
			 */
			GetHost: function(sHref) {

				var sHost;

				if (/^https?\:\/\//.test(sHref)) {
					sHost = sHref.match(/^https?\:\/\/[^\/]+/)[0] + '/';
				}
				else {
					sHost = location.protocol +'//'+ location.host +'/';
				}

				return sHost;
			},

			GetPropertyValuesFromMultiResponse: function(oMultiResponses, sHref) {

				for (var i = 0; i < oMultiResponses.Responses.length; i++) {
					var oResponse = oMultiResponses.Responses[i];

					if (!ITHit.WebDAV.Client.HierarchyItem.HrefEquals(oResponse.Href, sHref)) {
						continue;
					}

					var oProperties = [];
					for (var j = 0; j < oResponse.Propstats.length; j++) {
						var oPropstat = oResponse.Propstats[j];
						if (!oPropstat.Properties.length) {
							continue;
						}

						// Success
						if (oPropstat.Status.IsSuccess()) {

							for (var k = 0; k < oPropstat.Properties.length; k++) {
								var oProperty = oPropstat.Properties[k];

								// Add only custom properties.
								if (!oProperty.Name.IsStandardProperty()) {
									oProperties.push(oProperty);
								}
							}
							continue;
						}

						if (oPropstat.Status.Equals(ITHit.WebDAV.Client.HttpStatus.NotFound)) {
							throw new ITHit.WebDAV.Client.Exceptions.PropertyNotFoundException(ITHit.Phrases.Exceptions.PropertyNotFound, sHref, oPropstat.Properties[0].Name, new ITHit.WebDAV.Client.Exceptions.Info.PropertyMultistatus(oMultiResponses), null);
						}

						if (oPropstat.Status.Equals(ITHit.WebDAV.Client.HttpStatus.Forbidden)) {
							throw new ITHit.WebDAV.Client.Exceptions.PropertyForbiddenException(ITHit.Phrases.Exceptions.PropertyForbidden, sHref, oPropstat.Properties[0].Name, new ITHit.WebDAV.Client.Exceptions.Info.PropertyMultistatus(oMultiResponses), null);
						}

						throw new ITHit.WebDAV.Client.Exceptions.PropertyException(ITHit.Phrases.Exceptions.PropertyFailed, sHref, oPropstat.Properties[0].Name, new ITHit.WebDAV.Client.Exceptions.Info.PropertyMultistatus(oMultiResponses), oPropstat.Status, null);
					}
					return oProperties;
				}

				throw new ITHit.WebDAV.Client.Exceptions.WebDavException(ITHit.Phrases.ResponseItemNotFound.Paste(sHref));
			},

			GetPropertyNamesFromMultiResponse: function(oMultiResponses, sHref) {

				var oPropertyNames = [];
				var oProperties = this.GetPropertyValuesFromMultiResponse(oMultiResponses, sHref);
				for (var i = 0, l = oProperties.length; i < l; i++) {
					oPropertyNames.push(oProperties[i].Name);
				}

				return oPropertyNames;
			},

			GetSourceFromMultiResponse: function(oResponses, sHref) {

				for (var i = 0; i < oResponses.length; i++) {
					var oResponse = oResponses[i];

					if (!ITHit.WebDAV.Client.HierarchyItem.HrefEquals(oResponse.Href, sHref)) {
						continue;
					}

					var oSources = []
					for (var j = 0; j < oResponse.Propstats; j++) {
						var oPropstat = oResponse.Propstats[j];

						if (!oPropstat.Status.IsOk()) {
							if (oPropstat.Status.Equals(ITHit.WebDAV.Client.HttpStatus.NotFound)) {
								return null;
							}

							throw new ITHit.WebDAV.Client.Exceptions.PropertyForbiddenException(
								ITHit.Phrases.PropfindFailedWithStatus.Paste(oPropstat.Status.Description),
								sHref,
								oPropstat.Properties[0].Name,
								new ITHit.WebDAV.Client.Exceptions.Info.Multistatus(oResponse)
							);
						}

						for (var k = 0; k < oPropstat.Properties.length; k++) {
							var oProperty = oPropstat.Properties[k];

							if (oProperty.Name.Equals(ITHit.WebDAV.Client.DavConstants.Source)) {
								var aLinks = oProperty.Value.GetElementsByTagNameNS(DavConstants.NamespaceUri, DavConstants.Link);
								for (var l = 0; l < aLinks.length; l++) {
									var oLink = aLinks[i];
									var oSource = new ITHit.WebDAV.Client.Source(
										oLink.GetElementsByTagName(ITHit.WebDAV.Client.DavConstants.NamespaceUri, ITHit.WebDAV.Client.DavConstants.Src)[0].firstChild().nodeValue(),
										oLink.GetElementsByTagName(ITHit.WebDAV.Client.DavConstants.NamespaceUri, ITHit.WebDAV.Client.DavConstants.Dst)[0].firstChild().nodeValue()
									);
									oSources.push(oSource);
								}

								return oSources;
							}
						}
					}
				}

				throw new ITHit.WebDAV.Client.Exceptions.WebDavException(ITHit.Phrases.ResponseItemNotFound.Paste(sHref));
			}

		},

		/**
		 * Current WebDAV session.
		 * @api
		 * @type {ITHit.WebDAV.Client.WebDavSession}
		 */
		Session: null,

		/**
		 * This item path on the server.
		 * @api
		 * @type {string}
		 */
		Href: null,

		/**
		 * Most recent modification date.
		 * @api
		 * @type {Date}
		 */
		LastModified: null,

		/**
		 * User friendly item name.
		 * @api
		 * @type {string}
		 */
		DisplayName: null,

		/**
		 * The date item was created.
		 * @api
		 * @type {Date}
		 */
		CreationDate: null,

		/**
		 * Type of the item (File or Folder).
		 * @api
		 * @type {string}
		 * @see ITHit.WebDAV.Client.ResourceType
		 */
		ResourceType: null,

		/**
		 * Retrieves information about supported locks. Item can support exclusive, shared locks or do not support
		 * any locks. If you set exclusive lock other users will not be able to set any locks. If you set shared
		 * lock other users will be able to set shared lock on the item.
		 * @api
		 * @examplecode ITHit.WebDAV.Client.Tests.Locks.CheckSupport.CheckLockSupport
		 * @type {Array}
		 * @see ITHit.WebDAV.Client.LockScope
		 */
		SupportedLocks: null,

		/**
		 * Retrieves locks for this item.
		 * @examplecode ITHit.WebDAV.Client.Tests.Locks.GetLocks.GetList
		 * @api
		 * @type {Array}
		 */
		ActiveLocks: null,

		/**
		 * List of item properties.
		 * @api
		 * @type {ITHit.WebDAV.Client.Property[]}
		 */
		Properties: null,

		/**
		 * Returns true if file is under version control. Otherwise false. To detect if version control could
		 * be enabled for this item call SupportedFeaturesAsync and check for VersionControl token.
		 * To enable version control call PutUnderVersionControlAsync.
		 * @api
		 * @returns {boolean} Boolean, if true - versions supported
		 */
		VersionControlled: null,

		/**
		 * Server host.
		 * @type {string}
		 */
		Host: null,

		/**
		 * Number of bytes available for this user on server. -1 if server does not support Quota.
		 * @api
		 * @type {number}
		 */
		AvailableBytes: null,

		/**
		 * Number of bytes used by this user on server. -1 if server does not support Quota.
		 * @api
		 * @type {number}
		 */
		UsedBytes: null,

		/**
		 * Checked-in files list
		 * @type {Array|boolean}
		 */
		CheckedIn: null,

		/**
		 * Checked-in files list
		 * @type {Array|boolean}
		 */
		CheckedOut: null,

		/**
		 * Server version (engine header)
		 * @type {Array}
		 */
		ServerVersion: null,

		/**
		 * @type {string}
		 */
		_Url: null,

		/**
		 * @type {string}
		 */
		_AbsoluteUrl: null,

		/**
		 * Create new instance of HierarchyItem class which represents one WebDAV item (file, folder or lock-null).
		 * @param {ITHit.WebDAV.Client.WebDavSession} oSession Current WebDAV session
		 * @param {string} sHref This item path on the server.
		 * @param {Date} oLastModified Most recent modification date.
		 * @param {string} sDisplayName User friendly item name.
		 * @param {Date} oCreationDate The date item was created.
		 * @param {string} sResourceType Type of this item, see ResourceType.
		 * @param {Array} aSupportedLocks
		 * @param {Array} aActiveLocks
		 * @param {string} sHost
		 * @param {number} iAvailableBytes
		 * @param {number} iUsedBytes
		 * @param {Array|boolean} aCheckedIn
		 * @param {Array|boolean} aCheckedOut
		 * @param {object} aProperties
		 */
		constructor: function(oSession, sHref, oLastModified, sDisplayName, oCreationDate, sResourceType, aSupportedLocks, aActiveLocks, sHost, iAvailableBytes, iUsedBytes, aCheckedIn, aCheckedOut, aProperties) {
			this.Session = oSession;
			this.ServerVersion = oSession.ServerEngine;
			this.Href = sHref;
			this.LastModified = oLastModified;
			this.DisplayName = sDisplayName;
			this.CreationDate = oCreationDate;
			this.ResourceType = sResourceType;
			this.SupportedLocks = aSupportedLocks;
			this.ActiveLocks = aActiveLocks;
			this.Host = sHost;
			this.AvailableBytes = iAvailableBytes;
			this.UsedBytes = iUsedBytes;
			this.CheckedIn = aCheckedIn;
			this.CheckedOut = aCheckedOut;
			this.Properties = aProperties || [];

			this.VersionControlled = this.CheckedIn !== false || this.CheckedOut !== false;

			// Add shortcuts, used in AjaxFileBrowser
			this._AbsoluteUrl = ITHit.Decode(this.Href);
			this._Url = this._AbsoluteUrl.replace(/^http[s]?:\/\/[^\/]+\/?/, '\/');
		},

		/**
		 *
		 * @returns {boolean}
		 */
		IsFolder: function() {
			return false;
		},

		/**
		 * @param {string|ITHit.WebDAV.Client.HierarchyItem} mItem is absolute/relative url or HierarchyItem instance
		 * @returns {boolean}
		 */
		IsEqual: function(mItem) {
			if (mItem instanceof ITHit.WebDAV.Client.HierarchyItem) {
				return this.Href === mItem.Href;
			}

			if (ITHit.Utils.IsString(mItem)) {
				if (mItem.indexOf('://') !== -1 || mItem.indexOf(':\\') !== -1) {
					return this.GetAbsoluteUrl() === mItem;
				}

				return this.GetUrl() === mItem;
			}

			return false;
		},

		/**
		 * @returns {string}
		 */
		GetUrl: function() {
			return this._Url;
		},

		/**
		 * @returns {string}
		 */
		GetAbsoluteUrl: function() {
			return this._AbsoluteUrl;
		},

		/**
		 * Check to property exists
		 * @param {ITHit.WebDAV.Client.PropertyName} oPropName Property name.
		 * @returns {boolean}
		 */
		HasProperty: function(oPropName) {
			for (var i = 0, l = this.Properties.length; i < l; i++) {
				if (oPropName.Equals(this.Properties[i].Name)) {
					return true;
				}
			}
			return false;
		},

		/**
		 * Get additional property
		 * @api
		 * @param {ITHit.WebDAV.Client.PropertyName} oPropName Property name.
		 * @returns {*}
		 */
		GetProperty: function(oPropName) {
			for (var i = 0, l = this.Properties.length; i < l; i++) {
				if (oPropName.Equals(this.Properties[i].Name)) {
					return this.Properties[i].Value.firstChild().nodeValue();
				}
			}

			throw new ITHit.WebDAV.Client.Exceptions.PropertyNotFoundException('Not found property `' + oPropName.toString() + '` in resource `' + this.Href + '`.');
		},

		/**
		 * Refreshes item loading data from server.
		 * @api
		 * @deprecated Use asynchronous method instead
		 * @throws ITHit.WebDAV.Client.Exceptions.UnauthorizedException Incorrect credentials provided or insufficient permissions to access the requested item.
		 * @throws ITHit.WebDAV.Client.Exceptions.NotFoundException The requested folder doesn't exist on the server.
		 * @throws ITHit.WebDAV.Client.Exceptions.ForbiddenException The server refused to fulfill the request.
		 * @throws ITHit.WebDAV.Client.Exceptions.WebDavException Unexpected error occurred.
		 */
		Refresh: function() {
			var oRequest = this.Session.CreateRequest(this.__className + '.Refresh()');

			var aProperties = [];
			for (var i = 0, l = this.Properties.length; i < l; i++) {
				aProperties.push(this.Properties[i].Name);
			}

			var oItem = self.OpenItem(oRequest, this.Href, aProperties);
			for (var key in oItem) {
				if (oItem.hasOwnProperty(key)) {
					this[key] = oItem[key];
				}
			}

			oRequest.MarkFinish();
		},

		/**
		 * Callback function to be called when item data loaded from server and item is refreshed.
		 * @callback ITHit.WebDAV.Client.HierarchyItem~RefreshAsyncCallback
		 * @param {ITHit.WebDAV.Client.AsyncResult} oResult Result object
		 */

		/**
		 * Refreshes item loading data from server.
		 * @api
		 * @examplecode ITHit.WebDAV.Client.Tests.HierarchyItems.Refresh.Refresh
		 * @param {ITHit.WebDAV.Client.HierarchyItem~RefreshAsyncCallback} fCallback Function to call when operation is completed.
		 * @returns {ITHit.WebDAV.Client.Request} Request object.
		 */
		RefreshAsync: function(fCallback) {
			var that = this;
			var oRequest = this.Session.CreateRequest(this.__className + '.RefreshAsync()');

			var aProperties = [];
			for (var i = 0, l = this.Properties.length; i < l; i++) {
				aProperties.push(this.Properties[i].Name);
			}

			self.OpenItemAsync(oRequest, this.Href, aProperties, function(oAsyncResult) {
				if (oAsyncResult.IsSuccess) {
					for (var key in oAsyncResult.Result) {
						if (oAsyncResult.Result.hasOwnProperty(key)) {
							that[key] = oAsyncResult.Result[key];
						}
					}
					oAsyncResult.Result = null;
				}

				oRequest.MarkFinish();
				fCallback(oAsyncResult);
			});

			return oRequest;
		},

		/**
		 * Copies this item to destination folder.
		 * @api
		 * @deprecated Use asynchronous method instead
		 * @param {ITHit.WebDAV.Client.Folder} oDestinationFolder Folder to move to.
		 * @param {string} sDestinationName Name to assign to copied item.
		 * @param {boolean} bDeep Indicates whether children of this item should be copied.
		 * @param {boolean} bOverwrite Whether existing destination item shall be overwritten.
		 * @param {ITHit.WebDAV.Client.LockUriTokenPair[]} [oLockTokens] Lock tokens for destination folder.
		 * @throws ITHit.WebDAV.Client.Exceptions.ForbiddenException The source and destination URIs are the same.
		 * @throws ITHit.WebDAV.Client.Exceptions.LockedException The destination folder or items to be overwritten were locked.
		 * @throws ITHit.WebDAV.Client.Exceptions.PreconditionFailedException The destination item exists and overwrite was false.
		 * @throws ITHit.WebDAV.Client.Exceptions.NotFoundException This item doesn't exist on the server.
		 * @throws ITHit.WebDAV.Client.Exceptions.WebDavHttpException Server returned unknown error for specific resource.
		 * @throws ITHit.WebDAV.Client.Exceptions.WebDavException Unexpected error occurred.
		 */
		CopyTo: function(oDestinationFolder, sDestinationName, bDeep, bOverwrite, oLockTokens) {
			oLockTokens = oLockTokens || null;

			var oRequest = this.Session.CreateRequest(this.__className + '.CopyTo()');

			var oResult = ITHit.WebDAV.Client.Methods.CopyMove.Go(
				oRequest,
				ITHit.WebDAV.Client.Methods.CopyMove.Mode.Copy,
				this.Href,
				ITHit.WebDAV.Client.HierarchyItem.AppendToUri(oDestinationFolder.Href, sDestinationName),
				this.ResourceType === ITHit.WebDAV.Client.ResourceType.Folder,
				bDeep,
				bOverwrite,
				oLockTokens,
				this.Host
			);

			var oError = this._GetErrorFromCopyResponse(oResult.Response);
			if (oError) {
				oRequest.MarkFinish();
				throw oError;
			}

			oRequest.MarkFinish();
		},

		/**
		 * Callback function to be called when copy operation is complete on server.
		 * @callback ITHit.WebDAV.Client.HierarchyItem~CopyToAsyncCallback
		 * @param {ITHit.WebDAV.Client.AsyncResult} oResult Result object
		 */

		/**
		 * Copies this item to destination folder.
		 * @api
		 * @examplecode ITHit.WebDAV.Client.Tests.HierarchyItems.CopyMove.Copy
		 * @param {ITHit.WebDAV.Client.Folder} oDestinationFolder Folder to move to.
		 * @param {string} sDestinationName Name to assign to copied item.
		 * @param {boolean} bDeep Indicates whether children of this item should be copied.
		 * @param {boolean} bOverwrite Whether existing destination item shall be overwritten.
		 * @param {ITHit.WebDAV.Client.LockUriTokenPair[]} [oLockTokens] Lock tokens for destination folder.
		 * @param {ITHit.WebDAV.Client.HierarchyItem~CopyToAsyncCallback} fCallback Function to call when operation is completed.
		 */
		CopyToAsync: function(oDestinationFolder, sDestinationName, bDeep, bOverwrite, oLockTokens, fCallback) {
			oLockTokens = oLockTokens || null;

			var oRequest = this.Session.CreateRequest(this.__className + '.CopyToAsync()');

			var that = this;
			ITHit.WebDAV.Client.Methods.CopyMove.GoAsync(
				oRequest,
				ITHit.WebDAV.Client.Methods.CopyMove.Mode.Copy,
				this.Href,
				ITHit.WebDAV.Client.HierarchyItem.AppendToUri(oDestinationFolder.Href, sDestinationName),
				(this.ResourceType == ITHit.WebDAV.Client.ResourceType.Folder),
				bDeep,
				bOverwrite,
				oLockTokens,
				this.Host,
				function (oAsyncResult) {
					if (oAsyncResult.IsSuccess) {
						oAsyncResult.Error = that._GetErrorFromCopyResponse(oAsyncResult.Result.Response);
						if (oAsyncResult.Error !== null) {
							oAsyncResult.IsSuccess = false;
							oAsyncResult.Result = null;
						}
					}

					oRequest.MarkFinish();
					fCallback(oAsyncResult);
				}
			);

			return oRequest;
		},

		/**
		 * Deletes this item.
		 * @api
		 * @deprecated Use asynchronous method instead
		 * @param {ITHit.WebDAV.Client.LockUriTokenPair} [oLockTokens] Lock tokens for this item or any locked child item.
		 * @throws ITHit.WebDAV.Client.Exceptions.LockedException This folder or any child item is locked and no or invalid lock token was specified.
		 * @throws ITHit.WebDAV.Client.Exceptions.ForbiddenException User has not enough rights to perform this operation.
		 * @throws ITHit.WebDAV.Client.Exceptions.MethodNotAllowedException Trying to delete lock-null item. Lock-null items must be deleted using Unlock method.
		 * @throws ITHit.WebDAV.Client.Exceptions.WebDavHttpException Server returned unknown error.
		 * @throws ITHit.WebDAV.Client.Exceptions.WebDavException Unexpected error occurred.
		 */
		Delete: function(oLockTokens) {
			oLockTokens = oLockTokens || null;

			var oRequest = this.Session.CreateRequest(this.__className + '.Delete()');

			var oResult = ITHit.WebDAV.Client.Methods.Delete.Go(oRequest, this.Href, oLockTokens, this.Host);

			var oError = this._GetErrorFromDeleteResponse(oResult.Response);
			if (oError) {
				oRequest.MarkFinish();
				throw oError;
			}

			oRequest.MarkFinish();
		},

		/**
		 * Callback function to be called when delete operation is complete on server.
		 * @callback ITHit.WebDAV.Client.HierarchyItem~DeleteAsyncCallback
		 * @param {ITHit.WebDAV.Client.AsyncResult} oResult Result object
		 */

		/**
		 * Deletes this item.
		 * @api
		 * @examplecode ITHit.WebDAV.Client.Tests.HierarchyItems.Delete.Delete
		 * @param {ITHit.WebDAV.Client.LockUriTokenPair} oLockTokens Lock tokens for this item or any locked child item.
		 * @param {ITHit.WebDAV.Client.HierarchyItem~DeleteAsyncCallback} fCallback Function to call when operation is completed.
		 * @returns {ITHit.WebDAV.Client.Request} Request object.
		 */
		DeleteAsync: function(oLockTokens, fCallback) {
			oLockTokens = oLockTokens || null;

			var oRequest = this.Session.CreateRequest(this.__className + '.DeleteAsync()');

			var that = this;
			ITHit.WebDAV.Client.Methods.Delete.GoAsync(oRequest, this.Href, oLockTokens, this.Host, function(oAsyncResult) {
				if (oAsyncResult.IsSuccess) {
					oAsyncResult.Error = that._GetErrorFromDeleteResponse(oAsyncResult.Result.Response);
					if (oAsyncResult.Error !== null) {
						oAsyncResult.IsSuccess = false;
						oAsyncResult.Result = null;
					}
				}

				oRequest.MarkFinish();
				fCallback(oAsyncResult);
			});

			return oRequest;
		},

		/**
		 * Returns names of all custom properties exposed by this item.
		 * @api
		 * @returns {ITHit.WebDAV.Client.PropertyName[]} List of PropertyName objects.
		 * @throws ITHit.WebDAV.Client.Exceptions.NotFoundException This item doesn't exist on the server.
		 * @throws ITHit.WebDAV.Client.Exceptions.WebDavHttpException Server returned unknown error.
		 * @throws ITHit.WebDAV.Client.Exceptions.WebDavException Unexpected error occurred.
		 */
		GetPropertyNames: function() {

			var oRequest = this.Session.CreateRequest(this.__className + '.GetPropertyNames()');

			var oResult = ITHit.WebDAV.Client.Methods.Propfind.Go(
				oRequest,
				this.Href,
				ITHit.WebDAV.Client.Methods.Propfind.PropfindMode.PropertyNames,
				null,
				ITHit.WebDAV.Client.Depth.Zero,
				this.Host
			);

			var oPropertyName = self.GetPropertyNamesFromMultiResponse(oResult.Response, this.Href);

			oRequest.MarkFinish();
			return oPropertyName;
		},

		/**
		 * Callback function to be called when property names loaded from server.
		 * @callback ITHit.WebDAV.Client.HierarchyItem~GetPropertyNamesAsyncCallback
		 * @param {ITHit.WebDAV.Client.AsyncResult} oResult Result object
		 * @param {ITHit.WebDAV.Client.PropertyName[]} oResult.Result List of PropertyName objects.
		 */

		/**
		 * Returns names of all custom properties exposed by this item.
		 * @api
		 * @examplecode ITHit.WebDAV.Client.Tests.HierarchyProperties.GetProperties.GetPropertyNames
		 * @param {ITHit.WebDAV.Client.HierarchyItem~GetPropertyNamesAsyncCallback} fCallback Function to call when operation is completed.
		 * @returns {ITHit.WebDAV.Client.Request} Request object.
		 */
		GetPropertyNamesAsync: function(fCallback) {

			var oRequest = this.Session.CreateRequest(this.__className + '.GetPropertyNamesAsync()');

			var that = this;
			ITHit.WebDAV.Client.Methods.Propfind.GoAsync(
				oRequest,
				this.Href,
				ITHit.WebDAV.Client.Methods.Propfind.PropfindMode.PropertyNames,
				null,
				ITHit.WebDAV.Client.Depth.Zero,
				this.Host,
				function(oAsyncResult) {
					if (oAsyncResult.IsSuccess) {
						try {
							oAsyncResult.Result = self.GetPropertyNamesFromMultiResponse(oAsyncResult.Result.Response, that.Href);
						} catch(oError) {
							oAsyncResult.Error = oError;
							oAsyncResult.IsSuccess = false;
						}
					}

					oRequest.MarkFinish();
					fCallback(oAsyncResult);
				}
			);

			return oRequest;
		},

		/**
		 * Retrieves values of specific properties.
		 * @api
		 * @deprecated Use asynchronous method instead
		 * @param {ITHit.WebDAV.Client.PropertyName[]} [aNames] Array of requested properties with values.
		 * @returns {ITHit.WebDAV.Client.Property[]} List of Property objects.
		 * @throws ITHit.WebDAV.Client.Exceptions.NotFoundException This item doesn't exist on the server.
		 * @throws ITHit.WebDAV.Client.Exceptions.PropertyForbiddenException User has not enough rights to obtain one of requested properties.
		 * @throws ITHit.WebDAV.Client.Exceptions.PropertyNotFoundException If one of requested properties was not found.
		 * @throws ITHit.WebDAV.Client.Exceptions.PropertyException Server returned unknown error for specific property.
		 * @throws ITHit.WebDAV.Client.Exceptions.WebDavHttpException Server returned unknown error.
		 * @throws ITHit.WebDAV.Client.Exceptions.WebDavException Unexpected error occurred.
		 */
		GetPropertyValues: function(aNames) {
			aNames = aNames || null;

			var oRequest = this.Session.CreateRequest(this.__className + '.GetPropertyValues()');

			var oResult = ITHit.WebDAV.Client.Methods.Propfind.Go(
				oRequest,
				this.Href,
				ITHit.WebDAV.Client.Methods.Propfind.PropfindMode.SelectedProperties,
				aNames,
				ITHit.WebDAV.Client.Depth.Zero,
				this.Host
			);

			var oProperty = self.GetPropertyValuesFromMultiResponse(oResult.Response, this.Href);

			oRequest.MarkFinish();
			return oProperty;
		},

		/**
		 * Callback function to be called when item properties values loaded from server.
		 * @callback ITHit.WebDAV.Client.HierarchyItem~GetPropertyValuesAsyncCallback
		 * @param {ITHit.WebDAV.Client.AsyncResult} oResult Result object
		 * @param {ITHit.WebDAV.Client.Property[]} oResult.Result List of Property objects.
		 */

		/**
		 * Retrieves values of specific properties.
		 * @api
		 * @examplecode ITHit.WebDAV.Client.Tests.HierarchyProperties.GetProperties.GetPropertyValues
		 * @param {ITHit.WebDAV.Client.PropertyName[]} aNames
		 * @param {ITHit.WebDAV.Client.HierarchyItem~GetPropertyValuesAsyncCallback} fCallback Function to call when operation is completed.
		 * @returns {ITHit.WebDAV.Client.Request} Request object.
		 */
		GetPropertyValuesAsync: function(aNames, fCallback) {
			aNames = aNames || null;

			var oRequest = this.Session.CreateRequest(this.__className + '.GetPropertyValuesAsync()');

			var that = this;
			ITHit.WebDAV.Client.Methods.Propfind.GoAsync(
				oRequest,
				this.Href,
				ITHit.WebDAV.Client.Methods.Propfind.PropfindMode.SelectedProperties,
				aNames,
				ITHit.WebDAV.Client.Depth.Zero,
				this.Host,
				function(oAsyncResult) {
					if (oAsyncResult.IsSuccess) {
						try {
							oAsyncResult.Result = self.GetPropertyValuesFromMultiResponse(oAsyncResult.Result.Response, that.Href);
						} catch(oError) {
							oAsyncResult.Error = oError;
							oAsyncResult.IsSuccess = false;
						}
					}

					oRequest.MarkFinish();
					fCallback(oAsyncResult);
				}
			);

			return oRequest;
		},

		/**
		 * Retrieves all custom properties exposed by the item.
		 * @api
		 * @deprecated Use asynchronous method instead
		 * @returns {ITHit.WebDAV.Client.Property[]} List of Property objects.
		 * @throws ITHit.WebDAV.Client.Exceptions.NotFoundException This item doesn't exist on the server.
		 * @throws ITHit.WebDAV.Client.Exceptions.WebDavHttpException Server returned unknown error.
		 * @throws ITHit.WebDAV.Client.Exceptions.WebDavException Unexpected error occurred.
		 */
		GetAllProperties: function() {
			return this.GetPropertyValues(null);
		},

		/**
		 * Callback function to be called when all properties loaded from server.
		 * @callback ITHit.WebDAV.Client.HierarchyItem~GetAllPropertiesAsyncCallback
		 * @param {ITHit.WebDAV.Client.AsyncResult} oResult Result object
		 * @param {ITHit.WebDAV.Client.Property[]} oResult.Result List of Property objects.
		 */

		/**
		 * Retrieves all custom properties exposed by the item.
		 * @api
		 * @examplecode ITHit.WebDAV.Client.Tests.HierarchyProperties.GetProperties.GetAllProperties
		 * @param {ITHit.WebDAV.Client.HierarchyItem~GetAllPropertiesAsyncCallback} fCallback Function to call when operation is completed.
		 * @returns {ITHit.WebDAV.Client.Request} Request object.
		 */
		GetAllPropertiesAsync: function(fCallback) {
			return this.GetPropertyValuesAsync(null, fCallback);
		},

		/**
		 * Retrieves parent hierarchy item of this item.
		 * @api
		 * @param {ITHit.WebDAV.Client.PropertyName[]} [aProperties] Additional properties requested from server. Default is empty array.
		 * @returns {ITHit.WebDAV.Client.Folder} Parent hierarchy item of this item. Null for root item.
		 * @throws ITHit.WebDAV.Client.Exceptions.NotFoundException This item doesn't exist on the server.
		 * @throws ITHit.WebDAV.Client.Exceptions.WebDavHttpException Server returned unknown error.
		 * @throws ITHit.WebDAV.Client.Exceptions.WebDavException Unexpected error occurred.
		 */
		GetParent: function(aProperties) {
			aProperties = aProperties || [];

			var oRequest = this.Session.CreateRequest(this.__className + '.GetParent()');

			var sParentHref = ITHit.WebDAV.Client.HierarchyItem.GetFolderParentUri(ITHit.WebDAV.Client.Encoder.Decode(this.Href));
			if (sParentHref === null) {
				oRequest.MarkFinish();
				return null;
			}

			var oFolder = ITHit.WebDAV.Client.Folder.OpenItem(oRequest, sParentHref, aProperties);

			oRequest.MarkFinish();
			return oFolder;
		},

		/**
		 * Callback function to be called when parent folder loaded from server.
		 * @callback ITHit.WebDAV.Client.HierarchyItem~GetParentAsyncCallback
		 * @param {ITHit.WebDAV.Client.AsyncResult} oResult Result object
		 * @param {ITHit.WebDAV.Client.Folder} oResult.Result Parent hierarchy item of this item. Null for root item.
		 */

		/**
		 * Retrieves parent hierarchy item of this item.
		 * @api
		 * @examplecode ITHit.WebDAV.Client.Tests.HierarchyItems.GetParent.GetParent
		 * @param {ITHit.WebDAV.Client.PropertyName[]} aProperties Additional properties requested from server. Default is empty array.
		 * @param {ITHit.WebDAV.Client.HierarchyItem~GetParentAsyncCallback} fCallback Function to call when operation is completed.
		 * @returns {ITHit.WebDAV.Client.Request} Request object.
		 */
		GetParentAsync: function(aProperties, fCallback) {
			aProperties = aProperties || [];

			var oRequest = this.Session.CreateRequest(this.__className + '.GetParentAsync()');

			var sParentHref = ITHit.WebDAV.Client.HierarchyItem.GetFolderParentUri(ITHit.WebDAV.Client.Encoder.Decode(this.Href));
			if (sParentHref === null) {
				fCallback(new ITHit.WebDAV.Client.AsyncResult(null, true, null));
				return null;
			}

			ITHit.WebDAV.Client.Folder.OpenItemAsync(oRequest, sParentHref, aProperties, fCallback);

			return oRequest;
		},

		/**
		 * Retrieves media type independent links.
		 * @api
		 * @returns {ITHit.WebDAV.Client.Source[]|null} Media type independent links or null.
		 * @throws ITHit.WebDAV.Client.Exceptions.NotFoundException This item doesn't exist on the server.
		 * @throws ITHit.WebDAV.Client.Exceptions.PropertyNotFoundException If property is not supported.
		 * @throws ITHit.WebDAV.Client.Exceptions.PropertyException Server returned unknown error.
		 * @throws ITHit.WebDAV.Client.Exceptions.WebDavException Unexpected error occurred.
		 */
		GetSource: function() {
			var oRequest = this.Session.CreateRequest(this.__className + '.GetSource()');

			var oResult = ITHit.WebDAV.Client.Methods.Propfind.Go(
				oRequest,
				this.Href,
				ITHit.WebDAV.Client.Methods.Propfind.PropfindMode.SelectedProperties,
				[
					ITHit.WebDAV.Client.DavConstants.Source
				],
				ITHit.WebDAV.Client.Depth.Zero,
				this.Host
			);

			var aSource = self.GetSourceFromMultiResponse(oResult.Response.Responses, this.Href);

			oRequest.MarkFinish();
			return aSource;
		},

		/**
		 * Callback function to be called when source loaded from server.
		 * @callback ITHit.WebDAV.Client.HierarchyItem~GetSourceAsyncCallback
		 * @param {ITHit.WebDAV.Client.AsyncResult} oResult Result object
		 * @param {ITHit.WebDAV.Client.Source[]|null} oResult.Result Media type independent links or null.
		 */

		/**
		 * Retrieves media type independent links.
		 * @api
		 * @param {ITHit.WebDAV.Client.HierarchyItem~GetSourceAsyncCallback} fCallback Function to call when operation is completed.
		 * @returns {ITHit.WebDAV.Client.Request} Request object.
		 */
		GetSourceAsync: function(fCallback) {
			var oRequest = this.Session.CreateRequest(this.__className + '.GetSourceAsync()');

			var that = this;
			ITHit.WebDAV.Client.Methods.Propfind.GoAsync(
				oRequest,
				this.Href,
				ITHit.WebDAV.Client.Methods.Propfind.PropfindMode.SelectedProperties,
				[
					ITHit.WebDAV.Client.DavConstants.Source
				],
				ITHit.WebDAV.Client.Depth.Zero,
				this.Host,
				function(oAsyncResult) {
					if (oAsyncResult.IsSuccess) {
						try {
							oAsyncResult.Result = self.GetSourceFromMultiResponse(oAsyncResult.Result.Response.Responses, that.Href);
						} catch(oError) {
							oAsyncResult.Error = oError;
							oAsyncResult.IsSuccess = false;
						}
					}

					oRequest.MarkFinish();
					fCallback(oAsyncResult);
				}
			);

			return oRequest;
		},

		/**
		 * Locks the item.
		 * @api
		 * @deprecated Use asynchronous method instead
		 * @param {string} sLockScope Scope of the lock.
		 * @param {boolean} bDeep Whether to lock entire subtree.
		 * @param {string} sOwner Owner of the lock.
		 * @param {number} iTimeout Timeout after which lock expires.
		 * @returns {ITHit.WebDAV.Client.LockInfo} Instance of LockInfo with information about created lock.
		 * @throws ITHit.WebDAV.Client.Exceptions.PreconditionFailedException The included lock token was not enforceable on this resource or the server could not satisfy the request in the lockinfo XML element.
		 * @throws ITHit.WebDAV.Client.Exceptions.LockedException The resource is locked. The method has been rejected.
		 * @throws ITHit.WebDAV.Client.Exceptions.MethodNotAllowedException The item does not support locking.
		 * @throws ITHit.WebDAV.Client.Exceptions.NotFoundException The item doesn't exist on the server.
		 * @throws ITHit.WebDAV.Client.Exceptions.WebDavHttpException Server returned unknown error.
		 * @throws ITHit.WebDAV.Client.Exceptions.WebDavException Unexpected error occurred.
		 */
		Lock: function(sLockScope, bDeep, sOwner, iTimeout) {
			var oRequest = this.Session.CreateRequest(this.__className + '.Lock()');

			var oResult = ITHit.WebDAV.Client.Methods.Lock.Go(
				oRequest,
				this.Href,
				iTimeout,
				sLockScope,
				this.Host,
				bDeep,
				sOwner
			);

			// Return response object.
			oRequest.MarkFinish();
			return oResult.LockInfo;
		},

		/**
		 * Callback function to be called when item is locked on server.
		 * @callback ITHit.WebDAV.Client.HierarchyItem~LockAsyncCallback
		 * @param {ITHit.WebDAV.Client.AsyncResult} oResult Result object
		 * @param {ITHit.WebDAV.Client.LockInfo} oResult.Result Instance of LockInfo with information about created lock.
		 */

		/**
		 * Locks the item. If the lock was successfully applied, the server will return a lock token. You will pass this
		 * lock token back to the server when updating and unlocking the item. The actual lock time applied by the server
		 * may be different from the one requested by the client.
		 * @api
		 * @examplecode ITHit.WebDAV.Client.Tests.Locks.Lock.SetLock
		 * @param {string} sLockScope Scope of the lock. See LockScope Enumeration {@link ITHit.WebDAV.Client.LockScope}
		 * @param {boolean} bDeep Whether to lock entire subtree.
		 * @param {string} sOwner Owner of the lock.
		 * @param {number} iTimeout Timeout after which lock expires. Pass -1 to request an infinite timeout.
		 * @param {ITHit.WebDAV.Client.HierarchyItem~LockAsyncCallback} fCallback Function to call when operation is completed.
		 * @returns {ITHit.WebDAV.Client.Request} Request object.
		 */
		LockAsync: function(sLockScope, bDeep, sOwner, iTimeout, fCallback) {
			var oRequest = this.Session.CreateRequest(this.__className + '.LockAsync()');

			ITHit.WebDAV.Client.Methods.Lock.GoAsync(
				oRequest,
				this.Href,
				iTimeout,
				sLockScope,
				this.Host,
				bDeep,
				sOwner,
				function (oAsyncResult) {
					if (oAsyncResult.IsSuccess) {
						oAsyncResult.Result = oAsyncResult.Result.LockInfo;
					}

					oRequest.MarkFinish();
					fCallback(oAsyncResult);
				}
			);

			return oRequest;
		},

		/**
		 * Moves this item to another location.
		 * @api
		 * @deprecated Use asynchronous method instead
		 * @param {ITHit.WebDAV.Client.Folder} oDestinationFolder Folder to move to.
		 * @param {string} sDestinationName Name to assign to moved item.
		 * @param {boolean} bOverwrite Whether existing destination item shall be overwritten.
		 * @param {(string|ITHit.WebDAV.Client.LockUriTokenPair[])} [oLockTokens] Lock tokens for item to be moved, for destination folder or file to be overwritten that are locked.
		 * @throws ITHit.WebDAV.Client.Exceptions.ForbiddenException The source and destination URIs are the same.
		 * @throws ITHit.WebDAV.Client.Exceptions.ConflictException A resource cannot be created at the destination until one or more intermediate collections have been created.
		 * @throws ITHit.WebDAV.Client.Exceptions.PreconditionFailedException The destination resource exists and overwrite was false.
		 * @throws ITHit.WebDAV.Client.Exceptions.LockedException The destination folder or items to be overwritten were locked or source items were locked.
		 * @throws ITHit.WebDAV.Client.Exceptions.NotFoundException This item doesn't exist on the server.
		 * @throws ITHit.WebDAV.Client.Exceptions.WebDavHttpException Server returned unknown error for specific resource.
		 * @throws ITHit.WebDAV.Client.Exceptions.WebDavException Unexpected error occurred.
		 */
		MoveTo: function(oDestinationFolder, sDestinationName, bOverwrite, oLockTokens) {
			bOverwrite  = bOverwrite || false;
			oLockTokens = oLockTokens || null;

			var oRequest = this.Session.CreateRequest(this.__className + '.MoveTo()');

			// Check destination type.
			if (!(oDestinationFolder instanceof ITHit.WebDAV.Client.Folder)) {
				oRequest.MarkFinish();
				throw new ITHit.Exception(ITHit.Phrases.Exceptions.FolderWasExpectedAsDestinationForMoving);
			}

			// Move item.
			var oResult = ITHit.WebDAV.Client.Methods.CopyMove.Go(
				oRequest,
				ITHit.WebDAV.Client.Methods.CopyMove.Mode.Move,
				this.Href,
				ITHit.WebDAV.Client.HierarchyItem.AppendToUri(oDestinationFolder.Href, sDestinationName),
				this.ResourceType,
				true,
				bOverwrite,
				oLockTokens,
				this.Host
			);

			var oError = this._GetErrorFromMoveResponse(oResult.Response);
			if (oError !== null) {
				oRequest.MarkFinish();
				throw oError;
			}

			oRequest.MarkFinish();
		},

		/**
		 * Callback function to be called when item is moved on server.
		 * @callback ITHit.WebDAV.Client.HierarchyItem~MoveToAsyncCallback
		 * @param {ITHit.WebDAV.Client.AsyncResult} oResult Result object
		 */

		/**
		 * Moves this item to another location.
		 * @api
		 * @examplecode ITHit.WebDAV.Client.Tests.HierarchyItems.CopyMove.Move
		 * @param {ITHit.WebDAV.Client.Folder} oDestinationFolder Folder to move to.
		 * @param {string} sDestinationName Name to assign to moved item.
		 * @param {boolean} bOverwrite Whether existing destination item shall be overwritten.
		 * @param {(string|ITHit.WebDAV.Client.LockUriTokenPair[])} oLockTokens Lock tokens for item to be moved, for destination folder or file to be overwritten that are locked.
		 * @param {ITHit.WebDAV.Client.HierarchyItem~MoveToAsyncCallback} fCallback Function to call when operation is completed.
		 * @return {ITHit.WebDAV.Client.Request} Request object.
		 */
		MoveToAsync: function(oDestinationFolder, sDestinationName, bOverwrite, oLockTokens, fCallback) {
			bOverwrite  = bOverwrite || false;
			oLockTokens = oLockTokens || null;

			var oRequest = this.Session.CreateRequest(this.__className + '.MoveToAsync()');

			// Check destination type.
			if (!(oDestinationFolder instanceof ITHit.WebDAV.Client.Folder)) {
				oRequest.MarkFinish();
				throw new ITHit.Exception(ITHit.Phrases.Exceptions.FolderWasExpectedAsDestinationForMoving);
			}

			var that = this;
			ITHit.WebDAV.Client.Methods.CopyMove.GoAsync(
				oRequest,
				ITHit.WebDAV.Client.Methods.CopyMove.Mode.Move,
				this.Href,
				ITHit.WebDAV.Client.HierarchyItem.AppendToUri(oDestinationFolder.Href, sDestinationName),
				this.ResourceType,
				true,
				bOverwrite,
				oLockTokens,
				this.Host,
				function(oAsyncResult) {
					if (oAsyncResult.IsSuccess) {
						oAsyncResult.Error = that._GetErrorFromMoveResponse(oAsyncResult.Result.Response);
						if (oAsyncResult.Error !== null) {
							oAsyncResult.IsSuccess = false;
							oAsyncResult.Result = null;
						}
					}

					oRequest.MarkFinish();
					fCallback(oAsyncResult);
				}
			);

			return oRequest;
		},

		/**
		 * Prolongs the lock.
		 * @api
		 * @deprecated Use asynchronous method instead
		 * @param {string} sLockToken Identifies lock to be prolonged.
		 * @param {number} iTimeout New timeout to set.
		 * @returns {ITHit.WebDAV.Client.LockInfo} Instance of LockInfo with information about refreshed lock.
		 * @throws ITHit.WebDAV.Client.Exceptions.PreconditionFailedException The included lock token was not enforceable on this resource or the server could not satisfy the request in the lockinfo XML element.
		 * @throws ITHit.WebDAV.Client.Exceptions.LockedException The resource is locked, so the method has been rejected.
		 * @throws ITHit.WebDAV.Client.Exceptions.NotFoundException This item doesn't exist on the server.
		 * @throws ITHit.WebDAV.Client.Exceptions.WebDavHttpException Server returned unknown error.
		 * @throws ITHit.WebDAV.Client.Exceptions.WebDavException Unexpected error occurred.
		 */
		RefreshLock: function(sLockToken, iTimeout) {

			var oRequest = this.Session.CreateRequest(this.__className + '.RefreshLock()');

			var oResult = ITHit.WebDAV.Client.Methods.LockRefresh.Go(
				oRequest,
				this.Href,
				iTimeout,
				sLockToken,
				this.Host
			);

			// Return lock info object.
			oRequest.MarkFinish();
			return oResult.LockInfo;
		},

		/**
		 * Callback function to be called when item lock is refreshed on server.
		 * @callback ITHit.WebDAV.Client.HierarchyItem~RefreshLockAsyncCallback
		 * @param {ITHit.WebDAV.Client.AsyncResult} oResult Result object
		 * @param {ITHit.WebDAV.Client.LockInfo} oResult.Result Instance of LockInfo with information about refreshed lock.
		 */

		/**
		 * Prolongs the lock.
		 * @api
		 * @examplecode ITHit.WebDAV.Client.Tests.Locks.RefreshLock.RefreshLock
		 * @param {string} sLockToken Identifies lock to be prolonged.
		 * @param {number} iTimeout New timeout to set.
		 * @param {ITHit.WebDAV.Client.HierarchyItem~RefreshLockAsyncCallback} fCallback Function to call when operation is completed.
		 * @return {ITHit.WebDAV.Client.Request} Request object.
		 */
		RefreshLockAsync: function(sLockToken, iTimeout, fCallback) {

			var oRequest = this.Session.CreateRequest(this.__className + '.RefreshLockAsync()');

			ITHit.WebDAV.Client.Methods.LockRefresh.GoAsync(
				oRequest,
				this.Href,
				iTimeout,
				sLockToken,
				this.Host,
				function (oAsyncResult) {
					if (oAsyncResult.IsSuccess) {
						oAsyncResult.Result = oAsyncResult.Result.LockInfo;
					}

					oRequest.MarkFinish();
					fCallback(oAsyncResult);
				}
			);

			return oRequest;
		},

		/**
		 * Gets features supported by this item, such as WebDAV class support.
		 * @api
		 * @deprecated Use asynchronous method instead
		 * @returns {ITHit.WebDAV.Client.OptionsInfo} OptionsInfo object containing information about features supported by server.
		 */
		SupportedFeatures: function() {

			var oRequest = this.Session.CreateRequest(this.__className + '.SupportedFeatures()');
			var oOptions = ITHit.WebDAV.Client.Methods.Options.Go(oRequest, this.Href, this.Host).ItemOptions;

			oRequest.MarkFinish();
			return oOptions;
		},

		/**
		 * Callback function to be called when options info loaded from server.
		 * @callback ITHit.WebDAV.Client.HierarchyItem~SupportedFeaturesAsyncCallback
		 * @param {ITHit.WebDAV.Client.AsyncResult} oResult Result object
		 * @param {ITHit.WebDAV.Client.OptionsInfo} oResult.Result OptionsInfo object containing information about features supported by server.
		 */

		/**
		 * Gets features supported by this item, such as WebDAV class support.
		 * @api
		 * @examplecode ITHit.WebDAV.Client.Tests.HierarchyItems.SupportedFeatures.SupportedFeatures
		 * @param {ITHit.WebDAV.Client.HierarchyItem~SupportedFeaturesAsyncCallback} fCallback Function to call when operation is completed.
		 * @returns {ITHit.WebDAV.Client.Request} Request object.
		 */
		SupportedFeaturesAsync: function (fCallback) {

			var oRequest = this.Session.CreateRequest(this.__className + '.SupportedFeaturesAsync()');

			ITHit.WebDAV.Client.Methods.Options.GoAsync(oRequest, this.Href, this.Host, function(oAsyncResult) {
				if (oAsyncResult.IsSuccess) {
					oAsyncResult.Result = oAsyncResult.Result.ItemOptions;
				}

				oRequest.MarkFinish();
				fCallback(oAsyncResult);
			});

			return oRequest;
		},

		/**
		 * Removes the lock.
		 * @api
		 * @deprecated Use asynchronous method instead
		 * @param {string} [sLockToken] Identifies lock to be prolonged.
		 * @throws ITHit.WebDAV.Client.Exceptions.PreconditionFailedException The item is not locked.
		 * @throws ITHit.WebDAV.Client.Exceptions.MethodNotAllowedException The item does not support locking.
		 * @throws ITHit.WebDAV.Client.Exceptions.NotFoundException The item doesn't exist on the server.
		 * @throws ITHit.WebDAV.Client.Exceptions.WebDavException Unexpected error occurred.
		 */
		Unlock: function(sLockToken) {

			var oRequest = this.Session.CreateRequest(this.__className + '.Unlock()');

			// Unlock item.
			var oResult = ITHit.WebDAV.Client.Methods.Unlock.Go(
				oRequest,
				this.Href,
				sLockToken,
				this.Host
			);

			var oError = this._GetErrorFromUnlockResponse(oResult.Response);
			if (oError) {
				oRequest.MarkFinish();
				throw oError;
			}

			oRequest.MarkFinish();
		},

		/**
		 * Callback function to be called when item unlocked on server.
		 * @callback ITHit.WebDAV.Client.HierarchyItem~UnlockAsyncCallback
		 * @param {ITHit.WebDAV.Client.AsyncResult} oResult Result object
		 */

		/**
		 * Removes the lock.
		 * @api
		 * @examplecode ITHit.WebDAV.Client.Tests.Locks.Lock.SetUnLock
		 * @param {string} sLockToken Identifies lock to be prolonged.
		 * @param {ITHit.WebDAV.Client.HierarchyItem~UnlockAsyncCallback} fCallback Function to call when operation is completed.
		 * @returns {ITHit.WebDAV.Client.Request} Request object.
		 */
		UnlockAsync: function(sLockToken, fCallback) {

			var oRequest = this.Session.CreateRequest(this.__className + '.UnlockAsync()');

			var that = this;
			ITHit.WebDAV.Client.Methods.Unlock.GoAsync(
				oRequest,
				this.Href,
				sLockToken,
				this.Host,
				function (oAsyncResult) {
					if (oAsyncResult.IsSuccess) {
						oAsyncResult.Error = that._GetErrorFromUnlockResponse(oAsyncResult.Result.Response);
						if (oAsyncResult.Error !== null) {
							oAsyncResult.IsSuccess = false;
							oAsyncResult.Result = null;
						}
					}

					oRequest.MarkFinish();
					fCallback(oAsyncResult);
				}
			);

			return oRequest;
		},

		/**
		 * Updates values of properties exposed by this item.
		 * @api
		 * @deprecated Use asynchronous method instead
		 * @param {ITHit.WebDAV.Client.Property[]} oPropertiesToAddOrUpdate Properties to be updated.
		 * @param {ITHit.WebDAV.Client.PropertyName[]} oPropertiesToDelete Names of properties to be removed from this item.
		 * @param {string} [sLockToken] Lock token.
		 * @throws ITHit.WebDAV.Client.Exceptions.LockedException The item is locked and no or invalid lock token was provided.
		 * @throws ITHit.WebDAV.Client.Exceptions.PropertyForbiddenException Cannot alter one of the properties.
		 * @throws ITHit.WebDAV.Client.Exceptions.PropertyConflictException The client has provided a value whose semantics are not appropriate for the property. This includes trying to set read-only properties.
		 * @throws ITHit.WebDAV.Client.Exceptions.NotFoundException This item doesn't exist on the server.
		 * @throws ITHit.WebDAV.Client.Exceptions.PropertyException Server returned unknown error for specific property.
		 * @throws ITHit.WebDAV.Client.Exceptions.WebDavHttpException Server returned unknown error.
		 * @throws ITHit.WebDAV.Client.Exceptions.WebDavException Unexpected error occurred.
		 */
		UpdateProperties: function(oPropertiesToAddOrUpdate, oPropertiesToDelete, sLockToken) {
			sLockToken = sLockToken || null;

			var oRequest = this.Session.CreateRequest(this.__className + '.UpdateProperties()');

			// Properties
			var aPropsToAddOrUpdate = this._GetPropertiesForUpdate(oPropertiesToAddOrUpdate);
			var aPropsToDelete      = this._GetPropertiesForDelete(oPropertiesToDelete);

			// Check whether there is something to change.
			if (aPropsToAddOrUpdate.length + aPropsToDelete.length === 0) {
				ITHit.Logger.WriteMessage(ITHit.Phrases.Exceptions.NoPropertiesToManipulateWith);
				oRequest.MarkFinish();
				return;
			}

			var oResult = ITHit.WebDAV.Client.Methods.Proppatch.Go(
				oRequest,
				this.Href,
				aPropsToAddOrUpdate,
				aPropsToDelete,
				sLockToken,
				this.Host
			);

			var oError = this._GetErrorFromUpdatePropertiesResponse(oResult.Response);
			if (oError) {
				oRequest.MarkFinish();
				throw oError;
			}

			oRequest.MarkFinish();
		},

		/**
		 * Callback function to be called when item properties is updated on server.
		 * @callback ITHit.WebDAV.Client.HierarchyItem~UpdatePropertiesAsyncCallback
		 * @param {ITHit.WebDAV.Client.AsyncResult} oResult Result object
		 */

		/**
		 * Updates values of properties exposed by this item.
		 * @api
		 * @examplecode ITHit.WebDAV.Client.Tests.HierarchyProperties.UpdateProperties.Update
		 * @examplecode ITHit.WebDAV.Client.Tests.HierarchyProperties.UpdateProperties.Delete
		 * @param {ITHit.WebDAV.Client.Property[]} oPropertiesToAddOrUpdate Properties to be updated.
		 * @param {ITHit.WebDAV.Client.PropertyName[]} oPropertiesToDelete Names of properties to be removed from this item.
		 * @param {string} [sLockToken] Lock token.
		 * @param {ITHit.WebDAV.Client.HierarchyItem~UpdatePropertiesAsyncCallback} fCallback Function to call when operation is completed.
		 * @returns {ITHit.WebDAV.Client.WebDavRequest|null} WebDAV request
		 */
		UpdatePropertiesAsync: function(oPropertiesToAddOrUpdate, oPropertiesToDelete, sLockToken, fCallback) {
			sLockToken = sLockToken || null;

			var oRequest = this.Session.CreateRequest(this.__className + '.UpdatePropertiesAsync()');

			// Properties
			var aPropsToAddOrUpdate = this._GetPropertiesForUpdate(oPropertiesToAddOrUpdate);
			var aPropsToDelete      = this._GetPropertiesForDelete(oPropertiesToDelete);

			// Check whether there is something to change.
			if (aPropsToAddOrUpdate.length + aPropsToDelete.length === 0) {
				oRequest.MarkFinish();
				fCallback(new ITHit.WebDAV.Client.AsyncResult(true, true, null));
				return null;
			}

			var that = this;
			ITHit.WebDAV.Client.Methods.Proppatch.GoAsync(
				oRequest,
				this.Href,
				aPropsToAddOrUpdate,
				aPropsToDelete,
				sLockToken,
				this.Host,
				function(oAsyncResult) {
					if (oAsyncResult.IsSuccess) {
						oAsyncResult.Error = that._GetErrorFromUpdatePropertiesResponse(oAsyncResult.Result.Response);
						if (oAsyncResult.Error !== null) {
							oAsyncResult.IsSuccess = false;
							oAsyncResult.Result = null;
						}
					}

					oRequest.MarkFinish();
					fCallback(oAsyncResult);
				}
			);

			return oRequest;
		},

		_GetPropertiesForUpdate: function(oPropertiesToAddOrUpdate) {
			// Define variables for properties.
			var aPropsToAddOrUpdate = [];

			// Add or update properties.
			if (oPropertiesToAddOrUpdate) {
				for (var i = 0; i < oPropertiesToAddOrUpdate.length; i++) {
					if ( (oPropertiesToAddOrUpdate[i] instanceof ITHit.WebDAV.Client.Property) && oPropertiesToAddOrUpdate[i]) {
						if (oPropertiesToAddOrUpdate[i].Name.NamespaceUri != ITHit.WebDAV.Client.DavConstants.NamespaceUri) {
							aPropsToAddOrUpdate.push(oPropertiesToAddOrUpdate[i]);
						} else  {
							throw new ITHit.WebDAV.Client.Exceptions.PropertyException(ITHit.Phrases.Exceptions.AddOrUpdatePropertyDavProhibition.Paste(oPropertiesToAddOrUpdate[i]), this.Href, oPropertiesToAddOrUpdate[i]);
						}
					} else {
						throw new ITHit.WebDAV.Client.Exceptions.PropertyException(ITHit.Phrases.Exceptions.PropertyUpdateTypeException);
					}
				}
			}

			return aPropsToAddOrUpdate;
		},

		_GetPropertiesForDelete: function(oPropertiesToDelete) {
			// Define variables for properties.
			var aPropsToDelete      = [];

			// Delete properties.
			if (oPropertiesToDelete) {
				for (var i = 0; i < oPropertiesToDelete.length; i++) {
					if ( (oPropertiesToDelete[i] instanceof ITHit.WebDAV.Client.PropertyName) && oPropertiesToDelete[i]) {
						if (oPropertiesToDelete[i].NamespaceUri != ITHit.WebDAV.Client.DavConstants.NamespaceUri) {
							aPropsToDelete.push(oPropertiesToDelete[i]);
						} else {
							throw new ITHit.WebDAV.Client.Exceptions.PropertyException(ITHit.Phrases.Exceptions.DeletePropertyDavProhibition.Paste(oPropertiesToDelete[i]), this.Href, oPropertiesToDelete[i]);
						}
					} else {
						throw new ITHit.WebDAV.Client.Exceptions.PropertyException(ITHit.Phrases.Exceptions.PropertyDeleteTypeException);
					}
				}
			}

			return aPropsToDelete;
		},

		_GetErrorFromDeleteResponse: function(oResponse) {
			// Whether response is instance of MultiResponse class.
			if (oResponse instanceof ITHit.WebDAV.Client.Methods.MultiResponse) {
				return new ITHit.WebDAV.Client.Exceptions.WebDavHttpException(
					ITHit.Phrases.FailedToDelete,
					this.Href,
					new ITHit.WebDAV.Client.Exceptions.Info.Multistatus(oResponse),
					ITHit.WebDAV.Client.HttpStatus.MultiStatus,
					null
				);
			}

			// Whether response is instance of SingleResponse class.
			if (oResponse instanceof ITHit.WebDAV.Client.Methods.SingleResponse && !oResponse.Status.IsSuccess()) {
				var sMessage = ITHit.Phrases.DeleteFailedWithStatus.Paste(oResponse.Status.Code, oResponse.Status.Description);
				return new ITHit.WebDAV.Client.Exceptions.WebDavHttpException(sMessage, this.Href, null, oResponse.Status, null);
			}

			return null;
		},

		_GetErrorFromCopyResponse: function(oResponse) {
			if (oResponse instanceof ITHit.WebDAV.Client.Methods.MultiResponse) {
				for (var i = 0, l = oResponse.Responses.length; i < l; i++) {
					if (oResponse.Responses[i].Status.IsCopyMoveOk()) {
						continue;
					}

					return new ITHit.WebDAV.Client.Exceptions.WebDavHttpException(
						ITHit.Phrases.FailedToCopy,
						this.Href,
						new ITHit.WebDAV.Client.Exceptions.Info.Multistatus(oResponse),
						ITHit.WebDAV.Client.HttpStatus.MultiStatus,
						null
					);
				}
			}

			if (oResponse instanceof ITHit.WebDAV.Client.Methods.SingleResponse && !oResponse.Status.IsCopyMoveOk()) {
				return new ITHit.WebDAV.Client.Exceptions.WebDavHttpException(
					ITHit.Phrases.FailedToCopyWithStatus.Paste(oResponse.Status.Code, oResponse.Status.Description),
					this.Href,
					null,
					oResponse.Status,
					null
				);
			}

			return null;
		},

		_GetErrorFromMoveResponse: function(oResponse) {
			if (oResponse instanceof ITHit.WebDAV.Client.Methods.MultiResponse) {
				for (var i = 0, l = oResponse.Responses.length; i < l; i++) {
					if (oResponse.Responses[i].Status.IsCopyMoveOk()) {
						continue;
					}

					return new ITHit.WebDAV.Client.Exceptions.WebDavHttpException(
						ITHit.Phrases.FailedToMove,
						this.Href,
						new ITHit.WebDAV.Client.Exceptions.Info.Multistatus(oResponse),
						ITHit.WebDAV.Client.HttpStatus.MultiStatus,
						null
					);
				}
			}

			if (oResponse instanceof ITHit.WebDAV.Client.Methods.SingleResponse && !oResponse.Status.IsCopyMoveOk()) {
				return new ITHit.WebDAV.Client.Exceptions.WebDavHttpException(
					ITHit.Phrases.MoveFailedWithStatus.Paste(oResponse.Status.Code, oResponse.Status.Description),
					this.Href,
					null,
					oResponse.Status,
					null
				);
			}

			return null;
		},

		_GetErrorFromUnlockResponse: function(oResponse) {

			// Check status.
			if (!oResponse.Status.IsUnlockOk()) {
				return new ITHit.WebDAV.Client.Exceptions.WebDavHttpException(
					ITHit.Phrases.UnlockFailedWithStatus.Paste(oResponse.Status.Code, oResponse.Status.Description),
					this.Href,
					null,
					oResponse.Status,
					null
				);
			}

			return null;
		},

		_GetErrorFromUpdatePropertiesResponse: function(oResponse) {
			var oMultistatus = new ITHit.WebDAV.Client.Exceptions.Info.PropertyMultistatus(oResponse);

			for (var i = 0; i < oMultistatus.Responses.length; i++) {
				var oPropResp = oMultistatus.Responses[i];
				if (oPropResp.Status.IsSuccess()) {
					continue;
				}

				return new ITHit.WebDAV.Client.Exceptions.PropertyException(
					ITHit.Phrases.FailedToUpdateProp,
					this.Href,
					oPropResp.PropertyName,
					oMultistatus,
					ITHit.WebDAV.Client.HttpStatus.MultiStatus,
					null
				);
			}

			return null;
		}

	});

})();

/**
 * @class ITHit.WebDAV.Client.Methods.Put
 * @extends ITHit.WebDAV.Client.Methods.HttpMethod
 */
ITHit.DefineClass('ITHit.WebDAV.Client.Methods.Put', ITHit.WebDAV.Client.Methods.HttpMethod, /** @lends ITHit.WebDAV.Client.Methods.Put.prototype */{

	__static: /** @lends ITHit.WebDAV.Client.Methods.Put */{

		/**
		 *
		 * @param oRequest
		 * @param sHref
		 * @param sContentType
		 * @param sContent
		 * @param sLockToken
		 * @param sHost
		 * @returns {*}
		 */
		Go: function (oRequest, sHref, sContentType, sContent, sLockToken, sHost) {
			return this._super.apply(this, arguments);
		},

		/**
		 *
		 * @param oRequest
		 * @param sHref
		 * @param sContentType
		 * @param sContent
		 * @param sLockToken
		 * @param sHost
		 * @param fCallback
		 * @returns {*}
		 */
		GoAsync: function (oRequest, sHref, sContentType, sContent, sLockToken, sHost, fCallback) {
			return this._super.apply(this, arguments);
		},

		_CreateRequest: function (oRequest, sHref, sContentType, sContent, sLockToken, sHost) {

			// Create request.
			var oWebDavRequest = oRequest.CreateWebDavRequest(sHost, sHref, sLockToken);

			// Set method.
			oWebDavRequest.Method('PUT');

			// Add headers.
			if (sContentType) {
				// MSXML does not allow empty Content-Type header. Tested with MSXML 3.0 SP5
				oWebDavRequest.Headers.Add('Content-Type', sContentType);
			}

			// Assign content body.
			oWebDavRequest.Body(sContent);

			// Return request.
			return oWebDavRequest;
		}

	}
});


/**
 * @class ITHit.WebDAV.Client.Methods.Get
 * @extends ITHit.WebDAV.Client.Methods.HttpMethod
 */
ITHit.DefineClass('ITHit.WebDAV.Client.Methods.Get', ITHit.WebDAV.Client.Methods.HttpMethod, /** @lends ITHit.WebDAV.Client.Methods.Get.prototype */{

	__static: /** @lends ITHit.WebDAV.Client.Methods.Get */{

		/**
		 *
		 * @param oRequest
		 * @param sHref
		 * @param iBytesFrom
		 * @param iBytesTo
		 * @param sHost
		 * @returns {*}
		 */
		Go: function (oRequest, sHref, iBytesFrom, iBytesTo, sHost) {
			return this._super.apply(this, arguments);
		},

		/**
		 *
		 * @param oRequest
		 * @param sHref
		 * @param iBytesFrom
		 * @param iBytesTo
		 * @param sHost
		 * @returns {*}
		 * @constructor
		 */
		GoAsync: function (oRequest, sHref, iBytesFrom, iBytesTo, sHost) {
			return this._super.apply(this, arguments);
		},

		_CreateRequest: function (oRequest, sHref, iBytesFrom, iBytesTo, sHost) {

			// Create request.
			var oWebDavRequest = oRequest.CreateWebDavRequest(sHost, sHref);

			// Set method.
			oWebDavRequest.Method('GET');

			// Add headers.
			oWebDavRequest.Headers.Add('Translate', 'f');

			// Check whether byte-range is specified.
			if (iBytesFrom !== null) {

				var sByteRange = iBytesFrom;

				if (iBytesFrom >= 0) {
					if (iBytesTo !== null) {
						sByteRange += '-' + parseInt(iBytesTo);
					} else {
						sByteRange += '-';
					}
				} else {
					sByteRange = String(sByteRange);
				}

				// Set byte-range header.
				oWebDavRequest.Headers.Add('Range', 'bytes=' + sByteRange);
			}

			// Return request.
			return oWebDavRequest;
		}

	},

	GetContent: function () {
		return this.Response._Response.BodyText;
	}
});


;
(function() {

    /**
	 * This class provides methods for opening documents for editing directly from server and saving back to server
	 * without download and upload steps. This includes editing Microsoft Office documents as well as any other file
	 * types and works on Windows, Mac OS X and Linux.
	 * @api
	 * @class ITHit.WebDAV.Client.DocManager
	 */
    var self = ITHit.DefineClass('ITHit.WebDAV.Client.DocManager', null, {
        __static: /** @lends ITHit.WebDAV.Client.DocManager */{

            ObsoleteMessage: function (funcName) {
                if (confirm(funcName + " function is deprecated.\n\nSee how to upgrade here:\nhttp://www.webdavsystem.com/ajax/programming/upgrade\n\nSelect OK to navigate to the above URL.\n" )) {
                    window.open("http://www.webdavsystem.com/ajax/programming/upgrade", "_blank");
                }
            },

            /* Obsolete. Implementation provided for backward compatibility with v1.x. */
            JavaEditDocument: function (sDocumentUrl, sMountUrl, sJavaAppletUrl, oContainer) {
                self.ObsoleteMessage("DocManager.JavaEditDocument()");

                // get plugins folder location from jar file URL
                var pluginsFolder = sJavaAppletUrl != null ? self.GetFolder(sJavaAppletUrl) : null;
                var errorCallback = self.GetDefaultCallback(pluginsFolder);

                this.DavProtocolEditDocument(sDocumentUrl, sMountUrl, errorCallback);
            },

            /* Obsolete. Implementation provided for backward compatibility with v1.x. */
            JavaOpenFolderInOsFileManager: function (sFolderUrl, sMountUrl, sJavaAppletUrl, oContainer) {
                self.ObsoleteMessage("DocManager.JavaOpenFolderInOsFileManager()");

                // get plugins folder location from jar file URL
                var pluginsFolder = sJavaAppletUrl != null ? self.GetFolder(sJavaAppletUrl) : null;
                var errorCallback = self.GetDefaultCallback(pluginsFolder);

                this.DavProtocolOpenFolderInOsFileManager(sDocumentUrl, sMountUrl, errorCallback);
            },

            /* Obsolete. Implementation provided for backward compatibility with v1.x. */
            IsMicrosoftOfficeAvailable: function () {
				alert("The DocManager.IsMicrosoftOfficeAvailable() function is deprecated. See http://www.webdavsystem.com/ajax/programming/upgrade for more details.");
                return true; // return true for beackward compatibility. Typically this call is used in combination with IsMicrosoftOfficeDocument()
            },

            /* Obsolete. Implementation provided for backward compatibility with v1.x. */
            GetMsOfficeVersion: function () {
                self.ObsoleteMessage("DocManager.GetMsOfficeVersion()");
                return null;
            },

            /* Obsolete. Implementation provided for backward compatibility with v1.x. */
            ShowMicrosoftOfficeWarning: function () {
				alert("The DocManager.ShowMicrosoftOfficeWarning() function is deprecated. See http://www.webdavsystem.com/ajax/programming/upgrade for more details.");
            },

            /**
			 * Gets file name of the protocol installer depending on OS.
			 * @api
             * @return {string} File name of the protocol installer.
			 */
            GetInstallFileName: function () {
                var fileName = "ITHitEditDocumentOpener.";
                var ext;
                switch (ITHit.DetectOS.OS) {
                    case "Windows":
                        ext = "msi";
                        break;
                    case "MacOS":
                        ext = "pkg";
                        break;
                    case "Linux":
                    case "UNIX":
                    default:
                        ext = "deb";
                }
                return fileName + ext;
            },

            /**
			 * Mounts folder in file system and opens it in default OS file manger. Requests protocol installation if folder opening protocol is not installed.
			 * @api
			 * @param {string} sFolderUrl Url of the folder to open in OS file manager.
			 * @param {string} [sMountUrl] Url to mount file system to before opening the folder. Usually this is your WebDAV server root folder. If this perameter is not specified file system will be mounted to the folder in which document is located.
			 * @param {function} [errorCallback] Function to call if opening file manager has failed. Typically you will request the protocol installation in this callback. 
             * If not specified a default message offering protocol installation will be displayed.
			 */
            OpenFolderInOsFileManager: function (sFolderUrl, sMountUrl, errorCallback, loginName, oContainer) {

                if (oContainer == null)
                    oContainer = window.document.body;

                // Open using HttpBehavior component.
                if (ITHit.DetectBrowser.IE && (ITHit.DetectBrowser.IE < 11)) {

                    if (oContainer._httpFolder == null) {
                        var span = {
                            nodeName: 'span',
                            style: {display: 'none', behavior: 'url(#default#httpFolder)'}
                        };
                        oContainer._httpFolder = ITHit.Utils.CreateDOMElement(span);
                        oContainer.appendChild(oContainer._httpFolder);
                    }

                    var res = oContainer._httpFolder.navigate(sFolderUrl);
                    //return res == "OK";


                } else {
                    // Open folder using custom protocol.

                    var pluginsFolder = null;

                    // If errorCallback is a .jar file url this is a v1.x code call.
                    if ((typeof (errorCallback) == 'string') && (self.GetExtension(errorCallback) == "jar")) {
                        // recommend to upgrade
                        if (confirm("The DocManager.OpenFolderInOsFileManager() function signature changed.\n\nSee how to upgrade here:\nhttp://www.webdavsystem.com/ajax/programming/upgrade\n\nSelect OK to navigate to the above URL.\n")) {
                            window.open("http://www.webdavsystem.com/ajax/programming/upgrade", "_blank");
                        }

                        // get plugins folder location from jar file URL
                        pluginsFolder = self.GetFolder(errorCallback);

                        errorCallback = null;
                    }

                    if (errorCallback == null) {
                        errorCallback = self.GetDefaultCallback(pluginsFolder);
                    }

                    sFolderUrl = sFolderUrl.replace(/\/?$/, '/'); // add trailing slash if not present

                    this.OpenDavProtocol(sFolderUrl, sMountUrl, errorCallback, loginName);
                }
            },

            GetExtension: function (sDocumentUrl) {
                var queryIndex = sDocumentUrl.indexOf("?");
                if (queryIndex > -1) {
                    sDocumentUrl = sDocumentUrl.substr(0, queryIndex);
                }

                var aExt = sDocumentUrl.split(".");
                if (aExt.length === 1) {
                    return "";
                }
                return aExt.pop();
            },

            // get plugins folder location from jar file URL
            GetFolder: function (sUrl) {
                var queryIndex = sUrl.indexOf("?");
                if (queryIndex > -1) {
                    sUrl = sUrl.substr(0, queryIndex);
                }

                return sUrl.substring(0, sUrl.lastIndexOf("/")) + "/";
            },

			/**
			 * Extracts extension and returns true if URL points to Microsoft Office Document.
			 * @api
			 * @param {string} sDocumentUrl URL of the document.
			 * @return {boolean} True if URL points to Microsoft Office Document.
			 */
			IsMicrosoftOfficeDocument: function (sDocumentUrl) {
			    var ext = self.GetExtension(ITHit.Trim(sDocumentUrl));
				if (ext === "") {
					return false;
				}

				return self.FileFormats.MsOfficeEditExtensions.join('|').indexOf(ext) !== -1;
			},

			/**
			 *
			 * @param sExt
			 * @returns {string}
			 */
			GetMsOfficeSchemaByExtension: function (sExt) {
				sExt = sExt.toLowerCase();
				switch (sExt) {
					case "docx":
					case "doc":
					case "docm":
					case "dot":
					case "dotm":
					case "dotx":
					case "odt":
						return "ms-word";

					case "xltx":
					case "xltm":
					case "xlt":
					case "xlsx":
					case "xlsm":
					case "xlsb":
					case "xls":
					case "xll":
					case "xlam":
					case "xla":
					case "ods":
						return "ms-excel";

					case "pptx":
					case "pptm":
					case "ppt":
					case "ppsx":
					case "ppsm":
					case "pps":
					case "ppam":
					case "ppa":
					case "potx":
					case "potm":
					case "pot":
					case "odp":
						return "ms-powerpoint";

					case "accdb":
					case "mdb":
						return "ms-access";

					case "xsn":
					case "xsf":
						return "ms-infopath";

					case "pub":
						return "ms-publisher";

					case "vstx":
					case "vstm":
					case "vst":
					case "vssx":
					case "vssm":
					case "vss":
					case "vsl":
					case "vsdx":
					case "vsdm":
					case "vsd":
					case "vdw":
						return "ms-visio";

					case "mpp":
					case "mpt" :
						return "ms-project";

					default:
						return '';
				}
			},

			/**
			 * Opens Microsoft Office document using protocol. This method does not offer protocol installation if protocol is not found. Microsoft Office must be installed on a client machine.
			 * @api
			 * @param {string} sDocumentUrl Url of the document to edit. This must be a Microsoft Office document.
			 * @param {function} [errorCallback] Function to call if document opening failed.
			 */
			MicrosoftOfficeEditDocument: function (sDocumentUrl, errorCallback) {
			    sDocumentUrl = ITHit.Trim(sDocumentUrl);

				var ext = self.GetExtension(sDocumentUrl);
				if (ext === "" && errorCallback != undefined) {
				    errorCallback();
				}
				else {
				    this.OpenProtocol(self.GetMsOfficeSchemaByExtension(ext) + ':' + encodeURIComponent('ofe|u|') + sDocumentUrl, errorCallback);
				}
			},

			FileFormats: {

				ProtectedExtentions: [/*'ace', 'ade', 'adp', 'adt', 'app', 'asp', 'arj', 'asd', 'bas', 'bat', 'bin', 'btm', 'cbt', 'ceo', 'chm', 'cmd', 'cla', 'com', 'cpl', 'crt', 'csc', 'css', 'dll', 'drv', 'exe', 'email', 'fon', 'hlp', 'hta', 'htm', 'inf', 'ins', 'isp', 'je', 'js', 'lib', 'lnk', 'mdb', 'mde', 'mht', 'msc', 'msi', 'mso', 'msp', 'mst', 'obj', 'ocx', 'ov', 'pcd', 'pgm', 'pif', 'prc', 'rar', 'reg', 'scr', 'sct', 'shb', 'shs', 'smm', 'swf', 'sys', 'tar', 'url', 'vb', 'vxd', 'wsc', 'wsf', 'wsh', '{'*/],

				MsOfficeEditExtensions: [
					"docx", "doc", "docm", "dot", "dotm", "dotx", "odt",
					"xltx", "xltm", "xlt", "xlsx", "xlsm", "xlsb", "xls", "xll", "xlam", "xla", "ods",
					"pptx", "pptm", "ppt", "ppsx", "ppsm", "pps", "ppam", "ppa", "potx", "potm", "pot", "odp",
					"accdb", "mdb",
					"xsn", "xsf",
					"pub",
					"vstx", "vstm", "vst", "vssx", "vssm", "vssm", "vss", "vsl", "vsdx", "vsdm", "vsd", "vdw",
					"mpp", "mpt"
				]
			},

			GetDefaultCallback: function (pluginsFolder) {
			    if (pluginsFolder == null)
			        var pluginsFolder = "/Plugins/";

			    var errorCallback = function () {
			        if (confirm('To open document you must install a custom protocol. Continue?')) {
			            window.open(pluginsFolder + self.GetInstallFileName());
			        }
			    };
			    return errorCallback;
			},

			/**
			 * Opens document for editing. In case of Microsoft Office documents, it will try to use MS Office protocols first. 
             * If not found it will use davX: protocol and prompt to install it if not found. 
			 * @example
			 * &lt;!DOCTYPE html&gt;
			 * &lt;html&gt;
			 * &lt;head&gt;
			 *     &lt;meta charset="utf-8" /&gt;
			 *     &lt;script type="text/javascript" src="ITHitWebDAVClient.js" &gt;&lt;/script&gt;
			 * &lt;/head&gt;

			 * &lt;body&gt;
			 * &lt;script type="text/javascript"&gt;
			 *     function edit() {
			 *         ITHit.WebDAV.Client.DocManager.EditDocument("http://localhost:87654/folder/file.ext", "http://localhost:87654/", protocolInstallMessage);
			 *     }
			 *     
			 *     function protocolInstallMessage(message) {
			 *         var installerFilePath = "/Plugins/" + ITHit.WebDAV.Client.DocManager.GetInstallFileName();
             *
			 *         if (confirm("Opening this type of file requires a protocol installation. Select OK to download the protocol installer.")){
			 *             window.open(installerFilePath);
			 *         }
			 *     }
			 * &lt;/script&gt;
			 * &lt;input type="button" value="Edit Document" onclick="edit()" /&gt;
			 * &lt;/body&gt;
			 * &lt;/html&gt;
			 * @api
			 * @param {string} sDocumentUrl Url of the document to open for editing from server.
             * @param {string} [sMountUrl] Url to mount file system to before opening the folder. Usually this is your WebDAV server root folder. If this perameter is not specified file system will be mounted to the folder in which document is located.
			 * @param {function} [errorCallback] Function to call if document opening failed. Typically you will request the protocol installation in this callback. 
             * If not specified a default message offering protocol installation will be displayed.
			 */
			EditDocument: function (sDocumentUrl, sMountUrl, errorCallback, loginName) {

			    var pluginsFolder = null;

			    // If second param is a .jar file url this is a v1.x code call.
			    if ((typeof(sMountUrl) == 'string') && (self.GetExtension(sMountUrl) == "jar")) {
			        // recommend to upgrade
			        if (confirm("The DocManager.EditDocument() function signature changed.\n\nSee how to upgrade here:\nhttp://www.webdavsystem.com/ajax/programming/upgrade\n\nSelect OK to navigate to the above URL.\n")) {
			            window.open("http://www.webdavsystem.com/ajax/programming/upgrade", "_blank");			            
			        }

			        // get plugins folder location from jar file URL
			        pluginsFolder = self.GetFolder(sMountUrl);

			        sMountUrl = null;
			    }

			    if (errorCallback == null) {
			        errorCallback = self.GetDefaultCallback(pluginsFolder);
			    }

			    // Edit MS Office document with Microsoft Office, if failed use custom protocol
                // MS Office is available on Windows and OS X only, no need to try opening with MS Office protocols on Linux
			    if (self.IsMicrosoftOfficeDocument(sDocumentUrl) && ((ITHit.DetectOS.OS == 'Windows') || (ITHit.DetectOS.OS == 'MacOS'))) {
					self.MicrosoftOfficeEditDocument(sDocumentUrl, function (){
					    self.DavProtocolEditDocument(sDocumentUrl, sMountUrl, errorCallback, loginName);
					});
				}
				else {
			        this.DavProtocolEditDocument(sDocumentUrl, sMountUrl, errorCallback, loginName);
				}
			},
			
            /**
			 * Opens document for editing using davX: protocol and prompts to install the protocol it if not found. 
			 * @api
			 * @param {string} sDocumentUrl Url of the document to open for editing from server.
             * @param {string} [sMountUrl] Url to mount file system to before opening the folder. Usually this is your WebDAV server root folder. If this perameter is not specified file system will be mounted to the folder in which document is located.
			 * @param {function} [errorCallback] Function to call if document opening failed. Typically you will request the protocol installation in this callback. 
             * If not specified a default message offering protocol installation will be displayed.
			 */
			DavProtocolEditDocument: function (sDocumentUrl, sMountUrl, errorCallback, loginName) {
			    this.OpenDavProtocol(sDocumentUrl, sMountUrl, errorCallback, loginName);
			},

			DavProtocolOpenFolderInOsFileManager: function (sFolderUrl, sMountUrl, errorCallback, loginName) {
			    sFolderUrl = sFolderUrl.replace(/\/?$/, '/'); // add trailing slash if not present
			    this.OpenDavProtocol(sFolderUrl, sMountUrl, errorCallback, loginName);
			},

			OpenDavProtocol: function (sUrl, sMountUrl, errorCallback, loginName) {
			    if (sMountUrl == null)
			        sMountUrl = '';

			    sUrl = ITHit.Trim(sUrl);
			    sMountUrl = ITHit.Trim(sMountUrl);

			    var uri = 'dav3:' + sMountUrl + encodeURIComponent('|') + sUrl; // if URI contains '|' links doeas not work in Chrome on OS X
                
			    if (loginName != null){
			        uri += encodeURIComponent('|') + loginName;
			    }

                // Chrome on OS X does not open protocol link if it contains spaces
			    if (ITHit.DetectBrowser.Chrome && (ITHit.DetectOS.OS == 'MacOS')) {
			        uri = uri.split(' ').join('%20');
			    }

			    this.OpenProtocol(uri, errorCallback);
			}, 
			
			RegisterEvent: function (target, eventType, errorCallback) {
				if (target.addEventListener) {
					target.addEventListener(eventType, errorCallback);
					return {
						remove: function () {
							target.removeEventListener(eventType, errorCallback);
						}
					};
				} else {
					target.attachEvent(eventType, errorCallback);
					return {
						remove: function () {
							target.detachEvent(eventType, errorCallback);
						}
					};
				}
			},
			
			CreateHiddenFrame: function (target, uri) {
				var iframe = document.createElement("iframe");
				iframe.src = uri;
				iframe.id = "hiddenIframe";
				iframe.style.display = "none";
				target.appendChild(iframe);
				return iframe;
			},
			
			OpenUriWithHiddenFrame: function (uri, errorCallback) {
				var timeout = setTimeout(function () {
					errorCallback();
					handler.remove();
				}, 1000);

				var iframe = document.querySelector("#hiddenIframe");
				if (!iframe) {
					iframe = this.CreateHiddenFrame(document.body, "about:blank");
				}

				var handler = this.RegisterEvent(window, "blur", onBlur);

				function onBlur() {
					clearTimeout(timeout);
					handler.remove();
				}

				iframe.contentWindow.location.href = uri;
			},
			
			OpenUriWithTimeout: function (uri, errorCallback) {
				var timeout = setTimeout(function () {
					errorCallback();
					handler.remove();
				}, 1000);

				var handler = this.RegisterEvent(window, "blur", onBlur);

				function onBlur() {
					clearTimeout(timeout);
					handler.remove();
				}
				
				window.location = uri;
			},

			OpenUriUsingFirefox: function (uri, errorCallback) {
				var iframe = document.querySelector("#hiddenIframe");
				if (!iframe) {
					iframe = this.CreateHiddenFrame(document.body, "about:blank");
				}
				try {
					iframe.contentWindow.location.href = uri;
				} catch (e) {
					if (e.name == "NS_ERROR_UNKNOWN_PROTOCOL") {
						errorCallback();
					}
				}
			},
			
			OpenUriUsingIE: function (uri, errorCallback) {
			    if (navigator.msLaunchUri) {
			        navigator.msLaunchUri(uri, function () { }, errorCallback);
			    }
			    else {
			        //check if OS is Win 8 or 8.1
			        var ua = navigator.userAgent.toLowerCase();
			        var isWin8 = /windows nt 6.2/.test(ua) || /windows nt 6.3/.test(ua);

			        if (isWin8) {
			            this.OpenUriUsingIEInWindows8(uri, errorCallback);
			        } else {
			            if (ITHit.DetectBrowser.IE === 9 || ITHit.DetectBrowser.IE === 11) {
			                this.OpenUriWithHiddenFrame(uri, errorCallback);
			            } else {
			                this.OpenUriInNewWindow(uri, errorCallback);
			            }
			        }
			    }
			},
			
			OpenUriInNewWindow: function (uri, errorCallback) {
				var myWindow = window.open('', '', 'width=0,height=0');

				myWindow.document.write("<iframe src='" + uri + "'></iframe>");
				setTimeout(function () {
					try {
						myWindow.location.href;
						myWindow.setTimeout("window.close()", 1000);
					} catch (e) {
						myWindow.close();
						errorCallback();
					}
				}, 1000);
			},
			
			OpenUriUsingIEInWindows8: function (uri, errorCallback) {
				window.location.href = uri;
			},
			
			OpenUriUsingEdgeInWindows10: function (uri, errorCallback) {
			    if (navigator.msLaunchUri) {
			        // If fail callback is provided Edge will always call fail callback, looks like an Edge bug
					navigator.msLaunchUri(uri/*, function () { }, errorCallback*/);
				}
			},
			
			OpenProtocol: function (uri, errorCallback) {
			    function failCallback() {
			        errorCallback && errorCallback();
			    }
				
			    if (ITHit.DetectBrowser.FF) {
			        this.OpenUriUsingFirefox(uri, failCallback);
			    } else if (ITHit.DetectBrowser.Chrome) {
			        this.OpenUriWithTimeout(uri, failCallback);
			    } else if (ITHit.DetectBrowser.IE) {
			        this.OpenUriUsingIE(uri, errorCallback);
			    } else if (ITHit.DetectBrowser.Safari) {
			        this.OpenUriWithHiddenFrame(uri, failCallback);
			    } else if (ITHit.DetectBrowser.Edge) {
			        this.OpenUriUsingEdgeInWindows10(uri, errorCallback);
			    }
			    else {
			        this.OpenUriWithTimeout(uri, failCallback);
			    }
			}
			
		}
	});
	
})();


/**
 * @class ITHit.WebDAV.Client.Methods.CancelUpload
 * @extends ITHit.WebDAV.Client.Methods.HttpMethod
 */
ITHit.DefineClass('ITHit.WebDAV.Client.Methods.CancelUpload', ITHit.WebDAV.Client.Methods.HttpMethod, /** @lends ITHit.WebDAV.Client.Methods.CancelUpload.prototype */{

	__static: /** @lends ITHit.WebDAV.Client.Methods.CancelUpload */{

		Go: function (oRequest, sHref, sLockToken, sHost) {
			return this.GoAsync(oRequest, sHref, sLockToken, sHost);
		},

		GoAsync: function (oRequest, sHref, sLockToken, sHost, fCallback) {

			// Create request.
			var oWebDavRequest = ITHit.WebDAV.Client.Methods.CancelUpload.createRequest(oRequest, sHref, sLockToken, sHost);

			var self = this;
			var fOnResponse = typeof fCallback === 'function'
				? function (oResult) {
				self._GoCallback(sHref, oResult, fCallback)
			}
				: null;

			// Get response.
			var oResponse = oWebDavRequest.GetResponse(fOnResponse);

			if (typeof fCallback !== 'function') {
				var oResult = new ITHit.WebDAV.Client.AsyncResult(oResponse, oResponse != null, null);
				return this._GoCallback(sHref, oResult, fCallback);
			} else {
				return oWebDavRequest;
			}
		},

		_GoCallback: function (sHref, oResult, fCallback) {

			var oResponse = oResult;
			var bSuccess = true;
			var oError = null;

			if (oResult instanceof ITHit.WebDAV.Client.AsyncResult) {
				oResponse = oResult.Result;
				bSuccess = oResult.IsSuccess;
				oError = oResult.Error;
			}

			// Parse response response.
			var oUnlock = null;
			if (bSuccess) {
				oUnlock = new ITHit.WebDAV.Client.Methods.CancelUpload(new ITHit.WebDAV.Client.Methods.SingleResponse(oResponse));
			}

			// Return response.
			if (typeof fCallback === 'function') {
				var oUnlockResult = new ITHit.WebDAV.Client.AsyncResult(oUnlock, bSuccess, oError);
				fCallback.call(this, oUnlockResult);
			} else {
				// Return response object.
				return oUnlock;
			}
		},

		createRequest: function (oRequest, sHref, sLockToken, sHost) {

			// Create request.
			var oWebDavRequest = oRequest.CreateWebDavRequest(sHost, sHref, sLockToken);
			oWebDavRequest.Method('CANCELUPLOAD');

			return oWebDavRequest;
		}

	}
});




/**
 * Provides support partial uploads and resuming broken uploads.
 * @api
 * @class ITHit.WebDAV.Client.ResumableUpload
 */
ITHit.DefineClass('ITHit.WebDAV.Client.ResumableUpload', null, /** @lends ITHit.WebDAV.Client.ResumableUpload.prototype */{

	/**
	 * Current WebDAV session.
	 * @type {ITHit.WebDAV.Client.WebDavSession}
	 */
	Session: null,

	/**
	 * This item path on the server.
	 * @type {string}
	 */
	Href: null,

	/**
	 * Server host.
	 * @type {string}
	 */
	Host: null,

	/**
	 * @param {ITHit.WebDAV.Client.WebDavSession} oSession Current WebDAV session
	 * @param {string} sHref Item's path.
	 * @param {string} sHost
	 */
	constructor: function(oSession, sHref, sHost) {
		this.Session = oSession;
		this.Href = sHref;
		this.Host = sHost;
	},

	/**
	 * Amount of bytes successfully uploaded to server.
	 * @api
	 * @deprecated Use asynchronous method instead
	 * @returns {number} Number of bytes uploaded to server or -1 if server did not provide info about how much bytes uploaded.
	 * @throws ITHit.WebDAV.Client.Exceptions.NotImplementedException Is thrown if server doesn't support resumable upload.
	 * @throws ITHit.WebDAV.Client.Exceptions.LockedException This folder is locked and no or invalid lock token was specified.
	 * @throws ITHit.WebDAV.Client.Exceptions.WebDavHttpException Server returned unknown error.
	 * @throws ITHit.WebDAV.Client.Exceptions.WebDavException Unexpected error occurred.
	 */
	GetBytesUploaded: function() {
		var oRequest = this.Session.CreateRequest(this.__className + '.GetBytesUploaded()');
		var aUploadInfo = ITHit.WebDAV.Client.Methods.Report.Go(oRequest, this.Href, this.Host);
		var iBytes = aUploadInfo.length > 0 ? aUploadInfo[0].BytesUploaded : null;

		oRequest.MarkFinish();
		return iBytes;
	},

	/**
	 * Callback function to be called when result of bytes uploaded loaded from server.
	 * @callback ITHit.WebDAV.Client.ResumableUpload~GetBytesUploadedAsyncCallback
	 * @param {ITHit.WebDAV.Client.AsyncResult} oResult Result object
	 * @param {number} oResult.Result Number of bytes uploaded to server or -1 if server did not provide info about how much bytes uploaded.
	 */

	/**
	 * Get amount of bytes successfully uploaded to server.
	 * @api
	 * @param {ITHit.WebDAV.Client.ResumableUpload~GetBytesUploadedAsyncCallback} fCallback Function to call when operation is completed.
	 * @returns {ITHit.WebDAV.Client.Request} Request object.
	 */
	GetBytesUploadedAsync: function (fCallback) {
		var oRequest = this.Session.CreateRequest(this.__className + '.GetBytesUploadedAsync()');
		ITHit.WebDAV.Client.Methods.Report.GoAsync(oRequest, this.Href, this.Host, null, null, function(oAsyncResult) {
			oAsyncResult.Result = oAsyncResult.IsSuccess && oAsyncResult.Result.length > 0 ?
				oAsyncResult.Result[0].BytesUploaded :
				null;

			oRequest.MarkFinish();
			fCallback(oAsyncResult);
		});

		return oRequest;
	},

	/**
	 * Cancels upload of the file.
	 * @api
	 * @deprecated Use asynchronous method instead
	 * @param {string} mLockTokens Lock token for this file.
	 * @throws ITHit.WebDAV.Client.Exceptions.NotFoundException This folder doesn't exist on the server.
	 * @throws ITHit.WebDAV.Client.Exceptions.LockedException This folder is locked and no or invalid lock token was specified.
	 * @throws ITHit.WebDAV.Client.Exceptions.WebDavHttpException Server returned unknown error.
	 * @throws ITHit.WebDAV.Client.Exceptions.WebDavException Unexpected error occurred.
	 */
	CancelUpload: function(mLockTokens) {
		var oRequest = this.Session.CreateRequest(this.__className + '.CancelUpload()');
		ITHit.WebDAV.Client.Methods.CancelUpload.Go(oRequest, this.Href, mLockTokens, this.Host);
		oRequest.MarkFinish();
	},

	/**
	 * Callback function to be called when folder loaded from server.
	 * @callback ITHit.WebDAV.Client.ResumableUpload~CancelUploadAsyncCallback
	 * @param {ITHit.WebDAV.Client.AsyncResult} oResult Result object
	 */

	/**
	 * Cancels upload of the file.
	 * @api
	 * @param {string} mLockTokens Lock token for this file.
	 * @param {ITHit.WebDAV.Client.ResumableUpload~CancelUploadAsyncCallback} fCallback Function to call when operation is completed.
	 * @returns {ITHit.WebDAV.Client.Request} Request object.
	 */
	CancelUploadAsync: function (mLockTokens, fCallback) {
		var oRequest = this.Session.CreateRequest(this.__className + '.CancelUploadAsync()');
		return ITHit.WebDAV.Client.Methods.CancelUpload.GoAsync(oRequest, this.Href, this.Host, mLockTokens, function(oAsyncResult) {

			oRequest.MarkFinish();
			fCallback(oAsyncResult);
		});
	}

});


;
(function() {

	/**
	 * Represents a file on a WebDAV server.
	 * @api
	 * @class ITHit.WebDAV.Client.File
	 * @extends ITHit.WebDAV.Client.HierarchyItem
	 */
	var self = ITHit.WebDAV.Client.Resource = ITHit.DefineClass('ITHit.WebDAV.Client.File', ITHit.WebDAV.Client.HierarchyItem, /** @lends ITHit.WebDAV.Client.File.prototype */{

		__static: /** @lends ITHit.WebDAV.Client.File */{

			GetRequestProperties: function() {
				return [
					ITHit.WebDAV.Client.DavConstants.ResourceType,
					ITHit.WebDAV.Client.DavConstants.DisplayName,
					ITHit.WebDAV.Client.DavConstants.CreationDate,
					ITHit.WebDAV.Client.DavConstants.GetLastModified,
					ITHit.WebDAV.Client.DavConstants.GetContentType,
					ITHit.WebDAV.Client.DavConstants.GetContentLength,
					ITHit.WebDAV.Client.DavConstants.SupportedLock,
					ITHit.WebDAV.Client.DavConstants.LockDiscovery,
					ITHit.WebDAV.Client.DavConstants.QuotaAvailableBytes,
					ITHit.WebDAV.Client.DavConstants.QuotaUsedBytes,
					ITHit.WebDAV.Client.DavConstants.CheckedIn,
					ITHit.WebDAV.Client.DavConstants.CheckedOut
				];
			},

			ParseHref: function(sHref, isFolder) {
				// Normalize href
				var aHrefParts  = sHref.split('?');
				aHrefParts[0]   = aHrefParts[0].replace(/\/?$/, '');
				sHref           = ITHit.WebDAV.Client.Encoder.EncodeURI(aHrefParts.join('?'));

				return this._super(sHref);
			},

			/**
			 * Load resource from server
			 * @deprecated Use asynchronous method instead
			 * @param {ITHit.WebDAV.Client.Request} oRequest Current WebDAV session.
			 * @param {string} sHref This item path on the server.
			 * @param {ITHit.WebDAV.Client.PropertyName[]} [aProperties] Additional properties requested from server. Default is empty array.
			 * @returns {ITHit.WebDAV.Client.File} Opened folder object.
			 * @throws ITHit.WebDAV.Client.Exceptions.WebDavException A Resource was expected or the response doesn't have required item.
			 */
			OpenItem: function(oRequest, sHref, aProperties) {
				aProperties = aProperties || [];

				var oFolder = this._super(oRequest, sHref, aProperties);

				// Throw exception if there is not a folder type.
				if (!(oFolder instanceof self)) {
					throw new ITHit.WebDAV.Client.Exceptions.WebDavException(ITHit.Phrases.ResponseFileWrongType.Paste(sHref));
				}

				return oFolder;
			},

			/**
			 * Callback function to be called when resource loaded from server.
			 * @callback ITHit.WebDAV.Client.File~OpenAsyncCallback
			 * @param {ITHit.WebDAV.Client.AsyncResult} oResult Result object
			 * @param {ITHit.WebDAV.Client.File} oResult.Result Loaded resource object.
			 */

			/**
			 * Load resource from server
			 * @param {ITHit.WebDAV.Client.Request} oRequest Current WebDAV session.
			 * @param {string} sHref This item path on the server.
			 * @param {ITHit.WebDAV.Client.PropertyName[]} aProperties Additional properties requested from server. Default is empty array.
			 * @param {ITHit.WebDAV.Client.File~OpenAsyncCallback} fCallback Function to call when operation is completed.
			 * @returns {ITHit.WebDAV.Client.Request} Request object.
			 */
			OpenItemAsync: function(oRequest, sHref, aProperties, fCallback) {
				aProperties = aProperties || [];

				this._super(oRequest, sHref, aProperties, function(oAsyncResult) {
					// Throw exception if there is not a folder type.
					if (oAsyncResult.IsSuccess && !(oAsyncResult.Result instanceof self)) {
						oAsyncResult.Error = new ITHit.WebDAV.Client.Exceptions.WebDavException(ITHit.Phrases.ResponseFileWrongType.Paste(sHref));
						oAsyncResult.IsSuccess = false;
					}

					fCallback(oAsyncResult);
				});

				return oRequest;
			}

		},

		/**
		 * Length of the file.
         * @api
		 * @type {number}
		 */
		ContentLength: null,

		/**
		 * Content type of the file.
         * @api
		 * @type {string}
		 */
		ContentType: null,

		/**
		 * ResumableUpload instance to manage partially failed uploads.
		 * @api
		 * @type {ITHit.WebDAV.Client.ResumableUpload}
		 */
		ResumableUpload: null,

		/**
		 * Create new instance of File class which represents a file on a WebDAV server.
		 * @param {ITHit.WebDAV.Client.WebDavSession} oSession Current WebDAV session.
		 * @param {string} sHref This item path on the server.
		 * @param {object} oGetLastModified Most recent modification date.
		 * @param {string} sDisplayName User friendly item name.
		 * @param {object} oCreationDate The date item was created.
		 * @param {string} sContentType Content type.
		 * @param {number} iContentLength Content length.
		 * @param aSupportedLocks
		 * @param aActiveLocks
		 * @param sHost
		 * @param iAvailableBytes
		 * @param iUsedBytes
		 * @param aCheckedIn
		 * @param aCheckedOut
		 * @param {ITHit.WebDAV.Client.PropertyName[]} aProperties
		 */
		constructor: function(oSession, sHref, oGetLastModified, sDisplayName, oCreationDate, sContentType, iContentLength, aSupportedLocks, aActiveLocks, sHost, iAvailableBytes, iUsedBytes, aCheckedIn, aCheckedOut, aProperties) {

			// Inheritance definition.
			this._super(oSession, sHref, oGetLastModified, sDisplayName, oCreationDate, ITHit.WebDAV.Client.ResourceType.File, aSupportedLocks, aActiveLocks, sHost, iAvailableBytes, iUsedBytes, aCheckedIn, aCheckedOut, aProperties);

			// Declare class properties.
			this.ContentLength = iContentLength;
			this.ContentType   = sContentType;

			this.ResumableUpload = new ITHit.WebDAV.Client.ResumableUpload(this.Session, this.Href);
		},

		/**
		 * Reads file content.
		 * @api
		 * @deprecated Use asynchronous method instead
		 * @param {number} [iBytesFrom] Start position to retrieve lBytesCount number of bytes from.
		 * @param {number} [iBytesCount] Number of bytes to retrieve.
		 * @returns {string} Requested file content.
		 */
		ReadContent: function(iBytesFrom, iBytesCount) {
			iBytesFrom = iBytesFrom || null;
			iBytesCount = iBytesCount || null;

			var oRequest = this.Session.CreateRequest(this.__className + '.ReadContent()');

			var iBytesTo = iBytesFrom && iBytesCount ? iBytesFrom + iBytesCount - 1 : 0;

			// Create response.
			var oResult = ITHit.WebDAV.Client.Methods.Get.Go(
				oRequest,
				this.Href,
				iBytesFrom,
				iBytesTo,
				this.Host
			);

			// Return requested content.
			oRequest.MarkFinish();
			return oResult.GetContent();
		},

		/**
		 * Callback function to be called when file content loaded from server.
		 * @callback ITHit.WebDAV.Client.File~ReadContentAsyncCallback
		 * @param {ITHit.WebDAV.Client.AsyncResult} oResult Result object
		 * @param {string} oResult.Result Requested file content.
		 */

		/**
		 * Reads file content. To download only a part of a file you can specify 2 parameters in ReadContent call.
		 * First parameter is the starting byte (zero-based) at witch to start content download, the second – amount
		 * of bytes to be downloaded. The library will add Range header to the request in this case.
		 * @api
		 * @examplecode ITHit.WebDAV.Client.Tests.HierarchyItems.CreateFile.ReadContent
		 * @examplecode ITHit.WebDAV.Client.Tests.HierarchyItems.CreateFile.ReadContentRange
		 * @param {number} iBytesFrom Start position to retrieve lBytesCount number of bytes from.
		 * @param {number} iBytesCount Number of bytes to retrieve.
		 * @param {ITHit.WebDAV.Client.File~ReadContentAsyncCallback} fCallback Function to call when operation is completed.
		 * @returns {ITHit.WebDAV.Client.Request} Request object.
		 */
		ReadContentAsync: function(iBytesFrom, iBytesCount, fCallback) {
			iBytesFrom = iBytesFrom || null;
			iBytesCount = iBytesCount || null;

			var oRequest = this.Session.CreateRequest(this.__className + '.ReadContentAsync()');

			var iBytesTo = iBytesFrom && iBytesCount ? iBytesFrom + iBytesCount - 1 : null;

			// Create response.
			ITHit.WebDAV.Client.Methods.Get.GoAsync(
				oRequest,
				this.Href,
				iBytesFrom,
				iBytesTo,
				this.Host,
				function(oAsyncResult) {
					if (oAsyncResult.IsSuccess) {
						oAsyncResult.Result = oAsyncResult.Result.GetContent();
					}

					oRequest.MarkFinish();
					fCallback(oAsyncResult);
				}
			);

			return oRequest;
		},

		/**
		 * Writes file content.
		 * @api
		 * @deprecated Use asynchronous method instead
		 * @param {string} sContent File content.
		 * @param {string} [sLockToken] Lock token.
		 * @param {string} [sMimeType] File mime-type.
		 * @throws ITHit.WebDAV.Client.Exceptions.WebDavHttpException Content is not writtened to file.
		 */
		WriteContent: function(sContent, sLockToken, sMimeType) {
			sLockToken = sLockToken || null;
			sMimeType = sMimeType || '';

			var oRequest = this.Session.CreateRequest(this.__className + '.WriteContent()');

			// Create response.
			var oResult = ITHit.WebDAV.Client.Methods.Put.Go(
				oRequest,
				this.Href,
				sMimeType,
				sContent,
				sLockToken,
				this.Host
			);

			var oError = this._GetErrorFromWriteContentResponse(oResult.Response, this.Href);
			if (oError) {
				oRequest.MarkFinish();
				throw oError;
			}

			oRequest.MarkFinish();
		},

		/**
		 * Callback function to be called when content saved in file on server.
		 * @callback ITHit.WebDAV.Client.File~WriteContentAsyncCallback
		 * @param {ITHit.WebDAV.Client.AsyncResult} oResult Result object
		 */

		/**
		 * Writes file content.
		 * @api
		 * @examplecode ITHit.WebDAV.Client.Tests.HierarchyItems.CreateFile.OnlyWriteContent
		 * @param {string} sContent File content.
		 * @param {string} sLockToken Lock token.
		 * @param {string} sMimeType File mime-type.
		 * @param {ITHit.WebDAV.Client.File~WriteContentAsyncCallback} fCallback Function to call when operation is completed.
		 * @returns {ITHit.WebDAV.Client.Request} Request object.
		 */
		WriteContentAsync: function(sContent, sLockToken, sMimeType, fCallback) {
			sLockToken = sLockToken || null;
			sMimeType = sMimeType || '';

			var oRequest = this.Session.CreateRequest(this.__className + '.WriteContentAsync()');

			// Create response.
			var that = this;
			ITHit.WebDAV.Client.Methods.Put.GoAsync(
				oRequest,
				this.Href,
				sMimeType,
				sContent,
				sLockToken,
				this.Host,
				function (oAsyncResult) {
					if (oAsyncResult.IsSuccess) {
						oAsyncResult.Error = that._GetErrorFromWriteContentResponse(oAsyncResult.Result.Response, that.Href);
						if (oAsyncResult.Error !== null) {
							oAsyncResult.IsSuccess = false;
							oAsyncResult.Result = null;
						}
					}

					oRequest.MarkFinish();
					fCallback(oAsyncResult);
				}
			);

			return oRequest;
		},

		/**
		 * Opens document for editting.
		 * @api
		 * @param {string} [sJavaAppletUrl] Url to Java Applet to use for opening the documents in case web browser plugin is not found.
		 */
		EditDocument: function (sJavaAppletUrl) {
			ITHit.WebDAV.Client.DocManager.EditDocument(this.Href, sJavaAppletUrl);
		},

		/**
		 * Retrieves item versions.
		 * @api
		 * @deprecated Use asynchronous method instead
		 * @returns {ITHit.WebDAV.Client.Version[]} List of Versions.
		 * @throws ITHit.WebDAV.Client.Exceptions.NotFoundException This item doesn't exist on the server.
		 * @throws ITHit.WebDAV.Client.Exceptions.WebDavHttpException Server returned unknown error.
		 * @throws ITHit.WebDAV.Client.Exceptions.WebDavException Unexpected error occurred.
		 */
		GetVersions: function() {

			var oRequest = this.Session.CreateRequest(this.__className + '.GetVersions()');

			var oResult = ITHit.WebDAV.Client.Methods.Report.Go(
				oRequest,
				this.Href,
				this.Host,
				ITHit.WebDAV.Client.Methods.Report.ReportType.VersionsTree,
				ITHit.WebDAV.Client.Version.GetRequestProperties()
			);

			var aVersions = ITHit.WebDAV.Client.Version.GetVersionsFromMultiResponse(oResult.Response.Responses, this);

			oRequest.MarkFinish();
			return aVersions;
		},

		/**
		 * Callback function to be called when versions list loaded from server.
		 * @callback ITHit.WebDAV.Client.File~GetVersionsAsyncCallback
		 * @param {ITHit.WebDAV.Client.AsyncResult} oResult Result object
		 * @param {ITHit.WebDAV.Client.Version[]} oResult.Result List of Versions.
		 */

		/**
		 * Retrieves item versions.
		 * @examplecode ITHit.WebDAV.Client.Tests.Versions.GetVersions.GetVersions
		 * @api
		 * @param {ITHit.WebDAV.Client.File~GetVersionsAsyncCallback} fCallback Function to call when operation is completed.
		 * @returns {ITHit.WebDAV.Client.Request} Request object.
		 */
		GetVersionsAsync: function(fCallback) {

			var oRequest = this.Session.CreateRequest(this.__className + '.GetVersionsAsync()');

			var that = this;
			ITHit.WebDAV.Client.Methods.Report.GoAsync(
				oRequest,
				this.Href,
				this.Host,
				ITHit.WebDAV.Client.Methods.Report.ReportType.VersionsTree,
				ITHit.WebDAV.Client.Version.GetRequestProperties(),
				function(oAsyncResult) {
					if (oAsyncResult.IsSuccess) {
						oAsyncResult.Result = ITHit.WebDAV.Client.Version.GetVersionsFromMultiResponse(oAsyncResult.Result.Response.Responses, that);
					}

					oRequest.MarkFinish();
					fCallback(oAsyncResult);
				}
			);

			return oRequest;
		},

		/**
		 * Update to version.
		 * @api
		 * @deprecated Use asynchronous method instead
		 * @param {string|ITHit.WebDAV.Client.Version} mVersion Href to file with version attribute or {@link ITHit.WebDAV.Client.Version} instance.
		 * @returns {boolean}
		 */
		UpdateToVersion: function(mVersion) {
			var sToHrefUpdate = mVersion instanceof ITHit.WebDAV.Client.Version ?
				mVersion.Href :
				mVersion;

			var oRequest = this.Session.CreateRequest(this.__className + '.UpdateToVersion()');

			// Make request.
			var oResult = ITHit.WebDAV.Client.Methods.UpdateToVersion.Go(oRequest, this.Href, this.Host, sToHrefUpdate);

			// Get response.
			var oResponse = oResult.Response;

			// Check status
			var bIsSuccess = oResponse.Responses[0].Status.IsSuccess();

			oRequest.MarkFinish();
			return bIsSuccess;
		},

		/**
		 * Callback function to be called when version is updated on server.
		 * @callback ITHit.WebDAV.Client.File~UpdateToVersionAsyncCallback
		 * @param {ITHit.WebDAV.Client.AsyncResult} oResult Result object
		 */

		/**
		 * Update to version.
		 * @examplecode ITHit.WebDAV.Client.Tests.Versions.ManageVersions.UpdateToVersion
		 * @api
		 * @param {string|ITHit.WebDAV.Client.Version} mVersion Href to file with version attribute or {@link ITHit.WebDAV.Client.Version} instance.
		 * @param {ITHit.WebDAV.Client.File~UpdateToVersionAsyncCallback} fCallback Function to call when operation is completed.
		 * @returns {ITHit.WebDAV.Client.Request} Request object.
		 */
		UpdateToVersionAsync: function(mVersion, fCallback) {
			var sToHrefUpdate = mVersion instanceof ITHit.WebDAV.Client.Version ?
				mVersion.Href :
				mVersion;

			var oRequest = this.Session.CreateRequest(this.__className + '.UpdateToVersionAsync()');

			// Make request.
			ITHit.WebDAV.Client.Methods.UpdateToVersion.GoAsync(oRequest, this.Href, this.Host, sToHrefUpdate, function(oAsyncResult) {
				oAsyncResult.Result = oAsyncResult.IsSuccess && oAsyncResult.Result.Response.Responses[0].Status.IsSuccess();

				oRequest.MarkFinish();
				fCallback(oAsyncResult);
			});

			return oRequest;
		},

		/**
		 * Callback function to be called when versioning is enabled or disabled.
		 * @callback ITHit.WebDAV.Client.File~PutUnderVersionControlAsyncCallback
		 * @param {ITHit.WebDAV.Client.AsyncResult} oResult Result object
		 */

		/**
		 * Enables / disables version control for this file.
		 * @api
		 * @deprecated Use asynchronous method instead
		 * @param {boolean} bEnable True to enable version-control, false - to disable.
		 * @param {string} [mLockToken] Lock token for this item.
		 * @throws ITHit.WebDAV.Client.Exceptions.LockedException This item is locked and invalid lock token was provided.
		 * @throws ITHit.WebDAV.Client.Exceptions.UnauthorizedException Request is not authorized.
		 * @throws ITHit.WebDAV.Client.Exceptions.NotFoundException This file doesn't exist on the server.
		 * @throws ITHit.WebDAV.Client.Exceptions.WebDavHttpException Server returned unknown error.
		 * @throws ITHit.WebDAV.Client.Exceptions.WebDavException In case of any unexpected error.
		 */
		PutUnderVersionControl: function(bEnable, mLockToken) {
			mLockToken = mLockToken || null;

			var oRequest = null;
			var oResult = null;

			if (bEnable) {
				oRequest = this.Session.CreateRequest(this.__className + '.PutUnderVersionControl()');

				oResult = ITHit.WebDAV.Client.Methods.VersionControl.Go(oRequest, this.Href, mLockToken, this.Host);

				var oError = this._GetErrorFromPutUnderVersionControlResponse(oResult.Response);
				if (oError) {
					oRequest.MarkFinish();
					throw oError;
				}

				oRequest.MarkFinish();
			} else {
				oRequest = this.Session.CreateRequest(this.__className + '.PutUnderVersionControl()', 2);

				oResult = ITHit.WebDAV.Client.Methods.Propfind.Go(
					oRequest,
					this.Href,
					ITHit.WebDAV.Client.Methods.Propfind.PropfindMode.SelectedProperties,
					[ITHit.WebDAV.Client.DavConstants.VersionHistory],
					ITHit.WebDAV.Client.Depth.Zero,
					this.Host
				);

				var oProperty = self.GetPropertyValuesFromMultiResponse(oResult.Response, this.Href);

				var aUrls = ITHit.WebDAV.Client.Version.ParseSetOfHrefs(oProperty);
				if (aUrls.length !== 1) {
					throw new ITHit.WebDAV.Client.Exceptions.PropertyException(ITHit.Phrases.ExceptionWhileParsingProperties, this.Href,
						ITHit.WebDAV.Client.DavConstants.VersionHistory, null, ITHit.WebDAV.Client.HttpStatus.None, null);
				}

				oResult = ITHit.WebDAV.Client.Methods.Delete.Go(oRequest, aUrls[0], mLockToken, this.Host);

				var oError = this._GetErrorFromDeleteResponse(oResult.Response);
				if (oError) {
					oRequest.MarkFinish();
					throw oError;
				}

				oRequest.MarkFinish();
			}
		},

		/**
		 * Enables / disables version control for this file.
		 * @api
		 * @examplecode ITHit.WebDAV.Client.Tests.Versions.PutUnderVersion.EnableVersion
		 * @param {boolean} bEnable True to enable version-control, false - to disable.
		 * @param {string} mLockToken Lock token for this item.
		 * @param {ITHit.WebDAV.Client.File~PutUnderVersionControlAsyncCallback} fCallback Function to call when operation is completed.
		 * @returns {ITHit.WebDAV.Client.Request} Request object.
		 */
		PutUnderVersionControlAsync: function(bEnable, mLockToken, fCallback) {
			mLockToken = mLockToken || null;

			var that = this;
			var oRequest = null;

			if (bEnable) {
				oRequest = this.Session.CreateRequest(this.__className + '.PutUnderVersionControlAsync()');

				ITHit.WebDAV.Client.Methods.VersionControl.GoAsync(oRequest, this.Href, mLockToken, this.Host, function(oAsyncResult) {
					if (oAsyncResult.IsSuccess) {
						oAsyncResult.Error = that._GetErrorFromPutUnderVersionControlResponse(oAsyncResult.Result.Response);
						if (oAsyncResult.Error !== null) {
							oAsyncResult.IsSuccess = false;
							oAsyncResult.Result = null;
						}
					}

					oRequest.MarkFinish();
					fCallback(oAsyncResult);
				});

				return oRequest;
			} else {
				oRequest = this.Session.CreateRequest(this.__className + '.PutUnderVersionControlAsync()', 2);

				ITHit.WebDAV.Client.Methods.Propfind.GoAsync(
					oRequest,
					this.Href,
					ITHit.WebDAV.Client.Methods.Propfind.PropfindMode.SelectedProperties,
					[ITHit.WebDAV.Client.DavConstants.VersionHistory],
					ITHit.WebDAV.Client.Depth.Zero,
					this.Host,
					function(oAsyncResult) {
						if (oAsyncResult.IsSuccess) {
							try {
								oAsyncResult.Result = self.GetPropertyValuesFromMultiResponse(oAsyncResult.Result.Response, that.Href);
							} catch(oError) {
								oAsyncResult.Error = oError;
								oAsyncResult.IsSuccess = false;
							}
						}

						if (oAsyncResult.IsSuccess) {
							var aUrls = ITHit.WebDAV.Client.Version.ParseSetOfHrefs(oAsyncResult.Result);
							if (aUrls.length !== 1) {
								throw new ITHit.WebDAV.Client.Exceptions.PropertyException(ITHit.Phrases.ExceptionWhileParsingProperties, that.Href,
									ITHit.WebDAV.Client.DavConstants.VersionHistory, null, ITHit.WebDAV.Client.HttpStatus.None, null);
							}

							ITHit.WebDAV.Client.Methods.Delete.GoAsync(oRequest, aUrls[0], mLockToken, that.Host, function(oAsyncResult) {
								if (oAsyncResult.IsSuccess) {
									oAsyncResult.Error = that._GetErrorFromDeleteResponse(oAsyncResult.Result.Response);
									if (oAsyncResult.Error !== null) {
										oAsyncResult.IsSuccess = false;
										oAsyncResult.Result = null;
									}
								}

								oRequest.MarkFinish();
								fCallback(oAsyncResult);
							});

						} else if (oAsyncResult.Error instanceof ITHit.WebDAV.Client.Exceptions.PropertyNotFoundException) {
							oAsyncResult.IsSuccess = true;
							oAsyncResult.Error = null;
							oAsyncResult.Result = null;

							oRequest.MarkFinish();
							fCallback(oAsyncResult);
						} else {

							oRequest.MarkFinish();
							fCallback(oAsyncResult);
						}
					}
				);
			}
		},

		_GetErrorFromPutUnderVersionControlResponse: function(oResponse) {
			if (!oResponse.Status.IsSuccess()) {
				return new ITHit.WebDAV.Client.Exceptions.WebDavHttpException(ITHit.Phrases.PutUnderVersionControlFailed, this.Href, null, oResponse.Status, null);
			}

			return null;
		},

		_GetErrorFromWriteContentResponse: function(oResponse, sHref) {

			// Whether content is not saved.
			if ( !oResponse.Status.Equals(ITHit.WebDAV.Client.HttpStatus.OK) && !oResponse.Status.Equals(ITHit.WebDAV.Client.HttpStatus.NoContent) ) {
				return new ITHit.WebDAV.Client.Exceptions.WebDavHttpException(ITHit.Phrases.Exceptions.FailedToWriteContentToFile, sHref, null, oResponse.Status, null);
			}

			return null;
		}

	});

})();

/**
 * @class ITHit.WebDAV.Client.Methods.Mkcol
 * @extends ITHit.WebDAV.Client.Methods.HttpMethod
 */
ITHit.DefineClass('ITHit.WebDAV.Client.Methods.Mkcol', ITHit.WebDAV.Client.Methods.HttpMethod, /** @lends ITHit.WebDAV.Client.Methods.Mkcol.prototype */{

	__static: /** @lends ITHit.WebDAV.Client.Methods.Mkcol */{

		Go: function (oRequest, sHref, sLockToken, sHost) {
			// Create request.
			var oWebDavRequest = this.createRequest(oRequest, sHref, sLockToken, sHost);

			var oResponse = oWebDavRequest.GetResponse();
			var oSingleResponse = new ITHit.WebDAV.Client.Methods.SingleResponse(oResponse);
			return new ITHit.WebDAV.Client.Methods.Mkcol(oSingleResponse);
		},

		GoAsync: function (oRequest, sHref, sLockToken, sHost, fCallback) {
			// Create request.
			var oWebDavRequest = this.createRequest(oRequest, sHref, sLockToken, sHost);

			oWebDavRequest.GetResponse(function (oAsyncResult) {
				if (!oAsyncResult.IsSuccess) {
					fCallback(new ITHit.WebDAV.Client.AsyncResult(null, false, oAsyncResult.Error));
					return;
				}

				var oSingleResponse = new ITHit.WebDAV.Client.Methods.SingleResponse(oAsyncResult.Result);
				var oMkcol = new ITHit.WebDAV.Client.Methods.Mkcol(oSingleResponse);
				fCallback(new ITHit.WebDAV.Client.AsyncResult(oMkcol, true, null));
			});

			return oWebDavRequest;
		},

		createRequest: function (oRequest, sHref, sLockToken, sHost) {

			// Creare request.
			var oWebDavRequest = oRequest.CreateWebDavRequest(sHost, sHref, sLockToken);
			oWebDavRequest.Method('MKCOL');

			return oWebDavRequest;
		}

	}
});


;
(function () {

	/**
	 * @class ITHit.WebDAV.Client.Methods.Head
	 * @extends ITHit.WebDAV.Client.Methods.HttpMethod
	 */
	var self = ITHit.DefineClass('ITHit.WebDAV.Client.Methods.Head', ITHit.WebDAV.Client.Methods.HttpMethod, /** @lends ITHit.WebDAV.Client.Methods.Head.prototype */{

		__static: /** @lends ITHit.WebDAV.Client.Methods.Head */{

			Go: function (oRequest, sHref, sHost) {
				try {
					return this._super.apply(this, arguments);
				} catch (oException) {

					// Check whether exception is an instance of NotFoundException class.
					if (oException instanceof ITHit.WebDAV.Client.Exceptions.NotFoundException) {
						var oResult = new self(null, sHref);
						oResult.IsOK = false;
						return oResult;
					}

					// Rethrow exception for all other cases.
					throw oException;
				}
			},

			GoAsync: function (oRequest, sHref, sHost, fCallback) {
				return this._super(oRequest, sHref, sHost, function (oAsyncResult) {
					if (oAsyncResult.Error instanceof ITHit.WebDAV.Client.Exceptions.NotFoundException) {
						oAsyncResult.Result = new self(null, sHref);
						oAsyncResult.Result.IsOK = false;
						oAsyncResult.IsSuccess = true;
						oAsyncResult.Error = null;
					}

					fCallback(oAsyncResult);
				});
			},

			_ProcessResponse: function (oResponse, sHref) {
				var oResult = this._super(oResponse, sHref);
				oResult.IsOK = oResponse.Status.Equals(ITHit.WebDAV.Client.HttpStatus.OK);
				return oResult;
			},

			_CreateRequest: function (oRequest, sHref, sHost) {
				// Create request.
				var oWebDavRequest = oRequest.CreateWebDavRequest(sHost, sHref);
				oWebDavRequest.Method('HEAD');

				return oWebDavRequest;
			}

		},

		/**
		 * @type {boolean}
		 */
		IsOK: null
	});

})();


ITHit.DefineClass('ITHit.WebDAV.Client.SearchQuery', null, /** @lends ITHit.WebDAV.Client.SearchQuery.prototype */{

	/**
	 * Searching phrase
	 * @api
	 * @type {string}
	 */
	Phrase: null,

	/**
	 * Selected properties. Set empty array for select default list properties
	 * @api
	 * @type {ITHit.WebDAV.Client.PropertyName[]}
	 */
	SelectProperties: null,

	/**
	 * Set true for search by like condition in properties. @see `LikeProperties` param
	 * @api
	 * @type {boolean}
	 */
	EnableLike: null,

	/**
	 * Properties for like conditions
	 * @api
	 * @type {ITHit.WebDAV.Client.PropertyName[]}
	 */
	LikeProperties: null,

	/**
	 * Set true for search by file contents
	 * @api
	 * @type {boolean}
	 */
	EnableContains: null,

	/**
	 * Query class for search request
	 * Create new instance of SearchQuery class.
	 * @constructs
	 * @api
	 * @param {string} sSearchPhrase Searching phrase
	 */
	constructor: function(sSearchPhrase) {
		this.Phrase = sSearchPhrase;
		this.SelectProperties = [];
		this.EnableLike = true;
		this.LikeProperties = [
			ITHit.WebDAV.Client.DavConstants.DisplayName
		];
		this.EnableContains = true;
	}

});


/**
 * @class ITHit.WebDAV.Client.Methods.Search
 * @extends ITHit.WebDAV.Client.Methods.HttpMethod
 */
ITHit.DefineClass('ITHit.WebDAV.Client.Methods.Search', ITHit.WebDAV.Client.Methods.HttpMethod, /** @lends ITHit.WebDAV.Client.Methods.Search.prototype */{

	__static: /** @lends ITHit.WebDAV.Client.Methods.Search */{

		Go: function (oRequest, sHref, sHost, oSearchQuery) {
			// Create request.
			var oWebDavRequest = this._createRequest(oRequest, sHref, sHost, oSearchQuery);
			var oResponse = oWebDavRequest.GetResponse();

			return this._ProcessResponse(oResponse)
		},

		GoAsync: function (oRequest, sHref, sHost, oSearchQuery, fCallback) {
			// Create request.
			var oWebDavRequest = this._createRequest(oRequest, sHref, sHost, oSearchQuery);

			var that = this;
			oWebDavRequest.GetResponse(function (oAsyncResult) {
				if (!oAsyncResult.IsSuccess) {
					fCallback(new ITHit.WebDAV.Client.AsyncResult(null, false, oAsyncResult.Error));
					return;
				}

				var oResult = that._ProcessResponse(oAsyncResult.Result, sHref);
				fCallback(new ITHit.WebDAV.Client.AsyncResult(oResult, true, null));
			});

			return oWebDavRequest;
		},

		_ProcessResponse: function (oResponse, sUri) {
			// Receive data.
			var oResponseData = oResponse.GetResponseStream();

			var oMultiResponse = new ITHit.WebDAV.Client.Methods.MultiResponse(oResponseData, sUri);

			// Return result object.
			return new ITHit.WebDAV.Client.Methods.Search(oMultiResponse);
		},

		_createRequest: function (oRequest, sHref, sHost, oSearchQuery) {

			/*
			 Search example request:
			 <d:searchrequest xmlns:d="DAV:" >
			 <d:basicsearch>
			 <d:select>
			 <d:prop><d:allprop/></d:prop>
			 </d:select>
			 <d:where>
			 <d:or>
			 <d:like>
			 <d:prop><d:displayname/></d:prop>
			 <d:literal>search phrase</d:literal>
			 </d:like>
			 <d:contains>search phrase</d:contains>
			 </d:or>
			 </d:where>
			 </d:basicsearch>
			 </d:searchrequest>
			 */

			// Create request.
			var oWebDavRequest = oRequest.CreateWebDavRequest(sHost, sHref);
			oWebDavRequest.Method('SEARCH');

			// Create XML document.
			var oWriter = new ITHit.XMLDoc();
			var nsDavConstants = ITHit.WebDAV.Client.DavConstants;
			var sNamespaceUri = nsDavConstants.NamespaceUri;

			// Select block
			var eSelectProp = oWriter.createElementNS(sNamespaceUri, 'prop');
			if (oSearchQuery.SelectProperties && oSearchQuery.SelectProperties.length > 0) {
				// Selected properties.
				for (var i = 0; i < oSearchQuery.SelectProperties.length; i++) {
					eSelectProp.appendChild(oWriter.createElementNS(oSearchQuery.SelectProperties[i].NamespaceUri, oSearchQuery.SelectProperties[i].Name));
				}
			} else {
				// All properties.
				eSelectProp.appendChild(sNamespaceUri, 'allprop');
			}
			var eSelect = oWriter.createElementNS(sNamespaceUri, 'select');
			eSelect.appendChild(eSelectProp);

			// Where like
			var eLike = null;
			if (oSearchQuery.EnableLike) {
				var eWhereProp = oWriter.createElementNS(sNamespaceUri, 'prop');
				if (oSearchQuery.LikeProperties && oSearchQuery.LikeProperties.length > 0) {
					for (var i = 0; i < oSearchQuery.LikeProperties.length; i++) {
						eWhereProp.appendChild(oWriter.createElementNS(oSearchQuery.LikeProperties[i].NamespaceUri, oSearchQuery.LikeProperties[i].Name));
					}
				}
				var eLiteral = oWriter.createElementNS(sNamespaceUri, 'literal');
				eLiteral.appendChild(
					oWriter.createTextNode(oSearchQuery.Phrase)
				);
				eLike = oWriter.createElementNS(sNamespaceUri, 'like');
				eLike.appendChild(eWhereProp);
				eLike.appendChild(eLiteral);
			}

			// Where contains
			var eContains = null;
			if (oSearchQuery.EnableContains) {
				eContains = oWriter.createElementNS(sNamespaceUri, 'contains');
				eContains.appendChild(
					oWriter.createTextNode(oSearchQuery.Phrase)
				);
			}

			// Where block
			var eWhere = oWriter.createElementNS(sNamespaceUri, 'where');
			if (eLike && eContains) {
				var eOr = oWriter.createElementNS(sNamespaceUri, 'or');
				eOr.appendChild(eLike);
				eOr.appendChild(eContains);
				eWhere.appendChild(eOr);
			} else if (eLike) {
				eWhere.appendChild(eLike);
			} else if (eContains) {
				eWhere.appendChild(eContains);
			}

			var basicSearch = oWriter.createElementNS(sNamespaceUri, 'basicsearch');
			basicSearch.appendChild(eSelect);
			basicSearch.appendChild(eWhere);

			// Root block
			var eSearchRequest = oWriter.createElementNS(sNamespaceUri, 'searchrequest');
			eSearchRequest.appendChild(basicSearch);
			oWriter.appendChild(eSearchRequest);

			// Assign created document as body for request.
			oWebDavRequest.Body(oWriter);

			// Return request object.
			return oWebDavRequest;
		}

	}
});


;
(function() {

    /**
     * Represents a folder in a WebDAV repository.
     * @api
     * @class ITHit.WebDAV.Client.Folder
     * @extends ITHit.WebDAV.Client.HierarchyItem
     */
    var self = ITHit.DefineClass('ITHit.WebDAV.Client.Folder', ITHit.WebDAV.Client.HierarchyItem, /** @lends ITHit.WebDAV.Client.Folder.prototype */{

        __static: /** @lends ITHit.WebDAV.Client.Folder */{

            GetRequestProperties: function() {
                return [
                    ITHit.WebDAV.Client.DavConstants.ResourceType,
                    ITHit.WebDAV.Client.DavConstants.DisplayName,
                    ITHit.WebDAV.Client.DavConstants.CreationDate,
                    ITHit.WebDAV.Client.DavConstants.GetLastModified,
                    ITHit.WebDAV.Client.DavConstants.SupportedLock,
                    ITHit.WebDAV.Client.DavConstants.LockDiscovery,
                    ITHit.WebDAV.Client.DavConstants.QuotaAvailableBytes,
                    ITHit.WebDAV.Client.DavConstants.QuotaUsedBytes,
                    ITHit.WebDAV.Client.DavConstants.CheckedIn,
                    ITHit.WebDAV.Client.DavConstants.CheckedOut
                ];
            },

            ParseHref: function(sHref) {
                // Normalize href
                var aHrefParts = sHref.split('?');
                aHrefParts[0] = aHrefParts[0].replace(/\/?$/, '/');
                sHref = ITHit.WebDAV.Client.Encoder.EncodeURI(aHrefParts.join('?'));

                return this._super(sHref);
            },

            /**
             * Open folder.
             * @deprecated Use asynchronous method instead
             * @param {ITHit.WebDAV.Client.Request} oRequest Current WebDAV session.
             * @param {string} sHref This item path on the server.
			 * @param {ITHit.WebDAV.Client.PropertyName[]} [aProperties] Additional properties requested from server. Default is empty array.
             * @returns {ITHit.WebDAV.Client.Folder} Loaded folder object.
             * @throws ITHit.WebDAV.Client.Exceptions.WebDavException A Folder was expected or the response doesn't have required item.
             */
            OpenItem: function(oRequest, sHref, aProperties) {
				aProperties = aProperties || [];

                var oFolder = this._super(oRequest, sHref, aProperties);

                // Throw exception if there is not a folder type.
                if (!(oFolder instanceof self)) {
                    throw new ITHit.WebDAV.Client.Exceptions.WebDavException(ITHit.Phrases.ResponseFolderWrongType.Paste(sHref));
                }

                return oFolder;
            },

            /**
             * Callback function to be called when folder loaded from server.
             * @callback ITHit.WebDAV.Client.Folder~OpenItemAsyncCallback
             * @param {ITHit.WebDAV.Client.AsyncResult} oResult Result object
             * @param {ITHit.WebDAV.Client.Folder} oResult.Result Loaded folder object.
             */

            /**
             * Open folder.
             * @param {ITHit.WebDAV.Client.Request} oRequest Current WebDAV session.
             * @param {string} sHref This item path on the server.
			 * @param {ITHit.WebDAV.Client.PropertyName[]} aProperties Additional properties requested from server. Default is empty array.
             * @param {ITHit.WebDAV.Client.Folder~OpenItemAsyncCallback} fCallback Function to call when operation is completed.
             * @returns {ITHit.WebDAV.Client.Request} Request object.
             */
            OpenItemAsync: function(oRequest, sHref, aProperties, fCallback) {
				aProperties = aProperties || [];

                return this._super(oRequest, sHref, aProperties, function(oAsyncResult) {
                    // Throw exception if there is not a resource type.
                    if (oAsyncResult.IsSuccess && !(oAsyncResult.Result instanceof self)) {
                        oAsyncResult.Error = new ITHit.WebDAV.Client.Exceptions.WebDavException(ITHit.Phrases.ResponseFolderWrongType.Paste(sHref));
                        oAsyncResult.IsSuccess = false;
                    }

                    fCallback(oAsyncResult);
                });
            }

        },

        /**
         * Create new instance of Folder class which represents a folder in a WebDAV repository.
         * @param {ITHit.WebDAV.Client.WebDavSession} oSession Current WebDAV session.
         * @param {string} sHref This item path on the server.
         * @param {object} oGetLastModified Most recent modification date.
         * @param {string} sDisplayName User friendly item name.
         * @param {object} oCreationDate The date item was created.
         * @param {Array} aSupportedLocks
         * @param {Array} aActiveLocks
         * @param {string} sHost
         * @param {number} iAvailableBytes
         * @param {number} iUsedBytes
         * @param {Array} aCheckedIn
         * @param {Array} aCheckedOut
         * @param {ITHit.WebDAV.Client.PropertyName[]} aProperties
         */
        constructor: function(oSession, sHref, oGetLastModified, sDisplayName, oCreationDate, aSupportedLocks, aActiveLocks, sHost, iAvailableBytes, iUsedBytes, aCheckedIn, aCheckedOut, aProperties) {
            sHref = sHref.replace(/\/?$/, '/');

            this._super(oSession, sHref, oGetLastModified, sDisplayName, oCreationDate, ITHit.WebDAV.Client.ResourceType.Folder, aSupportedLocks, aActiveLocks, sHost, iAvailableBytes, iUsedBytes, aCheckedIn, aCheckedOut, aProperties);

            // Normalize folder shortcuts, force set end slash
            this._Url = this._Url.replace(/\/?$/, '/');
            this._AbsoluteUrl = this._AbsoluteUrl.replace(/\/?$/, '/');
        },

        /**
         *
         * @returns {boolean}
         */
        IsFolder: function() {
            return true;
        },

        /**
         * Creates a new folder with a specified name as child of this folder.
         * @api
         * @deprecated Use asynchronous method instead
         * @param {string} sName Name of the new folder.
         * @param {string} [sLockTokens] Lock token for ITHit.WebDAV.Client.Folder folder.
		 * @param {ITHit.WebDAV.Client.PropertyName[]} [aProperties] Additional properties requested from server. Default is empty array.
         * @returns {ITHit.WebDAV.Client.Folder} Created folder object.
         * @throws ITHit.WebDAV.Client.Exceptions.MethodNotAllowedException Item with specified name already exists.
         * @throws ITHit.WebDAV.Client.Exceptions.ForbiddenException Creation of child items not allowed.
         * @throws ITHit.WebDAV.Client.Exceptions.NotFoundException This folder doesn't exist on the server.
         * @throws ITHit.WebDAV.Client.Exceptions.LockedException This folder is locked and no or invalid lock token was specified.
         * @throws ITHit.WebDAV.Client.Exceptions.WebDavHttpException Server returned unknow error.
         */
        CreateFolder: function(sName, sLockTokens, aProperties) {
			aProperties = aProperties || [];

            // Start logging.
            var oRequest = this.Session.CreateRequest(this.__className + '.CreateFolder()', 2);

            // Set default value if needed.
            sLockTokens = sLockTokens || null;

            // Get URI for new folder.
            var sNewUri = ITHit.WebDAV.Client.HierarchyItem.AppendToUri(this.Href, sName);

            // Create folder.
            var oResponse = ITHit.WebDAV.Client.Methods.Mkcol.Go(oRequest, sNewUri, sLockTokens, this.Host).Response;

            // Whether folder is not created.
            if (!oResponse.Status.Equals(ITHit.WebDAV.Client.HttpStatus.Created)) {
                oRequest.MarkFinish();
                throw new ITHit.WebDAV.Client.Exceptions.WebDavHttpException(ITHit.Phrases.Exceptions.FailedCreateFolder, sNewUri, null, oResponse.Status, null);
            }

            // Return created folder object.
            var oFolder = ITHit.WebDAV.Client.Folder.OpenItem(oRequest, ITHit.WebDAV.Client.Encoder.DecodeURI(sNewUri), aProperties);

            oRequest.MarkFinish();
            return oFolder;
        },

        /**
         * Callback function to be called when folder is created on server.
         * @callback ITHit.WebDAV.Client.Folder~CreateFolderAsyncCallback
         * @param {ITHit.WebDAV.Client.AsyncResult} oResult Result object
         * @param {ITHit.WebDAV.Client.Folder} oResult.Result Created folder object.
         */

        /**
         * Creates a new folder with a specified name as child of this folder.
         * @api
         * @examplecode ITHit.WebDAV.Client.Tests.HierarchyItems.CreateFolder.CreateFolder
         * @param {string} sName Name of the new folder.
         * @param {string} [sLockTokens] Lock token for ITHit.WebDAV.Client.Folder folder.
		 * @param {ITHit.WebDAV.Client.PropertyName[]} aProperties Additional properties requested from server. Default is empty array.
         * @param {ITHit.WebDAV.Client.Folder~CreateFolderAsyncCallback} fCallback Function to call when operation is completed.
         * @returns {ITHit.WebDAV.Client.Request} Request object.
         */
        CreateFolderAsync: function (sName, sLockTokens, aProperties, fCallback) {
			aProperties = aProperties || [];

            var oRequest = this.Session.CreateRequest(this.__className + '.CreateFolderAsync()', 2);

            var sNewUri = ITHit.WebDAV.Client.HierarchyItem.AppendToUri(this.Href, sName);
            ITHit.WebDAV.Client.Methods.Mkcol.GoAsync(oRequest, sNewUri, sLockTokens, this.Host, function(oAsyncResult) {

                // Whether folder is not created.
                if (oAsyncResult.IsSuccess && !oAsyncResult.Result.Response.Status.Equals(ITHit.WebDAV.Client.HttpStatus.Created)) {
                    oAsyncResult.IsSuccess = false;
                    oAsyncResult.Error = new ITHit.WebDAV.Client.Exceptions.WebDavHttpException(
                        ITHit.Phrases.Exceptions.FailedCreateFolder,
                        sNewUri,
                        null,
                        oAsyncResult.Result.Response.Status
                    );
                }

                if (oAsyncResult.IsSuccess) {
                    self.OpenItemAsync(oRequest, sNewUri, aProperties, function(oAsyncResult) {

                        oRequest.MarkFinish();
                        fCallback(oAsyncResult);
                    });
                } else {
                    oAsyncResult.Result = null;

                    oRequest.MarkFinish();
                    fCallback(oAsyncResult);
                }
            });

            return oRequest;
        },

        /**
         * Creates a new file with a specified name as a child of this folder.
         * @api
         * @deprecated Use asynchronous method instead
         * @param {string} sName Name of the new folder.
         * @param {string} [sLockTokens] Lock token for current folder.
         * @param {string} [sContent] File content.
		 * @param {ITHit.WebDAV.Client.PropertyName[]} [aProperties] Additional properties requested from server. Default is empty array.
         * @returns {ITHit.WebDAV.Client.File} Created file object.
         * @throws ITHit.WebDAV.Client.Exceptions.ForbiddenException Creation of child items not allowed.
         * @throws ITHit.WebDAV.Client.Exceptions.NotFoundException This folder doesn't exist on the server.
         * @throws ITHit.WebDAV.Client.Exceptions.LockedException This folder is locked and no or invalid lock token was specified.
         * @throws ITHit.WebDAV.Client.Exceptions.WebDavHttpException Server returned unknown error.
         * @throws ITHit.WebDAV.Client.Exceptions.WebDavException Unexpected error occurred.
         */
        CreateFile: function(sName, sLockTokens, sContent, aProperties) {
            sLockTokens = sLockTokens || null;
            sContent = sContent || '';
			aProperties = aProperties || [];

            var oRequest = this.Session.CreateRequest(this.__className + '.CreateFile()', 2);

            // Get URI for new folder.
            var sNewUri = ITHit.WebDAV.Client.HierarchyItem.AppendToUri(this.Href, sName);

            // Create file.
            var oResult = ITHit.WebDAV.Client.Methods.Put.Go(
                oRequest,
                sNewUri,
                '',
                sContent,
                sLockTokens,
                this.Host
            );

            var oError = this._GetErrorFromCreateFileResponse(oResult.Response, sNewUri);
            if (oError) {
                oRequest.MarkFinish();
                throw oError;
            }

            // Return created file object.
            var oFile = ITHit.WebDAV.Client.File.OpenItem(oRequest, sNewUri, aProperties);

            oRequest.MarkFinish();
            return oFile;
        },

        /**
         * Callback function to be called when file is created on server.
         * @callback ITHit.WebDAV.Client.Folder~CreateFileAsyncCallback
         * @param {ITHit.WebDAV.Client.AsyncResult} oResult Result object
         * @param {ITHit.WebDAV.Client.File} oResult.Result Created file object.
         */

        /**
         * Creates a new file with a specified name as a child of this folder.
         * @api
         * @examplecode ITHit.WebDAV.Client.Tests.HierarchyItems.CreateFile.CreateAndWriteContent
         * @param {string} sName Name of the new folder.
         * @param {string} sLockTokens Lock token for current folder.
         * @param {string} sContent File content.
		 * @param {ITHit.WebDAV.Client.PropertyName[]} aProperties Additional properties requested from server. Default is empty array.
         * @param {ITHit.WebDAV.Client.Folder~CreateFileAsyncCallback} fCallback Function to call when operation is completed.
         * @returns {ITHit.WebDAV.Client.Request} Request object.
         */
        CreateFileAsync: function(sName, sLockTokens, sContent, aProperties, fCallback) {
            sLockTokens = sLockTokens || null;
            sContent = sContent || '';
			aProperties = aProperties || [];

            var oRequest = this.Session.CreateRequest(this.__className + '.CreateFileAsync()', 2);

            // Get URI for new folder.
            var sNewUri = ITHit.WebDAV.Client.HierarchyItem.AppendToUri(this.Href, sName);

            var that = this;
            ITHit.WebDAV.Client.Methods.Put.GoAsync(
                oRequest,
                sNewUri,
                '',
                sContent,
                sLockTokens,
                this.Host,
                function(oAsyncResult) {
                    if (oAsyncResult.IsSuccess) {
                        oAsyncResult.Error = that._GetErrorFromCreateFileResponse(oAsyncResult.Result.Response);
                        if (oAsyncResult.Error !== null) {
                            oAsyncResult.IsSuccess = false;
                            oAsyncResult.Result = null;
                        }
                    }

                    if (oAsyncResult.IsSuccess) {
                        ITHit.WebDAV.Client.File.OpenItemAsync(oRequest, sNewUri, aProperties, function(oAsyncResult) {

                            oRequest.MarkFinish();
                            fCallback(oAsyncResult);
                        });
                    } else {

                        oRequest.MarkFinish();
                        fCallback(oAsyncResult);
                    }
                }
            );

            return oRequest;
        },

        /**
         * Legacy proxy to CreateFile() method
         * @deprecated
         * @param sName
         * @param sLockTokens
         * @param sContent
         */
        CreateResource: function(sName, sLockTokens,  sContent, aProperties) {
            return this.CreateFile(sName, sLockTokens, sContent, aProperties);
        },

        /**
         * Legacy proxy to CreateFileAsync() method
         * @deprecated
         * @param sName
         * @param sLockTokens
         * @param sContent
         * @param fCallback
         */
        CreateResourceAsync: function(sName, sLockTokens, sContent, aProperties, fCallback) {
            return this.CreateFileAsync(sName, sLockTokens, sContent, aProperties, fCallback);
        },

        /**
         * Locks name for later use.
         * @api
         * @deprecated Use asynchronous method instead
         * @param {string} sNewItemName Name of new item.
         * @param {string} sLockScope Scope of the lock.
         * @param {boolean} bDeep Whether to lock entire subtree.
         * @param {string} sOwner Owner of the lock.
         * @param {number} iTimeout TimeOut after which lock expires.
         * @returns {ITHit.WebDAV.Client.LockInfo} Instance of LockInfo object with information about created lock.
         * @throws ITHit.WebDAV.Client.Exceptions.NotFoundException This folder doesn't exist on the server.(Server in fact returns Conflict)
         * @throws ITHit.WebDAV.Client.Exceptions.LockedException This folder is locked and no or invalid lock token was specified.
         * @throws ITHit.WebDAV.Client.Exceptions.ForbiddenException The client, for reasons the server chooses not to specify, cannot apply the lock.
         * @throws ITHit.WebDAV.Client.Exceptions.WebDavHttpException Server returned unknown error.
         * @throws ITHit.WebDAV.Client.Exceptions.WebDavException Unexpected error occurred.
         */
        CreateLockNull: function(sNewItemName, sLockScope, bDeep, sOwner, iTimeout) {

            var oRequest = this.Session.CreateRequest(this.__className + '.CreateLockNull()');

            // Get new URI.
            var sNewUri = ITHit.WebDAV.Client.HierarchyItem.AppendToUri(this.Href, sNewItemName);

            // Make request.
            var oResult = ITHit.WebDAV.Client.Methods.Lock.Go(
                oRequest,
                sNewUri,
                iTimeout,
                sLockScope,
                this.Host,
                bDeep,
                sOwner
            );

            oRequest.MarkFinish();

            // Return lock info.
            return oResult.LockInfo;
        },

        /**
         * Returns children of ITHit.WebDAV.Client.Folder folder.
         * @api
         * @deprecated Use asynchronous method instead
         * @param {boolean} [bRecursively] Indicates if all subtree of children should be return. Default is false.
         * @param {ITHit.WebDAV.Client.PropertyName[]} [aProperties] Additional properties requested from server. Default is empty array.
         * @returns {ITHit.WebDAV.Client.HierarchyItem[]} Array of file and folder objects.
         * @throws ITHit.WebDAV.Client.Exceptions.NotFoundException This folder doesn't exist on the server.
         * @throws ITHit.WebDAV.Client.Exceptions.WebDavHttpException Server returned unknown error.
         * @throws ITHit.WebDAV.Client.Exceptions.WebDavException Unexpected error occurred.
         */
        GetChildren: function(bRecursively, aProperties) {
            bRecursively = bRecursively || false;
            aProperties = aProperties || [];

            var oRequest = this.Session.CreateRequest(this.__className + '.GetChildren()');

            var aCustomProperties = ITHit.WebDAV.Client.HierarchyItem.GetCustomRequestProperties(aProperties);
            var aAllProperties = aCustomProperties.concat(ITHit.WebDAV.Client.HierarchyItem.GetRequestProperties());

            // Make response.
            var oResult = ITHit.WebDAV.Client.Methods.Propfind.Go(
                oRequest,
                this.Href,
                ITHit.WebDAV.Client.Methods.Propfind.PropfindMode.SelectedProperties,
                aAllProperties,
                bRecursively ? ITHit.WebDAV.Client.Depth.Infinity : ITHit.WebDAV.Client.Depth.One,
                this.Host
            );

            var aItems = ITHit.WebDAV.Client.HierarchyItem.GetItemsFromMultiResponse(oResult.Response, oRequest, this.Href, aCustomProperties);

            oRequest.MarkFinish();
            return aItems;
        },

        /**
         * Callback function to be called when children items loaded from server.
         * @callback ITHit.WebDAV.Client.Folder~GetChildrenAsyncCallback
         * @param {ITHit.WebDAV.Client.AsyncResult} oResult Result object
         * @param {ITHit.WebDAV.Client.HierarchyItem[]} oResult.Result Array of file and folder objects.
         */

        /**
         * Get children of ITHit.WebDAV.Client.Folder folder. If boolean parameter is set to false only children of this folder are requested.
         * Otherwise the entire subtree is requested.
         * @api
         * @examplecode ITHit.WebDAV.Client.Tests.HierarchyItems.GetFolderItems.GetChildren
         * @param {boolean} bRecursively Indicates if all subtree of children should be return. Default is false.
         * @param {ITHit.WebDAV.Client.PropertyName[]} aProperties Additional properties requested from server. Default is empty array.
         * @param {ITHit.WebDAV.Client.Folder~GetChildrenAsyncCallback} fCallback Function to call when operation is completed.
         * @returns {ITHit.WebDAV.Client.Request} Request object.
         */
        GetChildrenAsync: function(bRecursively, aProperties, fCallback) {
            bRecursively = bRecursively || false;
            if (typeof aProperties === 'function') {
                fCallback = aProperties;
                aProperties = [];
            } else {
                aProperties = aProperties || [];
                fCallback = fCallback || function() {};
            }

            var oRequest = this.Session.CreateRequest(this.__className + '.GetChildrenAsync()');

            var aCustomProperties = ITHit.WebDAV.Client.HierarchyItem.GetCustomRequestProperties(aProperties);
            var aAllProperties = aCustomProperties.concat(ITHit.WebDAV.Client.HierarchyItem.GetRequestProperties());

            // Make response.
            var that = this;
            ITHit.WebDAV.Client.Methods.Propfind.GoAsync(
                oRequest,
                this.Href,
                ITHit.WebDAV.Client.Methods.Propfind.PropfindMode.SelectedProperties,
                aAllProperties,
                bRecursively ? ITHit.WebDAV.Client.Depth.Infinity : ITHit.WebDAV.Client.Depth.One,
                this.Host,
                function(oAsyncResult) {
                    if (oAsyncResult.IsSuccess) {
                        oAsyncResult.Result = ITHit.WebDAV.Client.HierarchyItem.GetItemsFromMultiResponse(oAsyncResult.Result.Response, oRequest, that.Href, aCustomProperties);
                    }

                    oRequest.MarkFinish();
                    fCallback(oAsyncResult);
                }
            );

            return oRequest;
        },

        /**
         * Gets the specified folder from server.
         * @api
         * @deprecated Use asynchronous method instead
         * @param {string} sName Name of the folder.
         * @returns {ITHit.WebDAV.Client.Folder} Folder object corresponding to requested path.
         * @throws ITHit.WebDAV.Client.Exceptions.UnauthorizedException Incorrect credentials provided or insufficient permissions to access the requested item.
         * @throws ITHit.WebDAV.Client.Exceptions.NotFoundException The requested folder doesn't exist on the server.
         * @throws ITHit.WebDAV.Client.Exceptions.ForbiddenException The server refused to fulfill the request.
         * @throws ITHit.WebDAV.Client.Exceptions.WebDavException Unexpected error occurred.
         */
        GetFolder: function(sName) {
            var oRequest = this.Session.CreateRequest(this.__className + '.GetFolder()');
            var sHref = ITHit.WebDAV.Client.HierarchyItem.AppendToUri(this.Href, sName);
            var oFolder = self.OpenItem(oRequest, sHref);

            oRequest.MarkFinish();
            return oFolder;
        },

        /**
         * Callback function to be called when folder loaded from server.
         * @callback ITHit.WebDAV.Client.Folder~GetFolderAsyncCallback
         * @param {ITHit.WebDAV.Client.AsyncResult} oResult Result object
         * @param {ITHit.WebDAV.Client.Folder} oResult.Result Folder object corresponding to requested path.
         */

        /**
         * Gets the specified folder from server.
         * @api
         * @param {string} sName Name of the folder.
         * @param {ITHit.WebDAV.Client.Folder~GetFolderAsyncCallback} fCallback Function to call when operation is completed.
         * @returns {ITHit.WebDAV.Client.Request} Request object.
         */
        GetFolderAsync: function(sName, fCallback) {
            var oRequest = this.Session.CreateRequest(this.__className + '.GetFolderAsync()');
            var sHref = ITHit.WebDAV.Client.HierarchyItem.AppendToUri(this.Href, sName);
            self.OpenItemAsync(oRequest, sHref, null, function(oAsyncResult) {

                oRequest.MarkFinish();
                fCallback(oAsyncResult);
            });

            return oRequest;
        },

        /**
         * Gets the specified file from server.
         * @api
         * @deprecated Use asynchronous method instead
         * @param {string} sName Name of the file.
         * @returns {ITHit.WebDAV.Client.File} File corresponding to requested path.
         * @throws ITHit.WebDAV.Client.Exceptions.UnauthorizedException Incorrect credentials provided or insufficient permissions to access the requested item.
         * @throws ITHit.WebDAV.Client.Exceptions.NotFoundException The requested file doesn't exist on the server.
         * @throws ITHit.WebDAV.Client.Exceptions.ForbiddenException The server refused to fulfill the request.
         * @throws ITHit.WebDAV.Client.Exceptions.WebDavException Unexpected error occurred.
         */
        GetFile: function(sName) {
            var oRequest = this.Session.CreateRequest(this.__className + '.GetFile()');
            var sHref = ITHit.WebDAV.Client.HierarchyItem.AppendToUri(this.Href, sName);
            var oFile = ITHit.WebDAV.Client.File.OpenItem(oRequest, sHref);

            oRequest.MarkFinish();
            return oFile;
        },

        /**
         * Callback function to be called when file loaded from server.
         * @callback ITHit.WebDAV.Client.Folder~GetFileAsyncCallback
         * @param {ITHit.WebDAV.Client.AsyncResult} oResult Result object
         * @param {ITHit.WebDAV.Client.File} oResult.Result File corresponding to requested path.
         */

        /**
         * Gets the specified file from server.
         * @examplecode ITHit.WebDAV.Client.Tests.HierarchyItems.GetItemByFolder.GetFile
         * @api
         * @param {string} sName Name of the folder.
         * @param {ITHit.WebDAV.Client.Folder~GetFileAsyncCallback} fCallback Function to call when operation is completed.
         * @returns {ITHit.WebDAV.Client.Request} Request object.
         */
        GetFileAsync: function(sName, fCallback) {
            var oRequest = this.Session.CreateRequest(this.__className + '.GetFileAsync()');
            var sHref = ITHit.WebDAV.Client.HierarchyItem.AppendToUri(this.Href, sName);
            ITHit.WebDAV.Client.File.OpenItemAsync(oRequest, sHref, null, function(oAsyncResult) {

                oRequest.MarkFinish();
                fCallback(oAsyncResult);
            });

            return oRequest;
        },

        /**
         * Legacy proxy to GetFile() method
         * @deprecated
         * @param sName
         */
        GetResource: function(sName) {
            return this.GetFile(sName);
        },

        /**
         * Legacy proxy to GetFileAsync() method
         * @deprecated
         * @param sName
         * @param fCallback
         */
        GetResourceAsync: function(sName, fCallback) {
            return this.GetFileAsync(sName, fCallback);
        },

        /**
         * Gets the specified item from server.
         * @api
         * @deprecated Use asynchronous method instead
         * @param {string} sName Name of folder or file.
         * @returns {ITHit.WebDAV.Client.HierarchyItem} Item object corresponding to requested path.
         * @throws ITHit.WebDAV.Client.Exceptions.UnauthorizedException Incorrect credentials provided or insufficient permissions to access the requested item.
         * @throws ITHit.WebDAV.Client.Exceptions.NotFoundException The requested folder doesn't exist on the server.
         * @throws ITHit.WebDAV.Client.Exceptions.ForbiddenException The server refused to fulfill the request.
         * @throws ITHit.WebDAV.Client.Exceptions.WebDavException Unexpected error occurred.
         */
        GetItem: function(sName) {
            var oRequest = this.Session.CreateRequest(this.__className + '.GetItem()');
            var sHref = ITHit.WebDAV.Client.HierarchyItem.AppendToUri(this.Href, sName);
            var oItem = ITHit.WebDAV.Client.HierarchyItem.OpenItem(oRequest, sHref);

            oRequest.MarkFinish();
            return oItem;
        },

        /**
         * Callback function to be called when item loaded from server.
         * @callback ITHit.WebDAV.Client.Folder~GetItemAsyncCallback
         * @param {ITHit.WebDAV.Client.AsyncResult} oResult Result object
         * @param {ITHit.WebDAV.Client.HierarchyItem} oResult.Result Item object corresponding to requested path.
         */

        /**
         * Gets the specified item from server.
         * @api
         * @param {string} sName Name of the folder.
         * @param {ITHit.WebDAV.Client.Folder~GetItemAsyncCallback} fCallback Function to call when operation is completed.
         * @returns {ITHit.WebDAV.Client.Request} Request object.
         */
        GetItemAsync: function(sName, fCallback) {
            var oRequest = this.Session.CreateRequest(this.__className + '.GetItemAsync()');
            var sHref = ITHit.WebDAV.Client.HierarchyItem.AppendToUri(this.Href, sName);
            ITHit.WebDAV.Client.HierarchyItem.OpenItemAsync(oRequest, sHref, null, function(oAsyncResult) {

                oRequest.MarkFinish();
                fCallback(oAsyncResult);
            });

            return oRequest;
        },

        /**
         * Checks whether specified item exists in the folder.
         * @api
         * @deprecated Use asynchronous method instead
         * @param {string} sName Name of the folder.
         * @returns {boolean} Returns true, if specified item exists; false, otherwise.
         */
        ItemExists: function(sName) {

            var oRequest = this.Session.CreateRequest(this.__className + '.ItemExists()', 2);

            try {
                // Try to make HEAD request.
                var oResult = ITHit.WebDAV.Client.Methods.Head.Go(
                    oRequest,
                    ITHit.WebDAV.Client.HierarchyItem.AppendToUri(this.Href, sName),
                    this.Host
                );
            } catch (oError) {

                // If method is not allowed exception is raised.
                if (oError instanceof ITHit.WebDAV.Client.Exceptions.MethodNotAllowedException) {

                    try {
                        // Try to make PROPFIND request.
                        ITHit.WebDAV.Client.Methods.Propfind.Go(
                            oRequest,
                            ITHit.WebDAV.Client.HierarchyItem.AppendToUri(this.Href, sName),
                            ITHit.WebDAV.Client.Methods.Propfind.PropfindMode.SelectedProperties,
                            [
                                ITHit.WebDAV.Client.DavConstants.DisplayName
                            ],
                            ITHit.WebDAV.Client.Depth.Zero,
                            this.Host
                        );
                    } catch (oSubError) {
                        // Whether file not found.
                        if (oSubError instanceof ITHit.WebDAV.Client.Exceptions.NotFoundException) {
                            oRequest.MarkFinish();
                            return false;
                        }

                        // Rethrow exception for all other cases.
                        throw oSubError;
                    }

                    // Item is found.
                    oRequest.MarkFinish();
                    return true;
                }

                // Rethrow exception.
                throw oError;
            }

            oRequest.MarkFinish();
            return oResult.IsOK;
        },

        /**
         * Callback function to be called when check request is complete.
         * @callback ITHit.WebDAV.Client.Folder~ItemExistsAsyncCallback
         * @param {ITHit.WebDAV.Client.AsyncResult} oResult Result object
         * @param {boolean} oResult.Result Returns true, if specified item exists; false, otherwise.
         */

        /**
         * Checks whether specified item exists in the folder.
         * @api
         * @examplecode ITHit.WebDAV.Client.Tests.HierarchyItems.ItemExists.ItemExists
         * @param {string} sName Name of the folder.
         * @param {ITHit.WebDAV.Client.Folder~ItemExistsAsyncCallback} fCallback Function to call when operation is completed.
         * @returns {ITHit.WebDAV.Client.Request} Request object.
         */
        ItemExistsAsync: function(sName, fCallback) {

            var oRequest = this.Session.CreateRequest(this.__className + '.ItemExistsAsync()', 2);

            // Try to make HEAD request.
            var that = this;
            ITHit.WebDAV.Client.Methods.Head.GoAsync(
                oRequest,
                ITHit.WebDAV.Client.HierarchyItem.AppendToUri(this.Href, sName),
                this.Host,
                function(oAsyncResult) {
                    if (oAsyncResult.Error instanceof ITHit.WebDAV.Client.Exceptions.MethodNotAllowedException) {

                        // Try to make PROPFIND request.
                        ITHit.WebDAV.Client.Methods.Propfind.GoAsync(
                            oRequest,
                            ITHit.WebDAV.Client.HierarchyItem.AppendToUri(that.Href, sName),
                            ITHit.WebDAV.Client.Methods.Propfind.PropfindMode.SelectedProperties,
                            [
                                ITHit.WebDAV.Client.DavConstants.DisplayName
                            ],
                            ITHit.WebDAV.Client.Depth.Zero,
                            that.Host,
                            function(oSubAsyncResult) {
                                oSubAsyncResult.Result = oSubAsyncResult.IsSuccess;

                                if (oSubAsyncResult.Error instanceof ITHit.WebDAV.Client.Exceptions.NotFoundException) {
                                    oSubAsyncResult.IsSuccess = true;
                                    oSubAsyncResult.Result = false;
                                }

                                oRequest.MarkFinish();
                                fCallback(oSubAsyncResult);
                            }
                        );
                        return;
                    }

                    oAsyncResult.Result = oAsyncResult.Result.IsOK;

                    oRequest.MarkFinish();
                    fCallback(oAsyncResult);
                }
            );

            return oRequest;
        },

        /**
         * Search folder items by query object.
         * @deprecated Use asynchronous method instead
         * @param {ITHit.WebDAV.Client.SearchQuery} oSearchQuery Object with search query conditions
         * @returns {ITHit.WebDAV.Client.HierarchyItem[]}
         */
        SearchByQuery: function(oSearchQuery) {

            var oRequest = this.Session.CreateRequest(this.__className + '.SearchByQuery()');

            var aCustomProperties = ITHit.WebDAV.Client.HierarchyItem.GetCustomRequestProperties(oSearchQuery.SelectProperties);
            oSearchQuery.SelectProperties = aCustomProperties.concat(ITHit.WebDAV.Client.HierarchyItem.GetRequestProperties());

            var oResult = ITHit.WebDAV.Client.Methods.Search.Go(
                oRequest,
                this.Href,
                this.Host,
                oSearchQuery
            );

            var aItems = ITHit.WebDAV.Client.HierarchyItem.GetItemsFromMultiResponse(oResult.Response, oRequest, this.Href, aCustomProperties);

            oRequest.MarkFinish();
            return aItems;
        },

        /**
         * Callback function to be called when search is complete and children items loaded from server.
         * @callback ITHit.WebDAV.Client.Folder~SearchByQueryAsyncCallback
         * @param {ITHit.WebDAV.Client.AsyncResult} oResult Result object
         * @param {ITHit.WebDAV.Client.HierarchyItem[]} oResult.Result Array of file objects.
         */

        /**
         * Search folder items by query object.
         * @api
         * @examplecode ITHit.WebDAV.Client.Tests.HierarchyItems.Search.SearchByQuery
         * @param {ITHit.WebDAV.Client.SearchQuery} oSearchQuery Object with search query conditions
         * @param {ITHit.WebDAV.Client.Folder~SearchByQueryAsyncCallback} fCallback Function to call when operation is completed.
         * @returns {ITHit.WebDAV.Client.Request} Request object.
         */
        SearchByQueryAsync: function(oSearchQuery, fCallback) {

            var oRequest = this.Session.CreateRequest(this.__className + '.SearchByQueryAsync()');

            var aCustomProperties = ITHit.WebDAV.Client.HierarchyItem.GetCustomRequestProperties(oSearchQuery.SelectProperties);
            oSearchQuery.SelectProperties = aCustomProperties.concat(ITHit.WebDAV.Client.HierarchyItem.GetRequestProperties());

            var that = this;
            ITHit.WebDAV.Client.Methods.Search.GoAsync(
                oRequest,
                this.Href,
                this.Host,
                oSearchQuery,
                function(oAsyncResult) {
                    if (oAsyncResult.IsSuccess) {
                        oAsyncResult.Result = ITHit.WebDAV.Client.HierarchyItem.GetItemsFromMultiResponse(oAsyncResult.Result.Response, oRequest, that.Href, aCustomProperties);
                    }

                    oRequest.MarkFinish();
                    fCallback(oAsyncResult);
                }
            );

            return oRequest;
        },

        /**
         * Search folder items by string.
         * @deprecated Use asynchronous method instead
         * @param {string} sSearchQuery String of search query
         * @param {ITHit.WebDAV.Client.PropertyName[]} aAdditionalProperties Additional properties requested from server. Default is empty array.
         * @returns {ITHit.WebDAV.Client.HierarchyItem[]}
         */
        Search: function(sSearchQuery, aAdditionalProperties) {
            var oSearchQuery = new ITHit.WebDAV.Client.SearchQuery(sSearchQuery);
            oSearchQuery.SelectProperties = aAdditionalProperties || [];

            return this.SearchByQuery(oSearchQuery);
        },

        /**
         * Callback function to be called when search is complete and children items loaded from server.
         * @callback ITHit.WebDAV.Client.Folder~SearchAsyncCallback
         * @param {ITHit.WebDAV.Client.AsyncResult} oResult Result object
         * @param {ITHit.WebDAV.Client.HierarchyItem[]} oResult.Result Array of file objects.
         */

        /**
         * Search folder items by string.
         * @api
         * @examplecode ITHit.WebDAV.Client.Tests.HierarchyItems.Search.SearchByString
         * @param {string} sSearchQuery String of search query
         * @param {ITHit.WebDAV.Client.PropertyName[]} aAdditionalProperties Additional properties requested from server. Default is empty array.
         * @param {ITHit.WebDAV.Client.Folder~SearchAsyncCallback} fCallback Function to call when operation is completed.
         * @returns {ITHit.WebDAV.Client.Request} Request object.
         */
        SearchAsync: function(sSearchQuery, aAdditionalProperties, fCallback) {
            var oSearchQuery = new ITHit.WebDAV.Client.SearchQuery(sSearchQuery);
            oSearchQuery.SelectProperties = aAdditionalProperties || [];

            return this.SearchByQueryAsync(oSearchQuery, fCallback);
        },

        _GetErrorFromCreateFileResponse: function(oResponse, sNewUri) {
            // Whether folder is not created.
            if (!oResponse.Status.Equals(ITHit.WebDAV.Client.HttpStatus.Created) && !oResponse.Status.Equals(ITHit.WebDAV.Client.HttpStatus.OK)) {
                return new ITHit.WebDAV.Client.Exceptions.WebDavHttpException(ITHit.Phrases.Exceptions.FailedCreateFile, sNewUri, null, oResponse.Status, null);
            }

            return null;
        }

    });

})();

;
(function () {

	/**
	 * @class ITHit.WebDAV.Client.Methods.UpdateToVersion
	 * @extends ITHit.WebDAV.Client.Methods.HttpMethod
	 */
	var self = ITHit.DefineClass('ITHit.WebDAV.Client.Methods.UpdateToVersion', ITHit.WebDAV.Client.Methods.HttpMethod, /** @lends ITHit.WebDAV.Client.Methods.UpdateToVersion.prototype */{

		__static: /** @lends ITHit.WebDAV.Client.Methods.UpdateToVersion */{

			Go: function (oRequest, sHref, sHost, sToVersionHref) {

				// Create request.
				var oWebDavRequest = this.createRequest(oRequest, sHref, sHost, sToVersionHref);

				// Make request.
				var oResponse = oWebDavRequest.GetResponse();

				return this._ProcessResponse(oResponse, sHref);
			},

			GoAsync: function (oRequest, sHref, sHost, sToVersionHref, fCallback) {

				// Create request.
				var oWebDavRequest = this.createRequest(oRequest, sHref, sHost, sToVersionHref);

				// Make request.
				var that = this;
				oWebDavRequest.GetResponse(function (oAsyncResult) {
					if (!oAsyncResult.IsSuccess) {
						fCallback(new ITHit.WebDAV.Client.AsyncResult(null, false, oAsyncResult.Error));
						return;
					}

					var oResult = that._ProcessResponse(oAsyncResult.Result, sHref);
					fCallback(new ITHit.WebDAV.Client.AsyncResult(oResult, true, null));
				});

				return oWebDavRequest;
			},

			_ProcessResponse: function (oResponse, sHref) {

				// Receive data.
				var oResponseData = oResponse.GetResponseStream();
				return new self(new ITHit.WebDAV.Client.Methods.MultiResponse(oResponseData, sHref));
			},

			createRequest: function (oRequest, sHref, sHost, sToVersionHref) {

				var oWebDavRequest = oRequest.CreateWebDavRequest(sHost, sHref);

				oWebDavRequest.Method('UPDATE');
				oWebDavRequest.Headers.Add('Content-Type', 'text/xml; charset="utf-8"');

				// Create XML DOM document.
				var oWriter = new ITHit.XMLDoc();

				// Get namespace for XML elements.
				var sNamespaceUri = ITHit.WebDAV.Client.DavConstants.NamespaceUri;

				// Create elements.
				var oUpdateElement = oWriter.createElementNS(sNamespaceUri, 'update');
				var oVersionElement = oWriter.createElementNS(sNamespaceUri, 'version');
				var oHrefElement = oWriter.createElementNS(sNamespaceUri, 'href');

				// Set value
				oHrefElement.appendChild(oWriter.createTextNode(sToVersionHref));

				// Append created child nodes.
				oVersionElement.appendChild(oHrefElement);
				oUpdateElement.appendChild(oVersionElement);
				oWriter.appendChild(oUpdateElement);

				oWebDavRequest.Body(oWriter);

				// Return request object.
				return oWebDavRequest;
			}

		}
	});

})();

;
(function() {

	/**
	 * Represents a version on a WebDAV server.
	 * @api
	 * @class ITHit.WebDAV.Client.Version
	 * @extends ITHit.WebDAV.Client.File
	 */
	var self = ITHit.DefineClass('ITHit.WebDAV.Client.Version', ITHit.WebDAV.Client.File, /** @lends ITHit.WebDAV.Client.Version.prototype */{

		__static: /** @lends ITHit.WebDAV.Client.Version */{

			GetRequestProperties: function() {
				return [
					ITHit.WebDAV.Client.DavConstants.DisplayName,
					ITHit.WebDAV.Client.DavConstants.CreationDate,
					ITHit.WebDAV.Client.DavConstants.GetContentType,
					ITHit.WebDAV.Client.DavConstants.GetContentLength,
					ITHit.WebDAV.Client.DavConstants.VersionName,
					ITHit.WebDAV.Client.DavConstants.CreatorDisplayName,
					ITHit.WebDAV.Client.DavConstants.Comment
				];
			},

			/**
			 * Get version name from response.
			 * @param {ITHit.WebDAV.Client.WebDavResponse} oResponse Response object.
			 * @returns {string} Version name.
			 */
			GetVersionName: function(oResponse) {
				var oValue = ITHit.WebDAV.Client.HierarchyItem.GetProperty(oResponse, ITHit.WebDAV.Client.DavConstants.VersionName).Value;
				if (oValue.hasChildNodes()) {
					return oValue.firstChild().nodeValue();
				}
				return null;
			},

			/**
			 * Get creator user name from response.
			 * @param {ITHit.WebDAV.Client.WebDavResponse} oResponse Response object.
			 * @returns {string} Creator name.
			 */
			GetCreatorDisplayName: function(oResponse) {
				var oValue = ITHit.WebDAV.Client.HierarchyItem.GetProperty(oResponse, ITHit.WebDAV.Client.DavConstants.CreatorDisplayName).Value;
				if (oValue.hasChildNodes()) {
					return oValue.firstChild().nodeValue();
				}
				return null;
			},

			/**
			 * Get comment string from response.
			 * @param {ITHit.WebDAV.Client.WebDavResponse} oResponse Response object.
			 * @returns {string} Comment string.
			 */
			GetComment: function(oResponse) {
				var oValue = ITHit.WebDAV.Client.HierarchyItem.GetProperty(oResponse, ITHit.WebDAV.Client.DavConstants.Comment).Value;
				if (oValue.hasChildNodes()) {
					return oValue.firstChild().nodeValue();
				}
				return null;
			},

			GetVersionsFromMultiResponse: function(oResponses, oFile) {
				var aVersionList = [];

				for (var i = 0; i < oResponses.length; i++) {
					var oResponse = oResponses[i];

					aVersionList.push(new self(
						oFile.Session,
						oResponse.Href,
						oFile,
						this.GetDisplayName(oResponse),
						this.GetVersionName(oResponse),
						this.GetCreatorDisplayName(oResponse),
						this.GetComment(oResponse),
						this.GetCreationDate(oResponse),
						this.GetContentType(oResponse),
						this.GetContentLength(oResponse),
						oFile.Host,
						this.GetPropertiesFromResponse(oResponse)
					));
				}

				// Sort versions by version number (other symbols is skipped)
				aVersionList.sort(function(a, b) {
					var aVersionNumber = parseInt(a.VersionName.replace(/[^0-9]/g, ''));
					var bVersionNumber = parseInt(b.VersionName.replace(/[^0-9]/g, ''));

					if (aVersionNumber === bVersionNumber) {
						return 0;
					}
					return aVersionNumber > bVersionNumber ? 1 : -1;
				});

				return aVersionList;
			},

			ParseSetOfHrefs: function(aProperties) {
				var aUrls = [];

				for (var i = 0, l = aProperties.length; i < l; i++) {
					var xml = aProperties[i].Value;
					var aXmlHrefs = xml.getElementsByTagNameNS(ITHit.WebDAV.Client.DavConstants.NamespaceUri, 'href');

					for (var i2 = 0, l2 = aXmlHrefs.length; i2 < l2; i2++) {
						aUrls.push(aXmlHrefs[i2].firstChild().nodeValue());
					}
				}

				return aUrls;
			}

		},

		/**
		 * This property contains a server-defined string that is different for each version.
		 * This string is intended for display for a user.
		 * @api
		 * @type {string}
		 */
		VersionName: null,

		/**
		 *
		 * @type {string}
		 */
		CreatorDisplayName: null,

		/**
		 *
		 * @type {string}
		 */
		Comment: null,

		/**
		 *
		 * @type {ITHit.WebDAV.Client.File}
		 */
		_File: null,

		/**
		 * @type {null}
		 */
		ResumableUpload: null,

		/**
		 * @type {null}
		 */
		LastModified: null,

		/**
		 * @type {null}
		 */
		ActiveLocks: null,

		/**
		 * @type {null}
		 */
		AvailableBytes: null,

		/**
		 * @type {null}
		 */
		UsedBytes: null,

		/**
		 * @type {null}
		 */
		VersionControlled: null,

		/**
		 * @type {null}
		 */
		ResourceType: null,

		/**
		 * @type {null}
		 */
		SupportedLocks: null,

		/**
		 * Create new instance of Version class which represents a version on a WebDAV server.
		 * @param {ITHit.WebDAV.Client.WebDavSession} oSession Current WebDAV session.
		 * @param {string} sHref This item path on the server.
		 * @param {ITHit.WebDAV.Client.File} oFile File instance.
		 * @param {string} sDisplayName User friendly item name.
		 * @param {string} sVersionName User friendly version name.
		 * @param {string} sCreatorDisplayName Creator user name.
		 * @param {string} sComment Comment string.
		 * @param {object} oCreationDate The date item was created.
		 * @param {string} sContentType Content type.
		 * @param {number} iContentLength Content length.
		 * @param {string} sHost
		 * @param {object} oProperties
		 */
		constructor: function(oSession, sHref, oFile, sDisplayName, sVersionName, sCreatorDisplayName, sComment, oCreationDate, sContentType, iContentLength, sHost, oProperties) {
			this._File = oFile;
			this.VersionName = sVersionName;
			this.CreatorDisplayName = sCreatorDisplayName || '';
			this.Comment = sComment || '';

			// Inheritance definition.
			this._super(oSession, sHref, oCreationDate, sVersionName, oCreationDate, sContentType, iContentLength, null, null, sHost, null, null, null, null, oProperties);
		},

		/**
		 * Update file to current version.
		 * @api
		 * @deprecated Use asynchronous method instead
		 * @returns {boolean}
		 */
		UpdateToThis: function() {
			return this._File.UpdateToVersion(this);
		},

		/**
		 * Callback function to be called when version is updated on server.
		 * @callback ITHit.WebDAV.Client.Version~UpdateToThisVersionAsync
		 * @param {ITHit.WebDAV.Client.AsyncResult} oResult Result object
		 */

		/**
		 * Update file to current version.
		 * @examplecode ITHit.WebDAV.Client.Tests.Versions.ManageVersions.UpdateToThis
		 * @api
		 * @param {ITHit.WebDAV.Client.Version~UpdateToThisVersionAsync} fCallback Function to call when operation is completed.
		 * @returns {ITHit.WebDAV.Client.Request} Request object.
		 */
		UpdateToThisAsync: function(fCallback) {
			return this._File.UpdateToVersionAsync(this, fCallback);
		},

		/**
		 * Delete version by self href.
		 * @api
		 * @deprecated Use asynchronous method instead
		 */
		Delete: function() {
			var oRequest = this.Session.CreateRequest(this.__className + '.Delete()');

			// Make request.
			ITHit.WebDAV.Client.Methods.Delete.Go(oRequest, this.Href, null, this.Host);

			oRequest.MarkFinish();
		},

		/**
		 * Callback function to be called when version is deleted on server.
		 * @callback ITHit.WebDAV.Client.Version~DeleteAsyncCallback
		 * @param {ITHit.WebDAV.Client.AsyncResult} oResult Result object
		 */

		/**
		 * Delete version by self href.
		 * @api
		 * @param {ITHit.WebDAV.Client.Version~DeleteAsyncCallback} fCallback Function to call when operation is completed.
		 * @returns {ITHit.WebDAV.Client.Request} Request object.
		 */
		DeleteAsync: function(fCallback) {
			var oRequest = this.Session.CreateRequest(this.__className + '.DeleteAsync()');
			ITHit.WebDAV.Client.Methods.Delete.GoAsync(oRequest, this.Href, null, this.Host, function(oAsyncResult) {

				oRequest.MarkFinish();
				fCallback(oAsyncResult);
			});

			return oRequest;
		},

		/**
		 * Read file content. To download only a part of a file you can specify 2 parameters in ReadContent call.
		 * First parameter is the starting byte (zero-based) at witch to start content download, the second – amount
		 * of bytes to be downloaded. The library will add Range header to the request in this case.
		 * @examplecode ITHit.WebDAV.Client.Tests.Versions.ReadContent.ReadContent
		 * @api
		 * @param {number} iBytesFrom Start position to retrieve lBytesCount number of bytes from.
		 * @param {number} iBytesCount Number of bytes to retrieve.
		 * @param {ITHit.WebDAV.Client.File~ReadContentAsyncCallback} fCallback Function to call when operation is completed.
		 * @returns {ITHit.WebDAV.Client.Request} Request object.
		 */
		ReadContentAsync: function(iBytesFrom, iBytesCount, fCallback) {
			return this._super.apply(this, arguments);
		},

		/**
		 * Writes file content.
		 * @api
		 * @param {string} sContent File content.
		 * @param {string} sLockToken Lock token.
		 * @param {string} sMimeType File mime-type.
		 * @param {ITHit.WebDAV.Client.File~WriteContentAsyncCallback} fCallback Function to call when operation is completed.
		 * @returns {ITHit.WebDAV.Client.Request} Request object.
		 */
		WriteContentAsync: function(sContent, sLockToken, sMimeType, fCallback) {
			return this._super.apply(this, arguments);
		},

		/**
		 * Refreshes item loading data from server.
		 * @api
		 * @param {ITHit.WebDAV.Client.HierarchyItem~RefreshAsyncCallback} fCallback Function to call when operation is completed.
		 * @returns {ITHit.WebDAV.Client.Request} Request object.
		 */
		RefreshAsync: function(fCallback) {
			return this._super.apply(this, arguments);
		},

		/**
		 * @private
		 */
		GetSource: function() {
			throw new ITHit.Exception('The method or operation is not implemented.');
		},

		/**
		 * @private
		 */
		GetSourceAsync: function() {
			throw new ITHit.Exception('The method or operation is not implemented.');
		},

		/**
		 * @private
		 */
		GetSupportedLock: function() {
			throw new ITHit.Exception('The method or operation is not implemented.');
		},

		/**
		 * @private
		 */
		GetSupportedLockAsync: function() {
			throw new ITHit.Exception('The method or operation is not implemented.');
		},

		/**
		 * @private
		 */
		GetParent: function() {
			throw new ITHit.Exception('The method or operation is not implemented.');
		},

		/**
		 * @private
		 */
		GetParentAsync: function() {
			throw new ITHit.Exception('The method or operation is not implemented.');
		},

		/**
		 * @private
		 */
		UpdateProperties: function() {
			throw new ITHit.Exception('The method or operation is not implemented.');
		},

		/**
		 * @private
		 */
		UpdatePropertiesAsync: function() {
			throw new ITHit.Exception('The method or operation is not implemented.');
		},

		/**
		 * @private
		 */
		CopyTo: function() {
			throw new ITHit.Exception('The method or operation is not implemented.');
		},

		/**
		 * @private
		 */
		CopyToAsync: function() {
			throw new ITHit.Exception('The method or operation is not implemented.');
		},

		/**
		 * @private
		 */
		MoveTo: function() {
			throw new ITHit.Exception('The method or operation is not implemented.');
		},

		/**
		 * @private
		 */
		MoveToAsync: function() {
			throw new ITHit.Exception('The method or operation is not implemented.');
		},

		/**
		 * @private
		 */
		Lock: function() {
			throw new ITHit.Exception('The method or operation is not implemented.');
		},

		/**
		 * @private
		 */
		LockAsync: function() {
			throw new ITHit.Exception('The method or operation is not implemented.');
		},

		/**
		 * @private
		 */
		RefreshLock: function() {
			throw new ITHit.Exception('The method or operation is not implemented.');
		},

		/**
		 * @private
		 */
		RefreshLockAsync: function() {
			throw new ITHit.Exception('The method or operation is not implemented.');
		},

		/**
		 * @private
		 */
		Unlock: function() {
			throw new ITHit.Exception('The method or operation is not implemented.');
		},

		/**
		 * @private
		 */
		UnlockAsync: function() {
			throw new ITHit.Exception('The method or operation is not implemented.');
		},

		/**
		 * @private
		 */
		SupportedFeatures: function() {
			throw new ITHit.Exception('The method or operation is not implemented.');
		},

		/**
		 * @private
		 */
		SupportedFeaturesAsync: function() {
			throw new ITHit.Exception('The method or operation is not implemented.');
		},

		/**
		 * @private
		 */
		GetAllProperties: function() {
			throw new ITHit.Exception('The method or operation is not implemented.');
		},

		/**
		 * @private
		 */
		GetAllPropertiesAsync: function() {
			throw new ITHit.Exception('The method or operation is not implemented.');
		},

		/**
		 * @private
		 */
		GetPropertyNames: function() {
			throw new ITHit.Exception('The method or operation is not implemented.');
		},

		/**
		 * @private
		 */
		GetPropertyNamesAsync: function() {
			throw new ITHit.Exception('The method or operation is not implemented.');
		},

		/**
		 * @private
		 */
		GetPropertyValues: function() {
			throw new ITHit.Exception('The method or operation is not implemented.');
		},

		/**
		 * @private
		 */
		GetPropertyValuesAsync: function() {
			throw new ITHit.Exception('The method or operation is not implemented.');
		},

		/**
		 * @private
		 */
		GetVersions: function() {
			throw new ITHit.Exception('The method or operation is not implemented.');
		},

		/**
		 * @private
		 */
		GetVersionsAsync: function() {
			throw new ITHit.Exception('The method or operation is not implemented.');
		},

		/**
		 * @private
		 */
		PutUnderVersionControl: function() {
			throw new ITHit.Exception('The method or operation is not implemented.');
		},

		/**
		 * @private
		 */
		PutUnderVersionControlAsync: function() {
			throw new ITHit.Exception('The method or operation is not implemented.');
		},

		/**
		 * @private
		 */
		UpdateToVersion: function() {
			throw new ITHit.Exception('The method or operation is not implemented.');
		},

		/**
		 * @private
		 */
		UpdateToVersionAsync: function() {
			throw new ITHit.Exception('The method or operation is not implemented.');
		}

	});

})();


/**
 * @class ITHit.WebDAV.Client.Methods.Undelete
 */
ITHit.DefineClass('ITHit.WebDAV.Client.Methods.Undelete', null, /** @lends ITHit.WebDAV.Client.Methods.Undelete.prototype */{

	__static: /** @lends ITHit.WebDAV.Client.Methods.Undelete */{

		Go: function (oRequest, sHref, sHost) {

			// Create request.
			var oWebDavRequest = ITHit.WebDAV.Client.Methods.Undelete.createRequest(oRequest, sHref, sHost);

			// Make request.
			var oResponse = oWebDavRequest.GetResponse();

			return new ITHit.WebDAV.Client.Methods.Report(oResponse);
		},

		createRequest: function (oRequest, sHref, sHost) {

			// Create request.
			var oWebDavRequest = oRequest.CreateWebDavRequest(sHost, sHref);
			oWebDavRequest.Method('UNDELETE');

			// Return request object.
			return oWebDavRequest;
		}

	}
});


/**
 * WebDavResponse class.
 * @class ITHit.WebDAV.Client.WebDavResponse
 */
ITHit.DefineClass('ITHit.WebDAV.Client.WebDavResponse', null, /** @lends ITHit.WebDAV.Client.WebDavResponse.prototype */{

	__static: /** @lends {ITHit.WebDAV.Client.WebDavResponse} */{

		ignoreXmlByMethodAndStatus: {
			'DELETE': {
				200: true
			},
			'COPY': {
				201: true,
				204: true
			},
			'MOVE': {
				201: true,
				204: true
			}
		}

	},

	/**
	 * @type {object}
	 */
	_Response: null,

	/**
	 * @type {string}
	 */
	RequestMethod: null,

	/**
	 * @type {ITHit.WebDAV.Client.HttpStatus}
	 */
	Status: null,

	/**
	 * Create new instance of WebDavResponse class.
	 * @param {object} oResponseData Response object.
	 * @param {string} sRequestMethod Request method.
	 */
	constructor: function(oResponseData, sRequestMethod) {
		this._Response     = oResponseData;
		this.RequestMethod = sRequestMethod;
		this.Status        = new ITHit.WebDAV.Client.HttpStatus(oResponseData.Status, oResponseData.StatusDescription);
	},

	/**
	 * Get response headers.
	 * @returns {object} Response headers.
	 */
	Headers: function() {
		return this._Response.Headers;
	},

	/**
	 * Get response content.
	 * @returns {string} Response XML document.
	 */
	GetResponseStream: function() {

		var oOut = null;
		if (this._Response.BodyXml
			&& !(ITHit.WebDAV.Client.WebDavResponse.ignoreXmlByMethodAndStatus[this.RequestMethod]
			&& ITHit.WebDAV.Client.WebDavResponse.ignoreXmlByMethodAndStatus[this.RequestMethod][this._Response.Status]
			)
		) {
			oOut = new ITHit.XMLDoc(this._Response.BodyXml);
		}

		return oOut;
	}

});



ITHit.DefineClass('ITHit.WebDAV.Client.Methods.ErrorResponse', null, /** @lends ITHit.WebDAV.Client.Methods.ErrorResponse.prototype */{

	ResponseDescription: '',
	Properties: null,

	/**
	 *
	 * @param oXmlDoc
	 * @param sOriginalUri
	 * @constructs
	 */
	constructor: function(oXmlDoc, sOriginalUri) {

		// Declare properties.
		this.Properties          = [];

		var oDescription = new ITHit.WebDAV.Client.PropertyName("responsedescription", ITHit.WebDAV.Client.DavConstants.NamespaceUri);

		// Create namespace resolver.
		var oResolver = new ITHit.XPath.resolver();
		oResolver.add('d', ITHit.WebDAV.Client.DavConstants.NamespaceUri);

		// Select nodes.
		var oRes = ITHit.XPath.evaluate('/d:error/*', oXmlDoc, oResolver);

		// Loop through selected nodes.
		var oNode;
		while (oNode = oRes.iterateNext()) {

			var oProp = new ITHit.WebDAV.Client.Property(oNode.cloneNode());

			if (oDescription.Equals(oProp.Name)) {
				this.ResponseDescription = oProp.StringValue();
				continue;
			}

			this.Properties.push(oProp);
		}
	}

});


/**
 * Incorrect credentials provided or insufficient permissions to access the requested item. Initializes a new instance
 * of the UnauthorizedException class with a specified error message, a reference to the inner exception that is the
 * cause of this exception, href of the item and multistatus response caused the error.
 * @api
 * @class ITHit.WebDAV.Client.Exceptions.UnauthorizedException
 * @extends ITHit.WebDAV.Client.Exceptions.WebDavHttpException
 */
ITHit.DefineClass('ITHit.WebDAV.Client.Exceptions.UnauthorizedException', ITHit.WebDAV.Client.Exceptions.WebDavHttpException, /** @lends ITHit.WebDAV.Client.Exceptions.UnauthorizedException.prototype */{

	/**
	 * Exception name
	 * @type {string}
	 */
	Name: 'UnauthorizedException',

	/**
	 * @param {string} sMessage The error message string.
	 * @param {string} sHref The href of an item caused the current exception.
	 * @param {ITHit.Exception} [oInnerException] The ITHit.Exception instance that caused the current exception.
	 */
	constructor: function(sMessage, sHref, oInnerException) {
		this._super(sMessage, sHref, null, ITHit.WebDAV.Client.HttpStatus.Unauthorized, oInnerException);
	}

});


/**
 * The request could not be understood by the server due to malformed syntax.
 * Initializes a new instance of the BadRequestException class with a specified error message, a reference to the
 * inner exception that is the cause of this exception, href of the item and multistatus response caused the error.
 * @api
 * @class ITHit.WebDAV.Client.Exceptions.BadRequestException
 * @extends ITHit.WebDAV.Client.Exceptions.WebDavHttpException
 */
ITHit.DefineClass('ITHit.WebDAV.Client.Exceptions.BadRequestException', ITHit.WebDAV.Client.Exceptions.WebDavHttpException, /** @lends ITHit.WebDAV.Client.Exceptions.BadRequestException.prototype */{

	/**
	 * Exception name
	 * @type {string}
	 */
	Name: 'BadRequestException',

	/**
	 * @param {string} sMessage The error message string.
	 * @param {string} sHref The href of an item caused the current exception.
	 * @param {ITHit.WebDAV.Client.Multistatus} oMultistatus Multistatus response containing error information.
	 * @param {ITHit.IError} [oErrorInfo] Error response containing additional error information.
	 * @param {ITHit.Exception} [oInnerException] The ITHit.Exception instance that caused the current exception.
	 */
	constructor: function(sMessage, sHref, oMultistatus, oErrorInfo, oInnerException) {
		this._super(sMessage, sHref, oMultistatus, ITHit.WebDAV.Client.HttpStatus.BadRequest, oInnerException, oErrorInfo);
	}

});


/**
 * The request could not be carried because of conflict on server.
 * Initializes a new instance of the ConflictException class with a specified error message, a reference
 * to the inner exception that is the cause of this exception, href of the item and multistatus response
 * caused the error.
 * @api
 * @class ITHit.WebDAV.Client.Exceptions.ConflictException
 * @extends ITHit.WebDAV.Client.Exceptions.WebDavHttpException
 */
ITHit.DefineClass('ITHit.WebDAV.Client.Exceptions.ConflictException', ITHit.WebDAV.Client.Exceptions.WebDavHttpException, /** @lends ITHit.WebDAV.Client.Exceptions.ConflictException.prototype */{

	/**
	 * Exception name
	 * @type {string}
	 */
	Name: 'ConflictException',

	/**
	 * @param {string} sMessage The error message string.
	 * @param {string} sHref The href of an item caused the current exception.
	 * @param {ITHit.WebDAV.Client.Multistatus} oMultistatus Multistatus response containing error information.
	 * @param {ITHit.IError} [oErrorInfo] Error response containing additional error information.
	 * @param {ITHit.Exception} [oInnerException] The ITHit.Exception instance that caused the current exception.
	 */
	constructor: function(sMessage, sHref, oMultistatus, oErrorInfo, oInnerException) {
		this._super(sMessage, sHref, oMultistatus, ITHit.WebDAV.Client.HttpStatus.Conflict, oInnerException, oErrorInfo);
	}

});


/**
 * The item is locked. Initializes a new instance of the LockedException class with a specified error message,
 * a reference to the inner exception that is the cause of this exception, href of the item and multistatus
 * response caused the error.
 * @api
 * @class ITHit.WebDAV.Client.Exceptions.LockedException
 * @extends ITHit.WebDAV.Client.Exceptions.WebDavHttpException
 */
ITHit.DefineClass('ITHit.WebDAV.Client.Exceptions.LockedException', ITHit.WebDAV.Client.Exceptions.WebDavHttpException, /** @lends ITHit.WebDAV.Client.Exceptions.LockedException.prototype */{

	/**
	 * Exception name
	 * @type {string}
	 */
	Name: 'LockedException',

	/**
	 * @param {string} sMessage The error message string.
	 * @param {string} sHref The href of an item caused the current exception.
	 * @param {ITHit.WebDAV.Client.Multistatus} oMultistatus Multistatus response containing error information.
	 * @param {ITHit.IError} [oErrorInfo] Error response containing additional error information.
	 * @param {ITHit.Exception} [oInnerException] The ITHit.Exception instance that caused the current exception.
	 */
	constructor: function(sMessage, sHref, oMultistatus, oErrorInfo, oInnerException) {
		this._super(sMessage, sHref, oMultistatus, ITHit.WebDAV.Client.HttpStatus.Locked, oInnerException, oErrorInfo);
	}

});


/**
 * The server refused to fulfill the request. Initializes a new instance of the ForbiddenException class with
 * a specified error message, a reference to the inner exception that is the cause of this exception, href of
 * the item and multistatus response caused the error.
 * @api
 * @class ITHit.WebDAV.Client.Exceptions.ForbiddenException
 * @extends ITHit.WebDAV.Client.Exceptions.WebDavHttpException
 */
ITHit.DefineClass('ITHit.WebDAV.Client.Exceptions.ForbiddenException', ITHit.WebDAV.Client.Exceptions.WebDavHttpException, /** @lends ITHit.WebDAV.Client.Exceptions.ForbiddenException.prototype */{

	/**
	 * Exception name
	 * @type {string}
	 */
	Name: 'ForbiddenException',

	/**
	 * @param {string} sMessage The error message string.
	 * @param {string} sHref The href of an item caused the current exception.
	 * @param {ITHit.WebDAV.Client.Multistatus} oMultistatus Multistatus response containing error information.
	 * @param {ITHit.Exception} [oInnerException] The ITHit.Exception instance that caused the current exception.
	 * @param {ITHit.IError} [oErrorInfo] Error response containing additional error information.
	 * @param {ITHit.Exception} [oInnerException] The ITHit.Exception instance that caused the current exception.
	 */
	constructor: function(sMessage, sHref, oMultistatus, oErrorInfo, oInnerException) {
		this._super(sMessage, sHref, oMultistatus, ITHit.WebDAV.Client.HttpStatus.Forbidden, oInnerException, oErrorInfo);
	}

});


/**
 * The method is not allowed. Initializes a new instance of the MethodNotAllowedException class with a specified
 * error message, a reference to the inner exception that is the cause of this exception, href of the item and
 * multistatus response caused the error.
 * @api
 * @class ITHit.WebDAV.Client.Exceptions.MethodNotAllowedException
 * @extends ITHit.WebDAV.Client.Exceptions.WebDavHttpException
 */
ITHit.DefineClass('ITHit.WebDAV.Client.Exceptions.MethodNotAllowedException', ITHit.WebDAV.Client.Exceptions.WebDavHttpException, /** @lends ITHit.WebDAV.Client.Exceptions.MethodNotAllowedException.prototype */{

	/**
	 * Exception name
	 * @type {string}
	 */
	Name: 'MethodNotAllowedException',

	/**
	 * @param {string} sMessage The error message string.
	 * @param {string} sHref The href of an item caused the current exception.
	 * @param {ITHit.WebDAV.Client.Multistatus} oMultistatus Multistatus response containing error information.
	 * @param {ITHit.IError} [oErrorInfo] Error response containing additional error information.
	 * @param {ITHit.Exception} [oInnerException] The ITHit.Exception instance that caused the current exception.
	 */
	constructor: function(sMessage, sHref, oMultistatus, oErrorInfo, oInnerException) {
		this._super(sMessage, sHref, oMultistatus, ITHit.WebDAV.Client.HttpStatus.MethodNotAllowed, oInnerException, oErrorInfo);
	}

});


/**
 * The method is not implemented. Initializes a new instance of the NotImplementedException class with a specified
 * error message, a reference to the inner exception that is the cause of this exception, href of the item and
 * multistatus response caused the error.
 * @api
 * @class ITHit.WebDAV.Client.Exceptions.NotImplementedException
 * @extends ITHit.WebDAV.Client.Exceptions.WebDavHttpException
 */
ITHit.DefineClass('ITHit.WebDAV.Client.Exceptions.NotImplementedException', ITHit.WebDAV.Client.Exceptions.WebDavHttpException, /** @lends ITHit.WebDAV.Client.Exceptions.NotImplementedException.prototype */{

	/**
	 * Exception name
	 * @type {string}
	 */
	Name: 'NotImplementedException',

	/**
	 * @param {string} sMessage The error message string.
	 * @param {string} sHref The href of an item caused the current exception.
	 * @param {ITHit.WebDAV.Client.Multistatus} oMultistatus Multistatus response containing error information.
	 * @param {ITHit.IError} [oErrorInfo] Error response containing additional error information.
	 * @param {ITHit.Exception} [oInnerException] The ITHit.Exception instance that caused the current exception.
	 */
	constructor: function(sMessage, sHref, oMultistatus, oErrorInfo, oInnerException) {
		this._super(sMessage, sHref, oMultistatus, ITHit.WebDAV.Client.HttpStatus.NotImplemented, oInnerException, oErrorInfo);
	}

});


/**
 * The item doesn't exist on the server. Initializes a new instance of the NotFoundException class with a specified
 * error message, a reference to the inner exception that is the cause of this exception, href of the item and
 * multistatus response caused the error.
 * @api
 * @class ITHit.WebDAV.Client.Exceptions.NotFoundException
 * @extends ITHit.WebDAV.Client.Exceptions.WebDavHttpException
 */
ITHit.DefineClass('ITHit.WebDAV.Client.Exceptions.NotFoundException', ITHit.WebDAV.Client.Exceptions.WebDavHttpException, /** @lends ITHit.WebDAV.Client.Exceptions.NotFoundException.prototype */{

	/**
	 * Exception name
	 * @type {string}
	 */
	Name: 'NotFoundException',

	/**
	 * @param {string} sMessage The error message string.
	 * @param {string} sHref The href of an item caused the current exception.
	 * @param {ITHit.Exception} [oInnerException] The ITHit.Exception instance that caused the current exception.
	 */
	constructor: function(sMessage, sHref, oInnerException) {
		this._super(sMessage, sHref, null, ITHit.WebDAV.Client.HttpStatus.NotFound, oInnerException);
	}

});


/**
 * Precondition failed. Initializes a new instance of the PreconditionFailedException class with a specified error
 * message, a reference to the inner exception that is the cause of this exception, href of the item and multistatus
 * response with error details.
 * @api
 * @class ITHit.WebDAV.Client.Exceptions.PreconditionFailedException
 * @extends ITHit.WebDAV.Client.Exceptions.WebDavHttpException
 */
ITHit.DefineClass('ITHit.WebDAV.Client.Exceptions.PreconditionFailedException', ITHit.WebDAV.Client.Exceptions.WebDavHttpException, /** @lends ITHit.WebDAV.Client.Exceptions.PreconditionFailedException.prototype */{

	/**
	 * Exception name
	 * @type {string}
	 */
	Name: 'PreconditionFailedException',

	/**
	 * @param {string} sMessage The error message string.
	 * @param {string} sHref The href of an item caused the current exception.
	 * @param {ITHit.WebDAV.Client.Multistatus} oMultistatus Multistatus response containing error information.
	 * @param {ITHit.IError} [oErrorInfo] Error response containing additional error information.
	 * @param {ITHit.Exception} [oInnerException] The ITHit.Exception instance that caused the current exception.
	 */
	constructor: function(sMessage, sHref, oMultistatus, oErrorInfo, oInnerException) {
		this._super(sMessage, sHref, oMultistatus, ITHit.WebDAV.Client.HttpStatus.PreconditionFailed, oInnerException, oErrorInfo);
	}

});


/**
 * The method could not be performed on the resource because the requested action depended on another action
 * and that action failed. Initializes a new instance of the DependencyFailedException class with a specified
 * error message, a reference to the inner exception that is the cause of this exception, href of the item
 * and multistatus response caused the error.
 * @api
 * @class ITHit.WebDAV.Client.Exceptions.DependencyFailedException
 * @extends ITHit.WebDAV.Client.Exceptions.WebDavHttpException
 */
ITHit.DefineClass('ITHit.WebDAV.Client.Exceptions.DependencyFailedException', ITHit.WebDAV.Client.Exceptions.WebDavHttpException, /** @lends ITHit.WebDAV.Client.Exceptions.DependencyFailedException.prototype */{

	/**
	 * Exception name
	 * @type {string}
	 */
	Name: 'DependencyFailedException',

	/**
	 * @param {string} sMessage The error message string.
	 * @param {string} sHref The href of an item caused the current exception.
	 * @param {ITHit.WebDAV.Client.Multistatus} oMultistatus Multistatus response containing error information.
	 * @param {ITHit.IError} [oErrorInfo] Error response containing additional error information.
	 * @param {ITHit.Exception} [oInnerException] The ITHit.Exception instance that caused the current exception.
	 */
	constructor: function(sMessage, sHref, oMultistatus, oErrorInfo, oInnerException) {
		this._super(sMessage, sHref, oMultistatus, ITHit.WebDAV.Client.HttpStatus.DependencyFailed, oInnerException, oErrorInfo);
	}

});


/**
 * Insufficient storage exception. Initializes a new instance of the InsufficientStorageException class with
 * a specified error message, a reference to the inner exception that is the cause of this exception, href of
 * the item and error response caused the error.
 * @api
 * @class ITHit.WebDAV.Client.Exceptions.InsufficientStorageException
 * @extends ITHit.WebDAV.Client.Exceptions.WebDavHttpException
 */
ITHit.DefineClass('ITHit.WebDAV.Client.Exceptions.InsufficientStorageException', ITHit.WebDAV.Client.Exceptions.WebDavHttpException, /** @lends ITHit.WebDAV.Client.Exceptions.InsufficientStorageException.prototype */{

	/**
	 * Exception name
	 * @type {string}
	 */
	Name: 'InsufficientStorageException',

	/**
	 * @param {string} sMessage The error message string.
	 * @param {string} sHref The href of an item caused the current exception.
	 * @param {ITHit.WebDAV.Client.Multistatus} oMultistatus Multistatus response containing error information.
	 * @param {ITHit.IError} [oErrorInfo] Error response containing additional error information.
	 * @param {ITHit.Exception} [oInnerException] The ITHit.Exception instance that caused the current exception.
	 */
	constructor: function(sMessage, sHref, oMultistatus, oErrorInfo, oInnerException) {
		this._super(sMessage, sHref, oMultistatus, ITHit.WebDAV.Client.HttpStatus.InsufficientStorage, oInnerException, oErrorInfo);
	}

});


/**
 * Quota not exceeded exception. Initializes a new instance of the QuotaNotExceededException class with a
 * specified error message, a reference to the inner exception that is the cause of this exception, href of
 * the item and error response caused the error.
 * @api
 * @class ITHit.WebDAV.Client.Exceptions.QuotaNotExceededException
 * @extends ITHit.WebDAV.Client.Exceptions.InsufficientStorageException
 */
ITHit.DefineClass('ITHit.WebDAV.Client.Exceptions.QuotaNotExceededException', ITHit.WebDAV.Client.Exceptions.InsufficientStorageException, /** @lends ITHit.WebDAV.Client.Exceptions.QuotaNotExceededException.prototype */{

	/**
	 * Exception name
	 * @type {string}
	 */
	Name: 'QuotaNotExceededException',

	/**
	 * @param {string} sMessage The error message string.
	 * @param {string} sHref The href of an item caused the current exception.
	 * @param {ITHit.WebDAV.Client.Multistatus} oMultistatus Multistatus response containing error information.
	 * @param {ITHit.Exception} [oInnerException] The ITHit.Exception instance that caused the current exception.
	 * @param {ITHit.IError} [oError] Error response containing additional error information.
	 */
	constructor: function(sMessage, sHref, oMultistatus, oInnerException, oError) {
		this._super(sMessage, sHref, oMultistatus, ITHit.WebDAV.Client.HttpStatus.InsufficientStorage, oInnerException, oError);
	}

});


/**
 * Sufficient disk space exception. Initializes a new instance of the SufficientDiskSpaceException class with
 * a specified error message, a reference to the inner exception that is the cause of this exception, href of
 * the item and error response caused the error.
 * @api
 * @class ITHit.WebDAV.Client.Exceptions.SufficientDiskSpaceException
 * @extends ITHit.WebDAV.Client.Exceptions.InsufficientStorageException
 */
ITHit.DefineClass('ITHit.WebDAV.Client.Exceptions.SufficientDiskSpaceException', ITHit.WebDAV.Client.Exceptions.InsufficientStorageException, /** @lends ITHit.WebDAV.Client.Exceptions.SufficientDiskSpaceException.prototype */{

	/**
	 * Exception name
	 * @type {string}
	 */
	Name: 'SufficientDiskSpaceException',

	/**
	 * @param {string} sMessage The error message string.
	 * @param {string} sHref The href of an item caused the current exception.
	 * @param {ITHit.WebDAV.Client.Multistatus} oMultistatus Multistatus response containing error information.
	 * @param {ITHit.Exception} [oInnerException] The ITHit.Exception instance that caused the current exception.
	 * @param {ITHit.IError} [oError] Error response containing additional error information.
	 */
	constructor: function(sMessage, sHref, oMultistatus, oInnerException, oError) {
		this._super(sMessage, sHref, oMultistatus, ITHit.WebDAV.Client.HttpStatus.InsufficientStorage, oInnerException, oError);
	}

});


ITHit.DefineClass('ITHit.WebDAV.Client.Exceptions.Parsers.InsufficientStorage', null, /** @lends ITHit.WebDAV.Client.Exceptions.Parsers.InsufficientStorage.prototype */{

	/**
	 * @constructs
	 * @param sMessage
	 * @param sHref
	 * @param oMultistatus
	 * @param oError
	 * @param oInnerException
	 */
	constructor: function(sMessage, sHref, oMultistatus, oError, oInnerException) {

		var sExceptionClass = 'InsufficientStorageException';

		if (1 == oError.Properties.length) {
			var oPropName = oError.Properties[0].Name;

			if (oPropName.Equals(ITHit.WebDAV.Client.DavConstants.QuotaNotExceeded)) {
				sExceptionClass = 'QuotaNotExceededException';
			} else if (oPropName.Equals(ITHit.WebDAV.Client.DavConstants.SufficientDiskSpace)) {
				sExceptionClass = 'SufficientDiskSpaceException';
			}
		}

		return new ITHit.WebDAV.Client.Exceptions[sExceptionClass]((oError.Description || sMessage), sHref, oMultistatus, oInnerException, oError);
	}

});


/**
 * Represents information about errors occurred in different elements.
 * @api
 * @class ITHit.WebDAV.Client.Error
 */
ITHit.DefineClass('ITHit.WebDAV.Client.Error', null, /** @lends ITHit.WebDAV.Client.Error.prototype */{

    /**
     * Gets the generic description, if available.
     * @api
     * @type {string}
     */
    Description: null,

    /**
     * Array of the errors returned by server.
     * @api
     * @type {ITHit.WebDAV.Client.MultistatusResponse[]}
     */
    Responses: null

});

ITHit.DefineClass('ITHit.WebDAV.Client.Exceptions.Info.Error', ITHit.WebDAV.Client.Error, /** @lends ITHit.WebDAV.Client.Exceptions.Info.Error.prototype */{

	/**
	 * Gets the generic description, if available.
	 * @type {string}
	 */
	Description: '',

	/**
	 * Array of elements returned by server.
	 * @type {ITHit.WebDAV.Client.Property[]}
	 */
	Properties: null,

	/**
	 * Inline text, returned by server
	 * @type {string}
	 */
	BodyText: '',

	/**
	 * Represents information about errors occurred in different elements.
	 * @constructs
	 * @extends ITHit.WebDAV.Client.Error
	 */
	constructor: function(oErrorResponse) {
		this.Properties = [];

		this._super();

		// Whether error response object passed.
		if (oErrorResponse) {
			this.Description = oErrorResponse.ResponseDescription;
			this.Properties  = oErrorResponse.Properties;
		}
	}

});


ITHit.Phrases.LoadJSON(ITHit.Temp.WebDAV_Phrases);

;
(function() {

	/*
	 * Request header collection.
	 */
	var Headers = function(oHeaders) {
		this.Headers = oHeaders;
	};

	/*
	 * Save header for request.
	 * @function ITHit.WebDAV.Client.WebDavRequest.Add
	 *
	 * @param {string} sHeader Header's name.
	 * @param {string} sValue Header's value.
	 */
	Headers.prototype.Add = function(sHeader, sValue) {
		this.Headers[sHeader] = sValue;
	};

	/*
	 * Get headers dictionary as reference.
	 * @function {object} ITHit.WebDAV.Client.WebDavRequest.GetAll
	 *
	 * @returns Headers list.
	 */
	Headers.prototype.GetAll = function() {
		return this.Headers;
	};

	/**
	 * This class represents asynchronous request to WebDAV server. The instance of this class is returned from most
	 * asynchronous methods of the library. You can use it to cancel the request calling Abort() method of this class
	 * as well as to show progress attaching to Progress event.
	 * @class ITHit.WebDAV.Client.WebDavRequest
	 */
	var self = ITHit.DefineClass('ITHit.WebDAV.Client.WebDavRequest', null, /** @lends ITHit.WebDAV.Client.WebDavRequest.prototype */{

		__static: /** @lends ITHit.WebDAV.Client.WebDavRequest */{

			_IdCounter: 0,

			/**
			 * Create request.
			 * @param {string} sUri Page URI.
			 * @param {(string|Array)} mLockTokens Lock tokens for item.
			 * @param mLockTokens
			 * @param {string} [sUser]
			 * @param {string} [sPass]
			 * @param {string} [sHost]
			 * @returns {ITHit.WebDAV.Client.WebDavRequest} Request object.
			 */
			Create: function(sUri, mLockTokens, sUser, sPass, sHost) {

				// Whether host is not set than add it.
				if (/^\//.test(sUri)) {
					sUri = sHost + sUri.substr(1);
				}

				// Create WebDavRequest object for specified URI.
				var oWebDavRequest = new self(sUri, sUser, sPass);

				// If mLockTokens is string.
				if ('string' == typeof mLockTokens) {
					if (mLockTokens) {

						// Add If header.
						oWebDavRequest.Headers.Add('If', '(<'+ ITHit.WebDAV.Client.DavConstants.OpaqueLockToken + mLockTokens +'>)');
					}

					// Else if mLockTokens is an array type.
				} else if ( (mLockTokens instanceof Array) && mLockTokens.length) {

					var sLockTokensHeader = '';
					var bFirst = true;

					for (var i = 0; i < mLockTokens.length; i++) {

						// Check whether LockToken element is not null.
						ITHit.WebDAV.Client.WebDavUtil.VerifyArgumentNotNull(mLockTokens[i], "lockToken");

						// Append LockToken to header collection.
						sLockTokensHeader += (bFirst ? '' : ' ') + '(<'+ ITHit.WebDAV.Client.DavConstants.OpaqueLockToken + mLockTokens[i].LockToken +'>)';

						bFirst = false;
					}

					// Add If header.
					oWebDavRequest.Headers.Add("If", sLockTokensHeader);
				}

				return oWebDavRequest;
			},

			ProcessWebException: function(oResponseData) {

				// Get references for namespaces.
				var oResponse     = null;
				var sResponseData = '';

				if (oResponseData.BodyXml && oResponseData.BodyXml.childNodes.length) {
					oResponse     = new ITHit.XMLDoc(oResponseData.BodyXml);
					sResponseData = String(oResponse);
				}

				// Try parse multistatus response.
				var oInfo         = null,
					oErrorInfo    = null;

				if (oResponse) {
					var oErrorResponse = new ITHit.WebDAV.Client.Methods.ErrorResponse(oResponse, oResponseData.Href);
					oErrorInfo         = new ITHit.WebDAV.Client.Exceptions.Info.Error(oErrorResponse);

					var oMultiResponse = new ITHit.WebDAV.Client.Methods.MultiResponse(oResponse, oResponseData.Href);
					oInfo              = new ITHit.WebDAV.Client.Exceptions.Info.Multistatus(oMultiResponse);
				} else {
					oErrorInfo         = new ITHit.WebDAV.Client.Exceptions.Info.Error();
					oErrorInfo.BodyText = oResponseData.BodyText;
				}

				// Throw apropriate exception
				var oException = null,
					oExceptionToThrow;

				switch (oResponseData.Status) {

					case ITHit.WebDAV.Client.HttpStatus.Unauthorized.Code:
						oExceptionToThrow = new ITHit.WebDAV.Client.Exceptions.UnauthorizedException(ITHit.Phrases.Exceptions.Unauthorized, oResponseData.Href, oException);
						break;

					case ITHit.WebDAV.Client.HttpStatus.Conflict.Code:
						oExceptionToThrow = new ITHit.WebDAV.Client.Exceptions.ConflictException(ITHit.Phrases.Exceptions.Conflict, oResponseData.Href, oInfo, oErrorInfo, oException);
						break;

					case ITHit.WebDAV.Client.HttpStatus.Locked.Code:
						oExceptionToThrow = new ITHit.WebDAV.Client.Exceptions.LockedException(ITHit.Phrases.Exceptions.Locked, oResponseData.Href, oInfo, oErrorInfo, oException);
						break;

					case ITHit.WebDAV.Client.HttpStatus.BadRequest.Code:
						oExceptionToThrow = new ITHit.WebDAV.Client.Exceptions.BadRequestException(ITHit.Phrases.Exceptions.BadRequest, oResponseData.Href, oInfo, oErrorInfo, oException);
						break;

					case ITHit.WebDAV.Client.HttpStatus.Forbidden.Code:
						oExceptionToThrow = new ITHit.WebDAV.Client.Exceptions.ForbiddenException(ITHit.Phrases.Exceptions.Forbidden, oResponseData.Href, oInfo, oErrorInfo, oException);
						break;

					case ITHit.WebDAV.Client.HttpStatus.MethodNotAllowed.Code:
						oExceptionToThrow = new ITHit.WebDAV.Client.Exceptions.MethodNotAllowedException(ITHit.Phrases.Exceptions.MethodNotAllowed, oResponseData.Href, oInfo, oErrorInfo, oException);
						break;

					case ITHit.WebDAV.Client.HttpStatus.NotImplemented.Code:
						oExceptionToThrow = new ITHit.WebDAV.Client.Exceptions.NotImplementedException(ITHit.Phrases.Exceptions.MethodNotAllowed, oResponseData.Href, oInfo, oErrorInfo, oException);
						break;

					case ITHit.WebDAV.Client.HttpStatus.NotFound.Code:
						oExceptionToThrow = new ITHit.WebDAV.Client.Exceptions.NotFoundException(ITHit.Phrases.Exceptions.NotFound, oResponseData.Href, oException);
						break;

					case ITHit.WebDAV.Client.HttpStatus.PreconditionFailed.Code:
						oExceptionToThrow = new ITHit.WebDAV.Client.Exceptions.PreconditionFailedException(ITHit.Phrases.Exceptions.PreconditionFailed, oResponseData.Href, oInfo, oErrorInfo, oException);
						break;

					case ITHit.WebDAV.Client.HttpStatus.DependencyFailed.Code:
						oExceptionToThrow = new ITHit.WebDAV.Client.Exceptions.DependencyFailedException(ITHit.Phrases.Exceptions.DependencyFailed, oResponseData.Href, oInfo, oErrorInfo, oException);
						break;

					case ITHit.WebDAV.Client.HttpStatus.InsufficientStorage.Code:
						oExceptionToThrow = ITHit.WebDAV.Client.Exceptions.Parsers.InsufficientStorage(ITHit.Phrases.Exceptions.InsufficientStorage, oResponseData.Href, oInfo, oErrorInfo, oException);
						break;

					default:
						if (sResponseData) {
							sResponseData = '\n'+ ITHit.Phrases.ServerReturned +'\n----\n'+ sResponseData +'\n----\n';
						}

						oExceptionToThrow = new ITHit.WebDAV.Client.Exceptions.WebDavHttpException(
							ITHit.Phrases.Exceptions.Http + sResponseData,
							oResponseData.Href,
							oInfo,
							new ITHit.WebDAV.Client.HttpStatus(oResponseData.Status, oResponseData.StatusDescription),
							oException,
							oErrorInfo
						);
						break;
				}

				return oExceptionToThrow;
			}

		},

		_Href: null,
		_Method: 'GET',
		_Headers: null,
		_Body: '',
		_User: null,
		_Password: null,

		Id: null,
		Headers: null,
		PreventCaching: null,
		ProgressInfo: null,

		/**
		 * Custom handler for get progress event
		 */
		OnProgress: null,
		_XMLRequest: null,

		/**
		 * Create instance of WebDavRequest class.
		 * @param {string} sUri URI
		 * @param {string} [sUser]
		 * @param {string} [sPass]
		 */
		constructor: function(sUri, sUser, sPass) {
			this._Href     = sUri;
			this._Headers  = {};
			this._User     = sUser || null;
			this._Password = sPass || null;

			this.Id = self._IdCounter++;
			this.Headers   = new Headers(this._Headers);
		},

		/**
		 * Get or set method for request
		 * @param {string} sValue Method's value.
		 * @returns {string} Method's value.
		 */
		Method: function(sValue) {
			if (undefined !== sValue) {
				this._Method = sValue;
			}

			return this._Method;
		},

		Body: function(mBody) {
			if (undefined !== mBody) {
				this._Body = mBody;
			}

			return this._Body;
		},

		/**
		 * Abort request. Method called native XMLRequest.Abort method.
		 */
		Abort: function() {
			if (this._XMLRequest !== null) {
				this._XMLRequest.Abort();
			}
		},

		/**
		 * Make XMLHttpRequest and get response.
		 * @returns {ITHit.WebDAV.Client.WebDavResponse|null} Response object.
		 */
		GetResponse: function(fCallback) {

			// Send async request
			var bAsync = typeof fCallback === 'function';

			var sHref = this._Href;

			// Add nocache attribute for force disabled cache
			if ((ITHit.Config.PreventCaching && this.PreventCaching === null) || this.PreventCaching === true ) {
				var sAndSymbol = sHref.indexOf('?') !== -1 ? '&' : '?';
				var sNoCacheParam = sAndSymbol + 'nocache=' + new Date().getTime();

				if (sHref.indexOf('#') !== -1) {
					sHref.replace(/#/g, sNoCacheParam + '#');
				} else {
					sHref += sNoCacheParam;
				}
			}

			// Fix for XMLHttpRequest.
			// TODO: Remove fix when encoding special characters on server will be fixed.
			sHref = sHref.replace(/#/g, '%23');

			var oRequestData = new ITHit.HttpRequest(
				sHref,
				this._Method,
				this._Headers,
				String(this._Body)
			);

			var oResponseData = ITHit.Events.DispatchEvent(this, 'OnBeforeRequestSend', oRequestData);

			if (!oResponseData || !(oResponseData instanceof ITHit.HttpResponse)) {

				// Set default user and password if specified.
				oRequestData.User     = (null === oRequestData.User) ? this._User : oRequestData.User;
				oRequestData.Password = (null === oRequestData.Password) ? this._Password : oRequestData.Password;
				oRequestData.Body = String(oRequestData.Body) || '';

				// Send request.
				this._XMLRequest = new ITHit.XMLRequest(oRequestData, bAsync);
			}

			if (bAsync) {
				if (this._XMLRequest !== null) {
					// Call callback after receiving the response
					var that = this;
					this._XMLRequest.OnData = function (oResponseData) {
						var oWebDavResponse = null;
						var bSuccess = true;
						var oError = null;
						try {
							oWebDavResponse = that._onGetResponse(oRequestData, oResponseData);
							bSuccess = true;
						}
						catch (e) {
							oError = e;
							bSuccess = false;
						}

						var oAsyncResult = new ITHit.WebDAV.Client.AsyncResult(oWebDavResponse, bSuccess, oError);
						ITHit.Events.DispatchEvent(that, 'OnFinish', [oAsyncResult, that.Id]);
						fCallback.call(this, oAsyncResult);
					};
					this._XMLRequest.OnError = function (oException) {
						var oWebDavHttpException = new ITHit.WebDAV.Client.Exceptions.WebDavHttpException(oException.message, sHref, null, null, oException);
						var oAsyncResult = new ITHit.WebDAV.Client.AsyncResult(null, false, oWebDavHttpException);
						ITHit.Events.DispatchEvent(that, 'OnFinish', [oAsyncResult, that.Id]);
						fCallback.call(this, oAsyncResult);
					};
					this._XMLRequest.OnProgress = function (oEvent) {
						// IE8 has not arguments in onprogress event
						if (!oEvent) {
							return;
						}

						that.ProgressInfo = oEvent;
						ITHit.Events.DispatchEvent(that, 'OnProgress', [oEvent, that.Id]);

						if (typeof that.OnProgress === 'function') {
							that.OnProgress(oEvent);
						}
					};

					this._XMLRequest.Send();
				} else {
					// Only call callback
					var oWebDavResponse = this._onGetResponse(oRequestData, oResponseData);
					fCallback.call(this, oWebDavResponse);
				}
			} else {
				if (this._XMLRequest !== null) {
					this._XMLRequest.Send();
					oResponseData = this._XMLRequest.GetResponse();
				}

				// Send response synchronous
				return this._onGetResponse(oRequestData, oResponseData);
			}
		},

		_onGetResponse: function(oRequestData, oResponseData) {

			oResponseData.RequestMethod = this._Method;
			ITHit.Events.DispatchEvent(this, 'OnResponse', oResponseData);

			var oStatus = new ITHit.WebDAV.Client.HttpStatus(oResponseData.Status, oResponseData.StatusDescription);

			// If status is 278 treat this as a redirect, 302 redirect is processed automatically, no way to display login page
			// http://stackoverflow.com/questions/199099/how-to-manage-a-redirect-request-after-a-jquery-ajax-call
			if (oResponseData.Status == ITHit.WebDAV.Client.HttpStatus.Redirect.Code) {
				window.location.replace(oResponseData.Headers['Location']);
			}

			// If status is not success then raise exception.
			if (!oStatus.IsSuccess()) {
				throw self.ProcessWebException(oResponseData);
			}

			// Return response.
			return new ITHit.WebDAV.Client.WebDavResponse(oResponseData, oRequestData.Method);
		}

	});

})();


;
(function() {

	/**
	 * Represents a context for one or many requests.
	 * @api
	 * @class ITHit.WebDAV.Client.RequestProgress
	 */
	var self = ITHit.DefineClass('ITHit.WebDAV.Client.RequestProgress', null, /** @lends ITHit.WebDAV.Client.RequestProgress.prototype */{

		/**
		 * Progress in percents
		 * @api
		 * @type {number}
		 */
		Percent: 0,

		/**
		 * Count of complete operations
		 * @api
		 * @type {number}
		 */
		CountComplete: 0,

		/**
		 * Total operations count
		 * @api
		 * @type {number}
		 */
		CountTotal: 0,

		/**
		 * Count of loaded bytes
		 * @api
		 * @type {number}
		 */
		BytesLoaded: 0,

		/**
		 * Total bytes. This param can be changed in progress, if request has many operations (sub-requests).
		 * @api
		 * @type {number}
		 */
		BytesTotal: 0,

		/**
		 * Flag indicating if the resource concerned by the XMLHttpRequest ProgressEvent has a length that can be calculated.
		 * @api
		 * @type {boolean}
		 */
		LengthComputable: true,

		/**
		 * @type {object}
		 */
		_RequestsComplete: null,

		/**
		 * @type {object}
		 */
		_RequestsXhr: null,

		/**
		 *
		 * @param {number} [iRequestsCount] Requests count
		 */
		constructor: function(iRequestsCount) {
			this.CountTotal = iRequestsCount;
			this._RequestsComplete = {};
			this._RequestsXhr = {};
		},

		/**
		 * @param {number} iRequestId
		 */
		SetComplete: function(iRequestId) {
			if (this._RequestsComplete[iRequestId]) {
				return;
			}

			this._RequestsComplete[iRequestId] = true;
			this.CountComplete++;

			if (this._RequestsXhr[iRequestId]) {
				this._RequestsXhr[iRequestId].loaded = this._RequestsXhr[iRequestId].total;
				this.SetXhrEvent(iRequestId, this._RequestsXhr[iRequestId]);
			} else {
				this._UpdatePercent();
			}
		},

		/**
		 * @param {number} iRequestId
		 * @param {XMLHttpRequestProgressEvent} oXhrEvent
		 */
		SetXhrEvent: function(iRequestId, oXhrEvent) {
			this._RequestsXhr[iRequestId] = oXhrEvent;

			if (this.LengthComputable === false) {
				return;
			}

			this._ResetBytes();

			for (var iId in this._RequestsXhr) {
				if (!this._RequestsXhr.hasOwnProperty(iId)) {
					continue;
				}

				var oProgress = this._RequestsXhr[iId];
				if (oProgress.lengthComputable === false || !oProgress.total) {
					this.LengthComputable = false;
					this._ResetBytes();
					break;
				}

				this.BytesLoaded += oProgress.loaded;
				this.BytesTotal += oProgress.total;
			}

			this._UpdatePercent();
		},

		_ResetBytes: function() {
			this.BytesLoaded = 0;
			this.BytesTotal = 0;
		},

		_UpdatePercent: function() {
			if (this.LengthComputable) {
				this.Percent = 0;
				for (var iId in this._RequestsXhr) {
					if (!this._RequestsXhr.hasOwnProperty(iId)) {
						continue;
					}

					var oProgress = this._RequestsXhr[iId];
					this.Percent += (oProgress.loaded * 100 / oProgress.total) / this.CountTotal;
				}
			} else {
				this.Percent = this.CountComplete * 100 / this.CountTotal;
			}

			this.Percent = Math.round(this.Percent * 100) / 100;
		}

	});

})();


;
(function() {

	/**
	 * Represents a context for one or many requests.
	 * @api
	 * @fires ITHit.WebDAV.Client.Request#OnProgress
	 * @fires ITHit.WebDAV.Client.Request#OnError
	 * @fires ITHit.WebDAV.Client.Request#OnFinish
	 * @class ITHit.WebDAV.Client.Request
	 */
	var self = ITHit.DefineClass('ITHit.WebDAV.Client.Request', null, /** @lends ITHit.WebDAV.Client.Request.prototype */{

		__static: /** @lends ITHit.WebDAV.Client.Request */{

			/**
			 * Progress event trigger on update information about request progress.
			 * See {@link ITHit.WebDAV.Client.RequestProgress} for more information.
			 * @api
			 * @examplecode ITHit.WebDAV.Client.Tests.HierarchyItems.Progress.Progress
			 * @event ITHit.WebDAV.Client.Request#OnProgress
			 * @property {ITHit.WebDAV.Client.RequestProgress} Progress Progress info instance
			 * @property {ITHit.WebDAV.Client.Request} Request Current request
			 */
			EVENT_ON_PROGRESS: 'OnProgress',

			/**
			 * Error event trigger when one of request operations have error.
			 * Notice: This event trigger before async method callback.
			 * @api
			 * @event ITHit.WebDAV.Client.Request#OnError
			 * @property {Error|ITHit.WebDAV.Client.Exceptions.WebDavException} Error Error object
			 * @property {ITHit.WebDAV.Client.Request} Request Current request
			 */
			EVENT_ON_ERROR: 'OnError',

			/**
			 * Finish event trigger once when all operations in requests is complete.
			 * Notice: This event trigger before async method callback.
			 * @api
			 * @event ITHit.WebDAV.Client.Request#OnFinish
			 * @property {ITHit.WebDAV.Client.Request} Request Current request
			 */
			EVENT_ON_FINISH: 'OnFinish',

			IdCounter: 0

		},

		/**
		 * Auto generated unique request id
		 * @type {number}
		 */
		Id: null,

		/**
		 * Current WebDAV session.
		 * @type {ITHit.WebDAV.Client.WebDavSession}
		 */
		Session: null,

		/**
		 * Context name
		 * @type {string}
		 */
		Name: null,

		/**
		 * Progress info object, auto updated on `OnProgress` event.
		 * @api
		 * @type {ITHit.WebDAV.Client.RequestProgress}
		 */
		Progress: null,

		/**
		 * Count of requests
		 * @type {number}
		 */
		_RequestsCount: null,

		/**
		 * @type {ITHit.WebDAV.Client.WebDavRequest[]}
		 */
		_WebDavRequests: null,

		/**
		 * @type {boolean}
		 */
		_IsFinish: false,

		/**
		 *
		 * @param {ITHit.WebDAV.Client.WebDavSession} oSession Current WebDAV session.
		 * @param {string} [sName] Context name
		 * @param {number} [iRequestsCount] Requests count
		 */
		constructor: function(oSession, sName, iRequestsCount) {
			sName = sName || this.__instanceName;
			iRequestsCount = iRequestsCount || 1;

			this.Session = oSession;
			this.Name = sName;

			this.Id = self.IdCounter++;
			this._WebDavRequests = [];
			this._RequestsCount = iRequestsCount;
			this.Progress = new ITHit.WebDAV.Client.RequestProgress(iRequestsCount);
		},

		/**
		 * @api
		 * @param {string} sEventName
		 * @param fCallback
		 * @param {object} [oContext]
		 */
		AddListener: function(sEventName, fCallback, oContext) {
			oContext = oContext || null;

			switch (sEventName) {
				case self.EVENT_ON_PROGRESS:
				case self.EVENT_ON_ERROR:
				case self.EVENT_ON_FINISH:
					ITHit.Events.AddListener(this, sEventName, fCallback, oContext);
					break;

				default:
					throw new ITHit.WebDAV.Client.Exceptions.WebDavException('Not found event name `' + sEventName + '`');
			}
		},

		/**
		 * @api
		 * @param {string} sEventName
		 * @param fCallback
		 * @param {object} [oContext]
		 */
		RemoveListener: function(sEventName, fCallback, oContext) {
			oContext = oContext || null;

			switch (sEventName) {
				case self.EVENT_ON_PROGRESS:
				case self.EVENT_ON_ERROR:
				case self.EVENT_ON_FINISH:
					ITHit.Events.RemoveListener(this, sEventName, fCallback, oContext);
					break;

				default:
					throw new ITHit.WebDAV.Client.Exceptions.WebDavException('Not found event name `' + sEventName + '`');
			}
		},

		/**
		 * Cancels asynchronous request. The Finish event and the callback function will be called immediately after this method call.
		 * @api
		 */
		Abort: function() {
			for (var i = 0, l = this._WebDavRequests.length; i < l; i++) {
				this._WebDavRequests[i].Abort();
			}

			// @todo stop new requests, call finish?
		},

		MarkFinish: function() {
			if (this._IsFinish === true) {
				return;
			}
			this._IsFinish = true;

			ITHit.Events.DispatchEvent(this, self.EVENT_ON_FINISH, [{Request: this}]);

			var oDateNow = new Date();
			ITHit.Logger.WriteMessage('[' + this.Id + '] ----------------- Finished: ' + oDateNow.toUTCString() + ' [' + oDateNow.getTime() + '] -----------------' + '\n', ITHit.LogLevel.Info);

			// Remove listeners
			/*ITHit.Events.RemoveAllListeners(this, self.EVENT_ON_PROGRESS);
			ITHit.Events.RemoveAllListeners(this, self.EVENT_ON_ERROR);
			ITHit.Events.RemoveAllListeners(this, self.EVENT_ON_FINISH);*/
		},

		/**
		 * Create request.
		 * @param {string} sHost
		 * @param {string} sPath Item path.
		 * @param {(string|ITHit.WebDAV.Client.LockUriTokenPair)} [mLockTokens] Lock token for item.
		 * @returns {ITHit.WebDAV.Client.WebDavRequest} Request object.
		 */
		CreateWebDavRequest: function(sHost, sPath, mLockTokens) {
			var sId = this.Id;
			var oDateNow = new Date();

			// Check count requests
			if (this._WebDavRequests.length >= this._RequestsCount && typeof window.console !== 'undefined') {
				console.error('Wrong count of requests in [' + this.Id + '] `' + this.Name + '`');
			}

			ITHit.Logger.WriteMessage('\n[' + sId + '] ----------------- Started: ' + oDateNow.toUTCString() + ' [' + oDateNow.getTime() + '] -----------------', ITHit.LogLevel.Info);
			ITHit.Logger.WriteMessage('[' + sId + '] Context Name: ' + this.Name, ITHit.LogLevel.Info);

			var oWebDavRequest = this.Session.CreateWebDavRequest(sHost, sPath, mLockTokens);

			// Add listeners for write logs
			ITHit.Events.AddListener(oWebDavRequest, 'OnBeforeRequestSend', '_OnBeforeRequestSend', this);
			ITHit.Events.AddListener(oWebDavRequest, 'OnResponse', '_OnResponse', this);

			// Only for async requests
			ITHit.Events.AddListener(oWebDavRequest, 'OnProgress', '_OnProgress', this);
			ITHit.Events.AddListener(oWebDavRequest, 'OnFinish', '_OnFinish', this);

			this._WebDavRequests.push(oWebDavRequest);
			return oWebDavRequest;
		},

		_OnBeforeRequestSend: function(oRequestData) {
			this._WriteRequestLog(oRequestData);
		},

		_OnResponse: function(oResponseData) {
			this._WriteResponseLog(oResponseData);
		},

		_OnProgress: function(oEvent, iRequestId) {
			var iPreviousProgressPercent = this.Progress.Percent;
			this.Progress.SetXhrEvent(iRequestId, oEvent);

			if (this.Progress.Percent !== iPreviousProgressPercent) {
				ITHit.Events.DispatchEvent(this, self.EVENT_ON_PROGRESS, [{Progress: this.Progress, Request: this}]);
			}
		},

		_OnFinish: function(oAsyncResult, iRequestId) {
			var iPreviousProgressPercent = this.Progress.Percent;

			// Force set progress as 100%
			this.Progress.SetComplete(iRequestId);
			if (this.Progress.Percent !== iPreviousProgressPercent) {
				ITHit.Events.DispatchEvent(this, self.EVENT_ON_PROGRESS, [{Progress: this.Progress, Request: this}]);
			}

			if (!oAsyncResult.IsSuccess) {
				ITHit.Events.DispatchEvent(this, self.EVENT_ON_ERROR, [{Error: oAsyncResult.Error, AsyncResult: oAsyncResult, Request: this}]);
			}
		},

		_WriteRequestLog: function(oRequestData) {
			// Log request method and URI.
			ITHit.Logger.WriteMessage('[' + this.Id + '] ' + oRequestData.Method + ' ' + oRequestData.Href, ITHit.LogLevel.Info);

			// Log request headers.
			var aHeaders = [];
			for (var sHeader in oRequestData.Headers) {
				if (oRequestData.Headers.hasOwnProperty(sHeader)) {
					aHeaders.push(sHeader + ': ' + oRequestData.Headers[sHeader]);
				}
			}
			ITHit.Logger.WriteMessage('[' + this.Id + '] ' + aHeaders.join('\n'), ITHit.LogLevel.Info);

			var sBody = String(oRequestData.Body) || '';

			// Log request body if method is not PUT and body is not empty.
			if (oRequestData.Method.toUpperCase() !== 'PUT' && oRequestData.Body) {
				ITHit.Logger.WriteMessage('[' + this.Id + '] ' + sBody, ITHit.LogLevel.Info);
			}
		},

		_WriteResponseLog: function(oResponseData) {
			// Log response status
			ITHit.Logger.WriteMessage('\n[' + this.Id + '] '+ oResponseData.Status +' '+ oResponseData.StatusDescription, ITHit.LogLevel.Info);

			// Log response headers.
			var aHeaders = [];
			for (var sHeader in oResponseData.Headers) {
				if (oResponseData.Headers.hasOwnProperty(sHeader)) {
					aHeaders.push(sHeader + ': ' + oResponseData.Headers[sHeader]);
				}
			}
			ITHit.Logger.WriteMessage('[' + this.Id + '] ' + aHeaders.join('\n'), ITHit.LogLevel.Info);

			// Log error message.
			var bIsSuccess = (parseInt(oResponseData.Status / 100) == 2);
			var sResponseData = oResponseData.BodyXml && oResponseData.BodyXml.childNodes.length ?
				String(new ITHit.XMLDoc(oResponseData.BodyXml)) :
				oResponseData.BodyText;

			if (!bIsSuccess || oResponseData.RequestMethod.toUpperCase() !== "GET") {
				ITHit.Logger.WriteMessage('[' + this.Id + '] ' + sResponseData, bIsSuccess ? ITHit.LogLevel.Info : ITHit.LogLevel.Debug);
			}
		}

	});

})();


;
(function() {

    var self = ITHit.DefineClass('ITHit.WebDAV.Client.WebDavSession', null, /** @lends ITHit.WebDAV.Client.WebDavSession.prototype */{

        __static: /** @lends ITHit.WebDAV.Client.WebDavSession */{

            /**
             * Version of AJAX Library
             * @api
             */
            Version: '2.0.1735.0',

            /**
             * The OnBeforeRequestSend event is fired before request is being submitted to server and provides all
             * information that is used when creating the request such as URL, HTTP verb, headers and request body.
             * @api
             * @examplecode ITHit.WebDAV.Client.Tests.WebDavSession.Events.BeforeRequestSend
             * @event ITHit.WebDAV.Client.WebDavSession#OnBeforeRequestSend
             * @property {string} Method Request method
             * @property {string} Href Request absolute path
             * @property {object} Headers Key-value object with headers
             * @property {string} Body Request Body
             */
            EVENT_ON_BEFORE_REQUEST_SEND: 'OnBeforeRequestSend',

            /**
             * The OnResponse event fires when the data is received from server. In your event handler you can update
             * any data received from server.
             * @api
             * @examplecode ITHit.WebDAV.Client.Tests.WebDavSession.Events.Response
             * @event ITHit.WebDAV.Client.WebDavSession#OnResponse
             * @property {number} Status Response status code
             * @property {string} StatusDescription Response status description
             * @property {object} Headers Key-value object with headers
             * @property {string} Body Response Body
             */
            EVENT_ON_RESPONSE: 'OnResponse'

        },

        ServerEngine: null,
        _IsIisDetected: null,
        _User: '',
        _Pass: '',

        /**
         * @classdesc Session for accessing WebDAV servers.
         * @example
         *  &lt;!DOCTYPE html&gt;
         *  &lt;html lang="en"&gt;
         *  &lt;head&gt;
         *      &lt;title&gt;IT Hit WebDAV Ajax Library&lt;/title&gt;
         *      &lt;script src="http://www.ajaxbrowser.com/ITHitService/WebDAVAJAXLibrary/ITHitWebDAVClient.js" type="text/javascript"&gt;&lt;/script&gt;
         *      &lt;script type="text/javascript"&gt;
         *          var sFolderUrl = 'http://localhost:35829/';
         *          var oSession = new ITHit.WebDAV.Client.WebDavSession();
         *
         *          oSession.OpenFolderAsync(sFolderUrl, function (oFolderAsyncResult) {
         *              if (!oFolderAsyncResult.IsSuccess) {
         *                  console.error(oFolderAsyncResult.Error);
         *                  return;
         *               }
         *
         *              /&#42;&#42; &#64;typedef {ITHit.WebDAV.Client.Folder} oFolder &#42;/
         *              var oFolder = oFolderAsyncResult.Result;
         *
         *              console.log(oFolder.Href);
         *
         *              oFolder.GetChildrenAsync(false, null, function (oAsyncResult) {
         *                  if (!oAsyncResult.IsSuccess) {
         *                      console.error(oFolderAsyncResult.Error);
         *                      return;
         *                  }
         *
         *                  /&#42;&#42; &#64;typedef {ITHit.WebDAV.Client.HierarchyItem[]} aHierarchyItems &#42;/
         *                  var aHierarchyItems = oAsyncResult.Result;
         *
         *                  for (var i = 0, l = aHierarchyItems.length; i &lt; l; i++) {
         *                      var sSize = aHierarchyItems[i].ResourceType !== ITHit.WebDAV.Client.ResourceType.Folder ?
         *                          Math.round(aHierarchyItems[i].ContentLength / 1000) + ' Kb' :
         *                          null;
         *                      console.log(' [' + aHierarchyItems[i].ResourceType + '] ' + aHierarchyItems[i].DisplayName + (sSize ? ', ' + sSize : ''));
         *                  }
         *              });
         *          });
         *      &lt;/script&gt;
         *  &lt;/head&gt;
         *  &lt;body&gt;
         *  &lt;/body&gt;
         *  &lt;/html&gt;
         * @api
         * @fires ITHit.WebDAV.Client.WebDavSession#OnBeforeRequestSend
         * @fires ITHit.WebDAV.Client.WebDavSession#OnResponse
         * @constructs
         */
        constructor: function() {
        },

        /**
         * @api
         * @param {string} sEventName
         * @param fCallback
         * @param {object} [oContext]
         */
        AddListener: function(sEventName, fCallback, oContext) {
            oContext = oContext || null;

            switch (sEventName) {
                case self.EVENT_ON_BEFORE_REQUEST_SEND:
                case self.EVENT_ON_RESPONSE:
                    ITHit.Events.AddListener(this, sEventName, fCallback, oContext);
                    break;

                default:
                    throw new ITHit.WebDAV.Client.Exceptions.WebDavException('Not found event name `' + sEventName + '`');
            }
        },

        /**
         * @api
         * @param {string} sEventName
         * @param fCallback
         * @param {object} [oContext]
         */
        RemoveListener: function(sEventName, fCallback, oContext) {
            oContext = oContext || null;

            switch (sEventName) {
                case self.EVENT_ON_BEFORE_REQUEST_SEND:
                case self.EVENT_ON_RESPONSE:
                    ITHit.Events.RemoveListener(this, sEventName, fCallback, oContext);
                    break;

                default:
                    throw new ITHit.WebDAV.Client.Exceptions.WebDavException('Not found event name `' + sEventName + '`');
            }
        },

        /**
         * Load File object corresponding to path.
         * @api
         * @deprecated Use asynchronous method instead
         * @param {string} sPath Path to the file.
		 * @param {ITHit.WebDAV.Client.PropertyName[]} [aProperties] Additional properties requested from server. Default is empty array.
         * @returns {ITHit.WebDAV.Client.File} File corresponding to requested path.
         * @throws ITHit.WebDAV.Client.Exceptions.UnauthorizedException Incorrect credentials provided or insufficient permissions to access the requested item.
         * @throws ITHit.WebDAV.Client.Exceptions.NotFoundException The requested file doesn't exist on the server.
         * @throws ITHit.WebDAV.Client.Exceptions.ForbiddenException The server refused to fulfill the request.
         * @throws ITHit.WebDAV.Client.Exceptions.WebDavException Unexpected error occurred.
         */
        OpenFile: function(sPath, aProperties) {
			aProperties = aProperties || [];

            var oRequest = this.CreateRequest(this.__className + '.OpenFile()');
            var oFile = ITHit.WebDAV.Client.File.OpenItem(oRequest, sPath, aProperties);

            oRequest.MarkFinish();
            return oFile;
        },

        /**
         * Callback function to be called when file loaded from server.
         * @callback ITHit.WebDAV.Client.WebDavSession~OpenFileAsyncCallback
         * @param {ITHit.WebDAV.Client.AsyncResult} oResult Result object
         * @param {ITHit.WebDAV.Client.File} oResult.Result File corresponding to requested path.
         */

        /**
         * Load File object corresponding to path.
         * @api
         * @param {string} sPath Path to the file.
		 * @param {ITHit.WebDAV.Client.PropertyName[]} aProperties Additional properties requested from server. Default is empty array.
         * @param {ITHit.WebDAV.Client.WebDavSession~OpenFileAsyncCallback} fCallback Function to call when operation is completed.
         * @returns {ITHit.WebDAV.Client.Request} Request object.
         */
        OpenFileAsync: function(sPath, aProperties, fCallback) {
			aProperties = aProperties || [];

			var oRequest = this.CreateRequest(this.__className + '.OpenFileAsync()');
            ITHit.WebDAV.Client.File.OpenItemAsync(oRequest, sPath, aProperties, function(oAsyncResult) {

                oRequest.MarkFinish();
                fCallback(oAsyncResult);
            });

            return oRequest;
        },

        /**
         * Legacy proxy to OpenFile() method
         * @deprecated
         * @param sPath
         * @param [aProperties]
         */
        OpenResource: function(sPath, aProperties) {
			aProperties = aProperties || [];

			return this.OpenFile(sPath, aProperties);
        },

        /**
         * Legacy proxy to OpenFileAsync() method
         * @deprecated
         * @param sPath
         * @param aProperties
         * @param fCallback
         */
        OpenResourceAsync: function(sPath, aProperties, fCallback) {
			aProperties = aProperties || [];

			return this.OpenFileAsync(sPath, aProperties, fCallback);
        },

        /**
         * Returns Folder object corresponding to path.
         * @api
         * @deprecated Use asynchronous method instead
         * @param {string} sPath Path to the folder.
		 * @param {ITHit.WebDAV.Client.PropertyName[]} [aProperties] Additional properties requested from server. Default is empty array.
         * @returns {ITHit.WebDAV.Client.Folder} Folder corresponding to requested path.
         * @throws ITHit.WebDAV.Client.Exceptions.UnauthorizedException Incorrect credentials provided or insufficient permissions to access the requested item.
         * @throws ITHit.WebDAV.Client.Exceptions.NotFoundException The requested folder doesn't exist on the server.
         * @throws ITHit.WebDAV.Client.Exceptions.ForbiddenException The server refused to fulfill the request.
         * @throws ITHit.WebDAV.Client.Exceptions.WebDavException Unexpected error occurred.
         */
        OpenFolder: function(sPath, aProperties) {
			aProperties = aProperties || [];

			var oRequest = this.CreateRequest(this.__className + '.OpenFolder()');
            var oFolder = ITHit.WebDAV.Client.Folder.OpenItem(oRequest, sPath, aProperties);

            oRequest.MarkFinish();
            return oFolder;
        },

        /**
         * Callback function to be called when folder loaded from server.
         * @callback ITHit.WebDAV.Client.WebDavSession~OpenFolderAsyncCallback
         * @param {ITHit.WebDAV.Client.AsyncResult} oResult Result object
         * @param {ITHit.WebDAV.Client.Folder} oResult.Result Folder corresponding to requested path.
         */

        /**
         * Returns Folder object corresponding to path.
         * @examplecode ITHit.WebDAV.Client.Tests.HierarchyItems.GetItemBySession.GetFolder
         * @api
         * @param {string} sPath Path to the folder.
		 * @param {ITHit.WebDAV.Client.PropertyName[]} aProperties Additional properties requested from server. Default is empty array.
         * @param {ITHit.WebDAV.Client.WebDavSession~OpenFolderAsyncCallback} fCallback Function to call when operation is completed.
         */
        OpenFolderAsync: function(sPath, aProperties, fCallback) {
			aProperties = aProperties || [];

			var oRequest = this.CreateRequest(this.__className + '.OpenFolderAsync()');
            ITHit.WebDAV.Client.Folder.OpenItemAsync(oRequest, sPath, aProperties, function(oAsyncResult) {

                oRequest.MarkFinish();
                fCallback(oAsyncResult);
            });

            return oRequest;
        },

        /**
         * Returns HierarchyItem object corresponding to path.
         * @api
         * @deprecated Use asynchronous method instead
         * @param {string} sPath Path to the item.
		 * @param {ITHit.WebDAV.Client.PropertyName[]} [aProperties] Additional properties requested from server. Default is empty array.
         * @returns {ITHit.WebDAV.Client.HierarchyItem} Item corresponding to requested path.
         * @throws ITHit.WebDAV.Client.Exceptions.UnauthorizedException Incorrect credentials provided or insufficient permissions to access the requested item.
         * @throws ITHit.WebDAV.Client.Exceptions.NotFoundException The requested folder doesn't exist on the server.
         * @throws ITHit.WebDAV.Client.Exceptions.ForbiddenException The server refused to fulfill the request.
         * @throws ITHit.WebDAV.Client.Exceptions.WebDavException Unexpected error occurred.
         */
        OpenItem: function(sPath, aProperties) {
			aProperties = aProperties || [];

			var oRequest = this.CreateRequest(this.__className + '.OpenItem()');
            var oItem = ITHit.WebDAV.Client.HierarchyItem.OpenItem(oRequest, sPath, aProperties);

            oRequest.MarkFinish();
            return oItem;
        },

        /**
         * Callback function to be called when items loaded from server.
         * @callback ITHit.WebDAV.Client.WebDavSession~OpenItemAsyncCallback
         * @param {ITHit.WebDAV.Client.AsyncResult} oResult Result object
         * @param {ITHit.WebDAV.Client.HierarchyItem} oResult.Result Item corresponding to requested path.
         */

        /**
         * Returns HierarchyItem object corresponding to path.
         * @api
         * @param {string} sPath Path to the resource.
		 * @param {ITHit.WebDAV.Client.PropertyName[]} aProperties Additional properties requested from server. Default is empty array.
         * @param {ITHit.WebDAV.Client.WebDavSession~OpenItemAsyncCallback} fCallback Function to call when operation is completed.
         */
        OpenItemAsync: function(sPath, aProperties, fCallback) {
			aProperties = aProperties || [];

            var oRequest = this.CreateRequest(this.__className + '.OpenItemAsync()');
            ITHit.WebDAV.Client.HierarchyItem.OpenItemAsync(oRequest, sPath, aProperties, function(oAsyncResult) {

                oRequest.MarkFinish();
                fCallback(oAsyncResult);
            });

            return oRequest;
        },

        /**
         * Create request context.
         * @param {string} [sName]
         * @param {number} [iRequestsCount]
         * @returns {ITHit.WebDAV.Client.Request} Request context.
         */
        CreateRequest: function(sName, iRequestsCount){
            return new ITHit.WebDAV.Client.Request(this, sName, iRequestsCount);
        },

        /**
         * Create request.
         * @param {string} sHost
         * @param {string} sPath Item path.
         * @param {(string|ITHit.WebDAV.Client.LockUriTokenPair)} [mLockTokens] Lock token for item.
         * @returns {ITHit.WebDAV.Client.WebDavRequest} Request object.
         */
        CreateWebDavRequest: function(sHost, sPath, mLockTokens){

            if ('undefined' == typeof mLockTokens) {
                mLockTokens = [];
            }

            // Return new WebDavRequest object.
            var oWebDavRequest = ITHit.WebDAV.Client.WebDavRequest.Create(sPath, mLockTokens, this._User, this._Pass, sHost);

            // Attach event listener.
            ITHit.Events.AddListener(oWebDavRequest, 'OnBeforeRequestSend', 'OnBeforeRequestSendHandler', this);
            ITHit.Events.AddListener(oWebDavRequest, 'OnResponse', 'OnResponseHandler', this);

            return oWebDavRequest;
        },

        OnBeforeRequestSendHandler: function(oRequestData, oWebDavRequest) {

            ITHit.Events.RemoveListener(oWebDavRequest, 'OnBeforeRequestSend', 'OnBeforeRequestSendHandler', this);
            return ITHit.Events.DispatchEvent(this, 'OnBeforeRequestSend', oRequestData);
        },

        OnResponseHandler: function(oResponseData, oWebDavRequest) {

            var oWebDavRequest = arguments[arguments.length-1];

            // set version
            if (this.ServerEngine === null) {
                // Get webdav server version
                this.ServerEngine = oResponseData.GetResponseHeader('x-engine', true);
            }
            if (this._IsIisDetected === null) {
                // check Server header to match the IIS server
                var sServerHeader = oResponseData.GetResponseHeader('server', true);
                this._IsIisDetected = (/*/^Microsoft-HTTPAPI\//i.test(sServerHeader) ||*/ /^Microsoft-IIS\//i.test(sServerHeader));
            }

            ITHit.Events.RemoveListener(oWebDavRequest, 'OnResponse', 'OnResponseHandler', this);
            return ITHit.Events.DispatchEvent(this, 'OnResponse', oResponseData);
        },

        /**
         * Undo deleting file. Works only with ITHit WebDAV DeltaV ReumableUpload Server.
         * @param {string} sPath Item path.
         * @returns {object}
         */
        Undelete: function(sPath) {

            var oRequest = this.CreateRequest(this.__className + '.Undelete()');

            sPath   = ITHit.WebDAV.Client.Encoder.EncodeURI(sPath);
            var oReport = ITHit.WebDAV.Client.Methods.Undelete.Go(oRequest, sPath, ITHit.WebDAV.Client.HierarchyItem.GetHost(sPath));

            oRequest.MarkFinish();
            return oReport;
        },

        SetCredentials: function(sUser, sPass) {
            this._User    = sUser;
            this._Pass    = sPass;
        },

        GetIisDetected: function() {
            return this._IsIisDetected;
        }

    });

})();

// Clear temporary variable.
ITHit.Temp = {};
