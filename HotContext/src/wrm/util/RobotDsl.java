package wrm.util;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * 
 * Folgende Befehle kann der Runner zeilenbasiert aus einem Skript verarbeiten:
 * # - ein Kommentar, diese Zeile wird ignoriert - Leerzeilen werden ignoriert
 * delay [time in ms] - verzögert die Ausführung um die angegebene Zeit type
 * [zeichenkette] - gibt die Zeichenkette aus tab - drückt Tab tabctrl -
 * Tab+Ctrl enter - drückt Enter enterRelease - lässt Enter los cursordown -
 * Cursor down exit - Beendet das Skript waitForWindowFocus [Windowname] -
 * wartet, bis das angegebene Window im Fokus ist diese Steuerung ist allerdings
 * nicht nebenläufig, der Robot wartet immer auf genau ein Window und macht dann
 * weiter x [Ganzzahl] [Befehl]- der nachfolgende Befehl wiederholt angewandt
 * Beispiel: 'x 4 tab' drückt 4 mal die Tabtaste
 */
public final class RobotDsl {

	/** Der Name des Skripts */
	private String _scriptName = null;

	/**
	 * Bitte keine Instanzen von aussen anlegen, stattdessen startRobot
	 * aufrufen!
	 * 
	 * @param scriptName
	 *            Der Name des Skripts, das auszuführen ist
	 */
	private RobotDsl(String scriptName) {
		this._scriptName = scriptName;
	}

	private String windowName = null;

	/**
	 * Der Name des Windows, auf das zu warten ist
	 * 
	 * @return Der Windowname
	 */
	protected synchronized String getWindowName() {
		return this.windowName;
	}

	/**
	 * Der Name des Windows, auf das zu warten ist
	 * 
	 * @param name
	 *            Der Windowname
	 */
	protected synchronized void setWindowName(String name) {
		this.windowName = name;
		if (name != null) {
			this.notifyAll();
		}
	}

