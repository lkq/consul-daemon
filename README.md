# Consul Daemon

A daemon process to manage consul docker container lifecycle and auto forming a cluster

in order to achieve the target, it uses:
- [docker-java](https://github.com/docker-java/docker-java) - a java library to manipulate docker container lifecycle
- [aws-java-sdk-ec2]() - AWS EC2 java SDK
- [consul]() - the official consul docker image


### Start / Stop
a new consul container will be created and started together with the daemon process startup, if the daemon detects a running consul container which was created by the same daemon version, it will attach the running container's log into the daemon application log

when the daemon being shutdown gracefully, a shutdown hook will be triggered to stop the consul container.

#### Running on AWS
requires tags / values on your instance, e.g:

    consul.role=<server | client>
    consul.nodeName=<unique node name>

#### Running on non-AWS
requires VM options:

    -Dconsul.role=<server | client>
    -Dconsul.cluster.member=<server list>

or environment variables:

    export consul.role=<server | client>
    export consul.cluster.member=<server list>



### Clustering
on startup, the daemon will lookup the cluster server node list and pass to consul with -retry-join

if running on AWS environment, the server node list contains all the instances have tag consul.role=server

if running on non-AWS environment, the server node list is get from VM option:

    -Dconsul.cluster.member=<space separated ip addresses>

or from environment variable:

    export consul.cluster.member=<space separated ip addresses>


### Health Check
exposed a simple health check end point:
http://localhost:8500/consul-daemon/v1/health

### Testing

tests annotated with @IntegrationTest requires docker to be installed

@IntegrationTest are ignored by default:

    mvn clean test

run all tests including @IntegrationTest

    mvn clean test -PtestAll

### Local Testing
- LocalLauncher: start a self bootstrap server node
- LocalClusterNode: start server nodes which will form a 3 nodes cluster, given below node-index provided to each instance:

        -Dnode-index=0
        -Dnode-index=1
        -Dnode-index=2