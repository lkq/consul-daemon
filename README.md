# Smesh

a service mesh implementation using linkerd and consul

## Consul Daemon

A daemon process to manage consul docker container lifecycle and clustering
in order to achieve the target, it uses:
- [docker-java](https://github.com/docker-java/docker-java) - a java library to manipulate docker container lifecycle
- [aws-java-sdk-ec2](https://github.com/aws/aws-sdk-java) - AWS EC2 java SDK
- [consul](https://hub.docker.com/_/consul/) - the official consul docker image


### Start / Stop
a new consul container will be created and started together with the daemon process startup, if the daemon detects a running consul container which was created by the same daemon version, it will attach the running container's log into the daemon's application log

when the daemon being shutdown gracefully, a shutdown hook will be triggered to stop the consul container.

#### Running on EC2 environment
requires tags / values on your instance, e.g:

    consul.role=<server | client>
    consul.nodeName=<unique node name>

#### Running on non EC2 environment
requires VM options:

    -Dconsul.role=<server | client>
    -Dconsul.cluster.member=<server list>

or environment variables:

    export consul.role=<server | client>
    export consul.cluster.member=<server list>



### Clustering
on startup, the daemon will lookup the cluster server nodes and pass to consul with -retry-join

if running on EC2 environment, the server node list contains all the instances that tagged with consul.role=server

if running on non EC2 environment, the server node list is get from VM option:

    -Dconsul.cluster.member=<space separated ip addresses>

or environment variable:

    export consul.cluster.member=<space separated ip addresses>


### Health Check
exposed a simple health check end point:
http://localhost:1026/smesh-consul/v1/health

### Testing

tests annotated with @IntegrationTest requires docker to be installed

tests with @IntegrationTest are ignored by default:

    mvn clean test

run all tests including @IntegrationTest

    mvn clean test -PtestAll

### Local Testing
- LocalLauncher: start a self bootstrap server node
- LocalClusterNode: start server nodes which will retry-join to form a cluster, which requires minimum of 3 nodes, e.g, provide different node-index to each node by:

        -Dnode-index=0
        -Dnode-index=1
        -Dnode-index=2