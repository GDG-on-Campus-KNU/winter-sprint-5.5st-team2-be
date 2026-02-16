package gdgoc.be.controller;

import gdgoc.be.common.ApiResponse;
import gdgoc.be.dto.MenuDetailResponse;
import gdgoc.be.dto.MenuResponse;
import gdgoc.be.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    /*
        조건 없이 Menu Table 에 있는 모든 데이터를 반환
     */
    @GetMapping
    public ApiResponse<List<MenuResponse>> getMenus(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sort) {

        List<MenuResponse> menus = menuService.findAllMenus(category, search, sort);
        return ApiResponse.success(menus);
    }

    @GetMapping("/{id}")
    public ApiResponse<MenuDetailResponse> getMenuDetail(@PathVariable Long id) {
        return ApiResponse.success(menuService.findMenuDetail(id));
    }

}
