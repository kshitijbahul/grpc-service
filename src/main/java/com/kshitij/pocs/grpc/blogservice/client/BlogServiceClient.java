package com.kshitij.pocs.grpc.blogservice.client;

import com.proto.greet.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

public class BlogServiceClient {
    public static void main(String[] args) {
        run();
    }

    private static void run() {
        ManagedChannel blogChannel= ManagedChannelBuilder.forAddress("localhost",50051).usePlaintext().build();

        String blogId=createBlog(blogChannel);

        System.out.println("Blog created with id "+blogId);
        var blog=readBlog(blogChannel,blogId);
        System.out.println("Blog Read with id "+blogId);
        System.out.println("Updating read with id "+blogId);
        updateBlog(blogChannel,blog);
        System.out.println("Blog updated");
        System.out.println("Going to delete the account");
        deleteBlog(blogChannel,blogId);
        System.out.println("Deleted the Blog");
        createBlog(blogChannel);
        createBlog(blogChannel);
        createBlog(blogChannel);
        createBlog(blogChannel);
        getAllBlogs(blogChannel);
        System.out.println("Closed The channel");
        blogChannel.shutdown();
    }

    private static void getAllBlogs(ManagedChannel blogChannel) {
        BlogServiceGrpc.BlogServiceStub blogServiceStub=BlogServiceGrpc.newStub(blogChannel);
        blogServiceStub.getAllBlogs(ListBlogRequest.newBuilder().build(), new StreamObserver<ListBlogResponse>() {
            @Override
            public void onNext(ListBlogResponse value) {
                System.out.println("The blog is "+value.getBlog());
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("got an erroir"+t.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("Completed response from server");
            }
        });
    }

    private static void deleteBlog(ManagedChannel blogChannel, String blogId) {
        BlogServiceGrpc.BlogServiceBlockingStub blogServiceBlockingStub=BlogServiceGrpc.newBlockingStub(blogChannel);
        try {
            DeleteBlogResponse deleteBlogResponse = blogServiceBlockingStub.deleteBlog(DeleteBlogRequest.newBuilder().setBlogId(blogId).build());
            System.out.println("Blog deleted successfully");
        }catch (StatusRuntimeException sre){
            if(sre.getStatus().equals(Status.NOT_FOUND)){
                System.out.println("Could not find blog with id"+blogId);
            }
        }
    }

    private static void updateBlog(ManagedChannel blogChannel, Blog blog) {
        System.out.println("Replacing the doc");
        BlogServiceGrpc.BlogServiceBlockingStub blogServiceBlockingStub=BlogServiceGrpc.newBlockingStub(blogChannel);
        Blog readBlog=null;
        try{
            readBlog=blogServiceBlockingStub.updateBlog(UpdateBlogRequest.newBuilder().setBlog(blog.toBuilder().setContent("UpdatedContent").build()).build()).getBlog();
            System.out.println("The Blog is "+readBlog);
            System.out.println("Replaced the doc");
        }catch (StatusRuntimeException statusRuntimeException){
            if(statusRuntimeException.getStatus().equals(Status.NOT_FOUND)){
                System.out.println("Could not find blog with id"+blog.getId());
            }
        }
    }

    private static Blog readBlog(ManagedChannel blogChannel, String blogId) {
        BlogServiceGrpc.BlogServiceBlockingStub blogServiceBlockingStub=BlogServiceGrpc.newBlockingStub(blogChannel);
        Blog readBlog=null;
        try{
            readBlog=blogServiceBlockingStub.readBlog(ReadBlogRequest.newBuilder().setBlogId(blogId).build()).getBlog();
            System.out.println("The Blog is "+readBlog);
        }catch (StatusRuntimeException statusRuntimeException){
            System.out.println("In the exception "+statusRuntimeException);
            if(statusRuntimeException.getStatus().equals(Status.NOT_FOUND)){
                System.out.println("Could not find blog with id"+blogId);
            }
        }
        finally{
            return readBlog;
        }

    }

    private static String createBlog(ManagedChannel blogChannel) {
        BlogServiceGrpc.BlogServiceBlockingStub blogServiceStub=BlogServiceGrpc.newBlockingStub(blogChannel);
        CreateBlogRequest blogRequest=CreateBlogRequest.newBuilder()
                .setBlog(Blog.newBuilder()
                        .setContent("This is a blog written by me")
                        .setTitle("First Blog")
                        .setAuthorId("1")
                        .build())
                .build();
        CreateBlogResponse blogResponse=blogServiceStub.createBlog(blogRequest);
        System.out.println("The created blog is "+blogResponse);
        return blogResponse.getBlog().getId();
    }
}
