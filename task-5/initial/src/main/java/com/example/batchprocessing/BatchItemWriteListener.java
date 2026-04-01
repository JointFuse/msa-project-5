package com.example.batchprocessing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.stereotype.Component;

// import java.util.List;
import org.springframework.batch.item.Chunk;

@Component
public class BatchItemWriteListener implements ItemWriteListener<Product> {
    private static final Logger log = LoggerFactory.getLogger(BatchItemWriteListener.class);

    @Override
    public void beforeWrite(Chunk<? extends Product> items) {}

    @Override
    public void afterWrite(Chunk<? extends Product> items) {}

    @Override
    public void onWriteError(Exception exception, Chunk<? extends Product> items) {
        log.error("Error writing products: {}", items, exception);
    }
}