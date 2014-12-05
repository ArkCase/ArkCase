/**
 * TaskList.Page
 *
 * manages all dynamic created page element
 *
 * @author jwu
 */
TaskList.Page = {
    create : function() {
    }
	/**
	 * Build the task list on the left panel with attributes like task title, created date, and created by. register each task
	 * object for the click event. Populate the right side task details panel. If no task was listed, hide the right side
	 * panel and display a no task was found message.
	 */
    ,buildTaskList: function(arr) {
        var html = "";
        if (!Acm.isArrayEmpty(arr)) {
            var len = arr.length;

            for (var i = 0; i < len; i++) {
                var t = arr[i];
                var taskId = t.object_id_s;
                if (0 == i) {
                    Task.setTaskId(taskId);
                }

                html += "<li class='list-group-item'><a href='#' class='thumb-sm pull-left m-r-sm'> <img src='"
                    + App.getContextPath() + "/resources/vendors/acm-3.0/themes/basic/images/a1.png" + "' class='img-circle'>"
                    + "</a>"
                    + "<a href='#' class='clear text-ellipsis'>"
                    + "<strong class='block' id='titleName" + taskId +"'>"
                    + t.name + "</strong>"
                    + "<small>"
                    + "Created Date: " + Acm.getDateFromDatetime(t.create_dt)
                    + "</small></br>" 
                    + "<small>"
                    + "Created By: " + t.owner_s + "</small>"
                    + "</a>"
                    + "<input type='hidden' value='" + taskId + "' /> </li>";
            }

            TaskList.Object.setHtmlUlTasks(html);
        	TaskList.Object.registerClickListItemEvents();
        	TaskList.Event.doClickLnkListItem();
        }
        else {
        	TaskList.Object.showObject(TaskList.Object.$taskDetailView, false);
        	TaskList.Object.showObject(TaskList.Object.$noTaskFoundMeassge, true);
            Acm.Dialog.alert("No task assigned to you was found.");

        }    
    }
    
    ,buildSignatureList: function(arr) {
        var html = "";
        if (!Acm.isArrayEmpty(arr)) {
            var len = arr.length;
            for (var i = 0; i < len; i++) {
                var t = arr[i];         
                html += '<tr class="odd gradeA"><td>' + t.signedBy + '</td><td>' + Acm.getDateFromDatetime(t.signedDate) +'</td></tr>';
            }
        }
        else {
        	html += '<tr class="odd gradeA"><td>None</td><td>&nbsp;</td></tr>';
        }
        
        TaskList.Object.setSignatureList(html);
    }
    
    /**
     * Change the title text of the active task of the tasks list view
     */
    ,updateActiveTaskTitle : function(taskId, title) {
    	var titleSel = $("#titleName" + taskId);
    	titleSel.text(title);
    }
    
    // Reject Task
    ,buildDlgRejectTaskOwner: function(element, results) {
    	if (element) {
	    	for (var i = 0; i < results.length; i++) {
				var result = results[i];
				var checked = '';
				
				var selected = TaskList.Object.getDlgRejectTaskSelected();
	    		if (selected == null) {
	    			var task = TaskList.getTask();
	        		
	        		if (Acm.isNotEmpty(task) && task.owner) {
	        			selected = task.owner;
	        		}
	    		}
				
				if (selected && result.object_id_s == selected) {
					checked = 'checked="checked"';
					TaskList.Object.setDlgRejectTaskSelected(selected);
				}
				
				var tr = '<tr>' +
	            			'<td><label class="checkbox m-n"><input type="radio" value="' + result.object_id_s + '" id="returnToUser" name="returnToUser" ' + checked + ' /><i></i></label></td>' +
	            			'<td>' + result.first_name_lcs + '</td>' +
				            '<td>' + result.last_name_lcs + '</td>' +
				            '<td>' + result.object_id_s + '</td>' +
				            '<td>' + '' + '</td>' +
				         '</tr>';
				
				element.append(tr);
			}
	    	
	    	$('input[name=returnToUser]:radio').change(function(e) {TaskList.Event.onChangeDlgRejectTaskSelected(e);});
    	}
    }
    ,buildDlgRejectTaskUsers: function(element, results) {
    	if (element) {
	    	for (var i = 0; i < results.length; i++) {
				var result = results[i];
				var checked = '';
				
				var selected = TaskList.Object.getDlgRejectTaskSelected();
				
				if (selected && result.object_id_s == selected) {
					checked = 'checked="checked"';
					TaskList.Object.setDlgRejectTaskSelected(selected);
				}
				
				var tr = '<tr>' +
	            			'<td><label class="checkbox m-n"><input type="radio" value="' + result.object_id_s + '" id="returnToUser" name="returnToUser" ' + checked + ' /><i></i></label></td>' +
	            			'<td>' + result.first_name_lcs + '</td>' +
				            '<td>' + result.last_name_lcs + '</td>' +
				            '<td>' + result.object_id_s + '</td>' +
				            '<td>' + '' + '</td>' +
				         '</tr>';
				
				element.append(tr);
			}
	    	
	    	$('input[name=returnToUser]:radio').change(function(e) {TaskList.Event.onChangeDlgRejectTaskSelected(e);});
    	}
    }
    ,buildDlgRejectTaskMutedText: function(element, from, to, total) {
    	if (element) {
    		element.empty();
    		element.append('Showing ' + from + '-' + to +' of ' + total + ' items');
    	}
    }
    ,buildDlgRejectTaskPagination: function(element, page, pages) {
    	if (element) {
    		element.empty();
    		
    		// Left pagination button
    		var $leftBtnHtml = $($.parseHTML('<li><a href="#"><i class="fa fa-chevron-left"></i></a></li>'));
    		if (page == 1) {
    			$leftBtnHtml.addClass('disabled');
    		} else {
    			$leftBtnHtml.click(function(e) {TaskList.Event.onClickDlgRejectTaskLeftBtn(e);});
    		}
    		element.append($leftBtnHtml);
    		
    		// Page button
    		if (page != -1) {
        		for (var i = 0; i < pages; i++) {
        			var $page = $($.parseHTML('<li><a href="#">' + (i+1) + '</a></li>'));
        			var active = '';
        			
        			if (i == (page - 1)) {
        				$page.addClass('active');
        			} else {
        				$page.click(function(e) {TaskList.Event.onClickDlgRejectTaskPageBtn(e);});
        			}
        			
        			element.append($page);
        		}
        	}
        	
    		// Right pagination button
    		var $rightBtnHtml = $($.parseHTML('<li><a href="#"><i class="fa fa-chevron-right"></i></a></li>'));
    		if (page == pages || pages == 0) {
    			$rightBtnHtml.addClass('disabled');
    		} else {
    			$rightBtnHtml.click(function(e) {TaskList.Event.onClickDlgRejectTaskRightBtn(e);});
    		}
    		element.append($rightBtnHtml);
    	}
    }
    ,cleanDlgRejectTaskOwner: function(element) {
    	var $tbody = element.find('table#ownerTableRejectTask tbody');
    	
    	if ($tbody) {
    		$tbody.empty();
    	}
    }
    ,cleanDlgRejectTaskUsers: function(element) {
    	var $tbody = element.find('table#usersTableRejectTask tbody');
    	var $textMuted = element.find('footer.panel-footer small.text-muted');
    	var $ulPagination = element.find('footer.panel-footer ul.pagination');
    	
    	if ($tbody) {
    		$tbody.empty();
    	}
    	
    	if ($textMuted) {
    		$textMuted.empty();
    	}
    	
    	if ($ulPagination) {
    		$ulPagination.empty();
    	}
    }
};
