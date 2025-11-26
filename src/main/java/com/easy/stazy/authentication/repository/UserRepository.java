package com.easy.stazy.authentication.repository;

import com.easy.stazy.authentication.entities.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UsersEntity, Long> {
    boolean existsByPhoneNumber(String phoneNumber);


    Optional<UsersEntity> findByPhoneNumber(String phoneNumber);

    @Query(value = "SELECT u.id FROM users u WHERE u.phone_number = :phoneNumber", nativeQuery = true)
    Long findUserIdByPhoneNumber(@Param("phoneNumber") String phoneNumber);

    @Query(value = "SELECT * FROM users WHERE phone_number = :phoneNumber AND role = :roleType", nativeQuery = true)
    Optional<UsersEntity> findByPhoneNumberAndRole(@Param("phoneNumber") String mobileNumber, @Param("roleType") String roleType);//    Optional<Users> findByUsernameAndPassword(String username, String password);

    boolean existsByPhoneNumberAndRole(String phoneNumber, String roleType);

    Optional<UsersEntity> findNameById(Long userId);
}
