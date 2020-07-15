package com.kshitij.pocs.grpc.greeting.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class GreetingServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Hello gRPC");
        Server server = ServerBuilder.forPort(50051).build();
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
