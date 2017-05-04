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
    <title>Echo</title>
    <style type="text/css">
        #connect-container {
            float: left;
            width: 400px
        }

        #connect-container div {
            padding: 5px;
        }

        #console-container {
            float: left;
            margin-left: 15px;
            width: 400px;
        }

        #console {
            border: 1px solid #CCCCCC;
            border-right-color: #999999;
            border-bottom-color: #999999;
            height: 170px;
            overflow-y: scroll;
            padding: 5px;
            width: 100%;
        }

        #console p {
            padding: 0;
            margin: 0;
        }</style>
   
</head>
<body>
<div class="content">
  	<!-- Main bar -->
  	<div class="mainbar">
      <!-- Page heading -->
      <div class="page-head">
        <h2 class="pull-left"><i class="icon-table"></i>Echo</h2>
        <div class="clearfix"></div>
      </div>
      <!-- Page heading ends -->
	  	<!-- Matter -->
	    <div class="matter">
        <div class="container">
          <div class="row">
            <div class="col-md-12">
                <div class="widget-content">
                    <!-- Form starts.  -->
      				<div class="noscript"><h2 style="color: #ff0000">Seems your browser doesn't support Javascript! Websockets rely on Javascript being enabled. Please enable
    Javascript and reload this page!</h2></div>
			<div>
		    <div id="connect-container">
		        <div>
		            <span>Connect to service implemented using:</span>
		            <br/>
		            <!-- echo example using new programmatic API on the server side -->
		            <input id="radio1" type="radio" name="group1" value="/examples/websocket/echoProgrammatic"
		                   onclick="updateTarget(this.value);"/> <label for="radio1">programmatic API</label>
		            <br/>
		            <!-- echo example using new annotation API on the server side -->
		            <input id="radio2" type="radio" name="group1" value="/examples/websocket/echoAnnotation"
		                   onclick="updateTarget(this.value);"/> <label for="radio2">annotation API (basic)</label>
		            <br/>
		            <!-- echo example using new annotation API on the server side -->
		            <input id="radio3" type="radio" name="group1" value="/examples/websocket/echoStreamAnnotation"
		                   onclick="updateTarget(this.value);"/> <label for="radio3">annotation API (stream)</label>
		            <br/>
			        </div>
			        <div>
			            <input id="target" type="text" size="40" style="width: 350px"/>
			        </div>
			        <div>
			            <button id="connect" onclick="connect();">Connect</button>
			            <button id="disconnect" disabled="disabled" onclick="disconnect();">Disconnect</button>
			        </div>
			        <div>
			            <textarea id="message" style="width: 350px">Here is a message!</textarea>
			        </div>
			        <div>
			            <button id="echo" onclick="echo();" disabled="disabled">Echo message</button>
			        </div>
			    </div>
			    <div id="console-container">
			        <div id="console"/>
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
 <script type="text/javascript">
        "use strict";
        var ws = null;
        function setConnected(connected) {
            document.getElementById('connect').disabled = connected;
            document.getElementById('disconnect').disabled = !connected;
            document.getElementById('echo').disabled = !connected;
        }
        function connect() {
            var target = document.getElementById('target').value;
            if (target == '') {
                alert('Please select server side connection implementation.');
                return;
            }
            if ('WebSocket' in window) {
                ws = new WebSocket(target);
            } else if ('MozWebSocket' in window) {
                ws = new MozWebSocket(target);
            } else {
                alert('WebSocket is not supported by this browser.');
                return;
            }
            ws.onopen = function () {
                setConnected(true);
                log('Info: WebSocket connection opened.');
            };
            ws.onmessage = function (event) {
                log('Received: ' + event.data);
            };
            ws.onclose = function (event) {
                setConnected(false);
                log('Info: WebSocket connection closed, Code: ' + event.code + (event.reason == "" ? "" : ", Reason: " + event.reason));
            };
        }

        function disconnect() {
            if (ws != null) {
                ws.close();
                ws = null;
            }
            setConnected(false);
        }

        function echo() {
            if (ws != null) {
                var message = document.getElementById('message').value;
                log('Sent: ' + message);
                ws.send(message);
            } else {
                alert('WebSocket connection not established, please connect.');
            }
        }

        function updateTarget(target) {
            if (window.location.protocol == 'http:') {
            	var wsuri = "<%=socPath%>echo";  
                Chat.connect(wsuri);
            } else {
                document.getElementById('target').value = 'wss://' + window.location.host + target;
            }
        }

        function log(message) {
            var console = document.getElementById('console');
            var p = document.createElement('p');
            p.style.wordWrap = 'break-word';
            p.appendChild(document.createTextNode(message));
            console.appendChild(p);
            while (console.childNodes.length > 25) {
                console.removeChild(console.firstChild);
            }
            console.scrollTop = console.scrollHeight;
        }


        document.addEventListener("DOMContentLoaded", function() {
            // Remove elements with "noscript" class - <noscript> is not allowed in XHTML
            var noscripts = document.getElementsByClassName("noscript");
            for (var i = 0; i < noscripts.length; i++) {
                noscripts[i].parentNode.removeChild(noscripts[i]);
            }
        }, false);
        </script>
</body>
</html>
