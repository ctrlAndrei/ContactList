package com.phone;

import java.io.*;
import java.util.Map;
import java.util.TreeMap;

public class Main {

    public static void main(String[] args) throws IOException {

        Map<String, Contact> contacts = new TreeMap<>();
        String filePath = new File("").getAbsolutePath();
        BufferedReader in = new BufferedReader(new FileReader(filePath + "\\src\\com\\phone\\contacte.txt"));
        String[] line = in.readLine().split(","); // the header
        String str;

        while ((str = in.readLine()) != null) {
            line = str.split(",");
            contacts.put(line[0] + " " + line[1], new Contact(line[0], line[1], line[2], line[3]));
        }
        in.close();
        Agenda a = new Agenda(contacts);
        boolean exit = false;

        while (!exit) {
            a.print();
            exit = a.mainMenu();
        }

    }
}
