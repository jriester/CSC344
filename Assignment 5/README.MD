Write a Python program that collects, summarizes, and e-mails all the programming assignments for this course. Specifically, 
assuming the programs are in subdirectories a1, a2, …, a5 of directory csc344, your program should:

*create a summary_ai.html file for each ai, which contains (in reasonably formatted, valid HTML):
the name of each source file (linked to the file itself), along with the number of lines long the file is
*a list of all identifiers used in the program (class, function, rule, variable etc. names), omitting duplicates. You do NOT need to 
specially filter keywords or builtin functions;
*create a valid HTML web page in the csc344 directory called index.html with links to each of the summary files;
*create a zip file containing all assignment sources, but excluding non-sources (executables, .class files, etc). Also included should 
be the html files created above. Be sure the links in the webpages work after extracted;
*prompt the user for an email address and send the zip file.
