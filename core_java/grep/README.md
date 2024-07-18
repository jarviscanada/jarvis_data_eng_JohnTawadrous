# Introduction
The Grep app is a Java-based application that searches for a text pattern 
recursively within a given directory and outputs the matched lines to a file.
It leverages core Java concepts, the SLF4J logging framework, and Java streams for efficient processing. 
The app has been dockerized for easier distribution and deployment.

# Quick Start
To use the Grep app:

1. **Clone the repository**:
   ```bash
   git clone https://github.com/jarviscanada/jarvis_data_eng_JohnTawadrous.git
   cd grep-app
   ```

2. **Build the app**:
   ```bash
   mvn clean package
   ```

3. **Run the app**:
   ```bash
   java -jar target/grep-1.0-SNAPSHOT.jar ".*Romeo.*Juliet.*" /path/to/data /path/to/output.txt
   ```

4. **Run the app using docker**:
   ```bash
   docker run --rm -v "/path/to/data:/data" -v "/path/to/log:/log" johntawadrous/grep ".*Romeo.*Juliet.*" /data /log/grep.out

   ```


# Implemenation
## Pseudocode
process method pseudocode:
1. Initialize an empty list to store matched lines.
2. Traverse all files in the given directory recursively.
3. For each file, read all lines.
4. For each line, check if it contains the regex pattern.
5. If a line matches, add it to the list of matched lines.
6. Write all matched lines to the output file.

## Performance Issue
The current implementation reads the entire file content into memory,
which can cause memory issues with large files. To fix this, implement 
streaming file processing, reading and processing each line individually to reduce memory usage.

# Test
To test the application manually:

1. Prepare sample data files with known content.
2. Run the app with different regex patterns.
3. Verify the output files to ensure they contain the correct matched lines.

# Deployment
The app is dockerized to simplify distribution and deployment.
The Dockerfile copies the application JAR into the image and 
sets the entry point to run the JAR file. Users can pull the Docker image 
and run the app without worrying about the Java environment setup.

# Improvement
1. Implement streaming file processing to handle large files efficiently.
2. Add unit tests to improve code coverage and reliability.
3. Enhance error handling to provide more informative messages and robust recovery from failures.