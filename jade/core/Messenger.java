package jade.core;

import jade.util.Tools;

import java.io.Serializable;

/**
 * This class respresents an object that can remember String messages, and
 * retrieve messages from other messengers. Both jade World and Actor extend
 * this class with the motivation that the world could then retrieve all the
 * messages of its actors and store them in one place for easy access.
 */
public class Messenger implements Serializable {
	private String messages;

	public Messenger() {
		clearMessages();
	}

	/**
	 * Adds a message for the messenger to remember. If the message does not end
	 * with ". " it will be appended as well.
	 * 
	 * @param message
	 *            the message to be stored
	 */
	public void appendMessage(String message) {
		if (!message.equals(""))
			messages += Tools.strEnsureSuffix(message, ". ");
	}

	public void clearMessages() {
		messages = "";
	}

	/**
	 * Returns and clears this messengers messages.
	 * 
	 * @return The messengers messegages.
	 */
	public String getMessages() {
		final String result = messages;
		clearMessages();
		return result;
	}

	/**
	 * Retrieves and clears all messeges of the specified messenger and adds
	 * them to this messenger's messages.
	 * 
	 * @param messenger
	 *            the messenger whos messages are to be retrieved
	 */
	public void retrieveMessages(Messenger messenger) {
		messages += messenger.getMessages();
	}
}
