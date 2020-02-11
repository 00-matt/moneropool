package uk.offtopica.moneropool;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import uk.offtopica.monerorpc.daemon.BlockTemplate;

public class NewBlockTemplateEvent extends ApplicationEvent {
    @Getter
    private final BlockTemplate blockTemplate;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source        the object on which the event initially occurred or with
     *                      which the event is associated (never {@code null})
     * @param blockTemplate The new block template
     */
    public NewBlockTemplateEvent(Object source, BlockTemplate blockTemplate) {
        super(source);
        this.blockTemplate = blockTemplate;
    }
}
