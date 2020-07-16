package com.kshitij.pocs.grpc.greeting.server;

import com.proto.greet.GreetResponse;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.Greeting;
import com.proto.greet.GreetingRequest;
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
}
