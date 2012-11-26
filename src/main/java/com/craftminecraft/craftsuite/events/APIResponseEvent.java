import org.bukkit.event.Event;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
 
public class APIResponseEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private String message;

    public APIResponseEvent (String messageReturned) { // Maybe this should be a json string ?
        message = messageReturned;
    }

    public String getResponse() {
        return message;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
