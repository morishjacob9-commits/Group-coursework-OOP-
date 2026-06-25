package models;
import java.math.BigDecimal;

public class CurrentAccount extends Account {
    public CurrentAccount() { super("Current"); }
    @Override public BigDecimal getMinimumDeposit() { return new BigDecimal("200000"); }
}