import java.util.concurrent.Semaphore;

/**
 * Represents a thread capable of simulating a chat interaction between two students.
 * This class handles the concurrent updating of chat histories.
 */
public class ChatThread implements Runnable {
    private UniversityStudent sender;
    private UniversityStudent receiver;
    private String message;
    
    private static final Semaphore semaphore = new Semaphore(1);

    /**
     * Constructs a new ChatThread.
     *
     * @param sender   The student sending the message.
     * @param receiver The student receiving the message.
     * @param message  The content of the message to be sent.
     */
    public ChatThread(UniversityStudent sender, UniversityStudent receiver, String message) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
    }


    /**
     * Executes the chat logic.
     * Updates the chat history for both the sender and the receiver in a thread-safe manner.
     */
    @Override
    public void run() {
        try {
            semaphore.acquire();
            
            String chatMessage = sender.getName() + " to " + receiver.getName() + ": " + message;
            sender.addChatMessage(chatMessage);
            receiver.addChatMessage(chatMessage);
            
            System.out.println(chatMessage);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            semaphore.release();
        }
    }
}
