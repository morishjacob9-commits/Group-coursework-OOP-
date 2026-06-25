package ui;

import database.DBConnection;
import models.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class BankFormApplication extends JFrame {

    private JTextField txtFirstName, txtLastName, txtNIN, txtEmail, txtConfirmEmail, txtPhone, txtOpeningDeposit, txtSecondNIN;
    private JPasswordField txtPIN, txtConfirmPIN;
    private JComboBox<Integer> cmbYear, cmbDay;
    private JComboBox<String> cmbMonth, cmbAccountType, cmbBranch;
    private JTextArea txtSummary;
    private JLabel lblSecondNIN, errFirstName, errLastName, errNIN, errSecondNIN, errEmail, errConfirmEmail, errPhone, errPIN, errConfirmPIN, errDOB, errDeposit;
    
    private final String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    private final String[] accountTypes = {"Savings", "Current", "Fixed Deposit", "Student", "Joint"};
    private final String[] branches = {"Kampala", "Gulu", "Mbarara", "Jinja", "Mbale"};

    public BankFormApplication() {
        setTitle("First Bank Uganda - New Account Opening Form");
        setSize(850, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 10, 4, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Initialize Form Fields and Error Markers
        initFields();

        // Layout Assembly Loop
        int row = 0;
        addFormRow(mainPanel, gbc, new JLabel("First Name:"), txtFirstName, errFirstName, row++);
        addFormRow(mainPanel, gbc, new JLabel("Last Name:"), txtLastName, errLastName, row++);
        addFormRow(mainPanel, gbc, new JLabel("National ID (NIN):"), txtNIN, errNIN, row++);
        
        // Contextual Row for Joint Account Constraint
        gbc.gridx = 0; gbc.gridy = row; mainPanel.add(lblSecondNIN, gbc);
        gbc.gridx = 1; mainPanel.add(txtSecondNIN, gbc);
        gbc.gridx = 2; mainPanel.add(errSecondNIN, gbc);
        row++;

        addFormRow(mainPanel, gbc, new JLabel("Email:"), txtEmail, errEmail, row++);
        addFormRow(mainPanel, gbc, new JLabel("Confirm Email:"), txtConfirmEmail, errConfirmEmail, row++);
        addFormRow(mainPanel, gbc, new JLabel("Phone Number (+256...):"), txtPhone, errPhone, row++);
        addFormRow(mainPanel, gbc, new JLabel("PIN (4-6 digits):"), txtPIN, errPIN, row++);
        addFormRow(mainPanel, gbc, new JLabel("Confirm PIN:"), txtConfirmPIN, errConfirmPIN, row++);

        // Date of Birth Dropdowns setup
        JPanel dobPanel = new JPanel(new GridLayout(1, 3, 5, 5));
        dobPanel.add(cmbYear); dobPanel.add(cmbMonth); dobPanel.add(cmbDay);
        gbc.gridx = 0; gbc.gridy = row; mainPanel.add(new JLabel("Date of Birth:"), gbc);
        gbc.gridx = 1; mainPanel.add(dobPanel, gbc);
        gbc.gridx = 2; mainPanel.add(errDOB, gbc);
        row++;

        addFormRow(mainPanel, gbc, new JLabel("Account Type:"), cmbAccountType, null, row++);
        addFormRow(mainPanel, gbc, new JLabel("Branch:"), cmbBranch, null, row++);
        addFormRow(mainPanel, gbc, new JLabel("Opening Deposit (UGX):"), txtOpeningDeposit, errDeposit, row++);

        // Actions and Control Row
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton btnSubmit = new JButton("Submit");
        JButton btnReset = new JButton("Reset");
        btnPanel.add(btnSubmit); btnPanel.add(btnReset);
        
        gbc.gridx = 1; gbc.gridy = row; mainPanel.add(btnPanel, gbc);

        // Footer Summary Panel
        JPanel SouthPanel = new JPanel(new BorderLayout(5, 5));
        SouthPanel.setBorder(BorderFactory.createTitledBorder("Account Summary is Below:"));
        txtSummary = new JTextArea(4, 50);
        txtSummary.setEditable(false);
        SouthPanel.add(new JScrollPane(txtSummary), BorderLayout.CENTER);

        add(new JScrollPane(mainPanel), BorderLayout.CENTER);
        add(SouthPanel, BorderLayout.SOUTH);

        // Control Logic Wireframes
        cmbYear.addActionListener(e -> updateDays());
        cmbMonth.addActionListener(e -> updateDays());
        cmbAccountType.addActionListener(e -> toggleJointFields());
        btnReset.addActionListener(e -> resetForm());
        btnSubmit.addActionListener(e -> processSubmission());

        updateDays();
        toggleJointFields();
    }

    private void initFields() {
        txtFirstName = new JTextField(20); txtLastName = new JTextField(20);
        txtNIN = new JTextField(20); txtEmail = new JTextField(20);
        txtConfirmEmail = new JTextField(20); txtPhone = new JTextField(20);
        txtOpeningDeposit = new JTextField(20); txtSecondNIN = new JTextField(20);
        txtPIN = new JPasswordField(20); txtConfirmPIN = new JPasswordField(20);

        lblSecondNIN = new JLabel("Second NIN (Joint):");
        
        cmbYear = new JComboBox<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = currentYear; i >= currentYear - 100; i--) cmbYear.addItem(i);

        cmbMonth = new JComboBox<>(months);
        cmbDay = new JComboBox<>();
        cmbAccountType = new JComboBox<>(accountTypes);
        cmbBranch = new JComboBox<>(branches);

        // Setup Error UI markers
        errFirstName = createErrLabel(); errLastName = createErrLabel();
        errNIN = createErrLabel(); errSecondNIN = createErrLabel();
        errEmail = createErrLabel(); errConfirmEmail = createErrLabel();
        errPhone = createErrLabel(); errPIN = createErrLabel();
        errConfirmPIN = createErrLabel(); errDOB = createErrLabel();
        errDeposit = createErrLabel();
    }

    private JLabel createErrLabel() {
        JLabel lbl = new JLabel("*");
        lbl.setForeground(Color.RED);
        lbl.setVisible(false);
        return lbl;
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, JLabel label, Component field, JLabel errLabel, int row) {
        gbc.gridy = row;
        gbc.gridx = 0; panel.add(label, gbc);
        gbc.gridx = 1; panel.add(field, gbc);
        if (errLabel != null) { gbc.gridx = 2; panel.add(errLabel, gbc); }
    }

    private void updateDays() {
        if (cmbYear.getSelectedItem() == null || cmbMonth.getSelectedItem() == null) return;
        int year = (int) cmbYear.getSelectedItem();
        int monthIndex = cmbMonth.getSelectedIndex();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthIndex);
        int maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        int selectedDayIdx = cmbDay.getSelectedIndex();
        cmbDay.removeAllItems();
        for (int i = 1; i <= maxDays; i++) cmbDay.addItem(i);
        if (selectedDayIdx >= 0 && selectedDayIdx < maxDays) cmbDay.setSelectedIndex(selectedDayIdx);
    }

    private void toggleJointFields() {
        boolean isJoint = "Joint".equals(cmbAccountType.getSelectedItem());
        lblSecondNIN.setVisible(isJoint);
        txtSecondNIN.setVisible(isJoint);
        if (!isJoint) { txtSecondNIN.setText(""); errSecondNIN.setVisible(false); }
    }

    private void resetForm() {
        txtFirstName.setText(""); txtLastName.setText(""); txtNIN.setText("");
        txtEmail.setText(""); txtConfirmEmail.setText(""); txtPhone.setText("");
        txtOpeningDeposit.setText(""); txtSecondNIN.setText(""); txtPIN.setText(""); txtConfirmPIN.setText("");
        cmbYear.setSelectedIndex(0); cmbMonth.setSelectedIndex(0); updateDays();
        cmbAccountType.setSelectedIndex(0); cmbBranch.setSelectedIndex(0);
        txtSummary.setText("");
        clearErrors();
    }

    private void clearErrors() {
        errFirstName.setVisible(false); errLastName.setVisible(false); errNIN.setVisible(false);
        errSecondNIN.setVisible(false); errEmail.setVisible(false); errConfirmEmail.setVisible(false);
        errPhone.setVisible(false); errPIN.setVisible(false); errConfirmPIN.setVisible(false);
        errDOB.setVisible(false); errDeposit.setVisible(false);
    }

    private void processSubmission() {
        clearErrors();
        StringBuilder errorMsg = new StringBuilder();
        boolean isValid = true;

        // Data Retrieval Strategy & Compaction
        String fName = txtFirstName.getText().trim();
        String lName = txtLastName.getText().trim();
        String nin = txtNIN.getText().trim().toUpperCase();
        String secNin = txtSecondNIN.getText().trim().toUpperCase();
        String email = txtEmail.getText().trim();
        String confEmail = txtConfirmEmail.getText().trim();
        String phone = txtPhone.getText().trim();
        String pin = new String(txtPIN.getPassword()).trim();
        String confPin = new String(txtConfirmPIN.getPassword()).trim();
        String depositStr = txtOpeningDeposit.getText().trim();

        // 1. Structural Polymorphism Mapping
        Account accountObj;
        String selectedType = (String) cmbAccountType.getSelectedItem();
        switch (selectedType) {
            case "Savings": accountObj = new SavingsAccount(); break;
            case "Current": accountObj = new CurrentAccount(); break;
            case "Fixed Deposit": accountObj = new FixedDepositAccount(); break;
            case "Student": accountObj = new StudentAccount(); break;
            case "Joint": accountObj = new JointAccount(); break;
            default: accountObj = new SavingsAccount();
        }

        // 2. Comprehensive Validation Core Engine
        if (!fName.matches("[A-Za-z]{2,30}")) { errFirstName.setVisible(true); errorMsg.append("- First Name structural violation (Letters only, 2-30 chars).\n"); isValid = false; }
        if (!lName.matches("[A-Za-z]{2,30}")) { errLastName.setVisible(true); errorMsg.append("- Last Name structural violation (Letters only, 2-30 chars).\n"); isValid = false; }
        if (!nin.matches("^[A-Z0-9]{14}$")) { errNIN.setVisible(true); errorMsg.append("- NIN must be exactly 14 alphanumeric characters.\n"); isValid = false; }
        
        if ("Joint".equals(selectedType)) {
            if (!secNin.matches("^[A-Z0-9]{14}$")) { errSecondNIN.setVisible(true); errorMsg.append("- Joint Account requires a valid second 14-character NIN.\n"); isValid = false; }
        }

        if (!email.matches("^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$")) { errEmail.setVisible(true); errorMsg.append("- Email format validation failure.\n"); isValid = false; }
        if (!email.equalsIgnoreCase(confEmail)) { errConfirmEmail.setVisible(true); errorMsg.append("- Email confirmations mismatch.\n"); isValid = false; }
        if (!phone.matches("^\\+256\\d{9}$")) { errPhone.setVisible(true); errorMsg.append("- Phone must match Ugandan blueprint +256XXXXXXXXX.\n"); isValid = false; }
        
        if (!pin.matches("^\\d{4,6}$") || pin.matches("^(\\d)\\1+$")) { errPIN.setVisible(true); errorMsg.append("- PIN configuration rules violated (4-6 digits, non-identical sequence).\n"); isValid = false; }
        if (!pin.equals(confPin)) { errConfirmPIN.setVisible(true); errorMsg.append("- PIN structural mismatch.\n"); isValid = false; }

        // Date / Age Calculations
        int year = (int) cmbYear.getSelectedItem();
        int month = cmbMonth.getSelectedIndex() + 1;
        int day = (int) cmbDay.getSelectedItem();
        LocalDate dob = LocalDate.of(year, month, day);
        int age = Period.between(dob, LocalDate.now()).getYears();

        if (age < 18 || age > 75) { errDOB.setVisible(true); errorMsg.append("- Operational Age constraint violation (Must be 18-75).\n"); isValid = false; }
        if ("Student".equals(selectedType) && (age < 18 || age > 25)) { errDOB.setVisible(true); errorMsg.append("- Student account parameters dictate target age window 18-25.\n"); isValid = false; }

        BigDecimal deposit = BigDecimal.ZERO;
        try {
            deposit = new BigDecimal(depositStr);
            if (deposit.compareTo(accountObj.getMinimumDeposit()) < 0) {
                errDeposit.setVisible(true);
                errorMsg.append("- Insufficient Opening Capital base. Minimum for ").append(selectedType).append(" is ").append(accountObj.getMinimumDeposit()).append(" UGX.\n");
                isValid = false;
            }
        } catch (NumberFormatException e) {
            errDeposit.setVisible(true);
            errorMsg.append("- Format failure processing numeric payload for opening deposit.\n");
            isValid = false;
        }

        // Halt on Validation Failures
        if (!isValid) {
            JOptionPane.showMessageDialog(this, errorMsg.toString(), "Validation Issues Encountered", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 3. Execution Phase (Database Sequence and Account Generation)
        String selectedBranch = (String) cmbBranch.getSelectedItem();
        Map<String, String> branchCodes = new HashMap<>();
        branchCodes.put("Kampala", "KLA"); branchCodes.put("Gulu", "GUL");
        branchCodes.put("Mbarara", "MBR"); branchCodes.put("Jinja", "JNJ"); branchCodes.put("Mbale", "MBL");
        
        String bCode = branchCodes.get(selectedBranch);
        int currentYear = LocalDate.now().getYear();

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false); // Enable transactional integrity bounds

            // Dynamic Atomic Sequence fetch via locking mechanics
            String selectSeqSql = "SELECT current_counter FROM AccountSequences WHERE branch_code = ? AND year_val = ? FOR UPDATE";
            int nextVal = 1;
            
            try (PreparedStatement psSeq = conn.prepareStatement(selectSeqSql)) {
                psSeq.setString(1, bCode);
                psSeq.setInt(2, currentYear);
                try (ResultSet rs = psSeq.executeQuery()) {
                    if (rs.next()) {
                        nextVal = rs.getInt("current_counter") + 1;
                        String updateSeqSql = "UPDATE AccountSequences SET current_counter = ? WHERE branch_code = ? AND year_val = ?";
                        try (PreparedStatement psUp = conn.prepareStatement(updateSeqSql)) {
                            psUp.setInt(1, nextVal);
                            psUp.setString(2, bCode);
                            psUp.setInt(3, currentYear);
                            psUp.executeUpdate();
                        }
                    } else {
                        String insertSeqSql = "INSERT INTO AccountSequences (branch_code, year_val, current_counter) VALUES (?, ?, ?)";
                        try (PreparedStatement psIns = conn.prepareStatement(insertSeqSql)) {
                            psIns.setString(1, bCode);
                            psIns.setInt(2, currentYear);
                            psIns.setInt(3, nextVal);
                            psIns.executeUpdate();
                        }
                    }
                }
            }

            // Frame Serialized Account Token String
            String accountNumber = String.format("%s-%d-%06d", bCode, currentYear, nextVal);

            // Append Core Account Metrics
            String insertAccountSql = "INSERT INTO Accounts (account_number, first_name, last_name, nin, email, phone_number, dob, account_type, branch, opening_deposit) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement psAcc = conn.prepareStatement(insertAccountSql)) {
                psAcc.setString(1, accountNumber);
                psAcc.setString(2, fName);
                psAcc.setString(3, lName);
                psAcc.setString(4, "Joint".equals(selectedType) ? nin + "/" + secNin : nin);
                psAcc.setString(5, email);
                psAcc.setString(6, phone);
                psAcc.setDate(7, Date.valueOf(dob));
                psAcc.setString(8, selectedType);
                psAcc.setString(9, selectedBranch);
                psAcc.setBigDecimal(10, deposit);
                psAcc.executeUpdate();
            }

            conn.commit(); // Close transactional verification window cleanly

            // 4. Update Application Display UI Contexts
            String outputRecord = String.format("ACC: %s | %s %s | %s | %s | DOB %s | %s | Deposit %,.0f | %s",
                    accountNumber, fName, lName, selectedType, selectedBranch, dob, phone, deposit.doubleValue(), email);
            
            txtSummary.setText(outputRecord);
            JOptionPane.showMessageDialog(this, "Account provisioned successfully for: " + fName + " " + lName, "Transaction Completed", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database storage processing error occurred: " + ex.getMessage(), "Persistence Deficit", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BankFormApplication().setVisible(true));
    }
}