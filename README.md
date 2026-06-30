Server starts on **http://localhost:8080/api/v1**

## Endpoints

| Method | Path                        | Description                                  |
|--------|-----------------------------|----------------------------------------------|
| GET    | /api/v1/                    | Discovery – API info & resource links        |
| GET    | /api/v1/workspaces          | List all workspaces                          |
| POST   | /api/v1/workspaces          | Create a workspace                           |
| GET    | /api/v1/workspaces/{id}     | Get workspace by ID                          |
| HEAD   | /api/v1/workspaces/{id}     | Check workspace exists                       |
| DELETE | /api/v1/workspaces/{id}     | Delete workspace (must be empty)             |
| GET    | /api/v1/models              | List all models (optional ?status= filter)   |
| POST   | /api/v1/models              | Register a new model                         |
| GET    | /api/v1/models/{id}         | Get model by ID                              |
| GET    | /api/v1/models/{id}/metrics | List evaluation metrics for a model          |
| POST   | /api/v1/models/{id}/metrics | Add an evaluation metric                     |

## Seed Data

On startup the in-memory store contains:

- Workspace `WSVISION-01` (Computer Vision Lab)
- Models `MOD-8832` (DEPLOYED), `MOD-1103` (TRAINING), `MOD-4471` (DEPRECATED)
- One starter metric for `MOD-8832`

## curl Examples

```bash
# Discovery
curl -s http://localhost:8080/api/v1

# List workspaces
curl -s http://localhost:8080/api/v1/workspaces

# Create workspace
curl -s -X POST http://localhost:8080/api/v1/workspaces \
  -H "Content-Type: application/json" \
  -d '{"id":"WSTEST-01","teamName":"Test Team","storageQuotaGb":100}'

# Filter models by status
curl -s "http://localhost:8080/api/v1/models?status=DEPLOYED"

# Add evaluation metric
curl -s -X POST http://localhost:8080/api/v1/models/MOD-8832/metrics \
  -H "Content-Type: application/json" \
  -d '{"accuracyScore":0.97}'

# Trigger 409 - delete workspace with models
curl -s -X DELETE http://localhost:8080/api/v1/workspaces/WSVISION-01

# Trigger 422 - invalid workspaceId
curl -s -X POST http://localhost:8080/api/v1/models \
  -H "Content-Type: application/json" \
  -d '{"framework":"TensorFlow","workspaceId":"DOESNOTEXIST"}'
```

---

## Questions & Answers

### Part 1.1

**Question:** When returning a Java object from a method, it is automatically serialised into JSON. Explain the role of a MessageBodyWriter or a JSON provider (like Jackson) in this conversion process.

**Answer:** JAX-RS on its own cannot put a raw Java object on the wire over HTTP, it requires an intermediary to do the conversion. That is where Jackson comes in: it puts itself on record as a MessageBodyWriter for application/json. As the response pipeline executes, JAX-RS will look at the return type and the @Produces annotation, come across Jackson and let it use reflection to go through the object's fields and render them as JSON in the body. A MessageBodyReader handles the other way around. Do away with a registered provider and you are looking at a 500 or 415 from JAX-RS.

---

### Part 1.2

**Question:** REST architecture dictates that APIs should be strictly stateless. Define what statelessness means in this context and explain why it makes cloud APIs easier to scale horizontally across multiple servers.

**Answer:** With statelessness there is no room for sessions or any kind of cached client context; each HTTP request has to be self-contained with all the info the server requires. You have to put the auth token in every request, the page number in every request, and so on. It is what allows for easy horizontal scaling since any one of the servers in your load-balanced cluster can take on any given request without being tied down to an instance. Should a server go down, nothing is lost because it wasn't holding anything. Stateful APIs would force you into the expense of session replication.

---

### Part 2.1

**Question:** Discuss how implementing HTTP Cache-Control headers on the GET workspaces endpoint could improve performance for the client and reduce unnecessary processing load on the server.

