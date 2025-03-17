[ARO] Access PREPROD – Add/Modify User Access for AROGEST

ASIWANTSOTHAT

As an AROGEST user,
I want to add, modify, and execute access for folders X1 and X2 in the PREPROD environment,
So that I can efficiently manage and process files related to these folders without manual intervention.

⸻

Description

Data access for AROGEST users in PREPROD must be updated to include permissions for folders X1 and X2.
This includes adding user access, modifying existing permissions, and allowing execution of necessary processes.

Additionally, we need to ensure that users can upload files to the specified paths without encountering permission errors.

Required Actions:
	•	Grant AROGEST users access to X1 and X2 folders in PREPROD.
	•	Enable modification and execution rights for the specified access.
	•	Automate the process to avoid manual adjustments.
	•	Ensure that users can successfully upload files to designated paths.

⸻

Design

The system should be configured to allow AROGEST users to:
	•	Access X1 and X2 folders in PREPROD.
	•	Modify and execute files within these folders.
	•	Upload files without authorization issues.

⸻

Acceptance Criteria / BDD

Scenario 1: Access Verification
	•	Given: User access is configured in PREPROD.
	•	When: An AROGEST user attempts to access X1 and X2 folders.
	•	Then: The user should be able to add, modify, and execute files in these folders.

Scenario 2: File Upload Verification
	•	Given: The user has access rights to X1 and X2 folders.
	•	When: They attempt to upload the file XX.XX to the path XXX.
	•	Then: The file should be successfully uploaded without any authorization errors.

⸻

This document outlines the required updates and expectations for ensuring AROGEST users can efficiently access and manage files in the PREPROD environment. Let me know if you need any refinements!