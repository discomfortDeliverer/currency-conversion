package ru.discomfortDeliverer.dto;

public class ExchangeDto {
    private Integer id;
    private Integer baseCurrency;
    private Integer targetCurrency;
    private Double rate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(Integer baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public Integer getTargetCurrency() {
        return targetCurrency;
    }

    public void setTargetCurrency(Integer targetCurrency) {
        this.targetCurrency = targetCurrency;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }
}
