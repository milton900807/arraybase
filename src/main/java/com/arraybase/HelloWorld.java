package com.arraybase;

import com.arraybase.shell.Command;

public class HelloWorld {
    @Command
    public int add(int a, int b) {
        return a + b;
    }

    @Command
    public String echo(String s) {
        return s;
    }
}