package osmgraph3.graph;

import java.util.HashMap;
import java.util.StringJoiner;

/**
 *
 * @author viljinsky
 */
public class Tags extends HashMap<String, Object> {
    
    @Override
    public String toString() {
        StringJoiner sj = new StringJoiner(",\n", "\n", "\n");
        for (String key : keySet()) {
            String str = "{'tag' : 'k' : " + key + " 'v' : '" + get(key).toString() + "'}";
            sj.add(str);
        }
        return sj.toString();
    }
    
}
