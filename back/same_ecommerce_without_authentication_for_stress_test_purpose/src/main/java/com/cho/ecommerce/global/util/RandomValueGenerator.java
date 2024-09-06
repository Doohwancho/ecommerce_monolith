package com.cho.ecommerce.global.util;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.stereotype.Component;

@Component
public class RandomValueGenerator {
    
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    
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
    
    public String[] generateUniqueStrings(Integer numberOfCores, Integer count, Integer stringLength) {
        String[] uniqueStrings = new String[count];
        int numberOfThreads = numberOfCores;
        
        Thread[] threads = new Thread[numberOfThreads];
        for (int i = 0; i < numberOfThreads; i++) {
            int startIndex = i * (count / numberOfThreads);
            int endIndex =
                (i == numberOfThreads - 1) ? count : (i + 1) * (count / numberOfThreads);
            
            threads[i] = new Thread(() -> {
                ThreadLocalRandom random = ThreadLocalRandom.current();
                for (int j = startIndex; j < endIndex; j++) {
                    uniqueStrings[j] = generateRandomString(random, stringLength);
                }
            });
            threads[i].start();
        }
        
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
                throw new RuntimeException("Thread interrupted while generating unique strings", e);
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
    
    public int[] generateRandomIntegers(int count, int min, int max) {
        int[] randomIntegers = new int[count];
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < count; i++) {
            randomIntegers[i] = random.nextInt(min, max + 1);
        }
        return randomIntegers;
    }
    
    public double[] generateRandomDoublesByPointOne(double min, double max) {
        int size = ((int) max - (int) min) * 10;
        double[] randomDoubles = new double[size];
        for (int i = 0; i < size; i++) {
            randomDoubles[i] = min + 0.1;
        }
        return randomDoubles;
    }
    
    public double[] generateRandomDoubles(int count, double min, double max) {
        double[] randomDoubles = new double[count];
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < count; i++) {
            randomDoubles[i] = random.nextDouble(min, max);
        }
        return randomDoubles;
    }
    
    public LocalDateTime[] generateRandomDates(int count, int daysFromToday) {
        LocalDateTime[] randomDates = new LocalDateTime[count];
        
        LocalDateTime startInclusive = LocalDateTime.now().minusDays(daysFromToday);
        LocalDateTime endExclusive = LocalDateTime.now();
        
        ThreadLocalRandom random = ThreadLocalRandom.current();
        long startSeconds = startInclusive.toEpochSecond(ZoneOffset.UTC);
        long endSeconds = endExclusive.toEpochSecond(ZoneOffset.UTC);
        
        for (int i = 0; i < count; i++) {
            long randomSeconds = random.nextLong(startSeconds, endSeconds);
            LocalDateTime randomDate = LocalDateTime.ofEpochSecond(randomSeconds, 0,
                ZoneOffset.UTC);
            randomDates[i] = randomDate;
        }
        
        return randomDates;
    }
    
    /*********************************************************************8
     * ConcurrentLinkedQueue에 담고 .poll()하는 방식
     * 단점: 객체를 수백만개 만들어서 minor gc가 오래, 자주 일어나 latency가 느려진다.
     */

//    public ConcurrentLinkedQueue<String> generateUniqueStrings(Integer count,
//        Integer stringLength) {
//        ConcurrentLinkedQueue<String> uniqueStrings = new ConcurrentLinkedQueue<>();
//        Set<String> uniqueSet = ConcurrentHashMap.newKeySet(count);
//
//        Thread[] threads = new Thread[NUMBER_OF_THREAD];
//        for (int i = 0; i < NUMBER_OF_THREAD; i++) {
//            threads[i] = new Thread(() -> {
//                ThreadLocalRandom random = ThreadLocalRandom.current();
//                while (uniqueSet.size() < count) {
//                    String randomString = generateRandomString(random, stringLength);
//                    if (uniqueSet.add(randomString)) {
//                        uniqueStrings.offer(randomString);
//                    }
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
//        return uniqueStrings;
//    }
//
//    private String generateRandomString(ThreadLocalRandom random, Integer stringLength) {
//        StringBuilder sb = new StringBuilder(stringLength);
//        for (int i = 0; i < stringLength; i++) {
//            int randomIndex = random.nextInt(CHARACTERS.length());
//            sb.append(CHARACTERS.charAt(randomIndex));
//        }
//        return sb.toString();
//    }
//
//    public ConcurrentLinkedQueue<Integer> generateRandomIntegers(int count, int min, int max) {
//        ConcurrentLinkedQueue<Integer> randomIntegers = new ConcurrentLinkedQueue<>();
//        Thread[] threads = new Thread[NUMBER_OF_THREAD];
//
//        for (int i = 0; i < NUMBER_OF_THREAD; i++) {
//            threads[i] = new Thread(() -> {
//                ThreadLocalRandom random = ThreadLocalRandom.current();
//                int countPerThread = count / NUMBER_OF_THREAD;
//                for (int j = 0; j < countPerThread; j++) {
//                    randomIntegers.offer(random.nextInt(min, max + 1));
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
//        return randomIntegers;
//    }
//
//    public ConcurrentLinkedQueue<Double> generateRandomDoubles(int count, double min, double max) {
//        ConcurrentLinkedQueue<Double> randomDoubles = new ConcurrentLinkedQueue<>();
//        Thread[] threads = new Thread[NUMBER_OF_THREAD];
//
//        for (int i = 0; i < NUMBER_OF_THREAD; i++) {
//            threads[i] = new Thread(() -> {
//                ThreadLocalRandom random = ThreadLocalRandom.current();
//                int countPerThread = count / NUMBER_OF_THREAD;
//                for (int j = 0; j < countPerThread; j++) {
//                    randomDoubles.offer(random.nextDouble(min, max));
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
//        return randomDoubles;
//    }
//
//    public ConcurrentLinkedQueue<LocalDateTime> generateRandomDates(int count, int daysFromToday) {
//        ConcurrentLinkedQueue<LocalDateTime> randomDates = new ConcurrentLinkedQueue<>();
//        Thread[] threads = new Thread[NUMBER_OF_THREAD];
//
//        LocalDateTime startInclusive = LocalDateTime.now().minusDays(daysFromToday);
//        LocalDateTime endExclusive = LocalDateTime.now();
//
//        for (int i = 0; i < NUMBER_OF_THREAD; i++) {
//            threads[i] = new Thread(() -> {
//                ThreadLocalRandom random = ThreadLocalRandom.current();
//                int countPerThread = count / NUMBER_OF_THREAD;
//                long startSeconds = startInclusive.toEpochSecond(ZoneOffset.UTC);
//                long endSeconds = endExclusive.toEpochSecond(ZoneOffset.UTC);
//
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
