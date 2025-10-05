package kr.co.ksgk.ims.domain.returns.repository;

import kr.co.ksgk.ims.domain.returns.entity.ReturnMall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReturnMallRepository extends JpaRepository<ReturnMall, Long> {

    @Query("SELECT rm FROM ReturnMall rm WHERE rm.brand.id = :brandId")
    List<ReturnMall> findByBrandId(@Param("brandId") Long brandId);

    @Query("SELECT rm FROM ReturnMall rm WHERE rm.brand.id IN :brandIds")
    List<ReturnMall> findByBrandIdIn(@Param("brandIds") List<Long> brandIds);
}
