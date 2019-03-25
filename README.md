# dd-grpc-play-demo

## Usage

Build project:
```
> ./build.sh
```

Run Backend gRPC server and frontend play server with dd tracer:
```
> ./run_backend.sh
> ./run_frontend.sh
```

Build distributed traces
```
> curl -XGET localhost:9000/add3/1000
   "got 1003"
```

## Expected Tracing Behavior

We expect to see a trace across both process boundaries, capturing both calls to the gRPC service within a single HTTP request to Play.
 

 GET /add3*
|---------------------------|
   *addOne()*
  |-----------|
               *addTwo()*
               |-----------|

