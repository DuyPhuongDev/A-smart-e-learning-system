# Features to Services Mapping

Tài liệu này map các features của hệ thống LMS với các microservices tương ứng.

## Admin Features

### 1. Manage Courses
**Services:**
- **Course Management Service** (Primary)
- **Analytics & Reporting Service** (Statistics)
- **User Management Service** (Instructor validation)

**Endpoints:**
- `GET /api/courses` - View all courses
- `GET /api/courses/{id}` - View course details
- `PUT /api/courses/{id}/approve` - Approve course
- `PUT /api/courses/{id}/reject` - Reject course
- `PUT /api/courses/{id}/unpublish` - Unpublish course

---

### 2. Manage Assignments/Quizzes
**Services:**
- **Assessment Management Service** (Primary)
- **Course Management Service** (Course context)

**Endpoints:**
- `GET /api/assessments` - View all assessments
- `PUT /api/assessments/{id}/approve` - Approve assessment
- `PUT /api/assessments/{id}/reject` - Reject assessment
- `GET /api/assessments/{id}/preview` - Preview assessment

---

### 3. View Reports
**Services:**
- **Analytics & Reporting Service** (Primary)
- **Tracking Service** (Data source)
- **Assessment Management Service** (Grading data)

**Endpoints:**
- `GET /api/analytics/reports/system` - System-wide reports
- `GET /api/analytics/statistics/grading` - Grading statistics
- `GET /api/analytics/statistics/user-activity` - User activity report
- `GET /api/analytics/reports/course/{courseId}` - Course completion rates

---

### 4. Create and Send Announcement
**Services:**
- **Notification Service** (Primary)
- **User Management Service** (Target users)

**Endpoints:**
- `POST /api/notifications/announcement` - Create announcement
- `POST /api/notifications/send` - Send notifications

---

### 5. View System Logs
**Services:**
- **Analytics & Reporting Service** (Primary)

**Endpoints:**
- `GET /api/analytics/logs/system` - View system logs

---

### 6. Manage Users
**Services:**
- **User Management Service** (Primary)

**Endpoints:**
- `GET /api/users` - List all users
- `POST /api/users` - Create user
- `PUT /api/users/{id}` - Edit user
- `DELETE /api/users/{id}` - Delete user
- `POST /api/users/{id}/lock` - Lock account
- `POST /api/users/{id}/unlock` - Unlock account
- `POST /api/users/import` - Import users
- `POST /api/users/export` - Export users
- `POST /api/users/{id}/roles` - Assign roles

---

## Teacher Features

### 1. Manage Courses
**Services:**
- **Course Management Service** (Primary)
- **User Management Service** (Instructor validation)

**Endpoints:**
- `POST /api/courses` - Create course
- `PUT /api/courses/{id}` - Update course
- `GET /api/courses/{id}` - View course info
- `POST /api/courses/{id}/structure` - Build course structure
- `POST /api/courses/{id}/grading-system` - Set up grading
- `POST /api/courses/{id}/materials` - Upload materials
- `PUT /api/courses/{id}/materials/{materialId}` - Update materials

---

### 2. Create Supplementary Course
**Services:**
- **Course Management Service** (Primary)

**Endpoints:**
- `POST /api/courses/supplementary` - Create from scratch
- `POST /api/courses/{id}/clone` - Create from old course

---

### 3. Create Assignment
**Services:**
- **Assessment Management Service** (Primary)
- **Course Management Service** (Course context)

**Endpoints:**
- `POST /api/assessments` - Create assignment
- `POST /api/assessments/ai-generate` - AI-assisted creation

---

### 4. Edit Assignment
**Services:**
- **Assessment Management Service** (Primary)

**Endpoints:**
- `PUT /api/assessments/{id}` - Edit assignment
- `GET /api/assessments/{id}/preview` - Preview interface

---

### 5. Grade and Give Feedback
**Services:**
- **Assessment Management Service** (Primary)
- **Assessment Execution Service** (Submissions)

