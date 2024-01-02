<h1 align="center">
  <a href="https://github.com/dec0dOS/amazing-github-template">
    <img src="https://images.unsplash.com/photo-1494412552100-42e4e7a74ec6?q=80&w=1470&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D" alt="Logo" width="300" height="150">
  </a>
</h1>

<div align="center">
  <h1> Port Container Tracking Project </h1>
  <br />
  <br />

</div>

<details open="open">
<summary>Table of Contents</summary>

- [Context](#context)
    - [Tasks](#tasks)
- [Usage](#usage)
    - [Prerequisites](#prerequisites)
    - [To Run project](#to-run-project)
    - [API usage](#api-usage)
    - [To Run Test](#to-run-test)

</details>

---

## Context
The port industry is an economic activity that involves the exchange of products through imports and exports.
The logistic involved in it is complex. So, to manage that complexity, Software and Hardware solutions are used.

In this context, Terminal Operating System (TOS) aims to manage and optimize the operations of a container terminal
such as vessel planning, yard management, container management, gate operations and others.

Thus, in this project we will focus on the **container management**, specifically in some **container tracking features**.

### Tasks
1. Problem insights:
    1. In the container management, container tracking provides visibility about the location and status of containers within the port or terminal.
       It  is useful **for operators to make informed decisions and manage operations efficiently.**
    1. To delimit the scope, some container tracking features will be taken into consideration for the test. Specifically, **register container** and **get container detail**.
    1. Containers traffic: ~12 million container movements in Brazil - year 2023 ([source](https://public.flourish.studio/visualisation/14855225/)).
       For the test, I will assume all traffic containers unloading in a unique Port.
        1. Increase of ~1 million of containers per year by the next 30 years.
        1. Container movements in next 30 years: 12mll + 30mll = 42 = ~ 40 millions
    1. User: Port operators.
1. Features to implement ([system design](https://www.tldraw.com/v/gUwCSd1gTJ_YO3ZpVzSdA?viewport=-904,-87,2325,1487&page=page:page)):
    1. Register a container.
        1. Considering a normal distribution over the year, ~2 containers are registered per second.
        1. Considering a peak of 10 = 2 x 10 = **~20 containers are registered per second**.
    1. Get container detail.
        1. Considering 2 containers registered per second, thus, there are ~8x10^4 containers registered per day.
        1. Considering Containers pass through 5 checking points from unloading to pickup status,
           ~8x10^4 x 5 = ~4x10^5 by day retrieves of container detail.
        1. ~5 retrieves of containers detail per second.
        1. Considering a peak of 10 =  5 x 10 = **~50 containers detail retrieved per second**.
1. DataBase decisions:
    1. Feature 1: Register a container:
        1. Considering the traffic to create records of containers, the best choice is use an SQL database.
    1. Feature 2: get container detail.
        1. Considering the traffic to retrieve records of containers, the best choice is use an SQL database.
1. Stack decision:
    1. Backend:
        1. Options available: (Java, Python, .NET)
        1. Java and .NET are well known for enterprise-level applications.
        1. I chose Java to make the test, because it is compatible across all operating systems, able for migrate to kotlin,
           open source tools to be used with java, contrary to .NET.
        1. Also, considering my experience.
    1. Frontend:
        1. Options available: (React, Angular, Vue.js)
        1. Based on a Google research all of them are good options to develop the frontend app ([comparative](https://www.browserstack.com/guide/angular-vs-react-vs-vue)).
        2. Based on my preferences, I will use ReactJs.
    1. Database:
        1. Options available: (PostgreSQl, Oracle, SQL Server)
        2. I can choose PostgreSQl or Oracle based on the compatibility with the backend language and DB decisions for the
           features requirements, but due to PostgresSQL is an open-source RDBMS, I chose PostgreSQL.
1. Other Considerations:
    1. Best practices: I will use Clean Code, Clean Architecture, Left-Shift Testing, TDD and BDD methodology.

## Usage
### Prerequisites
- Install Java 17
- Install docker and docker-compose
- python -m pip install pre-commit
- pre-commit install
- pre-commit install --hook-type commit-msg
- clone the repo

### Additional
- create .env file in root of project, used in docker-compose.yaml
```yaml
POSTGRES_DB=db-name
POSTGRES_USER=user-name
POSTGRES_PASSWORD=db-password
```
### To Run project
#### Local
```sh
./gradlew bootRun
```
After run access to (http://localhost:8080/api-route)
### API usage
#### Endpoint Create Container example

| Method | Endpoint                 | Description                    | Request Headers                   | Request Body (Example)                     | Response Status           | Response Body (Example)                                                                                                       |
|--------|--------------------------|--------------------------------|------------------------------------|--------------------------------------------|---------------------------|-------------------------------------------------------------------------------------------------------------------------------|
| POST   | /container/api/v1/create | Create Container               | Content-Type: application/json| ```{"code": "ABC", "status": "PICKED_UP"}``` | 201 Created               | `{"id": 1, "code": "ABC", "status": "PICKED_UP"}`                                                                             |
| POST    | /container/api/v1/create | Create Container               | Content-Type: application/json| `{"code": "ABC", "status": "OTHER"}`       | 400 Bad Request           | `{"timestamp": "...", "status": 400, "error": "Bad Request", "message": "...", "path": "/container/api/v1/create"}`           |
| POST    | /container/api/v1/create | Create Container - null fields | Content-Type: application/json| `{"code": "ABC"}`     | 500 Internal Server Error | `{"timestamp": "...", "status": 500, "error": "Internal Server Error", "message": "...", "path": "/container/api/v1/create"}` |


#### Endpoint Detail Container example
container register
{
"id": 1,
"code": "ABC",
"status": "CUSTOMS_CLEARANCE"
}

| HTTP Method | Endpoint                   | Description                        | Response Status  | Response Body (Example)                               |
|-------------|----------------------------|------------------------------------|------------------|--------------------------------------------------------|
| GET         | /container/api/v1/detail/1 | Detail Container - exists container | 200 OK           | `{"id": 1, "code": "ABC", "status": "CUSTOMS_CLEARANCE"}` |
| GET         | /container/api/v1/detail/5 | Detail Container - no container    | 404 Not Found     |                                                        |


#### Endpoint List Container example
| HTTP Method | Endpoint                        | Description                         | Response Status | Response Body (Example)                               |
|-------------|---------------------------------|-------------------------------------|------------------|--------------------------------------------------------|
| GET         | /container/api/v1/containers    | List Containers - exists containers | 200 OK           | `[{"id": 1, "code": "ABC", "status": "UNLOADING"}, {"id": 2, "code": "ABC", "status": "PICKED_UP"}]` |
| GET         | /container/api/v1/containers    | List Containers - no containers     | 200 OK           | `[]`                                                   |


#### Endpoint Update Container example
Container register
{
"id": 1,
"code": "ABC",
"status": "UNLOADING"
}

| Method | Endpoint                   | Description     | Request Headers                   | Request Body (Example)                                | Response Status | Response Body (Example)                                                                                               |
|--------|----------------------------|-----------------|------------------------------------|-------------------------------------------------------|-----------------|-----------------------------------------------------------------------------------------------------------------------|
| PUT    | /container/api/v1/update/1 | Update Container | Content-Type: application/json| ```{"id": 1, "code": "EFG", "status": "PICKED_UP"}``` | 200 OK          | `{"id": 1, "code": "EFG", "status": "PICKED_UP"}`                                                                     |
| PUT   | /container/api/v1/update/1 | Update Container | Content-Type: application/json| `{"id": 1, "code": null, "status": null}`             | 200 OK          | `{"id": 1, "code": "ABC", "status": "UNLOADING"}`|
| PUT   | /container/api/v1/update/1 | Update Container | Content-Type: application/json| `{"id": 1, "code": "DFG", "status": "OTHER"}`         | 400 Bad Request | `{"timestamp": "...", "status": 400, "error": "Bad Request", "message": "...", "path": "/container/api/v1/update/1"}` |
| PUT   | /container/api/v1/update/5 | Update Container | Content-Type: application/json| `{"id": 5, "code": "DFG", "status": "PICKED_UP"}`     | 404 Not Found   | |

#### Endpoint Delete Container example
Container register
{
"id": 1,
"code": "ABC",
"status": "UNLOADING"
}

| Method | Endpoint                   | Description      | Response Status |
|--------|----------------------------|------------------|-----------------|
| DELETE | /container/api/v1/delete/1 | Delete Container | 204 Not Content |
| DELETE | /container/api/v1/delete/5 | Delete Container | 404 Not Found   |

### To Run Test
```sh
./gradlew test
```
### To see Coverage Report

```
path: build/reports/jacoco/test/html/index.html
```



### Stack
- Sprint boot
- Postgresql
- Mockito
- Junit
- Jacoco
- Testcontainer
- Instancio
- Spotless
- docker-compose


### Improvements
- Implement the Update and delete container API
- Add logging for logs
- Configuration of features flags
- Monitor the app with Grafana and Prometheus
- Use SonarQube in a collaborative quality code way.
