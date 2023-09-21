package com.tde.mescloud.utility;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Setter
@Getter
@Component
public class LockUtil {

    private final CountDownLatch latch = new CountDownLatch(1);

    public void waitForExecute() throws InterruptedException {
        latch.await();
    }

    public void signalExecute() {
        latch.countDown();
    }

}
