package example;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.neo4j.driver.Config;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.neo4j.harness.Neo4j;
import org.neo4j.harness.Neo4jBuilders;

public class JoinTest {

    private static final Config driverConfig = Config.builder().withoutEncryption().build();
    private Neo4j embeddedDatabaseServer;

    @Before
    public void initializeNeo4j() {
        this.embeddedDatabaseServer = Neo4jBuilders
                .newInProcessBuilder()
                .withFunction(Join.class)
                .build();
    }

    @Test
    public void shouldAllowIndexingAndFindingANode() {

        // This is in a try-block, to make sure we close the driver after the test
        try (Driver driver = GraphDatabase.driver(embeddedDatabaseServer.boltURI(), driverConfig);
             Session session = driver.session()) {

            // When
            String result = session.run("RETURN example.join(['Hello', 'World']) AS result").single().get("result").asString();

            // Then
            assertThat(result).isEqualTo(("Hello,World"));
        }
    }
}
