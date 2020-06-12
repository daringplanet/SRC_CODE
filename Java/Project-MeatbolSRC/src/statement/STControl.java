package statement;

import enums.SubClassif;
import enums.Classif;

public class STControl extends STEntry
{
    public SubClassif subClassif;

    public STControl(String s, Classif c, SubClassif subIf)
    {
        super(s, c);
        this.subClassif = subIf;
    }
}
