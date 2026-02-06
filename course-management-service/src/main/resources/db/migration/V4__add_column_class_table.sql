alter table course_management.class_sections add max_student int default -1;
alter table course_management.class_sections add current_student int default 0;

alter table course_management.lectures add estimate_time_spent int;