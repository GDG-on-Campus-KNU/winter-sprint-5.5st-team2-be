package gdgoc.be.dto;

import lombok.Builder;

@Builder
public record CheckEmailResponse(
    boolean isAvailable,
    String message
) {
}
