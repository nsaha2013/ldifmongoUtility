package uk.co.o2.ldifloader;

import com.mongodb.*;

import java.io.IOException;

public class MongoReader {
    protected DB mongodb;


    public MongoReader(String mongoconnection) throws IOException {

        MongoClientURI uri  = new MongoClientURI(mongoconnection);
        MongoClient client = new MongoClient(uri);
        mongodb = client.getDB(uri.getDatabase());

    }


    public DBObject searchUIDinMongo(String uid){

        DBCollection identity = mongodb.getCollection("identityV3");
        DBObject record = identity.findOne(new BasicDBObject("uid", uid));


       return record;

    }
}
