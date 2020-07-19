package com.kshitij.pocs.grpc.greeting.server;

import com.proto.greet.*;
import io.grpc.stub.StreamObserver;

//gRPC plugin code
public class GreetServiceImpl extends GreetServiceGrpc.GreetServiceImplBase {
    @Override
    public void greet(GreetingRequest request, StreamObserver<GreetResponse> responseObserver) {
        Greeting greeting= request.getGreeting();
        //Extract the response
        String firstName = greeting.getFirstName();
        String lastName = greeting.getLastName();
        String result = "Hello "+firstName;
        //Set the response
        GreetResponse greetResponse  = GreetResponse.newBuilder().setResponse(result).build();
        responseObserver.onNext(greetResponse);//Sending the response to the client
        responseObserver.onCompleted();//Marking it completed
        //super.greet(request, responseObserver);
    }

    @Override
    public void greetManyTimes(GreetManyTimesRequest request, StreamObserver<GreetManyTimesResponse> responseObserver) {
        String firstName = request.getGreeting().getFirstName();
        try {
            for (int i = 0; i < 10; i++) {
                String result = "Hello " + firstName + " this is the " + i + "th response.";
                GreetManyTimesResponse response = GreetManyTimesResponse.newBuilder().setResponse(result).build();
                responseObserver.onNext(response);

                Thread.sleep(1000);
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
         finally {
            responseObserver.onCompleted();
        }
    }
}
