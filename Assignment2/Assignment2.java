/*
	Program name: Assignment 2, File Processing Program
	Course: CMSC 3320, Technical Computing Using Java
	Group: #3
	Members:
		Shawn Gallagher - GAL82896@pennwest.edu
		Lucas Giovannelli - GIO07221@pennwest.edu
		Joshua Watson - WAT93888@pennwest.edu
*/
import java.io.*;
import java.util.StringTokenizer;
class Assignment2
{
    public static void main(String[] args) throws IOException
    {
        PrintWriter out = null;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        String line;
        String outLine;  
    
        FileReader fn = null;
        boolean Found = false;
        
       //Command line parameter handling 
        if (args.length >= 1)
        {
            line = args[0];//Take first parameter as input file
        }
        else 
        {
            line = null; 
        }

       
        if (args.length >= 2)
        {
            outLine = args[1]; //Take second parameter as output file
        }
        else 
        {
            outLine = null;
        }

        while(!Found)
        {
            
            if(line == null)
            {
                System.out.print("Enter your input filename followed by the output filename\n Or press enter to terminate: ");
                line = br.readLine();
            }
            
            if(line != null && !line.trim().isEmpty())
            {
                try
                {
                    fn = new FileReader(line); //Gets file from cmd line
                    BufferedReader in = new BufferedReader(fn);
                    
                    // Only prompt for output filename if not provided via command line
                    if(outLine == null)
                    {
                        System.out.print("Enter the output filename: ");
                        outLine = br.readLine();
                    
                        if(outLine == null || outLine.trim().isEmpty())
                        {
                            outLine = "output.txt";  // Default output filename
                            System.out.println("No output filename given. Using default: " + outLine);
                        }
                    }
                    
                    // Check if output file exists
                    File outputFile = new File(outLine);
                    boolean outputReady = false;
                    
                    while(!outputReady)
                    {
                        if(outputFile.exists())
                        {
                            System.out.println("Output file already exists.");
                            System.out.print("Enter new filename, 'Replace' to backup and overwrite, or press Enter to quit: ");
                            String choice = br.readLine();
                            
                            if(choice == null || choice.trim().isEmpty())
                            {
    
                                System.out.println("Quitting without opening files.");
                                in.close();
                                fn.close();
                                Found = true;  // Exit main loop
                                outputReady = true;  // Exit output file loop
                            }
                            else if(choice.trim().equalsIgnoreCase("Replace"))
                            {
                                // Backup existing file
                                File backup = new File(outLine + ".bak");
                                if(backup.exists())
                                {
                                    backup.delete();  // Delete old backup
                                }
                                outputFile.renameTo(backup);
                                System.out.println("Existing file backed up as: " + outLine + ".bak");
                                outputReady = true;
                            }
                            else
                            {
                                // User entered a new filename
                                outLine = choice.trim();
                                outputFile = new File(outLine);
                                // Loop continues to check if this new file exists
                            }
                        }
                        else
                        {
                            // File doesn't exist
                            outputReady = true;
                        }
                    }
                    
                    
                    if(!Found)
                    {
                        out = new PrintWriter(new FileWriter(outLine)); //Creates output file
                        
                        String fileLine;
                        while((fileLine = in.readLine()) != null)
                        {
                            System.out.println("Contents of input file: " + fileLine);//Print contents of in file
                            out.println(fileLine);  //Write to out file
                        }
                        in.close();
                        fn.close();
                        out.close();  
                        Found = true;
                    }
                }
                catch(FileNotFoundException e)
                {
                    System.out.println("File not found. Please try again.");
                    //Reset to prompt again on next iteration
                    line = null;
                    outLine = null;
                }
                catch(IOException e)  
                {
                    System.out.println("Output printing unsuccessful");
                }
            }
            else
            {
                System.out.println("No filename entered. Exiting. ");
                Found = true;
            }
        }
        
        br.close();
    }
}