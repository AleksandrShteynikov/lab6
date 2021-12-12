package ru.bmstu.iu9.lab6;

import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ConfigActor extends AbstractActor {
    private List<String> ports = new ArrayList<>();
    Random random = new Random();

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(PortRequest.class, req -> {
                    getSender().tell(getRandom(), self());
                })
                .match(PortList.class, portList -> {
                    ports = portList.getPorts();
                })
                .build();
    }

    private String getRandom() {
        return ports.get(random.nextInt(ports.size()));
    }
}
