package ru.bmstu.iu9.lab6;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.server.Route;
import akka.stream.javadsl.Flow;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.stream.ActorMaterializer;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.concurrent.CompletionStage;

public class Server implements Watcher {

    private final static String AKKA_SYSTEM_NAME = "AkkaAnonimizer";
    private final static String HOST_NAME = "localhost";

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create(AKKA_SYSTEM_NAME);
        ActorRef config = system.actorOf(Props.create(ConfigActor.class));
        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);
        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = createRoute(config).flow(system, materializer)
        final CompletionStage<ServerBinding> binding = http.bindAndHandle(routeFlow,
                ConnectHttp.toHost(HOST_NAME, args[1]));
    }

    private static Route createRoute(ActorRef config) {

    }

    @Override
    public void process(WatchedEvent watchedEvent) {

    }
}
