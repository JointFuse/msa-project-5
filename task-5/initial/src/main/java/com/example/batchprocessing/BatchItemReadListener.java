package com.example.batchprocessing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.stereotype.Component;

@Component
public class BatchItemReadListener implements ItemReadListener<Product> {
    private static final Logger log = LoggerFactory.getLogger(BatchItemReadListener.class);

    @Override
    public void beforeRead() {}

    @Override
    public void afterRead(Product item) {}

    @Override
    public void onReadError(Exception ex) {
        log.error("Error reading product", ex);
    }
}