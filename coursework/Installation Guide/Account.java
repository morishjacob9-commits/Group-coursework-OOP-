package models;

import java.math.BigDecimal;

public abstract class Account {
    private String accountType;

    public Account(String accountType) {
        this.accountType = accountType;
    }

    public String getAccountType() {
        return accountType;
    }

    public abstract BigDecimal getMinimumDeposit();
}