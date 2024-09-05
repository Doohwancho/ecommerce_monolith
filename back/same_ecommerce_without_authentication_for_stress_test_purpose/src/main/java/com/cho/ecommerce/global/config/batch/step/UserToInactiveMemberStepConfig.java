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
import lombok.Builder;
import lombok.Getter;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class UserToInactiveMemberStepConfig {
    
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private InactiveMemberRepository inactiveMemberRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private UserAuthorityRepository userAuthorityRepository;
    
    @Getter
    @Builder
    public static class UserInactiveMemberDTO {
        
        private UserEntity userEntity;
        private InactiveMemberEntity inactiveMemberEntity;
    }
    
    // Reader: Read all users, filter only inactive users
    @Bean
    public ItemReader<UserEntity> queryAllUsersReader() {
        return new ItemReader<UserEntity>() {
            private Iterator<UserEntity> inactiveUserIterator;
            
            @Override
            public UserEntity read() {
                if (inactiveUserIterator == null) {
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
    public ItemProcessor<UserEntity, UserInactiveMemberDTO> inactiveUserProcessor() {
        return new ItemProcessor<UserEntity, UserInactiveMemberDTO>() {
            
            @Override
            public UserInactiveMemberDTO process(UserEntity user) throws Exception {
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
                
                UserInactiveMemberDTO memberDTO = UserInactiveMemberDTO.builder()
                    .userEntity(user)
                    .inactiveMemberEntity(inactiveMember)
                    .build();
                
                return memberDTO;
            }
        };
    }
    
    // Writer: Write inactive users to INACTIVE_MEMBER table
    @Bean
    public ItemWriter<UserInactiveMemberDTO> inactiveMemberWriter() {
        return new ItemWriter<UserInactiveMemberDTO>() {
            @Override
            public void write(List<? extends UserInactiveMemberDTO> memberDTOs) throws Exception {
                for (UserInactiveMemberDTO dto : memberDTOs) {
                    inactiveMemberRepository.save(dto.getInactiveMemberEntity());
                    userRepository.deleteById(dto.getUserEntity().getMemberId());
//                    throw new Exception("안돼!"); //TODO - error: write()시 delete은 rollback이 되는데 save한건 롤백이 안됨
                }
            }
        };
    }
    
    // Define the step
    @Bean
    public Step userToInactiveMemberStep(
        StepBuilderFactory stepBuilderFactory,
        PlatformTransactionManager transactionManager,
        ItemReader<UserEntity> queryAllUsersReader,
        ItemProcessor<UserEntity, UserInactiveMemberDTO> inactiveUserProcessor,
        ItemWriter<UserInactiveMemberDTO> inactiveMemberWriter) {
        
        // note! - spring batch는 외부 transaction을 허용하지 않는다. Step에서 트랜젝션 만들어서 넣어줘야 한다.
//        DefaultTransactionAttribute attribute = new DefaultTransactionAttribute();
//        attribute.setIsolationLevel(TransactionDefinition.ISOLATION_SERIALIZABLE);
//        attribute.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
//        attribute.rollbackOn(new Exception());
//        attribute.setTimeout(30); // 30 seconds
        
        return stepBuilderFactory.get("userToInactiveMemberStep")
            .<UserEntity, UserInactiveMemberDTO>chunk(1000)
            .reader(queryAllUsersReader)
            .processor(inactiveUserProcessor)
            .writer(inactiveMemberWriter)
//            .transactionManager(transactionManager)
//            .transactionAttribute(attribute)
            .build();
    }
}
