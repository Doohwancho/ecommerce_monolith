package com.cho.ecommerce.domain.order.repository;

import com.cho.ecommerce.domain.order.entity.DenormalizedOrderEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<DenormalizedOrderEntity, Long> {
    
//    @Query("SELECT o FROM DenormalizedOrderEntity o WHERE o.memberName = :userName ORDER BY o.orderDate DESC") //order by는 cost-heavy하니까 디비에서 하지말고 자원 남는 백엔드 서버나 프론트에서 하는게 좋지 않을까?
    @Query("SELECT o FROM DenormalizedOrderEntity o WHERE o.memberName = :username")
    List<DenormalizedOrderEntity> findOrdersByMemberName(@Param("username") String username);
}