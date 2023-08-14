package osmgraph3.graph;

/**
 *
 * @author viljinsky
 */
public class Member {
    
    public String type;
    public String role;
    public Long ref;

    public String toString() {
        return String.format("member %s %s %s", type, ref, role);
    }
    
}
