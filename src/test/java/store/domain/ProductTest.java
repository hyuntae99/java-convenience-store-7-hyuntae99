package store.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ProductTest {

    private Product product;
    private Promotion promotion;

    @BeforeEach
    public void setUp() {
        promotion = new Promotion("탄산2+1", 2, 1, LocalDate.parse("2024-01-01"), LocalDate.parse("2024-12-31"));
        product = new Product("콜라", 1000, 10, promotion);
    }

    @Test
    @DisplayName("상품 이름 확인")
    public void 이름_확인() {
        assertEquals("콜라", product.getName());
    }

    @Test
    @DisplayName("상품 가격 확인")
    public void 가격_확인() {
        assertEquals(1000, product.getPrice());
    }

    @Test
    @DisplayName("상품 수량 확인")
    public void 수량_확인() {
        assertEquals(10, product.getQuantity());
    }

    @Test
    @DisplayName("상품 프로모션 확인")
    public void 프로모션_확인() {
        assertEquals(promotion, product.getPromotion());
    }

    @Test
    @DisplayName("상품 수량 감소 확인")
    public void 수량_감소_확인() {
        product.modifyQuantity(5);
        assertEquals(5, product.getQuantity());
    }

    @Test
    @DisplayName("상품의 수량보다 구매 수량이 큰 경우")
    public void 구매_수량_초과_감소_예외() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            product.modifyQuantity(15);
        });
        assertEquals("[ERROR] 재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.", exception.getMessage());
    }
}