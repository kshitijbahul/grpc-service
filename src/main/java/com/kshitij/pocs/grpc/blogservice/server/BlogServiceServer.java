package com.kshitij.pocs.grpc.blogservice.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class BlogServiceServer {
    public static void main(String[] args) throws InterruptedException, IOException {
        Server blogServer= ServerBuilder.forPort(50051)
                //.useTransportSecurity(new File("ssl/server.cert"),new File("ssl/server.pem"))
                .addService(new BlogServiceImpl())
                .build();
        blogServer.start();
        System.out.println("Server Started");
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            System.out.println("In the shutdown Hook");
            blogServer.shutdown();
            System.out.println("The thread is stopped");
        }));
        blogServer.awaitTermination();
    }
}
