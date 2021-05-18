# redis_serializer

Talking with Sergio we have seen the need on send messages throught Redis on and abstract way.

I mean we need to store message of diferent classes without knowing the previous format

This project is to study the problem and propose a solution.

On this first commit I just want to run a Redis

```buildoutcfg
docker run --name roger-redis -v -d -p 6379:6379 redis
```

and connect to it