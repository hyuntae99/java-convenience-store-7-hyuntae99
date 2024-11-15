package store.service;

import static store.utils.NumberFormatter.formatWithCommas;

import camp.nextstep.edu.missionutils.Console;
import java.util.ArrayList;
import java.util.List;
import store.domain.Product;
import store.domain.Promotion;
import store.dto.Order;
import store.dto.OrderResult;

public class PurchaseService {
    private InventoryService inventoryService;
    private int totalAmount = 0;
    private int promotionDiscount = 0;
    private int membershipDiscount = 0;
    private int nonPromotionAmount = 0;
    private List<Product> boughtProducts = new ArrayList<>();
    private List<Product> freeProducts = new ArrayList<>();

    public PurchaseService(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    public void processOrders(List<Order> orders) {
        List<Product> tempBoughtProducts = new ArrayList<>();
        List<Product> tempFreeProducts = new ArrayList<>();
        int tempTotalAmount = 0;
        int tempPromotionDiscount = 0;
        int tempNonPromotionAmount = 0;
        List<Product> modifiedProducts = new ArrayList<>();

        for (Order order : orders) {
            List<Product> productList = makeProductList(order);
            int buyQuantity = order.getQuantity();
            exceedQuantity(productList, buyQuantity);

            OrderResult orderResult = processBuy(productList, buyQuantity);
            if (orderResult == null) {
                restoreInventory(modifiedProducts);
                throw new IllegalArgumentException("[INFO] 알 수 없는 오류가 발생했습니다.");
            }

            tempTotalAmount += orderResult.getCost();
            tempPromotionDiscount += orderResult.getFreeQuantity() * productList.get(0).getPrice();
            tempNonPromotionAmount += orderResult.getNonPromotionAmount();
            mergeProducts(tempBoughtProducts, orderResult.getBoughtProducts());
            mergeProducts(tempFreeProducts, orderResult.getFreeProducts());
            modifiedProducts.addAll(orderResult.getModifiedProducts());
        }

        membershipDiscount = calculateMembershipDiscount(tempNonPromotionAmount);

        boughtProducts = tempBoughtProducts;
        freeProducts = tempFreeProducts;
        totalAmount = tempTotalAmount;
        promotionDiscount = tempPromotionDiscount;
        nonPromotionAmount = tempNonPromotionAmount;

        printAllReceipt();
    }

    private OrderResult processBuy(List<Product> productList, int buyQuantity) {
        int remainingQuantity = buyQuantity;
        int cost = 0;
        int freeQuantity = 0;
        int nonPromotionQuantity = 0;
        List<Product> boughtProducts = new ArrayList<>();
        List<Product> freeProducts = new ArrayList<>();
        List<Product> modifiedProducts = new ArrayList<>();
        int promotionProductCount = countPromotionProducts(productList);

        for (Product product : productList) {
            if (isPromotionApplicable(product) && remainingQuantity > 0) {
                Promotion promotion = product.getPromotion();
                OrderResult promotionResult = processPromotion(product, promotion, remainingQuantity);
                remainingQuantity -= promotionResult.getProcessedQuantity();
                cost += promotionResult.getCost();
                freeQuantity += promotionResult.getFreeQuantity();
                modifiedProducts.addAll(promotionResult.getModifiedProducts());
                mergeProducts(boughtProducts, promotionResult.getBoughtProducts());
                mergeProducts(freeProducts, promotionResult.getFreeProducts());
            }
        }

        if (remainingQuantity > 0) {
            boolean continuePurchase = true;
            if (promotionProductCount != 0) {
                continuePurchase = confirmNonPromotionPurchase(remainingQuantity, productList.get(0).getName());
            }
            if (!continuePurchase) {
                System.out.println("[INFO] 결제가 취소되었습니다. 이전까지의 거래는 반영되지 않습니다.");
                restoreInventory(modifiedProducts);
                return null;
            } else {
                OrderResult nonPromotionResult = processNonPromotion(productList, remainingQuantity);
                cost += nonPromotionResult.getCost();
                nonPromotionQuantity += nonPromotionResult.getNonPromotionAmount();
                mergeProducts(boughtProducts, nonPromotionResult.getBoughtProducts());
                modifiedProducts.addAll(nonPromotionResult.getModifiedProducts());
            }
        }

        return new OrderResult(cost, freeQuantity, nonPromotionQuantity, boughtProducts, freeProducts,
                modifiedProducts);
    }

    private boolean isPromotionApplicable(Product product) {
        return product.getPromotion() != null && product.getPromotion().isActive() && product.getQuantity() > 0;
    }

    private OrderResult processPromotion(Product product, Promotion promotion, int remainingQuantity) {
        int processedQuantity = 0;
        int freeQuantity = 0;
        int cost = 0;
        List<Product> modifiedProducts = new ArrayList<>();
        List<Product> boughtProducts = new ArrayList<>();
        List<Product> freeProducts = new ArrayList<>();

        while (remainingQuantity >= promotion.getBuy() && product.getQuantity() >= (promotion.getBuy()
                + promotion.getGet())) {
            if (remainingQuantity == promotion.getBuy()) {
                if (!confirmPromotionGet(product.getName(), promotion.getGet())) {
                    break;
                }
            }
            cost += (promotion.getBuy() + promotion.getGet()) * product.getPrice();
            freeQuantity += promotion.getGet();
            product.modifyQuantity(promotion.getBuy() + promotion.getGet());
            modifiedProducts.add(
                    new Product(product.getName(), product.getPrice(), promotion.getBuy() + promotion.getGet(),
                            product.getPromotion()));
            remainingQuantity -= (promotion.getBuy() + promotion.getGet());
            processedQuantity += (promotion.getBuy() + promotion.getGet());
            addOrUpdateProduct(boughtProducts, product.getName(), product.getPrice(), promotion.getBuy());
            addOrUpdateProduct(freeProducts, product.getName(), product.getPrice(), promotion.getGet());
        }

        return new OrderResult(cost, freeQuantity, 0, boughtProducts, freeProducts, modifiedProducts,
                processedQuantity);
    }

    private boolean confirmPromotionGet(String productName, int promotionGet) {
        String message = "현재 " + productName + "은(는) " + promotionGet + "개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)";
        return questionsValidation(message);
    }

    private boolean confirmNonPromotionPurchase(int nonPromotionQuantity, String productName) {
        String message =
                "현재 " + productName + " " + nonPromotionQuantity + "개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까? (Y/N)";
        return questionsValidation(message);
    }

    private OrderResult processNonPromotion(List<Product> productList, int nonPromotionQuantity) {
        int cost = 0;
        int remainingQuantity = nonPromotionQuantity;
        List<Product> boughtProducts = new ArrayList<>();
        List<Product> modifiedProducts = new ArrayList<>();

        for (Product product : productList) {
            if (product.getQuantity() > 0) {
                int availableQuantity = Math.min(remainingQuantity, product.getQuantity());
                cost += availableQuantity * product.getPrice();
                product.modifyQuantity(availableQuantity);
                modifiedProducts.add(
                        new Product(product.getName(), product.getPrice(), availableQuantity, product.getPromotion()));
                addOrUpdateProduct(boughtProducts, product.getName(), product.getPrice(), availableQuantity);
                remainingQuantity -= availableQuantity;
            }
            if (remainingQuantity == 0) {
                break;
            }
        }

        return new OrderResult(cost, 0, cost, boughtProducts, new ArrayList<>(), modifiedProducts);
    }

    private int calculateMembershipDiscount(int tempNonPromotionAmount) {
        boolean applyMembershipDiscount = questionsValidation("멤버십 할인을 받으시겠습니까? (Y/N)");
        if (applyMembershipDiscount && tempNonPromotionAmount > 0) {
            return Math.min((int) (tempNonPromotionAmount * 0.3), 8000);
        }
        return 0;
    }

    private void mergeProducts(List<Product> targetProducts, List<Product> sourceProducts) {
        for (Product sourceProduct : sourceProducts) {
            addOrUpdateProduct(targetProducts, sourceProduct.getName(), sourceProduct.getPrice(),
                    sourceProduct.getQuantity());
        }
    }

    private List<Product> makeProductList(Order order) {
        List<Product> productList = inventoryService.getProducts().get(order.getName());
        if (productList == null) {
            throw new IllegalArgumentException("[ERROR] 존재하지 않는 상품입니다. 다시 입력해 주세요.");
        }
        return productList;
    }

    private void exceedQuantity(List<Product> productList, int buyQuantity) {
        int totalAvailableQuantity = productList.stream().mapToInt(Product::getQuantity).sum();
        if (buyQuantity > totalAvailableQuantity) {
            throw new IllegalArgumentException("[ERROR] 재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.");
        }
    }

    private int countPromotionProducts(List<Product> productList) {
        return (int) productList.stream()
                .filter(product -> product.getPromotion() != null && product.getPromotion().isActive())
                .count();
    }

    private void addOrUpdateProduct(List<Product> products, String name, int price, int quantity) {
        for (Product product : products) {
            if (product.getName().equals(name)) {
                product.modifyQuantity(-quantity);
                return;
            }
        }
        products.add(new Product(name, price, quantity, null));
    }

    private void restoreInventory(List<Product> modifiedProducts) {
        for (Product modifiedProduct : modifiedProducts) {
            List<Product> productList = inventoryService.getProducts().get(modifiedProduct.getName());
            for (Product product : productList) {
                if (product.getPrice() == modifiedProduct.getPrice()
                        && product.getPromotion() == modifiedProduct.getPromotion()) {
                    product.modifyQuantity(-modifiedProduct.getQuantity());
                }
            }
        }
    }

    private void printAllReceipt() {
        int totalQuantity = printReceipt1();
        printReceipt2(totalQuantity);
        reset();
    }

    private int printReceipt1() {
        int totalQuantity = 0;
        System.out.println("\n==============W 편의점================");
        System.out.println("상품명  \t\t수량\t금액");
        for (Product product : boughtProducts) {
            int freeQuantity = getFreeProductQuantity(product.getName());
            int totalProductQuantity = product.getQuantity() + freeQuantity;
            totalQuantity += totalProductQuantity;
            System.out.printf("%s\t\t%s \t%s\n", product.getName(), formatWithCommas(totalProductQuantity),
                    formatWithCommas(product.getPrice() * totalProductQuantity));
        }
        if (!freeProducts.isEmpty()) {
            System.out.println("=============증\t정===============");
            for (Product product : freeProducts) {
                if (product.getQuantity() > 0) {
                    System.out.printf("%s\t\t%s\n", product.getName(), formatWithCommas(product.getQuantity()));
                }
            }
        }
        return totalQuantity;
    }

    private int getFreeProductQuantity(String productName) {
        for (Product product : freeProducts) {
            if (product.getName().equals(productName)) {
                return product.getQuantity();
            }
        }
        return 0;
    }

    private void printReceipt2(int totalQuantity) {
        System.out.println("====================================");
        System.out.printf("총구매액\t\t%s\t%s\n", formatWithCommas(totalQuantity), formatWithCommas(totalAmount));
        System.out.printf("행사할인\t\t\t-%s\n", formatWithCommas(promotionDiscount));
        System.out.printf("멤버십할인\t\t\t-%s\n", formatWithCommas(membershipDiscount));
        System.out.printf("내실돈\t\t\t %s\n", formatWithCommas(totalAmount - promotionDiscount - membershipDiscount));
    }

    private void reset() {
        totalAmount = 0;
        promotionDiscount = 0;
        membershipDiscount = 0;
        nonPromotionAmount = 0;
        boughtProducts.clear();
        freeProducts.clear();
    }

    private boolean questionsValidation(String message) {
        while (true) {
            System.out.println(message);
            String input = Console.readLine();
            if (input.equalsIgnoreCase("Y")) {
                return true;
            } else if (input.equalsIgnoreCase("N")) {
                return false;
            } else {
                System.out.println("[ERROR] 잘못된 입력입니다. 'Y' 또는 'N'을 입력해 주세요.");
            }
        }
    }
}