package AutoCHO.algorithm;
import AutoCHO.entity.DS_Candidate;
import java.util.*;

public class CandidateYieldOrderByDescending implements Comparator<DS_Candidate>{
    @Override
    public int compare(DS_Candidate c1, DS_Candidate c2) {
        return (c1.Yield > c2.Yield ? -1 : (c1.Yield == c2.Yield ? 0 : 1));
    }
}
