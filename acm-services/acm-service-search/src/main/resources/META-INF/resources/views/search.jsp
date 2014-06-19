<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout>
<jsp:attribute name="endOfHead">
    <title>${pageDescriptor.title}</title>

    <script type="text/javascript">
    $(document).ready(function () {
        SimpleSearch.initialize();
    });
</script>
</jsp:attribute>


<jsp:attribute name="endOfBody">
    <script type="text/javascript" src="<c:url value='/resources/js/search/simple/simpleSearch.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/search/simple/simpleSearchObject.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/search/simple/simpleSearchEvent.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/search/simple/simpleSearchPage.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/search/simple/simpleSearchRule.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/search/simple/simpleSearchService.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/search/simple/simpleSearchCallback.js'/>"></script>

    <script type="text/javascript" src="<c:url value='/resources/js/app.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/app.plugin.js'/>"></script>
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
</jsp:attribute>

<jsp:body>
<section id="content">
    <section class="hbox stretch">
        <aside class="aside-md bg-light dker b-r" id="subNav">
            <div class="wrapper b-b header">Advanced Search</div>
            tests
        </aside>
        <aside>
            <section class="vbox">
                <header class="header bg-white b-b clearfix">
                    <div class="row m-t-sm">
                        <div class="col-sm-8 m-b-xs">
                            <a href="#subNav" data-toggle="class:hide" class="btn btn-sm btn-default active"><i class="fa fa-caret-right text fa-lg"></i><i class="fa fa-caret-left text-active fa-lg"></i></a>
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
                        <div class="col-sm-4 m-b-xs">
                            <div class="input-group">
                                <input type="text" class="input-sm form-control" placeholder="Search">
                        <span class="input-group-btn">
                          <button class="btn btn-sm btn-default" type="button">Go!</button>
                        </span>
                            </div>
                        </div>
                    </div>
                </header>
                <section class="scrollable wrapper w-f">
                    <section class="panel panel-default">
                        <div class="table-responsive">
                            <table class="table table-striped m-b-none">
                                <thead>
                                <tr>
                                    <th width="20"><label class="checkbox m-n i-checks"><input type="checkbox"><i></i></label></th>
                                    <th width="20"></th>
                                    <th class="th-sortable" data-toggle="class">Type
                              <span class="th-sort">
                                <i class="fa fa-sort-down text"></i>
                                <i class="fa fa-sort-up text-active"></i>
                                <i class="fa fa-sort"></i>
                              </span>
                                    </th>
                                    <th>Task</th>
                                    <th>Date</th>
                                </tr>
                                </thead>
                                <tbody>
                                <tr>
                                    <td><label class="checkbox m-n i-checks"><input type="checkbox" name="ids[]"><i></i></label></td>
                                    <td><a href="#modal" data-toggle="modal"><i class="fa fa-search-plus text-muted"></i></a></td>
                                    <td>Idrawfast</td>
                                    <td>4c</td>
                                    <td>Jul 25, 2013</td>
                                </tr>
                                <tr class="bg-primary-ltest">
                                    <td><label class="checkbox m-n i-checks"><input type="checkbox" name="ids[]"><i></i></label></td>
                                    <td><a href="#modal" data-toggle="modal"><i class="fa fa-search-plus text-muted"></i></a></td>
                                    <td>Formasa</td>
                                    <td>8c</td>
                                    <td>Jul 22, 2013</td>
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
        </aside>
    </section>
</section>
</jsp:body>
</t:layout>


