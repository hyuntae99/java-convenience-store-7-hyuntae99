package store.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import store.dto.Order;

public class PurchaseServiceTest {

    private PurchaseService purchaseService;
    private InventoryService inventoryService;
    private final String promotionsFilePath = "test_promotions.csv";
    private final String productsFilePath = "test_products.csv";

    @BeforeEach
    public void setUp() {
        inventoryService = new InventoryService();
        inventoryService.loadPromotions(promotionsFilePath);
        inventoryService.loadProducts(productsFilePath);
        purchaseService = new PurchaseService(inventoryService);
    }

    @Test
    @DisplayName("존재하지 않는 상품 주문 시 예외 발생 확인")
    public void 존재하지_않는_상품_주문_확인() {
        List<Order> orders = new ArrayList<>();
        orders.add(new Order("없는상품", 2));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            purchaseService.processOrders(orders);
        });
        assertEquals("[ERROR] 존재하지 않는 상품입니다. 다시 입력해 주세요.", exception.getMessage());
    }

    @Test
    @DisplayName("재고 수량 초과 주문 시 예외 발생 확인")
    public void 재고_수량_초과_주문_확인() {
        List<Order> orders = new ArrayList<>();
        orders.add(new Order("콜라", 20)); // 재고보다 많은 수량을 주문

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            purchaseService.processOrders(orders);
        });
        assertEquals("[ERROR] 재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.", exception.getMessage());
    }
}
