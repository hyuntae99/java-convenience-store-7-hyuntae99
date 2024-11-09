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
            String name = parts[0];
            int buy = Integer.parseInt(parts[1]);
            int get = Integer.parseInt(parts[2]);
            LocalDate startDate = LocalDate.parse(parts[3]);
            LocalDate endDate = LocalDate.parse(parts[4]);
            promotions.put(name, new Promotion(name, buy, get, startDate, endDate));
        }
    }

    public void loadProducts(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                processProductLine(line);
            }
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
            String name = parts[0];
            int price = Integer.parseInt(parts[1]);
            int quantity = Integer.parseInt(parts[2]);
            Promotion promotion = promotions.get(parts[3]);
            addProduct(name, price, quantity, promotion);
        }
    }

    private void addProduct(String name, int price, int quantity, Promotion promotion) {
        products.putIfAbsent(name, new ArrayList<>());
        products.get(name).add(new Product(name, price, quantity, promotion));
    }

    public Map<String, List<Product>> getProducts() {
        return products;
    }
}
