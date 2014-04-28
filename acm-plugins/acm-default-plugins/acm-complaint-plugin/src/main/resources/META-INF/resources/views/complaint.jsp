<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>


<t:layout>
<jsp:attribute name="endOfHead">
    <title>Complaints | ACM | Armedia Case Management</title>

    <script type="text/javascript">
        $(document).ready(function () {
            Complaint.initialize();
        });
    </script>
</jsp:attribute>

<jsp:attribute name="endOfBody">
    <script type="text/javascript" src="<c:url value='/resources/js/complaint/complaint.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/complaint/complaintObject.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/complaint/complaintEvent.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/complaint/complaintPage.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/complaint/complaintRule.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/complaint/complaintService.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/complaint/complaintCallback.js'/>"></script>

    <script src="<c:url value='/resources/js/app.js'/>"></script>
    <script src="<c:url value='/resources/js/slimscroll/jquery.slimscroll.min.js'/>"></script>
    <script src="<c:url value='/resources/js/app.plugin.js'/>"></script>
</jsp:attribute>

<jsp:body>
<section id="content">
<section class="vbox">
<section class="scrollable">


<section class="hbox stretch">

<aside class="aside-lg" id="email-list">
    <section class="vbox">
        <header class="dker header clearfix">

            <h3 class="m-b-xs text-black pull-left">Complaints</h3>



            <div class="btn-toolbar">


                <div class="btn-group inline select pull-right">
                    <button class="btn btn-default btn-sm  dropdown-toggle" data-toggle="dropdown">
                        <span class="dropdown-label" style="width: 65px;"><i class="fa fa-sort"></i></span>
                        <span class="caret"></span>
                    </button>
                    <ul class="dropdown-menu text-left text-sm">
                        <li><a href="#">Sort Date Ascending</a></li>
                        <li><a href="#">Sort Date Descending</a></li>
                        <li><a href="#">Sort Title Ascending</a></li>
                        <li><a href="#">Sort Title Ascending</a></li>
                    </ul>
                </div>

                <div class="btn-group select pull-right">
                    <button class="btn btn-default btn-sm  dropdown-toggle" data-toggle="dropdown">
                        <span class="dropdown-label" style="width: 65px;"><i class="fa fa-filter"></i></span>
                        <span class="caret"></span>
                    </button>
                    <ul class="dropdown-menu text-left text-sm">
                        <li><a href="#">Filter 1</a></li>
                        <li><a href="#">Filter 2</a></li>
                        <li><a href="#">Filter 3</a></li>
                        <li><a href="#">Filter 4</a></li>
                    </ul>
                </div>


            </div>
        </header>
        <section class="scrollable hover">
            <ul class="list-group auto no-radius m-b-none m-t-n-xxs list-group-lg">
                <li class="list-group-item active">
                    <a href="#" class="thumb-sm pull-left m-r-sm">
                        <img src="resources/images/a0.png" class="img-circle">
                    </a>
                    <a href="#" class="clear text-ellipsis">
                        <small class="pull-right">[Date Created]</small>
                        <strong class="block">[Title]</strong>
                        <small>[Description]</small>
                    </a>
                </li>
                <li class="list-group-item">
                    <a href="#" class="thumb-sm pull-left m-r-sm">
                        <img src="resources/images/a1.png" class="img-circle">
                    </a>
                    <a href="#" class="clear text-ellipsis">
                        <small class="pull-right">[Date Created]</small>
                        <strong class="block">[Title]</strong>
                        <small>[Description]</small>
                    </a>
                </li>

                <li class="list-group-item">
                    <a href="#" class="thumb-sm pull-left m-r-sm">
                        <img src="resources/images/a1.png" class="img-circle">
                    </a>
                    <a href="#" class="clear text-ellipsis">
                        <small class="pull-right">[Date Created]</small>
                        <strong class="block">[Title]</strong>
                        <small>[Description]</small>
                    </a>
                </li>
                <li class="list-group-item">
                    <a href="#" class="thumb-sm pull-left m-r-sm">
                        <img src="resources/images/a1.png" class="img-circle">
                    </a>
                    <a href="#" class="clear text-ellipsis">
                        <small class="pull-right">[Date Created]</small>
                        <strong class="block">[Title]</strong>
                        <small>[Description]</small>
                    </a>
                </li>
                <li class="list-group-item">
                    <a href="#" class="thumb-sm pull-left m-r-sm">
                        <img src="resources/images/a1.png" class="img-circle">
                    </a>
                    <a href="#" class="clear text-ellipsis">
                        <small class="pull-right">[Date Created]</small>
                        <strong class="block">[Title]</strong>
                        <small>[Description]</small>
                    </a>
                </li>
                <li class="list-group-item">
                    <a href="#" class="thumb-sm pull-left m-r-sm">
                        <img src="resources/images/a1.png" class="img-circle">
                    </a>
                    <a href="#" class="clear text-ellipsis">
                        <small class="pull-right">[Date Created]</small>
                        <strong class="block">[Title]</strong>
                        <small>[Description]</small>
                    </a>
                </li>
                <li class="list-group-item">
                    <a href="#" class="thumb-sm pull-left m-r-sm">
                        <img src="resources/images/a1.png" class="img-circle">
                    </a>
                    <a href="#" class="clear text-ellipsis">
                        <small class="pull-right">[Date Created]</small>
                        <strong class="block">[Title]</strong>
                        <small>[Description]</small>
                    </a>
                </li>
                <li class="list-group-item">
                    <a href="#" class="thumb-sm pull-left m-r-sm">
                        <img src="resources/images/a1.png" class="img-circle">
                    </a>
                    <a href="#" class="clear text-ellipsis">
                        <small class="pull-right">[Date Created]</small>
                        <strong class="block">[Title]</strong>
                        <small>[Description]</small>
                    </a>
                </li>
                <li class="list-group-item">
                    <a href="#" class="thumb-sm pull-left m-r-sm">
                        <img src="resources/images/a1.png" class="img-circle">
                    </a>
                    <a href="#" class="clear text-ellipsis">
                        <small class="pull-right">[Date Created]</small>
                        <strong class="block">[Title]</strong>
                        <small>[Description]</small>
                    </a>
                </li>
                <li class="list-group-item">
                    <a href="#" class="thumb-sm pull-left m-r-sm">
                        <img src="resources/images/a1.png" class="img-circle">
                    </a>
                    <a href="#" class="clear text-ellipsis">
                        <small class="pull-right">[Date Created]</small>
                        <strong class="block">[Title]</strong>
                        <small>[Description]</small>
                    </a>
                </li>
                <li class="list-group-item">
                    <a href="#" class="thumb-sm pull-left m-r-sm">
                        <img src="resources/images/a1.png" class="img-circle">
                    </a>
                    <a href="#" class="clear text-ellipsis">
                        <small class="pull-right">[Date Created]</small>
                        <strong class="block">[Title]</strong>
                        <small>[Description]</small>
                    </a>
                </li>
            </ul>

            <a href="#" class="btn btn-default btn-md col-lg-12 m-b-xs"><i class="fa fa-repeat"></i> Lead More...</a>
        </section>
        <footer class="footer dk clearfix">
            <form class="m-t-sm">
                <div class="input-group">
                    <input type="text" class="input-sm form-control input-s-sm" placeholder="Search">
                    <div class="input-group-btn">
                        <button class="btn btn-sm btn-default"><i class="fa fa-search"></i></button>
                    </div>
                </div>
            </form>
        </footer>
    </section>
