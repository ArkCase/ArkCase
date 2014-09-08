<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<t:layout>
<jsp:attribute name="endOfHead">
    <title>${pageDescriptor.title}</title>
</jsp:attribute>


<jsp:attribute name="endOfBody">
    <script type="text/javascript" src="<c:url value='/resources/js/report/report.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/report/reportObject.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/report/reportEvent.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/report/reportPage.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/report/reportRule.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/report/reportService.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/report/reportCallback.js'/>"></script>

    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_slimscroll}/jquery.slimscroll.min.js"></script>

</jsp:attribute>

<jsp:body>
    <%--<section id="content">
        <section class="vbox">
            <section class="scrollable padder">
                <section class="row m-b-md">
                    <div class="col-sm-12">
                        <h3 class="m-b-xs text-black">Report</h3>
                    </div>
                </section>--%>



                        <!-- /.aside -->
                        <section id="content">
                            <section class="hbox stretch">
                                <aside class="aside-md bg-light dker b-r" id="subNav">
                                    <section class="scrollable">
                                        <div class="wrapper b-b header">Reports</div>
                                        <div class="wrapper">
                                            <label for="priority"  class="label">Report</label>
                                            <select name="priority" class="form-control" id ="selectReport">
                                                <option>Choose Report</option>
                                                <c:forEach items="${reportUrlsMap}" var="entry">
                            						<option title="${entry.key}" value="${entry.value}">${entry.key}</option>
                                            	</c:forEach>
                                            </select>
                                            <div class="line line-dashed b-b line-lg pull-in"></div>
                                            <label for="Case Number"  class="label" id="caseNumberlbl">Case Number</label>
                                            <input id="caseNumber" type="text" class="form-control" placeholder="Case Number"  >
                                            <div class="line line-dashed b-b line-lg pull-in"></div>                                            
                                            <label for="Data Range"  class="label">Date Range</label>
                                            <div class="clearfix"></div>
                                            <label class="label col-sm-3">From</label>
                                            <div class="col-sm-9">
                                                <input class="datepicker-input form-control" type="text" id = "startDate" value= "" data-date-format="dd-mm-yyyy" >
                                            </div>

                                            <label class="label col-sm-3">To</label>
                                            <div class="col-sm-9">

                                                <input class="datepicker-input form-control" type="text" id = "endDate" value= "" data-date-format="dd-mm-yyyy" >
                                            </div>

                                            <div class="clearfix"></div>
                                            <div class="line line-dashed b-b line-lg pull-in"></div>
                                            <button class="btn btn-sm btn-default center-block" type="button" id="generateReport">Generate Report</button>
                                        </div>
                                    </section>
                                </aside>
                                <aside>
                                    <label for="Report message" id="ReportMeassge" class="class="m-b-xs text-black pull-left">To run a report, select a report name, enter criteria, and click Generate Report</label>
                                    <iframe src="" name="report_iframe" style="width:100%; height:100%;" frameborder="0"></iframe>
                                </aside>
                            </section>
                        </section>
                    </section>
                </section>
                </section>
                <script src="resources/js/slimscroll/jquery.slimscroll.min.js"></script>
                <script src="resources/js/datepicker/bootstrap-datepicker.js"></script>


                <!-- Bootstrap -->
                <script src="resources/js/bootstrap.js"></script>
                <!-- App -->
                <script src="resources/js/app.js"></script>
                <script src="resources/js/slimscroll/jquery.slimscroll.min.js"></script>
                <script src="resources/js/app.plugin.js"></script>

                test
                <input type="button" value="Test" id="test" />

    		</section>
        </section>
    </section>
</jsp:body>
</t:layout>


