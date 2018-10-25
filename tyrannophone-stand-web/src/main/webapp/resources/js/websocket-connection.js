
const wsProtocol = "ws";
const wsURI = wsProtocol + '://' + window.location.host + window.location.pathname + "tyrannophoneEndpoint";

let webSocket = null;

function connect() {

    webSocket = new WebSocket(wsURI);

    webSocket.onopen = function () {
        console.log('Connection is opened.');
        document.getElementById('content').disabled = false;
    };

    webSocket.onmessage = function (event) {
        console.log('The response was received! ' + event.data);

        let content = document.getElementById('content');
        content.disabled = false;
        content.innerHTML = event.data;
    };

    webSocket.onerror = function (event) {
        console.log('Error! ' + event.data);
    };

    webSocket.onclose = function() {
        console.log("WebSocket closed.");
        document.getElementById('sayHello').disabled = true;
    };

}

document.onload = function () {
  connect();
};