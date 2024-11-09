package store;

import camp.nextstep.edu.missionutils.Console;
import java.util.List;
import store.dto.Order;
import store.service.InventoryService;
import store.service.PurchaseService;
import store.view.InputView;
import store.view.OutputView;

public class Application {
    public static void main(String[] args) {
        InventoryService inventoryService = new InventoryService();
        inventoryService.loadPromotions("src/main/resources/promotions.md");
        inventoryService.loadProducts("src/main/resources/products.md");

        PurchaseService purchaseService = new PurchaseService(inventoryService);
        boolean continueShopping = true;

        while (continueShopping) {
            OutputView.printProducts(inventoryService.getProducts());
            processUserInput(purchaseService);
            continueShopping = isContinueShopping();
        }
    }

    private static void processUserInput(PurchaseService purchaseService) {
        while (true) {
            try {
                String input = InputView.readItem();
                List<Order> orders = InputView.parseOrders(input);
                purchaseService.processOrders(orders);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static boolean isContinueShopping() {
        while (true) {
            System.out.println("감사합니다. 구매하고 싶은 다른 상품이 있나요? (Y/N)");
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
