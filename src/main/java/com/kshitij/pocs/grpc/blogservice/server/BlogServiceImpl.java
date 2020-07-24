package com.kshitij.pocs.grpc.blogservice.server;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.proto.greet.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.bson.Document;
import org.bson.types.ObjectId;

public class BlogServiceImpl extends BlogServiceGrpc.BlogServiceImplBase {

    private MongoClient mongoClient= MongoClients.create("mongodb://localhost:27017");
    private MongoDatabase database = mongoClient.getDatabase("blogDb");
    private MongoCollection<Document> collection=database.getCollection("blog");

    @Override
    public void createBlog(CreateBlogRequest request, StreamObserver<CreateBlogResponse> responseObserver) {
        Blog blog=request.getBlog();
        Document newBlogDoc = new Document("author_id",blog.getAuthorId())
                .append("content",blog.getContent())
                .append("title",blog.getTitle());
        //CREATE DOC IN MONGODB
        collection.insertOne(newBlogDoc);
        String id= newBlogDoc.get("_id").toString();
        System.out.println("Inserted Blog for Id "+id);
        CreateBlogResponse blogResponse= CreateBlogResponse.newBuilder()
                .setBlog(blog.toBuilder().setId(id).build())
                .build();
        responseObserver.onNext(blogResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void readBlog(ReadBlogRequest request, StreamObserver<ReadBlogResponse> responseObserver) {
        String blogId=request.getBlogId();
        var response=collection.find(Filters.eq("_id",new ObjectId(blogId))).first();
        if(response.isEmpty()){
            responseObserver.onError(Status.NOT_FOUND.withDescription("Blog with id "+blogId+" not found").asRuntimeException());
        }else {
            Blog blog=getBlogFromResponse(response);
            responseObserver.onNext(ReadBlogResponse.newBuilder().setBlog(blog).build());
            responseObserver.onCompleted();
        }

    }

    private Blog getBlogFromResponse(Document response) {
        return Blog.newBuilder()
                .setAuthorId(response.getString("author_id"))
                .setId(response.get("_id").toString())
                .setTitle(response.getString("title"))
                .setContent(response.getString("content"))
                .build();
    }

    @Override
    public void updateBlog(UpdateBlogRequest request, StreamObserver<UpdateBlogResponse> responseObserver) {
        Blog blog= request.getBlog();
        var response=collection.find(Filters.eq("_id",new ObjectId(blog.getId()))).first();
        try {
            if (response.isEmpty()) {
                responseObserver.onError(Status.NOT_FOUND.withDescription("Blog with id " + blog.getId() + " not found").asRuntimeException());
            } else {
                System.out.println("Blog found");
                Document newBlogDoc = new Document("author_id", blog.getAuthorId())
                        .append("content", blog.getContent())
                        .append("title", blog.getTitle());
                collection.replaceOne(Filters.eq("_id",new ObjectId(blog.getId())),newBlogDoc);
                //String id= newBlogDoc.get("_id").toString();
                responseObserver.onNext(UpdateBlogResponse.newBuilder().setBlog(blog).build());
            }
        }catch (Exception e){
            responseObserver.onError(Status.INTERNAL.withDescription("Error occured while updating the blog "+blog).asRuntimeException());
        }
        finally {
            responseObserver.onCompleted();
        }
    }

    @Override
    public void deleteBlog(DeleteBlogRequest request, StreamObserver<DeleteBlogResponse> responseObserver) {
        String blogId=request.getBlogId();
        try {
            DeleteResult deleteResult=collection.deleteOne(Filters.eq("_id",new ObjectId(blogId)));
            if (deleteResult.getDeletedCount()<1) {
                responseObserver.onError(Status.NOT_FOUND.withDescription("Blog with id " + blogId + " not found").asRuntimeException());
            } else {
                System.out.println("Blog found");
                //String id= newBlogDoc.get("_id").toString();
                responseObserver.onNext(DeleteBlogResponse.newBuilder().setBlogId(blogId).build());
            }
        }catch (Exception e){
            responseObserver.onError(Status.INTERNAL.withDescription("Error occured while updating the blogId "+blogId).asRuntimeException());
        }
        finally {
            responseObserver.onCompleted();
        }
    }

    @Override
    public void getAllBlogs(ListBlogRequest request, StreamObserver<ListBlogResponse> responseObserver) {
        collection.find().forEach(document -> {responseObserver.onNext(ListBlogResponse.newBuilder().setBlog(getBlogFromResponse(document)).build());});
        responseObserver.onCompleted();
    }
}
