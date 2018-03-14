<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>ACM | ArkCase | User Interface</title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.2/jquery.min.js"></script>
    <script src="<%= request.getContextPath()%>/lib/bootstrap/dist/js/bootstrap.js"></script>

    <link rel="stylesheet" href="<%= request.getContextPath()%>/lib/bootstrap/dist/css/bootstrap.css">
    <link rel="stylesheet" href="<%= request.getContextPath()%>/assets/css/login.css">
    <!-- custom css-->
    <link rel="stylesheet" href="<%= request.getContextPath()%>/branding/customcss">

    <script type="text/javascript">

        function send() {
            var path = "<%= request.getContextPath()%>/mfa/getcode";
            var params = {
                "factorId": "${factor}"
            };

            $.ajax({
                type: "POST",
                contentType: "application/json",
                url: path,
                data: JSON.stringify(params),
                cache: false,
                timeout: 600000,
                success: function (data) {

                    $('#feedback').html(data);
                    $('#code-resent').show();
                    console.log("SUCCESS : ", data);

                },
                error: function (e) {

                    $('#feedback').html(e);
                    $('#code-resent').hide();
                    console.log("ERROR : ", e);

                }
            });
        }
    </script>
</head>
<body>

<div class="login-wrapper">
    <div class="logo">
        <img src="<%= request.getContextPath()%>/branding/loginlogo.png" style="max-width: 100%;">
    </div>
    <header class="text-center">
        <strong>Verify</strong>
    </header>

    <c:if test="${not empty error}">
        <div id="errorMessage" style="color: red">
                ${error}
        </div>
    </c:if>

    <div id="code-resent" class="alert alert-success" style="display:none;">A new code has been sent.</div>
    <form id="login-form" action="<%= request.getContextPath()%>/mfa/verify" method="post">

        <div class="form-group">
            <input id="passcode"
                   name="passcode"
                   type="number"
                   required="required"
                   placeholder="Enter code digits here"
                   class="form-control"/>
            <input id="userId" name="userId" value="${user}" hidden/>
            <input id="factorId" name="factorId" value="${factor}" hidden/>
        </div>
        <button id="submit" type="submit" class="btn btn-lg btn-primary btn-block">Verify</button>
        <p></p>
        <div class="pull-left">
            <a href="<c:url value="/logout"/>">Cancel</a>
        </div>
        <c:if test="${sendCode == true}">
            <div class="pull-right">
                <a onclick="send();" style="cursor:pointer;">Get New Code?</a>
            </div>
        </c:if>
    </form>
</div>

<footer id="footer">
    <div class="text-center padder">
        <p>
            <small><a
                    href="http://www.arkcase.com/"><span>ArkCase</span></a><br>&copy;<span>2014, 2015, 2016, 2017</span>
            </small>
        </p>
    </div>
</footer>
</body>
</html>
