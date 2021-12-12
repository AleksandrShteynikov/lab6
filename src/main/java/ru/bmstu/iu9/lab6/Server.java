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

import java.io.IOException;
import java.util.concurrent.CompletionStage;

import static akka.http.javadsl.server.Directives.*;

public class Server implements Watcher {

    private final static String AKKA_SYSTEM_NAME = "AkkaAnonimizer";
    private final static String HOST_NAME = "localhost";
    private final static String SERVER_MSG = "Server online at http://" + HOST_NAME + ":";
    private final static String URL_EXT = "";

    public static void main(String[] args) throws IOException {
        ActorSystem system = ActorSystem.create(AKKA_SYSTEM_NAME);
        ActorRef config = system.actorOf(Props.create(ConfigActor.class));
        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);
        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = createRoute(config).flow(system, materializer)
        int Port = Integer.parseInt(args[1]);
        final CompletionStage<ServerBinding> binding = http.bindAndHandle(routeFlow,
                ConnectHttp.toHost(HOST_NAME, Port), materializer);
        System.out.println(SERVER_MSG + Port);
        System.in.read();
        binding.thenCompose(ServerBinding::unbind).thenAccept(unbound -> system.terminate());
    }

    private static Route createRoute(ActorRef config) {
        return route(
                path(URL_EXT, () -> route(get(() -> parameter())))
        );
    }

    @Override
    public void process(WatchedEvent watchedEvent) {

    }
}
