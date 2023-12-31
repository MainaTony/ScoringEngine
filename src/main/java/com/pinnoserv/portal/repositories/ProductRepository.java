package com.pinnoserv.portal.repositories;

import com.pinnoserv.portal.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author mwendwakelvin
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
//    Optional<Product> findByProductId(Integer id);

//    List<Product> findAllByProgramIdFk(Integer programId);
}