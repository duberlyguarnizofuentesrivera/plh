# PLH

by Duberly Guarnizo

## Software for transport management.

Using:

- PostgreSQL
- Spring Boot 3 (Web and Rest)
- Spring Data JPA
- Spring Security
- Thymeleaf for local presentation
- API controllers for other frontends

### First admin creation

Main program implements Spring's CommandLineRunner interface.
For first admin creation pass these arguments in the command line when executing:

``` bash
--create-admin=true --admin-password=<your_password>
```

If you keep the arguments after the first run, they will be ignored, but it's not a good practice, so please remove them
after first run.