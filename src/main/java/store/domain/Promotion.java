package store.domain;

import camp.nextstep.edu.missionutils.DateTimes;
import java.time.LocalDate;

public class Promotion {
    private String name;
    private int buy;
    private int get;
    private LocalDate startDate;
    private LocalDate endDate;

    public Promotion(String name, int buy, int get, LocalDate startDate, LocalDate endDate) {
        this.name = name;
        this.buy = buy;
        this.get = get;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getName() {
        return name;
    }

    public int getBuy() {
        return buy;
    }

    public int getGet() {
        return get;
    }

    public boolean isActive() {
        LocalDate today = LocalDate.from(DateTimes.now());
        return ((today.isEqual(startDate) || today.isAfter(startDate))) && ((today.isEqual(endDate) || today.isBefore(
                endDate)));
    }

    public boolean isActive(LocalDate today) {
        return ((today.isEqual(startDate) || today.isAfter(startDate))) && ((today.isEqual(endDate) || today.isBefore(
                endDate)));
    }
}