package jade.util;

import static java.awt.event.KeyEvent.VK_DOWN;
import static java.awt.event.KeyEvent.VK_KP_DOWN;
import static java.awt.event.KeyEvent.VK_KP_LEFT;
import static java.awt.event.KeyEvent.VK_KP_RIGHT;
import static java.awt.event.KeyEvent.VK_KP_UP;
import static java.awt.event.KeyEvent.VK_LEFT;
import static java.awt.event.KeyEvent.VK_RIGHT;
import static java.awt.event.KeyEvent.VK_UP;

public class ExtraTools extends Tools {
	public static Direction keycodeToStep(int keycode) {
		switch (keycode) {
		case VK_RIGHT:
		case VK_KP_RIGHT:
			return Direction.E;
		case VK_LEFT:
		case VK_KP_LEFT:
			return Direction.W;
		case VK_UP:
		case VK_KP_UP:
			return Direction.N;
		case VK_DOWN:
		case VK_KP_DOWN:
			return Direction.S;
		default:
			return null;
		}
	}

	static Enum next(Enum[] values, int ordinal, int length) {
		Enum next = null;

		for (Enum mo : values)
			if ((ordinal + 1) % length == mo.ordinal())
				next = mo;

		return next;
	}

	static int normalize(int i) {
		if (i == 0)
			return 0;
		else
			return i < 0 ? -1 : 1;
	}

	static Enum prev(Enum[] values, int ordinal, int length) {
		Enum next = null;

		for (Enum mo : values)
			if ((ordinal + length - 1) % length == mo.ordinal())
				next = mo;

		return next;
	}
}
