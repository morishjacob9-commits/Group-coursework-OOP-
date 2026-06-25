package models;
import java.math.BigDecimal;

public class StudentAccount extends Account {
    public StudentAccount() { super("Student"); }
    @Override public BigDecimal getMinimumDeposit() { return new BigDecimal("10s000"); }
}