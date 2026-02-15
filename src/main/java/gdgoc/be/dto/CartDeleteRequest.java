package gdgoc.be.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public record CartDeleteRequest(
        List<Long> itemIds
) {

}