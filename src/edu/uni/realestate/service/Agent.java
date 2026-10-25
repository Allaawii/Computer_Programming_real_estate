package edu.uni.realestate.service;

import edu.uni.realestate.model.ApartmentPurchase;
import edu.uni.realestate.model.ApartmentRental;
import edu.uni.realestate.model.HousePurchase;
import edu.uni.realestate.model.HouseRental;
import edu.uni.realestate.model.Property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Function;

public class Agent {
    private static final Map<String, Function<Map<String, String>, Property>> REGISTRY = new LinkedHashMap<>();
    private static final Map<String, Function<Scanner, Map<String, String>>> PROMPTS = new LinkedHashMap<>();
    private static final Set<String> TYPES = new LinkedHashSet<>();
    private static final Set<String> DEALS = new LinkedHashSet<>();

    static {
        register("house",     "rental",   HouseRental::promptInit,       HouseRental::new);
        register("house",     "purchase", HousePurchase::promptInit,     HousePurchase::new);
        register("apartment", "rental",   ApartmentRental::promptInit,   ApartmentRental::new);
        register("apartment", "purchase", ApartmentPurchase::promptInit, ApartmentPurchase::new);
    }

    private final List<Property> properties;

    public Agent() {
        properties = new ArrayList<>();
    }

    private static void register(String type, String deal,
                                 Function<Scanner, Map<String, String>> prompt,
                                 Function<Map<String, String>, Property> constructor) {
        TYPES.add(type);
        DEALS.add(deal);
        PROMPTS.put(type + " " + deal, prompt);
        REGISTRY.put(type + " " + deal, constructor);
    }

    public void addProperty(Property p) {
        if (p == null) throw new IllegalArgumentException("Property cannot be null");
        properties.add(p);
    }

    public void addProperty(Scanner in) {
        String type = readChoice(in, "Property type (" + String.join(", ", TYPES) + "): ", TYPES);
        String deal = readChoice(in, "Deal type (" + String.join(", ", DEALS) + "): ", DEALS);
        String key = type + " " + deal;
        if (!REGISTRY.containsKey(key)) {
            throw new IllegalArgumentException("\"" + key + "\" is not a supported property kind");
        }
        Property p = REGISTRY.get(key).apply(PROMPTS.get(key).apply(in));
        addProperty(p);
        System.out.println("Added property #" + p.getId() + ".");
    }

    public void displayProperties(boolean showAll) {
        int shown = 0;
        for (Property p : properties) {
            if (showAll || p.isAvailable()) {
                if (shown > 0) System.out.println();
                p.display();
                shown++;
            }
        }
        if (shown == 0) {
            System.out.println("No properties to show.");
        }
    }

    public boolean closeDeal(int id) {
        Property p = findById(id);
        if (p == null || !p.isAvailable()) {
            return false;
        }
        p.close();
        return true;
    }

    public int countAvailable() {
        int count = 0;
        for (Property p : properties) {
            if (p.isAvailable()) count++;
        }
        return count;
    }

    public List<Property> getProperties() {
        return Collections.unmodifiableList(properties);
    }

    private Property findById(int id) {
        for (Property p : properties) {
            if (p.getId() == id) return p;
        }
        return null;
    }

    private static String readChoice(Scanner in, String prompt, Set<String> options) {
        while (true) {
            System.out.print(prompt);
            String text = in.nextLine().trim();
            for (String option : options) {
                if (option.equalsIgnoreCase(text)) return option;
            }
            System.out.println("  Please choose one of: " + String.join(", ", options));
        }
    }
}
