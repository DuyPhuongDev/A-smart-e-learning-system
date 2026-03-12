alter table course_management.subjects add category varchar(50);
alter table course_management.subjects add en_name varchar(255);

-- ============================================================
-- UPDATE en_name và category cho bảng subjects
-- ============================================================

-- GENERAL_EDUCATION: Toán - Lý - Hóa
UPDATE course_management.subjects SET en_name = 'Calculus 1',                          category = 'GENERAL_EDUCATION' WHERE id = 'ef92e266-2bba-45ea-b66d-f85662a5da19';
UPDATE course_management.subjects SET en_name = 'Calculus 2',                          category = 'GENERAL_EDUCATION' WHERE id = '701f6a2e-24a0-421f-9d95-e0790caf9c00';
UPDATE course_management.subjects SET en_name = 'Linear Algebra',                      category = 'GENERAL_EDUCATION' WHERE id = '512d5d59-c79e-4bd2-988e-3dde68e5fc4f';
UPDATE course_management.subjects SET en_name = 'Probability and Statistics',          category = 'GENERAL_EDUCATION' WHERE id = '596fc53b-4089-49ef-8050-aba1e8a2562a';
UPDATE course_management.subjects SET en_name = 'General Chemistry',                   category = 'GENERAL_EDUCATION' WHERE id = '3fbb3896-e1c2-4224-bc2c-e1262fa9a9be';
UPDATE course_management.subjects SET en_name = 'Physics 1',                           category = 'GENERAL_EDUCATION' WHERE id = '9f079971-2acd-404d-b520-43d678494b9c';
UPDATE course_management.subjects SET en_name = 'Physics Laboratory',                  category = 'GENERAL_EDUCATION' WHERE id = '75038653-b2ee-48e7-8a63-2a5b6ffdeecf';

-- GENERAL_EDUCATION: Chính trị - Pháp luật
UPDATE course_management.subjects SET en_name = 'Introduction to Vietnamese Law',                  category = 'GENERAL_POLITICS_ECONOMICS_SOCIETY' WHERE id = '973e813a-993b-41b0-9379-d693fb35efbc';
UPDATE course_management.subjects SET en_name = 'Marxist-Leninist Philosophy',                     category = 'GENERAL_POLITICS_ECONOMICS_SOCIETY' WHERE id = 'd03ed994-a6da-4ee3-b8f2-f53f184bba0a';
UPDATE course_management.subjects SET en_name = 'Marxist-Leninist Political Economy',              category = 'GENERAL_POLITICS_ECONOMICS_SOCIETY' WHERE id = 'b79206eb-e1eb-4035-b683-14b395d4b583';
UPDATE course_management.subjects SET en_name = 'Scientific Socialism',                            category = 'GENERAL_POLITICS_ECONOMICS_SOCIETY' WHERE id = 'f52bedb4-6c58-4a15-92b0-bb14266fe08f';
UPDATE course_management.subjects SET en_name = 'Ho Chi Minh Thought',                             category = 'GENERAL_POLITICS_ECONOMICS_SOCIETY' WHERE id = '1d215d0d-bf85-4fa0-b8a8-fc4d49368fd2';
UPDATE course_management.subjects SET en_name = 'History of the Communist Party of Vietnam',       category = 'GENERAL_POLITICS_ECONOMICS_SOCIETY' WHERE id = 'ccb3a8f9-8a2e-416d-ba9e-1e1e668b6d3c';
UPDATE course_management.subjects SET en_name = 'National Defense Education',                      category = 'GENERAL_POLITICS_ECONOMICS_SOCIETY' WHERE id = '6fa2052f-4e6a-4675-9043-84013703c9fa';

-- GENERAL_EDUCATION: Anh văn
UPDATE course_management.subjects SET en_name = 'English 1', category = 'GENERAL_FOREIGN_LANGUAGE' WHERE id = '55ad7379-2a32-40e5-82eb-50965a6ee49f';
UPDATE course_management.subjects SET en_name = 'English 2', category = 'GENERAL_FOREIGN_LANGUAGE' WHERE id = 'a198923b-9238-48f1-bea9-05ee74c5cdbf';
UPDATE course_management.subjects SET en_name = 'English 3', category = 'GENERAL_FOREIGN_LANGUAGE' WHERE id = 'c7931c47-fd00-4109-a7dd-304a7704d5bd';
UPDATE course_management.subjects SET en_name = 'English 4', category = 'GENERAL_FOREIGN_LANGUAGE' WHERE id = 'b72713a2-69df-4f22-892f-fff24b964246';

