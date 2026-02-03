/*
	Program name: Assignment 1, Averaging calculator
	Course: CMSC 3320, Technical Computing Using Java
	Group: #3
	Members:
		Shawn Gallagher - GAL82896@pennwest.edu
		Lucas Giovannelli - GIO07221@pennwest.edu
		Joshua Watson - WAT93888@pennwest.edu
		
	
*/
import java.io.*;
/* creates a class names Average */
class Average
{
    public static void main(String[] args) throws IOException
    {
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        String line;
/* initializes variables */
        double val = 0;
        double sum = 0;
        double avg = 0;
        int count = 0;
/* prompts user for real number input between 0 and 100 */
        while (val >=0 && val <=100 ||count == 0)
        {
            System.out.print("Enter a real number: ");
            System.out.flush();
            line = stdin.readLine();

            boolean valid = true;
/* tries to turn input into double */ 
            try
            {
                val = Double.valueOf(line).doubleValue();
            }
/* catches bad inputs */
            catch (NumberFormatException e)
            {
                System.out.println("Invalid data entered. Please enter a real number.");
                valid = false;
            }
/* checks input, adds to sum, counts # of inputs*/ 
            if (valid)
            {
                if (!Double.isNaN(val))
                {
                    if (val >= 0 && val <= 100)
                    {
                        sum += val;
                        count++;

                        
                    }
                }
                else
                {
                    System.out.println("NaN entered. Please enter a valid number.");
                }
            }
        }
/* finds and prints average, sum, and input count*/
		if (count > 0)
		{
			avg = sum / count;
			System.out.println("Average: " + avg);
			System.out.println("Sum: " + sum);
			System.out.println("Number of grades entered: " + count);
		}
    }
}
