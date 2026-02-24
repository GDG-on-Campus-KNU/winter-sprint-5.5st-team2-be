package gdgoc.be.controller;

import gdgoc.be.dto.product.ProductDetailResponse;
import gdgoc.be.dto.product.ProductResponse;
import gdgoc.be.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Test
    @DisplayName("상품 목록 조회 테스트")
    void getProductsTest() throws Exception {
        ProductResponse response = ProductResponse.builder()
                .id(1L)
                .brand("Nike")
                .name("Air Jordan")
                .imageUrl("image.jpg")
                .originalPrice(200000)
                .discountRate(10)
                .price(180000)
                .stock(50)
                .isAvailable(true)
                .category("CHICKEN")
                .build();

        given(productService.findAllProducts(any(), any(), any())).willReturn(List.of(response));

        mockMvc.perform(get("/api/products")
                        .param("category", "CHICKEN")
                        .param("sort", "price,desc"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("Air Jordan"))
                .andExpect(jsonPath("$.data[0].brand").value("Nike"));
    }

    @Test
    @DisplayName("상품 상세 조회 테스트")
    void getProductDetailTest() throws Exception {
        ProductDetailResponse response = ProductDetailResponse.builder()
                .id(1L)
                .brand("Nike")
                .name("Air Jordan")
                .imageUrl("image.jpg")
                .originalPrice(200000)
                .discountRate(10)
                .price(180000)
                .stock(50)
                .isAvailable(true)
                .description("Good shoes")
                .sizesOptions(List.of("260", "270"))
                .detailImages(List.of("detail1.jpg"))
                .galleryImages(List.of("gallery1.jpg"))
                .rating(4.8)
                .category("CHICKEN")
                .build();

        given(productService.findProductDetail(1L)).willReturn(response);

        mockMvc.perform(get("/api/products/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.description").value("Good shoes"))
                .andExpect(jsonPath("$.data.rating").value(4.8));
    }

    @Test
    @DisplayName("상품 검색 테스트")
    void searchProductsTest() throws Exception {
        ProductResponse response = ProductResponse.builder()
                .id(1L)
                .name("Air Jordan")
                .build();

        given(productService.findAllProducts(null, "Jordan", null)).willReturn(List.of(response));

        mockMvc.perform(get("/api/products/search")
                        .param("q", "Jordan"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("Air Jordan"));
    }
}
