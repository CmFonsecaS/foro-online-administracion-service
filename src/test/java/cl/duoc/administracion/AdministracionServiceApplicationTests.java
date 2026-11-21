package cl.duoc.administracion;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.boot.test.context.SpringBootTest;

// Prueba de integracion con Oracle: solo se ejecuta si existe DB_URL (en CI se omite)
@SpringBootTest
@EnabledIfEnvironmentVariable(named = "DB_URL", matches = ".+")
class AdministracionServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
