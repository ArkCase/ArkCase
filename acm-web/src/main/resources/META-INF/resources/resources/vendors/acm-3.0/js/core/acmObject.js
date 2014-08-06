/**
 * Acm.Object
 *
 * common function for screen object management
 * Argument $s must be a valid jQuery selector
 *
 * @author jwu
 */
Acm.Object = {
    initialize : function() {
    }


    ,getValue : function($s) {
        return $s.val();
    }
    ,setValue : function($s, value) {
        if (null == value) {
            value = "";
        }
        $s.val(value);
    }
    ,getText : function($s) {
        return $s.text();
    }
    ,setText : function($s, value) {
        if (null == value) {
            value = "";
        }
    	$s.text(value);
    }

    //
    //i is zero based index to indeicate which text node to use
    //i not specified -- return all text nodes as whole
    //i = -1          -- return last text node
    //
    ,getTextNodeText : function($s, i) {
        var textNodes = $s.contents().filter(function() {return this.nodeType == 3;});

        if (0 >= textNodes.length) {
            return "";
        } else if (undefined === i) {
            return textNodes.text();
        } else if (-1 === i) {
            i = textNodes.length - 1;
        }

        return textNodes[i].nodeValue;
    }
    ,setTextNodeText : function($s, value, i) {
        if (null == value) {
            value = "";
        }

        var textNodes = $s.contents().filter(function() {return this.nodeType == 3;});

        if (0 >= textNodes.length) {
            return;
        } else if (undefined === i) {
            i = 0;
        } else if (-1 === i) {
            i = textNodes.length - 1;
        }

        textNodes[i].nodeValue = value;
//    	textNodes[0].data = value;
    }
//    ,getSelectValue : function($s) {
//    	return $s.attr('value');
////        return jQuery("select" + id + " option:selected").val();
//    }
    ,getSelectValue : function($s) {
        var v = $s.find("option:selected").val();
        if ("placeholder" == v) {
            v = "";
        }
        return v;
    }
    ,setSelectValue : function($s, value) {
        $s.find("option").filter(function() {
//    	jQuery("select" + id + " option").filter(function() {
            return jQuery(this).val() == value;
        //}).prop('selected', true); //for jQuery v1.6+
    	}).attr('selected', true);
    }
    ,appendSelect: function($s, key, val) {
        $s.append($("<option></option>")
            .attr("value",key)
            .text(val));
    }

    //ignore first option, which is instruction
    ,getSelectValueIgnoreFirst: function($s) {
        var selected = Acm.Object.getSelectValue($s);
        var firstOpt = $s.find("option:first").val();
        return (selected == firstOpt)? null : selected;
    }
    ,getSelectValues: function($s) {
        var mv = [];
        $s.find("option:selected").each(function(i, selected) {
            mv[i]  = $(selected).val();
        });
        return mv;
    }
    ,getSelectValuesAsString: function($s, sep) {
        return $s.find("option:selected").map(function(){
            return this.value;
        }).get().join(sep);
    }

    ,getPlaceHolderInput : function($s) {
        var v;
        v = $s.val();
        v = ($s.attr('placeholder') !== v) ? v : "";
        return v;
    }
    ,setPlaceHolderInput : function($s, val) {
        //$s.val(Acm.goodValue(val, ""));
        $s.trigger('focus').val(Acm.goodValue(val, "")).trigger('blur');
    }

    ,changePlaceHolderSelect : function($s) {
        if($s.val() == "placeholder") {
            $s.addClass("placeholder");
        } else {
            $s.removeClass("placeholder");
        }
    }
    ,isChecked : function($s) {
    	return $s.is(":checked");;
    }
    ,setChecked : function($s, value) {
        if ("true" == value || true == value) {
            $s.attr("checked", "checked");
            //$s.prop("checked", true); //for v1.6+
        } else {
            $s.removeAttr("checked");
            //$s.prop("checked", false); //for v1.6+
        }
    }
    ,getHtml : function($s) {
        return $s.html();
    }
    ,setHtml : function($s, value) {
        $s.html(value);
    }

    // Setting value directly to a date picker causes date picker popup initially visible.
    // Use setValueDatePicker() to solve the problem.
    ,setValueDatePicker: function($s, val) {
        $s.attr("style", "display:none");
        Acm.Object.setPlaceHolderInput($s, val);
        Acm.Object.show($s, true);
    }

    ,setEnable : function($s, value) {
        if (value == "true" || value == true) {
            $s.removeAttr("disabled");
            //$s.prop("disabled", false); //for v1.6+
        } else {
            $s.attr("disabled", "disabled");
            //$s.prop("disabled", true); //for v1.6+
        }
    }
    ,isEnable : function($s) {
        var d = $s.attr("disabled");
        return !d;
    }
    ,removeClick : function($s) {
        $s.unbind("click")
             .click(function(event){return event.preventDefault();});
    }
    ,show : function($s, show) {
        if (show == "true" || show == true) {
            $s.show();
        } else {
            $s.hide();
        }
    }
    ,showParent : function($s, show) {
        var p = $s.parent();
        if (p)
        if ("true" == show || true == show) {
            p.show();
        } else {
            p.hide();
        }
    }

    //work around for hiding options in select list in IE
    ,showOption: function($s, show) {
        if (show) {
            $s.each(function(index, val) {
                if(navigator.appName == 'Microsoft Internet Explorer') {
                    if (this.nodeName.toUpperCase() === 'OPTION') {
                        var span = $(this).parent();
                        var opt = this;
                        if($(this).parent().is('span')) {
                            $(opt).show();
                            $(span).replaceWith(opt);
                        }
                    }
                } else {
                    $(this).show(); //all other browsers use standard .show()
                }
            });
        } else {
            $s.each(function(index, val){
                if ($(this).is('option') && (!$(this).parent().is('span')))
                    $(this).wrap((navigator.appName == 'Microsoft Internet Explorer') ? '<span>' : null).hide();
            });
        }
    }
    ,isVisible : function($s) {
        return $s.is(":visible");
    }
    ,empty : function($s) {
        $s.empty();
    }

    ,getSummernote : function($s) {
        return $s.code();
    }
    ,setSummernote : function($s, value) {
        $s.code(value);
    }

    //
    // JTable functions
    //
    ,JTABLE_DEFAULT_PAGE_SIZE: 8
    ,jTableGetEmptyResult: function() { return {"Result": "OK","Records": [],"TotalRecordCount": 0};}
    ,jTableLoad: function($jt) {
        $jt.jtable('load');
    }
    ,_catNextParam: function(url) {
//        var rc;
//        if (0 < url.indexOf('?')) {
//            rc = "?";
//        } else {
//            rc = "&";
//        }
//        return rc;
        return (0 < url.indexOf('?'))? "&" : "?";
    }
    ,jTableDefaultListAction: function(postData, jtParams, sortMap, urlEvealuator, responseHandler) {
        if (Acm.isEmpty(App.getContextPath())) {
            return Acm.Object.jTableGetEmptyResult();
        }

        var url = urlEvealuator();
        if (Acm.isNotEmpty(jtParams.jtStartIndex)) {
            url += this._catNextParam(url) + "start=" + jtParams.jtStartIndex;
        }
        if (Acm.isNotEmpty(jtParams.jtPageSize)) {
            url += this._catNextParam(url) + "n=" + jtParams.jtPageSize;
        }
        if (Acm.isNotEmpty(jtParams.jtSorting)) {
            var arr = jtParams.jtSorting.split(" ");
            if (2 == arr.length) {
                for (var key in sortMap) {
                    if (key == arr[0]) {
                        url += this._catNextParam(url) + "s=" + sortMap[key] + "%20" + arr[1];
                    }
                }
            }
        }
        return $.Deferred(function ($dfd) {
            $.ajax({
                url: url,
                type: 'GET',
                dataType: 'json',
                data: postData,
                success: function (data) {
                    if (data) {
                        var jtResponse = responseHandler(data);
                    }

                    if (jtResponse.jtData) {
                        $dfd.resolve(jtResponse.jtData);
                    } else {
                        $dfd.reject();
                        Acm.Dialog.error(jtResponse.jtError);
                    }
                },
                error: function () {
                    $dfd.reject();
                }
            });
        });
    }
    ,jTableCreateSortable: function($jt, jtArg, sortMap) {
        jtArg.paging = true;
        if (!jtArg.pageSize) {
            jtArg.pageSize = Acm.Object.JTABLE_DEFAULT_PAGE_SIZE;
        }
        if (!jtArg.recordAdded) {
            jtArg.recordAdded = function(event, data){
                $jt.jtable('load');
            }
        }
        if (!jtArg.recordUpdated) {
            jtArg.recordUpdated = function(event, data){
                $jt.jtable('load');
            }
        }

        if (sortMap) {
            jtArg.sorting = true;
        } else if (!jtArg.sorting) {
            jtArg.sorting = false;
        }

        if (jtArg.actions.listActionSortable){
            jtArg.actions.listAction = function(postData, jtParams) {
                return jtArg.actions.listActionSortable(postData, jtParams, sortMap);
            }
        }

        $jt.jtable(jtArg);

        $jt.jtable('load');
    }



    ,useDobInput: function($s) {
        $s.datepicker({
            changeMonth: true, changeYear: true,
//            yearRange: "-100:+1", minDate: '-100y', maxDate: '0',
            yearRange: "-100:+1", minDate: '-100y', maxDate: '+1y',
            onSelect: function(dateText){
            	jQuery(this).css({'color' : '#000'});
            }
        });
    }

    ,useSsnInput: function($s) {
//        $s.keypress(function (keypressed) {
//            return Acm.Callbacks.onSsnKeypress(this, keypressed);
//        });

        $s.keyup(function() {
            // remove any characters that are not numbers
            var socnum = this.value.replace(/\D/g, '');
            var sslen  = socnum.length;

            if(sslen>3&&sslen<6) {
                var ssa=socnum.slice(0,3);
                var ssb=socnum.slice(3,5);
                this.value=ssa+"-"+ssb;
            } else {
                if(sslen>5) {
                    var ssa=socnum.slice(0,3);
                    var ssb=socnum.slice(3,5);
                    var ssc=socnum.slice(5,9);
                    this.value=ssa+"-"+ssb+"-"+ssc;
                } else {
                    this.value=socnum;
                }
            }
        });
    }
    ,getSsnValue: function (ssnDisplay) {
        var ssn = ssnDisplay.replace(/\D/g, '');
        return ssn;
    }
    ,getSsnDisplay: function (ssn) {
        if (Acm.isEmpty(ssn))
            return "";

        var ssnDisplay = ssn.substring(0, 3) + '-' + ssn.substring(3, 5) + '-' + ssn.substring(5, 9);
        return ssnDisplay;
    }

    ,usePagination: function ($s, totalItems, itemsPerPage, callback) {
        if (totalItems > 0) {
            $s.paginationskipinit(totalItems,
                {
                    items_per_page: itemsPerPage,
                    pagerLocation: "both",
                    prev_text: "Previous",
                    next_text: "Next",
                    prev_show_always: false,
                    next_show_always: false,
                    pages_show_always: true,
                    callback: callback
                }
            );
        } else {
            $s.html("");
        }
    }


};




