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

class FileCopy
{
    // Word class to store word and count
    static class Word
    {
        String word;
        int count;

        Word(String word)
        {
            this.word = word;
            this.count = 1;
        }
    }

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
                System.out.print("Enter your input filename or press enter to terminate: ");
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
                        
                        // Initialize word array and counters
                        final int MAX_WORDS = 100;
                        Word[] words = new Word[MAX_WORDS];
                        int uniqueWordCount = 0;
                        long integerSum = 0;
                        
                        String fileLine;
                        
                        // Process each line from input file
                        while((fileLine = in.readLine()) != null)
                        {
                            StringTokenizer tokenizer = new StringTokenizer(fileLine, " \t");

                            while (tokenizer.hasMoreTokens())
                            {
                                String token = tokenizer.nextToken();
                                int index = 0;

                                while (index < token.length())
                                {
                                    char ch = token.charAt(index);

                                    // Process number
                                    if (
                                        Character.isDigit(ch) ||
                                        (ch == '-' && index + 1 < token.length() &&
                                        Character.isDigit(token.charAt(index + 1)))
                                    ) {
                                        int start = index;
                                        index++; 

                                        while (index < token.length() && Character.isDigit(token.charAt(index)))
                                        {
                                            index++;
                                        }

                                        String numberStr = token.substring(start, index);

                                        try
                                        {
                                            integerSum += Long.parseLong(numberStr);
                                        }
                                        catch (NumberFormatException e)
                                        {
                                            System.out.println("Invalid number format: " + numberStr);
                                        }
                                    }

                                    // Process word
                                    else if (Character.isLetter(ch))
                                    {
                                        int start = index;
                                        int apostropheCount = 0;
                                        index++;

                                        while (index < token.length())
                                        {
                                            char c = token.charAt(index);

                                            if (Character.isLetterOrDigit(c) || c == '-')
                                            {
                                                index++;
                                            }
                                            else if (c == '\'' && apostropheCount == 0)
                                            {
                                                apostropheCount++;
                                                index++;
                                            }
                                            else {
                                                break;
                                            }
                                        }

                                        String wordStr = token.substring(start, index).toLowerCase();

                                        // Add or update word in array
                                        boolean found = false;
                                        int i = 0;

                                        while (i < uniqueWordCount && !found)
                                        {
                                            if (words[i].word.equals(wordStr))
                                            {
                                                words[i].count++;
                                                found = true;
                                            }
                                            i++;
                                        }

                                        if (!found && uniqueWordCount < MAX_WORDS)
                                        {
                                            words[uniqueWordCount] = new Word(wordStr);
                                            uniqueWordCount++;
                                        }
                                    }

                                    else
                                    {
                                        index++;
                                    }
                                }
                            }
                        }

                        // Write output file
                        out.println("WORD COUNT REPORT");
                        out.println("----------------------------");

                        for (int i = 0; i < uniqueWordCount; i++)
                        {
                            out.printf("%-20s %d%n", words[i].word, words[i].count);
                        }

                        out.println();
                        out.println("Total Unique Words: " + uniqueWordCount);
                        out.println("Sum of All Integers: " + integerSum);

                        System.out.println("Processing complete. Output written to " + outLine);
                        
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