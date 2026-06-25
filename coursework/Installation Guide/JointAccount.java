package models;
import java.math.BigDecimal;

public class JointAccount extends Account {
    public JointAccount() { super("Joint"); }
    @Override public BigDecimal getMinimumDeposit() { return new BigDecimal("100000"); }
}