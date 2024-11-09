package store.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import store.domain.Product;
import store.domain.Promotion;

public class InventoryServiceTest {

    private InventoryService inventoryService;
    private final String promotionsFilePath = "test_promotions.csv";
    private final String productsFilePath = "test_products.csv";

    @BeforeEach
    public void setUp() throws IOException {
        inventoryService = new InventoryService();
        createPromotionsFile();
        createProductsFile();
        inventoryService.loadPromotions(promotionsFilePath);
        inventoryService.loadProducts(productsFilePath);
    }

    @Test
    @DisplayName("프로모션 파일 로드 확인")
    public void 프로모션_파일_로드_확인() {
        Promotion promotion = inventoryService.getProducts().get("콜라").get(0).getPromotion();
        assertNotNull(promotion, "프로모션이 정상적으로 로드되어야 합니다.");
        assertEquals("탄산2+1", promotion.getName(), "프로모션 이름이 일치해야 합니다.");
        assertEquals(2, promotion.getBuy(), "프로모션의 구매 수량이 일치해야 합니다.");
        assertEquals(1, promotion.getGet(), "프로모션의 증정 수량이 일치해야 합니다.");
        assertEquals(LocalDate.parse("2024-01-01"), promotion.getStartDate(), "프로모션 시작일이 일치해야 합니다.");
        assertEquals(LocalDate.parse("2024-12-31"), promotion.getEndDate(), "프로모션 종료일이 일치해야 합니다.");
    }

    @Test
    @DisplayName("상품 파일 로드 확인")
    public void 상품_파일_로드_확인() {
        Map<String, List<Product>> products = inventoryService.getProducts();
        assertEquals(2, products.get("콜라").size(), "콜라 상품이 2개 로드되어야 합니다.");
        Product product1 = products.get("콜라").get(0);
        assertEquals(1300, product1.getPrice(), "첫 번째 콜라의 가격이 일치해야 합니다.");
        assertEquals(0, product1.getQuantity(), "첫 번째 콜라의 수량이 일치해야 합니다.");
        Product product2 = products.get("콜라").get(1);
        assertEquals(1300, product2.getPrice(), "두 번째 콜라의 가격이 일치해야 합니다.");
        assertEquals(10, product2.getQuantity(), "두 번째 콜라의 수량이 일치해야 합니다.");
    }

    private void createPromotionsFile() throws IOException {
        File file = new File(promotionsFilePath);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("name,buy,get,startDate,endDate\n");
            writer.write("탄산2+1,2,1,2024-01-01,2024-12-31\n");
        }
    }

    private void createProductsFile() throws IOException {
        File file = new File(productsFilePath);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("name,price,quantity,promotion\n");
            writer.write("콜라,1300,0,탄산2+1\n");
            writer.write("콜라,1300,10,null\n");
        }
    }
}
