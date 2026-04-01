package com.example.batchprocessing;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private static final Logger log = LoggerFactory.getLogger(JobController.class);
    private final JobLauncher jobLauncher;
    private final Job importProductJob;
    private final Tracer tracer;

    public JobController(JobLauncher jobLauncher,
                         @Qualifier("importProductJob") Job importProductJob,
                         Tracer tracer) {
        this.jobLauncher = jobLauncher;
        this.importProductJob = importProductJob;
        this.tracer = tracer;
    }

    @PostMapping("/importProductJob")
    public ResponseEntity<Map<String, String>> startJob(@RequestParam(required = false) String runId) {
        Span span = tracer.currentSpan();
        String traceId = span != null ? span.context().traceId() : "N/A";
        String spanId = span != null ? span.context().spanId() : "N/A";
        String uri = "/api/jobs/importProductJob";

        log.info("Request received | traceId={} | spanId={} | uri={}", traceId, spanId, uri);

        try {
            JobParametersBuilder builder = new JobParametersBuilder();
            String id = runId != null ? runId : String.valueOf(System.currentTimeMillis());
            builder.addString("runId", id);
            jobLauncher.run(importProductJob, builder.toJobParameters());

            Map<String, String> response = new HashMap<>();
            response.put("status", "Job started");
            response.put("runId", id);
            response.put("traceId", traceId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Job launch failed", e);
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}