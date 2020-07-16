package com.kshitij.pocs.grpc.greeting.client;

import com.proto.dummy.DummyServiceGrpc;
import com.proto.greet.GreetResponse;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.Greeting;
import com.proto.greet.GreetingRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GreetingClient {
    public static void main(String[] args) {
        System.out.println("Hello gRPC client");
        //creating a Channel
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost",50051)
                .usePlaintext()
                .build();
        //Creating a Sync client
        //DummyServiceGrpc.DummyServiceBlockingStub syncClient = DummyServiceGrpc.newBlockingStub(channel);

        //Creating an async client
        //DummyServiceGrpc.DummyServiceFutureStub asyncClient = DummyServiceGrpc.newFutureStub(channel);

        //Creating the Greet Service for Unary Communication
        GreetServiceGrpc.GreetServiceBlockingStub greetServiceBlockingStub = GreetServiceGrpc.newBlockingStub(channel);

        //Do something with the message
        Greeting greeting= Greeting.newBuilder().setFirstName("Kshitij").setLastName("Bahul").build();
        GreetingRequest greetingRequest= GreetingRequest.newBuilder().setGreeting(greeting).build();
        //The client is able to call the greet function as an RPC call an get the greet Response
        // This is the Greet RPC call;
        GreetResponse greetResponse= greetServiceBlockingStub.greet(greetingRequest);
        //Print the result
        System.out.println("The respons is "+greetResponse);
        System.out.println("Shutting down client");
        channel.shutdown();
    }
}
