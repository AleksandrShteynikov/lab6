package ru.bmstu.iu9.lab6;

import org.apache.zookeeper.KeeperException;

import java.io.IOException;

public class AnonApp {
    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        new Server(Integer.parseInt(args[1]));
        //new Server(8000);
    }
}
