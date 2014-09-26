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
    ,getSelectedText : function($s) {
        var v = $s.find("option:selected").text();
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
    ,getSelectTextIgnoreFirst: function($s) {
        var selected = Acm.Object.getSelectedText($s);
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


};




