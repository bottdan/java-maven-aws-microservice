package br.com.bottdan.messaging.sqs.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Validated
@ConfigurationProperties("javamavenawsexample.sqs")
public class SQSMessagingProperties {

    @NotNull
    private Boolean enabled = false;

    @NotNull
    @Max(value = 10)
    @Min(value = 1)
    private Integer maxNumberOfMessages;

    @NotNull
    private QueueProperty queue = new QueueProperty();

    @Getter
    @Setter
    @Validated
    public static class QueueProperty {
        @NotBlank
        private String queueName;

        @NotNull
        private Boolean enabled = false;
    }
}
