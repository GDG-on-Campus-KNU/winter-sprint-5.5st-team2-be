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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MenuService {

    private final MenuRepository menuRepository;

    public List<MenuResponse> findAllMenus(String category, String search, String sort) {

        if(category != null) {
            return menuRepository.findByCategory(Category.valueOf(category))
                    .stream()
                    .map(MenuResponse::from)
                    .collect(Collectors.toList());
        }

        if(search != null) {
            return menuRepository.findByNameContaining(search)
                    .stream()
                    .map(MenuResponse::from)
                    .collect(Collectors.toList());
        }

        return menuRepository.findAll()
                .stream()
                .map(MenuResponse::from)
                .collect(Collectors.toList());
    }

    public MenuDetailResponse findMenuDetail(Long id) {

        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.MENU_NOT_FOUND));

        return MenuDetailResponse.from(menu);
    }
}