

var theScope = 0;

function EventHandler($scope) {
	theScope = $scope;

	$scope.Status = "Welcome!";

	$scope.Arrived = {};
	$scope.Chart = [];





	$scope.Report = function(name) {
		var current = $scope.Arrived[name];
		console.log("Currently:", current);
		//$scope.Arrived[name] = !current;
		if (!current) {
			name = "!" + name;
		}
		$scope.socket.send(name);
	};

	window.onunload = function(e) {
		$scope.socket.close();
	};






	if(!("WebSocket" in window)) {
		alert("This will never work without websocks.");
		return;
	}

	$scope.socket = new WebSocket("ws://"+window.location.host+"/live-feed");
	$scope.socket.onmessage = function(event){
		$scope.$apply(function(){
			var msg = JSON.parse(event.data);
			console.log(msg);
			var done = false;
			if (typeof msg == "string") {
				console.log("Incoming announcement:", msg);
				if (msg.indexOf('!') == 0) {
					var name = msg.substring(1);
					$scope.Arrived[name] = false;
				} else {
					$scope.Arrived[msg] = true;
				}
				done = true;
			} else {
				for (name in msg) {
					console.log("assigning " + msg[name]);
					var table = msg[name];
					$scope.Chart.push({"name": name,
							   "table": table});
					$scope.Arrived[name] = false;
				}
				done = true;
			}
			if (!done) {
				$scope.Status = "Malformed message:  " + event.data;
			}
		});
	};

	$scope.socket.onclose = function(event){
		$scope.$apply(function(){
			$scope.Status = "Connection closed.  Please refresh.";
	       });
	};

	$scope.socket.onopen = function(){
		$scope.$apply(function(){
			$scope.Status = "";
		});
		console.log("Made a sock")
	};

}


