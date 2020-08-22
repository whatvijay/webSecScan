package realAutomata;

import java.util.ArrayList;

public class Example1 {

	public static void main(String args[])
	{
		String[] inputsAllowed = {"a","e"};
		String[] finalStates = {"ae","aee","aeae"};
		String[] transistionFunction = {"oneF","twoF","removeF"};
		String intialState ="a";
		String[] AllStates= {"a","ae","aea","aee","aeae"};
		
		String[] instInputMap= {"oneF:a","twoF:e","removeF:e","twoF:a","removeF:a","oneF:e"};
		// will instrInputMap fit into above machine.
		ArrayList<String> currentState= new ArrayList<>();
		boolean validInput= true;
		for(String ii :instInputMap)
		{
			
			String[] iiA=ii.split(":");
			for(String s:inputsAllowed)
			{
				if(s.equals(iiA[1]))
				{
					validInput=true;
				}
			}
			
			//boolean validInput;
			if(validInput)
			{
				//check if current state is null then 
				//check if the input is allowed initial input
				if(currentState.size()==0)
				{
					
						if(intialState.equals(iiA[1]))
						{
						  if(iiA[0].equals("oneF"))
						  {
							  currentState.add(iiA[1]);
						  }
						  if(iiA[0].equals("twoF"))
						  {
							  currentState.add(iiA[1]);
							  currentState.add(iiA[1]);
						  }
						  if(iiA[0].equals("removeF"))
						  {
							 return;
						  }
						}
					
				}
				else
				{
					if(iiA[0].equals("oneF"))
					  {
						  currentState.add(iiA[1]);
					  }
					if(iiA[0].equals("twoF"))
					  {
						currentState.add(iiA[1]);
						currentState.add(iiA[1]);
					  }
					if(iiA[0].equals("removeF"))
					  {
						//currentState.remove(iiA[1]);
						System.out.println(currentState.size()+currentState.get(currentState.size()-1));
						currentState.remove(currentState.size()-1);
						//currentState.remove(iiA[1]);
					  }
				}
			}
		}
		String currentString="";
		for(String s1:currentState) {
		System.out.println(""+s1);
		currentString+=s1;
		}
		
		for(String s3:finalStates)
		{
			//if(s3.equals(currentState.toString()))
			//{System.out.println("InputInstructionMap matched");}
			if(s3.equals(currentString))
			{System.out.println("InputInstructionMap matched2");}
		}
	}
}
