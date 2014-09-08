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
                                            <label for="Case Number"  class="label">Case Number</label>
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
                                    <section class="vbox" id="mainContent">
                                        <header class="header bg-white b-b clearfix">
                                            <div class="row m-t-sm">
                                                <div class="col-sm-6 m-b-xs"> <a href="#subNav" data-toggle="class:hide" class="btn btn-sm btn-default active"><i class="fa fa-caret-right text fa-lg"></i><i class="fa fa-caret-left text-active fa-lg"></i></a>
                                                    <div class="btn-group">
                                                        <button type="button" class="btn btn-sm btn-default" title="Refresh"><i class="fa fa-refresh"></i></button>
                                                        <button type="button" class="btn btn-sm btn-default" title="Filter" data-toggle="dropdown"><i class="fa fa-filter"></i> <span class="caret"></span></button>
                                                        <ul class="dropdown-menu">
                                                            <li><a href="#">Filter 1</a></li>
                                                            <li><a href="#">Filter 2</a></li>
                                                            <li><a href="#">Filter 3</a></li>
                                                        </ul>
                                                    </div>
                                                </div>
                                                <div class="col-sm-6 m-b-xs">
                                                    <button class="btn btn-sm btn-default pull-right">Apply</button>
                                                    <select class="input-sm form-control input-s-sm inline v-middle pull-right">
                                                        <option value="0">Action</option>
                                                        <option value="1">Export to Excel</option>
                                                    </select>
                                                </div>
                                            </div>
                                        </header>
                                        <section class="scrollable wrapper w-f">
                                            <section class="panel panel-default">
                                                <div class="table-responsive">
                                                    <table class="table table-striped m-b-none">
                                                        <thead>
                                                        <tr>
                                                            <th class="th-sortable" width="">Column 1 <span class="th-sort"> <i class="fa fa-sort-down text"></i> <i class="fa fa-sort-up text-active"></i> <i class="fa fa-sort"></i> </span></th>
                                                            <th class="th-sortable" data-toggle="class">Column 2 <span class="th-sort"> <i class="fa fa-sort-down text"></i> <i class="fa fa-sort-up text-active"></i> <i class="fa fa-sort"></i> </span> </th>
                                                            <th class="th-sortable">Column 3 <span class="th-sort"> <i class="fa fa-sort-down text"></i> <i class="fa fa-sort-up text-active"></i> <i class="fa fa-sort"></i> </span></th>
                                                            <th class="th-sortable" data-toggle="class">Column 4 <span class="th-sort"> <i class="fa fa-sort-down text"></i> <i class="fa fa-sort-up text-active"></i> <i class="fa fa-sort"></i> </span>
                                                            <th class="th-sortable">Column 5 <span class="th-sort"> <i class="fa fa-sort-down text"></i> <i class="fa fa-sort-up text-active"></i> <i class="fa fa-sort"></i> </span></th>
                                                            <th class="th-sortable">Column 6 <span class="th-sort"> <i class="fa fa-sort-down text"></i> <i class="fa fa-sort-up text-active"></i> <i class="fa fa-sort"></i> </span></th>
                                                        </tr>
                                                        </thead>
                                                        <tbody>
                                                        <tr>
                                                            <td>[Column 1]</td>
                                                            <td>[Column 2]</td>
                                                            <td>[Column 3]</td>
                                                            <td>[Column 4]</td>
                                                            <td>[Column 5]</td>
                                                            <td>[Column 6]</td>
                                                        </tr>
                                                        <tr class="bg-primary-ltest">
                                                            <td>[Column 1]</td>
                                                            <td>[Column 2]</td>
                                                            <td>[Column 3]</td>
                                                            <td>[Column 4]</td>
                                                            <td>[Column 5]</td>
                                                            <td>[Column 6]</td>
                                                        </tr>
                                                        </tbody>
                                                    </table>
                                                </div>
                                            </section>
                                        </section>
                                        <footer class="footer bg-white b-t">
                                            <div class="row text-center-xs">
                                                <div class="col-md-6 hidden-sm">
                                                    <p class="text-muted m-t">Showing 20-30 of 50</p>
                                                </div>
                                                <div class="col-md-6 col-sm-12 text-right text-center-xs">
                                                    <ul class="pagination pagination-sm m-t-sm m-b-none">
                                                        <li><a href="#"><i class="fa fa-chevron-left"></i></a></li>
                                                        <li class="active"><a href="#">1</a></li>
                                                        <li><a href="#">2</a></li>
                                                        <li><a href="#">3</a></li>
                                                        <li><a href="#">4</a></li>
                                                        <li><a href="#">5</a></li>
                                                        <li><a href="#"><i class="fa fa-chevron-right"></i></a></li>
                                                    </ul>
                                                </div>
                                            </div>
                                        </footer>
                                    </section>
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


