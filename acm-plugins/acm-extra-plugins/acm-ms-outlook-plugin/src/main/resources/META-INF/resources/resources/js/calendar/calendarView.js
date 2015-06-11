/**
 * Calendar.View
 *
 * @author md
 */
Calendar.View = Calendar.View || {
    create : function(args) {
        if (Calendar.View.OutlookCalendar.create)       {Calendar.View.OutlookCalendar.create(args);}
    }
    ,onInitialized: function() {
        if (Calendar.View.OutlookCalendar.onInitialized)       {Calendar.View.OutlookCalendar.onInitialized(args);}
    }

    ,OutlookCalendar: {
        create: function(args) {
            this.$outlookCalendar = (args.$outlookCalendar)? args.$outlookCalendar : $("#calendar");
            this.$btnRefreshCalendar  = (args.$btnRefreshCalendar)? args.$btnRefreshCalendar : $("#refreshCalendar");

            this.$btnRefreshCalendar.on("click", function(e) {Calendar.View.OutlookCalendar.onClickbtnRefreshCalendar(e, this);});

            this.createOutlookCalendarWidget(this.$outlookCalendar);

            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT     ,this.onViewSelectedObject);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT    ,this.onModelRetrievedObject);

            Acm.Dispatcher.addEventListener(Calendar.Controller.MODEL_RETRIEVED_OUTLOOK_CALENDAR_ITEMS     ,this.onModelRetrievedOutlookCalendarItem);
        }
        ,onInitialized: function() {
        }
        ,_parentId: 0
        ,getParentId: function(){
            return this._parentId;
        }
        ,setParentId: function(parentId){
            this._parentId = parentId;
        }
        ,reloadOutlookCalendar : function(objId){
            Calendar.View.OutlookCalendar.setParentId(objId);
            Calendar.View.OutlookCalendar.$outlookCalendar.html("");
            Calendar.View.OutlookCalendar.createOutlookCalendarWidget(Calendar.View.OutlookCalendar.$outlookCalendar);
        }
        ,onModelRetrievedObject: function(objData){
            Calendar.View.OutlookCalendar.reloadOutlookCalendar(Acm.goodValue(objData.id));
        }
        ,onViewSelectedObject: function(nodeType, nodeId) {
            Calendar.View.OutlookCalendar.reloadOutlookCalendar(nodeId);
        }
        ,onModelRetrievedOutlookCalendarItem: function(outlookCalendarItems){
            if(outlookCalendarItems.hasError){
                App.View.MessageBoard.show($.t("casefile:outlook-calendar.msg.error-occurred"), outlookCalendarItems.errorMsg);
            }
            else{
                Calendar.View.OutlookCalendar.$outlookCalendar.html("");
                Calendar.View.OutlookCalendar.createOutlookCalendarWidget(Calendar.View.OutlookCalendar.$outlookCalendar);
            }
        }
        ,onClickbtnRefreshCalendar: function(){
            Calendar.Controller.viewRefreshedOutlookCalendar(Calendar.View.OutlookCalendar.getParentId());
        }
        ,createCalendarSource:function(){
            var calendarSource = [];
            var parentId = Calendar.View.OutlookCalendar.getParentId();
            if(Acm.isNotEmpty(parentId)){
                var parentObject = Calendar.Model.OutlookCalendar.cacheParentObject.get(parentId);
                if(Acm.isNotEmpty(parentObject)){
                    var outlookCalendarItems = Calendar.Model.OutlookCalendar.cacheOutlookCalendarItems.get(parentId);
                    if(Calendar.Model.OutlookCalendar.validateOutlookCalendarItems(outlookCalendarItems)){
                        for(var i = 0; i<outlookCalendarItems.items.length; i++){
                            if(Calendar.Model.OutlookCalendar.validateOutlookCalendarItem(outlookCalendarItems.items[i])) {
                                var outlookCalendarItem = {};
                                outlookCalendarItem.id = Acm.goodValue(outlookCalendarItems.items[i].id);
                                outlookCalendarItem.title = Acm.goodValue(outlookCalendarItems.items[i].subject);
                                outlookCalendarItem.start = Acm.goodValue(outlookCalendarItems.items[i].startDate);
                                outlookCalendarItem.end = Acm.goodValue(outlookCalendarItems.items[i].endDate);
                                outlookCalendarItem.detail = Calendar.View.OutlookCalendar.makeDetail(outlookCalendarItems.items[i]);
                                outlookCalendarItem.className = Acm.goodValue("b-l b-2x b-info");
                                outlookCalendarItem.allDay = Acm.goodValue(outlookCalendarItems.items[i].allDayEvent);
                                calendarSource.push(outlookCalendarItem);
                            }
                        }
                    }
                }
            }
            return calendarSource;
        }

        ,makeDetail: function(calendarItem){
            if(Calendar.Model.OutlookCalendar.validateOutlookCalendarItem(calendarItem)) {
                var body = Acm.goodValue(calendarItem.body) + "</br>";
                var startDateTime = Acm.getDateTimeFromDatetime(calendarItem.startDate);
                var startDateTimeWithoutSecond = $.t("casefile:outlook-calendar.label.start") + " " + startDateTime.substring(0,startDateTime.lastIndexOf(":"))+ "</br>";
                var endDateTime = Acm.getDateTimeFromDatetime(calendarItem.endDate);
                var endDateTimeWithoutSecond = $.t("casefile:outlook-calendar.label.end") + " " + endDateTime.substring(0,endDateTime.lastIndexOf(":"))+ "</br>";
                var detail = body + startDateTimeWithoutSecond + endDateTimeWithoutSecond
                return detail;
            }
        }

        ,createOutlookCalendarWidget: function($s){
            var calendarSource = this.createCalendarSource();
            var addDragEvent = function($this){
                // create an Event Object (http://arshaw.com/fullcalendar/docs/event_data/Event_Object/)
                // it doesn't need to have a start or end
                var eventObject = {
                    title: $.trim($this.text()), // use the element's text as the event title
                    className: $this.attr('class').replace('label','')
                };

                // store the Event Object in the DOM element so we can get to it later
                $this.data('eventObject', eventObject);

                // make the event draggable using jQuery UI
                $this.draggable({
                    zIndex: 999,
                    revert: true,      // will cause the event to go back to its
                    revertDuration: 0  //  original position after the drag
                });
            };

            $s.fullCalendar({
                header: {
                    left: 'prev,next today',
                    center: 'title',
                    right: 'month,agendaWeek,agendaDay'
                },
                buttonText: {
                    today:    'Today',
                    month:    'Month',
                    week:     'Week',
                    day:      'Day'
                },
                timeFormat: 'h(:mm)t {-h(:mm)t}',
                displayEventEnd : true,
                editable: true,
                //disable fullcalendar droppable as it creates conflict with the doctree's.
                //looks like fullcalendar uses the generic jquery draggable
                //we might need to add our own external draggable event handlers
                //tailored for fullcalendar
                droppable: false, // this allows things to be dropped onto the calendar !!!
                drop: function(date, allDay) { // this function is called when something is dropped

                    // retrieve the dropped element's stored Event Object
                    var originalEventObject = $(this).data('eventObject');

                    // we need to copy it, so that multiple events don't have a reference to the same object
                    var copiedEventObject = $.extend({}, originalEventObject);

                    // assign it the date that was reported
                    copiedEventObject.start = date;
                    copiedEventObject.allDay = allDay;

                    // render the event on the calendar
                    // the last `true` argument determines if the event "sticks" (http://arshaw.com/fullcalendar/docs/event_rendering/renderEvent/)
                    this.$outlookCalendar.fullCalendar('renderEvent', copiedEventObject, true);

                    // is the "remove after drop" checkbox checked?
                    if ($('#drop-remove').is(':checked')) {
                        // if so, remove the element from the "Draggable Events" list
                        $(this).remove();
                    }

                }
                ,events: calendarSource
                ,eventRender: function (event, element) {
                    element.qtip({
                        content: {
                            text: Acm.goodValue(event.detail),
                            title: {
                                text: Acm.goodValue(event.title)
                            }
                        }
                        ,position: {
                            my: 'right center',
                            at: 'left center',
                            target: 'mouse',
                            viewport: $s,
                            adjust: {
                                mouse: false,
                                scroll: false
                            }
                        }
                        ,style: {
                            classes: "qtip-rounded qtip-shadow"
                        }
                        ,show: { solo: true} //, ready: true, when: false
                        ,hide: { when: 'mouseout', fixed: true}
                    });
                }
            });
            $('#myEvents').on('change', function(e, item){
                addDragEvent($(item));
            });

            $('#myEvents li > div').each(function() {
                addDragEvent($(this));
            });
        }
    }
};

