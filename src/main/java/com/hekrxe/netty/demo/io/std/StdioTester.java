package com.hekrxe.netty.demo.io.std;

import java.util.Scanner;

/**
 * @author tanhuayou on 2020/4/18
 */
public class StdioTester {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()){
            String in = scanner.nextLine();
            System.out.println(in);
        }

    }
}
