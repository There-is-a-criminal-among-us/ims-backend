package kr.co.ksgk.ims.domain.brand.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrandDto {
    private Long id;
    private String name;
    private String note;
    private LocalDateTime deletedAt;
}