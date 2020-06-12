package statement;

import enums.SubClassif;
import enums.Classif;
import java.util.ArrayList;
import meatbol.SymbolTable;

public class STFunction extends STEntry
{

    public SubClassif type;
    public SubClassif returnType;
    public String definedBy;
    public int numArgs;
    public ArrayList parmList;
    public SymbolTable symbolTable;
    public int tkPrec = 16;
    public int stPrec = 2;

    public STFunction(String s, Classif c, SubClassif type, SubClassif returnType, int numArgs)
    {
        super(s, c);
        this.type = type;
        this.returnType = returnType;
        this.numArgs = numArgs;


    }
}
