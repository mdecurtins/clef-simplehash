# Simplehash: An MIR Algorithm Server
Simplehash is a music information retrieval (MIR) algorithm server developed for demonstration purposes for the Clef system, which can be found at [this](#) repository.

Currently, Simplehash is configured to recognize Humdrum data only.

Current version: 0.1.0
## Installing
Inside the `algorithms/` directory of the Clef system, run the following commands:
```
mkdir simplehash
cd !$
git clone https://github.com/mdecurtins/clef-simplehash.git .
```
### Requirements
Simplehash requires the following to be installed on your system:
- Docker
- Java (SE 8+)
- Maven
## Providing Data
 You must provide sources of symbolic music data for Simplehash to parse and store. Place Humdrum `*.krn` files in the `data/` directory created when this repo is cloned.
## Running Simplehash
Before running, you must compile the source. Run:
```
simplehash/ $ cd simplehash
simplehash/simplehash/ $ mvn package
simplehash/simplehash/ $ cd ..
```
If you wish to change the version number of Simplehash, you need to change it in both the `Dockerfile` as well as in `simplehash/pom.xml`.

Now build the Docker image:
```
docker build -t clef_simplehash .
```
To run Simplehash:
```
docker run -d -p hostPort:8080 --rm --name clef-simplehash clef_simplehash
```
where `hostPort` is a port not already in use by another process or container. To verify that the container is running, type `docker ps`. To check the startup logs of Simplehash, type `docker logs clef-simplehash`. You should see that the Simplehash database has been initialized and populated with data processed from the `*.krn` files you placed in `data/`.
### Querying Simplehash
To query Simplehash, create an HTTP POST request to `http://127.0.0.1:hostPort/simplehash?staffIdx=#` where `#` is the staff index containing the query music notation. (The example URL here presumes that you are running Simplehash as a standalone container.)

You must set a header of `Content-Type: application/xml` and provide a well-formed MusicXML document as the request body.
 