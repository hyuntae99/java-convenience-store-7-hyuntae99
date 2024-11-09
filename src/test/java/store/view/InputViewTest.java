package store.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import store.dto.Order;

import java.util.List;

public class InputViewTest {

    private InputView inputView;

    @BeforeEach
    public void setUp() {
        inputView = new InputView();
    }

    @Test
    @DisplayName("올바른 형식의 입력을 읽고 파싱 확인")
    public void 올바른_형식_입력_확인() {
        String input = "[사이다-2],[감자칩-1]";
        List<Order> orders = InputView.parseOrders(input);

        assertEquals(2, orders.size(), "주문 항목의 수는 2이어야 합니다.");
        assertEquals("사이다", orders.get(0).getName(), "첫 번째 항목은 '사이다'이어야 합니다.");
        assertEquals(2, orders.get(0).getQuantity(), "첫 번째 항목의 수량은 2이어야 합니다.");
        assertEquals("감자칩", orders.get(1).getName(), "두 번째 항목은 '감자칩'이어야 합니다.");
        assertEquals(1, orders.get(1).getQuantity(), "두 번째 항목의 수량은 1이어야 합니다.");
    }

    @Test
    @DisplayName("잘못된 형식의 입력에 대한 예외 확인1")
    public void 잘못된_형식_입력_예외_확인_1() {
        String input = "[사이다2][감자칩1]";
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            InputView.validateInputFormat(input);
        });

        assertEquals("[ERROR] 입력 형식이 올바르지 않습니다. 형식에 맞게 다시 입력해 주세요.", exception.getMessage());
    }

    @Test
    @DisplayName("잘못된 형식의 입력에 대한 예외 확인2")
    public void 잘못된_형식_입력_예외_확인_2() {
        String input = "[사이다-2][감자칩/1]";
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            InputView.validateInputFormat(input);
        });

        assertEquals("[ERROR] 입력 형식이 올바르지 않습니다. 형식에 맞게 다시 입력해 주세요.", exception.getMessage());
    }

    @Test
    @DisplayName("수량이 0 이하인 경우 예외 확인")
    public void 수량_음수_또는_0_예외_확인() {
        String input = "[사이다-2],[감자칩-0]";
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            InputView.parseOrders(input);
        });

        assertEquals("[ERROR] 수량은 1 이상의 정수여야 합니다.", exception.getMessage());
    }

    @Test
    @DisplayName("입력이 비어 있는 경우 예외 확인")
    public void 빈_입력_예외_확인() {
        String input = "";
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            InputView.validateInputFormat(input);
        });

        assertEquals("[ERROR] 입력이 비어있습니다. 올바른 형식으로 입력해 주세요.", exception.getMessage());
    }
}
