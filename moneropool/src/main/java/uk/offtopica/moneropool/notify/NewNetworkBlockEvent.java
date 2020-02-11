package uk.offtopica.moneropool.notify;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class NewNetworkBlockEvent extends ApplicationEvent {
    @Getter
    private final String blockHash;

    /**
     * Create a new {@code NewBlockTemplateEvent}.
     *
     * @param source    the object on which the event initially occurred or with
     *                  which the event is associated (never {@code null})
     * @param blockHash the hash of the new block
     */
    public NewNetworkBlockEvent(Object source, String blockHash) {
        super(source);
        this.blockHash = blockHash;
    }
}
