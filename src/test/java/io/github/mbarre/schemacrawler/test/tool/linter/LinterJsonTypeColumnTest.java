/**
 * 
 */
package io.github.mbarre.schemacrawler.test.tool.linter;

import io.github.mbarre.schemacrawler.test.utils.PostgreSqlDatabase;
import io.github.mbarre.schemacrawler.tool.linter.LinterTableNameNotInLowerCase;

import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.commons.io.output.StringBuilderWriter;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.tools.executable.Executable;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.lint.LinterRegistry;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.TextOutputFormat;


/**
 * Check that columns with JSON content use data type jsonb
 * @author mbarre
 */
public class LinterJsonTypeColumnTest {

	private static final String CHANGE_LOG_JSON_CHECK = "src/test/db/liquibase/jsonbCheck/db.changelog.xml";
	private Logger LOGGER = LoggerFactory.getLogger(LinterJsonTypeColumnTest.class);
	private static PostgreSqlDatabase database;

	@BeforeClass
	public static void  init(){
		database = new PostgreSqlDatabase();
		database.setUp(CHANGE_LOG_JSON_CHECK);
	}

	@Test
	public void testLint() throws Exception{

		final LinterRegistry registry = new LinterRegistry();
		Assert.assertTrue(registry.hasLinter(LinterTableNameNotInLowerCase.class.getName()));

		final SchemaCrawlerOptions options = new SchemaCrawlerOptions();
		// Set what details are required in the schema - this affects the
		// time taken to crawl the schema
		options.setSchemaInfoLevel(SchemaInfoLevelBuilder.standard());
		options.setTableNamePattern("test_json");
		
		Connection connection = DriverManager.getConnection(PostgreSqlDatabase.CONNECTION_STRING, 
				PostgreSqlDatabase.USER_NAME, database.getPostgresPassword());
		
		final Executable executable = new SchemaCrawlerExecutable("lint");
		try (StringBuilderWriter out = new StringBuilderWriter()) {
			OutputOptions outputOptions = new OutputOptions(TextOutputFormat.json,out);
			executable.setOutputOptions(outputOptions);
			executable.setSchemaCrawlerOptions(options);
			executable.execute(connection);

//			Assert.assertNotNull(out.toString());
//			JSONObject json = new JSONObject(out.toString().substring(1, out.toString().length()-1)) ;
//			Assert.assertNotNull(json.getJSONObject("table_lints"));
//			Assert.assertEquals("TEST_CASE", json.getJSONObject("table_lints").getString("name"));
//
//			JSONArray lints = json.getJSONObject("table_lints").getJSONArray("lints");
//
//			boolean lint1Dectected = false;
//			boolean lint2Dectected = false;
//			
//			for (int i=0; i < lints.length(); i++) {
//				if(LinterTableNameNotInLowerCase.class.getName().equals(lints.getJSONObject(i).getString("id"))){
//					if("TEST_CASE".equals(lints.getJSONObject(i).getString("value").trim())){
//						Assert.assertEquals("name should be in lower case", lints.getJSONObject(i).getString("description").trim());
//						Assert.assertEquals("high", lints.getJSONObject(i).getString("severity").trim());
//						lint1Dectected = true;
//					}
//					else if("UPPERCASE_COLUMN_NAME".equals(lints.getJSONObject(i).getString("value").trim())){
//						Assert.assertEquals("name should be in lower case", lints.getJSONObject(i).getString("description").trim());
//						Assert.assertEquals("high", lints.getJSONObject(i).getString("severity").trim());
//						lint2Dectected = true;
//					}
//					else{
//						Assert.fail("Not expected error detected :"+lints.getJSONObject(i).getString("value").trim());
//					}
//				}
//			}
//
//			Assert.assertTrue("Some expected errors have not been detected.", lint1Dectected && lint2Dectected);
		}

	}

}
