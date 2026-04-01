package com.example.batchprocessing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.stereotype.Component;

@Component
public class BatchItemProcessListener implements ItemProcessListener<Product, Product> {
    private static final Logger log = LoggerFactory.getLogger(BatchItemProcessListener.class);

    @Override
    public void beforeProcess(Product item) {}

    @Override
    public void afterProcess(Product item, Product result) {}

    @Override
    public void onProcessError(Product item, Exception e) {
        log.error("Error processing product: {}", item, e);
    }
}