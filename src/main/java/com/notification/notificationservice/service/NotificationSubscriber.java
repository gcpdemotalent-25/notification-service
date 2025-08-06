package com.notification.notificationservice.service;

import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;

import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.inbound.PubSubInboundChannelAdapter;

@Component
public class NotificationSubscriber {

    // 1. Crée un canal de communication interne pour les messages Pub/Sub
    @Bean
    public MessageChannel pubsubInputChannel() {
        return new DirectChannel();
    }

    // 2. Crée un adaptateur qui écoute une subscription GCP et envoie les messages au canal interne
    @Bean
    public PubSubInboundChannelAdapter messageChannelAdapter(
            @Qualifier("pubsubInputChannel") MessageChannel channel,
            PubSubTemplate pubSubTemplate) {

        // Récupère le nom de la subscription depuis application.properties
        String subscriptionName = "envoi-notifications-sub";

        PubSubInboundChannelAdapter adapter = new PubSubInboundChannelAdapter(pubSubTemplate, subscriptionName);
        adapter.setOutputChannel(channel);

        System.out.println("Adaptateur Pub/Sub configuré pour écouter la subscription: " + subscriptionName);
        return adapter;
    }

    // 3. Crée un "consommateur" qui est activé dès qu'un message arrive sur le canal
    @ServiceActivator(inputChannel = "pubsubInputChannel")
    public void messageReceiver(String payload,
                                @Qualifier(GcpPubSubHeaders.ORIGINAL_MESSAGE) BasicAcknowledgeablePubsubMessage message) {

        System.out.println("=========================================");
        System.out.println("MESSAGE REÇU VIA PUB/SUB !");
        System.out.println("Payload: " + payload);
        System.out.println("=========================================");

        // Étape cruciale : Confirmer la réception du message à Pub/Sub.
        // Si vous ne le faites pas, Pub/Sub pensera que le message n'a pas été traité
        // et le renverra après un certain délai.
        message.ack();
    }
}
