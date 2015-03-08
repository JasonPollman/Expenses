/**
 * Created by Jason Pollman on 2/22/15.
 * ITCS 3312-001
 * Assignment #3 — Travel Expenses
 */

import javax.swing.*;
import javax.imageio.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedHashMap;
import java.text.DecimalFormat;
import java.io.*;

public class Expense {

    // The "Calculate" Button
    // Calculates trip expenses on click
    private JButton submit = new JButton("Calculate Expenses");

    // Constants
    private static final int FRAME_WIDTH    = 509;  // Frame Width
    private static final int FRAME_HEIGHT   = 424;  // Frame Height
    private static final int INPUT_WIDTH    = 5;    // JTextInput Width
    private static final int FRAME_BORDER   = 20;   // Padding for Window Frame

    private static final Color BKG_COLOR = new Color(230, 230, 230);

    /**
     * Constructor
     * Creates the GUI Frame & Panels
     */
    public Expense () {

        // Create the header:

        JPanel header = new JPanel(new FlowLayout());
        try {
            header.add(new JLabel(new ImageIcon(ImageIO.read(new File("img/logo.png")))));
        }
        catch (Exception err) { // Problem loading header image, use text...

            JLabel title = new JLabel("Travel Expense Calculator".toUpperCase());
            title.setFont(new Font("Arial", Font.BOLD, 24));
            header.add(title);
            header.setBorder(new MatteBorder(0, 0, 1, 0, new Color(0, 0, 0)));

        } // End try/catch block

        header.setBackground(BKG_COLOR);

        // Create the body
        // A hashmap in the format of: Input Label, Input Field...
        LinkedHashMap<JLabel, JTextField> fields = new LinkedHashMap<>();
        fields.put(new JLabel("Trip Length:  ",             SwingConstants.RIGHT), new JTextField("1",      INPUT_WIDTH));
        fields.put(new JLabel("Airfare Expenses:  ",        SwingConstants.RIGHT), new JTextField("0.00",   INPUT_WIDTH));
        fields.put(new JLabel("Car Rental Expenses:  ",     SwingConstants.RIGHT), new JTextField("0.00",   INPUT_WIDTH));
        fields.put(new JLabel("Miles Driven:  ",            SwingConstants.RIGHT), new JTextField("0",      INPUT_WIDTH));
        fields.put(new JLabel("Parking Fees:  ",            SwingConstants.RIGHT), new JTextField("0.00",   INPUT_WIDTH));
        fields.put(new JLabel("Taxi Fees:  ",               SwingConstants.RIGHT), new JTextField("0.00",   INPUT_WIDTH));
        fields.put(new JLabel("Event Registration Fees:  ", SwingConstants.RIGHT), new JTextField("0.00",   INPUT_WIDTH));
        fields.put(new JLabel("Lodging Charges:  ",         SwingConstants.RIGHT), new JTextField("0.00",   INPUT_WIDTH));

        // Create the footer
        LinkedHashMap<JLabel, JLabel> results = new LinkedHashMap<>();

        // The total trip's expenses
        JLabel totalExpenses = new JLabel("Total Expenses:", SwingConstants.RIGHT);
        results.put(totalExpenses, new JLabel("$0.00", SwingConstants.LEFT));

        // The trip's allowable expenses
        JLabel allowableExpenses = new JLabel("Allowable Expenses:", SwingConstants.RIGHT);
        results.put(allowableExpenses, new JLabel("$0.00", SwingConstants.LEFT));

        // The "excess charges" (total - allowable)
        JLabel excessCharges = new JLabel("Excess Charges:", SwingConstants.RIGHT);
        results.put(excessCharges, new JLabel("$0.00", SwingConstants.LEFT));

        // "Amount saved" (allowable - total)
        JLabel amountSaved = new JLabel("Amount Saved:", SwingConstants.RIGHT);
        results.put(amountSaved, new JLabel("$0.00", SwingConstants.LEFT));


        JPanel calculations = new JPanel(new GridLayout(4, 2));
        calculations.setBackground(BKG_COLOR);
        for (JLabel i : results.keySet()) {
            i.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 50));
            calculations.add(i);               // Add Input Labels
            calculations.add(results.get(i));   // Add Input Fields
        }

        // Simple disclaimer for employee notification...
        JLabel disclaimer = new JLabel("\n\n*Employee is responsible for all Excess Charges out of pocket.", SwingConstants.CENTER);
        disclaimer.setFont(new Font("Arial", Font.ITALIC, 10));
        disclaimer.setBackground(BKG_COLOR);

        JPanel disclaimerWrapper = new JPanel(new GridLayout(1, 1));
        disclaimerWrapper.setBackground(BKG_COLOR);
        disclaimerWrapper.add(disclaimer);

        // Add the action listener for the onclick action
        submit.addActionListener(new CalculateAction(submit, results, fields));

        JPanel footer = new JPanel();
        footer.setBackground(BKG_COLOR);

        // Add items to the footer...
        footer.add(submit);
        footer.add(Box.createVerticalStrut(10));
        footer.add(calculations);
        footer.add(Box.createVerticalStrut(20));
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));

        // Add the input fields to the panel
        JPanel body = new JPanel(new GridLayout(8, 2));
        body.setBackground(BKG_COLOR);

        for (JLabel i : fields.keySet()) {
            body.add(i);               // Add Input Labels
            body.add(fields.get(i));   // Add Input Fields

            // Update the results on field focus (tab)...
            fields.get(i).addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    submit.doClick();
                }

                @Override
                public void focusLost(FocusEvent e) {

                }
            });
        }

        // Update the results on field focus (tab)...
        submit.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                submit.doClick();
            }

            @Override
            public void focusLost(FocusEvent e) {

            }
        });

        body.setBorder(new EmptyBorder(10, 0, 10, 0));

        // Set frame options
        JFrame frame = new JFrame("Travel Expenses");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);

        // Add panels to the wrapper panel
        JPanel wrapper = new JPanel(new GridLayout(4, 2));
        wrapper.setBackground(BKG_COLOR);
        wrapper.add(header);
        wrapper.add(body);
        wrapper.add(footer);
        wrapper.add(disclaimerWrapper);

        // Add the wrapper panel to the frame
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBorder(new EmptyBorder(FRAME_BORDER, FRAME_BORDER, FRAME_BORDER, FRAME_BORDER));
        frame.add(wrapper);

        // Show the frame...
        frame.pack();
        frame.setResizable(false);
        frame.getContentPane().setBackground(BKG_COLOR);

        // Set initial values
        submit.doClick();
        frame.setVisible(true);

        Rectangle r = frame.getBounds();


    } // End Constructor

    /**
     * Calculates the trips expenses...
     */
    static class CalculateAction implements ActionListener {

        JComponent caller;

        LinkedHashMap   fields,     // The input fields (as a hashmap)
                        results;    // The "result" JLabels

        JLabel  totalExpenses,      // The total expenses label
                allowableExpenses,  // The allowable expenses label
                excessCharges,      // Excess Charges label
                amountSaved;        // Amount Saved label

        ImageIcon icon = null;      // The company logo, to stick on error messages...

        /**
         * Constructor
         * @param b The callee of this constructor (submit button, most likely)
         * @param r The results hashmap containing the "results" JLabels
         * @param f The fields hashmap containing the JInputLabels/Fields for each item
         */
        public CalculateAction (JComponent b, LinkedHashMap r, LinkedHashMap f) {

            // Get icon for message dialogs
            try {
                icon = new ImageIcon(ImageIO.read(new File("img/icon.png")));
            }
            catch(Exception err) {
                // Do nothing...
            }

            caller  = b;
            fields  = f;
            results = r;

            String comp;

            // Add the input fields to the panel
            for (Object i : results.keySet()) {
                comp = ((JLabel) i).getText();
                switch(comp) {
                    case "Total Expenses:":
                        totalExpenses = (JLabel) results.get(i);
                        break;

                    case "Allowable Expenses:":
                        allowableExpenses = (JLabel) results.get(i);
                        break;

                    case "Excess Charges:":
                        excessCharges = (JLabel) results.get(i);
                        break;

                    case "Amount Saved:":
                        amountSaved = (JLabel) results.get(i);
                        break;
                }
            }
        }


        /**
         * When a user clicks the "Calculate" Button...
         * @param e The event
         */
        public void actionPerformed (ActionEvent e) {

            DecimalFormat df = new DecimalFormat( "#,###,###,##0.00" );

            String strAmount;
            String fieldName;

            int days = 0;

            float total = 0;
            float airfare, carRentalFees, personalMiles, eventFees;
            airfare = carRentalFees = personalMiles = eventFees = 0f;

            // If the user has entered anything into "Mile Driven" this is true...
            boolean usedOwnCar = true;

            for (Object i : fields.keySet()) {

                strAmount = ((JTextField) fields.get(i)).getText();
                if(strAmount.equals("")) strAmount = "0.00";

                JTextField field = ((JTextField) fields.get(i));
                fieldName = ((JLabel) i).getText();
                fieldName = fieldName.substring(0, fieldName.length() - 3);

                float cleanValue = convertToNumber(strAmount, fieldName, field);
                if(Float.isNaN(cleanValue)) break;

                switch (fieldName) {

                    case "Trip Length": // Set the number of days...

                        days = (int) cleanValue;

                        if (!(cleanValue == days)) {
                            ((JTextField) fields.get(i)).setText(Integer.toString(days));
                        }

                        if (days < 1) {
                            ((JTextField) fields.get(i)).setText("1");
                            days = 1;
                        }

                        total = days * 40; // For daily food costs...
                        break;

                    case "Miles Driven": // Calculate the number of "personal miles driven"

                        total += .27 * (int) cleanValue;
                        break;

                    default: // Otherwise, accumulate total expenses

                        total += cleanValue;
                        break;

                } // End switch block


                // ----------- Lock down the Miles Driven field if Airfare or Car Rental fields != 0) ----------- //

                if((fieldName.equals("Car Rental Expenses") || fieldName.equals("Airfare Expenses")) && cleanValue != 0)
                    usedOwnCar = false;

                if(fieldName.equals("Miles Driven")) {

                    if(!usedOwnCar) {
                        cleanValue = 0;             // Revert the value, since you can't drive your own car AND have (airfare OR rental car)...
                        field.setEditable(false);   // Lock the field
                        field.setText("0");
                        field.setForeground(new Color(200, 200, 200)); // Dim the field...
                    }
                    else { // Both Airfare AND Car Rental == 0, Unlock the field...
                        field.setEditable(true);
                        field.setForeground(Color.BLACK);
                    }

                } // End if block

                if(fieldName.equals("Airfare Expenses"))        airfare         = cleanValue;
                if(fieldName.equals("Car Rental Expenses"))     carRentalFees   = cleanValue;
                if(fieldName.equals("Miles Driven"))            personalMiles   = cleanValue;
                if(fieldName.equals("Event Registration Fees")) eventFees       = cleanValue;

            } // End for loop

            // Display Total Charges
            totalExpenses.setText("$" + df.format(total));

            // Calculate Allowable Expenses
            float allowable = calculateAllowable(days, airfare, carRentalFees, personalMiles, eventFees);
            allowableExpenses.setText("$" + df.format(allowable));

            // Calculate Excess Charges
            float excess = total - allowable;
            if(excess < 0) excess = 0;
            setFontColorBasedOnAmount(-excess, excessCharges);
            excessCharges.setText("$" + df.format(excess));

            // Calculate Amount Saved
            Float saved = allowable - total;
            if(saved < 0) saved = 0.0f;
            setFontColorBasedOnAmount(saved, amountSaved);
            amountSaved.setText("$" + df.format(saved));

            // Set the total color based on allowable amount...
            setFontColorBasedOnAmount(allowable - total, totalExpenses);

        } // End actionPerformed()


        /**
         * Sets a JLabel's font color based on the amount (< || > || == 0)
         * @param amount The amount to base formatting
         * @param field The JLabel
         */
        private void setFontColorBasedOnAmount(float amount, JLabel field) {

            if(amount < 0) {
                field.setForeground(new Color(190, 0, 0));  // Red
            }
            else if(amount > 0) {
                field.setForeground(new Color(0, 130, 0));  // Green
            }
            else {
                field.setForeground(Color.BLACK);           // Black
            }

        } // End setFontColorBasedOnAmount()


        /**
         * Calculates the allowable trip expenses
         * @param days The duration of the trip in days
         * @param airfare The airfare costs
         * @param carRentalFees The car rental expenses
         * @param personalMiles The number of personal miles driven
         * @param eventFees The cost of event registration
         * @return The allowable amount
         */
        private float calculateAllowable(int days, float airfare, float carRentalFees, float personalMiles, float eventFees) {

            float allowable = 0.0f;

            allowable   += 40 * days;       // For meals
            allowable   += airfare;         // For airfare expenses
            allowable   += carRentalFees;   // For car rental expenses
            allowable   += eventFees;       // For event expenses

            // Add the parking fees, or the maximum allowable if exceeded...
            allowable += 10 * days;

            // Add the lodging charges, or the maximum allowable if exceeded...
            allowable += 95 * days;

            // Add the taxi fees, or the maximum allowable if exceeded...
            allowable += 20 * days;

            // Add the miles driven, or the maximum allowable if exceeded...
            allowable += .27 * personalMiles;

            return allowable;

        } // End calculateAllowable()


        /**
         * Custom exception for "Invalid Values" (In this case, < 0)
         */
        public class InvalidValueException extends Exception {
            public InvalidValueException(String message) {
                super(message);
            }
        }


        /**
         * Converts a string to a number, and checks that it's value is >= 0
         * @param s The string to convert
         * @param fieldName The name of the field (to print in error messages)
         * @param field The actual JTextField
         * @return The value as a float || NaN otherwise
         */
        private float convertToNumber (String s, String fieldName, JTextField field) {

            float value;

            try {
                value = Float.parseFloat(s);
                if(value < 0) throw new InvalidValueException("");
                return value;
            }
            catch(NumberFormatException err) {
                JOptionPane.showMessageDialog(null, "Field '" + fieldName + "' must contain a number.", "Input Error", JOptionPane.PLAIN_MESSAGE, icon);
                if(fieldName.equals("Trip Length")) field.setText("1"); else field.setText("0.00");
            }
            catch(InvalidValueException err) {
                JOptionPane.showMessageDialog(null, "Field '" + fieldName + "' must contain a positive number.", "Input Error", JOptionPane.PLAIN_MESSAGE, icon);
                if(fieldName.equals("Trip Length")) field.setText("1"); else field.setText("0.00");
            }
            catch(Exception err) {
                JOptionPane.showMessageDialog(null, "Field '" + fieldName + "' contains an invalid value.", "Input Error", JOptionPane.PLAIN_MESSAGE, icon);
                if(fieldName.equals("Trip Length")) field.setText("1"); else field.setText("0.00");
            }

            return Float.NaN;

        } // End convertToNumber()

    } // End CalculateAction Class

} // End Expense Class
