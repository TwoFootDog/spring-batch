package co.kr.kgc.point.batch.runner;

import co.kr.kgc.point.batch.KgcBatchApplication;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;

@RequiredArgsConstructor
public class SampleLauncher {
    private static final Logger logger = LogManager.getLogger(SampleLauncher.class);

    public static void main(String[] args) {
        SpringApplication.run(KgcBatchApplication.class, args);

        logger.info("SampleLauncher start........................");
    }

}
