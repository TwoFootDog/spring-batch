package co.kr.kgc.point.batch.job.Quartz;


import lombok.RequiredArgsConstructor;
import org.quartz.Scheduler;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class SchedulerJobService {
    private final Scheduler scheduler;
    private final SchedulerFactoryBean schedulerFactoryBean;
    
}
