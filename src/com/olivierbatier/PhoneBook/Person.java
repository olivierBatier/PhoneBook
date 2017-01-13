package com.olivierbatier.PhoneBook;


import java.util.List;

/**
 * Created by olivier on 12/01/2017.
 */

public class Person {
    private String name;
    private List<String> number;

    public Person(String name, List<String> number) {
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public List<String> getNumber() {
        return number;
    }

}
