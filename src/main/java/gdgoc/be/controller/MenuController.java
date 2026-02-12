package gdgoc.be.controller;

import gdgoc.be.common.ApiResponse;
import gdgoc.be.dto.MenuDetailResponse;
import gdgoc.be.dto.MenuResponse;
import gdgoc.be.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @Operation(summary = "메뉴 목록 조회", description = "전체 메뉴 목록을 조회합니다. 카테고리, 검색어, 정렬 옵션을 사용할 수 있습니다.")
    @GetMapping
    public ApiResponse<List<MenuResponse>> getMenus(
            @Parameter(description = "카테고리") @RequestParam(required = false) String category,
            @Parameter(description = "검색어") @RequestParam(required = false) String search,
            @Parameter(description = "정렬 옵션 (예: 'price,asc', 'price,desc', 'name')") @RequestParam(required = false) String sort)
    {

        List<MenuResponse> menus = menuService.findAllMenus(category, search, sort);
        return ApiResponse.success(menus);
    }

    @Operation(summary = "메뉴 상세 조회", description = "특정 메뉴의 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ApiResponse<MenuDetailResponse> getMenuDetail(@Parameter(description = "메뉴 ID") @PathVariable Long id) {
        return ApiResponse.success(menuService.findMenuDetail(id));
    }

}
