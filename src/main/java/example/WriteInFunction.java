package example;

import org.neo4j.graphdb.Node;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserAggregationFunction;
import org.neo4j.procedure.UserAggregationResult;
import org.neo4j.procedure.UserAggregationUpdate;
import org.neo4j.procedure.UserFunction;

public class WriteInFunction {

    @UserFunction
    public String setProp(
            @Name("node") Node node,
            @Name("name") String name,
            @Name("value") String value) {
        node.setProperty(name, value);
        return value;
    }

    @UserAggregationFunction
    public WriteAggregation setPropAggregation() {
        return new WriteAggregation();
    }

    public static class WriteAggregation {

        private long count;

        @UserAggregationUpdate
        public void aggregate(@Name("node") Node node) {
            node.setProperty("key", "val");
            count++;
        }

        @UserAggregationResult
        public Long result() {
            return count;
        }
    }

}