**Endpoints:**
- `POST /api/assessments/{id}/grade` - Manual grading
- `POST /api/assessments/{id}/ai-grade` - AI-assisted grading
- `POST /api/assessments/{id}/feedback` - Provide feedback

---

### 6. Messaging and Discussion
**Services:**
- **Communication Service** (Primary)
- **Notification Service** (Message notifications)

**Endpoints:**
- `POST /api/communication/message` - Send message
- `GET /api/communication/messages/{userId}` - Get messages
- `POST /api/communication/forum` - Create forum
- `POST /api/communication/forum/{forumId}/post` - Create post

---

### 7. View Course Reports and Statistics
**Services:**
- **Analytics & Reporting Service** (Primary)
- **Tracking Service** (Progress data)

**Endpoints:**
- `GET /api/analytics/reports/course/{courseId}` - Course reports
- `GET /api/analytics/dashboard/teacher/{teacherId}` - Teacher dashboard

---

### 8. Export Reports
**Services:**
- **Analytics & Reporting Service** (Primary)

**Endpoints:**
- `POST /api/analytics/export/report/{reportId}?format=pdf` - Export as PDF
- `POST /api/analytics/export/report/{reportId}?format=excel` - Export as Excel
- `POST /api/analytics/export/report/{reportId}?format=csv` - Export as CSV

---

### 9. Build a Material Library
**Services:**
- **Course Management Service** (Primary)

**Endpoints:**
- `POST /api/courses/materials/library` - Add to library
- `GET /api/courses/materials/library` - View library
- `PUT /api/courses/materials/library/{id}` - Update material
- `DELETE /api/courses/materials/library/{id}` - Remove material

---

## Student Features

### 1. Personal Academic Tracking
**Services:**
- **Tracking Service** (Primary)
- **Student Personalization Service** (Goals)
- **Assessment Execution Service** (Submissions)

**Endpoints:**
- `GET /api/tracking/dashboard/{studentId}` - Personal dashboard
- `GET /api/tracking/progress/{studentId}` - Overall progress

---

### 2. Set Academic Goals
**Services:**
- **Student Personalization Service** (Primary)

**Endpoints:**
- `POST /api/personalization/goals` - Set goals
- `PUT /api/personalization/goals/{id}` - Update goals

---

### 3. Personalized Learning Path Recommendation
**Services:**
- **Student Personalization Service** (Primary)
- **Enrollment Service** (Course data)
- **Tracking Service** (Current status)

**Endpoints:**
- `POST /api/personalization/learning-path/generate` - Generate path with AI
- `GET /api/personalization/learning-path` - View path
- `GET /api/personalization/learning-path/scenarios` - Compare scenarios

---

### 4. View Learning Path
**Services:**
- **Student Personalization Service** (Primary)

**Endpoints:**
- `GET /api/personalization/learning-path` - View learning path

---

### 5. View Academic Schedule
**Services:**
- **Student Personalization Service** (Primary)

**Endpoints:**
- `GET /api/personalization/schedule` - View schedule
- `POST /api/personalization/schedule/import` - Import from BK Portal
- `POST /api/personalization/schedule/sync-calendar` - Sync with Google Calendar
- `POST /api/personalization/schedule/events` - Add custom events

---

### 6. Adjust Learning Path
**Services:**
- **Student Personalization Service** (Primary)
- **Tracking Service** (Current progress)

**Endpoints:**
- `PUT /api/personalization/learning-path/adjust` - Adjust path
- `POST /api/personalization/learning-path/detect-deviation` - Detect deviations

---

### 7. Discover Courses
**Services:**
- **Enrollment Service** (Primary)
- **Course Management Service** (Course data)

**Endpoints:**
- `GET /api/enrollment/discover` - Discover courses

---

### 8. Enroll in Course
**Services:**
- **Enrollment Service** (Primary)
- **Course Management Service** (Course validation)
- **Notification Service** (Confirmation)

**Endpoints:**
- `POST /api/enrollment/enroll` - Enroll in course

---

