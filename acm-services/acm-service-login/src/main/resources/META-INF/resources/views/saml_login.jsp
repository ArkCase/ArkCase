<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>ACM | ArkCase | User Interface</title>

    <!-- Set the hash in localStorage, so when the user logs in the Angular application opens that state -->
    <script type="text/javascript">
	    function addUrlHashToLocalStorageAndRedirectToSamlLogin() {
	        if (window.location.hash != '#!/welcome' && window.location.hash != '#!/goodbye') {
				//localStorage.redirectURL = window.location.hash;
				sessionStorage.redirectURL = window.location.hash;
	        } else {
				//localStorage.removeItem('redirectURL');
				sessionStorage.removeItem('redirectURL');
	        }
	        // redirect to real SAML login page that actually authenticates the user
	        window.location.href = "<%= request.getContextPath()%>/saml/login";
	    }
	    window.onload = addUrlHashToLocalStorageAndRedirectToSamlLogin;
    </script>
    <!-- custom css-->
    <link rel="stylesheet" href="<%= request.getContextPath()%>/branding/customcss">
</head>
<body>
	<!-- This page is only used to save the hash of a not authorized request in local storage -->
</body>
</html>
