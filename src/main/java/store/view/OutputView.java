package store.view;

import java.util.List;
import java.util.Map;
import store.domain.Product;

public class OutputView {
    public static void printProducts(Map<String, List<Product>> products) {
        printHello();
        for (String key : products.keySet()) {
            for (Product product : products.get(key)) {
                printProduct(product);
            }
        }
    }

    public static void printHello() {
        System.out.println("안녕하세요. W편의점입니다.");
        System.out.println("현재 보유하고 있는 상품입니다.");
    }

    public static void printProduct(Product product) {
        System.out.printf("- %s %d원 %s %s\n",
                product.getName(),
                product.getPrice(),
                product.getQuantity() != 0 ? product.getQuantity() + "개" : "재고 없음",
                product.getPromotion() != null ? product.getPromotion().getName() : "");
    }
}