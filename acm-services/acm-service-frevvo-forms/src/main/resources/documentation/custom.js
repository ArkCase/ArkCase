// frevvo custom JavaScript

// Import jQuery
document.writeln('<script type="text/javascript" src="/frevvo/js-28315/arkcase/libs/jquery-1.11.0/jquery-1.11.0.js"></script>');
document.writeln('<script type="text/javascript">var frevvo_jQuery = jQuery.noConflict(true);</script>');

// Import jQuery UI
document.writeln('<script type="text/javascript" src="/frevvo/js-28315/arkcase/libs/jquery-ui-1.10.3/js/jquery-ui-1.10.3.custom.js"></script>');
document.writeln('<link href="/frevvo/js-28315/arkcase/libs/jquery-ui-1.10.3/css/ui-lightness/jquery-ui-1.10.3.custom.css" rel="stylesheet" />');

// Import Bootstrap
document.writeln('<script type="text/javascript" src="/frevvo/js-28315/arkcase/libs/bootstrap-3.1.1/js/bootstrap.js"></script>');
document.writeln('<link href="/frevvo/js-28315/arkcase/libs/bootstrap-3.1.1/css/bootstrap.css" rel="stylesheet" />');

// Import Font Awesome
document.writeln('<link rel="stylesheet" href="/frevvo/js-28315/arkcase/libs/font-awesome/css/font-awesome.css" type="text/css">');

// Import Rich TextArea Plugin
document.writeln('<link rel="stylesheet" href="/frevvo/js-28315/arkcase/rich-textarea-plugin-v3.0/summernote/summernote.css" type="text/css">');
document.writeln('<script type="text/javascript" src="/frevvo/js-28315/arkcase/rich-textarea-plugin-v3.0/summernote/summernote.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/js-28315/arkcase/rich-textarea-plugin-v3.0/richtextarea.plugin.js"></script>');

// Import ArkCase libs
// Still we need these libraries because for Advanced User Picker we are taking more information for the user using REST call (please see method "doAdvancedUserPicker(..)")
document.writeln('<script type="text/javascript" src="/frevvo/js-28315/arkcase/libs/app/app.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/js-28315/arkcase/libs/app/appCallback.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/js-28315/arkcase/libs/app/appController.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/js-28315/arkcase/libs/app/appEvent.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/js-28315/arkcase/libs/app/appModel.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/js-28315/arkcase/libs/app/appObject.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/js-28315/arkcase/libs/app/appService.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/js-28315/arkcase/libs/app/appView.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/js-28315/arkcase/libs/core/acm.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/js-28315/arkcase/libs/core/acmAjax.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/js-28315/arkcase/libs/core/acmDialog.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/js-28315/arkcase/libs/core/acmDispatcher.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/js-28315/arkcase/libs/core/acmModel.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/js-28315/arkcase/libs/core/acmObject.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/js-28315/arkcase/libs/core/acmService.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/js-28315/arkcase/libs/core/acmValidator.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/js-28315/arkcase/libs/ex/acmEx.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/js-28315/arkcase/libs/ex/acmExModel.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/js-28315/arkcase/libs/ex/acmExObject.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/js-28315/arkcase/libs/ex/acmExService.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/js-28315/arkcase/libs/profile/profile.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/js-28315/arkcase/libs/profile/profileController.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/js-28315/arkcase/libs/profile/profileModel.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/js-28315/arkcase/libs/profile/profileService.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/js-28315/arkcase/libs/profile/profileView.js"></script>');

// Frevvo Patch
document.writeln('<script type="text/javascript" src="/frevvo/js-28315/arkcase/patch/form.pack.js.patch"></script>');

// Frevvo Messaging
var frevvoMessaging = null;

