package com.kshitij.pocs.grpc.greeting.client;

import com.proto.greet.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class SumClient {

    public static void main(String[] args) {
        ManagedChannel sumChannel= ManagedChannelBuilder.forAddress("localhost",50051).usePlaintext().build();
        run();
    }
    public static void run(){
        ManagedChannel sumChannel=ManagedChannelBuilder.forAddress("localhost",50051).usePlaintext().build();
        //unaryCall(sumChannel);
        //serverStreamingCall(sumChannel);
        //clientStreamingCall(sumChannel);
        twoWayStreaming(sumChannel);
        sumChannel.shutdown();
    }

    private static void twoWayStreaming(ManagedChannel sumChannel) {
        SumServiceGrpc.SumServiceStub sumServiceStub= SumServiceGrpc.newStub(sumChannel);
        CountDownLatch waitForCompletionLatch=new CountDownLatch(1);
        StreamObserver<FindMaximumRequest> findMaximumRequestStreamObserver=sumServiceStub.findMaximum(new StreamObserver<FindMaximumResponse>() {
            @Override
            public void onNext(FindMaximumResponse value) {
                System.out.println("GOt a response , current max is "+value.getResponse());
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("Error occured");
            }

            @Override
            public void onCompleted() {
                System.out.println("Server has completed ");
                waitForCompletionLatch.countDown();
            }
        });
        IntStream.range(1,100).forEach(number->findMaximumRequestStreamObserver.onNext(FindMaximumRequest.newBuilder().setRequest(number).build()));
        findMaximumRequestStreamObserver.onCompleted();
        try {
            waitForCompletionLatch.await(3L,TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void clientStreamingCall(ManagedChannel sumChannel) {
        SumServiceGrpc.SumServiceStub asyncStub= SumServiceGrpc.newStub(sumChannel);
        CountDownLatch latch= new CountDownLatch(1);
        StreamObserver<AverageRequest> averageResponseStreamObserver= asyncStub.calculateAverage(new StreamObserver<AverageResponse>() {
            @Override
            public void onNext(AverageResponse value) {
                System.out.println("Got a response form server "+value.getResponse());
            }

            @Override
            public void onError(Throwable t) {
                //Should be doing something here
            }

            @Override
            public void onCompleted() {
                System.out.println("Got a completed Request from the server");
                latch.countDown();
            }
        });

        averageResponseStreamObserver.onNext(AverageRequest.newBuilder().setNumber(10).build());
        averageResponseStreamObserver.onNext(AverageRequest.newBuilder().setNumber(20).build());
        averageResponseStreamObserver.onNext(AverageRequest.newBuilder().setNumber(30).build());
        averageResponseStreamObserver.onNext(AverageRequest.newBuilder().setNumber(40).build());
        averageResponseStreamObserver.onCompleted();//sending signal that the request is completed

        try {
            latch.await(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.out.println("Got the interrupted exception ");
            e.printStackTrace();
        }

    }

    private static void serverStreamingCall(ManagedChannel sumChannel) {
        SumServiceGrpc.SumServiceBlockingStub sumServiceBlockingStub = SumServiceGrpc.newBlockingStub(sumChannel);
        sumServiceBlockingStub.primeNumbers(PrimeNumberRequest.newBuilder().setNumber(120).build())
                .forEachRemaining(eachPrimeNumber-> System.out.println("Response is "+ eachPrimeNumber.getResponse()));
    }

    public static void unaryCall(ManagedChannel sumChannel){
        SumServiceGrpc.SumServiceBlockingStub sumServiceBlockingStub = SumServiceGrpc.newBlockingStub(sumChannel);
        Sum sum= Sum.newBuilder().setFirstNumber(Integer.MAX_VALUE).setSecondNumber(Integer.MAX_VALUE).build();
        SumRequest sumRequest= SumRequest.newBuilder().setRequest(sum).build();
        SumResponse sumResponse=sumServiceBlockingStub.addNumbers(sumRequest);
        System.out.println("Got the Sum of 2 number's over gRPC, its excessive but necessary "+sumResponse.getResult());
    }
}
