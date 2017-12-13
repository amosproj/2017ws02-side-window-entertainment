package de.tuberlin.amos.ws17.swit.gps;

import org.apache.jena.base.Sys;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.logging.*;

public class ConsoleFilterText {

    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out) {
            public void println(String s) {
                if (s != "Test Message2") {
                    super.println(s);
                }
            }

            public void print(String s) {
                super.print(s);
            }
        });

        System.out.println("Test Message1");
        System.out.println("Test Message2");
        System.out.println("Test Message3");
    }
}
