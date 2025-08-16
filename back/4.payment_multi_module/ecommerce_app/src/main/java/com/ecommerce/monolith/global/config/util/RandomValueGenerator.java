package com.ecommerce.monolith.global.config.util;

import com.ecommerce.monolith.domain.product.domain.Product.DiscountDTO;
import com.ecommerce.monolith.domain.product.domain.Product.OptionDTO;
import com.ecommerce.monolith.global.config.parser.ObjectMapperUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class RandomValueGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int NUM_CORES = Runtime.getRuntime().availableProcessors();
    private String[] uniqueStrings;
    private static int SIZE_OF_UNIQUE_STRINGS = 10;

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
    private ObjectMapper getObjectMapper() {
        return ObjectMapperUtil.getObjectMapper();
    }
    
    public String[] generateUniqueStrings(Integer count, Integer stringLength) {
        this.uniqueStrings = new String[count];
        int numberOfThreads = NUM_CORES;
        SIZE_OF_UNIQUE_STRINGS = count;
        
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
        int size = (int) ((max - min) * 10);
        double[] randomDoubles = new double[size];
        for (int i = 0; i < size; i++) {
            randomDoubles[i] = min + i * 0.1; // Increment by 0.1 for each element
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
    
    //주의!
    //string pre-generation 이후에 시작해야 에러 안난다!
    public List<String> generateRandomOptionsList(int numberOfOptions) throws JsonProcessingException {
        List<String> preGeneratedOptionJsons = new ArrayList<>(numberOfOptions);
        for (int i = 0; i < numberOfOptions; i++) {
            List<OptionDTO> options = generateRandomOptions(i);
            preGeneratedOptionJsons.add(getObjectMapper().writeValueAsString(options));
        }
        return preGeneratedOptionJsons;
    }
    private List<OptionDTO> generateRandomOptions(int index) {
        List<OptionDTO> options = new ArrayList<>();
//        int optionCount = ThreadLocalRandom.current().nextInt(1, 4); // 1 to 3 options
        int optionCount = 3; //많은 양을 bulk-insert할 때, random 변수 넣으면 속도가 매우매우 느려지기 때문에 fixed number로 채운다.
        for (int i = 0; i < optionCount; i++) {
            OptionDTO option = new OptionDTO();
            option.setName(uniqueStrings[index % SIZE_OF_UNIQUE_STRINGS]);
//            int valueCount = ThreadLocalRandom.current().nextInt(1, 4); // 1 to 3 options
            int valueCount = 3; //많은 양을 bulk-insert할 때, random 변수 넣으면 속도가 매우매우 느려지기 때문에 fixed number로 채운다.
            List<String> values = new ArrayList<>();
            for (int j = 0; j < valueCount; j++) {
                values.add(uniqueStrings[index / 2 % SIZE_OF_UNIQUE_STRINGS]);
            }
            option.setValues(values);
            options.add(option);
        }
        return options;
    }
    
    //주의!
    //string pre-generation 이후에 시작해야 에러 안난다!
    public List[] generateRandomDiscountsInJsonFormatAndDiscountDTOFormat(int numberOfDiscounts, double[] uniqueDoublesOneToHundred, LocalDateTime[] uniqueLocalDateTimeThreeMonthsPastToToday) throws JsonProcessingException {
        List<String> uniqueDiscountJsons = new ArrayList<>(numberOfDiscounts);
        List<List<DiscountDTO>> listOfRandomDiscounts = new ArrayList<>(numberOfDiscounts);
        
        for (int i = 0; i < numberOfDiscounts; i++) {
            List<DiscountDTO> discounts = generateRandomDiscountsInDiscountDTOFormat(i, uniqueDoublesOneToHundred, uniqueLocalDateTimeThreeMonthsPastToToday);
            listOfRandomDiscounts.add(discounts);
            uniqueDiscountJsons.add(getObjectMapper().writeValueAsString(discounts));
        }
        return new List[]{uniqueDiscountJsons, listOfRandomDiscounts};
    }
    
    private List<DiscountDTO> generateRandomDiscountsInDiscountDTOFormat(int index, double[] uniqueDoublesOneToHundred, LocalDateTime[] uniqueLocalDateTimeThreeMonthsPastToToday) {
        List<DiscountDTO> discounts = new ArrayList<>();
        int discountCount = 2;
        for (int i = 0; i < discountCount; i++) {
            DiscountDTO discount = new DiscountDTO();
//            discount.setType(i % 2 == 0 ? "PERCENTAGE" : "FLAT_RATE"); //massive bulk-insert 할 때, random variable 있으면 속도가 크게 저하되니, FLAT RATE 파트는 버린다.
            discount.setType("PERCENTAGE");
            discount.setValue(uniqueDoublesOneToHundred[index % 100]);
            LocalDateTime startDate = uniqueLocalDateTimeThreeMonthsPastToToday[index % 90];
//            discount.setStartDate(startDate.atOffset(OffsetDateTime.now().getOffset()).toString());
            discount.setStartDate(startDate.atOffset(OffsetDateTime.now().getOffset()));
//            discount.setEndDate(startDate.plusDays(45).atOffset(OffsetDateTime.now().getOffset()).toString()); //discount_start_day가 90일 전부터 랜덤하게 시작되니, 그 이후 45일까지 되도록 해서, 절반은 discount가 valid하고 나머지 반은 invalid하게 설정
            discount.setEndDate(startDate.plusDays(45).atOffset(OffsetDateTime.now().getOffset())); //discount_start_day가 90일 전부터 랜덤하게 시작되니, 그 이후 45일까지 되도록 해서, 절반은 discount가 valid하고 나머지 반은 invalid하게 설정
            discounts.add(discount);
        }
        return discounts;
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

