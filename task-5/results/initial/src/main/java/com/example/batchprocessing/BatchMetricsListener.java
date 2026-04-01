package com.example.batchprocessing;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.batch.core.*;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class BatchMetricsListener extends JobExecutionListenerSupport
        implements StepExecutionListener {

    private final MeterRegistry meterRegistry;
    private Timer.Sample jobTimerSample;
    private Timer.Sample stepTimerSample;

    public BatchMetricsListener(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        jobTimerSample = Timer.start(meterRegistry);
        Counter.builder("batch.job.started.total")
                .tag("jobName", jobExecution.getJobInstance().getJobName())
                .description("Total number of job starts")
                .register(meterRegistry)
                .increment();
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobTimerSample != null) {
            jobTimerSample.stop(Timer.builder("batch.job.duration")
                    .tag("jobName", jobExecution.getJobInstance().getJobName())
                    .tag("status", jobExecution.getStatus().toString())
                    .description("Job execution duration")
                    .register(meterRegistry));
        }

        Counter.Builder counterBuilder = Counter.builder("batch.job.finished.total")
                .tag("jobName", jobExecution.getJobInstance().getJobName())
                .tag("status", jobExecution.getStatus().toString());
        counterBuilder.register(meterRegistry).increment();

        if (jobExecution.getStatus() == BatchStatus.FAILED) {
            Counter.builder("batch.job.errors.total")
                    .tag("jobName", jobExecution.getJobInstance().getJobName())
                    .description("Total number of job failures")
                    .register(meterRegistry)
                    .increment();
        }
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        stepTimerSample = Timer.start(meterRegistry);
        Counter.builder("batch.step.started.total")
                .tag("jobName", stepExecution.getJobExecution().getJobInstance().getJobName())
                .tag("stepName", stepExecution.getStepName())
                .register(meterRegistry)
                .increment();
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        if (stepTimerSample != null) {
            stepTimerSample.stop(Timer.builder("batch.step.duration")
                    .tag("jobName", stepExecution.getJobExecution().getJobInstance().getJobName())
                    .tag("stepName", stepExecution.getStepName())
                    .tag("status", stepExecution.getStatus().toString())
                    .register(meterRegistry));
        }

        Counter.builder("batch.step.finished.total")
                .tag("jobName", stepExecution.getJobExecution().getJobInstance().getJobName())
                .tag("stepName", stepExecution.getStepName())
                .tag("status", stepExecution.getStatus().toString())
                .register(meterRegistry)
                .increment();

        if (stepExecution.getStatus() == BatchStatus.FAILED) {
            Counter.builder("batch.step.errors.total")
                    .tag("jobName", stepExecution.getJobExecution().getJobInstance().getJobName())
                    .tag("stepName", stepExecution.getStepName())
                    .register(meterRegistry)
                    .increment();
        }
        return stepExecution.getExitStatus();
    }
}