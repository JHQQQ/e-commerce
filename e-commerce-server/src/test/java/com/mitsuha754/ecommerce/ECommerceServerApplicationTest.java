package com.mitsuha754.ecommerce;

import com.mitsuha754.ecommerce.mapper.OrderMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ECommerceServerApplicationTest {
    @Mock
    OrderMapper orderMapper;

    @Test
    void contextLoads() {
    }
}
