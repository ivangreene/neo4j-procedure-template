package example;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.SoftAssertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.driver.Config;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.neo4j.driver.Value;
import org.neo4j.driver.internal.value.NullValue;
import org.neo4j.harness.Neo4j;
import org.neo4j.harness.Neo4jBuilders;

public class WriteInFunctionTest {

    private static final Config driverConfig = Config.builder().withoutEncryption().build();
    private Neo4j embeddedDatabaseServer;
    private SoftAssertions softly;

    @Before
    public void initializeNeo4j() {
        this.embeddedDatabaseServer = Neo4jBuilders
                .newInProcessBuilder()
                .withFunction(WriteInFunction.class)
                .withAggregationFunction(WriteInFunction.class)
                .build();
        this.softly = new SoftAssertions();
    }

    @After
    public void tearDown() {
        softly.assertAll();
    }

    @Test
    public void shouldNotWriteInFunction() {
        try (Driver driver = GraphDatabase.driver(embeddedDatabaseServer.boltURI(), driverConfig);
             Session session = driver.session()) {
            session.run("CREATE (f:Foo)");

            Exception exception = null;
            try {
                session.run("MATCH (f:Foo) RETURN example.setProp(f, 'key', 'val')").consume();
            } catch (Exception e) {
                exception = e;
            }

            softly.assertThat(exception).isNotNull();

            Value result = session.run("MATCH (f:Foo) RETURN f").single().get("f").asNode().get("key");

            softly.assertThat(result).isEqualTo(NullValue.NULL);
        }
    }

    @Test
    public void shouldNotWriteInAggregationFunction() {
        try (Driver driver = GraphDatabase.driver(embeddedDatabaseServer.boltURI(), driverConfig);
             Session session = driver.session()) {
            session.run("CREATE (f:Foo)");

            Exception exception = null;
            try {
                session.run("MATCH (f:Foo) RETURN example.setPropAggregation(f)").consume();
            } catch (Exception e) {
                exception = e;
            }

            softly.assertThat(exception).isNotNull();

            Value result = session.run("MATCH (f:Foo) RETURN f").single().get("f").asNode().get("key");

            softly.assertThat(result).isEqualTo(NullValue.NULL);
        }
    }
}
