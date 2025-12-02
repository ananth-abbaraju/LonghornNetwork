import java.util.concurrent.Semaphore;

/**
 * Represents a thread used to simulate sending a friend request between students.
 * Handles the concurrent modification of friend lists.
 */
public class FriendRequestThread implements Runnable {
    private UniversityStudent sender;
    private UniversityStudent receiver;
    private static final Semaphore semaphore = new Semaphore(1);

    /**
     * Constructs a new FriendRequestThread.
     *
     * @param sender   The student sending the friend request.
     * @param receiver The student receiving the friend request.
     */
    public FriendRequestThread(UniversityStudent sender, UniversityStudent receiver) {
        this.sender = sender;
        this.receiver = receiver;
    }

    /**
     * Executes the friend request logic.
     * Adds the students to each other's friend lists if not already connected,
     * ensuring thread safety.
     */
    @Override
    public void run() {
        try {
            semaphore.acquire();
            
            sender.addFriend(receiver.getName());
            receiver.addFriend(sender.getName());
            System.out.println(sender.getName() + " sent a friend request to " + receiver.getName());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            semaphore.release();
        }
    }
}
