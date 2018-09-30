## ResourceService
RESTful API for storing web resources.
It allows to add a resource by posting URL and stores is in PostgreSQL database.
Users can access given resources by id or name. Accessing by id results in
downloading given resource.

### URLs
 ` POST /api/resources`
Requires JSON body with fields name and resourceUrl <br>
`GET /api/resources` Requires param `search`. Returns a list of resources which names
contains value of search param. <br>
`GET /api/resources/id` Downloads the resource with given id. <br>
`GET /api/resources/many/ids` Returns a list of resources with given ids. <br>
`DELETE /api/resources/id` Deletes a resource with given id. <br>

 
 
 