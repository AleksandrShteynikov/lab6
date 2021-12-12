package ru.bmstu.iu9.lab6;

import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;

import java.util.ArrayList;
import java.util.List;

public class ConfigActor extends AbstractActor {
    private List<String> ports = new ArrayList<>();

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(PortRequest.class, req -> {
                    getSender().tell(getRandom(), self());
                })
                .match(List.class, portList -> {
                    ports = portList;
                })
                .build();
    }

    private String getRandom() {
        return null;
    }
}
