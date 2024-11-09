package store.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PromotionTest {

    private Promotion promotion;

    @BeforeEach
    public void setUp() {
        promotion = new Promotion("탄산2+1", 2, 1, LocalDate.parse("2024-01-01"), LocalDate.parse("2024-12-31"));
    }

    @Test
    @DisplayName("프로모션 이름 확인")
    public void 이름_확인() {
        assertEquals("탄산2+1", promotion.getName());
    }

    @Test
    @DisplayName("구매 조건 확인")
    public void 구매조건_확인() {
        assertEquals(2, promotion.getBuy());
    }

    @Test
    @DisplayName("증정 조건 확인")
    public void 증정조건_확인() {
        assertEquals(1, promotion.getGet());
    }

    @Test
    @DisplayName("프로모션 활성 여부 - 활성 상태 확인")
    public void 프로모션_활성_상태_확인() {
        LocalDate today = LocalDate.parse("2024-06-01");
        assertTrue(promotion.isActive(today));
    }

    @Test
    @DisplayName("프로모션 활성 여부 - 비활성 상태 확인")
    public void 프로모션_비활성_상태_확인() {
        LocalDate today = LocalDate.parse("2025-01-01");
        assertFalse(promotion.isActive(today));
    }
}