-- GENERAL_EDUCATION: Kinh tế - Quản lý (IM, ME)
UPDATE course_management.subjects SET en_name = 'Introduction to Economics',               category = 'GENERAL_EDUCATION' WHERE id = '5c673401-1580-4e62-b22d-cdc6fcd50207';
UPDATE course_management.subjects SET en_name = 'Production Management for Engineers',     category = 'GENERAL_EDUCATION' WHERE id = 'c09d5f7e-9469-416f-acdc-aa3eec93b859';
UPDATE course_management.subjects SET en_name = 'Project Management for Engineers',        category = 'GENERAL_EDUCATION' WHERE id = '11525936-d414-47f4-9787-fb6cd7df98e1';
UPDATE course_management.subjects SET en_name = 'Engineering Economics',                   category = 'GENERAL_EDUCATION' WHERE id = 'd612fe07-1275-4345-a2d6-a95aaf2fcb98';
UPDATE course_management.subjects SET en_name = 'Business Administration for Engineers',   category = 'GENERAL_EDUCATION' WHERE id = 'f880fa3b-ce7d-4d0c-96d4-9141c4b58ee5';
UPDATE course_management.subjects SET en_name = 'Entrepreneurship and Innovation',         category = 'GENERAL_EDUCATION' WHERE id = 'ee98d417-7e91-44c5-9532-241afbcf4474';
UPDATE course_management.subjects SET en_name = 'Productivity and Quality Management',     category = 'GENERAL_EDUCATION' WHERE id = '6a7ea2cf-9c07-4996-9ac3-3705d019d79a';

-- GENERAL_EDUCATION: Thể dục (PE - Học phần 1)
UPDATE course_management.subjects SET en_name = 'Football (Part 1)',       category = 'PHYSICAL_EDUCATION' WHERE id = 'bc8d6329-5098-4195-a416-2510457ff7f9';
UPDATE course_management.subjects SET en_name = 'Volleyball (Part 1)',     category = 'PHYSICAL_EDUCATION' WHERE id = '11d0d89b-e0f1-4178-ae5a-8c141b520565';
UPDATE course_management.subjects SET en_name = 'Table Tennis (Part 1)',   category = 'PHYSICAL_EDUCATION' WHERE id = '7e1a215e-5fb9-4792-9dda-6d70a27e2933';
UPDATE course_management.subjects SET en_name = 'Basketball (Part 1)',     category = 'PHYSICAL_EDUCATION' WHERE id = '3c1e047e-92c8-4a25-a6fa-e95f7b6dd3b5';
UPDATE course_management.subjects SET en_name = 'Badminton (Part 1)',      category = 'PHYSICAL_EDUCATION' WHERE id = '3b815144-f887-4651-b940-b2daaf71a2e1';
UPDATE course_management.subjects SET en_name = 'Swimming (Part 1)',       category = 'PHYSICAL_EDUCATION' WHERE id = '752b0c32-8a74-4ec6-9918-3bbb2be1e7d5';
UPDATE course_management.subjects SET en_name = 'Aerobics (Part 1)',       category = 'PHYSICAL_EDUCATION' WHERE id = 'fd802e18-0512-4592-967b-bf42d2d1eaf1';
UPDATE course_management.subjects SET en_name = 'Athletics (Part 1)',      category = 'PHYSICAL_EDUCATION' WHERE id = '2534835f-d644-4639-b118-7dbd0d3f0c42';
UPDATE course_management.subjects SET en_name = 'Tennis (Part 1)',         category = 'PHYSICAL_EDUCATION' WHERE id = '4b351c0d-a6c9-41cc-96ef-4530ae9bdf71';
UPDATE course_management.subjects SET en_name = 'Chess (Part 1)',          category = 'PHYSICAL_EDUCATION' WHERE id = 'a30f9843-2529-47c5-9729-269ed8d440a2';
UPDATE course_management.subjects SET en_name = 'Bowling (Part 1)',        category = 'PHYSICAL_EDUCATION' WHERE id = 'beceef3f-6260-4930-9b2f-bafe5ba6b77b';
UPDATE course_management.subjects SET en_name = 'Pickleball (Part 1)',     category = 'PHYSICAL_EDUCATION' WHERE id = '533ad551-b27c-431e-91b7-130947ae7478';

