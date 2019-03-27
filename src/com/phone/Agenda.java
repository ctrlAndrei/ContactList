package com.phone;

import com.phone.Exceptions.InvalidEmail;
import com.phone.Exceptions.InvalidName;
import com.phone.Exceptions.InvalidNumber;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Agenda {
    private SortedMap<String, Contact> contacts = new TreeMap<>();
    private Scanner kb = new Scanner(System.in);

    public Agenda() throws IOException {
    }

    public Agenda(Map<String, Contact> contacts) throws IOException {
        this.contacts.putAll(contacts);
    }

    public void print() {
        AtomicInteger atomicInteger = new AtomicInteger();
        System.out.println("Contacte:");
        contacts.keySet().stream().forEach(el -> {
            System.out.println(atomicInteger.incrementAndGet() + ". " + el);
        });
    }

    public List<Contact> searchByName(String name) {
        return contacts.entrySet().stream()
                .filter(s -> s.getKey().contains(name))
                .map(stringInfoEntry -> stringInfoEntry.getValue())
                .collect(Collectors.toList());
    }

    public List<Contact> searchByIndex(int index) {
        AtomicInteger atomicInteger = new AtomicInteger();
        return contacts.entrySet().stream()
                .filter(el -> index == atomicInteger.incrementAndGet())
                .map(stringInfoEntry -> stringInfoEntry.getValue())
                .collect(Collectors.toList());
    }

    public boolean mainMenu() throws IOException {
        boolean exit = false;
        System.out.println("\nAlegeti o persoana dupa index sau cautati dupa literele introduse");
        System.out.println("Comenzi suplimentare: -add, -delete, -edit, -exit");

        String select = kb.nextLine();
        boolean showContact = !(select.equals("add") || select.equals("delete") || select.equals("exit") || select.equals("edit"));

        /*Afisare dupa index/cuvinte introduse*/

        if (!Character.isDigit(select.charAt(0)) && showContact) {

            showFilteredList(searchByName(select));
        } else if (showContact) {
            List<Contact> list = searchByIndex(Integer.parseInt(select));
            contactInfo(Integer.parseInt(select));
        }

        /* Comenzi Suplimentare : */

        if (select.equals("add")) {
            this.addContact(true);
        }
        if (select.equals("edit")) {
            this.addContact(false);
        }

        if (select.equals("delete")) {
            this.delContact();
        }

        if (select.equals("exit")) {
            exit = true;
        }
        return exit;
    }

    public void contactInfo(int index) {
        AtomicInteger atomicInteger = new AtomicInteger();
        contacts.keySet().stream().forEach(el -> {
            if (atomicInteger.incrementAndGet() == index) {
                System.out.println(contacts.get(el));
                System.out.println("Apasati enter pentru a va intoarce la meniul anterior");
                kb.nextLine();
            }
        });
    }

    public void showFilteredList(List list) {
        list.stream().forEach(el -> {
            System.out.println(el);
        });
        System.out.println("Apasati enter pentru a va intoarce la meniul anterior");
        kb.nextLine();
    }


    public void addContact(boolean newContact) throws IOException {
        System.out.println("introduceti nume/prenume, telefon si email");
        boolean safe;
        String nume = kb.nextLine();
        String prenume = kb.nextLine();
        String telefon = kb.nextLine();
        String email = kb.nextLine();
        safe = contactValidation(nume, prenume, telefon, email, newContact);
        if (safe) {
            this.contacts.put(nume + " " + prenume, new Contact(nume, prenume, telefon, email));
            saveFile();
        }
    }

    public boolean contactValidation(String nume, String prenume, String telefon, String email, boolean newContact) {
        boolean isValid = false;
        String pattern = "-?\\d+(\\.\\d+)?";
        Pattern mail = Pattern.compile("^[_A-Za-z]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        try {
            boolean duplicate = false;
            for (Map.Entry<String, Contact> entry : contacts.entrySet()) {
                Contact c = entry.getValue();
                if (nume.equals(c.getFirstName()) || prenume.equals(c.getLastName())) {
                    duplicate = true;
                }
            }
            if (newContact && duplicate) {
                throw new Exception("contact already exists: " + nume + " " + prenume);
            } else if (!newContact && !duplicate) {
                throw new Exception("cannot edit unregistered contact: " + nume + " " + prenume);
            }

            Matcher mtch = mail.matcher(email);
            if (nume.matches(pattern) || prenume.matches(pattern)) {
                throw new InvalidName();
            }
            if (!telefon.matches(pattern)) {
                throw new InvalidNumber();
            }
            if (!mtch.matches()) {
                throw new InvalidEmail();
            }
            isValid = true;
        } catch (InvalidName invalidName) {
            System.out.println("!!Invalid name: " + nume + " " + prenume + "\n");
        } catch (InvalidNumber invalidNumber) {
            System.out.println("!!Invalid number: " + telefon + "\n");
        } catch (InvalidEmail invalidEmail) {
            System.out.println("!!Invalid email: " + email + "\n");
        } catch (Exception e) {
            System.out.println(e);
        }
        return isValid;
    }

    public void delContact() throws IOException {
        String delKey = " ";
        System.out.println("index");
        int choice = kb.nextInt();
        int index = 1;
        for (Map.Entry<String, Contact> entry : contacts.entrySet()) {
            if (choice == index) {
                delKey = entry.getKey();
            }
            index++;
        }
        if (!(delKey.equals(" "))) {
            this.contacts.remove(delKey);
            saveFile();
        } else {
            System.out.println("acest contact nu exista");
        }
        kb.nextLine();
    }

    public SortedMap<String, Contact> getContacts() {
        return contacts;
    }

    public void saveFile() throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File("").getAbsoluteFile() + "\\src\\com\\phone\\contacte.txt"));
        bw.write("firstName,lastName,phoneNr,email\n");
        for (Map.Entry<String, Contact> entry : contacts.entrySet()) {
            Contact c = entry.getValue();
            bw.write(c.getFirstName() + "," + c.getLastName() + "," + c.getPhoneNr() + "," + c.getEmail() + "\n");
        }
        bw.close();
    }
}
