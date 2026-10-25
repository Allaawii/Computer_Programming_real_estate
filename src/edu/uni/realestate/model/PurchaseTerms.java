package edu.uni.realestate.model;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

public class PurchaseTerms {
    private double price;
    private double annualTaxes;

    public PurchaseTerms(Map<String, String> data) {
        setPrice(Double.parseDouble(data.get("price")));
        setAnnualTaxes(Double.parseDouble(data.get("taxes")));
    }

    // ----- getters -----
    public double getPrice()        { return price; }
    public double getAnnualTaxes()  { return annualTaxes; }

    // ----- private validating setters -----
    private void setPrice(double value) {
        if (!Double.isFinite(value) || value <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0");
        }
        price = value;
    }

    private void setAnnualTaxes(double value) {
        if (!Double.isFinite(value) || value < 0) {
            throw new IllegalArgumentException("Annual property taxes cannot be negative");
        }
        annualTaxes = value;
    }

    // ----- behavior -----
    public void display() {
        System.out.println(String.format(Locale.ROOT, "  Price:        %.2f EUR", price));
        System.out.println(String.format(Locale.ROOT, "  Annual taxes: %.2f EUR", annualTaxes));
    }

    public static Map<String, String> promptInit(Scanner in) {
        Map<String, String> data = new HashMap<>();
        data.put("price", InputHelper.readDouble(in, "Purchase price: ", 0.01));
        data.put("taxes", InputHelper.readDouble(in, "Estimated annual property taxes: ", 0));
        return data;
    }
}
