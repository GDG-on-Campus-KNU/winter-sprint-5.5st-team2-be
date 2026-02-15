package gdgoc.be.dto;

import java.util.List;

public record CartDeleteRequest(
        List<Long> itemIds
) {

}