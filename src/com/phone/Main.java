package com.phone;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {

    private static Scanner kb = new Scanner(System.in);

    public static void main(String[] args) {

        Agenda a = new Agenda("contacts.txt");
        boolean exit = false;

        while (!exit) {
            a.print();
            exit = mainMenu(a);
        }
    }


    public static boolean mainMenu(Agenda agenda) {
        boolean exit = false;
        System.out.println("\nAlegeti o persoana dupa index sau cautati dupa literele introduse");
        System.out.println("Comenzi suplimentare: -add, -delete, -edit, -backup, -revert(to prev. version), -exit");

        String select = kb.nextLine();
        boolean showContact = !(select.equals("add") || select.equals("delete") || select.equals("exit") ||
                select.equals("edit") || select.equals("backup") || select.equals("revert"));

        /*Afisare dupa index/cuvinte introduse*/

        if (!Character.isDigit(select.charAt(0)) && showContact) {

            agenda.printContacts(agenda.searchByName(select));
            kb.nextLine();
        } else if (showContact) {
            List<Contact> list = agenda.searchByIndex(Integer.parseInt(select));
            Optional<Contact> contactOptional = agenda.contactInfo(Integer.parseInt(select));
            if (contactOptional.isPresent()) {
                System.out.println(contactOptional.get());
            }
            System.out.println("Apasati enter pentru a va intoarce la meniul anterior");
            kb.nextLine();
        }

        /* Comenzi Suplimentare : */

        if (select.equals("add")) {
            agenda.addContact(true, kb);
        }
        if (select.equals("edit")) {
            agenda.addContact(false, kb);
        }

        if (select.equals("delete")) {
            agenda.delContact(kb);
        }

        if (select.equals("backup")) {
            agenda.backupData();
        }

        if (select.equals("revert")){
            agenda.returnPreviousVersion(kb);
            kb.nextLine();
        }

        if (select.equals("exit")) {
            exit = true;
        }
        return exit;
    }
}
