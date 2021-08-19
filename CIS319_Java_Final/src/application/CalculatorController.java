package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class CalculatorController {

	public enum opType { // types of operations that can be calculated
		DIVIDE, MULTIPLY, SUBTRACT, ADD
	}

	// Filename for last value of readout persistence on open
	private static String lastValFileName = "last_value.txt";

	private boolean refreshDisplay = false;
	// Calculator memory
	private BigDecimal memory;
	// Holds first number while second number is being entered for operation
	private BigDecimal entered;
	private opType operation; // current operation selected
	private opType lastOperation; // used for subsequent = ( 2 + 3 =: 5 =: 8)

	// sets of buttons
	private Set<Button> numerics;
	private Set<Button> operations;
	private Set<Button> memoryFunctions;

	// FXML controls
	/* Display */
	@FXML
	private Label readout;
	/* Memory Function Buttons */

	@FXML
	private Button mc;
	@FXML
	private Button mr;
	@FXML
	private Button mPlus;
	@FXML
	private Button mMinus;
	/* Math Operations Buttons */

	@FXML
	private Button slash;
	@FXML
	private Button c;
	@FXML
	private Button ce;
	@FXML
	private Button plusMinus;
	@FXML
	private Button star;
	@FXML
	private Button minus;
	@FXML
	private Button plus;
	@FXML
	private Button equal;

	/* Numeric Buttons */
	@FXML
	private Button decimal;
	@FXML
	private Button zero;
	@FXML
	private Button one;
	@FXML
	private Button two;
	@FXML
	private Button three;
	@FXML
	private Button four;
	@FXML
	private Button five;
	@FXML
	private Button six;
	@FXML
	private Button seven;
	@FXML
	private Button eight;
	@FXML
	private Button nine;

	// called by FXMLLoader to initialize the controller
	public void initialize() throws IOException {
		// Add buttons to sets of buttons for routing
		this.numerics = Stream.of(this.decimal, this.zero, this.one, this.two, this.three, this.four, this.five, this.six,
				this.seven, this.eight, this.nine).collect(Collectors.toSet());

		this.operations = Stream
				.of(this.slash, this.c, this.ce, this.plusMinus, this.star, this.minus, this.plus, this.equal)
				.collect(Collectors.toSet());

		this.memoryFunctions = Stream.of(this.mc, this.mr, this.mPlus, this.mMinus).collect(Collectors.toSet());

		this.readout.getParent().requestFocus(); // setup for keyboard input to work

		// Restore last entry (unless ERROR) on launch from valFile
		File valFile = new File(lastValFileName);
		if (valFile.exists()) {
			String lastValue = Files.readAllLines(valFile.toPath()).get(0);
			if (lastValue == "ERROR") {
				sendToReadout("0"); // don't restore "ERROR", just give 0 instead
			} else {
				sendToReadout(lastValue);
			}
		} else {
			sendToReadout("0"); // if no file found, set to 0
		}
	}

	// handles, routes all button presses
	@FXML
	public void buttonPressed(ActionEvent event) {
		try {
			Button button = (Button) event.getSource();
			System.out.println(button.getId());
			if (this.numerics.contains(button)) {
				this.handleNumeric(button); // .01234567890
			} else if (this.operations.contains(button)) {
				this.handleOperation(button); // /*+-=, C, CE, +-
			} else if (this.memoryFunctions.contains(button)) {
				this.handleMemoryFunction(button); // MC, MR, M+, M-
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Map key inputs by KeyEvent.text, forward non-text codes to handleCode
	@FXML
	public void keyPressed(KeyEvent event) { // Keyboard inputs
		try {
			String text = event.getText();
			System.out.println(event);
			switch (text) {
			case "0":
			case "1":
			case "2":
			case "3":
			case "4":
			case "5":
			case "6":
			case "7":
			case "8":
			case "9":
				this.updateReadout(text);
				break;
			case ".": // Period and decimal (numpad dot) should be handled the same
				this.decimal.fire();
				break;
			case "-":
				this.minus.fire();
				break;
			case "+":
				this.plus.fire();
				break;
			case "*":
				this.star.fire();
				break;
			case "/":
				this.slash.fire();
				break;
			case "=":
				this.equal.fire();
				break;
			case "c":
				this.c.fire();
				break;
			case "e":
				this.ce.fire();
				break;
			case "p":
				this.mPlus.fire();
				break;
			case "m":
				this.mMinus.fire();
				break;
			case "l":
				this.mc.fire();
				break;
			case "r":
				this.mr.fire();
				break;
			default:
				this.handleCode(event); // text-less/unmapped key presses
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void handleCode(KeyEvent event) {
		KeyCode code = event.getCode();
		// KeyCodes without characters are handled here:
		// ENTER -> =
		// BACKSPACE -> CE, DELETE -> C
		switch (code)
		{
		case ENTER:
			this.equal.fire();
			break;
		case BACK_SPACE:
			this.ce.fire();
			break;
		case DELETE:
			this.c.fire();
			break;
		default:
			break;
		}

		
	}

	// Map button Id's to numerals to try to be added.
	// This could probably be made less repetitive with a lambda.
	public void handleNumeric(Button button) {
		// Numbers and decimal point mapped to numeral representation from FXID
		switch (button.idProperty().get()) {
		case "decimal":
			this.updateReadout(".");
			break;
		case "zero":
			this.updateReadout("0");
			break;
		case "one":
			this.updateReadout("1");
			break;
		case "two":
			this.updateReadout("2");
			break;
		case "three":
			this.updateReadout("3");
			break;
		case "four":
			this.updateReadout("4");
			break;
		case "five":
			this.updateReadout("5");
			break;
		case "six":
			this.updateReadout("6");
			break;
		case "seven":
			this.updateReadout("7");
			break;
		case "eight":
			this.updateReadout("8");
			break;
		case "nine":
			this.updateReadout("9");
			break;
		}
	}

	public void handleOperation(Button button) {
		String oldReadout = this.readout.getText();
		if (oldReadout == "ERROR") {
			oldReadout = "0";
			sendToReadout("0"); // don't operate with an error message.
		}
		switch (button.idProperty().get()) {

		case "c":
			// restore to starting values besides memory
			sendToReadout("0"); // Clear
			operation = null; // unset requested operation
			entered = null;
			lastOperation = null;
			break;

		case "ce": // Clear entry: first numeral on right.
			if (oldReadout.length() == 1 || (oldReadout.contains("-") && oldReadout.length() == 2) || refreshDisplay)
			// output, 4 & -4 should become 0. Stale display should become 0.
			{
				sendToReadout("0");
			} else {
				oldReadout = oldReadout.substring(0, oldReadout.length() - 1);
				// Reformat separators
				oldReadout = this.formatReadoutFromString(oldReadout);
				sendToReadout(oldReadout);
			}
			break;

		case "plusMinus": // Switch number's polarity (-/+). 0 can't be negative.
			String pattern = "^0.?0*";
			if (oldReadout.startsWith("-")) {
				sendToReadout(oldReadout.substring(1));
			} else if (!oldReadout.matches(pattern)) {
				sendToReadout("-" + oldReadout);
			}
			break;

		case "slash":
			this.setOperation(opType.DIVIDE);
			break;

		case "star":
			this.setOperation(opType.MULTIPLY);
			break;

		case "minus":
			this.setOperation(opType.SUBTRACT);
			break;

		case "plus":
			this.setOperation(opType.ADD);
			break;

		case "equal":
			BigDecimal operand = getBigDecFromReadout();
			BigDecimal output = null;
			if (operation != null) { // set by +, -, /, *
				output = this.calculate(entered, operand, operation);
				lastOperation = operation; // sets up for subsequent =
				entered = operand;
			} else if (lastOperation != null) { // set by =
				output = this.calculate(entered, operand, lastOperation);
			} else {
				output = operand; // no operation, 4.0 =: 4
			}
			operation = null;
			updateReadout(output);
			this.refreshDisplay = true;
			break;
		}
	}

	public void handleMemoryFunction(Button button) {
		if (readout.getText() == "ERROR") {
			sendToReadout("0"); // don't operate with an error message.
		}
		switch (button.idProperty().get()) {

		case "mc": // Memory clear; set memory to null.
			this.memory = null;
			break;

		case "mr": // Memory restore; if memory non-null, set readout to it.
			if (this.memory != null) {
				if (this.memory.compareTo(BigDecimal.ZERO) == 0) {
					this.memory = new BigDecimal("0"); // Don't show 0.000000 from MR
				}
				String restored = this.memory.toPlainString(); // no exponent field
				restored = this.formatReadoutFromString(restored); // format output
				sendToReadout(restored);
			}
			break;

		case "mPlus": // Add to memory; set Memory to 0 if null, then add readout.
			BigDecimal value = this.getBigDecFromReadout();
			this.saveToMemory(value, false);
			break;

		case "mMinus": // Subtract from memory; set Memory to 0 if null, then subtract readout.
			BigDecimal sValue = this.getBigDecFromReadout();
			this.saveToMemory(sValue, true);
			break;

		}
	}

	// Attempts to add numeric input to readout.

	public void updateReadout(String numeral) {
		String oldReadout = this.readout.getText();
		if (this.refreshDisplay) // check flag; if set, clear display
		{
			oldReadout = "0";
			this.refreshDisplay = false; // unset flag
		}
		// Add numeric to string blindly before reformatting
		if (oldReadout.contains(".")) {
			if (numeral != ".")
				oldReadout = oldReadout + numeral;
		} else if (oldReadout.startsWith("0"))
		// Because any with decimal places are handled above,
		// this should only handle "0" in readout: "."->0(.), "1"->(1)
		{
			if (numeral == ".") {
				oldReadout = oldReadout + numeral; // add new decimal place
			} else {
				oldReadout = numeral; // replace output with numeral
			}
		} else // No special cases found, do default
		{
			oldReadout = oldReadout + numeral;
		}
		oldReadout = this.formatReadoutFromString(oldReadout);
		sendToReadout(oldReadout);
	}

	public void updateReadout(BigDecimal value) { // value can be null for errors
		if (value != null) // "ERROR" is displayed; don't overwrite it.
		{
			String newText = formatOutput(value);
			sendToReadout(newText);
		}
	}

	public void sendToReadout(String text) {
		// Send text to be displayed on calculator GUI and save it to persistent
		// file to show last value on re-launch after close.
		readout.setText(text);
		try {
			PrintWriter outFile = new PrintWriter(lastValFileName);
			outFile.print(text);
			outFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private String formatReadoutFromString(String readout) {
		// Format output w/o losing decimal point or post-decimal 0's
		/* Trim separators */
		readout = readout.replace(",", "");
		/* Grab negative prefix if present */
		String negative = "";
		if (readout.startsWith("-")) {
			readout = readout.replace("-", "");
			negative = "-";
		}
		/* Reverse iterate & add separators from decimal point or end */
		int i = readout.length() - 1; // get last valid index
		if (readout.contains(".")) {
			i = readout.indexOf(".") - 1; // replace w/ index before decimal place
		}
		for (; i > 2; i -= 4) // ",293" is 4 characters, so move 4 to left after
		{
			String before = readout.substring(0, i - 2);
			String after = readout.substring(i - 2);
			readout = before + "," + after;
			i++; // Added a character to array, so i should be updated.
		}

		/* Restore negative prefix */
		readout = negative + readout; // negative == "-" or ""
		return readout;
	}

	private BigDecimal getBigDecFromReadout() {
		String readout = this.readout.getText();
		readout = readout.replace(",", ""); // BigDecimal hates ","
		BigDecimal value = new BigDecimal(readout);
		return value;
	}

	private String formatOutput(BigDecimal value) {
		if (value.compareTo(BigDecimal.ZERO) == 0) {
			value = new BigDecimal("0"); // Don't show 0.000000
		}
		String out = value.toPlainString(); // we're not displaying exponential form
		out = this.formatReadoutFromString(out); // format output (comma separators)
		while (out.contains(".") && out.endsWith("0")) { // Trim "1.100 -> 1.1"
			out = out.substring(0, out.length() - 1);
		}
		if (out.endsWith(".")) { // if "1." is left, return "1"
			out = out.substring(0, out.length() - 1);
		}
		return out;
	}

	private void setOperation(opType type) {
		// Handle ex.'+' key presses intelligently. +, + means +, =.
		BigDecimal operand = this.getBigDecFromReadout();
		if (operation == type) {
			calculate(entered, operand, type);
		} else { // set up second half of operation (2, +, [4, = --> 6])
			entered = operand; // 2 in example above, save it for operating on
			operation = type; // + in example above, save it as current operation
		}
		refreshDisplay = true; // clear display on next entry
	}

	private BigDecimal calculate(BigDecimal operand1, BigDecimal operand2, opType type) {
		// perform calculations and return out.

		// Set out to operand2 (which is from readout) so if nothing happens,
		// readout won't change.
		BigDecimal out = operand2;

		switch (type) {
		case ADD:
			out = operand1.add(operand2);
			break;
		case DIVIDE:
			try {
				out = operand1.divide(operand2);
				break;
			} catch (ArithmeticException e) {
				if (e.getMessage().contains("expansion")) {
					// For infinite or very long decimals, give a reasonable precision
					out = operand1.divide(operand2, 15, RoundingMode.HALF_UP);
					break;
				}
				// Divide by zero and other nastiness
				out = null; // this signals calling functions to leave display alone
				// set readout text directly, don't save to persistence file
				this.readout.setText("ERROR");
				this.refreshDisplay = true;
				break;
			}
		case MULTIPLY:
			out = operand1.multiply(operand2);
			break;
		case SUBTRACT:
			out = operand1.subtract(operand2);
			break;
		}
		return out;
	}

	private void saveToMemory(BigDecimal num, boolean subtract) {
		if (this.memory == null) {
			this.memory = new BigDecimal("0");
		}
		if (subtract) {
			BigDecimal negative = new BigDecimal("-1");
			num = num.multiply(negative);
		}
		this.memory = this.memory.add(num);
	}

}