</aside>
<!-- /.aside -->
<!-- .aside -->
<aside id="email-content" class="bg-light lter">
    <section class="vbox">
        <section class="scrollable">
            <div class="wrapper dk  clearfix">
                <div class="pull-right inline">
                    <div class="btn-group">
                        <button class="btn btn-default btn-sm" data-toggle="tooltip" data-title="Edit"><i class="fa fa-pencil"></i></button>
                        <button class="btn btn-default btn-sm" data-toggle="tooltip" data-title="Forward"><i class="fa fa-share"></i></button>
                        <button class="btn btn-default btn-sm" data-toggle="tooltip" data-title="Lock"><i class="fa fa-lock"></i></button>
                        <button class="btn btn-default btn-sm dropdown-toggle" data-toggle="dropdown"><span class="caret"></span></button>
                        <ul class="dropdown-menu pull-right">
                            <li><a href="#">Other menu items</a></li>

                        </ul>

                    </div>
                    <a href="#chat" class="inline animated btn btn-default btn-sm " data-toggle="class:show"><i class="fa  fa-columns"></i></a>
                </div>
                <h4 class="m-n">[Title] ([ID])</h4>
            </div>
            <div>

                <div class="wrapper">
                    <p>[Complaint Details]</p>


                    <section class="panel panel-default">
                        <header class="panel-heading bg-light">
                            <ul class="nav nav-tabs nav-justified">
                                <li class="active"><a href="#notes" data-toggle="tab">Notes</a></li>
                                <li><a href="#documents" data-toggle="tab">Documents</a></li>
                                <li><a href="#tasks" data-toggle="tab">Tasks</a></li>
                                <li><a href="#history" data-toggle="tab">History</a></li>
                            </ul>
                        </header>
                        <div class="panel-body">
                            <div class="tab-content">
                                <div class="tab-pane active" id="notes">




                                    <ul class="list-group list-group-lg no-bg auto">
                                        <a href="#" class="list-group-item clearfix">
                            <span class="pull-left thumb-sm avatar m-r">
                              <img src="resources/images/a4.png" alt="John said">
                              <i class="on b-white bottom"></i>
                            </span>
                            <span class="clear">
                             <small class="text-muted pull-right">5m ago</small>
                              <span>Judy Hsu</span>
                              <small class="text-muted clear text-ellipsis">Sample notes go here.</small>
                            </span>
                                        </a>

                                    </ul>


                                    <div class="input-group">
                                        <input type="text" class="form-control input-sm " placeholder="Type in your notes here...">
                            <span class="input-group-btn">
                              <button type="submit" class="btn btn-default btn-sm"><i class="fa fa-arrow-right"></i></button>
                            </span>
                                    </div>




                                </div>
                                <div class="tab-pane" id="documents">Documents</div>
                                <div class="tab-pane" id="tasks">Tasks</div>
                                <div class="tab-pane" id="history">History</div>
                            </div>
                        </div>
                    </section>




                </div>
            </div>
            </div>

        </section>



    </section>