-- GENERAL_EDUCATION: Thể dục (PE - Học phần 2)
UPDATE course_management.subjects SET en_name = 'Football (Part 2)',       category = 'PHYSICAL_EDUCATION' WHERE id = '8562b5e7-48e7-4788-865f-db21497e86fd';
UPDATE course_management.subjects SET en_name = 'Volleyball (Part 2)',     category = 'PHYSICAL_EDUCATION' WHERE id = '8a4641cb-04ad-4b24-9aa9-63487f4b4349';
UPDATE course_management.subjects SET en_name = 'Table Tennis (Part 2)',   category = 'PHYSICAL_EDUCATION' WHERE id = '3a581b26-5b2b-4e86-9e4a-8a5132917e26';
UPDATE course_management.subjects SET en_name = 'Basketball (Part 2)',     category = 'PHYSICAL_EDUCATION' WHERE id = '3516b429-9278-4e5b-8fd6-1254f457d289';
UPDATE course_management.subjects SET en_name = 'Badminton (Part 2)',      category = 'PHYSICAL_EDUCATION' WHERE id = '7128ee2d-6157-440c-ae15-03073f1736d0';
UPDATE course_management.subjects SET en_name = 'Swimming (Part 2)',       category = 'PHYSICAL_EDUCATION' WHERE id = 'a62b6ebd-3536-406f-8a44-377c8091395d';
UPDATE course_management.subjects SET en_name = 'Aerobics (Part 2)',       category = 'PHYSICAL_EDUCATION' WHERE id = '5a5a6b97-ad40-4f42-b77d-de2e03e9ccdf';
UPDATE course_management.subjects SET en_name = 'Athletics (Part 2)',      category = 'PHYSICAL_EDUCATION' WHERE id = '8e853531-7d0d-4f06-af77-cfab8f65129f';
UPDATE course_management.subjects SET en_name = 'Tennis (Part 2)',         category = 'PHYSICAL_EDUCATION' WHERE id = 'f42f885f-111f-4411-bffd-e2a85c5c752e';
UPDATE course_management.subjects SET en_name = 'Chess (Part 2)',          category = 'PHYSICAL_EDUCATION' WHERE id = '7919774c-a028-4aca-a120-dd185673e071';
UPDATE course_management.subjects SET en_name = 'Bowling (Part 2)',        category = 'PHYSICAL_EDUCATION' WHERE id = '6862c95d-5180-4df4-b0d2-0b0b17a659f4';
UPDATE course_management.subjects SET en_name = 'Pickleball (Part 2)',     category = 'PHYSICAL_EDUCATION' WHERE id = '55de69ec-db6e-42f4-91f3-ee97774331ae';