**Answer:** Since the workspace list is not going to be different on the next call, we set Cache-Control: max-age=60 to have clients and proxies hold onto the response for a minute. The client gets an instant reply on repeated calls without the network overhead and the server doesn't have to process as many requests. Take 1000 clients polling every five seconds and caching will cut the load 12 times over. We also have must-revalidate in place so they don't hand out stale data once that window has passed.

---

### Part 2.2

**Question:** If a client needs to verify whether a specific workspace exists but wants to save bandwidth by not downloading the entire JSON body, which HTTP method should they use instead of GET? Explain your reasoning.

**Answer:** Use HEAD. In terms of semantics it is no different from a GET (you get a 200 or 404 as appropriate) but the server is not to send back a body. It is a good way for a client to get its answer without moving the whole JSON payload. Handy for validating links at scale or doing an existence check prior to a DELETE.

---

### Part 3.1

**Question:** When creating a new Model via a POST request, it is considered best practice for the server to generate the unique id rather than allowing the client to pass an id in their JSON payload. Discuss the security and data integrity reasons behind this architectural choice.

**Answer:** There are security and integrity concerns. A bad actor could feed in an ID to clobber another user's resource or try to enumerate the system by guessing something like MOD-0001 to MOD-9999. Then you have the matter of data integrity: if two clients happen to submit with the same self-assigned ID at the same time you have a race condition and one record is quietly overwritten. Not to mention special characters in client IDs can mess up URL routing. Let the server generate a UUID and you have global uniqueness and a consistent format.

---

### Part 3.2

**Question:** If a user attempts to search for a framework containing spaces or special characters (e.g. ?framework=Scikit Learn & Tools), how must the client modify the URL, and why is this encoding necessary?

**Answer:** The client has to percent-encode it to ?framework=Scikit%20Learn%20%26%20Tools. You cannot have unencoded characters in a URL because they carry structural weight; a space is seen as the end of the URL and an & will be parsed as a new parameter, leaving you with several malformed values. Percent-encoding is the way to replace those reserved characters with their hex ASCII equivalent.

---

### Part 4.1

**Question:** You can place annotations like @Produces(MediaType.APPLICATION_JSON) at either the class level or the individual method level. What is the benefit of class-level placement, and how does method-level overriding work?

**Answer:** You can put @Produces(APPLICATION_JSON) at the class level and it becomes the default for all the methods, sparing you from having to write it on every single @GET or @POST. If a particular method needs to deviate, say to return plain text, you just put a @Produces(TEXT_PLAIN) on it and JAX-RS will make an exception for that endpoint while the rest of the class stays in JSON.

---

### Part 5.2

**Question:** HTTP status codes are categorised into classes (e.g. 2xx, 4xx, 5xx). Explain fundamentally why a validation failure caused by the user providing a non-existent workspaceId must return a 4xx code rather than a 5xx code.

**Answer:** A 4xx is on the client, a 5xx is a server problem. If a client hands us a workspaceId for something that isn't there, the server is doing its job by telling them so. To return a 500 would be to mislead the client into thinking we broke and they should retry. A 422 is much clearer: we received and understood your request but the data is semantically invalid, so fix it and try again.

---

### Part 5.4

**Question:** If an operation throws a specific custom exception and you also have a global ExceptionMapper<Throwable>, how does the JAX-RS runtime determine which mapper to execute?

**Answer:** JAX-RS will go for the most specific mapper available, the one whose generic type is nearest to the exception in the hierarchy. Throw a LinkedWorkspaceNotFoundException and you have both an ExceptionMapper for that and one for Throwable? JAX-RS takes the former. The global Throwable mapper is only called when nothing more specific applies. It is the same principle as a catch block in Java.

---

### Part 5.5

**Question:** In your filter, you interact with ContainerRequestContext and ContainerResponseContext. List two pieces of crucial HTTP metadata you can extract from these contexts that are highly valuable for debugging server issues.

**Answer:** On the ContainerRequestContext side you have getUriInfo().getRequestUri() which is indispensable for debugging filtered requests as it gives you the full URI with query params. And getHeaderString("Content-Type") for the incoming media type if you need to track down a content negotiation issue. From the ContainerResponseContext, getStatus() is what you want to keep tabs on error rates and getHeaders().getFirst("Content-Type") to verify what was sent back.
