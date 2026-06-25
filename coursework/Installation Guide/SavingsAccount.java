package models;
import java.math.BigDecimal;

public class SavingsAccount extends Account {
    public SavingsAccount() { super("Savings"); }
    @Override public BigDecimal getMinimumDeposit() { return new BigDecimal("50000"); }
}