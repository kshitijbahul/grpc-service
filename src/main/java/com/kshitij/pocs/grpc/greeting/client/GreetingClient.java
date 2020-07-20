package com.kshitij.pocs.grpc.greeting.client;

import com.proto.dummy.DummyServiceGrpc;
import com.proto.greet.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GreetingClient {
    public static void main(String[] args) {
        System.out.println("Hello gRPC client");
        run();
    }
    public static void run(){
        //creating a Channel
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost",50051)
                .usePlaintext()
                .build();
        //doUnaryCall(channel);
        //doServerStreamingCall(channel);
        doClientStreamingCall(channel);
        System.out.println("Shutting down client");
        channel.shutdown();
    }
    private static void doUnaryCall(ManagedChannel channel) {
        GreetServiceGrpc.GreetServiceBlockingStub greetServiceBlockingStub = GreetServiceGrpc.newBlockingStub(channel);

        //Do something with the message
        Greeting greeting= Greeting.newBuilder().setFirstName("Kshitij").setLastName("Bahul").build();
        //Creating a Sync client
        //DummyServiceGrpc.DummyServiceBlockingStub syncClient = DummyServiceGrpc.newBlockingStub(channel);

        //Creating an async client
        //DummyServiceGrpc.DummyServiceFutureStub asyncClient = DummyServiceGrpc.newFutureStub(channel);

        //Creating the Greet Service for Unary Communication
        GreetingRequest greetingRequest= GreetingRequest.newBuilder().setGreeting(greeting).build();
        //The client is able to call the greet function as an RPC call an get the greet Response
        // This is the Greet RPC call;
        GreetResponse greetResponse= greetServiceBlockingStub.greet(greetingRequest);
        //Print the result
        System.out.println("The respons is "+greetResponse);
    }
    private static void doServerStreamingCall(ManagedChannel channel) {
        //Server Streaming
        //Creating a stub
        GreetServiceGrpc.GreetServiceBlockingStub greetServiceBlockingStub = GreetServiceGrpc.newBlockingStub(channel);
        greetServiceBlockingStub.greetManyTimes(GreetManyTimesRequest.newBuilder().setGreeting(Greeting.newBuilder().setFirstName("Kshitij").build()).build())
                .forEachRemaining((eachResponse)->{
                    System.out.println("The response I am getting is "+eachResponse.getResponse());
                });
        //Get Prime numbers
    }

    private static void doClientStreamingCall(ManagedChannel channel)  {
        //Create an Async Client
        GreetServiceGrpc.GreetServiceStub greetServiceBlockingStub= GreetServiceGrpc.newStub(channel);
        CountDownLatch latch=new CountDownLatch(1);
        StreamObserver<LongGreetRequest> requestObserver=greetServiceBlockingStub.longGreet(new StreamObserver<LongGreetResponse>() {

            @Override
            public void onNext(LongGreetResponse value) {
                //We get a response form teh server
                //Will be called only once since the server will only response one
                System.out.println("Received response from server");
                System.out.println("Response is "+value.getResponse());
            }

            @Override
            public void onError(Throwable t) {
                //We get an error from the server
            }

            @Override
            public void onCompleted() {
                //We get a response form teh server
                System.out.println("Server has completed sending something !!!!");
                latch.countDown();
            }
        });
        System.out.println("Sending message 1");
        requestObserver.onNext(LongGreetRequest.newBuilder().setGreeting(Greeting.newBuilder().setFirstName("Kshitij ").build()).build());
        System.out.println("Sending message 2");
        requestObserver.onNext(LongGreetRequest.newBuilder().setGreeting(Greeting.newBuilder().setFirstName("John ").build()).build());
        System.out.println("Sending message 3");
        requestObserver.onNext(LongGreetRequest.newBuilder().setGreeting(Greeting.newBuilder().setFirstName("Mark ").build()).build());
        System.out.println("Sending message 4");
        requestObserver.onNext(LongGreetRequest.newBuilder().setGreeting(Greeting.newBuilder().setFirstName("Cunt ").build()).build());
        requestObserver.onCompleted();//to tell the server the client is done sending data
        //We did so because the client knows what message is being sent
        try {
            latch.await(3L, TimeUnit.SECONDS);//to wait for the response to give a completed
        } catch (InterruptedException e) {
            System.out.println("Implementing the client");
            e.printStackTrace();
        }

    }
}
