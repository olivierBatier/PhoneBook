package com.olivierbatier.PhoneBook;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Created by olivier on 10/01/2017.
 *
 * This was not a difficult exercice, although the fact that you asked us to use as many fancy tricks as possible made it a little tricky
 * I really don't like the swing user interface in Java but it's the only one I really know and you asked not to search for new things.
 * I added a few new functionalities :
 *          + Partial word search
 *          + Verification of the unicity of the contact during the import
 *          + Possibility to have several numbers for one person
 *          +
 */
public class PhoneBook extends JFrame implements ActionListener {

    private JPanel container = new JPanel();
    private JTextField searchField = new JTextField();
    private JButton searchButton = new JButton("Search");
    private JButton importButton = new JButton("Import");
    private JLabel result = new JLabel();
    private List<Person> phoneEntries = new ArrayList<>();

    public PhoneBook(String title) throws HeadlessException {
        //Declaration of the differents components for the GUI
        super(title);
        this.setSize(600, 600);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel searchPan = new JPanel();
        searchPan.setPreferredSize(new Dimension(450, 100));
        this.searchField.setPreferredSize(new Dimension(200, 30));
        this.searchButton.setPreferredSize(new Dimension(75, 30));


        searchPan.add(searchField);
        searchPan.add(searchButton);


        JPanel importPan = new JPanel();
        importPan.setPreferredSize(new Dimension(75, 50));
        this.importButton.setPreferredSize(new Dimension(75, 30));
        importPan.add(importButton);

        JPanel resultPan = new JPanel();
        resultPan.setBorder(BorderFactory.createTitledBorder("Results"));
        resultPan.setPreferredSize(new Dimension(450, 250));
        resultPan.add(result);

        JPanel content = new JPanel();
        content.add(searchPan);
        content.add(resultPan);

        this.container.setBackground(Color.white);
        this.container.setLayout(new BorderLayout());
        JLabel labelTitle = new JLabel("PhoneBook");
        labelTitle.setHorizontalAlignment(JLabel.CENTER);
        this.container.add(labelTitle, BorderLayout.NORTH);
        this.container.add(content, BorderLayout.CENTER);
        this.container.add(importPan, BorderLayout.SOUTH);
        this.setVisible(true);
        this.setContentPane(container);

        importButton.addActionListener(this);
        searchButton.addActionListener(this);
    }

    /**
     * This method reloads the component of the view to show the modifications
     */
    public void reloadView() {
        container.revalidate();
        container.repaint();
    }

    /**
     *
     * @param phoneEntriesFile
     * @throws IOException
     *
     * This method imports the data from a .txt file
     *
     */
    public void injectEntries(File phoneEntriesFile) throws IOException {
        String[] listString;
        result.setText("");
        //We use a buffered reader to read the file
        //This will throw an exception is the file is not found
        try (BufferedReader reader = new BufferedReader(new FileReader(phoneEntriesFile))) {
            String line = reader.readLine();
        //We go through all the lines of the file
            while (line != null) {
                //We split the line in half where the ':' is
                listString = line.split(":");
                //If the length of this line is different than 2 it means there is a bad layout of the file
                if (listString.length == 2) {
                    //if not we just verify that the person doesn't already exists then we inject it
                    Person newPerson = new Person(listString[0], Arrays.asList(listString[1].split(";|\\.|\\,")));
                    if (!searchDuplicate(newPerson)) {
                        this.phoneEntries.add(newPerson);
                    } else {
                        result.setText(convertToMultilines(result.getText() + "\n" + "Person already exists: " + listString[0]));
                    }
                } else {
                    result.setText(convertToMultilines(result.getText() + "\n" + "File layout not supported"));
                }
                line = reader.readLine();
                reloadView();
            }
        }
    }

    public String searchName(String name) {
        String result = "No results found for this name";
        if (name != null) {
            //We just have to filter the list using the new stream functionnality from java 8
            List<Person> results = this.phoneEntries.stream()
                    .filter(x -> x.getName().toLowerCase().contains(name.toLowerCase()))
                    .collect(Collectors.toList());
            //If the number of results is greater than 0 we will simply add all the numbers found
            if (results.size() > 0) {
                result = "";
                for (Person person : results) {
                    result += person.getNumber().toString();
                    result += "\n";
                }
            }
            result = convertToMultilines(result.replaceAll("\\[|\\]", ""));
        }
        return result;
    }

    /**
     *
     * @param arg0
     *
     * This method is triggered when an action is realised on our view
     */
    public void actionPerformed(ActionEvent arg0) {
        if (arg0.getSource() == searchButton) {

            result.setText(this.searchName(this.searchField.getText()));
            reloadView();
        }
        if (arg0.getSource() == importButton) {

            //If the action is to import the file we will use the swing component FileCHooser to choose the file
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File phoneEntries = fileChooser.getSelectedFile();
                try {
                    this.injectEntries(phoneEntries);
                    result.setText(convertToMultilines(result.getText() + "\n" + "import successful"));
                    System.out.println(result.getText());
                } catch (IOException e) {
                    result.setText(convertToMultilines(result.getText() + "\n" + "fatal error"));
                    e.printStackTrace();
                }
            }
            reloadView();
        }
    }

    /**
     *
     * @param person
     * @return
     *
     * This method checks if the person already exists
     */
    public boolean searchDuplicate(Person person) {
        for (Person personToCheck : this.phoneEntries) {
            if (personToCheck.getName().toLowerCase().equals(person.getName().toLowerCase())
                    && personToCheck.getNumber().toString().equals(person.getNumber().toString())) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param label
     * @return
     *
     * This method transforms a simple label into a multiline label      *
     */
    public String convertToMultilines(String label) {
        //Swing permits us to use simple html
        return "<html>" + label.replaceAll("\n", "<br>");
    }
}

