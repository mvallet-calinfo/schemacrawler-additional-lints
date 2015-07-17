/**
 * 
 */
package io.github.mbarre.schemacrawler.test.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import liquibase.Liquibase;
import liquibase.database.core.PostgresDatabase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.FileSystemResourceAccessor;
import liquibase.resource.ResourceAccessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Classe utilitaire pour l'initialisation de la base de test
 * 
 * @author mbarre
 */
public class PostgreSqlDatabase {
    public static final String CHANGE_LOG_LOWERCASE_CHECK = "src/test/db/liquibase/lowerCaseCheck/db.changelog.xml";

    private static final String CONNECTION_STRING = "jdbc:postgresql://localhost:5432/sc_lint_test";
    private static final String USER_NAME = "postgres";
    private static final String PASSWORD = "";
    private static final Logger LOG = LoggerFactory.getLogger(PostgreSqlDatabase.class);

    private Liquibase liquibase;

    public void setUp(String changelog) {
	try {

	    createTables(changelog);

	} catch (Exception ex) {
	    LOG.error("Error during database initialization", ex);
	    throw new RuntimeException("Error during database initialization", ex);
	}
    }

    private Connection getConnectionImpl(String user, String password) throws SQLException {
	return DriverManager.getConnection(CONNECTION_STRING, user, password);
    }

    // Create tables...
    private void createTables(String changelog) {

	Connection holdingConnection;

	try {
	    ResourceAccessor resourceAccessor = new FileSystemResourceAccessor();

	    holdingConnection = getConnectionImpl(USER_NAME, PASSWORD);
	    JdbcConnection conn = new JdbcConnection(holdingConnection);

	    PostgresDatabase database = new PostgresDatabase();
	    database.setDefaultSchemaName("public");
	    database.setConnection(conn);

	    liquibase = new Liquibase(changelog, resourceAccessor, database);
	    liquibase.dropAll();
	    liquibase.update("test");

	    conn.close();

	} catch (Exception ex) {
	    LOG.error("Error during createTable step", ex);
	    throw new RuntimeException("Error during createTable step", ex);
	}

    }

}
