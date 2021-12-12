package ru.bmstu.iu9.lab6;

import java.util.List;

public class PortList {
    private final List<String> ports;

    public PortList(List<String> ports) {
        this.ports = ports;
    }

    public List<String> getPorts() {
        return ports;
    }
}
