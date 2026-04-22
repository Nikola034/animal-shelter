package com.animalshelter.analytics.config;

import com.animalshelter.analytics.messaging.event.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "animal-shelter-exchange";

    // Queues
    public static final String ANIMAL_REGISTERED_QUEUE = "analytics.animal.registered";
    public static final String ANIMAL_STATUS_CHANGED_QUEUE = "analytics.animal.status.changed";
    public static final String MEDICAL_TREATMENT_QUEUE = "analytics.medical.treatment.added";
    public static final String DAILY_METRICS_QUEUE = "analytics.activity.metrics.recorded";
    public static final String FEEDING_RECORDED_QUEUE = "analytics.activity.feeding.recorded";

    // Routing keys (must match producer keys)
    public static final String ANIMAL_REGISTERED_KEY = "animal.registered";
    public static final String ANIMAL_STATUS_CHANGED_KEY = "animal.status.changed";
    public static final String MEDICAL_TREATMENT_KEY = "medical.treatment.added";
    public static final String DAILY_METRICS_KEY = "activity.metrics.recorded";
    public static final String FEEDING_RECORDED_KEY = "activity.feeding.recorded";

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    // Queue declarations
    @Bean
    public Queue animalRegisteredQueue() {
        return QueueBuilder.durable(ANIMAL_REGISTERED_QUEUE).build();
    }

    @Bean
    public Queue animalStatusChangedQueue() {
        return QueueBuilder.durable(ANIMAL_STATUS_CHANGED_QUEUE).build();
    }

    @Bean
    public Queue medicalTreatmentQueue() {
        return QueueBuilder.durable(MEDICAL_TREATMENT_QUEUE).build();
    }

    @Bean
    public Queue dailyMetricsQueue() {
        return QueueBuilder.durable(DAILY_METRICS_QUEUE).build();
    }

    @Bean
    public Queue feedingRecordedQueue() {
        return QueueBuilder.durable(FEEDING_RECORDED_QUEUE).build();
    }

    // Bindings
    @Bean
    public Binding animalRegisteredBinding(Queue animalRegisteredQueue, TopicExchange exchange) {
        return BindingBuilder.bind(animalRegisteredQueue).to(exchange).with(ANIMAL_REGISTERED_KEY);
    }

    @Bean
    public Binding animalStatusChangedBinding(Queue animalStatusChangedQueue, TopicExchange exchange) {
        return BindingBuilder.bind(animalStatusChangedQueue).to(exchange).with(ANIMAL_STATUS_CHANGED_KEY);
    }

    @Bean
    public Binding medicalTreatmentBinding(Queue medicalTreatmentQueue, TopicExchange exchange) {
        return BindingBuilder.bind(medicalTreatmentQueue).to(exchange).with(MEDICAL_TREATMENT_KEY);
    }

    @Bean
    public Binding dailyMetricsBinding(Queue dailyMetricsQueue, TopicExchange exchange) {
        return BindingBuilder.bind(dailyMetricsQueue).to(exchange).with(DAILY_METRICS_KEY);
    }

    @Bean
    public Binding feedingRecordedBinding(Queue feedingRecordedQueue, TopicExchange exchange) {
        return BindingBuilder.bind(feedingRecordedQueue).to(exchange).with(FEEDING_RECORDED_KEY);
    }

    @Bean
    public DefaultClassMapper classMapper() {
        DefaultClassMapper classMapper = new DefaultClassMapper();
        Map<String, Class<?>> idClassMapping = new HashMap<>();

        // Map producer class names to consumer event classes
        idClassMapping.put("com.animalshelter.animalregistry.messaging.event.AnimalRegisteredEvent",
                AnimalRegisteredEvent.class);
        idClassMapping.put("com.animalshelter.animalregistry.messaging.event.AnimalStatusChangedEvent",
                AnimalStatusChangedEvent.class);
        idClassMapping.put("com.animalshelter.animalregistry.messaging.event.MedicalTreatmentAddedEvent",
                MedicalTreatmentAddedEvent.class);
        idClassMapping.put("com.animalshelter.activitytracking.messaging.event.DailyMetricsRecordedEvent",
                DailyMetricsRecordedEvent.class);
        idClassMapping.put("com.animalshelter.activitytracking.messaging.event.FeedingRecordedEvent",
                FeedingRecordedEvent.class);

        classMapper.setIdClassMapping(idClassMapping);
        classMapper.setTrustedPackages("com.animalshelter.*");
        return classMapper;
    }

    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper, DefaultClassMapper classMapper) {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);
        converter.setClassMapper(classMapper);
        return converter;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         MessageConverter jsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter);
        return template;
    }
}
