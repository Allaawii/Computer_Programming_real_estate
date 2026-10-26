package edu.uni.realestate.app;

import edu.uni.realestate.model.ApartmentPurchase;
import edu.uni.realestate.model.ApartmentRental;
import edu.uni.realestate.model.HousePurchase;
import edu.uni.realestate.model.HouseRental;
import edu.uni.realestate.model.Property;
import edu.uni.realestate.model.PurchaseTerms;
import edu.uni.realestate.model.RentalTerms;
import edu.uni.realestate.service.Agent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class AgentDemo {

    public static void main(String[] args) {
        Agent agent = new Agent();

        heading("1. One object of each concrete class, built from a Map");
        HouseRental houseRental = new HouseRental(map(
                "sqft", "1400", "beds", "3", "baths", "2",
                "stories", "2", "garage", "attached", "fenced", "yes",
                "rent", "1850", "furnished", "no", "utilities", "about 120 EUR/month"));
        HousePurchase housePurchase = new HousePurchase(map(
                "sqft", "2200", "beds", "4", "baths", "3",
                "stories", "2", "garage", "detached", "fenced", "no",
                "price", "385000", "taxes", "3200"));
        ApartmentRental apartmentRental = new ApartmentRental(map(
                "sqft", "650", "beds", "0", "baths", "1",
                "balcony", "yes", "laundry", "coin",
                "rent", "900", "furnished", "yes", "utilities", "included"));
        ApartmentPurchase apartmentPurchase = new ApartmentPurchase(map(
                "sqft", "980", "beds", "2", "baths", "1",
                "balcony", "no", "laundry", "ensuite",
                "price", "215000", "taxes", "1450.50"));
        agent.addProperty(houseRental);
        agent.addProperty(housePurchase);
        agent.addProperty(apartmentRental);
        agent.addProperty(apartmentPurchase);
        agent.displayProperties(true);
        System.out.println();
        System.out.println("Available properties: " + agent.countAvailable());

        heading("2. Invalid values are rejected");
        Map<String, String> validHouseRental = map(
                "sqft", "1400", "beds", "3", "baths", "2",
                "stories", "2", "garage", "attached", "fenced", "yes",
                "rent", "1850", "furnished", "no", "utilities", "included");
        Map<String, String> validApartmentPurchase = map(
                "sqft", "980", "beds", "2", "baths", "1",
                "balcony", "no", "laundry", "ensuite",
                "price", "215000", "taxes", "1450");
        tryCreate("square feet = 0",       HouseRental::new,       with(validHouseRental, "sqft", "0"));
        tryCreate("bedrooms = -1",         HouseRental::new,       with(validHouseRental, "beds", "-1"));
        tryCreate("bathrooms = 0",         HouseRental::new,       with(validHouseRental, "baths", "0"));
        tryCreate("square feet = \"abc\"", HouseRental::new,       with(validHouseRental, "sqft", "abc"));
        tryCreate("stories = 0",           HouseRental::new,       with(validHouseRental, "stories", "0"));
        tryCreate("garage = \"carport\"",  HouseRental::new,       with(validHouseRental, "garage", "carport"));
        tryCreate("monthly rent = 0",      HouseRental::new,       with(validHouseRental, "rent", "0"));
        tryCreate("utilities = \"   \"",   HouseRental::new,       with(validHouseRental, "utilities", "   "));
        tryCreate("laundry = \"shared\"",  ApartmentPurchase::new, with(validApartmentPurchase, "laundry", "shared"));
        tryCreate("price = -5",            ApartmentPurchase::new, with(validApartmentPurchase, "price", "-5"));
        tryCreate("price = 0",             ApartmentPurchase::new, with(validApartmentPurchase, "price", "0"));
        tryCreate("annual taxes = -1",     ApartmentPurchase::new, with(validApartmentPurchase, "taxes", "-1"));
        tryCreate("addProperty(null)",     data -> { agent.addProperty((Property) null); return null; }, null);
        System.out.println("Properties managed by the agent: " + agent.getProperties().size()
                + " (no invalid object was added)");

        heading("3. Closing a property twice is rejected");
        System.out.println("agent.closeDeal(1)  -> " + agent.closeDeal(1) + "   (#1 is now "
                + houseRental.getClosedLabel() + ")");
        System.out.println("agent.closeDeal(1)  -> " + agent.closeDeal(1) + "  (already closed)");
        System.out.println("agent.closeDeal(4)  -> " + agent.closeDeal(4) + "   (#4 is now "
                + apartmentPurchase.getClosedLabel() + ")");
        System.out.println("agent.closeDeal(99) -> " + agent.closeDeal(99) + "  (no such id)");
        try {
            houseRental.close();
            System.out.println("ERROR: houseRental.close() was accepted a second time");
        } catch (IllegalStateException e) {
            System.out.println("houseRental.close() -> IllegalStateException: " + e.getMessage());
        }

        heading("4. Closed properties disappear from the available list");
        System.out.println("--- displayProperties(false): available only ---");
        agent.displayProperties(false);
        System.out.println();
        System.out.println("--- displayProperties(true): all properties ---");
        agent.displayProperties(true);
        System.out.println();
        System.out.println("Available properties: " + agent.countAvailable() + " of "
                + agent.getProperties().size());
        agent.closeDeal(2);
        agent.closeDeal(3);
        System.out.println("After closing #2 and #3, displayProperties(false) prints:");
        agent.displayProperties(false);

        heading("5. getProperties() is read-only");
        try {
            agent.getProperties().clear();
            System.out.println("ERROR: the internal list was cleared");
        } catch (UnsupportedOperationException e) {
            System.out.println("agent.getProperties().clear() -> UnsupportedOperationException");
            System.out.println("Properties still managed: " + agent.getProperties().size());
        }

        heading("6. Positive rent and price below one cent are accepted");
        RentalTerms rentalTerms = new RentalTerms(map(
                "rent", "0.001", "furnished", "no", "utilities", "included"));
        PurchaseTerms purchaseTerms = new PurchaseTerms(map("price", "0.001", "taxes", "0"));
        System.out.println("Monthly rent accepted: " + rentalTerms.getMonthlyRent());
        System.out.println("Purchase price accepted: " + purchaseTerms.getPrice());
    }

    private static void tryCreate(String description, Function<Map<String, String>, Property> constructor,
                                  Map<String, String> data) {
        try {
            constructor.apply(data);
            System.out.println("ERROR: " + description + " was accepted");
        } catch (IllegalArgumentException e) {
            System.out.printf("%-22s -> rejected: %s%n", description, e.getMessage());
        }
    }

    private static void heading(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }

    private static Map<String, String> map(String... keysAndValues) {
        Map<String, String> data = new HashMap<>();
        for (int i = 0; i < keysAndValues.length; i += 2) {
            data.put(keysAndValues[i], keysAndValues[i + 1]);
        }
        return data;
    }

    private static Map<String, String> with(Map<String, String> data, String key, String value) {
        Map<String, String> copy = new HashMap<>(data);
        copy.put(key, value);
        return copy;
    }
}