</aside>
<!-- /.aside -->


<aside class="aside bg-light lt hide" id="chat">
    <section class="vbox animated fadeInLeft">

        <section class="scrollable">



            <div class="list-group bg-white">
                <a href="#" class="list-group-item">
                    <span class="label bg-light">Status</span> [Status]
                </a>
                <a href="#" class="list-group-item">
                    <span class="label bg-light">Type</span> [Type]
                </a>
                <a href="#" class="list-group-item">
                    <span class="label bg-light">Assigned</span> [Assigned]
                </a>
                <a href="#" class="list-group-item">
                    <span class="label bg-light">Incident</span> [Date]
                </a>
                <a href="#" class="list-group-item">
                    <span class="label bg-light">Priority</span> [Priority]
                </a>
            </div>



            <header class="dk header">
                <p>Approvers</p>
            </header>
            <div class="list-group auto list-group-alt no-radius no-borders">
                <a class="list-group-item" href="#">
                    <i class="fa fa-fw fa-circle-o text-success text-xs"></i>
                    <span>James Bailey</span>
                </a>
            </div>

            <header class="dk header">
                <p>Collaborators</p>
            </header>
            <div class="list-group auto list-group-alt no-radius no-borders">
                <a class="list-group-item" href="#">
                    <i class="fa fa-fw fa-circle-o text-success text-xs"></i>
                    <span>James Bailey</span>
                </a>
            </div>



            <header class="dk header">
                <p>Watchers</p>
            </header>
            <div class="list-group auto list-group-alt no-radius no-borders">
                <a class="list-group-item" href="#">
                    <i class="fa fa-fw fa-circle-o text-success text-xs"></i>
                    <span>James Bailey</span>
                </a>
            </div>


        </section>

        <footer class="footer bg text-center">
            <button class="btn btn-light bg-empty btn-sm"><i class="fa fa-plus"></i> New Partipant</button>
        </footer>
    </section>
</aside>



</section>
</section>




</section>
</section>
</jsp:body>
</t:layout>



