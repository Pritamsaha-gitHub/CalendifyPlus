# CalendifyPlus - Calendar Event Scheduling Web Application

This is a prototype web application for scheduling calendar events, developed using Java and MongoDB.

## Available APIs

1. **Create Busy Slots**: Allows users to create busy slots for themselves during the day.
   - Endpoint: `/rest/save/addEvent`
   - Method: POST
   - <img width="628" alt="create busy slots" src="https://github.com/Pritamsaha-gitHub/CalendifyPlus/assets/112754980/6df4b598-728a-4324-8dc2-d8dee2b53441">


2. **Fetch Events**: Allows users to fetch events of themselves and other users.
   - Endpoint: `/rest/retrive/fetchEvent`
   - Method: GET
   - <img width="631" alt="fetch event" src="https://github.com/Pritamsaha-gitHub/CalendifyPlus/assets/112754980/c99fd35f-274b-4315-9ff3-8f20d462d51d">


3. **Fetch Conflicting Events**: Allows users to fetch events where they have conflicts for a particular day.
   - Endpoint: `/rest/retrive/fetchConflict`
   - Method: GET
   - <img width="856" alt="Fetch Conflict" src="https://github.com/Pritamsaha-gitHub/CalendifyPlus/assets/112754980/34ca061f-5e26-4810-bd34-cd1d7f7ac13e">


4. **Fetch Favourable Upcoming Empty Slot**: Allows organizers of an event to fetch the most favorable upcoming empty slot for a given set of users and a particular duration.
   - Endpoint: `/rest/retrive/fetchUpcomingSlot`
   - Method: GET
   - <img width="862" alt="Fetch Slot" src="https://github.com/Pritamsaha-gitHub/CalendifyPlus/assets/112754980/7249fb19-07b2-4358-9d09-1fc728abe099">


5. **Create Event**: Allows users to create events with other users for a defined start time and end time.
   - Endpoint: `/rest/save/addEvent`
   - Method: POST
   - <img width="633" alt="add group event" src="https://github.com/Pritamsaha-gitHub/CalendifyPlus/assets/112754980/4a8e1224-e67e-42bd-ab47-aada6065bf65">


## Technical Choices

- **Java**: Chosen for its robustness, scalability, and extensive ecosystem of libraries and frameworks.
- **MongoDB**: Chosen as the database for its flexibility, scalability, and ability to handle unstructured data, which is suitable for storing calendar events.

## Trade-offs

- **Authentication**: Due to time constraints, authentication has been omitted. This simplifies the development process but sacrifices security.
- **UI**: The focus was on backend functionality, so no UI has been developed. This may limit the usability for end users who prefer graphical interfaces.
- **Testing**: While the code is designed to be modular and testable, comprehensive unit and integration testing may not have been implemented due to time constraints.

## Future Improvements

If I were to spend additional time on the project, I would:

- Implement authentication and authorization to secure the application.
- Develop a frontend UI to improve the user experience.
- Enhance testing coverage with unit tests, integration tests, and end-to-end tests.
- Optimize performance, especially for fetching events and calculating favorable slots.
- Refactor code for better readability, maintainability, and extensibility.

## Contributors

- [Pritam Saha](https://www.linkedin.com/in/pritamsaha-1996/)

Feel free to add more sections or customize the README according to your project's specific needs.
