package uk.co.o2.ldifloader;

import java.io.IOException;

public class LDIFloaderTest {

    public static int totalRecordVerified = 0;
    public static int invalidRecord = 0;
    public static int recordsNotFound = 0;
    public static int recordsError = 0;
    public static int recordsNotmatch = 0;
    private final GrepCommand grep;
    private final LdifReader ldifreader;

    public String ldiffile;
    public boolean sample;
    public String inputfile;

    public LDIFloaderTest(String ldiffile, String mongoconnectionUrl, String logfile, boolean sample, String inputfile) throws IOException {
        this.ldiffile = ldiffile;
        grep = new GrepCommand();
        ldifreader = new LdifReader(new MongoReader(mongoconnectionUrl), grep,logfile);
        this.sample = sample;
        this.inputfile = inputfile;
    }

    public static void main(String[] args) throws IOException {
        ReadPropertyFile readprop = new ReadPropertyFile();
        LDIFloaderTest test = new LDIFloaderTest(readprop.getPropValues("ldiffile"), readprop.getPropValues("mongoconnectionUrl"), readprop.getPropValues("logfile"), Boolean.parseBoolean(readprop.getPropValues("sample")), readprop.getPropValues("inputfile"));
        test.check();
    }

    private void check() throws IOException {
        if (sample) {
            System.out.println("specific sample testing");
            ldifreader.readSpecificUID(inputfile, ldiffile);
        } else {
            String outputFile = grep.processldifFile(ldiffile);
            ldifreader.readUIDfromFile(outputFile, ldiffile);
        }
        System.out.println("------------------------- Test Report ---------------------------");
        System.out.println();
        System.out.println();
        System.out.println("Total number of records in the LDIF file: " + totalRecordVerified);
        System.out.println("Invalid Records in the LDIF file: " + invalidRecord);
        System.out.println("Records not imported due to some other reason than duplicate or non numeric uid: " + recordsError);
        System.out.println("Records not imported to mongodb: " + recordsNotFound);
        System.out.println("Records imported to mongodb with error: " + recordsNotmatch);
        System.out.println("Successfully imported records: " + (totalRecordVerified - (invalidRecord + recordsNotFound + recordsNotmatch + recordsError)));
    }

}
