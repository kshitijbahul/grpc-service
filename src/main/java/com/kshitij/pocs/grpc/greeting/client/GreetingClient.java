package com.kshitij.pocs.grpc.greeting.client;

import com.proto.dummy.DummyServiceGrpc;
import com.proto.greet.*;
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


        /**
         * Trying the Sum Service
         */

       SumServiceGrpc.SumServiceBlockingStub sumServiceBlockingStub = SumServiceGrpc.newBlockingStub(channel);
       Sum sum= Sum.newBuilder().setFirstNumber(Integer.MAX_VALUE).setSecondNumber(Integer.MAX_VALUE).build();
       SumRequest sumRequest= SumRequest.newBuilder().setRequest(sum).build();

       SumResponse sumResponse=sumServiceBlockingStub.addNumbers(sumRequest);
       System.out.println("Got the Sum of 2 number's over gRPC, its excessive but necessary "+sumResponse.getResult());



       //Server Streaming
        /*greetServiceBlockingStub.greetManyTimes(GreetManyTimesRequest.newBuilder().setGreeting(Greeting.newBuilder().setFirstName("Kshitij").build()).build())
                .forEachRemaining((eachResponse)->{
                    System.out.println("The response I am getting is "+eachResponse.getResponse());
                });*/

        //Get Prime numbers

        sumServiceBlockingStub.primeNumbers(PrimeNumberRequest.newBuilder().setNumber(120).build())
                .forEachRemaining(eachPrimeNumber-> System.out.println("Response is "+ eachPrimeNumber.getResponse()));
        channel.shutdown();
    }
}
