package gdgoc.be.dto.cart;

import java.util.List;

public record CartBulkDeleteRequest(
        List<Long> cartItemIds
) {
}
