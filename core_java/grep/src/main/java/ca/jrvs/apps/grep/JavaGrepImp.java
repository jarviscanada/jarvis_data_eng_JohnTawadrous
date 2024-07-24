package ca.jrvs.apps.grep;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;

public class JavaGrepImp implements JavaGrep{

    private static final Logger logger = LoggerFactory.getLogger(JavaGrepImp.class);

    private String regex;
    private String rootPath;
    private String outFile;

    public static void main(String[] args) {
        if (args.length != 3) {
            throw new IllegalArgumentException("USAGE: regex rootPath outFile");
        }

        JavaGrepImp javaGrep = new JavaGrepImp();
        javaGrep.setRegex(args[0]);
        javaGrep.setRootPath(args[1]);
        javaGrep.setOutFile(args[2]);

        try {
            javaGrep.process();
        } catch (IOException e) {
            logger.error("Failed to process", e);
        }
    }

    @Override
    public void process() throws IOException {
        List<File> files = listFiles(rootPath);
        List<String> matchedLines = new ArrayList<>();

        for (File file : files) {
            List<String> lines = readLines(file);
            for (String line : lines) {
                if (containsPattern(line)) {
                    matchedLines.add(line);
                }
            }
        }

        writeToFile(matchedLines);
    }

    @Override
    public List<File> listFiles(String rootDir) {
        try {
            return Files.walk(Paths.get(rootDir))
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            logger.error("Failed to list files", e);
            return new ArrayList<>();
        }
    }

//    @Override
//    public List<File> listFiles(String rootDir) {
//        List<File> fileList = new ArrayList<>();
//        File root = new File(rootDir);
//
//        if (root.isDirectory()) {
//            File[] files = root.listFiles();
//            if (files != null) {
//                for (File file : files) {
//                    if (file.isFile()) {
//                        fileList.add(file);
//                    } else if (file.isDirectory()) {
//                        fileList.addAll(listFiles(file.getAbsolutePath()));
//                    }
//                }
//            }
//        } else {
//            logger.error("Root path is not a directory");
//        }
//
//        return fileList;
//    }

    @Override
    public List<String> readLines(File inputFile) {
        if (!inputFile.isFile()) {
            throw new IllegalArgumentException("Input should be a file");
        }

        try {
            return Files.readAllLines(inputFile.toPath());
        } catch (IOException e) {
            logger.error("Failed to read lines from file", e);
            return new ArrayList<>();
        }
    }

    @Override
    public boolean containsPattern(String line) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(line);
        return matcher.find();
    }

    @Override
    public void writeToFile(List<String> lines) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outFile))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }

    @Override
    public String getRootPath() {
        return rootPath;
    }

    @Override
    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    @Override
    public String getRegex() {
        return regex;
    }

    @Override
    public void setRegex(String regex) {
        this.regex = regex;
    }

    @Override
    public String getOutFile() {
        return outFile;
    }

    @Override
    public void setOutFile(String outFile) {
        this.outFile = outFile;
    }
}
