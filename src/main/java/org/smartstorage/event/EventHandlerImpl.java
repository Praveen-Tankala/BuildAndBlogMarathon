package org.smartstorage.event;


import lombok.extern.slf4j.Slf4j;
import org.smartstorage.Repository.MetaDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class EventHandlerImpl {

    @Autowired
    MetaDataRepository metaDataRepository;


    public long getCurrentTime (){
        return new Date().getTime();
    }


    @Async
    @EventListener
    public void eventForQuestion(EventHandler eventHandler) throws Exception{
        if(eventHandler.getMetaData()!=null) {
            log.info("saved in postgres " + eventHandler.getMetaData());
            metaDataRepository.save(eventHandler.getMetaData());
        }
    }
}
