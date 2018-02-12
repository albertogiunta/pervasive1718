// GUI elements
var url, username, password, virtualhost, connectBut, disconnectBut;
var consumeExchange;
var logConsole, clearBut, receivedMessageCount;

// A factory for creating AMQP clients and the AMQP client used.
var amqpClientFactory, amqpClient;

// Attributes used by the regular (non-transactional) channel.
var queueName, myConsumerTag, routingKey, consumeChannel;

// Track the number of messages consumed.
var receivedMessageCounter;

var graphs = [];

$(document).ready(function () {

	// Create references to GUI objects.

	url = "ws://localhost:8000/amqp";
	username = "pervasive";
	password = "zeronegativo";
	virtualhost = "/";
	routingKey = "broadcastkey";
	consumeExchange = ["DIA", "EtCO2", "HR", "SYS", "SpO2", "T"];

	//connectBut = $("#connectBut");
	//disconnectBut = $("#disconnectBut");

	//logConsole = $("div#console");
	//receivedMessageCount = $("#receivedMessageCount");
	//clearBut = $("#clearBut");

	// Add event handlers.
	//connectBut.click(handleConnect);
	//disconnectBut.click(handleDisconnect);
	//clearBut.click(handleClearLog);

	receivedMessageCounter = 0;

	// Add trim() to string, if not present.
	if (!String.prototype.trim) {
		String.prototype.trim = function () {
			// Make sure we trim BOM and NBSP
			rtrim = /^[\s\uFEFF\xA0]+|[\s\uFEFF\xA0]+$/g;
			return this.replace(rtrim, "");
		}
	}

	amqpClientFactory = new AmqpClientFactory();

	// Creating the WebSocketFactory once and decorating it as desired lets
	// you reuse it for multiple AMQP clients.
	var webSocketFactory = createWebSocketFactory();
	amqpClientFactory.setWebSocketFactory(webSocketFactory);

	window.onload = handleConnect();
});

// Event handler when the user clicks the Connect button to establish a connection
// to Kaazing Gateway.
//
var handleConnect = function () {
	//connectBut.prop("disabled", true);
	//log("CONNECTING: " + url + " " + username);

	queueName = "queue" + Math.floor(Math.random() * 1000000);

	amqpClient = amqpClientFactory.createAmqpClient();
	/*amqpClient.addEventListener("close", function () {
		log("DISCONNECTED");
		updateGuiState(false);
	});
	amqpClient.addEventListener("error", function (e) {
		log("CONNECTION ERROR:" + e.message);
		connectBut.prop("disabled", false);
	});*/

	var credentials = {username: username, password: password};
	var options = {
		url: url,
		virtualHost: virtualhost,
		credentials: credentials
	};
	amqpClient.connect(options, openHandler);
}

// Event handler invoked when the connection is successfully made.
//
var openHandler = function () {
	// log("CONNECTED");
	// updateGuiState(true);
	// log("OPEN: Consume Channel");
	consumeChannel = amqpClient.openChannel(consumeChannelOpenHandler);
};

// Event handler when the consume channel is opened.
//
var consumeChannelOpenHandler = function (channel) {
	// log("OPENED: Consume Channel");

	consumeChannel.addEventListener("message", function (message) {
		handleMessageReceived(message);
	});

	// The default value for noAck is true. Passing a false value for 'noAck' in
	// the AmqpChannel.consumeBasic() function means there should be be explicit
	// acknowledgement when the message is received. If set to true, then no
	// explicit acknowledgement is required when the message is received.
	
	consumeExchange.forEach(function(exchangeName) {
		myConsumerTag = "client" + Math.floor(Math.random() * 1000000);
		consumeChannel.declareQueue({queue: queueName})
			.bindQueue({queue: queueName, exchange: exchangeName, routingKey: routingKey})
			.consumeBasic({queue: queueName, consumerTag: myConsumerTag, noAck: false});
	});
};

// Event handler when the disconnect button is pressed.
//
var handleDisconnect = function () {
	// log("DISCONNECT");
	consumeChannel.closeChannel(consumeChannel);
	amqpClient.disconnect();
	console.log("handler has been called")
	
}

