[ARO] Update Java Spring – API Testing via Swagger

ASIWANTSOTHAT

As a developer,
I want to update the Java Spring application and test the REST API using Swagger,
So that I can ensure the API endpoints function correctly and securely with token-based authentication.

⸻

Description

The Java Spring application must be updated, and API endpoints should be tested using Swagger UI. Authentication will be handled using a Bearer Token, which will be retrieved via cURL and entered in Swagger’s authorization section.

Request Actions:
	•	Update the Java Spring application with the necessary changes.
	•	Access Swagger UI to test the API.
	•	Retrieve the authentication token using cURL.
	•	Authorize in Swagger by entering the token.
	•	Test API endpoints by using “Try it out”, filling in parameters or request bodies, and executing requests.

⸻

Design

The process for testing the API after the Java Spring update:
	1.	Retrieve the authentication token using cURL:

curl -X POST "https://your-api-url/auth/token" -H "Content-Type: application/json" -d '{"username":"your_user","password":"your_password"}'

	•	Copy the access token from the response.

	2.	Authorize in Swagger UI:
	•	Open Swagger UI in the browser (https://your-api-url/swagger).
	•	Click on “Authorize”.
	•	Enter the token in the format:

Bearer your_access_token


	•	Click “Authorize” and then “Close”.

	3.	Test API Endpoints:
	•	Locate the API endpoint to test.
	•	Click “Try it out”.
	•	If the endpoint requires parameters, fill them in.
	•	If the endpoint requires a request body, enter the JSON payload.
	•	Click “Execute” to send the request.
	•	Review the response code and response body for validation.

⸻

Acceptance Criteria / BDD

Scenario: API Authentication & Endpoint Testing
	•	Given: The Java Spring API is updated and running.
	•	When: A user retrieves a token using cURL and enters it in Swagger’s Authorize section.
	•	And: The user selects an endpoint, clicks “Try it out”, fills in parameters or request body if required, and clicks “Execute”.
	•	Then: The API should return a valid response with expected data and status codes.

⸻

This document outlines the required updates and API testing steps in Swagger after updating the Java Spring application. Let me know if any modifications are needed!