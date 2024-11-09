package store.domain;

public class Buy {
    private final String name;
    private final int quantity;

    public Buy(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }
}
