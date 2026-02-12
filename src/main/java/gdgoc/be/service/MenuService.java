package gdgoc.be.service;

import gdgoc.be.Repository.MenuRepository;
import gdgoc.be.domain.Category;
import gdgoc.be.domain.Menu;
import gdgoc.be.dto.MenuDetailResponse;
import gdgoc.be.dto.MenuResponse;
import gdgoc.be.exception.BusinessErrorCode;
import gdgoc.be.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;

    public List<MenuResponse> findAllMenus(String category, String search, String sort) {

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

        return menus.stream()
                .map(MenuResponse::from)
                .collect(Collectors.toList());

    }

    public MenuDetailResponse findMenuDetail(Long id) {

        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.MENU_NOT_FOUND));

        return MenuDetailResponse.from(menu);
    }
}