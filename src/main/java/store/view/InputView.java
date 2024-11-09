package store.view;

import camp.nextstep.edu.missionutils.Console;
import java.util.ArrayList;
import java.util.List;
import store.dto.Order;

public class InputView {

    public static String readItem() {
        while (true) {
            try {
                System.out.println("구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])");
                String input = Console.readLine();
                validateInputFormat(input);
                return input;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static List<Order> parseOrders(String input) {
        List<Order> orders = new ArrayList<>();

        String[] items = input.split("\\],\\[");

        for (String item : items) {
            item = item.replaceAll("[\\[\\]]", "");
            String[] parts = item.split("-");
            validateItemFormat(parts);
            String name = parts[0].trim();
            int quantity = parseQuantity(parts[1].trim());
            orders.add(new Order(name, quantity));
        }
        return orders;
    }

    protected static void validateInputFormat(String input) {
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("[ERROR] 입력이 비어있습니다. 올바른 형식으로 입력해 주세요.");
        }
        if (!input.matches("(\\[\\S+-\\d+\\](,\\[\\S+-\\d+\\])*)")) {
            throw new IllegalArgumentException("[ERROR] 입력 형식이 올바르지 않습니다. 형식에 맞게 다시 입력해 주세요.");
        }
    }

    static void validateItemFormat(String[] parts) {
        if (parts.length != 2) {
            throw new IllegalArgumentException("[ERROR] 각 항목은 '[이름-수량]' 형식이어야 합니다. 다시 입력해 주세요.");
        }
    }

    private static int parseQuantity(String quantityStr) {
        try {
            int quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0) {
                throw new IllegalArgumentException("[ERROR] 수량은 1 이상의 정수여야 합니다.");
            }
            return quantity;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("[ERROR] 수량은 정수로 입력해 주세요.");
        }
    }
}
