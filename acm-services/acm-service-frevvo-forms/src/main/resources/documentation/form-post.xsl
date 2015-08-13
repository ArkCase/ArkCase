<?xml version="1.0" encoding="UTF-8"?>
<xsl:transform version="2.0" 
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	    exclude-result-prefixes="servlet fn"
	    xmlns:fn="java:com.gauss.ecore.EcoreXPathFunctions"
        xmlns:servlet="http://servlet.javax"
        xmlns:futil="java:com.frevvo.locale.FrevvoLocaleResolver">
    <xsl:import href="../core/globals.xsl"/>
    <xsl:import href="../core/servlet.xsl"/>
    
    <xsl:param name="url"/>
    <xsl:param name="window">top</xsl:param>
    <xsl:param name="documentSet" as="item()*"></xsl:param>
    <xsl:param name="approvalsetupform">false</xsl:param>

    <!-- Page used to redirect top/parent window -->
    <xsl:template match="/">
        <xsl:result-document method="xhtml" indent="yes"
        				doctype-public="-//W3C//DTD XHTML 1.1//EN"
        				doctype-system="http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
	        <html xmlns="http://www.w3.org/1999/xhtml">
	            <head>
	                <title><xsl:value-of select="$frevvo.page.title"/></title>
			        <script type="text/javascript" src="{$js-url}/browserdocs.js"></script>
	                <script type="text/javascript">
	                	function redirect(){
						  	<xsl:choose>
						  		<xsl:when test="$url">
						  			<xsl:value-of select="$window"/>.location = document.getElementById('form-post').getAttribute('redirectUrl');
						  		</xsl:when>
						  		<xsl:otherwise>
								
								    // This code section resolves AFDP-1283 by redirecting back to the correct ACM plugin page when a Frevvo form is submitted
									
									// Reads the url arguments passed by ACM (including the redirect url back to ACM) from an xslt property
								    <![CDATA[var queryStringResource = "]]><xsl:value-of select="$com.frevvo.servlet.forward.query_string"/><![CDATA[";]]>
									
									//The xsl:text and CDATA combination is needed to force xslt to output unescaped JS code without errors
									<xsl:text disable-output-escaping="yes"><![CDATA[
									
									// Obtains the ACM redirect url portion of the query string
								    var urlArray = queryStringResource.split('&');
									var acmRedirectUrl = false;
									for (var i = 0; i < urlArray.length; i++) {
									    if (urlArray[i].indexOf('referrer') >= 0) {
										    acmRedirectUrl = urlArray[i].split('=')[1];
											break;
										}
									}
									
									if (acmRedirectUrl) {
									    // Replaces the special encoded characters like %2F with decoded values like / to allow it to work
									    acmRedirectUrl = unescape(acmRedirectUrl);
									
									    // We need to remove the /wizard portion of the url in order to redirect back to the main plugin page itself
									    acmRedirectUrl = acmRedirectUrl.replace('/wizard', '');
									
									    // Redirects the parent window (the main ACM page in which the iframe is located) 
									    // back to the plugin homepage associated with the form type which was submitted
									    try {
									        window.open(acmRedirectUrl, '_parent');
									    } catch (e) {
									        ;
									    }
									}
									
									]]></xsl:text>
								
						  			try {
						  			     parent._frevvo.lightBoxView.dismiss();
						  			} catch(e){}
						  		</xsl:otherwise>
						  	</xsl:choose>
	                	}
	                </script>
	            </head>
	            
	            <body id="form-post" onload="formAction(); redirect();" redirectUrl="{$url}">
	            	<xsl:if test="$documentSet">
						<form style="display:none;">
			               	<xsl:for-each select="$documentSet/*/documents">
		 						<textarea id="{@id}" namespace="{rootElement/@namespace}" element="{rootElement/@name}" class="f-document">
		 							<xsl:if test="@name">
		 								<xsl:attribute name="name" select="@name" />
		 							</xsl:if>
		 							<xsl:value-of select="fn:serialize(.)"/>
		 						</textarea>
			               	</xsl:for-each>
						</form>            	
					</xsl:if>
	            </body>
	        </html>
		</xsl:result-document>
    </xsl:template>
    
</xsl:transform>