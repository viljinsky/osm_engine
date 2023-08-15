package osmgraph3.graph;

/**
 *
 * @author viljinsky
 */
public class Member {
    
    public String type;
    public String role;
    public Long ref;

    public Member(Long ref,String type) {
        this(ref,type,null);
    }
    
    public Member(Long ref,String type, String role) {
        this.type = type;
        this.role = role;
        this.ref = ref;
    }

    public String toString() {
        return String.format("member %s %s %s", type, ref, role);
    }
    
}
