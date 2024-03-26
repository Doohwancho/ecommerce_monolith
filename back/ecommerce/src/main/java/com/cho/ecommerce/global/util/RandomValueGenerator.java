package com.cho.ecommerce.global.util;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.stereotype.Component;

@Component
public class RandomValueGenerator {
    
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private final Integer NUMBER_OF_THREAD = 8;

//    public void main(String[] args) {
//        long startTime = System.currentTimeMillis();
//        int count = 1_000_000; // Number of strings to generate
//        Integer stringLength = 10;
//
//        ConcurrentLinkedQueue<String> uniqueStrings = generateUniqueStrings(count, stringLength);
//
//        System.out.println("Generated " + uniqueStrings.size() + " unique strings");
//
//        // Process the strings
//        while (!uniqueStrings.isEmpty()) {
//            String str = uniqueStrings.poll();
//            // Process the string
//            // ...
//        }
//
//        long endTime = System.currentTimeMillis();
//        long duration = endTime - startTime;
//        System.out.println("Total execution time: " + duration + " ms");
//    }
    
    public ConcurrentLinkedQueue<String> generateUniqueStrings(Integer count,
        Integer stringLength) {
        ConcurrentLinkedQueue<String> uniqueStrings = new ConcurrentLinkedQueue<>();
        Set<String> uniqueSet = ConcurrentHashMap.newKeySet(count);
        
        Thread[] threads = new Thread[NUMBER_OF_THREAD];
        for (int i = 0; i < NUMBER_OF_THREAD; i++) {
            threads[i] = new Thread(() -> {
                ThreadLocalRandom random = ThreadLocalRandom.current();
                while (uniqueSet.size() < count) {
                    String randomString = generateRandomString(random, stringLength);
                    if (uniqueSet.add(randomString)) {
                        uniqueStrings.offer(randomString);
                    }
                }
            });
            threads[i].start();
        }
        
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        return uniqueStrings;
    }
    
    private String generateRandomString(ThreadLocalRandom random, Integer stringLength) {
        StringBuilder sb = new StringBuilder(stringLength);
        for (int i = 0; i < stringLength; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(randomIndex));
        }
        return sb.toString();
    }
    
    public ConcurrentLinkedQueue<Integer> generateRandomIntegers(int count, int min, int max) {
        ConcurrentLinkedQueue<Integer> randomIntegers = new ConcurrentLinkedQueue<>();
        Thread[] threads = new Thread[NUMBER_OF_THREAD];
        
        for (int i = 0; i < NUMBER_OF_THREAD; i++) {
            threads[i] = new Thread(() -> {
                ThreadLocalRandom random = ThreadLocalRandom.current();
                int countPerThread = count / NUMBER_OF_THREAD;
                for (int j = 0; j < countPerThread; j++) {
                    randomIntegers.offer(random.nextInt(min, max + 1));
                }
            });
            threads[i].start();
        }
        
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        return randomIntegers;
    }
    
    public ConcurrentLinkedQueue<Double> generateRandomDoubles(int count, double min, double max) {
        ConcurrentLinkedQueue<Double> randomDoubles = new ConcurrentLinkedQueue<>();
        Thread[] threads = new Thread[NUMBER_OF_THREAD];
        
        for (int i = 0; i < NUMBER_OF_THREAD; i++) {
            threads[i] = new Thread(() -> {
                ThreadLocalRandom random = ThreadLocalRandom.current();
                int countPerThread = count / NUMBER_OF_THREAD;
                for (int j = 0; j < countPerThread; j++) {
                    randomDoubles.offer(random.nextDouble(min, max));
                }
            });
            threads[i].start();
        }
        
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        return randomDoubles;
    }
    
    public ConcurrentLinkedQueue<LocalDateTime> generateRandomDates(int count, int daysFromToday) {
        ConcurrentLinkedQueue<LocalDateTime> randomDates = new ConcurrentLinkedQueue<>();
        Thread[] threads = new Thread[NUMBER_OF_THREAD];
        
        LocalDateTime startInclusive = LocalDateTime.now().minusDays(daysFromToday);
        LocalDateTime endExclusive = LocalDateTime.now();
        
        for (int i = 0; i < NUMBER_OF_THREAD; i++) {
            threads[i] = new Thread(() -> {
                ThreadLocalRandom random = ThreadLocalRandom.current();
                int countPerThread = count / NUMBER_OF_THREAD;
                long startSeconds = startInclusive.toEpochSecond(ZoneOffset.UTC);
                long endSeconds = endExclusive.toEpochSecond(ZoneOffset.UTC);
                
                for (int j = 0; j < countPerThread; j++) {
                    long randomSeconds = random.nextLong(startSeconds, endSeconds);
                    LocalDateTime randomDate = LocalDateTime.ofEpochSecond(randomSeconds, 0,
                        ZoneOffset.UTC);
                    randomDates.offer(randomDate);
                }
            });
            threads[i].start();
        }
        
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        return randomDates;
    }
    //start~end date를 넣어주면 그 사이에 랜덤한 날짜를 추출하는 함수
//    public ConcurrentLinkedQueue<LocalDateTime> generateRandomDates(int count,
//        LocalDateTime startInclusive, LocalDateTime endExclusive) {
//        ConcurrentLinkedQueue<LocalDateTime> randomDates = new ConcurrentLinkedQueue<>();
//        Thread[] threads = new Thread[NUMBER_OF_THREAD];
//
//        for (int i = 0; i < NUMBER_OF_THREAD; i++) {
//            threads[i] = new Thread(() -> {
//                ThreadLocalRandom random = ThreadLocalRandom.current();
//                int countPerThread = count / NUMBER_OF_THREAD;
//                long startSeconds = startInclusive.toEpochSecond(ZoneOffset.UTC);
//                long endSeconds = endExclusive.toEpochSecond(ZoneOffset.UTC);
//                for (int j = 0; j < countPerThread; j++) {
//                    long randomSeconds = random.nextLong(startSeconds, endSeconds);
//                    LocalDateTime randomDate = LocalDateTime.ofEpochSecond(randomSeconds, 0,
//                        ZoneOffset.UTC);
//                    randomDates.offer(randomDate);
//                }
//            });
//            threads[i].start();
//        }
//
//        for (Thread thread : threads) {
//            try {
//                thread.join();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//
//        return randomDates;
//    }
}
