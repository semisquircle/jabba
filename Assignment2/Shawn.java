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

if (!quitProgram)
{
	try
	{
		while ((line = inputFile.readLine()) != null)
		{
			StringTokenizer tokenizer = new StringTokenizer(line, " \t");

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
								index++;
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
		outputFile.println("WORD COUNT REPORT");
		outputFile.println("----------------------------");

		for (int i = 0; i < uniqueWordCount; i++)
		{
			outputFile.printf("%-20s %d%n", words[i].word, words[i].count);
		}

		outputFile.println();
		outputFile.println("Total Unique Words: " + uniqueWordCount);
		outputFile.println("Sum of All Integers: " + integerSum);

		System.out.println("Processing complete. Output written to " + outputFileName);
	}
	catch (IOException e)
	{
		System.out.println("File processing error: " + e.getMessage());
	}

	// Close files safely
	try
	{
		if (inputFile != null) inputFile.close();
		if (outputFile != null) outputFile.close();
	}
	catch (IOException e)
	{
		System.out.println("Error closing files.");
	}
}
