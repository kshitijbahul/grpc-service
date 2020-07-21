package com.kshitij.pocs.grpc.greeting.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.File;
import java.io.IOException;

public class GreetingServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Hello gRPC");
        //userPlainServer();
        userEncryptedServer();
    }

    private static void userEncryptedServer() throws InterruptedException, IOException {
        Server server = ServerBuilder.forPort(50051)
                //Adding the service to the server when it starts
                .useTransportSecurity(new File("ssl/server.crt"),new File("ssl/server.pem"))//add server security
                .addService(new GreetServiceImpl())
                .addService(new SumServiceImpl())//Adding the Sum Service to the server
                .build();
        server.start();//Start The server
        //Add a shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            System.out.println("Received Shutdown request ");
            server.shutdown();
            System.out.println("Successfully stopped the server");

        }));
        server.awaitTermination();//This is meant to blocking in the MAin thread
    }

    private static void userPlainServer() throws IOException, InterruptedException{
        Server server = ServerBuilder.forPort(50051)
                //Adding the service to the server when it starts
                .addService(new GreetServiceImpl())
                .addService(new SumServiceImpl())//Adding the Sum Service to the server
                .build();
        server.start();//Start The server
        //Add a shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            System.out.println("Received Shutdown request ");
            server.shutdown();
            System.out.println("Successfully stopped the server");

        }));
        server.awaitTermination();//This is meant to blocking in the MAin thread
    }


}
