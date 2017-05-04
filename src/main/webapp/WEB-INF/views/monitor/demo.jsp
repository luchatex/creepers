<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt"  uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%  
    String path = request.getContextPath();  
    String socPath="ws://"+request.getServerName()+":"+request.getServerPort()+path+"/";  
%> 
<!DOCTYPE html>  
<html> 
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">  
    <title>websocketIndex</title>  
    <script type="text/javascript">  
        var wsuri = "<%=socPath%>getServer";  
        var ws = null;  
        function startWebSocket() {  
            if ('WebSocket' in window)  
                ws = new WebSocket(wsuri);  
            else if ('MozWebSocket' in window)  
                ws = new MozWebSocket(wsuri);  
            else  
                console.error("not support WebSocket!");  
            ws.onmessage = function(evt) {  
                alert(evt.data);  
                console.info(evt);  
            };  
  
            ws.onclose = function(evt) {  
                  
                alert("close");  
                console.info(evt);  
            };  
  
            ws.onopen = function(evt) {  
                alert("open");  
                console.info(evt);  
            };  
        };  
          
        function init(){  
            startWebSocket();  
        };  
        init();  
         
  
        function sendMsg(){           
            ws.send(document.getElementById('writeMsg').value);  
        }  
    </script>  
</head> 
<body>
<div class="content">
  	<!-- Main bar -->
  	<div class="mainbar">
      <!-- Page heading -->
      <div class="page-head">
        <h2 class="pull-left"><i class="icon-table"></i> Mong List</h2>
        <div class="clearfix"></div>
      </div>
      <!-- Page heading ends -->
	  	<!-- Matter -->
	    <div class="matter">
        <div class="container">
          <div class="row">
            <div class="col-md-12">
              <div class="widget wgreen">
                <div class="widget-head">
                  <div class="pull-left">用户查询</div>
                  <div class="widget-icons pull-right">
                    <a href="#" class="wminimize"><i class="icon-chevron-up"></i></a> 
                    <a href="#" class="wclose"><i class="icon-remove"></i></a>
                  </div>
                  <div class="clearfix"></div>
                </div>

                <div class="widget-content">
                  <div class="padd">
                    <!-- Form starts.  -->
      				<input type="text" id="writeMsg"/>   
					<input type="button" value="sendSmgToServer" onclick="sendMsg()"/>  
					<br>  
					<span>  
					wait 8 second,server will send you a msg!  
					</span> 
                  </div>
                </div>
                <div class="widget-foot">
			    </div>
              </div>  
            </div>
      </div>
		<!-- Matter ends -->
    </div>
   <!-- Mainbar ends -->
   <div class="clearfix"></div>
</div>
</div>
</div>
</body>
</html>
</script>