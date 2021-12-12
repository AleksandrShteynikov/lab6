package ru.bmstu.iu9.lab6;

import akka.actor.AbstractActor;

import java.util.ArrayList;
import java.util.List;

public class ConfigActor extends AbstractActor {
    private List<String> ports = new ArrayList<>();

    @Override
    public Receive createReceive() {
        return null;
    }
}