-- ============================================================
-- MAJOR_FOUNDATION: Cơ sở ngành (CO1xxx, CO2xxx)
-- ============================================================
UPDATE course_management.subjects SET en_name = 'Introduction to Computing',                       category = 'MAJOR_FOUNDATION' WHERE id = 'b597e11c-9d87-4d5d-baa0-dac27931c2b2';
UPDATE course_management.subjects SET en_name = 'Discrete Structures for Computer Science',        category = 'MAJOR_FOUNDATION' WHERE id = 'c5644ef2-38fe-4257-a254-ea2799157986';
UPDATE course_management.subjects SET en_name = 'Digital Systems',                                 category = 'MAJOR_FOUNDATION' WHERE id = '9ae43407-96ef-447b-a889-2a5739bef13b';
UPDATE course_management.subjects SET en_name = 'Programming Techniques',                          category = 'MAJOR_FOUNDATION' WHERE id = '2b362369-d160-47b6-9193-4d179a119a09';
UPDATE course_management.subjects SET en_name = 'Professional Skills for Engineers',               category = 'MAJOR_FOUNDATION' WHERE id = '6a0d14c8-042e-4163-a275-0410c3188861';
UPDATE course_management.subjects SET en_name = 'Data Structures and Algorithms',                  category = 'MAJOR_FOUNDATION' WHERE id = 'a54d2bba-015c-4288-8c55-f4e292b383a5';
UPDATE course_management.subjects SET en_name = 'Computer Architecture',                           category = 'MAJOR_FOUNDATION' WHERE id = '16360a54-c270-4d66-89ee-a066b2574af8';
UPDATE course_management.subjects SET en_name = 'Mathematical Modeling',                           category = 'MAJOR_FOUNDATION' WHERE id = '1dfe3403-3306-476c-8d70-2f790427a243';
UPDATE course_management.subjects SET en_name = 'Database Systems',                                category = 'MAJOR_FOUNDATION' WHERE id = 'cbda091c-b964-4333-b4cd-0f78bd3de070';
UPDATE course_management.subjects SET en_name = 'Operating Systems',                               category = 'MAJOR_FOUNDATION' WHERE id = '069bae62-0480-40d2-ab17-714f230b4126';
UPDATE course_management.subjects SET en_name = 'Advanced Programming',                            category = 'MAJOR_FOUNDATION' WHERE id = '70d1e20f-1af6-439a-b258-66af94ad5f90';

