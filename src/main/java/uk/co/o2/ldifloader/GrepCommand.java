package uk.co.o2.ldifloader;


import org.grep4j.core.model.Profile;
import org.grep4j.core.model.ProfileBuilder;
import org.grep4j.core.result.GrepResults;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.grep4j.core.Grep4j.constantExpression;
import static org.grep4j.core.Grep4j.grep;
import static org.grep4j.core.fluent.Dictionary.on;

/**
 * Created by ee on 4/30/15.
 */
public class GrepCommand {

    protected String processldifFile(String filename){

        Profile localProfile = ProfileBuilder.newBuilder()
                .name("Local server log")
                .filePath(filename)
                .onLocalhost()
                .build();

        GrepResults results = grep(constantExpression("^dn: uid="), on(localProfile));
        //System.out.println("Grep results : " + results);
        System.out.println("Total lines found : " + results.totalLines());
        System.out.println("Total Execution Time : " + results.getExecutionTime());

        String outputFile = (new File(filename)).getParent()+"/sample.txt";
        System.out.println(outputFile);


        try {

            BufferedWriter out = new BufferedWriter(new FileWriter(outputFile));
            out.write(String.valueOf(results));
            out.close();
        }
        catch (IOException e)
        {
            System.out.println("Exception ");
        }

        return outputFile;
    }


}
