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
}
