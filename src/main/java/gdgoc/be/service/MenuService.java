package gdgoc.be.service;

import gdgoc.be.Repository.MenuRepository;
import gdgoc.be.domain.Category;
import gdgoc.be.domain.Menu;
import gdgoc.be.dto.MenuDetailResponse;
import gdgoc.be.dto.MenuResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;

    public List<MenuResponse> findAllMenus(String category, String search, String sort) {

        // 1. MenuRepository 에서 Entity 리스트를 가져옴
        // category -> search -> sort(최신식/가격순) 순서로 탐색을 진행
        List<Menu> menus;
        if(category != null) {
            menus = menuRepository.findByCategory(Category.valueOf(category));
        }
        else if(search != null) {
            menus = menuRepository.findByNameContaining(search);
        }
        else {
            menus = menuRepository.findAll();
        }

        // 2. Entity -> DTO 로 전환하여 반환
        return menus.stream()
                .map(menu -> MenuResponse.builder()
                        .id(menu.getId())
                        .name(menu.getName())
                        .price(menu.getPrice())
                        .category(menu.getCategory().name())
                        .build())
                .collect(Collectors.toList());
    }

    public MenuDetailResponse findMenuDetail(Long id) {

        // id 에 맞는 menu 가 존재하지 않을 시 Exception 반환
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu_NOT_FOUND"));

        return MenuDetailResponse.builder()
                .id(menu.getId())
                .name(menu.getName())
                .price(menu.getPrice())
                .description(menu.getDescription())
                .stock(menu.getStock())
                .category(menu.getCategory().name())
                .isAvailable(menu.getStock() > 0)
                .build();

    }
}