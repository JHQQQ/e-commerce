package com.mitsuha754.ecommerce;

import com.mitsuha754.ecommerce.controller.AuthController;
import com.mitsuha754.ecommerce.dto.RegisterDTO;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ECommerceServerApplicationTest {

    @Mock
    private AuthController authController;

    @Mock
    private RegisterDTO registerDTO;

    @Test
    void contextLoads() {
        registerDTO.setPassword("123456");
        authController.register(registerDTO);
    }

}

