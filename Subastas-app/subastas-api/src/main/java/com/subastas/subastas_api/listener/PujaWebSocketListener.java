package com.subastas.subastas_api.listener;

import com.subastas.subastas_api.events.PujaActualizadaEvent;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;

@Component
public class PujaWebSocketListener {

    private final SimpMessagingTemplate messagingTemplate;

    public PujaWebSocketListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPujaActualizada(PujaActualizadaEvent event) {
        Long idSubasta = event.getPayload().getIdSubasta();

        messagingTemplate.convertAndSend(
                "/topic/subastas/" + idSubasta + "/pujas",
                event.getPayload()
        );
    }
}