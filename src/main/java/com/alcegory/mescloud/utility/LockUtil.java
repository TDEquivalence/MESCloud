package com.alcegory.mescloud.utility;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Setter
@Getter
@Component
public class LockUtil {

    private final ConcurrentMap<String, CountDownLatch> locks = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Lock> lockMap = new ConcurrentHashMap<>();

    public void lock(String equipmentCode) {
        Lock lock = lockMap.computeIfAbsent(equipmentCode, k -> new ReentrantLock());
        lock.lock();
        try {
            CountDownLatch latch = new CountDownLatch(1);
            CountDownLatch existingLatch = locks.putIfAbsent(equipmentCode, latch);
            if (existingLatch != null) {
                throw new IllegalStateException("Lock already acquired for equipmentCode: " + equipmentCode);
            }
            latch.countDown();
        } finally {
            lock.unlock();
        }
    }

    public void unlock(String equipmentCode) {
        Lock lock = lockMap.get(equipmentCode);
        if (lock != null) {
            lock.lock();
        }
        try {
            CountDownLatch latch = locks.get(equipmentCode);
            if (latch == null) {
                throw new IllegalStateException("Lock not found for equipmentCode: " + equipmentCode);
            }
            latch.countDown();
            locks.remove(equipmentCode);
        } finally {
            if (lock != null) {
                lock.unlock();
            }
        }
    }

    public void waitForExecute(String equipmentCode) throws InterruptedException {
        CountDownLatch latch = locks.get(equipmentCode);
        if (latch == null) {
            throw new IllegalStateException("Lock not found for equipmentCode: " + equipmentCode);
        }
        latch.await();
    }
}

