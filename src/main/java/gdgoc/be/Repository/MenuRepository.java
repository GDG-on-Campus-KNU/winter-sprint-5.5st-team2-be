package gdgoc.be.Repository;

import gdgoc.be.domain.Category;
import gdgoc.be.domain.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu,Long> {

    // 카테고리별로 조회
    List<Menu> findByCategory(Category category);

    // 이름별로 검색 (포함 단어)
    // 매개변수 keyword 가 포함된 Menu 를 조회
    List<Menu> findByNameContaining(String keyword);
}
