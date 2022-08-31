package br.com.bottdan.messaging.sqs.configuration;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.retry.PredefinedRetryPolicies;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.aws.messaging.config.QueueMessageHandlerFactory;
import org.springframework.cloud.aws.messaging.config.SimpleMessageListenerContainerFactory;
import org.springframework.cloud.aws.messaging.config.annotation.EnableSqs;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.HeadersMethodArgumentResolver;
import org.springframework.messaging.handler.annotation.support.PayloadMethodArgumentResolver;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;

import java.util.List;

@EnableSqs
@Configuration
@Profile("local")
@EnableConfigurationProperties(SQSMessagingProperties.class)
@ConditionalOnProperty(value = "javamavenawsexample.sqs.enabled", havingValue = "true", matchIfMissing = false)
public class SQSMessagingConfigurationLocal {

    @Bean
    @Primary
    public AmazonSQSAsync amazonSQSAsync() {
        return AmazonSQSAsyncClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:4566", Regions.SA_EAST_1.getName()))
                .withClientConfiguration(new ClientConfiguration().withRetryPolicy(PredefinedRetryPolicies.getDefaultRetryPolicy()))
                .build();
    }

    @Bean
    @Primary
    public SimpleMessageListenerContainerFactory simpleMessageListenerContainerFactory(final AmazonSQSAsync amazonSQSAsync, final SQSMessagingProperties sqsMessagingProperties) {
        final SimpleMessageListenerContainerFactory factory = new SimpleMessageListenerContainerFactory();
        factory.setAmazonSqs(amazonSQSAsync);
        factory.setAutoStartup(Boolean.TRUE);
        factory.setMaxNumberOfMessages(sqsMessagingProperties.getMaxNumberOfMessages());
        return factory;
    }

    @Bean
    @Primary
    public QueueMessageHandlerFactory queueMessageHandlerFactory(final AmazonSQSAsync amazonSQSAsync, final MappingJackson2MessageConverter messageConverter) {
        final List<HandlerMethodArgumentResolver> argumentResolvers = List.of(new HeadersMethodArgumentResolver(), new PayloadMethodArgumentResolver(messageConverter));
        final QueueMessageHandlerFactory queueMessageHandlerFactory = new QueueMessageHandlerFactory();
        queueMessageHandlerFactory.setAmazonSqs(amazonSQSAsync);
        queueMessageHandlerFactory.setArgumentResolvers(argumentResolvers);
        return queueMessageHandlerFactory;
    }
}