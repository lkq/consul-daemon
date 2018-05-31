package com.github.lkq.smesh.smesh4j;

import java.net.URI;

public class WebSocketClientFactory {

    WebSocketClient create(URI uri, String service, ReconnectListener reconnectListener) {
        return new WebSocketClient(uri, service, reconnectListener);
    }

}
