package com.phone;

import com.phone.Exceptions.InvalidEmail;
import com.phone.Exceptions.InvalidName;
import com.phone.Exceptions.InvalidNumber;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Agenda {
    private SortedMap<String, Contact> contacts = new TreeMap<>();

    public Agenda() {
    }

    public Agenda(Map<String, Contact> contacts) {
        this.contacts.putAll(contacts);
    }

    public Agenda(String filePath) {
        try {
            Map<String, Contact> contactMap = init(filePath); // initialize the contacts from file
            this.contacts.putAll(contactMap); // add all contacts retrieved from the file to the contacts map
        } catch (IOException ex) {
            System.out.println("Failed to read contacts from file");
        }
    }

    private Map<String, Contact> init(String filePath) throws IOException {
        Map<String, Contact> contacts = new TreeMap<>();
        BufferedReader in = new BufferedReader(new FileReader(filePath));
        String header = in.readLine(); // the header
        String str;
        String[] line;
        while ((str = in.readLine()) != null) {
            line = str.split(",");
            Contact c = new Contact(line[0], line[1], line[2], line[3]);
            contacts.put(c.getFullName(), c);
        }
        in.close();

        return contacts;
    }

    public void print() {
        AtomicInteger atomicInteger = new AtomicInteger();
        System.out.println("Contacte:");
        contacts.keySet().stream().forEach(el -> System.out.println(atomicInteger.incrementAndGet() + ". " + el));
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

    public Optional<Contact> contactInfo(int index) {
        AtomicInteger atomicInteger = new AtomicInteger();
        return contacts.values().stream().filter(el -> atomicInteger.incrementAndGet() == index).findFirst();
    }

    public void printContacts(List<Contact> list) {
        list.stream().forEach(el -> {
            System.out.println(el);
        });
        System.out.println("Apasati enter pentru a va intoarce la meniul anterior");
//        kb.nextLine();
    }


    public void addContact(boolean newContact, Scanner kb) {
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

    public void delContact(Scanner kb) {
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

    public void backupData() {
        File file = new File("backups");
        if (!file.exists()) {
            file.mkdir();
        }

        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("backups//backup_" + System.currentTimeMillis() + ".txt"));
            out.write("firstName,lastName,phoneNr,email\n");
            for (Map.Entry<String, Contact> entry : contacts.entrySet()) {
                Contact c = entry.getValue();
                out.write(c.getFirstName() + "," + c.getLastName() + "," + c.getPhoneNr() + "," + c.getEmail() + "\n");
            }
            out.close();
        } catch (Exception e) {
            System.out.println("backup failed");
            System.out.println(String.valueOf(e));
        }
    }

    public void returnPreviousVersion(Scanner kb) {
        File file = new File("backups");
        if (file.list().length != 0) {
            int i = 1;
            for (String el : file.list()) {
                System.out.println(i + ". " + el);
                i++;
            }
            System.out.print("choose version: ");
            int choice = kb.nextInt();
            for (i = 1; i <= file.list().length; i++) {
                if (i == choice) {
                    try {
                        this.contacts = (SortedMap<String, Contact>) this.init("backups//" + file.list()[i - 1]);
                        this.saveFile();
                    } catch (IOException e) {
                        System.out.println(e);
                    }
                }
            }
        } else {
            System.out.println("no backups found");
        }
    }

    public SortedMap<String, Contact> getContacts() {
        return contacts;
    }

    public void saveFile() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("contacts.txt"));
            bw.write("firstName,lastName,phoneNr,email\n");
            for (Map.Entry<String, Contact> entry : contacts.entrySet()) {
                Contact c = entry.getValue();
                bw.write(c.getFirstName() + "," + c.getLastName() + "," + c.getPhoneNr() + "," + c.getEmail() + "\n");
            }
            bw.close();
        } catch (IOException ex) {
            System.out.println("Failed to write contacts to file");
        }
    }


}
