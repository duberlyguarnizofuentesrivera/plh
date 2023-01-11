# Package *generators*

This file has 3 classes:

- **TicketCodeGenerator**: Generator of a custom code for every ticket. This contains the logic and creates a code in
  the format: *DUB059-8547*.
- **TicketCode**: Annotation to auto-generate a code for every new ticket created. This is used in the entity and calls
  the logic.
- **CurrentUserAuditorAware**: Returns a User entity (current logged-in user) to associate to a field in other entity
  using the @CreatedBy annotation, provided by Spring Data.

*Written by Duberly Guarnizo (2023).*