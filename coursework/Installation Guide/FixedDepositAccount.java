package models;
import java.math.BigDecimal;

public class FixedDepositAccount extends Account {
    public FixedDepositAccount() { super("Fixed Deposit"); }
    @Override public BigDecimal getMinimumDeposit() { return new BigDecimal("1000000"); }
}