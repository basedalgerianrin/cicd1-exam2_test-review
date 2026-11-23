# CICD-1 Coding Test — Review, Improve and Reflect

**Student ID:** G00393324  
**Examination group:** A (Room Booking / JPQL) or B (OpenFeign):  Group B.

**Original examination pull-request URL (`exam-work`):** https://github.com/basedalgerianrin/cicd1-exam2_BookingService 

**New review repository URL:** https://github.com/basedalgerianrin/cicd1-exam2_test-review.git

## 1. My original examination self-assessment

Review the code **as submitted in the original examination**. Do not change the original repository, branch or pull request. Check your own Group A or Group B examination paper and award marks only for evidence present in the original submission.

### Task 1 — /40
**My mark: 23** 

**Evidence from the original code (files, methods, working or missing requirements):**
What went right:
- Model was correctly done and is a real JPA entity with its relevant data entries.
- Repository was also done and initialized correctly.
- @NotBlank on the organiser name is correct for the "not blank" rule.
- The getters work (getAttendeeCount, getStatus, getOrgName, and Lombok's @Getter on roomId).
- It actually compiles

What went wrong: 
- some getters and setters are empty meaning bookings arent saved correctly 
- @Id shouldn't encompass attendeeCount



### Task 2 — 10 /40
**My mark:0**  
I was unable to progress to this task as there was a major SDK issue and IntelliJ kept locking me out of building and running. rendering me from running the code at all. 
Due to this i was not even able to check what was wrong with task 1 etc. This was a major handicap in the exam that definitely needs to get sorted for the next labs. 
What went right: 
- Repository was initialized correctly
- Service was written up properly and given time the imports that were unused would've been used correctly.

What went wrong:
- No Controller set up endpoints do not exist.
- No client was written
- function to save data locally to repository was never called.
### Task 3 — 3/20
**My mark:0**
 
Task 3 never reached due to same issues i mentioned in task 2 but the trajectory and foundations was set with the imports in the service class
yet a 3/20 would be too optimistic.

**Total self-assessed original examination mark: 36/100**

**Checkpoint:** Commit this completed self-assessment as the **first review commit in your new repository**, before you modify or add application code. The lecturer decides the official original examination mark.

## 2. Corrections and improvements in my new project

Start from a **fresh copy of the starter template for your own exam group**, in a **new GitHub repository**. You may consult or selectively reuse your own original examination code, but the new project must demonstrate that you understand and can correct it. Do not change your original examination submission.

Describe **two or three important problems** in your original code. For each, explain what was wrong, which class or method you changed in the new project, and why the corrected approach works.

**Problem and correction 1:**

In Booking Model @Id @GeneratedValue annotations were on attendeeCount so primary key was the number of attendees and no booking ID at all.
Two bookings with 18 attendees would collide. The setters also had empties bodies, so no value was ever actually stored.
Status was also capitalized which broke property name.

Booking package now has 
- @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id
- attendeeCount is an int
- lombok generated working getters and setters with @Data

Database now generates actual working unique IDs for every booking so save() creates a new row each time and findById has real key to search for.
Working setters mean the values I set before save() are actually stroed.

**Problem and correction 2:**

Bookingdto was a single class used for everything. It required bookingID and Status from the client, and it had no getters and setters.
@NotNull was on a primitive long instead of object Long so long can never fail. There was also no @Size(2, 60) on the name or @Positive on attendee count.
This did not meet validation rules in Task 1.

What changed:
- Created BookingRequest class with @NotNull Long roomId
- @NotBlank @Size(min = 2, max = 60) organiserName
- @Positive attendeeCount
- Created seperate BookingResponse for Output.

**Problem and correction 3 (optional):**

Previous submission had empty service, no controller and Feign client. No endpoint existed so no way of making or recieving booking.

What changed: 
- Created RoomClient 
- BookingService.create
- BookingController with POST/GET
## 3. API testing and evidence

Use Swagger UI or another HTTP client. For **at least four meaningful checks**, record the request/input, expected result, actual HTTP status/response, and pass/fail. Include unsuccessful cases; a running application alone is not enough.

**Test 1 — successful booking request:**
- Request / input:  `POST /api/bookings` with `{"roomId":1,"organiserName":"Aoife Byrne","attendeeCount":18}`
- Expected: 201 Created. Booking saved with status ACTIVE and ID generated.
- Actual HTTP status / body: ing saved with status ACTIVE and a generated ID
- Actual HTTP status / body: `201` `{"attendeeCount":18,"id":3,"organiserName":"Aoife Byrne","roomId":1,"status":"ACTIVE"}`
- Pass / fail: **PASS**

**Test 2 — invalid input or excessive attendee count:**
- Request / input: `POST /api/bookings` with `{"roomId":1,"organiserName":"Aoife Byrne","attendeeCount":30}`
- Expected: 400. 30 exceeds the room capacity of 24
- Actual HTTP status / body: 400 `{"status":400,"error":"Bad Request","message":"Attendee count 30 exceeds room capacity 24","path":"/api/bookings"}`
- Pass / fail: **PASS**

- Request / input: `POST /api/bookings` with `{"organiserName":"A","attendeeCount":0}`
- Expected: 400 from validation
- Actual HTTP status / body: `400` `{"status":400,"error":"Bad Request","message":"Validation failed for object='bookingRequest'. Error count: 3","path":"/api/bookings"}`
- Pass / fail: **PASS**

**Test 3 — missing booking or room:**
- Request / input: `GET /api/bookings/9999`
- Expected: 404 with meaningful message
- Actual HTTP status / body: `404` `{"status":404,"error":"Not Found","message":"Booking 9999 not found","path":"/api/bookings/9999"}`
- Pass / fail: **PASS**

- Request / input: `POST /api/bookings` with `{"roomId":999,"organiserName":"Aoife Byrne","attendeeCount":5}`
- Expected: 404. Room Service has no room 999
- Actual HTTP status / body: first run `500` (uncaught Feign exception). After fix: `BookingService.create`: `404` `{"status":404,"error":"Not Found","message":"Room 999 not found","path":"/api/bookings"}`
- Pass / fail: Case 1: **FAIL** Case 2:  **PASS**

**Test 4 — requirement specific to my group:**
- Request / input: `GET /api/bookings/1`
- Expected: 200
- Actual HTTP status / body:  `{"attendeeCount":18,"id":1,"organiserName":"Aoife Byrne","roomId":1,"status":"ACTIVE"}`
- Pass / fail: **PASS**

**Group A:** Demonstrate `GET /api/bookings/search?roomName=...` using an exact room name, bookings ordered by attendee count descending, and an empty list for no matches. Add checks if necessary to show all three behaviours.

**Group B:** Run the **provided** Room Service separately on port 8082 and the **new** Booking Service on port 8081. Demonstrate that booking creation obtains room details through OpenFeign and that booking retrieval uses the local Booking database. Do not modify the Room Service.

## 4. Reflection

**What did I understand during the examination but struggle to implement?**

Mainly Time constraint was my biggest struggle. also the SDK lock was also a major issue. 
From this i noted whether it was code issue or project creation issue.

**Which Spring Boot, JPA, validation, JPQL or OpenFeign concept became clearer after correcting my code? Give a specific example.**
 - Primitive vs Long 
 - validaion became clearer and how it works
 - Entity vs DTO on why response and requests should be in different classes.

**What will I do differently in my next coding test or API development task?**

- Check if the IDE is set up correctly before the exam
- Build in layers and run tests on each layer
- Smarter coding exam tactics

## Review lab assessment — 20 marks

- Honest original self-assessment with code evidence: **4 marks**
- Explanation of mistakes and Spring Boot concepts: **6 marks**
- Corrections in the new application: **4 marks**
- API testing and demonstration of understanding: **4 marks**
- Reflection and future improvement: **2 marks**

**Total: 20 marks.** Simply submitting a working solution does not demonstrate the learning required by this review.

## Proposed mark recovery — only if confirmed for CICD-1

If the same approach as OOP is adopted, the review could recover **up to 30% of marks lost** in the original examination, scaled by the review-lab mark.

**Marks recovered = (100 − official original exam mark) × 0.30 × (review lab mark ÷ 20)**

Example: exam **40/100** and review **16/20** → **14.4 recovered marks**, revised exam **54.4/100**. This is **not an approved CICD-1 policy unless confirmed by the lecturer**. The original official exam mark—not your self-awarded grade—is used in the calculation.
proposed mark after recovery: 40/100 minimum. 

## Submission

Commit and push the **new review repository** with the completed README, corrected Spring Boot code and API-test evidence. Submit its GitHub URL on Moodle. **Do not push any changes to the original examination repository or `exam-work` pull request.**