### 9. Search for Courses
**Services:**
- **Enrollment Service** (Primary)

**Endpoints:**
- `GET /api/enrollment/search?keyword=...&topic=...&level=...` - Search courses

---

### 10. Access Course Resources
**Services:**
- **Course Delivery Service** (Primary)
- **Course Management Service** (Course content)

**Endpoints:**
- `GET /api/delivery/course/{courseId}/materials` - Get materials
- `GET /api/delivery/course/{courseId}/structure` - View structure

---

### 11. View Learning Progress
**Services:**
- **Course Delivery Service** (Primary)
- **Tracking Service** (Progress data)

**Endpoints:**
- `GET /api/delivery/course/{courseId}/progress` - View course progress

---

### 12. Rate Course
**Services:**
- **Enrollment Service** (Primary)

**Endpoints:**
- `POST /api/enrollment/course/{id}/rate` - Rate course

---

### 13. Do Assignments and Quizzes
**Services:**
- **Assessment Execution Service** (Primary)
- **Assessment Management Service** (Assignment data)

**Endpoints:**
- `GET /api/execution/quiz/{id}` - Get quiz
- `POST /api/execution/quiz/{id}/submit` - Submit quiz
- `POST /api/execution/assignment/{id}/submit` - Submit assignment
- `POST /api/execution/code/{id}/execute` - Execute code

---

### 14. Look Up Grades and Feedback
**Services:**
- **Assessment Execution Service** (Primary)
- **Tracking Service** (Grade history)

**Endpoints:**
- `GET /api/execution/results/{id}` - Get results
- `GET /api/tracking/grades/{studentId}` - View all grades

---

### 15. Get Learning Support from AI Assistant
**Services:**
- **Course Delivery Service** (Primary)

**Endpoints:**
- `POST /api/delivery/ai/qa` - Ask questions to AI
- `POST /api/delivery/ai/explain-error` - Error analysis

---

### 16. Create Review Quizzes
**Services:**
- **Course Delivery Service** (Primary)

**Endpoints:**
- `POST /api/delivery/ai/quiz-generation` - Generate quiz from materials

---

### 17. Q&A on Course Knowledge
**Services:**
- **Course Delivery Service** (Primary)

**Endpoints:**
- `POST /api/delivery/ai/qa` - Ask course-related questions

---

### 18. Receive Notifications
**Services:**
- **Notification Service** (Primary)

**Endpoints:**
- `GET /api/notifications/{userId}` - Get notifications
- `PUT /api/notifications/{id}/read` - Mark as read

---

### 19. Customize Notifications
**Services:**
- **Notification Service** (Primary)

**Endpoints:**
- `GET /api/notifications/preferences/{userId}` - Get preferences
- `PUT /api/notifications/preferences/{userId}` - Update preferences

---

## Service Feature Count Summary

| Service | Primary Features Count |
|---------|------------------------|
| Student Personalization Service | 6 features |
| Tracking Service | 3 features |
| Enrollment Service | 5 features |
| Course Delivery Service | 6 features |
| Assessment Execution Service | 3 features |
| Course Management Service | 5 features |
| Assessment Management Service | 4 features |
| Analytics & Reporting Service | 4 features |
| User Management Service | 2 features |
| Notification Service | 3 features |
| Communication Service | 1 feature |
| Authentication Service | 0 direct features (supporting role) |

**Total Features Covered: 42+**

## Cross-Service Dependencies

### High Coupling Services
- **Course Management ↔ Enrollment Service**
- **Assessment Management ↔ Assessment Execution**
- **Student Personalization ↔ Tracking Service**

### Common Dependencies
- **All Services → Authentication Service** (Security)
- **All Services → Notification Service** (Notifications)
- **Most Services → Analytics & Reporting** (Data aggregation)

## Notes

- Các endpoints trên là cơ bản, cần implement chi tiết
- Một số features phức tạp cần nhiều services phối hợp
- AI features cần tích hợp external AI services
- Real-time features cần WebSocket support

