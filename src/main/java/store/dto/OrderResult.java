package store.dto;

import java.util.List;
import store.domain.Product;

public class OrderResult {
    private int cost;
    private int freeQuantity;
    private int nonPromotionAmount;
    private List<Product> boughtProducts;
    private List<Product> freeProducts;
    private List<Product> modifiedProducts;
    private int processedQuantity;

    public OrderResult(int cost, int freeQuantity, int nonPromotionAmount, List<Product> boughtProducts,
                       List<Product> freeProducts, List<Product> modifiedProducts) {
        this.cost = cost;
        this.freeQuantity = freeQuantity;
        this.nonPromotionAmount = nonPromotionAmount;
        this.boughtProducts = boughtProducts;
        this.freeProducts = freeProducts;
        this.modifiedProducts = modifiedProducts;
        this.processedQuantity = 0;
    }

    public OrderResult(int cost, int freeQuantity, int nonPromotionAmount, List<Product> boughtProducts,
                       List<Product> freeProducts, List<Product> modifiedProducts, int processedQuantity) {
        this(cost, freeQuantity, nonPromotionAmount, boughtProducts, freeProducts, modifiedProducts);
        this.processedQuantity = processedQuantity;
    }

    public int getCost() {
        return cost;
    }

    public int getFreeQuantity() {
        return freeQuantity;
    }

    public int getNonPromotionAmount() {
        return nonPromotionAmount;
    }

    public List<Product> getBoughtProducts() {
        return boughtProducts;
    }

    public List<Product> getFreeProducts() {
        return freeProducts;
    }

    public List<Product> getModifiedProducts() {
        return modifiedProducts;
    }

    public int getProcessedQuantity() {
        return processedQuantity;
    }
}
