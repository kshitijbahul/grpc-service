package com.kshitij.pocs.grpc.greeting.server;

import com.proto.greet.*;
import io.grpc.stub.StreamObserver;

public class SumServiceImpl extends SumServiceGrpc.SumServiceImplBase {
    @Override
    public void addNumbers(SumRequest request, StreamObserver<SumResponse> responseObserver) {
        Sum sum = request.getRequest();
        SumResponse sumResponse = SumResponse
                .newBuilder()
                .setResult(sum.getFirstNumber()+sum.getSecondNumber())
                .build();
        responseObserver.onNext(sumResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void primeNumbers(PrimeNumberRequest request, StreamObserver<PrimeNumberResponse> responseObserver) {
        Integer number = request.getNumber();
        Integer num=number;
        Integer divisor=2;
        while(num>1){
            /*for(int i=2;i<=num;i++){
                if(num%i==0){
                    num=num/i;
                    responseObserver.onNext(PrimeNumberResponse.newBuilder().setResponse(i).build());
                    break;
                }
            }*/
            if(num%divisor==0){
                num=num/divisor;
                responseObserver.onNext(PrimeNumberResponse.newBuilder().setResponse(divisor).build());
            }else{
                divisor++;
            }
            System.out.println("Checking the while again "+num);
        }
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<AverageRequest> calculateAverage(StreamObserver<AverageResponse> responseObserver) {

        StreamObserver<AverageRequest> averageRequestStreamObserver= new StreamObserver<AverageRequest>() {
            Integer countOfElements=0;
            Long sum=0L;

            @Override
            public void onNext(AverageRequest value) {
                    sum+=value.getNumber();
                    countOfElements++;
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                System.out.println("recieved the signal that request is completed ");
                System.out.println("Sum is  "+sum);
                System.out.println("Count is  "+countOfElements);
                responseObserver.onNext(AverageResponse.newBuilder().setResponse(sum/countOfElements).build());
            }
        };
        return averageRequestStreamObserver;
    }
}