// Event handler when a message has been received from the gateway.
//
var handleMessageReceived = function (event) {

	// receivedMessageCount.text(++receivedMessageCounter);

	var body = null;

	// Check how the payload was packaged since older browsers like IE7 don't
	// support ArrayBuffer. In those cases, a Kaazing ByteBuffer was used instead.
	if (typeof(ArrayBuffer) === "undefined") {
		body = event.getBodyAsByteBuffer().getString(Charset.UTF8);
	}
	else {
		body = arrayBufferToString(event.getBodyAsArrayBuffer())
	}
	var exchange = event.args.exchange;

	var curGraph = graphs.filter(graph => graph.channel === exchange)[0];
	if (curGraph !== undefined)
		curGraph.setData(body);
	else {
		var classId = "#"+exchange;
		$(classId).text(body);
	}
	// log("MESSAGE FROM " + exchange + ": " + body);
}

// Create a WebSocketFactory which can be used for multiple AMQP clients if
// required. This lets you defined the attributes of a WebSocket connection
// just once – such as a ChallengeHandler – and reuse it.
//
// <<<<< IF POSSIBLE, REMOVE THIS USELESS AUTHENTICATOR >>>>
var createWebSocketFactory = function () {
	webSocketFactory = new WebSocketFactory();

	// Add a BasicChallengeHandler in case the service has enabled basic authentication.
	basicHandler = new BasicChallengeHandler();
	basicHandler.loginHandler = function (callback) {
		// Yeah, nobody needs to hide this stuff.
		var credentials = new PasswordAuthentication("admin", "admin");
		callback(credentials);
	}
	webSocketFactory.setChallengeHandler(basicHandler);
	return webSocketFactory;
}

// Event handler when the user presses the clear log button.
//
/*var handleClearLog = function () {
	logConsole.empty();
}

// Log a string message to the log console pane.
//
var log = function (message) {
	var div = $('<div>');
	div.addClass("logMessage");
	div.html(message);
	logDiv(div);
}

// Write a div that's in the correct form to the log console pane.
//
var logDiv = function (div) {
	logConsole.append(div);

	// Make sure the last line is visible.
	logConsole.scrollTop(logConsole[0].scrollHeight);

	// Only keep the most recent few rows so the log doesn't grow out of control.
	while (logConsole.children().length > 40) {
		// Delete two rows to preserve the alternate background colors.
		logConsole.children().first().remove();
		logConsole.children().first().remove();
	}
}
*/
// Convert a string to an ArrayBuffer.
//
var stringToArrayBuffer = function (str) {
	var buf = new ArrayBuffer(str.length);
	var bufView = new Uint8Array(buf);
	for (var i = 0, strLen = str.length; i < strLen; i++) {
		bufView[i] = str.charCodeAt(i);
	}
	return buf;
}

// Convert an ArrayBuffer to a string.
//
var arrayBufferToString = function (buf) {
	return String.fromCharCode.apply(null, new Uint8Array(buf));
}


// Returns a random integer between min (inclusive) and max (exclusive).
//
function getRandomInt(min, max) {
	return Math.floor(Math.random() * (max - min)) + min;
}

// Enable or disable buttons on the screen based on whether we are currently
// connected or not.
/*var updateGuiState = function (connected) {
	connectBut.prop("disabled", connected);
	disconnectBut.prop("disabled", !connected);
}*/


var printConnectionStatus = function() {
	console.log(amqpClient)
}

//setInterval(printConnectionStatus, 1000)

graphs.push(new Graph("Battito cardiaco", "HR", 0, 220, "black"));
//graphs.push(new Graph("Temperatura", "T", 0, 45, "red"));
graphs.push(new Graph("Pressione sistolica", "SYS", 0, 230, "blue"));
graphs.push(new Graph("Pressione diastolica", "DIA", 0, 150, "green"));
//graphs.push(new Graph("Saturazione ossigeno", "SpO2", 0, 100, "purple"));
//graphs.push(new Graph("Fine respirazione CO2", "EtCO2", 0, 15, "gray"));