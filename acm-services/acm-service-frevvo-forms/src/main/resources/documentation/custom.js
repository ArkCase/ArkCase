// frevvo custom JavaScript

// Import Rich TextArea Plugin
document.writeln('<script type="text/javascript" src="/frevvo/js-25644/libs/rich-textarea-plugin-v2.1/tinymce/tinymce.min.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/js-25644/libs/rich-textarea-plugin-v2.1/richtextarea.plugin.js"></script>');

// Import Application CSS
document.writeln('<link href="/frevvo/js-25644/libs/user-picker-plugin-v1.0/app.css" rel="stylesheet" />');

// Import jQuery
document.writeln('<script type="text/javascript" src="/frevvo/js-25644/libs/user-picker-plugin-v1.0/jquery-1.11.0/jquery-1.11.0.js"></script>');
document.writeln('<script type="text/javascript">var frevvo_jQuery = jQuery.noConflict(true);</script>');

// Import jQuery UI
document.writeln('<script type="text/javascript" src="/frevvo/js-25644/libs/user-picker-plugin-v1.0/jquery-ui-1.10.3/js/jquery-ui-1.10.3.custom.js"></script>');
document.writeln('<link href="/frevvo/js-25644/libs/user-picker-plugin-v1.0/jquery-ui-1.10.3/css/ui-lightness/jquery-ui-1.10.3.custom.css" rel="stylesheet" />');

// Import Bootstrap
document.writeln('<script type="text/javascript" src="/frevvo/js-25644/libs/user-picker-plugin-v1.0/bootstrap-3.1.1/js/bootstrap.js"></script>');
document.writeln('<link href="/frevvo/js-25644/libs/user-picker-plugin-v1.0/bootstrap-3.1.1/css/bootstrap.css" rel="stylesheet" />');

// Import jTable
document.writeln('<script type="text/javascript" src="/frevvo/js-25644/libs/user-picker-plugin-v1.0/jtable-2.4.0/jquery.jtable.js"></script>');
document.writeln('<link href="/frevvo/js-25644/libs/user-picker-plugin-v1.0/jtable-2.4.0/themes/acm/jtable.css" rel="stylesheet" />');

// Import User Picker Plugin
document.writeln('<script type="text/javascript" src="/frevvo/js-25644/libs/user-picker-plugin-v1.0/app/app.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/js-25644/libs/user-picker-plugin-v1.0/app/appCallback.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/js-25644/libs/user-picker-plugin-v1.0/app/appController.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/js-25644/libs/user-picker-plugin-v1.0/app/appEvent.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/js-25644/libs/user-picker-plugin-v1.0/app/appModel.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/js-25644/libs/user-picker-plugin-v1.0/app/appObject.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/js-25644/libs/user-picker-plugin-v1.0/app/appService.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/js-25644/libs/user-picker-plugin-v1.0/app/appView.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/js-25644/libs/user-picker-plugin-v1.0/core/acm.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/js-25644/libs/user-picker-plugin-v1.0/core/acmAjax.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/js-25644/libs/user-picker-plugin-v1.0/core/acmDialog.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/js-25644/libs/user-picker-plugin-v1.0/core/acmDispatcher.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/js-25644/libs/user-picker-plugin-v1.0/core/acmModel.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/js-25644/libs/user-picker-plugin-v1.0/core/acmObject.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/js-25644/libs/user-picker-plugin-v1.0/core/acmService.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/js-25644/libs/user-picker-plugin-v1.0/core/acmValidator.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/js-25644/libs/user-picker-plugin-v1.0/ex/acmEx.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/js-25644/libs/user-picker-plugin-v1.0/ex/acmExModel.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/js-25644/libs/user-picker-plugin-v1.0/ex/acmExObject.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/js-25644/libs/user-picker-plugin-v1.0/ex/acmExService.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/js-25644/libs/user-picker-plugin-v1.0/search/searchBase.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/js-25644/libs/user-picker-plugin-v1.0/search/searchBaseController.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/js-25644/libs/user-picker-plugin-v1.0/search/searchBaseModel.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/js-25644/libs/user-picker-plugin-v1.0/search/searchBaseService.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/js-25644/libs/user-picker-plugin-v1.0/search/searchBaseView.js"></script>');


var CustomEventHandlers = {
   setup: function (el) {
       if (CustomView.hasClass(el, 'nextTab')) {
           FEvent.observe(el, 'click', this.scrollTop.bindAsObserver(this, el));
       } else if (CustomView.hasClass(el, 'previousTab')) {
           FEvent.observe(el, 'click', this.scrollTop.bindAsObserver(this, el));
       } else if (CustomView.hasClass(el, 'createUserPicker')) {
           this.createUserPicker();
       } else if (CustomView.hasClass(el, 'userPicker')) {
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
   },
   
   showUserPicker: function(event, element) {
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
						element.value = record.name;
						var changeEvent_name = document.createEvent("Event");
						changeEvent_name.initEvent("change", true, true);
						element.dispatchEvent(changeEvent_name);
						
						// Find id filed for this element
						var ids = document.getElementsBySelector('.' + element.name + '_id input');
						var elementId = null;
						
						if (ids && ids.length == 1) {
							elementId = ids[0];
						} else if (ids.length > 1){
							try{
								elementId = element.parentNode.parentNode.parentNode.parentNode.getElementsBySelector('.' + element.name + '_id input')[0];
							}catch(e) {
							
							}
						}
						
						if (elementId != null) {
							elementId.value = record.id;
							var changeEvent_id = document.createEvent("Event");
							changeEvent_id.initEvent("change", true, true);
							elementId.dispatchEvent(changeEvent_id);
						}
					}
				});
			}
		});
   }
}

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
						
function getOwningGroup() {
	var owningGroup = null;
	
	try{
		var element = document.getElementsBySelector('.owningGroup input')[0];
		return element.value;
	}catch(e) {
	
	}
}

function filterByOwningGroup(element){
	try{
		var elementType = element.parentNode.parentNode.parentNode.parentNode.getElementsBySelector('.' + element.name + '_type input')[0];
		if (elementType && elementType.value === 'group-user') {
			return true;
		}
	}catch(e) {
	
	}
	
	return false;
}