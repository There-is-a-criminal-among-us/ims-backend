package kr.co.ksgk.ims.domain.returns.repository;

import kr.co.ksgk.ims.domain.returns.entity.ReturnHandler;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReturnHandlerRepository extends JpaRepository<ReturnHandler, Long> {

    @Query("SELECT rh FROM ReturnHandler rh WHERE rh.brand.id = :brandId")
    List<ReturnHandler> findByBrandId(@Param("brandId") Long brandId);

    @Query("SELECT rh FROM ReturnHandler rh WHERE rh.brand.id IN :brandIds")
    List<ReturnHandler> findByBrandIdIn(@Param("brandIds") List<Long> brandIds);
}
