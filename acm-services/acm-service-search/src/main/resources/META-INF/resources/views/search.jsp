<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout>
<jsp:attribute name="endOfHead">
    <title>${pageDescriptor.title}</title>
</script>
</jsp:attribute>


<jsp:attribute name="endOfBody">
    <script type="text/javascript" src="<c:url value='/resources/js/search/Search.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/search/SearchObject.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/search/SearchEvent.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/search/SearchPage.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/search/SearchRule.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/search/SearchService.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/search/SearchCallback.js'/>"></script>

    <%--<script type="text/javascript" src="<c:url value='/resources/js/app.js'/>"></script>--%>
    <%--<script type="text/javascript" src="<c:url value='/resources/js/app.plugin.js'/>"></script>--%>
    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_slimscroll}/jquery.slimscroll.min.js"></script>

    <script src="<c:url value='/'/>resources/vendors/${vd_charts}/easypiechart/jquery.easy-pie-chart.js"></script>
    <script src="<c:url value='/'/>resources/vendors/${vd_charts}/sparkline/jquery.sparkline.min.js"></script>
    <script src="<c:url value='/'/>resources/vendors/${vd_charts}/flot/jquery.flot.min.js"></script>
    <script src="<c:url value='/'/>resources/vendors/${vd_charts}/flot/jquery.flot.tooltip.min.js"></script>
    <script src="<c:url value='/'/>resources/vendors/${vd_charts}/flot/jquery.flot.spline.js"></script>
    <script src="<c:url value='/'/>resources/vendors/${vd_charts}/flot/jquery.flot.pie.min.js"></script>
    <script src="<c:url value='/'/>resources/vendors/${vd_charts}/flot/jquery.flot.resize.js"></script>
    <script src="<c:url value='/'/>resources/vendors/${vd_charts}/flot/jquery.flot.grow.js"></script>
    <script src="<c:url value='/'/>resources/vendors/${vd_charts}/flot/demo.js"></script>

    <!-- JTable -->
    <link rel="stylesheet" href="<c:url value='/'/>resources/vendors/${vd_jtable}/themes/lightcolor/blue/jtable.css" type="text/css"/>
    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_jtable}/jquery.jtable.js"></script>
</jsp:attribute>

<jsp:body>
<section id="content">
    <section class="hbox stretch">
        <aside class="aside-md bg-light dker b-r" id="subNav">
            <div class="wrapper b-b header">Advanced Search</div>

            <div class="wrapper">

                <div class="input-group">
                    <input type="text" class="input-sm form-control" id="searchQuery" placeholder="Search">
                        <span class="input-group-btn">
                          <button class="btn btn-sm btn-default" type="button">Go!</button>
                        </span>

                </div>

                <div class="line line-dashed b-b line-lg pull-in"></div>
                <div class="form-group">
                    <label class="col-sm-6 control-label">Complaints</label>
                    <div class="col-sm-4">
                        <label class="switch">
                            <input type="checkbox">
                            <span></span>
                        </label>
                    </div>
                </div>

                <div class="line line-dashed b-b line-lg pull-in"></div>

                <div class="form-group">
                    <label class="col-sm-6 control-label">Cases</label>
                    <div class="col-sm-4">
                        <label class="switch">
                            <input type="checkbox">
                            <span></span>
                        </label>
                    </div>
                </div>

                <div class="line line-dashed b-b line-lg pull-in"></div>

                <div class="form-group">
                    <label class="col-sm-6 control-label">Tasks</label>
                    <div class="col-sm-4">
                        <label class="switch">
                            <input type="checkbox">
                            <span></span>
                        </label>
                    </div>
                </div>

                <div class="line line-dashed b-b line-lg pull-in"></div>


                <div class="form-group">
                    <label class="col-sm-6 control-label">Documents</label>
                    <div class="col-sm-4">
                        <label class="switch">
                            <input type="checkbox">
                            <span></span>
                        </label>
                    </div>
                </div>

                <div class="line line-dashed b-b line-lg pull-in"></div>


            </div>





        </aside>
        <aside>
            <section class="vbox">
                <header class="header bg-white b-b clearfix">
                    <div class="row m-t-sm">
                        <div class="col-sm-12 m-b-xs">
                            <%--<a href="#subNav" data-toggle="class:hide" class="btn btn-sm btn-default active"><i class="fa fa-caret-right text fa-lg"></i><i class="fa fa-caret-left text-active fa-lg"></i></a>--%>
                            <a href="#subNav" data-toggle="class:hide" class="btn btn-sm btn-default "><i class="fa fa-caret-right text fa-lg"></i><i class="fa fa-caret-left text-active fa-lg"></i></a>
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

                    </div>
                </header>
                <section class="scrollable wrapper w-f">
                    <section class="panel panel-default">
                        <div id="divResults" style="width:98%"></div>
                        <!--
                        </br></br>
                        <div class="table-responsive">
                            <table class="table table-striped m-b-none">
                                <thead>
                                <tr>
                                    <th width="20"><label class="checkbox m-n i-checks"><input type="checkbox"><i></i></label></th>
                                    <th width="20"></th>
                                    <th width="20">ID</th>
                                    <th class="th-sortable" data-toggle="class">Type
                              <span class="th-sort">
                                <i class="fa fa-sort-down text"></i>
                                <i class="fa fa-sort-up text-active"></i>
                                <i class="fa fa-sort"></i>
                              </span>
                                    </th>
                                    <th class="th-sortable" data-toggle="class">Title
                              <span class="th-sort">
                                <i class="fa fa-sort-down text"></i>
                                <i class="fa fa-sort-up text-active"></i>
                                <i class="fa fa-sort"></i>
                              </span>
                                    <th>Owner</th>
                                    <th>Created</th>
                                </tr>
                                </thead>
                                <tbody>
                                <tr>
                                    <td><label class="checkbox m-n i-checks"><input type="checkbox" name="ids[]"><i></i></label></td>
                                    <td><a href="#modal" data-toggle="modal"><i class="fa fa-search-plus text-muted"></i></a></td>
                                    <td>[ID]</td>
                                    <td>[Type]</td>
                                    <td>[Title]</td>
                                    <td>[Owner]</td>
                                    <td>[Date Created]</td>
                                </tr>
                                <tr class="bg-primary-ltest">
                                    <td><label class="checkbox m-n i-checks"><input type="checkbox" name="ids[]"><i></i></label></td>
                                    <td><a href="#modal" data-toggle="modal"><i class="fa fa-search-plus text-muted"></i></a></td>
                                    <td>[ID]</td>
                                    <td>[Type]</td>
                                    <td>[Title]</td>
                                    <td>[Owner]</td>
                                    <td>[Date Created]</td>
                                </tr>
                                </tbody>
                            </table>
                        </div>
                        -->
                    </section>
                </section>
                <footer class="footer bg-white b-t">
                    <!--
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
                    -->
                </footer>
            </section>
        </aside>
    </section>
</section>
</jsp:body>
</t:layout>


