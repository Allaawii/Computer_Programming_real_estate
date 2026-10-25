package edu.uni.realestate.app;

import edu.uni.realestate.model.Property;
import edu.uni.realestate.service.Agent;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {
    private static final String MENU =
            "1) Add property   2) List available   3) List all   4) Mark sold/rented   0) Exit";

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        Agent agent = new Agent();

        System.out.println("=== Real Estate Agent ===");
        System.out.println(MENU);
        try {
            boolean running = true;
            while (running) {
                System.out.print("Choice: ");
                String choice = in.nextLine().trim();
                switch (choice) {
                    case "1": addProperty(agent, in);        break;
                    case "2": agent.displayProperties(false); break;
                    case "3": agent.displayProperties(true);  break;
                    case "4": closeDeal(agent, in);          break;
                    case "0": running = false;               break;
                    default:
                        System.out.println("Please choose one of the options below.");
                        System.out.println(MENU);
                }
                if (running) System.out.println();
            }
        } catch (NoSuchElementException e) {
            System.out.println();
        }
        System.out.println("Goodbye.");
    }

    private static void addProperty(Agent agent, Scanner in) {
        try {
            agent.addProperty(in);
        } catch (IllegalArgumentException e) {
            System.out.println("Could not add property: " + e.getMessage());
        }
    }

    private static void closeDeal(Agent agent, Scanner in) {
        System.out.print("Property id: ");
        String text = in.nextLine().trim();
        int id;
        try {
            id = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            System.out.println("\"" + text + "\" is not a valid property id.");
            return;
        }

        Property property = findById(agent, id);
        if (agent.closeDeal(id)) {
            System.out.println("Property #" + id + " marked as " + property.getClosedLabel() + ".");
        } else if (property == null) {
            System.out.println("There is no property #" + id + ".");
        } else {
            System.out.println("Property #" + id + " is not available.");
        }
    }

    private static Property findById(Agent agent, int id) {
        for (Property p : agent.getProperties()) {
            if (p.getId() == id) return p;
        }
        return null;
    }
}
