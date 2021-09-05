package co.kr.kgc.point.batch.runner;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;

public class SampleRunner implements CommandLineRunner {
    private static final Logger logger = LogManager.getLogger(SampleRunner.class);

    @Override
    public void run(String... args) throws Exception {
        logger.info("sample runner111111");
    }
}
