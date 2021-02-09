package com.example.demo.services;

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.FileWritingMessageHandler;
import org.springframework.integration.file.filters.CompositeFileListFilter;
import org.springframework.integration.file.filters.SimplePatternFileListFilter;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.integration.file.transformer.FileToStringTransformer;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Configuration
@EnableIntegration
public class FileWriteConfig {

	public static final Logger LOG = LoggerFactory.getLogger(FileWriteConfig.class);

	@Value("${input.dir}")
    private String inputDir;
	@Value("${output.dir}")
    private String outputDir;
	@Value("${file.patter}")
    private String filePattern;

    @Bean
    public MessageChannel fileInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow processFileFlow() {
        return IntegrationFlows
                .from("fileInputChannel")
                .transform(fileToStringTransformer())
                .handle("fileProcessor", "process")
                .handle(fileWritingMessageHandler()).get();
    }

    @Bean
    @InboundChannelAdapter(value = "fileInputChannel", poller = @Poller(fixedDelay = "1000"))
    public MessageSource<File> fileReadingMessageSource() {
        CompositeFileListFilter<File> filters = new CompositeFileListFilter<>();
        filters.addFilter(new SimplePatternFileListFilter(filePattern));
        filters.addFilter(new LastModifiedFileFilter());

        FileReadingMessageSource source = new FileReadingMessageSource();
        source.setAutoCreateDirectory(true);
        source.setDirectory(new File(inputDir));
        source.setFilter(filters);

        LOG.info("adaptor setup");
        return source;
    }

    @Bean
    public FileToStringTransformer fileToStringTransformer() {
        return new FileToStringTransformer();
    }

//    @Bean
//    public static FileToByteArrayTransformer toByteArrayTransformer(boolean deleteFiles) {
//        FileToByteArrayTransformer fileToByteArrayTransformer = new FileToByteArrayTransformer();
//        fileToByteArrayTransformer.setDeleteFiles(deleteFiles);
//        return fileToByteArrayTransformer;
//    }

    /**
     * get file from fileChannle and write to fileChannel
     * @return MessageHandler
     */
    @Bean
    //@ServiceActivator(inputChannel = "fileChannel")
    public MessageHandler fileWritingMessageHandler() {
        FileWritingMessageHandler handler = new FileWritingMessageHandler(new File(outputDir));
        handler.setFileExistsMode(FileExistsMode.REPLACE_IF_MODIFIED);
        handler.setExpectReply(false);
        handler.setDeleteSourceFiles(true);

        LOG.info("Complete write to dest-dir: " + outputDir);
        LOG.info("delete file from source_dir: " + inputDir);

        return handler;
    }

    @Bean
    public FileProcessor fileProcessor() {
        return new FileProcessor();
    }
}
