package ru.bmstu.iu9.lab6;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.server.Route;
import akka.pattern.Patterns;
import akka.stream.javadsl.Flow;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.stream.ActorMaterializer;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;

import static akka.http.javadsl.server.Directives.*;

public class Server implements Watcher {

    private final static int TIMEOUT = 5000;
    private final static String KEEPER_PATH = "/servers";
    private final static String AKKA_SYSTEM_NAME = "AkkaAnonimizer";
    private final static String KEEPER_SERVER = "localhost:2181";
    private final static String HOST_NAME = "localhost";
    private final static String DELIMITER = ":";
    private final static String SCHEMA = "http://";
    private final static String QUERY_DELIM = "&";
    private final static String QUERY_START = "/?";
    private final static String QUERY_ASSIGN = "=";
    private final static String SERVER_MSG = "Server online at http://" + HOST_NAME + ":";
    private final static String URL_EXT = "";
    private final static String URL_QUERY_KEY = "testUrl";
    private final static String COUNT_QUERY_KEY = "count";

    private ZooKeeper zoo;
    private ActorRef config;

    public void main(String[] args) throws IOException {
        ActorSystem system = ActorSystem.create(AKKA_SYSTEM_NAME);
        config = system.actorOf(Props.create(ConfigActor.class));
        zoo = new ZooKeeper(KEEPER_SERVER, TIMEOUT, this);
        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);
        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = createRoute(http, config).flow(system, materializer);
        int Port = Integer.parseInt(args[1]);
        final CompletionStage<ServerBinding> binding = http.bindAndHandle(routeFlow,
                ConnectHttp.toHost(HOST_NAME, Port), materializer);
        System.out.println(SERVER_MSG + Port);
        System.in.read();
        binding.thenCompose(ServerBinding::unbind).thenAccept(unbound -> system.terminate());
    }

    private static Route createRoute(Http http, ActorRef config) {
        return route(
                path(URL_EXT, () -> route(get(() -> parameter(URL_QUERY_KEY, url ->
                        parameter(COUNT_QUERY_KEY, count -> {
                            int numOfRedir = Integer.parseInt(count);
                            if (numOfRedir == 0) {
                                return completeWithFuture(http.singleRequest(HttpRequest.create(url)));
                            } else {
                                return completeWithFuture(Patterns
                                        .ask(config, new PortRequest(), java.time.Duration.ofMillis(TIMEOUT))
                                        .thenCompose(port -> http.singleRequest(HttpRequest.create(composeRequest((String)port,
                                                                                                                   url,
                                                                                                             numOfRedir - 1)))));
                            }
                        })))))
        );
    }

    private static String composeRequest(String port, String url, int count) {
        return SCHEMA + HOST_NAME + DELIMITER + port + QUERY_START + URL_QUERY_KEY + QUERY_ASSIGN
                + url + QUERY_DELIM + COUNT_QUERY_KEY + QUERY_ASSIGN + count;
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        try {
            List<String> ports = new ArrayList<>(zoo.getChildren(KEEPER_PATH, this));
            config.tell(new PortList(ports), ActorRef.noSender());
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
