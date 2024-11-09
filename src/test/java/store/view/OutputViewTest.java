package store.view;

import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import store.domain.Product;
import store.domain.Promotion;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OutputViewTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("상품 목록 출력 확인")
    public void 상품_목록_출력_확인() {
        Map<String, List<Product>> products = new HashMap<>();
        List<Product> beverageList = new ArrayList<>();
        beverageList.add(new Product("콜라", 1300, 0, new Promotion("탄산2+1", 2, 1, LocalDate.parse("2024-01-01"), LocalDate.parse("2024-12-31"))));
        beverageList.add(new Product("콜라", 1300, 10, null));
        products.put("콜라", beverageList);

        OutputView.printProducts(products);

        String lineSeparator = System.lineSeparator();
        String expectedOutput = "안녕하세요. W편의점입니다." + lineSeparator +
                "현재 보유하고 있는 상품입니다." + lineSeparator +
                "- 콜라 1300원 재고 없음 탄산2+1" + lineSeparator +
                "- 콜라 1300원 10개 " + lineSeparator;

        assertEquals(normalize(expectedOutput), normalize(outContent.toString()));
    }

    private String normalize(String input) {
        return input.replaceAll("\\s+", "");
    }
} 
