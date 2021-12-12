package ru.bmstu.iu9.lab6;

import java.io.IOException;

public class AnonApp {
    public static void main(String[] args) throws IOException {
        new Server(Integer.parseInt(args[1]));
    }
}
