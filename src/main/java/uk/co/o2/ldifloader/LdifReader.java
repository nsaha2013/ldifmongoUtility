package uk.co.o2.ldifloader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by ee on 4/30/15.
 */
public class LdifReader {
    protected MongoReader mongoReader;

    public LdifReader(MongoReader mongoReader) {
        this.mongoReader = mongoReader;
    }

    protected void readUIDfromFile(String fileName) throws IOException {

        String line;
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        try {

            while ((line= br.readLine()) != null) {
                String uid = line.substring(line.indexOf("uid=")+4,line.indexOf(",cn"));
                LDIFloaderTest.totalRecordVerified++;
                //System.out.println(uid);
                //System.out.println(isNumeric(uid));
                if(!isNumeric(uid)){
                    LDIFloaderTest.invalidRecord++;
                }else{
                    //System.out.println(uid);
                    if(!mongoReader.searchUIDinMongo(uid)){
                       LDIFloaderTest.recordsNotFound++;
                    }

                }
            }

        } finally {
            br.close();
        }
    }

    public boolean isNumeric(String str)
    {
        return str.matches("\\d+");
    }
}
