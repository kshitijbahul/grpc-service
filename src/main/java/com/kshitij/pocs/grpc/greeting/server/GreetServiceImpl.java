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

    @Override
    public StreamObserver<LongGreetRequest> longGreet(StreamObserver<LongGreetResponse> responseObserver) {
        //respond whenever server wants
        //We are going ot respond with a Stream Observer i.e what we do on a stream observer
        StreamObserver<LongGreetRequest> requestObserver= new StreamObserver<LongGreetRequest>() {
            StringBuilder result=new StringBuilder();
            @Override
            public void onNext(LongGreetRequest value) {
                //client sends a message
                result.append("Hello "+value.getGreeting().getFirstName()+" ! I am accumulating");
            }

            @Override
            public void onError(Throwable t) {
                //client sends and error
            }

            @Override
            public void onCompleted() {
                //client is done
                //send a response
                responseObserver.onNext(LongGreetResponse.newBuilder().setResponse(result.toString()).build());
            }
        };
        return requestObserver;
    }

    @Override
    public StreamObserver<TwoWayRequest> twoWayGreet(StreamObserver<TwoWayResponse> responseObserver) {
        StreamObserver<TwoWayRequest> twoWayRequestStreamObserver= new StreamObserver<TwoWayRequest>() {
            @Override
            public void onNext(TwoWayRequest value) {
                //got a message
                System.out.println("Got a message and  reploying ");
                responseObserver.onNext(TwoWayResponse.newBuilder().setResponse("Got this in the Request, pinging back "+value.getGreeting().getFirstName()).build());
            }

            @Override
            public void onError(Throwable t) {
            }

            @Override
            public void onCompleted() {
                System.out.println("Got a completed request ");
                responseObserver.onCompleted();//Mark te process as completed
            }
        };
        return twoWayRequestStreamObserver;
    }
}
