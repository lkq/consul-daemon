package com.github.lkq.smesh.linkerd.routes.v1;

import com.github.lkq.smesh.server.Routes;
import spark.Service;

public class LinkerdRoutes implements Routes {
    @Override
    public void ignite(Service service) {
        service.get("/smesh-linkerd/v1/health", (req, res) -> "Hello");
    }
}
