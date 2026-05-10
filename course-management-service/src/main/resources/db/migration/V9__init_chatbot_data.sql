INSERT INTO course_management.lectures (id,order_index,title,description,is_mandatory,lecture_type,completion_rate,view_count,allow_preview,is_downloadable,chapter_id,estimate_time_spent,created_at,updated_at) VALUES
	 ('b77e943d-ac1b-4e01-b614-53c501344569'::uuid,4,'Các ứng dụng liên quan đến chương trình dịch','',false,'VIDEO',0.0,0,true,false,'a96d6b9e-00e1-4275-9642-30d0909461d4'::uuid,NULL,'2026-03-19 17:10:47.055634+07','2026-03-19 17:10:47.055634+07'),
	 ('74e22a79-6b77-413f-9053-f6f79566226b'::uuid,7,'US WILL LOSE Iran War','',false,'VIDEO',0.0,0,true,false,'a96d6b9e-00e1-4275-9642-30d0909461d4'::uuid,NULL,'2026-03-19 17:15:50.61209+07','2026-03-19 17:15:50.61209+07'),
	 ('febe6123-a925-44cf-a109-aefc7cf61420'::uuid,6,'Đồ án','',false,'DOCUMENT',0.0,0,true,true,'a96d6b9e-00e1-4275-9642-30d0909461d4'::uuid,NULL,'2026-03-19 17:15:51.898884+07','2026-03-19 17:15:51.898884+07'),
	 ('9c1e8ddc-00f9-4393-90b8-5400e63c3b36'::uuid,5,'Các giai đoạn của một trình biên dịch','',false,'VIDEO',0.0,0,true,false,'a96d6b9e-00e1-4275-9642-30d0909461d4'::uuid,NULL,'2026-03-19 17:15:53.38484+07','2026-03-19 17:15:53.38484+07'),
	 ('5655cc4d-1de7-4263-9950-dfa39500d821'::uuid,8,'Tổng Quan Về Data Engineering (Kỹ Sư Dữ Liệu)','',false,'TEXT',0.0,0,true,false,'a96d6b9e-00e1-4275-9642-30d0909461d4'::uuid,NULL,'2026-03-19 17:25:05.190448+07','2026-03-19 17:25:05.190448+07');

INSERT INTO course_management.document_lectures (lecture_id,file_url,num_pages,file_format) VALUES
    ('febe6123-a925-44cf-a109-aefc7cf61420'::uuid,'https://d14s8phoypchnb.cloudfront.net/documents/f7851ec0-db43-4a09-859a-6e4d49839f4e-hk251-dagd1-048_2212703_2212651_2213951.pdf',NULL,NULL);


INSERT INTO course_management.video_lectures (lecture_id,video_url,duration) VALUES
	 ('b77e943d-ac1b-4e01-b614-53c501344569'::uuid,'https://d14s8phoypchnb.cloudfront.net/videos/e4e33917-6a4d-4002-a310-b0fc9b1a92c9-cac-ung-dung-lien-quan-den-chuong-trinh-dich.mp4',0),
	 ('74e22a79-6b77-413f-9053-f6f79566226b'::uuid,'https://d14s8phoypchnb.cloudfront.net/videos/d82505ca-5e34-496b-98ac-bd9d5824f1f7-us-will-lose-iran-war.mp4',0),
	 ('9c1e8ddc-00f9-4393-90b8-5400e63c3b36'::uuid,'https://d14s8phoypchnb.cloudfront.net/videos/6808153e-6e66-45ba-82e3-0f3b2abf4e79-cac-giai-oan-cua-mot-trinh-bien-dich.mp4',0);

INSERT INTO course_management.text_lectures (lecture_id,"content",word_count,format_type) VALUES
	 ('5655cc4d-1de7-4263-9950-dfa39500d821'::uuid,'# Bài Giảng: Tổng Quan Về Data Engineering (Kỹ Sư Dữ Liệu)

Data Engineering là xương sống của mọi hệ thống dữ liệu hiện đại. Không có dữ liệu sạch và dễ truy cập, các nhà phân tích hay nhà khoa học dữ liệu không thể làm việc.

---

## 1. Data Engineer là gì?

**Data Engineer** (*Kỹ sư dữ liệu*) là những người chịu trách nhiệm thiết kế, xây dựng, bảo trì và tối ưu hóa hạ tầng dữ liệu. Họ đảm bảo rằng dữ liệu từ nhiều nguồn khác nhau được thu thập một cách trơn tru, được làm sạch, biến đổi và lưu trữ an toàn để các bộ phận khác (Data Analyst, Data Scientist) có thể sử dụng dễ dàng.

## 2. Trách nhiệm chính của một Data Engineer

Công việc hàng ngày của một Kỹ sư dữ liệu thường xoay quanh các nhiệm vụ cốt lõi sau:

- **Thiết kế Data Pipeline (Đường ống dữ liệu):** Xây dựng hệ thống tự động hóa việc di chuyển dữ liệu từ hệ thống nguồn đến hệ thống đích.
- **Thực hiện quy trình ETL/ELT:** - *Extract* (Trích xuất): Lấy dữ liệu từ các nguồn (API, Database, File log).
  - *Transform* (Biến đổi): Làm sạch, lọc và chuẩn hóa dữ liệu.
  - *Load* (Tải): Đưa dữ liệu vào kho lưu trữ.
- **Quản lý Data Warehouse và Data Lake:** Thiết kế kiến trúc lưu trữ dữ liệu tập trung, tối ưu hóa tốc độ truy vấn.
- **Đảm bảo chất lượng và bảo mật:** Giám sát hệ thống để tránh mất mát dữ liệu và tuân thủ các quy định bảo mật.

## 3. Kỹ năng và Công nghệ cốt lõi

Để trở thành một Data Engineer giỏi, bạn cần nắm vững một hệ sinh thái công nghệ đa dạng:

* **Ngôn ngữ lập trình:** Python, Java, Scala.
    * *Ví dụ một đoạn code Python cơ bản để đọc dữ liệu:*
        ```python
        import pandas as pd
        df = pd.read_csv(''data.csv'')
        print(df.head())
        ```
* **Hệ quản trị cơ sở dữ liệu (DBMS):** * SQL (PostgreSQL, MySQL, SQL Server) để xử lý dữ liệu có cấu trúc.
    * NoSQL (MongoDB, Cassandra) cho dữ liệu phi cấu trúc.
* **Công cụ xử lý Big Data:** Apache Spark, Hadoop, Apache Kafka, Flink.
* **Nền tảng Cloud Computing:** AWS (Redshift, S3), Google Cloud Platform (BigQuery), Microsoft Azure.

## 4. Phân biệt Data Engineer, Data Analyst và Data Scientist

| Vai trò | Mục tiêu chính | Công cụ thường dùng |
| :--- | :--- | :--- |
| **Data Engineer** | Xây dựng hạ tầng, chuẩn bị và làm sạch dữ liệu. | SQL, Python, Spark, Cloud, Airflow |
| **Data Analyst** | Trực quan hóa dữ liệu, báo cáo, tìm ra *insights* kinh doanh. | SQL, Excel, Tableau, PowerBI |
| **Data Scientist**| Dự đoán tương lai, xây dựng mô hình Machine Learning. | Python, R, TensorFlow, Scikit-learn |

## 5. Tài liệu tham khảo thêm

Nếu bạn muốn bắt đầu con đường này, lộ trình học tập là vô cùng quan trọng. Bạn có thể tham khảo lộ trình chi tiết tại đây:
[Roadmap dành cho Data Engineer](https://roadmap.sh/data-engineer)

---',528,'MARKDOWN');