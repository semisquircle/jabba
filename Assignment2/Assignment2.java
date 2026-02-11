/*
	Program name: Assignment 1, Average Program
	Course: CMSC 3320, Technical Computing Using Java
	Group: #3
	Members:
		Shawn Gallagher - GAL82896@pennwest.edu
		Lucas Giovannelli - GIO07221@pennwest.edu
		Joshua Watson - WAT93888@pennwest.edu
*/
import java.io.*;

class Assignment2
{
    public static void main(String[] args) throws IOException
    {
        
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line;
        FileReader fn = null;
        boolean Found = false;

        while(!Found)
        {
            System.out.print("Enter the filename: ");
            line = br.readLine();
            
            // If no filename is given (empty or null), terminate
            if(line != null)
            {
                try
                {
                    fn = new FileReader(line); //Gets file from cmd line
                    //Read given file 
                    BufferedReader in = new BufferedReader(fn);
                    String fileLine;
                    while((fileLine = in.readLine()) != null)
                    {
                        System.out.println(fileLine);
                    }
                    in.close();
                    fn.close();
                    Found = true; 
                }
                catch(FileNotFoundException e)
                {
                    System.out.println("File not found. Please try again.");
                    
                }
            }
            else
            {
                System.out.println("No filename entered. Exiting. ");
                Found = true; // Exit loop to terminate
            }
        }
        
        br.close();
    }
}