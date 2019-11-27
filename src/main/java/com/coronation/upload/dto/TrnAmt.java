package com.coronation.upload.dto;

import java.math.BigDecimal;

public class TrnAmt {
    private BigDecimal amountValue;

    private String currencyCode;

    public BigDecimal getAmountValue() {
        return amountValue;
    }

    public void setAmountValue(BigDecimal amountValue) {
        this.amountValue = amountValue;
    }

    public String getCurrencyCode ()
    {
        return currencyCode;
    }

    public void setCurrencyCode (String currencyCode)
    {
        this.currencyCode = currencyCode;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [amountValue = "+amountValue+", currencyCode = "+currencyCode+"]";
    }
}
