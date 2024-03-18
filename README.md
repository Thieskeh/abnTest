# Getting Started

### Initializing
*   First run the docker-compose file to create the database.
* Then run the AbnApplication.
* The first time, RecipeDbInit will ensure that the Json in the recource folder is read into the MySQL database. Then, through Swagger, you can access all endpoints at:
  http://localhost:8789/swagger-ui/index.html#/

### Testing

* Testing uses a H2 in memory database so no docker is required.
* Not all unit tests are made, a total of 81% of all classes are tested.


### Time Constraint

* Since the time constraint was limited I have not created everything i wanted.
* Elasticsearch would have been nice to have.
* Also testing containers could be done if the time constraint was longer.
* Actuators would also useful to add to make sure the database is still there.