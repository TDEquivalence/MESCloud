package com.alcegory.mescloud.utility;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;

@Setter
@Getter
@Component
public class LockUtil {

    private final ConcurrentMap<String, CountDownLatch> locks = new ConcurrentHashMap<>();

    public void lock(String equipmentCode) {
        CountDownLatch latch = new CountDownLatch(1);
        CountDownLatch existingLatch = locks.putIfAbsent(equipmentCode, latch);
        if (existingLatch != null) {
            throw new IllegalStateException("Lock already acquired for equipmentCode: " + equipmentCode);
        }
        latch.countDown();
    }

    public void unlock(String equipmentCode) {
        CountDownLatch latch = locks.get(equipmentCode);
        if (latch == null) {
            throw new IllegalStateException("Lock not found for equipmentCode: " + equipmentCode);
        }
        latch.countDown();
        locks.remove(equipmentCode);
    }

    public void waitForExecute(String equipmentCode) throws InterruptedException {
        CountDownLatch latch = locks.get(equipmentCode);
        if (latch == null) {
            throw new IllegalStateException("Lock not found for equipmentCode: " + equipmentCode);
        }
        latch.await();
    }
}