	public static void evalCommandLine(String commandLine) {
		Tokenizer tokenizer = new Tokenizer(commandLine);
		String operand = tokenizer.nextToken();

		try {
			Robot r = new Robot();

			while (operand != null) {
				evalCommand(r, operand, tokenizer);
				operand = tokenizer.nextToken();
			}
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Primitives Mapping von 'Skriptbefehlen' auf die entsprechenden
	 * java.awt.Robot - Methoden.
	 * 
	 * @param robot
	 *            Die Robot-Instanz
	 * @param operand
	 *            Der auszuführende Operand
	 * @param tokenizer
	 *            Der Tokenizer
	 */
	private static void evalCommand(Robot robot, String operand,
			Tokenizer tokenizer) {
		if (operand.equals("delay")) {
			robot.delay(tokenizer.nextTokenAsInt());
		} else if (operand.equals("type")) {
			String input = tokenizer.nextToken();
			for (int i = 0, length = input.length(); i < length; i++) {
				type(robot, input.charAt(i));
				robot.delay(100);
			}
		} else if (operand.equals("ctrl")) {
			String input = tokenizer.nextToken();

			robot.keyPress(KeyEvent.VK_CONTROL);
			type(robot, input.toLowerCase().charAt(0));
			robot.delay(100);
			robot.keyRelease(KeyEvent.VK_CONTROL);

		} else if (operand.equals("tab")) {
			robot.keyPress(KeyEvent.VK_TAB);
		} else if (operand.equals("tabctrl")) {
			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_TAB);
			robot.keyRelease(KeyEvent.VK_CONTROL);
		} else if (operand.equals("enter")) {
			robot.keyPress(KeyEvent.VK_ENTER);
		} else if (operand.equals("enterRelease")) {
			robot.keyRelease(KeyEvent.VK_ENTER);
		} else if (operand.equals("cursordown")) {
			robot.keyPress(KeyEvent.VK_DOWN);
		} else if (operand.equals("mouseclick")) {
			robot.mousePress(MouseEvent.BUTTON1);
			robot.delay(100);
			robot.mouseRelease(MouseEvent.BUTTON1);
		}
	}

	/**
	 * Helfer für die Ausgabe von Tastaturevents.
	 * 
	 * Übersetzt eine char in Tastaturevents, dabei wird ermittelt, ob Shiftkey
	 * gedrückt werden muss etc.
	 * 
	 * @param robot
	 *            Der Robot, an dem dann das keyevent produziert wird
	 * @param c
	 *            Der zu drückende Buchstabe
	 * 
	 *            TODO: Sonderzeichen etc. berücksichtigen
	 */
	private static void type(Robot robot, char c) {
		if (c != 0) {
			if (65 <= c && c <= 96) {
				robot.keyPress(KeyEvent.VK_SHIFT);
				robot.keyPress(c);
				robot.keyRelease(KeyEvent.VK_SHIFT);
			} else if (c > 96) {
				robot.keyPress(c - 32);
			} else {
				robot.keyPress(c);
			}
		}
	}

	/**
	 * Helferklasse für das Parsen einer Zeile im Skript.
	 * 
	 * Parsen heißt: - suche das erste nicht-Whitespace, Position merken - suche
	 * das erste Whitespace, Position merken - gebe den Substring dieser
	 * Positionen, alternativ, gebe null, wenn kein weiteres Token gefunden
	 * 
	 * Man kann also beliebig oft nextToken rufen und es gibt kein hasNext oder
	 * so.
	 */
	private static class Tokenizer {

		private String _line;
		private int _pointer = 0;

		/**
		 * Erzeugt einen Tokenizer mit der angegebenen Befehlszeile
		 * 
		 * @param line
		 *            Die Befehlszeile
		 */
		Tokenizer(String line) {
			if (line == null) {
				throw new IllegalArgumentException("line must not be null");
			}
			this._line = line;
		}

		/**
		 * Erzeugt eine Kopie der Instanz des Tokenizers
		 * 
		 * @return Ein Tokenizer als Clone
		 */
		Tokenizer cloneTokenizer() {
			Tokenizer clone = new Tokenizer(this._line);
			clone._pointer = this._pointer;
			return clone;
		}

		/**
		 * Read the token and produce an int.
		 * 
		 * If this is not possible (an NumberFormatException is produced), give
		 * -1
		 * 
		 * @return The int of the next token or -1 if this is not parsable.
		 */
		int nextTokenAsInt() {
			String token = nextToken();
			if (token != null) {
				try {
					return Integer.parseInt(token);
				} catch (NumberFormatException e) {
					return -1;
				}
			}
			return -1;
		}

		/**
		 * Give all remaining characters as String.
		 * 
		 * After this operation the pointer has not changed.
		 * 
		 * @return The remaining characters as String
		 */
		String restToken() {
			// walk to the start of the next token
			while (this._line.length() > _pointer
					&& Character.isWhitespace(this._line.charAt(_pointer))) {
				_pointer++;
			}
			return _line.substring(_pointer, _line.length()).trim();
		}

		/**
		 * Read the next token, which is the next whitespace-delimitted section
		 * of characters in the line.
		 * 
		 * The Method will never give the empty string "" but null instead.
		 * 
		 * @return The next whitespace-delimitted section of characters in the
		 *         line or null if there isn't a next token or it would be the
		 *         empty String
		 */
		String nextToken() {
			// walk to the start of the next token
			while (this._line.length() > _pointer
					&& Character.isWhitespace(this._line.charAt(_pointer))) {
				_pointer++;
			}
			int s = this._pointer;
			// walk to the end of the next token
			while (this._line.length() > _pointer
					&& !Character.isWhitespace(this._line.charAt(_pointer))) {
				_pointer++;
			}
			// if s and p matches, create a Substring
			if (this._line.length() >= _pointer && s < _pointer) {
				return _line.substring(s, _pointer);
			}
			return null;
		}
	}
}
