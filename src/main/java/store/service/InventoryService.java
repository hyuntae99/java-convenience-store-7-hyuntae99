package store.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import store.domain.Product;
import store.domain.Promotion;

public class InventoryService {
    private Map<String, List<Product>> products = new LinkedHashMap<>();
    private Map<String, Promotion> promotions = new HashMap<>();
    private Map<String, Boolean> hasNonPromotionalVersion = new HashMap<>();

    public void loadPromotions(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                processPromotionLine(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processPromotionLine(String line) {
        String[] parts = line.split(",");
        if (parts[0].equals("name")) {
            return;
        }
        if (parts.length == 5) {
            String name = parts[0].trim();
            int buy = Integer.parseInt(parts[1].trim());
            int get = Integer.parseInt(parts[2].trim());
            LocalDate startDate = LocalDate.parse(parts[3].trim());
            LocalDate endDate = LocalDate.parse(parts[4].trim());
            promotions.put(name, new Promotion(name, buy, get, startDate, endDate));
        }
    }

    public void loadProducts(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                processProductLine(line);
            }
            addMissingNullProducts();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processProductLine(String line) {
        String[] parts = line.split(",");
        if (parts[0].equals("name")) {
            return;
        }
        if (parts.length == 4) {
            String name = parts[0].trim();
            int price = Integer.parseInt(parts[1].trim());
            int quantity = Integer.parseInt(parts[2].trim());
            Promotion promotion = promotions.get(parts[3].trim());

            if (promotion == null) {
                hasNonPromotionalVersion.put(name, true);
            } else {
                hasNonPromotionalVersion.putIfAbsent(name, false);
            }

            addProduct(name, price, quantity, promotion);
        }
    }

    private void addProduct(String name, int price, int quantity, Promotion promotion) {
        products.putIfAbsent(name, new ArrayList<>());
        products.get(name).add(new Product(name, price, quantity, promotion));
    }

    private void addMissingNullProducts() {
        for (String productName : hasNonPromotionalVersion.keySet()) {
            if (!hasNonPromotionalVersion.get(productName)) {
                Product nullProduct = new Product(productName, getPriceOfFirstProduct(productName), 0, null);
                products.get(productName).add(nullProduct);
            }
        }
    }

    private int getPriceOfFirstProduct(String name) {
        if (products.containsKey(name) && !products.get(name).isEmpty()) {
            return products.get(name).get(0).getPrice();
        }
        return 0;
    }

    public Map<String, List<Product>> getProducts() {
        return products;
    }
}
