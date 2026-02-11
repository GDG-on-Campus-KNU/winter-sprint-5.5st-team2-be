package gdgoc.be.service;

import gdgoc.be.domain.Coupon;
import gdgoc.be.domain.OrderItem;
import gdgoc.be.dto.CalculationResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OrderCalculatorTest {

    @Test
    @DisplayName("쿠폰이 없는 경우 올바르게 계산되는지 확인")
    void calculateTotal_noCoupon_shouldCalculateCorrectly() {
        // Given
        OrderItem item1 = mock(OrderItem.class);
        when(item1.getOrderPrice()).thenReturn(BigDecimal.valueOf(10000));

        OrderItem item2 = mock(OrderItem.class);
        when(item2.getOrderPrice()).thenReturn(BigDecimal.valueOf(15000));

        List<OrderItem> items = Arrays.asList(item1, item2);

        // Define expected values
        BigDecimal expectedTotalAmount = BigDecimal.valueOf(25000);
        BigDecimal expectedDiscountAmount = BigDecimal.ZERO;
        BigDecimal expectedShippingFee = BigDecimal.valueOf(3000);
        BigDecimal expectedFinalAmount = BigDecimal.valueOf(28000);

        // When
        CalculationResult result = OrderCalculator.calculateTotal(items, null);

        // Print expected and actual values
        System.out.println("--- Test: " + "쿠폰이 없는 경우 올바르게 계산되는지 확인" + " ---");
        System.out.println("Expected Total Amount: " + expectedTotalAmount + ", Actual: " + result.getTotalAmount());
        System.out.println("Expected Discount Amount: " + expectedDiscountAmount + ", Actual: " + result.getDiscountAmount());
        System.out.println("Expected Shipping Fee: " + expectedShippingFee + ", Actual: " + result.getShippingFee());
        System.out.println("Expected Final Amount: " + expectedFinalAmount + ", Actual: " + result.getFinalAmount());
        System.out.println("--------------------------------------------------");


        // Then
        assertEquals(expectedTotalAmount, result.getTotalAmount(),
                () -> "Expected totalAmount " + expectedTotalAmount + " but got " + result.getTotalAmount());
        assertEquals(expectedDiscountAmount, result.getDiscountAmount(),
                () -> "Expected discountAmount " + expectedDiscountAmount + " but got " + result.getDiscountAmount());
        assertEquals(expectedShippingFee, result.getShippingFee(),
                () -> "Expected shippingFee " + expectedShippingFee + " but got " + result.getShippingFee());
        assertEquals(expectedFinalAmount, result.getFinalAmount(),
                () -> "Expected finalAmount " + expectedFinalAmount + " but got " + result.getFinalAmount());
    }

    @Test
    @DisplayName("정액 쿠폰이 적용된 경우 올바르게 계산되는지 확인")
    void calculateTotal_fixedCoupon_shouldCalculateCorrectly() {
        // Given
        OrderItem item1 = mock(OrderItem.class);
        when(item1.getOrderPrice()).thenReturn(BigDecimal.valueOf(10000));

        OrderItem item2 = mock(OrderItem.class);
        when(item2.getOrderPrice()).thenReturn(BigDecimal.valueOf(15000));

        List<OrderItem> items = Arrays.asList(item1, item2);

        Coupon fixedCoupon = mock(Coupon.class);
        when(fixedCoupon.getDiscountType()).thenReturn(Coupon.DiscountType.FIXED);
        when(fixedCoupon.getDiscountValue()).thenReturn(BigDecimal.valueOf(5000)); // 5,000원 할인
        when(fixedCoupon.getMinOrderAmount()).thenReturn(BigDecimal.valueOf(20000)); // 최소 주문 금액 20,000원

        // Define expected values
        BigDecimal expectedTotalAmount = BigDecimal.valueOf(25000);
        BigDecimal expectedDiscountAmount = BigDecimal.valueOf(5000);
        BigDecimal expectedShippingFee = BigDecimal.valueOf(3000);
        BigDecimal expectedFinalAmount = BigDecimal.valueOf(23000);

        // When
        CalculationResult result = OrderCalculator.calculateTotal(items, fixedCoupon);

        // Print expected and actual values
        System.out.println("--- Test: " + "정액 쿠폰이 적용된 경우 올바르게 계산되는지 확인" + " ---");
        System.out.println("Expected Total Amount: " + expectedTotalAmount + ", Actual: " + result.getTotalAmount());
        System.out.println("Expected Discount Amount: " + expectedDiscountAmount + ", Actual: " + result.getDiscountAmount());
        System.out.println("Expected Shipping Fee: " + expectedShippingFee + ", Actual: " + result.getShippingFee());
        System.out.println("Expected Final Amount: " + expectedFinalAmount + ", Actual: " + result.getFinalAmount());
        System.out.println("--------------------------------------------------");

        // Then
        assertEquals(expectedTotalAmount, result.getTotalAmount(),
                () -> "Expected totalAmount " + expectedTotalAmount + " but got " + result.getTotalAmount());
        assertEquals(expectedDiscountAmount, result.getDiscountAmount(),
                () -> "Expected discountAmount " + expectedDiscountAmount + " but got " + result.getDiscountAmount());
        assertEquals(expectedShippingFee, result.getShippingFee(),
                () -> "Expected shippingFee " + expectedShippingFee + " but got " + result.getShippingFee());
        assertEquals(expectedFinalAmount, result.getFinalAmount(),
                () -> "Expected finalAmount " + expectedFinalAmount + " but got " + result.getFinalAmount());
    }

    @Test
    @DisplayName("퍼센트 쿠폰이 적용된 경우 올바르게 계산되는지 확인")
    void calculateTotal_percentCoupon_shouldCalculateCorrectly() {
        // Given
        OrderItem item1 = mock(OrderItem.class);
        when(item1.getOrderPrice()).thenReturn(BigDecimal.valueOf(10000));

        OrderItem item2 = mock(OrderItem.class);
        when(item2.getOrderPrice()).thenReturn(BigDecimal.valueOf(15000));

        List<OrderItem> items = Arrays.asList(item1, item2);

        Coupon percentCoupon = mock(Coupon.class);
        when(percentCoupon.getDiscountType()).thenReturn(Coupon.DiscountType.PERCENT);
        when(percentCoupon.getDiscountValue()).thenReturn(BigDecimal.valueOf(20)); // 20% 할인
        when(percentCoupon.getMinOrderAmount()).thenReturn(BigDecimal.valueOf(20000)); // 최소 주문 금액 20,000원

        // Define expected values
        BigDecimal expectedTotalAmount = BigDecimal.valueOf(25000);
        BigDecimal expectedDiscountAmount = BigDecimal.valueOf(5000);
        BigDecimal expectedShippingFee = BigDecimal.valueOf(3000);
        BigDecimal expectedFinalAmount = BigDecimal.valueOf(23000);

        // When
        CalculationResult result = OrderCalculator.calculateTotal(items, percentCoupon);

        // Print expected and actual values
        System.out.println("--- Test: " + "퍼센트 쿠폰이 적용된 경우 올바르게 계산되는지 확인" + " ---");
        System.out.println("Expected Total Amount: " + expectedTotalAmount + ", Actual: " + result.getTotalAmount());
        System.out.println("Expected Discount Amount: " + expectedDiscountAmount + ", Actual: " + result.getDiscountAmount());
        System.out.println("Expected Shipping Fee: " + expectedShippingFee + ", Actual: " + result.getShippingFee());
        System.out.println("Expected Final Amount: " + expectedFinalAmount + ", Actual: " + result.getFinalAmount());
        System.out.println("--------------------------------------------------");

        // Then
        assertEquals(expectedTotalAmount, result.getTotalAmount(),
                () -> "Expected totalAmount " + expectedTotalAmount + " but got " + result.getTotalAmount());
        assertEquals(expectedDiscountAmount, result.getDiscountAmount(),
                () -> "Expected discountAmount " + expectedDiscountAmount + " but got " + result.getDiscountAmount());
        assertEquals(expectedShippingFee, result.getShippingFee(),
                () -> "Expected shippingFee " + expectedShippingFee + " but got " + result.getShippingFee());
        assertEquals(expectedFinalAmount, result.getFinalAmount(),
                () -> "Expected finalAmount " + expectedFinalAmount + " but got " + result.getFinalAmount());
    }

    @Test
    @DisplayName("무료 배송 임계값을 초과하는 경우 배송비가 0으로 계산되는지 확인")
    void calculateTotal_freeShippingThresholdExceeded_shouldHaveZeroShippingFee() {
        // Given
        OrderItem item1 = mock(OrderItem.class);
        when(item1.getOrderPrice()).thenReturn(BigDecimal.valueOf(15000));

        OrderItem item2 = mock(OrderItem.class);
        when(item2.getOrderPrice()).thenReturn(BigDecimal.valueOf(15000));

        OrderItem item3 = mock(OrderItem.class);
        when(item3.getOrderPrice()).thenReturn(BigDecimal.valueOf(5000));

        List<OrderItem> items = Arrays.asList(item1, item2, item3); // Total 35,000

        // Define expected values
        BigDecimal expectedTotalAmount = BigDecimal.valueOf(35000);
        BigDecimal expectedDiscountAmount = BigDecimal.ZERO;
        BigDecimal expectedShippingFee = BigDecimal.ZERO;
        BigDecimal expectedFinalAmount = BigDecimal.valueOf(35000);

        // When
        CalculationResult result = OrderCalculator.calculateTotal(items, null); // No coupon

        // Print expected and actual values
        System.out.println("--- Test: " + "무료 배송 임계값을 초과하는 경우 배송비가 0으로 계산되는지 확인" + " ---");
        System.out.println("Expected Total Amount: " + expectedTotalAmount + ", Actual: " + result.getTotalAmount());
        System.out.println("Expected Discount Amount: " + expectedDiscountAmount + ", Actual: " + result.getDiscountAmount());
        System.out.println("Expected Shipping Fee: " + expectedShippingFee + ", Actual: " + result.getShippingFee());
        System.out.println("Expected Final Amount: " + expectedFinalAmount + ", Actual: " + result.getFinalAmount());
        System.out.println("--------------------------------------------------");

        // Then
        assertEquals(expectedTotalAmount, result.getTotalAmount(),
                () -> "Expected totalAmount " + expectedTotalAmount + " but got " + result.getTotalAmount());
        assertEquals(expectedDiscountAmount, result.getDiscountAmount(),
                () -> "Expected discountAmount " + expectedDiscountAmount + " but got " + result.getDiscountAmount());
        assertEquals(expectedShippingFee, result.getShippingFee(),
                () -> "Expected shippingFee " + expectedShippingFee + " but got " + result.getShippingFee());
        assertEquals(expectedFinalAmount, result.getFinalAmount(),
                () -> "Expected finalAmount " + expectedFinalAmount + " but got " + result.getFinalAmount());
    }

    @Test
    @DisplayName("쿠폰 최소 주문 금액 미달 시 할인이 적용되지 않는지 확인")
    void calculateTotal_minOrderAmountNotMet_shouldNotApplyDiscount() {
        // Given
        OrderItem item1 = mock(OrderItem.class);
        when(item1.getOrderPrice()).thenReturn(BigDecimal.valueOf(10000));

        OrderItem item2 = mock(OrderItem.class);
        when(item2.getOrderPrice()).thenReturn(BigDecimal.valueOf(5000));

        List<OrderItem> items = Arrays.asList(item1, item2); // Total 15,000

        Coupon fixedCoupon = mock(Coupon.class);
        when(fixedCoupon.getDiscountType()).thenReturn(Coupon.DiscountType.FIXED);
        when(fixedCoupon.getDiscountValue()).thenReturn(BigDecimal.valueOf(5000)); // 5,000원 할인
        when(fixedCoupon.getMinOrderAmount()).thenReturn(BigDecimal.valueOf(20000)); // 최소 주문 금액 20,000원

        // Define expected values
        BigDecimal expectedTotalAmount = BigDecimal.valueOf(15000);
        BigDecimal expectedDiscountAmount = BigDecimal.ZERO; // 할인 적용 안 됨
        BigDecimal expectedShippingFee = BigDecimal.valueOf(3000); // 총액 15,000 < 30,000 이므로 배송비 3,000원
        BigDecimal expectedFinalAmount = BigDecimal.valueOf(18000); // 15,000 - 0 + 3,000

        // When
        CalculationResult result = OrderCalculator.calculateTotal(items, fixedCoupon);

        // Then
        assertEquals(expectedTotalAmount, result.getTotalAmount(),
                () -> "Expected totalAmount " + expectedTotalAmount + " but got " + result.getTotalAmount());
        assertEquals(expectedDiscountAmount, result.getDiscountAmount(),
                () -> "Expected discountAmount " + expectedDiscountAmount + " but got " + result.getDiscountAmount());
        assertEquals(expectedShippingFee, result.getShippingFee(),
                () -> "Expected shippingFee " + expectedShippingFee + " but got " + result.getShippingFee());
        assertEquals(expectedFinalAmount, result.getFinalAmount(),
                () -> "Expected finalAmount " + expectedFinalAmount + " but got " + result.getFinalAmount());
    }
}