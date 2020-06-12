package statement;

import enums.ClassStruct;
import enums.SubClassif;
import enums.Classif;

public class STIdentifier extends STEntry
{
    public SubClassif dclType;
    public ClassStruct structure;
    public int parm;
    public int nonLocal;
    public int stPrec;
    public int tkPrec;

    public STIdentifier(String symbol, Classif primClass, SubClassif dcl)
    {
        super(symbol, primClass);
        this.dclType = dcl;

    }
    
    public STIdentifier(String symbol, Classif primClass, SubClassif dcl, int stPrec, int tkPrec)
    {
        super(symbol, primClass);
        this.dclType = dcl;
        this.stPrec = stPrec;
        this.tkPrec = tkPrec;

    }
     public STIdentifier(String symbol, ClassStruct struct, Classif primClass, SubClassif dcl, int stPrec, int tkPrec)
    {
        super(symbol, primClass);
        this.dclType = dcl;
        this.stPrec = stPrec;
        this.tkPrec = tkPrec;
        this.structure = struct;

    }


}