var CustomEventHandlers = {
   setup: function (el) {
	   var elState = CustomView.getState(el);
       if (CustomView.hasClass(el, 'nextTab')) {
           FEvent.observe(el, 'click', this.scrollTop.bindAsObserver(this, el));
       } else if (CustomView.hasClass(el, 'previousTab')) {
           FEvent.observe(el, 'click', this.scrollTop.bindAsObserver(this, el));
       } else if (CustomView.hasClass(el, 'createUserPicker')) {
           this.createUserPicker();
       } else if (isSimpleUserPicker(el) || isAdvancedUserPicker(el)) {
           FEvent.observe(el, 'focus', this.showUserPicker.bindAsObserver(this, el));
       }
   },
   
   scrollTop: function (event, element) {
       document.getElementById("wrapper").scrollIntoView();
   },
   
   createUserPicker: function() {
       if (frevvoMessaging == null) {
            frevvoMessaging = {};
            frevvoMessaging.elements = {};
            frevvoMessaging.receiver = window.parent.parent;
            frevvoMessaging.send = function send(message) {
                frevvoMessaging.receiver.postMessage(message, '*');
            }
            frevvoMessaging.receive = function receive(e) {
                if (e.data.source == "arkcase") {
                    // Do actions sent from Arkcase
                    if (e.data.action == "fill-user-picker-data") {
                        var element = frevvoMessaging.elements[e.data.elementId];
                        if (isSimpleUserPicker(element)) {
                            // Simple user picker (fill only user id and full name)
                            doSimpleUserPicker(element, e.data.data.object_id_s, e.data.data.name);
                        } else if (isAdvancedUserPicker(element)){
                            // Advanced user picker (fill user id, full name, first name, last name, location, email, phone ... etc ...)
                            doAdvancedUserPicker(element, e.data.data.object_id_s);
                        }
                    }
                }
            }
            
            window.addEventListener('message', frevvoMessaging.receive);
        }
   },
   
   showUserPicker: function(event, element) {
        if (frevvoMessaging != null) {
            var message = {};
            message.source = "frevvo";
            message.data = "";
            message.action = "open-user-picker";
            message.elementId = element.id;
            frevvoMessaging.elements[element.id] = element;
            
            var owningGroup = getOwningGroup();
            if (owningGroup != null) {
                message.data = {"owningGroup": owningGroup};
            }
            
            // Open user picker
            frevvoMessaging.send(message);
        }
   }
}

/**
 * The logic for populating fields after clicking "Add" button in the user picker when we should fill only simple information, like User Id and Full Name
 */
function doSimpleUserPicker(element, userId, value) {
	updateElement(element, 'fullName', value)
	updateElement(element, 'id', userId);
}

/**
 * The logic for populating fields after clicking "Add" button in the user picker when we should fill multiple fields, like User Id, First Name, Last Name, Location, Email, Phone
 */
function doAdvancedUserPicker(element, userId) {
	var response = Profile.Service.Info.retrieveProfileInfo(userId);				
	if (response) {
		var responseObj = JSON.parse(response);
		if (responseObj) {
			updateElement(element, 'id', responseObj.userId);
			updateElement(element, 'location', responseObj.firstAddress);
			updateElement(element, 'firstName', responseObj.firstName);
			updateElement(element, 'lastName', responseObj.lastName);
			updateElement(element, 'email', responseObj.email);
			updateElement(element, 'phone', responseObj.mobilePhoneNumber);
		}
	}
}

/**
 * Dispatch change event for the field that we are changing the value. We need to do this because on that way
 * Frevvo make update on his data model on the backend side
 */
function dispatchChangeEvent(element) {
	var changeEvent = document.createEvent("Event");
	changeEvent.initEvent("change", true, true);
	element.dispatchEvent(changeEvent);
}

/**
 * Recognizing if the simple user picker logic should be executed - fill single field
 */
function isSimpleUserPicker(element) {
	var elementState = CustomView.getState(element);
	if((elementState && elementState.cssClass && elementState.cssClass.indexOf('userPickerSimple') != -1)) {
		return true;
	} else {
		return false;
	}
}

/**
 * Recognizing if the advanced user picker logic should be executed - fill multiple fields
 */
function isAdvancedUserPicker(element) {
	var elementState = CustomView.getState(element);
	if((elementState && elementState.cssClass && elementState.cssClass.indexOf('userPickerAdvanced') != -1)) {
		return true;
	} else {
		return false;
	}
}

/**
 * Taking CSS class from the element. This CSS class is the class entered in the Frevvo form while designing the form with Frevvo Engine.
 * It can be "userPickerSimple_<RECOGNITIONTEXT>_<FIELDNAME>" - for simple user picker or "userPickerAdvanced_<RECOGNITIONTEXT>_<FIELDNAME>" - for advanced user picer
 */
function getCssClass(element) {
	var elementState = CustomView.getState(element);
	if(elementState && elementState.cssClass) {
		return elementState.cssClass;
	}
	return null;
}

/**
 * The class is in the format: "userPickerSimple_<RECOGNITIONTEXT>_<FIELDNAME>" or "userPickerAdvanced_<RECOGNITIONTEXT>_<FIELDNAME>"
 * For example: userPickerAdvanced_prosecutor_firstName
 * This method will return: userPickerAdvanced_prosecutor 
 */
