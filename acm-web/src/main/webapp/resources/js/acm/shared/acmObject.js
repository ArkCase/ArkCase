/**
 * ACM.Object
 *
 * common function for screen object management
 * Argument $s must be a valid jQuery selector
 *
 * @author jwu
 */
ACM.Object = {
    initialize : function() {
    }

    ,getValue : function($s) {
        return $s.val();;
    }
    ,setValue : function($s, value) {
        $s.val(value);
    }
    ,getText : function($s) {
        return $s.val();;
    }
    ,setText : function($s, value) {
    	$s.text(value);
    }
    ,getTextNodeText : function($s) {
        var textNode = $s.contents()
            .filter(function() {return this.nodeType == 3;});
        return textNode.text();
    }
    ,setTextNodeText : function($s, value) {
        var textNode = $s.contents()
            .filter(function() {return this.nodeType == 3;});
//    	textNode[0].data = value;
        textNode[0].nodeValue = value;
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
    ,getPlaceHolderInput : function($s) {
        var v;
        v = $s.val();
        v = ($s.attr('placeholder') !== v) ? v : "";
        return v;
    }
    ,setPlaceHolderInput : function($s, val) {
        //$s.val(ACM.Common.goodValue(val, ""));
        $s.trigger('focus').val(ACM.Common.goodValue(val, "")).trigger('blur');
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
        return $s.html();;
    }
    ,setHtml : function($s, value) {
        $s.html(value);
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
//            return ACM.Callbacks.onSsnKeypress(this, keypressed);
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
        if (ACM.Common.isEmpty(ssn))
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




