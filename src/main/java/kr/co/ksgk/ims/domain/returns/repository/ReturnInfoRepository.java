package kr.co.ksgk.ims.domain.returns.repository;

import kr.co.ksgk.ims.domain.returns.entity.ProcessingStatus;
import kr.co.ksgk.ims.domain.returns.entity.ReturnInfo;
import kr.co.ksgk.ims.domain.returns.entity.ReturnStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReturnInfoRepository extends JpaRepository<ReturnInfo, Long> {

    Optional<ReturnInfo> findByOriginalInvoice(String originalInvoice);

    Optional<ReturnInfo> findByReturnInvoice(String returnInvoice);

    @Query("SELECT ri FROM ReturnInfo ri " +
            "JOIN FETCH ri.returnMall rm " +
            "JOIN FETCH ri.returnHandler rh " +
            "JOIN FETCH rm.brand mb " +
            "JOIN FETCH rh.brand hb " +
            "WHERE rm.brand.id IN :brandIds OR rh.brand.id IN :brandIds")
    List<ReturnInfo> findByManagedBrands(@Param("brandIds") List<Long> brandIds);

    @Query(value = "SELECT ri FROM ReturnInfo ri " +
            "JOIN ri.returnMall rm " +
            "JOIN ri.returnHandler rh " +
            "WHERE (rm.brand.id IN :brandIds OR rh.brand.id IN :brandIds) " +
            "AND (:startDate IS NULL OR CAST(ri.createdAt AS date) >= :startDate) " +
            "AND (:endDate IS NULL OR CAST(ri.createdAt AS date) <= :endDate) " +
            "AND (:status IS NULL OR ri.returnStatus = :status) " +
            "AND (:processingStatus IS NULL OR ri.processingStatus = :processingStatus) " +
            "AND (:search IS NULL OR :search = '' OR " +
            "ri.buyer LIKE %:search% OR " +
            "ri.receiver LIKE %:search% OR " +
            "ri.phone LIKE %:search% OR " +
            "ri.productName LIKE %:search% OR " +
            "ri.originalInvoice LIKE %:search% OR " +
            "ri.returnInvoice LIKE %:search% OR " +
            "rh.name LIKE %:search%)",
            countQuery = "SELECT COUNT(ri) FROM ReturnInfo ri " +
                    "JOIN ri.returnMall rm " +
                    "JOIN ri.returnHandler rh " +
                    "WHERE (rm.brand.id IN :brandIds OR rh.brand.id IN :brandIds) " +
                    "AND (:startDate IS NULL OR CAST(ri.createdAt AS date) >= :startDate) " +
                    "AND (:endDate IS NULL OR CAST(ri.createdAt AS date) <= :endDate) " +
                    "AND (:status IS NULL OR ri.returnStatus = :status) " +
                    "AND (:processingStatus IS NULL OR ri.processingStatus = :processingStatus) " +
                    "AND (:search IS NULL OR :search = '' OR " +
                    "ri.buyer LIKE %:search% OR " +
                    "ri.receiver LIKE %:search% OR " +
                    "ri.phone LIKE %:search% OR " +
                    "ri.productName LIKE %:search% OR " +
                    "ri.originalInvoice LIKE %:search% OR " +
                    "ri.returnInvoice LIKE %:search% OR " +
                    "rh.name LIKE %:search%)")
    Page<ReturnInfo> findByManagedBrandsWithFilters(
            @Param("brandIds") List<Long> brandIds,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") ReturnStatus status,
            @Param("processingStatus") ProcessingStatus processingStatus,
            @Param("search") String search,
            Pageable pageable);

    @Query(value = "SELECT ri FROM ReturnInfo ri " +
            "JOIN ri.returnHandler rh " +
            "WHERE (:startDate IS NULL OR CAST(ri.createdAt AS date) >= :startDate) " +
            "AND (:endDate IS NULL OR CAST(ri.createdAt AS date) <= :endDate) " +
            "AND (:status IS NULL OR ri.returnStatus = :status) " +
            "AND (:processingStatus IS NULL OR ri.processingStatus = :processingStatus) " +
            "AND (:search IS NULL OR :search = '' OR " +
            "ri.buyer LIKE %:search% OR " +
            "ri.receiver LIKE %:search% OR " +
            "ri.phone LIKE %:search% OR " +
            "ri.productName LIKE %:search% OR " +
            "ri.originalInvoice LIKE %:search% OR " +
            "ri.returnInvoice LIKE %:search% OR " +
            "rh.name LIKE %:search%)",
            countQuery = "SELECT COUNT(ri) FROM ReturnInfo ri " +
                    "JOIN ri.returnHandler rh " +
                    "WHERE (:startDate IS NULL OR CAST(ri.createdAt AS date) >= :startDate) " +
                    "AND (:endDate IS NULL OR CAST(ri.createdAt AS date) <= :endDate) " +
                    "AND (:status IS NULL OR ri.returnStatus = :status) " +
                    "AND (:processingStatus IS NULL OR ri.processingStatus = :processingStatus) " +
                    "AND (:search IS NULL OR :search = '' OR " +
                    "ri.buyer LIKE %:search% OR " +
                    "ri.receiver LIKE %:search% OR " +
                    "ri.phone LIKE %:search% OR " +
                    "ri.productName LIKE %:search% OR " +
                    "ri.originalInvoice LIKE %:search% OR " +
                    "ri.returnInvoice LIKE %:search% OR " +
                    "rh.name LIKE %:search%)")
    Page<ReturnInfo> findAllWithFilters(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") ReturnStatus status,
            @Param("processingStatus") ProcessingStatus processingStatus,
            @Param("search") String search,
            Pageable pageable);
}
