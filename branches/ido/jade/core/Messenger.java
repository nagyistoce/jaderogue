package jade.core;

import jade.util.Tools;

import java.io.Serializable;
import java.util.Vector;

import java.util.logging.Logger;

/**
 * This class represents an object that can remember String messages, and
 * retrieve messages from other messengers. Both jade World and Actor extend
 * this class with the motivation that the world could then retrieve all the
 * messages of its actors and store them in one place for easy access.
 */
public class Messenger implements Serializable {

	private Vector<String> messages;
	private String message = "";
	private Logger logger = Logger.getLogger(Messenger.class.getName());

	public Messenger() {
		messages = new Vector<String>();
		messages.setSize(2);
	}

	/**
	 * Adds a message for the messenger to remember. If the message does not end
	 * with ". " it will be appended as well.
	 * 
	 * @param message
	 *            the message to be stored
	 */
	public void appendMessage(String message) {
		messages.add(Tools.strEnsureSuffix(message, ". "));
		if(message.length()>2)
			messages.remove(0);

		logger.info("append message: " + message);
		if(message.equals("guard kills you"))
			logger.info("");
	}

	/**
	 * Returns and clears this messengers messages.
	 * 
	 * @return The messengers messegages.
	 */
	public String getMessages() {
		message = "";
		if (messages.size() > 0) {
			for (String m : messages)
				if (m != null && !m.equals(""))
					message += m;
		}

		return message;
	}

	/**
	 * Retrieves and clears all messeges of the specified messenger and adds
	 * them to this messenger's messages.
	 * 
	 * @param messenger
	 *            the messenger whos messages are to be retrieved
	 */
	public void retrieveMessages(Messenger messenger) {
		message += messenger.getMessages();
	}
}
