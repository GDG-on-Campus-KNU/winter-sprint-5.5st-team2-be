package gdgoc.be.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CartDeleteRequest {

    // itemId 를 일괄로 받아 한 번에 삭제
    public List<Long> itemIds;
}
