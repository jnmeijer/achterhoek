package net.xytra.achterhoek;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Pattern;

public class Main {
    private static Pattern DATE_RECORD_START_PATTERN = Pattern.compile("^\\d\\d[.]\\d\\d[.]\\d\\d\\d\\d .*" );
    private static Pattern YEAR_LINE_PATTERN = Pattern.compile("^\\d\\d\\d\\d$");

    public static void main(String[] args) {
        String inputFileName = null;
        String outputFileName = "output.txt";

        for (int i=0; i<args.length; i++) {
            if ("-o".equals(args[i])) {
                if (++i == args.length) {
                    System.err.println("Missing output file argument after -o");
                    System.exit(1);
                } else {
                    outputFileName = args[i];
                }
            } else if (args[i].startsWith("-")) {
                System.err.println("Invalid option: " + args[i]);
                System.exit(2);
            } else {
                inputFileName = args[i];
            }
        }

        if (inputFileName == null) {
            System.err.println("Specify an input file name");
            System.exit(3);
        }
        System.err.println("inputFileName" + inputFileName + "; outputFileName=" + outputFileName);

        processInputFile(new File(inputFileName), new File(outputFileName));
    }

    private static void processInputFile(File inputFile, File outputFile) {
        BufferedWriter outputWriter = null;
        BufferedReader inputReader = null;

        try {
            outputWriter = new BufferedWriter(new FileWriter(outputFile));
            inputReader = new BufferedReader(new FileReader(inputFile));

            processInput(inputReader, outputWriter);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputReader != null) {
                    inputReader.close();
                }
                if (outputWriter != null) {
                    outputWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void processInput(BufferedReader inputReader, BufferedWriter outputWriter) throws IOException {
        int pageNum = 1;
        int entryNum = 1;
        String parish = null;
        String docName = null;
        String sectionName = null;
        String existingLine = null;
        boolean indexReached = false;
        String newLine;
        while (!indexReached && (newLine = inputReader.readLine()) != null) {
            //outputWriter.write(newLine);
            System.err.println("newLine="+newLine);
            //if (pageNum==23&&entryNum==15) {
            //    throw new RuntimeException("Stop");
            //}
            if (newLine.startsWith("Nederduits Gereformeerde Gemeente")) {
                parish = newLine.substring(34);
                System.err.println("parish="+parish);

                // Possibly end of record; process
                if (existingLine != null) {
                    outputWriter.write(processRecord(parish, pageNum, entryNum, existingLine).toString());
                    // reset
                    existingLine = null;
                    entryNum++;
                }
            } else if (newLine.startsWith("Doopboek")) {
                docName = newLine;
                System.err.println("docName="+docName);
            } else if (newLine.startsWith("Tijdvak")) {
                sectionName = newLine;
                System.err.println("sectionname="+sectionName);
            } else if (newLine.startsWith("Index - ")) {
                indexReached = true;
            } else if (newLine.endsWith("www.genealogiedomein.nl")) {
                // Ignore safely
            } else if (newLine.startsWith("- ")) {
                String pageId = newLine.substring(2);
                pageId = pageId.substring(0, pageId.indexOf(" -"));
                pageNum = Integer.valueOf(pageId);
                System.err.println("pageNum="+pageNum);
                entryNum = 1; // restart at 1 on new page
            } else if (newLine.startsWith("Transcriptie")) {
                // Ignore safely
            } else if (YEAR_LINE_PATTERN.matcher(newLine).matches()) {
                System.err.println("year="+newLine);
            } else if (DATE_RECORD_START_PATTERN.matcher(newLine).matches()) {
                // Possibly end of record; process
                if (existingLine != null) {
                    outputWriter.write(processRecord(parish, pageNum, entryNum, existingLine).toString());
                    // reset
                    existingLine = null;
                    entryNum++;
                }

                // Start accumulating
                existingLine = newLine;
            } else {
                if (existingLine == null) {
                    throw new RuntimeException("Expected existingLine not null");
                }
                existingLine = existingLine + '\n' + newLine;
            }
        }
    }

    private static BaptismRecord processRecord(String parish, int page, int entry, String record) {
        switch (parish) {
            case "LOCHEM":
                return processRecordLochem(page, entry, record);
            default:
                throw new RuntimeException("Unexpected parish: " + parish);
        }
    }

    private static BaptismRecord processRecordLochem(int page, int entry, String record) {
        System.err.println("record="+record);
        EventDate baptismDate = EventDate.parseDate(record.substring(0, 10));
        System.err.println("baptismDate="+baptismDate);

        String rest = record.substring(11);
        String[] restParts = rest.split(", geb[.] ");

        // If baptism occurred elsewhere, catch it here
        String[] baptismLocParts = restParts[0].split(", gedoopt te ");
        String baptismLocation = null;
        if (baptismLocParts.length > 1) {
            baptismLocation = baptismLocParts[1];
            restParts[0] = baptismLocParts[0];
        }
        System.err.println("baptismLocation="+baptismLocation);

        // Catch any qualifiers for the child
        String[] qualifierParts = restParts[0].split(", ");
        String[] qualifiers = new String[qualifierParts.length-1];
        if (qualifierParts.length > 1) {
            for (int i=1; i<qualifierParts.length; i++) {
                qualifiers[i-1] = qualifierParts[i];
            }
            restParts[0] = qualifierParts[0];
        }

        // First names
        String firstNames = restParts[0];
        //System.err.println("firstNames="+firstNames);
        PersonIdentity childIdentity = PersonIdentity.parseFirstNames(firstNames);
        System.err.println("childIdentity="+childIdentity);

        rest = restParts[1];

        // Birth date
        //restParts = rest.split("\n");
        int firstLFIndex = rest.indexOf('\n');
        restParts[0] = rest.substring(0, firstLFIndex); // dob
        restParts[1] = rest.substring(firstLFIndex+1, rest.length()); // rest

        EventDate birthDate = baptismDate.parseMdBornDate(restParts[0]);
        System.err.println("birthDate="+birthDate);

        rest = restParts[1];

        // Before considering the rest as a single line, try to find the attestor on last line
        PersonIdentity attestor = null;

        String[] attestorParts = rest.split("\n");
        int alsGetuigeIndex = attestorParts[attestorParts.length-1].indexOf(" als getuige.");
        if (alsGetuigeIndex >= 0) {
            attestor = PersonIdentity.parseFullName(attestorParts[attestorParts.length-1].substring(0, alsGetuigeIndex));
            System.err.println("als attestor="+attestor);

            // Remove the last line of the record ahead of further processing
            rest = rest.substring(0, rest.lastIndexOf('\n'));
        }

        // Prep the rest by replacing LFs with spaces
        rest = rest.replace('\n', ' ');

        // Clean up by removing the trailing period, if present
        if (rest.charAt(rest.length()-1) == '.') {
            rest = rest.substring(0, rest.length()-1);
        }

        // First parent name
        restParts = rest.split(" - ");
        PersonIdentity parent1Identity = null;
        PersonIdentity parent2Identity = null;
        String location = null;

        if (restParts.length == 1) {
            //String[] parts = restParts[0].split(" ");
            // Remove "moeder" as first part of names, if present
            //if ("moeder".equals(parts[0])) {
            if (restParts[0].startsWith("moeder ")) {
                restParts[0] = restParts[0].substring(7, restParts[0].length());
            }

            // Split off attestor if found
            String[] parts = restParts[0].split(" getuige ");
            if (parts.length > 1) {
                attestor = PersonIdentity.parseFullName(parts[1]);
                System.err.println(attestor);
            }

            // Assume single name is mother
            parent2Identity = PersonIdentity.parseFullName(parts[0]);
            System.err.println("parent2Identity="+parent2Identity);
        } else {
            // parse 2 parents
            parent1Identity = PersonIdentity.parseFullName(restParts[0]);
            System.err.println("parent1Identity="+parent1Identity);

            rest = restParts[1];

            // There might be an "ehel" indication: remove it
            int ehelIndex = rest.indexOf(", ehel");
            if (ehelIndex >= 0) {
                rest = rest.substring(0, ehelIndex) + rest.substring(ehelIndex+6, rest.length());
            }

            System.err.println("rest="+rest);
            // First, catch a missing comma before the location
            int locationIndex = rest.indexOf(" op het ");
            if (locationIndex > 0 && rest.charAt(locationIndex-1) != ',') {
                restParts = new String[2];
                restParts[0] = rest.substring(0, locationIndex);
                restParts[1] = rest.substring(locationIndex+1, rest.length());
            } else if (rest.indexOf(", ") < 0) {
                restParts = new String[1];
                restParts[0] = rest;
            } else {
                locationIndex = rest.indexOf(", ");
                restParts = new String[2];
                restParts[0] = rest.substring(0, locationIndex);
                restParts[1] = rest.substring(locationIndex+2, rest.length());
            }

            // Location
            if (restParts.length > 1) {
                location = restParts[1];
                System.err.println("location="+location);
            }

            // Split off attestor if found
            restParts = restParts[0].split(" getuige ");
            if (restParts.length > 1) {
                // Only keep the name, before the comma, if there is a comma
                int commaIndex = restParts[1].indexOf(',');
                if (commaIndex > 0) {
                    restParts[1] = restParts[1].substring(0, restParts[1].indexOf(','));
                }

                attestor = PersonIdentity.parseFullName(restParts[1]);
                System.err.println("attestor="+attestor);
            }

            // Second parent name
            parent2Identity = PersonIdentity.parseFullName(restParts[0]);
            System.err.println("parent2Identity="+parent2Identity);
        }

        return new BaptismRecord("Lochem", page, entry, birthDate, baptismDate,
                baptismLocation, childIdentity, qualifiers,
                parent1Identity, parent2Identity, location, attestor);

//        throw new RuntimeException("stop here");
    }
}
