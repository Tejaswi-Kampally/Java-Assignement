import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AgeDOBCalculator extends JFrame implements ActionListener {
    private JComboBox<String> optionComboBox;
    private JTextField dateInputField;
    private JTextField referenceDateField;
    private JTextArea resultArea;
    private JButton calculateButton;

    public AgeDOBCalculator() {
        setTitle("Age and DOB Calculator");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        // Create components
        optionComboBox = new JComboBox<>(new String[]{"Select Option", "DOB", "AGE"});
        dateInputField = new JTextField(20);
        referenceDateField = new JTextField(20);
        resultArea = new JTextArea(5, 30);
        resultArea.setEditable(false);
        calculateButton = new JButton("Calculate");

        // Add components to the frame
        add(new JLabel("Choose an Option:"));
        add(optionComboBox);
        add(new JLabel("Enter DOB or Age (use 'dlc' for delimiter):"));
        add(dateInputField);
        add(new JLabel("Enter Reference Date (e.g., DD-MM-YYYY):"));
        add(referenceDateField);
        add(calculateButton);
        add(new JScrollPane(resultArea)); // Scroll pane for result area

        // Add action listener to the button
        calculateButton.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        String selectedOption = (String) optionComboBox.getSelectedItem();
        String dateParam = dateInputField.getText().trim();
        String referenceDateParam = referenceDateField.getText().trim();

        if (selectedOption.equals("Select Option")) {
            resultArea.setText("Please select a valid option.");
            return;
        }

        // Assuming a standard date format for simplicity
        String dateFormat = "dd-MM-yyyy"; // Change as needed
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        sdf.setLenient(false);

        try {
            if (selectedOption.equals("DOB")) {
                Date dob = sdf.parse(dateParam);
                String ageResult = calculateAge(dob, referenceDateParam, sdf);
                resultArea.setText(ageResult);
            } else if (selectedOption.equals("AGE")) {
                Date dob = calculateDOB(dateParam, referenceDateParam, sdf);
                if (dob != null) {
                    resultArea.setText("DOB is: " + sdf.format(dob));
                } else {
                    resultArea.setText("Invalid age input format. Please use 'yearsdlcmonthsdlcdays' format.");
                }
            }
        } catch (ParseException ex) {
            resultArea.setText("Invalid date format or date: " + ex.getMessage());
        } catch (NumberFormatException ex) {
            resultArea.setText("Error in age input: " + ex.getMessage());
        } catch (Exception ex) {
            resultArea.setText("An unexpected error occurred: " + ex.getMessage());
        }
    }

    private String calculateAge(Date dob, String currentDateString, SimpleDateFormat sdf) throws ParseException {
        Date currentDate = sdf.parse(currentDateString);
        Calendar birthDate = Calendar.getInstance();
        birthDate.setTime(dob);

        Calendar current = Calendar.getInstance();
        current.setTime(currentDate);

        int years = current.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);
        int months = current.get(Calendar.MONTH) - birthDate.get(Calendar.MONTH);
        int days = current.get(Calendar.DAY_OF_MONTH) - birthDate.get(Calendar.DAY_OF_MONTH);

        if (days < 0) {
            months--;
            current.add(Calendar.MONTH, -1);
            days += current.getActualMaximum(Calendar.DAY_OF_MONTH);
        }

        if (months < 0) {
            years--;
            months += 12;
        }

        return String.format("Age is: %d years, %d months, %d days", years, months, days);
    }

    private Date calculateDOB(String ageString, String currentDateString, SimpleDateFormat sdf) throws ParseException {
        String[] ageParts = ageString.split("dlc");
        if (ageParts.length != 3) {
            return null;
        }

        int years, months, days;
        try {
            years = Integer.parseInt(ageParts[0]);
            months = Integer.parseInt(ageParts[1]);
            days = Integer.parseInt(ageParts[2]);
        } catch (NumberFormatException ex) {
            throw new NumberFormatException("Invalid numbers in age input.");
        }

        Date currentDate = sdf.parse(currentDateString);
        Calendar current = Calendar.getInstance();
        current.setTime(currentDate);

        // Subtracting years, months, and days to find DOB
        current.add(Calendar.YEAR, -years);
        current.add(Calendar.MONTH, -months);
        current.add(Calendar.DAY_OF_MONTH, -days);

        return current.getTime();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AgeDOBCalculator app = new AgeDOBCalculator();
            app.setVisible(true);
        });
    }
}
