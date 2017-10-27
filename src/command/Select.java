package command;
public class Select extends Command
{
	
	public static Boolean isme(String cmd)  {
		return cmd == "select";
	}
	
	public static String tome()  {
		return "select";
	}
	
}
