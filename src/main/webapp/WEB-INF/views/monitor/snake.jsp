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
    <title>pache Tomcat WebSocket Examples: Multiplayer Snake</title>  
    <style type="text/css">
        #playground {
            width: 640px;
            height: 480px;
            background-color: #000;
        }

        #console-container {
            float: left;
            margin-left: 15px;
            width: 300px;
        }

        #console {
            border: 1px solid #CCCCCC;
            border-right-color: #999999;
            border-bottom-color: #999999;
            height: 480px;
            overflow-y: scroll;
            padding-left: 5px;
            padding-right: 5px;
            width: 100%;
        }

        #console p {
            padding: 0;
            margin: 0;
        }
	</style>
</head>
<body>
<div class="content">
  	<!-- Main bar -->
  	<div class="mainbar">
      <!-- Page heading -->
      <div class="page-head">
        <h2 class="pull-left"><i class="icon-table"></i>爬虫大战</h2>
        <div class="clearfix"></div>
      </div>
      <!-- Page heading ends -->
	  	<!-- Matter -->
	    <div class="matter">
        <div class="container">
          <div class="row">
            <div class="col-md-12">
              <div class="widget wgreen">
                <div class="widget-content">
                    <!-- Form starts.  -->
      				<div class="noscript">
      				<h2 style="color: #ff0000">Seems your browser doesn't support Javascript! Websockets rely on Javascript being enabled. Please enable
				    Javascript and reload this page!</h2></div>
				    <div style="float: left">
				        <canvas id="playground" width="640" height="480"/>
				    </div>
				    <div id="console-container">
				        <div id="console"/>
				    </div>
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
        var Game = {};
        Game.fps = 30;
        Game.socket = null;
        Game.nextFrame = null;
        Game.interval = null;
        Game.direction = 'none';
        Game.gridSize = 10;
        function Snake() {
            this.snakeBody = [];
            this.color = null;
        }

        Snake.prototype.draw = function(context) {
            for (var id in this.snakeBody) {
                context.fillStyle = this.color;
                context.fillRect(this.snakeBody[id].x, this.snakeBody[id].y, Game.gridSize, Game.gridSize);
            }
        };

        Game.initialize = function() {
            this.entities = [];
            var canvas = document.getElementById('playground');
            if (!canvas.getContext) {
                Console.log('Error: 2d canvas not supported by this browser.');
                return;
            }
            this.context = canvas.getContext('2d');
            window.addEventListener('keydown', function (e) {
                var code = e.keyCode;
                if (code > 36 && code < 41) {
                    switch (code) {
                        case 37:
                            if (Game.direction != 'east') Game.setDirection('west');
                            break;
                        case 38:
                            if (Game.direction != 'south') Game.setDirection('north');
                            break;
                        case 39:
                            if (Game.direction != 'west') Game.setDirection('east');
                            break;
                        case 40:
                            if (Game.direction != 'north') Game.setDirection('south');
                            break;
                    }
                }
            }, false);
            if (window.location.protocol == 'http:') {
            	var wsuri = "<%=socPath%>snake";  
                Game.connect(wsuri);
            } else {
                Game.connect('wss://' + window.location.host + '/examples/websocket/snake');
            }
        };

        Game.setDirection  = function(direction) {
            Game.direction = direction;
            Game.socket.send(direction);
            Console.log('Sent: Direction ' + direction);
        };

        Game.startGameLoop = function() {
            if (window.webkitRequestAnimationFrame) {
                Game.nextFrame = function () {
                    webkitRequestAnimationFrame(Game.run);
                };
            } else if (window.mozRequestAnimationFrame) {
                Game.nextFrame = function () {
                    mozRequestAnimationFrame(Game.run);
                };
            } else {
                Game.interval = setInterval(Game.run, 1000 / Game.fps);
            }
            if (Game.nextFrame != null) {
                Game.nextFrame();
            }
        };

        Game.stopGameLoop = function () {
            Game.nextFrame = null;
            if (Game.interval != null) {
                clearInterval(Game.interval);
            }
        };

        Game.draw = function() {
            this.context.clearRect(0, 0, 640, 480);
            for (var id in this.entities) {
                this.entities[id].draw(this.context);
            }
        };

        Game.addSnake = function(id, color) {
            Game.entities[id] = new Snake();
            Game.entities[id].color = color;
        };

        Game.updateSnake = function(id, snakeBody) {
            if (typeof Game.entities[id] != "undefined") {
                Game.entities[id].snakeBody = snakeBody;
            }
        };

        Game.removeSnake = function(id) {
            Game.entities[id] = null;
            // Force GC.
            delete Game.entities[id];
        };

        Game.run = (function() {
            var skipTicks = 1000 / Game.fps, nextGameTick = (new Date).getTime();

            return function() {
                while ((new Date).getTime() > nextGameTick) {
                    nextGameTick += skipTicks;
                }
                Game.draw();
                if (Game.nextFrame != null) {
                    Game.nextFrame();
                }
            };
        })();

        Game.connect = (function(host) {
            if ('WebSocket' in window) {
                Game.socket = new WebSocket(host);
            } else if ('MozWebSocket' in window) {
                Game.socket = new MozWebSocket(host);
            } else {
                Console.log('Error: WebSocket is not supported by this browser.');
                return;
            }

            Game.socket.onopen = function () {
                // Socket open.. start the game loop.
                Console.log('Info: WebSocket connection opened.');
                Console.log('Info: Press an arrow key to begin.');
                Game.startGameLoop();
                setInterval(function() {
                    // Prevent server read timeout.
                    Game.socket.send('ping');
                }, 5000);
            };

            Game.socket.onclose = function () {
                Console.log('Info: WebSocket closed.');
                Game.stopGameLoop();
            };

            Game.socket.onmessage = function (message) {
                var packet = JSON.parse(message.data);
                switch (packet.type) {
                    case 'update':
                        for (var i = 0; i < packet.data.length; i++) {
                            Game.updateSnake(packet.data[i].id, packet.data[i].body);
                        }
                        break;
                    case 'join':
                        for (var j = 0; j < packet.data.length; j++) {
                            Game.addSnake(packet.data[j].id, packet.data[j].color);
                        }
                        break;
                    case 'leave':
                        Game.removeSnake(packet.id);
                        break;
                    case 'dead':
                        Console.log('Info: Your snake is dead, bad luck!');
                        Game.direction = 'none';
                        break;
                    case 'kill':
                        Console.log('Info: Head shot!');
                        break;
                }
            };
        });

        var Console = {};

        Console.log = (function(message) {
            var console = document.getElementById('console');
            var p = document.createElement('p');
            p.style.wordWrap = 'break-word';
            p.innerHTML = message;
            console.appendChild(p);
            while (console.childNodes.length > 25) {
                console.removeChild(console.firstChild);
            }
            console.scrollTop = console.scrollHeight;
        });

        Game.initialize();


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
  