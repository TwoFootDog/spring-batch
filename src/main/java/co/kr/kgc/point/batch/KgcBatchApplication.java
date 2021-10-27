package co.kr.kgc.point.batch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableBatchProcessing
@SpringBootApplication
public class KgcBatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(KgcBatchApplication.class, args);
    }
}
