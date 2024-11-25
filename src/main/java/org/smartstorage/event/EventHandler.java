package org.smartstorage.event;


import lombok.Getter;
import org.smartstorage.Entity.MetaData;
import org.springframework.context.ApplicationEvent;

@Getter
public class EventHandler extends ApplicationEvent {

    private MetaData metaData;

    public EventHandler(Object source, MetaData metaData) {
        super(source);
        this.metaData = metaData;
    }

}
