package com.alcegory.mescloud.utility;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Setter
@Getter
@Log
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
        } catch (IllegalStateException e) {
            log.severe(() -> String.format("Failed acquire lock for equipment with code [%s]", equipmentCode));
        }
    }


    public void unlock(String equipmentCode) {
        Lock lock = lockMap.get(equipmentCode);
        CountDownLatch latch = locks.get(equipmentCode);

        if (lock == null || latch == null) {
            throw new IllegalStateException("Lock not found for equipmentCode: " + equipmentCode);
        }

        try {
            latch.countDown();
        } finally {
            lock.unlock();
            locks.remove(equipmentCode);
        }
    }

    public void waitForExecute(String equipmentCode) throws InterruptedException {
        CountDownLatch latch = locks.get(equipmentCode);
        if (latch == null) {
            throw new IllegalStateException("Lock not found for equipmentCode: " + equipmentCode);
        }
        latch.await();
    }

    public boolean hasLock(String equipmentCode) {
        return lockMap.containsKey(equipmentCode);
    }
}

