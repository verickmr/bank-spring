package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class V2__add_password_to_correntista extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        Connection connection = context.getConnection();

        if (!possuiColunaSenha(connection)) {
            executar(
                    connection,
                    "ALTER TABLE correntista ADD COLUMN senha VARCHAR(60) NULL"
            );
        }

        bloquearCredenciaisLegadas(connection);
        tornarSenhaObrigatoria(connection);
    }

    private boolean possuiColunaSenha(Connection connection) throws Exception {
        DatabaseMetaData metadata = connection.getMetaData();

        try (ResultSet columns = metadata.getColumns(
                connection.getCatalog(), null, null, null)) {
            while (columns.next()) {
                if ("correntista".equalsIgnoreCase(columns.getString("TABLE_NAME"))
                        && "senha".equalsIgnoreCase(columns.getString("COLUMN_NAME"))) {
                    return true;
                }
            }
        }

        return false;
    }

    private void bloquearCredenciaisLegadas(Connection connection) throws Exception {
        List<Long> idsSemHashValido = new ArrayList<>();

        try (Statement statement = connection.createStatement();
             ResultSet rows = statement.executeQuery(
                     "SELECT id, senha FROM correntista"
             )) {
            while (rows.next()) {
                String senha = rows.getString("senha");
                if (!ehHashBcrypt(senha)) {
                    idsSemHashValido.add(rows.getLong("id"));
                }
            }
        }

        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE correntista SET senha = ? WHERE id = ?"
        )) {
            for (Long id : idsSemHashValido) {
                statement.setString(1, gerarHashInacessivel());
                statement.setLong(2, id);
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    private boolean ehHashBcrypt(String senha) {
        return senha != null
                && senha.matches("\\$2[aby]\\$\\d{2}\\$.{53}");
    }

    private void tornarSenhaObrigatoria(Connection connection) throws Exception {
        String database = connection.getMetaData().getDatabaseProductName();
        String sql = database.toLowerCase().contains("h2")
                ? "ALTER TABLE correntista ALTER COLUMN senha VARCHAR(60) NOT NULL"
                : "ALTER TABLE correntista MODIFY COLUMN senha VARCHAR(60) NOT NULL";
        executar(connection, sql);
    }

    private String gerarHashInacessivel() {
        byte[] randomBytes = new byte[32];
        new SecureRandom().nextBytes(randomBytes);
        String randomPassword = Base64.getEncoder().encodeToString(randomBytes);
        return BCrypt.hashpw(randomPassword, BCrypt.gensalt(12));
    }

    private void executar(Connection connection, String sql) throws Exception {
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }
}
