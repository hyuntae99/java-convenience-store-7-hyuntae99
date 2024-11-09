package store.service;

import java.util.ArrayList;
import java.util.List;
import camp.nextstep.edu.missionutils.Console;
import store.domain.Buy;
import store.domain.Product;
import store.domain.Promotion;

public class Checkout {
    private Inventory inventory;
    private int totalAmount = 0;
    private int promotionDiscount = 0;
    private int membershipDiscount = 0;
    private int nonPromotionAmount = 0;
    private List<Product> boughtProducts = new ArrayList<>();
    private List<Product> freeProducts = new ArrayList<>();

    public Checkout(Inventory inventory) {
        this.inventory = inventory;
    }

    public void processOrders(List<Buy> buys) {
        List<Product> tempBoughtProducts = new ArrayList<>();
        List<Product> tempFreeProducts = new ArrayList<>();
        int tempTotalAmount = 0;
        int tempPromotionDiscount = 0;
        int tempNonPromotionAmount = 0;
        List<Product> modifiedProducts = new ArrayList<>();

        for (Buy buy : buys) {
            List<Product> productList = inventory.getProducts().get(buy.getName());
            if (productList == null) {
                throw new IllegalArgumentException("[ERROR] 존재하지 않는 상품입니다. 다시 입력해 주세요.");
            }

            int buyQuantity = buy.getQuantity();
            int totalAvailableQuantity = productList.stream().mapToInt(Product::getQuantity).sum();
            if (buyQuantity > totalAvailableQuantity) {
                throw new IllegalArgumentException("[ERROR] 재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.");
            }

            int remainingQuantity = buyQuantity;
            int freeQuantity = 0;
            int cost = 0;
            int nonPromotionQuantity = 0;

            // 행사 상품을 우선 소모
            for (Product product : productList) {
                if (product.getPromotion() != null && product.getPromotion().isActive() && product.getQuantity() > 0) {
                    Promotion promotion = product.getPromotion();
                    int promotionBuy = promotion.getBuy();
                    int promotionGet = promotion.getGet();

                    while (remainingQuantity >= promotionBuy) {
                        if (product.getQuantity() < (promotionBuy + promotionGet)) {
                            break;
                        }

                        if (remainingQuantity == promotionBuy) {
                            System.out.printf("현재 %s은(는) %d개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)\n", product.getName(), promotionGet);
                            String input = Console.readLine();
                            if (input.equalsIgnoreCase("Y")) {
                                cost += (promotionBuy + promotionGet) * product.getPrice();
                                freeQuantity += promotionGet;
                                product.reduceQuantity(promotionBuy + promotionGet);
                                modifiedProducts.add(new Product(product.getName(), product.getPrice(), promotionBuy + promotionGet, product.getPromotion()));
                                remainingQuantity -= promotionBuy;
                                addOrUpdateProduct(tempBoughtProducts, product.getName(), product.getPrice(), promotionBuy);
                                addOrUpdateProduct(tempBoughtProducts, product.getName(), product.getPrice(), promotionGet);
                                addOrUpdateProduct(tempFreeProducts, product.getName(), product.getPrice(), promotionGet);
                            } else {
                                cost += promotionBuy * product.getPrice();
                                product.reduceQuantity(promotionBuy);
                                modifiedProducts.add(new Product(product.getName(), product.getPrice(), promotionBuy, product.getPromotion()));
                                remainingQuantity -= promotionBuy;
                                addOrUpdateProduct(tempBoughtProducts, product.getName(), product.getPrice(), promotionBuy);
                            }
                        } else {
                            cost += (promotionBuy + promotionGet) * product.getPrice();
                            freeQuantity += promotionGet;
                            product.reduceQuantity(promotionBuy + promotionGet);
                            modifiedProducts.add(new Product(product.getName(), product.getPrice(), promotionBuy + promotionGet, product.getPromotion()));
                            remainingQuantity -= (promotionBuy + promotionGet);
                            addOrUpdateProduct(tempBoughtProducts, product.getName(), product.getPrice(), promotionBuy);
                            addOrUpdateProduct(tempBoughtProducts, product.getName(), product.getPrice(), promotionGet);
                            addOrUpdateProduct(tempFreeProducts, product.getName(), product.getPrice(), promotionGet);
                        }
                    }
                }
            }

            // 남은 수량에 대해 한 번에 확인
            if (remainingQuantity > 0) {
                nonPromotionQuantity = remainingQuantity;
                System.out.printf("현재 %s %d개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까? (Y/N)\n", buy.getName(), nonPromotionQuantity);
                String input = Console.readLine();
                if (input.equalsIgnoreCase("N")) {
                    System.out.println("[INFO] 결제가 취소되었습니다. 이전까지의 거래는 반영되지 않습니다.");
                    restoreInventory(modifiedProducts);
                    return;
                } else {
                    for (Product product : productList) {
                        if (product.getQuantity() > 0) {
                            int availableQuantity = Math.min(nonPromotionQuantity, product.getQuantity());
                            cost += availableQuantity * product.getPrice();
                            product.reduceQuantity(availableQuantity);
                            modifiedProducts.add(new Product(product.getName(), product.getPrice(), availableQuantity, product.getPromotion()));
                            addOrUpdateProduct(tempBoughtProducts, product.getName(), product.getPrice(), availableQuantity);
                            nonPromotionQuantity -= availableQuantity;

                            // 프로모션이 없는 상품의 금액을 따로 계산
                            tempNonPromotionAmount += availableQuantity * product.getPrice();
                        }
                        if (nonPromotionQuantity == 0) {
                            break;
                        }
                    }
                }
            }

            tempPromotionDiscount += freeQuantity * productList.get(0).getPrice();
            tempTotalAmount += cost;
        }

        System.out.print("멤버십 할인을 받으시겠습니까? (Y/N)\n");
        String input = Console.readLine();
        boolean applyMembershipDiscount = input.equalsIgnoreCase("Y");
        if (applyMembershipDiscount && tempNonPromotionAmount > 0) {
            membershipDiscount = Math.min((int) (tempNonPromotionAmount * 0.3), 8000);
        }

        // 반영된 값을 실제 변수로 갱신
        boughtProducts = tempBoughtProducts;
        freeProducts = tempFreeProducts;
        totalAmount = tempTotalAmount;
        promotionDiscount = tempPromotionDiscount;
        nonPromotionAmount = tempNonPromotionAmount;

        printAllReceipt();
    }

