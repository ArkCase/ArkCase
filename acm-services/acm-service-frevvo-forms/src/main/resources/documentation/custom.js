// frevvo custom JavaScript

// Import Application CSS
document.writeln('<link href="/frevvo/arkcase/libs/app.css" rel="stylesheet" />');

// Import jQuery
document.writeln('<script type="text/javascript" src="/frevvo/arkcase/libs/jquery-1.11.0/jquery-1.11.0.js"></script>');
document.writeln('<script type="text/javascript">var frevvo_jQuery = jQuery.noConflict(true);</script>');

// Import jQuery UI
document.writeln('<script type="text/javascript" src="/frevvo/arkcase/libs/jquery-ui-1.10.3/js/jquery-ui-1.10.3.custom.js"></script>');
document.writeln('<link href="/frevvo/arkcase/libs/jquery-ui-1.10.3/css/ui-lightness/jquery-ui-1.10.3.custom.css" rel="stylesheet" />');

// Import Bootstrap
document.writeln('<script type="text/javascript" src="/frevvo/arkcase/libs/bootstrap-3.1.1/js/bootstrap.js"></script>');
document.writeln('<link href="/frevvo/arkcase/libs/bootstrap-3.1.1/css/bootstrap.css" rel="stylesheet" />');

// Import jTable
document.writeln('<script type="text/javascript" src="/frevvo/arkcase/libs/jtable-2.4.0/jquery.jtable.js"></script>');
document.writeln('<link href="/frevvo/arkcase/libs/jtable-2.4.0/themes/acm/jtable.css" rel="stylesheet" />');

// Import Font Awesome
document.writeln('<link rel="stylesheet" href="/frevvo/arkcase/libs/font-awesome/css/font-awesome.css" type="text/css">');

// Import Rich TextArea Plugin
document.writeln('<link rel="stylesheet" href="/frevvo/arkcase/rich-textarea-plugin-v3.0/summernote/summernote.css" type="text/css">');
document.writeln('<script type="text/javascript" src="/frevvo/arkcase/rich-textarea-plugin-v3.0/summernote/summernote.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/arkcase/rich-textarea-plugin-v3.0/richtextarea.plugin.js"></script>');

// Import ArkCase libs
document.writeln('<script type="text/javascript" src="/frevvo/arkcase/libs/app/app.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/arkcase/libs/app/appCallback.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/arkcase/libs/app/appController.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/arkcase/libs/app/appEvent.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/arkcase/libs/app/appModel.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/arkcase/libs/app/appObject.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/arkcase/libs/app/appService.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/arkcase/libs/app/appView.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/arkcase/libs/core/acm.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/arkcase/libs/core/acmAjax.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/arkcase/libs/core/acmDialog.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/arkcase/libs/core/acmDispatcher.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/arkcase/libs/core/acmModel.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/arkcase/libs/core/acmObject.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/arkcase/libs/core/acmService.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/arkcase/libs/core/acmValidator.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/arkcase/libs/ex/acmEx.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/arkcase/libs/ex/acmExModel.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/arkcase/libs/ex/acmExObject.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/arkcase/libs/ex/acmExService.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/arkcase/libs/profile/profile.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/arkcase/libs/profile/profileController.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/arkcase/libs/profile/profileModel.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/arkcase/libs/profile/profileService.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/arkcase/libs/profile/profileView.js"></script>');

// Import User Picker Plugin
document.writeln('<script type="text/javascript" src="/frevvo/arkcase/user-picker-plugin-v1.0/search/searchBase.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/arkcase/user-picker-plugin-v1.0/search/searchBaseController.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/arkcase/user-picker-plugin-v1.0/search/searchBaseModel.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/arkcase/user-picker-plugin-v1.0/search/searchBaseService.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/arkcase/user-picker-plugin-v1.0/search/searchBaseView.js"></script>');

// Frevvo Patch
document.writeln('<script type="text/javascript" src="/frevvo/arkcase/patch/form.pack.js.patch"></script>');

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
		var container = document.getElementById('container');
		var userPickerContainer = document.createElement('div');
		
		userPickerContainer.innerHTML = userPickerString;
		container.appendChild(userPickerContainer);
		
		App.create();
		App.onInitialized();
		
		AcmEx.create();
		AcmEx.onInitialize();
		
		SearchBase.create();
		SearchBase.onInitialized();
		
		Profile.create();
		Profile.onInitialized();
   },
   
   showUserPicker: function(event, element) {
		this.scrollTop(event, element);
   
		var filters = [{key: "Object Type", values: ["USER"]}];
		
		var owningGroup = getOwningGroup();
		if (owningGroup !== null && owningGroup !== "" && filterByOwningGroup(element)){
			filters.push({key: "Group", values: [owningGroup]});
		}
		
		SearchBase.showSearchDialog({name: "pickUser"
			,title: "Add User"
			,prompt: "Enter to search for users."
			,btnGoText: "Go!"
			,btnOkText: "Add"
			,btnCancelText: "Cancel"
			,filters: filters
			,jtArgs: {
				multiselect: false
				,selecting:true
				,selectingCheckboxes:true
			}
			,onClickBtnPrimary : function(event, ctrl) {
				SearchBase.View.Results.getSelectedRows().each(function () {
					var record = frevvo_jQuery(this).data('record');
					if (record && record.id && record.name) {						
						if (isSimpleUserPicker(element)) {
							// Simple user picker (fill only user id and full name)
							doSimpleUserPicker(element, record.id, record.name);
						} else if (isAdvancedUserPicker(element)){
							// Advanced user picker (fill user id, full name, first name, last name, location, email, phone ... etc ...)
							doAdvancedUserPicker(element, record.id);
						}
					}
				});
			}
		});
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
											'<div class="col-xs-3">' +
												'<div class="facets" id="divPoFacets">' +
						
												'</div>' +
											'</div>' +
											'<div class="col-xs-9">' +
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