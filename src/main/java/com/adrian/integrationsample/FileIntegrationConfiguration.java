package com.adrian.integrationsample;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.FileWritingMessageHandler;
import org.springframework.integration.file.support.FileExistsMode;

import java.io.File;

@Configuration
public class FileIntegrationConfiguration {

  @Bean
  IntegrationFlow fileIntegrationFlow(MessageSource<File> readingMessageSource,
      FileTransformer transformer,
      FileWritingMessageHandler fileWritingMessageHandler) {
    return IntegrationFlows.from(readingMessageSource, config -> config.poller(Pollers.fixedDelay(1000)))
        .transform(transformer, "getFileName")
        .handle(fileWritingMessageHandler)
        .get();
  }

  @Bean
  MessageSource<File> fileAdapter() {
    FileReadingMessageSource fileSource = new FileReadingMessageSource();
    fileSource.setDirectory(new File("data/input"));

    return fileSource;
  }

  @Bean
  FileTransformer transformer() {
    return new FileTransformer();
  }

  @Bean
  FileWritingMessageHandler outputFileAdapter() {
    File directory = new File("data/output");
    FileWritingMessageHandler handler = new FileWritingMessageHandler(directory);
    handler.setFileNameGenerator(message -> "result.txt");
    handler.setFileExistsMode(FileExistsMode.APPEND);
    handler.setAppendNewLine(true);
    handler.setExpectReply(false);

    return handler;
  }
}