    private void addOrUpdateProduct(List<Product> products, String name, int price, int quantity) {
        for (Product product : products) {
            if (product.getName().equals(name)) {
                product.reduceQuantity(-quantity); // Increase quantity
                return;
            }
        }
        products.add(new Product(name, price, quantity, null));
    }

    private void restoreInventory(List<Product> modifiedProducts) {
        for (Product modifiedProduct : modifiedProducts) {
            List<Product> productList = inventory.getProducts().get(modifiedProduct.getName());
            for (Product product : productList) {
                if (product.getPrice() == modifiedProduct.getPrice() && product.getPromotion() == modifiedProduct.getPromotion()) {
                    product.reduceQuantity(-modifiedProduct.getQuantity()); // Restore quantity
                }
            }
        }
    }

    private void printAllReceipt() {
        int totalQuantity = printReceipt1();
        printReceipt2(totalQuantity);
        reset();
    }

    public int printReceipt1() {
        int totalQuantity = 0;
        System.out.println("\n==============W 편의점================");
        System.out.println("상품명\t\t수량\t금액");
        for (Product product : boughtProducts) {
            totalQuantity += product.getQuantity();
            System.out.printf("%s\t\t%d\t%d\n", product.getName(), product.getQuantity(), product.getPrice() * product.getQuantity());
        }
        if (!freeProducts.isEmpty()) {
            System.out.println("=============증\t정===============");
            for (Product product : freeProducts) {
                if (product.getQuantity() > 0) {
                    System.out.printf("%s\t\t%d\n", product.getName(), product.getQuantity());
                }
            }
        }
        return totalQuantity;
    }

    public void printReceipt2(int totalQuantity) {
        System.out.println("====================================");
        System.out.printf("총구매액\t\t%d\t%d\n", totalQuantity, totalAmount);
        System.out.printf("행사할인\t\t\t-%d\n", promotionDiscount);
        System.out.printf("멤버십할인\t\t\t-%d\n", membershipDiscount);
        System.out.printf("내실돈\t\t\t%d\n", totalAmount - promotionDiscount - membershipDiscount);
    }

    public void reset() {
        totalAmount = 0;
        promotionDiscount = 0;
        membershipDiscount = 0;
        nonPromotionAmount = 0;
        boughtProducts.clear();
        freeProducts.clear();
    }
}