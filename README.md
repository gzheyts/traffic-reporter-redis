# Traffic report service with Redis database

### Installation
*  Start Redis server 
```bash
redis-server --loglevel verbose
```
*  Start application
```bash
./mvnw test spring-boot:run
```
* Open [Swagger](http://localhost:8080/swagger-ui.html)

### Usage

* Save traffic links
```bash
curl -X POST "http://localhost:8080/visited_links"\
    -H "accept: */*" -H "Content-Type: application/json"\
    -d "{ \"links\": [ \"https://ya.ru\", \"https://ya.ru?q=123\", \"google.com\", \"https://stackoverflow.com/questions/11828270/how-to-exit-the-vim-editor\"]}"
    
```
response
```bash
{"status":"ok"}
```
* Query unique domains
```bash
 curl -X GET "http://localhost:8080/visited_domains?from=$(( `date +%s` - 60))&to=$(( `date +%s`))" -H "accept: */*"
```
response
```bash
{"domains":["ya.ru","google.com","stackoverflow.com"],"status":"ok"}
```


