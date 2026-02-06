package gdgoc.be.dto;


import lombok.Builder;
import lombok.Getter;


import lombok.*;
import java.math.BigDecimal;

@Getter @Builder
@NoArgsConstructor @AllArgsConstructor
public class MenuResponse {
    private Long id;
    private String name;
    private int price;
    private String category;
}