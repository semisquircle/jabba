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

class Average
{
    public static void main(String[] args) throws IOException
    {
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        String line;

        // Initializes variables
        double val = 0;
        double sum = 0;
        double avg = 0;
        int count = 0;

        System.out.println();
        System.out.println("––––––––––––––––––––––––––––––––––––––––––––––––––––");
        System.out.println("GRADE AVERAGER");
        System.out.println("––––––––––––––––––––––––––––––––––––––––––––––––––––");

        // Prompts user for real number input between 0 and 100
        while (val >= 0 && val <= 100)
        {
            System.out.print("Enter a grade (0-100): ");
            System.out.flush();
            line = stdin.readLine();

            boolean valid = true;

            // Tries to turn input into double
            try
            {
                val = Double.valueOf(line).doubleValue();
            }
            // Catches bad inputs
            catch (NumberFormatException e)
            {
                System.out.println("Invalid data entered. Please enter a real number.");
                valid = false;
            }

            // Checks input, adds to sum, counts # of inputs
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

        // Calculates and prints average, sum, and input count
		if (count > 0)
		{
			avg = sum / count;
            System.out.println("––––––––––––––––––––––––––––––––––––––––––––––––––––");
			System.out.println("Average: " + avg);
			System.out.println("Sum: " + sum);
			System.out.println("Number of grades entered: " + count);
            System.out.println("––––––––––––––––––––––––––––––––––––––––––––––––––––");
		}
        else
        {
            System.out.println("Must have at least one valid grade. Exiting program.");
            System.out.println("––––––––––––––––––––––––––––––––––––––––––––––––––––");
        }
        System.out.println();
    }
}
