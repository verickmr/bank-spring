package db.migration;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FlywayMigrationTest {

    @Test
    void deveCriarSchemaNovoComTodasAsMigracoes() throws Exception {
        String url = novaUrl();

        migrar(url, false);

        try (Connection connection = conectar(url);
             Statement statement = connection.createStatement();
             ResultSet columns = statement.executeQuery(
                     "SELECT IS_NULLABLE FROM INFORMATION_SCHEMA.COLUMNS "
                             + "WHERE TABLE_NAME = 'CORRENTISTA' "
                             + "AND COLUMN_NAME = 'SENHA'"
             )) {
            assertTrue(columns.next());
            assertEquals("NO", columns.getString("IS_NULLABLE"));
        }
    }

    @Test
    void devePreservarDadosAoMigrarSchemaLegadoSemSenha() throws Exception {
        String url = novaUrl();

        try (Connection connection = conectar(url);
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE correntista ("
                    + "id BIGINT AUTO_INCREMENT PRIMARY KEY, "
                    + "cpf VARCHAR(14) NOT NULL, "
                    + "nome VARCHAR(255), "
                    + "email VARCHAR(255))");
            statement.execute("INSERT INTO correntista (cpf, nome, email) VALUES ("
                    + "'12345678900', 'Victor Erick', 'victor@email.com')");
        }

        migrar(url, true);

        try (Connection connection = conectar(url);
             Statement statement = connection.createStatement();
             ResultSet row = statement.executeQuery(
                     "SELECT cpf, nome, senha FROM correntista"
             )) {
            assertTrue(row.next());
            assertEquals("12345678900", row.getString("cpf"));
            assertEquals("Victor Erick", row.getString("nome"));
            assertTrue(row.getString("senha").startsWith("$2"));
            assertFalse(BCrypt.checkpw("senha-conhecida", row.getString("senha")));
            assertFalse(row.next());
        }
    }

    @Test
    void deveManterHashBcryptExistente() throws Exception {
        String url = novaUrl();
        String hashExistente = BCrypt.hashpw(
                "senha-segura",
                BCrypt.gensalt(10)
        );

        try (Connection connection = conectar(url);
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE correntista ("
                    + "id BIGINT AUTO_INCREMENT PRIMARY KEY, "
                    + "cpf VARCHAR(14) NOT NULL, "
                    + "nome VARCHAR(255), "
                    + "email VARCHAR(255), "
                    + "senha VARCHAR(60))");
            statement.execute("INSERT INTO correntista "
                    + "(cpf, nome, email, senha) VALUES ("
                    + "'12345678900', 'Victor Erick', 'victor@email.com', '"
                    + hashExistente + "')");
        }

        migrar(url, true);

        try (Connection connection = conectar(url);
             Statement statement = connection.createStatement();
             ResultSet row = statement.executeQuery(
                     "SELECT senha FROM correntista"
             )) {
            assertTrue(row.next());
            assertEquals(hashExistente, row.getString("senha"));
        }
    }

    private void migrar(String url, boolean schemaLegado) {
        Flyway.configure()
                .dataSource(url, "sa", "")
                .locations("classpath:db/migration")
                .baselineOnMigrate(schemaLegado)
                .baselineVersion("1")
                .load()
                .migrate();
    }

    private Connection conectar(String url) throws Exception {
        return DriverManager.getConnection(url, "sa", "");
    }

    private String novaUrl() {
        return "jdbc:h2:mem:flyway_"
                + UUID.randomUUID().toString().replace("-", "")
                + ";MODE=MySQL;DB_CLOSE_DELAY=-1";
    }
}
