package co.kr.kgc.point.batch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing
@SpringBootApplication
public class KgcBatchApplication {
//
//    @Autowired
//    private SampleQuarzScheduler sampleQuarzScheduler;

    public static void main(String[] args) {
        SpringApplication.run(KgcBatchApplication.class, args);
//        int exitCode = SpringApplication.exit(SpringApplication.run(KgcBatchApplication.class, args));
//        System.exit(exitCode);
    }

}