function getCssClassDivided(element) {
	var cssClass = getCssClass(element);
	if (cssClass != null) {
		var cssClassArray = cssClass.split('_');
		
		if (cssClassArray && cssClassArray.length === 3) {
			return cssClassArray[0] + '_' + cssClassArray[1];
		}
	}
	return null;
}

/**
 * This method will find the element for given class name (the class name entered in the Frevvo form while designing the form with Frevvo Engine).
 * After finding the element (if exist), the value will be changed and dispathed change event (on that way the data model on the Frevvo backend will be updated too)
 */
function updateElement(element, fieldName, value) {
	var cssClassDivided = getCssClassDivided(element);
	if (cssClassDivided != null) {
		if (value === null) {
			value = '';
		}
		var elementToUpdate = null;
		var elements = document.getElementsBySelector('.' + cssClassDivided + '_' + fieldName + ' input');
		if (elements && elements.length == 1) {
			elementToUpdate = elements[0];
		} else if (elements && elements.length > 1){
			try{
				elementToUpdate = element.parentNode.parentNode.parentNode.parentNode.getElementsBySelector('.' + cssClassDivided + '_' + fieldName + ' input')[0];
			}catch(e) {
				// Normal behaviour - element is not found
			}
		}
		
		if (elementToUpdate != null) {
			elementToUpdate.value = value;
			dispatchChangeEvent(elementToUpdate);
		}
	}
}

/**
 * This method will return the value selected in the owning group if exist that kind of element
 */
function getOwningGroup() {
	var owningGroup = null;
	try{
		var element = document.getElementsBySelector('.owningGroup input')[0];
		return element.value;
	}catch(e) {
		// Normal behaviour - the element is not found
        return null;
	}
}

/**
 * This method will return the state if we should add filter by groups while showing the user picker for the first time.
 * This can be happen if we have picked some group
 */
function filterByOwningGroup(element){
	try{
		var elementType = element.parentNode.parentNode.parentNode.parentNode.getElementsBySelector('.' + element.name + '_type input')[0];
		if (elementType && elementType.value === 'group-user') {
			return true;
		}
	}catch(e) {
		// Normal behaviour - the element is not found
	}
	return false;
}

/**
 * UI for the user picker
 */
var userPickerString = '<div class="modal fade" id="dlgObjectPicker" tabindex="-1" role="dialog" aria-labelledby="labPoTitle" aria-hidden="true" style="display: none;">' +
							'<div class="modal-dialog modal-lg">' +
								'<div class="modal-content">' +
									'<div class="modal-header">' +
										'<button type="button" class="close" data-dismiss="modal">&times;<span class="sr-only">Close</span></button>' +
										'<h4 class="modal-title" id="labPoTitle">Choose Objects</h4>' +
									'</div>' +
									'<header class="header bg-gradient b-b clearfix">' +
										'<div class="row m-t-sm">' +
											'<div class="col-md-12 m-b-sm">' +
												'<div class="input-group">' +
													'<input type="text" class="input-md form-control" id="edtPoSearch" placeholder=\'<spring:message code="search.input.placeholder" text="Type in your search query to find complaints, cases, tasks, and documents." />\'>' +
													'<span class="input-group-btn">' +
													'<button class="btn btn-md" type="button"><spring:message code="search.submit.text" text="Go!" /></button>' +
													'</span> </div>' +
											'</div>' +
										'</div>' +
									'</header>' +
									'<div class="modal-body">' +
										'<div class="row">' +
											'<div class="col-xs-3 hidden">' +
												'<div class="facets" id="divPoFacets">' +
						
												'</div>' +
											'</div>' +
											'<div class="full-width">' +
												'<section class="panel panel-default">' +
													'<div class="table-responsive" id="divPoResults">' +
														
													'</div>' +
												'</section>' +
											'</div>' +
										'</div>' +
									'</div>' +
									'<div class="modal-footer">' +
										'<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>' +
										'<button type="button" class="btn btn-primary">OK</button>' +
									'</div>' +
								'</div>' +
							'</div>' +
						'</div>';

/* Rich Text Area properties - START */
var rtaSelector = 'div.rta_container span.f-message:not([style="display: none;"])';

var rtaSummernoteOptions = {
							toolbar: [
							  ['style', ['style']],
							  ['font', ['bold', 'italic', 'underline', 'clear']],
							  ['fontsize', ['fontsize']],
							  ['color', ['color']],
							  ['para', ['ul', 'ol', 'paragraph']],
							  ['height', ['height']],
							  ['table', ['table']],
							  ['view', ['fullscreen', 'codeview']],
							  ['help', ['help']]
							],
					  
							height: 280
						};
						
var rtaRefreshMilliseconds = 500;
/* Rich Text Area properties - END */