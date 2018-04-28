#!/usr/bin/env bash

docker run --name linkerd -p 9990:9990 -v `pwd`/config.yaml:/config.yaml buoyantio/linkerd:1.3.7 /config.yaml
