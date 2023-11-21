package com.cho.ecommerce.global.config.batch.step;

import com.cho.ecommerce.domain.member.entity.InactiveMemberEntity;
import com.cho.ecommerce.domain.member.entity.UserEntity;
import com.cho.ecommerce.domain.member.repository.AddressRepository;
import com.cho.ecommerce.domain.member.repository.InactiveMemberRepository;
import com.cho.ecommerce.domain.member.repository.UserAuthorityRepository;
import com.cho.ecommerce.domain.member.repository.UserRepository;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserToInactiveMemberStepConfig {
    
    private final Logger log = LoggerFactory.getLogger(UserToInactiveMemberStepConfig.class);
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private InactiveMemberRepository inactiveMemberRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private UserAuthorityRepository userAuthorityRepository;
    
    // Reader: Read all users, filter only inactive users
    @Bean
    public ItemReader<UserEntity> queryAllUsersReader() {
        return new ItemReader<UserEntity>() {
            private Iterator<UserEntity> inactiveUserIterator;
    
            @Override
            public UserEntity read() {
                if(inactiveUserIterator == null) {
                    inactiveUserIterator = userRepository.findAll()
                        .stream()
                        .filter(user -> !user.isEnabled())
                        .collect(Collectors.toList())
                        .iterator();
                }
                
                if (inactiveUserIterator.hasNext()) {
                    return inactiveUserIterator.next();
                } else {
                    return null; // Return null to indicate the end of data
                }
            }
        };
    }
    
    // Processor: Process and filter out inactive users
    @Bean
    public ItemProcessor<UserEntity, InactiveMemberEntity> inactiveUserProcessor() {
        return new ItemProcessor<UserEntity, InactiveMemberEntity>() {
    
            @Override
            public InactiveMemberEntity process(UserEntity user) throws Exception {
                //member + authorities + address를 inactiveMember에 옮겨닮기
                InactiveMemberEntity inactiveMember = InactiveMemberEntity.builder()
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .name(user.getName())
                    .password(user.getPassword())
                    .role(user.getRole())
                    .failedAttempt(user.getFailedAttempt())
                    .enabled(user.getEnabled())
                    .createdAt(user.getCreated())
                    .updatedAt(user.getUpdated())
                    .street(user.getAddress().getStreet())
                    .city(user.getAddress().getCity())
                    .state(user.getAddress().getState())
                    .country(user.getAddress().getCountry())
                    .zipCode(user.getAddress().getZipCode())
                    .build();
    
                
                //TODO - writer()에서 실패시, processor()에 delete가 rollback 되는지 확인
                //user 지우기 (userAuthority, address, order, orderItems, productOptionVariation을 cascade로 지운다)
                userRepository.deleteById(user.getMemberId());
    
                return inactiveMember;
            }
        };
    }
    
    // Writer: Write inactive users to INACTIVE_MEMBER table
    @Bean
    public ItemWriter<InactiveMemberEntity> inactiveMemberWriter() {
        return new ItemWriter<InactiveMemberEntity>() {
            @Override
            public void write(List<? extends InactiveMemberEntity> inactiveUsers) {
                inactiveMemberRepository.saveAll(inactiveUsers);
            }
        };
    }
    
    // Define the step
    @Bean
    public Step userToInactiveMemberStep(StepBuilderFactory stepBuilderFactory,
        ItemReader<UserEntity> queryAllUsersReader,
        ItemProcessor<UserEntity, InactiveMemberEntity> inactiveUserProcessor,
        ItemWriter<InactiveMemberEntity> inactiveMemberWriter) {
        
        return stepBuilderFactory.get("userToInactiveMemberStep")
            .<UserEntity, InactiveMemberEntity>chunk(1000)
            .reader(queryAllUsersReader)
            .processor(inactiveUserProcessor)
            .writer(inactiveMemberWriter)
            .build();
    }
}
