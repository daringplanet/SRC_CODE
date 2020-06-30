/*
  This is a simple driver for Meatbol
  Command Arguments:
      java Meatbol arg1
             arg1 is the meatbol source file name.
 */
package meatbol;

public class Meatbol
{
    public static void main(String[] args)
    {
        // Create the SymbolTable
        SymbolTable symbolTable = new SymbolTable();

        try
        {
            // Print a column heading
            //System.out.printf("%-11s %-12s %s\n", "primClassif", "subClassif", "tokenStr");

            Scanner scan = new Scanner(args[0], symbolTable);
            Parser parse = new Parser(scan);
            scan.getToken();
            while(!scan.currentToken.isEmpty())
            {
                //System.out.println("MAIN");
                parse.exec(true);
                //scan.getToken();
            }



        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return;

    }

}