-- ============================================================
-- MAJOR_SPECIALIZATION: Chuyên ngành (CO3xxx, CO4xxx)
-- ============================================================
UPDATE course_management.subjects SET en_name = 'Software Engineering',                                        category = 'MAJOR_SPECIALIZATION' WHERE id = 'f3f3eaae-2310-4d60-b63e-2166b96e4bad';
UPDATE course_management.subjects SET en_name = 'Principles of Programming Languages',                         category = 'MAJOR_SPECIALIZATION' WHERE id = '890f3437-6ecb-489f-9a06-4a924576d801';
UPDATE course_management.subjects SET en_name = 'Computer Networks',                                           category = 'MAJOR_SPECIALIZATION' WHERE id = '19dfd8ae-132b-4bd3-b2f3-02b932c44688';
UPDATE course_management.subjects SET en_name = 'Database Management Systems',                                 category = 'MAJOR_SPECIALIZATION' WHERE id = '1a8706de-a53e-4b08-a83e-61c32379aa2b';
UPDATE course_management.subjects SET en_name = 'Electronic Commerce',                                         category = 'MAJOR_SPECIALIZATION' WHERE id = 'e10de3f9-ca4c-4459-ba0c-0ddca0d075e4';
UPDATE course_management.subjects SET en_name = 'Data Mining',                                                 category = 'MAJOR_SPECIALIZATION' WHERE id = 'a09976b2-3a87-4100-9c2b-f92b9e07ed3c';
UPDATE course_management.subjects SET en_name = 'Information System Security',                                 category = 'MAJOR_SPECIALIZATION' WHERE id = 'b6c62cf6-e6ac-4604-a647-3b6a1d86085c';
UPDATE course_management.subjects SET en_name = 'Systems Analysis and Design',                                 category = 'MAJOR_SPECIALIZATION' WHERE id = '05fd11b4-423e-409a-9536-8c895e9fdd40';
UPDATE course_management.subjects SET en_name = 'Data Warehouse and Decision Support Systems',                 category = 'MAJOR_SPECIALIZATION' WHERE id = '9f84d728-b235-45c2-92eb-ed0b59ea40f6';
UPDATE course_management.subjects SET en_name = 'Big Data Analytics and Business Intelligence',                category = 'MAJOR_SPECIALIZATION' WHERE id = '423df3dc-27d2-4a80-870e-4d5117e0e9fc';
UPDATE course_management.subjects SET en_name = 'Enterprise Resource Planning',                                category = 'MAJOR_SPECIALIZATION' WHERE id = '288693e9-40cf-4ccd-9a37-398b878d4d08';
UPDATE course_management.subjects SET en_name = 'Management Information Systems',                              category = 'MAJOR_SPECIALIZATION' WHERE id = '3f755a3a-5011-4394-9cb4-3e0b2ac5a585';
UPDATE course_management.subjects SET en_name = 'Biometric Security',                                         category = 'MAJOR_SPECIALIZATION' WHERE id = '1562eca9-13a6-4965-a196-c87daccf2e5d';
UPDATE course_management.subjects SET en_name = 'Digital Transformation',                                     category = 'MAJOR_SPECIALIZATION' WHERE id = '9c4ac3d9-1c02-4f34-ba01-ee1c2503857b';
UPDATE course_management.subjects SET en_name = 'Big Data',                                                    category = 'MAJOR_SPECIALIZATION' WHERE id = 'af5d62bd-526c-49d4-8b5c-7187a009e935';
UPDATE course_management.subjects SET en_name = 'Software Project Management',                                 category = 'MAJOR_SPECIALIZATION' WHERE id = '1511f96d-1a30-416d-a8ed-67aa9137bdeb';
UPDATE course_management.subjects SET en_name = 'Software Security',                                          category = 'MAJOR_SPECIALIZATION' WHERE id = '88419bc2-4c7f-43bd-ab6e-3efe07d3876b';
UPDATE course_management.subjects SET en_name = 'Next-Generation Software Engineering',                        category = 'MAJOR_SPECIALIZATION' WHERE id = '18d918b5-f428-4583-bb11-167eaa114b51';
UPDATE course_management.subjects SET en_name = 'Software Testing',                                           category = 'MAJOR_SPECIALIZATION' WHERE id = 'da17c133-cccc-4809-9502-963795c41db7';
UPDATE course_management.subjects SET en_name = 'Software Architecture',                                      category = 'MAJOR_SPECIALIZATION' WHERE id = 'e50bd2f0-f37b-4a9a-b456-1b916a2e7cba';
UPDATE course_management.subjects SET en_name = 'Advanced Software Engineering',                               category = 'MAJOR_SPECIALIZATION' WHERE id = 'bf683157-89cc-4242-85c5-243e86d3bbca';
UPDATE course_management.subjects SET en_name = 'Intelligent Systems',                                        category = 'MAJOR_SPECIALIZATION' WHERE id = '27ce0cb2-64bb-4cad-bfc3-dfa895155198';
UPDATE course_management.subjects SET en_name = 'Web Programming',                                            category = 'MAJOR_SPECIALIZATION' WHERE id = 'fa94c7f9-289a-4e03-8b61-c209ed2862f6';
UPDATE course_management.subjects SET en_name = 'Programming for AI and Data Science',                        category = 'MAJOR_SPECIALIZATION' WHERE id = '690b987f-44ad-42e9-80ef-a54f12572a36';
UPDATE course_management.subjects SET en_name = 'Advanced Topics in Computer Science',                        category = 'MAJOR_SPECIALIZATION' WHERE id = '526c961f-0a86-4f4a-bddf-211a5d107256';
UPDATE course_management.subjects SET en_name = 'Network Administration',                                     category = 'MAJOR_SPECIALIZATION' WHERE id = 'a2b19f97-90c2-468f-b474-152d5ef6c526';
UPDATE course_management.subjects SET en_name = 'Mobile Device Systems',                                      category = 'MAJOR_SPECIALIZATION' WHERE id = '41d3bb67-aa49-4b0b-9080-1e74bea77d61';
UPDATE course_management.subjects SET en_name = 'Cryptography and Network Security',                          category = 'MAJOR_SPECIALIZATION' WHERE id = '77802b08-da40-444e-8fe6-e8a3ce344462';
UPDATE course_management.subjects SET en_name = 'Computer Network Security Assessment',                       category = 'MAJOR_SPECIALIZATION' WHERE id = 'd93cd4da-0343-417c-9900-30273a6b0e9c';
UPDATE course_management.subjects SET en_name = 'Introduction to Artificial Intelligence',                    category = 'MAJOR_SPECIALIZATION' WHERE id = '4ab4a7cc-ff48-464d-ac86-b089a8f13f1b';
UPDATE course_management.subjects SET en_name = 'Machine Learning',                                           category = 'MAJOR_SPECIALIZATION' WHERE id = 'fc3ac3de-2708-4fde-9809-a7df9620aa52';
UPDATE course_management.subjects SET en_name = 'Deep Learning and Applications',                             category = 'MAJOR_SPECIALIZATION' WHERE id = 'a1777d5f-2f00-4a21-80c4-14624d8473e6';
UPDATE course_management.subjects SET en_name = 'Natural Language Processing',                                category = 'MAJOR_SPECIALIZATION' WHERE id = 'd8c186bf-998f-4c80-8078-90d7f7854995';
UPDATE course_management.subjects SET en_name = 'Digital Image Processing and Computer Vision',               category = 'MAJOR_SPECIALIZATION' WHERE id = '684fed78-99e0-44b9-b59d-cd5c5ad3ac7d';
UPDATE course_management.subjects SET en_name = 'Game Programming',                                           category = 'MAJOR_SPECIALIZATION' WHERE id = 'f81d8645-fc39-4856-8191-170faca76fc6';
UPDATE course_management.subjects SET en_name = 'Computer Graphics',                                          category = 'MAJOR_SPECIALIZATION' WHERE id = '3d6b4ec7-a46b-4298-84e6-a2a5951ce3e1';
UPDATE course_management.subjects SET en_name = 'Parallel Computing',                                         category = 'MAJOR_SPECIALIZATION' WHERE id = '8b0ca8ad-4616-4a4e-aee0-bb1392ba6872';
UPDATE course_management.subjects SET en_name = 'Internet of Things Application Development',                 category = 'MAJOR_SPECIALIZATION' WHERE id = '9d15d61c-622d-45b2-9621-b015287278f1';
UPDATE course_management.subjects SET en_name = 'Mobile Application Development',                             category = 'MAJOR_SPECIALIZATION' WHERE id = 'feeb194b-eeba-4524-983b-e834797bb7d5';
-- Đồ án tổng hợp / Thực tập đồ án (CO3xxx)
UPDATE course_management.subjects SET en_name = 'Capstone Project - AI Track',                                category = 'MAJOR_SPECIALIZATION' WHERE id = '867a000b-5d73-40a0-807e-71ee531ec12f';
UPDATE course_management.subjects SET en_name = 'Capstone Project - Software Engineering Track',              category = 'MAJOR_SPECIALIZATION' WHERE id = '3ebbf163-bf48-4c65-b03a-d2e7b322a8d5';
UPDATE course_management.subjects SET en_name = 'Capstone Project - Information Systems Track',               category = 'MAJOR_SPECIALIZATION' WHERE id = '899d75b7-abe2-4bd7-b590-63a25a0a6dca';
UPDATE course_management.subjects SET en_name = 'Capstone Project - Data Engineering Track',                  category = 'MAJOR_SPECIALIZATION' WHERE id = '0bcb84da-c690-476c-bd74-4b31f24646bc';
UPDATE course_management.subjects SET en_name = 'Computer Networks Project',                                  category = 'MAJOR_SPECIALIZATION' WHERE id = '615e6ed3-318d-4283-be4b-d8662b101111';
UPDATE course_management.subjects SET en_name = 'Multidisciplinary Project Practice - AI Track',              category = 'MAJOR_SPECIALIZATION' WHERE id = 'ee6e346e-0e30-4e99-ad25-c40b7a083cf8';
UPDATE course_management.subjects SET en_name = 'Multidisciplinary Project Practice - SE Track',              category = 'MAJOR_SPECIALIZATION' WHERE id = 'b31762b1-7876-4fbd-abe8-6c0e6a091248';
UPDATE course_management.subjects SET en_name = 'Multidisciplinary Project Practice - IS Track',              category = 'MAJOR_SPECIALIZATION' WHERE id = 'b5bc04fb-3856-4e77-ae33-7d13ea6dff02';

-- ============================================================
-- GRADUATION_REQUIREMENT: Thực tập & Đồ án tốt nghiệp
-- ============================================================
UPDATE course_management.subjects SET en_name = 'Internship',                          category = 'GRADUATION_REQUIREMENT' WHERE id = '00ab48fd-1190-4dd5-ac0c-4bf92ec5b16c';
UPDATE course_management.subjects SET en_name = 'Major Project',                       category = 'GRADUATION_REQUIREMENT' WHERE id = 'dd1e558b-1b68-4574-b2f7-7954cad071b2';
UPDATE course_management.subjects SET en_name = 'Graduation Thesis (Computer Science)',category = 'GRADUATION_REQUIREMENT' WHERE id = 'd1e05f1d-c007-4b1d-81ff-d4d27b197a25';