INSERT INTO coaching_chatbot.lecture_knowledge (lecture_knowledge_id,sync_status,last_synced_at,error_message,embedding_model,content_type,total_chunks,created_at,updated_at) VALUES
	 ('b77e943d-ac1b-4e01-b614-53c501344569'::uuid,'COMPLETED','2026-03-21 13:29:32.522643+07',NULL,'gemini-text-embedding-004','VIDEO',5,'2026-03-21 13:28:28.984167+07','2026-03-21 13:29:32.532156+07'),
	 ('9c1e8ddc-00f9-4393-90b8-5400e63c3b36'::uuid,'COMPLETED','2026-03-21 13:29:37.720487+07',NULL,'gemini-text-embedding-004','VIDEO',7,'2026-03-21 13:28:06.240292+07','2026-03-21 13:29:37.733071+07'),
	 ('74e22a79-6b77-413f-9053-f6f79566226b'::uuid,'COMPLETED','2026-03-21 13:31:20.157354+07',NULL,'gemini-text-embedding-004','VIDEO',10,'2026-03-21 13:29:02.438897+07','2026-03-21 13:31:20.170251+07'),
	 ('5655cc4d-1de7-4263-9950-dfa39500d821'::uuid,'COMPLETED','2026-03-21 13:35:24.128059+07',NULL,'gemini-text-embedding-004','TEXT',2,'2026-03-21 13:35:23.055778+07','2026-03-21 13:35:24.134518+07'),
	 ('febe6123-a925-44cf-a109-aefc7cf61420'::uuid,'COMPLETED','2026-03-21 13:37:00.427715+07',NULL,'gemini-text-embedding-004','DOCUMENT',273,'2026-03-21 13:35:13.306847+07','2026-03-21 13:37:01.022142+07');


INSERT INTO coaching_chatbot.lecture_knowledge_chunks (lecture_knowledge_chunks_id,lecture_knowledge_id,chunk_index,chunk_content,qdrant_point_id,start_time_seconds,end_time_seconds,page_number,token_count,created_at) VALUES
	 ('05eb5e57-4fd0-4518-8956-af939a681031'::uuid,'b77e943d-ac1b-4e01-b614-53c501344569'::uuid,0,'=== ORIGINAL TRANSCRIPT ===
như vậy thì thầy mới vừa giới thiệu cho các bạn xong về những quá trình làm compiler như vậy thì với việc các bạn học một quá trình làm compiler thì nó sẽ có những ứng dụng gì mà các bạn có thể làm với bạn áp dụng vào trong lĩnh vực công nghệ thông tin của chúng ta Thứ nhất là công nghệ làm compiler ngày nay nó sẽ có khả năng là Nó sẽ có khả năng tạo ra những ngôn ngữ lập trình cấp cao Mà ngày nay chúng ta thấy càng ngay càng xuất hiện những ngôn ngữ lập trình hay những phiên bản của các ngôn ngữ lập trình tư hơn Nếu không có lĩnh vực công nghệ về Compiler thì chắc chắn rằng sẽ không có những cái phiên bản cập nhật cho Python ví dụ nha các bạn từ Python 3.7, 3.8, 3.9, 3.10 v.v. Điện cho thì hiện tại các nguồn lập trình đều cập nhật hết và khi cập nhật thì người ta muốn bổ sung một cái chức năng nào đó trong vào trong nguồn lập trình chắc chắn người ta phải thực hiện cái việc đó là hiện thực cái Compiler Hiện thực trình dịch của ngôn ngữ đó Vấn đề thứ 2 Nó sẽ đi vào quá trình tố u cho một số kiến trúc máy tính Ngôn ngữ sẽ phát triển để làm sao nó phù hợp với chính kiến trúc máy tính đó Và càng ngày nó sẽ càng có những kiến trúc máy tính mới ra đời sẽ đáp ứng được nhu cầu của con người về

=== SUMMARY ===
Đoạn trích trình bày các ứng dụng quan trọng của công nghệ Compiler trong lĩnh vực công nghệ thông tin. Thứ nhất, nó cho phép tạo ra và cập nhật các ngôn ngữ lập trình cấp cao (như các phiên bản Python từ 3.7 đến 3.10). Việc hiện thực trình dịch là bắt buộc khi muốn bổ sung tính năng mới. Thứ hai, công nghệ này giúp tối ưu hóa mã nguồn để tương thích và phát huy tối đa sức mạnh của các kiến trúc máy tính mới, đáp ứng nhu cầu ngày càng cao của con người.

=== REVIEW QUESTIONS ===
1. Công nghệ Compiler đóng vai trò như thế nào trong việc phát triển và cập nhật các phiên bản ngôn ngữ lập trình như Python?
2. Tại sao việc hiện thực trình dịch (Compiler) là yêu cầu bắt buộc khi muốn bổ sung chức năng mới vào một ngôn ngữ lập trình?
3. Compiler giúp ích gì trong việc tối ưu hóa hiệu suất trên các kiến trúc máy tính khác nhau?
4. Mối quan hệ giữa sự phát triển của ngôn ngữ lập trình và sự ra đời của các kiến trúc máy tính mới là gì?
','bd5f50c7-6827-4730-b972-7e024a4ba2b7'::uuid,0,93,NULL,548,'2026-03-21 13:29:29.745891+07'),
	 ('ecb3c1f7-c1a0-4f4d-9a9d-33140fd597b6'::uuid,'b77e943d-ac1b-4e01-b614-53c501344569'::uuid,1,'=== ORIGINAL TRANSCRIPT ===
tốc độ, v.v. Thứ 3 là nó sẽ có khả năng là quay ngược lại, bổ sung cho các kiến trúc máy tính đó và thiết kế ra được những kiến trúc máy tính mới Vấn đề thứ 4 đó là vấn đề chuyển những ngôn ngữ lập trình chuyển giữa những chương trình với nhau. Ví dụ như là một chương trình mà mấy bạn thực hiện rất là cũ rồi, bằng một ngôn ngữ lập trình rất là cũ rồi, bây giờ làm sao có thể chuyển tương đương sang một ngôn ngữ lập trình mới mà đảm bảo được rằng hiệu quả về mặt thực thi của nó là không thay đổi. Đây là một bài toán cũng mang tính chất là thách thức rất là lớn đối với con người chúng ta. Mặc dù đó cũng là những bộ quy tắc cho con người đặt ra thôi, Nhưng do tính chất của mỗi ngôn ngữ là khác nhau, do cơ chế của mỗi ngôn ngữ là khác nhau Cho nên có khả năng là nó dịch ra thì nó chạy không có đúng như là cái chúng ta mong muốn Rồi, và vấn đề thứ 5 đó là những cái tool mà chúng ta có thể sử dụng để chúng ta phát triển được những cái ứng dụng chúng ta Ví dụ như là Visual Studio Code các bạn sử dụng ngày nay rất là nhiều, các bạn sử dụng rất là nhiều cái extension trên đó Mà cái extension đó chủ yếu phục vụ cho cái việc viết mã Như vậy thì hầu hết tất cả các extension đó đều phải được phát triển dựa trên những bước cơ bản trong quá trình làm Compiler. Chỉ cần một ứng dụng đơn giản thôi. Đó là ứng dụng làm sao có thể tô màu được những keyword trong Visual Studio Code

=== SUMMARY ===
Đoạn trích tiếp tục giới thiệu các ứng dụng của công nghệ biên dịch, bao gồm: thiết kế và bổ trợ kiến trúc máy tính mới; chuyển đổi mã nguồn giữa các ngôn ngữ lập trình khác nhau (như từ ngôn ngữ cũ sang ngôn ngữ mới) mà vẫn đảm bảo hiệu năng; và phát triển các công cụ hỗ trợ lập trình. Điển hình là các extension trong Visual Studio Code, chẳng hạn như tính năng tô màu từ khóa, đều dựa trên các bước cơ bản của quá trình làm Compiler.

=== REVIEW QUESTIONS ===
1. Tại sao việc chuyển đổi tương đương giữa các ngôn ngữ lập trình khác nhau lại là một thách thức lớn?
2. Công nghệ Compiler giúp ích gì trong việc thiết kế các kiến trúc máy tính mới?
3. Các extension hỗ trợ viết mã trong Visual Studio Code có mối liên hệ như thế nào với quá trình biên dịch?
4. Ví dụ đơn giản nhất về việc ứng dụng Compiler trong các trình soạn thảo mã nguồn được nhắc đến là gì?
','78f63324-8820-4cdf-a3d0-60825f314236'::uuid,93,186,NULL,573,'2026-03-21 13:29:29.745891+07'),
	 ('1cf8c2ee-8edc-423b-9628-e6777c15292a'::uuid,'b77e943d-ac1b-4e01-b614-53c501344569'::uuid,2,'=== ORIGINAL TRANSCRIPT ===
thì phải có Compiler chứ. Khi mấy bạn viết, trong quá trình mấy bạn viết nó phải dịch. nó phải đưa qua bước phân tích từ vận A để biết từ vận đó thuộc loại từ loại nào thì nó highlight những keyword đó lên nó highlight những keyword mà người ta viết lên dù như là for, while, in, float, v.v. thì nó phải đưa qua bước phân tích từ vận đó thì đó chính là người ta gọi đó là những software productivity tools một số những chương trình mà chúng ta thấy được là nó có liên quan trong quá trình hiện thực ngôn ngữ ví dụ như pre-processor, những bộ tiền xử lý bộ tiền xử lý này thật ra tiền xử lý về mặt dữ liệu là một chuyện rồi nhưng mà tiền xử lý về mặt ngôn ngữ có những đoạn mã mà chúng ta đưa vào đó những chỉ thị tiền xử lý Ví dụ như mấy bạn viết ngôn ngữ lập trình C rất là nhiều thì mấy bạn thấy có những chỉ thị tiền xử lý Ví dụ include, define, include là làm gì? Là khi mà nó đưa vào bước, nó đưa vào cái bộ chỉ thị tiền xử lý, bộ xử lý tiền xử lý này á thì nó sẽ thế toàn bộ cái đoạn mã của chúng ta đoạn mã mà chúng ta include đó, chúng ta thay vào đúng cái dòng đó hay là chỉ thị tiền xử lý Define để chúng ta, chúng ta nói là chúng ta sử dụng nó để chúng ta nình nghĩa ra các hàng nhưng mà thực tế là không phải, nó không phải hàng nó về bản chất là khi mà nó dịch đó nó sẽ thay thế cái giá trị đó trong toàn bộ cái chỗ mà chúng ta viết bằng

=== SUMMARY ===
Đoạn giảng này giải thích ứng dụng của trình biên dịch trong các công cụ hỗ trợ lập trình, cụ thể là việc sử dụng phân tích từ vựng để tô sáng cú pháp (syntax highlighting). Ngoài ra, nội dung còn giới thiệu về bộ tiền xử lý (pre-processor) trong ngôn ngữ C, làm rõ cơ chế hoạt động của các chỉ thị như #include (chèn mã nguồn) và #define (thay thế văn bản) trước khi quá trình biên dịch chính thức diễn ra.

=== REVIEW QUESTIONS ===
1. Tại sao bước phân tích từ vựng lại cần thiết cho việc tô sáng các từ khóa (keyword) trong trình soạn thảo mã nguồn?
2. Software productivity tools được minh họa trong đoạn video bao gồm những loại công cụ nào?
3. Chỉ thị tiền xử lý #include thực chất thực hiện hành động gì với đoạn mã nguồn của chúng ta?
4. Bản chất của chỉ thị #define trong giai đoạn tiền xử lý là gì và nó khác với việc định nghĩa hằng số thông thường như thế nào?
','a170c0f8-09f0-4747-836b-70beeb48af57'::uuid,186,278,NULL,568,'2026-03-21 13:29:29.745891+07'),
	 ('15c94c56-3c9e-4e01-8727-448c55287b47'::uuid,'b77e943d-ac1b-4e01-b614-53c501344569'::uuid,3,'=== ORIGINAL TRANSCRIPT ===
cái ký hiệu mà chúng ta Define đó nó thay thế thôi rồi nó mới dịch nhưng mà về bản chất nó không phải là hàng đâu, về bản chất nó không phải là hàng Assembler là chuyện chúng ta chuyển từ computer instruction sang machine code tức là chuyển từ ngôn ngữ assembly sang mã máy chứ không phải là trend data dịch từ ngôn ngữ cấp cao dịch từ hợp ngữ chính hay là dịch từ từ mả trung gian của chúng ta, được chưa? Rồi cái linker đó là khi mà chúng ta hiện thực bằng một chương trình mà chúng ta viết bằng nhiều file khác nhau, đúng không? Mà chúng ta dịch nhiều file đó, chúng ta sẽ dịch ra thành những object file khác nhau như vậy, bây giờ chúng ta phải combine những file này lại như thế nào? hay là cái loader là chúng ta sẽ nó chính là một cái bộ phận quan trọng của cái hệ điều hành mà chúng ta sẽ đảm bảo được rằng là những cái chương trình mà cần thiết hay là những cái thư viện cần thiết mà cho cái đoạn chương trình của chúng ta nó sẽ được đảm bảo là nó load lên trong cái quá trình mà mà chúng ta thực thi cái đoạn mã cho chúng ta viết ra cái debugger là chúng ta dùng để chúng ta test và chúng ta kiểm tra lỗi những cái chương trình mà do chúng ta viết ra, đúng không? Còn cái editor hay là bây giờ chúng ta gọi nó là cái Hầu hết là chúng ta làm bằng IDE rồi chứ không có sử dụng editor thuần nữa thì do chúng ta sẽ

=== SUMMARY ===
Đoạn giảng giải thích các công cụ bổ trợ trong quá trình phát triển phần mềm. Nội dung bao gồm: cơ chế thay thế của chỉ thị tiền xử lý #define; chức năng của Assembler trong việc chuyển hợp ngữ sang mã máy; vai trò của Linker trong việc kết hợp các file đối tượng và Loader trong việc nạp chương trình vào bộ nhớ. Cuối cùng, giảng viên đề cập đến Debugger dùng để kiểm tra lỗi và sự phổ biến của IDE so với các trình soạn thảo văn bản thuần túy.

=== REVIEW QUESTIONS ===
1. Tại sao nói chỉ thị #define chỉ thực hiện thay thế ký hiệu chứ không hẳn là tạo ra một hằng số?
2. Chức năng chính của Assembler khác gì so với trình biên dịch ngôn ngữ cấp cao?
3. Khi một chương trình được viết từ nhiều tệp nguồn, Linker đóng vai trò gì để tạo ra sản phẩm cuối cùng?
4. Loader và Debugger có nhiệm vụ cụ thể nào trong quá trình thực thi và kiểm thử chương trình?
','f5acfcb3-6192-4ca4-860b-9c251284ec28'::uuid,278,370,NULL,556,'2026-03-21 13:29:29.745891+07'),
	 ('13893502-a412-42a2-82a5-e1f81674b448'::uuid,'b77e943d-ac1b-4e01-b614-53c501344569'::uuid,4,'=== ORIGINAL TRANSCRIPT ===
cho phép người dùng có thể tạo ra và chỉnh sửa những đoạn mã nguồn một cách dễ dàng

=== SUMMARY ===
Đoạn trích này tập trung vào vai trò cốt lõi của các trình soạn thảo (Editor) hoặc IDE trong lập trình. Chức năng chính của chúng là cung cấp một giao diện thuận tiện, giúp người dùng dễ dàng khởi tạo, viết và điều chỉnh các tệp mã nguồn, tạo tiền đề cho quá trình phát triển và biên dịch phần mềm sau này.

=== REVIEW QUESTIONS ===
1. Mục đích chính của công cụ được nhắc đến trong đoạn trích là gì?
2. Tại sao việc cho phép chỉnh sửa mã nguồn ''một cách dễ dàng'' lại quan trọng đối với lập trình viên?
3. Trong ngữ cảnh công nghệ phần mềm, những công cụ nào thường thực hiện chức năng này?
4. Trình soạn thảo mã nguồn đóng vai trò gì trong quy trình làm việc với trình biên dịch?
','8e75ba18-07b3-4eb7-b620-22c5f342ce0c'::uuid,370,377,NULL,203,'2026-03-21 13:29:29.745891+07'),
	 ('c2468ce3-816e-4daf-bc2c-38fd6ef66825'::uuid,'9c1e8ddc-00f9-4393-90b8-5400e63c3b36'::uuid,0,'=== ORIGINAL TRANSCRIPT ===
thì đó chính là 4 phương pháp mà chúng ta nói tới câu chuyện hiện thực một ngôn ngữ chúng ta làm như thế nào và ở đây thì thầy muốn nói tới compiler chính là một trong những cái mà chúng ta sẽ làm trong môn học này chúng ta phải hiện thực compiler cho một ngôn ngữ lập trình đơn giản nói là compiler nhưng về bản chất nó chính là một mô hình hybrid Chứ vậy thì tổng quan trong cái quá trình làm compiler thì chúng ta sẽ trải qua 2 cái bước lớn. Một là cái frontend và 2 cái là cái backend. thì cái brand name chính là từ cái đầu tiên luôn tức nghĩa là một cái mã nguồn của chúng ta chúng ta làm sao chúng ta chuyển thành được cái mã trung gian thì đó chính là cái quá trình brand name và trong cái nội dung của môn học này thì chúng ta sẽ dừng lại tại đây chúng ta gen được cái intermediate code là chúng ta sẽ dừng đúng không? tức là chúng ta gen được cái Java by code là chúng ta sẽ dừng còn cái phần backend đó chúng ta sẽ không thực thị tiếp hả? chúng ta không thực thị được không? rồi thì đối với phần brand name đó thì chúng ta sẽ thấy được rằng là khi mà chúng ta nhận một cái mã nguồn đó là một cái file ví dụ như là trong ngôn ngữ C thì chúng ta sẽ có cái file là .C đúng không trong ngôn ngữ C công cộng thì chúng ta là .cpp đúng không cpp thì về bản chất những cái file đó cũng chẳng có khác gì những cái file text cả nó cũng chỉ là một chuỗi các gì các ký tự thôi Thì quá trình đầu

=== SUMMARY ===
Đoạn trích giới thiệu về việc hiện thực một ngôn ngữ lập trình thông qua mô hình compiler hybrid. Quá trình xây dựng compiler được chia làm hai giai đoạn chính: Frontend và Backend. Trong khuôn khổ môn học, trọng tâm là phần Frontend, bắt đầu từ việc tiếp nhận các tệp mã nguồn (.c, .cpp) dưới dạng chuỗi ký tự và kết thúc bằng việc tạo ra mã trung gian (Intermediate Code) như Java bytecode.

=== REVIEW QUESTIONS ===
1. Mô hình compiler mà môn học này hướng tới thực hiện là mô hình gì?
2. Hai giai đoạn lớn trong quá trình xây dựng một compiler là gì?
3. Mục tiêu đầu ra cuối cùng của phần Frontend trong phạm vi môn học này là gì?
4. Bản chất của các tệp mã nguồn như .c hay .cpp khi bắt đầu quá trình xử lý là gì?
','921f03b9-a7c2-4fdc-bb62-61e5396def4c'::uuid,2,94,NULL,540,'2026-03-21 13:29:34.706787+07'),
	 ('e49168b8-a079-482d-8af6-596ff2349017'::uuid,'9c1e8ddc-00f9-4393-90b8-5400e63c3b36'::uuid,1,'=== ORIGINAL TRANSCRIPT ===
tiên mà chúng ta phải nói tới đó là quá trình phân tích từ vận. Quá trình phân tích từ vận sẽ làm gì? Nó sẽ đọc chuỗi ký tự đó, chuỗi ký tự tạo nên mã nguồn đó. Nó sẽ nhóm những ký tự đó lại thành những nhóm có nghĩa được gọi là từ. Và phân loại những từ đó vào trong những từ loại khác nhau được gọi là token Ví dụ từ loại đó là danh hiệu, hay từ loại đó là kiểu, hay là từ loại đó là dấu Ví dụ như là dấu phép gán, phép cộng trừ, nhân chia, v.v. Thì đó chính là quá trình lexical analysis, quá trình chính của lexical analysis là nó làm như vậy Ngoài ra thì nó sẽ còn có một số chức năng khác, ví dụ như nó xóa đi những cái ký tự gọi là khoảng trắng, ví dụ như là cái space, ví dụ như là cái tab, ví dụ như là cái new line, v.v. Nó sẽ cũng remove ra được những cái comment, những cái tag mà đối với con người của chúng ta nó mới có ý nghĩa thôi, còn đối với chương trình thì nó không cần thiết, nó phải thực thi những cái đó. Thì cái quá trình đó là quá trình phân tích tự vận. Ví dụ như ở đây chúng ta có một cái statement, một cái phát biểu ở trong ngôn ngữ lập trình C A bằng B cộng C nhân 60 chấm phẩy, đúng không? Thì chúng ta sẽ tách ra thành 8 cái từ khác nhau, ví dụ A bằng B cộng C nhân 60 chấm phẩy và chúng ta phân nó thành những cái từ loại, ví dụ A nó là một cái ID, hiểu không?

=== SUMMARY ===
Phân đoạn này giải thích về quá trình phân tích từ vựng (lexical analysis), bước khởi đầu của compiler. Nhiệm vụ của nó là đọc chuỗi ký tự từ mã nguồn, nhóm chúng thành các từ có nghĩa và phân loại thành các ''token'' như định danh (ID), toán tử hay kiểu dữ liệu. Ngoài ra, quá trình này còn làm sạch mã nguồn bằng cách loại bỏ các khoảng trắng, tab, dòng mới và các ghi chú (comment) không cần thiết cho máy tính, giúp chuẩn bị dữ liệu cho các bước phân tích tiếp theo.

=== REVIEW QUESTIONS ===
1. Mục đích chính của quá trình phân tích từ vựng (lexical analysis) là gì?
2. Token được định nghĩa như thế nào trong quá trình biên dịch?
3. Ngoài việc tạo ra các token, bộ phân tích từ vựng còn loại bỏ những yếu tố nào khỏi mã nguồn?
4. Trong ví dụ ''A = B + C * 60;'', thực tế có bao nhiêu token được tạo ra sau khi phân tích?
','696c13d4-61e3-44c3-bf30-e94f6c9e8e0a'::uuid,94,185,NULL,541,'2026-03-21 13:29:34.706787+07'),
	 ('c288a39c-63b9-49f9-9023-c9bbccb3181b'::uuid,'9c1e8ddc-00f9-4393-90b8-5400e63c3b36'::uuid,2,'=== ORIGINAL TRANSCRIPT ===
Dấu bằng nó là một cái dấu equal chẳng hạn, những cái từ loại này dĩ nhiên chúng ta phải đặt chúng ta đặt tên cho nó cũng giống như là chúng ta đặt A là chủ ngữ, tính từ, động từ, v.v. thì đó chính là quá trình lexical analysis như vậy thì kết thúc quá trình lexical analysis chúng ta sẽ nhận ra được là chúng ta có 1 token stream từ đầu chúng ta có chuỗi 1 chiếc tự, bây giờ chúng ta sẽ có 1 chuỗi từ hoặc chuỗi từ loại chuỗi từ loại đó sẽ đưa vào quá trình thứ 2 gọi là quá trình Syntax Analysis, phân tích ngữ pháp quá trình phân tích ngữ pháp này sẽ cố gắng sử dụng ngữ pháp mà chúng ta mô tả nó nó sẽ tạo thành 1 cái cây được gọi là battery Ví dụ này là trong trường hợp nãy nó sẽ dựa vào cái cú pháp nó tạo ra được một cái cây như thế này. Dĩ nhiên nó phải kiểm tra là cái chuỗi nhập vào nó có phù hợp với cái cú pháp của tôi mô tả hay không? Cái ngữ pháp của tôi mô tả hay không? Ví dụ tôi mô tả là cái gì? Tôi mô tả là một cái xếp gắn thì bên trái của nó phải là một cái danh hiệu bên phải của nó phải là một cái biểu thức. Thì lúc này nó sẽ tạo ra một cái cây như vậy thì cái cây này được gọi là Bad Tree. Có nhầm chút xíu đây là Bad Tree chứ không phải Syntactree. Thì quá trình thứ 2 mục đích của nó là như vậy Nó sẽ dựa vào ngữ pháp mà chúng ta mô tả đó Và dựa vào chuỗi mà Token Stream ở bước

=== SUMMARY ===
Đoạn giảng giải thích sự chuyển đổi từ quá trình phân tích từ vựng (Lexical Analysis) sang phân tích cú pháp (Syntax Analysis). Phân tích từ vựng chuyển đổi chuỗi ký tự thành luồng token (token stream). Luồng này sau đó được đưa vào bộ phân tích cú pháp để kiểm tra xem cấu trúc câu lệnh có khớp với quy tắc ngữ pháp đã định nghĩa hay không (ví dụ: cấu trúc một phép gán). Kết quả của giai đoạn này là việc hình thành một cây phân tích cú pháp (Parse Tree/Bad Tree).

=== REVIEW QUESTIONS ===
1. Kết quả đầu ra của quá trình phân tích từ vựng (Lexical Analysis) được gọi là gì?
2. Quá trình phân tích cú pháp (Syntax Analysis) sử dụng tài liệu nào để kiểm tra tính hợp lệ của chuỗi nhập vào?
3. Trong ví dụ về phép gán (assignment), cấu trúc ngữ pháp yêu cầu bên trái và bên phải dấu bằng phải là gì?
4. Cấu trúc dữ liệu dạng cây được tạo ra ở giai đoạn phân tích cú pháp được gọi là gì trong đoạn trích?
','c7417733-bff4-4e90-9bb2-821af42e918c'::uuid,185,277,NULL,564,'2026-03-21 13:29:34.706787+07'),
	 ('8bad930b-39fb-4fe9-baba-e3edf53f9519'::uuid,'9c1e8ddc-00f9-4393-90b8-5400e63c3b36'::uuid,3,'=== ORIGINAL TRANSCRIPT ===
số 1 mà chúng ta thu về đó Thì nó phối hợp để coi nó có phù hợp hay không và nó tạo nên cây này Cái bước thứ 3 là trên cây bạc tree đó, đôi khi chúng ta thấy được là nó có những cái dấu, hoặc là những cái chi vợt, hoặc là những cái từ Nó chính xác là 1 cái từ, 1 cái lexeme mà nó chỉ mang tính chất biểu tượng đối với ngôn ngữ lập trình đó thôi Lấy ví dụ như là phép gán ở trong ngôn ngữ lập trình C thì người ta sử dụng dấu bằng nhưng mà phép gán trong ngôn ngữ lập trình Pascal thì người ta sử dụng dấu 2.bằng Như vậy thì việc giữ lại dấu bằng hay giữ lại dấu 2.bằng không có ý nghĩa. Về mặt ngữ nghĩa thì tính thời điểm này là tôi đã phát hiện ra xong là nó có phù hợp hay không rồi, nó có phù hợp với ngữ pháp của ngôn ngữ hay không là tôi đã phát hiện là nó phù hợp. Như vậy thì bây giờ tôi phải đào sâu vô, tôi phân tích những cái phía sau, tôi phân tích là cái ngữ nghĩa của cái đoạn mã này nó phù hợp hay không thì để làm cho cái chuyện đó thì chúng ta thường có thêm một cái bước nữa đôi khi trong một số cái compiler thì người ta vẫn dụng luôn cả cái cây từ phía trước đó luôn, cái cây bạc tree đó luôn người ta không có chuyển sang cái bước này là cái bước Abstract Syntax Tree Generation tức nghĩa là xinh cây cú pháp trù tượng thì trong quá trình sinh cây cứu pháp trù tượng nó sẽ loại đi những cái nốt mà người ta gọi là artificial do con người tạo ra,

=== SUMMARY ===
Đoạn giảng dạy này tập trung vào bước chuyển đổi từ Parse Tree sang Abstract Syntax Tree (AST). Quá trình này giúp loại bỏ các nút ''nhân tạo'' (artificial nodes) như các ký hiệu phép gán (= hoặc :=) vốn chỉ mang tính biểu tượng và khác nhau tùy ngôn ngữ lập trình. Việc đơn giản hóa cây cú pháp giúp trình biên dịch tập trung vào phân tích ngữ nghĩa của đoạn mã một cách hiệu quả hơn sau khi đã xác nhận mã nguồn tuân thủ đúng các quy tắc cú pháp.

=== REVIEW QUESTIONS ===
1. Tại sao các ký hiệu như dấu ''='' hay '':='' lại được coi là không còn ý nghĩa trong giai đoạn phân tích ngữ nghĩa sâu?
2. Quá trình ''Abstract Syntax Tree Generation'' thực hiện việc loại bỏ những thành phần nào từ Parse Tree?
3. Mục đích của việc ''đào sâu'' phân tích sau khi đã xác nhận mã nguồn phù hợp với ngữ pháp là gì?
4. Khái niệm ''nút nhân tạo'' (artificial nodes) trong ngữ cảnh xây dựng cây cú pháp được hiểu như thế nào?
','db1a8c63-3cf6-41d9-bce3-549bdf9ec5b0'::uuid,277,367,NULL,579,'2026-03-21 13:29:34.706787+07'),
	 ('0d0d041d-7083-4c89-9b89-adede491ea06'::uuid,'9c1e8ddc-00f9-4393-90b8-5400e63c3b36'::uuid,4,'=== ORIGINAL TRANSCRIPT ===
do những cái thứ mà do con người quy định thì nó sẽ xóa bỏ đi ví dụ như cái phép gắn hồi nãy thì bây giờ nó loại bỏ đi dấu bằng nè dấu 2 chấm, nó loại đi những cái dấu đó, đúng không? và nó sinh ra một cái cây, nó là cái cây assignment bên trái của nó là một cái ID, bên phải của nó là một cái biểu thức, v.v... thì bước này là quá trình số 2, Abstract Syntact Tree bước số 3, bước số 4 trên sơ đồ này là bước số 4 đó là bước Semantic Analysis hay gọi là kiểm tra tỉnh phân tích ngữ nghĩa hay gọi là kiểm tra tỉnh thì trên bước này nó sẽ cố gắng kiểm tra những rules, những luật liệu nó có phù hợp hay không Ví dụ, thường nó sẽ kiểm tra 2 vấn đề. Một là vấn đề về ngữ pháp. Ngoài những ngữ pháp mà được mô tả một cách chính quy rồi thì nó sẽ còn có một số thứ nữa. Ví dụ như là về tên. Ví dụ như là một cái tên phải được khai báo rồi mới được sử dụng. thì nó sẽ được kiểm tra ở giai đoạn này rồi kiểm tra về kiểu thì ví dụ nó kiểm tra là cái toán tử đó nó có phù hợp hay không ví dụ như trong một số ngôn ngữ lập trình ví dụ như phép dấu cộng có được áp dụng trên kiểu string hay không thì

=== SUMMARY ===
Đoạn trích giải thích về việc tạo Cây Cú pháp Trù tượng (AST) bằng cách loại bỏ các ký hiệu quy ước không cần thiết và tập trung vào bước Phân tích Ngữ nghĩa (kiểm tra tĩnh). Trong giai đoạn này, trình biên dịch thực hiện kiểm tra các quy tắc quan trọng như: biến phải được khai báo trước khi sử dụng và tính tương thích của kiểu dữ liệu đối với các toán tử (ví dụ: dấu cộng có dùng được cho chuỗi hay không).

=== REVIEW QUESTIONS ===
1. Cây Cú pháp Trù tượng (AST) khác với cây phân tích cú pháp ở điểm nào về việc xử lý các ký hiệu như dấu bằng?
2. Bước Phân tích Ngữ nghĩa (Semantic Analysis) còn được gọi bằng tên gọi nào khác trong quá trình biên dịch?
3. Tại sao việc kiểm tra khai báo tên biến lại được thực hiện ở giai đoạn Phân tích Ngữ nghĩa?
4. Kiểm tra kiểu (type checking) trong giai đoạn này thường xem xét những vấn đề gì liên quan đến toán tử?
','8a4d388a-a91e-4fd9-a635-5803aea680e7'::uuid,367,461,NULL,499,'2026-03-21 13:29:34.706787+07');
INSERT INTO coaching_chatbot.lecture_knowledge_chunks (lecture_knowledge_chunks_id,lecture_knowledge_id,chunk_index,chunk_content,qdrant_point_id,start_time_seconds,end_time_seconds,page_number,token_count,created_at) VALUES
	 ('9af89f59-5213-4a5f-8095-d33f938c1834'::uuid,'9c1e8ddc-00f9-4393-90b8-5400e63c3b36'::uuid,5,'=== ORIGINAL TRANSCRIPT ===
cái type tracking nó sẽ được gắn ở trong quá trình static tracking này kiểm tra tỉnh Nhắc lại 1 lần, kiểm tra tỉnh có nghĩa là mọi kiểm tra phải diễn ra trước dòng đầu tiên của chương trình chạy Và cái bước cuối cùng của quá trình brand name đó chính là cái Intermediate Code Generation. Đó là cái quá trình mà chúng ta sẽ hiện thực cái chuyện đó là chuyển từ cái AST, đúng không? Từ cái Abstract Syntax Tree, cái vũ pháp trù tượng, thành cái dạng đó là một cái mả trung gian. Mà ở đây cụ thể chúng ta sẽ chuyển nó thành cái mả Java by Code trong cái nội dung của môn học này của chúng ta. tuy nhiên thì chúng ta sẽ có rất là nhiều loại mã trung gian khác ví dụ như là mã CIL trong ngôn ngữ lập trình NCSAP thì đây chính là những quá trình của brand này Còn quá trình của backend thì chúng ta thấy được rằng chủ yếu là quá trình code optimization sau khi chúng ta sinh ra được intermediate code rồi đó là mã trung gian rồi thì chúng ta có một bước gọi là tư mã trung gian sau đó thì mả trung gian này mới được đưa vào trong quá trình sinh ra mả máy Rồi có 1 quá trình nữa gọi là quá trình tối ưu mã máy Thì chủ yếu cái phần backend chính là quá trình sinh

=== SUMMARY ===
Đoạn trích giải thích về quá trình kiểm tra tĩnh (static checking), trong đó việc kiểm tra kiểu (type tracking) phải hoàn tất trước khi chương trình khởi chạy. Nó cũng mô tả bước cuối của giai đoạn front-end là tạo mã trung gian (Intermediate Code Generation) từ cây cú pháp trừu tượng (AST), như Java Bytecode hay CIL. Cuối cùng, giai đoạn back-end tập trung vào tối ưu hóa mã trung gian, sinh mã máy và tối ưu hóa mã máy để hoàn tất quá trình biên dịch.

=== REVIEW QUESTIONS ===
1. Khái niệm kiểm tra tĩnh (static checking) được hiểu như thế nào trong quá trình biên dịch?
2. Bước cuối cùng của quá trình front-end là gì và nó chuyển đổi từ cấu trúc nào?
3. Trong nội dung môn học này, cây cú pháp trừu tượng (AST) sẽ được chuyển thành loại mã trung gian cụ thể nào?
4. Phần back-end của trình biên dịch chủ yếu đảm nhận những nhiệm vụ quan trọng nào?
','4d2df5fb-640f-4f3c-a488-b2ac87c6147f'::uuid,461,552,NULL,514,'2026-03-21 13:29:34.706787+07'),
	 ('96f76ca5-65bd-4ed3-90ba-47a9949703f6'::uuid,'9c1e8ddc-00f9-4393-90b8-5400e63c3b36'::uuid,6,'=== ORIGINAL TRANSCRIPT ===
ra mã máy và tối ưu những mã đó Tối ưu nghĩa là cái gì? Có thể là nó sẽ loại đi những bước không cần thiết Các bước mà có khả năng là sẽ phát hiện ra được và có thể thực thi được trong quá trình tỉnh này Trong quá trình trước thực thi, nó sẽ loại bỏ những mã đó Thì những bước đó được gọi là tối ưu mã Rồi.

=== SUMMARY ===
Đoạn này tập trung vào giai đoạn backend của quá trình biên dịch, bao gồm việc sinh mã máy và tối ưu hóa mã. Tối ưu hóa mã được giải thích là việc loại bỏ các bước thừa hoặc những đoạn mã không cần thiết mà trình biên dịch có thể phát hiện được trước khi chương trình chính thức thực thi (trong giai đoạn tĩnh), giúp chương trình đạt hiệu suất cao hơn.

=== REVIEW QUESTIONS ===
1. Giai đoạn backend của trình biên dịch thực hiện những nhiệm vụ chính nào?
2. Trong ngữ cảnh trình biên dịch, tối ưu hóa mã (code optimization) có nghĩa là gì?
3. Quá trình tối ưu hóa sẽ loại bỏ những loại mã hay bước thực thi nào?
4. Việc phát hiện và loại bỏ các mã không cần thiết được thực hiện vào thời điểm nào?
','9b6e3bb1-6182-4b49-9b6e-12d98a1b18d1'::uuid,552,578,NULL,263,'2026-03-21 13:29:34.706787+07'),
	 ('2db06924-ae87-4362-b794-d3a130b6db09'::uuid,'74e22a79-6b77-413f-9053-f6f79566226b'::uuid,0,'=== ORIGINAL TRANSCRIPT ===
All right, guys, we''ve got yet another heavy hitter guest and first time guest on the show, Professor Zhang. He is the star of the popular YouTube channel Predictive History. He uses game theory to analyze past, current, and to predict future geopolitical events. And he joins us now. Professor, great to have you. Good to see you, sir. Thanks for inviting me. Yeah, of course. So for people who aren''t familiar with your work, I wanted to show folks that back in 2024, you made three big predictions. One, that Trump would win, two, that he would start a war with Iran, and three, that the US would lose that war. Let''s take a listen to that. In this class this semester, I''m making three big predictions, right? First is that Trump will win in November. Second is that United States will go to war against Iran. The third big prediction is that the United States will lose this war, which will forever change the global order. So obviously that last one is the only one that has not yet come to fruition is quite a stunning prediction. Do you stand by it? And what have you seen so far that leads you to sort of stand firm in that conclusion? So given my analysis of how the war is progressing, I think that Iran has many more advantages over the United States. The reality is that right now it''s

=== SUMMARY ===
This segment introduces Professor Zhang, the creator of the ''Predictive History'' YouTube channel, who utilizes game theory to analyze geopolitical trends. The host highlights three major predictions Zhang made in 2024: Donald Trump winning the election, the U.S. entering a war with Iran, and the U.S. ultimately losing that conflict. As the first two predictions have already occurred, Zhang reiterates his stance on the third, suggesting that Iran possesses strategic advantages that could lead to a U.S. defeat and a reordering of the global system.

=== REVIEW QUESTIONS ===
1. What analytical methodology does Professor Zhang use to predict future geopolitical events?
2. What were the three specific predictions Professor Zhang made in 2024 regarding U.S. politics and foreign policy?
3. According to the segment, which of the professor''s predictions have already been fulfilled?
4. Why does Professor Zhang believe the United States is at risk of losing the conflict with Iran?
','6f72116f-de99-4966-8eb7-3ff0bf2d97ed'::uuid,0,90,NULL,583,'2026-03-21 13:31:15.900924+07'),
	 ('af91ea9a-9d65-4110-b3ca-8a8661dbc04c'::uuid,'74e22a79-6b77-413f-9053-f6f79566226b'::uuid,1,'=== ORIGINAL TRANSCRIPT ===
a war of attrition between the United States and Iran. And Iranians have been preparing 20 years for this conflict. In their eschatology, in their religion, this is a war against the great Satan. They''ve had many practice runs. Last June was a 12 day war when the Iranians were able to examine, analyze the strike capacities of both the Israelis and the Americans. And they''ve had a lot of time, eight months, to prepare fully for this new attack. Through their proxies, the Houthis, Hezbollah, Hamas and the Shia militias have been able to really grasp the American mentality. And now they have a pretty good strategy of how to weaken and ultimately destroy the American empire. So what the Iranians are doing is they''re waging war against the entire global economy. And so they are striking the GCC countries and not only are they striking GCC countries, American bases, but they''re going after the critical energy infrastructure of these bases. They blocked off the trip of Hormuz, and eventually they will go after the water desalination plants, which is a lifeblood of these nations because they don''t have fresh water supply. In fact, the water desalination plant provides 60%

=== SUMMARY ===
Professor Zhang outlines Iran''s long-term strategy in a war of attrition against the United States. Rooted in twenty years of preparation and religious conviction, Iran has utilized regional proxies and past skirmishes to analyze American and Israeli military capabilities. Their strategy focuses on destabilizing the global economy by targeting critical energy infrastructure in GCC countries, blocking the Strait of Hormuz, and threatening vital water desalination plants. This multi-pronged approach aims to weaken the ''American empire'' by attacking the essential resources and financial lifelines of its Middle Eastern allies.

=== REVIEW QUESTIONS ===
1. How has Iran used its regional proxies and past conflicts to refine its military strategy against the United States?
2. What role does eschatology and religious belief play in Iran''s motivation for this conflict?
3. Beyond direct military engagement, what specific economic and infrastructure targets is Iran prioritizing?
4. Why are water desalination plants described as the ''lifeblood'' of the GCC nations in the context of this war?
','810ab750-c115-4be4-89ed-91fccdfd62e5'::uuid,90,180,NULL,581,'2026-03-21 13:31:15.900924+07'),
	 ('b9c23b21-d603-42d5-a66c-e1dc9af7d325'::uuid,'74e22a79-6b77-413f-9053-f6f79566226b'::uuid,2,'=== ORIGINAL TRANSCRIPT ===
of the GCC''s water supply. So if a drone, and you know, these drones cost $50,000 if they wiped out a desalination plant in Riyadh, Saudi Arabia, and it''s a city of 10 million people. Right. They''d be out of water in two weeks. In two weeks. And right now, the Iranians have de facto closed off the strip of Hormuz. And the GCC gets 90% of its food from the strip of Hormuz. So I know a lot of you are talking about the disruptions to global economy, but right now the Iranians are actually threatening the very existence of Saudi Arabia, uae, Bahrain, Qatar. And why this is important is that the Gulf states are really the linchpin of the American economy. So what they do is they sell petrodollars and then they recycle the petrodollars back into the American economy through the investments in the stock market. And right now we know that the entire American economy is propped up by investments in data centers. And a lot of that comes from the Gulf states. So if the Gulf states are no longer able to sell oil and they''re no longer able to finance AI, this AI bubble in United States, then this AI will burst.

=== SUMMARY ===
Professor Zhang explains how Iran''s strategy targets the survival of GCC nations to undermine the US economy. By threatening desalination plants and blocking food imports via the Strait of Hormuz, Iran puts Gulf states at existential risk. This is critical because GCC petrodollars are the ''linchpin'' of the American economy, heavily funding the US stock market and the current AI investment bubble. If these states lose their ability to export oil and reinvest profits, the US financial system faces potential collapse.

=== REVIEW QUESTIONS ===
1. How do Iranian drone strikes on desalination plants pose an existential threat to major cities like Riyadh?
2. What is the strategic significance of the Strait of Hormuz regarding the GCC''s food security?
3. Explain the relationship between ''petrodollars'' and the stability of the American stock market as described in the segment.
4. According to Professor Zhang, what would be the direct consequence for the US AI sector if GCC countries were unable to sell oil?
','a144402d-ee38-4fee-851b-440f41ae38f1'::uuid,181,271,NULL,544,'2026-03-21 13:31:15.900924+07'),
	 ('70eaa512-2f04-4087-ad1a-cd2ee818eb91'::uuid,'74e22a79-6b77-413f-9053-f6f79566226b'::uuid,3,'=== ORIGINAL TRANSCRIPT ===
And with it will burst as well as the entire American economy, which is really a financial Ponzi scheme. So that''s a dire situation that the markets are facing right now. Yeah, I mean, to your point, sir, an Amazon data center was literally hit in the uae. And now of course, big tech companies, which were looking at the UAE as a major potential data center investment hub with cheap and abundant energy, probably going to be rethinking that. We also wanted to talk to you about munitions. We can put this next one up here on the screen. The United States is racing to accomplish Iran mission before munitions run out. This is specifically around interceptor math. There''s a famous video just from yesterday in Israel which shows a single Iranian ballistic missile which is being targeted by some 11 different interceptors, all of which, miss, most of those come from the United States, not to mention all of the different bases, the GCC countries that you just talked about. And the asymmetry of the cost. It''s a million dollars for a missile. Tens of millions per interceptor in some of these cases. With these munitions running out, how does that change the global picture? So you''re in China, obviously much of the stocks in Asia of the United States are likely to have to be cannibalized if this were to go on. How''s that going to affect the global picture here? Right. So my first point is that the United States military is not designed to fight against a 21st century war. Remember the military industrial complex came to being after World War II and it''s designed to fight the Cold War. And the Cold War was really about muscle flexing about about who was able to send people up into space,

=== SUMMARY ===
This segment explores the economic and military vulnerabilities of the United States in a modern conflict. It highlights how attacks on infrastructure, like UAE data centers, threaten the AI-driven economy. Furthermore, it discusses the ''interceptor math'' crisis: the unsustainable cost asymmetry where expensive US interceptors fail against cheaper missiles, depleting global munitions stocks. Finally, it argues that the US military-industrial complex remains outdated, geared toward Cold War-style displays of power rather than the technological and asymmetric realities of 21st-century warfare.

=== REVIEW QUESTIONS ===
1. How does the attack on an Amazon data center in the UAE affect the strategic outlook for big tech companies in the region?
2. What does the term ''interceptor math'' refer to in the context of the military engagement described?
3. According to the text, why is the current cost asymmetry of munitions considered unsustainable for the United States?
4. Why does the speaker argue that the United States military is ill-equipped for 21st-century warfare?
','072cbd06-69b0-483e-8e66-593a2016b8ba'::uuid,271,365,NULL,707,'2026-03-21 13:31:15.900924+07'),
	 ('f778cd53-ed8b-452a-b3c4-877404fb8d03'::uuid,'74e22a79-6b77-413f-9053-f6f79566226b'::uuid,4,'=== ORIGINAL TRANSCRIPT ===
was able to, who was the first get the person on the moon who had the more complex missile systems. And so the entire American military strategy revolves around very sophisticated technology that, that costs a lot of money to build. And that''s what the American air defense system is basically. And that''s why we''re seeing this asymmetry as you point out, in this war where you have these million dollar missiles trying to take out these $50,000 drones. And it''s not sustainable in the long term. And so what we''re seeing is really the puncturing of the aura of invincibility and invisibility that sustain American hegemony for the past 20 years, especially after the collapse of the Soviet Union. And this is really a reordering of not just a global economy because this signals the collapse of the petrodollar and with it the entire US dollar based reserve currency system, but also global, the global hegemony of the United States. We''re moving towards a multipolar world. Professor this morning, Secretary of War Pete Hegseth was asked a question about potential ground troops in Iran. He refused to rule out that possibility. And he said, you know, oh well, we''re not going to project to you what we will or will not do, what we don''t want to rule anything out. We will do what it takes. Do you think that America will end up invading Iran from the

=== SUMMARY ===
This segment discusses the unsustainable nature of current American military strategy, which relies on high-cost technology to combat inexpensive threats like drones. The speaker argues this ''cost asymmetry'' is eroding U.S. military invincibility and threatening the petrodollar-based global economy. As the world moves toward a multipolar order, the collapse of the US dollar''s reserve status becomes a real risk. Finally, the text notes that U.S. officials have not ruled out the possibility of a ground invasion of Iran, indicating a potential shift in military policy.

=== REVIEW QUESTIONS ===
1. How does the speaker describe the financial ''asymmetry'' in modern air defense warfare?
2. What long-term economic consequence does the speaker predict following the puncturing of the US ''aura of invincibility''?
3. How does the current military strategy relate to the Cold War era, according to the segment?
4. What was Secretary of War Pete Hegseth''s response regarding the potential use of ground troops in Iran?
','aeff1f2b-f107-4b26-8e80-bc2a54c56d0c'::uuid,365,458,NULL,604,'2026-03-21 13:31:15.900924+07'),
	 ('38786609-62e6-4b19-9e6a-6c25edc606a1'::uuid,'74e22a79-6b77-413f-9053-f6f79566226b'::uuid,5,'=== ORIGINAL TRANSCRIPT ===
ground since it''s already becoming relatively clear they won''t be able to accomplish the goal of regime change or even regime collapse using just air power alone. Right? So everyone says that the worst calamity that could happen in the United States is if it were to send ground troops into Iran. At the same time the United States are committed to regime change in Iran. We''ve never had a precedent in history where you were able to regime change from the air alone. You need ground troops. And so unfortunately what''s going to happen over the next few months is that pressure will build on America to send ground troops, especially from the GCC countries and from Israel, which are being pounded right now by the Iranians. So remember that if the GCC countries, Saudi Arabia, Qatar, uae, Bahrain, if they go, then the petrodollar goes with them. So the Americans need to protect these countries. And these countries are going to demand that either the Americans bribe the Iranians to cease and this is like five, twenty dollars in indemnity. Okay. Or send ground troops to wipe out the Iranian threat once and for all. And I know that there''s no political will for ground troops to be used against Iran among the American people. But remember that 78% American people was against initial strikes against Iran in the first place. Right. And one of the things that really comes into question, I think, with all of

=== SUMMARY ===
This segment explores the military and economic pressures pushing the US toward a ground invasion of Iran. The speaker argues that air power alone is historically insufficient for regime change. Allies in the GCC and Israel are pressuring the US to act, as the survival of these nations is directly tied to the petrodollar system. The US faces a choice: pay significant indemnities to Iran or deploy ground troops. Despite strong domestic public opposition, historical precedents suggest the government may proceed with military action to protect global economic interests.

=== REVIEW QUESTIONS ===
1. Why does the speaker believe air power alone is insufficient for achieving regime change in Iran?
2. How is the survival of GCC countries like Saudi Arabia and Qatar linked to the stability of the petrodollar?
3. What two primary options are allies likely to present to the United States to resolve the Iranian threat?
4. What statistic is cited to illustrate the gap between US public opinion and military policy?
','a13f5984-3dfa-4109-89af-ce8541074601'::uuid,458,549,NULL,620,'2026-03-21 13:31:15.900924+07'),
	 ('3245096d-bc3a-4f6d-a158-221c9b74a51f'::uuid,'74e22a79-6b77-413f-9053-f6f79566226b'::uuid,6,'=== ORIGINAL TRANSCRIPT ===
this, sir, when we''re looking at the geopolitical picture and the sacrificing in much of this from the United States on behalf of Israel is kind of a question as to who and what wanted this. We were actually curious for your view on a disputed report here from the Washington Post. We can put it up here on the screen. F3 A push from the Saudis Israel helped move Trump to attack Iran. Now, this has been internally disputed by Saudi Arabia saying that they did not push the Trump administration to attack Iran. However, this is clearly an authorized leak from our own government trying to at least lay some of the blame on behalf of the gcc. What do you make of this analysis and of this narrative that Saudi Arabia was also trying to be behind this push to bomb Iran? So I''ve always argued that both Saudi Arabia and Israel are, are heavily invested in regime change in Iran. In fact, Saudi Arabia sees Iran as much more of an existential crisis than Israel because remember, Israel still has nuclear weapons and Israel is a very diverse, very sophisticated economy, whereas Saudi Arabia is completely reliant on oil. And it''s always had problems with Iran because Iran is a theocracy opposed to the Saudi monarchy. Iran funds and equips the Houthis, which have been antagonistic towards Saudi Arabia for the longest time. And right now the Saudi economy is suffering. It''s been trying to pivot towards more of a tourist economy.

=== SUMMARY ===
This segment explores the geopolitical influence of Saudi Arabia and Israel on US policy toward Iran. It references a Washington Post report suggesting both nations pressured the Trump administration to initiate an attack. The speaker argues that Saudi Arabia views Iran as an existential threat—even more so than Israel—due to its reliance on oil, Iran’s theocratic opposition to the Saudi monarchy, and Iran''s support for the Houthis. These factors jeopardize Saudi Arabia''s efforts to transition its struggling economy toward tourism.

=== REVIEW QUESTIONS ===
1. According to the segment, what did the Washington Post report regarding Saudi Arabia and Israel''s influence on President Trump?
2. Why does the speaker contend that Saudi Arabia perceives Iran as a greater existential threat than Israel does?
3. How does the political structure of Iran create ideological tension with the Saudi Arabian government?
4. What economic challenges and transitions in Saudi Arabia are being impacted by its ongoing conflict with Iran?
','4d64e19e-d150-4c02-b639-ed7c3fd2fd51'::uuid,549,639,NULL,627,'2026-03-21 13:31:15.900924+07'),
	 ('70ae6c3a-9fd0-4e85-af92-3d0021b210bf'::uuid,'74e22a79-6b77-413f-9053-f6f79566226b'::uuid,7,'=== ORIGINAL TRANSCRIPT ===
That''s why they brought in Ronaldo. That''s why they''re switching to E Gaming. That''s why they''re building something called a line Neom and all these things are not working out. So they need to be able to control the oil resources of the entire Middle east if they are to survive and thrive as a nation. So I do believe that this reporting is credible, even though it does make Saudi Arabia look bad. But remember, Saudi Arabia says that they wanted peace, but they are helping the Israelis and Americans attack Iran right now because they''re allowing the Israelis and Americans to use Saudi airspace. So help us understand, because it still feels to me like there are some pieces that don''t totally add up. You had, in advance of this war on Iran, you had top military brass up to the chairman of the Joint Chiefs of Staff saying this is a really bad idea. You know, we are not ready for this. You know, all the math on the interceptors, that''s been talked about for months at this point, and yet Trump stands still, decided to go ahead. What is the calculus that he was making here that led him to take what already appears to be catastrophic actions? Right. Okay, so this is the key question. Why do they do this? I think there are three possibilities. Okay. And I think all three possibilities are valid. The first is the idea of hubris. You look at history. This is how empires behave. So the Mindoro kidnapping

=== SUMMARY ===
This segment discusses Saudi Arabia''s failed economic diversification efforts, such as Neom and sports investments, which the speaker argues drives their need for regional oil control. Despite claiming a desire for peace, Saudi Arabia reportedly aids U.S. and Israeli military efforts against Iran by providing airspace. The discussion also addresses why U.S. leadership ignored warnings from top military officials, identifying imperial ''hubris'' as a primary psychological driver behind the decision to pursue potentially catastrophic military actions despite strategic risks.

=== REVIEW QUESTIONS ===
1. What specific projects or initiatives are mentioned as part of Saudi Arabia''s attempt to diversify its economy?
2. How is Saudi Arabia reportedly assisting U.S. and Israeli military operations against Iran?
3. What was the stated opinion of the ''top military brass'' regarding the potential war with Iran?
4. How does the speaker define ''hubris'' in the context of imperial behavior and decision-making?
','6875cf07-af57-4f26-8d53-e51b6c1d5a32'::uuid,639,730,NULL,618,'2026-03-21 13:31:15.900924+07');
INSERT INTO coaching_chatbot.lecture_knowledge_chunks (lecture_knowledge_chunks_id,lecture_knowledge_id,chunk_index,chunk_content,qdrant_point_id,start_time_seconds,end_time_seconds,page_number,token_count,created_at) VALUES
	 ('d874698e-be83-4aaa-850e-ada150fdff80'::uuid,'74e22a79-6b77-413f-9053-f6f79566226b'::uuid,8,'=== ORIGINAL TRANSCRIPT ===
was quick, successful, and it was adrenaline rush for Trump, and that made him overconfident in the capacity of the American military. Okay? So hubris is a factor. And we see this throughout history. Why did Hitler invade Stalin? Because he conquered Europe really easily and he thought he was invincible. Right. And that led to the destruction of the German army in the Soviet Union. So hubris is a factor. Then you have to look at internal political calculus where even though America does not benefit from this war against Iran, Trump himself personally benefits. Why? Because the Saudis and Israelis are bribing him to attack Iran. Remember that The Saudis invested $2 billion in the private equity fund of Jared Kushner, who is the son in law of Donald Trump. And Israelis, through Mayor Adelson, have been financing Trump''s local career. Right. So Mayor Adelson a few months back said that she will put up $250 million if Trump were to run a third term. So Trump is getting a lot of financial and political support from the Saudis and Israelis. Also remember that if this war goes sideways and Trump is forced to use ground troops, he will probably get approval from Congress, and this will give him emergency war powers, which will allow him to influence the midterms. So Trump is thinking about a third term. And I think at the ballot box, he probably won''t get it. But if there''s a war going on and you can delay elections

=== SUMMARY ===
This segment analyzes the motivations behind Donald Trump''s potential military actions against Iran, identifying three primary factors: hubris, personal financial gain, and political strategy. Drawing a historical parallel to Hitler, the speaker suggests that early military successes created a sense of overconfidence. Furthermore, the speaker cites financial support from Saudi Arabia and Israel as personal incentives. Finally, the segment explores how a state of war could grant Trump emergency powers to influence the midterms and potentially secure a third term by disrupting normal electoral processes.

=== REVIEW QUESTIONS ===
1. What historical analogy does the speaker use to illustrate the concept of military ''hubris''?
2. According to the segment, what specific financial investments and donations have linked the Trump administration to Saudi and Israeli interests?
3. How could a war with Iran lead to ''emergency war powers'' for the president, and what impact might this have on domestic politics?
4. What is the speaker''s theory regarding the relationship between military conflict and the possibility of a third presidential term?
','4e224b47-7cf8-468e-874d-68a8859a6b51'::uuid,730,826,NULL,657,'2026-03-21 13:31:15.900924+07'),
	 ('4fd258f3-20d0-4313-8e64-06074c1af295'::uuid,'74e22a79-6b77-413f-9053-f6f79566226b'::uuid,9,'=== ORIGINAL TRANSCRIPT ===
and you have emergency war powers and people rally around the flag, then he probably will get a third term. And the last factor that is very important is eschatological factor, where if you look at the Epstein files, it''s clear that we are run by secret societies. It''s clear that the world is run by these individuals who have a lot of power. We don''t know who they are, but they control the military, they control the national security apparatus and these people. There are different names for these people. You can call them Illuminati. And Illuminati are composed of three major groups, okay? You have the Jesuits who control the Vatican. You have the Sapatine Frankists which control the modern state of Israel today. And you have the Freemasons, which control the national security apparatus of the United States. And they believe that Israel, this war in the Middle east, is key to the end times, including heaven on earth. So it''s almost like a script that they''re following, even though it doesn''t make any jupical sense. Okay? So I would say that these three are the best reasons why this is happening. Wow. Well, sir, I hope you come back. This was very informative, I think, for both of us. We deeply appreciate your time. Thank you. Thank you. Hey, if you like that video, hit the like button or leave a comment below. It really helps get the show to more people. And if you''d like to get the full show ad free and in your inbox every morning, you can sign up@breakingpoints.com that''s right, get the full show. Help support the future of independent media@breakingpoints.com.

=== SUMMARY ===
This segment outlines two key motivations for a potential conflict with Iran: domestic political advantage and eschatological ideology. The speaker argues that a president could use emergency war powers to delay elections or rally public support for a third term. Additionally, the speaker introduces a theory involving secret societies—comprising Jesuits, Sabbatean Frankists, and Freemasons—who allegedly follow an ''end times'' script regarding Israel. These factors are presented as the underlying logic for military actions that the speaker claims do not align with traditional geopolitical reasoning.

=== REVIEW QUESTIONS ===
1. In what ways does the speaker suggest a conflict could be used to influence a domestic election or secure a third term?
2. What is the ''eschatological factor'' and how is it used to explain military conflict in the Middle East?
3. According to the speaker, which three groups compose the ''Illuminati'' and which specific institutions do they allegedly control?
4. How does the speaker characterize the relationship between the ''end times'' script and traditional geopolitical logic?
','d5e86074-c508-4b75-8e3d-40c02e6bb76a'::uuid,826,914,NULL,688,'2026-03-21 13:31:15.900924+07'),
	 ('be15f765-6a69-482b-b1ce-3ab281410345'::uuid,'5655cc4d-1de7-4263-9950-dfa39500d821'::uuid,0,'=== ORIGINAL CONTENT ===
# Bài Giảng: Tổng Quan Về Data Engineering (Kỹ Sư Dữ Liệu)

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

=== SUMMARY ===
Bài giảng cung cấp cái nhìn tổng quan về ngành Kỹ thuật Dữ liệu (Data Engineering). Nội dung tập trung vào định nghĩa vai trò của Kỹ sư dữ liệu là người xây dựng hạ tầng và chuẩn bị dữ liệu sạch. Các nhiệm vụ trọng tâm bao gồm thiết kế Data Pipeline, thực hiện quy trình ETL/ELT, quản lý kho dữ liệu và bảo mật. Cuối cùng, bài viết liệt kê các kỹ năng thiết yếu như lập trình Python/SQL, công cụ Big Data và nền tảng điện toán đám mây.

=== REVIEW QUESTIONS ===
1. Tại sao Data Engineering được coi là ''xương sống'' của mọi hệ thống dữ liệu hiện đại?
2. Giải thích ý nghĩa của ba bước trong quy trình ETL (Extract, Transform, Load).
3. Sự khác biệt giữa việc sử dụng SQL và NoSQL trong công việc của một Data Engineer là gì?
4. Một Data Engineer cần những kỹ năng lập trình và công cụ Big Data nào để xây dựng hệ thống xử lý dữ liệu quy mô lớn?','50dd1443-671b-4cc3-9122-5b9c8b93ef27'::uuid,NULL,NULL,NULL,729,'2026-03-21 13:35:23.068229+07'),
	 ('216d91ab-b240-4e05-847d-afbf3d7bd4aa'::uuid,'5655cc4d-1de7-4263-9950-dfa39500d821'::uuid,1,'=== ORIGINAL CONTENT ===
* **Công cụ xử lý Big Data:** Apache Spark, Hadoop, Apache Kafka, Flink. * **Nền tảng Cloud Computing:** AWS (Redshift, S3), Google Cloud Platform (BigQuery), Microsoft Azure. ## 4. Phân biệt Data Engineer, Data Analyst và Data Scientist

| Vai trò | Mục tiêu chính | Công cụ thường dùng |
| :--- | :--- | :--- |
| **Data Engineer** | Xây dựng hạ tầng, chuẩn bị và làm sạch dữ liệu. | SQL, Python, Spark, Cloud, Airflow |
| **Data Analyst** | Trực quan hóa dữ liệu, báo cáo, tìm ra *insights* kinh doanh. | SQL, Excel, Tableau, PowerBI |
| **Data Scientist**| Dự đoán tương lai, xây dựng mô hình Machine Learning. | Python, R, TensorFlow, Scikit-learn |

## 5. Tài liệu tham khảo thêm

Nếu bạn muốn bắt đầu con đường này, lộ trình học tập là vô cùng quan trọng. Bạn có thể tham khảo lộ trình chi tiết tại đây:
[Roadmap dành cho Data Engineer](https://roadmap.sh/data-engineer)

=== SUMMARY ===
Đoạn nội dung giới thiệu các công cụ Big Data và nền tảng Cloud thiết yếu, đồng thời phân biệt rõ ba vai trò chính trong ngành dữ liệu: Data Engineer (xây dựng hạ tầng), Data Analyst (phân tích insight) và Data Scientist (xây dựng mô hình dự đoán). Qua đó, người học có cái nhìn tổng quan về công cụ và trách nhiệm của từng vị trí để định hướng lộ trình học tập phù hợp.

=== REVIEW QUESTIONS ===
1. Kể tên 3 nền tảng Cloud Computing phổ biến được đề cập để lưu trữ và xử lý dữ liệu?
2. Sự khác biệt chính về mục tiêu công việc giữa một Data Engineer và một Data Analyst là gì?
3. Những công cụ và thư viện nào thường được Data Scientist sử dụng để xây dựng mô hình Machine Learning?
4. Theo nội dung bài viết, vai trò nào chịu trách nhiệm chính trong việc chuẩn bị và làm sạch dữ liệu?','c0c722cd-78c4-4c85-8678-3ca86cfbad12'::uuid,NULL,NULL,NULL,426,'2026-03-21 13:35:23.068229+07'),
	 ('77ff9e4b-620a-49a6-8d84-45243e6b9a2a'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,0,'=== ORIGINAL CONTENT ===
ĐẠI HỌC QUỐC GIA THÀNH PHỐHỒCHÍ MINH
TRƯỜNG ĐẠI HỌC BÁCH KHOA
KHOA KHOA HỌC VÀ KỸTHUẬT MÁY TÍNH
BÁO CÁO
ĐỒÁN CHUYÊN NGÀNH
XÂY DỰNG HỆTHỐNG HỌC TẬP TRỰC TUYẾN TÍCH
HỢP TÍNH NĂNG CÁ NHÂN HÓA LỘTRÌNH HỌC TẬP
NGÀNH: KHOA HỌC MÁY TÍNH
Giảng viên hướng dẫn
: ThS. Lê ThịBảo Thu
Sinh viên 1
: Trần Duy Phương - 2212703
Sinh viên 2
: Trần Hồng Phúc - 2212651
Sinh viên 3
: Trần Đại Việt - 2213951
HồChí Minh, Ngày 21 tháng 12 năm 2025

=== SUMMARY ===
Đây là trang bìa báo cáo đồ án chuyên ngành của sinh viên ngành Khoa học Máy tính, Trường Đại học Bách khoa - ĐHQG TP.HCM. Đề tài tập trung vào việc xây dựng một hệ thống học tập trực tuyến (E-learning) có tích hợp khả năng cá nhân hóa lộ trình học tập cho người dùng. Báo cáo cung cấp thông tin về đơn vị đào tạo, đội ngũ thực hiện gồm ba sinh viên và giảng viên hướng dẫn là ThS. Lê Thị Bảo Thu, hoàn thành vào tháng 12 năm 2025.

=== REVIEW QUESTIONS ===
1. Mục tiêu chính của đề tài đồ án chuyên ngành này là xây dựng hệ thống gì?
2. Tính năng cốt lõi nào được tích hợp để tăng tính hiệu quả cho lộ trình học tập trong hệ thống?
3. Đồ án này được thực hiện bởi các sinh viên thuộc ngành học và khoa nào?
4. Giảng viên chịu trách nhiệm hướng dẫn chuyên môn cho đề tài này là ai?','b110cbe5-2346-427a-b3b3-b4a91eddcc3b'::uuid,NULL,NULL,1,312,'2026-03-21 13:35:13.315967+07'),
	 ('6b6f53c4-85ff-4506-b7ab-1d577c1fb108'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,1,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
LỜI CAM ĐOAN
Nhóm chúng em xin cam đoan rằng đồán này là kết quảcủa quá trình học
tập, nghiên cứu và làm việc nghiêm túc của chính các thành viên trong nhóm,
được thực hiện dưới sựhướng dẫn của quý Thầy Cô khoa Khoa học và Kỹ
thuật Máy tính, Trường Đại học Bách khoa – ĐHQG TP.HCM.
Các sốliệu, kết quả, hình ảnh và nội dung trình bày trong đồán đều trung
thực, được thu thập và xửlý một cách khách quan, không sao chép từbất kỳ
công trình nào khác mà không trích dẫn nguồn. Những phần nội dung, tài
liệu, kết quảnghiên cứu của các tác giảvà công trình khác (nếu có) được sử
dụng trong đồán đều đã được trích dẫn và ghi nguồn tham khảo đầy đủ, đúng
quy định.
Nhóm chúng em hoàn toàn chịu trách nhiệm vềtính trung thực và nguyên
bản của toàn bộnội dung đồán này. Nếu có bất kỳsai sót hoặc vi phạm nào
liên quan đến vấn đềbản quyền, trung thực học thuật hay quy chếđào tạo,
chúng em xin hoàn toàn chịu trách nhiệm trước nhà trường.
Có thểtruy cập file báo cáo này (bản PDF không bịnén) tại đây.
TP. HồChí Minh, Ngày 21 tháng 12 năm 2025
Nhóm sinh viên thực hiện
Trần Duy Phương
Trần Hồng Phúc
Trần Đại Việt
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang i/254

=== SUMMARY ===
Nội dung này là lời cam đoan của nhóm sinh viên về tính trung thực và nguyên bản của đồ án chuyên ngành. Nhóm khẳng định kết quả nghiên cứu là công sức của các thành viên, dữ liệu được thu thập khách quan, và mọi nguồn tài liệu tham khảo đều được trích dẫn đầy đủ theo quy định. Đồng thời, các sinh viên cam kết chịu hoàn toàn trách nhiệm trước nhà trường nếu có bất kỳ vi phạm nào về bản quyền hoặc đạo đức học thuật.

=== REVIEW QUESTIONS ===
1. Mục đích chính của phần ''Lời cam đoan'' trong báo cáo đồ án là gì?
2. Nhóm sinh viên đã đưa ra những cam kết nào liên quan đến việc trích dẫn nguồn tài liệu tham khảo?
3. Ai là người chịu trách nhiệm nếu đồ án có sai sót hoặc vi phạm quy chế đào tạo?
4. Các số liệu và hình ảnh trong đồ án được nhóm sinh viên khẳng định có tính chất như thế nào?','e0b4844b-bd42-46ad-aa17-0ea05895f643'::uuid,NULL,NULL,2,522,'2026-03-21 13:35:13.31666+07'),
	 ('926a04fc-68b8-4dcd-abe9-69274b3b72e4'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,2,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
LỜI CẢM ƠN
Trước hết, nhóm xin bày tỏlòng biết ơn sâu sắc tới quý Thầy Cô khoa Khoa
học và Kỹthuật Máy tính, Trường Đại học Bách khoa – ĐHQG TP.HCM, vì
đã tận tình giảng dạy và trang bịcho em nền tảng kiến thức vững chắc cũng
như tư duy khoa học trong suốt quá trình học tập. Những kiến thức đó là cơ
sởquan trọng giúp em có thểnghiên cứu và hoàn thành đồán này một cách
có hệthống và nghiêm túc.
Em đặc biệt gửi lời cảm ơn chân thành tới Cô Lê ThịBảo Thu, người
đã trực tiếp hướng dẫn, định hướng chuyên môn, góp ý chi tiết và luôn đồng
hành trong suốt quá trình thực hiện đồán. Sựtận tâm, trách nhiệm và những
nhận xét quý báu của Cô đã giúp nhóm hoàn thiện hơn cảvềnội dung nghiên
cứu lẫn phương pháp tiếp cận vấn đề.
Em cũng xin cảm ơn các Thầy Cô trong Hội đồng đã dành thời gian xem
xét, đánh giá và đưa ra những ý kiến nhận xét, góp ý chân thành. Những gợi ý
và định hướng từHội đồng là cơ sởquan trọng đểem nhìn nhận rõ hơn những
hạn chếcủa đềtài và định hướng phát triển trong tương lai.
Mặc dù đã cốgắng hoàn thiện đồán trong khảnăng của mình, song khó
tránh khỏi những thiếu sót; nhóm rất mong nhận được các ý kiến đóng góp
của quý Thầy Cô đểcó thểtiếp tục hoàn thiện hơn trong tương lai.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang ii/254

=== SUMMARY ===
Đoạn văn là lời tri ân của nhóm sinh viên thực hiện đồ án chuyên ngành thuộc Khoa Khoa học và Kỹ thuật Máy tính, Trường ĐH Bách khoa TP.HCM. Nội dung nhấn mạnh lòng biết ơn đối với các giảng viên đã truyền đạt kiến thức, đặc biệt là giảng viên hướng dẫn Lê Thị Bảo Thu vì sự hỗ trợ chuyên môn tận tâm. Đồng thời, nhóm cũng gửi lời cảm ơn Hội đồng đánh giá và bày tỏ tinh thần cầu thị, mong muốn nhận thêm góp ý để hoàn thiện nghiên cứu.

=== REVIEW QUESTIONS ===
1. Nhóm sinh viên bày tỏ lòng biết ơn đối với tập thể giảng viên Khoa Khoa học và Kỹ thuật Máy tính về điều gì?
2. Giảng viên Lê Thị Bảo Thu đã đóng vai trò cụ thể nào trong quá trình thực hiện đồ án của nhóm?
3. Ý kiến từ Hội đồng đánh giá có ý nghĩa như thế nào đối với sự phát triển của đề tài trong tương lai?
4. Thái độ của nhóm sinh viên đối với những thiếu sót có thể có trong đồ án được thể hiện qua câu văn nào?','95af0395-9ac5-464d-84c3-9bc19d6f0f14'::uuid,NULL,NULL,3,569,'2026-03-21 13:35:13.31666+07'),
	 ('d0975d86-8d4d-4ea4-88db-85a2966de330'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,3,'=== ORIGINAL CONTENT ===
Trường Đại Học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa Học & KỹThuật Máy Tính
Mục lục
1
Giới thiệu đềtài
1
1.1
Thực trạng đềtài . . . . . . . . . . . . . . . . . . . . . . . .
1
1.2
Mục tiêu và nhiệm vụ
. . . . . . . . . . . . . . . . . . . . .
2
1.2.1
Mục tiêu
. . . . . . . . . . . . . . . . . . . . . . . .
2
1.2.2
Nhiệm vụ. . . . . . . . . . . . . . . . . . . . . . . .
3
1.3
Đối tượng và phạm vi nghiên cứu . . . . . . . . . . . . . . .
3
2
Nghiên cứu thịtrường
4
2.1
Coursera . . . . . . . . . . . . . . . . . . . . . . . . . . . .
4
2.1.1
Tổng quan vềCoursera . . . . . . . . . . . . . . . . .
4
2.1.2
Đánh giá kỹthuật . . . . . . . . . . . . . . . . . . . .
5
2.1.3
Phân tích tính năng . . . . . . . . . . . . . . . . . . .
6
2.1.4
Kết luận . . . . . . . . . . . . . . . . . . . . . . . . .
7
2.2
Udemy . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
8
2.2.1
Tổng quan vềUdemy . . . . . . . . . . . . . . . . . .
8
2.2.2
Đánh giá kỹthuật . . . . . . . . . . . . . . . . . . . .
9
2.2.3
Phân tích tính năng . . . . . . . . . . . . . . . . . . .
9
2.2.4
Kết luận . . . . . . . . . . . . . . . . . . . . . . . . .
11
2.3
LinkedIn Learning . . . . . . . . . . . . . . . . . . . . . . .
11
2.3.1
Tổng quan vềLinkedIn Learning . . . . . . . . . . . .
11
2.3.2
Đánh giá kỹthuật . . . . . . . . . . . . . . . . . . . .
12
2.3.3
Phân tích tính năng . . . . . . . . . . . . . . . . . . .
15
2.4
Khảo sát thực tế
. . . . . . . . . . . . . . . . . . . . . . . .
17
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang iii/254

=== SUMMARY ===
Phân đoạn này trình bày mục lục của báo cáo đồ án chuyên ngành, tập trung vào hai chương đầu tiên. Chương 1 giới thiệu tổng quan về đề tài bao gồm thực trạng, mục tiêu, nhiệm vụ và phạm vi nghiên cứu. Chương 2 đi sâu vào nghiên cứu thị trường thông qua việc đánh giá kỹ thuật và phân tích tính năng của các nền tảng E-learning nổi tiếng như Coursera, Udemy và LinkedIn Learning, kết hợp với các khảo sát thực tế để định hướng phát triển hệ thống.

=== REVIEW QUESTIONS ===
1. Những nội dung chính nào được trình bày trong chương Giới thiệu đề tài để định hình nghiên cứu?
2. Báo cáo đã thực hiện phân tích và đánh giá kỹ thuật cho những nền tảng học trực tuyến cụ thể nào?
3. Trong mục nghiên cứu thị trường, các nền tảng như Coursera và Udemy được phân tích dựa trên những tiêu chí nào?
4. Ngoài việc nghiên cứu các nền tảng quốc tế, báo cáo còn thực hiện hoạt động gì ở mục 2.4 để thu thập thông tin?','45bb5503-c048-4126-b199-1e53dba30982'::uuid,NULL,NULL,4,622,'2026-03-21 13:35:13.31666+07'),
	 ('1b8b75ab-a1f1-4853-ba90-a3b77b97f799'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,4,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
2.4.1
Tổng quan khảo sát . . . . . . . . . . . . . . . . . . . .
17
2.4.2
Kết quảkhảo sát . . . . . . . . . . . . . . . . . . . .
17
2.4.3
Đánh giá và đềxuất tính năng . . . . . . . . . . . . .
21
3
Phân tích hệthống
24
3.1
Stakeholders . . . . . . . . . . . . . . . . . . . . . . . . . .
24
3.1.1
Người dùng cuối . . . . . . . . . . . . . . . . . . . .
24
3.1.2
Các bên cung cấp dịch vụthứ3
. . . . . . . . . . . .
26
3.2
Yêu cầu chức năng . . . . . . . . . . . . . . . . . . . . .
26
3.2.1
Epic 1: Quản lý học tập cá nhân (Sinh viên) . . . . . .
27
3.2.2
Epic 2: Khám phá và đăng ký khóa học (Sinh viên) . .
28
3.2.3
Epic 3: Truy cập học liệu và tài nguyên (Sinh viên) . .
28
3.2.4
Epic 4: Nhận phản hồi và đánh giá (Sinh viên)
. . . .
29
3.2.5
Epic 5: Giao tiếp và thông báo (Sinh viên) . . . . . . .
29
3.2.6
Epic 6: HỗtrợAI học tập (Sinh viên) . . . . . . . . .
29
3.2.7
Epic 7: Quản lý khóa học và tài liệu (Giảng viên) . . .
29
3.2.8
Epic 8: Hệthống bài tập và kiểm tra (Giảng viên) . . .
30
3.2.9
Epic 9: Phân tích và thống kê (Giảng viên)
. . . . . .
31
3.2.10 Epic 10: Trợlý AI cho Giảng viên . . . . . . . . . . .
31
3.2.11 Epic 11: Quản lý người dùng và hệthống (Admin) . .
31
3.2.12 Epic 12: Thống kê và báo cáo hệthống (Admin)
. . .
32
3.3
Yêu cầu phi chức năng . . . . . . . . . . . . . . . . . . . .
32
3.4
Lược đồuse-case (UC) . . . . . . . . . . . . . . . . . . . .
34
3.4.1
Lược đồuse-case đối với student . . . . . . . . . . . .
34
3.4.1.1
UC-02: Thiết lập mục tiêu học tập . . . . . .
35
3.4.1.2
UC-03: Gợi ý lộtrình học cá nhân . . . . . .
38
3.4.1.3
UC-50: Hỗtrợ học tập từ trợ lý ảo . . . . . .
41
3.4.2
Lược đồuse-case đối với giảng viên . . . . . . . . . .
44
3.4.2.1
UC-23: Xây dựng cấu trúc khóa học . . . . .
45
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang iv/254

=== SUMMARY ===
Phần này tóm tắt kết quả khảo sát thực tế và trình bày chi tiết giai đoạn phân tích hệ thống cho một nền tảng giáo dục. Nội dung bao quát các bên liên quan (sinh viên, giảng viên, admin, bên thứ 3) cùng 12 nhóm yêu cầu chức năng (Epic) trọng tâm như: cá nhân hóa lộ trình học, hỗ trợ AI cho người học và người dạy, quản lý khóa học và hệ thống báo cáo. Ngoài ra, tài liệu còn liệt kê các yêu cầu phi chức năng và sơ đồ Use-case chi tiết.

=== REVIEW QUESTIONS ===
1. Các bên liên quan (Stakeholders) chính trong hệ thống giáo dục này bao gồm những nhóm đối tượng nào?
2. Hệ thống hỗ trợ sinh viên trong việc cá nhân hóa học tập thông qua những yêu cầu chức năng (Epic) và Use-case cụ thể nào?
3. Vai trò của Trợ lý AI (Virtual Assistant) được định nghĩa như thế nào đối với cả sinh viên và giảng viên?
4. Hệ thống quản lý và báo cáo dành cho Quản trị viên (Admin) được mô tả qua các Epic nào trong tài liệu?','6a878d05-7474-495a-91e9-e5780a0e081a'::uuid,NULL,NULL,5,710,'2026-03-21 13:35:13.31666+07'),
	 ('dacd67e6-e67c-4366-85e8-a20b5f16b931'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,5,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
3.4.2.2
UC-26: Tạo bài tập . . . . . . . . . . . . . . .
46
3.4.2.3
UC-28: Chấm điểm và nhận xét . . . . . . .
48
3.4.3
Lược đồuse-case đối với admin . . . . . . . . . . . .
49
3.4.3.1
UC-36: Xem báo cáo . . . . . . . . . . . . .
50
3.4.3.2
UC-43: Cấu hình truy cập theo vai trò . . . .
52
3.4.3.3
UC-53: Xem log hệthống
. . . . . . . . . .
53
3.5
Sơ đồtuần tự(Sequence Diagram) . . . . . . . . . . . .
55
3.5.1
Sơ đồtuần tựlàm bài tập và kiểm tra
. . . . . . . . .
55
3.5.2
Sơ đồtuần tựtạo đềkiểm tra bằng AI . . . . . . . . .
56
4
Thiết kếhệthống
59
4.1
Cơ sởdữliệu (Database) . . . . . . . . . . . . . . . . . .
59
4.1.1
Sơ đồEERD (Enhanced Entity-Relationship Diagram)
59
4.1.2
Ánh xạEERD sang mô hình quan hệ(Relational Model)
tương ứng với từng service . . . . . . . . . . . . . . .
61
4.1.2.1
User Management Service . . . . . . . . . .
61
4.1.2.2
Communication Service . . . . . . . . . . .
62
4.1.2.3
Assessment Service
. . . . . . . . . . . . .
63
4.1.2.4
Learning Service . . . . . . . . . . . . . . .
64
4.1.2.5
Notification Service
. . . . . . . . . . . . .
65
4.1.2.6
Course Management Service . . . . . . . . .
65
4.2
Kiến trúc hệthống . . . . . . . . . . . . . . . . . . . . . .
67
4.2.1
So sánh các mô hình kiến trúc phần mềm phổbiến . .
67
4.2.2
Lựa chọn kiến trúc hệthống . . . . . . . . . . . . . .
71
5
Cá nhân hóa học tập cho sinh viên và kho dữliệu môn học cho
Coaching Chatbot
76
5.1
Mô hình dựđoán kết quảmôn học theo năng lực . . . . . . .
76
5.1.1
Chuẩn bịdữliệu . . . . . . . . . . . . . . . . . . . .
77
5.1.2
Xây dựng mô hình dựđoán kết quảmôn học
. . . . .
84
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang v/254

=== SUMMARY ===
Phân đoạn này tập trung vào thiết kế chi tiết hệ thống quản lý học tập, bao gồm lược đồ use-case cho giảng viên và quản trị viên, các sơ đồ tuần tự và thiết kế cơ sở dữ liệu theo kiến trúc microservices (như quản lý người dùng, đánh giá, và học tập). Tài liệu cũng trình bày việc so sánh các kiến trúc phần mềm và ứng dụng AI trong việc tạo đề thi tự động cũng như xây dựng mô hình dự đoán kết quả học tập để cá nhân hóa trải nghiệm cho sinh viên.

=== REVIEW QUESTIONS ===
1. Những chức năng (use-case) chính nào được thiết kế dành riêng cho vai trò quản trị viên (Admin)?
2. Cơ sở dữ liệu của hệ thống được chia thành các dịch vụ (services) cụ thể nào?
3. Vai trò của AI được thể hiện như thế nào trong quy trình làm bài tập và kiểm tra?
4. Mục tiêu của việc xây dựng mô hình dự đoán kết quả môn học trong hệ thống này là gì?','0278d29a-a7cc-40e9-b100-49c44bcb409e'::uuid,NULL,NULL,6,653,'2026-03-21 13:35:13.31666+07');
INSERT INTO coaching_chatbot.lecture_knowledge_chunks (lecture_knowledge_chunks_id,lecture_knowledge_id,chunk_index,chunk_content,qdrant_point_id,start_time_seconds,end_time_seconds,page_number,token_count,created_at) VALUES
	 ('df176c3e-448e-48e8-affc-e7797b84d283'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,6,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
5.1.3
Kết quảvà thảo luận . . . . . . . . . . . . . . . . . . . .
86
5.1.4
Ước lượng phân phối xác suất điểm trên thang 4
. . .
87
5.1.5
Kết luận . . . . . . . . . . . . . . . . . . . . . . . . .
89
5.2
Thuật toán đánh giá tính khảthi của mục tiêu học tập . . . . .
90
5.3
Định lượng giá trịnghềnghiệp của môn học
. . . . . . . . .
108
5.3.1
Cơ sởdữliệu nghềnghiệp O*NET . . . . . . . . . . .
108
5.3.2
Kỹthuật Vector hóa văn bản (Text Embedding) . . . .
108
5.3.3
Đồng nhất hóa dữliệu nghềnghiệp
. . . . . . . . . .
109
5.3.4
Chuẩn hóa hệsốtầm quan trọng (SIM) . . . . . . . . .
110
5.3.5
Kỹthuật So khớp Ngữnghĩa (Semantic Matching)
. .
111
5.3.6
Công thức tính Tổng giá trị. . . . . . . . . . . . . . .
112
5.4
Xây dựng lộtrình học cá nhân hóa cho sinh viên . . . . . . .
113
5.4.1
Pha 1: Xây dựng lộtrình cơ bản và độưu tiên môn học
113
5.4.2
Pha 2: Điều chỉnh lộtrình theo tiến độvà nguyện vọng
sinh viên . . . . . . . . . . . . . . . . . . . . . . . .
116
5.4.3
Kết luận . . . . . . . . . . . . . . . . . . . . . . . . .
121
5.5
Kho dữliệu tri thức môn học cho Coaching Chatbot . . . . .
124
6
Thiết KếGiao Diện Người Dùng
126
6.1
Giao diện sinh viên . . . . . . . . . . . . . . . . . . . . . . .
126
6.1.1
Dashboard
. . . . . . . . . . . . . . . . . . . . . . .
126
6.1.1.1
Dashboard
. . . . . . . . . . . . . . . . . . .
126
6.1.2
Quản Lý Lớp Học
. . . . . . . . . . . . . . . . . . .
128
6.1.2.1
Nội Dung Lớp Học với Chatbot
. . . . . . .
128
6.1.2.2
Nội Dung Lớp Học - Ghi Chú và Chatbot . .
129
6.1.3
Quiz và Bài Kiểm Tra
. . . . . . . . . . . . . . . . . .
129
6.1.3.1
Quiz với Chatbot . . . . . . . . . . . . . . .
129
6.1.3.2
Quiz Code
. . . . . . . . . . . . . . . . . .
130
6.1.4
Khóa Học Tăng Cường . . . . . . . . . . . . . . . . .
131
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang vi/254

=== SUMMARY ===
Đoạn trích này đề cập đến các phương pháp xây dựng hệ thống học tập cá nhân hóa, bao gồm: thuật toán đánh giá mục tiêu, kỹ thuật định lượng giá trị nghề nghiệp của môn học thông qua cơ sở dữ liệu O*NET và Text Embedding. Nội dung cũng trình bày quy trình hai pha để thiết lập lộ trình học tập, xây dựng kho tri thức cho Coaching Chatbot và thiết kế các giao diện người dùng tương tác cho sinh viên như Dashboard, quản lý lớp học và hệ thống bài kiểm tra.

=== REVIEW QUESTIONS ===
1. Hệ thống sử dụng kỹ thuật gì để thực hiện việc định lượng giá trị nghề nghiệp của môn học từ dữ liệu O*NET?
2. Quy trình xây dựng lộ trình học tập cá nhân hóa cho sinh viên được chia thành những giai đoạn (pha) nào?
3. Vai trò của ''Kho dữ liệu tri thức môn học'' đối với Coaching Chatbot là gì?
4. Giao diện sinh viên bao gồm những thành phần chức năng chính nào để hỗ trợ việc học tập và kiểm tra?','67a05f95-2be5-436d-a645-3055bc7fed4e'::uuid,NULL,NULL,7,707,'2026-03-21 13:35:13.317528+07'),
	 ('0860d363-74cb-40bb-976c-7d5bfd8165bb'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,7,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
6.1.4.1
Danh Sách Các Khóa Học Tăng Cường
. . .
131
6.1.4.2
Giới Thiệu Khóa Học Tăng Cường . . . . . .
132
6.1.5
LộTrình Học Tập Cá Nhân
. . . . . . . . . . . . . .
133
6.1.5.1
LộTrình Học Cá Nhân . . . . . . . . . . . .
133
6.1.5.2
Tạo LộTrình Học Cá Nhân
. . . . . . . . .
134
6.1.5.3
Tạo LộTrình - Bước 2 . . . . . . . . . . . .
135
6.1.5.4
Tạo LộTrình - Bước 3-1 . . . . . . . . . . .
137
6.1.5.5
Tạo LộTrình - Bước 3-2 . . . . . . . . . . .
138
6.1.5.6
Tạo LộTrình - Bước 4 . . . . . . . . . . . .
138
6.1.5.7
LộTrình Sau Khi Tạo
. . . . . . . . . . . .
139
6.1.6
Điểm Sốvà Tiến Độ. . . . . . . . . . . . . . . . . .
141
6.1.6.1
Tiến ĐộHiện Tại Môn Học
. . . . . . . . .
141
6.1.7
Kết Luận . . . . . . . . . . . . . . . . . . . . . . . .
143
6.2
Giao diện giáo viên
. . . . . . . . . . . . . . . . . . . .
143
6.2.1
Tạo mới khóa học tăng cường
. . . . . . . . . . . . .
143
6.2.1.1
Thông tin cơ bản . . . . . . . . . . . . . . .
143
6.2.1.2
Chương trình dạy học . . . . . . . . . . . . .
144
6.2.1.3
Bài giảng ghi chú (lecture notes) . . . . . . .
145
6.2.1.4
Bài giảng video . . . . . . . . . . . . . . . .
146
6.2.1.5
File đính kèm . . . . . . . . . . . . . . . . .
147
6.2.2
Quản lý câu hỏi và review . . . . . . . . . . . . . . .
148
6.2.2.1
Thêm câu hỏi bằng AI . . . . . . . . . . . .
148
6.2.2.2
Review bài quiz . . . . . . . . . . . . . . . .
150
6.2.3
Kết Luận . . . . . . . . . . . . . . . . . . . . . . . .
151
6.3
Giao diện admin . . . . . . . . . . . . . . . . . . . . . . . .
151
6.3.1
Báo cáo hệthống . . . . . . . . . . . . . . . . . . . . .
151
6.3.1.1
Báo cáo học tập . . . . . . . . . . . . . . . .
151
6.3.1.2
Báo cáo hoạt động người dùng . . . . . . . .
152
6.3.2
Quản lý người dùng
. . . . . . . . . . . . . . . . . .
154
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang vii/254

=== SUMMARY ===
Phần này liệt kê cấu trúc thiết kế giao diện người dùng cho ba đối tượng chính: sinh viên, giáo viên và quản trị viên. Nội dung tập trung vào các tính năng như quy trình tạo lộ trình học tập cá nhân hóa, quản lý khóa học tăng cường với sự hỗ trợ của AI, và các công cụ báo cáo hệ thống. Đây là tài liệu hướng dẫn về mặt chức năng và trải nghiệm người dùng trong hệ thống quản lý học tập cá nhân hóa.

=== REVIEW QUESTIONS ===
1. Quy trình tạo lộ trình học tập cá nhân hóa cho sinh viên gồm bao nhiêu bước chính theo mục lục?
2. Những loại tài liệu nào giáo viên có thể đính kèm khi tạo mới một khóa học tăng cường?
3. Ứng dụng của Trí tuệ nhân tạo (AI) được thể hiện như thế nào trong giao diện của giáo viên?
4. Quản trị viên (admin) có thể truy cập những loại báo cáo hệ thống nào để theo dõi hoạt động học tập?','b8605ec5-97e0-421d-8ee5-319ae7662287'::uuid,NULL,NULL,8,689,'2026-03-21 13:35:13.317528+07'),
	 ('15a81b68-3d67-4651-90b8-d6572990050f'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,8,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
6.3.2.1
Danh sách người dùng . . . . . . . . . . . .
154
6.3.3
Quản lý khóa học . . . . . . . . . . . . . . . . . . . .
155
6.3.3.1
Quản lý khóa học – Danh sách chương trình học155
6.3.3.2
Tạo mới học kỳ– Danh sách môn học (Im-
port/Export)
. . . . . . . . . . . . . . . . .
155
6.3.4
Quản trịhệthống . . . . . . . . . . . . . . . . . . . .
156
6.3.4.1
Quản lý truy cập dựa trên vai trò . . . . . . .
156
6.3.5
Kết Luận . . . . . . . . . . . . . . . . . . . . . . . .
158
7
Tổng kết
159
7.1
Kết quảđạt được . . . . . . . . . . . . . . . . . . . . . .
159
7.2
Những hạn chếcủa đềtài
. . . . . . . . . . . . . . . .
160
7.3
Kếhoạch phát triển trong giai đoạn tiếp theo . . . . . . . . .
161
A Lược đồUse-Case
165
A.1 Lược đồuse-case đối với sinh viên
. . . . . . . . . . . . . .
165
A.1.1 UC-01: Theo dõi học tập cá nhân
. . . . . . . . . . .
165
A.1.2 UC-04: Xem lộtrình học . . . . . . . . . . . . . . . . .
167
A.1.3 UC-05: Xem lịch trình học tập . . . . . . . . . . . . . .
170
A.1.4 UC-06: Điều chỉnh lộtrình học
. . . . . . . . . . . .
172
A.1.5 UC-09: Khám phá khóa học . . . . . . . . . . . . . . .
175
A.1.6 UC-11: Đăng ký khóa học . . . . . . . . . . . . . . . .
177
A.1.7 UC-46: Truy cập tài nguyên khóa học . . . . . . . . . .
178
A.1.8 UC-47: Xem tiến độhọc tập . . . . . . . . . . . . . . .
179
A.1.9 UC-48: Làm bài tập và kiểm tra . . . . . . . . . . . .
180
A.1.10 UC-49: Tra cứu điểm và nhận xét
. . . . . . . . . . .
182
A.1.11 UC-51: Tạo bài kiểm tra, ôn tập bằng AI
. . . . . . .
182
A.1.12 UC-52: Hỏi đáp kiến thức trong khóa học . . . . . . .
184
A.1.13 UC-54: Tùy chỉnh thông báo . . . . . . . . . . . . . .
185
A.2 Lược đồuse-case đối với giảng viên . . . . . . . . . . . . . .
186
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang viii/254

=== SUMMARY ===
Phân đoạn tài liệu này liệt kê các nội dung về quản trị hệ thống, tổng kết đề tài và lược đồ use-case. Các mục tiêu chính bao gồm quản lý người dùng, khóa học, và phân quyền dựa trên vai trò. Tài liệu cũng trình bày kết quả, hạn chế và hướng phát triển tương lai. Đặc biệt, phần phụ lục chi tiết hóa các hành động của sinh viên như theo dõi lộ trình học tập, tương tác với tài nguyên và sử dụng AI để hỗ trợ ôn tập.

=== REVIEW QUESTIONS ===
1. Các chức năng quản trị hệ thống dành cho Admin bao gồm những mục quan trọng nào?
2. Phần tổng kết đề tài tập trung vào những khía cạnh nào để đánh giá dự án?
3. Lược đồ use-case dành cho sinh viên hỗ trợ những hoạt động học tập cá nhân hóa nào?
4. Công nghệ AI được ứng dụng trong những trường hợp sử dụng (use-case) cụ thể nào đối với sinh viên?','4c88d9a1-6b43-4c19-ac64-ee2874bdae8e'::uuid,NULL,NULL,9,673,'2026-03-21 13:35:13.318054+07'),
	 ('15022346-646a-4cd9-9f91-6b0171d11fa8'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,36,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
b. Mức độhài lòng với LMS hiện tại
Hình 2.6: Mức độhài lòng của người dùng với LMS hiện tại.
Kết quảkhảo sát cho thấy mức độhài lòng của người dùng đối với hệthống LMS
hiện tại chủyếu ởmức trung bình (mức 3), chiếm gần một nửa tổng sốngười tham gia
khảo sát (48,5%). Sốlượng người dùng đánh giá ởmức cao (mức 4 và 5) chiếm khoảng
45,5%, trong khi tỷlệđánh giá thấp (mức 1 và 2) chỉchiếm 5%.
Điều này cho thấy mặc dù hệthống đã đáp ứng được nhu cầu cơ bản của đa sốngười
dùng, vẫn còn nhiều cơ hội đểcải thiện trải nghiệm và nâng cao mức độhài lòng, đặc
biệt trong các khía cạnh liên quan đến tính ổn định, giao diện thân thiện, tốc độxửlý và
các tính năng hỗtrợhọc tập nâng cao.
c. Mức độhài lòng với các tính năng chính của LMS
Hình 2.7: Đánh giá các khía cạnh quan trọng của LMS.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 18/254

=== SUMMARY ===
Đoạn văn bản trình bày kết quả khảo sát về mức độ hài lòng của người dùng đối với hệ thống LMS tại Trường Đại học Bách khoa TP.HCM. Phần lớn người dùng đánh giá ở mức trung bình (48,5%) hoặc mức cao (45,5%), trong khi tỷ lệ không hài lòng rất thấp (5%). Mặc dù hệ thống đáp ứng tốt các nhu cầu cơ bản, báo cáo nhấn mạnh nhu cầu cải thiện về tính ổn định, giao diện, tốc độ xử lý và các tính năng hỗ trợ học tập chuyên sâu để nâng cao trải nghiệm.

=== REVIEW QUESTIONS ===
1. Tỷ lệ người dùng đánh giá mức độ hài lòng với hệ thống LMS ở mức trung bình (mức 3) là bao nhiêu?
2. Nhóm người dùng đánh giá mức độ hài lòng ở mức cao (mức 4 và 5) chiếm tổng cộng bao nhiêu phần trăm?
3. Dựa trên kết quả khảo sát, hệ thống LMS hiện tại đã đáp ứng được những yêu cầu gì của người dùng?
4. Những khía cạnh cụ thể nào của hệ thống cần được cải thiện để nâng cao mức độ hài lòng của sinh viên và giảng viên?','75e7b63a-8aa1-4293-a66b-b97f1c358532'::uuid,NULL,NULL,37,468,'2026-03-21 13:35:13.322607+07'),
	 ('b52e96c0-9cc8-44c6-9843-262ed72a36f6'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,9,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
A.2.1 UC-17: Nhắn tin và thảo luận
. . . . . . . . . . . . .
186
A.2.2 UC-20: Tạo khóa học phụtrợ
. . . . . . . . . . . . .
187
A.2.3 UC-201: Tạo mới hoàn toàn khóa học . . . . . . . . .
188
A.2.4 UC-202: Tạo từkhóa học cũ . . . . . . . . . . . . . .
189
A.2.5 UC-22: Xem thông tin khóa học . . . . . . . . . . . .
190
A.2.6 UC-24: Thiết lập hệthống thang điểm . . . . . . . . .
192
A.2.7 UC-25: Tải lên và cập nhật tài liệu . . . . . . . . . . .
193
A.2.8 UC-27: Chỉnh sửa bài tập
. . . . . . . . . . . . . . .
194
A.2.9 UC-29: Quản lý khoá học
. . . . . . . . . . . . . . .
196
A.3 Lược đồuse-case đối với admin . . . . . . . . . . . . . . .
197
A.3.1 UC-24: Tạo và gửi thông báo . . . . . . . . . . . . . .
197
A.3.2 UC-38: Báo cáo lượt học, tần suất đăng nhập . . . . .
199
A.3.3 UC-39: Quản lý người dùng . . . . . . . . . . . . . . .
200
A.3.4 UC-40: Tạo/sửa/xóa tài khoản . . . . . . . . . . . . . .
202
A.3.5 UC-41: Import/export dữliệu người dùng . . . . . . .
204
A.3.6 UC-42: Phân quyền . . . . . . . . . . . . . . . . . . .
206
B Giao diện người dùng
208
B.1 Giao diện sinh viên . . . . . . . . . . . . . . . . . . . . . .
208
B.1.1 Quản Lý Lớp Học
. . . . . . . . . . . . . . . . . . .
208
B.1.1.1 Danh Sách Lớp Học - Card View
. . . . . .
208
B.1.2 Quiz và Bài Kiểm Tra
. . . . . . . . . . . . . . . . .
209
B.1.2.1 Quiz Trắc Nghiệm - Chọn Đáp Án . . . . . .
209
B.1.2.2 Quiz Trắc Nghiệm - Xem Lại Bài Làm
. . .
210
B.1.2.3 Quiz Trắc Nghiệm - Đã Hoàn Thành (1 Lần
Làm)
. . . . . . . . . . . . . . . . . . . . .
211
B.1.2.4 Quiz Trắc Nghiệm - Nhiều Lần Làm . . . . .
212
B.1.2.5 Quiz Code - Xem Lại Bài Làm . . . . . . . .
213
B.1.3 Bài Tập Lớn
. . . . . . . . . . . . . . . . . . . . . .
214
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang ix/254

=== SUMMARY ===
Đoạn trích liệt kê các kịch bản sử dụng (Use-Case) và thiết kế giao diện người dùng cho hệ thống quản lý học tập. Nội dung tập trung vào ba đối tượng chính: giảng viên (quản lý khóa học, tài liệu, điểm số), quản trị viên (quản lý người dùng, phân quyền, báo cáo hệ thống) và sinh viên (giao diện lớp học, làm bài kiểm tra trắc nghiệm và lập trình). Đây là phần mục lục chi tiết mô tả các chức năng nghiệp vụ và thành phần giao diện của đồ án.

=== REVIEW QUESTIONS ===
1. Giảng viên có thể tạo khóa học phụ trợ thông qua những phương thức cụ thể nào theo lược đồ Use-Case?
2. Các chức năng quản trị viên (Admin) cần thực hiện để quản lý dữ liệu người dùng một cách hệ thống là gì?
3. Giao diện sinh viên hỗ trợ những loại bài tập và bài kiểm tra (Quiz) cụ thể nào?
4. Lược đồ Use-Case của quản trị viên bao gồm các loại báo cáo nào để theo dõi tương tác của người dùng?','328924a5-499d-48f9-9347-782aca5a034a'::uuid,NULL,NULL,10,692,'2026-03-21 13:35:13.318054+07'),
	 ('c2868521-f0c9-4196-b24f-db7be52581a9'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,10,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
B.1.3.1 Bài Tập Lớn
. . . . . . . . . . . . . . . . .
214
B.1.3.2 Bài Tập Lớn - Nộp File . . . . . . . . . . . .
215
B.1.3.3 Bài Tập Lớn Dạng Quiz
. . . . . . . . . . .
216
B.1.4 Điểm Sốvà Tiến Độ. . . . . . . . . . . . . . . .
217
B.1.4.1 Bảng Điểm . . . . . . . . . . . . . . . . . .
217
B.1.5 Diễn Đàn . . . . . . . . . . . . . . . . . . . .
218
B.1.5.1 Diễn Đàn - Popup Theo Dõi . . . . . . . . .
218
B.1.5.2 Diễn Đàn - Topic CụThể. . . . . . . . . . .
219
B.1.6 Thông Tin Cá Nhân
. . . . . . . . . . . . . . . .
220
B.1.6.1 Thông Tin Cá Nhân
. . . . . . . . . . . . .
220
B.1.7 Thời Khóa Biểu
. . . . . . . . . . . . . . . .
221
B.1.7.1 Thời Khóa Biểu . . . . . . . . . . . . . . . .
221
B.2 Giao diện giáo viên
. . . . . . . . . . . . . . . . . . . .
223
B.2.1 Khám phá lớp học
. . . . . . . . . . . . . . . .
223
B.2.1.1 Lớp học của tôi . . . . . . . . . . . . . . . .
223
B.2.1.2 Danh sách lớp học - Dạng card . . . . . . . .
224
B.2.1.3 Popup thông báo lớp học . . . . . . . . . . .
225
B.2.1.4 Nội dung thông báo lớp học . . . . . . . . . .
226
B.2.2 Chi tiết lớp học . . . . . . . . . . . . . . . . . . . . .
227
B.2.2.1 Danh sách bài tập lớn - Card . . . . . . . . .
227
B.2.2.2 Danh sách bài tập lớn - Danh sách . . . . . .
228
B.2.2.3 Chi tiết bài tập lớn . . . . . . . . . . . . . . .
229
B.2.2.4 Chi tiết bài tập lớn của sinh viên . . . . . . .
230
B.2.2.5 Danh sách sinh viên làm quiz
. . . . . . . .
231
B.2.2.6 Diễn đàn lớp học . . . . . . . . . . . . . . .
232
B.2.2.7 Popup theo dõi diễn đàn . . . . . . . . . . .
233
B.2.2.8 Topic diễn đàn cụthể. . . . . . . . . . . . .
234
B.2.2.9 Thêm topic diễn đàn . . . . . . . . . . . . .
235
B.2.3 Thời khóa biểu . . . . . . . . . . . . . . . . . . . . .
236
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang x/254

=== SUMMARY ===
Đoạn văn bản này liệt kê cấu trúc mục lục về giao diện người dùng của một hệ thống quản lý học tập (LMS). Nội dung tập trung vào các tính năng dành cho sinh viên (nộp bài tập lớn, theo dõi điểm số, diễn đàn và thời khóa biểu) và các chức năng quản lý dành cho giáo viên (quản lý lớp học, theo dõi bài tập lớn, quản lý danh sách sinh viên làm bài quiz và tương tác trên diễn đàn lớp học). Đây là phần mô tả chi tiết các thành phần giao diện trong báo cáo đồ án.

=== REVIEW QUESTIONS ===
1. Hệ thống cung cấp những hình thức nộp bài tập lớn nào cho sinh viên theo danh sách mục lục?
2. Trong giao diện giáo viên, mục ''Chi tiết lớp học'' bao gồm các tính năng nào để quản lý bài tập của sinh viên?
3. Các thành phần nào trong mục ''Diễn đàn'' hỗ trợ việc theo dõi và tương tác giữa các thành viên?
4. Dựa trên mục lục, giáo viên có thể quản lý thông tin sinh viên thông qua những chức năng cụ thể nào liên quan đến bài kiểm tra (quiz) và bài tập lớn?','fd7683d9-cf91-4a3b-b9d7-55c426f9561a'::uuid,NULL,NULL,11,715,'2026-03-21 13:35:13.318054+07'),
	 ('e4c1544e-8042-49be-8795-4e16eaaf8eeb'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,11,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
B.2.3.1 Lịch tuần biểu
. . . . . . . . . . . . . . . .
236
B.2.3.2 Lịch học kỳ. . . . . . . . . . . . . . . . . .
237
B.2.4 Quản lý câu hỏi . . . . . . . . . . . . . . . . . . . . .
238
B.2.4.1 Thêm câu hỏi thủcông - Trắc nghiệm . . . .
238
B.2.4.2 Thêm câu hỏi thủcông - Tựluận . . . . . . .
239
B.2.4.3 Thêm câu hỏi thủcông - Code . . . . . . . .
240
B.2.4.4 Thêm câu hỏi bằng file . . . . . . . . . . . .
241
B.2.5 Cấu hình điểm số. . . . . . . . . . . . . . . . . . . .
242
B.2.5.1 Cấu hình điểm số. . . . . . . . . . . . . . .
242
B.3 Giao diện admin . . . . . . . . . . . . . . . . . . . . . .
242
B.3.1 Quản lý người dùng
. . . . . . . . . . . . . . . . . .
243
B.3.1.1 Popup thêm mới người dùng và chỉnh sửa người
dùng
. . . . . . . . . . . . . . . . . . . . .
243
B.3.1.2 Import người dùng . . . . . . . . . . . . . .
244
B.3.2 Quản lý khóa học . . . . . . . . . . . . . . . . . . . .
246
B.3.2.1 Tạo mới học kỳ– Chỉnh sửa chi tiết lớp của
một môn
. . . . . . . . . . . . . . . . . . . .
246
B.3.3 Quản lý thông báo . . . . . . . . . . . . . . . . . . .
248
B.3.3.1 Danh sách thông báo . . . . . . . . . . . . .
248
B.3.3.2 Tạo mới thông báo . . . . . . . . . . . . . .
249
B.3.4 Quản trịhệthống . . . . . . . . . . . . . . . . . . . .
250
B.3.4.1 Nhật ký hệthống (Danh sách log) . . . . . .
250
Tài liệu tham khảo
252
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang xi/254

=== SUMMARY ===
Nội dung này liệt kê các thành phần giao diện quản trị dành cho giảng viên và người quản lý hệ thống (Admin) trong một hệ thống quản lý học tập (LMS). Các tính năng trọng tâm bao gồm quản lý lịch học, ngân hàng câu hỏi đa dạng (trắc nghiệm, tự luận, lập trình), cấu hình thang điểm, cùng các tác vụ quản trị như quản lý người dùng, khóa học, thông báo và giám sát nhật ký hệ thống.

=== REVIEW QUESTIONS ===
1. Giảng viên có thể thêm các loại câu hỏi nào vào hệ thống LMS theo danh sách mục lục?
2. Các phương thức nào được hỗ trợ để đưa thông tin người dùng mới vào hệ thống quản lý?
3. Trong phần quản lý khóa học của Admin, những chức năng cụ thể nào liên quan đến học kỳ và lớp học được đề cập?
4. Công cụ nào giúp người quản trị theo dõi các hoạt động đã diễn ra trên hệ thống?','835925b8-16e6-460f-9bc8-644276a687b1'::uuid,NULL,NULL,12,576,'2026-03-21 13:35:13.318816+07'),
	 ('785596f9-0e25-4fe5-b03d-89a68ddb7fa5'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,12,'=== ORIGINAL CONTENT ===
Trường Đại Học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa Học & KỹThuật Máy Tính
Danh sách hình vẽ
Hình 2.1
Giao diện trang chủCoursera [1]. . . . . . . . . . . .
4
Hình 2.2
Giao diện trang chủUdemy [2]. . . . . . . . . . . . .
8
Hình 2.3
Giao diện người dùng LinkedIn Learning [3]. . . . . .
12
Hình 2.4
Giao diện lộtrình nghềnghiệp trên LinkedIn Learning
[3]. . . . . . . . . . . . . . . . . . . . . . . . . . . .
14
Hình 2.5
Tần suất sửdụng hệthống LMS của sinh viên và giảng
viên.
. . . . . . . . . . . . . . . . . . . . . . . . . .
17
Hình 2.6
Mức độhài lòng của người dùng với LMS hiện tại. . .
18
Hình 2.7
Đánh giá các khía cạnh quan trọng của LMS. . . . . .
18
Hình 2.8
Đánh giá các khía cạnh quan trọng của LMS. . . . . .
19
Hình 2.9
Mục tiêu chính khi sửdụng LMS. . . . . . . . . . . .
20
Hình 2.10
Kỳvọng của người dùng vềcác tính năng mới.
. . . .
21
Hình 3.1
Sơ đồusecase đối với student
. . . . . . . . . . . . .
34
Hình 3.2
Sơ đồusecase đối với giảng viên . . . . . . . . . . . .
44
Hình 3.3
Sơ đồusecase đối với admin . . . . . . . . . . . . . .
50
Hình 3.4
Sơ đồtuần tựcho UC-48: Làm bài tập và kiểm tra
. .
56
Hình 3.5
Sơ đồtuần tựtạo đềkiểm tra bằng AI . . . . . . . . .
57
Hình 4.1
Sơ đồEERD của hệthống LMS . . . . . . . . . . . .
60
Hình 4.2
Mô hình quan hệcủa User Management Service
. . .
61
Hình 4.3
Mô hình quan hệcủa Communication Service
. . . .
62
Hình 4.4
Mô hình quan hệcủa Assessment Service . . . . . . .
63
Hình 4.5
Mô hình quan hệcủa Learning Service
. . . . . . . .
64
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang xii/254

=== SUMMARY ===
Danh sách hình vẽ này liệt kê các minh họa quan trọng trong báo cáo đồ án về hệ thống Quản lý học tập (LMS). Nội dung bao gồm: phân tích giao diện các nền tảng học trực tuyến phổ biến (Coursera, Udemy, LinkedIn Learning), kết quả khảo sát người dùng về mức độ hài lòng và kỳ vọng, các sơ đồ thiết kế hệ thống như sơ đồ Use Case cho từng đối tượng (sinh viên, giảng viên, admin), sơ đồ tuần tự và mô hình thực thể quan hệ (EERD) cho các dịch vụ cốt lõi.

=== REVIEW QUESTIONS ===
1. Dựa vào danh sách, những nền tảng học trực tuyến nào đã được nhóm tác giả nghiên cứu về giao diện người dùng?
2. Các sơ đồ Use Case trong báo cáo tập trung vào những đối tượng người dùng cụ thể nào?
3. Hệ thống LMS này bao gồm những dịch vụ (services) chính nào được thể hiện qua các mô hình quan hệ ở chương 4?
4. Tính năng nào liên quan đến trí tuệ nhân tạo (AI) được đề cập trong sơ đồ tuần tự của hệ thống?','78cc4c02-ecc0-4d4b-9596-845206ef2cc4'::uuid,NULL,NULL,13,628,'2026-03-21 13:35:13.318816+07'),
	 ('0f65f32d-652b-4bfb-b3c9-c4796e1607a2'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,13,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Hình 4.6
Mô hình quan hệcủa Notification Service . . . . . . .
65
Hình 4.7
Mô hình quan hệcủa Course Management Service . .
66
Hình 4.8
Mô hình kiến trúc phân lớp
. . . . . . . . . . . . . .
67
Hình 4.9
Mô hình kiến trúc máy khách – máy chủ
. . . . . . .
68
Hình 4.10
Mô hình kiến trúc đường ống
. . . . . . . . . . . . .
69
Hình 4.11
Mô hình kiến trúc hướng sựkiện . . . . . . . . . . . .
70
Hình 4.12
Mô hình kiến trúc microservices . . . . . . . . . . . .
70
Hình 4.13
Kiến trúc microservices cho hệthống LMS . . . . . .
75
Hình 5.1
Phân phối sai sốdựđoán trên thang 4 của mô hình
Ridge (tập test). . . . . . . . . . . . . . . . . . . . . .
87
Hình 5.2
Flowchart của thuật toán đánh giá mức độkhảthi của
mục tiêu học tập . . . . . . . . . . . . . . . . . . . .
106
Hình 5.3
Flowchart của thuật toán đánh giá mức độkhảthi của
mục tiêu học tập . . . . . . . . . . . . . . . . . . . .
107
Hình 5.4
Flowchart của thuật toán xây dựng lộtrình học cá nhân
hóa . . . . . . . . . . . . . . . . . . . . . . . . . . .
122
Hình 5.5
Flowchart của thuật toán xây dựng lộtrình học cá nhân
hóa . . . . . . . . . . . . . . . . . . . . . . . . . . .
123
Hình 5.6
Mô hình RAG kết hợp LLM và kho dữliệu tri thức [4]
124
Hình 6.1
Dashboard - Trang chủcủa sinh viên
. . . . . . . . .
127
Hình 6.2
Nội dung lớp học với tích hợp Chatbot . . . . . . . . .
128
Hình 6.3
Nội dung lớp học với tính năng Ghi chú và Chatbot . .
129
Hình 6.4
Quiz với Chatbot hỗtrợ
. . . . . . . . . . . . . . . .
130
Hình 6.5
Quiz Code - Giao diện làm bài lập trình . . . . . . . .
131
Hình 6.6
Danh sách các khóa học tăng cường . . . . . . . . . .
132
Hình 6.7
Trang giới thiệu khóa học tăng cường . . . . . . . . .
133
Hình 6.8
Lộtrình học cá nhân . . . . . . . . . . . . . . . . . .
134
Hình 6.9
Tạo lộtrình học cá nhân - Màn hình bắt đầu . . . . . .
135
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang xiii/254

=== SUMMARY ===
Đoạn văn bản liệt kê danh sách các hình vẽ trong báo cáo đồ án về hệ thống Quản lý Học tập (LMS). Nội dung bao gồm các mô hình kiến trúc hệ thống (như microservices, hướng sự kiện), mô hình quan hệ dữ liệu cho các dịch vụ thông báo và quản lý khóa học. Ngoài ra, tài liệu còn đề cập đến các thuật toán đánh giá mức độ khả thi của mục tiêu học tập, xây dựng lộ trình cá nhân hóa, và việc tích hợp công nghệ AI/Chatbot vào giao diện người dùng.

=== REVIEW QUESTIONS ===
1. Hệ thống LMS được đề cập sử dụng những loại kiến trúc phần mềm nào?
2. Những dịch vụ (service) cụ thể nào đã có mô hình quan hệ được liệt kê trong danh sách?
3. Các thuật toán AI và mô hình ngôn ngữ lớn (LLM) được ứng dụng như thế nào trong hệ thống này?
4. Giao diện người dùng của sinh viên tích hợp những tính năng hỗ trợ thông minh nào?','200d652a-ea5d-4e94-a077-0b04349b3692'::uuid,NULL,NULL,14,705,'2026-03-21 13:35:13.318816+07'),
	 ('5eca9497-4307-496e-81bf-d0375211c9a0'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,14,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Hình 6.10
Tạo lộtrình học cá nhân - Bước 2 . . . . . . . . . . .
136
Hình 6.11
Tạo lộtrình học cá nhân - Bước 3 . . . . . . . . . . .
137
Hình 6.12
Tạo lộtrình học cá nhân - Bước 3-2 . . . . . . . . . .
138
Hình 6.13
Tạo lộtrình học cá nhân - Bước 4 . . . . . . . . . . .
139
Hình 6.14
Lộtrình học tập sau khi tạo
. . . . . . . . . . . . . .
140
Hình 6.15
Tiến độhiện tại môn học . . . . . . . . . . . . . . . .
142
Hình 6.16
Thông tin cơ bản khóa học tăng cường . . . . . . . . .
144
Hình 6.17
Chương trình dạy học
. . . . . . . . . . . . . . . . .
145
Hình 6.18
Bài giảng ghi chú (lecture notes) . . . . . . . . . . . .
146
Hình 6.19
Bài giảng video . . . . . . . . . . . . . . . . . . . . .
147
Hình 6.20
File đính kèm . . . . . . . . . . . . . . . . . . . . . .
148
Hình 6.21
Thêm câu hỏi bằng AI . . . . . . . . . . . . . . . . .
149
Hình 6.22
Review bài quiz
. . . . . . . . . . . . . . . . . . . .
150
Hình 6.23
Giao diện báo cáo học tập . . . . . . . . . . . . . . .
152
Hình 6.24
Giao diện báo cáo hoạt động người dùng
. . . . . . .
153
Hình 6.25
Giao diện danh sách người dùng . . . . . . . . . . . .
154
Hình 6.26
Giao diện danh sách chương trình học . . . . . . . . .
155
Hình 6.27
Giao diện tạo mới học kỳ- danh sách môn học . . . .
156
Hình 6.28
Giao diện quản lý truy cập dựa trên vai trò
. . . . . .
157
Hình B.1
Danh sách lớp học - Hiển thịdạng Card . . . . . . . .
209
Hình B.2
Quiz trắc nghiệm - Giao diện chọn đáp án . . . . . . .
210
Hình B.3
Quiz trắc nghiệm - Xem lại bài làm . . . . . . . . . .
211
Hình B.4
Quiz trắc nghiệm - Đã hoàn thành (1 lần làm) . . . . .
212
Hình B.5
Quiz trắc nghiệm - Cho phép làm nhiều lần . . . . . .
213
Hình B.6
Quiz Code - Xem lại bài làm . . . . . . . . . . . . . .
214
Hình B.7
Giao diện Bài tập lớn . . . . . . . . . . . . . . . . . .
215
Hình B.8
Bài tập lớn - Giao diện nộp file
. . . . . . . . . . . .
216
Hình B.9
Bài tập lớn dạng Quiz
. . . . . . . . . . . . . . . . .
217
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang xiv/254

=== SUMMARY ===
Danh sách hình vẽ này liệt kê các thành phần giao diện quan trọng của một hệ thống quản lý học tập (LMS). Nội dung bao gồm quy trình tạo lộ trình học tập cá nhân hóa, quản lý nội dung bài giảng (video, ghi chú), tích hợp công nghệ AI để tạo câu hỏi kiểm tra, và các chức năng quản trị như báo cáo hoạt động, quản lý người dùng theo vai trò. Ngoài ra, tài liệu còn minh họa các giao diện làm bài trắc nghiệm, lập trình và nộp bài tập lớn.

=== REVIEW QUESTIONS ===
1. Quy trình tạo lộ trình học tập cá nhân hóa trong hệ thống này bao gồm ít nhất bao nhiêu bước dựa trên danh sách hình vẽ?
2. Hệ thống hỗ trợ những định dạng bài giảng và tài liệu học tập cụ thể nào cho sinh viên?
3. Công nghệ AI được ứng dụng cụ thể vào tính năng nào trong việc quản trị nội dung học tập?
4. Bên cạnh bài trắc nghiệm thông thường, hệ thống còn hỗ trợ những hình thức kiểm tra hoặc nộp bài tập nào khác?','7c090efa-bb5e-4d42-bcad-36621389c1d3'::uuid,NULL,NULL,15,754,'2026-03-21 13:35:13.319403+07');
INSERT INTO coaching_chatbot.lecture_knowledge_chunks (lecture_knowledge_chunks_id,lecture_knowledge_id,chunk_index,chunk_content,qdrant_point_id,start_time_seconds,end_time_seconds,page_number,token_count,created_at) VALUES
	 ('7124d078-a162-4725-a036-c40bbd4e975f'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,15,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Hình B.10
Bảng điểm của sinh viên . . . . . . . . . . . . . . . . .
218
Hình B.11
Diễn đàn - Popup theo dõi chủđề. . . . . . . . . . .
219
Hình B.12
Diễn đàn - Chi tiết chủđề
. . . . . . . . . . . . . . .
220
Hình B.13
Thông tin cá nhân của sinh viên . . . . . . . . . . . .
221
Hình B.14
Thời khóa biểu học tập . . . . . . . . . . . . . . . . .
222
Hình B.15
Thời khóa biểu - Phiên bản 3 . . . . . . . . . . . . . .
222
Hình B.16
Lớp học của tôi . . . . . . . . . . . . . . . . . . . . .
223
Hình B.17
Danh sách lớp học dạng card . . . . . . . . . . . . . .
224
Hình B.18
Thông báo lớp học . . . . . . . . . . . . . . . . . . .
225
Hình B.19
Nội dung thông báo chi tiết
. . . . . . . . . . . . . .
226
Hình B.20
Danh sách bài tập lớn trên dạng card . . . . . . . . . .
227
Hình B.21
Danh sách bài tập lớn dạng list . . . . . . . . . . . . .
228
Hình B.22
Chi tiết bài tập lớn . . . . . . . . . . . . . . . . . . .
229
Hình B.23
Chi tiết bài tập lớn của sinh viên . . . . . . . . . . . .
230
Hình B.24
Danh sách sinh viên làm quiz
. . . . . . . . . . . . .
231
Hình B.25
Diễn đàn lớp học . . . . . . . . . . . . . . . . . . . . .
232
Hình B.26
Popup theo dõi diễn đàn . . . . . . . . . . . . . . . .
233
Hình B.27
Topic diễn đàn cụthể. . . . . . . . . . . . . . . . . .
234
Hình B.28
Thêm topic diễn đàn . . . . . . . . . . . . . . . . . .
235
Hình B.29
Lịch tuần biểu chưa đồng bộ. . . . . . . . . . . . . .
236
Hình B.30
Lịch học kỳ. . . . . . . . . . . . . . . . . . . . . . .
237
Hình B.31
Thêm câu hỏi trắc nghiệm . . . . . . . . . . . . . . .
238
Hình B.32
Thêm câu hỏi tựluận . . . . . . . . . . . . . . . . . .
239
Hình B.33
Thêm câu hỏi code . . . . . . . . . . . . . . . . . . .
240
Hình B.34
Thêm câu hỏi bằng file . . . . . . . . . . . . . . . . .
241
Hình B.35
Cấu hình điểm số. . . . . . . . . . . . . . . . . . . .
242
Hình B.36
Giao diện thêm mới người dùng . . . . . . . . . . . .
243
Hình B.37
Giao diện chỉnh sửa người dùng . . . . . . . . . . . .
243
Hình B.38
Giao diện import người dùng
. . . . . . . . . . . . .
244
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang xv/254

=== SUMMARY ===
Danh mục này liệt kê các hình ảnh minh họa giao diện người dùng trong báo cáo đồ án chuyên ngành về hệ thống quản lý học tập (LMS). Nội dung bao gồm các màn hình chức năng cho sinh viên như xem bảng điểm, thời khóa biểu và diễn đàn; các công cụ cho giảng viên như tạo câu hỏi trắc nghiệm, tự luận, lập trình và cấu hình điểm số; cùng các giao diện quản trị để quản lý thông tin và nhập dữ liệu người dùng.

=== REVIEW QUESTIONS ===
1. Hệ thống LMS này hỗ trợ những hình thức tạo câu hỏi kiểm tra nào cho giảng viên?
2. Những chức năng nào liên quan đến diễn đàn lớp học được hiển thị trong danh mục hình ảnh?
3. Người quản trị hệ thống có thể thực hiện những thao tác gì đối với dữ liệu người dùng?
4. Sinh viên có thể theo dõi những loại lịch biểu và thông báo nào thông qua giao diện hệ thống?','c13676d9-b911-49b9-a9af-18d788954423'::uuid,NULL,NULL,16,764,'2026-03-21 13:35:13.319403+07'),
	 ('c6965e0b-1b4d-4545-8d74-dfbd962a837a'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,16,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹ thuật Máy tính
Hình B.39
Giao diện import người dùng với file đã tải lên . . . .
245
Hình B.40
Giao diện tạo mới học kỳ- chỉnh sửa chi tiết lớp của
một môn
. . . . . . . . . . . . . . . . . . . . . . . .
246
Hình B.41
Giao diện popup chọn giảng viên
. . . . . . . . . . .
247
Hình B.42
Giao diện danh sách thông báo . . . . . . . . . . . . .
248
Hình B.43
Giao diện tạo mới thông báo . . . . . . . . . . . . . .
249
Hình B.44
Giao diện nhật ký hệ thống . . . . . . . . . . . . . . .
250
Hình B.45
Giao diện popup chi tiết nhật ký hệ thống . . . . . . .
251
Báo cáo đồ án chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang xvi/254

=== SUMMARY ===
Đoạn văn bản liệt kê danh mục các hình ảnh minh họa về giao diện quản trị trong báo cáo đồ án chuyên ngành Khoa học & Kỹ thuật Máy tính. Các nội dung chính bao gồm giao diện nhập dữ liệu người dùng, quản lý chi tiết lớp học và học kỳ, phân bổ giảng viên, hệ thống quản lý thông báo và nhật ký hệ thống (system logs). Đây là các tính năng cốt lõi hỗ trợ việc vận hành, giám sát và quản lý dữ liệu cho một hệ thống đào tạo.

=== REVIEW QUESTIONS ===
1. Các hình từ B.39 đến B.41 mô tả những chức năng quản lý đào tạo cụ thể nào?
2. Hệ thống cung cấp những công cụ gì để hỗ trợ việc giao tiếp và truyền tải thông tin đến người dùng?
3. Việc theo dõi nhật ký hệ thống (Hình B.44 và B.45) có ý nghĩa gì trong công tác quản trị kỹ thuật?
4. Trong quy trình thiết lập học kỳ mới, người quản trị có thể tùy chỉnh những thông tin chi tiết nào cho mỗi môn học?','e47d0c67-231e-4cce-97c3-9f00cf597125'::uuid,NULL,NULL,17,397,'2026-03-21 13:35:13.319403+07'),
	 ('fc1764cb-1b99-406a-b523-840b7e7170ae'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,17,'=== ORIGINAL CONTENT ===
Trường Đại Học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa Học & KỹThuật Máy Tính
Danh sách bảng
Bảng 2.1
Đánh giá kỹthuật của Coursera. . . . . . . . . . . . .
5
Bảng 2.2
Phân tích tính năng của Coursera. . . . . . . . . . . .
6
Bảng 2.3
Đánh giá kỹthuật của Udemy. . . . . . . . . . . . . .
9
Bảng 2.4
Phân tích tính năng của Udemy. . . . . . . . . . . . .
9
Bảng 2.5
Đánh giá kỹthuật của LinkedIn Learning. . . . . . . .
13
Bảng 2.6
Phân tích tính năng của LinkedIn Learning. . . . . . .
15
Bảng 3.1
Đặc tảUC-02: Thiết lập mục tiêu học tập . . . . . . .
35
Bảng 3.2
Đặc tảUC-03: Gợi ý lộtrình học cá nhân . . . . . . .
38
Bảng 3.3
Đặc tảUC-50: Hỗtrợ học tập từtrợlý ảo . . . . . . .
41
Bảng 3.4
Đặc tảUC-23: Xây dựng cấu trúc khóa học . . . . . .
45
Bảng 3.5
Đặc tảUC-26: Tạo bài tập . . . . . . . . . . . . . . .
46
Bảng 3.6
Đặc tảUC-28: Chấm điểm và nhận xét
. . . . . . . .
48
Bảng 3.7
Đặc tảUC-36: Xem báo cáo . . . . . . . . . . . . . .
50
Bảng 3.8
Đặc tảUC-43: Cấu hình truy cập theo vai trò . . . . .
52
Bảng 3.9
Đặc tảUC-53: Xem log hệthống
. . . . . . . . . . .
53
Bảng 4.1
Các actor và actions tương ứng . . . . . . . . . . . . .
71
Bảng 5.1
So sánh hiệu năng giữa baseline và mô hình Ridge (dự
đoán trực tiếp course_grade).
. . . . . . . . . . . .
86
Bảng 5.2
Trọng sốưu tiên thứcấp theo nhóm môn học. . . . . .
115
Bảng 7.1
Timeline triển khai dự án theo mô hình Scrum–Agile .
164
Bảng A.1
Đặc tảUC-01: Theo dõi học tập cá nhân
. . . . . . .
165
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang xvii/254

=== SUMMARY ===
Phân đoạn này liệt kê danh mục các bảng biểu trong báo cáo đồ án chuyên ngành về hệ thống quản lý học tập. Nội dung bao gồm việc đánh giá kỹ thuật và tính năng của các nền tảng học trực tuyến lớn (Coursera, Udemy, LinkedIn Learning), chi tiết đặc tả các trường hợp sử dụng (Use Case) như thiết lập mục tiêu, gợi ý lộ trình, và hỗ trợ từ AI. Ngoài ra, tài liệu còn đề cập đến phân tích hiệu năng mô hình dự đoán kết quả học tập và kế hoạch triển khai theo Scrum-Agile.

=== REVIEW QUESTIONS ===
1. Những nền tảng học trực tuyến nào đã được nhóm thực hiện đồ án phân tích và đánh giá tính năng?
2. Hệ thống dự kiến tích hợp các tính năng thông minh nào để hỗ trợ cá nhân hóa việc học tập của sinh viên?
3. Mô hình Ridge được đề cập trong bảng 5.1 nhằm mục đích dự đoán chỉ số cụ thể nào trong học tập?
4. Quy trình quản lý và triển khai dự án phần mềm này được thực hiện theo mô hình nào?','40262180-6226-41c5-9789-82277732a79a'::uuid,NULL,NULL,18,615,'2026-03-21 13:35:13.319403+07'),
	 ('0979db54-8b9d-4c50-8e69-39c07d6a2020'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,18,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Bảng A.2
Đặc tảUC-04: Xem lộtrình học . . . . . . . . . . . .
167
Bảng A.3
Đặc tảUC-05: Xem lịch trình học tập (rút gọn) . . . .
170
Bảng A.4
Đặc tảUC-06: Điều chỉnh lộtrình học . . . . . . . . .
172
Bảng A.5
Đặc tảUC-09: Khám phá khóa học . . . . . . . . . .
175
Bảng A.6
Đặc tảUC-11: Đăng ký khóa học . . . . . . . . . . .
177
Bảng A.7
Đặc tảUC-46: Truy cập tài nguyên khóa học . . . . .
178
Bảng A.8
Đặc tảUC-47: Xem tiến độhọc tập . . . . . . . . . .
179
Bảng A.9
Đặc tảUC-48: Làm bài tập và kiểm tra
. . . . . . . .
180
Bảng A.10 Đặc tảUC-49: Tra cứu điểm và nhận xét
. . . . . . .
182
Bảng A.11 Đặc tảrút gọn UC-51: Tạo bài kiểm tra, ôn tập bằng AI 183
Bảng A.12 Đặc tảUC-52: Hỏi đáp kiến thức trong khóa học . . .
184
Bảng A.13 Đặc tảUC-54: Tùy chỉnh thông báo . . . . . . . . . .
185
Bảng A.14 Đặc tảUC-17: Nhắn tin và thảo luận
. . . . . . . . .
186
Bảng A.15 Đặc tảUC-20: Tạo khóa học phụtrợ. . . . . . . . . .
187
Bảng A.16 Đặc tảUC-201: Tạo mới hoàn toàn khóa học . . . . .
188
Bảng A.17 Đặc tảUC-202: Tạo từkhóa học cũ . . . . . . . . . .
189
Bảng A.18 Đặc tảUC-22: Xem thông tin khóa học . . . . . . . .
190
Bảng A.19 Đặc tảUC-24: Thiết lập hệthống thang điểm . . . . .
192
Bảng A.20 Đặc tảUC-25: Tải lên và cập nhật tài liệu . . . . . . .
193
Bảng A.21 Đặc tảUC-27: Chỉnh sửa bài tập . . . . . . . . . . . .
194
Bảng A.22 Đặc tảUC-29: Quản lý khóa học . . . . . . . . . . . .
196
Bảng A.23 Đặc tảUC-24: Tạo và gửi thông báo . . . . . . . . . .
197
Bảng A.24 Đặc tảUC-38: Báo cáo lượt học, tần suất đăng nhập
.
199
Bảng A.25 Đặc tảUC-39: Quản lý người dùng
. . . . . . . . . .
201
Bảng A.26 Đặc tảUC-40: Tạo/sửa/xóa tài khoản
. . . . . . . . .
202
Bảng A.27 Đặc tảUC-41: Import/export dữliệu người dùng . . .
204
Bảng A.28 Đặc tảUC-42: Phân quyền . . . . . . . . . . . . . . .
206
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang xviii/254

=== SUMMARY ===
Phân đoạn này liệt kê danh mục các bảng đặc tả Use Case (UC) từ A.2 đến A.28 thuộc phụ lục của báo cáo đồ án chuyên ngành. Nội dung bao gồm các đặc tả chi tiết về chức năng của hệ thống LMS như: quản lý lộ trình học tập, tương tác với khóa học, tích hợp AI hỗ trợ học tập, quản lý nội dung giảng dạy của giảng viên, và các chức năng quản trị hệ thống như quản lý người dùng, phân quyền và xuất/nhập dữ liệu.

=== REVIEW QUESTIONS ===
1. Danh mục các bảng từ A.2 đến A.28 mô tả các thành phần đặc tả nào của hệ thống WeLearning?
2. Dựa vào bảng A.11, hệ thống tích hợp công nghệ gì để hỗ trợ sinh viên trong việc ôn tập và làm bài kiểm tra?
3. Những Use Case nào (từ bảng A.15 đến A.17) liên quan đến quy trình khởi tạo một khóa học mới?
4. Các chức năng quản trị hệ thống và người dùng được quy định chi tiết trong những bảng đặc tả nào?','3e452dff-ae1a-450e-8a90-e4f409b25f8e'::uuid,NULL,NULL,19,707,'2026-03-21 13:35:13.319403+07'),
	 ('f70faadc-4ebb-4cd4-ab3b-c52c4e59530a'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,19,'=== ORIGINAL CONTENT ===
Trường Đại Học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa Học & KỹThuật Máy Tính
Chương 1
Giới thiệu đềtài
1.1
Thực trạng đềtài
Hiện nay, hệthống quản lý học tập (LMS) đã trởthành một phần không thểthiếu
trong môi trường giáo dục hiện đại, đặc biệt là khi xu hướng học trực tuyến ngày càng
phát triển mạnh mẽ. LMS không chỉgiúp quản lý các khóa học hiệu quảmà còn đóng
vai trò như một cầu nối quan trọng giữa giảng viên và sinh viên, góp phần nâng cao chất
lượng giảng dạy và học tập. Chính vì vậy, việc tìm hiểu và cải tiến LMS là điều cần thiết
đểhệthống này ngày càng phù hợp hơn với nhu cầu thực tế.
Tuy nhiên, khi sửdụng hệthống LMS của trường (https://lms.hcmut.edu.vn/), cảsinh
vien và giảng viên đều không tránh khỏi những bất tiện nhất định. Ví dụ, sinh viên
thường cảm thấy giao diện chưa thực sựthân thiện, còn thiếu các tính năng tương tác
như các công cụhỗtrợhọc tập cá nhân hóa hay một sốlỗi còn tồn đọng. Đối với giảng
viên, (có thể) việc quản lý lớp học và upload điểm sốđôi khi còn phức tạp, tốn thời
gian. Những điểm hạn chếnày khiến trải nghiệm sửdụng hệthống chưa được như mong
muốn, thậm chí có thểảnh hưởng đến hiệu quảdạy và học.
Chính vì vậy, việc cải tiến hệthống LMS sẽmang lại rất nhiều lợi ích thiết thực. Nếu
hệthống có giao diện dễdùng, các tính năng học tập hiện đại và khảnăng tương tác tốt,
sinh viên chắc chắn sẽcảm thấy hứng thú và chủđộng hơn khi học. Ngược lại, với các
công cụhỗtrợquản lý lớp học tiện lợi và thao tác đơn giản, giảng viên cũng sẽgiảm bớt
gánh nặng công việc và có thêm thời gian đểđầu tư cho chuyên môn giảng dạy.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 1/254

=== SUMMARY ===
Nội dung trình bày tầm quan trọng của hệ thống quản lý học tập (LMS) trong giáo dục hiện đại và thực trạng tại Trường Đại học Bách khoa TP.HCM. Dù LMS là cầu nối quan trọng, hệ thống hiện tại vẫn bộc lộ hạn chế về giao diện, tính tương tác và quy trình quản lý điểm số. Việc cải tiến LMS không chỉ nâng cao trải nghiệm, sự chủ động của sinh viên mà còn giúp giảng viên tối ưu hóa quy trình làm việc, từ đó nâng cao chất lượng dạy và học.

=== REVIEW QUESTIONS ===
1. Hệ thống LMS đóng vai trò như thế nào trong mối quan hệ giữa giảng viên và sinh viên?
2. Những hạn chế cụ thể nào mà sinh viên thường gặp phải khi sử dụng hệ thống LMS hiện tại của trường?
3. Tại sao việc quản lý lớp học và upload điểm số hiện nay lại gây khó khăn cho giảng viên?
4. Việc cải tiến hệ thống LMS theo hướng hiện đại sẽ mang lại những lợi ích thiết thực gì cho cả người dạy và người học?','acf0a3b6-6b46-43d1-bf40-6e7c3a3b2d4f'::uuid,NULL,NULL,20,637,'2026-03-21 13:35:13.319403+07'),
	 ('9226a91b-1744-4ff4-8936-b75844a3031a'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,20,'=== ORIGINAL CONTENT ===
Đềtài này không chỉmang ý nghĩa học thuật mà còn có tính ứng dụng rất cao, bởi kết quảnghiên cứu hoàn toàn có thểtriển khai trực tiếp trên LMS của trường. Nhóm cũng mong muốn học hỏi, tham khảo các tính năng tiên tiến từcác nền tảng lớn như Coursera, Udemy, LinkedIn Learning, ... Đây là những hệthống LMS nổi bật với giao diện thân thiện, công cụhọc tập tương tác, hệthống đánh giá tựđộng và nhiều tiện ích hỗtrợngười dùng. Đây cũng là nguồn cảm hứng lớn đểnhóm định hướng cho các giải pháp cải tiến sắp tới.
Cuối cùng, việc thực hiện đềtài là cơ hội đểcảnhóm được áp dụng kiến thức đã học vào thực tiễn, từkhâu phân tích nhu cầu, thiết kếgiải pháp cho đến đềxuất phương án nâng cấp hệthống. Đây không chỉlà cách đểgiải quyết nhu cầu cấp thiết của nhà trường, mà còn là dịp đểcác thành viên trong nhóm phát triển kỹnăng chuyên môn, khảnăng tư duy sáng tạo cũng như kinh nghiệm làm việc thực tế- những hành trang quan trọng cho các dựán trong tương lai.
1.2
Mục tiêu và nhiệm vụ
1.2.1
Mục tiêu
Mục tiêu của đềtài là xây dựng hệthống quản lý học tập trực tuyến WeLearning để nâng cao trải nghiệm học tập của sinh viên và đơn giản hóa quy trình nhập điểm cho giảng viên có tích hợp tính năng thông mình hỗtrợngười dùng. Cụthể:
• Nâng cao trải nghiệm người dùng cho sinh viên thông qua giao diện thân thiện và các tính năng tương tác.
• Đơn giản hóa quy trình nhập điểm/chấm điểm, nhận xét và phản hồi cho bài nộp của sinh viên cho giảng viên, giúp tiết kiệm thời gian và công sức.
• Tích hợp các công cụhỗtrợhọc tập cá nhân hóa cho sinh viên.
• Cải thiện khảnăng quản lý lớp học cho giảng viên thông qua các tính năng tựđộng hóa.

=== SUMMARY ===
Đoạn văn trình bày ý nghĩa thực tiễn và mục tiêu của đề tài xây dựng hệ thống LMS ''WeLearning''. Đề tài hướng tới việc cải thiện trải nghiệm học tập của sinh viên qua giao diện thân thiện và công cụ cá nhân hóa, đồng thời tối ưu hóa quy trình quản lý lớp học và chấm điểm cho giảng viên thông qua tích hợp tính năng thông minh. Nhóm nghiên cứu cũng chú trọng việc áp dụng kiến thức thực tế và học hỏi từ các nền tảng lớn như Coursera để giải quyết nhu cầu cấp thiết của nhà trường.

=== REVIEW QUESTIONS ===
1. Mục tiêu cốt lõi của việc xây dựng hệ thống quản lý học tập trực tuyến WeLearning là gì?
2. Những nền tảng học tập trực tuyến nổi tiếng nào được nhóm nghiên cứu tham khảo để định hướng giải pháp cải tiến?
3. Hệ thống WeLearning dự kiến sẽ hỗ trợ giảng viên như thế nào trong quy trình chấm điểm và quản lý lớp học?
4. Việc thực hiện đề tài này mang lại những giá trị gì cho các thành viên trong nhóm về mặt phát triển kỹ năng?','9cc4046d-7184-4e27-84a2-b5a1f69f462f'::uuid,NULL,NULL,21,651,'2026-03-21 13:35:13.319403+07'),
	 ('e7fa9aff-3fcd-4a8d-b8f0-e734cc68ed82'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,21,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
1.2.2
Nhiệm vụ
Đểđạt được các mục tiêu trên, nhóm sẽthực hiện các nhiệm vụsau:
• Phân tích các tính năng học tập của các nền tảng LMS như Coursera, LinkedIn
Learning, Udemy đểrút ra các bài học kinh nghiệm.
• Thiết kếvà thực hiện khảo sát thu thập ý kiến từsinh viên và giảng viên.
• Phân tích dữliệu khảo sát đểxây dựng danh sách yêu cầu cải tiến.
• Tổng hợp kết quảđểđềxuất thiết kếsơ bộcho phần LMS cải tiến.
1.3
Đối tượng và phạm vi nghiên cứu
Hệthống quản lý học tập (LMS) của trường hiện tại, cụthểlà phần nơi sinh viên học
các khóa học và giảng viên upload điểm, là đối tượng chính của nghiên cứu. Phần này
đóng vai trò quan trọng trong việc hỗtrợquá trình học tập và quản lý điểm số, nhưng
vẫn tồn tại một sốhạn chếvềtính năng và trải nghiệm sửdụng. Việc cải tiến phần này
được xem là ưu tiên đểnâng cao hiệu quảcủa toàn bộhệthống LMS.
Phạm vi nghiên cứu tập trung vào việc phân tích các vấn đềhiện tại liên quan đến trải
nghiệm học tập của sinh viên và quy trình upload tài nguyên khóa học của giảng viên.
Nhóm sẽnghiên cứu các tính năng học tập nổi bật từnhiều nền tảng LMS tiên tiến như
Coursera, LinkedIn Learning, Udemy đểhọc hỏi và áp dụng. Đồng thời, một khảo sát sẽ
được thực hiện đểthu thập ý kiến từsinh viên và giảng viên vềkhó khăn họgặp phải và
mong muốn cải tiến, đảm bảo các giải pháp đáp ứng nhu cầu thực tế.
Đềtài giới hạn phạm vi ởphần LMS đã xác định, không mởrộng sang các module
khác như đăng ký học phần hay quản lý tài chính. Các vấn đềvềtương thích với các phần
khác sẽđược thảo luận thêm với giáo viên hướng dẫn.

=== SUMMARY ===
Phần này trình bày các nhiệm vụ cụ thể của đề tài bao gồm phân tích các nền tảng LMS quốc tế (Coursera, Udemy, LinkedIn Learning), khảo sát người dùng và đề xuất thiết kế cải tiến. Đối tượng nghiên cứu trọng tâm là hệ thống LMS hiện tại của trường, tập trung vào trải nghiệm học tập của sinh viên và quản lý điểm của giảng viên. Phạm vi nghiên cứu giới hạn ở các tính năng học tập, loại trừ các phân hệ như đăng ký học phần hay quản lý tài chính.

=== REVIEW QUESTIONS ===
1. Nhóm thực hiện đề tài dự định nghiên cứu những nền tảng LMS quốc tế nào để rút kinh nghiệm?
2. Các bước cụ thể để xây dựng danh sách yêu cầu cải tiến cho hệ thống là gì?
3. Đối tượng nghiên cứu chính của đề tài tập trung vào những phân hệ nào của LMS hiện tại?
4. Những mảng quản lý nào nằm ngoài phạm vi nghiên cứu của đề tài này?','f2add8d0-6b1b-4c25-8711-0c2a00fb4f25'::uuid,NULL,NULL,22,619,'2026-03-21 13:35:13.320431+07'),
	 ('388504f6-8ca3-40be-9502-0b39107b9a06'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,22,'=== ORIGINAL CONTENT ===
Trường Đại Học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa Học & KỹThuật Máy Tính
Chương 2
Nghiên cứu thịtrường
2.1
Coursera
2.1.1
Tổng quan vềCoursera
Coursera [1] là một nền tảng học tập trực tuyến toàn cầu, cung cấp cho mọi người,
ởmọi nơi, cơ hội tiếp cận các khóa học và bằng cấp trực tuyến từcác trường đại học
và công ty hàng đầu. Được thành lập vào năm 2012, Coursera mang đến cho mọi người
cơ hội tiếp cận giáo dục chất lượng cao, bất kểvịtrí địa lý hay điều kiện kinh tế. Từ
đó, Coursera đã phát triển nhanh chóng và trởthành một trong những nền tảng học trực
tuyến lớn nhất thếgiới, thu hút hàng triệu người học từkhắp nơi. Hình 2.1 thểhiện trang
chủcủa hệthống Coursera.
Hình 2.1: Giao diện trang chủCoursera [1].
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 4/254

=== SUMMARY ===
Đoạn văn bản giới thiệu về Coursera, một trong những nền tảng giáo dục trực tuyến (LMS) lớn nhất thế giới được thành lập vào năm 2012. Coursera cung cấp khả năng tiếp cận các khóa học và bằng cấp chất lượng cao từ các trường đại học và công ty hàng đầu toàn cầu. Sứ mệnh của nền tảng là giúp mọi người học tập không giới hạn bởi rào cản địa lý hay tài chính, từ đó thu hút hàng triệu học viên tham gia.

=== REVIEW QUESTIONS ===
1. Coursera được chính thức thành lập vào năm nào?
2. Nguồn gốc của các khóa học và bằng cấp được cung cấp trên Coursera đến từ đâu?
3. Mục tiêu chính của Coursera khi cung cấp giáo dục trực tuyến là gì?
4. Văn bản mô tả quy mô và sự phát triển của Coursera như thế nào?','446f88f3-f97c-4a03-abc9-a03f5b1b5bc7'::uuid,NULL,NULL,23,384,'2026-03-21 13:35:13.320431+07'),
	 ('47de254b-c375-4feb-ba78-7cd96dfb812d'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,78,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Hình 4.1: Sơ đồEERD của hệthống LMS
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 60/254

=== SUMMARY ===
Nội dung này trình bày Hình 4.1, sơ đồ thực thể mối quan hệ mở rộng (EERD) của hệ thống quản lý học tập (LMS). Đây là một phần quan trọng trong chương thiết kế hệ thống, giúp trực quan hóa cấu trúc dữ liệu, các thực thể, thuộc tính và mối quan hệ giữa chúng. Sơ đồ này đóng vai trò là nền tảng kỹ thuật để triển khai các mô hình quan hệ chi tiết cho từng dịch vụ như quản lý người dùng và giao tiếp trong các bước tiếp theo.

=== REVIEW QUESTIONS ===
1. Mục đích chính của sơ đồ EERD (Hình 4.1) trong thiết kế hệ thống LMS là gì?
2. Sơ đồ EERD giúp ích gì cho việc ánh xạ sang mô hình quan hệ (Relational Model) ở các bước sau?
3. Trong ngữ cảnh báo cáo, sơ đồ EERD này thuộc về chương nào và tập trung vào thành phần gì của hệ thống?
4. Tại sao việc xác định các thuộc tính và mối quan hệ giữa các thực thể lại quan trọng trong thiết kế cơ sở dữ liệu?','f4c01a88-42a2-485d-8f6e-8197f09debf4'::uuid,NULL,NULL,79,270,'2026-03-21 13:35:13.331495+07'),
	 ('cf51d703-3461-4d39-9824-f4e7a0d09e53'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,23,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Coursera đem đến một nền tảng học tập trực tuyến toàn cầu, mang đến cho mọi người
ởmọi nơi cơ hội tiếp cận các khóa học và bằng cấp trực tuyến từcác trường đại học
và công ty hàng đầu thếgiới. Với sựhợp tác của hơn 300 trường đại học và công ty
danh tiếng như Stanford, Duke, Google, IBM, và nhiều tổchức khác, Coursera cung cấp
một loạt các khóa học đa dạng vềnhiều lĩnh vực khác nhau, từcông nghệ, kinh doanh,
khoa học nhân văn đến y tế, nghệthuật,. . . . Các khóa học trên Coursera bao gồm nhiều
chuyên ngành, có phí hoặc miễn phí. Khi hoàn thành khóa học trực tuyến trên Coursera,
nếu muốn được cấp chứng chỉ, bạn thường phải đóng một khoản phí. Tuy nhiên, bạn vẫn
có thểxin tài trợđểcó cơ hội nhận chứng chỉmà không tốn phí.
2.1.2
Đánh giá kỹthuật
Điểm mạnh và điểm yếu của Coursera [1] được phân tích trong Bảng 2.1.
Bảng 2.1: Đánh giá kỹthuật của Coursera.
Tiêu chí
Điểm mạnh
Điểm yếu
Hiệu suất hệthống
Nền tảng hoạt động ổn
định, có khảnăng xửlý
lượng truy cập rất lớn với
độtrễthấp; tối ưu cho cả
web và mobile.
Một sốngười dùng phản hồi về
tình trạng chậm nhẹkhi tải nội
dung có nhiều video hoặc dự
án nhóm nặng.
Giao diện người dùng
Giao diện hiện đại, rõ ràng
và trực quan; điều hướng
dễdàng giữa các mô-đun
học, bài tập, diễn đàn.
Một sốphần hiển thịdanh sách
khóa học khá dày đặc, gây cảm
giác “quá tải thông tin” cho
người dùng mới.
Tính năng học tập
Hỗtrợvideo chất lượng
cao, auto-grading, peer re-
view, dựán thực hành và
cấp chứng chỉđược công
hận.
Một sốkhóa học thiếu tính
tương tác trực tiếp hoặc mô
phỏng thực tếchuyên sâu (đặc
biệt trong lĩnh vực kỹthuật).
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 5/254

=== SUMMARY ===
Đoạn văn giới thiệu về Coursera, một nền tảng giáo dục trực tuyến toàn cầu hợp tác với hơn 300 đối tác uy tín. Nội dung tập trung vào tính đa dạng của khóa học và chính sách cấp chứng chỉ. Đồng thời, văn bản cung cấp bảng đánh giá kỹ thuật chi tiết về hiệu suất ổn định, giao diện trực quan và các tính năng hỗ trợ học tập như chấm điểm tự động. Tuy nhiên, hệ thống vẫn tồn tại hạn chế về sự quá tải thông tin và tính tương tác trực tiếp.

=== REVIEW QUESTIONS ===
1. Coursera hợp tác với những đối tác nào để cung cấp các khóa học và bằng cấp trực tuyến?
2. Người học có thể làm gì để nhận chứng chỉ hoàn thành khóa học trên Coursera nếu không có khả năng đóng phí?
3. Về mặt hiệu suất hệ thống, Coursera có điểm mạnh và điểm yếu gì đáng lưu ý?
4. Tại sao tính năng học tập của Coursera bị đánh giá là còn hạn chế trong lĩnh vực kỹ thuật?','86e030ba-c9e6-4106-8f16-fc3762e4673b'::uuid,NULL,NULL,24,653,'2026-03-21 13:35:13.320937+07');
INSERT INTO coaching_chatbot.lecture_knowledge_chunks (lecture_knowledge_chunks_id,lecture_knowledge_id,chunk_index,chunk_content,qdrant_point_id,start_time_seconds,end_time_seconds,page_number,token_count,created_at) VALUES
	 ('0d58456c-bcf1-4888-ae70-9185b9c0ea47'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,24,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Mobile compatibility
Ứng dụng di động ổn định,
hỗtrợhọc offline, đồng bộ
tiến độvà tải tài liệu dễ
dàng.
Một sốtính năng quản trịvà
thảo luận nhóm nâng cao bị
giới hạn trên bản mobile.
Bảo mật
Tuân thủcác tiêu chuẩn
bảo mật toàn cầu (GDPR,
FERPA), mã hóa dữliệu
người dùng và thanh toán
an toàn.
Người dùng ởkhu vực mạng
yếu có thểgặp khó khăn khi
xác thực hoặc đồng bộdữliệu
học tập.
Khảnăng tích hợp
Cung cấp API, LTI và
SSO
để
tích
hợp
với
LMS
nội
bộ,
Google
Workspace,
Microsoft
Teams và hệthống HR.
Cấu hình tích hợp đôi khi phức
tạp cho đơn vịtriển khai quy
mô nhỏ.
2.1.3
Phân tích tính năng
Danh sách các tính năng chính của Coursera được liệt kê và mô tảchi tiết trong Bảng
2.2.
Bảng 2.2: Phân tích tính năng của Coursera.
Tính năng
Có/Không
Mô tảchi tiết
Video streaming
Có
Hỗtrợphát video trực tuyến với chất lượng cao,
có thểđiều chỉnh độphân giải.
Live sessions
Không
Chỉtổchức các buổi học ghi hình sẵn, không có
tương tác thời gian thực.
Discussion forums
Có
Diễn đàn thảo luận cho phép học viên trao đổi,
hỏi đáp và chia sẻkinh nghiệm.
Auto-grading
Có
Hệthống tựđộng chấm điểm bài tập, giúp giảm
tải cho giảng viên.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 6/254

=== SUMMARY ===
Đoạn văn bản phân tích các khía cạnh kỹ thuật và tính năng cốt lõi của Coursera. Nền tảng được đánh giá cao về khả năng tương thích di động, bảo mật chuẩn toàn cầu (GDPR, FERPA) và tích hợp hệ thống linh hoạt qua API/SSO. Về tính năng, Coursera cung cấp video chất lượng cao, diễn đàn thảo luận và hệ thống chấm điểm tự động tối ưu cho giảng viên, tuy nhiên vẫn còn hạn chế do thiếu các buổi học tương tác trực tiếp (live sessions).

=== REVIEW QUESTIONS ===
1. Coursera tuân thủ các tiêu chuẩn bảo mật quốc tế nào và người dùng tại khu vực mạng yếu có thể gặp khó khăn gì?
2. Những tính năng quản trị và thảo luận nào bị giới hạn khi sử dụng Coursera trên ứng dụng di động?
3. Để tích hợp với các hệ thống quản lý nhân sự (HR) hoặc LMS nội bộ, Coursera cung cấp những phương thức kết nối nào?
4. Trong số các tính năng được liệt kê, tính năng nào giúp giảm bớt gánh nặng cho giảng viên và tính năng nào hiện chưa có trên hệ thống?','140270c9-8038-47a6-a9d3-24eac137dae8'::uuid,NULL,NULL,25,564,'2026-03-21 13:35:13.320937+07'),
	 ('95e31653-a3c2-41ce-8c41-19ee93035be4'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,25,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
AI recommendations
Có
Đềxuất khóa học dựa trên sởthích và lịch sửhọc
tập của người dùng.
Mobile app
Có
Ứng dụng di động cho phép học viên truy cập
khóa học mọi lúc, mọi nơi.
Offline access
Có
Cho phép tải video và tài liệu vềthiết bịđểhọc
offline.
Certificates
Có
Cung cấp chứng chỉhoàn thành khóa học cho học
viên.
Progress tracking
Có
Theo dõi tiến độhọc tập của học viên, hiển thịcác
chỉsốquan trọng.
Social learning
Có
Khuyến khích học viên tương tác, hợp tác và học
hỏi lẫn nhau thông qua các hoạt động nhóm.
2.1.4
Kết luận
Coursera thểhiện một mô hình học tập toàn cầu có tính học thuật cao. Nền tảng tập
trung vào việc chứng nhận kỹnăng nghềnghiệp thực tiễn qua các đối tác uy tín và tích
hợp chặt chẽgiữa học thuật – doanh nghiệp.
Khi phát triển hệthống LMS cho trường Đại học Bách khoa, có thểhọc hỏi từCours-
era ởcác khía cạnh sau:
• Kết nối với các tổchức và doanh nghiệp đểcung cấp khóa học thực tế, có chứng chỉ
nghềnghiệp.
• Ứng dụng AI đểgợi ý khóa học, lộtrình kỹnăng và cá nhân hóa trải nghiệm học
tập.
• Thiết kếhạtầng cloud-based có khảnăng mởrộng, tối ưu cho lượng người học lớn.
• Xây dựng hệthống theo dõi tiến độhọc tập và cấp chứng chỉtựđộng, có thểtích
hợp với hồsơ sinh viên.
Tổng thể, Coursera là hình mẫu tiêu biểu cho một nền tảng học thuật mở, hiện đại và
hướng tới thịtrường toàn cầu, là nguồn tham khảo quan trọng khi định hình LMS đại
học theo hướng quốc tếhóa và liên kết doanh nghiệp.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 7/254

=== SUMMARY ===
Đoạn văn bản phân tích các tính năng ưu việt của Coursera như đề xuất bằng AI, học ngoại tuyến, theo dõi tiến độ và cấp chứng chỉ. Nội dung nhấn mạnh Coursera là mô hình lý tưởng để phát triển LMS cho Trường Đại học Bách khoa nhờ sự kết hợp chặt chẽ giữa học thuật và doanh nghiệp. Các bài học kinh nghiệm bao gồm ứng dụng AI để cá nhân hóa lộ trình học, xây dựng hạ tầng đám mây linh hoạt và tự động hóa quy trình cấp chứng chỉ hướng tới tiêu chuẩn quốc tế.

=== REVIEW QUESTIONS ===
1. Các tính năng nào của Coursera giúp tăng cường tính linh hoạt và khả năng tiếp cận bài học cho người dùng mọi lúc, mọi nơi?
2. Tại sao việc kết nối với các doanh nghiệp và tổ chức uy tín lại là một thế mạnh quan trọng của mô hình Coursera?
3. Hệ thống LMS của Trường Đại học Bách khoa có thể ứng dụng AI vào những khía cạnh nào để cải thiện trải nghiệm học tập của sinh viên?
4. Lợi ích của việc thiết kế hạ tầng cloud-based cho một hệ thống quản lý học tập (LMS) quy mô lớn là gì?','37af0544-408e-423b-a54e-0ac62f006b87'::uuid,NULL,NULL,26,646,'2026-03-21 13:35:13.320937+07'),
	 ('98e2edc3-f33d-4b32-a5dc-13f32d66cc3b'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,26,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
2.2
Udemy
2.2.1
Tổng quan vềUdemy
Udemy [2] là một nền tảng học trực tuyến phổbiến, cung cấp hàng ngàn khóa học
vềmọi lĩnh vực từkỹthuật số, kinh doanh, cho đến nghệthuật và phát triển cá nhân.
Được thành lập vào năm 2010, nền tảng đã nhanh chóng trởthành điểm đến lý tưởng cho
những ai muốn nâng cao kỹnăng và kiến thức của mình thông qua các khóa học chất
lượng cao. Udemy được sáng lập bởi Eren Bali, Gagan Biyani, và Oktay Caglar với mục
tiêu mang đến cơ hội học tập linh hoạt và đa dạng cho mọi người. Từkhi ra mắt, nền
tảng này đã phát triển vượt bậc, thu hút hàng triệu học viên và giảng viên trên khắp thế
giới. Hiện tại, Udemy có hơn 155,000 khóa học và hơn 40 triệu học viên, chứng tỏsự
phổbiến và uy tín của nền tảng này trong cộng đồng học tập trực tuyến.
Cái tên Udemy là sựkết hợp của hai từ“you + academy” (bạn + học viện), Udemy
khẳng định mình là một thịtrường học tập toàn cầu – nói cách khác, là một tập hợp các
khóa học trực tuyến.
Hình 2.2 thểhiện giao diện trang chủcủa hệthống Udemy.
Hình 2.2: Giao diện trang chủUdemy [2].
Udemy hỗtrợcác hình thức học linh hoạt với video theo yêu cầu, bài tập trắc nghiệm,
Q&A, và chứng chỉhoàn thành. Ngoài ra, phiên bản Udemy Business phục vụcho các tổ
chức và doanh nghiệp, cung cấp thư viện chọn lọc hơn 25.000 khóa học, kèm tính năng
quản lý nhân sự, báo cáo và tích hợp với các hệthống LMS khác.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 8/254

=== SUMMARY ===
Đoạn văn tổng quan về Udemy, một nền tảng học trực tuyến hàng đầu thế giới được thành lập năm 2010. Udemy cung cấp hơn 155.000 khóa học đa dạng lĩnh vực cho hơn 40 triệu học viên. Với tên gọi kết hợp từ "you" và "academy", nền tảng chú trọng vào tính linh hoạt thông qua video, bài tập và hệ thống Q&A. Ngoài ra, Udemy còn có phiên bản Business dành cho doanh nghiệp với các công cụ quản lý, báo cáo và khả năng tích hợp LMS.

=== REVIEW QUESTIONS ===
1. Tên gọi ''Udemy'' được kết hợp từ những từ nào và mang ý nghĩa gì?
2. Ai là những người sáng lập ra Udemy và mục tiêu ban đầu của họ là gì?
3. Nêu các hình thức và công cụ học tập mà Udemy hỗ trợ để tạo sự linh hoạt cho người học.
4. Phiên bản Udemy Business cung cấp những tính năng bổ sung nào so với phiên bản dành cho cá nhân?','849ab93f-7c89-4794-bad9-e09194fa16a3'::uuid,NULL,NULL,27,584,'2026-03-21 13:35:13.321582+07'),
	 ('96fc5915-bc5b-4185-b9fb-e4bec382eb45'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,27,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
2.2.2
Đánh giá kỹthuật
Điểm mạnh và điểm yếu của Udemy [2] được phân tích trong Bảng 2.3.
Bảng 2.3: Đánh giá kỹthuật của Udemy.
Tiêu chí
Điểm mạnh
Điểm yếu
Hiệu suất hệthống
Nền tảng ổn định, khả
năng chịu tải cao, stream-
ing video tốt.
Đôi khi tải trang chậm ở
một sốkhu vực.
Giao diện người dùng
Trực quan, dễsửdụng, tập
trung vào nội dung khóa
học.
Giao diện có thểhơi rối với
người dùng mới do có quá
nhiều khóa học.
Tính năng học tập
Cung cấp nhiều công cụ
học tập: video, bài tập, hỏi
đáp, ghi chú.
Thiếu các buổi học trực tiếp
(live sessions) và công cụ
tương tác thời gian thực.
Mobile compatibility
Ứng dụng di động đầy đủ
tính năng trên iOS và An-
droid, hỗtrợhọc offline.
Giao diện trên tablet chưa
được tối ưu hóa hoàn toàn.
Bảo mật
Tuân thủcác tiêu chuẩn bảo
mật cơ bản, bảo vệthông
tin người dùng.
Các khóa học có thểbịtải
xuống và chia sẻtrái phép.
Khảnăng tích hợp
Cung cấp API cho doanh
nghiệp nhưng hạn chếcho
người dùng cá nhân.
Khảnăng tích hợp với các
hệthống LMS của bên thứ
ba còn hạn chế.
2.2.3
Phân tích tính năng
Danh sách các tính năng chính của Udemy [2] được liệt kê và mô tảchi tiết trong
Bảng 2.4.
Bảng 2.4: Phân tích tính năng của Udemy.
Tính năng
Có/Không
Mô tảchi tiết
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 9/254

=== SUMMARY ===
Đoạn văn bản cung cấp đánh giá chi tiết về kỹ thuật và các tính năng của nền tảng Udemy. Về mặt kỹ thuật, Udemy có hiệu suất ổn định, giao diện trực quan và hỗ trợ di động tốt (học offline), nhưng thiếu tương tác trực tiếp và gặp hạn chế trong tích hợp LMS bên thứ ba. Về bảo mật, nền tảng tuân thủ các tiêu chuẩn cơ bản nhưng vẫn đối mặt với rủi ro nội dung bị tải xuống trái phép.

=== REVIEW QUESTIONS ===
1. Dựa trên bảng đánh giá kỹ thuật, những hạn chế chính của Udemy trong tính năng học tập là gì?
2. Khả năng tương thích di động của Udemy có ưu điểm và nhược điểm cụ thể nào?
3. Tại sao việc bảo mật nội dung trên Udemy vẫn được coi là một điểm yếu dù đã tuân thủ các tiêu chuẩn cơ bản?
4. Sự khác biệt trong khả năng cung cấp API của Udemy giữa đối tượng doanh nghiệp và cá nhân là gì?','cea529e7-b38e-43ca-8427-3c4d7d436647'::uuid,NULL,NULL,28,548,'2026-03-21 13:35:13.321582+07'),
	 ('263fc6f8-ef18-4bce-92c5-2a230207fbb5'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,28,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Video streaming
Có
Nền tảng chính của Udemy là cung cấp các khóa học
qua video chất lượng cao.
Live sessions
Không
Udemy chủyếu tập trung vào các khóa học tựhọc (self-
paced), không có các buổi học trực tiếp.
Discussion forums
Có
Mỗi khóa học đều có mục Hỏi & Đáp (Q&A) đểhọc
viên và giảng viên trao đổi.
Auto-grading
Có
Cung cấp các bài kiểm tra trắc nghiệm và bài tập được
chấm điểm tựđộng.
AI recommendations
Có
Hệthống gợi ý các khóa học dựa trên lịch sửhọc tập và
sởthích của người dùng.
Mobile app
Có
Cung cấp ứng dụng di động đầy đủtính năng cho cả
iOS và Android.
Offline access
Có
Người dùng có thểtải xuống các bài giảng đểhọc of-
fline trên ứng dụng di động.
Certificates
Có
Cung cấp chứng chỉhoàn thành sau khi học viên hoàn
thành tất cảcác yêu cầu của khóa học.
Progress tracking
Có
Theo dõi và hiển thịtiến độhọc tập của học viên trong
từng khóa học.
Social learning
Không
Thiếu các tính năng học tập xã hội như học nhóm, thảo
luận nhóm hay các dựán chung.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 10/254

=== SUMMARY ===
Đoạn văn bản phân tích chi tiết các tính năng kỹ thuật cốt lõi của Udemy. Hệ thống nổi bật với việc cung cấp bài giảng video chất lượng cao, hỗ trợ học offline trên ứng dụng di động, chấm điểm tự động và cá nhân hóa trải nghiệm qua gợi ý AI. Tuy nhiên, Udemy tập trung vào hình thức tự học (self-paced) nên thiếu các buổi học trực tiếp (live sessions) và các tính năng tương tác cộng đồng (social learning) như thảo luận nhóm hay dự án chung.

=== REVIEW QUESTIONS ===
1. Udemy hỗ trợ học viên và giảng viên tương tác với nhau thông qua tính năng cụ thể nào?
2. Hệ thống gợi ý khóa học (AI recommendations) của Udemy dựa trên những yếu tố nào của người dùng?
3. Tại sao Udemy không được đánh giá cao về khả năng học tập xã hội (social learning)?
4. Người dùng có thể học tập trên Udemy khi không có kết nối internet không? Nếu có thì bằng cách nào?','287a6e30-0d52-4815-a9f2-95a06a6f6c00'::uuid,NULL,NULL,29,503,'2026-03-21 13:35:13.321582+07'),
	 ('493d9bb2-0f82-4985-9dc8-1dc054dfb340'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,29,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
2.2.4
Kết luận
Udemy là nền tảng học trực tuyến quy mô lớn với mô hình mở, cho phép mọi người
có thểdạy và học bất kỳkỹnăng nào. Điểm mạnh của Udemy nằm ởsựđa dạng nội
dung, mô hình mua một lần học trọn đời, và trải nghiệm di động vượt trội. Đây là lựa
chọn lý tưởng cho cá nhân muốn học nhanh kỹnăng cụthểhoặc nâng cao chuyên môn
trong thời gian ngắn.
Tuy nhiên, Udemy thiếu các tính năng cộng tác và học tập xã hội sâu, khiến trải
nghiệm học có phần đơn lẻso với các nền tảng hướng cộng đồng như Coursera hoặc
LinkedIn Learning. Ngoài ra, việc kiểm soát chất lượng nội dung phụthuộc vào người
dạy, dẫn đến độđồng nhất chưa cao.
Khi phát triển hệthống LMS cho trường Đại học Bách khoa, Udemy có thểgợi ý
những hướng tiếp cận hữu ích sau:
• Khuyến khích mô hình “giảng viên mởrộng” – cho phép giảng viên, cựu sinh viên
hoặc chuyên gia doanh nghiệp tựtạo khóa học chia sẻkiến thức.
• Áp dụng mô hình marketplace nội bộcho các khóa học tựdo trong trường (student-
created learning).
• Phát triển tính năng Q&A, quiz tựchấm và chứng chỉhoàn thành tựđộng.
Tổng thể, Udemy đại diện cho xu hướng dân chủhóa việc dạy và học, nơi bất kỳai
cũng có thểtrởthành người chia sẻtri thức. Dù còn thiếu tính cộng tác chuyên sâu, đây
vẫn là nền tảng tiêu biểu cho học tập linh hoạt, lấy người học làm trung tâm, phù hợp với
mô hình đào tạo kỹnăng trong kỷnguyên số.
2.3
LinkedIn Learning
2.3.1
Tổng quan vềLinkedIn Learning
LinkedIn Learning [3] là nền tảng học trực tuyến thân thiện do LinkedIn phát triển,
mang đến hàng loạt khóa học đa dạng vềcông nghệ, kinh doanh, sáng tạo và kỹnăng
mềm. Với hơn 24.900 khóa học từhơn 3.900 chuyên gia hàng đầu, nền tảng hỗtrợphụ
đềbằng hơn 20 ngôn ngữvà thư viện gốc bằng 7 ngôn ngữ. Đặc biệt, nó cá nhân hóa
hành trình học tập bằng cách dùng dữliệu thực tếtừLinkedIn đểgợi ý nội dung phù hợp
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 11/254

=== SUMMARY ===
Đoạn văn cung cấp cái nhìn tổng kết về Udemy và giới thiệu LinkedIn Learning. Udemy được đánh giá là nền tảng học tập mở, linh hoạt với nội dung đa dạng nhưng thiếu tính tương tác cộng đồng. Từ đó, tác giả đề xuất các tính năng cho hệ thống LMS trường đại học như mô hình giảng viên mở rộng và marketplace nội bộ. LinkedIn Learning nổi bật là nền tảng chuyên nghiệp với kho khóa học đồ sộ, tích hợp dữ liệu LinkedIn để cá nhân hóa lộ trình phát triển kỹ năng cho người dùng.

=== REVIEW QUESTIONS ===
1. Những điểm mạnh và điểm yếu chính của mô hình học tập mở trên Udemy là gì?
2. Tác giả đề xuất những hướng tiếp cận cụ thể nào từ Udemy để áp dụng cho hệ thống LMS của Trường Đại học Bách khoa?
3. LinkedIn Learning cá nhân hóa hành trình học tập của người dùng dựa trên cơ sở dữ liệu nào?
4. Tại sao Udemy được coi là đại diện cho xu hướng ''dân chủ hóa việc dạy và học''?','5604db82-d464-4ba0-b10e-25fa9f8e5d46'::uuid,NULL,NULL,30,720,'2026-03-21 13:35:13.321582+07'),
	 ('d20d41aa-d7fb-4e05-9ad9-2570013d6451'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,30,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
với mục tiêu nghềnghiệp và kỹnăng còn thiếu của bạn.
Hình 2.3 thểhiện giao diện trang chủcủa hệthống LinkedIn Learning.
Hình 2.3: Giao diện người dùng LinkedIn Learning [3].
Nền tảng nổi bật với trọng tâm nâng cao kỹnăng chuyên môn qua các định dạng
dễtiếp cận như video, audio, văn bản và ’Nano Tips’ ngắn gọn. Bạn có thểthực hành
thực tếvới hơn 300.000 câu quiz, 10.000 bài tập và môi trường mã hóa ảo trên GitHub
Codespaces. Chứng chỉlà điểm cộng lớn, bao gồm Chứng chỉChuyên nghiệp từđối
tác như Microsoft, Zendesk, LambdaTest, BluePrism, cùng hỗtrợchuẩn bịcho hơn 120
chứng chỉbên ngoài, đơn vịgiáo dục liên tục và tín chỉhọc thuật. Nhiều người dùng chia
sẻrằng nó giúp tăng tựtin, học linh hoạt và hiệu quảhơn, với các công ty như Godiva
Chocolatier hay LEGO dùng đểphát triển nhân viên.
2.3.2
Đánh giá kỹthuật
Điểm mạnh và điểm yếu của LinkedIn Learning [3] được phân tích trong Bảng 2.5.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 12/254

=== SUMMARY ===
LinkedIn Learning là nền tảng đào tạo trực tuyến tập trung vào phát triển kỹ năng chuyên môn thông qua video, audio, văn bản và các ''Nano Tips''. Hệ thống cá nhân hóa lộ trình học tập dựa trên dữ liệu LinkedIn, tích hợp công cụ thực hành thực tế như GitHub Codespaces và hệ thống câu hỏi trắc nghiệm phong phú. Điểm mạnh của nền tảng là cung cấp chứng chỉ chuyên nghiệp từ các đối tác lớn và hỗ trợ chuẩn bị cho hơn 120 chứng chỉ quốc tế, giúp tăng hiệu quả phát triển nhân sự.

=== REVIEW QUESTIONS ===
1. LinkedIn Learning sử dụng dữ liệu từ đâu để cá nhân hóa lộ trình học tập cho người dùng?
2. Những định dạng nội dung nào giúp người học dễ dàng tiếp cận kiến thức trên LinkedIn Learning?
3. Người học có thể thực hành lập trình trực tiếp trên LinkedIn Learning thông qua công cụ nào?
4. LinkedIn Learning hỗ trợ chuẩn bị cho bao nhiêu chứng chỉ bên ngoài và kể tên một số đối tác cấp chứng chỉ chuyên nghiệp trên nền tảng?','97cc1821-c962-452f-8cac-0fc36b8b149f'::uuid,NULL,NULL,31,504,'2026-03-21 13:35:13.321582+07'),
	 ('ce66b4f6-e2b8-4a7b-9068-d1ce45df3b36'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,31,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Bảng 2.5: Đánh giá kỹthuật của LinkedIn Learning.
Tiêu chí
Điểm mạnh
Điểm yếu
Hiệu suất hệthống
Nền tảng hoạt động ổn
định, tốc độtải video
hanh và xửlý mượt trên
cảweb và mobile.
Hiệu suất phụthuộc vào tốc
độmạng và cấu hình thiết bị,
đôi khi xảy ra giật nhẹkhi phát
video độphân giải cao.
Giao diện người dùng
Giao diện hiện đại, trực
quan, tuân theo phong
cách thiết kếLinkedIn; dễ
sửdụng và quen thuộc
với người dùng chuyên
nghiệp.
Thiếu tùy chọn cá nhân hóa
giao diện; bốcục danh mục
khóa học đôi khi hơi dày đặc
gây khó tìm kiếm nhanh.
Tính năng học tập
Cung cấp video chất lượng
cao, phụđềđa ngôn ngữ,
đánh giá kỹnăng, chứng
chỉ, và lộtrình học nghề
nghiệp.
Thiếu tính năng học nhóm
hoặc dựán hợp tác; chưa hỗtrợ
đầy đủhọc tập tương tác thời
gian thực.
Mobile compatibility
Ứng dụng di động ổn định,
hỗtrợhọc ngoại tuyến và
đồng bộtiến độhọc tập
với web.
Một sốtính năng quản lý
doanh
nghiệp
và
báo
cáo
không khảdụng trên app.
Bảo mật
Sửdụng tiêu chuẩn bảo
mật cấp doanh nghiệp của
Microsoft, mã hóa dữliệu
và tuân thủGDPR.
Người dùng doanh nghiệp đôi
khi lo ngại vềchia sẻdữliệu
giữa LinkedIn và Microsoft
Learning Hub.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 13/254

=== SUMMARY ===
Bảng 2.5 cung cấp cái nhìn chi tiết về đánh giá kỹ thuật của LinkedIn Learning dựa trên 5 tiêu chí: hiệu suất, giao diện, tính năng học tập, khả năng tương thích di động và bảo mật. Nền tảng nổi bật với sự ổn định, giao diện chuyên nghiệp và bảo mật cấp doanh nghiệp của Microsoft. Tuy nhiên, nó vẫn tồn tại các hạn chế về khả năng cá nhân hóa, thiếu tính năng tương tác nhóm và sự phụ thuộc vào chất lượng mạng khi truyền tải video độ phân giải cao.

=== REVIEW QUESTIONS ===
1. Dựa trên bảng đánh giá, những yếu tố nào có thể ảnh hưởng tiêu cực đến hiệu suất phát video trên LinkedIn Learning?
2. Tại sao giao diện của LinkedIn Learning được coi là chuyên nghiệp nhưng lại gây khó khăn trong việc tìm kiếm nhanh?
3. Hạn chế lớn nhất của LinkedIn Learning trong việc hỗ trợ các mô hình học tập hiện đại (như học tập cộng tác) là gì?
4. Về mặt bảo mật, người dùng doanh nghiệp có lo ngại gì khi sử dụng nền tảng này mặc dù nó tuân thủ tiêu chuẩn GDPR?','316acc7f-d858-4b98-8486-e86d8b4a8575'::uuid,NULL,NULL,32,568,'2026-03-21 13:35:13.321582+07'),
	 ('56e7fd02-841c-4a76-aeaf-692bc92a3689'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,32,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Khản năng tích hợp
Tích
hợp
sâu
với
hồ
sơ
LinkedIn,
Microsoft
Teams, và các LMS như
Moodle,
Cornerstone,
Canvas; có API doanh
nghiệp.
Việc triển khai API hoặc SSO
cho doanh nghiệp có thểyêu
cầu cấu hình kỹthuật phức tạp.
LinkedIn Learning là nền tảng học trực tuyến nổi bật, được cộng đồng đánh giá cao
vềtính tiện lợi, giao diện thân thiện và khản năng tích hợp — đặc biệt qua phản hồi từ
Capterra [5] và G2 [6] năm 2025. Điểm mạnh của nền tảng nằm ởviệc liên kết chứng
chỉtrực tiếp với hồsơ LinkedIn, tăng giá trịchuyên môn cho người học.
Vềmặt kỹthuật, LinkedIn Learning sửdụng kết hợp rendering phía server và client
đểgiảm độtrễ, hỗtrợhàng triệu người dùng đồng thời với độtin cậy cao. Tuy nhiên,
hiệu suất phụthuộc vào kết nối mạng ổn định; trên đường truyền yếu có thểxảy ra hiện
tượng gián đoạn video.
Giao diện người dùng trực quan, dễsửdụng và có hỗtrợaccessibility tốt, song khả
năng cá nhân hóa còn hạn chếvà thiếu yếu tốtương tác nhóm. Do đó, nền tảng phù hợp
hơn cho học tập cá nhân hơn là môi trường học cộng tác.
Hình 2.4 minh họa giao diện lộtrình nghềnghiệp trên LinkedIn Learning.
Hình 2.4: Giao diện lộtrình nghềnghiệp trên LinkedIn Learning [3].
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 14/254

=== SUMMARY ===
Nội dung tập trung đánh giá LinkedIn Learning về khả năng tích hợp và đặc tính kỹ thuật. Nền tảng nổi bật với việc kết nối sâu với hồ sơ LinkedIn, Microsoft Teams và các LMS phổ biến như Moodle, Canvas. Về mặt kỹ thuật, hệ thống sử dụng kết hợp rendering server và client để tối ưu hiệu suất cho hàng triệu người dùng. Tuy nhiên, nền tảng vẫn còn hạn chế về khả năng tương tác nhóm và cá nhân hóa, khiến nó phù hợp nhất cho việc tự học cá nhân.

=== REVIEW QUESTIONS ===
1. LinkedIn Learning có khả năng tích hợp với những nền tảng và hệ thống LMS cụ thể nào?
2. Điểm mạnh lớn nhất của LinkedIn Learning trong việc hỗ trợ giá trị chuyên môn của người học là gì?
3. Phương pháp kỹ thuật nào được nền tảng sử dụng để giảm độ trễ và duy trì độ tin cậy cho lượng người dùng lớn?
4. Tại sao LinkedIn Learning lại được đánh giá là phù hợp cho học tập cá nhân hơn là môi trường học tập cộng tác?','20a48893-b1ae-4ca9-a15a-c5e1892e8dfb'::uuid,NULL,NULL,33,562,'2026-03-21 13:35:13.321582+07'),
	 ('31dcef98-30b8-49e9-bc4e-80d0bff8ad8e'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,33,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
2.3.3
Phân tích tính năng
Điểm mạnh của LinkedIn Learning nằm ởviệc tập trung vào phát triển kỹnăng nghề
nghiệp, với các tính năng hỗtrợhọc tập cá nhân hóa và tích hợp sâu với hồsơ LinkedIn.
Dưới đây là bảng phân tích chi tiết các tính năng chính của nền tảng trong Bảng 2.6.
Bảng 2.6: Phân tích tính năng của LinkedIn Learning.
Tính năng
Có/Không
Mô tảchi tiết
Video streaming
Có
Hỗtrợphát video chất lượng cao,
có phụđềđa ngôn ngữ, điều
chỉnh tốc độvà độphân giải linh
hoạt.
Live sessions
Có (giới hạn)
Một sốkhóa học cung cấp buổi
học trực tiếp hoặc webinar theo
lịch cụthể.
Discussion forums
Có
Mục hỏi đáp (Q&A) cho phép
học viên trao đổi với giảng viên
và cộng đồng học viên.
Auto-grading
Có
Áp dụng cho các bài kiểm tra trắc
nghiệm hoặc đánh giá kỹnăng,
hệthống tựđộng chấm điểm.
AI recommendations
Có
Thuật toán đềxuất khóa học và
kỹnăng dựa trên hồsơ nghề
nghiệp và hành vi học tập của
người dùng.
Mobile app
Có
Ứng dụng trên iOS và Android
cho phép học mọi lúc, đồng bộ
với tiến độhọc trên web.
Offline access
Có
Cho phép tải video và tài liệu
đểhọc offline qua ứng dụng di
dộng.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 15/254

=== SUMMARY ===
Đoạn văn bản phân tích các tính năng cốt lõi của LinkedIn Learning, tập trung vào việc hỗ trợ phát triển kỹ năng nghề nghiệp và cá nhân hóa trải nghiệm học tập. Các tính năng nổi bật bao gồm phát video chất lượng cao, các buổi học trực tiếp, diễn đàn thảo luận Q&A, và hệ thống tự động chấm điểm. Đặc biệt, nền tảng sử dụng AI để đề xuất khóa học dựa trên hồ sơ người dùng và hỗ trợ ứng dụng di động linh hoạt với khả năng học ngoại tuyến.

=== REVIEW QUESTIONS ===
1. LinkedIn Learning sử dụng công nghệ gì để cá nhân hóa việc đề xuất khóa học cho người dùng?
2. Tính năng ''Auto-grading'' trên LinkedIn Learning thường được áp dụng cho những loại bài tập nào?
3. Học viên có thể tương tác với giảng viên và cộng đồng thông qua tính năng nào trên nền tảng?
4. Lợi ích chính của tính năng ''Offline access'' trên ứng dụng di động của LinkedIn Learning là gì?','3fb59073-4059-4cd1-8584-e3d5eec56efe'::uuid,NULL,NULL,34,534,'2026-03-21 13:35:13.322607+07');
INSERT INTO coaching_chatbot.lecture_knowledge_chunks (lecture_knowledge_chunks_id,lecture_knowledge_id,chunk_index,chunk_content,qdrant_point_id,start_time_seconds,end_time_seconds,page_number,token_count,created_at) VALUES
	 ('1aaa7931-646b-4132-b8c8-7125fb2bbcf5'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,34,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Certificates
Có
Cấp chứng chỉhoàn thành khóa
học; có thểhiển thịtrực tiếp trên
hồsơ LinkedIn.
Progress tracking
Có
Theo dõi tiến độ, thời lượng học,
và các kỹnăng đã đạt được trong
từng khóa học.
Social learning
Có
Hỗtrợtương tác xã hội thông qua
bình luận, chia sẻkhóa học, và
hiển thịkỹnăng trên LinkedIn.
Integration with LinkedIn profile
Có
Tích hợp chặt chẽvới hồsơ
LinkedIn, cho phép thêm chứng
chỉvà kỹnăng trực tiếp.
Learning paths
Có
Chuỗi khóa học theo chủđềhoặc
nghềnghiệp (ví dụ: Data Ana-
lyst, Project Manager).
LinkedIn Learning thểhiện một mô hình học tập chuyên nghiệp, cá nhân hóa cao và
tích hợp sâu với hệsinh thái nghềnghiệp — điểm đặc biệt giúp người học kết nối trực
tiếp giữa việc “học” và “phát triển sựnghiệp”. Nền tảng tập trung mạnh vào kỹnăng
thực tiễn, chứng chỉgiá trịvà khảnăng tích hợp linh hoạt với các hệthống khác.
Khi xây dựng hệthống LMS cho Đại học Bách Khoa, nên học tập các tính năng sau:
• Cá nhân hóa hành trình học tập dựa trên dữliệu và mục tiêu của từng sinh viên.
• Tích hợp hồsơ học tập – nghềnghiệp, cho phép sinh viên thểhiện thành tựu và kỹ
năng (tương tựcách LinkedIn hiển thịchứng chỉ).
• Thiết kếgiao diện trực quan, hiện đại, dễdùng và tối ưu cho nhiều loại thiết bị.
• Tập trung vào lộtrình kỹnăng và học tập thực hành, giúp sinh viên gắn kết giữa
học thuật và ứng dụng thực tế.
Tổng thể, LinkedIn Learning là hình mẫu tiêu biểu cho một LMS hướng nghềnghiệp,
lấy người học làm trung tâm — một định hướng đáng tham khảo khi phát triển nền tảng
học tập hiện đại cho sinh viên Bách khoa.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 16/254

=== SUMMARY ===
Đoạn văn bản phân tích các tính năng nổi bật của LinkedIn Learning như cấp chứng chỉ, theo dõi tiến độ, học tập xã hội và lộ trình học tập theo nghề nghiệp. Điểm mạnh của nền tảng là sự tích hợp sâu với hồ sơ LinkedIn, giúp kết nối học thuật với phát triển sự nghiệp. Từ đó, tác giả đề xuất 4 hướng phát triển cho hệ thống LMS của Đại học Bách khoa: cá nhân hóa lộ trình, tích hợp hồ sơ kỹ năng, giao diện hiện đại và chú trọng thực hành.

=== REVIEW QUESTIONS ===
1. Các tính năng ''Certificates'' và ''Integration with LinkedIn profile'' hỗ trợ người học như thế nào trong việc phát triển sự nghiệp?
2. Khái niệm ''Learning paths'' trên LinkedIn Learning được hiểu như thế nào và ví dụ cụ thể là gì?
3. Tại sao LinkedIn Learning được coi là hình mẫu tiêu biểu cho một hệ thống quản lý học tập (LMS) hướng nghề nghiệp?
4. Dựa trên phân tích về LinkedIn Learning, những yếu tố nào cần được ưu tiên khi xây dựng hệ thống LMS cho sinh viên Đại học Bách khoa?','da57e6eb-b96d-4495-9352-b9acd401c09a'::uuid,NULL,NULL,35,671,'2026-03-21 13:35:13.322607+07'),
	 ('f0862758-4763-4e1b-823f-c7f56690c00e'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,35,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
2.4
Khảo sát thực tế
2.4.1
Tổng quan khảo sát
Đểhiểu rõ hơn vềnhu cầu và kỳvọng của người dùng đối với hệthống quản lý học
tập (LMS), nhóm đã tiến hành khảo sát thực tếvới sinh viên và giảng viên tại trường Đại
học Bách khoa TP.HCM. Khảo sát tập trung vào việc đánh giá các khía cạnh quan trọng
của LMS hiện tại và kỳvọng vềcác tính năng cần thiết.
2.4.2
Kết quảkhảo sát
a. Tần suất sửdụng LMS
Hình 2.5: Tần suất sửdụng hệthống LMS của sinh viên và giảng viên.
Hình 2.5 là biểu đồtổng kết kết quảkhảo sát tần suất sửdụng hệthống LMS của
sinh viên và giảng viên của trường Đại học Bách khoa cho thấy phần lớn người tham gia
(chiếm tỉlệcao nhất) sửdụng hệthống LMS với tần suất rất thường xuyên. Điều này
phản ánh mức độgắn bó và phụthuộc của sinh viên cũng như giảng viên vào hệthống
trong các hoạt động học tập và giảng dạy hàng ngày.
Tần suất sửdụng cao cho thấy LMS đã trởthành một công cụtrọng yếu trong quá
tình quản lý và tổchức học tập, đồng thời khẳng định tầm quan trọng của việc tiếp tục
cải tiến, tối ưu trải nghiệm người dùng và phát triển thêm các tính năng hỗtrợhọc tập –
giảng dạy hiệu quảhơn.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 17/254

=== SUMMARY ===
Đoạn văn bản trình bày kết quả khảo sát thực tế về hệ thống quản lý học tập (LMS) tại Trường Đại học Bách khoa TP.HCM. Khảo sát tập trung vào nhu cầu và kỳ vọng của sinh viên cùng giảng viên. Kết quả nổi bật cho thấy tần suất sử dụng LMS rất thường xuyên, minh chứng cho sự gắn bó và phụ thuộc của người dùng vào hệ thống trong hoạt động học tập hàng ngày. Từ đó, nhóm nghiên cứu nhấn mạnh nhu cầu cấp thiết trong việc cải tiến, tối ưu hóa trải nghiệm và phát triển thêm các tính năng hỗ trợ hiệu quả.

=== REVIEW QUESTIONS ===
1. Mục tiêu chính của việc tiến hành khảo sát thực tế đối với hệ thống LMS là gì?
2. Những đối tượng nào đã tham gia vào quá trình khảo sát về nhu cầu sử dụng LMS?
3. Kết quả khảo sát về tần suất sử dụng hệ thống LMS của sinh viên và giảng viên phản ánh điều gì?
4. Tại sao việc người dùng sử dụng LMS thường xuyên lại là căn cứ quan trọng để tiếp tục cải tiến hệ thống?','97cd1ed5-e09a-4377-9179-bc0d9ae3fc90'::uuid,NULL,NULL,36,550,'2026-03-21 13:35:13.322607+07'),
	 ('36a44934-3b94-4bca-8f9e-52609534dc5e'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,37,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Hình 2.8: Đánh giá các khía cạnh quan trọng của LMS.
Kết quảkhảo sát vềcác khía cạnh của hệthống LMS cho thấy người dùng có mức
đánh giá trung bình đến tốt ởhầu hết các tiêu chí, tuy nhiên vẫn tồn tại nhiều điểm cần
cải thiện rõ rệt.
• Giao diện và điều hướng: Phần lớn người dùng chỉđánh giá ởmức trung bình hoặc
tốt, phản ánh giao diện hiện tại tuy dễsửdụng nhưng chưa thật sựtối ưu vềtính
trực quan và khảnăng tìm kiếm nhanh.
• Tốc độtải trang và độổn định: Đây là một trong những yếu tốbịđánh giá thấp nhất,
với tỉlệ“trung bình” chiếm ưu thế. Điều này cho thấy vấn đềhiệu năng và khảnăng
phản hồi của hệthống vẫn cần được chú trọng cải thiện.
• Tìm kiếm tài liệu và quy trình nộp bài tập: Nhìn chung, người dùng đánh giá các
tính năng này ởmức “trung bình – tốt”, thểhiện mức độđáp ứng cơ bản nhưng chưa
mang lại trải nghiệm thật sựmượt mà hoặc thông minh.
• Trải nghiệm làm bài kiểm tra (Quiz) và xem điểm, phản hồi từgiảng viên nhận được
phản hồi tương đối tích cực, cho thấy chức năng kiểm tra – phản hồi vận hành ổn
định và hữu ích cho người học.
• Các công cụgiao tiếp (Forum, Tin nhắn) và công cụhỗtrợđánh dấu lịch vẫn còn
hạn chếvềtính năng, giao diện và độthuận tiện, dẫn đến mức hài lòng chưa cao.
• Trải nghiệm với bài tập tương tác (như lập trình) chỉđược đánh giá ởmức trung
bình, cho thấy cần cải thiện thêm vềtính năng và hiệu năng đểđáp ứng tốt hơn nhu
cầu học tập chuyên sâu.
Tổng thể, các kết quảnày chỉra rằng hệthống LMS hiện tại đáp ứng được nhu cầu học
tập cơ bản, nhưng vẫn còn nhiều tiềm năng đểnâng cao trải nghiệm người dùng, đặc biệt
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 19/254

=== SUMMARY ===
Đoạn văn bản tổng hợp đánh giá của người dùng về các khía cạnh của hệ thống LMS tại trường Đại học Bách khoa. Kết quả cho thấy hệ thống đáp ứng tốt các nhu cầu cơ bản như làm bài kiểm tra và xem điểm. Tuy nhiên, các yếu tố về hiệu năng (tốc độ tải trang, độ ổn định), giao diện người dùng và các công cụ tương tác (diễn đàn, tin nhắn, bài tập lập trình) vẫn chỉ ở mức trung bình và cần được cải thiện đáng kể để nâng cao trải nghiệm học tập.

=== REVIEW QUESTIONS ===
1. Khía cạnh nào của hệ thống LMS bị người dùng đánh giá thấp nhất trong khảo sát?
2. Người dùng nhận xét như thế nào về giao diện và khả năng điều hướng của hệ thống hiện tại?
3. Những tính năng nào của LMS nhận được phản hồi tương đối tích cực từ phía người học?
4. Tại sao các công cụ giao tiếp và bài tập tương tác (như lập trình) lại có mức độ hài lòng chưa cao?','0c26f36d-304c-4225-9afd-44eefe1d1b7e'::uuid,NULL,NULL,38,648,'2026-03-21 13:35:13.323222+07'),
	 ('b77ed862-762f-401d-841a-d0eb63d1e485'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,38,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
vềhiệu năng, giao diện, và tính linh hoạt trong tương tác học tập.
d. Mục tiêu sửdụng LMS
Hình 2.9: Mục tiêu chính khi sửdụng LMS.
Kết quảkhảo sát vềmục đích sửdụng hệthống LMS cho thấy người dùng chủyếu
khai thác hệthống cho các hoạt động học tập cốt lõi, bao gồm:
• Xem bài giảng/tài liệu học tập (93,8%) và làm bài quiz/trắc nghiệm (95,4%) là hai
hoạt động phổbiến nhất, phản ánh vai trò trung tâm của LMS trong việc truy cập
nội dung và đánh giá quá trình học.
• Làm bài tập chiếm tỉlệcao (86,2%), cho thấy LMS được sửdụng thường xuyên như
công cụnộp bài và theo dõi tiến độhọc tập.
• Xem điểm đạt mức 73,8%, thểhiện sựquan tâm lớn của sinh viên đến phản hồi và
kết quảhọc tập trên hệthống.
• Ngược lại, các tính năng như giao tiếp với giảng viên (15,4%) và cập nhật thông
tin (52,3%) có tần suất sửdụng thấp hơn đáng kể, cho thấy chức năng tương tác và
thông tin học vụtrên hệthống vẫn chưa được khai thác hiệu quả.
Nhìn chung, các kết quảnày chỉra rằng người dùng tập trung chủyếu vào các hoạt động
học thuật trực tiếp, trong khi các tính năng hỗtrợtương tác, trao đổi và cập nhật thông
tin vẫn còn tiềm năng đểphát triển, nhằm nâng cao trải nghiệm tổng thểvà tăng tính gắn
kết giữa sinh viên – giảng viên trên hệthống LMS.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 20/254

=== SUMMARY ===
Đoạn văn bản báo cáo về mục đích sử dụng hệ thống LMS của người dùng tại Khoa Khoa học & Kỹ thuật Máy tính. Kết quả khảo sát cho thấy các hoạt động cốt lõi như xem tài liệu (93,8%) và làm bài kiểm tra (95,4%) chiếm tỉ lệ cao nhất. Tuy nhiên, tính năng tương tác với giảng viên chỉ đạt 15,4%, cho thấy các công cụ giao tiếp và thông tin học vụ chưa được khai thác hiệu quả. Hệ thống cần phát triển thêm các tính năng tương tác để tăng tính gắn kết.

=== REVIEW QUESTIONS ===
1. Hai hoạt động nào trên hệ thống LMS có tỉ lệ người dùng sử dụng cao nhất theo kết quả khảo sát?
2. Tỉ lệ người dùng sử dụng LMS để xem điểm và nộp bài tập là bao nhiêu?
3. Tại sao tính năng giao tiếp với giảng viên (15,4%) bị đánh giá là chưa được khai thác hiệu quả?
4. Mục tiêu chính của việc phát triển thêm các tính năng tương tác và trao đổi trên LMS là gì?','dd84bd75-7419-440c-b83f-84c990c60415'::uuid,NULL,NULL,39,566,'2026-03-21 13:35:13.323222+07'),
	 ('6e98c8de-819e-4004-825d-721b1df24db8'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,39,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
e. Kỳvọng vềtính năng mới
Hình 2.10: Kỳvọng của người dùng vềcác tính năng mới.
Khi được hỏi vềưu tiên cải thiện quan trọng nhất của hệthống LMS, hơn một nửa
sốngười tham gia (55,4%) cho rằng hiệu năng của hệthống (Performance) cần được tập
trung nâng cấp, bao gồm tăng tốc độtải trang, giảm độtrễvà hạn chếlỗi. Điều này phản
ánh mối quan tâm hàng đầu của người dùng đối với tính ổn định và khảnăng vận hành
mượt mà của hệthống, vốn là yếu tốảnh hưởng trực tiếp đến trải nghiệm học tập.
Bên cạnh đó, 27,7% người dùng đềxuất nâng cấp hệthống thông báo (Notifications)
- như nhắc deadline, điểm số, và cập nhật tựđộng - cho thấy nhu cầu cao vềtính chủ
động và tiện ích trong việc theo dõi tiến độhọc tập.
Trong khi đó, 9,2% người dùng mong muốn cải thiện giao diện (UI/UX) trởnên hiện
đại và trực quan hơn, còn các khía cạnh khác như khảnăng tương thích di động, công cụ
tương tác – hợp tác, và tính năng nộp bài/quiz chỉchiếm tỉlệnhỏhơn (dưới 10%).
Tổng thể, kết quảnày chỉra rằng người dùng đánh giá tốc độ, độổn định và khảnăng
tựđộng hỗtrợhọc tập là những yếu tốưu tiên hàng đầu trong quá trình cải tiến hệthống
LMS. Việc tập trung cải thiện hai nhóm yếu tốnày sẽmang lại tác động lớn nhất đến
mức độhài lòng và hiệu quảsửdụng của người học và giảng viên.
2.4.3
Đánh giá và đềxuất tính năng
Dựa trên kết quảkhảo sát, nhóm đưa ra đánh giá và đềxuất cho các tính năng chính
của hệthống LMS:
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 21/254

=== SUMMARY ===
Đoạn văn này phân tích kỳ vọng của người dùng đối với việc nâng cấp hệ thống LMS. Kết quả khảo sát chỉ ra rằng hiệu năng hệ thống (chiếm 55,4%) và hệ thống thông báo (chiếm 27,7%) là hai ưu tiên hàng đầu. Người dùng đặc biệt quan tâm đến tốc độ tải trang, độ ổn định và tính chủ động trong việc nhắc nhở deadline. Ngược lại, các yếu tố về giao diện và tương thích di động nhận được ít sự ưu tiên hơn, cho thấy tính vận hành mượt mà là cốt lõi của trải nghiệm học tập.

=== REVIEW QUESTIONS ===
1. Theo kết quả khảo sát, yếu tố nào được người dùng ưu tiên cải thiện nhất và chiếm tỉ lệ bao nhiêu?
2. Hệ thống thông báo (Notifications) cần được nâng cấp những tính năng cụ thể nào để đáp ứng nhu cầu người dùng?
3. Tại sao hiệu năng của hệ thống (Performance) lại được coi là yếu tố ảnh hưởng trực tiếp đến trải nghiệm học tập?
4. Nhóm tính năng nào (giao diện, tương thích di động, công cụ tương tác) có tỉ lệ người dùng mong muốn cải thiện dưới 10%?','0d8e944a-5531-460d-ac5c-79d3d7a3e710'::uuid,NULL,NULL,40,633,'2026-03-21 13:35:13.323222+07'),
	 ('e4500ccb-a296-45e5-9990-9078490ac00f'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,40,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
a. Tính năng truy cập khóa học và tài liệu học tập
Đánh giá: Đây là tính năng cốt lõi được sửdụng nhiều nhất với tần suất cao hàng
ngày.
Đềxuất triển khai:
• Độưu tiên: Cao - Tính năng thiết yếu cần được triển khai đầu tiên
• Hỗtrợ đa dạng định dạng tài liệu: PDF, DOCX, video, audio, text
• Giao diện trực quan, dễ điều hướng với cấu trúc phân cấp rõ ràng
• Tính năng tìm kiếm nâng cao theo tên khóa học, nội dung, loại tài liệu
• Hỗtrợ xem trước tài liệu trực tuyến mà không cần tải về
• Tính năng đánh dấu và ghi chú cá nhân trên tài liệu
• Theo dõi tiến độ học tập
b. Tính năng làm bài tập và kiểm tra
Đánh giá: Tính năng quan trọng với nhu cầu cao về phản hồi tự động và đánh giá
khách quan.
Đềxuất triển khai:
• Độ ưu tiên: Cao - Cần thiết cho quá trình đánh giá học tập
• Hỗtrợ đa dạng loại câu hỏi: trắc nghiệm, tự luận, kéo thả, điền từ
• Hệthống chấm điểm tự động với phản hồi chi tiết
• Ngân hàng câu hỏi với khả năng phân loại theo độ khó và chủ đề
• Tính năng anti-cheating: giới hạn thời gian, trộn câu hỏi, chặn copy-paste
c. Tính năng thảo luận và tương tác
Đánh giá: Nhu cầu cao về tương tác xã hội trong môi trường học tập trực tuyến.
Đềxuất triển khai:
• Độ ưu tiên: Trung bình - Quan trọng nhưng có thể triển khai sau các tính năng cốt
lõi
• Diễn đàn thảo luận theo chủ đề và khóa học
• Chat trực tiếp giữa giảng viên và sinh viên
• Hệthống thông báo real-time cho tin nhắn và thảo luận mới
Báo cáo đồ án chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 22/254

=== SUMMARY ===
Văn bản trình bày đánh giá và đề xuất triển khai ba nhóm tính năng chính cho hệ thống LMS: truy cập tài liệu, làm bài tập/kiểm tra và thảo luận tương tác. Trong đó, tính năng quản lý tài liệu và kiểm tra được ưu tiên cao nhất vì là nhu cầu cốt lõi. Các đề xuất tập trung vào việc đa dạng hóa định dạng, tối ưu giao diện, hỗ trợ chấm điểm tự động, tích hợp công cụ chống gian lận và tăng cường kết nối thời gian thực giữa người dùng.

=== REVIEW QUESTIONS ===
1. Những tính năng nào được ưu tiên triển khai hàng đầu trong hệ thống LMS và tại sao?
2. Để hỗ trợ việc làm bài tập và kiểm tra hiệu quả, hệ thống cần tích hợp những cơ chế chống gian lận (anti-cheating) nào?
3. Tính năng truy cập tài liệu học tập cần đáp ứng những yêu cầu kỹ thuật gì để tối ưu hóa trải nghiệm người dùng?
4. Sự khác biệt về mức độ ưu tiên giữa nhóm tính năng tương tác và nhóm tính năng học thuật cốt lõi là gì?','7bd5356f-e1ff-4e35-8035-cfd26b26319d'::uuid,NULL,NULL,41,624,'2026-03-21 13:35:13.323222+07'),
	 ('452dfd49-eb45-4adc-b0cf-4f6aa4f407e2'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,41,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹ thuật Máy tính
d. Tính năng AI hỗ trợ học tập
Đánh giá: Tính năng tiên tiến có tiềm năng cao nhưng cần cân nhắc về tài nguyên
triển khai.
Đề xuất triển khai:
• Độ ưu tiên: Trung bình - Tính năng nâng cao, triển khai trong giai đoạn sau
• Chatbot AI hỗ trợ trả lời câu hỏi thường gặp
• Gợi ý nội dung học tập cá nhân hóa dựa trên tiến độ học tập
• Tự động tạo câu hỏi ôn tập từ tài liệu học tập
• Tóm tắt tự động nội dung bài giảng dài
e. Hệ thống thông báo thông minh
Đánh giá: Tính năng thiết yếu giúp người dùng theo dõi tiến độ và deadline.
Đề xuất triển khai:
• Độ ưu tiên: Cao - Cần thiết cho việc quản lý thời gian học tập
• Nhắc nhở thông minh về deadline bài tập, lịch thi
• Thông báo điểm số với biểu đồ tiến độ học tập
• Cập nhật tự động về tài liệu mới, thông báo từ giảng viên
• Lịch học cá nhân với tích hợp Google Calendar/Outlook
• Tùy chỉnh loại thông báo và tần suất theo nhu cầu của người dùng
f. Cải thiện hiệu suất và trải nghiệm người dùng
Đánh giá: Yếu tố quyết định sự thành công của hệ thống, ảnh hưởng đến trải nghiệm
người dùng.
Đề xuất triển khai:
• Độ ưu tiên: Cao - Cần được tối ưu liên tục trong suốt quá trình phát triển
• Tối ưu tốc độ tải trang với lazy loading và caching thông minh
• Responsive design tương thích đa thiết bị (desktop, tablet, mobile)
Báo cáo đồ án chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 23/254

=== SUMMARY ===
Nội dung đề xuất ba nhóm cải tiến quan trọng cho hệ thống LMS: ứng dụng AI (chatbot, tóm tắt bài giảng, cá nhân hóa), hệ thống thông báo thông minh (nhắc deadline, tích hợp lịch) và tối ưu hóa hiệu suất/UX (tốc độ tải, thiết kế tương thích). Trong đó, hệ thống thông báo và hiệu suất được ưu tiên cao vì ảnh hưởng trực tiếp đến trải nghiệm và quản lý học tập, còn AI là tính năng nâng cao triển khai sau.

=== REVIEW QUESTIONS ===
1. Các tính năng AI nào được đề xuất để hỗ trợ sinh viên trong việc ôn tập và tiếp nhận nội dung bài giảng?
2. Tại sao hệ thống thông báo thông minh lại có độ ưu tiên triển khai mức ''Cao''?
3. Những giải pháp kỹ thuật nào được đề xuất để tối ưu hóa tốc độ tải trang và trải nghiệm người dùng trên các thiết bị khác nhau?
4. Người dùng có thể cá nhân hóa việc nhận thông báo và theo dõi lịch học như thế nào theo đề xuất trên?','20b154ac-bae6-4107-8794-8720cde2bb56'::uuid,NULL,NULL,42,581,'2026-03-21 13:35:13.323898+07'),
	 ('d5d94f19-1b97-4a62-bab7-e09a45b2fc54'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,42,'=== ORIGINAL CONTENT ===
Trường Đại Học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa Học & KỹThuật Máy Tính
Chương 3
Phân tích hệthống
Nội dung chương này tập trung vào việc phân tích các khía cạnh yêu cầu của hệthống,
bao gồm xác định stakeholders (các bên liên quan), các yêu cầu chức năng và phi chức
năng, và mô hình hóa các quy trình thông qua biểu đồUse-Case, biểu đồhoạt động
(Activity Diagram) và biểu đồtuần tự(Sequence Diagram).
3.1
Stakeholders
3.1.1
Người dùng cuối
Hệthống bao gồm các nhóm đối tượng người dùng chính: Sinh viên, Giảng viên và
Quản trịviên hệthống. Chi tiết từng vai trò được mô tảnhư sau:
a. Sinh viên
Mô tả: Nhóm người dùng chính của hệthống, bao gồm tất cảsinh viên học tại trường
Đại học Bách khoa TP.HCM.
Vai trò và trách nhiệm:
• Sửdụng hệthống đểtiếp cận tài liệu học tập, bài giảng
• Tham gia các hoạt động học tập trực tuyến (quiz, assignment, discussion)
• Theo dõi tiến độhọc tập và kết quảđánh giá
• Tương tác với giảng viên và các sinh viên khác
• Cung cấp phản hồi vềchất lượng hệthống
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 24/254

=== SUMMARY ===
Chương 3 tập trung vào phân tích hệ thống, bao gồm xác định các bên liên quan (stakeholders), yêu cầu chức năng, phi chức năng và mô hình hóa quy trình qua các biểu đồ chuyên dụng. Trong đó, sinh viên được xác định là nhóm người dùng chính với các trách nhiệm cốt lõi như tiếp cận học liệu, tham gia hoạt động trực tuyến (quiz, thảo luận), theo dõi tiến độ học tập, tương tác với giảng viên và đóng góp phản hồi để cải thiện hệ thống.

=== REVIEW QUESTIONS ===
1. Chương 3 tập trung phân tích những khía cạnh nào của hệ thống?
2. Những loại biểu đồ nào được sử dụng để mô hình hóa các quy trình trong hệ thống?
3. Kể tên ba nhóm đối tượng người dùng cuối được xác định trong tài liệu.
4. Vai trò và trách nhiệm chính của sinh viên khi tham gia vào hệ thống là gì?','dce3bbc7-a6cf-4859-a9de-8d95c2a154f1'::uuid,NULL,NULL,43,469,'2026-03-21 13:35:13.323898+07'),
	 ('caaaefd8-8de9-4bc9-96d5-1dcb460788ce'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,43,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Nhu cầu và kỳvọng:
• Giao diện thân thiện, dễsửdụng trên mọi thiết bị
• Tốc độtruy cập nhanh và ổn định
• Hệthống thông báo thông minh và kịp thời
• Công cụhỗtrợhọc tập hiệu quả
• Bảo mật thông tin cá nhân và dữliệu học tập
Mức độảnh hưởng: Cao - Quyết định trực tiếp đến sựthành công của hệthống
b. Giảng viên
Mô tả: Đội ngũ giảng dạy tại trường Đại học Bách khoa TP.HCM, sửdụng hệthống
đểquản lý khóa học và tương tác với sinh viên.
Vai trò và trách nhiệm:
• Tạo và quản lý nội dung, tài nguyên học tập
• Thiết kếvà tổchức các hoạt động đánh giá (quiz, assignment, exam)
• Tương tác và hỗtrợsinh viên qua các kênh trực tuyến
• Theo dõi tiến độhọc tập của sinh viên
• Phân tích dữliệu đểcải thiện phương pháp giảng dạy
Nhu cầu và kỳvọng:
• Công cụtạo nội dung mạnh mẽvà linh hoạt
• Hệthống quản lý điểm sốvà báo cáo chi tiết
• Tích hợp với các công cụgiảng dạy hiện có
• Phân tích vềhiệu quảhọc tập của sinh viên
• Bảo mật nội dung và quyền sởhữu trí tuệ
Mức độảnh hưởng: Cao - Tạo ra nội dung chính và quyết định cách sửdụng hệ
thống
c. Quản trịviên hệthống
Mô tả: Đội ngũ kỹthuật chịu trách nhiệm vận hành, bảo trì và quản lý hệthống LMS.
Vai trò và trách nhiệm:
• Quản lý cấu hình hệthống và phân quyền người dùng
• Giám sát hiệu suất và bảo mật hệthống
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 25/254

=== SUMMARY ===
Nội dung này phân tích chi tiết các nhóm Stakeholders chính của hệ thống LMS, bao gồm Sinh viên, Giảng viên và Quản trị viên. Văn bản làm rõ vai trò, trách nhiệm, cùng các nhu cầu và kỳ vọng riêng biệt của từng nhóm đối tượng. Việc đáp ứng các yêu cầu về giao diện, tính năng quản lý, bảo mật và hiệu suất từ các bên liên quan được xác định là yếu tố then chốt, có mức độ ảnh hưởng cao đến sự thành công và hiệu quả vận hành của hệ thống.

=== REVIEW QUESTIONS ===
1. Những nhu cầu và kỳ vọng chính của nhóm đối tượng Sinh viên đối với hệ thống là gì?
2. Vai trò của Giảng viên trong việc quản lý nội dung và tương tác với sinh viên được mô tả như thế nào?
3. Nhóm Quản trị viên hệ thống chịu trách nhiệm cho những nhiệm vụ kỹ thuật quan trọng nào?
4. Tại sao mức độ ảnh hưởng của Giảng viên và Quản trị viên lại được đánh giá ở mức Cao đối với hệ thống?','36ab9993-5927-4303-b455-091a80ae6ff4'::uuid,NULL,NULL,44,576,'2026-03-21 13:35:13.324422+07'),
	 ('7215ec86-4969-43f9-887c-6fe1364498c5'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,44,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
• Backup và khôi phục dữliệu
• Cập nhật và bảo trì hệthống
• Hỗtrợkỹthuật cho người dùng cuối
• Quản lý tích hợp với các hệthống khác
Nhu cầu và kỳvọng:
• Dashboard quản trịtrực quan và mạnh mẽ
• Công cụkiểm tra và cảnh báo tựđộng
• Hệthống log chi tiết
Mức độảnh hưởng: Cao - Đảm bảo hệthống hoạt động ổn định và hiệu quả
3.1.2
Các bên cung cấp dịch vụthứ3
Mô tả: Các công ty cung cấp tích hợp bên ngoài như lưu trữ(AWS, Google), đăng
nhập (Google), AI service (Google Gemini).
Nhu cầu: Cần một môi trường tích hợp dễsửdụng và tương thích với ứng dụng, đồng
thời có quy trình bảo mật rõ ràng đểđảm bảo an toàn cho dữliệu người dùng. Các dịch
vụcần khảnăng mởrộng, bảo mật dữliệu và kết nối ổn định.
Lợi ích: Cung cấp dịch vụổn định, hỗtrợkỹthuật và tài liệu hướng dẫn chi tiết giúp
đội ngũ phát triển tích hợp nhanh chóng.
Mức độảnh hưởng: Trung bình - Ảnh hưởng đến hiệu suất và tính năng của hệthống
thông qua các dịch vụtích hợp
3.2
Yêu cầu chức năng
Dựa trên phân tích các hệthống đang có trên thịtrường và kết quảkhảo sát mà nhóm
đã tiến hành, hệthống WeLearning bao gồm các nhóm chức năng chính sau:
• Quản lý học tập cá nhân (Sinh viên): thiết lập mục tiêu, theo dõi tiến độ, lịch và thời
khóa biểu.
• Khám phá và đăng ký khóa học (Sinh viên): duyệt danh sách, điều kiện tiên quyết,
đánh giá khóa học.
• Truy cập học liệu và tài nguyên (Sinh viên): tài liệu đa định dạng, làm bài tập, thực
hành lập trình.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 26/254

=== SUMMARY ===
Phần nội dung này trình bày về vai trò của quản trị viên hệ thống và các bên cung cấp dịch vụ thứ ba trong hệ thống WeLearning. Quản trị viên chịu trách nhiệm vận hành, bảo trì và hỗ trợ kỹ thuật, trong khi các đối tác bên ngoài cung cấp hạ tầng lưu trữ và dịch vụ AI. Ngoài ra, văn bản còn liệt kê các nhóm chức năng chính dành cho sinh viên như quản lý học tập cá nhân, đăng ký khóa học và tiếp cận tài nguyên học tập.

=== REVIEW QUESTIONS ===
1. Quản trị viên hệ thống có những vai trò và trách nhiệm chính nào để đảm bảo hệ thống vận hành ổn định?
2. Các bên cung cấp dịch vụ thứ ba (như Google, AWS) có những nhu cầu và kỳ vọng gì khi tích hợp với hệ thống?
3. Nhóm chức năng ''Quản lý học tập cá nhân'' cung cấp cho sinh viên những công cụ cụ thể nào?
4. Mức độ ảnh hưởng của các bên cung cấp dịch vụ thứ ba đối với hệ thống được đánh giá như thế nào và tại sao?','b3abdb9a-50cf-4a3a-bed1-44ab4e12dad6'::uuid,NULL,NULL,45,619,'2026-03-21 13:35:13.324422+07');
INSERT INTO coaching_chatbot.lecture_knowledge_chunks (lecture_knowledge_chunks_id,lecture_knowledge_id,chunk_index,chunk_content,qdrant_point_id,start_time_seconds,end_time_seconds,page_number,token_count,created_at) VALUES
	 ('a9c29838-b6bc-4728-a1d9-a550781feb6f'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,84,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Hình 4.7: Mô hình quan hệcủa Course Management Service
Link hình ảnh: Tại đây
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 66/254

=== SUMMARY ===
Phần này tập trung vào mô hình quan hệ (Relational Model) của Course Management Service trong hệ thống. Dịch vụ này chịu trách nhiệm quản lý toàn diện các khóa học, từ cấu trúc chương trình đào tạo đến nội dung chi tiết như bài học, tài liệu và các hoạt động học tập liên quan, giúp đảm bảo dữ liệu được tổ chức một cách logic và hiệu quả.

=== REVIEW QUESTIONS ===
1. Hình 4.7 mô tả loại mô hình nào cho dịch vụ quản lý khóa học?
2. Dựa vào ngữ cảnh, Course Management Service quản lý những loại khóa học nào?
3. Nội dung chi tiết của một khóa học trong dịch vụ này bao gồm những thành phần gì?
4. Tại sao mô hình quan hệ lại cần thiết cho việc quản lý các hoạt động và tài liệu trong khóa học?','b866d09b-b3da-4958-8486-597f91ba532d'::uuid,NULL,NULL,85,242,'2026-03-21 13:35:13.332512+07'),
	 ('f0af3f6e-6e28-4a97-a835-e2e4b9bc4be5'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,45,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
• Nhận phản hồi và đánh giá (Sinh viên): tra cứu điểm, nhận xét giảng viên, đánh giá
khóa học.
• Giao tiếp và thông báo (Sinh viên): nhận thông báo quan trọng, tham gia thảo luận
học thuật.
• HỗtrợAI học tập (Sinh viên): chatbot hỏi đáp, tạo bài ôn tập, đềxuất lộtrình cá
nhân hóa.
• Quản lý khóa học và tài liệu (Giảng viên): tạo/chỉnh sửa khóa học, cấu trúc nội
dung, thang điểm, tài liệu.
• Hệthống bài tập và kiểm tra (Giảng viên): tạo bài tập đa dạng, nhập đềtrắc nghiệm,
cấu hình và xem trước.
• Phân tích và thống kê (Giảng viên): theo dõi tiến độsinh viên, xuất báo cáo học tập.
• Trợlý AI cho Giảng viên: sinh câu hỏi từtài liệu, hỗtrợxây dựng ngân hàng câu
hỏi.
• Quản lý người dùng và hệthống (Admin): tài khoản, phân quyền, lớp học, phê duyệt
nội dung.
• Thống kê và báo cáo hệthống (Admin): tiến trình chấm điểm, hoạt động người
dùng, kết quảhọc tập, báo cáo, thông báo, nhật ký hệthống.
Chi tiết từng nhóm chức năng được mô tảtrong các Epic bên dưới.
3.2.1
Epic 1: Quản lý học tập cá nhân (Sinh viên)
US-SV-001: Thiết lập mục tiêu học tập:
Là sinh viên, tôi muốn thiết lập các mục tiêu học tập cá nhân bao gồm chuyên ngành
mong muốn, điểm sốtrung bình tích luỹ(GPA) mục tiêu và thời điểm hoàn thành dự
kiến. Đểcó thểlập kếhoạch học tập rõ ràng và theo dõi tiến độđạt mục tiêu
US-SV-002: Theo dõi tổng quan học tập:
Là sinh viên, tôi muốn xem bảng tổng quan hiển thịtiến độhoàn thành khóa học,
sốbài tập đã nộp, tín chỉtích lũy. Đểtheo dõi mức độđạt được so với mục tiêu và nhận
cảnh báo khi tiến độchậm
US-SV-003: Nhập lịch học từnguồn ngoài:
Là sinh viên, tôi muốn nhập lịch học và lịch thi từcổng thông tin trường bằng cách
sao chép-dán. Đểđồng bộlịch học một cách nhanh chóng và tựđộng với hệthống LMS
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 27/254

=== SUMMARY ===
Đoạn văn bản mô tả các nhóm chức năng chính của hệ thống WeLearning dành cho sinh viên, giảng viên và quản trị viên. Sinh viên có thể quản lý học tập cá nhân, nhận hỗ trợ từ AI và tương tác học thuật. Giảng viên được cung cấp công cụ quản lý khóa học, bài tập và phân tích dữ liệu. Quản trị viên chịu trách nhiệm vận hành và báo cáo hệ thống. Epic 1 tập trung vào việc giúp sinh viên thiết lập mục tiêu, theo dõi tiến độ và đồng bộ lịch học.

=== REVIEW QUESTIONS ===
1. Hệ thống WeLearning cung cấp những tính năng hỗ trợ AI nào cho cả sinh viên và giảng viên?
2. Theo yêu cầu US-SV-001, sinh viên có thể thiết lập những mục tiêu học tập cụ thể nào?
3. Mục đích của chức năng ''Theo dõi tổng quan học tập'' (US-SV-002) đối với sinh viên là gì?
4. Sinh viên thực hiện việc đồng bộ lịch học từ nguồn bên ngoài vào hệ thống LMS bằng phương thức nào theo US-SV-003?','b7389d6f-6fe4-4af9-b59f-fe63fdcbb1f4'::uuid,NULL,NULL,46,693,'2026-03-21 13:35:13.324801+07'),
	 ('36fe2007-109e-4f4c-90fb-bfdee597983b'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,46,'=== ORIGINAL CONTENT ===
sao chép-dán. Đểđồng bộlịch học một cách nhanh chóng và tựđộng với hệthống LMS
US-SV-004: Truy cập thời khóa biểu:
Là sinh viên, tôi muốn xem thời khóa biểu dưới dạng lịch tuần/tháng với đầy đủ
thông tin học tập. Đểquản lý thời gian và không bỏlỡcác hoạt động quan trọng
3.2.2
Epic 2: Khám phá và đăng ký khóa học (Sinh viên)
US-SV-005: Xem danh sách khóa học:
Là sinh viên, tôi muốn xem danh sách các khóa học đang mở, đã đăng ký và đang
học. Đểquản lý và tìm kiếm các khóa học phù hợp với nhu cầu học tập
US-SV-006: Đăng ký khóa học có điều kiện:
Là sinh viên, tôi muốn đăng ký các khóa học mởnếu đủđiều kiện tiên quyết. Đểtiếp
tục tiến độhọc tập theo đúng chương trình đào tạo
US-SV-007: Xem thông tin chi tiết khóa học:
Là sinh viên, tôi muốn xem thông tin chi tiết của khóa học gồm mục tiêu, đềcương,
đánh giá. Đểhiểu rõ nội dung và quyết định có nên đăng ký hay không
US-SV-008: Xem đánh giá từsinh viên trước:
Là sinh viên, tôi muốn đọc đánh giá và nhận xét từnhững sinh viên đã học khóa học.
Đểcó thông tin tham khảo vềchất lượng khóa học trước khi đăng ký
3.2.3
Epic 3: Truy cập học liệu và tài nguyên (Sinh viên)
US-SV-009: Truy cập tài liệu học tập:
Là sinh viên, tôi muốn truy cập và tải vềtài liệu học tập đa định dạng do giảng viên
cung cấp. Đểhọc tập hiệu quảvới các loại tài liệu phù hợp
US-SV-010: Làm bài tập và kiểm tra trực tuyến:
Là sinh viên, tôi muốn làm và nộp bài tập/quiz trực tuyến với nhiều cách thức khác
nhau. Đểhoàn thành các yêu cầu đánh giá của khóa học một cách linh hoạt
US-SV-011: Làm bài tập lập trình:
Là sinh viên, tôi muốn viết, chạy thửvà nộp code trực tiếp trong LMS. Đểhoàn
thành bài tập lập trình mà không cần công cụbên ngoài
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 28/254

=== SUMMARY ===
Đoạn văn bản chi tiết các yêu cầu chức năng (User Stories) cho sinh viên trong hệ thống WeLearning qua ba Epic chính: Quản lý học tập (đồng bộ lịch học và thời khóa biểu), Khám phá và đăng ký khóa học (xem danh sách, điều kiện tiên quyết và đánh giá), và Truy cập tài nguyên (tải tài liệu, làm bài tập trực tuyến và thực hành lập trình trực tiếp). Các chức năng này giúp sinh viên chủ động quản lý lộ trình học tập và tương tác hiệu quả với học liệu.

=== REVIEW QUESTIONS ===
1. Sinh viên có thể thực hiện những thao tác nào để quản lý thời gian học tập dựa trên Epic 1?
2. Điều kiện cần thiết để sinh viên có thể đăng ký một khóa học mới trong hệ thống là gì?
3. Việc xem đánh giá từ những sinh viên trước (US-SV-008) giúp ích gì cho người dùng mới?
4. Chức năng làm bài tập lập trình (US-SV-011) mang lại sự tiện lợi gì so với phương pháp truyền thống?','d3bdc320-4312-49bf-9d98-2785edaebfe7'::uuid,NULL,NULL,47,659,'2026-03-21 13:35:13.324801+07'),
	 ('062904c0-8555-4ef7-98a0-a6f50f094036'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,47,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
3.2.4
Epic 4: Nhận phản hồi và đánh giá (Sinh viên)
US-SV-012: Tra cứu điểm và nhận xét:
Là sinh viên, tôi muốn xem điểm sốchi tiết và nhận xét từgiảng viên cho từng bài
làm. Đểhiểu được mức độhiểu bài và cách cải thiện
US-SV-013: Đánh giá khóa học:
Là sinh viên, tôi muốn đánh giá khóa học sau khi hoàn thành bằng biểu mẫu chi tiết.
Đểđóng góp phản hồi giúp cải thiện chất lượng giảng dạy
3.2.5
Epic 5: Giao tiếp và thông báo (Sinh viên)
US-SV-014: Nhận thông báo học tập:
Là sinh viên, tôi muốn nhận thông báo kịp thời vềcác sựkiện học tập quan trọng.
Đểkhông bỏlỡdeadline, lịch thi và các thông báo quan trọng
US-SV-015: Tham gia thảo luận học thuật:
Là sinh viên, tôi muốn tham gia diễn đàn thảo luận đểđặt câu hỏi và chia sẻkiến
thức. Đểhọc hỏi từbạn bè và nhận hỗtrợtừgiảng viên
3.2.6
Epic 6: HỗtrợAI học tập (Sinh viên)
US-SV-016: Hỏi đáp với Chatbot AI:
Là sinh viên, tôi muốn đặt câu hỏi cho AI chatbot vềnội dung bài giảng. Đểnhận trợ
giúp ngay lập tức khi gặp khó khăn trong học tập
US-SV-017: Tạo bài ôn tập bằng AI:
Là sinh viên, tôi muốn AI tựđộng tạo bài kiểm tra ôn tập dựa trên nội dung đã học.
Đểrèn luyện kiến thức và chuẩn bịcho kỳthi
US-SV-018: Nhận lộtrình học tập cá nhân hóa:
Là sinh viên, tôi muốn AI đềxuất lộtrình học tập phù hợp với mục tiêu cá nhân. Để
tối ưu hóa thời gian học và đạt được mục tiêu đã đềra
3.2.7
Epic 7: Quản lý khóa học và tài liệu (Giảng viên)
US-TE-001: Quản lý thông tin khóa học:
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 29/254

=== SUMMARY ===
Nội dung này chi tiết hóa các yêu cầu chức năng (User Stories) từ Epic 4 đến Epic 7 của một hệ thống quản lý học tập. Trọng tâm bao gồm việc sinh viên tra cứu phản hồi và đánh giá khóa học, các kênh giao tiếp và thông báo học vụ, cùng các công cụ AI hỗ trợ học tập cá nhân hóa như chatbot và tự động tạo bài ôn tập. Ngoài ra, tài liệu bắt đầu đề cập đến vai trò của giảng viên trong việc quản lý thông tin khóa học.

=== REVIEW QUESTIONS ===
1. Việc tra cứu điểm và nhận xét chi tiết giúp ích gì cho sinh viên trong quá trình học tập?
2. Sinh viên có thể sử dụng diễn đàn thảo luận (US-SV-015) vào những mục đích cụ thể nào?
3. Nêu ba cách mà trí tuệ nhân tạo (AI) hỗ trợ sinh viên trong Epic 6 để tối ưu hóa việc học.
4. Theo US-TE-001, mục tiêu của giảng viên khi quản lý và chỉnh sửa thông tin khóa học là gì?','70a847d3-eac3-4fc5-8c7a-2f542649eb73'::uuid,NULL,NULL,48,609,'2026-03-21 13:35:13.32536+07'),
	 ('387803bf-568e-4527-b52d-a7afcdbeab22'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,48,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Là giảng viên, tôi muốn tạo mới và chỉnh sửa thông tin khóa học một cách toàn diện. Đểcung cấp đầy đủthông tin cần thiết cho sinh viên
US-TE-002: Gắn danh mục và thẻ:
Là giảng viên, tôi muốn phân loại khóa học bằng danh mục và thẻ. Đểgiúp sinh viên
tìm kiếm và tổchức nội dung hiệu quả
US-TE-003: Xây dựng cấu trúc khóa học:
Là giảng viên, tôi muốn tổchức nội dung thành cấu trúc phân cấp rõ ràng. Đểsinh
viên dễtheo dõi và học tập có hệthống
US-TE-004: Thiết lập thang điểm:
Là giảng viên, tôi muốn cấu hình hệthống thang điểm linh hoạt cho khóa học. Để
đánh giá sinh viên theo tiêu chuẩn phù hợp
US-TE-005: Quản lý tài liệu học tập:
Là giảng viên, tôi muốn tải lên và quản lý các loại tài liệu học tập đa dạng. Đểcung
cấp nguồn học liệu phong phú cho sinh viên
3.2.8
Epic 8: Hệthống bài tập và kiểm tra (Giảng viên)
US-TE-006: Tạo bài tập đa dạng:
Là giảng viên, tôi muốn tạo các loại bài tập khác nhau phù hợp với nội dung giảng
dạy. Đểđánh giá sinh viên theo nhiều hình thức và mức độ
US-TE-007: Nhập đềtrắc nghiệm nhanh:
Là giảng viên, tôi muốn nhập đềtrắc nghiệm từExcel hoặc tạo trực tiếp. Đểtiết kiệm
thời gian và tận dụng đềcó sẵn
US-TE-008: Cấu hình bài tập chi tiết:
Là giảng viên, tôi muốn thiết lập các thuộc tính chi tiết cho từng bài tập. Đểkiểm
soát quá trình làm bài và chấm điểm
US-TE-009: Xem trước giao diện bài tập:
Là giảng viên, tôi muốn xem trước bài tập từgóc nhìn sinh viên. Đểđảm bảo nội
dung hiển thịchính xác và rõ ràng
US-TE-010: Chấm điểm và nhận xét:
Là giảng viên, tôi muốn chấm điểm thủcông và đểlại phản hồi chi tiết. Đểđánh giá
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 30/254

=== SUMMARY ===
Tài liệu liệt kê các yêu cầu người dùng dành cho giảng viên trên hệ thống LMS, tập trung vào hai nhóm chức năng chính: quản lý khóa học và hệ thống bài tập. Giảng viên có khả năng thiết lập thông tin, cấu trúc phân cấp, thang điểm và tài liệu học tập. Đồng thời, hệ thống hỗ trợ tạo bài tập đa dạng, nhập đề trắc nghiệm từ Excel, cấu hình chi tiết, xem trước giao diện dưới góc nhìn sinh viên và thực hiện chấm điểm kèm phản hồi chi tiết.

=== REVIEW QUESTIONS ===
1. Giảng viên có thể sử dụng những công cụ nào để giúp sinh viên phân loại và tìm kiếm nội dung khóa học hiệu quả?
2. Theo US-TE-003, việc tổ chức nội dung thành cấu trúc phân cấp rõ ràng nhằm mục đích gì đối với sinh viên?
3. Hệ thống cung cấp những phương thức nào để giảng viên có thể nhập đề thi trắc nghiệm một cách nhanh chóng?
4. Tại sao tính năng xem trước giao diện bài tập (US-TE-009) lại cần thiết đối với giảng viên trước khi chính thức giao bài?','e6608f19-fdba-436d-870e-d5e018ec92ab'::uuid,NULL,NULL,49,670,'2026-03-21 13:35:13.32536+07'),
	 ('c3c10ce7-34cd-4fde-8d71-8e97097b9f3b'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,49,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
và hướng dẫn sinh viên cải thiện
3.2.9
Epic 9: Phân tích và thống kê (Giảng viên)
US-TE-011: Theo dõi tiến độhọc tập:
Là giảng viên, tôi muốn theo dõi tiến độhọc tập của từng sinh viên trong khóa học.
Đểphát hiện sớm những sinh viên gặp khó khăn và hỗtrợkịp thời
US-TE-012: Xuất báo cáo học tập:
Là giảng viên, tôi muốn xuất báo cáo chi tiết vềkết quảhọc tập. Đểbáo cáo với đơn
vịquản lý và phân tích hiệu quảgiảng dạy
3.2.10
Epic 10: Trợlý AI cho Giảng viên
US-TE-013: AI tạo câu hỏi từtài liệu:
Là giảng viên, tôi muốn AI tựđộng sinh câu hỏi kiểm tra từtài liệu bài giảng. Để
tiết kiệm thời gian và tạo ra ngân hàng câu hỏi đa dạng
3.2.11
Epic 11: Quản lý người dùng và hệthống (Admin)
US-AD-001: Quản lý tài khoản người dùng:
Là admin, tôi muốn quản lý toàn bộtài khoản người dùng trong hệthống. Đểđảm
bảo access control và bảo mật hệthống
US-AD-002: Quản lý phân quyền:
Là admin, tôi muốn định nghĩa và cấu hình quyền truy cập cho từng vai trò. Đểkiểm
soát chặt chẽquyền sửdụng các tính năng
US-AD-003: Gán sinh viên vào lớp:
Là admin, tôi muốn gán sinh viên vào các lớp và chương trình đào tạo. Đểtổchức
quản lý học vụcó hệthống
US-AD-004: Quản lý danh sách khóa học:
Là admin, tôi muốn xem và quản lý tất cảkhóa học trong hệthống. Đểgiám sát nội
dung và chất lượng giảng dạy
US-AD-005: Phê duyệt nội dung khóa học:
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 31/254

=== SUMMARY ===
Đoạn văn bản mô tả các yêu cầu chức năng dành cho Giảng viên và Quản trị viên (Admin) trong hệ thống quản lý học tập. Giảng viên được hỗ trợ các công cụ phân tích tiến độ, xuất báo cáo và trợ lý AI để tạo câu hỏi ôn tập. Quản trị viên chịu trách nhiệm quản lý tài khoản, phân quyền, điều phối lớp học và phê duyệt nội dung khóa học. Các chức năng này nhằm mục tiêu tối ưu hóa hiệu quả giảng dạy, đảm bảo chất lượng đào tạo và tính bảo mật của hệ thống.

=== REVIEW QUESTIONS ===
1. Việc theo dõi tiến độ học tập (US-TE-011) giúp ích gì cho giảng viên trong việc hỗ trợ sinh viên?
2. Lợi ích chính của việc sử dụng AI để tạo câu hỏi từ tài liệu bài giảng (US-TE-013) là gì?
3. Quản trị viên sử dụng chức năng US-AD-002 để kiểm soát quyền truy cập hệ thống như thế nào?
4. Mục đích của việc admin thực hiện phê duyệt nội dung khóa học trước khi công khai là gì?','23772659-3d89-4a5f-9de3-369235a94489'::uuid,NULL,NULL,50,593,'2026-03-21 13:35:13.325874+07'),
	 ('095cdd1b-e970-4b81-aa31-03d4e6a9e4d4'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,50,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Là admin, tôi muốn review và phê duyệt khóa học trước khi công khai. Đểđảm bảo
chất lượng và tuân thủquy định đào tạo
3.2.12
Epic 12: Thống kê và báo cáo hệthống (Admin)
US-AD-006: Theo dõi tiến trình chấm điểm:
Là admin, tôi muốn giám sát tiến trình chấm điểm của các lớp học. Đểđảm bảo việc
đánh giá được thực hiện đúng tiến độ
US-AD-007: Thống kê hoạt động người dùng:
Là admin, tôi muốn theo dõi mức độsửdụng hệthống của người dùng. Đểđánh giá
engagement và hiệu quảcủa platform
US-AD-008: Thống kê kết quảhọc tập:
Là admin, tôi muốn phân tích kết quảhọc tập tổng thể. Đểđánh giá hiệu quảđào tạo
và cải tiến chương trình
US-AD-009: Xuất báo cáo dữliệu:
Là admin, tôi muốn xuất các báo cáo tùy chỉnh vềhệthống. Đểphục vụnhu cầu báo
cáo và phân tích đa dạng
US-AD-010: Gửi thông báo hệthống:
Là admin, tôi muốn gửi thông báo đến người dùng hoặc nhóm cụthể. Đểtruyền đạt
thông tin quan trọng và cập nhật hệthống
US-AD-011: Xem nhật ký hệthống:
Là admin, tôi muốn truy cập audit log của tất cảhoạt động trong hệthống. Đểgiám
sát bảo mật và troubleshoot các vấn đề
3.3
Yêu cầu phi chức năng
Bên cạnh các yêu cầu chức năng, danh sách yêu cầu phi chức năng của hệthống bao
gồm:
• Hiệu năng (Performance): 90% lượt truy cập tải giao diện chính < 3 giây; các API
thường dùng phản hồi trung bình < 2s.
• Bảo mật (Security):
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 32/254

=== SUMMARY ===
Đoạn văn bản chi tiết hóa các yêu cầu chức năng dành cho vai trò Quản trị viên (Admin) và các tiêu chuẩn phi chức năng của hệ thống LMS. Các nhiệm vụ chính của Admin bao gồm phê duyệt khóa học, giám sát tiến độ chấm điểm, thống kê mức độ tương tác và kết quả học tập, cũng như quản lý nhật ký hệ thống (audit log). Về yêu cầu phi chức năng, hệ thống đặt mục tiêu cao về hiệu năng với thời gian tải trang dưới 3 giây và phản hồi API dưới 2 giây.

=== REVIEW QUESTIONS ===
1. Tại sao vai trò Admin cần phải phê duyệt khóa học trước khi công khai trên hệ thống?
2. Admin có thể thực hiện những loại thống kê và báo cáo nào để đánh giá hiệu quả đào tạo?
3. Chỉ số hiệu năng (Performance) cụ thể mà hệ thống cần đạt được đối với giao diện và API là gì?
4. Tính năng ''Xem nhật ký hệ thống'' (US-AD-011) đóng vai trò gì trong việc bảo mật và duy trì hệ thống?','a8dc134f-40cb-4283-8018-c27ea5ea440c'::uuid,NULL,NULL,51,591,'2026-03-21 13:35:13.325874+07'),
	 ('739d5e92-9d67-46f5-86ef-1500b16469fb'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,51,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
– Xác thực người dùng bằng OAuth2/SSO, hỗtrợMFA cho tài khoản quản trịvà
giảng viên.
– Phân quyền dựa trên vai trò (RBAC), đảm bảo mỗi người dùng chỉtruy cập
được tài nguyên phù hợp.
– Toàn bộgiao tiếp qua HTTPS (TLS 1.2+).
– Cơ chếchống tấn công phổbiến (SQL Injection, XSS, CSRF, brute force) và
rate limiting cho API.
• Khảnăng mởrộng (Scalability): Hỗtrợscale-out (thêm node) và scale-up (tăng tài
nguyên) đểchịu tải lên đến 30.000 users đồng thời. Tựđộng cân bằng tải.
• Tính sẵn sàng (Availability): Đảm bảo hệthống hoạt động ≥95% hoặc thời gian
bảo trì phải được sắp xếp vào thời gian mà báo cáo log cho thấy user sửdụng ít
nhất.
• Khảnăng sửdụng (Usability): Giao diện người dùng phải thân thiện, dễsửdụng và
đáp ứng nhu cầu của người dùng cuối.
• Khảnăng phục hồi (Recoverability): Hệthống phải có cơ chếsao lưu và phục hồi
dữliệu trong trường hợp xảy ra sựcố.
• Dễbảo trì (Maintainability): Kiến trúc module, coding standard, CI/CD. Coverage
unit test ≥80%. Tài liệu API & developer guide.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 33/254

=== SUMMARY ===
Đoạn văn bản liệt kê các yêu cầu phi chức năng trọng yếu của hệ thống, bao gồm: bảo mật đa lớp (OAuth2, RBAC, HTTPS, chống tấn công), khả năng mở rộng để đáp ứng 30.000 người dùng đồng thời, tính sẵn sàng ≥95%, và khả năng phục hồi dữ liệu. Ngoài ra, hệ thống chú trọng tính dễ bảo trì thông qua kiến trúc module, CI/CD, và độ bao phủ kiểm thử (unit test) ≥80% nhằm đảm bảo chất lượng vận hành ổn định và hiệu quả.

=== REVIEW QUESTIONS ===
1. Hệ thống sử dụng những phương thức nào để thực hiện xác thực người dùng và bảo mật giao tiếp dữ liệu?
2. Khả năng mở rộng (Scalability) của hệ thống được thiết kế để đáp ứng tải trọng tối đa là bao nhiêu người dùng đồng thời?
3. Trong trường hợp cần bảo trì, hệ thống ưu tiên lựa chọn thời điểm nào để đảm bảo tính sẵn sàng (Availability)?
4. Những tiêu chuẩn kỹ thuật nào được áp dụng để đảm bảo tính dễ bảo trì (Maintainability) và chất lượng mã nguồn của hệ thống?','a3e76e95-b4d5-46d2-9a17-1240af960b6c'::uuid,NULL,NULL,52,526,'2026-03-21 13:35:13.325874+07'),
	 ('9e4a42aa-4a09-48d9-a51d-d154f6fd9dd0'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,52,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
3.4
Lược đồuse-case (UC)
3.4.1
Lược đồuse-case đối với student
Sơ đồusecase dành cho sinh viên được minh hoạởhình 3.1.
Hình 3.1: Sơ đồusecase đối với student
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 34/254

=== SUMMARY ===
Phần văn bản này giới thiệu về lược đồ Use-case (UC) trong báo cáo đồ án chuyên ngành Khoa Khoa học & Kỹ thuật Máy tính. Cụ thể, mục 3.4.1 tập trung vào việc mô tả các chức năng và tương tác của đối tượng sinh viên (student) với hệ thống thông qua sơ đồ minh họa tại Hình 3.1. Đây là cơ sở để hiểu rõ các quyền hạn và hành động mà người học có thể thực hiện trên nền tảng.

=== REVIEW QUESTIONS ===
1. Mục 3.4.1 trong tài liệu tập trung vào lược đồ use-case của đối tượng người dùng nào?
2. Hình 3.1 trong báo cáo dùng để minh họa nội dung gì?
3. Tài liệu này thuộc báo cáo của môn học nào và mã môn học là gì?
4. Dựa trên ngữ cảnh, lược đồ use-case đóng vai trò gì trong việc phân tích yêu cầu hệ thống?','6004cb6f-c3ef-4eb9-a523-6a3a3fae4c24'::uuid,NULL,NULL,53,264,'2026-03-21 13:35:13.32647+07'),
	 ('865cc8dc-d970-43f9-8a5d-1ec27b09a222'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,85,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
4.2
Kiến trúc hệthống
4.2.1
So sánh các mô hình kiến trúc phần mềm phổbiến
a. Kiến trúc phân lớp (Layered Architecture)
Kiến trúc phân lớp được mô tảdưới Hình 4.8 là một mô hình kiến trúc phần mềm
trong đó hệthống được tổchức thành nhiều lớp (layers) riêng biệt, mỗi lớp đảm nhận
một nhóm chức năng cụthể. Các lớp giao tiếp với nhau thông qua các giao diện được
định nghĩa rõ ràng, thường theo nguyên tắc lớp trên sửdụng dịch vụcủa lớp dưới [7,8].
Hình 4.8: Mô hình kiến trúc phân lớp
Ưu điểm:
• Dễhiểu và dễphát triển do tách biệt rõ ràng các mối quan tâm (separation of con-
cerns).
• Thuận tiện cho việc bảo trì và kiểm thửdo mỗi lớp có thểđược kiểm tra độc lập.
• Phù hợp với các hệthống có yêu cầu nghiệp vụổn định và ít thay đổi.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 67/254

=== SUMMARY ===
Đoạn văn bản giới thiệu về kiến trúc phân lớp (Layered Architecture), một mô hình tổ chức hệ thống thành các tầng chức năng riêng biệt. Các lớp tương tác với nhau qua giao diện xác định, trong đó lớp trên sử dụng dịch vụ của lớp dưới. Mô hình này nổi bật với khả năng tách biệt các mối quan tâm, giúp dễ dàng phát triển, bảo trì và kiểm thử độc lập. Đây là lựa chọn lý tưởng cho các hệ thống có yêu cầu nghiệp vụ ổn định.

=== REVIEW QUESTIONS ===
1. Kiến trúc phân lớp (Layered Architecture) tổ chức các thành phần trong hệ thống như thế nào?
2. Nguyên tắc giao tiếp chủ yếu giữa các lớp trong mô hình kiến trúc này là gì?
3. Tại sao việc áp dụng kiến trúc phân lớp lại giúp quá trình kiểm thử phần mềm trở nên thuận tiện hơn?
4. Kiến trúc phân lớp phù hợp nhất với những loại hệ thống có đặc điểm gì?','48c99e63-f11f-47f3-8c63-081fc6eba7a8'::uuid,NULL,NULL,86,433,'2026-03-21 13:35:13.332512+07');
INSERT INTO coaching_chatbot.lecture_knowledge_chunks (lecture_knowledge_chunks_id,lecture_knowledge_id,chunk_index,chunk_content,qdrant_point_id,start_time_seconds,end_time_seconds,page_number,token_count,created_at) VALUES
	 ('470125dd-95cf-4447-9ace-d703dbb8ce9f'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,53,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
3.4.1.1
UC-02: Thiết lập mục tiêu học tập
Đặc tảUC-02: Thiết lập mục tiêu học tập được trình bày trong bảng 3.1.
Bảng 3.1: Đặc tảUC-02: Thiết lập mục tiêu học tập
Use-case Code
UC-02
Use-case Name
Thiết lập mục tiêu học tập
Description
Sinh viên khai báo hoặc cập nhật mục tiêu học tập cá nhân. Hệ
thống hướng dẫn theo trình tự4 bước:
1. Chọn chuyên ngành
2. Đặt mục tiêu và ưu tiên học tập
3. Đánh giá mức độkhảthi
4. Xem lại và xác nhận
Nếu sinh viên truy cập trang “Lộtrình học cá nhân” lần đầu và
chưa có mục tiêu, hệthống sẽyêu cầu thực hiện UC này.
Actors
Sinh viên
Trigger
Sinh viên vào trang “Lộtrình học cá nhân” lần đầu hoặc chọn
chỉnh sửa mục tiêu học tập trong trang “Lộtrình học cá nhân”.
Pre-Conditions
• Đăng nhập hợp lệ; hồsơ sinh viên tồn tại.
• Dữliệu chương trình đào tạo (CTĐT) và quy định thời gian
đào tạo tối đa (ví dụ: 6 năm) đã sẵn sàng.
• Dữliệu học tập hiện tại của sinh viên sẵn có (GPA, tín chỉ
tích lũy...).
Post Conditions
• Mục tiêu đã được đánh giá và xác nhận tính khảthi.
• Mục tiêu được lưu và gắn với CTĐT tương ứng.
• Mục tiêu có thểđược sửdụng đểsinh lộtrình học cá nhân
(UC-03).
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 35/254

=== SUMMARY ===
Phân đoạn này trình bày đặc tả chi tiết cho Use-case UC-02: Thiết lập mục tiêu học tập trong hệ thống quản lý học tập. Nội dung mô tả quy trình 4 bước giúp sinh viên khai báo chuyên ngành, mục tiêu GPA, thời gian tốt nghiệp và các ưu tiên cá nhân. Tài liệu xác định rõ các tác nhân, điều kiện tiên quyết về dữ liệu đào tạo và kết quả sau khi thực hiện là cơ sở để hệ thống tự động sinh lộ trình học tập cá nhân hóa (UC-03).

=== REVIEW QUESTIONS ===
1. Quy trình thực hiện UC-02 ''Thiết lập mục tiêu học tập'' bao gồm những bước cụ thể nào?
2. Những điều kiện tiên quyết (Pre-conditions) nào về dữ liệu sinh viên và hệ thống cần có trước khi thực hiện UC này?
3. Trường hợp nào hệ thống sẽ tự động yêu cầu sinh viên phải thực hiện đặc tả UC-02?
4. Kết quả sau khi hoàn thành UC-02 (Post-conditions) sẽ hỗ trợ cho việc thực hiện Use-case nào tiếp theo?','3d0c29cd-15a2-47b8-b3db-96cd654c43af'::uuid,NULL,NULL,54,540,'2026-03-21 13:35:13.32647+07'),
	 ('42c24484-2452-4fbf-82ab-525df3078afb'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,54,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Normal Flow
1. Chọn chuyên ngành
• Hệthống hiển thịdanh sách chuyên ngành hợp lệ.
• Sinh viên chọn một chuyên ngành (bắt buộc).
• Hiển thịthông báo: “Bạn có thểthay đổi sau, lộtrình
sẽđược tối ưu lại tương ứng.”
2. Mục tiêu & ưu tiên học tập
Sinh viên thực hiện khai báo theo trình tựsau đểđảm bảo
logic:
A. Các chỉsốcơ bản:
• GPA mục tiêu: Nhập con sốmong muốn (thang 4.0,
bắt buộc).
• Thời gian tốt nghiệp dựkiến: Sinh viên chọn kỳ/năm
muốn ra trường (VD: HK1–2027).
B. Cường độhọc tập (Học kỳchính):
• Sinh viên chọn sốlượng tín chỉmong muốn học trong
mỗi học kỳchính từcác tùy chọn có sẵn:
– Nhẹnhàng (x ≤9 tín chỉ)
– Trung bình (9 < x ≤13 tín chỉ)
– Khá (13 < x ≤17 tín chỉ)
– Nặng (17 < x ≤22 tín chỉ)
C. Kếhoạch học Hè:
• Dựa vào Thời gian tốt nghiệp dựkiến (ởmục A) và
quy định tối đa 6 năm, hệthống tính ra sốlượng kỳhè
khảthi còn lại (x).
• Hệthống hiển thịcâu hỏi: "Bạn dựđịnh học bao nhiêu
kỳhè?"kèm danh sách lựa chọn từ0 đến x.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 36/254

=== SUMMARY ===
Đoạn văn bản mô tả quy trình chuẩn (Normal Flow) để sinh viên thiết lập mục tiêu học tập. Quy trình gồm các bước: chọn chuyên ngành bắt buộc, xác định các chỉ số cơ bản (GPA mục tiêu, thời gian tốt nghiệp), lựa chọn cường độ học tập trong học kỳ chính (từ mức Nhẹ nhàng đến Nặng), và thiết lập kế hoạch học hè. Hệ thống sẽ tự động tính toán số kỳ hè khả thi dựa trên thời gian tốt nghiệp dự kiến và quy định đào tạo tối đa 6 năm.

=== REVIEW QUESTIONS ===
1. Trong bước thiết lập mục tiêu, những chỉ số cơ bản nào mà sinh viên bắt buộc hoặc cần phải chọn?
2. Mức cường độ học tập ''Khá'' và ''Nặng'' trong học kỳ chính được quy định cụ thể là bao nhiêu tín chỉ?
3. Hệ thống dựa trên các yếu tố nào để tính toán ra số lượng kỳ hè khả thi (x) còn lại của sinh viên?
4. Sau khi đã chọn chuyên ngành, sinh viên có thể thay đổi lựa chọn này về sau không và điều đó ảnh hưởng thế nào đến lộ trình học tập?','e5a7f050-9519-41ad-9e3e-c5828d52d386'::uuid,NULL,NULL,55,508,'2026-03-21 13:35:13.32647+07'),
	 ('4f4c6053-85fc-4dab-9014-41f3f9f5198c'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,55,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
– Nếu sinh viên chọn sốkỳhè > 0, hệthống yêu cầu
chọn tiếp cường độhọc hè:
* Thấp (x ≤4 tín chỉ)
* Trung bình (4 < x ≤7 tín chỉ)
* Cao (7 < x < 11 tín chỉ)
D. Định hướng & Ưu tiên:
• Định hướng nghềnghiệp (tùy chọn).
• Sắp xếp thứtựưu tiên học tập:
1. Đạt GPA mong muốn.
2. Kiến thức chuyên sâu.
3. Ra trường đúng hạn.
• Chứng chỉbắt buộc theo chuyên ngành: đánh dấu “Đã đạt”
hoặc nhập kỳdựkiến đạt.
3. Đánh giá mức độkhảthi
Hệthống phân tích dựa trên dữliệu vừa nhập:
• Kiểm tra xem với sốtín chỉmỗi kỳ(chính + hè) đã
chọn, sinh viên có kịp hoàn thành tổng tín chỉtrước
thời gian tốt nghiệp dựkiến không.
• Hiển thịMức rủi ro (Thấp/Trung bình/Cao) và Gợi ý
điều chỉnh (VD: "Với mục tiêu ra trường năm 2026,
bạn cần tăng sốtín chỉmỗi kỳlên mức ’Khá’").
4. Xem lại & xác nhận
• Hệthống hiển thịbảng tóm tắt mục tiêu.
• Sinh viên chọn “Hoàn tất”.
• Hệthống lưu dữliệu và chuyển hướng vềDashboard.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 37/254

=== SUMMARY ===
Đoạn văn bản chi tiết các bước hoàn thiện thiết lập mục tiêu học tập (UC-02) bao gồm: lựa chọn cường độ học hè, xác định ưu tiên (GPA, kiến thức, tiến độ) và khai báo chứng chỉ chuyên ngành. Hệ thống thực hiện đánh giá tính khả thi của kế hoạch bằng cách phân tích số tín chỉ so với thời gian tốt nghiệp dự kiến, đưa ra cảnh báo mức độ rủi ro và gợi ý điều chỉnh. Quy trình kết thúc bằng việc xem lại tóm tắt và xác nhận lưu dữ liệu.

=== REVIEW QUESTIONS ===
1. Dựa vào số tín chỉ, hệ thống phân loại cường độ học kỳ hè thành những mức cụ thể nào?
2. Những yếu tố nào được đưa vào danh sách sắp xếp thứ tự ưu tiên học tập của sinh viên?
3. Hệ thống cung cấp những thông tin gì trong bước ''Đánh giá mức độ khả thi'' để hỗ trợ sinh viên?
4. Sau khi nhấn ''Hoàn tất'', dữ liệu sẽ được xử lý như thế nào và sinh viên được chuyển hướng đến đâu?','e2c2e692-8da3-476b-9c1d-8e7ee211bab0'::uuid,NULL,NULL,56,481,'2026-03-21 13:35:13.32647+07'),
	 ('5492aa44-f162-433b-93d2-c655f9df32d1'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,56,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Alternative Flow
• 1a. Sửdụng trợlý AI đểhỏi vềquy chếđào tạo.
• 2b. Thay đổi mốc tốt nghiệp: Nếu ởbước 2C (chọn kỳ
hè), sinh viên thấy sốkỳhè khảthi (X) quá ít, sinh viên
quay lại bước 2A đểchọn thời gian tốt nghiệp xa hơn →
Hệthống tựđộng cập nhật lại sốX lớn hơn.
Exceptions
• 2e1. Không đủthời gian: Nếu sinh viên chọn thời gian tốt
nghiệp quá gấp nhưng lại chọn mức tín chỉ"Nhẹnhàng"và
"Không học hè"→Hệthống cảnh báo ngay tại bước 3 là
"Không khảthi"(Rủi ro Cao) và chặn hoặc yêu cầu xác
nhận rủi ro.
• 2e2. GPA nhập vào không hợp lệ(ngoài 0.0–4.0).
• 5e1. Lỗi kết nối khi lưu dữliệu.
3.4.1.2
UC-03: Gợi ý lộtrình học cá nhân
Đặc tảUC-03: Gợi ý lộtrình học cá nhân được trình bày trong bảng 3.2.
Bảng 3.2: Đặc tảUC-03: Gợi ý lộtrình học cá nhân
Use-case Code
UC-03
Use-case Name
Gợi ý lộtrình học cá nhân
Description
Dựa trên mục tiêu học tập đã thiết lập (GPA mục tiêu, thời gian
tốt nghiệp dựkiến, sốtín chỉ/học kỳmong muốn) và chương
tình đào tạo, hệthống tựđộng sinh ra một lộtrình học cá nhân
hóa cho sinh viên. Lộtrình thểhiện danh sách các học kỳ, số
tín chỉmỗi kỳvà các môn dựkiến đăng ký, giúp sinh viên hình
dung chặng đường học tập của mình.
Actors
Sinh viên
Trigger
Sau khi hoàn tất UC-02 (Thiết lập mục tiêu học tập), sinh viên
chọn nút “Đềxuất lộtrình học tập”.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 38/254

=== SUMMARY ===
Đoạn văn bản trình bày các luồng xử lý thay thế và ngoại lệ trong quá trình thiết lập mục tiêu học tập, đồng thời bắt đầu đặc tả Use-case UC-03: Gợi ý lộ trình học cá nhân. Nội dung nhấn mạnh việc kiểm tra tính khả thi của lộ trình (dựa trên thời gian tốt nghiệp và cường độ học tập) và cách hệ thống tự động sinh ra kế hoạch học tập chi tiết dựa trên dữ liệu đầu vào của sinh viên để giúp họ đạt được mục tiêu đề ra.

=== REVIEW QUESTIONS ===
1. Trong trường hợp sinh viên chọn thời gian tốt nghiệp quá ngắn nhưng cường độ học tập thấp, hệ thống sẽ xử lý như thế nào?
2. Mục tiêu và vai trò của Use-case UC-03 trong hệ thống là gì?
3. Điều kiện cần thiết (Trigger) để sinh viên có thể bắt đầu sử dụng tính năng gợi ý lộ trình học cá nhân là gì?
4. Hệ thống yêu cầu GPA mục tiêu nhập vào phải nằm trong khoảng giá trị nào để được coi là hợp lệ?','9bacbe51-6dc3-421a-936a-7c581935204d'::uuid,NULL,NULL,57,580,'2026-03-21 13:35:13.327288+07'),
	 ('3d5ea64c-8e60-47c5-9dce-b0cfb605fcbd'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,147,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
6.1.2.2
Nội Dung Lớp Học - Ghi Chú và Chatbot
Phiên bản nâng cao của giao diện nội dung lớp học, tích hợp thêm tính năng ghi chú
cá nhân và chatbot. Giao diện này được minh hoạởhình 6.3.
Hình 6.3: Nội dung lớp học với tính năng Ghi chú và Chatbot
Đặc điểm chính:
• Sidebar chương/bài hiển thịtrạng thái hoàn thành, mở/khóa.
• Tab Ghi chú cung cấp rich-text (bold, italic, underline, link, list) và trường tiêu đề.
• Hỗtrợtải xuống ghi chú, phục vụlưu trữngoại tuyến.
• Chatbot giữgợi ý câu hỏi, cho phép hỏi nhanh trong khi ghi chú.
• Transcript bài học hiển thịđểtham chiếu khi soạn ghi chú.
6.1.3
Quiz và Bài Kiểm Tra
6.1.3.1
Quiz với Chatbot
Giao diện làm bài quiz với tích hợp chatbot hỗtrợtrong quá trình làm bài. Giao diện
này được minh hoạởhình 6.4.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 129/254

=== SUMMARY ===
Nội dung giới thiệu các tính năng nâng cao trong giao diện học tập và kiểm tra của sinh viên. Điểm nổi bật bao gồm hệ thống ghi chú cá nhân hỗ trợ định dạng phong phú (rich-text), khả năng tải ghi chú ngoại tuyến và chatbot hỗ trợ giải đáp nhanh. Bên cạnh đó, giao diện làm bài quiz cung cấp thông tin chi tiết về cấu hình bài thi và chatbot đồng hành, giúp sinh viên vừa học tập vừa ôn luyện kiến thức một cách hiệu quả và thuận tiện.

=== REVIEW QUESTIONS ===
1. Tính năng ghi chú rich-text hỗ trợ những định dạng văn bản cụ thể nào cho sinh viên?
2. Việc tích hợp transcript bài học vào giao diện ghi chú mang lại lợi ích gì?
3. Những thông tin cấu hình nào về bài quiz được hiển thị tập trung trong giao diện làm bài?
4. Chatbot được tích hợp trong giao diện học tập và quiz đóng vai trò gì trong việc hỗ trợ sinh viên?','b03c4f5a-2e0e-4c93-a411-ed6b7eec2769'::uuid,NULL,NULL,148,444,'2026-03-21 13:35:13.349004+07'),
	 ('fc70446a-2eb5-44af-b349-7e94e4fc4988'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,57,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Pre-Conditions
• Sinh viên đăng nhập hợp lệ; hồsơ sinh viên tồn tại.
• Sinh viên đã thiết lập mục tiêu học tập (UC-02) với tối
thiểu các thông tin: GPA mục tiêu, thời gian tốt nghiệp dự
kiến, sốtín chỉ/học kỳmong muốn.
• Dữliệu CTĐT đúng khóa tuyển sinh sẵn sàng: danh sách
môn, tổng tín chỉtốt nghiệp, các tiên quyết/cùng học, điều
kiện ra trường.
• Hệthống có dữliệu học tập hiện tại: GPA hiện tại, tín chỉ
đã tích lũy.
Post Conditions
• Một lộtrình học cá nhân được sinh ra và hiển thịtrên màn
hình Lộtrình học (theo từng học kỳ).
• Lộtrình được lưu lại đểsinh viên có thểxem lại trong các
lần truy cập sau.
• Lộtrình có thểđược xuất ra file (ví dụ: PDF/Excel).
Normal Flow
1. Mởtính năng
[Sinh viên] Chọn “Đềxuất lộtrình học tập” sau khi hoàn
tất UC-02.
[Hệthống] Kiểm tra sinh viên đã có mục tiêu học tập hợp
lệ.
2. Nạp mục tiêu và dữliệu chương trình
[Hệthống] Tải mục tiêu học tập hiện tại của sinh viên
(GPA mục tiêu, thời gian tốt nghiệp, sốtín chỉ/học kỳ
mong muốn) và CTĐT tương ứng (danh sách môn, sốtín
chỉ, tiên quyết, chuyên ngành đã chọn).
Đồng thời, hệthống lấy hiện trạng học tập: GPA hiện tại,
tín chỉtích lũy, các môn đã hoàn thành.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 39/254

=== SUMMARY ===
Phân đoạn này mô tả chi tiết điều kiện tiên quyết, kết quả đạt được và quy trình vận hành của tính năng gợi ý lộ trình học cá nhân (UC-03). Để hệ thống hoạt động, sinh viên cần hoàn tất thiết lập mục tiêu và có dữ liệu chương trình đào tạo tương ứng. Quy trình bao gồm việc nạp dữ liệu hiện tại, đối chiếu với mục tiêu để sinh ra một lộ trình học tập chi tiết theo từng học kỳ, cho phép sinh viên lưu trữ hoặc xuất file để theo dõi.

=== REVIEW QUESTIONS ===
1. Những thông tin tối thiểu nào sinh viên cần thiết lập ở bước UC-02 để hệ thống có thể gợi ý lộ trình?
2. Dữ liệu về chương trình đào tạo (CTĐT) cần bao gồm những yếu tố nào để đảm bảo tính chính xác của lộ trình?
3. Sau khi thực hiện xong tính năng, lộ trình học cá nhân có thể được lưu trữ dưới những định dạng nào?
4. Trong luồng xử lý thông thường, hệ thống thực hiện kiểm tra và nạp những dữ liệu hiện trạng nào của sinh viên?','5a5bd59c-fadd-46fd-a86e-8c131ffc9f8f'::uuid,NULL,NULL,58,560,'2026-03-21 13:35:13.327288+07'),
	 ('b9125cd1-ef21-49d4-90cd-dbd53464ef05'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,58,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
3. Sinh lộtrình học cá nhân
[Hệthống] Dựa trên:
• Các môn còn thiếu theo CTĐT.
• Các ràng buộc tiên quyết, song hành, khuyến nghị.
• Thời gian tốt nghiệp dựkiến và sốtín chỉ/học kỳmong
muốn.
Hệthống sắp xếp các môn còn lại vào từng học kỳtiếp
theo, đảm bảo:
• Không vi phạm tiên quyết cơ bản.
• Tổng tín chỉmỗi kỳgần với giá trịmục tiêu (có thểcó
sựkhác biệt trong vòng 2-3 tín chỉ).
• Tổng sốkỳhọc phải phù hợp với mốc tốt nghiệp dự
kiến.
4. Hiển thịlộtrình
[Hệthống] Hiển thịmàn hình “Lộtrình học cá nhân”, bao
gồm:
• Tổng tín chỉcòn lại.
• Sốhọc kỳdựkiến còn lại.
• Danh sách từng học kỳ: các môn được đềxuất, sốtín
chỉ/học kỳ.
• (Nếu có) các cảnh báo đơn giản: học kỳcó khối lượng
cao, môn đặt sát mốc tốt nghiệp, môn phải đạt đểkịp
tiên quyết kỳsau,...
[Sinh viên] Xem lộtrình đềxuất.
5. Lưu / xuất lộtrình
[Sinh viên] Chọn “Lưu lộtrình” hoặc “Xuất file” (nếu có).
[Hệthống] Lưu lộtrình gắn với sinh viên và xác nhận
thành công.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 40/254

=== SUMMARY ===
Đoạn văn bản mô tả quy trình hệ thống tự động tạo lộ trình học tập cá nhân cho sinh viên. Hệ thống dựa trên chương trình đào tạo, các điều kiện tiên quyết và mục tiêu cá nhân để sắp xếp môn học hợp lý theo từng kỳ. Kết quả bao gồm danh sách môn học đề xuất, tổng tín chỉ và các cảnh báo về tiến độ. Cuối cùng, sinh viên có thể xem, lưu trữ hoặc xuất lộ trình để hỗ trợ việc lập kế hoạch học tập.

=== REVIEW QUESTIONS ===
1. Hệ thống cần dựa vào những tiêu chí và ràng buộc nào để sắp xếp các môn học vào lộ trình cá nhân?
2. Màn hình hiển thị lộ trình học tập cá nhân bao gồm những thông tin chi tiết nào?
3. Những loại cảnh báo nào có thể xuất hiện trong lộ trình học tập được đề xuất?
4. Sau khi xem lộ trình, sinh viên có thể thực hiện những hành động gì để lưu trữ thông tin?','e24cdcbe-a4c0-4860-adab-217e8f042261'::uuid,NULL,NULL,59,477,'2026-03-21 13:35:13.327288+07'),
	 ('05dfc395-5f74-4bcf-a9df-72da7a770213'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,59,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Alternative Flow
• 1a. Chưa có mục tiêu học tập
[Hệthống] Phát hiện sinh viên chưa thiết lập mục tiêu
(UC-02)
→Hiển thịthông báo và gợi ý sinh viên sang “Thiết lập
mục tiêu học tập” (extend UC-02).
• 5a. Xem nhưng chưa lưu
[Sinh viên] Chỉxem lộtrình, không nhấn Lưu.
[Hệthống] Không cập nhật lộtrình, nhưng vẫn cho phép
quay lại màn hình gợi ý bất kỳlúc nào đểsinh lại theo mục
tiêu hiện tại.
Exceptions
• 3e1. Không tìm được lộtrình phù hợp
Trong một sốtrường hợp ràng buộc quá chặt (ví dụ: thời
gian tốt nghiệp quá sớm so với sốtín chỉcòn lại), hệthống
không thểsắp xếp môn vào các học kỳmà vẫn đảm bảo
tiên quyết.
→Hiển thịthông báo: “Không thểtạo lộtrình phù hợp
với mục tiêu hiện tại. Vui lòng điều chỉnh lại mục tiêu học
tập.” và gợi ý quay lại UC-02.
• 5e1. Lỗi lưu hoặc lỗi hệthống
Khi lưu hoặc xuất lộtrình gặp lỗi, hệthống hiển thịthông
báo: “Không thểlưu/xuất lộtrình, vui lòng thửlại sau.”
3.4.1.3
UC-50: Hỗtrợhọc tập từtrợlý ảo
Đặc tảUC-50: Hỗtrợhọc tập từtrợlý ảo được trình bày trong bảng 3.3.
Bảng 3.3: Đặc tảUC-50: Hỗtrợhọc tập từtrợlý ảo
Use-case Code
UC-50
Use-case Name
Hỗtrợhọc tập từtrợlý ảo
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 41/254

=== SUMMARY ===
Văn bản này chi tiết hóa các luồng thay thế và trường hợp ngoại lệ trong quy trình đề xuất lộ trình học tập cho sinh viên, bao gồm việc xử lý khi thiếu mục tiêu học tập hoặc không tìm được lộ trình khả thi do ràng buộc thời gian. Ngoài ra, tài liệu còn giới thiệu Use-case UC-50 về việc hỗ trợ học tập thông qua trợ lý ảo (AI), cung cấp mã và tên gọi chính thức của chức năng này trong hệ thống.

=== REVIEW QUESTIONS ===
1. Hệ thống sẽ phản hồi như thế nào nếu sinh viên cố gắng tạo lộ trình học tập khi chưa hoàn tất thiết lập mục tiêu (UC-02)?
2. Trong trường hợp sinh viên xem lộ trình nhưng không nhấn ''Lưu'', dữ liệu lộ trình sẽ được hệ thống xử lý ra sao?
3. Tại sao hệ thống có thể gặp lỗi ''Không tìm được lộ trình phù hợp'' và sinh viên cần làm gì để khắc phục?
4. Tên và mã của Use-case liên quan đến việc sử dụng trợ lý ảo để hỗ trợ học tập là gì?','ec1978d4-01e3-4585-9c60-f4be24377f33'::uuid,NULL,NULL,60,541,'2026-03-21 13:35:13.327288+07'),
	 ('1dd5ce12-baac-4e19-ba04-9437448b85e7'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,60,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Description
Sinh viên sửdụng trợlý ảo (AI-based) đểnhận hỗtrợhọc tập
cá nhân hóa, bao gồm giải đáp thắc mắc nhanh, gợi ý tài liệu bổ
sung, hướng dẫn ôn tập hoặc phân tích lỗi trong bài làm, nhằm
nâng cao hiệu quảhọc tập trong khóa học.
Actors
Sinh viên
Trigger
Sinh viên sửdụng bong bóng chatbot trên màn hình.
Pre-Conditions
• Sinh viên đã đăng nhập hệthống.
• Sinh viên đã đăng ký tham gia khóa học.
• Hệthống trợlý ảo (AI) đang hoạt động ổn định.
• Khóa học có dữliệu liên quan (tài liệu, bài tập) đểtrợlý
tham chiếu.
Post Conditions
• Sinh viên nhận được phản hồi hỗtrợtừtrợlý ảo.
• Lịch sửtương tác được lưu đểxem lại nếu cần.
Normal Flow
1. Sinh viên truy cập trang khóa học.
2. Chọn "Trợlý ảo"hoặc biểu tượng chat hỗtrợ.
3. Hệthống hiển thịgiao diện chat với trợlý.
4. Sinh viên nhập câu hỏi hoặc yêu cầu hỗtrợ(ví dụ: giải
thích khái niệm, gợi ý bài tập tương tự).
5. Hệthống AI xửlý yêu cầu dựa trên dữliệu khóa học và
kiến thức chung.
6. Hệthống hiển thịphản hồi (văn bản, liên kết tài liệu, ví dụ
minh họa).
7. Sinh viên có thểtiếp tục hỏi thêm hoặc kết thúc phiên.
8. Hệthống lưu lịch sửchat.
9. Hiển thịthông báo "Hỗtrợhoàn tất"nếu phiên kết thúc.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 42/254

=== SUMMARY ===
Đoạn văn bản mô tả chi tiết ca sử dụng (Use Case) UC-50 về việc hỗ trợ học tập thông qua trợ lý ảo AI cho sinh viên. Hệ thống cho phép sinh viên đặt câu hỏi, nhận giải đáp cá nhân hóa, gợi ý tài liệu và hướng dẫn ôn tập dựa trên dữ liệu khóa học. Quy trình bao gồm các bước từ truy cập, đặt yêu cầu đến việc AI xử lý thông tin và lưu lại lịch sử tương tác, giúp nâng cao hiệu quả học tập.

=== REVIEW QUESTIONS ===
1. Mục đích chính của việc triển khai trợ lý ảo (AI-based) cho sinh viên trong hệ thống này là gì?
2. Để trợ lý ảo có thể hoạt động và hỗ trợ chính xác, hệ thống cần những điều kiện tiên quyết (Pre-conditions) nào?
3. Hệ thống AI dựa trên những nguồn dữ liệu nào để xử lý và đưa ra phản hồi cho sinh viên?
4. Sau khi phiên hỗ trợ kết thúc, hệ thống thực hiện các bước hậu điều kiện (Post Conditions) gì để quản lý dữ liệu người dùng?','581bcebf-6086-462c-a36b-d0935206f7c8'::uuid,NULL,NULL,61,550,'2026-03-21 13:35:13.327288+07'),
	 ('017801f7-31b4-4ba7-84bb-68afbe97e52a'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,94,'=== ORIGINAL CONTENT ===
Trường Đại Học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa Học & KỹThuật Máy Tính
Chương 5
Cá nhân hóa học tập cho sinh viên
và kho dữliệu môn học cho
Coaching Chatbot
5.1
Mô hình dựđoán kết quảmôn học theo năng lực
Phần này trình bày cách nhóm xây dựng một mô hình dựđoán điểm tổng kết (thang
10) của sinh viên cho từng môn học [14], dựa trên các đặc trưng mô tả:
• năng lực và lịch sửhọc tập của sinh viên
• độkhó và mặt bằng điểm lịch sửcủa từng môn
• kết quảởcác môn liên quan (cùng nhóm kiến thức, môn tiên quyết/khuyến nghị)
Ý tưởng chính
Một đặc trưng quan trọng trong mô hình là course_hist_median_smooth – điểm
trung vịtừlịch sửđiểm sốcủa từng môn trong 3 năm gần nhất, được điều chỉnh sao cho:
với môn có ít dữliệu thì giá trịđược kéo gần vềtrung vịchung toàn hệthống, còn với
môn có nhiều dữliệu thì gần trùng với trung vịriêng của môn:
• Với mỗi môn, nhìn lại 3 năm gần nhất (dựa trên sem_key) và tính trung vịđiểm
các lượt học trước đó trong khoảng thời gian này.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 76/254

=== SUMMARY ===
Nội dung này trình bày phương pháp xây dựng mô hình dự đoán điểm tổng kết của sinh viên nhằm mục tiêu cá nhân hóa học tập. Mô hình dựa trên ba nhóm đặc trưng chính: năng lực cá nhân, độ khó của môn học và kết quả các môn liên quan. Một yếu tố quan trọng là chỉ số ''course_hist_median_smooth'' giúp tính toán điểm trung vị lịch sử trong 3 năm gần nhất, đồng thời áp dụng kỹ thuật làm mượt dữ liệu để cân bằng giữa dữ liệu riêng của môn và dữ liệu chung hệ thống.

=== REVIEW QUESTIONS ===
1. Mô hình dự đoán kết quả môn học trong văn bản sử dụng thang điểm bao nhiêu?
2. Ba nhóm đặc trưng chính được sử dụng để xây dựng mô hình dự đoán là gì?
3. Chỉ số ''course_hist_median_smooth'' được tính toán dựa trên dữ liệu lịch sử trong khoảng thời gian bao lâu?
4. Kỹ thuật điều chỉnh giá trị trung vị đối với môn học có ít dữ liệu lịch sử được thực hiện như thế nào?','17fca565-b070-40ee-b908-315eedbc420d'::uuid,NULL,NULL,95,486,'2026-03-21 13:35:13.333934+07');
INSERT INTO coaching_chatbot.lecture_knowledge_chunks (lecture_knowledge_chunks_id,lecture_knowledge_id,chunk_index,chunk_content,qdrant_point_id,start_time_seconds,end_time_seconds,page_number,token_count,created_at) VALUES
	 ('660c39e3-7807-4a30-bab6-508a043ab514'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,61,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Alternative Flow
4a. Sinh viên đính kèm file (bài làm, ảnh): Hệthống upload và
AI phân tích →quay lại bước 5.
5a. Nếu yêu cầu phức tạp: AI gợi ý các bước tựhọc hoặc liên
kết với diễn đàn →quay lại bước 6.
6a. Sinh viên chọn "Lưu phản hồi": Hệthống lưu riêng phản
hồi vào ghi chú cá nhân →quay lại bước 7.
Exceptions
• 3e. Trợlý không sẵn sàng (lỗi AI): Báo "Trợlý đang bảo
trì, vui lòng thửlại sau"→quay lại bước 2.
Các đặc tảkhác vềcác use-case liên quan đến sinh viên sẽđược trình bày trong phần
phụlục A.1.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 43/254

=== SUMMARY ===
Đoạn văn bản này chi tiết hóa các luồng thay thế và ngoại lệ cho Use Case trợ lý ảo hỗ trợ học tập (UC-50). Các tình huống bao gồm việc AI phân tích tài liệu đính kèm, cung cấp hướng dẫn tự học cho các yêu cầu phức tạp, và cho phép lưu phản hồi vào ghi chú cá nhân. Ngoài ra, tài liệu cũng nêu rõ quy trình xử lý lỗi khi hệ thống AI không sẵn sàng hoạt động.

=== REVIEW QUESTIONS ===
1. Hệ thống sẽ thực hiện quy trình gì khi sinh viên đính kèm file bài làm hoặc ảnh trong luồng 4a?
2. Trong trường hợp sinh viên đưa ra yêu cầu phức tạp, trợ lý ảo AI sẽ cung cấp những hỗ trợ gì thay thế?
3. Phản hồi của trợ lý ảo sẽ được lưu vào đâu nếu sinh viên chọn chức năng ''Lưu phản hồi''?
4. Thông báo cụ thể nào sẽ hiển thị khi hệ thống gặp lỗi AI hoặc trợ lý đang bảo trì?','d872455e-36c5-49a4-8ecf-71902c12aed5'::uuid,NULL,NULL,62,367,'2026-03-21 13:35:13.328292+07'),
	 ('0ae32bb9-c79f-4ba3-b780-60fa5d23f2b3'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,62,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
3.4.2
Lược đồuse-case đối với giảng viên
Sơ đồusecase đối với giảng viên được minh hoạởhình 3.2.
Hình 3.2: Sơ đồusecase đối với giảng viên
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 44/254

=== SUMMARY ===
Phần này giới thiệu lược đồ use-case dành cho giảng viên trong hệ thống quản lý học tập tại Trường Đại học Bách khoa. Nội dung tập trung vào việc xác định các tương tác của giảng viên thông qua Hình 3.2, làm tiền đề để mô tả chi tiết các chức năng như xây dựng cấu trúc khóa học và tạo bài tập có hỗ trợ bởi AI.

=== REVIEW QUESTIONS ===
1. Mục 3.4.2 tập trung mô tả lược đồ use-case dành cho đối tượng nào?
2. Hình 3.2 trong tài liệu đóng vai trò gì trong việc trình bày các chức năng của hệ thống?
3. Dựa vào thông tin trang 44, báo cáo này thuộc học kỳ và năm học nào?
4. Dựa vào ngữ cảnh các trang tiếp theo, hãy cho biết hai ví dụ về chức năng cụ thể của giảng viên được minh họa trong lược đồ này là gì?','46d9e516-11b1-469c-ab7b-417ee696a491'::uuid,NULL,NULL,63,260,'2026-03-21 13:35:13.328292+07'),
	 ('1208fd5f-df08-40d1-8f5a-f283fb0aef9d'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,63,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
3.4.2.1
UC-23: Xây dựng cấu trúc khóa học
Đặc tảUC-23: Xây dựng cấu trúc khóa học được trình bày trong bảng 3.4.
Bảng 3.4: Đặc tảUC-23: Xây dựng cấu trúc khóa học
Use-case Code
UC-23
Use-case Name
Xây dựng cấu trúc khóa học
Description
Giảng viên tổchức nội dung khóa học theo dạng cấu trúc gồm các
module, tuần hoặc chủđề. Mỗi module có thểchứa nhiều loại tài
nguyên (bài giảng, tài liệu, bài tập, quiz).
Actors
Giảng viên
Trigger
Giảng viên chọn chức năng "Xây dựng cấu trúc khóa học".
Pre-Conditions
• Giảng viên đã đăng nhập hệthống.
• Giảng viên có quyền quản lý khóa học.
• Khóa học đã tồn tại.
Post
Condi-
tions
• Cấu trúc khóa học được lưu thành công.
• Sinh viên có thểxem cấu trúc khi tham gia.
Normal Flow
1. Giảng viên truy cập khóa học cần chỉnh sửa.
2. Chọn "Xây dựng cấu trúc khóa học".
3. Hệthống hiển thịgiao diện quản lý module/chủđề.
4. Giảng viên thao tác: thêm, chỉnh sửa, xóa, sắp xếp module và
nội dung.
5. Giảng viên nhấn "Lưu".
6. Hệthống lưu dữliệu.
7. Thông báo "Cập nhật cấu trúc thành công".
Alternative
Flow
3a. Giảng viên chọn "Duplicate"từkhóa học khác   hệthống copy
cấu trúc   quay lại bước 4.
4a. Trước khi lưu, giảng viên chọn "Xem trước"  hệthống hiển
thịgiao diện preview   quay lại bước 5.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 45/254

=== SUMMARY ===
Đoạn văn bản này trình bày đặc tả Use-case UC-23 về việc xây dựng cấu trúc khóa học dành cho giảng viên. Giảng viên có thể tổ chức nội dung học tập theo module, tuần hoặc chủ đề, bao gồm nhiều loại tài nguyên như bài giảng và bài tập. Tài liệu mô tả chi tiết các điều kiện tiên quyết, luồng xử lý chuẩn (thêm, sửa, xóa, sắp xếp) và các luồng thay thế như sao chép cấu trúc từ khóa học khác hoặc xem trước giao diện trước khi lưu.

=== REVIEW QUESTIONS ===
1. Mục đích của Use-case UC-23 là gì và giảng viên có thể tổ chức nội dung theo những dạng cấu trúc nào?
2. Gi giảng viên cần thỏa mãn những điều kiện tiên quyết nào trước khi thực hiện xây dựng cấu trúc khóa học?
3. Trong luồng sự kiện chính (Normal Flow), hệ thống sẽ thực hiện bước gì sau khi giảng viên nhấn nút ''Lưu''?
4. Tính năng ''Duplicate'' trong phần Alternative Flow cho phép giảng viên thực hiện thao tác gì để tối ưu hóa việc tạo khóa học?','914f8599-4c46-4677-90b3-9f2be70bae1d'::uuid,NULL,NULL,64,582,'2026-03-21 13:35:13.328292+07'),
	 ('e5a4788d-4714-4358-9a30-d2f6d07fccb5'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,64,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Exceptions
• 4e. Nếu giảng viên chưa thêm module   Thông báo "Hãy
thêm bài giảng ngay"  quay lại bước 4.
• 6e. Hệthống không lưu được   báo "Không thểlưu"  quay
lại bước 5.
• 4e2. Nếu nhập trùng   hệthống yêu cầu đổi tên   quay lại
bước 4.
3.4.2.2
UC-26: Tạo bài tập
Đặc tảUC-26: Tạo bài tập được trình bày trong bảng 3.5.
Bảng 3.5: Đặc tảUC-26: Tạo bài tập
Use-case Code
UC-26
Use-case Name
Tạo bài tập
Description
Giảng viên tạo mới bài tập với nhiều hình thức (tựluận, trắc
nghiệm, bài lập trình, nhóm hoặc kết hợp). Bài tập có thểkèm
file hướng dẫn, tài liệu tham khảo và cấu hình chi tiết (deadline,
điểm tối đa, cho phép nộp lại. . . ). Ngoài ra, hệthống cung cấp tùy
chọn AI hỗtrợsinh tựđộng đềbài hoặc câu hỏi trắc nghiệm dựa
trên mô tả/nguồn tài liệu mà giảng viên cung cấp.
Actors
Giảng viên
Trigger
Giảng viên chọn chức năng "Tạo bài tập".
Pre-Conditions
• Giảng viên đã đăng nhập hệthống.
• Khóa học tồn tại và giảng viên có quyền chỉnh sửa.
• Thang điểm khóa học đã được thiết lập.
• Hệthống AI được tích hợp và hoạt động ổn định.
Post
Condi-
tions
• Bài tập mới được lưu vào hệthống và hiển thịcho sinh viên.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 46/254

=== SUMMARY ===
Đoạn văn bản trình bày chi tiết đặc tả Use-case UC-26: "Tạo bài tập" dành cho giảng viên và các trường hợp ngoại lệ liên quan. Giảng viên có thể thiết lập nhiều dạng bài tập (tự luận, trắc nghiệm, lập trình, nhóm) kèm theo các cấu hình như thời hạn và tài liệu tham khảo. Đặc biệt, hệ thống tích hợp AI để hỗ trợ tự động tạo đề bài từ nguồn tài liệu giảng viên cung cấp, giúp tối ưu hóa quy trình soạn thảo học liệu.

=== REVIEW QUESTIONS ===
1. Giảng viên có thể tạo những hình thức bài tập nào thông qua Use-case UC-26?
2. Để thực hiện chức năng tạo bài tập, giảng viên cần đáp ứng những điều kiện tiên quyết (Pre-conditions) nào?
3. Hệ thống AI hỗ trợ giảng viên như thế nào trong quá trình xây dựng nội dung bài tập?
4. Trong phần ngoại lệ (Exceptions), hệ thống sẽ xử lý như thế nào nếu giảng viên nhập tên module bị trùng?','fc3ccb61-3bca-471f-8757-2e70f97c9098'::uuid,NULL,NULL,65,536,'2026-03-21 13:35:13.329313+07'),
	 ('6d021431-9b4d-426b-938c-bf2576bbbe7f'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,95,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
• Giá trịtrung vịnày được làm mượt bằng cách kéo vềgần trung vịchung toàn hệ
thống nếu môn có ít dữliệu, và giữgần như nguyên nếu môn có nhiều dữliệu:
course_hist_median_smooth = medianpast ·N +medianglobal ·k
N +k
,
trong đó N là sốbản ghi lịch sửcủa môn trong 3 năm gần nhất, medianpast là trung
v vịriêng của môn, medianglobal là trung vịtoàn hệthống, k là tham sốlàm mượt
(chọn k = 10).
Nhờđó:
• Mô hình phân biệt được phần do độkhó của môn với phần do năng lực sinh viên.
• Có thểso sánh điểm của sinh viên ởcác môn khác nhau trên cùng một “mặt bằng
chuẩn”, không nhìn điểm tổng kết cuối nữa mà so sánh với mặt bằng của môn
• Xây dựng một baseline - quy tắc dựđoán đơn giản luôn cho điểm bằng điểm trung
vịlịch sửcủa môn học làm mốc so sánh, từđó đánh giá mức độcải thiện của mô
hình Ridge.
ˆybaseline = course_hist_median_smooth.
5.1.1
Chuẩn bịdữliệu
Quy trình chuẩn bịdữliệu đểxây dựng bảng trained_dataset gồm 3 bước chính:
1. Trích xuất và chuẩn hoá dữliệu điểm từcơ sởdữliệu.
2. Xây dựng các nhóm đặc trưng (feature) ởcấp mỗi lần học một môn, mỗi học kỳcủa
sinh viên, mỗi môn học, và các môn liên quan.
3. Xửlý giá trịkhuyết (imputation) đểtạo ra một tập dữliệu huấn luyện hoàn chỉnh.
Nguồn dữliệu và thời gian học kỳ
Dữliệu ban đầu được trích xuất từba bảng chính:
• grade_record: chứa student_id, course_id, semester_id, final_score,
letter_grade, updated_at
• subject: chứa credits, subject_category
• student: chứa intake_year_id
Chỉgiữlại các bản ghi có đủbốn trường quan trọng: final_score, student_id,
course_id, semester_id (không được NULL).
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 77/254

=== SUMMARY ===
Đoạn văn bản giải thích phương pháp làm mượt điểm trung vị lịch sử (course_hist_median_smooth) để xử lý các môn học thiếu dữ liệu, giúp phân tách độ khó môn học và năng lực sinh viên. Giá trị này cũng đóng vai trò làm điểm chuẩn (baseline) để đánh giá mô hình. Ngoài ra, nội dung còn mô tả quy trình chuẩn bị dữ liệu gồm ba bước chính: trích xuất chuẩn hóa, xây dựng đặc trưng đa cấp và xử lý giá trị khuyết từ các nguồn dữ liệu gốc như bảng điểm, môn học và thông tin sinh viên.

=== REVIEW QUESTIONS ===
1. Công thức tính course_hist_median_smooth có tác dụng gì đối với các môn học có ít dữ liệu lịch sử?
2. Tham số k trong công thức làm mượt được quy định giá trị bằng bao nhiêu và ý nghĩa của nó là gì?
3. Quy trình chuẩn bị dữ liệu để xây dựng bảng dữ liệu huấn luyện (trained_dataset) bao gồm những bước chính nào?
4. Những trường dữ liệu quan trọng nào bắt buộc không được để trống (NULL) khi trích xuất từ các bảng ban đầu?','641ab669-c1eb-4dad-a724-c7b3cc2336b9'::uuid,NULL,NULL,96,672,'2026-03-21 13:35:13.334961+07'),
	 ('f11a7379-9bed-4466-acc0-2ad147072eb8'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,96,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Từmã học kỳsemester_id (ví dụHK231), dữliệu được tách thành:
• start_year: năm học bắt đầu (ví dụ2023)
• semester_no: thứtựhọc kỳtrong năm (1, 2, 3)
• sem_key = start_year ×10 + semester_no, dùng làm trục thời gian đểsắp xếp
và tính các đặc trưng lịch sử
Sau đó, dữliệu được sắp xếp theo (student_id, sem_key, updated_at, course_id)
đểđảm bảo thứtựthời gian ổn định.
Đặc trưng cơ bản cho từng lần sinh viên học một môn
Mỗi dòng dữliệu tương ứng với một lần sinh viên học một môn trong một học kỳ.
Trên mỗi dòng này, các trường cơ bản được bổsung gồm:
• course_grade: điểm tổng kết môn, lấy từfinal_score
• credits: sốtín chỉcủa môn (thiếu thì gán 0)
• weighted_score = course_grade × credits
• is_fail: cờrớt môn (True nếu course_grade < 4.0)
• fail_credits: sốtín chỉbịrớt trong lần học đó (0 nếu đậu)
• retake_no: sốlần học lại môn (0 = lần đầu, 1 = lần hai, . . . )
• các cờđiểm chữis_A_plus, is_A, . . . , is_D dựa trên các ngưỡng điểm (ví dụA+
từ9.5 trởlên). Những cờnày là nền đểvềsau thống kê phân bốđiểm của sinh viên
và của từng môn
Đặc trưng lịch sửtheo học kỳcủa sinh viên
Tiếp theo, dữliệu được tổng hợp theo cặp (student_id, semester_id), và chỉsử
dụng các học kỳtrước thời điểm dựđoán đểxây dựng các đặc trưng lịch sử:
• num_semesters_prior: sốhọc kỳsinh viên đã hoàn thành trước kỳhiện tại
• sem_grade_avg: điểm trung bình trong học kỳ
• cumulative_grade_avg: điểm trung bình tích lũy đến hết kỳtrước
• previous_sem_grade_avg: điểm trung bình của học kỳliền trước
• grade_trend: so sánh kỳgần nhất với trung bình các kỳtrước (dương nghĩa là
đang lên, âm nghĩa là đang đi xuống)
• historic_fail_ratio: tỉlệtín chỉbịrớt trong toàn bộcác học kỳtrước
• grade_consistency: độổn định điểm qua các môn đã học - độlệch chuẩn của
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 78/254

=== SUMMARY ===
Đoạn văn bản mô tả quy trình trích xuất và xử lý đặc trưng dữ liệu để dự đoán kết quả học tập. Nội dung bao gồm cách chuẩn hóa mã học kỳ thành trục thời gian (sem_key), xây dựng các đặc trưng cơ bản cho mỗi lần học (điểm số, tín chỉ, trạng thái rớt môn, số lần học lại) và tổng hợp các đặc trưng lịch sử của sinh viên (điểm trung bình tích lũy, xu hướng điểm số, tỉ lệ rớt môn và độ ổn định). Các thuộc tính này giúp mô hình nắm bắt lộ trình học tập theo thời gian của sinh viên.

=== REVIEW QUESTIONS ===
1. Công thức tính sem_key là gì và nó đóng vai trò gì trong việc xử lý dữ liệu?
2. Các đặc trưng cơ bản nào được trích xuất cho mỗi lần sinh viên đăng ký học một môn cụ thể?
3. Đặc trưng ''grade_trend'' được tính toán như thế nào và nó phản ánh điều gì về kết quả học tập của sinh viên?
4. Tại sao việc sắp xếp dữ liệu theo thứ tự (student_id, sem_key, updated_at, course_id) lại quan trọng đối với mô hình dự đoán?','47cdc01d-9a0c-40d1-9635-ebd8b7cea6ad'::uuid,NULL,NULL,97,712,'2026-03-21 13:35:13.334961+07'),
	 ('fa31770f-070b-440c-a043-28b0e0befe16'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,97,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
điểm:
grade_consistency =
q
E[X2]−(E[X])2,
trong đó X là điểm các môn đã học trước đó của sinh viên, E[X] là điểm trung bình
và E[X2] là trung bình của bình phương điểm.
• student_x_grade_rate với x ∈{A+,A,B+,B,C+,C,D+,D}: tỉlệcác môn trước
đây mà sinh viên đạt ít nhất mức điểm tương ứng
• sem_rank_percentile: phần trăm xếp hạng điểm trung bình học kỳso với các
bạn cùng khóa, cùng kỳ
• gpa_rank_percentile: phần trăm xếp hạng GPA tích lũy so với các bạn cùng
khóa
• sem_credits, sem_credits_squared: tổng sốtín chỉđăng ký trong kỳvà bình
phương của giá trịđó. Việc đưa thêm sem_credits_squared giúp mô hình tuyến
tính vẫn nắm được hiệu ứng phi tuyến, ví dụkhi sốtín chỉquá nhiều thì mức độ
“quá tải” tăng nhanh hơn tuyến tính và có thểlàm điểm giảm mạnh hơn
Nhờnhóm đặc trưng này, mô hình “nhìn thấy” và sửdụng quá trình học tập của sinh
viên theo thời gian mà không dùng dữliệu của tương lai.
Đặc trưng lịch sửvà độkhó của môn học
Với mỗi course_id, hệthống nhìn lại ba năm gần nhất trên trục sem_key (chỉlấy
các học kỳcó sem_key nhỏhơn hiện tại) đểtính các đặc trưng mô tảmặt bằng điểm và
độkhó của môn:
• course_hist_count: sốlượt sinh viên đã học môn đó trong 3 năm gần nhất
• course_fail_rate: tỉlệrớt môn trong giai đoạn đó
• Các tỉlệ, ví dụcourse_a_plus_grade_rate, . . . , course_d_grade_rate: cho
biết trong 3 năm gần nhất, bao nhiêu phần trăm lượt sinh viên đạt ít nhất mức điểm
A+, A, B+, B, C+, C, D+, D ởmôn đó.
• rank_course_difficulty: xếp hạng tỉlệrớt môn so với các môn học khác.
• course_hist_median_smooth: trung vịđiểm lịch sửcủa môn trong 3 năm gần
nhất, đã được làm mượt vềtrung vịchung của toàn hệthống đểtránh bịméo khi số
mẫu quá ít
• course_hist_missing: cho biết môn có đủlịch sửtrong 3 năm gần nhất hay
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 79/254

=== SUMMARY ===
Đoạn văn bản trình bày cách xây dựng các nhóm đặc trưng cho mô hình dự đoán kết quả học tập. Các đặc trưng bao gồm: chỉ số cá nhân của sinh viên (độ ổn định điểm số, tỉ lệ điểm chữ, xếp hạng GPA, số tín chỉ đăng ký) và đặc trưng về môn học (lịch sử 3 năm gần nhất, tỉ lệ rớt, trung vị điểm đã làm mượt). Việc sử dụng bình phương số tín chỉ giúp mô hình nhận diện các hiệu ứng phi tuyến tính khi sinh viên bị quá tải học tập.

=== REVIEW QUESTIONS ===
1. Công thức tính grade_consistency (độ ổn định điểm) được xác định dựa trên những yếu tố nào của sinh viên?
2. Tại sao việc đưa biến bình phương số tín chỉ (sem_credits_squared) vào mô hình lại giúp nhận diện hiện tượng ''quá tải'' học tập?
3. Nhóm đặc trưng lịch sử môn học được hệ thống phân tích dựa trên khoảng thời gian bao lâu trước thời điểm dự đoán?
4. Biến course_hist_median_smooth có vai trò gì và tại sao nó cần được làm mượt về trung vị chung của hệ thống?','a661f547-f723-4a7c-8abd-615dd9b2dadb'::uuid,NULL,NULL,98,712,'2026-03-21 13:35:13.334961+07'),
	 ('f662fa4d-6dc7-4b14-ae34-b685f4cd7f41'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,98,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
không.
Đặc trưng các môn liên quan
Đểtận dụng thông tin “học tốt môn nền tảng thì thường học tốt môn nâng cao”, mỗi
môn được gắn với một tập môn liên quan R gồm:
• các môn cùng subject_category
• các môn tiên quyết hoặc khuyến nghị(Prerequisite, Recommended) trong chương
trình đào tạo.
Chỉxét các môn trong R mà sinh viên đã học ởcác kỳtrước đểtạo ra:
• has_relative_course: sinh viên đã từng học ít nhất một môn liên quan hay chưa
• relative_avg_course_grade: điểm trung bình (theo tín chỉ) của sinh viên trên
các môn liên quan đã học
• relative_avg_course_grade_rank_percentile: trung bình phần trăm xếp hạng
của sinh viên trong các lớp (mỗi lớp là một cặp course_id, semester_id) chứa
các môn liên quan đó giá trịcàng nhỏthì vịtrí càng gần top.
Nhóm đặc trưng này giúp mô hình nắm được xem sinh viên đang đứng ởmức nào so
với các bạn trong những môn nền tảng có liên quan.
Xửlý giá trịkhuyết (Imputation)
Dữliệu điểm thật ngoài đời thường không đầy đủ: có sinh viên mới vào chưa có lịch
sử, có môn học mới mởchưa có nhiều người học, có học kỳbịthiếu một vài chỉsốtổng
hợp, . . . Nếu đểtrống, mô hình không học được nếu điền bừa, mô hình sẽbịsai lệch.
Đểxửlý vấn đềnày, các giá trịkhuyết không được điền bằng một con sốchung cho
toàn bộdữliệu, mà luôn cốgắng tận dụng:
• Lịch sửhọc tập theo thời gian của từng sinh viên
• Lịch sửgiảng dạy của từng môn học trong khoảng 3 năm gần nhất
Nhờđó, các giá trịbổsung (impute) “hợp lý” hơn và giữđược cấu trúc chuỗi thời gian
của dữliệu giáo dục [14].
Nhóm đặc trưng phía sinh viên (Student Features)
Nhóm này mô tảquá trình học của từng sinh viên qua các học kỳ.
• Xác định sinh viên đã có lịch sửhay chưa Biến num_semesters_prior cho biết
sinh viên đã hoàn thành bao nhiêu học kỳtrước đó. Nếu thiếu, biến này được gán 0.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 80/254

=== SUMMARY ===
Đoạn trích trình bày phương pháp xây dựng các nhóm đặc trưng quan trọng cho mô hình dự đoán kết quả học tập. Nội dung bao gồm việc khai thác thông tin từ các môn học liên quan (tiên quyết, cùng nhóm ngành), kỹ thuật xử lý dữ liệu khuyết (imputation) dựa trên lịch sử 3 năm gần nhất để đảm bảo tính hợp lý theo thời gian, và cách xác định lịch sử học tập của sinh viên qua số học kỳ đã hoàn thành. Các đặc trưng này giúp mô hình đánh giá năng lực sinh viên chính xác hơn.

=== REVIEW QUESTIONS ===
1. Nhóm đặc trưng ''môn liên quan'' dựa trên những tiêu chí nào để xác định tập môn R?
2. Ý nghĩa của biến ''relative_avg_course_grade_rank_percentile'' trong việc đánh giá năng lực sinh viên là gì?
3. Tại sao việc xử lý giá trị khuyết lại cần dựa trên lịch sử học tập và giảng dạy trong 3 năm gần nhất thay vì dùng một con số chung?
4. Biến ''num_semesters_prior'' đóng vai trò gì trong việc xác định hồ sơ dữ liệu của một sinh viên?','56169968-c5f2-4edf-8133-ab17585692be'::uuid,NULL,NULL,99,721,'2026-03-21 13:35:13.335961+07'),
	 ('c1627066-197f-4598-96a6-c04f0c06c422'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,65,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Normal Flow
1. Giảng viên vào khóa học   chọn "Bài tập".
2. Nhấn "Tạo bài tập mới".
3. Hệthống hiển thịform nhập thông tin.
4. Giảng viên nhập: Tiêu đề, mô tả; Hình thức: tựluận, trắc
nghiệm, lập trình, nhóm, kết hợp; Deadline, điểm tối đa, tùy
chọn cho phép nộp lại.
5. (Tùy chọn) Upload file hướng dẫn/tài liệu kèm theo.
6. (Nếu chọn trắc nghiệm)   hệthống cho phép: Nhập câu hỏi
trực tiếp; Hoặc Import từfile Excel theo mẫu; Hoặc Sinh tự
động câu hỏi bằng AI từtài liệu/đềcương do giảng viên nhập;
Phân loại câu hỏi theo mức độ, chủđề.
7. Giảng viên chọn "Xem trước giao diện làm bài"từgóc nhìn
sinh viên.
8. Nhấn "Lưu và phát hành".
9. Hệthống kiểm tra dữliệu hợp lệ.
10. Lưu bài tập vào database.
11. Thông báo "Tạo bài tập thành công".
Alternative
Flow
5a. Upload nhiều file hướng dẫn: Giảng viên chọn nhiều file   hệ
thống lưu tất cả  quay lại bước 6.
6a. Import Excel không đủcột: Hệthống báo thiếu dữliệu, cho
phép giảng viên sửa trực tiếp trên giao diện   quay lại bước 6.
6b. Sinh tựđộng từAI: 1. Giảng viên chọn "Sinh đềbằng AI". 2.
Nhập mô tảyêu cầu. 3. Hệthống AI xửlý và trảvềbộcâu hỏi
nháp. 4. Giảng viên xem trước, chỉnh sửa, loại bỏhoặc thêm câu
hỏi. 5. Nhấn "Chấp nhận"  quay lại bước 7.
7a. Preview có lỗi định dạng: Hệthống cảnh báo, giảng viên chỉnh
sửa trước khi quay lại bước 8.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 47/254

=== SUMMARY ===
Đoạn văn bản mô tả chi tiết quy trình nghiệp vụ của chức năng ''Tạo bài tập'' (UC-26) trong hệ thống quản lý học tập. Giảng viên có thể thiết lập nhiều loại bài tập khác nhau (tự luận, trắc nghiệm, lập trình, nhóm), đính kèm tài liệu và cấu hình các thông số như deadline hay điểm số. Hệ thống hỗ trợ các tính năng hiện đại như nhập câu hỏi từ Excel hoặc sử dụng AI để tự động sinh đề bài, đồng thời cho phép xem trước giao diện trước khi lưu chính thức.

=== REVIEW QUESTIONS ===
1. Giảng viên có thể thiết lập những hình thức bài tập nào cho sinh viên theo quy trình trên?
2. Đối với bài tập trắc nghiệm, hệ thống cung cấp những phương thức nào để đưa câu hỏi vào đề bài?
3. Quy trình sử dụng công nghệ AI để hỗ trợ giảng viên tạo đề bài diễn ra qua các bước nào?
4. Hệ thống sẽ xử lý như thế nào trong trường hợp file Excel nhập câu hỏi bị thiếu dữ liệu cột?','95c8d273-5d1d-40c4-9fc8-5566f355b170'::uuid,NULL,NULL,66,589,'2026-03-21 13:35:13.329313+07'),
	 ('f50bf4bd-ebe8-4154-a3a8-8b86c6da7de3'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,66,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Exceptions
• 4e. Nhập ngày < hiện tại   báo lỗi   quay lại bước 4.
• 5e. File sai định dạng/dung lượng: Báo lỗi   quay lại bước 5.
• 6e. File Excel sai định dạng: Không import được   báo lỗi
  quay lại bước 6.
• 9e. Dữliệu thiếu (tiêu đề/điểm): Báo lỗi   quay lại bước 4.
• 10e. Lỗi database/hệthống: Báo "Không thểtạo bài tập"
quay lại bước 8.
3.4.2.3
UC-28: Chấm điểm và nhận xét
Đặc tảUC-28: Chấm điểm và nhận xét được trình bày trong bảng 3.6.
Bảng 3.6: Đặc tảUC-28: Chấm điểm và nhận xét
Use-case Code
UC-28
Use-case Name
Chấm điểm và nhận xét
Description
Giảng viên chấm điểm các bài nộp. Với trắc nghiệm, hệthống
chấm tựđộng. Với tựluận, lập trình, nhóm, giảng viên có thểchấm
thủcông hoặc sửdụng AI hỗtrợđểsinh điểm gợi ý, nhận xét sơ
bộvà đềxuất rubric phù hợp. Giảng viên có toàn quyền chỉnh sửa
điểm cuối cùng và feedback trước khi lưu.
Actors
Giảng viên
Trigger
Giảng viên chọn "Chấm điểm"trong phần bài tập.
Pre-Conditions
• Giảng viên đã đăng nhập hệthống.
• Bài tập đã tồn tại.
• Sinh viên đã nộp bài.
• Thang điểm khóa học đã được thiết lập.
• Hệthống AI đang hoạt động ổn định.
Post
Condi-
tions
• Điểm và phản hồi của bài nộp được lưu.
• Sinh viên có thểxem kết quảvà feedback.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 48/254

=== SUMMARY ===
Đoạn văn bản mô tả các trường hợp ngoại lệ khi tạo bài tập (UC-26) và đặc tả chi tiết quy trình chấm điểm và nhận xét (UC-28). Trong UC-28, giảng viên quản lý việc chấm điểm cho nhiều loại bài tập khác nhau. Hệ thống tự động chấm trắc nghiệm, trong khi các bài tự luận, lập trình hoặc nhóm có thể được hỗ trợ bởi AI để sinh điểm gợi ý và nhận xét. Giảng viên giữ quyền quyết định cuối cùng đối với điểm số và phản hồi trước khi công bố cho sinh viên.

=== REVIEW QUESTIONS ===
1. Những lỗi dữ liệu nào về file và thời gian có thể ngăn cản việc tạo bài tập thành công?
2. Hệ thống hỗ trợ giảng viên như thế nào trong việc chấm điểm các bài tập tự luận và lập trình?
3. Giảng viên có vai trò gì đối với các gợi ý chấm điểm và nhận xét do AI tạo ra?
4. Để thực hiện được use case UC-28, hệ thống cần đảm bảo những điều kiện tiên quyết nào về mặt dữ liệu và kỹ thuật?','68a7f1e4-2c2f-4ef2-b666-70b4a6110f00'::uuid,NULL,NULL,67,563,'2026-03-21 13:35:13.329313+07');
INSERT INTO coaching_chatbot.lecture_knowledge_chunks (lecture_knowledge_chunks_id,lecture_knowledge_id,chunk_index,chunk_content,qdrant_point_id,start_time_seconds,end_time_seconds,page_number,token_count,created_at) VALUES
	 ('d8d0cb3d-2167-472d-922a-530801f5d1af'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,67,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Normal Flow
1. Giảng viên mởdanh sách bài tập.
2. Chọn một bài tập   xem danh sách sinh viên đã nộp.
3. Chọn một bài nộp.
4. Hệthống hiển thịnội dung bài làm.
5. Giảng viên chấm điểm: Nếu tựluận/lập trình/nhóm: nhập
điểm thủcông hoặc bật AI hỗtrợ; Nếu trắc nghiệm: hệthống
hiển thịđiểm tựđộng, giảng viên có thểchỉnh sửa.
6. Giảng viên điều chỉnh, bổsung nhận xét chi tiết (nếu cần).
7. Nhấn "Lưu".
8. Hệthống kiểm tra tính hợp lệcủa điểm.
9. Lưu điểm và nhận xét vào database.
10. Hiển thịthông báo thành công.
Alternative
Flow
2a. Lọc bài chưa chấm: Giảng viên chọn filter   hệthống hiển thị
danh sách rút gọn.
Exceptions
• 3e. Bài nộp không tồn tại: Báo lỗi   quay lại bước 2.
• 6e. AI không thểchấm: (do lỗi model, input không rõ ràng,
timeout)   báo "Không thểsửdụng AI đểchấm điểm, vui
lòng chấm thủcông"  quay lại bước 5.
• 7e. Điểm ngoài phạm vi: Báo lỗi   quay lại bước 7.
• 9e. Dữliệu không hợp lệ: Báo lỗi   quay lại bước 7.
• 10e. Lỗi: Không thểlưu   quay lại bước 8.
Các đặc tảkhác vềcác use-case liên quan đến giảng viên sẽđược trình bày trong phần
phụlục A.2.
3.4.3
Lược đồuse-case đối với admin
Sơ đồusecase đối với admin được minh hoạởhình 3.3.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 49/254

=== SUMMARY ===
Đoạn văn bản chi tiết quy trình nghiệp vụ của chức năng chấm điểm và nhận xét (UC-28) dành cho giảng viên. Quy trình bao gồm các bước từ việc xem danh sách bài nộp, thực hiện chấm điểm (thủ công, tự động hoặc hỗ trợ bởi AI), đến việc lưu kết quả vào hệ thống. Nội dung cũng xác định các tình huống ngoại lệ như lỗi AI hoặc sai lệch điểm số, đồng thời giới thiệu sự chuyển tiếp sang phần lược đồ use-case dành cho quản trị viên (Admin).

=== REVIEW QUESTIONS ===
1. Giảng viên có thể thực hiện chấm điểm bài tập tự luận và lập trình bằng những phương thức nào?
2. Hệ thống sẽ đưa ra thông báo và yêu cầu gì nếu tính năng AI gặp lỗi trong quá trình chấm điểm?
3. Điều kiện nào được hệ thống kiểm tra tại bước 8 trước khi lưu điểm vào cơ sở dữ liệu?
4. Chức năng ''Lọc bài chưa chấm'' (2a) đóng vai trò gì trong việc hỗ trợ giảng viên?','6f94f0b4-4903-45e5-8a23-9147a6353630'::uuid,NULL,NULL,68,546,'2026-03-21 13:35:13.329313+07'),
	 ('9eabe403-9df8-47ae-afaa-10e4c489b325'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,68,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Hình 3.3: Sơ đồusecase đối với admin
3.4.3.1
UC-36: Xem báo cáo
Đặc tảUC-36: Xem báo cáo được trình bày trong bảng 3.7.
Bảng 3.7: Đặc tảUC-36: Xem báo cáo
Use-case Code
UC-36
Use-case Name
Xem báo cáo
Description
Admin xem báo cáo học tập và hoạt động của toàn hệthống: sốbài
chưa chấm, tỷlệhoàn thành, sốlượng người dùng, tần suất đăng
nhập, điểm trung bình lớp, tỷlệđạt/rớt theo môn.
Actors
Admin
Trigger
Admin chọn chức năng "Báo cáo"từdashboard chính.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 50/254

=== SUMMARY ===
Đoạn văn bản cung cấp chi tiết đặc tả use-case UC-36 "Xem báo cáo" dành cho quản trị viên (Admin). Chức năng này cho phép Admin theo dõi toàn diện các hoạt động học tập và tương tác trên hệ thống, từ thống kê số lượng người dùng, tần suất đăng nhập đến các chỉ số học thuật như tỷ lệ hoàn thành bài tập, điểm trung bình và tỷ lệ đạt/rớt. Đây là công cụ hỗ trợ Admin quản lý và đánh giá hiệu quả vận hành của hệ thống một cách hiệu quả.

=== REVIEW QUESTIONS ===
1. Mã use-case và tên gọi chính xác của chức năng được mô tả trong tài liệu là gì?
2. Tác nhân (Actor) nào có quyền thực hiện chức năng xem báo cáo hệ thống?
3. Hãy liệt kê ít nhất 4 loại dữ liệu mà Admin có thể theo dõi thông qua báo cáo học tập và hoạt động.
4. Hành động cụ thể nào của Admin sẽ kích hoạt (Trigger) use-case xem báo cáo này?','32673b87-6824-4651-b439-63cbeceb0517'::uuid,NULL,NULL,69,364,'2026-03-21 13:35:13.329313+07'),
	 ('43567548-de9c-440a-a8dc-fcf9ed32c93f'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,69,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Pre-Conditions
• Admin đã đăng nhập hệthống.
• Hệthống đã thu thập đủdữliệu vềhoạt động học tập và người
dùng.
Post
Condi-
tions
• Báo cáo được tạo và hiển thịtheo các tham sốAdmin đã chọn.
• Admin có thểtrích xuất dữliệu báo cáo ra các định dạng file
phổbiến (PDF, CSV).
Normal Flow
1. Admin truy cập trang "Báo cáo".
2. Hệthống hiển thịmột dashboard tổng quan với các sốliệu
chính (KPIs) và danh sách các loại báo cáo có sẵn (ví dụ: Báo
cáo người dùng, Báo cáo khóa học, Báo cáo học tập).
3. Admin chọn một loại báo cáo, ví dụ"Báo cáo học tập".
4. Hệthống hiển thịcác báo cáo con, ví dụ:
• "Thống kê tỷlệhoàn thành bài tập"(extend UC-37)
• "Phân tích điểm sốtrung bình theo khóa học"
5. Admin chọn một báo cáo cụthể. Hệthống hiển thịcác bộlọc
(ví dụ: khoảng thời gian, khóa học, giảng viên).
6. Admin thiết lập bộlọc và nhấn "Xem báo cáo".
7. Hệthống truy vấn, tổng hợp dữliệu và trình bày kết quảdưới
dạng biểu đồ(tròn, cột) và bảng sốliệu chi tiết.
Alternative
Flow
7a. Xuất báo cáo: Sau khi báo cáo được hiển thị, Admin có thể
nhấn nút "Xuất file"và chọn định dạng (PDF hoặc CSV) đểtải về.
7b. Lưu bộlọc: Admin có thểlưu lại một bộlọc thường dùng (ví
dụ: "Báo cáo hoạt động tháng này") đểtruy cập nhanh trong những
lần sau.
Exceptions
• 3e. Không có dữliệu: Nếu không có dữliệu phù hợp với bộ
lọc được chọn, hệthống sẽhiển thịthông báo "Không tìm
thấy dữliệu cho tiêu chí này."
• 7e. Lỗi tạo báo cáo: Nếu truy vấn dữliệu quá lớn và gây ra
lỗi timeout, hệthống sẽbáo "Yêu cầu xửlý quá lâu. Vui lòng
thu hẹp phạm vi bộlọc và thửlại."
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 51/254

=== SUMMARY ===
Phân đoạn này mô tả chi tiết quy trình thực hiện use case ''Xem báo cáo'' (UC-36) dành cho quản trị viên (Admin). Tài liệu bao gồm các điều kiện tiên quyết, luồng sự kiện chuẩn từ việc truy cập dashboard đến hiển thị kết quả dưới dạng biểu đồ/bảng, cùng các tính năng mở rộng như xuất file PDF/CSV và lưu bộ lọc tìm kiếm. Ngoài ra, nội dung còn hướng dẫn cách hệ thống xử lý các trường hợp ngoại lệ như thiếu dữ liệu hoặc lỗi kết nối do truy vấn quá tải.

=== REVIEW QUESTIONS ===
1. Admin cần thỏa mãn những điều kiện tiên quyết nào trước khi có thể thực hiện xem báo cáo?
2. Hệ thống hỗ trợ Admin xuất dữ liệu báo cáo ra những định dạng tệp tin cụ thể nào?
3. Trong quy trình chuẩn, sau khi Admin thiết lập bộ lọc và nhấn ''Xem báo cáo'', hệ thống sẽ thực hiện các thao tác gì để hiển thị kết quả?
4. Hệ thống sẽ xử lý như thế nào và đưa ra lời khuyên gì khi gặp lỗi timeout do truy vấn dữ liệu quá lớn?','ef6caa23-541e-41e1-ae34-c086aaad0215'::uuid,NULL,NULL,70,658,'2026-03-21 13:35:13.330313+07'),
	 ('59f7ad19-5bca-4a4f-a740-706842843828'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,70,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
3.4.3.2
UC-43: Cấu hình truy cập theo vai trò
Đặc tảUC-43: Cấu hình truy cập theo vai trò được trình bày trong bảng 3.8.
Bảng 3.8: Đặc tảUC-43: Cấu hình truy cập theo vai trò
Use-case Code
UC-43
Use-case Name
Cấu hình truy cập theo vai trò
Description
Cho phép Admin có thểđịnh nghĩa chi tiết các quyền hạn cho từng
vai trò, sao chép cấu hình giữa các vai trò, và quản lý vòng đời của
chúng. Chức năng này đảm bảo quyền truy cập vào các tính năng
được kiểm soát chặt chẽvà linh hoạt theo quy định của tổchức.
Actors
Admin
Trigger
Admin truy cập vào mục "Quản lý hệthống"và chọn "Cấu hình
truy cập theo vai trò".
Pre-Conditions
• Admin đã đăng nhập với quyền quản trịcao.
• Hệthống đã định nghĩa sẵn một danh sách các quyền hạn
(permissions) có thểgán.
Post
Condi-
tions
• Vai trò của người dùng được cập nhật.
• Quyền truy cập của người dùng thay đổi (có thểcó hiệu lực
ngay lập tức hoặc sau lần đăng nhập tiếp theo).
Normal Flow
1. Từgiao diện "Quản lý người dùng", Admin chọn chức năng
"Cấu hình truy cập theo vai trò".
2. Hệthống hiển thịdanh sách các vai trò hiện có trong hệthống.
3. Admin chọn một vai trò cụthểđểcấu hình (ví dụ: "Giảng
viên").
4. Hệthống hiển thịmột giao diện chi tiết, bao gồm:
• Tên và mô tảcủa vai trò đã chọn.
• Danh sách các quyền hạn hiện tại mà vai trò này đang có.
• Danh sách tất cảcác quyền hạn có sẵn trong hệthống để
Admin có thểgán thêm.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 52/254

=== SUMMARY ===
Phân đoạn này mô tả chi tiết Use-case UC-43 về việc cấu hình truy cập dựa trên vai trò dành cho Admin. Chức năng này cho phép Admin quản lý, định nghĩa và gán các quyền hạn cụ thể cho từng vai trò người dùng, đảm bảo tính bảo mật và linh hoạt của hệ thống. Quy trình bao gồm việc chọn vai trò từ danh sách và điều chỉnh các quyền hạn từ một danh sách có sẵn. Kết quả sau khi thực hiện là quyền truy cập của người dùng sẽ được cập nhật.

=== REVIEW QUESTIONS ===
1. Mục đích chính của chức năng cấu hình truy cập theo vai trò (UC-43) là gì?
2. Để thực hiện UC-43, Admin cần phải thỏa mãn những điều kiện tiên quyết nào?
3. Sau khi Admin hoàn tất cấu hình, quyền truy cập mới của người dùng sẽ có hiệu lực khi nào?
4. Trong luồng xử lý chuẩn, Admin sẽ nhìn thấy những thông tin chi tiết gì khi chọn một vai trò cụ thể để cấu hình?','a4a0b8b3-dd29-4112-881a-cacfb70b6eb7'::uuid,NULL,NULL,71,599,'2026-03-21 13:35:13.330313+07'),
	 ('11b68fce-fe3e-466e-8bb4-affb586318eb'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,71,'=== ORIGINAL CONTENT ===
5. Admin thực hiện thay đổi quyền hạn cho vai trò:
• Thêm quyền: Admin chọn một hoặc nhiều quyền từ
danh sách có sẵn và gán cho vai trò.
• Xóa quyền: Admin bỏchọn một hoặc nhiều quyền mà
vai trò đó không còn được phép thực hiện.
6. Admin nhấn nút "Lưu thay đổi".
7. Hệthống xác thực các thay đổi và cập nhật lại bộquyền
cho vai trò đã chọn trong cơ sởdữliệu.
8. Hệthống hiển thịthông báo "Cập nhật quyền cho vai trò
[Tên vai trò] thành công". Use case kết thúc.
Alternative
Flow
6a. Admin hủy bỏthay đổi:
6a.1. Ởbước 6, Admin nhấn nút "Hủy bỏ".
6a.2. Hệthống bỏqua mọi thay đổi chưa được lưu và quay trởlại
màn hình danh sách các vai trò. Use case kết thúc.
Exceptions
• 7a. Lỗi hệthống khi đang cập nhật: Ởbước 7, nếu có lỗi xảy
ra (ví dụ: mất kết nối cơ sởdữliệu), hệthống sẽkhông lưu
lại thay đổi. Hệthống hiển thịthông báo lỗi: "Đã có lỗi xảy
ra. Vui lòng thửlại."Hệthống giữnguyên các lựa chọn của
Admin trên giao diện đểhọcó thểthửlưu lại.
3.4.3.3
UC-53: Xem log hệthống
Đặc tảUC-53: Xem log hệthống được trình bày trong bảng 3.9.
Bảng 3.9: Đặc tảUC-53: Xem log hệthống
Use-case Code
UC-53
Use-case Name
Xem log hệthống
Description
Cung cấp một công cụđểAdmin theo dõi, kiểm tra và truy vết
mọi hoạt động quan trọng diễn ra trên hệthống. Log ghi lại chi
tiết "ai, làm gì, khi nào, ởđâu", rất cần thiết cho việc khắc phục sự
cố, kiểm toán bảo mật và giám sát tuân thủ.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 53/254

=== SUMMARY ===
Đoạn văn bản mô tả quy trình hoàn tất cấu hình quyền truy cập theo vai trò (UC-43), bao gồm các bước thêm/xóa quyền, lưu thay đổi và xử lý ngoại lệ khi gặp lỗi hệ thống. Ngoài ra, tài liệu giới thiệu chức năng Xem log hệ thống (UC-53), giúp quản trị viên theo dõi và truy vết các hoạt động trên hệ thống nhằm mục đích bảo mật, giám sát tuân thủ và khắc phục sự cố hiệu quả thông qua các thông tin chi tiết về đối tượng và hành động.

=== REVIEW QUESTIONS ===
1. Trong UC-43, quy trình hệ thống xử lý như thế nào khi Admin nhấn nút ''Lưu thay đổi''?
2. Nếu xảy ra lỗi mất kết nối cơ sở dữ liệu trong quá trình cập nhật quyền, hệ thống sẽ phản hồi như thế nào với Admin?
3. Mục đích cốt lõi của chức năng ''Xem log hệ thống'' (UC-53) đối với quản trị viên là gì?
4. Theo mô tả của UC-53, một bản ghi log cần cung cấp những thông tin chi tiết nào để phục vụ việc truy vết?','0f40cead-87a2-4315-9c10-86ed1b313d35'::uuid,NULL,NULL,72,587,'2026-03-21 13:35:13.330313+07'),
	 ('6a3aa0e5-b5ca-474a-99fd-c59607c4e2b5'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,72,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Actors
Admin
Trigger
Admin chọn chức năng "Nhật ký hệthống"từmenu quản trị.
Pre-Conditions
• Admin đã đăng nhập hệthống với quyền truy cập log.
• Cơ chếghi log của hệthống đang hoạt động.
Post
Condi-
tions
• Lịch sửhoạt động được hiển thịcho Admin theo các tiêu chí
đã lọc.
• Admin có thểxuất file log đểphục vụmục đích lưu trữhoặc
phân tích sâu hơn.
Normal Flow
1. Admin truy cập trang "Nhật ký hệthống".
2. Hệthống hiển thịdanh sách các hoạt động gần nhất, được
sắp xếp theo thời gian từmới đến cũ. Mỗi dòng log bao gồm:
Thời gian, Người dùng, Hành động, Đối tượng bịtác động,
Địa chỉIP.
3. Admin sửdụng các công cụlọc đểthu hẹp phạm vi tìm kiếm:
• Lọc theo khoảng thời gian (ví dụ: 24 giờqua, 7 ngày qua,
tùy chỉnh).
• Lọc theo loại hành động (ví dụ: Đăng nhập, Tạo khóa
học, Xóa người dùng).
• Tìm kiếm theo tên người dùng hoặc địa chỉIP.
4. Hệthống cập nhật danh sách log theo bộlọc.
5. Admin nhấp vào một dòng log đểxem thông tin chi tiết hơn
(nếu có), ví dụnhư dữliệu trước và sau khi thay đổi.
Alternative
Flow
4a. Xuất log: Sau khi đã lọc được kết quảmong muốn, Admin
nhấn nút "Xuất CSV"đểtải vềmột file chứa toàn bộdữliệu log
đang hiển thị.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 54/254

=== SUMMARY ===
Phân đoạn này mô tả chi tiết ca sử dụng UC-53: Xem log hệ thống. Tài liệu cung cấp quy trình để Admin theo dõi và quản lý nhật ký hoạt động, bao gồm các điều kiện tiên quyết, luồng sự kiện chuẩn từ việc truy cập, hiển thị danh sách log (thời gian, người dùng, hành động, IP) đến các thao tác lọc dữ liệu nâng cao. Ngoài ra, Admin còn có khả năng xem chi tiết từng dòng log và xuất dữ liệu ra file CSV để phục vụ công tác kiểm toán và bảo mật.

=== REVIEW QUESTIONS ===
1. Điều kiện tiên quyết để Admin có thể thực hiện chức năng xem log hệ thống là gì?
2. Mỗi dòng log hiển thị trong danh sách bao gồm những thông tin cơ bản nào?
3. Admin có thể sử dụng những tiêu chí nào để lọc và thu hẹp phạm vi tìm kiếm nhật ký?
4. Trong trường hợp muốn lưu trữ hoặc phân tích sâu hơn, hệ thống hỗ trợ Admin xuất dữ liệu log dưới định dạng file nào?','78c9f3a8-c03f-4449-a33b-42746a71eb4c'::uuid,NULL,NULL,73,545,'2026-03-21 13:35:13.330313+07'),
	 ('6c0974d6-c61b-4158-942e-2051f47eb2ea'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,73,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Exceptions
• 2e. Lỗi kết nối CSDL log: Nếu hệthống không thểtruy cập
vào cơ sởdữliệu log, một thông báo lỗi sẽđược hiển thị.
• 3e. Truy vấn quá lớn: Nếu Admin chọn một khoảng thời gian
quá dài, hệthống có thểhiển thịcảnh báo "Phạm vi dữliệu
quá lớn. Vui lòng chọn khoảng thời gian ngắn hơn đểđảm
bảo hiệu suất."
Các đặc tảkhác vềcác use-case liên quan đến admin sẽđược trình bày trong phần
phụlục A.3.
3.5
Sơ đồtuần tự(Sequence Diagram)
Trong khuôn khổtài liệu thiết kếnày, sơ đồtuần tự(Sequence Diagram) được xây
dựng tập trung vào các use-case sửdụng cốt lõi (Core Use Cases) có luồng xửlý nghiệp
vụphức tạp, nhằm làm rõ các tương tác quan trọng giữa các thành phần hệthống.
3.5.1
Sơ đồtuần tựlàm bài tập và kiểm tra
Dưới đây là sơ đồtuần tựcho UC-48: Làm bài tập và kiểm tra. Đây là chức năng
trọng tâm với nhiều tương tác đa chiều giữa sinh viên, hệthống kiểm soát bài làm và cơ
sởdữliệu.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 55/254

=== SUMMARY ===
Đoạn văn bản mô tả các tình huống ngoại lệ khi quản trị viên truy cập nhật ký hệ thống, bao gồm lỗi kết nối cơ sở dữ liệu và giới hạn phạm vi truy vấn để đảm bảo hiệu suất. Nội dung cũng giới thiệu phần sơ đồ tuần tự, tập trung vào các ca sử dụng cốt lõi có luồng nghiệp vụ phức tạp. Trong đó, chức năng ''Làm bài tập và kiểm tra'' (UC-48) được xác định là trọng tâm với nhiều tương tác phức tạp giữa sinh viên, hệ thống và cơ sở dữ liệu.

=== REVIEW QUESTIONS ===
1. Những lỗi ngoại lệ (exceptions) nào có thể xảy ra khi Admin thực hiện xem nhật ký hệ thống?
2. Hệ thống sẽ đưa ra cảnh báo gì nếu quản trị viên chọn khoảng thời gian lọc dữ liệu quá dài?
3. Tại sao tài liệu thiết kế lại tập trung xây dựng sơ đồ tuần tự cho các ''Core Use Cases''?
4. Chức năng ''Làm bài tập và kiểm tra'' (UC-48) đóng vai trò gì và liên quan đến những thành phần nào trong hệ thống?','9ffe1a1a-3b8a-45cf-974e-9f5531bf57fd'::uuid,NULL,NULL,74,487,'2026-03-21 13:35:13.330313+07'),
	 ('58d7f0b4-36e1-426c-a172-669ac04f6fce'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,74,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Hình 3.4: Sơ đồtuần tựcho UC-48: Làm bài tập và kiểm tra
3.5.2
Sơ đồtuần tựtạo đềkiểm tra bằng AI
Hình 3.5 là sơ đồtuần tựminh họa quy trình tạo đềkiểm tra bằng AI trong hệthống.
Quy trình này bao gồm các bước từviệc giảng viên yêu cầu tạo đềkiểm tra, hệthống
gửi yêu cầu đến dịch vụAI, nhận kết quảvà lưu trữđềkiểm tra vào cơ sởdữliệu.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 56/254

=== SUMMARY ===
Đoạn văn bản giới thiệu về các sơ đồ tuần tự trong hệ thống, tập trung vào hai chức năng chính: thực hiện bài tập và kiểm tra (UC-48) và quy trình tạo đề thi bằng trí tuệ nhân tạo (AI). Quy trình tạo đề bằng AI mô tả sự tương tác giữa giảng viên, hệ thống và dịch vụ AI, bao gồm các bước từ gửi yêu cầu, xử lý dữ liệu cho đến khi lưu trữ kết quả vào cơ sở dữ liệu.

=== REVIEW QUESTIONS ===
1. Sơ đồ tuần tự Hình 3.4 mô tả cho trường hợp sử dụng (use-case) cụ thể nào?
2. Quy trình tạo đề kiểm tra bằng AI bao gồm những bước cơ bản nào?
3. Trong quy trình tạo đề bằng AI, hệ thống gửi yêu cầu đến thành phần nào để sinh nội dung?
4. Dữ liệu đề kiểm tra sau khi được AI tạo ra sẽ được lưu trữ ở đâu?','3652a0aa-7c68-4c96-82dd-e5762ced779f'::uuid,NULL,NULL,75,307,'2026-03-21 13:35:13.330313+07'),
	 ('a047b078-831a-402e-a10f-22b620f80fd1'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,75,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Hình 3.5: Sơ đồtuần tựtạo đềkiểm tra bằng AI
Đặc tảsơ đồtuần tự: AI hỗtrợgiảng viên tạo câu hỏi
Sơ đồtuần tựmô tảquy trình giảng viên sửdụng hệthống AI đểsinh câu hỏi từtài
liệu hoặc đềcương môn học. Luồng xửlý được thực hiện theo các bước sau:
1. Giảng viên truy cập giao diện xây dựng bài kiểm tra và chọn chức năng “Tạo câu
hỏi bằng AI”, đồng thời cấu hình các tham sốnhư sốlượng câu hỏi, độkhó và chủ
đề.
2. Web UI gửi yêu cầu tạo câu hỏi nháp đến dịch vụquản lý đánh giá của hệthống.
3. (Tuỳchọn) Nếu giảng viên cung cấp tài liệu hoặc đềcương, hệthống tải tài liệu lên
dịch vụlưu trữvà nhận vềđịnh danh tài liệu.
4. Dịch vụquản lý đánh giá gọi dịch vụAI đểsinh câu hỏi nháp dựa trên yêu cầu và
tài liệu đã cung cấp.
5. Trường hợp AI xửlý thành công, danh sách câu hỏi nháp được trảvề, lưu vào cơ sở
dữliệu với trạng thái DRAFT và hiển thịcho giảng viên.
6. Trường hợp AI gặp lỗi hoặc quá thời gian xửlý, hệthống thông báo lỗi và cho phép
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 57/254

=== SUMMARY ===
Đoạn văn bản này trình bày đặc tả sơ đồ tuần tự cho chức năng tạo câu hỏi kiểm tra hỗ trợ bởi AI. Quy trình bắt đầu từ việc giảng viên thiết lập tham số, hệ thống xử lý tài liệu (nếu có) và gửi yêu cầu đến dịch vụ AI. Kết quả là các câu hỏi nháp được tạo ra, lưu trữ vào cơ sở dữ liệu dưới trạng thái DRAFT hoặc thông báo lỗi nếu quy trình thất bại. Đây là tài liệu quan trọng mô tả sự tương tác giữa các thành phần hệ thống.

=== REVIEW QUESTIONS ===
1. Giảng viên có thể thiết lập các tham số đầu vào nào khi yêu cầu AI tạo câu hỏi?
2. Dịch vụ nào trong hệ thống chịu trách nhiệm trực tiếp gọi dịch vụ AI để sinh câu hỏi?
3. Sau khi AI xử lý thành công, các câu hỏi nháp sẽ được lưu vào cơ sở dữ liệu với trạng thái gì?
4. Hệ thống sẽ phản hồi như thế nào trong trường hợp AI gặp lỗi hoặc xử lý quá thời gian?','08949d29-cb3a-4230-af36-a3123308f8e5'::uuid,NULL,NULL,76,487,'2026-03-21 13:35:13.331495+07'),
	 ('8c064bb9-e642-43bc-b67a-e3ce7fa4a305'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,76,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
giảng viên thực hiện lại thao tác.
7. Giảng viên xem trước, chỉnh sửa, xóa hoặc bổsung câu hỏi; các thay đổi được cập
nhật vào hệthống.
8. Sau khi hoàn tất hiệu chỉnh, giảng viên chọn “Lưu và phát hành”, hệthống cập
nhật trạng thái câu hỏi sang PUBLISHED.
9. Hệthống thông báo phát hành thành công và kết thúc quy trình.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 58/254

=== SUMMARY ===
Đoạn văn bản mô tả các bước cuối cùng trong quy trình giảng viên sử dụng AI để tạo câu hỏi kiểm tra. Giảng viên có quyền xem trước, chỉnh sửa hoặc xóa các câu hỏi nháp trước khi quyết định lưu và phát hành. Khi quá trình hiệu chỉnh hoàn tất, trạng thái của câu hỏi sẽ được chuyển từ nháp sang ''PUBLISHED'', và hệ thống sẽ gửi thông báo xác nhận việc phát hành thành công để kết thúc quy trình.

=== REVIEW QUESTIONS ===
1. Giảng viên có thể thực hiện những thao tác nào đối với các câu hỏi nháp do AI tạo ra?
2. Sau khi giảng viên chọn ''Lưu và phát hành'', trạng thái của câu hỏi trong hệ thống sẽ thay đổi như thế nào?
3. Hệ thống thực hiện hành động gì để xác nhận quy trình tạo câu hỏi đã hoàn tất thành công?
4. Mục đích của việc cho phép giảng viên chỉnh sửa câu hỏi ở bước 7 là gì?','b85227bf-d06d-428b-a5c1-b3c0ea6bae4c'::uuid,NULL,NULL,77,325,'2026-03-21 13:35:13.331495+07');
INSERT INTO coaching_chatbot.lecture_knowledge_chunks (lecture_knowledge_chunks_id,lecture_knowledge_id,chunk_index,chunk_content,qdrant_point_id,start_time_seconds,end_time_seconds,page_number,token_count,created_at) VALUES
	 ('d9bd7ad0-161f-4b91-86fb-beaf06778a44'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,77,'=== ORIGINAL CONTENT ===
Trường Đại Học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa Học & KỹThuật Máy Tính
Chương 4
Thiết kếhệthống
Chương này trình bày thiết kếhệthống LMS dựa trên mô hình phân tích nghiệp vụ
và yêu cầu chức năng đã xác định. Thiết kếtập trung vào hai thành phần chính: kiến trúc
hệthống và cơ sởdữliệu. Các nội dung trong chương là cơ sởcho quá trình hiện thực
hoá và triển khai hệthống ởcác chương tiếp theo.
4.1
Cơ sởdữliệu (Database)
4.1.1
Sơ đồEERD (Enhanced Entity-Relationship Diagram)
Hình 4.1 thểhiện sơ đồEERD (Enhanced Entity-Relationship Diagram) của hệthống
quản lý học tập (LMS). Sơ đồmô tảchi tiết các thực thểchính, các thuộc tính và mối
quan hệgiữa chúng trong cơ sởdữliệu của hệthống.
Link hình ảnh: Tại đây
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 59/254

=== SUMMARY ===
Chương 4 trình bày chi tiết về thiết kế hệ thống LMS, tập trung vào hai thành phần cốt lõi: kiến trúc hệ thống và cơ sở dữ liệu. Dựa trên phân tích nghiệp vụ và yêu cầu chức năng, nội dung này tạo tiền đề cho việc triển khai thực tế. Điểm nhấn chính trong phần này là sơ đồ EERD, giúp mô tả rõ ràng các thực thể, thuộc tính và mối quan hệ cấu thành nên cơ sở dữ liệu của hệ thống quản lý học tập.

=== REVIEW QUESTIONS ===
1. Thiết kế hệ thống LMS trong chương này được xây dựng dựa trên những yếu tố tiền đề nào?
2. Hai thành phần trọng tâm được nhắc đến trong quá trình thiết kế hệ thống là gì?
3. Sơ đồ EERD của hệ thống LMS có vai trò gì trong việc mô tả cơ sở dữ liệu?
4. Tại sao các nội dung trong chương thiết kế lại đóng vai trò là cơ sở cho các chương tiếp theo của đồ án?','92b7619c-4618-4c09-881a-4cd7a24d5872'::uuid,NULL,NULL,78,403,'2026-03-21 13:35:13.331495+07'),
	 ('713474e7-c943-4a4d-956d-06e0a98d388c'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,79,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
4.1.2
Ánh xạEERD sang mô hình quan hệ(Relational Model) tương
ứng với từng service
4.1.2.1
User Management Service
Hình 4.2 thểhiện mô hình quan hệ(Relational Model) của User Management Service,
bao gồm các bảng đểquản lý quyền truy cập và vai trò của người dùng trong hệthống.
Hình 4.2: Mô hình quan hệcủa User Management Service
Link hình ảnh: Tại đây
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 61/254

=== SUMMARY ===
Phần này hướng dẫn cách chuyển đổi từ sơ đồ thực thể kết hợp mở rộng (EERD) sang mô hình quan hệ cho từng dịch vụ cụ thể trong hệ thống LMS. Trọng tâm là User Management Service, nơi các bảng dữ liệu được thiết kế để quản lý chi tiết quyền truy cập và vai trò của người dùng. Đây là bước quan trọng để cụ thể hóa cấu trúc dữ liệu, phục vụ cho việc quản lý định danh và phân quyền trong hệ thống.

=== REVIEW QUESTIONS ===
1. Mục đích của việc ánh xạ từ sơ đồ EERD sang mô hình quan hệ trong thiết kế hệ thống là gì?
2. User Management Service trong hệ thống LMS bao gồm các bảng dữ liệu nhằm quản lý những đối tượng nào?
3. Hình 4.2 trong tài liệu minh họa cho mô hình nào của User Management Service?
4. Theo nội dung văn bản, các bảng trong mô hình quan hệ của User Management Service hỗ trợ chức năng gì cho người dùng?','31ef71b7-3edf-4d10-ace9-e04d50540d6b'::uuid,NULL,NULL,80,342,'2026-03-21 13:35:13.331495+07'),
	 ('def7e748-8251-4da3-89ce-5502cf7c8a3e'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,80,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
4.1.2.2
Communication Service
Hình 4.3 thểhiện mô hình quan hệ(Relational Model) của Communication Service,
bao gồm các bảng đểquản lý các hoạt động giao tiếp và thảo luận trong hệthống.
Hình 4.3: Mô hình quan hệcủa Communication Service
Link hình ảnh: Tại đây
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 62/254

=== SUMMARY ===
Nội dung này trình bày về mô hình quan hệ (Relational Model) dành riêng cho Communication Service (Dịch vụ Giao tiếp) trong hệ thống LMS. Dịch vụ này đóng vai trò quan trọng trong việc quản lý các bảng dữ liệu liên quan đến hoạt động tương tác, trao đổi và thảo luận giữa các thành viên. Đây là bước chuyển đổi từ thiết kế EERD tổng quát sang cấu trúc cơ sở dữ liệu chi tiết cho từng dịch vụ cụ thể của hệ thống.

=== REVIEW QUESTIONS ===
1. Dịch vụ Communication Service trong hệ thống LMS chịu trách nhiệm quản lý những hoạt động gì?
2. Hình 4.3 trong tài liệu minh họa loại mô hình nào cho dịch vụ giao tiếp?
3. Việc thiết lập các bảng dữ liệu trong Communication Service nhằm mục đích gì?
4. Mô hình quan hệ của Communication Service là kết quả của quá trình ánh xạ từ sơ đồ nào?','c78e9d16-b91f-470d-b94d-af8db8b3f819'::uuid,NULL,NULL,81,309,'2026-03-21 13:35:13.331495+07'),
	 ('13246efd-7977-4f73-822a-27959cfa0442'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,81,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
4.1.2.3
Assessment Service
Hình 4.4 thểhiện mô hình quan hệ(Relational Model) của Assessment Service, bao
gồm các bảng đểquản lý các hoạt động đánh giá và kiểm tra trong hệthống.
Hình 4.4: Mô hình quan hệcủa Assessment Service
Link hình ảnh: Tại đây
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 63/254

=== SUMMARY ===
Đoạn văn bản này giới thiệu về mô hình quan hệ (Relational Model) của dịch vụ Assessment Service (Dịch vụ Đánh giá). Mục tiêu chính của dịch vụ này là cung cấp cấu trúc các bảng dữ liệu cần thiết để quản lý hiệu quả các hoạt động liên quan đến đánh giá và kiểm tra năng lực của người dùng trong hệ thống.

=== REVIEW QUESTIONS ===
1. Dịch vụ Assessment Service có vai trò gì trong cấu trúc hệ thống được mô tả?
2. Mô hình quan hệ của Assessment Service được trình bày cụ thể tại hình nào?
3. Các bảng trong mô hình quan hệ của Assessment Service được dùng để quản lý những hoạt động gì?
4. Assessment Service thuộc tiểu mục nào trong báo cáo đồ án chuyên ngành này?','cc79e4d3-6982-4c5f-a285-4d6cd1d5671e'::uuid,NULL,NULL,82,277,'2026-03-21 13:35:13.331495+07'),
	 ('e7e19e12-27e4-4db4-9860-4d739cf765c7'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,82,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
4.1.2.4
Learning Service
Hình 4.5 thểhiện mô hình quan hệ(Relational Model) của Learning Service, bao gồm
các bảng đểquản lý các hoạt động học tập và tiến trình của sinh viên trong hệthống.
Hình 4.5: Mô hình quan hệcủa Learning Service
Link hình ảnh: Tại đây
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 64/254

=== SUMMARY ===
Đoạn văn giới thiệu về Learning Service, một thành phần trong hệ thống quản lý học tập. Nội dung tập trung vào mô hình quan hệ (Relational Model) của dịch vụ này, với mục tiêu chính là quản lý các hoạt động học tập và theo dõi tiến trình của sinh viên thông qua các bảng dữ liệu được thiết kế sẵn.

=== REVIEW QUESTIONS ===
1. Mục đích chính của Learning Service trong hệ thống là gì?
2. Mô hình dữ liệu nào được sử dụng để thể hiện cấu trúc của Learning Service?
3. Learning Service quản lý thông tin gì liên quan đến sinh viên?
4. Hình 4.5 trong tài liệu minh họa cho nội dung cụ thể nào?','1dd3279b-eba9-488e-8736-6d0de423cfb7'::uuid,NULL,NULL,83,261,'2026-03-21 13:35:13.331495+07'),
	 ('b9447b29-a19f-47e6-8ec4-5cbaeaf8a89d'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,83,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
4.1.2.5
Notification Service
Hình 4.6 thểhiện mô hình quan hệ(Relational Model) của Notification Service, bao
gồm các bảng đểquản lý các thông báo và cài đặt thông báo trong hệthống.
Hình 4.6: Mô hình quan hệcủa Notification Service
Link hình ảnh: Tại đây
4.1.2.6
Course Management Service
Hình 4.7 thểhiện mô hình quan hệ(Relational Model) của Course Management Ser-
vice, bao gồm các bảng liên quan đểquản lý các khóa học trong và ngoài chương trình
đào tạo cùng với nội dung chi tiết của từng khóa học bao gồm các bài học, tài liệu, và
hoạt động liên quan.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 65/254

=== SUMMARY ===
Đoạn văn bản mô tả mô hình quan hệ (Relational Model) cho hai dịch vụ quan trọng trong hệ thống: Notification Service và Course Management Service. Notification Service quản lý các thông báo và cài đặt thông báo của người dùng. Course Management Service đảm nhận vai trò quản lý thông tin các khóa học trong và ngoài chương trình đào tạo, bao gồm chi tiết về bài học, tài liệu và các hoạt động liên quan. Cả hai đều được minh họa thông qua các sơ đồ bảng dữ liệu cụ thể.

=== REVIEW QUESTIONS ===
1. Dịch vụ Notification Service chịu trách nhiệm quản lý những nội dung cụ thể nào trong hệ thống?
2. Mô hình quan hệ của Course Management Service bao gồm những thành phần chi tiết nào của một khóa học?
3. Ngoài các khóa học trong chương trình đào tạo, Course Management Service còn quản lý loại khóa học nào khác không?
4. Mục đích chung của việc xây dựng các bảng trong mô hình quan hệ (Relational Model) cho các dịch vụ này là gì?','ca914e6f-73a1-4e77-8032-8e025f99b8d2'::uuid,NULL,NULL,84,421,'2026-03-21 13:35:13.332512+07'),
	 ('ee3bd34c-08ec-487c-9775-a4e0c3cab02d'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,86,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Nhược điểm:
• Khảnăng mởrộng hạn chếkhi hệthống lớn dần, đặc biệt với các yêu cầu phân tán.
• Hiệu năng có thểbịảnh hưởng do dữliệu phải đi qua nhiều lớp trung gian.
• Không phù hợp với các hệthống yêu cầu tính linh hoạt và mởrộng cao.
b. Kiến trúc máy khách – máy chủ(Client–Server Architecture)
Kiến trúc máy khách – máy chủđược mô tảdưới Hình 4.9 là mô hình trong đó hệ
thống được chia thành hai thành phần chính: máy khách (client) và máy chủ(server).
Máy khách chịu trách nhiệm giao diện và tương tác người dùng, trong khi máy chủxửlý
nghiệp vụvà quản lý dữliệu [8].
Hình 4.9: Mô hình kiến trúc máy khách – máy chủ
Ưu điểm:
• Quản lý tập trung dữliệu và tài nguyên, giúp tăng cường bảo mật.
• Dễbảo trì và nâng cấp do logic nghiệp vụtập trung tại máy chủ.
• Phù hợp với các hệthống web truyền thống.
Nhược điểm:
• Máy chủcó thểtrởthành điểm nghẽn khi sốlượng người dùng tăng cao.
• Phụthuộc nhiều vào chất lượng kết nối mạng.
• Khảnăng mởrộng theo chiều ngang còn hạn chếnếu không kết hợp các kỹthuật
bổtrợ.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 68/254

=== SUMMARY ===
Đoạn văn bản phân tích các nhược điểm của kiến trúc phân lớp và giới thiệu chi tiết về kiến trúc máy khách - máy chủ (Client-Server). Trong mô hình Client-Server, hệ thống được phân định rõ ràng giữa máy khách (xử lý giao diện) và máy chủ (xử lý nghiệp vụ, dữ liệu). Mặc dù có ưu điểm về quản lý tập trung và bảo mật, mô hình này đối mặt với thách thức về nghẽn cổ chai tại máy chủ, phụ thuộc vào mạng và hạn chế trong việc mở rộng quy mô lớn.

=== REVIEW QUESTIONS ===
1. Trách nhiệm cụ thể của máy khách và máy chủ trong mô hình kiến trúc Client-Server là gì?
2. Tại sao việc tập trung logic nghiệp vụ tại máy chủ lại giúp hệ thống dễ bảo trì và nâng cấp hơn?
3. Nêu những nguyên nhân khiến hiệu năng của mô hình Client-Server bị ảnh hưởng khi số lượng người dùng tăng cao.
4. Dựa trên nội dung, tại sao kiến trúc phân lớp lại bị coi là có khả năng mở rộng hạn chế đối với các yêu cầu phân tán?','c106e2d7-df85-4787-a5a5-039c7bee2f1e'::uuid,NULL,NULL,87,526,'2026-03-21 13:35:13.332512+07'),
	 ('bfe7319d-0540-4af8-87ad-14cbf4c6cd6e'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,87,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
c. Kiến trúc đường ống (Pipeline Architecture)
Kiến trúc đường ống được mô tảdưới Hình 4.10 được tổchức hệthống dưới dạng
chuỗi các giai đoạn xửlý liên tiếp. Dữliệu được truyền qua các giai đoạn (filters), trong
đó mỗi giai đoạn đảm nhận một phần xửlý độc lập [9].
Hình 4.10: Mô hình kiến trúc đường ống
Ưu điểm:
• Hỗtrợxửlý song song, nâng cao hiệu suất.
• Các giai đoạn có thểtái sửdụng và dễdàng thay thế.
Nhược điểm:
• Khó xửlý lỗi khi một giai đoạn gặp sựcố.
• Không phù hợp với các hệthống yêu cầu xửlý linh hoạt hoặc tương tác phức tạp.
d. Kiến trúc hướng sựkiện (Event–Driven Architecture)
Kiến trúc hướng sựkiện được mô tảdưới Hình 4.11 là mô hình trong đó các thành
phần giao tiếp thông qua việc phát sinh và xửlý sựkiện. Các thành phần có thểhoạt
động độc lập và phản hồi bất đồng bộ[8,10].
Ưu điểm:
• Hỗtrợxửlý bất đồng bộvà mởrộng tốt.
• Phù hợp với hệthống real-time và phân tán.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 69/254

=== SUMMARY ===
Nội dung này giới thiệu về hai mô hình kiến trúc phần mềm quan trọng: Kiến trúc đường ống (Pipeline Architecture) và Kiến trúc hướng sự kiện (Event-Driven Architecture). Kiến trúc đường ống tổ chức hệ thống thành chuỗi các giai đoạn xử lý liên tiếp giúp tối ưu hiệu suất nhưng khó xử lý lỗi. Trong khi đó, kiến trúc hướng sự kiện tập trung vào giao tiếp bất đồng bộ qua các sự kiện, mang lại khả năng mở rộng cao và đặc biệt phù hợp cho các hệ thống thời gian thực hoặc hệ thống phân tán.

=== REVIEW QUESTIONS ===
1. Nguyên lý hoạt động cơ bản của kiến trúc đường ống (Pipeline Architecture) là gì?
2. Tại sao kiến trúc đường ống lại hỗ trợ nâng cao hiệu suất hệ thống?
3. Đặc điểm nào khiến kiến trúc hướng sự kiện phù hợp với các hệ thống real-time và phân tán?
4. Nêu những nhược điểm cần lưu ý khi áp dụng kiến trúc đường ống vào phát triển phần mềm.','0519db79-216a-437e-a336-19302b849b82'::uuid,NULL,NULL,88,486,'2026-03-21 13:35:13.333409+07'),
	 ('bf6299d9-99db-4983-8d2e-bef29cd3ab9f'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,88,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Hình 4.11: Mô hình kiến trúc hướng sựkiện
Nhược điểm:
• Khó theo dõi luồng xửlý và gỡlỗi.
• Phức tạp trong quản lý và điều phối sựkiện.
e. Kiến trúc microservices (Microservices Architecture)
Kiến trúc microservices được mô tảdưới Hình 4.12 chia hệthống thành các dịch vụ
nhỏ, độc lập, mỗi dịch vụđảm nhận một chức năng nghiệp vụriêng và giao tiếp với nhau
thông qua API [11].
Hình 4.12: Mô hình kiến trúc microservices
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 70/254

=== SUMMARY ===
Đoạn văn bản giới thiệu về đặc điểm và hạn chế của hai mô hình kiến trúc phần mềm hiện đại: hướng sự kiện (Event-Driven) và microservices. Kiến trúc hướng sự kiện gặp thách thức trong việc quản lý và gỡ lỗi do luồng xử lý phức tạp. Trong khi đó, kiến trúc microservices tập trung vào việc chia nhỏ hệ thống thành các dịch vụ độc lập, thực hiện chức năng nghiệp vụ riêng biệt và kết nối với nhau thông qua API, giúp tăng tính linh hoạt cho hệ thống.

=== REVIEW QUESTIONS ===
1. Những khó khăn chính khi triển khai mô hình kiến trúc hướng sự kiện là gì?
2. Mô hình kiến trúc microservices chia hệ thống dựa trên cơ sở nào?
3. Các thành phần trong kiến trúc microservices tương tác với nhau bằng cách nào?
4. Tại sao việc theo dõi luồng xử lý dữ liệu trong kiến trúc hướng sự kiện lại phức tạp hơn các mô hình truyền thống?','4562f122-6079-4903-bb1b-7344173cc32b'::uuid,NULL,NULL,89,359,'2026-03-21 13:35:13.333409+07'),
	 ('6db35f22-42b2-4e08-9cb8-d16a3ae87b5e'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,89,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Ưu điểm:
• Linh hoạt, dễmởrộng và triển khai độc lập.
• Phù hợp với hệthống lớn, nhiều nghiệp vụphức tạp.
• Tăng khảnăng chịu lỗi và sẵn sàng của hệthống.
Nhược điểm:
• Phức tạp trong quản lý, giám sát và triển khai.
• Gia tăng chi phí vận hành và yêu cầu hạtầng.
4.2.2
Lựa chọn kiến trúc hệthống
Đểxây dựng kiến trúc hệthống theo hướng thành phần (component-based) cho LMS,
nhóm dựán tiếp cận theo hướng Actor/Actions, tức là phân rã yêu cầu hệthống theo
các tác nhân (actor) và các hành động (actions) tương ứng như bảng 4.1. Cách tiếp cận
này giúp xác định các miền nghiệp vụ(business domains) và các ranh giới chức năng
(service boundaries) một cách rõ ràng, phù hợp.
Bảng 4.1: Các actor và actions tương ứng
Actor
Actions
Sinh viên
• Thiết lập mục tiêu học tập
• Chỉnh sửa mục tiêu học tập
• Xem lộtrình học
• Xem lịch trình học
• Đăng kí khoá học
• Xem tiến độhọc tập
• Xem tài liệu, bài giảng
• Ghi chú trực tiếp trong quá trình học
• Làm bài tập trắc nghiệm
• Nộp bài tập tựluận
• Làm bài code online
• Tra cứu điểm và xem nhận xét
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 71/254

=== SUMMARY ===
Đoạn văn bản phân tích ưu và nhược điểm của kiến trúc microservices, nhấn mạnh khả năng mở rộng và tính phức tạp trong vận hành. Tài liệu cũng trình bày phương pháp lựa chọn kiến trúc hệ thống cho LMS thông qua việc phân rã yêu cầu theo mô hình Actor/Actions. Phương pháp này giúp xác định các miền nghiệp vụ và ranh giới chức năng rõ ràng, minh họa cụ thể qua các hành động của tác nhân Sinh viên như thiết lập mục tiêu, xem lộ trình học và thực hiện bài tập.

=== REVIEW QUESTIONS ===
1. Kiến trúc microservices có những ưu điểm và nhược điểm gì đối với các hệ thống lớn?
2. Tại sao nhóm dự án lại chọn tiếp cận theo hướng Actor/Actions khi xây dựng kiến trúc hệ thống?
3. Việc phân rã yêu cầu theo Actor/Actions giúp ích gì trong việc xác định cấu trúc các dịch vụ (services)?
4. Hãy liệt kê ít nhất 5 hành động của tác nhân ''Sinh viên'' trong hệ thống LMS theo bảng 4.1.','0a31a95b-4d83-4981-a3ea-dde8017f6fe7'::uuid,NULL,NULL,90,527,'2026-03-21 13:35:13.333934+07');
INSERT INTO coaching_chatbot.lecture_knowledge_chunks (lecture_knowledge_chunks_id,lecture_knowledge_id,chunk_index,chunk_content,qdrant_point_id,start_time_seconds,end_time_seconds,page_number,token_count,created_at) VALUES
	 ('6e529bdb-eec7-4c32-9e1c-ba0f31c26cca'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,123,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
cho phép và dùng tối đa các kỳhè, đồng thời chấp nhận giảm mục tiêu GPA.
– Nếu có thểlinh hoạt vềthời gian, ưu tiên lùi mốc tốt nghiệp đểkhông phải học
trong trạng thái quá tải kéo dài.
Trong mọi trường hợp mức Yếu, hệthống sẽgiải thích rõ nguyên nhân (ví dụ: “Sốtín
chỉcòn lại vượt quá khảnăng tích luỹtối đa” hoặc “GPA cần đạt cho các môn còn lại
vượt quá 4,0”) và khuyến nghịsinh viên quay lại UC-02: Thiết lập mục tiêu học tập
đểđiều chỉnh lại mục tiêu học tập rồi chạy lại thuật toán. Mục tiêu chỉđược sửdụng để
sinh lộtrình chi tiết (UC-03) khi đã đạt mức Trung bình hoặc Tốt.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 105/254

=== SUMMARY ===
Đoạn văn bản hướng dẫn cách xử lý và điều chỉnh mục tiêu học tập khi mức độ khả thi bị đánh giá ở mức ''Yếu''. Hệ thống sẽ phân tích dựa trên các ưu tiên về GPA, Kiến thức hoặc Thời gian để đưa ra gợi ý như lùi mốc tốt nghiệp, giảm tải học tập hoặc hạ mục tiêu GPA. Khi ở mức này, sinh viên bắt buộc phải quay lại bước thiết lập mục tiêu (UC-02) để điều chỉnh cho đến khi đạt mức Trung bình hoặc Tốt mới có thể sinh lộ trình chi tiết.

=== REVIEW QUESTIONS ===
1. Hệ thống đưa ra những nguyên nhân cụ thể nào khi đánh giá mức độ khả thi của mục tiêu ở mức ''Yếu''?
2. Nếu sinh viên ưu tiên về Thời gian nhưng mục tiêu ở mức ''Yếu'', họ cần phải thực hiện những thay đổi gì?
3. Tại sao sinh viên ở mức ''Yếu'' được khuyến nghị quay lại bước UC-02 thay vì tiếp tục sinh lộ trình chi tiết?
4. Điều kiện cần thiết để hệ thống có thể chuyển sang bước sinh lộ trình chi tiết (UC-03) là gì?','2571f4f9-0c08-49ec-9d3b-d16ccae6c002'::uuid,NULL,NULL,124,413,'2026-03-21 13:35:13.342564+07'),
	 ('3d18da7d-3721-4c92-b783-eb0067ceefb8'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,90,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
• Tạo câu hỏi ôn tập từAI
• Chat với AI vềnội dung học tập
• Nhận thông báo
• Nhắn tin & thảo luận với giảng viên
Giảng viên
• Xem thông tin lớp học
• Tạo lớp học
• Xây dựng cấu trúc khoá học
• Thiết lập hệthống thang điểm
• Tải lên và cập nhật nội dung khoá học
• Tạo bài tập (quiz, tựluận, code online)
• Chấm điểm và nhận xét bài tập
• Nhận thông báo
• Nhắn tin & thảo luận với sinh viên
Admin
• Phân bổgiảng viên quản lý khoá học
• Xem thống kê lượt học, tần suất đăng nhập
• Gửi và nhận thông báo
• Xem logs hệthống
• Tạo tài khoản
• Phân quyền người dùng
Dựa trên các actor và actions đã liệt kê, nhóm dựán lựa chọn kiến trúc phần mềm
theo hướng microservices nhằm phân tách hệthống theo các miền nghiệp vụ(business
domains) và triển khai độc lập theo từng dịch vụ. Cách tiếp cận này phù hợp với hệthống
LMS có nhiều phân hệvà có tích hợp AI, giúp tăng tính linh hoạt khi phát triển, dễmở
rộng theo tải, và giảm ảnh hưởng dây chuyền khi thay đổi hoặc gặp sựcố[11].
Các dịch vụnghiệp vụ(Core Services)
Theo kiến trúc trong Hình 4.13, nhóm đềxuất các dịch vụban đầu như sau:
• Authentication Service: Xác thực và cấp phát token truy cập (ví dụOAuth2/JWT),
phục vụkiểm tra truy cập cho toàn hệthống.
– Đăng nhập/đăng xuất
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 72/254

=== SUMMARY ===
Đoạn văn bản chi tiết hóa các hành động của ba đối tượng chính (Sinh viên, Giảng viên, Admin) trong hệ thống LMS. Dựa trên nhu cầu này, kiến trúc microservices được lựa chọn nhằm tối ưu hóa tính linh hoạt, khả năng mở rộng và tích hợp AI. Ngoài ra, nội dung còn giới thiệu dịch vụ xác thực (Authentication Service) sử dụng OAuth2/JWT để quản lý truy cập an toàn cho toàn bộ hệ thống.

=== REVIEW QUESTIONS ===
1. Các hành động cụ thể mà Sinh viên có thể tương tác với AI trong hệ thống này là gì?
2. Tại sao kiến trúc microservices được đánh giá là phù hợp cho hệ thống LMS có tích hợp AI?
3. Admin đóng vai trò gì trong việc quản lý và giám sát hoạt động của hệ thống?
4. Dịch vụ Authentication Service sử dụng công nghệ nào để thực hiện xác thực và kiểm soát truy cập?','1acbb001-b458-4221-90db-7167b3404dab'::uuid,NULL,NULL,91,547,'2026-03-21 13:35:13.333934+07'),
	 ('70ef2555-96a9-4793-855e-5853364b21b7'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,91,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
– Cấp phát và xác minh token
• User Management Service: Quản lý tài khoản người dùng và thông tin hồsơ.
– Tạo/cập nhật thông tin tài khoản
– Khoá tài khoản
– Quản lý hồsơ người dùng
• Student Personalization Service: Quản lý mục tiêu và lộtrình học cá nhân hoá
(tích hợp AI).
– Thiết lập/chỉnh sửa mục tiêu học tập
– Sinh và cập nhật lộtrình học cá nhân hoá
– Gợi ý nội dung học theo tiến độ
• Course Management Service: Quản lý vòng đời khóa học và cấu trúc khóa học.
– Tạo/chỉnh sửa khóa học
– Xây dựng cấu trúc khóa học (Chương, bài học)
– Tải lên và cập nhật nội dung khóa học
– Thiết lập thang điểm và cấu hình khóa học
• Learning Service: Phân phối nội dung học tập và hỗtrợhọc tập (bao gồm các
tương tác trong quá trình học).
– Truy cập tài liệu/bài giảng
– Ghi chú trong quá trình học
– Hỗtrợhỏi đáp nội dung học tập (AI)
• Assessment Service: Quản lý nội dung đánh giá và ngân hàng câu hỏi.
– Tạo/chỉnh sửa bài tập, quiz
– Quản lý ngân hàng câu hỏi
– Thiết lập quy tắc tính điểm
• Assessment Execution Service: Thực thi quá trình làm bài và chấm điểm tựđộng
theo từng hình thức.
– Làm bài trắc nghiệm
– Nộp bài tựluận
– Làm bài code online (code judge)
– Ghi nhận kết quảlàm bài
• Communication Service: Hỗtrợtương tác hai chiều giữa người dùng trong hệ
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 73/254

=== SUMMARY ===
Nội dung này chi tiết các dịch vụ nghiệp vụ cốt lõi trong kiến trúc microservices của một hệ thống LMS. Các dịch vụ bao gồm: xác thực và quản lý người dùng, cá nhân hóa lộ trình học bằng AI, quản lý cấu trúc khóa học, phân phối nội dung học tập, quản lý ngân hàng câu hỏi, thực thi đánh giá tự động (trắc nghiệm, tự luận, lập trình), và dịch vụ giao tiếp. Cách phân chia này giúp hệ thống linh hoạt, dễ mở rộng và tối ưu hóa trải nghiệm học tập.

=== REVIEW QUESTIONS ===
1. Dịch vụ Student Personalization Service đóng vai trò gì trong việc hỗ trợ sinh viên học tập?
2. Sự khác biệt về chức năng giữa Assessment Service và Assessment Execution Service là gì?
3. Các nhiệm vụ chính của Course Management Service trong việc quản lý khóa học bao gồm những gì?
4. Learning Service tích hợp AI nhằm mục đích gì để hỗ trợ người học trong quá trình tương tác?','1bf29c48-63af-4806-94f8-296e91b7723a'::uuid,NULL,NULL,92,577,'2026-03-21 13:35:13.333934+07'),
	 ('974d3f90-455b-4697-b597-634cc014a5ff'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,92,'=== ORIGINAL CONTENT ===
thống.
– Nhắn tin trực tiếp
– Tạo forum thảo luận theo khóa học/chủđề
• Notification Service: Gửi/nhận thông báo một chiều đến người dùng.
– Phát sinh thông báo theo sựkiện hệthống
– Đẩy thông báo tới người dùng
Hạtầng triển khai và tích hợp (Infrastructure Layer)
Ngoài các core services, kiến trúc hệthống còn bao gồm các thành phần hạtầng nhằm
hỗtrợkhảnăng mởrộng, quan sát hệthống và tích hợp bất đồng bộ:
• API Gateway: Đóng vai trò điểm vào (entry point) cho client, thực hiện định tuyến
request tới các microservices, có thểtích hợp thêm các chức năng như kiểm soát
truy cập, giới hạn tốc độvà cache [12].
• Service Discovery: Hỗtrợđăng ký và phát hiện dịch vụđộng trong môi trường
triển khai (phù hợp khi chạy container/Kubernetes) [13].
• Cache: Sửdụng cơ chếcache (ví dụRedis) nhằm giảm tải cho các dịch vụtruy vấn
nhiều và cải thiện hiệu năng phản hồi.
• Event Bus (Kafka): Hỗtrợgiao tiếp bất đồng bộtheo mô hình publish/subscribe
giữa các service, đặc biệt phù hợp cho các luồng sựkiện như phát sinh thông báo,
cập nhật trạng thái học tập [10].
• Logging/Observability (ELK): Thu thập log tập trung và hỗtrợtruy vết/giám sát
hệthống thông qua các công cụnhư Logstash, Elasticsearch và Kibana.
• Static Content & CDN: Phân phối nội dung tĩnh (ảnh, tài liệu, tài nguyên frontend)
qua CDN nhằm tối ưu tốc độtải và giảm tải cho backend.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 74/254

=== SUMMARY ===
Đoạn văn bản mô tả các dịch vụ nghiệp vụ cuối cùng (Communication, Notification) và chi tiết tầng hạ tầng (Infrastructure Layer) trong kiến trúc microservices của hệ thống LMS. Các thành phần hạ tầng then chốt bao gồm API Gateway để định tuyến, Service Discovery quản lý dịch vụ động, Cache và CDN giúp tối ưu hiệu năng. Ngoài ra, Event Bus (Kafka) hỗ trợ giao tiếp bất đồng bộ và bộ công cụ ELK đảm nhiệm vai trò giám sát, quản lý log tập trung, giúp hệ thống vận hành ổn định và dễ mở rộng.

=== REVIEW QUESTIONS ===
1. Dịch vụ Communication Service và Notification Service khác nhau như thế nào về mục đích tương tác?
2. API Gateway đóng vai trò gì trong việc quản lý các yêu cầu (requests) từ phía người dùng đến các microservices?
3. Tại sao việc sử dụng Event Bus (Kafka) lại quan trọng đối với các luồng sự kiện như thông báo hoặc cập nhật trạng thái học tập?
4. Vai trò của bộ công cụ ELK (Logstash, Elasticsearch, Kibana) trong việc duy trì tính ổn định của hệ thống là gì?','bce6fd20-ee9a-4b96-9a85-d9a860a839eb'::uuid,NULL,NULL,93,609,'2026-03-21 13:35:13.333934+07'),
	 ('8b31f9e8-039e-41fc-bf63-2374ce18b66b'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,93,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Hình 4.13: Kiến trúc microservices cho hệ thống LMS
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 75/254

=== SUMMARY ===
Đoạn văn bản giới thiệu về hình minh họa kiến trúc microservices cho hệ thống Quản lý Học tập (LMS). Kiến trúc này bao gồm các dịch vụ cốt lõi như quản lý người dùng, cá nhân hóa học tập bằng AI, quản lý khóa học, và dịch vụ đánh giá. Hệ thống tích hợp các thành phần hạ tầng hiện đại như API Gateway, Service Discovery, Redis Cache, Event Bus (Kafka) và ELK để đảm bảo khả năng mở rộng, hiệu suất cao và khả năng giám sát toàn diện.

=== REVIEW QUESTIONS ===
1. Kiến trúc microservices của hệ thống LMS này bao gồm những nhóm dịch vụ chức năng chính nào?
2. Vai trò của API Gateway trong việc điều phối các yêu cầu từ client đến các microservices là gì?
3. Hệ thống sử dụng thành phần nào để hỗ trợ giao tiếp bất đồng bộ giữa các dịch vụ và ví dụ cụ thể là gì?
4. Các công cụ thuộc nhóm ELK đóng vai trò gì trong việc quản lý vận hành hệ thống microservices này?','9c374324-ed11-4964-9349-b477bb0b253a'::uuid,NULL,NULL,94,277,'2026-03-21 13:35:13.333934+07'),
	 ('68ace306-37ee-46dc-bb52-b7458ed3edf0'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,259,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
B.2.4.4
Thêm câu hỏi bằng file
Hình B.34: Thêm câu hỏi bằng file
Đặc điểm chính:
• Cho phép import câu hỏi từfile với định dạng trắc nghiệm, giảm thao tác nhập tay.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 241/254

=== SUMMARY ===
Nội dung này giới thiệu về chức năng ''Thêm câu hỏi bằng file'' trong hệ thống LMS của Trường Đại học Bách khoa TP.HCM. Tính năng này cho phép người dùng nhập câu hỏi trắc nghiệm hàng loạt từ tệp tin thay vì nhập thủ công, giúp tiết kiệm thời gian và nâng cao hiệu suất quản lý ngân hàng câu hỏi.

=== REVIEW QUESTIONS ===
1. Mục đích chính của tính năng ''Thêm câu hỏi bằng file'' là gì?
2. Loại định dạng câu hỏi nào được hỗ trợ khi import từ file theo mô tả?
3. Lợi ích lớn nhất của việc sử dụng file để thêm câu hỏi so với nhập tay là gì?
4. Hình B.34 trong tài liệu minh họa cho chức năng cụ thể nào của hệ thống?','aa83fc64-3143-41b9-9cd1-531140d80bfb'::uuid,NULL,NULL,260,243,'2026-03-21 13:35:13.370006+07'),
	 ('0d250276-f247-447b-90de-60e98fa7f3d9'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,99,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Từđó suy ra cờ:
has_student_history =
{
True
nếu num_semesters_prior > 0,
False
nếu num_semesters_prior = 0.
Nói cách khác, sinh viên chưa học kỳnào trước đó được xem là “chưa có lịch sử”.
• Các chỉsốtrung bình theo thời gian
Những biến như:
– cumulative_grade_avg: điểm trung bình tích luỹđến hết các kỳtrước
– previous_sem_grade_avg: điểm trung bình của kỳliền trước
– grade_consistency: độổn định điểm qua các môn đã học
– Nếu sinh viên quá mới hoặc bịđứt đoạn, không có dữliệu trong 3 năm gần
nhất, thì dùng trung vịtoàn cục tính trên tất cảsinh viên có lịch sử.
• Tỷlệphân bốđiểm của sinh viên
Các biến dạng tỉlệnhư student_a_plus_grade_rate, student_b_grade_rate,
. . . mô tảnăng lực trong quá khứcủa sinh viên (ví dụ: tỉlệmôn đạt từB trởlên).
Các biến này được điền khuyết bằng cách tương tựdùng trung bình toàn cục của
nhóm sinh viên có lịch sửnếu sinh viên chưa có lịch sửhoặc thiếu dữliệu.
• Xu hướng và tỉlệrớt
– grade_trend: nếu thiếu thì gán 0, coi như không thấy xu hướng tăng hoặc
giảm rõ rệt.
– historic_fail_ratio: nếu sinh viên chưa có lịch sử(has_student_history
= False) thì gán 0 (chưa rớt môn nào trong quá khứ)
• Các chỉsốxếp hạng
Hai biến sem_rank_percentile (xếp hạng trong kỳ) và gpa_rank_percentile
(xếp hạng GPA tích luỹso với bạn cùng khoá) được xửlý như sau:
– Sinh viên chưa có lịch sử: gán 0.5, coi như đứng ởmức trung bình của khoá.
Nhóm đặc trưng phía môn học
Nhóm này mô tảđộkhó của từng môn học, dựa trên dữliệu 3 năm gần nhất.
• Độkhó và tỉlệrớt Các biến như course_fail_rate, course_a_grade_rate,
. . . mô tảphân bốđiểm của môn trong quá khứ.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 81/254

=== SUMMARY ===
Đoạn văn bản hướng dẫn cách xây dựng và xử lý các nhóm đặc trưng cho mô hình dự đoán kết quả học tập. Nội dung bao gồm việc xác định trạng thái lịch sử học tập của sinh viên, cách tính toán các chỉ số trung bình, tỷ lệ phân bố điểm, xu hướng và xếp hạng. Đặc biệt, văn bản nhấn mạnh các phương pháp xử lý giá trị khuyết (imputation) như dùng trung vị toàn cục hoặc gán giá trị mặc định để đảm bảo mô hình hoạt động ổn định.

=== REVIEW QUESTIONS ===
1. Điều kiện nào để biến has_student_history được gán giá trị True?
2. Trong trường hợp sinh viên chưa có lịch sử học tập, các chỉ số xếp hạng percentile được xử lý như thế nào?
3. Giá trị mặc định của biến grade_trend khi thiếu dữ liệu là bao nhiêu và nó mang ý nghĩa gì?
4. Nhóm đặc trưng phía môn học (như course_fail_rate) được mô tả dựa trên dữ liệu trong khoảng thời gian bao lâu?','b770967f-bf12-476f-a45f-e638a219a4d4'::uuid,NULL,NULL,100,653,'2026-03-21 13:35:13.335961+07'),
	 ('6579bec7-d6b1-4306-96d3-2d5467a5c156'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,100,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Nếu môn hoàn toàn chưa có dữliệu trong 3 năm gần nhất, rank_course_difficulty
được gán 0.5 (coi như mức khó trung bình), còn các tỉlệđiểm chữđược điền bằng
trung bình của các môn đã có lịch sử.
Nhóm đặc trưng môn liên quan
Nhóm này tận dụng thông tin từcác môn tiên quyết, môn khuyến nghịvà các môn
cùng nhóm ngành mà sinh viên đã học trước đó.
• has_relative_course: Nếu không tìm thấy môn liên quan nào thoảđiều kiện
(cùng subject_category hoặc là tiên quyết/khuyến nghị), biến này được gán
False.
• Điểm trung bình các môn liên quan: relative_avg_course_grade
1. Nếu sinh viên chưa học môn liên quan nào, dùng cumulative_grade_avg
(điểm trung bình tích luỹ) của chính sinh viên.
2. Nếu tất cảcác bước trên đều không áp dụng được, gán 0.0 như một giá trịan
toàn.
• relative_avg_course_grade_rank_percentile: Nếu thiếu, biến này được gán
0.5, đểgiảđịnh sinh viên đang đứng ởvịtrí trung bình trong các môn học liên quan.
Sau toàn bộbước xửlý trên, mọi cột trong bảng trained_dataset đều có giá trịhợp
lệ, phản ánh hợp lý lịch sửhọc tập của sinh viên, đặc trưng môn học và mối liên hệgiữa
các môn. Bộdữliệu này là đầu vào cho giai đoạn huấn luyện mô hình ởcác phần tiếp
theo.
Cấu trúc bảng trained_dataset
Sau tất cảcác bước xửlý ởtrên, dữliệu cuối cùng được chuẩn hoá và ghi vào bảng
trained_dataset trong cơ sởdữliệu, với 19 376 dòng tương ứng 485 sinh viên. Mỗi dòng
đại diện cho một lần một sinh viên học một môn trong một học kỳ. Các cột trong bảng
được chia thành các nhóm chính như sau:
• Nhóm nhận diện bản ghi và thông tin điểm gốc
– student_id, semester_id, course_id: bộba định danh cho mỗi lần sinh viên học
một môn trong một học kỳ(mỗi bộba là duy nhất).
– course_grade: điểm tổng kết môn (thang 10) của lần học đó, đây là giá trịmà
mô hình trong phần này cốgắng dựđoán.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 82/254

=== SUMMARY ===
Đoạn văn bản trình bày phương pháp xử lý dữ liệu khuyết thiếu (imputation) cho các đặc trưng môn học và môn liên quan, cùng cấu trúc của bảng trained_dataset. Các giá trị thiếu được thay thế bằng những con số hợp lý như giá trị trung bình hoặc trung vị để duy trì tính nhất quán cho mô hình. Bảng dữ liệu cuối cùng bao gồm hơn 19.000 bản ghi từ 485 sinh viên, cung cấp các thông tin định danh và điểm số mục tiêu phục vụ cho việc huấn luyện mô hình dự đoán kết quả học tập.

=== REVIEW QUESTIONS ===
1. Nếu một môn học hoàn toàn chưa có dữ liệu trong 3 năm gần nhất, chỉ số rank_course_difficulty sẽ được gán giá trị bao nhiêu?
2. Hệ thống xử lý đặc trưng ''relative_avg_course_grade'' như thế nào khi sinh viên chưa học bất kỳ môn liên quan nào?
3. Bảng ''trained_dataset'' bao gồm bao nhiêu dòng dữ liệu và tương ứng với bao nhiêu sinh viên?
4. Bộ ba thông tin nào được sử dụng để tạo thành định danh duy nhất cho mỗi bản ghi trong bảng dữ liệu huấn luyện?','67dc3211-68d5-451f-8cfa-55797122570d'::uuid,NULL,NULL,101,733,'2026-03-21 13:35:13.335961+07'),
	 ('8611d145-5c30-4490-ae8e-4d933900573e'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,101,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹ thuật Máy tính
– num_semesters_prior: số học kỳ sinh viên đã hoàn thành trước học kỳ hiện tại.
– has_student_history: cho biết sinh viên đã có lịch sử học tập hay chưa (đúng
nếu num_semesters_prior > 0).
– sem_credits, sem_credits_squared: tổng số tín chỉ sinh viên đăng ký trong học
kỳ đó và bình phương của tổng này, giúp mô tả mức độ “nặng” của học kỳ.
• Nhóm đặc trưng lịch sử học tập của sinh viên (student-level)
– cumulative_grade_avg: điểm trung bình tích luỹ của sinh viên tính đến trước
học kỳ hiện tại.
– previous_sem_grade_avg: điểm trung bình của học kỳ liền trước.
– grade_trend: xu hướng điểm (so sánh điểm kỳ gần nhất với trung bình các kỳ
trước) giá trị dương cho thấy điểm đang có xu hướng tăng, âm cho thấy xu
hướng giảm.
– historic_fail_ratio: tỉ lệ tín chỉ bị rớt trong toàn bộ các học kỳ trước.
– grade_consistency: mức độ ổn định điểm của sinh viên (độ dao động quanh giá
trị trung bình dao động nhỏ nghĩa là điểm khá ổn định).
– sem_rank_percentile: vị trí phần trăm của sinh viên trong nhóm sinh viên cùng
khoá, xét theo điểm trung bình học kỳ (0 gần top, 1 gần cuối).
– gpa_rank_percentile: vị trí phần trăm của sinh viên về GPA tích luỹ so với các
bạn cùng khoá.
– student_a_plus_grade_rate, student_a_grade_rate, . . . , student_d_grade_rate:
tỉ lệ các môn trước đây mà sinh viên đạt ít nhất A+, A, B+, B, C+, C, D+,
D. Nhóm cột này mô tả “dải điểm quen thuộc” của sinh viên trong quá khứ.
• Nhóm đặc trưng về môn học và mặt bằng lịch sử (course-level)
– course_hist_count: số lượt sinh viên đã học môn đó.
– course_hist_median_smooth: điểm trung vị lịch sử của môn trong 3 năm gần
nhất, đã được làm mượt về trung vị chung của toàn hệ thống để tránh lệch khi
số mẫu ít. Đây cũng là giá trị được dùng làm baseline.
– course_hist_missing: cho biết môn có đủ lịch sử trong 3 năm gần nhất hay
không.
– course_fail_rate: tỉ lệ rớt môn trong 3 năm gần nhất, phản ánh độ khó thực tế
của môn.
Báo cáo đồ án chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 83/254

=== SUMMARY ===
Đoạn văn bản mô tả các nhóm đặc trưng dữ liệu dùng để huấn luyện mô hình dự đoán điểm số sinh viên. Các đặc trưng bao gồm: thông tin học kỳ (số tín chỉ, số học kỳ đã học), lịch sử học tập cá nhân (GPA, xu hướng điểm, tỷ lệ rớt, xếp hạng, dải điểm quen thuộc) và đặc điểm của môn học (tỷ lệ rớt, điểm trung vị lịch sử). Việc kết hợp năng lực cá nhân và độ khó môn học giúp tạo ra bộ dữ liệu đầu vào toàn diện cho việc dự báo.

=== REVIEW QUESTIONS ===
1. Biến `grade_trend` được tính toán dựa trên cơ sở nào và ý nghĩa của giá trị dương/âm là gì?
2. Tại sao biến `course_hist_median_smooth` lại cần được ''làm mượt'' về trung vị chung của toàn hệ thống?
3. Làm thế nào để mô tả mức độ học tập ''nặng'' của một sinh viên trong một học kỳ cụ thể thông qua các biến số tín chỉ?
4. Nhóm đặc trưng ''student-level'' bao gồm những chỉ số nào để đánh giá sự ổn định và vị thế của sinh viên so với bạn cùng khóa?','8e569201-a50a-42f3-b258-b876eb53be95'::uuid,NULL,NULL,102,748,'2026-03-21 13:35:13.336962+07'),
	 ('5077315f-1116-4cc3-b224-5151b95be81c'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,102,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
– rank_course_difficulty: vịtrí xếp hạng độkhó của môn so với các môn khác
(dựa trên tỉlệrớt).
– course_a_plus_grade_rate, course_a_grade_rate, . . . , course_d_grade_rate: tỉ
lệsinh viên đạt ít nhất A+, A, B+, B, C+, C, D+, D trong lịch sử3 năm gần
nhất của môn cho thấy phân bốđiểm điển hình của môn đó.
• Nhóm đặc trưng vềcác môn liên quan (relative group)
– has_relative_course: cho biết sinh viên đã từng học ít nhất một môn liên quan
(môn tiên quyết, môn khuyến nghịhoặc môn cùng nhóm kiến thức).
– relative_avg_course_grade: điểm trung bình (theo tín chỉ) của sinh viên trên
các môn liên quan đã học, phản ánh nền tảng kiến thức liên quan.
– relative_avg_course_grade_rank_percentile: vịtrí trung bình của sinh viên trong
lớp ởcác môn liên quan đó (0 là gần top lớp, 1 là gần cuối), cho thấy sinh viên
đang đứng ởđâu so với bạn học trong các môn nền tảng.
• Nhóm thông tin vềsốlần học lại môn
– retake_no: sốlần học lại môn (0 là lần đầu, 1 là học lại lần một, . . . ), giúp mô
hình phân biệt giữa lần học đầu tiên và các lần học lại.
Nhờcách thiết kếnhư trên, mỗi trường trong bảng trained_dataset đều gắn với một
ý nghĩa dễhiểu: hoặc mô tảquá trình học tập của sinh viên, hoặc mô tảđộkhó và mặt
bằng điểm của môn học, hoặc thểhiện mối liên hệgiữa các môn. Đồng thời, mọi giá trị
đã được xửlý đểkhông còn bịtrống, tạo nền tảng vững chắc cho bước huấn luyện và
đánh giá các mô hình dựđoán điểm môn học ởcác phần tiếp theo.
5.1.2
Xây dựng mô hình dựđoán kết quảmôn học
Mô hình được sửdụng là Ridge Regression [15], một dạng hồi quy tuyến tính có
thêm thành phần phạt L2 đểtránh overfitting.
Lý do lựa chọn
• Bài toán là hồi quy: dựđoán trực tiếp course_grade trên thang 10.
• Mô hình tuyến tính nên dễdiễn giải: có thểnhìn được hướng ảnh hưởng (tăng/giảm)
của từng đặc trưng.
• Sốlượng đặc trưng khá nhiều và có tương quan với nhau (điểm lịch sử, tỉlệrớt, xếp
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 84/254

=== SUMMARY ===
Đoạn văn bản mô tả các nhóm đặc trưng trong bảng dữ liệu trained_dataset bao gồm: độ khó môn học (dựa trên tỉ lệ rớt và phân bố điểm), các môn liên quan (tiên quyết, cùng nhóm kiến thức) và số lần học lại. Nội dung cũng giới thiệu việc sử dụng mô hình Ridge Regression để dự đoán điểm học phần trên thang 10. Mô hình này được chọn nhờ tính dễ diễn giải và khả năng sử dụng phạt L2 để ổn định mô hình khi có nhiều đặc trưng tương quan, giúp tránh hiện tượng quá khớp.

=== REVIEW QUESTIONS ===
1. Đặc trưng ''rank_course_difficulty'' được tính toán dựa trên cơ sở nào để xếp hạng độ khó của môn học?
2. Nhóm đặc trưng về các môn liên quan (relative group) giúp mô hình phản ánh điều gì về năng lực của sinh viên?
3. Tại sao Ridge Regression lại được lựa chọn thay vì các mô hình khác cho bài toán dự đoán điểm số này?
4. Thành phần phạt L2 trong mô hình Ridge Regression đóng vai trò gì trong việc xử lý các đặc trưng có tính tương quan cao?','bfd56b9f-7836-4cdf-8149-3ac7f8ad0528'::uuid,NULL,NULL,103,749,'2026-03-21 13:35:13.336962+07');
INSERT INTO coaching_chatbot.lecture_knowledge_chunks (lecture_knowledge_chunks_id,lecture_knowledge_id,chunk_index,chunk_content,qdrant_point_id,start_time_seconds,end_time_seconds,page_number,token_count,created_at) VALUES
	 ('afa85461-e2d7-4103-87c5-70a2042bb83d'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,103,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
hạng, ...), nên cần L2 giúp mô hình ổn định hơn.
Dạng toán học (tóm tắt)
Với vector đặc trưng x = (x1,...,xn), mô hình dựđoán:
ˆy = w⊤x+b.
Trong quá trình học, mô hình tìm bộtrọng số(w,b) sao cho sai sốbình phương giữa
ˆy và y là nhỏnhất, đồng thời tổng bình phương các trọng số∥w∥2
2 không quá lớn. Hệsố
điều chỉnh mức phạt L2 được chọn là α = 1.0.
Quy trình huấn luyện
Quy trình huấn luyện gồm ba bước chính:
1. Hoàn thiện dữliệu: sau các bước imputation ởphần trước, đảm bảo không còn giá
trịkhuyết trong trained_dataset.
2. Chuẩn hoá đặc trưng: các cột sốđược chuẩn hoá vềcùng thang đo (trung bình ≈0,
độlệch chuẩn ≈1) đểmô hình không bịlệch vềcác feature có đơn vịlớn [15].
3. Huấn luyện mô hình Ridge với α = 1.0 trên tập train, sau đó cốđịnh toàn bộtham
sốđểđánh giá trên tập test.
Chia tập Train/Test theo sinh viên
Dữliệu trong trained_dataset được chia theo student_id:
• Tập train: 15 482 bản ghi, tương ứng 388 sinh viên.
• Tập test: 3 894 bản ghi, tương ứng 97 sinh viên.
Mỗi sinh viên chỉxuất hiện ởmột trong hai tập (train hoặc test). Cách chia này mô
phỏng đúng bài toán thực tế: mô hình phải dựđoán cho các sinh viên mà nó chưa “thấy”
dữliệu trước đó.
Baseline đểso sánh
Baseline được định nghĩa rất đơn giản:
ˆybaseline = course_hist_median_smooth,
tức là luôn dựđoán điểm của sinh viên bằng trung vịlịch sửđã làm mượt của môn đó.
Baseline này đã tính tới độkhó môn học và mặt bằng điểm quá khứ, nên là một mốc so
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 85/254

=== SUMMARY ===
Đoạn văn bản mô tả việc xây dựng mô hình Ridge Regression để dự đoán kết quả học tập. Mô hình sử dụng hồi quy tuyến tính kết hợp thành phần phạt L2 để tránh overfitting và tăng tính ổn định. Quy trình bao gồm: hoàn thiện dữ liệu, chuẩn hóa đặc trưng và chia tập dữ liệu theo sinh viên (Train/Test) nhằm mô phỏng thực tế. Ngoài ra, một Baseline dựa trên trung vị điểm lịch sử của môn học được thiết lập làm tiêu chuẩn so sánh hiệu năng cho mô hình.

=== REVIEW QUESTIONS ===
1. Tại sao mô hình Ridge Regression lại cần sử dụng thành phần phạt L2 trong bài toán dự đoán điểm số này?
2. Hệ số điều chỉnh mức phạt L2 (α) được nhóm nghiên cứu lựa chọn là bao nhiêu trong quá trình huấn luyện?
3. Tại sao việc chia tập dữ liệu Train/Test theo student_id lại quan trọng đối với tính thực tế của mô hình?
4. Mô hình Baseline được định nghĩa như thế nào và nó đóng vai trò gì trong việc đánh giá kết quả?','c22e309e-75fa-48ac-829c-ab800d1c64ff'::uuid,NULL,NULL,104,630,'2026-03-21 13:35:13.336962+07'),
	 ('84b49b12-482a-4bcb-9d90-1fcf516dc354'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,104,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
sánh khá công bằng cho mô hình.
5.1.3
Kết quảvà thảo luận
Chỉsốđánh giá
Mô hình được đánh giá bằng ba thước đo quen thuộc trong bài toán hồi quy: [15]
• MAE (Mean Absolute Error): sai sốtuyệt đối trung bình giữa điểm dựđoán và
điểm thực tế.
MAE = 1
n
n
∑
i=1
|yi −ˆyi|
• RMSE (Root Mean Squared Error): phạt nặng hơn các sai sốlớn do bình phương
trước khi lấy trung bình.
RMSE =
s
1
n
n
∑
i=1
(yi −ˆyi)2
• R2: đo xem mô hình giải thích được bao nhiêu phần trăm biến thiên của course_grade
(giá trịcàng gần 1 càng tốt).
So sánh với baseline
Tập
Mô hình
MAE
RMSE
R2
Train
Baseline
1.3092
1.9614
0.0233
Train
Ridge
1.1533
1.7128
0.2552
Test
Baseline
1.3424
2.0498
0.0209
Test
Ridge
1.1729
1.7491
0.2871
Bảng 5.1: So sánh hiệu năng giữa baseline và mô hình Ridge (dựđoán trực tiếp
course_grade).
Có thểrút ra một sốnhận xét chính như sau:
• Trên tập train: So với baseline, mô hình Ridge giảm MAE khoảng 11.9% và giảm
RMSE khoảng 12.7%. R2 tăng từgần 0 (hầu như không giải thích được gì) lên
khoảng 0.26.
• Trên tập test: MAE giảm từkhoảng 1.34 điểm xuống còn khoảng 1.17 điểm (giảm
12.6%), RMSE giảm khoảng 14.7%, R2 ≈0.29, tức mô hình giải thích được gần
29% độbiến thiên điểm sốthực tếtrên các sinh viên chưa từng xuất hiện trong quá
trình huấn luyện.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 86/254

=== SUMMARY ===
Phần này trình bày kết quả đánh giá mô hình Ridge Regression trong việc dự đoán điểm môn học. Sử dụng các chỉ số MAE, RMSE và R2, nhóm nghiên cứu so sánh mô hình với phương pháp baseline (trung vị lịch sử). Kết quả thực nghiệm cho thấy mô hình Ridge vượt trội hơn baseline trên cả tập train và test. Cụ thể, trên tập test, mô hình giảm sai số MAE xuống 1.17 điểm và giải thích được khoảng 29% sự biến thiên điểm số, chứng minh khả năng khái quát hóa tốt cho sinh viên mới.

=== REVIEW QUESTIONS ===
1. Ba chỉ số chính được sử dụng để đánh giá hiệu năng của mô hình dự đoán trong báo cáo này là gì?
2. Sự khác biệt cơ bản giữa cách tính MAE và RMSE là gì và tại sao RMSE lại được sử dụng?
3. Dựa trên Bảng 5.1, mô hình Ridge đã cải thiện bao nhiêu phần trăm MAE trên tập test so với baseline?
4. Chỉ số R2 xấp xỉ 0.29 trên tập test có ý nghĩa gì về khả năng giải thích dữ liệu của mô hình?','c7d3f924-f423-47c9-b673-b672e1bd211e'::uuid,NULL,NULL,105,583,'2026-03-21 13:35:13.336962+07'),
	 ('8022fa2d-fc67-4f2c-98f0-8b4d95ec410d'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,105,'=== ORIGINAL CONTENT ===
Nói ngắn gọn, nếu chỉ dựa vào baseline “điểm bằng trung vị lịch sử của môn” thì sai số trung bình khoảng 1.34 điểm (thang 10). Khi bổ sung thêm thông tin về lịch sử học tập của sinh viên, độ khó môn học và kết quả ở các môn liên quan, mô hình Ridge đưa sai số xuống khoảng 1.17 điểm và vẫn giữ được khả năng khái quát hoá tốt sang sinh viên mới.
5.1.4
Ước lượng phân phối xác suất điểm trên thang 4
Các kết quả trên mới dừng lại ở việc cung cấp một điểm dự đoán cho từng lần sinh viên học một môn (thang 4) và các chỉ số sai số tổng quát. Để phục vụ các chức năng phân tích và đánh giá mục tiêu học tập, nhóm cần tiến thêm một bước: biến điểm dự đoán đơn lẻ thành một phân phối xác suất của điểm thật.
Giả thiết về phân phối sai số
Hình 5.1: Phân phối sai số dự đoán trên thang 4 của mô hình Ridge (tập test).
Từ Hình 5.1, ta định nghĩa sai số dự đoán trên thang 4 là
R = ˆg4 − g4,

=== SUMMARY ===
Đoạn văn bản so sánh hiệu quả của mô hình Ridge với phương pháp baseline trong dự đoán điểm số, cho thấy sai số giảm từ 1.34 xuống 1.17 trên thang điểm 10 nhờ tích hợp lịch sử học tập và độ khó môn học. Nội dung cũng giới thiệu bước tiến quan trọng là chuyển từ dự đoán điểm đơn lẻ sang ước lượng phân phối xác suất trên thang điểm 4.0. Qua đó, sai số dự đoán R được định nghĩa là hiệu số giữa điểm dự đoán và điểm thực tế.

=== REVIEW QUESTIONS ===
1. So với phương pháp baseline dựa trên trung vị lịch sử, mô hình Ridge đã cải thiện sai số trung bình như thế nào?
2. Những nguồn thông tin bổ sung nào đã giúp mô hình Ridge đạt được kết quả dự đoán chính xác hơn?
3. Tại sao nhóm nghiên cứu cần chuyển đổi từ một điểm dự đoán đơn lẻ sang phân phối xác suất của điểm thật?
4. Trong bài toán ước lượng trên thang điểm 4, sai số dự đoán R được tính toán dựa trên công thức nào?','c094089e-4091-48e0-8f9e-b0db0b6604e7'::uuid,NULL,NULL,106,449,'2026-03-21 13:35:13.336962+07'),
	 ('4bcbdd9a-89c4-44ae-90ce-a964500e0b83'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,106,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
, trong đó ˆg4 là điểm dựđoán và g4 là điểm thực tế.
Các cột màu xanh là histogram (chuẩn hoá theo mật độ) của sai sốR, phản ánh phân
phối sai sốquan sát được từdữliệu.
Đường cong màu vàng/cam xuất hiện vì nhóm chủđộng vẽchồng lên histogram một
đường cong chuẩn đểso sánh. Cụthể, từtập sai sốR trên test, nhóm tính được hai tham
sốµR (trung bình) và σR (độlệch chuẩn), sau đó tính mật độxác suất của phân phối
chuẩn N(µR,σ2
R) và vẽlên cùng trục với histogram. Mục đích của đường cong này là
đánh giá mức độphù hợp của giảthiết “sai sốR có thểđược xấp xỉbởi một phân phối
chuẩn”. Nếu đường cong chuẩn khớp tốt với histogram quan sát được, ta có thểsửdụng
N(µR,σ2
R) như một mô hình xác suất cho sai số, từđó suy ra phân phối của g4 và tính
các xác suất vượt ngưỡng ởphần sau.
Quan sát histogram ởHình 5.1 cho thấy các giá trịR tập trung quanh một giá trịtrung
tâm, và hình dạng phân bốcó dạng giống phân phối chuẩn. Vì vậy, đểmô tảbất định của
dựđoán một cách đơn giản và thuận tiện cho việc tính xác suất, nhóm xem phân phối
thực nghiệm của sai sốR gần đúng bằng một phân phối chuẩn với các tham sốước
lượng trực tiếp từdữliệu:
µR ≈−0.10,
σR ≈0.92.
Vì vậy, nhóm xấp xỉphân phối thực nghiệm của R bằng một phân phối chuẩn:
R ≈N(µR,σ2
R).
Từdựđoán điểm đến phân phối điểm thật
Mô hình Ridge ởtrên được huấn luyện trực tiếp trên điểm course_grade (thang 10)
và cho ra dựđoán ˆy trên thang 10. Đểphù hợp với cách tính GPA của trường, cảđiểm
thật y và điểm dựđoán ˆy đều được quy đổi sang thang 4.0.
Với một quan sát mới (một sinh viên, một môn, một học kỳ), mô hình cho ta một điểm
dựđoán trên thang 4 là ˆg4. Điểm thật g4 được xem như một biến ngẫu nhiên, và sai số
dựđoán trên thang 4 được định nghĩa là
R = ˆg4 −g4.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 88/254

=== SUMMARY ===
Đoạn văn bản trình bày phương pháp ước lượng phân phối xác suất của điểm thực tế từ kết quả dự đoán của mô hình Ridge. Bằng cách định nghĩa sai số R trên thang điểm 4 và kiểm chứng qua histogram, nhóm nghiên cứu đã xấp xỉ sai số này bằng một phân phối chuẩn với các tham số cụ thể (trung bình xấp xỉ -0.10 và độ lệch chuẩn xấp xỉ 0.92). Việc mô hình hóa sai số giúp chuyển đổi dự đoán điểm số đơn lẻ thành một phân phối xác suất để tính toán khả năng đạt mục tiêu học tập.

=== REVIEW QUESTIONS ===
1. Sai số dự đoán R trên thang điểm 4 được định nghĩa bằng công thức nào?
2. Mục đích của việc vẽ đường cong phân phối chuẩn chồng lên histogram của sai số R là gì?
3. Các tham số trung bình và độ lệch chuẩn của sai số R được ước lượng cụ thể là bao nhiêu trong thực nghiệm?
4. Tại sao dự đoán từ mô hình Ridge cần được quy đổi sang thang điểm 4.0?','66c8cda6-5a4f-4653-8ac6-0a0ce001bad8'::uuid,NULL,NULL,107,689,'2026-03-21 13:35:13.337961+07'),
	 ('a86bd921-9e6d-4a87-bf4e-5afaf4ff2e89'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,107,'=== ORIGINAL CONTENT ===
Từphân tích thống kê ởtrên, sai sốR có phân phối xấp xỉchuẩn với trung bình µR và độ
lệch chuẩn σR:
R ∼N(µR,σ2
R).
Do đó, ta suy ra
g4 = ˆg4 −R ∼N( ˆg4 −µR, σ2
R).
Trong thực nghiệm, µR có giá trịnhỏ(ví dụkhoảng −0,10), nghĩa là mô hình có xu
hướng dựđoán thấp hơn điểm thật một chút (underestimate). Nếu muốn loại bỏđộlệch
hệthống nhỏnày, ta dịch toàn bộdựđoán lên lại một lượng µR và định nghĩa điểm dự
đoán đã hiệu chỉnh:
˜g4 = ˆg4 + µR.
Khi đó có thểcoi:
g4 ∼N( ˜g4, σ2
R),
Trong đánh giá tính khảthi mục tiêu GPA ởphần sau, ta quan tâm đến xác suất điểm
thật vượt qua một ngưỡng t nào đó trên thang 4. Với xấp xỉchuẩn ởtrên, xác suất này
được tính bằng:
P(g4 ≥t) = 1−Φ
 t −µg4
σR

,
trong đó µg4 = ˜g4 và Φ(·) là hàm phân phối tích luỹcủa chuẩn hoá N(0,1).
5.1.5
Kết luận
Mô hình dựđoán kết quảmôn học theo năng lực ởtrên có thểxem như một mảnh
ghép cốt lõi trong hệthống phân tích học tập của sinh viên. Mặc dù chỉsửdụng một
mô hình tuyến tính đơn giản (Ridge Regression), kết quảcho thấy mô hình đã giúp giảm
dáng kểsai sốdựđoán so với baseline và giải thích được một phần đáng kểđộbiến thiên
điểm sốgiữa các sinh viên và các môn học khác nhau.
Quan trọng hơn, cách xây dựng dữliệu và đặc trưng của mô hình này đã tách bạch
được hai yếu tố: độkhó nội tại của môn học và năng lực cá nhân của sinh viên theo thời
gian. Điều này tạo ra một thước đo năng lực tương đối ổn định, có thểdùng làm nền tảng
đểphát triển các chức năng cá nhân hóa trong hệthống hỗtrợhọc tập, ví dụnhư:

=== SUMMARY ===
Đoạn văn trình bày phương pháp sử dụng phân phối chuẩn để ước lượng xác suất điểm số thực tế từ dự báo của mô hình Ridge. Nhóm nghiên cứu thực hiện hiệu chỉnh sai số hệ thống để tính toán xác suất sinh viên đạt điểm mục tiêu vượt ngưỡng t. Kết luận khẳng định mô hình Ridge giúp giảm sai số so với baseline, đồng thời tách biệt được độ khó môn học và năng lực cá nhân, tạo nền tảng cho việc cá nhân hóa lộ trình học tập và đánh giá mục tiêu.

=== REVIEW QUESTIONS ===
1. Tại sao cần phải định nghĩa điểm dự đoán đã hiệu chỉnh g̃4 từ điểm dự đoán ban đầu ĝ4?
2. Công thức tính xác suất để điểm thực tế g4 lớn hơn hoặc bằng một ngưỡng t dựa trên hàm phân phối tích lũy Φ là gì?
3. Mô hình Ridge Regression mang lại ưu điểm gì so với phương pháp baseline trong việc dự đoán kết quả môn học?
4. Việc tách bạch được độ khó môn học và năng lực cá nhân sinh viên có ý nghĩa gì đối với hệ thống hỗ trợ học tập?','90d5fe0b-418f-457e-afa7-26be2374f82f'::uuid,NULL,NULL,108,607,'2026-03-21 13:35:13.337961+07'),
	 ('7b52b3c6-6630-43cc-ad8d-2ad790bec9af'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,108,'=== ORIGINAL CONTENT ===
• Ước lượng trước khảnăng đạt điểm mục tiêu của sinh viên cho từng môn, từng học kỳdựa trên lịch sửhọc tập và lộtrình đăng ký dựkiến
• Giúp sinh viên đánh giá tính khảthi của các mục tiêu (GPA mong muốn, sốtín chỉđăng ký trong mỗi kỳ, kếhoạch tốt nghiệp đúng hạn, . . . )
• Đềxuất lộtrình học tập phù hợp hơn, gợi ý điều chỉnh sốtín chỉ, thứtựhọc các môn hoặc lựa chọn môn tựchọn đểgiảm rủi ro rớt môn và quá tải.
5.2
Thuật toán đánh giá tính khảthi của mục tiêu học tập
Trong phần này, chúng em trình bày một thuật toán dùng đểđánh giá mức độkhảthi của mục tiêu học tập cá nhân mà sinh viên đã thiết lập trong UC-02. Thuật toán chỉtrảlời câu hỏi: “Mục tiêu này có thực tếhay không, với tình trạng hiện tại của sinh viên?” và phân loại kết quảthành ba mức: Yếu, Trung bình hoặc Tốt.
Thuật toán không tựsinh ra kếhoạch đăng ký môn chi tiết. Nó đóng vai trò như một lớp “tiền kiểm tra” trước khi hệthống sinh lộtrình học cá nhân (UC-03). Nếu mục tiêu bịđánh giá là quá rủi ro (mức Yếu), sinh viên được khuyến nghịđiều chỉnh ngay từbước này.
Dữliệu đầu vào và đầu ra
Thuật toán sửdụng ba nhóm thông tin đầu vào:
• Chương trình đào tạo và quy định:
– Tổng sốtín chỉcần tích lũy đểtốt nghiệp theo chuyên ngành
– Danh sách môn bắt buộc, tựchọn, các điều kiện tiên quyết
– Giới hạn tối đa vềthời gian đào tạo (ví dụ: 6 năm, tương ứng khoảng 12 học kỳchính, kèm các học kỳhè với giới hạn tín chỉriêng)
• Tình trạng sinh viên hiện tại:
– Sốtín chỉđã tích lũy, sốtín chỉcòn thiếu
– GPA hiện tại (thang 4)
– Kết quảcác môn đã học

=== SUMMARY ===
Phân đoạn này giới thiệu về thuật toán đánh giá tính khả thi của mục tiêu học tập cá nhân (UC-02) dành cho sinh viên. Thuật toán hoạt động như một lớp "tiền kiểm tra" trước khi xây dựng lộ trình chi tiết, giúp xác định xem mục tiêu của sinh viên có thực tế hay không dựa trên ba mức độ: Yếu, Trung bình, và Tốt. Quá trình đánh giá căn cứ vào dữ liệu về chương trình đào tạo, quy định thời gian và tình trạng học tập thực tế của sinh viên như GPA và số tín chỉ tích lũy.

=== REVIEW QUESTIONS ===
1. Mục tiêu chính của thuật toán đánh giá tính khả thi trong học tập là gì?
2. Thuật toán phân loại mức độ khả thi của mục tiêu học tập thành những cấp độ nào?
3. Tại sao thuật toán này được gọi là lớp ''tiền kiểm tra'' trước bước sinh lộ trình cá nhân (UC-03)?
4. Những nhóm thông tin đầu vào nào được sử dụng để đánh giá tính thực tế của mục tiêu học tập?','7bc33c04-5904-4480-8e87-c7a9bc002bcb'::uuid,NULL,NULL,109,603,'2026-03-21 13:35:13.337961+07'),
	 ('4178af57-944e-40ed-89e3-bacf85bbde03'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,109,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
– Trạng thái các chứng chỉbắt buộc (ngoại ngữ, tin học, . . . ): đã đạt hay chưa, và
nếu chưa thì đã có kỳdựkiến hoàn thành hay chưa
• Mục tiêu cá nhân:
– Chuyên ngành lựa chọn trong CTĐT hiện hành
– GPA mục tiêu Gtarget (thang 4)
– Thời điểm tốt nghiệp dựkiến Tgrad (ví dụ: HK1–2027), dùng đểxác định sốhọc
kỳchính và các học kỳhè còn lại mà vẫn nằm trong giới hạn 6 năm
– Cường độhọc tập các học kỳchính: mô tảsốtín chỉsinh viên dựkiến đăng ký
trong mỗi học kỳchính. Hệthống chia thành bốn mức:
Nhẹ, Trung bình, Khá, Nặng.
Mỗi mức tương ứng với một khoảng sốtín chỉ[TCmin
chính,TCmax
chính]. Ví dụcó thể
quy ước:
Nhẹ:
x ≤9,
Trung bình:
9 < x ≤13,
Khá:
13 < x ≤17,
Nặng:
17 < x ≤22,
trong đó x là sốtín chỉcủa một học kỳchính. Khi đã biết mức cường độ, hệ
thống suy ra TCmin
chính và TCmax
chính.
– Sốhọc kỳhè dựkiến sửdụng Skỳhè: là sốhọc kỳhè sinh viên dựđịnh học trong
phần thời gian còn lại, là sốnguyên thỏa
0 ≤Skỳhè ≤Smax
kỳhè,
trong đó Smax
kỳhè do hệthống tính dựa trên Tgrad và giới hạn 6 năm đào tạo.
– Cường độhọc tập các học kỳhè: mô tảsốtín chỉsinh viên dựkiến đăng ký
trong mỗi học kỳhè. Hệthống chia thành ba mức:
Thấp, Trung bình, Cao.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 91/254

=== SUMMARY ===
Đoạn văn bản mô tả các nhóm dữ liệu đầu vào cần thiết cho thuật toán đánh giá tính khả thi của mục tiêu học tập. Các thông tin bao gồm tình trạng chứng chỉ bắt buộc, các mục tiêu cá nhân về GPA và thời điểm tốt nghiệp, cùng với cường độ học tập dự kiến (số tín chỉ) trong các học kỳ chính và học kỳ hè. Hệ thống sử dụng các mức phân loại này để tính toán khả năng hoàn thành chương trình trong giới hạn thời gian đào tạo 6 năm.

=== REVIEW QUESTIONS ===
1. Hệ thống phân loại cường độ học tập trong các học kỳ chính thành những mức nào và số tín chỉ tương ứng cho mỗi mức?
2. Thông tin về thời điểm tốt nghiệp dự kiến (Tgrad) đóng vai trò gì trong việc tính toán của thuật toán?
3. Điều kiện ràng buộc đối với số học kỳ hè dự kiến sử dụng (Skỳhè) là gì?
4. Các mức độ cường độ học tập cho học kỳ hè được chia thành bao nhiêu mức?','5ee16315-25fc-44e5-bf51-f5bd11e2ac47'::uuid,NULL,NULL,110,544,'2026-03-21 13:35:13.337961+07'),
	 ('c20631cd-e8ff-44b6-8dc4-3777ed8ba8a9'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,110,'=== ORIGINAL CONTENT ===
Mỗi mức tương ứng với một khoảng sốtín chỉ[TCmin
hè ,TCmax
hè ]. Ví dụ:
Thấp:
x ≤4,
Trung bình:
4 < x ≤7,
Cao:
7 < x ≤11,
trong đó x là sốtín chỉcủa một học kỳhè. Nếu Skỳhè = 0 thì tham sốcường độ
hè không dùng đến.
– Thứtựưu tiên học tập: một hoán vịcủa bộ{GPA, Kiến thức, Thời gian}, thể
hiện sinh viên ưu tiên điều gì hơn: đạt GPA cao, học sâu vềkiến thức hay tốt
nghiệp đúng hạn. Thứtựnày sẽđược dùng ởbước sinh gợi ý điều chỉnh.
Đầu ra của thuật toán gồm:
• Mức độkhảthi của mục tiêu: Yếu / Trung bình / Tốt
• Một sốgợi ý điều chỉnh đơn giản (kéo dài thời gian, giảm GPA, tăng/giảm tải từng
kỳ, ...)
• Một chỉbáo định lượng vềmức nỗlực cần có so với mặt bằng chung của sinh viên
cùng khóa.
Bước 1: Kiểm tra sơ bộtính khảthi
Bước đầu tiên, thuật toán thực hiện một loạt kiểm tra nhanh những điều kiện cơ bản
nhất đểloại bỏcác mục tiêu rõ ràng bất khảthi. Nếu mục tiêu vi phạm những điều kiện
"cứng"này, hệthống sẽkết luận ngay là Yếu (không khảthi) và đềnghịsinh viên điều
chỉnh mục tiêu trước khi đi xa hơn. Các kiểm tra sơ bộgồm:
(1) Kiểm tra khối lượng tín chỉvà thời gian
Bước đầu tiên, thuật toán kiểm tra xem với sốtín chỉcòn lại và thời gian học còn lại
thì kếhoạch tải học mà sinh viên đã đặt ra có đủđểhoàn thành chương trình hay không.
Dựa trên chương trình đào tạo và kết quảhiện tại, hệthống tính được tổng sốtín chỉ
sinh viên còn thiếu đểđủđiều kiện tốt nghiệp theo chuyên ngành đã chọn, ký hiệu là
TCcòn lại. Đây là sốtín chỉcủa tất cảcác môn chưa học hoặc đã học nhưng chưa đạt.
Tiếp theo, từthời điểm hiện tại đến mốc tốt nghiệp dựkiến Tgrad, hệthống xác định:
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 92/254

=== SUMMARY ===
Đoạn trích trình bày các thông số đầu vào và quy trình kiểm tra sơ bộ của thuật toán đánh giá tính khả thi mục tiêu học tập. Nội dung bao gồm định nghĩa mức tải học kỳ hè, xác định thứ tự ưu tiên (GPA, Kiến thức, Thời gian) và phân loại kết quả đầu ra (Yếu/Trung bình/Tốt). Bước quan trọng nhất là kiểm tra liệu số tín chỉ còn lại (TCcòn lại) có thể hoàn thành trong quỹ thời gian dự kiến (Tgrad) dựa trên kế hoạch đăng ký của sinh viên hay không.

=== REVIEW QUESTIONS ===
1. Các mức cường độ học tập trong học kỳ hè (Thấp, Trung bình, Cao) được quy định cụ thể theo khoảng số tín chỉ như thế nào?
2. Thứ tự ưu tiên học tập giữa các yếu tố GPA, Kiến thức và Thời gian được hệ thống sử dụng vào mục đích gì?
3. Đầu ra của thuật toán đánh giá tính khả thi bao gồm những thông tin định lượng và định tính nào?
4. Trong bước kiểm tra sơ bộ, điều kiện ''cứng'' nào khiến hệ thống kết luận ngay mục tiêu của sinh viên ở mức ''Yếu''?','c25bf91c-14c4-4635-9ab8-ea7680230bc7'::uuid,NULL,NULL,111,653,'2026-03-21 13:35:13.338962+07'),
	 ('63a63150-7499-48b8-8c25-b211cfe9c863'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,111,'=== ORIGINAL CONTENT ===
• Sốhọc kỳchính còn lại Skỳchính mà sinh viên sẽtrải qua (đảm bảo tổng thời gian
đào tạo không vượt quá giới hạn 6 năm).
• Sốhọc kỳhè tối đa có thểhọc Smax
kỳhè trong quãng thời gian đó. Đây là sốkỳhè tối
đa mà sinh viên có thểtận dụng từbây giờđến Tgrad nếu muốn, vẫn nằm trong giới
hạn 6 năm đào tạo.
Từmục tiêu cá nhân (UC-02), thuật toán nhận thêm thông tin vềkếhoạch tải học:
• Mức tải các học kỳchính: cho biết sinh viên dựkiến đăng ký khoảng bao nhiêu
tín chỉmỗi học kỳchính (Nhẹ/ Trung bình / Khá / Nặng). Mỗi mức tương ứng với
một sốtín chỉtối đa mỗi học kỳchính, ký hiệu TCmax
chính. Ví dụ, mức Nặng có thể
tương ứng với việc sẵn sàng học tới khoảng 22 tín chỉ/kỳ.
• Sốhọc kỳhè sẽhọc Skỳhè: là sốkỳhè mà sinh viên thật sựcó ý định học, với điều
kiện:
0 ≤Skỳhè ≤Smax
kỳhè.
Nếu Skỳhè = 0 nghĩa là sinh viên không dựđịnh học kỳhè nào.
• Mức tải các học kỳhè: cho biết sinh viên dựkiến đăng ký khoảng bao nhiêu tín
chỉtrong mỗi kỳhè (Thấp / Trung bình / Cao). Mỗi mức tương ứng với một sốtín
chỉtối đa mỗi kỳhè, ký hiệu TCmax
hè . Ví dụ, mức Cao có thểtương ứng với việc học
tới khoảng 11 tín chỉtrong một kỳhè.
Dựa trên các thông tin này, thuật toán tính tổng sốtín chỉtối đa mà sinh viên có thể
tích lũy từbây giờđến khi tốt nghiệp, theo đúng kếhoạch đã khai báo:
TCtối đa = Skỳchính ·TCmax
chính +Skỳhè ·TCmax
hè ,
trong đó:
• TCmax
chính là sốtín chỉtối đa mỗi học kỳchính (tùy theo mức tải học kỳchính mà sinh
viên đã chọn),
• TCmax
hè
là sốtín chỉtối đa mỗi học kỳhè (tùy theo mức tải hè mà sinh viên đã chọn,
nếu có học hè).
Điều kiện bất khảthi (phân loại Yếu).
Nếu sốtín chỉcần học còn lại lớn hơn tổng sốtín chỉtối đa có thểtích lũy theo kế
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 93/254

=== SUMMARY ===
Đoạn văn bản mô tả phương pháp thuật toán sử dụng để tính toán tổng số tín chỉ tối đa (TCtối đa) mà sinh viên có thể tích lũy từ thời điểm hiện tại đến khi tốt nghiệp. Việc tính toán dựa trên số học kỳ chính, số học kỳ hè dự kiến và mức độ tải học tập (Nhẹ đến Nặng) mà sinh viên lựa chọn. Nếu tổng số tín chỉ còn thiếu vượt quá khả năng tích lũy tối đa này, kế hoạch học tập sẽ bị đánh giá là không khả thi (loại Yếu).

=== REVIEW QUESTIONS ===
1. Công thức tính tổng số tín chỉ tối đa (TCtối đa) mà sinh viên có thể tích lũy được xác định như thế nào?
2. Mức tải học tập ''Nặng'' trong học kỳ chính thường tương ứng với tối đa khoảng bao nhiêu tín chỉ?
3. Điều kiện nào khiến thuật toán phân loại mục tiêu học tập của sinh viên vào mức ''Yếu''?
4. Sự khác biệt giữa số học kỳ hè tối đa (Smax kỳ hè) và số học kỳ hè thực tế sẽ học (Skỳ hè) là gì?','131b1c0e-f6de-44af-bc5b-b98ffb959791'::uuid,NULL,NULL,112,652,'2026-03-21 13:35:13.338962+07'),
	 ('4651860e-3466-426e-96d4-4a53be0f57a4'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,112,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
hoạch, tức là:
TCcòn lại > TCtối đa
thì với cường độhọc tập và quỹthời gian như hiện tại, sinh viên không thểhoàn thành
đủtín chỉđểtốt nghiệp đúng hạn.
Nói cách khác, ngay cảkhi:
• mỗi học kỳchính đều đăng ký ởmức tín chỉcao nhất theo mức tải đã chọn, và
• mỗi học kỳhè (trong Skỳhè kỳhè dựđịnh học) cũng đăng ký ởmức tín chỉcao nhất
tương ứng,
thì tổng sốtín chỉtích lũy được vẫn không đủso với TCcòn lại mà chương trình yêu cầu.
Trong trường hợp này, thuật toán kết luận mục tiêu là không khảthi vềmặt tín chỉ
và thời gian, và xếp vào mức Yếu ngay tại bước này.
Hành động đềxuất. Khi rơi vào trường hợp trên, hệthống gợi ý sinh viên điều chỉnh
lại kếhoạch, chẳng hạn:
• Kéo dài thêm thời gian học: lùi thời điểm tốt nghiệp dựkiến, đồng nghĩa với việc
tăng thêm Skỳchính hoặc tận dụng thêm các kỳhè (nếu vẫn trong giới hạn 6 năm).
• Tăng khối lượng học mỗi kỳ: chọn mức tải cao hơn cho các học kỳchính và/hoặc
các kỳhè (ví dụchuyển từmức Trung bình sang Khá hoặc Nặng), miễn là vẫn nằm
trong giới hạn tín chỉtối đa cho phép của trường.
• Kết hợp cảhai: vừa kéo dài thời gian, vừa tăng sốtín chỉmỗi kỳđểđạt được sự
cân bằng giữa áp lực học tập và tiến độtốt nghiệp.
Sau khi điều chỉnh, sinh viên có thểchạy lại thuật toán đểkiểm tra. Chỉkhi điều kiện
TCcòn lại ≤TCtối đa
được thỏa mãn, mục tiêu mới được xem là vượt qua bước kiểm tra tín chỉvà thời gian và
chuyển sang các bước đánh giá tiếp theo (GPA mục tiêu, mô hình dựđoán điểm, . ..).
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 94/254

=== SUMMARY ===
Đoạn văn bản mô tả bước kiểm tra tính khả thi về khối lượng tín chỉ và thời gian trong lộ trình học tập của sinh viên. Nếu số tín chỉ còn thiếu vượt quá khả năng tích lũy tối đa theo kế hoạch, mục tiêu bị xếp loại ''Yếu''. Hệ thống sẽ đề xuất sinh viên kéo dài thời gian học, tăng tải trọng tín chỉ mỗi kỳ hoặc cả hai. Đây là điều kiện tiên quyết cần được thỏa mãn trước khi thuật toán chuyển sang đánh giá các tiêu chí như GPA.

=== REVIEW QUESTIONS ===
1. Điều kiện toán học nào dẫn đến việc thuật toán kết luận mục tiêu của sinh viên là không khả thi (mức Yếu)?
2. Theo văn bản, ''TCtối đa'' được hiểu và tính toán dựa trên những giả định gì về việc đăng ký tín chỉ của sinh viên?
3. Hệ thống gợi ý những hành động cụ thể nào để sinh viên điều chỉnh kế hoạch học tập khi không đủ thời gian tốt nghiệp dự kiến?
4. Tại sao sinh viên cần phải thỏa mãn điều kiện TCcòn lại ≤ TCtối đa trước khi hệ thống đánh giá GPA mục tiêu?','97360a78-40e7-4447-b854-8e60c5dc7bca'::uuid,NULL,NULL,113,638,'2026-03-21 13:35:13.338962+07');
INSERT INTO coaching_chatbot.lecture_knowledge_chunks (lecture_knowledge_chunks_id,lecture_knowledge_id,chunk_index,chunk_content,qdrant_point_id,start_time_seconds,end_time_seconds,page_number,token_count,created_at) VALUES
	 ('88f59de2-2975-481b-a48f-ced8c6b463ee'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,113,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
(2) Kiểm tra GPA mục tiêu và điểm trung bình cần đạt
Sau khi kiểm tra vềtín chỉvà thời gian, thuật toán xét tiếp đến mục tiêu GPA đểxem
liệu mức điểm mà sinh viên đặt ra có thực tếvới tình trạng hiện tại hay không.
Giảsử:
• tổng sốtín chỉyêu cầu đểtốt nghiệp là TCtốt nghiệp,
• sốtín chỉđã tích lũy (đã hoàn thành) là TCtích lũy,
• GPA hiện tại là Ghiện tại,
• GPA mục tiêu là Gmục tiêu,
• sốtín chỉcòn lại là TCcòn lại (thông thường TCcòn lại = TCtốt nghiệp −TCtích lũy sau
khi trừđi các môn đã đạt).
Đểkhi tốt nghiệp GPA cuối cùng đúng bằng Gmục tiêu, phần tín chỉcòn lại phải đạt
một GPA trung bình tối thiểu Gcần đạt, được tính gần đúng bởi công thức:
Gcần =
Gmục tiêu ·TCtốt nghiệp −Ghiện tại ·TCtích lũy
TCcòn lại
.
Cách hiểu đơn giản: công thức này trảlời câu hỏi “Từbây giờđến lúc tốt nghiệp,
trung bình mỗi tín chỉcòn lại phải đạt GPA bao nhiêu (thang 4) đểkéo GPA chung từ
Ghiện tại lên Gmục tiêu?”.
Thuật toán kiểm tra giá trịGcần đạt như sau:
• Nếu Gcần đạt > 4,0 (vượt quá thang điểm 4) thì có nghĩa là, dù ởtất cảcác môn còn
lại sinh viên đều đạt điểm tối đa, vẫn không thểnâng GPA lên tới mức mục tiêu.
Đây là yêu cầu không thểxảy ra trên thang điểm 4. Trong trường hợp này, nếu mục
tiêu chưa bịloại ởbước (1), thuật toán sẽđánh giá mục tiêu GPA là không khảthi
và xếp mục tiêu vào mức Yếu.
• Nếu Gcần đạt ≤4,0, tức là vẫn nằm trong giới hạn của thang điểm 4, thuật toán coi
mục tiêu GPA không bịloại bởi bước kiểm tra này và cho phép chuyển sang các
bước đánh giá tiếp theo.
Lưu ý: với sinh viên năm nhất hoặc mới chỉhọc rất ít tín chỉ(khi TCtích lũy còn rất
nhỏ), giá trịGcần đạt sẽgần với chính Gmục tiêu, vì gần như toàn bộđiểm sốđều nằm ởcác
môn sẽhọc trong tương lai.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 95/254

=== SUMMARY ===
Nội dung này trình bày phương pháp kiểm tra tính khả thi của mục tiêu điểm trung bình tích lũy (GPA). Thuật toán sử dụng công thức tính mức điểm trung bình tối thiểu cần đạt (Gcần) cho số tín chỉ còn lại dựa trên GPA hiện tại và GPA mục tiêu. Nếu Gcần lớn hơn 4.0, mục tiêu bị đánh giá là không khả thi (loại Yếu). Ngược lại, nếu Gcần nằm trong thang điểm cho phép, sinh viên có thể tiếp tục thực hiện kế hoạch học tập.

=== REVIEW QUESTIONS ===
1. Công thức tính điểm trung bình cần đạt (Gcần) phụ thuộc vào những biến số nào?
2. Tại sao thuật toán lại xếp loại ''Yếu'' nếu giá trị Gcần tính toán được lớn hơn 4.0?
3. Đối với sinh viên năm nhất, tại sao giá trị Gcần lại thường xấp xỉ với GPA mục tiêu?
4. Mục đích của việc tính toán Gcần trong quy trình quản lý lộ trình học tập của sinh viên là gì?','30845c74-168e-46a7-8ca4-084b1986ebd7'::uuid,NULL,NULL,114,670,'2026-03-21 13:35:13.338962+07'),
	 ('abd77a5d-d525-4598-bfd9-2fdb489c000d'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,114,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
(3) Kiểm tra tiến độtối thiểu theo chuỗi tiên quyết
Bên cạnh khối lượng tín chỉ, thuật toán xét thêm trình tựtiên quyết của các môn học
còn lại đểđảm bảo rằng quỹthời gian từnay đến tốt nghiệp không bịthiếu do ràng buộc
môn học nối tiếp. Dựa trên danh sách môn học chưa hoàn thành và các điều kiện tiên
quyết của chúng, hệthống xác định:
• Độdài chuỗi tiên quyết dài nhất – ký hiệu Lchuỗi: sốmôn học nhiều nhất nối tiếp
nhau theo quan hệtiên quyết mà sinh viên còn phải hoàn thành. Nói cách khác, đây
là sốhọc phần liên tục tối thiểu mà sinh viên cần học lần lượt qua từng kỳđểhoàn
thành toàn bộchương trình, do bịràng buộc bởi các môn học kếtiếp nhau (môn sau
đòi hỏi hoàn thành môn trước).
• Tổng sốhọc kỳcòn lại Scòn lại: gồm toàn bộcác học kỳchính và học kỳhè mà sinh
viên dựkiến sẽtheo học từhiện tại đến Tgrad (bằng Skỳchính +Skỳhè theo kếhoạch
đã chọn).
Dễthấy rằng, ngay cảtrong trường hợp sinh viên sắp xếp lộtrình một cách tối ưu (học
tất cảnhững môn có thểsong song, chỉtrì hoãn những môn thực sựphải học nối tiếp),
thì sốhọc kỳtối thiểu cần thiết đểhoàn thành chương trình cũng chính là Lchuỗi. Nếu:
Lchuỗi > Scòn lại
thì có nghĩa là với quỹthời gian hiện tại, sinh viên không đủsốhọc kỳđểhoàn thành
toàn bộchuỗi môn học dài nhất theo yêu cầu tiên quyết. Nói cách khác, tồn tại ít nhất
một nhóm môn học mà dù sinh viên có học liên tục từng kỳ, vẫn cần nhiều học kỳhơn
so với kếhoạch tốt nghiệp hiện tại.
Trong trường hợp này, mục tiêu bịcoi là không khảthi vềmặt tiến độvà thuật toán
xếp loại Yếu ngay từbước kiểm tra sơ bộ.
Hành động đềxuất. Khi rơi vào tình huống trên, hệthống gợi ý sinh viên điều chỉnh
kếhoạch, ví dụ:
• Kéo dài thời gian học: lùi thời điểm tốt nghiệp dựkiến thêm ít nhất một học kỳ
(hoặc nhiều hơn tuỳtheo mức độthiếu hụt), đểcó đủsốhọc kỳthực hiện tuần tự
các môn học trong chuỗi tiên quyết.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 96/254

=== SUMMARY ===
Đoạn văn bản trình bày bước kiểm tra tính khả thi của kế hoạch học tập dựa trên chuỗi môn học tiên quyết. Thuật toán so sánh độ dài chuỗi tiên quyết dài nhất (Lchuỗi) với tổng số học kỳ còn lại (Scòn lại). Nếu Lchuỗi lớn hơn Scòn lại, sinh viên sẽ không đủ thời gian để hoàn thành các môn học nối tiếp nhau theo đúng lộ trình, dẫn đến việc kế hoạch bị xếp loại Yếu. Hệ thống sẽ đề xuất sinh viên lùi thời điểm tốt nghiệp để đảm bảo hoàn thành chương trình.

=== REVIEW QUESTIONS ===
1. Độ dài chuỗi tiên quyết dài nhất (Lchuỗi) được định nghĩa như thế nào trong hệ thống?
2. Tại sao một kế hoạch học tập bị coi là không khả thi nếu Lchuỗi > Scòn lại, ngay cả khi sinh viên sắp xếp lộ trình tối ưu?
3. Tổng số học kỳ còn lại (Scòn lại) được tính toán dựa trên những yếu tố nào?
4. Khi rơi vào trường hợp không đủ thời gian hoàn thành chuỗi tiên quyết, hệ thống đề xuất sinh viên thực hiện hành động gì?','9c4ae91e-5184-4031-8b5f-5b99d695988e'::uuid,NULL,NULL,115,730,'2026-03-21 13:35:13.340518+07'),
	 ('bbd3a56a-ecb2-49a3-9e94-b009f523e87f'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,115,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
• Tận dụng học kỳhè: nếu vẫn còn các học kỳhè chưa dùng đến, sinh viên nên cân
nhắc đăng ký một sốmôn tiên quyết vào kỳhè (nếu được mởlớp) nhằm rút ngắn
chuỗi thời gian chờ.
• Điều chỉnh lộtrình môn học: xem xét chọn các môn tựchọn hoặc hướng chuyên
ngành khác (nếu có) với ít ràng buộc tiên quyết hơn, giúp giảm độdài chuỗi môn
học phải học nối tiếp.
Sau khi điều chỉnh, sinh viên cần chạy lại thuật toán đểkiểm tra. Chỉkhi điều kiện
Lchuỗi ≤Scòn lại
được thỏa mãn, mục tiêu mới vượt qua bước kiểm tra ràng buộc tiên quyết vềthời gian
và tiếp tục sang các bước đánh giá tiếp theo.
(4) Kiểm tra các yêu cầu tốt nghiệp khác
Ngoài tín chỉvà GPA, chương trình còn có các điều kiện như chứng chỉngoại ngữ,
tin học, kỹnăng, ...Thuật toán kiểm tra xem với mốc Tgrad, sinh viên đã:
• đạt đủcác chứng chỉbắt buộc, hoặc
• có kếhoạch cụthểvềkỳdựkiến thi/hoàn thành.
Nếu thời gian tốt nghiệp dựkiến đã gần mà chứng chỉvẫn chưa đạt và cũng chưa có
kếhoạch rõ ràng, thuật toán đánh dấu đây là rủi ro cao. Mục tiêu không bịloại ngay,
nhưng sẽnhận cảnh báo mạnh trong phần gợi ý.
Nếu sau Bước 1 không có vi phạm nghiêm trọng, thuật toán chuyển sang Bước 2 để
phân tích chi tiết hơn. Ngược lại, nếu đã “vượt khung” vềtín chỉhoặc yêu cầu điểm, mục
tiêu được phân loại Yếu và sinh viên được khuyến nghịđiều chỉnh trước khi tiếp tục.
Bước 2: Phân tích định lượng mức độkhảthi
Sau khi vượt qua các kiểm tra ràng buộc cứng ởBước 1 (tín chỉ, thời gian, GPA mục
tiêu không vượt quá thang điểm), thuật toán đi sâu hơn vào việc ước lượng mức độkhả
thi dựa trên dữliệu lịch sửvà/hoặc mô hình dựđoán điểm.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 97/254

=== SUMMARY ===
Nội dung này hướng dẫn cách khắc phục khi lộ trình học tập bị vướng ràng buộc môn tiên quyết, bao gồm việc học kỳ hè và điều chỉnh môn tự chọn. Tài liệu cũng nêu rõ việc kiểm tra các điều kiện tốt nghiệp khác như chứng chỉ ngoại ngữ, tin học. Sau khi vượt qua các ràng buộc cứng ở Bước 1 (tín chỉ, thời gian, GPA), thuật toán sẽ chuyển sang Bước 2 để phân tích định lượng mức độ khả thi dựa trên dữ liệu lịch sử và mô hình dự đoán điểm.

=== REVIEW QUESTIONS ===
1. Những giải pháp nào được đề xuất để giảm độ dài chuỗi môn học tiên quyết và rút ngắn thời gian chờ?
2. Điều kiện toán học nào về chuỗi môn học (Lchuỗi) cần được thỏa mãn để vượt qua bước kiểm tra thời gian?
3. Hệ thống xử lý như thế nào nếu sinh viên chưa đạt chứng chỉ ngoại ngữ/tin học và không có kế hoạch hoàn thành rõ ràng?
4. Sự khác biệt cơ bản giữa Bước 1 và Bước 2 trong thuật toán đánh giá mức độ khả thi của mục tiêu tốt nghiệp là gì?','51ed0733-ddc3-4b06-94ea-70b638bd7a85'::uuid,NULL,NULL,116,671,'2026-03-21 13:35:13.340518+07'),
	 ('871cdcf8-4c42-436c-a4d4-2bb9ef2c2d99'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,116,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Trường hợp 1: Sinh viên chưa có lịch sửhọc tập (tân sinh viên)
Với sinh viên năm nhất chưa có dữliệu điểm cá nhân, thuật toán hoàn toàn dựa trên
thống kê của các khóa trước đểđánh giá mức độkhảthi của mục tiêu đềra. Từcác mục
tiêu cá nhân đã khai báo, hệthống xác định:
• Khối lượng tín chỉtrung bình mỗi kỳ(cảkỳchính và kỳhè) mà sinh viên cần
hoàn thành theo kếhoạch hiện tại đểtốt nghiệp đúng hạn.
• Mức điểm trung bình cần đạt mỗi kỳđểđến cuối khóa đạt được GPA mục tiêu
Gtarget (thang 4).
Dựa trên dữliệu khoảng 3 năm gần đây, hệthống tính toán xác suất hoàn thành mục
tiêu trực tiếp từkết quảcủa các sinh viên khóa trước. Cụthể, thuật toán xét:
• Pđạt mục tiêu: tỷlệsinh viên (trong cùng chuyên ngành) thỏa đồng thời hai điều kiện:
– tốt nghiệp trong khoảng thời gian không dài hơn kếhoạch hiện tại (tổng sốkỳ
chính + kỳhè ≤kếhoạch của sinh viên), và
– GPA tốt nghiệp không thấp hơn Gtarget.
Xác suất này có thểđược xấp xỉbằng tỉlệ:
Pfeasible ≈#{SV cùng chuyên ngành: tốt nghiệp đúng/nhanh hơn kếhoạch, GPA ≥Gtarget}
#{SV cùng chuyên ngành trong dữliệu khảo sát}
.
Lưu ý rằng ởbước này, mục tiêu của sinh viên đã vượt qua lớp kiểm tra sơ bộvềsốtín
chỉcòn lại, sốhọc kỳcòn lại và các ràng buộc thời gian/CTĐT, nên mức độkhảthi có
thểđạt được đã trên mức trung bình. Giá trịPđạt mục tiêu chủyếu dùng đểphân biệt mức
độrủi ro trong nhóm mục tiêu còn lại:
• Nếu Pđạt mục tiêu < 0,20 (dưới 20%): mục tiêu thuộc nhóm khá hiếm, chỉmột tỉlệ
nhỏsinh viên các khóa trước đạt được trong cùng khoảng thời gian. Hệthống vẫn
phân loại ởmức khảthi Trung bình, nhưng kèm nhận xét rủi ro cao và gợi ý cân
nhắc điều chỉnh.
• Nếu 0,20 ≤Pfeasible < 0,50 (từ20% đến dưới 50%): mục tiêu ởmức thách thức
nhưng vẫn có một tỉlệđáng kểsinh viên đạt được. Mức khảthi tổng thểvẫn là
Trung bình, song hệthống khuyến nghịsinh viên cần nỗlực nhiều hơn so với mặt
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 98/254

=== SUMMARY ===
Đoạn văn bản mô tả thuật toán đánh giá tính khả thi về mục tiêu tốt nghiệp dành cho tân sinh viên chưa có lịch sử học tập. Hệ thống sử dụng dữ liệu thống kê từ các khóa trước trong cùng chuyên ngành để tính toán xác suất P_feasible dựa trên hai điều kiện: thời gian tốt nghiệp và điểm GPA mục tiêu. Tùy vào giá trị xác suất thu được, hệ thống sẽ phân loại mức độ rủi ro và đưa ra các khuyến nghị nỗ lực hoặc điều chỉnh kế hoạch học tập phù hợp.

=== REVIEW QUESTIONS ===
1. Thuật toán dựa trên cơ sở dữ liệu nào để đánh giá tính khả thi cho tân sinh viên khi họ chưa có điểm cá nhân?
2. Hai điều kiện cần thiết để một sinh viên khóa trước được tính vào nhóm đạt mục tiêu (P_feasible) là gì?
3. Hệ thống đưa ra cảnh báo rủi ro cao và gợi ý điều chỉnh khi xác suất đạt mục tiêu (P_feasible) nằm ở mức nào?
4. Tại sao xác suất P_feasible lại quan trọng trong việc phân biệt mức độ rủi ro của các mục tiêu đã vượt qua bước kiểm tra sơ bộ?','9d6be669-c30b-4880-8d60-e8a6f5b10918'::uuid,NULL,NULL,117,741,'2026-03-21 13:35:13.340518+07'),
	 ('db5c7a71-ef3f-4824-8c88-aff9b00fde5d'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,117,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
bằng chung.
• Nếu Pđạt mục tiêu ≥0,50 (từ50% trởlên): mục tiêu nằm trong vùng khá phổbiến,
hơn một nửa sinh viên trong dữliệu đã đạt được hoặc vượt qua. Mục tiêu được phân
loại ởmức khảthi Tốt.
Tóm lại, trong trường hợp tân sinh viên:
Mức độkhảthi tổng thể=
{






Tốt,
nếu Pfeasible ≥0,50,
Trung bình,
nếu Pfeasible < 0,50,
Trường hợp 2: Sinh viên đã có lịch sửhọc tập
Ý tưởng chính.
Thay vì coi điểm của các môn chưa học là những giá trịcốđịnh, thuật toán giảsửmỗi
điểm môn tương lai là một biến ngẫu nhiên có phân phối chuẩn quanh giá trịdựđoán
của mô hình. Cách làm này giống với mô hình “Soft Grades” của Stanford [14], trong đó
điểm cuối cùng của sinh viên được biểu diễn dưới dạng phân phối xác suất chứkhông
phải một con sốduy nhất. Nhờvậy, ta có thểkết hợp cảgiá trịdựđoán và độkhông
chắc chắn khi đánh giá khảnăng đạt mục tiêu.
(1) Mô hình hoá phân phối điểm của từng môn
Với mỗi môn chưa học i, mô hình dựđoán cho ta:
• điểm kỳvọng ˆsi (thang 4),
• độlệch chuẩn dựđoán σi thểhiện mức độkhông chắc chắn.
Khi đó, ta mô hình hoá điểm thực tếcủa môn i là một biến ngẫu nhiên:
˜si ∼N(ˆsi,σ2
i ),
tức là điểm môn i phân bốchuẩn quanh ˆsi với “biên độdao động” là σi.
(2) Tính phân phối điểm trung bình tốt nghiệp
Giảsử:
• sinh viên đã tích luỹTCtích lũy tín chỉvới điểm trung bình hiện tại shiện tại (thang 10),
• tổng sốtín chỉcần đểtốt nghiệp là TCtổng,
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 99/254

=== SUMMARY ===
Đoạn văn bản mô tả quy trình đánh giá mức độ khả thi của mục tiêu học tập cho sinh viên. Đối với tân sinh viên, mức độ khả thi được phân loại dựa trên xác suất thống kê (Pfeasible). Đối với sinh viên đã có lịch sử học tập, thuật toán áp dụng mô hình "Soft Grades", coi điểm các môn học tương lai là biến ngẫu nhiên có phân phối chuẩn. Phương pháp này giúp tính toán phân phối điểm trung bình tốt nghiệp dự kiến bằng cách kết hợp điểm hiện tại và dự đoán điểm tương lai kèm theo độ lệch chuẩn.

=== REVIEW QUESTIONS ===
1. Dựa trên tiêu chí nào để phân loại mục tiêu của tân sinh viên là mức độ khả thi "Tốt"?
2. Mô hình "Soft Grades" từ Stanford giúp ích gì trong việc đánh giá khả năng đạt mục tiêu của sinh viên?
3. Trong mô hình hóa điểm của từng môn học chưa học, đại lượng nào thể hiện mức độ không chắc chắn của dự đoán?
4. Cách tính phân phối điểm trung bình tốt nghiệp thay đổi như thế nào khi sinh viên đã có lịch sử học tập so với tân sinh viên?','2a19b58b-8b62-4949-9585-d3f84b2767c9'::uuid,NULL,NULL,118,629,'2026-03-21 13:35:13.341538+07'),
	 ('00d8fc11-3c56-45f9-a347-5f4822b1aea4'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,118,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
• mỗi môn chưa học i có TCi tín chỉ.
Điểm trung bình tốt nghiệp scuối có thểviết dưới dạng trung bình có trọng số:
scuối = shiện tại ·TCtích lũy +∑i∈{môn chưa học} ˜si ·TCi
TCtổng
.
Trong đó:
• shiện tại ·TCtích lũy là phần “điểm quá khứ” đã cốđịnh,
• ˜si ·TCi là phần “điểm tương lai” còn bất định của từng môn.
Vì ˜si ∼N(ˆsi,σ2
i ) độc lập, tổhợp tuyến tính này vẫn cho ra một phân phối chuẩn:
scuối ∼N(µ,σ2).
Cụthể:
µ = E[scuối] = shiện tại ·TCtích lũy +∑i ˆsi ·TCi
TCtổng
,
σ2 = Var(scuối) = ∑i(TCi)2σ2
i
(TCtổng)2 .
Ởđây, hạng tửshiện tại·TCtích lũy là hằng sốnên không góp phần vào phương sai. Công
thức phương sai xuất phát từtính chất: nếu Y = ∑iciXi với Xi ∼N(µi,σ2
i ) độc lập, thì
Y ∼N
 ∑iciµi, ∑ic2
i σ2
i

.
(3) Tính xác suất đạt mục tiêu
Khi đã có phân phối scuối ∼N(µ,σ2), ta tính được xác suất đểsinh viên đạt hoặc
vượt mức điểm mục tiêu smục tiêu (thang 10, đã quy đổi từGPA mục tiêu):
Pđạt = P
 scuối ≥smục tiêu

= 1−Φ
 smục tiêu −µ
σ

,
trong đó Φ là hàm phân phối tích luỹcủa phân phối chuẩn chuẩn hoá N(0,1).
Kết quảbước này gồm:
• xác suất Pđạt – khảnăng đạt hoặc vượt mức điểm mục tiêu,
• giá trịkỳvọng µ – mức điểm tốt nghiệp trung bình dựkiến,
• độlệch chuẩn σ – mức “dao động” quanh điểm dựkiến đó.
Nhờđó, sinh viên có được một đánh giá định lượng vềmức độkhảthi của mục tiêu.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 100/254

=== SUMMARY ===
Đoạn văn bản trình bày phương pháp tính toán xác suất đạt mục tiêu GPA cho sinh viên đã có lịch sử học tập bằng mô hình phân phối chuẩn. Thay vì coi điểm số là cố định, thuật toán giả định điểm các môn tương lai là biến ngẫu nhiên. Bằng cách kết hợp điểm quá khứ và dự đoán tương lai, hệ thống xác định giá trị kỳ vọng (μ) và độ lệch chuẩn (σ) của điểm tốt nghiệp. Cuối cùng, xác suất đạt mục tiêu được tính toán thông qua hàm phân phối tích lũy chuẩn hóa.

=== REVIEW QUESTIONS ===
1. Tại sao điểm của các môn học chưa hoàn thành lại được mô hình hóa dưới dạng một biến ngẫu nhiên có phân phối chuẩn thay vì một con số cố định?
2. Trong công thức tính phương sai (σ²), tại sao thành phần điểm quá khứ (shiện tại * TCtích lũy) lại không đóng góp vào kết quả cuối cùng?
3. Hàm phân phối tích lũy Φ của phân phối chuẩn chuẩn hóa đóng vai trò gì trong việc xác định xác suất đạt mục tiêu (Pđạt)?
4. Ba thông số đầu ra (Pđạt, μ, σ) cung cấp những thông tin định lượng khác nhau nào giúp sinh viên đánh giá kế hoạch học tập của mình?','fb564108-f41f-40fb-91e7-16b34db63eae'::uuid,NULL,NULL,119,632,'2026-03-21 13:35:13.341538+07'),
	 ('ed3d4ec6-00d2-4d73-9837-8c53a964c045'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,119,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Bước 3: Phân loại mức độkhảthi và gợi ý điều chỉnh
Ởbước này, thuật toán sửdụng kết quảhai bước trước đểđưa ra đánh giá cuối cùng
cho mục tiêu học tập:
Đầu ra của bước này gồm:
• Mức độkhảthi: Tốt / Trung bình / Yếu
• Giải thích ngắn gọn lý do xếp loại
• Danh sách gợi ý điều chỉnh dựa trên ưu tiên học tập (GPA, Kiến thức, Thời gian)
mà sinh viên đã khai báo
• (Nếu có) một sốcảnh báo bổsung vềchứng chỉbắt buộc
(1) Xác định mức độkhảthi tổng quát
Thuật toán trước hết xem xét các ràng buộc cứng từBước 1:
• Điều kiện tín chỉ:
TCcòn lại ≤TCtối đa,
trong đó TCcòn lại là tổng sốtín chỉcòn thiếu đểtốt nghiệp, TCtối đa là sốtín chỉtối
đa có thểtích luỹtheo kếhoạch tải học (sốkỳ, mức tín chỉmỗi kỳ).
• Điều kiện GPA:
Gcần đạt ≤4,0,
với Gcần đạt là GPA trung bình (thang 4,0) mà các môn chưa học cần đạt đểvươn tới
GPA mục tiêu.
Nếu vi phạm bất kỳđiều kiện nào ởtrên, cụthể:
TCcòn lại > TCtối đa
hoặc
Gcần đạt > 4,0,
hoặc
Lchuỗi ≤Scòn lại
thì mục tiêu được xếp mức Yếu ngay lập tức và hệthống đưa ra gợi ý điều chỉnh.
Ngược lại, nếu không vi phạm ràng buộc cứng thì có thểcoi mục tiêu ít nhất nằm
trong vùng “không quá phi thực tế”. Khi đó, thuật toán chỉcòn phân biệt giữa hai mức
Tốt và Trung bình dựa trên xác suất đạt mục tiêu Pđạt từBước 2:
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 101/254

=== SUMMARY ===
Đoạn văn bản trình bày Bước 3 của thuật toán đánh giá kế hoạch học tập, tập trung vào việc phân loại mức độ khả thi (Tốt, Trung bình, Yếu) và đưa ra gợi ý điều chỉnh. Thuật toán kiểm tra các ràng buộc cứng về tổng số tín chỉ còn lại và điểm GPA cần đạt. Nếu vi phạm các giới hạn vật lý hoặc học thuật này, mục tiêu bị xếp loại Yếu. Nếu thỏa mãn, hệ thống dựa trên xác suất đạt mục tiêu để phân loại mức Tốt hoặc Trung bình.

=== REVIEW QUESTIONS ===
1. Đầu ra của Bước 3 trong quy trình đánh giá mục tiêu học tập bao gồm những thông tin cụ thể nào?
2. Điều kiện về tín chỉ (TC còn lại và TC tối đa) được sử dụng như thế nào để xác định một mục tiêu là ''Yếu''?
3. Tại sao chỉ số G cần đạt > 4,0 lại dẫn đến việc xếp loại mục tiêu ở mức Yếu ngay lập tức?
4. Trong trường hợp không vi phạm các ràng buộc cứng, thuật toán dựa vào đâu để phân biệt giữa mức độ khả thi Tốt và Trung bình?','ddfede9b-2250-4bfa-85d1-34f3a093c69e'::uuid,NULL,NULL,120,580,'2026-03-21 13:35:13.341538+07'),
	 ('ecb6ae20-d359-454b-96f8-ea020e70f7e3'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,120,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
• Mức Tốt nếu:
Pđạt ≥0,5.
Điều này cho thấy khảnăng đạt mục tiêu từtrung bình trởlên, nói cách khác là “khá
sáng sủa” nếu sinh viên giữvững kếhoạch.
• Mức Trung bình nếu:
Pđạt < 0,5.
Khảnăng đạt mục tiêu không thấp đến mức bất khảthi, nhưng vẫn còn nhiều rủi
ro; sinh viên cần chú ý và có thểphải điều chỉnh nhẹkếhoạch hoặc tăng nỗlực so
với hiện tại.
Lưu ý vềchứng chỉbắt buộc.
Các yêu cầu như chứng chỉngoại ngữ, tin học, . . . không được dùng đểthay đổi mức
Tốt/Trung bình/Yếu. Thay vào đó, nếu thuật toán phát hiện:
• thời gian tới mốc tốt nghiệp Tgrad đã rất gần,
• sinh viên vẫn chưa đạt một sốchứng chỉbắt buộc,
• và trong kếhoạch hiện tại chưa nêu rõ kỳdựkiến hoàn thành chứng chỉ,
thì hệthống sẽgắn thêm cảnh báo trong phần gợi ý, ví dụ: “Bạn cần hoàn thành
chứng chỉngoại ngữtrước HK2-2027 đểkhông ảnh hưởng tới tiến độtốt nghiệp”. Cảnh
báo này giúp sinh viên chú ý hơn, nhưng không làm thay đổi mức Tốt/Trung bình/Yếu
đã phân loại ởtrên.
(2) Gợi ý điều chỉnh theo mức khảthi và ưu tiên học tập
Sau khi xác định mức độkhảthi (Tốt/Trung bình/Yếu), thuật toán không chỉtrảvề
nhãn đơn thuần, mà còn sinh ra gợi ý điều chỉnh dựa trên:
• Mức độkhảthi hiện tại
• Ưu tiên học tập mà sinh viên đã chọn (ưu tiên GPA, ưu tiên Kiến thức, ưu tiên Thời
gian tốt nghiệp)
(a) Khi mục tiêu được xếp mức Tốt
Điều kiện điển hình: không vi phạm ràng buộc cứng, Pđạt ≥0,5.
Ý nghĩa: mục tiêu nằm trong vùng an toàn, kếhoạch tương đồng với những gì nhiều
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 102/254

=== SUMMARY ===
Đoạn văn bản mô tả quy trình phân loại mức độ khả thi của mục tiêu học tập thành các mức Tốt và Trung bình dựa trên xác suất Pđạt. Nội dung cũng đề cập đến cách xử lý các chứng chỉ bắt buộc thông qua hệ thống cảnh báo mà không làm thay đổi phân loại khả thi. Cuối cùng, thuật toán đưa ra các gợi ý điều chỉnh kế hoạch cá nhân hóa dựa trên mức độ khả thi và thứ tự ưu tiên của sinh viên về GPA, kiến thức hoặc thời gian.

=== REVIEW QUESTIONS ===
1. Điều kiện về xác suất Pđạt để một mục tiêu học tập được phân loại ở mức ''Tốt'' là gì?
2. Việc thiếu các chứng chỉ bắt buộc (ngoại ngữ, tin học) có làm thay đổi mức độ khả thi Tốt/Trung bình/Yếu không?
3. Hệ thống sẽ đưa ra cảnh báo về chứng chỉ bắt buộc trong những trường hợp cụ thể nào?
4. Thuật toán dựa trên những yếu tố nào để sinh ra các gợi ý điều chỉnh kế hoạch học tập cho sinh viên?','c5348e8a-e306-46de-8bd3-72100144c257'::uuid,NULL,NULL,121,619,'2026-03-21 13:35:13.341538+07'),
	 ('fdfcd40f-5adf-4ba7-adae-3efd22df556f'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,121,'=== ORIGINAL CONTENT ===
sinh viên trước đã làm được.
Gợi ý điều chỉnh:
• Nếu ưu tiên số1 là GPA:
– Giữtải học ởmức vừa phải, tránh đăng ký quá tải kéo dài.
– Tập trung duy trì thói quen học tập đang hiệu quả, đặc biệt ởcác môn có nhiều
tín chỉ.
• Nếu ưu tiên số1 là Kiến thức:
– Có thểbổsung thêm một vài môn chuyên sâu, môn tựchọn nâng cao hoặc dự
án thực tế.
– Tham gia hoạt động học thuật (CLB, nghiên cứu khoa học) vì điểm sốhiện tại
an toàn so với mục tiêu.
• Nếu ưu tiên số1 là Thời gian:
– Bám sát lộtrình đã đềra, không nên trì hoãn các môn bắt buộc.
– Tránh bỏhẳn kỳhè hoặc nghỉhọc dài kỳnếu điều đó làm thu hẹp “khoảng an
toàn” vềtiến độ.
(b) Khi mục tiêu được xếp mức Trung bình
Điều kiện điển hình: không vi phạm ràng buộc cứng, nhưng Pđạt < 0,5.
Ý nghĩa: mục tiêu khá thách thức, có thểđạt nhưng đòi hỏi sinh viên phải nỗlực hơn
mặt bằng chung và quản lý kếhoạch học tập chặt chẽhơn.
Gợi ý điều chỉnh chung:
• Xem xét lại tải học: có cần tăng/giảm nhẹsốtín chỉmỗi kỳkhông.
• Tận dụng kỳhè (nếu còn trong giới hạn) đểgiảm bớt áp lực kỳchính.
• Ưu tiên các môn nhiều tín chỉvà môn chuyên ngành quan trọng để“kéo” GPA.
Sau đó, cá nhân hoá theo ưu tiên:
• Nếu ưu tiên số1 là GPA:
– Cân nhắc sửdụng các kỳhè còn lại đểhọc thêm môn, giúp tăng tổng sốtín chỉ
tích luỹhoặc học đểcải thiện GPA.
– Cân nhắc giảm nhẹsốtín chỉmỗi kỳchính đểtập trung học tốt hơn, tránh rủi
ro rớt môn hay điểm không đạt mục tiêu làm ảnh hưởng GPA.
– Ưu tiên các môn có độkhó vừa phải, dễcó điểm đểtăng GPA.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 103/254

=== SUMMARY ===
Đoạn trích hướng dẫn cách phân loại và đưa ra gợi ý điều chỉnh cho mục tiêu học tập ở hai mức: Tốt (xác suất đạt ≥ 0.5) và Trung bình (xác suất đạt < 0.5). Với mức Tốt, sinh viên được khuyến khích duy trì lộ trình hoặc mở rộng kiến thức. Với mức Trung bình, hệ thống cảnh báo rủi ro và đề xuất các giải pháp như tận dụng kỳ hè, điều chỉnh tải học hoặc ưu tiên môn học quan trọng dựa trên ba tiêu chí: GPA, Kiến thức và Thời gian.

=== REVIEW QUESTIONS ===
1. Điều kiện về xác suất (Pđạt) để một mục tiêu học tập được xếp loại ở mức Tốt và Trung bình là gì?
2. Với mức khả thi Tốt, nếu sinh viên ưu tiên hàng đầu là Kiến thức thì hệ thống gợi ý những hoạt động bổ sung nào?
3. Đối với mức Trung bình, tại sao hệ thống lại khuyên sinh viên nên tận dụng các kỳ hè?
4. Khi ưu tiên GPA ở mức Trung bình, chiến thuật chọn môn học và phân bổ tín chỉ được gợi ý như thế nào?','b016462d-dc03-41a4-8c1d-a7ec7b5442e0'::uuid,NULL,NULL,122,611,'2026-03-21 13:35:13.341538+07'),
	 ('959fa7b9-2aa5-4848-8e5a-77de086c50e2'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,122,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
• Nếu ưu tiên số1 là Kiến thức:
– Nên giảm sốtín chỉhọc mỗi kỳđểcó thời gian học kỹhơn các môn quan trọng.
– Nếu cần học sâu hơn, có thểlùi mốc tốt nghiệp thêm 1 kỳđểtránh quá tải.
– Có thểgiảm nhẹmục tiêu GPA nếu cảm thấy quá áp lực.
• Nếu ưu tiên số1 là Thời gian:
– Tăng nhẹsốtín chỉmỗi kỳ(trong giới hạn cho phép) và tận dụng 1–2 kỳhè nếu
còn.
– Nếu chênh lệch giữa dựđoán và mục tiêu GPA quá lớn, cân nhắc giảm nhẹmục
tiêu GPA đểgiảm bớt rủi ro.
– Có thểchọn những môn học dễqua môn đểđảm bảo tích luỹtín chỉđúng tiến
độ.
(c) Khi mục tiêu được xếp mức Yếu
Điều kiện điển hình:
• Vi phạm ràng buộc cứng:
TCcòn lại > TCtối đa
hoặc
Gcần đạt > 4,0.
Ý nghĩa: với điều kiện hiện tại, khảnăng đạt mục tiêu là rất thấp, kếhoạch cần được
xem xét và điều chỉnh lại một cách nghiêm túc.
Gợi ý điều chỉnh theo ưu tiên:
• Nếu ưu tiên số1 là GPA:
– Gợi ý kéo dài thời gian học (lùi Tgrad), thêm học kỳchính hoặc học kỳhè để
giảm áp lực.
– Nếu sau khi kéo dài thời gian mà Gcần đạt vẫn ởmức quá cao, nên hạGPA mục
tiêu xuống một mức thực tếhơn.
• Nếu ưu tiên số1 là Kiến thức:
– Giảm sốtín chỉmỗi kỳđểcó thời gian học chắc, chấp nhận tốt nghiệp trễhơn.
– Tập trung vào các môn nền tảng và môn chuyên ngành cốt lõi, không cốnhồi
thêm quá nhiều môn một lúc.
• Nếu ưu tiên số1 là Thời gian:
– Nếu bắt buộc phải giữmốc tốt nghiệp hiện tại, cần tăng tải học lên mức tối đa
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 104/254

=== SUMMARY ===
Đoạn văn hướng dẫn các phương án điều chỉnh lộ trình học tập dựa trên mức độ khả thi (Trung bình và Yếu) kết hợp với ưu tiên cá nhân của sinh viên về Kiến thức, Thời gian hoặc GPA. Đặc biệt, mức độ khả thi ''Yếu'' được xác định khi sinh viên vi phạm các ràng buộc cứng như số tín chỉ còn lại vượt quá khả năng tích lũy tối đa hoặc điểm GPA yêu cầu vượt quá 4.0. Hệ thống đề xuất các giải pháp thực tế như giảm tải, lùi thời gian tốt nghiệp hoặc điều chỉnh lại mục tiêu điểm số.

=== REVIEW QUESTIONS ===
1. Những điều kiện cụ thể nào khiến một mục tiêu học tập bị hệ thống phân loại ở mức ''Yếu''?
2. Nếu sinh viên ưu tiên ''Kiến thức'' nhưng đang ở mức khả thi thấp, họ được khuyên nên làm gì với số lượng tín chỉ mỗi kỳ?
3. Trong trường hợp ưu tiên ''Thời gian'' ở mức ''Yếu'', sinh viên cần phải chấp nhận đánh đổi điều gì nếu vẫn muốn giữ mốc tốt nghiệp hiện tại?
4. Tại sao việc lùi mốc tốt nghiệp (Tgrad) lại là một giải pháp quan trọng khi sinh viên không thể đạt được mức GPA mục tiêu?','9d2585b6-4c39-43b8-ab33-186c8065376d'::uuid,NULL,NULL,123,634,'2026-03-21 13:35:13.342564+07');
INSERT INTO coaching_chatbot.lecture_knowledge_chunks (lecture_knowledge_chunks_id,lecture_knowledge_id,chunk_index,chunk_content,qdrant_point_id,start_time_seconds,end_time_seconds,page_number,token_count,created_at) VALUES
	 ('7a012995-9280-4577-a925-df2543d7eb0b'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,124,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Flowchart tổng quát của thuật toán
Hình 5.2: Flowchart của thuật toán đánh giá mức độkhảthi của mục tiêu học tập
Hình 5.2 minh hoạflowchart tổng quát của thuật toán đánh giá mức độkhảthi của
mục tiêu học tập ởbước 1.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 106/254

=== SUMMARY ===
Phân đoạn này giới thiệu sơ đồ luồng (flowchart) tổng quát của thuật toán đánh giá tính khả thi của mục tiêu học tập. Đây là bước đầu tiên trong quy trình xử lý, giúp xác định xem các mục tiêu mà sinh viên thiết lập có phù hợp với điều kiện thực tế hay không trước khi sinh lộ trình chi tiết.

=== REVIEW QUESTIONS ===
1. Hình 5.2 trong tài liệu minh họa cho nội dung cụ thể nào?
2. Thuật toán đánh giá mức độ khả thi của mục tiêu học tập được thực hiện ở bước thứ mấy theo mô tả trên?
3. Mục đích chính của thuật toán được nhắc đến trong đoạn văn là gì?
4. Nội dung này nằm trong báo cáo đồ án của học phần nào và thuộc năm học nào?','3649a29e-dfc3-44d5-a8e9-54af90c906cf'::uuid,NULL,NULL,125,261,'2026-03-21 13:35:13.342564+07'),
	 ('a111aeea-2b83-4254-be37-ac0a4a91690c'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,125,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Hình 5.3: Flowchart của thuật toán đánh giá mức độkhảthi của mục tiêu học tập
Hình 5.3 minh hoạflowchart tổng quát của thuật toán đánh giá mức độkhảthi của
mục tiêu học tập ởbước 2.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 107/254

=== SUMMARY ===
Đoạn văn này giới thiệu về sơ đồ luồng (flowchart) của thuật toán đánh giá mức độ khả thi của mục tiêu học tập, tập trung cụ thể vào bước thứ hai trong quy trình xử lý. Đây là một thành phần quan trọng trong hệ thống hỗ trợ sinh viên xác định tính thực tế của các mục tiêu đào tạo trước khi lập lộ trình chi tiết.

=== REVIEW QUESTIONS ===
1. Hình 5.3 trong tài liệu minh họa cho quy trình nào của thuật toán?
2. Nội dung này tập trung vào bước thứ mấy trong thuật toán đánh giá mức độ khả thi?
3. Mục đích chính của việc sử dụng flowchart trong trường hợp này là gì?
4. Tại sao việc đánh giá mức độ khả thi của mục tiêu học tập lại quan trọng đối với sinh viên?','d40059d3-34f7-46b1-9cb9-dd73f0ca9a2c'::uuid,NULL,NULL,126,260,'2026-03-21 13:35:13.342564+07'),
	 ('9560975b-05b6-410d-a975-bf285e06d424'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,126,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
5.3
Định lượng giá trịnghềnghiệp của môn học
Đểtrảlời câu hỏi: "Môn học này đóng góp bao nhiêu giá trịcho một vịtrí công việc
cụthể?", chúng ta không thểchỉdựa vào cảm tính hay các nhận định chủquan. Vì vậy,
nhóm nghiên cứu đã xây dựng một phương pháp tính toán định lượng, cho phép chuyển
đổi các mô tảvăn bản của môn học và dữliệu nghềnghiệp thành các con sốđo lường
được.
Trọng tâm của phương pháp là sựkết hợp giữa cơ sởdữliệu chuẩn hóa quốc tế
O*NET và kỹthuật Vector hóa văn bản (Text Embedding) đểso sánh nội dung giảng
dạy với yêu cầu thực tếcủa thịtrường lao động.
5.3.1
Cơ sởdữliệu nghềnghiệp O*NET
Đểcó thước đo chuẩn xác, chúng em sửdụng dữliệu từhệthống O*NET (Occupa-
tional Information Network). Đây là cơ sởdữliệu vềnghềnghiệp chính thống được
bảo trợbởi BộLao động Hoa Kỳ, và được xem là "tiêu chuẩn vàng"trong việc phân tích
nghềnghiệp trên toàn thếgiới [16].
Khác với các trang tin tuyển dụng thông thường chỉliệt kê đầu việc, O*NET cung
cấp một cấu trúc dữliệu khoa học, phân rã một nghềnghiệp thành hàng trăm yếu tốcấu
thành chi tiết, bao gồm:
• Knowledge (Kiến thức): Các nguyên lý và sựkiện cần nắm vững.
• Skills (Kỹnăng): Khảnăng thực hiện thao tác đểhoàn thành công việc.
• Tasks (Nhiệm vụ): Các hành động cụthểdiễn ra hàng ngày.
• Technology (Công nghệ): Các phần mềm và công cụcần sửdụng.
Nhờsựchi tiết này, O*NET giúp chúng ta không chỉbiết tên nghề, mà còn hiểu sâu sắc
"cấu tạo"bên trong của nghềđó.
5.3.2
Kỹthuật Vector hóa văn bản (Text Embedding)
Vấn đềcốt lõi mà chúng em cần giải quyết không phải là sựkhác biệt vềngôn ngữ,
mà là sựkhác biệt trong cách diễn đạt ngữnghĩa.
Mô tảmôn học thường được viết bằng ngôn ngữhọc thuật (Academic Language)
mang tính khái quát và lý thuyết. Ngược lại, dữliệu O*NET sửdụng ngôn ngữthực
hành (Occupational Language) tập trung vào các hành động cụthể. Do đó, hai đoạn văn
bản dù nói vềcùng một năng lực nhưng lại sửdụng các từvựng hoàn toàn khác nhau,
khiến cho phương pháp so khớp từkhóa truyền thống trởnên vô hiệu.
Đểkhắc phục điều này, chúng em sửdụng kỹthuật Text Embedding với mô hình
Gemini Embedding 004 [17].
Sức mạnh thực sựcủa Text Embedding nằm ởkhảnăng bắt trọn ngữnghĩa (semantics)
của văn bản. Thay vì so sánh lớp vỏngôn từbên ngoài, mô hình này phân tích ý nghĩa
cốt lõi bên trong và chuyển đổi chúng thành các vector sốhọc. Trong không gian đa
chiều này, hai đoạn văn bản có ý nghĩa tương đương sẽđược đặt ởvịtrí rất gần nhau, bất
chấp sựkhác biệt vềcâu từ.
Ví dụ: Hệthống có thểnhận diện chính xác rằng chuẩn đầu ra "Implement algorithms
for data processing" (Triển khai thuật toán xửlý dữliệu) có sựtương đồng ngữnghĩa
rất cao với nhiệm vụ"Write software code to analyze data" (Viết mã phần mềm đểphân
tích dữliệu), mặc dù hai câu này hầu như không chia sẻcác từkhóa giống nhau.
5.3.3
Đồng nhất hóa dữliệu nghềnghiệp
Dữliệu O*NET rất phức tạp vì nó chia yêu cầu công việc thành nhiều bảng khác
nhau như: Kiến thức (Knowledge), Kỹnăng (Skills), Nhiệm vụcụthể(Tasks), Công
nghệ(Technology) hay Hoạt động công việc (IWA/DWA). Nếu tính toán riêng lẻtừng
bảng sẽrất rời rạc.
Vì vậy, bước đầu tiên là chúng ta gom tất cảcác dữliệu này vềmột cấu trúc chung
du nhất, gọi là Yêu cầu Nghềnghiệp (ORU). Có thểhiểu một cách đơn giản rằng: Dù
là một "kỹnăng mềm"hay một "nhiệm vụkỹthuật", chúng đều là những yêu cầu mà
sinh viên cần đáp ứng.
Cấu trúc chung của một đơn vịORU bao gồm:
• ID & Loại: Đểbiết yêu cầu này thuộc nhóm nào (ví dụ: là Skill hay Task).
• Nội dung: Mô tảchi tiết yêu cầu.
• Điểm quan trọng (SIM): Con sốthểhiện mức độcần thiết của yêu cầu này đối với
nghề.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 108/254

=== SUMMARY ===
Nội dung trình bày phương pháp định lượng giá trị nghề nghiệp của môn học bằng cách kết hợp cơ sở dữ liệu O*NET và kỹ thuật Text Embedding. O*NET giúp phân tích chi tiết nghề nghiệp qua các yếu tố kiến thức, kỹ năng, nhiệm vụ và công nghệ. Trong khi đó, Text Embedding (sử dụng mô hình Gemini) giải quyết sự khác biệt giữa ngôn ngữ học thuật và ngôn ngữ thực hành bằng cách so sánh ý nghĩa ngữ nghĩa dưới dạng vector số học thay vì so khớp từ khóa.

=== REVIEW QUESTIONS ===
1. Tại sao cơ sở dữ liệu O*NET được coi là ''tiêu chuẩn vàng'' trong việc phân tích nghề nghiệp?
2. O*NET phân chia một vị trí công việc thành những thành phần chi tiết nào?
3. Sự khác biệt chính giữa ngôn ngữ học thuật (Academic Language) và ngôn ngữ thực hành (Occupational Language) là gì?
4. Kỹ thuật Text Embedding giúp giải quyết vấn đề so khớp dữ liệu như thế nào khi các từ khóa không trùng lặp?','0aec4f4a-5344-4d52-9366-663ceaf57f5b'::uuid,NULL,NULL,127,1157,'2026-03-21 13:35:13.342564+07'),
	 ('abadd63c-f9cb-4f77-ba13-c0451b35e452'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,127,'=== ORIGINAL CONTENT ===
mang tính khái quát và lý thuyết. Ngược lại, dữliệu O*NET sửdụng ngôn ngữthực hành (Occupational Language) tập trung vào các hành động cụthể. Do đó, hai đoạn văn bản dù nói vềcùng một năng lực nhưng lại sửdụng các từvựng hoàn toàn khác nhau, khiến cho phương pháp so khớp từkhóa truyền thống trởnên vô hiệu. Đểkhắc phục điều này, chúng em sửdụng kỹthuật Text Embedding với mô hình Gemini Embedding 004 [17]. Sức mạnh thực sựcủa Text Embedding nằm ởkhảnăng bắt trọn ngữnghĩa (semantics) của văn bản. Thay vì so sánh lớp vỏngôn từbên ngoài, mô hình này phân tích ý nghĩa cốt lõi bên trong và chuyển đổi chúng thành các vector sốhọc. Trong không gian đa chiều này, hai đoạn văn bản có ý nghĩa tương đương sẽđược đặt ởvịtrí rất gần nhau, bất chấp sựkhác biệt vềcâu từ. Ví dụ: Hệthống có thểnhận diện chính xác rằng chuẩn đầu ra "Implement algorithms for data processing" (Triển khai thuật toán xửlý dữliệu) có sựtương đồng ngữnghĩa rất cao với nhiệm vụ"Write software code to analyze data" (Viết mã phần mềm đểphân tích dữliệu), mặc dù hai câu này hầu như không chia sẻcác từkhóa giống nhau. 5.3.3 Đồng nhất hóa dữliệu nghềnghiệp Dữliệu O*NET rất phức tạp vì nó chia yêu cầu công việc thành nhiều bảng khác nhau như: Kiến thức (Knowledge), Kỹnăng (Skills), Nhiệm vụcụthể(Tasks), Công nghệ(Technology) hay Hoạt động công việc (IWA/DWA). Nếu tính toán riêng lẻtừng bảng sẽrất rời rạc. Vì vậy, bước đầu tiên là chúng ta gom tất cảcác dữliệu này vềmột cấu trúc chung duy nhất, gọi là Yêu cầu Nghềnghiệp (ORU). Có thểhiểu một cách đơn giản rằng: Dù là một "kỹnăng mềm"hay một "nhiệm vụkỹthuật", chúng đều là những yêu cầu mà sinh viên cần đáp ứng. Cấu trúc chung của một đơn vịORU bao gồm: • ID & Loại: Đểbiết yêu cầu này thuộc nhóm nào (ví dụ: là Skill hay Task). • Nội dung: Mô tảchi tiết yêu cầu. • Điểm quan trọng (SIM): Con sốthểhiện mức độcần thiết của yêu cầu này đối với nghề.

=== SUMMARY ===
Đoạn văn bản trình bày cách giải quyết sự khác biệt giữa ngôn ngữ học thuật của môn học và ngôn ngữ thực hành của O*NET bằng kỹ thuật Text Embedding (Gemini Embedding 004). Thay vì so khớp từ khóa, phương pháp này sử dụng vector số học để tìm sự tương đồng về ngữ nghĩa. Ngoài ra, nội dung còn đề cập đến việc chuẩn hóa dữ liệu O*NET phức tạp thành cấu trúc chung gọi là Yêu cầu Nghề nghiệp (ORU), giúp đồng nhất việc đánh giá kiến thức, kỹ năng và nhiệm vụ.

=== REVIEW QUESTIONS ===
1. Tại sao phương pháp so khớp từ khóa truyền thống lại không hiệu quả khi đối chiếu mô tả môn học với dữ liệu nghề nghiệp O*NET?
2. Kỹ thuật Text Embedding với mô hình Gemini Embedding 004 giúp nhận diện sự tương đồng giữa hai văn bản dựa trên cơ chế nào?
3. Việc đồng nhất hóa dữ liệu nghề nghiệp thành cấu trúc ORU (Occupational Requirement Unit) nhằm mục đích gì?
4. Một đơn vị ORU bao gồm những thông tin thành phần nào để phục vụ cho quá trình tính toán và định lượng?','7111293a-2277-4295-b366-52d1cbbcbd03'::uuid,NULL,NULL,128,719,'2026-03-21 13:35:13.343662+07'),
	 ('266d5d86-b059-4326-b81e-73eb419320d2'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,128,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Cách làm này giúp hệthống nhìn nhận một nghềnghiệp là một danh sách tổng hợp
tất cảcác yêu cầu, thay vì các mảnh ghép rời rạc.
5.3.4
Chuẩn hóa hệsốtầm quan trọng (SIM)
Trong dữliệu gốc của O*NET, mức độquan trọng thường được đánh giá từ1 đến 5.
Đểdễtính toán phần trăm đóng góp sau này, chúng ta sẽquy đổi thang điểm này vềhệ
sốtừ0 đến 100.
Công thức quy đổi như sau:
SIM =
 IMraw −1
5−1

×100
(5.1)
Trong đó:
• IMraw: Là điểm gốc lấy từO*NET (từ1 đến 5).
• SIM: Là điểm đã chuẩn hóa. Ví dụ: Nếu một kỹnăng cực kỳquan trọng (5 điểm),
nó sẽcó SIM = 100. Nếu ít quan trọng hơn (3 điểm), nó sẽlà 50.
Lưu ý: Đối với các bảng dữliệu định tính không có sẵn thang điểm 1-5 (như Task,
Technology, hay Detailed Work Activity), hệthống áp dụng quy tắc Định giá Heuristic
dựa trên các thuộc tính phân loại có sẵn trong O*NET:
• Đối với nhiệm vụ: Điểm sốđược gán dựa trên phân loại mức độthiết yếu (Task
Type). Các nhiệm vụcốt lõi (Core) được gán mức điểm Cao (tương đương 4.5/5),
trong khi các nhiệm vụbổtrợ(Supplemental) chỉnhận mức điểm Thấp.
• Đối với công nghệ: THệthống dựa vào các cờbáo thịtrường của O*NET. Nếu một
công cụđược gắn nhãn là đang có nhu cầu cao hoặc đang nổi, nó sẽđược gán mức
độquan trọng Cao. Các công cụcòn lại được gán mức Trung bình.
• Đối với hoạt động chi tiết (DWA): Một DWA sẽthừa hưởng độquan trọng của
nhiệm vụmà nó hỗtrợ. Trong trường hợp một hoạt động DWA xuất hiện trong
nhiều nhiệm vụkhác nhau, hệthống sẽghi nhận mức điểm của nhiệm vụquan
trọng nhất đểđảm bảo không bỏsót giá trịđóng góp của nó.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 110/254

=== SUMMARY ===
Đoạn văn bản mô tả quy trình chuẩn hóa hệ số tầm quan trọng (SIM) từ dữ liệu O*NET sang thang điểm 100. Nội dung bao gồm công thức quy đổi tuyến tính cho các kỹ năng định lượng và phương pháp định giá Heuristic cho các thành phần định tính (nhiệm vụ, công nghệ, DWA). Việc chuẩn hóa giúp đồng nhất các yêu cầu nghề nghiệp rời rạc thành một danh sách đo lường được, hỗ trợ tính toán mức độ đóng góp của môn học vào năng lực nghề nghiệp.

=== REVIEW QUESTIONS ===
1. Công thức cụ thể để chuyển đổi điểm quan trọng từ thang điểm 5 của O*NET sang hệ số SIM 100 là gì?
2. Theo quy tắc định giá Heuristic, các nhiệm vụ cốt lõi (Core) và nhiệm vụ bổ trợ (Supplemental) được gán mức điểm như thế nào?
3. Hệ thống dựa vào yếu tố nào trong O*NET để xác định mức độ quan trọng cho các loại công nghệ?
4. Trong trường hợp một Hoạt động chi tiết (DWA) xuất hiện trong nhiều nhiệm vụ khác nhau, điểm quan trọng của nó được tính như thế nào?','f214854a-8e6d-473d-b779-8ea24e54903d'::uuid,NULL,NULL,129,664,'2026-03-21 13:35:13.343662+07'),
	 ('10ab6e54-a46f-4960-986e-0f00c7c4da84'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,129,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
5.3.5
Kỹthuật So khớp Ngữnghĩa (Semantic Matching)
Một thách thức lớn khi liên kết dữliệu là sựkhác biệt trong cách diễn đạt. Mặc dù cả
hai nguồn dữliệu đều mô tảvềcùng một kỹnăng đào tạo hay chuẩn đầu ra, nhưng từngữ
sửdụng lại khác nhau. Do đó, các phương pháp so khớp từkhóa (keyword matching)
truyền thống sẽkhông hiệu quả, vì không thểnhận ra rằng hai bên đang nói vềcùng một
vấn đềnếu từngữkhông hoàn toàn giống hệt nhau.
Đểgiải quyết vấn đềnày, hệthống áp dụng kỹthuật Vector hóa văn bản (Text Em-
bedding) sửdụng mô hình Gemini Embedding 004.
Kỹthuật này chuyển đổi các đoạn văn bản thành các vector đặc trưng trong không gian
nhiều chiều (High-dimensional Vector Space). Trong không gian này, vịtrí của vector
được xác định bởi ngữnghĩa (semantics) thay vì mặt chữ. Điều này đảm bảo rằng hai
đoạn văn bản diễn đạt cùng một ý niệm sẽcó vector đại diện nằm gần nhau vềmặt hình
học.
• Gọi VLO là vector biểu diễn của Chuẩn đầu ra môn học.
• Gọi VORUi là vector biểu diễn của Yêu cầu nghềnghiệp (từO*NET).
Mức độtương đồng vềmặt ý nghĩa giữa hai đoạn văn bản được định lượng thông qua
độđo **Cosine Similarity** (Độtương đồng Cosine) [18] giữa hai vector:
Sim(LO,ORUi) =
VLO ·VORUi
∥VLO∥∥VORUi∥
(5.2)
Cơ chếlọc ngưỡng (Thresholding):
Kết quảcủa phép tính trên là một giá trịvô hướng biểu thịđộgần gũi ngữnghĩa. Để
đảm bảo độtin cậy của mô hình tính điểm, chúng em thiết lập một tham sốngưỡng θ
(thường chọn θ ≥0.6). Hệthống sẽloại bỏcác cặp so sánh có độtương đồng thấp hơn
ngưỡng này (coi như nhiễu), chỉgiữlại các kết quảcó sựliên kết ngữnghĩa chặt chẽđể
đưa vào công thức tính toán.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 111/254

=== SUMMARY ===
Đoạn văn này trình bày kỹ thuật So khớp Ngữnghĩa (Semantic Matching) nhằm giải quyết sự khác biệt về ngôn ngữ diễn đạt giữa chuẩn đầu ra môn học và yêu cầu nghề nghiệp O*NET. Hệ thống sử dụng mô hình Gemini Embedding 004 để chuyển đổi văn bản thành vector trong không gian đa chiều, nơi khoảng cách hình học phản ánh ý nghĩa cốt lõi. Độ tương đồng được đo bằng Cosine Similarity kết hợp với cơ chế lọc ngưỡng (θ ≥ 0.6) để đảm bảo độ chính xác, loại bỏ các liên kết nhiễu.

=== REVIEW QUESTIONS ===
1. Tại sao các phương pháp so khớp từ khóa truyền thống lại không hiệu quả trong việc liên kết chuẩn đầu ra với yêu cầu nghề nghiệp?
2. Kỹ thuật Text Embedding (sử dụng Gemini Embedding 004) giúp nhận diện sự tương đồng giữa các văn bản dựa trên yếu tố nào thay vì mặt chữ?
3. Độ đo Cosine Similarity được sử dụng như thế nào để định lượng mức độ tương đồng giữa chuẩn đầu ra và yêu cầu nghề nghiệp?
4. Mục đích của việc thiết lập tham số ngưỡng θ trong cơ chế lọc ngưỡng (Thresholding) là gì?','5cb76724-bb25-4044-9267-a34cf287a068'::uuid,NULL,NULL,130,694,'2026-03-21 13:35:13.343662+07'),
	 ('8f72b952-d55a-4436-86b9-d74d8821b3d5'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,130,'=== ORIGINAL CONTENT ===
5.3.6
Công thức tính Tổng giá trị
Giá trị thực tiễn của một môn học không được đo đếm bằng số lượng kiến thức hàn
lâm, mà bằng khả năng giải quyết các vấn đề cụ thể của nghề nghiệp.
1. Giá trị của một Chuẩn đầu ra (ValLO)
Một chuẩn đầu ra (LO) có thể đáp ứng cùng lúc nhiều yêu cầu khác nhau của nghề
nghiệp (ví dụ: vừa cung cấp kiến thức nền, vừa rèn luyện kỹ năng thao tác, vừa hướng
dẫn sử dụng công cụ). Do đó, giá trị của nó là tổng hòa của tất cả các đóng góp này.
Công thức được định nghĩa như sau:
ValLO = ∑
i∈Ω
(Sim(LO,ORUi)×SIM(ORUi))
(5.3)
Trong đó:
• Sim(LO,ORUi): Là độ tương đồng ngữ nghĩa giữa chuẩn đầu ra và yêu cầu nghề
nghiệp (đã qua lọc ngưỡng).
• SIM(ORUi): Là điểm tầm quan trọng của yêu cầu nghề nghiệp đó (thang 0-100).
• ∑: Phép cộng dồn trên toàn bộ tập dữ liệu O*NET.
Cơ chế cộng dồn giá trị:
Nếu một nội dung giảng dạy có tính ứng dụng cao, ứng dụng được vào nhiều khía
cạnh của công việc (ví dụ: học lập trình Python giúp sinh viên vừa thỏa mãn yêu cầu về
Kỹ năng tư duy, vừa thực hiện được Nhiệm vụ phân tích, và biết dùng Công nghệ), thì
điểm số sẽ được cộng dồn từ tất cả các khía cạnh đó.
2. Tổng giá trị của Môn học (ValSub ject)
Cuối cùng, mức độ đóng góp của toàn bộ môn học đối với vị trí công việc là tổng
điểm của tất cả các chuẩn đầu ra thành phần cộng lại:
ValSub ject =
M
∑
k=1
ValLOk
Chỉ số ValSub ject càng cao, chứng tỏ nội dung môn học càng bao phủ được nhiều yêu
cầu cốt lõi và quan trọng của nghề nghiệp mục tiêu.

=== SUMMARY ===
Đoạn văn bản giải thích phương pháp định lượng giá trị thực tiễn của môn học đối với nghề nghiệp. Giá trị của mỗi chuẩn đầu ra (ValLO) được tính bằng tổng tích số giữa độ tương đồng ngữ nghĩa và mức độ quan trọng của các yêu cầu công việc từ O*NET. Tổng giá trị môn học (ValSubject) là kết quả cộng dồn tất cả các ValLO thành phần. Chỉ số này phản ánh mức độ bao phủ và tính ứng dụng của nội dung đào tạo đối với các yêu cầu thực tế trong công việc.

=== REVIEW QUESTIONS ===
1. Giá trị của một Chuẩn đầu ra (ValLO) được tính toán dựa trên những thành phần nào trong công thức?
2. Cơ chế cộng dồn giá trị trong hệ thống có ý nghĩa gì khi một nội dung giảng dạy đáp ứng nhiều khía cạnh công việc khác nhau?
3. Làm thế nào để xác định Tổng giá trị của một môn học (ValSubject) từ các chuẩn đầu ra thành phần?
4. Một môn học có chỉ số ValSubject cao thể hiện điều gì về mối quan hệ giữa nội dung giảng dạy và nghề nghiệp mục tiêu?','fcd136bc-3363-481a-91a6-86f21f37b337'::uuid,NULL,NULL,131,607,'2026-03-21 13:35:13.344566+07'),
	 ('3cce4c51-e927-4ab5-920d-80d671bbebfe'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,131,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
5.4
Xây dựng lộtrình học cá nhân hóa cho sinh viên
Trong phần này, do thiếu dữliệu thực tếcho toàn bộcác ngành trong trường, nhóm
tập trung vào việc xây dựng thuật toán gợi ý lộtrình học cá nhân hoá cho sinh viên Khoa
Khoa học và Kỹthuật Máy tính (Computer Science and Engineering – CSE).
Thuật toán làm việc trên chương trình đào tạo chuẩn (ví dụ: 4 năm với 8 học kỳchính)
và cho phép cá nhân hoá theo các tham số:
• GPA mục tiêu.
• Thời gian dựkiến tốt nghiệp.
• Sốtín chỉtối đa/tối thiểu mỗi kỳmà sinh viên có thể/ muốn đăng ký.
• Định hướng nghềnghiệp (học sâu một sốnhóm môn, chọn nhánh chuyên ngành,...).
• Mức độưu tiên: ra trường đúng hạn, tối ưu GPA, hay tối đa hoá kiến thức chuyên
sâu.
Thuật toán được thiết kếđểáp dụng cho cả:
• Sinh viên mới bắt đầu (chưa học bất kỳhọc phần nào).
• Sinh viên đang học dởchương trình (đã hoàn thành một sốhọc phần).
Tổng thể, quá trình xây dựng lộtrình gồm hai pha chính:
1. Xây dựng lộtrình cơ bản và độưu tiên môn học từchương trình chuẩn.
2. Điều chỉnh lộtrình theo tiến độhiện tại và nguyện vọng cụthểcủa sinh viên (rút
ngắn, học hè, kéo dài).
5.4.1
Pha 1: Xây dựng lộtrình cơ bản và độưu tiên môn học
Bước 1: Lập lộtrình cơ bản từchương trình chuẩn.
Trước tiên, dựa trên chương trình đào tạo chuẩn (ví dụchương trình 4 năm với 8 học
kỳchính), hệthống xây dựng một lộtrình học tập cơ bản cho sinh viên. Lộtrình này
phân bổtất cảcác môn học bắt buộc và các môn tựchọn vào 8 kỳchính, tuân thủđầy
đủcác ràng buộc tiên quyết, song hành và khuyến nghịnhư đã nêu (môn tiên quyết được
xếp trước môn phụthuộc, môn song hành có thểhọc cùng kỳnếu khối lượng hợp lý, và
ưu tiên sắp xếp theo thứtựmôn học khuyến nghịđểhỗtrợnền tảng kiến thức).
Khi phân bổcác môn tựchọn (các nhóm môn mà sinh viên phải chọn một sốmôn để
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 113/254

=== SUMMARY ===
Đoạn văn bản trình bày phương pháp xây dựng lộ trình học tập cá nhân hóa cho sinh viên ngành Khoa học và Kỹ thuật Máy tính. Thuật toán dựa trên chương trình đào tạo chuẩn và cho phép tùy chỉnh theo các tham số cá nhân như GPA mục tiêu, thời gian tốt nghiệp và định hướng nghề nghiệp. Quy trình gồm hai pha: xây dựng lộ trình cơ bản tuân thủ các ràng buộc tiên quyết, và điều chỉnh lộ trình dựa trên tiến độ học tập thực tế cùng nguyện vọng của sinh viên.

=== REVIEW QUESTIONS ===
1. Thuật toán xây dựng lộ trình cá nhân hóa dựa trên những tham số đầu vào nào của sinh viên?
2. Hai pha chính trong quá trình xây dựng lộ trình học tập được mô tả như thế nào?
3. Trong bước lập lộ trình cơ bản, hệ thống cần tuân thủ những loại ràng buộc môn học nào?
4. Thuật toán này được thiết kế để áp dụng cho những đối tượng sinh viên cụ thể nào?','56a2e8bb-0b82-4c6f-b4ce-ee596e332733'::uuid,NULL,NULL,132,696,'2026-03-21 13:35:13.344566+07'),
	 ('b834d473-e2b1-4a7e-be7b-6b17cc8089b6'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,132,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
đủsốtín chỉyêu cầu), hệthống sẽlựa chọn các môn cụthểtrong những nhóm này dựa
trên mục tiêu ưu tiên của sinh viên:
• Ưu tiên chuyên sâu kiến thức (theo nghềnghiệp mục tiêu): Nếu sinh viên đặt mục
tiêu hàng đầu là trang bịkiến thức chuyên sâu cho một nghềnghiệp cụthể, thì từ
mỗi nhóm môn tựchọn hệthống sẽchọn những môn có giá trịcao nhất đối với
nghềnghiệp mục tiêu đó. Trường hợp sinh viên chưa xác định rõ nghềnghiệp mong
muốn, hệthống sẽchọn những môn có giá trịtrung bình cao nhất đối với các nghề
nghiệp thuộc ngành học (tức là những môn được đánh giá mang lại nhiều lợi ích
nhất trên mặt bằng chung cho ngành). Các giá trịnày được xác định sẵn bởi thuật
toán đánh giá giá trịcủa môn học đối với nghềnghiệp (được trình bày ởphần khác
của hệthống).
• Ưu tiên GPA cao hoặc tốt nghiệp đúng hạn: Nếu mục tiêu chính của sinh viên là đạt
điểm trung bình (GPA) cao hoặc tốt nghiệp đúng hạn (tức tập trung vào hiệu quả
học tập và tránh rủi ro kéo dài thời gian), hệthống sẽưu tiên chọn những môn dễ
đạt điểm cao nhất trong mỗi nhóm môn tựchọn. Cụthể, dựa trên mô hình dựđoán
điểm sốcủa sinh viên cho các môn, chọn các môn mà điểm sốdựkiến của sinh viên
là cao nhất – nói cách khác, những môn mà sinh viên có khảnăng đạt điểm tốt nhất
sẽđược chọn đểvừa cải thiện GPA vừa tăng xác suất qua môn đúng tiến độ.
Sau khi lựa chọn xong toàn bộcác môn tựchọn theo nguyên tắc trên, kết hợp cùng
các môn bắt buộc, ta thu được danh sách đầy đủtất cảcác môn học mà sinh viên cần
hoàn thành, kèm theo kếhoạch học chi tiết trong từng học kỳ.
Bước 2: Gán độưu tiên cho từng học phần.
Từlộtrình cơ bản, mỗi môn học m được gán một độưu tiên sơ cấp dựa trên thời điểm
dựkiến học môn đó trong chương trình chuẩn. Giảsửchương trình chuẩn kéo dài 4 năm
với 8 học kỳchính, ta quy ước độưu tiên như sau:
• Năm 1 – HK1 (học kỳ1 năm nhất) có độưu tiên = 1 (cao nhất, nên học sớm nhất).
• Năm 1 – HK2 có độưu tiên = 2.
• Năm 2 – HK1 có độưu tiên = 3, . . .
• Năm 4 – HK2 (học kỳchính cuối) có độưu tiên = 8 (thấp nhất, học muộn nhất).
Ký hiệu Năm(m) là năm dựkiến học môn m, và Kỳ(m) ∈{1,2} là học kỳtrong năm
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 114/254

=== SUMMARY ===
Đoạn trích hướng dẫn cách hệ thống lựa chọn môn tự chọn và gán độ ưu tiên cho học phần trong lộ trình học tập cá nhân hóa. Việc chọn môn tự chọn dựa trên hai mục tiêu chính của sinh viên: chuyên sâu kiến thức nghề nghiệp hoặc tối ưu hóa GPA và thời gian tốt nghiệp. Ngoài ra, mỗi môn học được gán độ ưu tiên sơ cấp từ 1 đến 8 dựa theo học kỳ dự kiến trong chương trình đào tạo chuẩn để đảm bảo tính tuần tự của kế hoạch học tập.

=== REVIEW QUESTIONS ===
1. Hệ thống sẽ dựa trên tiêu chí nào để lựa chọn môn học tự chọn cho sinh viên ưu tiên chuyên sâu kiến thức nghề nghiệp?
2. Trong trường hợp sinh viên chưa xác định rõ nghề nghiệp mục tiêu, các môn tự chọn sẽ được lựa chọn dựa trên cơ sở nào?
3. Để hỗ trợ mục tiêu đạt GPA cao, hệ thống sử dụng mô hình nào để gợi ý các môn học cho sinh viên?
4. Độ ưu tiên sơ cấp của một môn học được quy định như thế nào dựa trên thời gian dự kiến trong chương trình chuẩn 4 năm?','db6f4354-66e3-4a40-b8c5-5d33c9972c46'::uuid,NULL,NULL,133,799,'2026-03-21 13:35:13.344566+07'),
	 ('fb963c8f-5509-4c36-bb96-a430671aa459'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,133,'=== ORIGINAL CONTENT ===
đó. Độưu tiên sơ cấp của m được tính bằng công thức:
priority1(m) = 2×
 Năm(m)−1

+Kỳ(m).
Bên cạnh đó, đểso sánh mức độ“cần thiết” giữa các môn dựkiến học cùng một học
kỳ, mỗi môn được gán thêm một độưu tiên thứcấp dựa trên nhóm môn học của nó.
Nguyên tắc: nhóm môn nào nên học sớm hơn sẽcó trọng sốưu tiên nhỏhơn (tức ưu tiên
cao hơn). Bảng dưới đây liệt kê các nhóm môn chính và trọng sốưu tiên đềxuất cho
từng nhóm:
Bảng 5.2: Trọng sốưu tiên thứcấp theo nhóm môn học.
Nhóm môn học
Trọng sốưu tiên nhóm
Kinh tế, Chính trị, Xã hội, Pháp luật
1
Giáo dục Quốc phòng
2
Giáo dục Thểchất
3
Kiến thức Cơ sởngành
4
Toán và Khoa học Tựnhiên
5
Ngoại ngữ
6
Nhập môn ngành
7
Kiến thức Ngành
8
Kỹnăng mềm, Quản lý, Kinh tếKỹthuật
9
Chuyên ngành
10
Tựchọn chuyên ngành
11
Tựchọn tựdo
12
Học phần tốt nghiệp
13
Khi sắp xếp kếhoạch, ta sẽso sánh môn học trước tiên theo độưu tiên sơ cấp, sau đó
theo độưu tiên thứcấp. Chẳng hạn, hai môn đều dựkiến học kỳ5 (cùng ưu tiên sơ cấp),
môn nào thuộc nhóm có trọng số4 sẽđược xếp trước môn thuộc nhóm có trọng số6.
Bước 3: Xác định các môn đầu vào cho lộtrình cá nhân hóa.
Với một sinh viên cụthể, từbảng điểm hiện tại và lựa chọn của sinh viên vềcác môn
muốn học lại, hệthống xác định tập môn còn phải học như sau:
• Đối với mỗi môn trong chương trình đào tạo:
– Nếu môn chưa đạt (chưa học hoặc đã học nhưng không đủđiểm qua) ⇒được
xem là chưa hoàn thành.
– Nếu môn đã đạt nhưng sinh viên chủđộng chọn học lại (ví dụđểcải thiện điểm)
⇒cũng được xem là còn phải học.
– Các môn đã đạt và sinh viên không có nhu cầu học lại ⇒xem là đã hoàn thành.
Kết quảcủa bước này là một tập môn Cremain gồm toàn bộcác học phần mà hệthống
cần sắp xếp vào các học kỳcòn lại trong Pha 2 (bao gồm cảmôn nợvà môn học lại).

=== SUMMARY ===
Đoạn văn bản trình bày cách xác định độ ưu tiên và danh sách môn học cần hoàn thành để xây dựng lộ trình học tập cá nhân hóa. Hệ thống sử dụng độ ưu tiên sơ cấp (theo học kỳ chuẩn) và độ ưu tiên thứ cấp (theo nhóm môn học) để sắp xếp thứ tự ưu tiên học tập. Đồng thời, bước xác định môn đầu vào giúp lọc ra các môn chưa đạt hoặc môn sinh viên muốn học lại để đưa vào danh sách sắp xếp trong các giai đoạn tiếp theo.

=== REVIEW QUESTIONS ===
1. Công thức tính độ ưu tiên sơ cấp priority1(m) dựa trên những yếu tố nào của chương trình chuẩn?
2. Theo bảng trọng số ưu tiên thứ cấp, nhóm môn học nào được ưu tiên sắp xếp học sớm nhất và nhóm nào học muộn nhất?
3. Trong trường hợp hai môn học có cùng độ ưu tiên sơ cấp, hệ thống sẽ căn cứ vào đâu để quyết định môn nào được xếp trước?
4. Những tiêu chí nào được sử dụng để xác định một môn học thuộc tập hợp ''môn còn phải học'' (Cremain)?','424fdd06-ed17-4b0a-a5a7-9ce42e064025'::uuid,NULL,NULL,134,664,'2026-03-21 13:35:13.344566+07');
INSERT INTO coaching_chatbot.lecture_knowledge_chunks (lecture_knowledge_chunks_id,lecture_knowledge_id,chunk_index,chunk_content,qdrant_point_id,start_time_seconds,end_time_seconds,page_number,token_count,created_at) VALUES
	 ('4fe3dd54-6c3d-4d3d-ab85-b9012ae3c43b'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,134,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
– Nếu môn chưa đạt (chưa học hoặc đã học nhưng không đủđiểm qua) ⇒được
xem là chưa hoàn thành.
– Nếu môn đã đạt nhưng sinh viên chủđộng chọn học lại (ví dụđểcải thiện điểm)
⇒cũng được xem là còn phải học.
– Các môn đã đạt và sinh viên không có nhu cầu học lại ⇒xem là đã hoàn thành.
Kết quảcủa bước này là một tập môn Cremain gồm toàn bộcác học phần mà hệthống
cần sắp xếp vào các học kỳcòn lại trong Pha 2 (bao gồm cảmôn nợvà môn học lại).
Với mỗi môn m ∈Cremain, ta đã biết sẵn:
• Vịtrí dựkiến trong lộtrình cơ bản (năm/kỳchuẩn).
• Cặp độưu tiên được gán ởBước 2.
Danh sách Cremain cùng với thông tin trên sẽlà đầu vào cho Pha 2, nơi thuật toán tiến
hành phân bổlại các môn vào từng học kỳcụthể.
5.4.2
Pha 2: Điều chỉnh lộtrình theo tiến độvà nguyện vọng sinh
viên
Pha 2 sẽlấy lộtrình cơ bản và danh sách môn cần học Cremain từPha 1 làm đầu vào,
kết hợp với mục tiêu học tập và khối lượng học mỗi kỳmà sinh viên đã thiết lập trong
UC-02 (Thiết lập mục tiêu học tập). Nhiệm vụcủa Pha 2 là phân chia lại các môn này
vào các học kỳcòn lại sao cho:
• Phù hợp với tiến độthực tế(các môn đã qua, môn nợ, môn học lại).
• Thỏa các ràng buộc tiên quyết, song hành và chuỗi môn.
• Không vượt quá khối lượng học tối đa mà sinh viên đã chọn cho từng loại học kỳ
(chính/hè) trong UC-02.
Sau UC-02, hệthống đã xác định:
• Có M′ học kỳdựkiến còn lại (tính cảhè nếu sinh viên có kếhoạch học hè), được
sắp xếp theo thứtựthời gian: HK1,HK2,...,HKM′.
• Với mỗi học kỳHKk, biết được:
– Loại học kỳ(học kỳchính hay học kỳhè).
– Mức cường độhọc mà sinh viên đã chọn (Nhẹnhàng/Trung bình/Khá/Nặng đối
với kỳchính; Thấp/Trung bình/Cao đối với kỳhè).
Từđó, hệthống ánh xạra một giới hạn tín chỉcho từng học kỳHKk, Cap(k), chính
là mốc trên của khoảng khối lượng học mà sinh viên đã chọn trong UC-02 (đồng thời
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 116/254

=== SUMMARY ===
Đoạn văn bản mô tả quy trình xác định tập môn học cần hoàn thành (Cremain) và Pha 2 của việc điều chỉnh lộ trình học tập cá nhân hóa. Hệ thống xác định các môn chưa đạt hoặc cần học lại để phân bổ vào các học kỳ còn lại. Pha 2 sử dụng thông tin này cùng với mục tiêu về cường độ học tập của sinh viên để sắp xếp lịch học, đảm bảo tuân thủ các ràng buộc về môn tiên quyết, chuỗi môn và giới hạn tín chỉ tối đa cho mỗi học kỳ.

=== REVIEW QUESTIONS ===
1. Tập môn học ''Cremain'' được hình thành dựa trên những tiêu chí nào đối với tình trạng học tập của sinh viên?
2. Mục tiêu chính của Pha 2 trong quy trình điều chỉnh lộ trình học tập là gì?
3. Những ràng buộc nào cần được thỏa mãn khi phân chia môn học vào các học kỳ còn lại trong Pha 2?
4. Giới hạn tín chỉ Cap(k) cho mỗi học kỳ được hệ thống xác định dựa trên những thông tin đầu vào nào?','d58293f8-9962-4522-ab4a-22e138b4a5e8'::uuid,NULL,NULL,135,706,'2026-03-21 13:35:13.345564+07'),
	 ('e1f9ed62-fbf1-4077-a00d-92a251294bed'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,135,'=== ORIGINAL CONTENT ===
không vượt quá giới hạn quy chế).
Ví dụ: nếu sinh viên chọn mức “Khá” cho học kỳchính (13 < x ≤17) thì với các
học kỳchính tương ứng ta có Cap(k) = 17; nếu chọn mức “Trung bình” cho học kỳhè
(4 < x ≤7) thì với các học kỳhè tương ứng ta có Cap(k) = 7.
Bước 1: Chuẩn bịthông tin và kiểm tra sơ bộ
Trước khi phân bổchi tiết vào từng học kỳ, hệthống:
1. Lấy danh sách các học kỳcòn lại HK1,...,HKM′ và giới hạn tín chỉCap(k) tương
ứng từkết quảUC-02, bao gồm cả:
• Cường độhọc tập mỗi học kỳchính.
• Sốkỳhè sẽhọc và cường độhọc hè (nếu có).
2. Lấy danh sách môn cần học Cremain từBước 3 Pha 1, kèm theo:
• Sốtín chỉcr(m) của từng môn.
• Cặp độưu tiên của từng môn (theo lộtrình chuẩn và nhóm môn).
Trong toàn bộcác bước phân môn phía sau, ta luôn đảm bảo rằng sốtín chỉđăng ký
ởmỗi học kỳHKk không vượt quá mốc lớn nhất mà sinh viên đã chọn cho kỳđó, tức là:
load(k) ≤Cap(k)
∀k = 1,...,M′.
Điều này đảm bảo lộtrình sinh ra luôn tôn trọng khối lượng học mục tiêu mà sinh viên
đã đặt trong UC-02.
Bước 2: Khởi tạo cấu trúc lộtrình mới.
Tạo khung M′ học kỳtrống (gồm các học kỳchính và hè theo kếhoạch) đểsẵn sàng
xếp môn. Mỗi học kỳcó một giới hạn tín chỉdựa vào mục tiêu học tập của sinh viên.
Sắp xếp một sốhọc phần cốđịnh như giáo dục quốc phòng, thực tập ngoài trường,... Xác
định rõ học kỳtốt nghiệp cuối cùng (HKM′) sẽchứa học phần tốt nghiệp (đồán/khóa
luận), và học kỳáp cuối (nếu HKM′ là hè thì lấy kỳchính trước đó, nếu HKM′ là kỳchính
thì chính là kỳliền trước) dựkiến đểđồán chuyên ngành. Các học phần đặc biệt này sẽ
được cốđịnh vào các kỳtương ứng vềcuối lộtrình.
Bước 3: Tính độưu tiên cho các môn chưa hoàn thành.
Sửdụng độưu tiên sơ cấp và thứcấp từpha 1 cho từng môn chưa hoàn thành:
• priority1(m) – thứtựhọc kỳdựkiến theo chương trình chuẩn (đã có công thức ở
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 117/254

=== SUMMARY ===
Đoạn văn bản mô tả ba bước đầu tiên của Pha 2 trong quy trình xây dựng lộ trình học tập cá nhân hóa. Nội dung tập trung vào việc xác định giới hạn tín chỉ (Cap) dựa trên nguyện vọng sinh viên, chuẩn bị danh sách môn học còn thiếu (Cremain), và khởi tạo khung lộ trình. Hệ thống sẽ cố định các học phần đặc biệt như giáo dục quốc phòng và đồ án tốt nghiệp vào các kỳ tương ứng, đồng thời sử dụng các mức độ ưu tiên để chuẩn bị sắp xếp các môn học còn lại.

=== REVIEW QUESTIONS ===
1. Giới hạn tín chỉ Cap(k) của mỗi học kỳ được xác định dựa trên cơ sở nào?
2. Hệ thống cần lấy những thông tin cụ thể nào từ danh sách Cremain để chuẩn bị cho việc phân bổ môn học?
3. Trong Bước 2, những loại học phần nào được ưu tiên sắp xếp cố định vào khung lộ trình?
4. Việc đảm bảo load(k) ≤ Cap(k) có ý nghĩa như thế nào đối với lộ trình học tập của sinh viên?','69267f06-2999-4a9c-93b3-a2dc87900739'::uuid,NULL,NULL,136,683,'2026-03-21 13:35:13.345986+07'),
	 ('46d20c82-a871-43c5-a56d-73efd0818a16'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,136,'=== ORIGINAL CONTENT ===
pha 1).
• priority2(m) – trọng sốnhóm môn học (theo bảng ởpha 1).
Dựa vào hai mức ưu tiên này, ta có thểsắp xếp các môn theo thứtựưu tiên chung:
môn nào có priority1 nhỏhơn sẽưu tiên trước; nếu bằng nhau thì so sánh priority2 (nhỏ
hơn nghĩa là ưu tiên hơn).
Bước 4: Phân bổmôn học vào các học kỳcòn lại.
Thuật toán duyệt tuần tựtừng học kỳtừhiện tại đến HKM′ và phân môn theo thứtự
ưu tiên. Gọi HKk là học kỳđang xét (với k = 1 là học kỳkếtiếp sắp học, k = M′ là kỳ
cuối). Tại mỗi HKk, thực hiện các bước:
(a) Lọc ra tập môn chưa hoàn thành và chưa được xếp, thỏa đồng thời:
• Môn được mởtrong loại học kỳtương ứng với HKk (chính/hè).
• Tất cảcác môn tiên quyết bắt buộc của nó đã hoàn thành, hoặc đang/ sẽđược xếp
trong cùng HKk nếu quy định cho phép học song hành.
(b) Sắp xếp tập môn đủđiều kiện ởtrên theo độưu tiên:
• Ưu tiên tăng dần theo priority1(m) (kỳdựkiến trong chương trình chuẩn).
• Nếu trùng priority1, sắp tăng dần theo priority2(m) (trọng sốnhóm môn).
• Nếu vẫn trùng, có thểưu tiên tiếp những môn có ˆg(m) cao hơn (môn được dựđoán
là dễđạt điểm hơn).
(c) Nếu gặp các các chuỗi môn học liên kết (tiên quyết, khuyến nghị, song hành):
Sau khi chọn môn cho HKk ởbước (a) và (b), nếu trong danh sách môn vừa xếp vào
HKk có môn nào là môn đầu tiên của một chuỗi môn liên kết (chuỗi tiên quyết hoặc
khuyến nghị), hoặc một môn có yêu cầu song hành với môn khác, thì tiến hành cốđịnh
lịch cho cảchuỗi môn đó ngay. Điều này nhằm đảm bảo các môn trong chuỗi được phân
bổliên tục hợp lý và không bịquên ởcác bước sau. Cách xửlý chi tiết:
• Đối với chuỗi môn tiên quyết:
Giảsửphát hiện môn m1 là môn đầu của một chuỗi tiên quyết
{m1 →m2 →··· →mn}
(nghĩa là m1 là tiên quyết của m2, m2 tiên quyết của m3, . . . ).
Ta tiến hành xếp cốđịnh các môn tiếp theo m2,m3,...,mn lần lượt như sau: với mỗi
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 118/254

=== SUMMARY ===
Đoạn văn bản mô tả các bước chi tiết trong Pha 2 của thuật toán sắp xếp lộ trình học tập cho sinh viên. Nội dung tập trung vào việc xác định độ ưu tiên dựa trên chương trình chuẩn (priority1) và nhóm môn (priority2). Thuật toán thực hiện phân bổ môn học vào các học kỳ còn lại bằng cách lọc môn đủ điều kiện, sắp xếp ưu tiên và đặc biệt xử lý các chuỗi môn tiên quyết để đảm bảo tính liên tục và khả thi của kế hoạch học tập.

=== REVIEW QUESTIONS ===
1. Dựa vào đâu để xác định priority1 và priority2 cho mỗi môn học?
2. Trong Bước 4(a), những điều kiện nào giúp một môn học được chọn để phân bổ vào học kỳ đang xét?
3. Nếu hai môn học có cùng priority1 và priority2, tiêu chí phụ nào có thể được dùng để ưu tiên sắp xếp?
4. Thuật toán xử lý chuỗi môn tiên quyết {m1 -> m2 -> ... -> mn} như thế nào sau khi đã xếp được môn m1 vào học kỳ HKk?','550e7161-a02e-4361-b005-e17f0544efb0'::uuid,NULL,NULL,137,688,'2026-03-21 13:35:13.345986+07'),
	 ('4771fb73-1c99-4918-8e5a-c49298214d4c'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,137,'=== ORIGINAL CONTENT ===
môn mi+1 trong chuỗi, xếp vào học kỳsớm nhất sau học kỳcủa mi mà còn chỗ
trống. Thông thường sẽlà ngay HKt+1 nếu mi ởHKt, nhưng nếu HKt+1 đã đầy thì
có thểlùi sang HKt+2. Tiếp tục đến khi xếp hết môn mn. Mọi môn trong chuỗi này
sau khi xếp đều được đánh dấu đã cốđịnh lịch và sẽkhông bịđiều chỉnh nữa trong
các bước sau.
Trong quá trình xếp chuỗi tiên quyết, nếu phát hiện không thểxếp hết chuỗi vào các
học kỳcòn lại (ví dụdo quá tải hoặc không còn kỳ), lúc này ta sẽquay lại kỳtrước
đó HKk−1 đểchọn lại môn cho kỳnày, trong đó ưu tiên xếp m1 vào HKk−1, và tiếp
tục chạy thuật toán.
Trong trường hợp không thểxếp m1 vào HKk mà không còn kỳtrước đểlùi, thuật
toán kết luận lộtrình hiện tại không khảthi với cấu hình khối lượng học đã đặt. Hãy
thửđiều chỉnh lại mục tiêu học tập (UC-02) đểcó lộtrình khảthi hơn.
• Đối với chuỗi môn khuyến nghị:
Xửlý tương tựchuỗi tiên quyết nhưng linh hoạt hơn vì đây không phải ràng buộc
bắt buộc. Khi một môn m1 là đầu chuỗi khuyến nghịđược chọn vào HKk, thuật toán
cũng kiểm tra độdài chuỗi so với sốkỳcòn lại. Nếu chuỗi khuyến nghị
{m1,m2,...,mn}
có n > (M′ −k +1) (tức nếu học tuần tựtừng môn một thì không đủkỳ), ta không
nhất thiết phải đẩy m1 lên kỳtrước, mà có thểsắp xếp dồn một sốmôn trong chuỗi
vào cùng một kỳđểtiết kiệm thời gian. Nguyên tắc phân bổcho chuỗi khuyến nghị:
– Cốgắng xếp các môn theo đúng thứtựchuỗi, mỗi môn một kỳnối tiếp nếu có
đủkỳ.
– Nếu sốkỳcòn lại không đủ, cho phép một kỳhọc hai môn liên tiếp trong chuỗi
(đặc biệt nếu nội dung hai môn không quá nặng khi học cùng nhau). Ví dụ:
chuỗi 3 môn dựkiến cho 3 kỳliên tiếp, nhưng chỉcòn 2 kỳ, thì có thểxếp m1
và m2 cùng HKk, rồi m3 ởHKk+1.
– Nếu vẫn không thểxếp đủ(do quá tải hoặc không còn kỳ), thửđẩy m1 lên các
kỳtrước (tương tựchuỗi tiên quyết).
Sau khi xếp được vịtrí cho toàn bộchuỗi khuyến nghị, đánh dấu cốđịnh lịch các

=== SUMMARY ===
Đoạn văn bản hướng dẫn chi tiết cách thuật toán phân bổ các chuỗi môn học có liên kết (tiên quyết và khuyến nghị) vào lộ trình học tập. Đối với môn tiên quyết, thuật toán xếp tuần tự và sử dụng cơ chế quay lui nếu thiếu thời gian. Đối với môn khuyến nghị, quy trình linh hoạt hơn khi cho phép ghép nhiều môn vào cùng một học kỳ để đảm bảo hoàn thành chương trình. Cả hai trường hợp đều hướng tới việc cố định lịch học nhằm đảm bảo tính khả thi của lộ trình.

=== REVIEW QUESTIONS ===
1. Trong trường hợp học kỳ kế tiếp đã đầy, thuật toán sẽ xử lý việc xếp môn tiếp theo trong chuỗi tiên quyết như thế nào?
2. Cơ chế quay lui (backtracking) được thực hiện khi nào nếu chuỗi môn tiên quyết không thể xếp hết vào các học kỳ còn lại?
3. Sự khác biệt cơ bản trong cách xử lý giữa chuỗi môn khuyến nghị và chuỗi môn tiên quyết khi gặp giới hạn về thời gian là gì?
4. Khi số học kỳ còn lại ít hơn số môn trong chuỗi khuyến nghị, thuật toán áp dụng nguyên tắc nào để tối ưu hóa lộ trình?','af258c09-9f21-4438-a396-28c5a46cfd1b'::uuid,NULL,NULL,138,715,'2026-03-21 13:35:13.345986+07'),
	 ('bcd17204-e884-46ce-827d-9d122bb4c1f9'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,138,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
môn này đểtránh xáo trộn vềsau.
• Đối với môn song hành:
Nếu một môn m vừa xếp có ràng buộc song hành với môn m′:
– Hai môn m và m′ bắt buộc phải được học cùng một học kỳ. Thuật toán ưu tiên
thửxếp cảhai vào chính HKk:
* m và m′ đều được mởởHKk (nếu HKk là học kỳhè thì cảhai phải nằm
trong danh sách môn có thểmởhè), và
load(k)+cr(m)+cr(m′) ≤Cap(k),
có thểloại bớt các môn khác ít ưu tiên hơn trong HKk nếu cần đểđủchỗ.
– Nếu không thểxếp cùng ởHKk (do một trong hai không mởkỳnày hoặc không
đủchỗtín chỉdù đã loại bớt các môn khác), thuật toán sẽlần lượt xét các học
kỳkếtiếp HKk+1,HKk+2,... và tìm học kỳđầu tiên HKt thỏa các điều kiện nêu
trên. Khi tìm được HKt như vậy, cảhai môn m và m′ sẽđược xếp cùng vào Lt.
Nếu duyệt hết các học kỳcòn lại mà vẫn không tìm được HKt thoảmãn, thuật
toán kết luận lộtrình hiện tại không khảthi với cấu hình khối lượng học đã đặt.
(d) Hoàn tất kỳhiện tại và chuyển tiếp:
Kết thúc phân môn cho HKk, lúc này kỳđã bao gồm các môn cần học.
Nếu tổng tín chỉtrong HKk vẫn thấp (dưới ngưỡng mong muốn) do sinh viên đăng ký
học nhẹhoặc không còn môn đểhọc, phần tín chỉtrống đó sẽđược bỏtrống.
Bước 5: Xuất kết quảlộtrình
Hệthống xuất ra danh sách các học kỳtừhiện tại đến HKM′, kèm các học phần sẽ
học trong từng kỳ. Sinh viên có thểxem xét lộtrình và nếu cần thiết, điều chỉnh lại tham
số(mục tiêu GPA, thời gian học, môn tựchọn, . . . ) rồi chạy lại thuật toán đểcó lộtrình
mới. Lộtrình cũng có thểđược cập nhật định kỳsau mỗi học kỳ(dựa trên kết quảhọc
tập thực tế) đểphản ánh chính xác tiến độcủa sinh viên.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 120/254

=== SUMMARY ===
Đoạn văn mô tả quy trình xử lý môn học song hành và bước hoàn tất lộ trình học tập cá nhân hóa. Thuật toán đảm bảo các môn song hành được xếp cùng kỳ nếu thỏa mãn điều kiện mở lớp và giới hạn tín chỉ; nếu không, lộ trình sẽ được coi là không khả thi. Bước cuối cùng là xuất lộ trình chi tiết đến khi tốt nghiệp, cho phép sinh viên điều chỉnh tham số hoặc cập nhật định kỳ dựa trên kết quả thực tế để tối ưu hóa kế hoạch học tập.

=== REVIEW QUESTIONS ===
1. Theo thuật toán, hai môn học có ràng buộc song hành phải thỏa mãn những điều kiện cụ thể nào để được xếp vào cùng một học kỳ?
2. Trong trường hợp không thể xếp các môn song hành vào học kỳ hiện tại, thuật toán sẽ thực hiện các bước tiếp theo như thế nào?
3. Kết quả đầu ra của Bước 5 cung cấp cho sinh viên những thông tin gì về lộ trình học tập?
4. Việc cập nhật lộ trình định kỳ sau mỗi học kỳ mang lại lợi ích gì cho sinh viên trong quá trình đào tạo?','e8b3eb32-e4e3-4b71-b099-ee4c453891c6'::uuid,NULL,NULL,139,663,'2026-03-21 13:35:13.345986+07'),
	 ('9089f6d0-232a-46d7-85c5-c569772d6037'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,139,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
5.4.3
Kết luận
Thuật toán gợi ý lộtrình học cá nhân hoá trên đây giúp biến chương trình đào tạo chuẩn thành một kếhoạch học tập linh hoạt, phù hợp với từng sinh viên cụthể. Bằng cách kết hợp thông tin vềmục tiêu (GPA, thời gian tốt nghiệp, định hướng nghềnghiệp, mức độcường độhọc), tiến độthực tế(môn đã qua, môn nợ, môn học lại) và các ràng buộc học vụ(tiên quyết, song hành, mởlớp theo kỳ), hệthống có thểtựđộng xây dựng một lộtrình rõ ràng cho từng học kỳcòn lại.
Cách thiết kếtheo hai pha — tạo lộtrình cơ bản rồi điều chỉnh theo cá nhân — vừa đảm bảo tuân thủkhung chương trình chung của Khoa, vừa cho phép tối ưu theo ưu tiên riêng của sinh viên (ra trường đúng hạn, tối ưu GPA hay chuyên sâu kiến thức). Lộtrình cũng có thểđược cập nhật định kỳsau mỗi học kỳ, giúp sinh viên luôn có một “lộtrình học tập” mới, sát với kết quảthực tếvà hỗtrợhọra quyết định đăng ký học phần một cách chủđộng, tựtin và hiệu quảhơn.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 121/254

=== SUMMARY ===
Phần kết luận trình bày về thuật toán gợi ý lộ trình học cá nhân hóa, giúp chuyển đổi chương trình đào tạo chuẩn thành kế hoạch linh hoạt cho từng sinh viên. Thuật toán kết hợp các mục tiêu cá nhân, tiến độ thực tế và ràng buộc học vụ thông qua hai pha thiết kế: tạo lộ trình cơ bản và điều chỉnh cá nhân. Hệ thống đảm bảo tuân thủ khung chương trình chung đồng thời tối ưu hóa lợi ích riêng của sinh viên, hỗ trợ ra quyết định đăng ký học phần hiệu quả.

=== REVIEW QUESTIONS ===
1. Thuật toán gợi ý lộ trình học cá nhân hóa dựa trên những thông tin mục tiêu và tiến độ thực tế nào của sinh viên?
2. Cấu trúc thiết kế hai pha của thuật toán mang lại những lợi ích song song nào?
3. Các ràng buộc học vụ nào được hệ thống tự động tính toán khi xây dựng lộ trình học tập?
4. Việc cập nhật lộ trình định kỳ sau mỗi học kỳ có ý nghĩa như thế nào đối với sinh viên?','234d8cb7-50c8-4cbf-a86a-8ffd949cb64b'::uuid,NULL,NULL,140,494,'2026-03-21 13:35:13.347016+07'),
	 ('a2fb4242-e8e5-4b33-a2bb-21adb2c25669'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,140,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Flowchart tổng quát của thuật toán
Hình 5.4: Flowchart của thuật toán xây dựng lộtrình học cá nhân hóa
Hình 5.4 minh họa flowchart tổng quát của thuật toán xây dựng lộtrình học cá nhân
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 122/254

=== SUMMARY ===
Đoạn văn bản giới thiệu Hình 5.4, trình bày sơ đồ quy trình (flowchart) tổng quát của thuật toán xây dựng lộ trình học tập cá nhân hóa cho sinh viên. Sơ đồ này đóng vai trò minh họa trực quan cho các bước logic và trình tự xử lý của thuật toán nhằm tạo ra kế hoạch học tập linh hoạt, đáp ứng nhu cầu và điều kiện thực tế của từng cá nhân dựa trên khung chương trình đào tạo.

=== REVIEW QUESTIONS ===
1. Hình 5.4 minh họa nội dung cụ thể nào trong hệ thống xây dựng lộ trình học tập?
2. Vai trò của flowchart trong việc mô tả thuật toán xây dựng lộ trình cá nhân hóa là gì?
3. Thuật toán được nhắc đến trong đoạn văn bản nhằm mục đích giải quyết vấn đề gì cho sinh viên?
4. Dựa vào ngữ cảnh tài liệu, flowchart này là một phần của báo cáo đồ án chuyên ngành nào?','9d5fbd97-1b5c-4521-970c-d8ae9dc6c07e'::uuid,NULL,NULL,141,285,'2026-03-21 13:35:13.347016+07'),
	 ('ac680f16-6868-489c-9803-aa65d03cc67f'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,141,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
ở pha 1.
Hình 5.5: Flowchart của thuật toán xây dựng lộtrình học cá nhân hóa
Hình 5.5 minh họa flowchart tổng quát của thuật toán xây dựng lộtrình học cá nhân
ở pha 2.
Báo cáo đồ án chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 123/254

=== SUMMARY ===
Đoạn văn bản giới thiệu về Hình 5.5, cung cấp sơ đồ quy trình (flowchart) cho thuật toán xây dựng lộ trình học tập cá nhân hóa. Thuật toán này được chia làm hai giai đoạn chính: pha 1 và pha 2, giúp sinh viên hình dung cách hệ thống xử lý để tạo ra kế hoạch học tập riêng biệt. Nội dung này thuộc báo cáo đồ án chuyên ngành tại Khoa Khoa học & Kỹ thuật Máy tính, trường Đại học Bách khoa TP.HCM.

=== REVIEW QUESTIONS ===
1. Hình 5.5 trong tài liệu minh họa cho nội dung cụ thể nào?
2. Theo đoạn trích, thuật toán xây dựng lộ trình học cá nhân hóa được thực hiện qua mấy pha?
3. Mục tiêu cuối cùng của thuật toán được nhắc đến trong sơ đồ là gì?
4. Tài liệu này thuộc báo cáo của học phần nào và dành cho đối tượng nào?','c668c584-3c0e-4ac9-8d8b-e3e4a548c700'::uuid,NULL,NULL,142,271,'2026-03-21 13:35:13.347016+07'),
	 ('4c4df713-cdc1-454f-a4dc-2983dba8d75a'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,142,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
5.5
Kho dữliệu tri thức môn học cho Coaching Chatbot
Hình 5.6: Mô hình RAG kết hợp LLM và kho dữliệu tri thức [4]
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 124/254

=== SUMMARY ===
Đoạn văn này giới thiệu về việc xây dựng kho dữ liệu tri thức cho Coaching Chatbot thông qua mô hình RAG (Retrieval-Augmented Generation). Kiến trúc này cho phép kết hợp sức mạnh suy luận của các mô hình ngôn ngữ lớn (LLM) với nguồn dữ liệu thực tế từ tài liệu môn học. Việc ứng dụng RAG giúp chatbot đưa ra những phản hồi chính xác, có căn cứ và bám sát chương trình đào tạo, đồng thời khắc phục hạn chế về sự sai lệch thông tin của các mô hình AI thông thường.

=== REVIEW QUESTIONS ===
1. Mô hình RAG được đề cập trong văn bản là viết tắt của thuật ngữ nào và vai trò của nó là gì?
2. Tại sao Coaching Chatbot cần phải kết hợp LLM với một kho dữ liệu tri thức riêng biệt?
3. Việc sử dụng kiến trúc RAG mang lại lợi ích gì cho sinh viên khi tương tác với chatbot?
4. Dựa trên hình 5.6, các thành phần chính cấu thành nên hệ thống trả lời thông minh này là gì?','b08950d0-f03e-4882-b589-a488a761ede8'::uuid,NULL,NULL,143,292,'2026-03-21 13:35:13.347016+07'),
	 ('69be3330-81de-410b-bdcd-6e0672157a43'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,143,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Đểxây dựng một kho dữliệu tri thức cho các tài nguyên môn học (giáo trình, slide,
đềbài, video, ...) được lưu trữtrong cơ sởdữliệu PostgreSQL và S3 Bucket. Một Job
Agent có nhiệm vụđịnh kỳtruy vấn đểlấy danh sách tài nguyên môn học mới, sau đó
khởi tạo các job xửlý. Mỗi tài liệu sẽđược đưa qua bước Preprocessing Document để
trích xuất nội dung văn bản, làm sạch dữliệu và chia nhỏthành các đoạn văn phù hợp.
Các đoạn văn này được mã hóa thành vector nhúng (embedding) bằng mô hình Gemini
Embedding 001 và được lưu trữvào Qdrant Vector Database. Đây là bước xây dựng chỉ
mục vector cho toàn bộkho tri thức.
Vềphía xửlý truy vấn người dùng, khi người dùng gửi câu hỏi cho chatbot, truy vấn
được đưa vào mô hình Gemini Embedding 001 đểtạo ra query embedding. Vector truy
vấn này được dùng đểthực hiện similarity search trên Qdrant, nhằm tìm ra các đoạn tài
liệu có độtương đồng cao nhất với câu hỏi (top-K matches) [19]. Các tài liệu này có thể
tiếp tục đi qua một Reranking Pipeline đểsắp xếp lại theo mức độliên quan trước khi
trảvềtập Retrieved Documents cuối cùng.
Ởbước cuối, các tài liệu đã truy xuất được kết hợp với câu hỏi người dùng đểtạo
query context và prompt template đầu vào cho LLM Model. Mô hình LLM sửdụng ngữ
cảnh này đểsinh ra câu trảlời tựnhiên, chính xác hơn so với việc chỉdựa trên tri thức
nội bộcủa mô hình. Câu trảlời sau đó được gửi lại cho người dùng thông qua giao diện
chatbot.
Nhờkiến trúc RAG như trên, Coaching Chatbot có thểtận dụng đồng thời sức mạnh
suy luận của LLM và độtin cậy của kho dữliệu tri thức môn học, giúp trảlời câu hỏi
bám sát nội dung học phần, dễcập nhật và kiểm soát hơn.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 125/254

=== SUMMARY ===
Văn bản mô tả quy trình xây dựng kho dữ liệu tri thức và vận hành Coaching Chatbot dựa trên kiến trúc RAG. Tài liệu từ PostgreSQL và S3 được tiền xử lý, chuyển thành vector bằng Gemini Embedding 001 và lưu trữ tại Qdrant. Khi người dùng đặt câu hỏi, hệ thống tìm kiếm tài liệu tương đồng, sắp xếp lại qua Reranking Pipeline và kết hợp với LLM để tạo câu trả lời. Kiến trúc này giúp chatbot phản hồi chính xác, cập nhật và bám sát nội dung học tập.

=== REVIEW QUESTIONS ===
1. Các bước chính trong quy trình tiền xử lý tài liệu (Preprocessing Document) trước khi đưa vào kho tri thức là gì?
2. Mô hình Gemini Embedding 001 và cơ sở dữ liệu Qdrant đóng vai trò gì trong việc xử lý truy vấn của người dùng?
3. Mục đích của việc sử dụng Reranking Pipeline trong quy trình truy xuất tài liệu là gì?
4. Tại sao kiến trúc RAG lại giúp câu trả lời của chatbot chính xác hơn so với việc chỉ dựa vào tri thức nội bộ của mô hình LLM?','dc5cdef3-6d62-4b03-a34f-d04ff754a0dc'::uuid,NULL,NULL,144,685,'2026-03-21 13:35:13.347016+07');
INSERT INTO coaching_chatbot.lecture_knowledge_chunks (lecture_knowledge_chunks_id,lecture_knowledge_id,chunk_index,chunk_content,qdrant_point_id,start_time_seconds,end_time_seconds,page_number,token_count,created_at) VALUES
	 ('d59bc201-254d-419b-8ab3-7b66d1a32b5e'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,144,'=== ORIGINAL CONTENT ===
Trường Đại Học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa Học & KỹThuật Máy Tính
Chương 6
Thiết KếGiao Diện Người Dùng
Báo cáo này mô tảchi tiết các giao diện người dùng được thiết kếcho phần mềm quản
lý học tập (LMS) của trường Đại học Bách khoa TP.HCM (HCMUT). Tài liệu tập trung
vào các giao diện dành cho sinh viên, bao gồm các chức năng chính như quản lý lớp học,
làm bài tập, xem điểm, quản lý lộtrình học tập và các tính năng hỗtrợkhác.
6.1
Giao diện sinh viên
6.1.1
Dashboard
6.1.1.1
Dashboard
Dashboard là trang chủchính của sinh viên, cung cấp cái nhìn tổng quan vềcác hoạt
động học tập, lớp học đang tham gia, thông báo mới nhất và các thông tin quan trọng
khác. Giao diện Dashboard được minh hoạởhình 6.1.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 126/254

=== SUMMARY ===
Chương 6 trình bày chi tiết về thiết kế giao diện người dùng cho hệ thống quản lý học tập (LMS) của trường Đại học Bách khoa TP.HCM. Nội dung tập trung vào trải nghiệm của sinh viên, đặc biệt là trang Dashboard. Đây là giao diện trung tâm giúp sinh viên theo dõi tổng quan các hoạt động học tập, quản lý lớp học, lộ trình cá nhân và cập nhật thông báo quan trọng, từ đó hỗ trợ quản lý việc học tập một cách hiệu quả và trực quan.

=== REVIEW QUESTIONS ===
1. Mục tiêu chính của Chương 6 trong báo cáo đồ án này là gì?
2. Hệ thống quản lý học tập (LMS) được đề cập trong văn bản dành cho đối tượng nào sử dụng?
3. Trang Dashboard cung cấp những loại thông tin tổng quan nào cho sinh viên?
4. Kể tên ít nhất ba chức năng chính được tích hợp trong giao diện sinh viên của hệ thống LMS này.','11fbcefd-006e-4e72-900f-3c7344003498'::uuid,NULL,NULL,145,404,'2026-03-21 13:35:13.347016+07'),
	 ('ebf67420-f970-4712-b08e-e4e4fb008b1b'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,145,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Hình 6.1: Dashboard - Trang chủcủa sinh viên
Đặc điểm chính:
• Thanh điều hướng trái liệt kê đầy đủcác chức năng học tập và hồsơ, giúp truy cập
nhanh.
• Lời chào cá nhân hóa và thông điệp động thúc đẩy học tập hằng ngày.
• Khối KPI học tập: tiến độtổng, GPA hiện tại, đếm khóa tăng cường.
• Biểu đồcột thống kê thời gian học và kiểm tra trực tuyến theo tháng, hỗtrợđánh
giá xu hướng.
• Lịch tháng kèm sựkiện giúp sinh viên theo dõi lịch quan trọng.
• Bộlọc mốc thời gian, ô tìm kiếm và danh sách sựkiện "Hôm nay"với thao tác Vào
học/Làm ngay.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 127/254

=== SUMMARY ===
Đoạn văn mô tả chi tiết các thành phần chính của giao diện Dashboard dành cho sinh viên trong hệ thống quản lý học tập (LMS). Dashboard cung cấp cái nhìn tổng quan thông qua thanh điều hướng, lời chào cá nhân hóa, các chỉ số KPI học tập (GPA, tiến độ), và biểu đồ thống kê thời gian học. Ngoài ra, giao diện còn tích hợp lịch cá nhân, bộ lọc thời gian và danh sách các sự kiện quan trọng trong ngày, giúp sinh viên quản lý lộ trình học tập hiệu quả và thuận tiện.

=== REVIEW QUESTIONS ===
1. Các chỉ số KPI học tập nào được hiển thị trên giao diện Dashboard của sinh viên?
2. Thanh điều hướng bên trái của giao diện có vai trò gì trong việc hỗ trợ người dùng?
3. Biểu đồ cột trên Dashboard cung cấp thông tin gì để giúp sinh viên đánh giá xu hướng học tập?
4. Danh sách sự kiện "Hôm nay" hỗ trợ sinh viên thực hiện những thao tác nhanh nào?','4c4fba6e-90c5-45d6-a007-f96c57bd4b5c'::uuid,NULL,NULL,146,395,'2026-03-21 13:35:13.347016+07'),
	 ('f54e5411-f13e-428f-bce4-4af8e344dadc'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,146,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
• Khối Tài nguyên liệt kê tài liệu gần nhất, cho phép tải nhanh và xem thêm.
• Danh sách lớp gần đây dạng card có nút Vào học, hỗtrợtiếp tục học liền mạch.
6.1.2
Quản Lý Lớp Học
6.1.2.1
Nội Dung Lớp Học với Chatbot
Giao diện hiển thịchi tiết nội dung của một lớp học cụthể, bao gồm các bài học, tài
liệu và tích hợp chatbot hỗtrợhọc tập. Giao diện này được minh hoạởhình 6.2.
Hình 6.2: Nội dung lớp học với tích hợp Chatbot
Đặc điểm chính:
• Tabs Khóa học/Bài tập/Điểm số/Diễn đàn giúp chuyển nhanh giữa nội dung, bài tập
và thảo luận.
• Sidebar dạng accordion quản lý chương/bài và trạng thái truy cập, thuận tiện điều
hướng.
• Trình phát video có điều khiển tốc độ, tua, transcript đểhỗtrợhọc đa phương thức.
• Khu Transcript/Ghi chú đặt ngay dưới nội dung, hỗtrợghi chú song song.
• Chatbot tích hợp bên phải cho phép đặt câu hỏi ngay trong lúc học.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 128/254

=== SUMMARY ===
Đoạn văn bản mô tả chi tiết các thành phần trong giao diện Quản lý lớp học của sinh viên trên hệ thống LMS. Nội dung tập trung vào giao diện nội dung lớp học tích hợp Chatbot, bao gồm các tính năng như: khối tài nguyên, danh sách lớp gần đây, hệ thống tab điều hướng nhanh, sidebar quản lý bài học dạng accordion, trình phát video hỗ trợ đa phương thức và khu vực ghi chú/transcript giúp sinh viên tối ưu hóa việc học tập và tương tác.

=== REVIEW QUESTIONS ===
1. Khối Tài nguyên và Danh sách lớp gần đây hỗ trợ gì cho quá trình truy cập của sinh viên?
2. Các tab điều hướng trong giao diện nội dung lớp học bao gồm những chức năng chính nào?
3. Trình phát video được thiết kế với những tính năng gì để hỗ trợ học tập đa phương thức?
4. Vị trí và vai trò của Chatbot và mục Ghi chú trong giao diện học tập giúp ích gì cho sinh viên?','2fa14055-7938-41bd-999a-01a234a80358'::uuid,NULL,NULL,147,470,'2026-03-21 13:35:13.348001+07'),
	 ('343bdb3e-00df-43b5-a447-03d3cb919c49'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,148,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Hình 6.4: Quiz với Chatbot hỗtrợ
Đặc điểm chính:
• Tabs điều hướng Khóa học/Bài tập/Điểm số/Diễn đàn, kèm sidebar nội dung giúp
chuyển ngữcảnh nhanh.
• Thông tin cấu hình quiz (sốcâu, thời gian, sốlần làm, ngưỡng đạt, quyền xem lại)
hiển thịtập trung.
• Lịch mở/đóng và nhắc hoàn thành giúp quản lý tiến độnộp.
• CTA bắt đầu làm bài và chatbot hỗtrợtrong quá trình làm.
6.1.3.2
Quiz Code
Giao diện làm bài quiz vềlập trình với môi trường code editor tích hợp. Giao diện này
được minh hoạởhình 6.5.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 130/254

=== SUMMARY ===
Phân đoạn này mô tả chi tiết hai loại giao diện bài kiểm tra: Quiz với sự hỗ trợ của Chatbot và Quiz Code. Giao diện Quiz thông thường cung cấp đầy đủ thông tin cấu hình như số câu hỏi, thời gian, lịch đóng/mở và tích hợp chatbot để hỗ trợ sinh viên. Trong khi đó, Quiz Code được thiết kế chuyên biệt cho việc lập trình với môi trường soạn thảo mã nguồn tích hợp (code editor), giúp sinh viên thực hiện các bài đánh giá kỹ năng code một cách thuận tiện.

=== REVIEW QUESTIONS ===
1. Những thông tin cấu hình cụ thể nào của bài Quiz được hiển thị tập trung cho người học?
2. Vai trò của chatbot trong giao diện làm bài Quiz là gì?
3. Hệ thống giúp sinh viên quản lý tiến độ nộp bài thông qua những tính năng nào?
4. Đặc điểm khác biệt lớn nhất của giao diện Quiz Code so với Quiz thông thường là gì?','07fe0f87-fac8-402b-a2a8-39662953fa6e'::uuid,NULL,NULL,149,373,'2026-03-21 13:35:13.349004+07'),
	 ('def1b920-8467-4b2f-85a6-6baf71ac6cb9'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,149,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Hình 6.5: Quiz Code - Giao diện làm bài lập trình
Đặc điểm chính:
• Đềbài code kèm ràng buộc và ví dụgiúp xác định yêu cầu rõ ràng.
• Hỗtrợtimer và điều hướng câu hỏi đểkiểm soát tiến độlàm bài.
• Trình soạn thảo code tô màu cú pháp, chạy kiểm tra và phản hồi test case.
• Bảng test case so sánh kết quảchạy với đáp án chuẩn, hỗtrợchấm tựđộng.
6.1.4
Khóa Học Tăng Cường
6.1.4.1
Danh Sách Các Khóa Học Tăng Cường
Giao diện hiển thịdanh sách các khóa học tăng cường mà sinh viên có thểđăng ký để
nâng cao kiến thức. Giao diện này được minh hoạởhình 6.6.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 131/254

=== SUMMARY ===
Đoạn văn bản mô tả chi tiết hai tính năng trong hệ thống học tập: giao diện làm bài Quiz Code và danh sách khóa học tăng cường. Quiz Code cung cấp môi trường lập trình với trình soạn thảo thông minh, bộ đếm thời gian và hệ thống tự động chấm điểm qua test case. Trong khi đó, phần khóa học tăng cường hỗ trợ sinh viên tìm kiếm và đăng ký các chương trình bổ trợ nhằm nâng cao kiến thức chuyên môn.

=== REVIEW QUESTIONS ===
1. Giao diện Quiz Code cung cấp những công cụ nào để hỗ trợ sinh viên trong quá trình viết mã nguồn?
2. Làm thế nào sinh viên có thể kiểm soát tiến độ và thời gian khi thực hiện bài kiểm tra lập trình?
3. Vai trò của bảng test case trong giao diện Quiz Code là gì?
4. Mục đích của việc thiết kế giao diện Danh sách các Khóa học Tăng cường cho sinh viên là gì?','b72c4882-6f74-44a3-ad43-3e3fc43fcccf'::uuid,NULL,NULL,150,382,'2026-03-21 13:35:13.349004+07'),
	 ('2c35aad7-76a3-4d82-954c-f5fb42f34a12'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,150,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹ thuật Máy tính
Hình 6.6: Danh sách các khóa học tăng cường
Đặc điểm chính:
• Tìm kiếm, lọc/sắp xếp khóa học tăng cường theo nhu cầu.
• Phân tách rõ các khóa đã tham gia và khóa gợi ý theo năm học.
• Card khóa học cung cấp nhanh tên môn, mã lớp, giảng viên, nút vào chi tiết.
6.1.4.2
Giới Thiệu Khóa Học Tăng Cường
Trang chi tiết giới thiệu vềmột khóa học tăng cường cụthể, bao gồm mô tả, nội dung,
giảng viên và cách đăng ký. Giao diện này được minh hoạởhình 6.7.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 132/254

=== SUMMARY ===
Đoạn văn bản mô tả các tính năng chính của giao diện danh sách và chi tiết khóa học tăng cường trong hệ thống quản lý học tập. Sinh viên có thể tìm kiếm, lọc và phân loại các khóa học đã tham gia hoặc được gợi ý dựa trên năm học. Mỗi khóa học được hiển thị qua các thẻ thông tin tóm tắt và trang chi tiết đầy đủ về mô tả, nội dung, giảng viên cũng như quy trình đăng ký, giúp tối ưu hóa việc nâng cao kiến thức chuyên môn.

=== REVIEW QUESTIONS ===
1. Giao diện danh sách các khóa học tăng cường cung cấp những công cụ nào để sinh viên dễ dàng tìm kiếm nội dung phù hợp?
2. Hệ thống phân loại các khóa học tăng cường dựa trên những tiêu chí nào để giúp sinh viên quản lý tiến độ?
3. Những thông tin định danh quan trọng nào xuất hiện trên mỗi thẻ (card) khóa học?
4. Khi truy cập vào trang giới thiệu chi tiết của một khóa học, sinh viên sẽ tìm thấy những thông tin cụ thể gì?','e8b98b73-5de2-4c00-9ef2-9d3bb12e00c7'::uuid,NULL,NULL,151,380,'2026-03-21 13:35:13.349004+07'),
	 ('95f30d56-7ae7-4590-a0e7-26b2658afcea'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,151,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Hình 6.7: Trang giới thiệu khóa học tăng cường
Đặc điểm chính:
• Hero video kèm thông tin giảng viên và nút phát đểxem giới thiệu khóa.
• Điểm đánh giá tổng, phân bốsao chi tiết và tổng sốlượt đánh giá.
• CTA "Tham Gia Ngay", mô tảkhóa và danh sách lợi ích/đầu ra học tập.
• Nhận xét người học với thời gian và nội dung, hỗtrợtham khảo chất lượng.
6.1.5
LộTrình Học Tập Cá Nhân
6.1.5.1
LộTrình Học Cá Nhân
Giao diện hiển thịlộtrình học tập cá nhân đã được tạo cho sinh viên. Giao diện này
được minh hoạởhình 6.8.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 133/254

=== SUMMARY ===
Đoạn văn bản này mô tả chi tiết các thành phần của giao diện giới thiệu khóa học tăng cường và lộ trình học tập cá nhân. Trang giới thiệu khóa học cung cấp các thông tin trực quan như video, đánh giá, mô tả lợi ích và phản hồi từ người học khác để sinh viên dễ dàng quyết định tham gia. Ngoài ra, tài liệu cũng giới thiệu về phần lộ trình học tập cá nhân, giúp sinh viên theo dõi kế hoạch học tập được thiết kế riêng cho bản thân.

=== REVIEW QUESTIONS ===
1. Các thành phần chính xuất hiện trên trang giới thiệu khóa học tăng cường là gì?
2. Làm thế nào sinh viên có thể tham khảo chất lượng của khóa học trước khi đăng ký?
3. Thông tin về giảng viên và video giới thiệu khóa học được trình bày như thế nào trên giao diện?
4. Giao diện lộ trình học cá nhân (hình 6.8) có vai trò gì đối với sinh viên?','932522e7-9235-413b-8590-34fbfbd3a8fb'::uuid,NULL,NULL,152,377,'2026-03-21 13:35:13.349004+07'),
	 ('706689c7-3e24-427b-9bc3-5c3622750d92'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,152,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Hình 6.8: Lộtrình học cá nhân
Đặc điểm chính:
• Thông điệp hướng dẫn thiết lập mục tiêu học tập và CTA bắt đầu ngay.
• Nêu rõ lợi ích: xác định chuyên ngành, đặt mục tiêu GPA, nhận lộtrình tối ưu, theo
dõi/điều chỉnh.
6.1.5.2
Tạo LộTrình Học Cá Nhân
Giao diện bắt đầu quá trình tạo lộtrình học tập cá nhân. Giao diện này được minh hoạ
ởhình 6.9.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 134/254

=== SUMMARY ===
Đoạn văn bản giới thiệu về tính năng Lộ trình học cá nhân dành cho sinh viên, bao gồm các thông điệp hướng dẫn thiết lập mục tiêu học tập và nút kêu gọi hành động để bắt đầu. Nội dung nhấn mạnh các lợi ích như xác định chuyên ngành, đặt mục tiêu GPA, nhận lộ trình học tối ưu và khả năng theo dõi, điều chỉnh kế hoạch. Ngoài ra, tài liệu còn đề cập đến giao diện khởi đầu cho quy trình tạo lộ trình này tại Hình 6.9.

=== REVIEW QUESTIONS ===
1. Mục tiêu chính của giao diện Lộ trình học cá nhân được mô tả trong Hình 6.8 là gì?
2. Kể tên ít nhất ba lợi ích mà sinh viên nhận được khi sử dụng tính năng lộ trình học tập cá nhân.
3. Thành phần nào trong giao diện thúc đẩy sinh viên bắt đầu thiết lập mục tiêu ngay lập tức?
4. Hình 6.9 trong tài liệu minh họa cho bước nào trong quy trình xây dựng lộ trình học tập?','cb4196ae-e52f-49ca-9419-f432e7a82a7f'::uuid,NULL,NULL,153,339,'2026-03-21 13:35:13.349004+07'),
	 ('20610d78-547b-4ecc-b1bd-641f0e350664'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,153,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Hình 6.9: Tạo lộtrình học cá nhân - Màn hình bắt đầu
Đặc điểm chính:
• Mởđầu wizard tạo lộtrình, kêu gọi nhập thông tin mục tiêu.
• CTA khởi động quy trình cá nhân hóa lộtrình.
6.1.5.3
Tạo LộTrình - Bước 2
Giao diện bước 2 trong quá trình tạo lộtrình học tập. Giao diện này được minh hoạở
hình 6.10.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 135/254

=== SUMMARY ===
Đoạn văn bản giới thiệu các bước khởi đầu trong quy trình tạo lộ trình học tập cá nhân hóa cho sinh viên. Màn hình bắt đầu (Hình 6.9) đóng vai trò là một trình hướng dẫn (wizard) yêu cầu người dùng nhập thông tin mục tiêu và sử dụng nút kêu gọi hành động (CTA) để bắt đầu quy trình. Tài liệu cũng dẫn dắt sang Bước 2 (Hình 6.10), cho thấy cấu trúc phân cấp và trình tự logic của hệ thống trong việc hỗ trợ sinh viên thiết lập kế hoạch học tập.

=== REVIEW QUESTIONS ===
1. Mục đích chính của màn hình bắt đầu (Hình 6.9) trong quy trình tạo lộ trình học tập là gì?
2. Nút CTA trong giao diện khởi đầu có chức năng cụ thể như thế nào?
3. Hệ thống sử dụng thành phần giao diện nào để hướng dẫn người dùng nhập thông tin mục tiêu?
4. Theo tài liệu, giao diện của Bước 2 trong quá trình tạo lộ trình được minh họa tại hình số mấy?','52e86eaa-02a8-4a8a-be5d-87453ab57c07'::uuid,NULL,NULL,154,330,'2026-03-21 13:35:13.349004+07'),
	 ('7485af61-5330-44a4-bdf9-e4b5e26ed617'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,154,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Hình 6.10: Tạo lộtrình học cá nhân - Bước 2
Đặc điểm chính:
• Form bước 2: GPA mục tiêu, thời gian tốt nghiệp, sốtín chỉ/khóa học mỗi kỳ.
• Chọn cường độhọc, kếhoạch học hè, định hướng nghềnghiệp, ưu tiên học tập (kéo
thả).
• Đánh dấu chứng chỉđã hoàn thành (TOEIC, MOS Excel, Python Certificate).
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 136/254

=== SUMMARY ===
Nội dung này mô tả chi tiết Bước 2 trong quy trình thiết lập lộ trình học tập cá nhân của sinh viên. Người dùng sẽ nhập các thông số quan trọng như GPA mục tiêu, thời gian tốt nghiệp và số tín chỉ mỗi kỳ. Đồng thời, sinh viên có thể tùy chỉnh cường độ học, kế hoạch học hè, định hướng nghề nghiệp và sắp xếp thứ tự ưu tiên học tập. Ngoài ra, việc cập nhật các chứng chỉ đã đạt được (TOEIC, MOS, Python) giúp hệ thống tối ưu hóa lộ trình cá nhân.

=== REVIEW QUESTIONS ===
1. Những thông số mục tiêu cụ thể nào mà sinh viên cần nhập trong form ở Bước 2?
2. Người dùng có thể thực hiện những tùy chọn nào để cá nhân hóa cường độ và kế hoạch học tập của mình?
3. Thao tác ''kéo thả'' được áp dụng cho mục thông tin nào trong giao diện này?
4. Việc đánh dấu các chứng chỉ đã hoàn thành (như TOEIC hay Python) có ý nghĩa gì trong việc tạo lộ trình?','df54688b-d8ed-4b55-9ac7-c68d017176ae'::uuid,NULL,NULL,155,333,'2026-03-21 13:35:13.349004+07');
INSERT INTO coaching_chatbot.lecture_knowledge_chunks (lecture_knowledge_chunks_id,lecture_knowledge_id,chunk_index,chunk_content,qdrant_point_id,start_time_seconds,end_time_seconds,page_number,token_count,created_at) VALUES
	 ('edf94d39-5366-4589-bf09-0e8fb983e5a6'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,155,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
6.1.5.4
Tạo LộTrình - Bước 3-1
Giao diện bước 3 trong quá trình tạo lộtrình học tập. Giao diện này được minh hoạở
hình 6.11.
Hình 6.11: Tạo lộtrình học cá nhân - Bước 3
Đặc điểm chính:
• Bước đánh giá khảthi: hiển thịtín chỉcòn lại, sốkỳtối thiểu, GPA cần đạt/kỳ.
• Cảnh báo vấn đề(thiếu tín chỉ/kỳ) và gợi ý điều chỉnh (kéo dài thời gian, học hè).
• Hành động "Quay Lại Điều Chỉnh"hoặc "Tiếp Tục".
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 137/254

=== SUMMARY ===
Nội dung này mô tả Bước 3 trong quy trình tạo lộ trình học tập cá nhân hóa, tập trung vào việc đánh giá tính khả thi của kế hoạch học tập. Hệ thống hiển thị các thông số quan trọng như số tín chỉ còn lại, số kỳ học tối thiểu và mức GPA cần thiết mỗi kỳ. Nếu lộ trình không khả thi, hệ thống sẽ đưa ra cảnh báo và gợi ý điều chỉnh (như học hè hoặc kéo dài thời gian) để người dùng cân nhắc trước khi tiếp tục.

=== REVIEW QUESTIONS ===
1. Mục đích chính của Bước 3 trong quy trình tạo lộ trình học tập là gì?
2. Giao diện Bước 3 hiển thị những thông số cụ thể nào để đánh giá tính khả thi của lộ trình?
3. Hệ thống sẽ đưa ra những gợi ý điều chỉnh nào khi phát hiện vấn đề về số lượng tín chỉ mỗi kỳ?
4. Người dùng có thể thực hiện những hành động nào sau khi xem kết quả đánh giá ở Bước 3?','31821a72-fa11-43f4-8a93-9e1b6af59b78'::uuid,NULL,NULL,156,346,'2026-03-21 13:35:13.350014+07'),
	 ('f7759db9-1576-4a83-8d5a-8ccbeddb229c'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,156,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
6.1.5.5
Tạo LộTrình - Bước 3-2
Giao diện bước 3-2 (phiên bản mởrộng) trong quá trình tạo lộtrình học tập. Giao
diện này được minh hoạởhình 6.12.
Hình 6.12: Tạo lộtrình học cá nhân - Bước 3-2
Đặc điểm chính:
• Xác nhận mục tiêu khảthi khi thông sốđáp ứng yêu cầu.
• Tóm tắt tín chỉcòn lại, sốkỳtối thiểu, GPA cần đạt/kỳ.
6.1.5.6
Tạo LộTrình - Bước 4
Giao diện bước cuối cùng trong quá trình tạo lộtrình học tập. Giao diện này được
minh hoạởhình 6.13.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 138/254

=== SUMMARY ===
Nội dung này mô tả các giai đoạn cuối trong quy trình tạo lộ trình học tập cá nhân hóa. Bước 3-2 tập trung vào việc xác nhận tính khả thi của mục tiêu học tập bằng cách tóm tắt các chỉ số quan trọng như số tín chỉ còn lại, số học kỳ tối thiểu và mức GPA cần đạt mỗi kỳ. Bước 4 là bước cuối cùng để hoàn tất giao diện tạo lộ trình, giúp người dùng chốt kế hoạch học tập của mình.

=== REVIEW QUESTIONS ===
1. Tại bước 3-2, hệ thống dựa vào điều kiện gì để xác nhận mục tiêu học tập là khả thi?
2. Các thông số cụ thể nào được tóm tắt trong giao diện bước 3-2?
3. Hình 6.12 và 6.13 trong tài liệu lần lượt minh họa cho những bước nào?
4. Vai trò chính của bước 4 trong quy trình tạo lộ trình học tập là gì?','e83947f7-ebb0-45c4-8c92-184029a60fe0'::uuid,NULL,NULL,157,337,'2026-03-21 13:35:13.350014+07'),
	 ('c873e973-5831-450e-8303-5e8f310a915b'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,157,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Hình 6.13: Tạo lộtrình học cá nhân - Bước 4
Đặc điểm chính:
• Bước xem lại: chuyên ngành, GPA mục tiêu, thời gian tốt nghiệp, cường độ, học hè,
định hướng nghềnghiệp.
• Liệt kê ưu tiên học tập và trạng thái chứng chỉ.
• CTA "Hoàn Tất"đểchốt lộtrình.
6.1.5.7
LộTrình Sau Khi Tạo
Giao diện hiển thịlộtrình học tập sau khi đã được tạo thành công. Giao diện này được
minh hoạởhình 6.14.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 139/254

=== SUMMARY ===
Nội dung này trình bày bước cuối cùng (Bước 4) trong quy trình thiết lập lộ trình học tập cá nhân và giới thiệu giao diện lộ trình sau khi hoàn tất. Tại bước 4, người dùng thực hiện kiểm tra tổng thể các thông số như chuyên ngành, GPA mục tiêu, thời gian tốt nghiệp và định hướng nghề nghiệp. Sau khi nhấn ''Hoàn Tất'', hệ thống sẽ chính thức xác lập và hiển thị lộ trình học tập chi tiết (Hình 6.14) để sinh viên theo dõi.

=== REVIEW QUESTIONS ===
1. Những thông số quan trọng nào sinh viên cần xem lại tại Bước 4 trước khi chốt lộ trình học tập?
2. Hành động (CTA) cụ thể nào dùng để hoàn tất quy trình tạo lộ trình học tập?
3. Ngoài các mục tiêu học tập, Bước 4 còn liệt kê thêm các trạng thái và ưu tiên gì?
4. Giao diện được minh họa ở hình 6.14 hiển thị nội dung gì sau khi quy trình tạo kết thúc?','45058cb0-6807-4025-a93a-f33e430e73cf'::uuid,NULL,NULL,158,345,'2026-03-21 13:35:13.350014+07'),
	 ('9c35b5ca-b151-4a09-99ae-bdcbaddeb664'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,158,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Hình 6.14: Lộtrình học tập sau khi tạo
Đặc điểm chính:
• Timeline năm/kỳ(Y1–Y4) kèm GPA dựđoán và khối lượng tín chỉ.
• Mỗi kỳliệt kê môn, tín chỉ, trạng thái bắt buộc/tựchọn và tiên quyết.
• Tóm tắt mục tiêu: GPA mục tiêu, thời gian tốt nghiệp, khối lượng học kỳ.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 140/254

=== SUMMARY ===
Nội dung này mô tả các thành phần chính của giao diện lộ trình học tập sau khi được thiết lập thành công. Giao diện cung cấp cái nhìn tổng quan thông qua dòng thời gian từ năm nhất đến năm tư, hiển thị GPA dự đoán và khối lượng tín chỉ. Người dùng có thể theo dõi chi tiết từng môn học về số tín chỉ, tính chất bắt buộc hay tự chọn và điều kiện tiên quyết. Ngoài ra, hệ thống còn tóm tắt các mục tiêu quan trọng như GPA đích và thời gian tốt nghiệp dự kiến.

=== REVIEW QUESTIONS ===
1. Dòng thời gian (Timeline) trong giao diện lộ trình học tập hiển thị các giai đoạn nào?
2. Những thông số dự đoán nào được hiển thị kèm theo trong lộ trình học tập theo từng năm và kỳ?
3. Mỗi môn học trong danh sách học kỳ được liệt kê kèm theo những thông tin chi tiết gì?
4. Phần tóm tắt mục tiêu cung cấp cho sinh viên những thông tin quan trọng nào để theo dõi tiến độ?','b80df891-9e85-4786-a7eb-fc4d33ed79a6'::uuid,NULL,NULL,159,330,'2026-03-21 13:35:13.350014+07'),
	 ('34ff7a35-25ea-4be9-ae29-5fafb842872f'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,159,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
6.1.6
Điểm Sốvà Tiến Độ
6.1.6.1
Tiến ĐộHiện Tại Môn Học
Giao diện hiển thịtiến độhọc tập hiện tại của sinh viên trong một môn học cụthể.
Giao diện này được minh hoạởhình 6.15.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 141/254

=== SUMMARY ===
Đoạn văn bản này giới thiệu về chức năng theo dõi điểm số và tiến độ trong hệ thống quản lý học tập. Cụ thể, mục 6.1.6.1 mô tả giao diện cho phép sinh viên quan sát tiến trình học tập hiện tại của mình trong từng môn học nhất định, kèm theo hình ảnh minh họa thực tế tại Hình 6.15.

=== REVIEW QUESTIONS ===
1. Mục đích chính của giao diện được mô tả trong mục 6.1.6.1 là gì?
2. Thông tin về tiến độ học tập được hiển thị ở phạm vi nào (toàn trường hay từng môn học cụ thể)?
3. Hình 6.15 trong tài liệu cung cấp minh họa cho tính năng nào của hệ thống?
4. Hệ thống LMS này thuộc quản lý của đơn vị giáo dục nào?','bbaa79c1-3aa2-4359-965f-6c5a734e54f6'::uuid,NULL,NULL,160,245,'2026-03-21 13:35:13.350545+07'),
	 ('b855a1b7-46d9-4462-8169-9c8be5885503'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,160,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Hình 6.15: Tiến độhiện tại môn học
Đặc điểm chính:
• Theo dõi tiến độhọc: checklist video/bài tập, tỉlệhoàn thành.
• Thống kê thời gian học bằng biểu đồcột đểphát hiện phân bổthời gian.
• Bảng điểm tóm tắt thành phần và tỉtrọng ngay trong màn tiến độ.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 142/254

=== SUMMARY ===
Đoạn văn bản mô tả các đặc điểm của giao diện theo dõi tiến độ môn học dành cho sinh viên. Hệ thống cung cấp các công cụ trực quan như danh sách kiểm tra (checklist) để theo dõi bài giảng và bài tập, biểu đồ cột thống kê thời gian học nhằm tối ưu hóa phân bổ thời gian, cùng bảng tóm tắt điểm số và tỉ trọng thành phần. Những tính năng này giúp sinh viên quản lý lộ trình học tập hiệu quả và nắm bắt tình hình cá nhân nhanh chóng.

=== REVIEW QUESTIONS ===
1. Những công cụ cụ thể nào giúp sinh viên theo dõi tiến độ học tập trong một môn học?
2. Mục đích của việc thống kê thời gian học bằng biểu đồ cột là gì?
3. Bảng điểm tóm tắt trong giao diện tiến độ hiển thị những thông tin quan trọng nào?
4. Làm thế nào sinh viên có thể biết được tỉ lệ hoàn thành các nội dung video và bài tập của mình?','14d20a12-7c0b-4f1d-99a8-5d8cc177c741'::uuid,NULL,NULL,161,311,'2026-03-21 13:35:13.350545+07'),
	 ('6ee20b3e-b507-4a70-b893-250fef188542'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,264,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
B.3.2
Quản lý khóa học
B.3.2.1
Tạo mới học kỳ– Chỉnh sửa chi tiết lớp của một môn
Hình B.40: Giao diện tạo mới học kỳ- chỉnh sửa chi tiết lớp của một môn
Đặc điểm chính:
• Phần đầu giữngữcảnh môn học (tên môn, mã môn, tín chỉ, tiên quyết, sốlớp).
• Bảng nhóm lớp cho phép cấu hình nhanh: mã nhóm, sĩ số, ngôn ngữ, thứ/tiết, phòng.
• Cột "Chọn giảng viên"đểphân công giảng viên BT/TN cho từng nhóm lớp.
• Mỗi nhóm có thao tác riêng (chỉnh sửa / thêm / xoá) đểquản trịlinh hoạt.
• Có nút điều hướng rõ ràng: Trước và Lưu và tiếp tục.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 246/254

=== SUMMARY ===
Nội dung này mô tả quy trình quản lý khóa học, cụ thể là giao diện tạo mới học kỳ và chỉnh sửa chi tiết lớp học. Các tính năng chính bao gồm: hiển thị thông tin môn học (tên, mã môn, tín chỉ), cấu hình chi tiết nhóm lớp (sĩ số, phòng, lịch học), phân công giảng viên cho từng nhóm và các công cụ quản trị linh hoạt như thêm, sửa, xóa lớp học. Hệ thống cũng cung cấp các nút điều hướng rõ ràng để tối ưu hóa thao tác người dùng.

=== REVIEW QUESTIONS ===
1. Những thông tin ngữ cảnh môn học nào được hiển thị ở phần đầu của giao diện chỉnh sửa chi tiết lớp?
2. Bảng nhóm lớp cho phép người dùng cấu hình nhanh những thông số cụ thể nào?
3. Vai trò của cột ''Chọn giảng viên'' trong việc quản lý từng nhóm lớp là gì?
4. Hệ thống cung cấp các thao tác quản trị riêng biệt nào cho mỗi nhóm lớp để đảm bảo tính linh hoạt?','a9f12c45-2c2f-41f8-a40f-f73a1e9a5157'::uuid,NULL,NULL,265,385,'2026-03-21 13:35:13.371031+07'),
	 ('66ba43a9-cace-4269-b3f5-eb6a161959d1'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,161,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
• Nút "Đánh Giá Tình Hình Học Tập"đểnhận phân tích thêm.
Các giao diện sinh viên khác của hệthống (như danh sách lớp học, quiz trắc nghiệm,
bài tập lớn, bảng điểm, diễn đàn, thông tin cá nhân, thời khóa biểu) được trình bày chi
tiết trong Phụlục B.1.
6.1.7
Kết Luận
Tài liệu này đã trình bày các giao diện người dùng quan trọng và đặc biệt được thiết
kếcho phần mềm LMS HCMUT dành cho sinh viên, tập trung vào các tính năng tích
hợp AI (chatbot), hỗtrợhọc tập thông minh và cá nhân hóa lộtrình học tập. Các giao
dien được thiết kếvới mục tiêu:
• Trực quan và dễsửdụng: Giao diện được thiết kếvới UX/UI hiện đại, dễdàng
điều hướng và sửdụng
• Tích hợp AI hỗtrợhọc tập: Chatbot tích hợp trong quá trình học và làm bài, giúp
sinh viên giải đáp thắc mắc ngay lập tức
• Cá nhân hóa: Cho phép sinh viên tạo lộtrình học tập cá nhân phù hợp với mục
tiêu của mình
• Hỗtrợđa phương thức: Tích hợp video, transcript, ghi chú và các công cụhỗtrợ
học tập khác
Các giao diện này sẽđược triển khai trong phần frontend của hệthống và tích hợp với
các API backend đểcung cấp trải nghiệm học tập hoàn chỉnh cho sinh viên.
6.2
Giao diện giáo viên
6.2.1
Tạo mới khóa học tăng cường
6.2.1.1
Thông tin cơ bản
Giao diện thông tin cơ bản khóa học tăng cường được minh hoạởhình 6.16.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 143/254

=== SUMMARY ===
Phân đoạn này tổng kết các đặc điểm nổi bật của giao diện sinh viên trên hệ thống LMS HCMUT, bao gồm tích hợp AI (chatbot), cá nhân hóa lộ trình học tập và hỗ trợ đa phương thức nhằm tối ưu hóa trải nghiệm người dùng. Tài liệu cũng chuyển sang giới thiệu giao diện dành cho giáo viên, bắt đầu với quy trình tạo khóa học tăng cường và thiết lập các thông tin cơ bản cần thiết.

=== REVIEW QUESTIONS ===
1. Hệ thống LMS HCMUT tích hợp AI dưới hình thức nào để hỗ trợ sinh viên giải đáp thắc mắc?
2. Bốn mục tiêu chính trong việc thiết kế giao diện người dùng cho sinh viên là gì?
3. Thông tin về các giao diện như quiz trắc nghiệm và diễn đàn có thể được tìm thấy ở đâu trong tài liệu?
4. Trong giao diện của giáo viên, bước đầu tiên khi tạo một khóa học tăng cường là gì?','02495911-f196-4e3e-a7ad-4b30dbc9959d'::uuid,NULL,NULL,162,556,'2026-03-21 13:35:13.350545+07'),
	 ('33539ffe-b9f2-4bae-8d74-4e04f2bba199'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,162,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Hình 6.16: Thông tin cơ bản khóa học tăng cường
Đặc điểm chính:
• Giao diện form nhiều trường (tên, mục tiêu, ngôn ngữ, chủđề, cấp độ, thời lượng)
đảm bảo nhập đầy đủmetadata khóa học.
• Khu vực upload thumbnail/video và mô tảgiúp chuẩn bịnội dung quảng cáo và
thông tin giới thiệu.
6.2.1.2
Chương trình dạy học
Giao diện chương trình dạy học được minh hoạởhình 6.17.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 144/254

=== SUMMARY ===
Phần nội dung này mô tả các giao diện quan trọng dành cho giáo viên khi tạo mới một khóa học tăng cường trên hệ thống LMS. Cụ thể, nó chi tiết hóa giao diện nhập thông tin cơ bản bao gồm các trường metadata (tên, mục tiêu, cấp độ...) và khu vực tải lên tư liệu quảng bá (hình thu nhỏ, video giới thiệu). Đồng thời, tài liệu cũng dẫn dắt sang giao diện thiết lập chương trình dạy học, giúp giáo viên cấu trúc nội dung bài giảng một cách logic.

=== REVIEW QUESTIONS ===
1. Giao diện thông tin cơ bản của khóa học tăng cường yêu cầu những trường dữ liệu (metadata) cụ thể nào?
2. Mục đích của việc thiết kế khu vực upload thumbnail và video trong giao diện là gì?
3. Giao diện chương trình dạy học được minh họa ở hình số mấy trong tài liệu?
4. Tại sao việc nhập đầy đủ thông tin về mục tiêu và mô tả khóa học lại quan trọng đối với giáo viên?','950760b9-da7b-41db-ae42-a2a3df7b55ad'::uuid,NULL,NULL,163,351,'2026-03-21 13:35:13.350545+07'),
	 ('dee2de20-7a54-4a11-89e6-4fe9cb2b5b40'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,163,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Hình 6.17: Chương trình dạy học
Đặc điểm chính:
• Cho phép cấu trúc chương/bài với thông tin tiêu đề, mô tảvà khu vực ghi chú.
• Nút thêm chương mới và điều hướng từng phần giúp mởrộng nội dung từng bước
trong wizard tạo khóa.
6.2.1.3
Bài giảng ghi chú (lecture notes)
Giao diện ghi chú được minh hoạởhình 6.18.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 145/254

=== SUMMARY ===
Đoạn văn mô tả các thành phần giao diện dành cho giáo viên trên hệ thống LMS, cụ thể là phần xây dựng chương trình dạy học và bài giảng ghi chú. Hệ thống hỗ trợ cấu trúc hóa khóa học theo chương và bài với đầy đủ tiêu đề, mô tả, cùng khu vực ghi chú. Các công cụ như nút thêm chương và điều hướng wizard giúp giáo viên dễ dàng mở rộng và quản lý nội dung. Ngoài ra, tài liệu cũng giới thiệu về giao diện bài giảng dưới dạng ghi chú (lecture notes).

=== REVIEW QUESTIONS ===
1. Giao diện chương trình dạy học cho phép giáo viên cấu trúc nội dung theo các cấp độ nào?
2. Mục đích của các nút điều hướng và nút thêm chương mới trong wizard tạo khóa học là gì?
3. Mỗi chương hoặc bài học trong giao diện này bao gồm những thông tin cơ bản nào?
4. Phần 6.2.1.3 giới thiệu về loại giao diện bài giảng nào dành cho giáo viên?','af89f0f9-f045-4d5e-a020-8cc6ad69d497'::uuid,NULL,NULL,164,331,'2026-03-21 13:35:13.350545+07');
INSERT INTO coaching_chatbot.lecture_knowledge_chunks (lecture_knowledge_chunks_id,lecture_knowledge_id,chunk_index,chunk_content,qdrant_point_id,start_time_seconds,end_time_seconds,page_number,token_count,created_at) VALUES
	 ('3ca759c7-7f51-4652-9f99-01856dd2bd81'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,164,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Hình 6.18: Bài giảng ghi chú (lecture notes)
Đặc điểm chính:
• Khu vực ghi chú đa phương thức (text, tải file) hỗtrợgiáo viên đính kèm nội dung
hỗtrợhọc tập.
6.2.1.4
Bài giảng video
Giao diện bài giảng video được minh hoạởhình 6.19.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 146/254

=== SUMMARY ===
Nội dung này giới thiệu về hai thành phần quan trọng trong cấu trúc khóa học là bài giảng ghi chú và bài giảng video. Bài giảng ghi chú hỗ trợ tính năng đa phương thức, cho phép giáo viên nhập văn bản hoặc tải tệp tin để bổ sung tài liệu học tập. Tài liệu cũng xác định vị trí minh họa cho giao diện bài giảng video, thuộc khuôn khổ báo cáo đồ án chuyên ngành ngành Khoa học Máy tính.

=== REVIEW QUESTIONS ===
1. Hình 6.18 minh họa cho thành phần nào trong hệ thống?
2. Tính năng ghi chú đa phương thức hỗ trợ những loại định dạng nội dung nào?
3. Mục tiêu của việc cung cấp khu vực ghi chú và tải file là gì?
4. Giao diện bài giảng video được trình bày ở mục nào và minh họa bằng hình số mấy?','dfc4174d-a5dd-49b2-8fc2-9b46c48395fd'::uuid,NULL,NULL,165,280,'2026-03-21 13:35:13.350545+07'),
	 ('225b4353-93f9-4f11-8c11-43332e732bca'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,165,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Hình 6.19: Bài giảng video
Đặc điểm chính:
• Module upload video với hướng dẫn kích thước/dung lượng đảm bảo chất lượng nội
dung đa phương tiện.
6.2.1.5
File đính kèm
Giao diện file đính kèm được minh hoạởhình 6.20.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 147/254

=== SUMMARY ===
Nội dung này giới thiệu về hai thành phần quan trọng trong giao diện tạo khóa học: bài giảng video và tệp đính kèm. Module video tích hợp tính năng tải lên kèm hướng dẫn về kích thước và dung lượng để đảm bảo chất lượng hiển thị. Trong khi đó, phần file đính kèm cho phép bổ sung các tài liệu học tập hỗ trợ, giúp làm phong phú nội dung bài giảng và cung cấp tài nguyên tham khảo cho người học.

=== REVIEW QUESTIONS ===
1. Đặc điểm chính của module upload video trong hệ thống là gì?
2. Tại sao hệ thống cần đưa ra hướng dẫn về kích thước và dung lượng khi tải video lên?
3. Mục 6.2.1.5 đề cập đến chức năng nào của hệ thống quản lý học tập?
4. Hình 6.20 trong tài liệu dùng để minh họa cho giao diện của thành phần nào?','ea7dbef9-af5f-43e5-8367-74ad7ebc83dc'::uuid,NULL,NULL,166,283,'2026-03-21 13:35:13.35155+07'),
	 ('65ffdafc-8ec1-46ba-9e0b-85409ebf3351'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,166,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Hình 6.20: File đính kèm
Đặc điểm chính:
• Cho phép đính kèm tài liệu hỗtrợ(PDF, tài liệu tham khảo) trong từng bài giảng,
đảm bảo giảng viên có thểcung cấp tài nguyên bổsung.
6.2.2
Quản lý câu hỏi và review
6.2.2.1
Thêm câu hỏi bằng AI
Giao diện thêm câu hỏi bằng AI được minh hoạởhình 6.21.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 148/254

=== SUMMARY ===
Đoạn văn bản trình bày hai chức năng quan trọng trong hệ thống quản lý học tập: tính năng đính kèm tài liệu (như PDF, tài liệu tham khảo) để bổ sung tài nguyên cho bài giảng và chức năng quản lý câu hỏi. Đặc biệt, hệ thống tích hợp công nghệ AI nhằm hỗ trợ giảng viên trong việc thêm câu hỏi một cách nhanh chóng và hiệu quả, giúp tối ưu hóa quy trình biên soạn nội dung kiểm tra.

=== REVIEW QUESTIONS ===
1. Tính năng đính kèm tài liệu hỗ trợ những loại định dạng hoặc tài nguyên cụ thể nào?
2. Việc cho phép đính kèm tài liệu trong từng bài giảng mang lại lợi ích gì cho giảng viên và sinh viên?
3. Trong mục 6.2.2.1, công nghệ nào đã được ứng dụng để hỗ trợ việc tạo câu hỏi?
4. Hình 6.21 trong báo cáo minh họa cho giao diện của chức năng nào?','fee977e6-afbd-4a2e-ac8e-a45cb36e843c'::uuid,NULL,NULL,167,309,'2026-03-21 13:35:13.35155+07'),
	 ('1bc7a2f4-1791-46aa-99cc-88e2f2359668'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,167,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹ thuật Máy tính
Hình 6.21: Thêm câu hỏi bằng AI
Đặc điểm chính:
• Tích hợp đềxuất bằng AI hỗtrợgợi ý câu hỏi hoặc đáp án nhanh chóng theo chủ
đềđược chọn.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 149/254

=== SUMMARY ===
Nội dung giới thiệu về tính năng tích hợp trí tuệ nhân tạo (AI) trong hệ thống LMS của Trường Đại học Bách khoa TP.HCM. Tính năng này hỗ trợ giảng viên tạo câu hỏi và đáp án một cách nhanh chóng thông qua các đề xuất tự động dựa trên chủ đề đã chọn, giúp tối ưu hóa quy trình quản lý học tập và soạn thảo nội dung kiểm tra.

=== REVIEW QUESTIONS ===
1. Tính năng chính của giao diện được minh họa trong hình 6.21 là gì?
2. AI hỗ trợ giảng viên như thế nào trong việc quản lý câu hỏi?
3. Việc gợi ý câu hỏi và đáp án của AI được thực hiện dựa trên tiêu chí nào?
4. Lợi ích của việc tích hợp AI vào hệ thống LMS dành cho giáo viên là gì?','e30fd546-e2b8-412a-ac72-7c3aba545449'::uuid,NULL,NULL,168,242,'2026-03-21 13:35:13.35155+07'),
	 ('6d428d3a-dfff-4139-b9ef-f9c6410d1579'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,168,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
6.2.2.2
Review bài quiz
Giao diện review bài quiz được minh hoạởhình 6.22.
Hình 6.22: Review bài quiz
Đặc điểm chính:
• Hiển thịhình thức quiz, trang thái hoàn thành và danh sách câu hỏi đã tạo đểkiểm
tra lại trước khi công bố.
Các giao diện giáo viên khác của hệthống (như danh sách lớp học, popup thông báo,
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 150/254

=== SUMMARY ===
Nội dung này giới thiệu về tính năng ''Review bài quiz'' dành cho giảng viên trên hệ thống LMS. Tính năng này cho phép giáo viên xem lại hình thức bài kiểm tra, trạng thái hoàn thành và danh sách câu hỏi đã tạo để đảm bảo tính chính xác trước khi công bố. Ngoài ra, văn bản cũng nhắc đến sự tồn tại của các giao diện quản lý khác như danh sách lớp và thông báo được chi tiết hóa trong phần phụ lục.

=== REVIEW QUESTIONS ===
1. Tính năng ''Review bài quiz'' hỗ trợ giảng viên kiểm tra những thông tin cụ thể nào?
2. Tại sao việc hiển thị danh sách câu hỏi trước khi công bố lại là một đặc điểm quan trọng của hệ thống?
3. Mục đích cuối cùng của việc sử dụng giao diện review bài quiz là gì?
4. Các giao diện quản lý khác dành cho giáo viên (như danh sách lớp học) được trình bày chi tiết ở đâu trong tài liệu?','f279473e-f931-4404-83ba-89f738ec92a1'::uuid,NULL,NULL,169,327,'2026-03-21 13:35:13.35155+07'),
	 ('7a432ace-7ae9-4e73-ab02-cba7dcad2b23'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,169,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
bài tập lớn, danh sách sinh viên làm quiz, diễn đàn, thời khóa biểu, thêm câu hỏi thủ
công, cấu hình điểm số) được trình bày chi tiết trong Phụlục B.2.
6.2.3
Kết Luận
Tài liệu này đã trình bày các giao diện người dùng quan trọng và đặc biệt được thiết
kếcho phần mềm LMS HCMUT dành cho giáo viên, tập trung vào các tính năng tạo
khóa học tăng cường và tích hợp AI trong quản lý câu hỏi. Các giao diện được thiết kế
với mục tiêu:
• Trực quan và dễsửdụng: Giao diện được thiết kếvới UX/UI hiện đại, dễdàng
điều hướng và sửdụng
• Tích hợp AI hỗtrợgiảng dạy: AI tích hợp trong quá trình tạo câu hỏi, giúp giáo
viên tạo nội dung nhanh chóng và hiệu quả
• Quản lý khóa học toàn diện: Cho phép giáo viên tạo và quản lý khóa học tăng
cường với đầy đủcác thành phần từthông tin cơ bản đến nội dung chi tiết
• Hỗtrợđa phương thức: Tích hợp video, file đính kèm, lecture notes và các công
cụhỗtrợgiảng dạy khác
Các giao diện này sẽđược triển khai trong phần frontend của hệthống và tích hợp với
các API backend đểcung cấp trải nghiệm giảng dạy hoàn chỉnh cho giáo viên.
6.3
Giao diện admin
6.3.1
Báo cáo hệthống
6.3.1.1
Báo cáo học tập
Giao diện báo cáo học tập được minh hoạởhình 6.23.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 151/254

=== SUMMARY ===
Đoạn văn bản tóm tắt các đặc điểm nổi bật của giao diện hệ thống LMS HCMUT dành cho giáo viên và quản trị viên. Hệ thống nhấn mạnh vào trải nghiệm người dùng trực quan, tích hợp AI hỗ trợ tạo câu hỏi, quản lý khóa học đa phương thức (video, tài liệu) và các báo cáo học tập chi tiết cho admin. Mục tiêu là cung cấp một công cụ giảng dạy hoàn chỉnh, kết nối mượt mà giữa frontend và backend để nâng cao hiệu quả quản lý giáo dục.

=== REVIEW QUESTIONS ===
1. Mục tiêu chính trong việc thiết kế giao diện người dùng cho giáo viên trên hệ thống LMS HCMUT là gì?
2. Công nghệ AI được tích hợp như thế nào để hỗ trợ giáo viên trong việc quản lý và tạo nội dung câu hỏi?
3. Hệ thống hỗ trợ các loại phương thức và công cụ nào để làm phong phú nội dung bài giảng của giáo viên?
4. Giao diện báo cáo học tập dành cho admin đóng vai trò gì trong việc quản lý hệ thống LMS?','f5ca0642-06ec-46c9-a686-dd073e3eb601'::uuid,NULL,NULL,170,558,'2026-03-21 13:35:13.35155+07'),
	 ('137204c0-032d-4446-b45c-477cee0d75b3'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,170,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Hình 6.23: Giao diện báo cáo học tập
Đặc điểm chính:
• Chọn khoảng thời gian đểxem sốliệu nhanh.
• Các thẻtổng quan: bài chưa chấm, tỷlệhoàn thành trung bình, tổng người dùng,
tần suất đăng nhập trung bình.
• Biểu đồgiúp nhìn nhanh: điểm trung bình theo lớp/khóa và tỷlệĐạt/Rớt.
• Có nút xuất báo cáo đểtải dữliệu.
6.3.1.2
Báo cáo hoạt động người dùng
Giao diện báo cáo hoạt động người dùng được minh hoạởhình 6.24.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 152/254

=== SUMMARY ===
Đoạn văn này mô tả các tính năng chính của giao diện báo cáo dành cho quản trị viên trong hệ thống LMS. Nội dung tập trung vào báo cáo học tập với các chỉ số như tỷ lệ hoàn thành, tần suất đăng nhập và biểu đồ so sánh điểm số, tỷ lệ đạt/rớt. Hệ thống hỗ trợ lọc dữ liệu theo thời gian và xuất báo cáo, giúp người quản lý nắm bắt nhanh chóng tình hình học tập và hoạt động của người dùng để đưa ra các đánh giá chính xác.

=== REVIEW QUESTIONS ===
1. Những chỉ số tổng quan nào được hiển thị trên các thẻ của giao diện báo cáo học tập?
2. Biểu đồ trong giao diện báo cáo học tập hỗ trợ người quản lý theo dõi những thông tin gì?
3. Tính năng xuất báo cáo trong hệ thống có vai trò như thế nào đối với người dùng admin?
4. Việc tích hợp bộ lọc khoảng thời gian mang lại lợi ích gì khi xem số liệu thống kê?','2a79f744-8423-421b-b67e-bb6c28186915'::uuid,NULL,NULL,171,354,'2026-03-21 13:35:13.35155+07'),
	 ('ed013a65-1153-44da-ab9e-ffd63f9b509e'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,171,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Hình 6.24: Giao diện báo cáo hoạt động người dùng
Đặc điểm chính:
• Tổng hợp mức độhoạt động: sốngười dùng active, lượt đăng nhập, thời gian sử
dụng trung bình, giờcao điểm.
• Biểu đồxu hướng đăng nhập theo tháng đểtheo dõi tăng/giảm.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 153/254

=== SUMMARY ===
Nội dung này mô tả các tính năng của giao diện báo cáo hoạt động người dùng trong hệ thống quản lý học tập. Giao diện tập trung vào việc cung cấp các số liệu tổng hợp như số người dùng hoạt động, lượt đăng nhập, thời gian sử dụng trung bình và xác định giờ cao điểm. Ngoài ra, hệ thống còn sử dụng biểu đồ xu hướng theo tháng để giúp người quản lý theo dõi sự biến động về mức độ tương tác của người dùng một cách trực quan.

=== REVIEW QUESTIONS ===
1. Giao diện báo cáo hoạt động người dùng cung cấp những chỉ số cụ thể nào để đánh giá mức độ hoạt động của hệ thống?
2. Làm thế nào để người quản lý có thể theo dõi sự thay đổi về lượng đăng nhập qua các tháng?
3. Chỉ số ''giờ cao điểm'' trong báo cáo có vai trò gì trong việc quản lý vận hành hệ thống?
4. Dựa trên các đặc điểm đã nêu, những thông tin nào giúp xác định mức độ gắn bó trung bình của người dùng với nền tảng?','76d24c9e-e0a7-4fe6-ac76-fc01cb88fc70'::uuid,NULL,NULL,172,326,'2026-03-21 13:35:13.35155+07'),
	 ('077036af-4f20-4aff-ae58-c219448fdb70'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,172,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
• Bảng chi tiết từng người dùng (vai trò, lần đăng nhập gần nhất, lượt truy cập, thời
gian học), có phân trang.
6.3.2
Quản lý người dùng
6.3.2.1
Danh sách người dùng
Giao diện danh sách người dùng được minh hoạởhình 6.25.
Hình 6.25: Giao diện danh sách người dùng
Đặc điểm chính:
• Trên cùng có 3 nút chính: Thêm mới, Import, Export.
• Có ô tìm kiếm nhanh theo Tên / Email / Tên đăng nhập.
• Có bộlọc theo Vai trò và Trạng thái đểlọc đúng nhóm người dùng cần xem.
• Bảng hiển thịcác thông tin quan trọng: Họtên, Email, Vai trò, Tên đăng nhập, Trạng
thái, Ngày tạo.
• Hỗtrợchọn nhiều dòng bằng checkbox và thao tác từng người bằng menu "...".
• Có phân trang đểquản lý danh sách lớn.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 154/254

=== SUMMARY ===
Đoạn văn bản này mô tả chi tiết giao diện Quản lý người dùng trong một hệ thống phần mềm. Nội dung tập trung vào các tính năng quản trị thiết yếu như: thêm mới, nhập/xuất dữ liệu hàng loạt, tìm kiếm và lọc người dùng theo vai trò/trạng thái. Giao diện được thiết kế tối ưu với bảng hiển thị thông tin đầy đủ, hỗ trợ chọn nhiều dòng qua checkbox và phân trang để quản lý danh sách lớn một cách hiệu quả.

=== REVIEW QUESTIONS ===
1. Ba nút chức năng chính nằm ở trên cùng của giao diện danh sách người dùng là gì?
2. Người quản trị có thể tìm kiếm nhanh người dùng dựa trên những thông tin nào?
3. Các tiêu chí nào được sử dụng trong bộ lọc để phân loại nhóm người dùng cần xem?
4. Bảng hiển thị người dùng bao gồm những cột thông tin quan trọng nào để hỗ trợ quản lý?','b800f4b0-4083-4583-bf83-b9b6f85cb0ca'::uuid,NULL,NULL,173,411,'2026-03-21 13:35:13.35155+07'),
	 ('8f536d3d-c8a2-4fcc-b018-4455cf3ef002'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,173,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
6.3.3
Quản lý khóa học
6.3.3.1
Quản lý khóa học – Danh sách chương trình học
Giao diện danh sách chương trình học được minh hoạởhình 6.26.
Hình 6.26: Giao diện danh sách chương trình học
Đặc điểm chính:
• Hiển thịdanh sách các chương trình/học kỳđang có theo dạng thẻ, dễtheo dõi.
• Mỗi chương trình có nút "Dừng lại"đểtạm ngưng áp dụng khi cần.
• Thông tin cơ bản được thểhiện rõ (mã học kỳ, năm. . . ).
• Có khu vực upload chương trình đào tạo mới (kéo thảhoặc chọn file) đểcập nhật
nhanh.
• Nút Hủy giúp thoát thao tác an toàn.
6.3.3.2
Tạo mới học kỳ– Danh sách môn học (Import/Export)
Giao diện tạo mới học kỳ- danh sách môn học được minh hoạởhình 6.27.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 155/254

=== SUMMARY ===
Đoạn văn bản mô tả chức năng quản lý khóa học và chương trình đào tạo trong hệ thống. Các đặc điểm chính bao gồm giao diện hiển thị danh sách theo dạng thẻ trực quan, tính năng tạm dừng chương trình, và hỗ trợ cập nhật dữ liệu nhanh qua việc tải lên tệp tin. Ngoài ra, hệ thống còn cho phép tạo mới học kỳ và quản lý danh sách môn học thông qua các công cụ nhập và xuất dữ liệu (Import/Export), giúp việc quản trị thông tin học thuật trở nên thuận tiện hơn.

=== REVIEW QUESTIONS ===
1. Giao diện danh sách chương trình học được thiết kế theo dạng nào để người dùng dễ dàng theo dõi?
2. Người quản trị sử dụng nút chức năng nào để tạm ngưng áp dụng một chương trình đào tạo khi cần thiết?
3. Hệ thống hỗ trợ những cách thức nào để người dùng có thể upload chương trình đào tạo mới?
4. Tính năng nào được tích hợp trong giao diện tạo mới học kỳ để hỗ trợ quản lý danh sách môn học nhanh chóng?','9f8c58e2-33e1-423a-b001-44a743ac85cb'::uuid,NULL,NULL,174,436,'2026-03-21 13:35:13.352563+07');
INSERT INTO coaching_chatbot.lecture_knowledge_chunks (lecture_knowledge_chunks_id,lecture_knowledge_id,chunk_index,chunk_content,qdrant_point_id,start_time_seconds,end_time_seconds,page_number,token_count,created_at) VALUES
	 ('457cf52d-5f51-43aa-ad84-113a2f5a9976'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,183,'=== ORIGINAL CONTENT ===
Trường Đại Học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa Học & KỹThuật Máy Tính
Phụlục A
Lược đồUse-Case
Phần phụlục này trình bày chi tiết các lược đồUse-Case còn lại cho từng loại người
dùng trong hệthống LMS HCMUT, bao gồm sinh viên, giảng viên và quản trịviên. Mỗi
lược đồminh họa các chức năng chính mà mỗi loại người dùng có thểthực hiện trong hệ
thống, giúp làm rõ các yêu cầu chức năng và tương tác giữa người dùng và hệthống.
A.1
Lược đồuse-case đối với sinh viên
A.1.1
UC-01: Theo dõi học tập cá nhân
Đặc tảUC-01: Theo dõi học tập cá nhân được trình bày trong bảng A.1.
Bảng A.1: Đặc tảUC-01: Theo dõi học tập cá nhân
Use-case Code
UC-01
Use-case Name
Theo dõi học tập cá nhân
Description
Bảng tổng quan (dashboard) hiển thịtiến độkhóa học, bài
tập/quizz đã nộp, tín chỉtích lũy, mức độđạt so với mục tiêu.
Actors
Sinh viên
Trigger
Sau khi đăng nhập thành công, sinh viên sẽđược đưa vào giao
diện Dashboard.
Pre-Conditions
• Thông tin sinh viên đã được thêm hợp lệvà đầy đủvào hệ
thống.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 165/254

=== SUMMARY ===
Đoạn văn bản trình bày Phụ lục A về lược đồ Use-case của hệ thống LMS HCMUT, tập trung vào đặc tả Use-case UC-01: ''Theo dõi học tập cá nhân'' dành cho sinh viên. Chức năng này cung cấp một bảng điều khiển (Dashboard) tổng quát giúp sinh viên giám sát tiến độ học tập, bài tập, tín chỉ tích lũy và so sánh kết quả thực tế với mục tiêu đề ra. Dashboard sẽ tự động hiển thị ngay sau khi sinh viên đăng nhập thành công.

=== REVIEW QUESTIONS ===
1. Use-case UC-01 cung cấp những thông tin quản lý học tập nào cho sinh viên trên Dashboard?
2. Đối tượng người dùng (Actor) nào trực tiếp tương tác với Use-case UC-01?
3. Sự kiện nào được coi là ''Trigger'' để hệ thống hiển thị giao diện theo dõi học tập cá nhân?
4. Điều kiện tiên quyết (Pre-conditions) để Use-case UC-01 có thể hoạt động bình thường là gì?','bdfdf28b-025f-4d28-86f2-ed72ba9be393'::uuid,NULL,NULL,184,477,'2026-03-21 13:35:13.35355+07'),
	 ('29d66e04-751f-4c55-96d7-b7fa60aef162'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,174,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Hình 6.27: Giao diện tạo mới học kỳ- danh sách môn học
Đặc điểm chính:
• Thống kê nhanh sốmôn học và sốlớp học của học kỳ.
• Bảng môn học rõ ràng: tên môn, mã môn, tín chỉ, tiên quyết, sốlớp.
• Có nút Import / Export đểnhập – xuất dữliệu hàng loạt.
• Mỗi dòng có nút thao tác nhanh (xem chi tiết / thêm / xoá) giúp chỉnh sửa nhanh.
• Có phân trang và chọn sốdòng hiển thị.
6.3.4
Quản trịhệthống
6.3.4.1
Quản lý truy cập dựa trên vai trò
Giao diện quản lý truy cập dựa trên vai trò được minh hoạởhình 6.28.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 156/254

=== SUMMARY ===
Nội dung tập trung vào hai thành phần quản trị cốt lõi của hệ thống LMS: giao diện quản lý học kỳ/môn học và quản lý truy cập theo vai trò. Giao diện môn học hỗ trợ thống kê nhanh, thao tác dữ liệu hàng loạt (Import/Export) và quản lý chi tiết các thuộc tính môn học như mã môn, tín chỉ, môn tiên quyết. Trong khi đó, phần quản trị hệ thống giới thiệu về cơ chế phân quyền dựa trên vai trò nhằm đảm bảo tính bảo mật và kiểm soát truy cập.

=== REVIEW QUESTIONS ===
1. Giao diện tạo mới học kỳ cung cấp những thông tin thống kê nhanh nào?
2. Chức năng Import/Export trong danh sách môn học hỗ trợ người quản trị như thế nào?
3. Bảng danh sách môn học hiển thị những thông tin chi tiết nào của mỗi môn?
4. Mục 6.3.4.1 đề cập đến phương pháp quản lý truy cập nào trong hệ thống?','964ece27-8eca-427b-bea9-cc72c7c697aa'::uuid,NULL,NULL,175,369,'2026-03-21 13:35:13.352563+07'),
	 ('60c3c406-3873-4668-8a9c-7910c7e32e6f'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,175,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Hình 6.28: Giao diện quản lý truy cập dựa trên vai trò
Đặc điểm chính:
• Phân quyền theo từng nhóm chức năng (Người dùng / Khóa học / Bài tập / Báo cáo
/ Hệthống).
• Mỗi nhóm có chọn All hoặc tick chi tiết từng quyền, giúp cấp quyền đúng nhu cầu
và tránh cấp thừa.
Các giao diện admin khác của hệthống (như popup thêm/sửa người dùng, import
người dùng, chỉnh sửa chi tiết lớp, popup chọn giảng viên, quản lý thông báo, nhật ký hệ
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 157/254

=== SUMMARY ===
Văn bản mô tả giao diện quản trị hệ thống LMS, tập trung vào tính năng quản lý truy cập dựa trên vai trò. Hệ thống cho phép phân quyền chi tiết theo các nhóm chức năng như người dùng, khóa học, và báo cáo, giúp kiểm soát quyền hạn chính xác và bảo mật. Ngoài ra, đoạn trích còn liệt kê các công cụ hỗ trợ admin khác như quản lý thông báo, nhật ký hệ thống và chỉnh sửa thông tin người dùng, đảm bảo khả năng quản trị toàn diện.

=== REVIEW QUESTIONS ===
1. Việc phân quyền truy cập trong hệ thống LMS này được thực hiện theo các nhóm chức năng chính nào?
2. Tại sao hệ thống lại cung cấp tùy chọn tick chi tiết từng quyền thay vì chỉ có nút chọn tất cả (All)?
3. Hãy liệt kê ít nhất ba giao diện quản trị khác (ngoài quản lý vai trò) được nhắc đến trong đoạn văn bản.
4. Mục tiêu quan trọng nhất của việc thiết kế giao diện quản lý truy cập dựa trên vai trò là gì?','e66072a1-62c2-46b2-b56d-7e909a15e654'::uuid,NULL,NULL,176,372,'2026-03-21 13:35:13.352563+07'),
	 ('102bd332-74ec-4d53-a1e3-fb40582abbf8'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,176,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
thống) được trình bày chi tiết trong Phụlục B.3.
6.3.5
Kết Luận
Tài liệu này đã trình bày các giao diện người dùng quan trọng và đặc biệt được thiết
kếcho phần mềm LMS HCMUT dành cho admin, tập trung vào các tính năng báo cáo
hệthống, quản lý người dùng, quản lý khóa học và phân quyền. Các giao diện được thiết
kếvới mục tiêu:
• Trực quan và dễsửdụng: Giao diện được thiết kếvới UX/UI hiện đại, dễdàng
điều hướng và sửdụng
• Báo cáo toàn diện: Cung cấp các báo cáo chi tiết vềhọc tập và hoạt động người
dùng đểhỗtrợquản lý và ra quyết định
• Quản lý hiệu quả: Cho phép admin quản lý người dùng và khóa học một cách có
hệthống và linh hoạt
• Bảo mật và phân quyền: Hệthống phân quyền dựa trên vai trò đảm bảo an toàn
và kiểm soát truy cập
Các giao diện này sẽđược triển khai trong phần frontend của hệthống và tích hợp với
các API backend đểcung cấp trải nghiệm quản trịhoàn chỉnh cho admin.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 158/254

=== SUMMARY ===
Đoạn văn tổng kết các giao diện quản trị của hệ thống LMS HCMUT, tập trung vào báo cáo, quản lý người dùng, khóa học và phân quyền. Các giao diện được thiết kế hiện đại, trực quan, hỗ trợ quản trị viên ra quyết định thông qua báo cáo chi tiết và đảm bảo an toàn thông qua kiểm soát truy cập dựa trên vai trò. Các thành phần này sẽ được tích hợp giữa frontend và API backend để tạo nên một hệ thống quản trị hoàn chỉnh.

=== REVIEW QUESTIONS ===
1. Mục tiêu chính của việc thiết kế giao diện người dùng cho admin trong hệ thống LMS HCMUT là gì?
2. Hệ thống LMS này cung cấp những loại báo cáo nào để hỗ trợ quản lý?
3. Việc phân quyền dựa trên vai trò mang lại lợi ích gì cho công tác bảo mật hệ thống?
4. Các giao diện frontend sẽ được kết nối với thành phần nào để cung cấp trải nghiệm quản trị hoàn chỉnh?','a6ee59e6-e5d3-40d7-91c4-fcd7bbe54ba7'::uuid,NULL,NULL,177,473,'2026-03-21 13:35:13.352563+07'),
	 ('71a95659-37e4-4e66-ac51-2bdc6e642c6f'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,177,'=== ORIGINAL CONTENT ===
Trường Đại Học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa Học & KỹThuật Máy Tính
Chương 7
Tổng kết
7.1
Kết quảđạt được
Trong khuôn khổđồán chuyên ngành với đềtài “Xây dựng hệthống học tập trực
tuyến tích hợp tính năng cá nhân hóa lộtrình học tập”, nhóm đã đạt được một sốkết
quảquan trọng cảvềmặt học thuật lẫn tính ứng dụng thực tiễn.
Trước hết, nhóm đã tiến hành nghiên cứu, khảo sát và phân tích một cách có hệthống
các nền tảng LMS phổbiến hiện nay như Coursera, Udemy và LinkedIn Learning, kết
hợp với khảo sát thực tếsinh viên và giảng viên tại Trường Đại học Bách khoa TP.HCM.
Kết quảkhảo sát đã giúp nhóm xác định rõ các vấn đềcòn tồn tại của hệthống LMS
hiện tại, cũng như nhu cầu và kỳvọng của người dùng, làm cơ sởcho việc đềxuất các
yêu cầu chức năng và phi chức năng phù hợp.
Tiếp theo, nhóm đã xây dựng được bộyêu cầu hệthống đầy đủ, bao quát các nhóm
người dùng chính gồm sinh viên, giảng viên và quản trịviên. Các yêu cầu được đặc tả
chi tiết thông qua hệthống các use-case, user story và các epic, đảm bảo tính rõ ràng,
nhất quán và có khảnăng mởrộng trong tương lai. Trên cơ sởđó, kiến trúc hệthống
LMS theo hướng hiện đại đã được đềxuất, đáp ứng các yêu cầu vềhiệu năng, khảnăng
mởrộng, bảo mật và dễbảo trì.
Một đóng góp nổi bật của đềtài là việc nghiên cứu và đềxuất các mô hình, thuật toán
hỗtrợcá nhân hóa học tập cho sinh viên. Cụthể, nhóm đã xây dựng mô hình dựđoán
kết quảhọc tập, thuật toán đánh giá mức độkhảthi của mục tiêu học tập, phương pháp
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 159/254

=== SUMMARY ===
Đoạn văn tổng kết các kết quả đạt được của đồ án về hệ thống LMS tích hợp cá nhân hóa. Nhóm nghiên cứu đã khảo sát các nền tảng học trực tuyến lớn và nhu cầu thực tế để xây dựng bộ yêu cầu chi tiết cho sinh viên, giảng viên và quản trị viên. Điểm nhấn quan trọng là việc đề xuất kiến trúc hệ thống hiện đại cùng các mô hình, thuật toán hỗ trợ cá nhân hóa lộ trình học tập như dự đoán kết quả và đánh giá mục tiêu học tập.

=== REVIEW QUESTIONS ===
1. Đề tài đồ án này tập trung vào việc xây dựng hệ thống gì?
2. Nhóm tác giả đã tham khảo các nền tảng LMS phổ biến nào trước khi đề xuất yêu cầu hệ thống?
3. Các yêu cầu hệ thống được đặc tả thông qua những công cụ nào để đảm bảo tính rõ ràng và nhất quán?
4. Những đóng góp nổi bật về mặt thuật toán mà đề tài đã đạt được nhằm hỗ trợ sinh viên là gì?','0a593297-26c2-4452-833c-e96d21ace434'::uuid,NULL,NULL,178,597,'2026-03-21 13:35:13.352563+07'),
	 ('91077a50-fba8-4ca4-b180-e3ea495851c7'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,178,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹ thuật Máy tính
định lượng giá trị nghề nghiệp của môn học, cũng như quy trình xây dựng lộ trình học
cá nhân hóa dựa trên mục tiêu, năng lực và tiến độ học tập của sinh viên. Các mô hình
và thuật toán này đã được trình bày chi tiết, kèm theo phân tích kết quả và thảo luận.
Bên cạnh đó, nhóm đã thiết kế giao diện người dùng cho hệ thống LMS với nhiều
chức năng cốt lõi và nâng cao như dashboard theo dõi học tập, quản lý khóa học, làm
bài tập và kiểm tra, theo dõi điểm số và tiến độ, diễn đàn thảo luận, cũng như giao diện
lộ trình học tập cá nhân hóa. Các giao diện được thiết kế theo hướng trực quan, hiện đại,
bám sát hành vi sử dụng thực tế của người dùng, góp phần nâng cao trải nghiệm học tập
và giảng dạy.
Nhìn chung, đồ án đã hoàn thành các mục tiêu đề ra ban đầu, thể hiện khả năng vận
dụng kiến thức chuyên ngành vào việc phân tích, thiết kế và đề xuất giải pháp cho một
hệ thống LMS hiện đại, có tiềm năng ứng dụng thực tế trong môi trường đào tạo đại học.
7.2
Những hạn chế của đề tài
Bên cạnh những kết quả đạt được, đề tài vẫn còn tồn tại một số hạn chế nhất định do
các ràng buộc về thời gian, nguồn lực và phạm vi nghiên cứu.
Thứ nhất, đề tài chủ yếu dừng lại ở mức độ phân tích, thiết kế và mô phỏng, chưa triển
khai đầy đủ một hệ thống hoàn chỉnh để đưa vào vận hành thực tế. Do đó, các đánh giá
về hiệu năng, khả năng mở rộng và độ ổn định của hệ thống mới chỉ mang tính lý thuyết
hoặc dựa trên giả định, chưa được kiểm chứng thông qua triển khai thực tế với số lượng
người dùng lớn.
Thứ hai, các mô hình và thuật toán cá nhân hóa học tập được xây dựng và đánh giá
dựa trên tập dữ liệu giả lập hoặc dữ liệu hạn chế. Điều này có thể ảnh hưởng đến độ
chính xác và khả năng tổng quát hóa của kết quả khi áp dụng vào môi trường thực tế với
dữ liệu đa dạng và phức tạp hơn.
Thứ ba, việc tích hợp các công nghệ trí tuệ nhân tạo, đặc biệt là các mô hình ngôn
ngữ lớn (LLM) và dịch vụ AI bên thứ ba, mới chỉ được đề xuất ở mức khái niệm và thiết
kế.
Cuối cùng, do giới hạn phạm vi của đồ án, một số chức năng mở rộng như tích hợp
sâu với các hệ thống quản lý đào tạo khác của nhà trường, hỗ trợ học tập nâng cao, hay
Báo cáo đồ án chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 160/254

=== SUMMARY ===
Đoạn văn bản tổng kết kết quả đạt được và những hạn chế của đồ án chuyên ngành về hệ thống LMS. Nhóm nghiên cứu đã thành công trong việc xây dựng mô hình lộ trình học tập cá nhân hóa và thiết kế giao diện người dùng hiện đại. Tuy nhiên, đề tài vẫn còn những mặt hạn chế như: hệ thống mới chỉ ở mức mô phỏng chưa vận hành thực tế, dữ liệu thử nghiệm còn mang tính giả lập, và việc ứng dụng AI/LLM mới dừng lại ở mức đề xuất khái niệm.

=== REVIEW QUESTIONS ===
1. Những yếu tố nào được sử dụng làm cơ sở để xây dựng lộ trình học tập cá nhân hóa cho sinh viên trong đồ án này?
2. Các giao diện người dùng của hệ thống LMS đã được thiết kế nhằm đáp ứng những chức năng cốt lõi và nâng cao nào?
3. Tại sao các đánh giá về hiệu năng và độ ổn định của hệ thống hiện tại vẫn chỉ mang tính lý thuyết?
4. Hạn chế của việc sử dụng dữ liệu giả lập trong việc xây dựng các thuật toán cá nhân hóa học tập là gì?','9da72660-b0d8-4eac-812a-24fec8ec5ccc'::uuid,NULL,NULL,179,802,'2026-03-21 13:35:13.35355+07'),
	 ('7efd11f1-8398-4fe4-a1d1-9a9e8b5a4cdd'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,179,'=== ORIGINAL CONTENT ===
phân tích dữliệu học tập quy mô lớn (learning analytics) chưa được nghiên cứu đầy đủ. Đây là những hướng phát triển tiềm năng có thểđược tiếp tục khai thác trong các nghiên cứu hoặc đồán sau này.
7.3
Kếhoạch phát triển trong giai đoạn tiếp theo
Trong giai đoạn tiếp theo, đềtài dựkiến được triển khai và hoàn thiện theo mô hình phát triển phần mềm linh hoạt Scrum–Agile. Quá trình phát triển được chia thành 9 Sprint liên tiếp, mỗi Sprint tập trung vào một nhóm chức năng cụthểnhằm đảm bảo tiến độ, chất lượng và khảnăng mởrộng của hệthống.
Sprint 1: Khởi tạo dựán và quản lý người dùng (1 tuần)
Sprint đầu tiên tập trung vào việc thiết lập nền tảng kỹthuật cho toàn bộhệthống. Các công việc chính bao gồm:
• Cấu hình môi trường phát triển, thiết lập kiến trúc tổng thểvà khởi tạo các repository cần thiết.
• Xây dựng hệthống quản lý người dùng cơ bản, bao gồm đăng ký, đăng nhập và quản lý hồsơ cá nhân.
• Tích hợp cơ chếxác thực và phân quyền cơ bản (authentication & authorization) như JWT hoặc OAuth2.
• Đảm bảo các yêu cầu bảo mật tối thiểu cho việc lưu trữvà xửlý thông tin người dùng.
Sprint 2: Xây dựng hệthống quản lý khóa học (2 tuần)
Sprint này tập trung phát triển các chức năng cốt lõi liên quan đến nội dung học tập:
• Xây dựng chức năng tạo, chỉnh sửa và quản lý khóa học dành cho giảng viên.
• Thiết kếcấu trúc khóa học theo chapter/lecture, hỗtrợnhiều loại học liệu khác nhau.
• Phát triển chức năng đăng ký, truy cập và theo dõi khóa học dành cho sinh viên.
• Đảm bảo khảnăng mởrộng và tái sửdụng nội dung khóa học.

=== SUMMARY ===
Văn bản xác định các hướng phát triển tương lai như phân tích dữ liệu học tập và đề xuất kế hoạch triển khai hệ thống LMS theo mô hình Scrum-Agile gồm 9 Sprint. Hai giai đoạn đầu tiên được chi tiết hóa: Sprint 1 tập trung vào khởi tạo hạ tầng kỹ thuật, quản lý người dùng và bảo mật (JWT/OAuth2); Sprint 2 tập trung vào xây dựng hệ thống quản lý khóa học, cho phép giảng viên soạn thảo nội dung và sinh viên đăng ký theo dõi lộ trình học tập.

=== REVIEW QUESTIONS ===
1. Mô hình phát triển phần mềm nào được lựa chọn để triển khai hệ thống trong giai đoạn tiếp theo?
2. Trong Sprint 1, những cơ chế xác thực và phân quyền nào được đề xuất tích hợp?
3. Nội dung chính của Sprint 2 tập trung vào việc phát triển những chức năng nào cho giảng viên và sinh viên?
4. Ngoài các chức năng đã thiết kế, hướng phát triển tiềm năng nào được nhắc đến để khai thác trong tương lai?','98cad205-4375-4f61-8f55-dcdc057f9040'::uuid,NULL,NULL,180,611,'2026-03-21 13:35:13.35355+07'),
	 ('fb62bbb5-5513-429e-9fb4-6d078d9a9d4a'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,180,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Sprint 3: Hệthống bài tập, quiz và chấm bài tựđộng (2 tuần)
Sprint thứba tập trung vào các chức năng đánh giá học tập cơ bản:
• Xây dựng hệthống quiz với nhiều dạng câu hỏi khác nhau (trắc nghiệm, tựluận).
• Phát triển hệthống chấm điểm tựđộng và phản hồi kết quảcho sinh viên.
• Xây dựng ngân hàng câu hỏi dùng chung cho nhiều khóa học và kỳđánh giá.
Sprint 4: Môi trường lập trình (1 tuần)
Sprint này tập trung phát triển hạtầng cho các bài tập lập trình:
• Thiết kếgiao diện và luồng làm bài cho quiz dạng code trong LMS.
• Tích hợp môi trường đểbiên dịch, chạy test và chấm điểm tựđộng các bài tập lập
trình.
• Xây dựng bộtest case mẫu và quy ước format input/output cho các bài tập code.
• Kiểm thửhiệu năng, độổn định và bảo mật của môi trường chạy code (sandbox,
giới hạn tài nguyên).
Sprint 5: Hệthống thông báo và diễn đàn thảo luận (1 tuần)
Sprint này nhằm nâng cao khảnăng tương tác và trao đổi trong hệthống:
• Phát triển hệthống thông báo cho các sựkiện quan trọng như deadline, điểm sốvà
cập nhật khóa học.
• Xây dựng diễn đàn thảo luận theo khóa học và chủđề.
• Hỗtrợtrao đổi giữa sinh viên với giảng viên và giữa các sinh viên với nhau.
Sprint 6: Phân quyền theo vai trò (RBAC) (1 tuần)
Sprint thứsáu tập trung hoàn thiện hệthống phân quyền:
• Xây dựng cơ chếphân quyền chi tiết theo vai trò như sinh viên, giảng viên và quản
trịviên.
• Kiểm soát quyền truy cập vào các chức năng và tài nguyên của hệthống.
• Đảm bảo tính bảo mật và tuân thủnguyên tắc phân quyền tối thiểu.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 162/254

=== SUMMARY ===
Đoạn văn chi tiết hóa các giai đoạn từ Sprint 3 đến Sprint 6 trong quy trình phát triển hệ thống LMS. Nội dung bao gồm: xây dựng hệ thống bài tập và chấm điểm tự động; thiết lập môi trường lập trình hỗ trợ biên dịch và kiểm thử mã nguồn; phát triển các tính năng tương tác như thông báo và diễn đàn thảo luận; và cuối cùng là hoàn thiện hệ thống phân quyền chi tiết (RBAC). Các bước này nhằm đảm bảo tính toàn diện, khả năng tương tác và bảo mật cho nền tảng giáo dục.

=== REVIEW QUESTIONS ===
1. Sprint 3 tập trung vào những chức năng cụ thể nào để hỗ trợ việc đánh giá và phản hồi kết quả học tập cho sinh viên?
2. Những yếu tố kỹ thuật nào cần được kiểm thử trong môi trường chạy code (sandbox) ở Sprint 4?
3. Hệ thống thông báo và diễn đàn trong Sprint 5 giúp cải thiện khả năng tương tác giữa những đối tượng nào?
4. Mục tiêu chính của việc triển khai cơ chế Phân quyền theo vai trò (RBAC) trong Sprint 6 là gì?','c3796bc2-5169-4a9a-a164-20e7d3f83145'::uuid,NULL,NULL,181,650,'2026-03-21 13:35:13.35355+07'),
	 ('efb205ea-0aa5-445a-aff9-0bee65c90d4d'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,181,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Sprint 7: Coaching Chatbot hỗtrợhọc tập (2 tuần)
Sprint này tập trung xây dựng và tích hợp chatbot AI đểhỗtrợsinh viên trong quá
trình học:
• Tích hợp mô hình AI chatbot vào hệthống LMS thông qua API.
• Kết nối chatbot với kho dữliệu tri thức môn học (tài liệu, bài giảng, FAQ) đểnâng
cao chất lượng phản hồi.
• Kiểm thửnội bộcác tình huống hội thoại phổbiến và tinh chỉnh prompt/mô hình.
Sprint 8: Cá nhân hóa lộtrình học tập (2 tuần)
Sprint này tập trung hiện thực các tính năng cá nhân hóa dựa trên dữliệu và mục tiêu
học tập:
• Tích hợp thuật toán dựđoán kết quảhọc tập và đánh giá năng lực vào hệthống.
• Hiện thực chức năng đềxuất và cá nhân hóa lộtrình học tập dựa trên mục tiêu GPA,
sốtín chỉvà tiến độhiện tại của sinh viên.
• Xây dựng giao diện cho phép sinh viên thiết lập mục tiêu và xem lộtrình học tập
được đềxuất.
• Kiểm thửchức năng đềxuất lộtrình trên một sốkịch bản mẫu và thu thập phản hồi
đểcải tiến.
Sprint 9: Kiểm thửvà triển khai hệthống (3 tuần)
Sprint cuối cùng tập trung vào hoàn thiện và đưa hệthống vào vận hành:
• Thực hiện kiểm thửtoàn hệthống (functional testing, integration testing).
• Đánh giá hiệu năng, bảo mật và độổn định của hệthống.
• Triển khai hạtầng, cấu hình môi trường production và chuẩn bịtài liệu hướng dẫn
sửdụng.
Thông qua kếhoạch phát triển theo mô hình Scrum–Agile, hệthống LMS đềxuất có
thểđược triển khai từng bước một cách linh hoạt, dễkiểm soát rủi ro và sẵn sàng mở
rộng trong các giai đoạn phát triển tiếp theo. Sơ đồtimeline triển khai dựán được trình
bày trong bảng 7.1.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 163/254

=== SUMMARY ===
Nội dung trình bày chi tiết ba giai đoạn cuối (Sprint 7, 8, 9) trong quy trình phát triển LMS theo mô hình Scrum-Agile. Sprint 7 tập trung tích hợp Chatbot AI hỗ trợ học tập. Sprint 8 xây dựng các tính năng cá nhân hóa lộ trình học dựa trên dữ liệu dự đoán và mục tiêu cá nhân. Sprint 9 thực hiện kiểm thử toàn diện, đánh giá hiệu năng và triển khai hệ thống vào vận hành thực tế, đảm bảo tính linh hoạt và khả năng mở rộng.

=== REVIEW QUESTIONS ===
1. Các công việc chính cần thực hiện để xây dựng Coaching Chatbot trong Sprint 7 là gì?
2. Hệ thống LMS sử dụng những dữ liệu nào để đề xuất lộ trình học tập cá nhân hóa cho sinh viên?
3. Trong giai đoạn kiểm thử cuối cùng (Sprint 9), những khía cạnh nào của hệ thống sẽ được đánh giá?
4. Tại sao việc áp dụng mô hình Scrum-Agile lại giúp hệ thống LMS dễ dàng kiểm soát rủi ro và mở rộng?','c601300e-ccb5-491b-9128-65fb3c49a9c8'::uuid,NULL,NULL,182,641,'2026-03-21 13:35:13.35355+07'),
	 ('3b7c3f3d-f461-4e4a-ae12-377e3dc6064a'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,182,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Bảng 7.1: Timeline triển khai dựán theo mô hình Scrum–Agile
Timeline theo tuần
1
2
3
4
5
6
7
8
9
10
11
12
13
14
15
Sprint 1
Sprint 2
Sprint 3
Sprint 4
Sprint 5
Sprint 6
Sprint 7
Sprint 8
Sprint 9
Course
Assessment
Learning Path
Release
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 164/254

=== SUMMARY ===
Nội dung cung cấp bảng timeline chi tiết về việc triển khai dự án phần mềm LMS theo mô hình Scrum-Agile trong tổng thời gian 15 tuần. Kế hoạch được phân chia thành 9 Sprint với các giai đoạn trọng tâm bao gồm: xây dựng nội dung khóa học (Course), hệ thống đánh giá (Assessment), cá nhân hóa lộ trình học tập (Learning Path) và giai đoạn phát hành (Release). Đây là lộ trình giúp quản lý tiến độ và đảm bảo tính linh hoạt trong quá trình phát triển hệ thống.

=== REVIEW QUESTIONS ===
1. Dự án được triển khai theo mô hình quản lý nào và tổng thời gian thực hiện là bao nhiêu tuần?
2. Dựa vào bảng 7.1, giai đoạn ''Assessment'' (Đánh giá) bắt đầu và kết thúc vào khoảng tuần thứ mấy?
3. Có bao nhiêu Sprint được hoạch định trong toàn bộ quá trình phát triển dự án này?
4. Giai đoạn ''Release'' (Phát hành) diễn ra sau khi đã hoàn thành những mốc quan trọng nào trong timeline?','ea9193b0-d60f-466d-aa41-09d63acfa802'::uuid,NULL,NULL,183,325,'2026-03-21 13:35:13.35355+07');
INSERT INTO coaching_chatbot.lecture_knowledge_chunks (lecture_knowledge_chunks_id,lecture_knowledge_id,chunk_index,chunk_content,qdrant_point_id,start_time_seconds,end_time_seconds,page_number,token_count,created_at) VALUES
	 ('755706ff-75a0-4a7c-a8be-490cfe441246'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,184,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Post Conditions
• Các bảng đánh giá tình hình và tiến độhọc tập của sinh
viên (Lịch trình, các khóa học đã đăng ký, các bài tập hoặc
tài liệu đã tải lên gần đây, thống kê vềtình hình điểm sốvà
các cảnh báo vềnhững bất thường liên quan tới lịch trình
hay kết quảhọc tập).
Normal Flow
1. Sinh viên đăng nhập thành công từtrang Login hay qua
SSO, hệthống sẽtựđộng chuyển hướng sinh viên tới trang
Dashboard.
2. Hệthống sẽtổng hợp dữliệu: Lịch trình học tập bao gồm
lịch học các khóa học đã đăng ký, hay thời hạn của bài tập,
kiểm tra, điểm sốtrung bình tích lũy (GPA) hiện tại, các
khóa học gần đây đã đăng ký, thống kê vềtần suất tương
tác với hệthống, ...
3. Hệthống sẽđối chiếu kết quảthu được, đối chiếu so sánh
với mục tiêu học tập cụthểnếu đã thiết lập.
4. Hệthống hiển thịkết quả, so sánh tiến độ, danh sách việc
cần làm.
5. Sinh viên có thểxem chi tiết vềmục tiêu học tập của mình
(extend UC-02).
6. Sinh viên có thểxem chi tiết lộtrình học của mình (extend
UC-04).
7. Sinh viên có thểxem chi tiết lịch trình học của mình (ex-
tend UC-06).
8. Sinh viên có thểnhấn đểxem chi tiết tiến độtừng môn
(extend UC-47).
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 166/254

=== SUMMARY ===
Đoạn văn mô tả quy trình luồng chuẩn và điều kiện sau của chức năng theo dõi học tập cá nhân trên hệ thống LMS. Sau khi đăng nhập, sinh viên được chuyển đến trang Dashboard nơi hệ thống tổng hợp dữ liệu về lịch trình, GPA, bài tập và tần suất tương tác. Hệ thống thực hiện đối chiếu kết quả với mục tiêu học tập, hiển thị tiến độ và danh sách việc cần làm, đồng thời cung cấp các liên kết mở rộng để xem chi tiết lộ trình, lịch trình và tiến độ từng môn học.

=== REVIEW QUESTIONS ===
1. Sau khi đăng nhập thành công qua SSO, hệ thống sẽ tự động chuyển hướng sinh viên đến trang nào?
2. Hệ thống tổng hợp những loại dữ liệu học tập cụ thể nào để hiển thị cho sinh viên trên Dashboard?
3. Mục đích của việc đối chiếu kết quả thu được với mục tiêu học tập đã thiết lập là gì?
4. Sinh viên có thể truy cập thêm những thông tin chi tiết nào thông qua các chức năng mở rộng (extend UC) từ Dashboard?','b82a5972-6a75-4d66-9dc8-937c523b2e34'::uuid,NULL,NULL,185,551,'2026-03-21 13:35:13.354563+07'),
	 ('69d8c882-61a2-4aac-894a-cf0cbcb6b294'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,185,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Alternative Flow
1a. Nếu chưa có mục tiêu học tập cụthể→Sinh viên được gợi
ý chuyển hướng tới trang thiết lập mục tiêu cá nhân (extend
UC-102)
5a. Nếu tính hình học tập xuất hiện dữliệu bất thường, có thể
nhận lời khuyên từtrợlý ảo AI Coaching (extend UC-50)
Nếu chưa đăng ký môn nào, thay các section thểhiện tiến trình
học tập, thành section gợi ý đăng ký môn cho kỳhọc tập tiếp
theo.
Exceptions
• 2e1. Lỗi tải dữliệu→hiển thịthông báo "Không thểtải
dữliệu, vui lòng thửlại"; cho phép tải lại.
A.1.2
UC-04: Xem lộtrình học
Đặc tảUC-04: Xem lộtrình học được trình bày trong bảng A.2.
Bảng A.2: Đặc tảUC-04: Xem lộtrình học
Use-case Code
UC-04
Use-case Name
Xem lộtrình học
Description
Sinh viên xem lộtrình học cá nhân đã được hệthống sinh ra
dựa trên CTĐT và mục tiêu học tập (UC-02, UC-03).
Actors
Sinh viên
Trigger
Chọn mục “Lộtrình học” từmenu chính, từDashboard, hoặc
sau khi tạo xong lộtrình ởUC-03.
Pre-Conditions
• Sinh viên đã đăng nhập hệthống.
• Đã có một lộtrình học cho sinh viên (được sinh từUC-03).
Post Conditions
• Lộtrình học được hiển thịrõ ràng, giúp sinh viên nắm được
các kỳđã hoàn thành, kỳhiện tại và các kỳsắp tới.
• Sinh viên hiểu được tổng tín chỉ, GPA dựđoán/đã đạt theo
từng kỳvà biết được những môn/khối kỳcần chú ý.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 167/254

=== SUMMARY ===
Đoạn văn bản này trình bày các luồng thay thế và ngoại lệ của chức năng theo dõi học tập, bao gồm việc gợi ý thiết lập mục tiêu và hỗ trợ từ trợ lý AI Coaching. Ngoài ra, tài liệu chi tiết hóa đặc tả Use-case UC-04 (Xem lộ trình học), cho phép sinh viên theo dõi tiến độ học tập, số tín chỉ và GPA dự đoán qua từng học kỳ dựa trên chương trình đào tạo và mục tiêu cá nhân.

=== REVIEW QUESTIONS ===
1. Nếu sinh viên chưa thiết lập mục tiêu học tập cụ thể, hệ thống sẽ đưa ra gợi ý gì?
2. Trong trường hợp tình hình học tập có dữ liệu bất thường, sinh viên có thể nhận hỗ trợ từ đâu?
3. Điều kiện tiên quyết để sinh viên có thể thực hiện Use-case UC-04 ''Xem lộ trình học'' là gì?
4. Mục tiêu cuối cùng của việc hiển thị lộ trình học (Post-conditions) đối với sinh viên là gì?','168badd3-e9fa-47ba-b278-3d288e45c68b'::uuid,NULL,NULL,186,554,'2026-03-21 13:35:13.354563+07'),
	 ('dd2f6701-d31f-4edd-ba15-d1bec401cafc'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,186,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Normal Flow
1. Mởlộtrình học
[Sinh viên] Chọn mục “Lộtrình học tập” trên giao diện.
[Hệthống] Tải lộtrình hiện hành cùng mục tiêu, CTĐT
và kết quảhọc tập hiện tại của sinh viên.
2. Hiển thịtimeline năm học
[Hệthống] Hiển thịtrục dọc theo từng học kỳhọc tập,
chia thành các năm học (Năm 1, Năm 2, ...). Mỗi năm học
gồm 2-3 kỳ(HK1, HK2, HK Hè). Mỗi khối kỳhiển thị:
• Trạng thái: “Đã hoàn thành”, “Kỳhiện tại” hoặc không
có nhãn (kỳtương lai).
• Tổng sốtín chỉtrong kỳ.
• GPA dựđoán hoặc GPA thực tếcủa kỳ.
3. Hiển thịdanh sách học phần trong từng kỳ
[Hệthống] Bên trong mỗi khối kỳ, hiển thịdanh sách môn
học dạng thẻ:
• Mã môn (VD: CS101, MATH101, ENG101, ...).
• Tên môn (VD: Nhập môn Lập trình, Giải tích 1, Tiếng
Anh 1...).
• Sốtín chỉ(VD: 3 TC, 4 TC).
• Loại môn.
• Độkhó gợi ý: Dễ/ Trung bình / Khó.
• ĐTB hoặc ĐTB dựđoán (VD: ĐTB: 7.8).
• Điều kiện môn liên quan tới tiên quyết, song hành,
khuyến nghị(nếu có).
Các kỳđã hoàn thành (VD: “Kỳ1 - Năm 1”) được đánh
dấu “Đã hoàn thành” và hiển thịđiểm thực tế; kỳhiện tại
(VD: “Kỳ1 - Năm 2”) được đánh dấu “Kỳhiện tại”.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 168/254

=== SUMMARY ===
Đoạn trích mô tả quy trình hiển thị lộ trình học tập cho sinh viên. Hệ thống tự động tổng hợp dữ liệu từ chương trình đào tạo và kết quả cá nhân để trình bày dưới dạng trục thời gian theo năm học. Mỗi học kỳ cung cấp thông tin về trạng thái hoàn thành, tín chỉ và GPA. Chi tiết từng môn học bao gồm mã môn, độ khó, điểm trung bình dự kiến và các điều kiện tiên quyết, giúp sinh viên dễ dàng theo dõi và quản lý kế hoạch học tập.

=== REVIEW QUESTIONS ===
1. Hệ thống sẽ tải những dữ liệu cụ thể nào khi sinh viên chọn mục ''Lộ trình học tập''?
2. Timeline năm học được tổ chức phân cấp như thế nào và bao gồm những loại học kỳ nào?
3. Mỗi thẻ môn học trong danh sách học phần hiển thị những thông tin chi tiết nào để hỗ trợ sinh viên?
4. Làm thế nào để sinh viên nhận biết được một học kỳ đã hoàn thành hoặc đang ở học kỳ hiện tại trên giao diện?','74cd5148-ae97-43ab-ae18-68224d199522'::uuid,NULL,NULL,187,527,'2026-03-21 13:35:13.354563+07'),
	 ('c8cc2b47-25c0-44fe-be44-ec54ed325ace'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,187,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
4. Hiển thịcảnh báo trong kỳ(nếu có)
[Hệthống] Nếu trong kỳcó môn khó hoặc khối lượng kiến
thức lớn, hiển thịmột thanh cảnh báo ởđầu khối kỳ, ví dụ:
• “CS201 (Thuật toán) là môn khó – khối lượng kiến
thức lớn.”
• “CS203 (Hệđiều hành) yêu cầu nền tảng vững vềcấu
trúc dữliệu.”
5. Xem chi tiết và theo dõi tiến độ
[Sinh viên] Cuộn và quan sát toàn bộlộtrình qua các
năm/kỳ:
• Nhìn nhanh các kỳđã hoàn thành, GPA theo kỳ.
• Nhận biết kỳhiện tại và các kỳsắp tới với sốtín chỉvà
môn học dựkiến.
• Có thểchọn chi tiết một môn học đểxem lịch sửđiểm,
gợi ý học tập hoặc thông tin chi tiết hơn vềmôn học.
6. Hành động bổsung (tuỳchọn)
[Sinh viên] Có thể:
• Chọn “Thiết lập lại mục tiêu” đểquay lại UC-02 (nếu
muốn thay đổi mục tiêu và sinh lộtrình mới).
• Chọn “Điều chỉnh lộtrình” đểtùy chỉnh lộtrình học
(nếu muốn thay đổi các môn trong lộtrình).
Alternative Flow
1a. Chưa có lộtrình cá nhân
[Hệthống] Gợi ý thiết lập mục tiêu học tập (UC-02) nếu chưa
có mục tiêu học tập nào được thiết lập hoặc tạo lộtrình học
(UC-03) nếu sinh viên chưa có lộtrình cá nhân.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 169/254

=== SUMMARY ===
Đoạn văn bản mô tả chi tiết các bước cuối và luồng thay thế của tính năng Xem lộ trình học (UC-04). Hệ thống hỗ trợ sinh viên nhận diện các môn học khó qua thanh cảnh báo, theo dõi tiến độ GPA, số tín chỉ dự kiến và lịch sử điểm số. Ngoài ra, người dùng có thể linh hoạt điều chỉnh lộ trình hoặc thiết lập lại mục tiêu học tập ban đầu. Trường hợp chưa có lộ trình, hệ thống sẽ tự động gợi ý các bước khởi tạo cần thiết.

=== REVIEW QUESTIONS ===
1. Hệ thống hiển thị thông tin cảnh báo như thế nào đối với các môn học được đánh giá là khó hoặc có khối lượng kiến thức lớn?
2. Sinh viên có thể xem được những thông tin chi tiết gì khi chọn vào một môn học cụ thể trong lộ trình?
3. Để thay đổi hoàn toàn mục tiêu và sinh ra một lộ trình học mới, sinh viên cần thực hiện hành động bổ sung nào?
4. Trong trường hợp sinh viên chưa thiết lập lộ trình học tập cá nhân, hệ thống sẽ đưa ra gợi ý gì?','42fe40fa-5ae8-4db4-a86d-e94c7b9fb605'::uuid,NULL,NULL,188,535,'2026-03-21 13:35:13.354563+07'),
	 ('e377be1b-8087-48e6-a65d-513f6b010cce'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,225,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Alternative
Flow
4a. Hủy thao tác: Trước khi nhấn "Lưu", Admin chọn "Hủy"hoặc
rời khỏi form. Không có thay đổi nào được ghi nhận.
5a. Hiệu lực sau đăng nhập lại: Nếu kiến trúc phiên không hỗtrợ
làm mới quyền động, hệthống hiển thịghi chú rằng quyền mới áp
dụng từlần đăng nhập kếtiếp.
Exceptions
• 4e. Cốgắng hạquyền của Super Admin cuối cùng: Hệthống
sẽkhông cho phép thay đổi vai trò của tài khoản Super Admin
cuối cùng thành một vai trò có quyền thấp hơn, đểtránh việc
không còn ai có thểquản trịhệthống. Một thông báo lỗi sẽ
được hiển thị.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 207/254

=== SUMMARY ===
Nội dung này trình bày các luồng thay thế và trường hợp ngoại lệ của quy trình phân quyền (UC-42). Các điểm chính bao gồm: khả năng hủy thao tác trước khi lưu, thông báo về thời điểm quyền mới có hiệu lực (tức thì hoặc sau khi đăng nhập lại), và cơ chế bảo mật quan trọng ngăn chặn việc hạ cấp tài khoản Super Admin cuối cùng để đảm bảo hệ thống luôn có người quản trị.

=== REVIEW QUESTIONS ===
1. Điều gì xảy ra khi Admin chọn ''Hủy'' hoặc rời khỏi form trước khi nhấn ''Lưu''?
2. Trong điều kiện nào thì hệ thống sẽ thông báo quyền mới chỉ có hiệu lực từ lần đăng nhập kế tiếp?
3. Tại sao hệ thống không cho phép hạ quyền của tài khoản Super Admin cuối cùng?
4. Hệ thống sẽ phản hồi như thế nào khi Admin cố gắng thay đổi vai trò của Super Admin duy nhất sang vai trò thấp hơn?','69af81c6-20ac-454b-8422-a9c39818bd1e'::uuid,NULL,NULL,226,379,'2026-03-21 13:35:13.363674+07'),
	 ('d992af04-4dd0-41ec-8bfe-5035448c041b'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,188,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Exceptions
• 1e1. Không lấy được dữliệu lộtrình
Lỗi kết nối hoặc lỗi dữliệu khiến hệthống không tải được
lộtrình.
→Hiển thịthông báo: “Không thểtải lộtrình học, vui lòng
thửlại sau.” và không hiển thịcác khối kỳ.
• 6e1. Lỗi khi xuất file (nếu có)
Khi sinh file PDF/CSV gặp lỗi.
→Thông báo: “Không thểxuất lộtrình, vui lòng thửlại
sau.” (không ảnh hưởng việc xem trên màn hình).
A.1.3
UC-05: Xem lịch trình học tập
Đặc tảUC-05: Xem lịch trình học tập được trình bày trong bảng A.3.
Bảng A.3: Đặc tảUC-05: Xem lịch trình học tập (rút gọn)
Use-case Code
UC-05
Use-case Name
Xem lịch trình học tập
Description
Sinh viên xem và quản lý lịch học theo tuần hoặc học kỳ; có
thểchọn ngày đểhiển thịtuần tương ứng, thêm/chỉnh sửa/xóa
sựkiện (học, kiểm tra, cá nhân), xem chi tiết và bật/tắt đồng bộ
Google Calendar.
Actors
Sinh viên
Trigger
Chọn “Lịch trình” trên thanh điều hướng hoặc từdashboard.
Pre-Conditions
• Sinh viên đã đăng nhập hệthống.
Post Conditions
• Lịch hiển thịđúng chếđộ(Tuần/Học kỳ) và cập nhật sự
kiện mới nhất.
• Trạng thái đồng bộGoogle Calendar được lưu.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 170/254

=== SUMMARY ===
Đoạn văn bản cung cấp đặc tả chi tiết cho Use-case UC-05 (Xem lịch trình học tập) và các kịch bản ngoại lệ (Exceptions) liên quan đến lộ trình học. Nội dung chính bao gồm các chức năng xem lịch theo tuần/học kỳ, quản lý sự kiện cá nhân, và đồng bộ hóa Google Calendar cho sinh viên. Ngoài ra, tài liệu còn mô tả cách hệ thống xử lý các lỗi kỹ thuật như mất kết nối dữ liệu hoặc lỗi khi xuất file PDF/CSV để đảm bảo trải nghiệm người dùng ổn định.

=== REVIEW QUESTIONS ===
1. Sinh viên có thể thực hiện những thao tác cụ thể nào đối với các sự kiện trong lịch trình học tập?
2. Theo đặc tả UC-05, có những chế độ hiển thị lịch trình nào mà sinh viên có thể lựa chọn?
3. Hệ thống sẽ đưa ra phản hồi gì khi gặp lỗi không thể tải được dữ liệu lộ trình học (ngoại lệ 1e1)?
4. Điều kiện tiên quyết (Pre-Conditions) duy nhất được nêu để thực hiện Use-case UC-05 là gì?','d98cd2c5-5e34-48e2-9025-6e85cf7a80d2'::uuid,NULL,NULL,189,531,'2026-03-21 13:35:13.354563+07'),
	 ('a40b4eb6-70f8-4e6a-8d59-ca67176339ac'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,189,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Normal Flow
1. Mởtrang Lịch trình. Hiển thịhai tab: Lịch tuần biểu và
Lịch học kỳ.
2. Chọn ngày/tháng/năm. Chọn trên ô lịch mini →hiển thị
tuần tương ứng (ví dụ: “14–20 February, 2025”).
3. Xem chi tiết sựkiện. Click sựkiện đểmởpopup gồm: thời
gian, hạn nộp (nếu có), loại, ghi chú, khóa học liên quan
và trạng thái (Sắp diễn ra / Đang diễn ra / Quá hạn). Có
nút “Đi tới hoạt động” hoặc “Đóng”.
4. Thêm/chỉnh sửa/xóa sựkiện. Nhấn “Thêm sựkiện” →
popup nhập: tiêu đề, loại, thời gian, mô tả, địa điểm, sự
kiện lặp lại, đường dẫn (nếu có). Có thể“Chỉnh sửa”,
“Hủy”, hoặc “Xóa sựkiện”.
5. Đồng bộGoogle Calendar. Bật công tắc đồng bộ. Khi bật,
hệthống yêu cầu đăng nhập Google và hiển thị“Đã đồng
bộlúc 09:15 • 27/10”.
Alternative Flow
2a. Xem theo học kỳ
Chuyển sang tab “Lịch học kỳ” →hiển thịtiêu đềhọc kỳ, filter
học kỳvà tuần hiện tại.
3a. Xem Homework
Nhấn vào lịch trình liên quan tới bài tập sắp tới →popup hiện
lên có liên kết đến khóa học tương ứng.
Exceptions
• 1e. Lỗi mạng: “Không thểtải lịch. Vui lòng thửlại.”
• 4e. Thiếu trường bắt buộc: “Trường này là bắt buộc.”
• 5e. Lỗi đồng bộ: “Đồng bộthất bại. Vui lòng đăng nhập
lại.”
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 171/254

=== SUMMARY ===
Nội dung này trình bày các bước thực hiện của ca sử dụng ''Xem lịch trình học tập'' (UC-05). Tài liệu chi tiết hóa luồng xử lý chính bao gồm xem lịch theo tuần/kỳ, quản lý sự kiện và đồng bộ Google Calendar. Ngoài ra, văn bản còn đề cập đến các luồng thay thế như xem bài tập về nhà và cách hệ thống xử lý các ngoại lệ như lỗi mạng hoặc thiếu thông tin bắt buộc.

=== REVIEW QUESTIONS ===
1. Sinh viên có thể chọn xem lịch trình thông qua những chế độ hiển thị nào theo mô tả trong Normal Flow?
2. Những thông tin chi tiết nào được hiển thị khi người dùng click vào một sự kiện cụ thể trên lịch?
3. Quy trình đồng bộ hóa với Google Calendar yêu cầu người dùng thực hiện những thao tác gì?
4. Hệ thống sẽ hiển thị thông báo gì trong trường hợp người dùng cố gắng lưu sự kiện mà thiếu các trường thông tin bắt buộc?','20defa89-8241-4dcc-80fe-ee35aef980b0'::uuid,NULL,NULL,190,536,'2026-03-21 13:35:13.355562+07'),
	 ('ffab5fdc-03c8-49fb-b326-2269fe0ac6f1'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,190,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
A.1.4
UC-06: Điều chỉnh lộtrình học
Đặc tảUC-06: Điều chỉnh lộtrình học được trình bày trong bảng A.4.
Bảng A.4: Đặc tảUC-06: Điều chỉnh lộtrình học
Use-case Code
UC-06
Use-case Name
Điều chỉnh lộtrình học
Description
Sinh viên điều chỉnh lộtrình học cá nhân đã có, tập trung vào
các học kỳsắp tới. Sinh viên có thểthêm/xóa/chuyển môn giữa
các kỳhoặc chọn chiến lược xửlý các môn bịlệch so với kế
hoạch ban đầu (môn rớt, hoãn, chưa kịp đăng ký...). Mỗi lần sinh
viên chỉnh sửa, hệthống đều đánh giá lại lộtrình (tín chỉ/kỳ,
mốc tốt nghiệp, rủi ro tiên quyết, môn khó...) và hiển thịkết
quảđểsinh viên cân nhắc trước khi lưu.
Actors
Sinh viên
Trigger
• Từmàn hình Xem lộtrình học (UC-04), sinh viên bấm nút
“Điều chỉnh lộtrình”.
• Hoặc từthông báo hệthống phát hiện lộtrình bịlệch so
với kếhoạch (môn rớt/hoãn, không đăng ký được lớp theo
dựkiến...) và gợi ý “Điều chỉnh lộtrình”.
Pre-Conditions
• Sinh viên đã đăng nhập hệthống.
• Đã tồn tại một lộtrình học cá nhân (được sinh từUC-03)
gắn với mục tiêu hiện tại (UC-02).
• Hệthống có dữliệu cập nhật mới nhất vềkết quảhọc tập,
tình trạng đăng ký môn và CTĐT.
Post Conditions
• Lộtrình học được cập nhật theo các chỉnh sửa của sinh
viên và được lưu lại như phiên bản mới.
• Các chỉsốtổng quan (tín chỉ/kỳ, thời gian tốt nghiệp dự
kiến, các cảnh báo rủi ro) được cập nhật.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 172/254

=== SUMMARY ===
Văn bản này đặc tả Use-case UC-06: Điều chỉnh lộ trình học, cho phép sinh viên thay đổi kế hoạch học tập cá nhân cho các học kỳ tương lai. Chức năng này bao gồm việc thêm, xóa, hoặc di chuyển môn học và xử lý các môn bị lệch kế hoạch (như môn rớt hoặc hoãn). Hệ thống sẽ tự động đánh giá lại các chỉ số về tín chỉ, điều kiện tiên quyết và thời gian tốt nghiệp sau mỗi lần chỉnh sửa để hỗ trợ sinh viên ra quyết định chính xác.

=== REVIEW QUESTIONS ===
1. Mục tiêu chính của việc thực hiện Use-case UC-06 là gì?
2. Có những sự kiện (Trigger) nào dẫn đến việc sinh viên cần điều chỉnh lộ trình học?
3. Hệ thống sẽ tự động kiểm tra và hiển thị các chỉ số nào sau khi sinh viên thực hiện chỉnh sửa môn học?
4. Để có thể điều chỉnh lộ trình, sinh viên cần thỏa mãn những điều kiện tiên quyết nào về mặt hệ thống?','859e2fda-39d4-492c-9e86-c6ce82e1d401'::uuid,NULL,NULL,191,581,'2026-03-21 13:35:13.355562+07'),
	 ('55b543fb-60be-4180-a89a-49e701813825'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,191,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Normal Flow
1. Vào chếđộđiều chỉnh
[Sinh viên] Bấm nút “Điều chỉnh lộtrình” từmàn hình lộ
trình.
[Hệthống] Mởlộtrình ởchếđộchỉnh sửa, khóa các kỳđã
hoàn thành, cho phép chỉnh sửa đối với các kỳhọc hiện tại
và sắp tới.
2. Chọn kỳcần chỉnh sửa
[Sinh viên] Chọn một học kỳsắp tới trên lịch trình (ví dụ:
“Kỳ2 - Năm 2”).
[Hệthống] Tô sáng kỳđược chọn và hiển thịdanh sách
môn hiện có trong kỳđó.
3. Thực hiện chỉnh sửa thủcông
[Sinh viên] Thực hiện một hoặc nhiều thao tác:
• Thêm môn mới vào học kỳ(từdanh sách môn còn
thiếu hoặc môn tựchọn).
• Xóa một môn khỏi học kỳ(đẩy môn đó vềtrạng thái
chưa xếp vào học kỳnào).
• Chuyển môn từhọc kỳnày sang học kỳkhác (kéo thả
hoặc chọn học kỳđích).
4. Chọn chiến lược xửlý môn bịlệch (nếu có)
[Hệthống] Đánh dấu các môn bịlệch so với kếhoạch ban
đầu (môn rớt, hoãn, không đăng ký được. . . ) và gợi ý một
sốchiến lược:
• Học lại sớm nhất có thểhoặc dời sang học kỳsau có
khối lương học nhẹhơn
• Đối với các môn tựchọn, nếu môn bịlệch trong học
kỳhiện tại, có thểchọn rút môn và gợi ý môn thay thế
cho các học kỳsắp tới.
[Sinh viên] Chọn chiến lược áp dụng cho từng môn lệch.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 173/254

=== SUMMARY ===
Đoạn văn mô tả quy trình chuẩn để sinh viên điều chỉnh lộ trình học tập cá nhân. Quy trình bao gồm các bước: kích hoạt chế độ chỉnh sửa (hệ thống sẽ khóa các kỳ đã hoàn thành), chọn học kỳ tương lai, và thực hiện các thao tác như thêm, xóa hoặc di chuyển môn học. Ngoài ra, hệ thống còn hỗ trợ xử lý các môn học bị lệch kế hoạch (do trượt môn hoặc hoãn) bằng cách gợi ý các chiến lược tối ưu như học lại sớm hoặc thay thế môn học.

=== REVIEW QUESTIONS ===
1. Khi sinh viên vào chế độ điều chỉnh, hệ thống áp dụng quy tắc gì đối với các học kỳ đã hoàn thành?
2. Sinh viên có thể thực hiện những thao tác thủ công nào để thay đổi môn học trong một học kỳ?
3. Trạng thái của một môn học sẽ như thế nào sau khi sinh viên thực hiện thao tác xóa khỏi học kỳ?
4. Hệ thống gợi ý những chiến lược nào để xử lý các môn học bị lệch so với kế hoạch ban đầu?','4b2548ff-317e-40c0-a1cc-34fcf408f0f6'::uuid,NULL,NULL,192,539,'2026-03-21 13:35:13.355562+07'),
	 ('c1d1b84f-1314-4cb5-81c1-5b1790c4ee0d'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,192,'=== ORIGINAL CONTENT ===
5. Đánh giá lại lộtrình sau chỉnh sửa
Sau mỗi lần thao tác:
• [Hệthống] Kiểm tra lại lộtrình:
– Tổng tín chỉmỗi học kỳso với thiết lập trong mục
tiêu.
– Điều kiện tiên quyết của môn học trong lộtrình.
– Ảnh hưởng đến thời gian tốt nghiệp dựkiến.
– Ảnh hưởng đến GPA mong muốn.
• Hiển thịcảnh báo nếu hệthống phát hiện lộtrình hiện
tại có vấn đềnào.
6. Quyết định lưu hay tiếp tục chỉnh
[Sinh viên] Dựa trên đánh giá ởbước 5:
• Nếu thấy hợp lý: chọn “Lưu lộtrình”.
• Nếu chưa hài lòng: quay lại các bước 2–4 đểtiếp tục
chỉnh sửa.
[Hệthống] Khi sinh viên chọn lưu, hệthống cập nhật lộ
tình chính thức và quay lại màn hình xem lộtrình với dữ
liệu mới.
Alternative Flow
• 1a. Vào từcảnh báo lệch lộtrình
[Hệthống] Phát hiện sinh viên rớt hay rút một sốmôn so
với kỳtrước, gửi thông báo và gợi ý “Điều chỉnh lộtrình”.
Khi sinh viên bấm vào thông báo, hệthống mởtrực tiếp kỳ
bịảnh hưởng và tô sáng các môn cần xửlý.
• 6a. Hủy chỉnh sửa
Trong quá trình chỉnh, sinh viên chọn “Hủy” hoặc “Thoát
chếđộchỉnh sửa”.
[Hệthống] Bỏmọi thay đổi chưa lưu, khôi phục lộtrình
theo phiên bản trước đó và quay lại chếđộxem bình
thường.

=== SUMMARY ===
Đoạn văn bản mô tả quy trình đánh giá và hoàn tất việc điều chỉnh lộ trình học tập của sinh viên. Hệ thống tự động kiểm tra các tiêu chí về tín chỉ, điều kiện tiên quyết, thời gian tốt nghiệp và GPA để đưa ra cảnh báo. Sinh viên có quyền lưu lộ trình mới hoặc tiếp tục chỉnh sửa. Ngoài ra, tài liệu còn chi tiết hóa các luồng thay thế như truy cập từ cảnh báo hệ thống khi sinh viên rớt/rút môn và quy trình hủy bỏ thay đổi để khôi phục phiên bản cũ.

=== REVIEW QUESTIONS ===
1. Hệ thống sẽ kiểm tra những tiêu chí cụ thể nào để đánh giá lại lộ trình sau khi sinh viên thực hiện thao tác chỉnh sửa?
2. Sinh viên có những lựa chọn nào sau khi nhận được kết quả đánh giá lộ trình từ hệ thống ở bước 5?
3. Trong luồng thay thế 1a, hệ thống sẽ thực hiện những hành động gì khi phát hiện sinh viên rớt hoặc rút môn học?
4. Điều gì sẽ xảy ra với dữ liệu lộ trình nếu sinh viên chọn thoát chế độ chỉnh sửa mà không nhấn lưu?','4937a196-49d0-40af-b0c5-5e5267fa824e'::uuid,NULL,NULL,193,518,'2026-03-21 13:35:13.355562+07');
INSERT INTO coaching_chatbot.lecture_knowledge_chunks (lecture_knowledge_chunks_id,lecture_knowledge_id,chunk_index,chunk_content,qdrant_point_id,start_time_seconds,end_time_seconds,page_number,token_count,created_at) VALUES
	 ('f5c49d1e-1b82-40c3-bfb0-840bee311f21'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,193,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Exceptions
• 3e1. Thêm/xóa môn không hợp lệ
Sinh viên cốgắng:
– Thêm môn khi chưa thỏa điều kiện tiên quyết/cùng
học.
– Xóa môn bắt buộc.
→Hệthống từchối thao tác, hiển thịlý do cụthểvà giữ
nguyên lộtrình hiện tại.
• 6e1. Lỗi lưu dữliệu
Khi lưu lộtrình mới gặp lỗi hệthống (DB/Network).
→Thông báo: “Không thểlưu lộtrình, vui lòng thửlại
sau.” và giữnguyên lộtrình cũ.
A.1.5
UC-09: Khám phá khóa học
Đặc tảUC-09: Khám phá khóa học được trình bày trong bảng A.5.
Bảng A.5: Đặc tảUC-09: Khám phá khóa học
Use-case Code
UC-09
Use-case Name
Khám phá khóa học
Description
Sinh viên có thểkhám phá danh sách các khóa học được hệ
thống gợi ý và phân nhóm theo tiêu chí (chủđề, kỹnăng, xu
hướng, độphổbiến. . . ). Hệthống hiển thịcác nhóm khóa học
nổi bật, các khóa học liên quan đến mục tiêu cá nhân, và gợi ý
dựa trên hành vi trước đó.
Actors
Sinh viên
Trigger
Chọn "Khám phá khóa học"từmenu.
Pre-Conditions
• Sinh viên đã đăng nhập hệthống.
Post Conditions
• Danh sách khóa học được hiển thịtheo các nhóm gợi ý.
• Sinh viên có thểchuyển hướng sang chi tiết khóa học hoặc
thực hiện đăng ký.
• Lịch sửtruy cập được ghi nhận đểcải thiện gợi ý lần sau.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 175/254

=== SUMMARY ===
Tài liệu trình bày các tình huống ngoại lệ khi điều chỉnh lộ trình học tập, như vi phạm điều kiện môn học hoặc lỗi lưu trữ dữ liệu. Đồng thời, tài liệu chi tiết hóa Use-case UC-09 về tính năng "Khám phá khóa học". Tính năng này cho phép sinh viên tìm kiếm các khóa học bổ trợ được cá nhân hóa dựa trên kỹ năng, xu hướng và lịch sử học tập cá nhân nhằm hỗ trợ quá trình định hướng nghề nghiệp.

=== REVIEW QUESTIONS ===
1. Hệ thống sẽ phản hồi như thế nào khi sinh viên cố gắng xóa một môn học bắt buộc trong lộ trình?
2. Trong Use-case UC-09, những tiêu chí nào được sử dụng để phân nhóm các khóa học gợi ý?
3. Điều kiện tiên quyết (Pre-Conditions) bắt buộc để sinh viên có thể sử dụng chức năng khám phá khóa học là gì?
4. Hệ thống ghi nhận lịch sử truy cập sau khi khám phá khóa học nhằm mục đích gì?','57f29e52-24de-4161-8a8c-28b777d66deb'::uuid,NULL,NULL,194,535,'2026-03-21 13:35:13.356562+07'),
	 ('d8d11cd2-8534-42aa-b6fb-f61fedec3dc4'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,194,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Normal Flow
1. Hệthống tải thông tin hồsơ, mục tiêu và hành vi học tập
trước đó.
2. Hệthống truy xuất danh sách khóa học trong cơ sởdữliệu.
3. Hệthống phân loại và gom nhóm theo tiêu chí: chủđề,
mức độ, xu hướng, phổbiến, phù hợp mục tiêu.
4. Hệthống hiển thịgiao diện "Khám phá khóa học"với các
nhóm:
• Khóa học được đềxuất cho bạn.
• Khóa học phổbiến.
• Nhóm theo lĩnh vực/chuyên ngành.
• Khóa học mới.
5. Sinh viên cuộn/duyệt danh sách, có thểlọc thêm theo mức
độ, thời lượng, hoặc đánh giá.
6. Sinh viên chọn một khóa học đểxem chi tiết. (extend UC-
22)
Alternative Flow
3a. Không đủdữliệu cá nhân đểgợi ý
Hệthống chỉhiển thịcác nhóm khóa học mặc định: phổbiến,
mới nhất, theo chuyên ngành.
5a. Sinh viên chọn "Xem tất cả"ởmột nhóm
Hệthống hiển thịtoàn bộdanh sách trong nhóm đó, có kèm
công cụtìm kiếm và lọc.
Exceptions
• 3e1. Hệthống không thểchạy thuật toán gợi ý
Thông điệp: "Không thểtạo gợi ý cá nhân hóa. Tạm thời
hiển thịdanh sách mặc định."
Hệthống fallback sang dữliệu nhóm mặc định.
• 5e1. Mất kết nối khi tải thêm dữliệu
Thông điệp: "Kết nối bịgián đoạn, vui lòng thửlại."
Hệthống giữnguyên danh sách hiện tại đã hiển thị.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 176/254

=== SUMMARY ===
Phân đoạn này chi tiết quy trình hoạt động của tính năng "Khám phá khóa học" (UC-09). Hệ thống phân tích hồ sơ và hành vi của sinh viên để gợi ý các khóa học theo chủ đề, mức độ và xu hướng. Tài liệu mô tả trình tự từ lúc tải dữ liệu, hiển thị giao diện đến các kịch bản thay thế khi thiếu dữ liệu cá nhân hoặc gặp lỗi kỹ thuật (như mất kết nối, lỗi thuật toán), đảm bảo sinh viên luôn có danh sách khóa học mặc định để tham khảo.

=== REVIEW QUESTIONS ===
1. Hệ thống dựa trên những yếu tố nào của sinh viên để thực hiện phân loại và gợi ý khóa học?
2. Giao diện ''Khám phá khóa học'' thông thường sẽ hiển thị những nhóm khóa học cụ thể nào?
3. Trong trường hợp hệ thống không đủ dữ liệu cá nhân hoặc lỗi thuật toán gợi ý, sinh viên sẽ nhìn thấy gì?
4. Cách xử lý của hệ thống khi xảy ra lỗi mất kết nối trong quá trình sinh viên đang duyệt danh sách khóa học là gì?','7044afda-3b05-418c-bd4d-03446b1f1c14'::uuid,NULL,NULL,195,550,'2026-03-21 13:35:13.356562+07'),
	 ('26b2008b-831a-4696-9ade-579d52c944e9'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,195,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
A.1.6
UC-11: Đăng ký khóa học
Đặc tảUC-11: Đăng ký khóa học được trình bày trong bảng A.6.
Bảng A.6: Đặc tảUC-11: Đăng ký khóa học
Use-case Code
UC-11
Use-case Name
Đăng ký khóa học
Description
Sinh viên tham gia các khóa học bổtrợdo hệthống cung cấp,
được phân loại theo chủđềvà cấp độ.
Actors
Sinh viên
Trigger
Nhấn "Đăng ký"từtrang chi tiết khóa học hoặc từdanh sách
khám phá/tìm kiếm.
Pre-Conditions
• Sinh viên đã đăng nhập hệthống.
• Thời gian đăng ký đang mởđối với sinh viên.
• Khóa học và các lớp học phần (section) tồn tại.
Post Conditions
• Sinh viên được ghi danh vào khóa học hoặc được đưa vào
danh sách chờ.
Normal Flow
1. Sinh viên chọn khoá học phù hợp.
2. Sinh viên mởchi tiết khóa học và nhấn "Đăng ký".
3. Hệthống kiểm tra điều kiện:
• Sinh viên đã hoàn thành tiên quyết;
4. Sinh viên xác nhận "Đăng ký".
5. Hệthống ghi nhận đăng ký và hiển thị"Đăng ký thành
công"
Alternative Flow
3a. Thiếu tiên quyết: Hệthống hiển thịthông báo "Bạn chưa
hoàn thành môn tiên quyết: [Tên môn]. Vui lòng hoàn thành
trước khi đăng ký."
Exceptions
• 5e1. Không phù hợp cấp độ: "Bạn chưa đạt cấp độyêu cầu
cho khóa học này."
• 5e2. Khóa tạm ẩn/không công khai: "Khóa học hiện không
sẵn sàng."
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 177/254

=== SUMMARY ===
Tài liệu này đặc tả quy trình đăng ký khóa học (UC-11) dành cho sinh viên. Quy trình bao gồm các bước từ việc chọn khóa học, kiểm tra điều kiện tiên quyết đến khi xác nhận đăng ký thành công. Hệ thống cũng quy định các điều kiện cần có như đăng nhập và thời gian mở đăng ký, đồng thời xử lý các tình huống ngoại lệ như thiếu môn tiên quyết, không đủ cấp độ yêu cầu hoặc khóa học đang bị tạm ẩn.

=== REVIEW QUESTIONS ===
1. Để thực hiện đăng ký khóa học, sinh viên cần thỏa mãn những điều kiện tiên quyết (Pre-Conditions) nào?
2. Trong quy trình luồng chuẩn (Normal Flow), hệ thống thực hiện kiểm tra điều kiện gì trước khi cho phép sinh viên xác nhận đăng ký?
3. Nếu sinh viên chưa hoàn thành môn học tiên quyết, hệ thống sẽ xử lý và hiển thị thông báo như thế nào?
4. Nêu hai trường hợp ngoại lệ (Exceptions) có thể khiến sinh viên không thể đăng ký khóa học thành công.','800d1959-cbd5-46b6-af2d-3a692b530a95'::uuid,NULL,NULL,196,565,'2026-03-21 13:35:13.356562+07'),
	 ('8c6918a5-8c2e-4e1f-808f-1a4864d5ed29'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,226,'=== ORIGINAL CONTENT ===
Trường Đại Học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa Học & KỹThuật Máy Tính
Phụlục B
Giao diện người dùng
Phần phụlục này trình bày các giao diện người dùng tiêu chuẩn của hệthống LMS
HCMUT. Các giao diện này bao gồm các chức năng cơ bản và phổbiến trong các hệ
thống quản lý học tập hiện đại, được thiết kếđểđảm bảo tính nhất quán và dễsửdụng.
Khác với các giao diện được trình bày trong phần chính tập trung vào các tính năng đặc
biệt như tích hợp AI và cá nhân hóa lộtrình học tập, phần phụlục này cung cấp cái nhìn
toàn diện vềcác chức năng quản lý lớp học, quiz, bài tập, điểm sốvà các tính năng hỗ
trợkhác của hệthống.
B.1
Giao diện sinh viên
Phần phụlục này trình bày các giao diện người dùng đại trà và tiêu chuẩn của hệ
thống LMS HCMUT dành cho sinh viên. Các giao diện này bao gồm các chức năng cơ
bản và phổbiến trong các hệthống quản lý học tập hiện đại, được thiết kếđểđảm bảo
tính nhất quán và dễsửdụng.
B.1.1
Quản Lý Lớp Học
B.1.1.1
Danh Sách Lớp Học - Card View
Giao diện hiển thịdanh sách các lớp học mà sinh viên đã đăng ký dưới dạng card,
giúp dễdàng tìm kiếm và truy cập vào các lớp học.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 208/254

=== SUMMARY ===
Nội dung này giới thiệu Phụ lục B về giao diện người dùng của hệ thống LMS HCMUT, tập trung vào các chức năng quản lý học tập tiêu chuẩn dành cho sinh viên. Khác với phần chính nhấn mạnh vào AI và cá nhân hóa, phụ lục này cung cấp cái nhìn chi tiết về các giao diện cơ bản như quản lý lớp học, quiz và bài tập. Đặc biệt, giao diện danh sách lớp học được thiết kế dưới dạng thẻ (Card View) nhằm đảm bảo tính nhất quán, dễ tìm kiếm và truy cập.

=== REVIEW QUESTIONS ===
1. Mục đích chính của Phụ lục B trong tài liệu báo cáo này là gì?
2. Giao diện trong Phụ lục B có điểm gì khác biệt so với giao diện được trình bày trong phần chính của báo cáo?
3. Các tiêu chuẩn thiết kế nào được áp dụng cho giao diện người dùng của hệ thống LMS HCMUT?
4. Ưu điểm của việc hiển thị danh sách lớp học dưới dạng ''Card View'' đối với sinh viên là gì?','d47c44ed-6d5a-47e4-ad8c-1c852d79cdec'::uuid,NULL,NULL,227,515,'2026-03-21 13:35:13.364485+07'),
	 ('642cbc62-64a0-43ed-988d-e4104ad75128'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,196,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
A.1.7
UC-46: Truy cập tài nguyên khóa học
Đặc tảUC-46: Truy cập tài nguyên khóa học được trình bày trong bảng A.7.
Bảng A.7: Đặc tảUC-46: Truy cập tài nguyên khóa học
Use-case Code
UC-46
Use-case Name
Truy cập tài nguyên khóa học
Description
Sinh viên truy cập vào khóa học đã đăng ký đểxem và học các
tài liệu được cấu trúc rõ ràng theo module, tuần hoặc chủđề,
bao gồm bài giảng, tài liệu, video và các tài nguyên khác.
Actors
Sinh viên
Trigger
Sinh viên chọn khóa học từdanh sách khóa học đã đăng ký.
Pre-Conditions
• Sinh viên đã đăng nhập hệthống.
• Sinh viên đã đăng ký tham gia khóa học.
• Khóa học đã tồn tại và ởtrạng thái Active.
Post Conditions
• Cấu trúc khóa học và tài nguyên được hiển thịcho sinh
viên.
• Sinh viên có thểbắt đầu học tập.
Normal Flow
1. Sinh viên truy cập trang "Khóa học của tôi".
2. Hệthống hiển thịdanh sách khóa học đã đăng ký.
3. Sinh viên chọn một khóa học.
4. Hệthống hiển thịcấu trúc khóa học (các module/chủđề).
5. Sinh viên chọn một module hoặc tài nguyên cụthể.
6. Hệthống hiển thịnội dung tài nguyên (slide, video, tài
liệu).
7. Sinh viên có thểtải xuống hoặc xem trực tuyến.
Alternative Flow
4a. Sinh viên chọn "Tiếp tục từlần học trước"→hệthống tự
động mởmodule chưa hoàn thành →quay lại bước 5.
5a. Nếu tài nguyên là video →hệthống hỗtrợphát với tùy chọn
tốc độ, phụđề→quay lại bước 6.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 178/254

=== SUMMARY ===
Tài liệu này đặc tả ca sử dụng UC-46: Truy cập tài nguyên khóa học dành cho sinh viên. Nội dung hướng dẫn quy trình sinh viên tiếp cận các tài liệu học tập như bài giảng, video và slide được tổ chức theo module hoặc chủ đề. Hệ thống yêu cầu sinh viên phải đăng nhập và đã đăng ký khóa học đang hoạt động. Ngoài luồng cơ bản, hệ thống còn hỗ trợ tính năng tiếp tục học từ lần trước và các tùy chọn điều chỉnh khi xem video.

=== REVIEW QUESTIONS ===
1. Để thực hiện ca sử dụng UC-46, khóa học cần phải ở trạng thái nào?
2. Hệ thống sẽ hiển thị những gì sau khi sinh viên chọn một khóa học cụ thể từ danh sách ''Khóa học của tôi''?
3. Trong luồng thay đổi 4a, hệ thống hỗ trợ sinh viên như thế nào khi họ muốn tiếp tục việc học?
4. Sinh viên có những tùy chọn hỗ trợ nào khi truy cập tài nguyên dưới dạng video?','c3012aef-4057-4502-8537-92513ebce0d0'::uuid,NULL,NULL,197,583,'2026-03-21 13:35:13.356562+07'),
	 ('3f44db6d-1128-45e3-acf2-b7836e90599d'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,197,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Exceptions
• 3e. Khóa học chưa bắt đầu: Hệthống báo lỗi "Khóa học
chưa mở"→quay lại bước 2.
• 5e. Tài nguyên không tồn tại: Hiển thị"Tài nguyên không
tìm thấy"→quay lại bước 4.
• 6e. Lỗi tải tài nguyên (mất kết nối): Báo "Không thểtải,
vui lòng thửlại"→quay lại bước 5
A.1.8
UC-47: Xem tiến độhọc tập
Đặc tảUC-47: Xem tiến độhọc tập được trình bày trong bảng A.8.
Bảng A.8: Đặc tảUC-47: Xem tiến độhọc tập
Use-case Code
UC-47
Use-case Name
Xem tiến độhọc tập
Description
Sinh viên xem tiến độhọc tập của mình trong khóa học, bao
gồm phần trăm hoàn thành, module đã học, điểm sốtạm thời và
các chỉsốkhác.
Actors
Sinh viên
Trigger
Sinh viên chọn "Xem tiến độ"ởcác khóa học đã đăng ký trong
danh sách
Pre-Conditions
• Sinh viên đã đăng nhập hệthống.
• Sinh viên đã đăng ký tham gia khóa học.
• Khóa học đã tồn tại và có dữliệu tiến độ.
Post Conditions
• Tiến độhọc tập được hiển thịdưới dạng biểu đồhoặc bảng.
Normal Flow
1. Sinh viên truy cập trang khóa học.
2. Chọn "Xem tiến độhọc tập".
3. Hệthống tổng hợp dữliệu (module hoàn thành, bài tập
nộp, điểm số).
4. Hệthống hiển thịtiến độdưới dạng biểu đồ(phần trăm
hoàn thành) và chi tiết (danh sách module, điểm tạm thời).
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 179/254

=== SUMMARY ===
Nội dung này trình bày các ngoại lệ khi truy cập tài nguyên khóa học và đặc tả chi tiết cho Use-case UC-47: Xem tiến độ học tập. Sinh viên có thể theo dõi quá trình học tập của mình thông qua các chỉ số như phần trăm hoàn thành, danh sách module đã học và điểm số tạm thời. Hệ thống sẽ tổng hợp dữ liệu và hiển thị trực quan dưới dạng biểu đồ hoặc bảng để giúp sinh viên quản lý lộ trình học tập hiệu quả.

=== REVIEW QUESTIONS ===
1. Các lỗi ngoại lệ nào có thể xảy ra khi sinh viên truy cập tài nguyên khóa học?
2. Để thực hiện Use-case Xem tiến độ học tập, sinh viên cần thỏa mãn những điều kiện tiên quyết nào?
3. Hệ thống sẽ tổng hợp những loại dữ liệu nào để hiển thị tiến độ học tập cho sinh viên?
4. Kết quả đầu ra (Post-conditions) của chức năng xem tiến độ học tập được hiển thị dưới dạng nào?','65a5eeb6-62ed-4336-99ee-65ed36a3b275'::uuid,NULL,NULL,198,542,'2026-03-21 13:35:13.356562+07'),
	 ('9cc740de-8b05-4477-8092-b8164919dfa9'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,198,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
5. Sinh viên có thểlọc theo module hoặc thời gian.
6. Hệthống cập nhật và hiển thịkết quảlọc.
Alternative Flow
3a. Sinh viên chọn xem chi tiết điểm số→hệthống hiển thị
bảng điểm chi tiết →quay lại bước 4.
4a. Xuất báo cáo tiến độ: Sinh viên chọn "Xuất PDF"→hệ
thống tạo file và tải về.
Exceptions
• 3e. Chưa có dữliệu: Hệthống hiển thị"Chưa có tiến độ
học tập"→quay lại bước 1.
• 4e. Lỗi hệthống: Trang không tải được →báo "Lỗi hệ
thống, vui lòng thửlại sau".
• 5e. Quyền truy cập: Nếu sinh viên chưa tham gia →báo
"Bạn chưa đăng ký khóa học này".
A.1.9
UC-48: Làm bài tập và kiểm tra
Đặc tảUC-48: Làm bài tập và kiểm tra được trình bày trong bảng A.9.
Bảng A.9: Đặc tảUC-48: Làm bài tập và kiểm tra
Use-case Code
UC-48
Use-case Name
Làm bài tập và kiểm tra
Description
Sinh viên thực hiện bài tập hoặc kiểm tra trong khóa học, với
các hình thức đa dạng như trắc nghiệm (multiple choice), điền
khuyết (fill-in-the-blank), tựluận, lập trình (code submission),
nhóm hoặc kết hợp. Sinh viên có thểnhập câu trảlời trực tiếp,
upload file, và nộp lại nếu bài tập cho phép, nhằm đánh giá kiến
thức và kỹnăng.
Actors
Sinh viên
Trigger
Sinh viên chọn bài tập từcấu trúc khóa học.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 180/254

=== SUMMARY ===
Văn bản cung cấp chi tiết về hai trường hợp sử dụng (use-case) UC-47 (Xem tiến độ học tập) và UC-48 (Làm bài tập và kiểm tra). Sinh viên có thể theo dõi tiến độ qua việc lọc dữ liệu, xem bảng điểm chi tiết hoặc xuất báo cáo PDF. Đồng thời, hệ thống hỗ trợ nhiều hình thức làm bài đa dạng như trắc nghiệm, tự luận, điền khuyết và lập trình, cho phép nộp bài trực tiếp hoặc tải tệp lên để đánh giá năng lực.

=== REVIEW QUESTIONS ===
1. Sinh viên có thể sử dụng những tiêu chí nào để lọc kết quả khi xem tiến độ học tập?
2. Trong trường hợp muốn lưu trữ tiến độ học tập ngoại tuyến, sinh viên cần thực hiện thao tác gì?
3. Hệ thống sẽ hiển thị thông báo gì nếu một sinh viên chưa đăng ký khóa học nhưng cố gắng truy cập vào phần tiến độ?
4. Kể tên ít nhất 4 hình thức làm bài tập/kiểm tra mà trường hợp sử dụng UC-48 hỗ trợ.','b4596f3e-0764-48bd-bdd5-2c35833f334a'::uuid,NULL,NULL,199,547,'2026-03-21 13:35:13.357548+07'),
	 ('9fd3afbe-4fa2-487b-bf73-9a0e54f99860'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,199,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Pre-Conditions
• Sinh viên đã đăng nhập hệthống.
• Sinh viên đã đăng ký khóa học.
• Bài tập đã tồn tại và trong thời hạn nộp.
Post Conditions
• Bài làm được lưu thành công và chờchấm điểm.
Normal Flow
1. Sinh viên truy cập khóa học.
2. Chọn module hoặc phần chứa bài tập/kiểm tra.
3. Chọn bài tập/kiểm tra cần thực hiện.
4. Hệthống hiển thịgiao diện làm bài (câu hỏi, form nhập,
hướng dẫn).
5. Sinh viên nhập câu trảlời tùy theo hình thức:
• Trắc nghiệm (multiple choice): Chọn đáp án.
• Điền khuyết (fill-in-the-blank): Nhập vào ô trống.
• Tựluận: Viết văn bản dài.
• Lập trình (code): Viết mã code trong editor tích hợp.
• Nhóm: Hợp tác với thành viên khác (nếu áp dụng).
7. (Tùy chọn) Upload file đính kèm (báo cáo, code file, hình
ảnh).
8. Sinh viên chọn "Xem trước"đểkiểm tra bài làm.
9. Nhấn "Nộp bài".
10. Hệthống kiểm tra dữliệu hợp lệ(deadline, định dạng file,
hoàn thành bắt buộc).
11. Hệthống lưu bài nộp và thông báo "Nộp bài thành công".
Alternative Flow
7a. Sinh viên chọn "Lưu nháp"thay vì nộp →hệthống lưu tạm
thời mà không submit →quay lại bước 4.
Exceptions
• 3e. Bài tập hết hạn: Hệthống báo lỗi "Hết hạn nộp bài"→
quay lại bước 2.
• 9e. Lỗi hệthống hoặc mất kết nối: Báo "Nộp bài thất bại,
vui lòng thửlại sau"→quay lại bước 7.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 181/254

=== SUMMARY ===
Phân đoạn này mô tả chi tiết quy trình thực hiện Use-case UC-48: Làm bài tập và kiểm tra. Nội dung bao quát từ các điều kiện cần thiết như đăng nhập và đăng ký khóa học, đến luồng thực hiện gồm nhiều hình thức trả lời (trắc nghiệm, tự luận, lập trình). Văn bản cũng nêu rõ các bước kiểm tra tính hợp lệ khi nộp bài, tính năng lưu bản nháp và cách hệ thống xử lý các ngoại lệ như quá hạn nộp bài hoặc gặp lỗi kết nối mạng.

=== REVIEW QUESTIONS ===
1. Sinh viên cần thỏa mãn những điều kiện tiên quyết nào để có thể bắt đầu làm bài tập?
2. Hệ thống hỗ trợ những hình thức nhập câu trả lời cụ thể nào cho sinh viên?
3. Trong trường hợp chưa muốn nộp bài chính thức, sinh viên có thể thực hiện thao tác gì để giữ lại nội dung đã làm?
4. Hệ thống sẽ thông báo và xử lý như thế nào khi sinh viên nộp bài vào thời điểm bài tập đã hết hạn?','b9c213f1-e03b-45fa-adbf-ad5f01ec8902'::uuid,NULL,NULL,200,569,'2026-03-21 13:35:13.357548+07'),
	 ('11918188-26d7-4502-b2b0-40016b5e500d'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,256,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
B.2.4
Quản lý câu hỏi
B.2.4.1
Thêm câu hỏi thủcông - Trắc nghiệm
Hình B.31: Thêm câu hỏi trắc nghiệm
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 238/254

=== SUMMARY ===
Phân đoạn này giới thiệu về chức năng Quản lý câu hỏi trong hệ thống, cụ thể là quy trình thêm mới câu hỏi trắc nghiệm bằng phương pháp thủ công (mục B.2.4.1). Đây là một phần của báo cáo đồ án chuyên ngành thuộc Khoa Khoa học và Kỹ thuật Máy tính, cung cấp minh họa qua Hình B.31 để giảng viên có thể khởi tạo các câu hỏi trắc nghiệm phục vụ công tác kiểm tra.

=== REVIEW QUESTIONS ===
1. Nội dung chính của mục B.2.4 trong tài liệu là gì?
2. Mục B.2.4.1 tập trung vào loại hình câu hỏi cụ thể nào?
3. Theo nội dung văn bản, việc thêm câu hỏi trắc nghiệm được thực hiện bằng phương thức nào?
4. Hình B.31 minh họa cho chức năng nào của hệ thống quản lý câu hỏi?','c4383fe2-93c4-4782-b252-e31b01edddab'::uuid,NULL,NULL,257,240,'2026-03-21 13:35:13.370006+07'),
	 ('520211d2-920a-4a95-81c5-4e773a0f5766'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,200,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
A.1.10
UC-49: Tra cứu điểm và nhận xét
Đặc tảUC-49: Tra cứu điểm và nhận xét được trình bày trong bảng A.10.
Bảng A.10: Đặc tảUC-49: Tra cứu điểm và nhận xét
Use-case Code
UC-49
Use-case Name
Tra cứu điểm và nhận xét
Description
Sinh viên tra cứu điểm sốvà nhận xét chi tiết của bài tập hoặc
kiểm tra đã nộp
Actors
Sinh viên
Trigger
Sinh viên chọn tab "Tiến độhọc tập"trong trang khóa học hiện
tại và lướt xuống section kết quảhọc tập.
Pre-Conditions
• Sinh viên đã đăng nhập hệthống.
• Sinh viên đã nộp bài tập/kiểm tra.
• Bài tập/kiểm tra đã được chấm điểm
Post Conditions
• Điểm sốvà nhận xét được hiển thịrõ ràng.
• Sinh viên có thểtải vềnếu cần.
Normal Flow
1. Sinh viên truy cập trang khóa học hoặc "Tiến độhọc tập".
2. Hệthống hiển thịchi tiết: điểm số(tổng và chi tiết theo
phần), nhận xét từgiảng viên, và so sánh với thang điểm
khóa học.
3. Sinh viên có thểtải bảng điểm.
Alternative Flow
5a. Nếu có nhận xét chi tiết: Sinh viên mởpop-up xem feedback
đầy đủ→quay lại bước 6.
Exceptions
• 3e. Nếu chưa có bất kỳkết quảcho khóa học này, section
kết quảhọc tập hiển thị"Vui lòng đợi cập nhật".
A.1.11
UC-51: Tạo bài kiểm tra, ôn tập bằng AI
Đặc tảUC-51: Tạo bài kiểm tra, ôn tập bằng AI được trình bày trong bảng A.11.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 182/254

=== SUMMARY ===
Đoạn văn bản trình bày đặc tả chi tiết cho Use-case UC-49 (Tra cứu điểm và nhận xét) trong hệ thống quản lý học tập. Sinh viên có thể theo dõi kết quả học tập, xem điểm chi tiết, nhận xét từ giảng viên và tải bảng điểm từ tab ''Tiến độ học tập''. Tài liệu cũng nêu rõ các điều kiện tiên quyết như sinh viên phải đăng nhập và bài làm đã được chấm. Ngoài ra, nội dung bắt đầu giới thiệu về UC-51 liên quan đến việc ứng dụng AI để tạo bài ôn tập.

=== REVIEW QUESTIONS ===
1. Để thực hiện UC-49, bài tập hoặc bài kiểm tra của sinh viên cần phải ở trạng thái nào?
2. Sinh viên cần thực hiện thao tác gì (trigger) để bắt đầu quá trình tra cứu điểm và nhận xét?
3. Hệ thống sẽ hiển thị những thông tin cụ thể nào khi sinh viên xem chi tiết kết quả học tập?
4. Trong trường hợp khóa học chưa có bất kỳ kết quả nào, hệ thống sẽ xử lý như thế nào theo luồng ngoại lệ?','991ad9c4-cd8f-4b75-a923-3d7576d40ca6'::uuid,NULL,NULL,201,569,'2026-03-21 13:35:13.357548+07');
INSERT INTO coaching_chatbot.lecture_knowledge_chunks (lecture_knowledge_chunks_id,lecture_knowledge_id,chunk_index,chunk_content,qdrant_point_id,start_time_seconds,end_time_seconds,page_number,token_count,created_at) VALUES
	 ('da6fd0a0-d178-475a-8fe9-5932ad620c18'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,201,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Bảng A.11: Đặc tảrút gọn UC-51: Tạo bài kiểm tra, ôn tập bằng AI
Use-case Code
UC-51
Use-case Name
Tạo bài kiểm tra, ôn tập bằng AI
Description
Sinh viên tạo đềôn tập cá nhân do AI sinh tựđộng dựa trên nội
dung môn học.
Actors
Sinh viên
Trigger
Sinh viên chọn “Luyện tập cá nhân” và nhấn “Tạo đềluyện tập”.
Pre-conditions
• Sinh viên đã đăng nhập và tham gia ít nhất một khóa học.
• Khóa học có dữliệu/chapter đểAI sinh câu hỏi.
Post-conditions
Đềđược lưu trong danh sách luyện tập và sinh viên có thểlàm
bài và xem kết quả.
Normal Flow
1. Mởtrang “Luyện tập cá nhân”   Nhấn “Tạo đềluyện tập”.
2. B1: Chọn môn, dạng câu hỏi, mức độ, sốlượng.
3. B2: Chọn chapter ưu tiên (hoặc “Chọn tất cả”).
4. B3: Đặt tên đề(gợi ý: “Đềôn [Tên môn] [n]”)   xác nhận.
5. B4: Theo dõi tiến trình AI tạo đề.
6. B5: Nếu thành công   thông báo “Tạo đềthành công”,
cho phép “Làm ngay” hoặc “Xem chi tiết”. Nếu thất bại
báo lỗi và cho phép “Thửlại”.
7. Làm bài trên giao diện quiz   hệthống chấm tựđộng và
hiển thịkết quả.
Alternative Flow
• 4a. Chọn “Sinh lại bằng AI”   hệthống tái sinh bộcâu hỏi.
• 5a. Hủy tạo đề  quay lại danh sách, không lưu tạm.
Exception Flow
• 5e1. AI timeout hoặc lỗi   báo “Tạo đềthất bại, thửlại sau.”
• 9e1. Mất kết nối khi làm bài   tựlưu tạm và khôi phục khi
online.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 183/254

=== SUMMARY ===
Văn bản này đặc tả Use-case UC-51, cung cấp tính năng tạo đề ôn tập cá nhân tự động bằng AI cho sinh viên. Người dùng có thể tùy chỉnh môn học, độ khó, số lượng câu hỏi và phạm vi chương học. Hệ thống hỗ trợ quy trình từ lúc thiết lập thông số, theo dõi tiến trình AI tạo đề đến khi làm bài và nhận kết quả chấm điểm tự động. Ngoài ra, tài liệu còn mô tả các luồng thay thế và xử lý ngoại lệ như lỗi kết nối hoặc AI quá tải.

=== REVIEW QUESTIONS ===
1. Để AI có thể tạo câu hỏi, khóa học cần phải đáp ứng điều kiện tiên quyết nào về dữ liệu?
2. Trong quy trình chuẩn (Normal Flow), sinh viên cần thực hiện những lựa chọn tùy chỉnh nào ở bước 2 và bước 3?
3. Hệ thống sẽ xử lý như thế nào nếu xảy ra lỗi ''AI timeout'' trong quá trình tạo đề?
4. Tính năng ''Tự lưu tạm và khôi phục khi online'' (9e1) giải quyết vấn đề gì cho sinh viên?','b9068d1c-5032-4e62-9d2e-3ecab5336638'::uuid,NULL,NULL,202,576,'2026-03-21 13:35:13.357548+07'),
	 ('25c5b7d9-f1eb-41cd-83df-c49d5d8e6e4a'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,202,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
A.1.12
UC-52: Hỏi đáp kiến thức trong khóa học
Đặc tảUC-52: Hỏi đáp kiến thức trong khóa học được trình bày trong bảng A.12.
Bảng A.12: Đặc tảUC-52: Hỏi đáp kiến thức trong khóa học
Use-case Code
UC-52
Use-case Name
Hỏi đáp kiến thức trong khóa học
Description
Sinh viên hỏi đáp kiến thức liên quan đến nội dung khóa học
qua chatbot, nhận câu trảlời tức thì dựa trên tài liệu khóa học.
Actors
Sinh viên
Trigger
Sinh viên chọn chức năng "Hỏi đáp với chatbot"từtrang khóa
học
Pre-Conditions
• Sinh viên đã đăng nhập hệthống.
• Sinh viên đã đăng ký tham gia khóa học.
Post Conditions
• Câu hỏi được xửlý và phản hồi được cung cấp.
Normal Flow
1. Sinh viên truy cập trang khóa học.
2. Chọn "Hỏi đáp với chatbot".
3. Hệthống hiển thịgiao diện chat với chatbot.
4. Sinh viên nhập câu hỏi (văn bản, có thểkèm từkhóa hoặc
tham chiếu module).
5. Hệthống AI xửlý dựa trên nội dung khóa học và trảvề
phản hồi (giải thích, ví dụ, liên kết tài liệu nội bộ).
6. Sinh viên xem phản hồi và có thểhỏi tiếp trong cùng phiên.
Alternative Flow
5a. Nếu câu hỏi mơ hồ: Chatbot yêu cầu làm rõ thêm →sinh
viên nhập bổsung →quay lại bước 5.
Exceptions
• 3e. Chatbot không sẵn sàng (lỗi AI hoặc bảo trì): Báo
"Chatbot đang tạm ngưng, vui lòng thửlại sau"→quay
lại bước 2.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 184/254

=== SUMMARY ===
Đặc tả UC-52 mô tả chức năng hỗ trợ sinh viên hỏi đáp kiến thức thông qua chatbot AI tích hợp trong khóa học. Sinh viên có thể đặt câu hỏi về nội dung bài học và nhận được phản hồi tức thì bao gồm giải thích, ví dụ và liên kết tài liệu dựa trên dữ liệu khóa học. Hệ thống yêu cầu sinh viên đăng nhập và tham gia khóa học trước khi sử dụng. Ngoài ra, chatbot có khả năng xử lý các câu hỏi mơ hồ và thông báo khi gặp lỗi hệ thống.

=== REVIEW QUESTIONS ===
1. Mục đích chính của chức năng ''Hỏi đáp với chatbot'' (UC-52) là gì?
2. Hệ thống AI dựa trên nguồn dữ liệu nào để cung cấp câu trả lời cho sinh viên?
3. Trong trường hợp sinh viên nhập câu hỏi mơ hồ, chatbot sẽ xử lý như thế nào theo luồng thay thế?
4. Điều kiện tiên quyết để sinh viên có thể kích hoạt chức năng hỏi đáp này là gì?','6d30a7d2-5b68-4136-a97e-9b8ed8c9e12b'::uuid,NULL,NULL,203,557,'2026-03-21 13:35:13.357548+07'),
	 ('1cdb4c88-b00d-434d-8702-e534090d6206'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,203,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
A.1.13
UC-54: Tùy chỉnh thông báo
Đặc tảUC-54: Tùy chỉnh thông báo được trình bày trong bảng A.13.
Bảng A.13: Đặc tảUC-54: Tùy chỉnh thông báo
Use-case Code
UC-54
Use-case Name
Tùy chỉnh thông báo
Description
Sinh viên tùy chỉnh thông báo nhận được từhệthống, bao gồm
bật/tắt cho từng hạng mục (bài tập mới, điểm sốcập nhật, dead-
line, thông báo giảng viên, hoạt động thảo luận), kênh nhận (in-
app, email, push) và tần suất, nhằm cá nhân hóa trải nghiệm.
Actors
Sinh viên
Trigger
Sinh viên chọn chức năng "Cài đặt thông báo"từmenu cá nhân
hoặc trang cài đặt.
Pre-Conditions
• Sinh viên đã đăng nhập hệthống.
Post Conditions
• Cài đặt thông báo được lưu thành công và áp dụng ngay
lập tức cho các thông báo sau.
Normal Flow
1. Sinh viên truy cập trang "Cài đặt cá nhân".
2. Chọn "Tùy chỉnh thông báo".
3. Hệthống hiển thịform với các loại thông báo hiện tại
(bật/tắt, kênh nhận, tần suất như hàng ngày/tức thì).
4. Sinh viên chỉnh sửa (ví dụ: tắt thông báo deadline qua
email, bật gửi thông báo cho điểm số).
5. Nhấn "Lưu thay đổi".
6. Hệthống kiểm tra dữliệu hợp lệ(không xung đột).
7. Hệthống lưu cài đặt vào DB.
8. Hiển thịthông báo "Cài đặt đã cập nhật thành công".
Alternative Flow
3a. Sinh viên chọn "Khôi phục mặc định": Hệthống đặt lại về
cài đặt gốc →quay lại bước 4.
4a. Xem preview: Sinh viên chọn "Xem ví dụthông báo"→hệ
thống hiển thịmẫu →quay lại bước 5.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 185/254

=== SUMMARY ===
Tài liệu trình bày đặc tả Use-case UC-54 về chức năng tùy chỉnh thông báo dành cho sinh viên. Chức năng này cho phép người dùng cá nhân hóa trải nghiệm bằng cách bật/tắt các loại thông báo (bài tập, điểm số, deadline, thảo luận), lựa chọn kênh nhận (in-app, email, push) và điều chỉnh tần suất nhận tin. Quy trình bao gồm các bước từ truy cập cài đặt, chỉnh sửa form đến lưu trữ vào cơ sở dữ liệu, cùng các luồng thay thế như khôi phục mặc định và xem trước thông báo.

=== REVIEW QUESTIONS ===
1. Mục đích chính của Use-case UC-54 là gì và những hạng mục thông báo nào sinh viên có thể tùy chỉnh?
2. Hệ thống hỗ trợ những kênh nhận thông báo và tần suất nhận tin cụ thể nào cho sinh viên?
3. Trình bày các bước chính trong luồng hoạt động bình thường (Normal Flow) của chức năng tùy chỉnh thông báo.
4. Trong các luồng thay thế (Alternative Flow), sinh viên có thể thực hiện những thao tác bổ sung nào?','9f93d3be-7698-4587-b27a-908c9cc0e8ee'::uuid,NULL,NULL,204,617,'2026-03-21 13:35:13.358567+07'),
	 ('021f5f90-6f2b-4bcf-a1a6-ee1f2dd4708a'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,204,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Exceptions
• Không có exception được liệt kê.
A.2
Lược đồuse-case đối với giảng viên
A.2.1
UC-17: Nhắn tin và thảo luận
Đặc tảUC-17: Nhắn tin và thảo luận được trình bày trong bảng A.14.
Bảng A.14: Đặc tảUC-17: Nhắn tin và thảo luận
Use-case Code
UC-17
Use-case Name
Nhắn tin và thảo luận
Description
Cho phép sinh viên và giảng viên trao đổi trong lớp học qua tin
nhắn hoặc diễn đàn thảo luận. Tin nhắn có thểtheo cá nhân, nhóm
nhỏhoặc toàn bộlớp.
Actors
Giảng viên, Sinh viên
Trigger
Người dùng chọn "Tin nhắn/Thảo luận".
Pre-Conditions
• Người dùng đã đăng nhập và tham gia khóa học.
• Kết nối mạng ổn định.
Post
Condi-
tions
• Tin nhắn/thảo luận được gửi và hiển thịcho người nhận.
• Lịch sửhội thoại được lưu lại.
Normal Flow
1. Người dùng mởmục "Tin nhắn/Thảo luận".
2. Hệthống hiển thịdanh sách phòng chat hoặc chủđềthảo
luận.
3. Người dùng chọn một phòng chat/chủđề.
4. Hệthống hiển thịnội dung hội thoại/thảo luận hiện có.
5. Người dùng nhập tin nhắn hoặc bình luận.
6. Nhấn "Gửi".
7. Hệthống kiểm tra tin nhắn (format, nội dung, dung lượng file
đính kèm nếu có).
8. Tin nhắn được gửi và hiển thịtức thì cho người khác.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 186/254

=== SUMMARY ===
Tài liệu này đặc tả use-case UC-17 ''Nhắn tin và thảo luận'', một chức năng quan trọng cho phép giảng viên và sinh viên tương tác trong môi trường học tập. Use-case này hỗ trợ trao đổi qua tin nhắn cá nhân, nhóm hoặc diễn đàn chung. Nội dung tập trung vào các điều kiện cần thiết (đăng nhập, mạng ổn định), quy trình thực hiện các bước gửi tin nhắn và việc hệ thống tự động kiểm tra định dạng, nội dung cũng như lưu trữ lịch sử hội thoại để phục vụ việc tra cứu sau này.

=== REVIEW QUESTIONS ===
1. Những đối tượng (actors) nào có quyền tham gia vào use-case UC-17?
2. Để sử dụng chức năng nhắn tin, người dùng cần thỏa mãn những điều kiện tiên quyết nào?
3. Trong luồng xử lý chuẩn (Normal Flow), hệ thống thực hiện kiểm tra những yếu tố gì trước khi gửi tin nhắn?
4. Kết quả sau khi thực hiện thành công use-case này (Post-conditions) bao gồm những gì?','65360997-705d-4cde-b84c-08c11386679b'::uuid,NULL,NULL,205,544,'2026-03-21 13:35:13.358567+07'),
	 ('d13928b6-2759-4448-808f-1b2928f79e6f'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,205,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
9. Hệthống lưu tin nhắn vào DB.
Alternative
Flow
2a. Tạo chủđềmới: Người dùng chọn "Tạo chủđềmới"  nhập
tiêu đề+ nội dung   quay lại bước 3.
5a. Đính kèm file: Người dùng chọn file (PDF, Word, ảnh, video)
  hệthống upload và đính kèm   quay lại bước 6.
6a. Tin nhắn riêng tư: Người dùng chọn gửi 1-1   chỉhiển thịcho
người nhận   quay lại bước 7.
Exceptions
• 3e. Chủđềkhông tồn tại: Hệthống báo lỗi   quay lại bước
2.
• 7e. Nội dung vi phạm: Nếu phát hiện từkhóa cấm   báo lỗi,
yêu cầu sửa lại   quay lại bước 5.
• • 8e. Kết nối bịgián đoạn: Tin nhắn không gửi được   hệthống
lưu tạm ởqueue   thửgửi lại sau.
• 9e. Lỗi DB: Không thểlưu tin nhắn   cảnh báo cho người
dùng.
A.2.2
UC-20: Tạo khóa học phụtrợ
Đặc tảUC-20: Tạo khóa học phụtrợđược trình bày trong bảng A.15.
Bảng A.15: Đặc tảUC-20: Tạo khóa học phụtrợ
Use-case Code
UC-20
Use-case Name
Tạo khóa học phụtrợ
Description
Giảng viên có thểtạo các khóa học bổtrợ(không thuộc chương
tình đào tạo chính thức, không ràng buộc học kỳ). Các khóa học
này phục vụmục đích học thêm, ôn tập, củng cốkiến thức.
Actors
Giảng viên
Trigger
Giảng viên chọn chức năng "Tạo khóa học"trong mục quản lý khóa
học.
Pre-Conditions
• Giảng viên đã được cấp quyền tạo khóa học mới
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 187/254

=== SUMMARY ===
Nội dung này trình bày các luồng xử lý thay thế và ngoại lệ của tính năng nhắn tin/thảo luận (UC-17), bao gồm tạo chủ đề mới, đính kèm tệp và xử lý lỗi kết nối hoặc nội dung vi phạm. Đồng thời, tài liệu giới thiệu đặc tả Use-case UC-20: Tạo khóa học phụ trợ. Đây là chức năng dành cho giảng viên để thiết lập các khóa học không chính quy, không ràng buộc học kỳ, nhằm phục vụ mục đích bổ trợ kiến thức và ôn tập cho sinh viên.

=== REVIEW QUESTIONS ===
1. Trong UC-17, hệ thống xử lý như thế nào khi phát hiện tin nhắn có nội dung vi phạm từ khóa cấm?
2. Hệ thống sẽ làm gì nếu tin nhắn không gửi được do kết nối mạng bị gián đoạn theo đặc tả ngoại lệ 8e?
3. Đặc điểm khác biệt lớn nhất của khóa học phụ trợ (UC-20) so với khóa học chính thức là gì?
4. Để thực hiện chức năng tạo khóa học phụ trợ, giảng viên cần thỏa mãn điều kiện tiên quyết nào?','82a6296b-2439-4cd9-8adc-559b074fca80'::uuid,NULL,NULL,206,564,'2026-03-21 13:35:13.358567+07'),
	 ('f576b43a-10d9-464c-bab9-3fd91a7b2206'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,206,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Post
Condi-
tions
• Khóa học online được tạo thành công.
• Khóa học này không gắn với học kỳ, sinh viên có thểtham
gia bất kỳlúc nào.
Normal Flow
1. Giảng viên chọn Tạo khóa học phụtrợ.
2. Hệthống hiển thị2 lựa chọn:
• UC-201: Tạo mới hoàn toàn
• UC-202: Tạo từkhóa học cũ
3. Giảng viên chọn 1 trong 2 cách và thực hiện theo flow tương
ứng.
4. Hệthống tạo khóa học và hiển thịthông báo thành công.
Alternative
Flow
Không có
Exceptions
Không có
A.2.3
UC-201: Tạo mới hoàn toàn khóa học
Đặc tảUC-201: Tạo mới hoàn toàn khóa học được trình bày trong bảng A.16.
Bảng A.16: Đặc tảUC-201: Tạo mới hoàn toàn khóa học
Use-case Code
UC-201
Use-case Name
Tạo mới hoàn toàn khóa học
Description
Giảng viên có thểtạo các khóa học bổtrợ(không thuộc chương
trình đào tạo chính thức, không ràng buộc học kỳ). Các khóa học
này phục vụmục đích học thêm, ôn tập, củng cốkiến thức.
Actors
Giảng viên
Trigger
Giảng viên chọn chức năng "Tạo khóa học"trong mục quản lý khóa
học.
Pre-Conditions
• Giảng viên đã chọn UC-20   Tạo mới hoàn toàn.
Post
Condi-
tions
• Khóa học online được tạo thành công.
• Khóa học này không gắn với học kỳ, sinh viên có thểtham
gia bất kỳlúc nào.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 188/254

=== SUMMARY ===
Phân đoạn này trình bày đặc tả chi tiết cho Use-case UC-201 về việc tạo mới hoàn toàn một khóa học phụ trợ dành cho giảng viên. Các khóa học này không thuộc chương trình đào tạo chính thức, không bị ràng buộc bởi học kỳ, nhằm phục vụ mục đích bổ trợ kiến thức và ôn tập cho sinh viên. Nội dung bao gồm các điều kiện tiên quyết, luồng xử lý chuẩn từ việc lựa chọn phương thức tạo đến khi hệ thống xác nhận thành công, cùng các điều kiện sau khi thực hiện.

=== REVIEW QUESTIONS ===
1. Mục đích chính của các khóa học phụ trợ được tạo thông qua UC-201 là gì?
2. Có những lựa chọn nào khi giảng viên bắt đầu quy trình tạo một khóa học phụ trợ?
3. Đặc điểm về thời gian tham gia của sinh viên đối với loại khóa học này có gì khác biệt so với khóa học chính thức?
4. Điều kiện tiên quyết để giảng viên có thể thực hiện đặc tả UC-201 là gì?','2000b0b8-0ce4-4efb-8caf-31fed6484fdf'::uuid,NULL,NULL,207,546,'2026-03-21 13:35:13.358567+07'),
	 ('3c762c45-ad2c-4575-a447-237da76698f3'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,207,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Normal Flow
1. Hệthống hiển thịform nhập chi tiết thông tin khóa học (Mã
khóa học, Topic, Tên khóa học, Mô tả, Ngôn ngữ, Trình độ,
Các khóa học tiên quyết).
2. Giảng viên nhập đầy đủcác trường bắt buộc.
3. Giảng viên có thểthêm thông tin tùy chọn: ảnh bìa, video giới
thiệu, tags.
4. Nhấn Lưu (có thểchọn lưu ởtrạng thái Draft hoặc Public).
5. Hệthống kiểm tra hợp lệ(tên không trống, mô tả<= 500 ký
tự, ...).
6. Hệthống lưu và hiển thịthông báo thành công.
Alternative
Flow
2a. Nếu giảng viên chỉnhập thông tin cơ bản và chọn Draft
khóa học tạo nhưng không công khai.
3a. Nếu không upload ảnh bìa   hệthống gán ảnh mặc định.
Exceptions
• 5e1. Nếu thiếu tên hoặc mô tả  báo "Vui lòng nhập thông
tin bắt buộc".
• 5e2. Nếu nhập mô tảquá dài   báo lỗi "Mô tảvượt quá giới
hạn ký tự".
• 6e. Nếu lỗi lưu DB   báo "Không thểlưu khóa học, thửlại
sau".
A.2.4
UC-202: Tạo từkhóa học cũ
Đặc tảUC-202: Tạo từkhóa học cũ được trình bày trong bảng A.17.
Bảng A.17: Đặc tảUC-202: Tạo từkhóa học cũ
Use-case Code
UC-202
Use-case Name
Tạo từkhóa học cũ
Description
Giảng viên có thểtạo các khóa học bổtrợ(không thuộc chương
tình đào tạo chính thức, không ràng buộc học kỳ). Các khóa học
này phục vụmục đích học thêm, ôn tập, củng cốkiến thức.
Actors
Giảng viên
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 189/254

=== SUMMARY ===
Nội dung này mô tả chi tiết quy trình nghiệp vụ cho hai Use-case: UC-201 (Tạo mới hoàn toàn khóa học) và UC-202 (Tạo từ khóa học cũ). Giảng viên có thể thiết lập các khóa học bổ trợ bằng cách nhập thông tin chi tiết, tùy chọn hình ảnh và trạng thái hiển thị. Hệ thống thực hiện kiểm tra tính hợp lệ của dữ liệu như độ dài mô tả và các trường bắt buộc, đồng thời xử lý các tình huống ngoại lệ như lỗi kết nối cơ sở dữ liệu.

=== REVIEW QUESTIONS ===
1. Trong quy trình UC-201, giới hạn ký tự tối đa cho phần mô tả khóa học là bao nhiêu?
2. Hệ thống sẽ xử lý như thế nào nếu giảng viên không tải lên ảnh bìa cho khóa học?
3. Giảng viên có thể chọn những trạng thái lưu trữ nào khi hoàn tất nhập liệu khóa học?
4. Mục đích của việc tạo các khóa học phụ trợ (UC-202) là gì và đối tượng nào thực hiện?','2ea21662-e506-48b1-875b-fbaf26f484b5'::uuid,NULL,NULL,208,559,'2026-03-21 13:35:13.358567+07'),
	 ('9d3a7129-397c-4bcc-8eee-58913f63ea86'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,257,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Đặc điểm chính:
• Cho phép nhập nội dung câu hỏi, đáp án A/B/C/D, điểm, điều kiện và tùy chọn cho
phép nộp file hoặc xem điểm trực tiếp.
• Rubric chấm bài và khu vực test case giúp đảm bảo tiêu chuẩn chấm điểm rõ ràng
trước khi lưu câu hỏi.
B.2.4.2
Thêm câu hỏi thủcông - Tựluận
Hình B.32: Thêm câu hỏi tựluận
Đặc điểm chính:
• Cho phép nhập nội dung câu hỏi dài, xác định điểm, sốlần làm, ngôn ngữvà kiểm
soát điều kiện vượt qua.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 239/254

=== SUMMARY ===
Đoạn văn bản này hướng dẫn cách thêm câu hỏi thủ công vào hệ thống cho hai loại hình: trắc nghiệm và tự luận. Đối với câu hỏi trắc nghiệm, giảng viên có thể thiết lập đáp án, điểm số, rubric và test case để đảm bảo tính minh bạch. Đối với câu hỏi tự luận, hệ thống cho phép nhập nội dung dài, quy định số lần làm bài, ngôn ngữ và điều kiện vượt qua. Các tính năng này giúp chuẩn hóa quy trình chấm điểm và quản lý học liệu hiệu quả.

=== REVIEW QUESTIONS ===
1. Khi thêm câu hỏi trắc nghiệm thủ công, giảng viên có thể thiết lập những tùy chọn nào liên quan đến phản hồi kết quả cho sinh viên?
2. Vai trò của Rubric chấm bài và khu vực test case là gì trong quá trình lưu câu hỏi?
3. Những đặc điểm chính nào cần xác định khi giảng viên tạo một câu hỏi tự luận mới?
4. Làm thế nào để đảm bảo tiêu chuẩn chấm điểm rõ ràng trước khi lưu một câu hỏi vào hệ thống?','819e24cf-c64e-4340-be69-a8ab7062b811'::uuid,NULL,NULL,258,371,'2026-03-21 13:35:13.370006+07'),
	 ('15207c60-3439-4b66-a6cf-74bd5ab5a5b2'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,208,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Trigger
Giảng viên chọn chức năng "Tạo khóa học"trong mục quản lý khóa
học.
Pre-Conditions
• Giảng viên đã có ít nhất một khóa học trước đó.
Post
Condi-
tions
• Một khóa học mới được tạo, có thông tin và nội dung được
copy theo lựa chọn.
• Khóa học này không gắn với học kỳ, sinh viên có thểtham
gia bất kỳlúc nào.
Normal Flow
1. Hệthống hiển thịdanh sách khóa học cũ.
2. Giảng viên chọn một khóa học nguồn.
3. Hệthống hiển thịpopup chọn thành phần cần copy: Chỉcopy
cấu trúc module/section; Copy cảtài liệu, bài tập, quiz; Copy
cấu hình hiển thị(ảnh bìa, video giới thiệu).
4. Giảng viên tick chọn và nhấn Tiếp tục.
5. Hệthống hiển thịform nhập thông tin cơ bản cho khóa học
mới (Tên, Mô tả, Thểloại, Trạng thái).
6. Giảng viên chỉnh sửa thông tin và nhấn Tạo.
7. Hệthống kiểm tra hợp lệvà tạo khóa học mới dưới trạng thái
Draft.
8. Hệthống hiển thị"Sao chép khóa học thành công".
Alternative
Flow
3a. Nếu giảng viên chọn Copy toàn bộ  hệthống copy tất cảtrừ
danh sách sinh viên.
3b. Nếu giảng viên chọn Chỉcopy cấu trúc module   hệthống
tạo course trống (chỉcó skeleton).
Exceptions
• 6e. Nếu lỗi   báo "Không sao chép khóa học, thửlại sau".
A.2.5
UC-22: Xem thông tin khóa học
Đặc tảUC-22: Xem thông tin khóa học được trình bày trong bảng A.16.
Bảng A.18: Đặc tảUC-22: Xem thông tin khóa học
Use-case Code
UC-22
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 190/254

=== SUMMARY ===
Đoạn văn bản mô tả chi tiết đặc tả cho Use-case UC-202 (Tạo khóa học từ khóa học cũ) và giới thiệu UC-22 (Xem thông tin khóa học). Nội dung tập trung vào quy trình giảng viên sao chép các thành phần như cấu trúc module, tài liệu, bài tập từ một khóa học nguồn để tạo khóa học bổ trợ mới. Hệ thống cho phép tùy chỉnh các thành phần được copy, thiết lập thông tin cơ bản và mặc định lưu ở trạng thái bản nháp (Draft) mà không ràng buộc học kỳ.

=== REVIEW QUESTIONS ===
1. Điều kiện tiên quyết để giảng viên có thể thực hiện chức năng tạo khóa học từ khóa học cũ là gì?
2. Trong luồng xử lý chính của UC-202, giảng viên có những lựa chọn nào khi chọn thành phần cần sao chép?
3. Khi giảng viên chọn chế độ ''Copy toàn bộ'', thành phần cụ thể nào sẽ bị hệ thống loại trừ không sao chép?
4. Sau khi hệ thống kiểm tra hợp lệ và tạo khóa học mới thành công, khóa học đó sẽ ở trạng thái mặc định nào?','65e0aa42-ec2b-47f7-a714-b0760e2cca94'::uuid,NULL,NULL,209,600,'2026-03-21 13:35:13.360093+07'),
	 ('057dde35-82b7-4738-8402-0cca5ad11d51'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,209,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Use-case Name
Xem thông tin khóa học
Description
Giảng viên xem chi tiết thông tin khóa học (mô tả, tài liệu, thang
điểm, lịch học).
Actors
Giảng viên
Trigger
Giảng viên chọn chức năng "Xem thông tin khóa học"trong mục
quản lý khóa học.
Pre-Conditions
• Giảng viên đã đăng nhập hệthống.
• Giảng viên có quyền quản lý khóa học.
• Khóa học đã tồn tại.
Post
Condi-
tions
• Thông tin khóa học được hiển thịtheo tiêu chí tìm kiếm/ sắp
xếp.
Normal Flow
1. Giảng viên truy cập vào trang "Danh sách khóa học".
2. Hệthống hiển thịdanh sách tất cảkhóa học giảng viên quản
lý.
3. Giảng viên có thể:
• Sort: sắp xếp theo tên, ngày tạo, ngày bắt đầu, trạng thái.
• Filter:
lọc
theo
học
kỳ,
danh
mục,
trạng
thái
(Draft/Active/Closed).
• Search: nhập từkhóa (mã khóa học, tên khóa học).
4. Giảng viên chọn 1 khóa học từdanh sách.
5. Hệthống hiển thịchi tiết thông tin khóa học (tên, mã, mô tả,
tài liệu, thang điểm, cấu trúc. . . ).
Alternative
Flow
3a. Giảng viên có thểxuất danh sách dưới dạng Excel/PDF.
4a. Cho phép mởpop-up xem tóm tắt thay vì vào trang chi tiết.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 191/254

=== SUMMARY ===
Tài liệu trình bày đặc tả Use-case về việc xem thông tin khóa học dành cho giảng viên. Giảng viên có thể truy cập danh sách các khóa học do mình quản lý, thực hiện các thao tác tìm kiếm, sắp xếp và lọc thông tin theo nhiều tiêu chí khác nhau. Hệ thống cung cấp thông tin chi tiết về mã khóa học, tài liệu, thang điểm và cấu trúc. Ngoài ra, giảng viên có thể xuất dữ liệu ra tệp Excel/PDF hoặc xem nhanh qua cửa sổ pop-up.

=== REVIEW QUESTIONS ===
1. Để thực hiện xem thông tin khóa học, giảng viên cần đáp ứng những điều kiện tiên quyết nào?
2. Hệ thống hỗ trợ giảng viên lọc (Filter) danh sách khóa học dựa trên các tiêu chí nào?
3. Trong luồng thay thế (Alternative Flow), giảng viên có thể thực hiện những hành động bổ sung nào thay vì chỉ xem chi tiết?
4. Thông tin chi tiết của một khóa học được hiển thị bao gồm những thành phần cơ bản nào?','e6b593e9-017b-4667-96e1-8307cb848a5e'::uuid,NULL,NULL,210,524,'2026-03-21 13:35:13.360093+07');
INSERT INTO coaching_chatbot.lecture_knowledge_chunks (lecture_knowledge_chunks_id,lecture_knowledge_id,chunk_index,chunk_content,qdrant_point_id,start_time_seconds,end_time_seconds,page_number,token_count,created_at) VALUES
	 ('dafbbaaf-b1c1-46c7-8333-2f02a02e9f43'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,210,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Exceptions
• 3e. Không có kết quả: Khi tìm kiếm/ lọc không khớp   hiển
thị"Không tìm thấy khóa học nào".
• 4e. Quyền truy cập: Nếu giảng viên cốxem khóa học không
thuộc quyền quản lý   báo lỗi "Bạn không có quyền xem
khóa học này".
• 5e. Lỗi hệthống: Trang không tải được   hiển thịthông báo
"Lỗi hệthống, vui lòng thửlại sau".
A.2.6
UC-24: Thiết lập hệthống thang điểm
Đặc tảUC-24: Thiết lập hệthống thang điểm được trình bày trong bảng A.19.
Bảng A.19: Đặc tảUC-24: Thiết lập hệthống thang điểm
Use-case Code
UC-24
Use-case Name
Thiết lập hệthống thang điểm
Description
Giảng viên định nghĩa cách tính điểm của khóa học, bao gồm các
thành phần như Assignment, Quiz, Midterm, Final. Thang điểm là
cơ sởđểsinh viên theo dõi kết quảhọc tập.
Actors
Giảng viên
Trigger
Giảng viên chọn chức năng "Thiết lập thang điểm".
Pre-Conditions
• Giảng viên đã đăng nhập hệthống.
• Giảng viên có quyền quản lý khóa học.
• Khóa học đã tồn tại.
Post
Condi-
tions
• Thang điểm hợp lệđược lưu thành công.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 192/254

=== SUMMARY ===
Nội dung này trình bày các trường hợp ngoại lệ của việc xem thông tin khóa học (UC-22) và đặc tả chi tiết cho Use-case UC-24: Thiết lập hệ thống thang điểm. UC-24 cho phép giảng viên định nghĩa các thành phần điểm số như bài tập, kiểm tra, thi giữa kỳ và cuối kỳ, giúp sinh viên theo dõi lộ trình học tập. Tài liệu nêu rõ các điều kiện cần thiết như đăng nhập và quyền quản lý, cùng các thông báo lỗi hệ thống hoặc quyền truy cập.

=== REVIEW QUESTIONS ===
1. Trong UC-22, hệ thống sẽ hiển thị thông báo gì nếu giảng viên truy cập vào khóa học không thuộc quyền quản lý của mình?
2. Mục đích chính của chức năng ''Thiết lập hệ thống thang điểm'' (UC-24) là gì?
3. Kể tên các thành phần điểm số được nhắc đến trong phần mô tả của UC-24.
4. Để thực hiện thiết lập thang điểm, giảng viên cần thỏa mãn những điều kiện tiên quyết nào?','ba4cd131-713b-4ce2-a2b1-3bb945b1a031'::uuid,NULL,NULL,211,502,'2026-03-21 13:35:13.360093+07'),
	 ('bc0c0b12-4047-4cdf-8cf5-0974bf4ab6c6'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,211,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Normal Flow
1. Giảng viên truy cập khóa học cần chỉnh sửa.
2. Chọn "Thiết lập thang điểm".
3. Hệthống hiển thịform nhập các cột điểm.
4. Giảng viên nhập thông tin (tên cột, trọng số).
5. Nhấn "Lưu".
6. Hệthống kiểm tra tổng trọng số= 100% và không trùng tên.
7. Lưu thành công   báo "Thiết lập thành công".
Alternative
Flow
3a. Giảng viên tải file Excel mẫu   upload   hệthống đọc dữ
liệu   quay lại bước 6.
3b. Giảng viên chọn mẫu chuẩn   hệthống điền sẵn   quay lại
bước 4.
Exceptions
• 6e1. Tổng trọng số!= 100%: Hệthống báo lỗi   quay lại
bước 4.
• 6e2. Trùng tên cột: Báo lỗi   quay lại bước 4.
• 7e. Hệthống không lưu được   báo "Không thểlưu"  quay
lại bước 5.
A.2.7
UC-25: Tải lên và cập nhật tài liệu
Đặc tảUC-25: Tải lên và cập nhật tài liệu được trình bày trong bảng A.20.
Bảng A.20: Đặc tảUC-25: Tải lên và cập nhật tài liệu
Use-case Code
UC-25
Use-case Name
Tải lên và cập nhật tài liệu
Description
Giảng viên tải lên các tài liệu phục vụhọc tập (slide, ebook, video,
bài đọc, liên kết) đểcung cấp cho sinh viên. Ngoài ra có thểcập
nhật hoặc thay thếcác tài liệu cũ nhằm đảm bảo nội dung luôn mới
nhất.
Actors
Giảng viên
Trigger
Giảng viên chọn chức năng "Bài giảng".
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 193/254

=== SUMMARY ===
Đoạn văn bản mô tả chi tiết hai quy trình nghiệp vụ trong hệ thống quản lý học tập: UC-24 (Thiết lập hệ thống thang điểm) và UC-25 (Tải lên và cập nhật tài liệu). Giảng viên có thể định nghĩa các thành phần điểm số với điều kiện tổng trọng số đạt 100% thông qua nhập liệu trực tiếp hoặc file Excel. Đồng thời, giảng viên có quyền quản lý và cập nhật đa dạng các loại tài liệu học tập như slide, video, ebook nhằm cung cấp nội dung mới nhất cho sinh viên.

=== REVIEW QUESTIONS ===
1. Trong UC-24, hệ thống thực hiện những kiểm tra ràng buộc nào trước khi xác nhận thiết lập thang điểm thành công?
2. Giảng viên có thể sử dụng những phương thức thay thế nào để nhập liệu thang điểm thay vì nhập trực tiếp vào biểu mẫu?
3. Khi tổng trọng số các cột điểm không bằng 100%, hệ thống sẽ xử lý như thế nào và người dùng phải thực hiện lại từ bước nào?
4. Mục đích và các loại định dạng tài liệu mà giảng viên có thể quản lý trong trường hợp sử dụng UC-25 là gì?','51edab92-b44e-4fb7-be85-2f64472ddbbc'::uuid,NULL,NULL,212,582,'2026-03-21 13:35:13.360093+07'),
	 ('6f4a73f8-e4c5-497e-aff5-ac2dee8b87e2'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,258,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
B.2.4.3
Thêm câu hỏi thủcông - Code
Hình B.33: Thêm câu hỏi code
Đặc điểm chính:
• Cho phép giáo viên nhập input/output mẫu, quy định giới hạn thực thi và tải file
kèm test case.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 240/254

=== SUMMARY ===
Đoạn văn bản này mô tả chức năng thêm câu hỏi lập trình (Code) một cách thủ công trong hệ thống quản lý học tập. Chức năng này cho phép giáo viên thiết lập các yếu tố kỹ thuật quan trọng như dữ liệu đầu vào/đầu ra mẫu, cấu hình giới hạn thực thi của chương trình và tải lên các tệp tin chứa bộ kiểm thử (test case) để phục vụ quá trình chấm bài tự động.

=== REVIEW QUESTIONS ===
1. Mục B.2.4.3 trình bày về chức năng cụ thể nào của hệ thống?
2. Giáo viên cần nhập những thông tin mẫu nào khi tạo câu hỏi dạng Code?
3. Ngoài input/output mẫu, giáo viên còn có thể thiết lập giới hạn nào cho bài làm của sinh viên?
4. Làm thế nào để giáo viên đưa các bộ test case vào hệ thống khi tạo câu hỏi thủ công?','84d6fb59-9a4d-4a6b-b45d-0d6055ee2989'::uuid,NULL,NULL,259,269,'2026-03-21 13:35:13.370006+07'),
	 ('b3ec4cf2-1108-45fb-a567-967288aab444'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,212,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Pre-Conditions
• Giảng viên đã đăng nhập hệthống.
• Giảng viên có quyền quản lý khóa học.
• Khóa học đã tồn tại.
Post
Condi-
tions
• Tài liệu được lưu thành công, hiển thịcho sinh viên.
Normal Flow
1. Giảng viên truy cập khóa học cần chỉnh sửa.
2. Chọn "Bài giảng".
3. Hệthống hiển thịdanh sách tài liệu.
4. Giảng viên chọn "Tải lên"hoặc "Cập nhật".
5. Chọn file từmáy tính.
6. Nhập thông tin mô tả(tên, loại, module).
7. Nhấn "Upload".
8. Hệthống kiểm tra định dạng và dung lượng.
9. Lưu file vào storage + cập nhật DB.
10. Hiển thị"Upload thành công".
Alternative
Flow
4a. Giảng viên chọn nhiều file   hệthống xửlý tuần tự  tiếp tục
bước 7.
4b. Giảng viên chọn file đã có   thay thếbằng file mới   tiếp tục
bước 7.
3c. Giảng viên chọn file đã có   xoá.
Exceptions
• 8e. File sai định dạng: Báo lỗi "Định dạng không hỗtrợ"
quay lại bước 4.
• 8e2. File quá dung lượng: Báo lỗi "Vượt quá giới hạn"  quay
lại bước 4.
• 9e. Upload thất bại: Báo "Upload thất bại"  quay lại bước 6.
A.2.8
UC-27: Chỉnh sửa bài tập
Đặc tảUC-27: Chỉnh sửa bài tập được trình bày trong bảng A.21.
Bảng A.21: Đặc tảUC-27: Chỉnh sửa bài tập
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 194/254

=== SUMMARY ===
Phân đoạn này mô tả chi tiết quy trình nghiệp vụ của Use-case UC-25 (Tải lên và cập nhật tài liệu) và giới thiệu Use-case UC-27 (Chỉnh sửa bài tập). Giảng viên có thể thực hiện các thao tác quản lý tài liệu như tải lên, cập nhật hoặc xóa, đồng thời hệ thống đảm bảo kiểm tra định dạng và dung lượng tệp tin. Tài liệu cũng xác định các điều kiện cần thiết trước khi thực hiện và các thông báo lỗi tương ứng khi quá trình tải lên gặp sự cố.

=== REVIEW QUESTIONS ===
1. Hệ thống thực hiện những kiểm tra kỹ thuật cụ thể nào đối với tệp tin ở bước 8 của Use-case UC-25?
2. Nêu ba điều kiện tiên quyết (Pre-conditions) để giảng viên có thể thực hiện các thao tác trong tài liệu này.
3. Trong trường hợp giảng viên muốn tải lên nhiều tệp cùng lúc, hệ thống sẽ xử lý như thế nào theo luồng thay thế 4a?
4. Khi gặp lỗi ''File quá dung lượng'' (ngoại lệ 8e2), quy trình sẽ quay trở lại bước nào để người dùng thực hiện lại?','11f00ad5-4cc5-4b67-a9f8-c8666a912f56'::uuid,NULL,NULL,213,556,'2026-03-21 13:35:13.361121+07'),
	 ('38732120-e44b-4f6a-a8be-2a67b420db46'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,213,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Use-case Code
UC-27
Use-case Name
Chỉnh sửa bài tập
Description
Giảng viên chỉnh sửa bài tập đã được tạo, bao gồm nội dung, hình
thức, cấu hình (deadline, điểm tối đa, cho phép nộp lại), hoặc cập
nhật file đính kèm. Giảng viên cũng có thểxem trước lại giao diện
trước khi lưu.
Actors
Giảng viên
Trigger
Giảng viên chọn "Chỉnh sửa"tại một bài tập.
Pre-Conditions
• Giảng viên đã đăng nhập hệthống.
• Bài tập đã tồn tại.
• Giảng viên có quyền chỉnh sửa bài tập đó.
Post
Condi-
tions
• Thông tin bài tập được cập nhật thành công.
Normal Flow
1. Giảng viên mởdanh sách bài tập.
2. Chọn một bài tập   nhấn "Chỉnh sửa".
3. Hệthống hiển thịform với dữliệu hiện tại.
4. Giảng viên chỉnh sửa thông tin: tiêu đề, mô tả, hình thức,
deadline, file.
5. (Nếu có trắc nghiệm)   chỉnh sửa câu hỏi hoặc import Excel
mới.
6. Giảng viên chọn "Xem trước giao diện làm bài".
7. Nhấn "Lưu thay đổi".
8. Hệthống kiểm tra dữliệu hợp lệ.
9. Cập nhật vào DB.
10. Hiển thịthông báo thành công.
Alternative
Flow
4a. Xóa file đính kèm cũ: Giảng viên chọn "Xóa file"  quay lại
bước 4.
5a. Thêm câu hỏi mới: Giảng viên nhập thêm câu hỏi trắc nghiệm
  quay lại bước 6.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 195/254

=== SUMMARY ===
Đoạn văn bản mô tả chi tiết Use-case UC-27 về quy trình chỉnh sửa bài tập của giảng viên trên hệ thống. Nội dung bao gồm các thông tin có thể điều chỉnh như nội dung, thời hạn, tệp đính kèm và cấu hình bài tập. Quy trình bắt đầu từ việc chọn bài tập, chỉnh sửa dữ liệu, xem trước giao diện và kết thúc khi hệ thống lưu thành công vào cơ sở dữ liệu. Tài liệu cũng nêu rõ các điều kiện tiên quyết và các luồng thay thế khi quản lý câu hỏi trắc nghiệm.

=== REVIEW QUESTIONS ===
1. Giảng viên có thể thực hiện những thay đổi cụ thể nào đối với một bài tập đã tồn tại?
2. Điều kiện tiên quyết để giảng viên có thể thực hiện Use-case UC-27 là gì?
3. Trong luồng công việc chuẩn, bước nào giúp giảng viên kiểm tra lại hiển thị của bài tập trước khi xác nhận lưu?
4. Hệ thống sẽ thực hiện những hành động gì sau khi giảng viên nhấn nút ''Lưu thay đổi''?','e70f5aff-0e53-4bd7-a6f4-6709bc0642eb'::uuid,NULL,NULL,214,544,'2026-03-21 13:35:13.361121+07'),
	 ('3c3f1d08-1616-4d81-9719-073822116016'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,214,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Exceptions
• 4e. Deadline sửa thành ngày quá khứ: Báo lỗi   quay lại bước
4.
• 5e. File Excel lỗi định dạng: Không import được   quay lại
bước 5.
• 9e. Lỗi DB: Không thểlưu   quay lại bước 7.
A.2.9
UC-29: Quản lý khoá học
Đặc tảUC-29: Quản lý khóa học được trình bày trong bảng A.22.
Bảng A.22: Đặc tảUC-29: Quản lý khóa học
Use-case Code
UC-29
Use-case Name
Quản lý khóa học
Description
Giảng viên quản lý toàn bộthông tin liên quan đến khóa học bao
gồm tạo/thay đổi khóa học, gán danh mục, xem thông tin, xây dựng
cấu trúc, thiết lập thang điểm và cập nhật tài liệu.
Actors
Teacher (Giảng viên)
Trigger
Giảng viên đăng nhập vào hệthống và chọn chức năng "Quản lý
khóa học"
Pre-Conditions
• Giảng viên đã được cấp quyền quản lý ít nhất một khóa học
Post
Condi-
tions
• Khóa học được tạo hoặc chỉnh sửa thành công.
• Các thông tin liên quan đến khóa học được lưu trong hệthống
và hiển thịcho sinh viên.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 196/254

=== SUMMARY ===
Đoạn văn bản trình bày các trường hợp ngoại lệ trong việc chỉnh sửa bài tập và chi tiết đặc tả Use-case UC-29 về quản lý khóa học. UC-29 cho phép giảng viên thực hiện các quyền quản trị như thay đổi thông tin, xây dựng cấu trúc, thiết lập thang điểm và cập nhật tài liệu. Mục tiêu của quy trình là đảm bảo thông tin khóa học được cập nhật chính xác trong hệ thống và hiển thị đầy đủ cho sinh viên.

=== REVIEW QUESTIONS ===
1. Trong trường hợp giảng viên nhập deadline là một ngày trong quá khứ, hệ thống sẽ xử lý như thế nào?
2. Điều kiện tiên quyết để giảng viên có thể thực hiện chức năng Quản lý khóa học (UC-29) là gì?
3. Hãy liệt kê 3 nhiệm vụ cụ thể mà giảng viên có thể thực hiện trong chức năng Quản lý khóa học.
4. Sau khi thực hiện thành công UC-29, thông tin khóa học sẽ được hiển thị cho đối tượng nào?','78e7648e-78b0-4693-8e6a-a8747d3e5b57'::uuid,NULL,NULL,215,478,'2026-03-21 13:35:13.361121+07'),
	 ('24316104-bbcd-48d7-ae70-b056fc7ec831'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,215,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Normal Flow
1. Giảng viên chọn chức năng "Quản lý khóa học"
2. Hệthống hiển thịdanh sách các khóa học mà giảng viên quản
lý
3. Giảng viên chọn:
• Tạo khóa học phụtrợ(UC-20)
• Chỉnh sửa khoá học (UC-21)
• Xem thông tin khóa học (UC-22)
• Xây dựng cấu trúc khóa học (UC-23)
• Thiết lập thang điểm (UC-24)
• Cập nhật tài liệu khóa học (UC-25)
4. Hệthống điều hướng giảng viên đến chức năng tương ứng
Alternative
Flow
Không có
Exceptions
Không có
A.3
Lược đồuse-case đối với admin
A.3.1
UC-24: Tạo và gửi thông báo
Đặc tảUC-24: Tạo và gửi thông báo được trình bày trong bảng A.23.
Bảng A.23: Đặc tảUC-24: Tạo và gửi thông báo
Use-case Code
UC-24
Use-case Name
Tạo và gửi thông báo
Description
Cho phép Admin soạn thảo và gửi các thông báo quan trọng đến
toàn bộngười dùng hoặc các nhóm đối tượng cụthể(sinh viên,
giảng viên) qua nhiều kênh khác nhau (thông báo trong ứng dụng,
email).
Actors
Admin
Trigger
Admin chọn chức năng "Thông báo"từmenu quản trị.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 197/254

=== SUMMARY ===
Đoạn văn bản mô tả quy trình nghiệp vụ (Normal Flow) cho chức năng quản lý khóa học của giảng viên và đặc tả use-case tạo/gửi thông báo của quản trị viên. Giảng viên có thể thực hiện các tác vụ như tạo, chỉnh sửa, xây dựng cấu trúc và cập nhật tài liệu khóa học. Quản trị viên có vai trò soạn thảo thông báo quan trọng để gửi đến các nhóm đối tượng (sinh viên, giảng viên) thông qua hệ thống hoặc email.

=== REVIEW QUESTIONS ===
1. Trong chức năng Quản lý khóa học (UC-29), giảng viên có thể lựa chọn những tác vụ cụ thể nào để thao tác?
2. Ai là tác nhân chính (Actor) thực hiện use-case UC-24 và điều gì kích hoạt (Trigger) chức năng này?
3. Theo mô tả của UC-24, quản trị viên có thể gửi thông báo qua những phương thức (kênh) nào?
4. Hệ thống sẽ thực hiện hành động gì sau khi giảng viên chọn một tác vụ cụ thể trong danh sách quản lý khóa học?','ace0ce8c-5ec9-4904-b36b-303f78049f66'::uuid,NULL,NULL,216,499,'2026-03-21 13:35:13.361121+07'),
	 ('b0cc1f75-b650-4b9a-a48a-dd7bf884bd1c'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,216,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Pre-Conditions
• Admin đã đăng nhập hệthống.
• Hệthống đã cấu hình sẵn các nhóm người dùng và kênh gửi
thông báo.
Post
Condi-
tions
• Thông báo được tạo và lưu vào hệthống.
• Thông báo được gửi thành công đến các đối tượng đã chọn
theo lịch trình.
Normal Flow
1. Admin truy cập trang "Quản lý thông báo"và chọn "Tạo thông
báo mới".
2. Hệthống hiển thịmột trình soạn thảo (composer).
3. Bước 1: Chọn đối tượng nhận: Admin chọn người nhận từ
các tùy chọn: "Tất cảngười dùng", "Tất cảsinh viên", "Tất cả
giảng viên", hoặc chọn theo khóa học/ngành học cụthể.
4. Bước 2: Soạn nội dung: Admin nhập "Tiêu đề"và "Nội
dung"thông báo. Trình soạn thảo hỗtrợđịnh dạng văn bản
(in đậm, in nghiêng, chèn link...).
5. Bước 3: Chọn kênh gửi: Admin tick vào các ô tương ứng:
"Thông báo trên hệthống", "Gửi qua Email".
6. Bước 4: Gửi: Admin chọn "Gửi ngay".
7. Hệthống yêu cầu xác nhận lần cuối trước khi gửi.
8. Sau khi xác nhận, hệthống xửlý và gửi thông báo, sau đó
hiển thị"Đã gửi thông báo thành công".
Alternative
Flow
6a. Lên lịch gửi: Thay vì "Gửi ngay", Admin chọn "Lên lịch"và
chọn một ngày giờcụthểtrong tương lai. Thông báo sẽđược lưu
lại và tựđộng gửi vào thời điểm đó.
2a. Sửdụng mẫu có sẵn: Admin có thểchọn một mẫu thông báo
có sẵn (ví dụ: "Thông báo bảo trì hệthống") đểtiết kiệm thời gian
soạn thảo.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 198/254

=== SUMMARY ===
Đoạn văn bản mô tả quy trình nghiệp vụ (Use-case) của quản trị viên (Admin) trong việc tạo và gửi thông báo. Tài liệu chi tiết các bước từ việc xác định đối tượng nhận, soạn thảo nội dung với trình biên tập hỗ trợ định dạng, đến lựa chọn kênh gửi (hệ thống hoặc email). Ngoài luồng thực hiện chuẩn, Admin còn có thể sử dụng các tính năng mở rộng như lên lịch gửi trong tương lai hoặc sử dụng các mẫu thông báo sẵn có để tối ưu hóa quy trình.

=== REVIEW QUESTIONS ===
1. Để thực hiện tạo thông báo mới, Admin cần thỏa mãn những điều kiện tiên quyết nào?
2. Admin có thể lựa chọn những đối tượng nhận thông báo cụ thể nào theo quy trình mô tả?
3. Trình soạn thảo nội dung thông báo hỗ trợ những tính năng định dạng văn bản nào?
4. Sự khác biệt chính giữa luồng gửi thông báo ngay lập tức và luồng thay thế ''Lên lịch gửi'' là gì?','db8f47c6-33dd-4719-9200-3c18a9865786'::uuid,NULL,NULL,217,580,'2026-03-21 13:35:13.361121+07'),
	 ('0f0e28e0-73e5-44d9-a2a6-103f502f3388'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,217,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Exceptions
• 3e. Chưa chọn đối tượng nhận: Nếu Admin nhấn gửi mà chưa
chọn đối tượng, hệthống sẽbáo lỗi "Vui lòng chọn ít nhất
một đối tượng nhận thông báo."
• 8e. Lỗi dịch vụEmail: Nếu kênh email được chọn nhưng dịch
vụgửi email gặp sựcố, thông báo trên hệthống vẫn được gửi
đi, đồng thời hệthống hiển thịcảnh báo "Gửi thông báo trên
hệthống thành công. Gửi qua email thất bại."
A.3.2
UC-38: Báo cáo lượt học, tần suất đăng nhập
Đặc tảUC-38: Báo cáo lượt học, tần suất đăng nhập được trình bày trong bảng A.24.
Bảng A.24: Đặc tảUC-38: Báo cáo lượt học, tần suất đăng nhập
Use-case Code
UC-38
Use-case Name
Báo cáo lượt học, tần suất đăng nhập
Description
Là một báo cáo chuyên sâu trong UC-36, tập trung vào việc đo
lường mức độtương tác và sựgắn kết của người dùng với hệthống.
Báo cáo này giúp Admin hiểu được hành vi người dùng, xác định
các thời điểm hoạt động cao điểm và đánh giá mức độsửdụng của
hệthống.
Actors
Admin
Trigger
Từdashboard báo cáo (UC-36), Admin chọn báo cáo "Hoạt động
người dùng".
Pre-Conditions
• Admin đã đăng nhập.
• Hệthống có ghi nhận nhật ký truy cập và hoạt động của người
dùng.
Post
Condi-
tions
• Báo cáo vềhoạt động người dùng được hiển thịtrực quan.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 199/254

=== SUMMARY ===
Đoạn văn bản này chi tiết hóa các trường hợp ngoại lệ của chức năng gửi thông báo và đặc tả Use-case UC-38 về báo cáo tương tác người dùng. UC-38 cho phép Admin theo dõi tần suất đăng nhập và lượt học để đánh giá mức độ gắn kết của người dùng với hệ thống. Thông qua việc phân tích nhật ký truy cập, Admin có thể xác định các thời điểm hoạt động cao điểm và hiểu rõ hơn về hành vi người dùng trên nền tảng.

=== REVIEW QUESTIONS ===
1. Trong trường hợp Admin chưa chọn đối tượng nhận khi gửi thông báo, hệ thống sẽ hiển thị thông báo lỗi cụ thể như thế nào?
2. Hệ thống xử lý ra sao nếu xảy ra lỗi dịch vụ email trong khi thông báo trên hệ thống vẫn được gửi đi thành công?
3. Mục đích chính của báo cáo UC-38 đối với việc quản trị hệ thống là gì?
4. Để xem được báo cáo hoạt động người dùng trong UC-38, hệ thống cần đáp ứng những điều kiện tiên quyết nào về dữ liệu?','e97636b2-cc85-42f6-910e-202ba8345ce8'::uuid,NULL,NULL,218,562,'2026-03-21 13:35:13.361121+07'),
	 ('b5be80f9-9644-4703-96af-2305bd4be031'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,218,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Normal Flow
1. Admin vào mục "Báo cáo"và chọn "Báo cáo hoạt động người
dùng".
2. Hệthống hiển thịdashboard báo cáo với các bộlọc: Khoảng
thời gian, Vai trò (Sinh viên/Giảng viên), Khóa học.
3. Báo cáo mặc định hiển thịcác chỉsốtổng quan:
• Sốngười dùng hoạt động (Active Users): Sốlượng người
dùng duy nhất đăng nhập trong khoảng thời gian đã chọn.
• Biểu đồxu hướng đăng nhập: Biểu đồđường thểhiện số
lượt đăng nhập mỗi ngày.
• Thời gian trung bình trên hệthống (Average Session Du-
ration).
4. Admin lọc theo vai trò "Sinh viên"và chọn một khóa học cụ
thể.
5. Báo cáo cập nhật và hiển thị:
• Danh sách sinh viên trong khóa học.
• Với mỗi sinh viên: Lần đăng nhập cuối, Tổng sốlượt truy
cập, Tổng thời gian học.
Alternative
Flow
3a. So sánh các nhóm: Admin có thểchọn so sánh hoạt động giữa
hai khoảng thời gian khác nhau (ví dụ: tháng này so với tháng
trước) đểxem xu hướng tăng trưởng.
5a. Phát hiện người dùng không hoạt động: Admin có thểsắp xếp
danh sách theo "Lần đăng nhập cuối"đểxác định các tài khoản
không hoạt động trong một thời gian dài.
Exceptions
• 3e. Dữliệu không đầy đủ: Nếu hệthống chỉbắt đầu ghi nhận
log từmột thời điểm nhất định, báo cáo sẽhiển thịmột lưu ý
vềphạm vi dữliệu có sẵn khi Admin chọn khoảng thời gian
quá xa vềtrước.
A.3.3
UC-39: Quản lý người dùng
Đặc tảUC-39: Quản lý người dùng được trình bày trong bảng A.25.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 200/254

=== SUMMARY ===
Đoạn văn bản mô tả chi tiết quy trình thực hiện báo cáo hoạt động người dùng (UC-38) trong hệ thống quản lý học tập. Nội dung bao gồm các bước từ việc lọc dữ liệu theo thời gian và vai trò, đến việc hiển thị các chỉ số quan trọng như số người dùng hoạt động, biểu đồ xu hướng và thời gian học tập trung bình. Ngoài ra, tài liệu còn hướng dẫn cách so sánh dữ liệu giữa các giai đoạn và phát hiện người dùng không hoạt động để hỗ trợ quản trị viên.

=== REVIEW QUESTIONS ===
1. Admin có thể sử dụng những tiêu chí bộ lọc nào để tùy chỉnh hiển thị dashboard báo cáo?
2. Các chỉ số tổng quan mặc định mà hệ thống cung cấp trong báo cáo hoạt động người dùng là gì?
3. Làm thế nào để Admin có thể phát hiện các tài khoản sinh viên không hoạt động trong một thời gian dài?
4. Hệ thống sẽ phản hồi như thế nào trong trường hợp Admin chọn khoảng thời gian báo cáo mà dữ liệu log chưa được ghi nhận đầy đủ?','31580d24-3b76-44ff-a037-a533c9e6e994'::uuid,NULL,NULL,219,610,'2026-03-21 13:35:13.362647+07');
INSERT INTO coaching_chatbot.lecture_knowledge_chunks (lecture_knowledge_chunks_id,lecture_knowledge_id,chunk_index,chunk_content,qdrant_point_id,start_time_seconds,end_time_seconds,page_number,token_count,created_at) VALUES
	 ('17735dc3-cfce-4c59-b573-79449b9c90c9'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,219,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Bảng A.25: Đặc tảUC-39: Quản lý người dùng
Use-case Code
UC-39
Use-case Name
Quản lý người dùng
Description
Admin thực hiện các thao tác quản lý vòng đời tài khoản của tất
cảngười dùng (sinh viên, giảng viên, admin khác). Bao gồm tạo
mới, chỉnh sửa thông tin, cấp/thu hồi quyền, khóa/mởkhóa, và xóa
tài khoản.
Actors
Admin
Trigger
Admin chọn chức năng "Quản lý người dùng"từgiao diện quản trị.
Pre-Conditions
• Admin đã đăng nhập hệthống.
• Danh sách các vai trò (roles) người dùng đã được định nghĩa
sẵn trong hệthống.
Post
Condi-
tions
• Thông tin tài khoản người dùng được tạo mới hoặc cập nhật
thành công trong cơ sởdữliệu.
• Người dùng bịảnh hưởng có thể/không thểđăng nhập hoặc
có quyền truy cập khác đi tùy theo hành động của Admin.
Normal Flow
Tạo người dùng mới:
1. Admin truy cập trang "Quản lý người dùng"và nhấn nút
"Thêm mới". (extend UC-40)
2. Hệthống hiển thịform nhập thông tin: Họtên, Email, Tên
đăng nhập, Mật khẩu (tạo tựđộng hoặc nhập tay), Vai trò
(chọn từdanh sách).
3. Admin điền đầy đủthông tin và nhấn "Lưu".
4. Hệthống kiểm tra tính hợp lệ(ví dụ: email/tên đăng nhập
không được trùng) và tạo tài khoản mới.
5. Hệthống hiển thịthông báo "Tạo người dùng thành công".
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 201/254

=== SUMMARY ===
Đoạn văn bản trình bày chi tiết đặc tả Use-case UC-39 về quản lý người dùng. Admin đóng vai trò chính trong việc quản lý vòng đời tài khoản của sinh viên, giảng viên và các quản trị viên khác. Các hoạt động bao gồm tạo mới, chỉnh sửa, cấp quyền, khóa và xóa tài khoản. Quy trình chuẩn cho việc tạo tài khoản mới yêu cầu nhập liệu chính xác và hệ thống phải xác thực tính duy nhất của email hoặc tên đăng nhập trước khi lưu trữ thành công.

=== REVIEW QUESTIONS ===
1. Actor nào có quyền thực hiện các thao tác quản lý vòng đời tài khoản trong UC-39?
2. Điều kiện tiên quyết nào cần được đáp ứng trước khi thực hiện quản lý người dùng?
3. Trong luồng sự kiện chuẩn, hệ thống thực hiện kiểm tra tính hợp lệ của những thông tin nào khi tạo người dùng mới?
4. Kết quả sau khi thực hiện thành công (Post-conditions) của Use-case này là gì đối với người dùng và hệ thống?','9aeff056-d829-4996-b834-fdc85dd5625f'::uuid,NULL,NULL,220,563,'2026-03-21 13:35:13.362647+07'),
	 ('0167ea31-09dd-4022-a436-28113eedb96e'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,220,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Chỉnh sửa người dùng:
1. Admin tìm và chọn một người dùng từdanh sách.
2. Admin nhấn nút "Chỉnh sửa".
3. Admin có thểthay đổi thông tin cá nhân, đặt lại mật khẩu,
hoặc thay đổi vai trò. (extend UC-42)
4. Admin nhấn "Lưu"đểcập nhật.
Alternative
Flow
1a. Nhập/Xuất hàng loạt:
- Admin chọn chức năng "Import người dùng"đểtải lên một file
(CSV/Excel) chứa danh sách người dùng mới. (extend UC-41)
- Admin có thểchọn "Export người dùng"đểtải vềdanh sách toàn
bộngười dùng hiện tại.
3a. Khóa/Mởkhóa tài khoản: Thay vì chỉnh sửa, Admin có thể
chọn hành động "Khóa tài khoản"đểtạm thời vô hiệu hóa quyền
đăng nhập của người dùng mà không xóa dữliệu. Hành động "Mở
khóa"sẽkích hoạt lại tài khoản.
Exceptions
• 4e. Dữliệu không hợp lệ: Nếu email đã tồn tại hoặc tên đăng
nhập đã được sửdụng, hệthống sẽbáo lỗi "Email/Tên đăng
nhập đã tồn tại."và không cho phép tạo.
• 1e. File import sai định dạng: Khi import, nếu file không
đúng mẫu hoặc chứa dữliệu lỗi, hệthống sẽbáo lỗi và chỉ
ra dòng/cột bịsai đểAdmin sửa lại.
A.3.4
UC-40: Tạo/sửa/xóa tài khoản
Đặc tảUC-40: Tạo/sửa/xóa tài khoản được trình bày trong bảng A.26.
Bảng A.26: Đặc tảUC-40: Tạo/sửa/xóa tài khoản
Use-case Code
UC-40
Use-case Name
Tạo/sửa/xóa tài khoản
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 202/254

=== SUMMARY ===
Đoạn văn bản hướng dẫn quy trình chỉnh sửa tài khoản người dùng (UC-40) trong hệ thống quản trị. Admin có quyền cập nhật thông tin cá nhân, vai trò, hoặc đặt lại mật khẩu. Nội dung còn bao gồm các tính năng nâng cao như nhập/xuất dữ liệu hàng loạt qua file CSV/Excel và quản lý trạng thái tài khoản thông qua việc khóa/mở khóa. Các thông báo lỗi về dữ liệu trùng lặp hoặc file sai định dạng cũng được quy định cụ thể để đảm bảo an toàn hệ thống.

=== REVIEW QUESTIONS ===
1. Admin có thể thực hiện những thay đổi cụ thể nào khi chọn chức năng chỉnh sửa một người dùng?
2. Tính năng ''Khóa tài khoản'' khác với việc chỉnh sửa thông tin thông thường ở điểm nào?
3. Trong quy trình nhập dữ liệu hàng loạt, định dạng file nào được hỗ trợ và hệ thống sẽ xử lý lỗi định dạng như thế nào?
4. Hệ thống sẽ hiển thị thông báo gì nếu Admin cố gắng cập nhật email hoặc tên đăng nhập đã tồn tại?','6fc4a863-90a0-41b5-bfac-793803a3e8ca'::uuid,NULL,NULL,221,571,'2026-03-21 13:35:13.362647+07'),
	 ('20f3fed4-09d1-41d7-944b-143aa4381763'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,221,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Description
Mô tảchi tiết các thao tác cơ bản (CRUD) trên một tài khoản người
dùng đơn lẻ, được thực hiện thủcông bởi Admin. Đây là một phần
cốt lõi của UC-39.
Actors
Admin
Trigger
Admin thực hiện các hành động "Thêm mới", "Chỉnh sửa",
"Xóa"trong trang "Quản lý người dùng".
Pre-Conditions
• Admin đang ởtrong trang "Quản lý người dùng"(UC-39).
Post
Condi-
tions
• Một tài khoản mới được tạo, một tài khoản hiện có được cập
nhật, hoặc một tài khoản bịxóa/vô hiệu hóa.
Normal Flow
Tạo tài khoản:
1. Admin nhấn "Thêm mới".
2. Admin điền các trường bắt buộc: Tên đăng nhập, Email, Họ
tên, Vai trò. Mật khẩu có thểđược tạo tựđộng và gửi qua
email.
3. Admin nhấn "Lưu". Hệthống xác thực dữliệu.
4. Tài khoản được tạo. Hệthống gửi email chào mừng (nếu được
cấu hình).
Sửa tài khoản:
1. Admin tìm và chọn một tài khoản, sau đó nhấn "Chỉnh sửa".
2. Admin thay đổi thông tin cần thiết (ví dụ: cập nhật họtên,
gán lại vai trò).
3. Admin nhấn "Lưu".
Xóa tài khoản:
1. Admin tìm và chọn một tài khoản, sau đó nhấn "Xóa".
2. Hệthống hiển thịhộp thoại xác nhận, cảnh báo vềhậu quả
(ví dụ: "Hành động này không thểhoàn tác và sẽxóa toàn bộ
dữliệu liên quan của người dùng.").
3. Admin xác nhận. Tài khoản bịxóa khỏi CSDL.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 203/254

=== SUMMARY ===
Đoạn văn bản mô tả quy trình thực hiện các thao tác CRUD (Thêm, Sửa, Xóa) đối với tài khoản người dùng đơn lẻ do Admin quản lý. Nội dung chi tiết hóa các bước từ việc nhập thông tin bắt buộc, xác thực dữ liệu, đến các cơ chế an toàn như xác nhận trước khi xóa dữ liệu vĩnh viễn. Đây là đặc tả cốt lõi giúp Admin duy trì và cập nhật danh sách người dùng trong hệ thống một cách chính xác.

=== REVIEW QUESTIONS ===
1. Những thông tin nào là bắt buộc phải điền khi Admin tạo mới một tài khoản người dùng?
2. Hệ thống thực hiện bước nào để đảm bảo Admin không vô tình xóa nhầm dữ liệu người dùng?
3. Sau khi tài khoản được tạo thành công, hệ thống có thể thực hiện hành động tự động nào để thông báo cho người dùng?
4. Admin cần phải ở giao diện nào trước khi có thể thực hiện các thao tác thêm, sửa hoặc xóa tài khoản?','8fb0081c-9d47-4e6b-bb01-1a3d1fc5a336'::uuid,NULL,NULL,222,554,'2026-03-21 13:35:13.362647+07'),
	 ('9be08b46-0a01-4b51-a27b-ac8fd98ed5c6'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,222,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Alternative
Flow
2a. Đặt lại mật khẩu: Trong giao diện chỉnh sửa, Admin có thể
nhấn nút "Đặt lại mật khẩu". Hệthống sẽtạo một mật khẩu tạm
thời và gửi cho người dùng qua email.
1b. Vô hiệu hóa/Khóa tài khoản: Thay vì "Xóa", Admin có thể
chọn "Khóa". Tài khoản sẽkhông thểđăng nhập nhưng dữliệu
vẫn được giữlại.
Exceptions
• 3e. Tên đăng nhập/Email đã tồn tại: Khi tạo mới, nếu tên đăng
nhập hoặc email đã được sửdụng, hệthống sẽbáo lỗi và yêu
cầu Admin nhập lại.
• 2e. Không thểxóa Admin cấp cao: Hệthống sẽngăn chặn
việc Admin tựxóa tài khoản của chính mình hoặc xóa tài
khoản Super Admin cuối cùng của hệthống.
A.3.5
UC-41: Import/export dữliệu người dùng
Đặc tảUC-41: Import/export dữliệu người dùng được trình bày trong bảng A.27.
Bảng A.27: Đặc tảUC-41: Import/export dữliệu người dùng
Use-case Code
UC-41
Use-case Name
Import/export dữliệu người dùng
Description
Cung cấp công cụđểAdmin quản lý dữliệu người dùng hàng loạt,
giúp tiết kiệm thời gian khi cần thêm sốlượng lớn người dùng mới
(ví dụ: đầu năm học) hoặc khi cần sao lưu, di chuyển dữliệu.
Actors
Admin
Trigger
Admin chọn các chức năng "Import"hoặc "Export"trong trang
"Quản lý người dùng".
Pre-Conditions
• Admin đang ởtrong trang "Quản lý người dùng"(UC-39).
Post
Condi-
tions
• Dữliệu người dùng được thêm mới/cập nhật hàng loạt vào hệ
thống, hoặc một file chứa dữliệu người dùng được tải vềmáy
của Admin.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 204/254

=== SUMMARY ===
Phân đoạn này trình bày chi tiết các luồng thay thế và ngoại lệ trong quản lý tài khoản, cùng với đặc tả Use Case UC-41 về Import/Export dữ liệu. Nội dung tập trung vào các tính năng như đặt lại mật khẩu, khóa tài khoản thay vì xóa để bảo toàn dữ liệu, và các quy tắc bảo mật ngăn chặn xóa Admin cấp cao. UC-41 hỗ trợ Admin quản lý người dùng hàng loạt, giúp tối ưu hóa thời gian khi xử lý lượng lớn dữ liệu trong các dịp đặc biệt như đầu năm học.

=== REVIEW QUESTIONS ===
1. Sự khác biệt chính giữa hành động ''Khóa tài khoản'' và ''Xóa tài khoản'' trong hệ thống này là gì?
2. Hệ thống đưa ra những ràng buộc nào để bảo vệ tài khoản Admin cấp cao và Super Admin?
3. Mục đích và lợi ích chính của việc triển khai Use Case UC-41 (Import/Export dữ liệu) là gì?
4. Điều kiện tiên quyết (Pre-conditions) để thực hiện Use Case UC-41 là gì và kết quả sau khi thực hiện (Post-conditions) bao gồm những gì?','88979542-8c61-4905-9f93-c2618f729f89'::uuid,NULL,NULL,223,616,'2026-03-21 13:35:13.362647+07'),
	 ('56fcbb4b-05a9-4091-abb1-414b0389d8db'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,223,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Normal Flow
Import:
1. Admin chọn "Import người dùng".
2. Hệthống cung cấp một liên kết đểtải vềfile mẫu
(CSV/Excel) với các cột tiêu đềchuẩn.
3. Admin điền dữliệu người dùng vào file mẫu và lưu lại.
4. Admin quay lại hệthống, chọn "Tải lên file"và chọn file vừa
tạo.
5. Hệthống phân tích file, xác thực dữliệu và hiển thịmột bản
xem trước (ví dụ: "Sẽtạo mới 100 tài khoản, cập nhật 5 tài
khoản. 2 dòng bịlỗi.").
6. Admin xác nhận import.
7. Hệthống xửlý tác vụdưới nền và gửi email thông báo kết
quảcho Admin khi hoàn tất.
Export:
1. Admin có thểsửdụng bộlọc đểchọn nhóm người dùng cần
xuất (ví dụ: tất cảsinh viên K23).
2. Admin nhấn "Export người dùng".
3. Hệthống tạo một file CSV/Excel chứa thông tin của các người
dùng đã chọn và tựđộng tải về.
Alternative
Flow
5a. Tùy chọn import: Hệthống có thểcung cấp các tùy chọn như
"Bỏqua nếu người dùng đã tồn tại"hoặc "Cập nhật thông tin nếu
người dùng đã tồn tại".
Exceptions
• 4e. File sai định dạng/cấu trúc: Nếu file tải lên không đúng
mẫu, thiếu cột bắt buộc, hệthống sẽtừchối và thông báo lỗi
rõ ràng.
• 5e. Lỗi dữliệu trong file: Nếu một sốdòng trong file có dữ
liệu không hợp lệ(ví dụ: email sai định dạng), hệthống sẽbỏ
qua các dòng đó và xửlý phần còn lại, đồng thời cung cấp
một file log chi tiết vềcác lỗi đã gặp.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 205/254

=== SUMMARY ===
Đoạn văn bản này chi tiết hóa quy trình Import và Export dữ liệu người dùng (UC-41) dành cho quản trị viên. Nội dung bao gồm các bước chuẩn bị file mẫu, tải lên, xác thực dữ liệu và xử lý hàng loạt. Tài liệu cũng nêu rõ các luồng thay thế khi dữ liệu đã tồn tại và cách hệ thống xử lý các ngoại lệ như sai định dạng file hoặc lỗi dữ liệu từng dòng, đảm bảo tính toàn vẹn và hiệu quả khi quản lý số lượng lớn tài khoản.

=== REVIEW QUESTIONS ===
1. Trước khi tải file lên để Import, Admin cần thực hiện những bước chuẩn bị gì theo quy trình chuẩn?
2. Hệ thống sẽ hiển thị thông tin gì cho Admin sau khi phân tích file Import nhưng trước khi xác nhận thực hiện?
3. Trong trường hợp một số dòng trong file chứa dữ liệu không hợp lệ (như sai định dạng email), hệ thống sẽ xử lý như thế nào?
4. Admin có thể làm gì để chỉ xuất (Export) dữ liệu của một nhóm người dùng cụ thể thay vì toàn bộ hệ thống?','21dcbb35-ea60-491e-8582-b8e65584721b'::uuid,NULL,NULL,224,591,'2026-03-21 13:35:13.363674+07'),
	 ('d2279a8c-2ba3-4a72-a71e-c538f181d9ea'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,224,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
A.3.6
UC-42: Phân quyền
Đặc tảUC-42: Phân quyền được trình bày trong bảng A.28.
Bảng A.28: Đặc tảUC-42: Phân quyền
Use-case Code
UC-42
Use-case Name
Phân quyền
Description
Mô tảthao tác gán hoặc cập nhật duy nhất một vai trò (role) cho
người dùng nhằm điều chỉnh quyền truy cập. Không bao gồm việc
tạo, chỉnh sửa hay cấu hình chi tiết quyền hạn của các vai trò (nằm
ởUC-43).
Actors
Admin
Trigger
Admin chỉnh sửa một người dùng trong UC-39 và thay đổi trường
"Vai trò".
Pre-Conditions
• Admin đang chỉnh sửa thông tin của một người dùng.
• Hệthống đã định nghĩa sẵn các vai trò và bộquyền hạn tương
ứng.
Post
Condi-
tions
• Vai trò của người dùng được cập nhật.
• Quyền truy cập của người dùng thay đổi theo vai trò mới (có
thểcó hiệu lực ngay lập tức hoặc sau lần đăng nhập tiếp theo).
Normal Flow
1. Trong form chỉnh sửa thông tin người dùng (UC-40), Admin
tìm đến trường "Vai trò"(Role).
2. Đây là một danh sách thảxuống (dropdown) chứa tất cảcác
vai trò có sẵn (ví dụ: Sinh viên, Giảng viên, Quản trịviên).
3. Admin chọn một vai trò mới cho người dùng.
4. Admin nhấn "Lưu".
5. Hệthống cập nhật vai trò mới cho người dùng trong CSDL.
6. (Nếu được cấu hình) Hệthống làm mới phiên hoặc cache
quyền đểquyền mới có hiệu lực ngay; nếu không, quyền mới
có hiệu lực từlần đăng nhập tiếp theo.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 206/254

=== SUMMARY ===
Tài liệu này trình bày đặc tả Use-case UC-42 về việc phân quyền cho người dùng trong hệ thống LMS. Quy trình cho phép Admin gán hoặc cập nhật vai trò của người dùng thông qua danh sách thả xuống trong giao diện chỉnh sửa thông tin. Mục tiêu chính là điều chỉnh quyền truy cập dựa trên các vai trò đã được định nghĩa sẵn như Sinh viên, Giảng viên, hoặc Quản trị viên. Việc thay đổi quyền hạn có thể có hiệu lực ngay lập tức hoặc sau khi người dùng đăng nhập lại.

=== REVIEW QUESTIONS ===
1. Mục đích chính của Use-case UC-42 là gì và nó khác biệt thế nào với UC-43?
2. Ai là tác nhân chính thực hiện việc phân quyền và điều kiện tiên quyết để thực hiện là gì?
3. Quy trình chuẩn để cập nhật vai trò cho một người dùng bao gồm những bước nào?
4. Khi nào quyền truy cập mới của người dùng chính thức có hiệu lực sau khi Admin nhấn lưu?','f078fa8b-482c-4fb4-9624-9be89d8d4293'::uuid,NULL,NULL,225,579,'2026-03-21 13:35:13.363674+07'),
	 ('c271fddd-d109-442e-90cb-3b21e9b96fc6'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,227,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Hình B.1: Danh sách lớp học - Hiển thịdạng Card
Đặc điểm chính:
• Thanh điều hướng trái nhất quán, truy cập nhanh mục Lớp học của tôi.
• Tìm kiếm khoá học tức thì, kèm nút sắp xếp và lọc.
• Hiển thịrõ học kỳhiện hành (Semester 1, 2025-2026).
• Lưới card đa cột cung cấp tên môn, mã lớp, giảng viên, nút Vào học.
• Khu vực All giúp duyệt nhanh tất cảlớp hiện có.
B.1.2
Quiz và Bài Kiểm Tra
B.1.2.1
Quiz Trắc Nghiệm - Chọn Đáp Án
Giao diện làm bài quiz trắc nghiệm với các tùy chọn đáp án rõ ràng.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 209/254

=== SUMMARY ===
Đoạn văn bản giới thiệu các thành phần giao diện người dùng trên hệ thống LMS của Trường Đại học Bách khoa TP.HCM dành cho sinh viên. Nội dung tập trung vào mô tả giao diện danh sách lớp học dưới dạng Card với các tính năng tìm kiếm, lọc và điều hướng tiện lợi. Đồng thời, tài liệu cũng giới thiệu sơ lược về giao diện làm bài kiểm tra trắc nghiệm (Quiz), giúp sinh viên dễ dàng tương tác và theo dõi tiến độ học tập trong học kỳ hiện hành.

=== REVIEW QUESTIONS ===
1. Giao diện danh sách lớp học dạng Card hiển thị những thông tin chi tiết nào cho sinh viên?
2. Thanh điều hướng bên trái của hệ thống LMS có chức năng chính là gì?
3. Làm thế nào sinh viên có thể nhanh chóng tìm thấy một khóa học cụ thể trong danh sách?
4. Phần B.1.2.1 mô tả đặc điểm gì của giao diện làm bài Quiz trắc nghiệm?','bef9aafb-458d-4118-8561-1416eb348856'::uuid,NULL,NULL,228,372,'2026-03-21 13:35:13.364485+07'),
	 ('f1672168-ddbe-48b4-9dc8-97a2a8fef9e3'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,228,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Hình B.2: Quiz trắc nghiệm - Giao diện chọn đáp án
Đặc điểm chính:
• Màn hình làm bài trắc nghiệm với câu hỏi trung tâm, đáp án lựa chọn và điều hướng
câu hỏi.
• Đồng hồđếm ngược và điều hướng câu hỏi theo danh sách giúp kiểm soát tiến độ.
• Hỗtrợđánh dấu/trạng thái câu và nút chuyển câu tiếp theo.
• Sidebar chương/bài hỗtrợtham chiếu nội dung khi cần.
B.1.2.2
Quiz Trắc Nghiệm - Xem Lại Bài Làm
Giao diện xem lại kết quảbài làm quiz trắc nghiệm sau khi hoàn thành.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 210/254

=== SUMMARY ===
Nội dung này mô tả chi tiết giao diện làm bài quiz trắc nghiệm và giao diện xem lại kết quả trên hệ thống LMS. Các tính năng chính bao gồm câu hỏi trung tâm, đồng hồ đếm ngược để quản lý thời gian, danh sách điều hướng câu hỏi, và tính năng đánh dấu trạng thái. Đặc biệt, hệ thống cung cấp sidebar hỗ trợ tham chiếu nội dung học tập ngay khi làm bài và cho phép sinh viên xem lại chi tiết bài làm sau khi hoàn thành để tự đánh giá.

=== REVIEW QUESTIONS ===
1. Giao diện làm bài quiz trắc nghiệm hỗ trợ những tính năng nào giúp sinh viên quản lý tiến độ làm bài?
2. Chức năng của đồng hồ đếm ngược và danh sách điều hướng trong giao diện làm bài là gì?
3. Sidebar trong giao diện làm bài quiz có vai trò gì đối với sinh viên?
4. Mục đích của giao diện ''Xem Lại Bài Làm'' sau khi hoàn thành quiz là gì?','fce0f947-afb4-4834-b3d2-855594639e20'::uuid,NULL,NULL,229,366,'2026-03-21 13:35:13.364485+07'),
	 ('da0c47ba-67d1-462d-a557-34081716d4eb'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,229,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Hình B.3: Quiz trắc nghiệm - Xem lại bài làm
Đặc điểm chính:
• Trang tổng hợp kết quảquiz: trạng thái hoàn thành và thời lượng thực hiện.
• Danh sách câu hỏi kèm đáp án đã chọn và liên kết xem lại, hỗtrợrà soát.
• Điều hướng theo chương/bài đểtruy cập nhanh các câu liên quan.
B.1.2.3
Quiz Trắc Nghiệm - Đã Hoàn Thành (1 Lần Làm)
Giao diện hiển thịkết quảsau khi hoàn thành quiz chỉđược làm 1 lần.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 211/254

=== SUMMARY ===
Phân đoạn này mô tả giao diện xem lại bài làm và trạng thái hoàn thành của quiz trắc nghiệm trong trường hợp chỉ được làm một lần. Các đặc điểm chính bao gồm trang tổng hợp kết quả với trạng thái và thời gian thực hiện, danh sách câu hỏi kèm đáp án đã chọn để rà soát, và hệ thống điều hướng theo chương/bài. Nội dung giúp người dùng hiểu cách hệ thống hiển thị kết quả cuối cùng và hỗ trợ ôn tập kiến thức sau khi kiểm tra.

=== REVIEW QUESTIONS ===
1. Trang tổng hợp kết quả quiz trắc nghiệm cung cấp những thông tin cụ thể nào về quá trình làm bài?
2. Làm thế nào người dùng có thể rà soát lại các câu hỏi và đáp án đã chọn sau khi hoàn thành bài thi?
3. Tính năng điều hướng theo chương/bài trong giao diện xem lại bài làm có vai trò gì?
4. Giao diện tại mục B.1.2.3 được thiết kế dành riêng cho loại hình bài tập quiz nào?','c263de47-7680-4c45-ad1d-756fbcd1c77c'::uuid,NULL,NULL,230,355,'2026-03-21 13:35:13.364485+07'),
	 ('2d772940-9a81-46c3-b724-b9d970aadefa'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,230,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Hình B.4: Quiz trắc nghiệm - Đã hoàn thành (1 lần làm)
Đặc điểm chính:
• Tóm tắt cấu hình quiz (cấu trúc bài, sốlần cho phép, ngưỡng đạt).
• Hiển thịtrạng thái hoàn thành và kết quảđạt được.
• CTA xem lại bài làm đểmởchi tiết từng câu.
B.1.2.4
Quiz Trắc Nghiệm - Nhiều Lần Làm
Giao diện làm bài quiz trắc nghiệm cho phép làm nhiều lần đểcải thiện điểm số.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 212/254

=== SUMMARY ===
Nội dung mô tả hai loại giao diện hệ thống quiz trắc nghiệm: giao diện sau khi hoàn thành bài làm (đối với loại chỉ cho phép thực hiện một lần) và giao diện hỗ trợ làm bài nhiều lần. Các đặc điểm chính bao gồm tóm tắt cấu hình quiz (cấu trúc, ngưỡng đạt), hiển thị kết quả, trạng thái hoàn thành và nút kêu gọi hành động (CTA) để xem lại chi tiết bài làm, giúp sinh viên theo dõi tiến độ và cải thiện điểm số.

=== REVIEW QUESTIONS ===
1. Giao diện Quiz trắc nghiệm khi hoàn thành một lần cung cấp những thông tin tóm tắt nào về cấu hình bài thi?
2. Mục đích của nút CTA ''xem lại bài làm'' trong giao diện kết quả là gì?
3. Giao diện ''Quiz Trắc Nghiệm - Nhiều Lần Làm'' khác biệt như thế nào so với loại làm một lần về mục tiêu sử dụng?
4. Trạng thái hoàn thành và kết quả đạt được giúp ích gì cho sinh viên sau khi nộp bài?','ab0ef939-8f87-480a-b39e-09e5bd6f1773'::uuid,NULL,NULL,231,343,'2026-03-21 13:35:13.364485+07');
INSERT INTO coaching_chatbot.lecture_knowledge_chunks (lecture_knowledge_chunks_id,lecture_knowledge_id,chunk_index,chunk_content,qdrant_point_id,start_time_seconds,end_time_seconds,page_number,token_count,created_at) VALUES
	 ('537276aa-97ae-4d47-b44e-cd3f327e73d5'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,231,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Hình B.5: Quiz trắc nghiệm - Cho phép làm nhiều lần
Đặc điểm chính:
• Quiz hỗtrợlàm lại, kèm yêu cầu đạt tối thiểu đểcải thiện điểm.
• Lịch sửbài làm hiển thịthời điểm, thời lượng, kết quảvà trạng thái từng lần.
• Hành động "Làm Lại"và "Xem Lại Bài Làm"cho mỗi lần nộp.
B.1.2.5
Quiz Code - Xem Lại Bài Làm
Giao diện xem lại bài làm quiz code sau khi hoàn thành.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 213/254

=== SUMMARY ===
Phân đoạn này mô tả chi tiết về hai loại giao diện kiểm tra: quiz trắc nghiệm cho phép làm nhiều lần và xem lại bài làm quiz code. Quiz trắc nghiệm hỗ trợ sinh viên cải thiện điểm số thông qua việc làm lại, đồng thời cung cấp lịch sử chi tiết về thời gian và kết quả của từng lượt nộp. Trong khi đó, giao diện quiz code cho phép người dùng rà soát lại các đoạn mã đã nộp sau khi hoàn thành bài thi.

=== REVIEW QUESTIONS ===
1. Tính năng làm lại bài quiz trắc nghiệm kèm theo yêu cầu gì để sinh viên có thể cải thiện điểm số?
2. Lịch sử làm bài của quiz trắc nghiệm cung cấp những thông số chi tiết nào cho mỗi lượt thực hiện?
3. Người dùng có thể thực hiện những hành động cụ thể nào đối với mỗi lần nộp bài trong hệ thống quiz?
4. Giao diện ''Quiz Code - Xem Lại Bài Làm'' xuất hiện vào thời điểm nào trong quá trình học tập của sinh viên?','475ea240-eebd-492a-8a03-b28af0736064'::uuid,NULL,NULL,232,349,'2026-03-21 13:35:13.364485+07'),
	 ('0b90e286-0602-4b8f-bbf7-d2990564fe5b'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,232,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Hình B.6: Quiz Code - Xem lại bài làm
Đặc điểm chính:
• Trang tổng hợp kết quảcode quiz với trạng thái hoàn thành.
• Danh sách câu kèm code đã nộp, điểm từng câu và liên kết xem lại.
• Điều hướng theo chương/bài đểrà soát nhanh các đoạn code liên quan.
B.1.3
Bài Tập Lớn
B.1.3.1
Bài Tập Lớn
Giao diện hiển thịdanh sách và chi tiết các bài tập lớn được giao cho sinh viên.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 214/254

=== SUMMARY ===
Nội dung này mô tả hai giao diện quan trọng trong hệ thống học tập: giao diện xem lại kết quả Quiz Code và giao diện quản lý Bài Tập Lớn. Sinh viên có thể kiểm tra lại mã nguồn đã nộp, điểm số chi tiết từng câu và trạng thái hoàn thành của mình. Đồng thời, phần Bài Tập Lớn cung cấp cái nhìn tổng quan về danh sách và chi tiết các nhiệm vụ dài hạn được giao, giúp sinh viên dễ dàng theo dõi và thực hiện các yêu cầu của học phần.

=== REVIEW QUESTIONS ===
1. Giao diện ''Xem lại bài làm'' của Quiz Code hiển thị những thông tin chi tiết nào cho mỗi câu hỏi?
2. Chức năng điều hướng theo chương/bài trong phần Quiz Code hỗ trợ sinh viên như thế nào trong việc rà soát?
3. Giao diện được mô tả trong mục B.1.3.1 có vai trò gì đối với sinh viên?
4. Dựa trên nội dung, sinh viên có thể xem lại mã nguồn (code) đã nộp ở đâu?','848e5c26-019e-41d1-a63d-3a246892ed35'::uuid,NULL,NULL,233,346,'2026-03-21 13:35:13.364485+07'),
	 ('9097cc33-5560-4c90-9632-6170c520129e'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,233,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Hình B.7: Giao diện Bài tập lớn
Đặc điểm chính:
• Tab Bài tập hiển thịfile giao (đề, hướng dẫn) và hạn nộp cụthể.
• Nhắc thời gian còn lại đểnộp bài.
• Khu vực upload hỗtrợkéo-thảhoặc chọn file.
• Sidebar chọn nhanh giữa các bài tập lớn.
B.1.3.2
Bài Tập Lớn - Nộp File
Giao diện nộp bài tập lớn dưới dạng file.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 215/254

=== SUMMARY ===
Đoạn văn bản mô tả các tính năng chính của giao diện Bài tập lớn và quy trình nộp file. Hệ thống cung cấp đầy đủ thông tin về đề bài, hướng dẫn, thời hạn nộp và đồng hồ nhắc nhở thời gian còn lại. Người dùng có thể nộp bài linh hoạt thông qua tính năng kéo-thả hoặc chọn file trực tiếp, kết hợp với thanh sidebar giúp chuyển đổi nhanh chóng giữa các bài tập khác nhau.

=== REVIEW QUESTIONS ===
1. Tab Bài tập trong giao diện cung cấp cho sinh viên những tài liệu và thông tin cụ thể nào?
2. Hệ thống hỗ trợ sinh viên quản lý thời gian nộp bài bằng cách nào?
3. Nêu các phương thức upload file được hỗ trợ trong giao diện nộp bài tập lớn.
4. Chức năng của Sidebar trong giao diện Bài tập lớn là gì?','c90e17ac-9af7-4e6f-81ea-e781ffb5ab98'::uuid,NULL,NULL,234,301,'2026-03-21 13:35:13.366509+07'),
	 ('a2f4cb68-8d3d-4bd0-ab3e-d0b8eb56f593'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,234,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Hình B.8: Bài tập lớn - Giao diện nộp file
Đặc điểm chính:
• Quản lý file đã upload (thumbnail, menu Xóa/Thay đổi).
• Giữlink tải đềvà hướng dẫn kèm hạn nộp.
• Sidebar chuyển bài, kèm nhắc thời gian còn lại.
B.1.3.3
Bài Tập Lớn Dạng Quiz
Giao diện làm bài tập lớn dưới dạng quiz với nhiều câu hỏi.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 216/254

=== SUMMARY ===
Nội dung này mô tả các tính năng chính của giao diện nộp bài tập lớn và giao diện bài tập lớn dạng quiz. Sinh viên có thể quản lý tệp tin đã tải lên, xem hướng dẫn, theo dõi thời hạn qua thanh sidebar và thực hiện các bài kiểm tra dạng quiz nhiều câu hỏi. Hệ thống tập trung vào việc cung cấp các công cụ trực quan để quản lý tiến độ và nội dung học tập hiệu quả.

=== REVIEW QUESTIONS ===
1. Giao diện nộp file cung cấp những tính năng gì để sinh viên quản lý các tệp tin đã tải lên?
2. Ngoài việc nộp bài, sinh viên có thể tìm thấy những tài liệu hỗ trợ nào khác trên giao diện này?
3. Chức năng của thanh bên (sidebar) trong giao diện nộp bài tập lớn là gì?
4. Sự khác biệt chính giữa bài tập lớn thông thường và bài tập lớn dạng quiz theo mô tả là gì?','3200deca-6cea-4b91-829f-9721d26d309e'::uuid,NULL,NULL,235,312,'2026-03-21 13:35:13.366509+07'),
	 ('15448240-dab3-4086-af36-1e3249e64c05'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,235,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Hình B.9: Bài tập lớn dạng Quiz
Đặc điểm chính:
• Bài tập lớn dạng quiz với sốcâu, thời gian, sốlần làm và yêu cầu điểm qua được
nêu rõ.
• Thông tin mở/đóng và trạng thái giúp lên kếhoạch nộp.
• Nút "Làm Bài Ngay"đểbắt đầu thực hiện.
B.1.4
Điểm Sốvà Tiến Độ
B.1.4.1
Bảng Điểm
Giao diện hiển thịbảng điểm tổng hợp của sinh viên cho các môn học.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 217/254

=== SUMMARY ===
Nội dung này giới thiệu về hai thành phần quan trọng trong hệ thống quản lý học tập: giao diện Bài tập lớn dạng Quiz và Bảng điểm. Giao diện Quiz cung cấp đầy đủ thông tin về số câu hỏi, thời gian, số lần làm và điều kiện đạt, giúp sinh viên chủ động thực hiện bài làm. Phần Bảng điểm hỗ trợ sinh viên theo dõi tổng quát kết quả học tập và tiến độ của các môn học để quản lý lộ trình học tập hiệu quả.

=== REVIEW QUESTIONS ===
1. Những thông tin chi tiết nào được hiển thị trong giao diện bài tập lớn dạng Quiz để hỗ trợ sinh viên chuẩn bị?
2. Thông tin về thời gian mở/đóng và trạng thái của bài tập giúp ích gì cho sinh viên trong quá trình học tập?
3. Nút ''Làm Bài Ngay'' có vai trò gì trong giao diện bài tập lớn dạng Quiz?
4. Mục đích chính của giao diện ''Bảng Điểm'' trong phân hệ Điểm số và Tiến độ là gì?','a6225c1d-e04e-4a61-9102-291f75c9fc06'::uuid,NULL,NULL,236,337,'2026-03-21 13:35:13.366509+07'),
	 ('9296b316-2004-4786-b676-a94dae6bf654'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,236,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Hình B.10: Bảng điểm của sinh viên
Đặc điểm chính:
• Bảng điểm học kỳhiển thịtỉtrọng, điểm, phần trăm cho từng hạng mục.
• Nút "Phân tích điểm"đểxem chi tiết từng thành phần.
• Thông điệp động khích lệkhi hoàn thành mục tiêu.
B.1.5
Diễn Đàn
B.1.5.1
Diễn Đàn - Popup Theo Dõi
Popup cho phép sinh viên theo dõi các chủđềtrong diễn đàn đểnhận thông báo khi
có cập nhật.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 218/254

=== SUMMARY ===
Đoạn văn bản mô tả hai thành phần giao diện chính trong hệ thống quản lý học tập: Bảng điểm và Diễn đàn. Giao diện Bảng điểm giúp sinh viên theo dõi tỉ trọng, điểm số chi tiết và nhận thông điệp khích lệ. Giao diện Diễn đàn cung cấp tính năng Popup theo dõi, cho phép người dùng đăng ký nhận thông báo về các chủ đề thảo luận quan tâm để không bỏ lỡ các cập nhật mới.

=== REVIEW QUESTIONS ===
1. Bảng điểm học kỳ cung cấp những thông số chi tiết nào cho từng hạng mục điểm của sinh viên?
2. Chức năng ''Phân tích điểm'' hỗ trợ sinh viên như thế nào trong việc quản lý kết quả học tập?
3. Hệ thống sử dụng phương thức nào để tạo động lực cho sinh viên khi họ hoàn thành mục tiêu?
4. Tính năng Popup trong phần Diễn đàn có tác dụng gì đối với việc theo dõi các chủ đề thảo luận?','81dc2ef0-e850-4711-859f-fbd89925e960'::uuid,NULL,NULL,237,334,'2026-03-21 13:35:13.366509+07'),
	 ('75d2a316-0af8-46a6-befb-3c615bdb427a'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,237,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Hình B.11: Diễn đàn - Popup theo dõi chủđề
Đặc điểm chính:
• Popup theo dõi/bỏtheo dõi chủđề, bật/tắt thông báo cập nhật.
• Quản lý danh sách chủđềquan tâm nhanh chóng, không rời màn hình hiện tại.
B.1.5.2
Diễn Đàn - Topic CụThể
Giao diện hiển thịchi tiết một chủđềcụthểtrong diễn đàn với các bài viết và bình
luận.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 219/254

=== SUMMARY ===
Đoạn văn bản này giới thiệu về hai thành phần quan trọng trong phân hệ Diễn đàn của hệ thống quản lý học tập: Popup theo dõi chủ đề và giao diện Topic cụ thể. Tính năng popup cho phép sinh viên đăng ký hoặc hủy nhận thông báo về các cập nhật mới một cách nhanh chóng mà không cần chuyển trang. Giao diện Topic cụ thể đóng vai trò là nơi hiển thị nội dung chi tiết của các bài đăng cùng với các phản hồi, bình luận, hỗ trợ việc thảo luận và học tập cộng đồng.

=== REVIEW QUESTIONS ===
1. Tính năng chính của ''Popup theo dõi chủ đề'' là gì?
2. Ưu điểm của việc quản lý danh sách chủ đề qua popup thay vì chuyển sang trang mới là gì?
3. Giao diện ''Topic cụ thể'' (B.1.5.2) cung cấp cho người dùng những thông tin chi tiết nào?
4. Làm thế nào để sinh viên có thể bật hoặc tắt thông báo cập nhật cho một chủ đề thảo luận?','bc6a90b8-b02f-4c26-947f-ed695d2eabb8'::uuid,NULL,NULL,238,331,'2026-03-21 13:35:13.367509+07'),
	 ('7be95570-e28f-403b-8bc4-88b7ffab90a0'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,238,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Hình B.12: Diễn đàn - Chi tiết chủđề
Đặc điểm chính:
• Danh sách thread và bộlọc New/Top/Hot đểđiều hướng nhanh chủđề.
• Mỗi bài viết hiển thịtác giả, thời gian, tag, sốlượt xem/bình luận/like; hỗtrợtheo
dõi/bỏtheo dõi.
• Trang chi tiết hỗtrợcode block, tag, và ô bình luận đểthảo luận sâu.
B.1.6
Thông Tin Cá Nhân
B.1.6.1
Thông Tin Cá Nhân
Giao diện quản lý thông tin cá nhân của sinh viên.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 220/254

=== SUMMARY ===
Nội dung này mô tả chi tiết hai tính năng quan trọng trong hệ thống: giao diện chi tiết chủ đề trên Diễn đàn và giao diện Thông tin cá nhân của sinh viên. Diễn đàn hỗ trợ các bộ lọc nội dung (Mới/Top/Hot), cung cấp thông tin tương tác chi tiết và hỗ trợ định dạng code block cho thảo luận. Phần thông tin cá nhân cho phép sinh viên quản lý và theo dõi các thông tin định danh cá nhân trong môi trường học thuật.

=== REVIEW QUESTIONS ===
1. Hệ thống cung cấp những bộ lọc nào để giúp người dùng điều hướng nhanh các chủ đề trong diễn đàn?
2. Những thông tin chi tiết nào được hiển thị cho mỗi bài viết trên diễn đàn để người dùng tiện theo dõi?
3. Trang chi tiết chủ đề hỗ trợ các tính năng kỹ thuật nào để phục vụ việc thảo luận chuyên sâu?
4. Mục đích chính của giao diện được mô tả trong phần B.1.6.1 là gì?','2d200baf-167f-45c2-9681-8350b30f7fa2'::uuid,NULL,NULL,239,349,'2026-03-21 13:35:13.367509+07'),
	 ('5b53808d-7f61-4ddc-bf56-c71797efc6c8'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,239,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Hình B.13: Thông tin cá nhân của sinh viên
Đặc điểm chính:
• Header hồsơ kèm nút Edit cho phép chuyển sang chếđộchỉnh sửa.
• Biểu mẫu thông tin cá nhân và liên lạc đầy đủ(MSSV, ngày sinh, CCCD, giới tính, điện thoại, email).
• Thông tin đào tạo: khoa, đơn vịquản lý, mã lớp, năm CTĐT, hệ/ ngành đào tạo, tình trạng sinh viên.
• Mặc định chỉđọc đểtránh thay đổi nhầm; cho phép bật chỉnh sửa khi cần.
B.1.7
Thời Khóa Biểu
B.1.7.1
Thời Khóa Biểu
Giao diện hiển thịthời khóa biểu học tập của sinh viên.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 221/254

=== SUMMARY ===
Đoạn văn bản mô tả chi tiết hai giao diện quan trọng dành cho sinh viên trong hệ thống quản lý học tập: giao diện Thông tin cá nhân và giao diện Thời khóa biểu. Giao diện thông tin cá nhân quản lý các dữ liệu về định danh, liên lạc và lộ trình đào tạo với cơ chế bảo vệ mặc định chỉ đọc để tránh sai sót. Giao diện thời khóa biểu hỗ trợ sinh viên theo dõi lịch trình học tập cụ thể của mình.

=== REVIEW QUESTIONS ===
1. Những thông tin đào tạo nào được hiển thị trong giao diện thông tin cá nhân của sinh viên?
2. Tại sao giao diện thông tin cá nhân lại được thiết lập mặc định ở chế độ chỉ đọc?
3. Người dùng cần thực hiện thao tác gì để có thể chỉnh sửa thông tin hồ sơ cá nhân?
4. Mục đích chính của giao diện được trình bày trong phần B.1.7.1 là gì?','18ca27a1-e07d-45eb-927b-493495b6505e'::uuid,NULL,NULL,240,362,'2026-03-21 13:35:13.367509+07'),
	 ('3d087d83-f838-4828-923f-86d10f85325c'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,240,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
(a) Thời khóa biểu - Phiên bản 1
(b) Thời khóa biểu - Phiên bản 2
Hình B.14: Thời khóa biểu học tập
Hình B.15: Thời khóa biểu - Phiên bản 3
Đặc điểm chính:
• Phiên bản 1: bảng thời khóa biểu học kỳvới tìm kiếm, chọn học kỳ, lọc; kèm lịch mini và sựkiện tuần.
• Phiên bản 2: lưới "Luyện tập cùng AI"với thẻđềôn (thời gian, sốcâu, mức độ, điểm, trạng thái) và nút Bắt đầu/Làm lại.
• Phiên bản 3: lịch tuần dạng calendar, popover sựkiện (phòng, giảng viên, trạng thái), lịch tháng và danh sách sựkiện tuần.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 222/254

=== SUMMARY ===
Nội dung trình bày ba phiên bản giao diện thời khóa biểu và luyện tập cho sinh viên. Phiên bản 1 tập trung vào quản lý lịch học kỳ với các bộ lọc; phiên bản 2 tích hợp hệ thống ''Luyện tập cùng AI'' với các thẻ đề ôn tập chi tiết; phiên bản 3 cung cấp chế độ xem lịch tuần trực quan dạng calendar, hỗ trợ hiển thị chi tiết sự kiện như địa điểm và giảng viên qua popover.

=== REVIEW QUESTIONS ===
1. Phiên bản 1 của thời khóa biểu cung cấp những công cụ nào để người dùng quản lý lịch học kỳ?
2. Tính năng ''Luyện tập cùng AI'' trong phiên bản 2 hiển thị những thông tin cụ thể nào trên mỗi thẻ đề ôn?
3. Trong phiên bản 3, làm thế nào để người dùng có thể xem được thông tin về phòng học và giảng viên của một sự kiện?
4. Điểm khác biệt chính giữa cách hiển thị của phiên bản 3 so với các phiên bản khác là gì?','6ff43dfc-1c72-4af5-9f6f-8c9e651bb2cd'::uuid,NULL,NULL,241,376,'2026-03-21 13:35:13.367509+07');
INSERT INTO coaching_chatbot.lecture_knowledge_chunks (lecture_knowledge_chunks_id,lecture_knowledge_id,chunk_index,chunk_content,qdrant_point_id,start_time_seconds,end_time_seconds,page_number,token_count,created_at) VALUES
	 ('f3d738a0-aeac-4793-bb26-972d08e7c424'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,241,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
B.2
Giao diện giáo viên
Phần phụlục này trình bày các giao diện người dùng đại trà và tiêu chuẩn của hệ
thống LMS HCMUT dành cho giáo viên. Các giao diện này bao gồm các chức năng cơ
bản và phổbiến trong các hệthống quản lý học tập hiện đại, được thiết kếđểđảm bảo
tính nhất quán và dễsửdụng.
B.2.1
Khám phá lớp học
B.2.1.1
Lớp học của tôi
Giao diện tổng quan lớp học cung cấp cho giáo viên khảnăng rà soát nhanh các lớp
đang quản lý.
Hình B.16: Lớp học của tôi
Đặc điểm chính:
• Danh sách lớp dưới dạng card đồng nhất hỗtrợxem nhanh tên môn, mã lớp, giảng
viên cùng các hành động ngữcảnh.
• Thanh tìm kiếm và bộlọc đặt nổi cho phép giới hạn lớp theo từkhóa, giúp cập nhật
lớp mới nhanh chóng.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 223/254

=== SUMMARY ===
Đoạn văn bản giới thiệu về giao diện dành cho giáo viên trong hệ thống LMS HCMUT, tập trung vào tính năng ''Lớp học của tôi''. Giao diện được thiết kế nhất quán và dễ sử dụng, cho phép giáo viên quản lý nhanh các lớp học thông qua danh sách dạng thẻ (card). Các thẻ này hiển thị thông tin chi tiết như tên môn, mã lớp và giảng viên. Ngoài ra, hệ thống tích hợp thanh tìm kiếm và bộ lọc giúp tối ưu hóa việc tra cứu và cập nhật lớp học.

=== REVIEW QUESTIONS ===
1. Mục tiêu thiết kế chính của giao diện người dùng dành cho giáo viên trên LMS HCMUT là gì?
2. Thông tin cụ thể nào được hiển thị trên mỗi thẻ (card) lớp học trong giao diện ''Lớp học của tôi''?
3. Làm thế nào giáo viên có thể giới hạn hoặc tìm kiếm nhanh các lớp học trong danh sách?
4. Giao diện ''Lớp học của tôi'' hỗ trợ giáo viên thực hiện những hành động rà soát nào?','8c2ba9d7-8aa3-4717-955f-5a3161bcc39b'::uuid,NULL,NULL,242,430,'2026-03-21 13:35:13.367509+07'),
	 ('185c3eff-d862-4c19-945c-d7b5882875f8'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,242,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
• Các hành động mởrộng (menu 3 chấm) tập trung các thao tác như xem trước, nhân
bản, sửa, xóa đểgiảm tải giao diện chính.
B.2.1.2
Danh sách lớp học - Dạng card
Hình B.17: Danh sách lớp học dạng card
Đặc điểm chính:
• Danh sách lớp được sắp xếp theo trình tự, hiển thịmã lớp và trạng thái lớp giúp xác
định nhanh lớp nào đang hoạt động.
• Cơ chếtruy cập nhanh tới chi tiết mỗi lớp thông qua nút chi tiết hoặc khu vực card
giúp giáo viên chuyển ngữcảnh dễdàng.
• Thanh điều hướng bên trái giữcác luồng chức năng thống nhất, tập trung vào các
mục Lớp học, Thời khóa biểu và Khóa tăng cường.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 224/254

=== SUMMARY ===
Đoạn văn bản mô tả chi tiết giao diện danh sách lớp học dạng thẻ (card) dành cho giáo viên trên hệ thống LMS. Các đặc điểm nổi bật bao gồm việc sử dụng menu 3 chấm để tinh gọn giao diện, hiển thị trực quan mã lớp và trạng thái hoạt động, cùng cơ chế truy cập nhanh giúp giáo viên dễ dàng chuyển đổi ngữ cảnh. Hệ thống cũng duy trì sự thống nhất qua thanh điều hướng tập trung vào các chức năng cốt lõi như lớp học và thời khóa biểu.

=== REVIEW QUESTIONS ===
1. Menu 3 chấm trong giao diện được thiết kế nhằm mục đích gì và bao gồm những thao tác nào?
2. Việc hiển thị mã lớp và trạng thái lớp trên danh sách dạng card mang lại lợi ích gì cho giáo viên?
3. Người dùng có thể truy cập vào chi tiết lớp học thông qua những cách thức nào?
4. Thanh điều hướng bên trái của hệ thống tập trung vào những mục chức năng chính nào?','3f4b78f4-b34e-495f-a273-78b9156b4bf3'::uuid,NULL,NULL,243,401,'2026-03-21 13:35:13.367509+07'),
	 ('4ab3d051-678b-4342-9759-8d6f706b60bd'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,243,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
B.2.1.3
Popup thông báo lớp học
Hình B.18: Thông báo lớp học
Đặc điểm chính:
• Popup thông báo hiển thịluồng cập nhật gần đây với các tab Tất cả/Chưa đọc/Đã
đọc đểưu tiên xửlý và đánh dấu lại trạng thái.
• Mỗi thông báo kèm tiêu đề, nội dung tóm tắt và thời gian giúp giáo viên đánh giá
nhanh mức độưu tiên phản hồi.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 225/254

=== SUMMARY ===
Nội dung này giới thiệu về giao diện popup thông báo lớp học dành cho giáo viên trên hệ thống LMS. Giao diện hỗ trợ quản lý các cập nhật mới thông qua hệ thống phân loại theo trạng thái (Tất cả, Chưa đọc, Đã đọc). Mỗi thông báo bao gồm các thông tin thiết yếu như tiêu đề, tóm tắt và thời gian, giúp người dùng nhanh chóng nhận diện và ưu tiên xử lý các phản hồi quan trọng.

=== REVIEW QUESTIONS ===
1. Popup thông báo lớp học cung cấp những tab nào để giáo viên ưu tiên xử lý thông tin?
2. Mỗi thông báo trong hệ thống bao gồm những thành phần chi tiết nào?
3. Mục đích chính của việc hiển thị thời gian trong mỗi thông báo là gì?
4. Làm thế nào giao diện này giúp giáo viên đánh giá nhanh mức độ ưu tiên của các phản hồi?','2d4f181e-ea7f-4a5e-963e-f13eea5c2dda'::uuid,NULL,NULL,244,309,'2026-03-21 13:35:13.367509+07'),
	 ('7ae0da4e-2fd1-41ca-9db1-9b6d7eab8aaa'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,244,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
B.2.1.4
Nội dung thông báo lớp học
Hình B.19: Nội dung thông báo chi tiết
Đặc điểm chính:
• Mỗi thông báo trình bày chi tiết tiêu đề, nội dung văn bản dài và CTA nhận xét hoặc
đánh dấu, hỗtrợtruyền tải thông tin sựkiện quan trọng.
• Nút "Đánh dấu đã đọc"đặt cốđịnh giúp đóng vòng lặp phản hồi cho toàn bộthông
báo.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 226/254

=== SUMMARY ===
Đoạn văn bản này mô tả chi tiết giao diện nội dung thông báo lớp học. Các đặc điểm chính bao gồm việc hiển thị đầy đủ tiêu đề, nội dung văn bản dài và các nút hành động (CTA) như nhận xét hoặc đánh dấu. Thiết kế tập trung vào việc hỗ trợ truyền tải thông tin quan trọng và cung cấp nút ''Đánh dấu đã đọc'' cố định để giúp người dùng hoàn tất quy trình tương tác với thông báo một cách hiệu quả.

=== REVIEW QUESTIONS ===
1. Những thành phần thông tin chi tiết nào được hiển thị trong mỗi thông báo lớp học?
2. Vai trò của các nút CTA (nhận xét hoặc đánh dấu) trong giao diện thông báo là gì?
3. Nút ''Đánh dấu đã đọc'' được thiết kế như thế nào để hỗ trợ đóng vòng lặp phản hồi?
4. Làm thế nào giao diện chi tiết này giúp giáo viên truyền tải thông tin về các sự kiện quan trọng?','2cc49c63-2a86-4e58-823a-908073657b6c'::uuid,NULL,NULL,245,321,'2026-03-21 13:35:13.367509+07'),
	 ('c284fa38-cfd5-46c7-9fe4-4624915de4bb'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,245,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
B.2.2
Chi tiết lớp học
B.2.2.1
Danh sách bài tập lớn - Card
Hình B.20: Danh sách bài tập lớn trên dạng card
Đặc điểm chính:
• Liệt kê bài tập lớn với biểu tượng, sốfile liên quan, hạn nộp và sốsinh viên tham
gia giúp theo dõi tiến độgiao việc.
• Các thao tác nhanh như mởrộng chi tiết, nhân bản hoặc xóa đặt sát card tăng trải
nghiệm quản lý khối lượng bài tập.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 227/254

=== SUMMARY ===
Nội dung này giới thiệu về giao diện danh sách bài tập lớn dưới dạng thẻ (card) trong hệ thống quản lý lớp học. Giao diện cung cấp các thông tin thiết yếu như biểu tượng, số tệp liên quan, hạn nộp và số sinh viên tham gia để theo dõi tiến độ. Đồng thời, việc tích hợp các nút thao tác nhanh (nhân bản, xóa, xem chi tiết) ngay trên thẻ giúp giảng viên quản lý khối lượng bài tập một cách thuận tiện và hiệu quả hơn.

=== REVIEW QUESTIONS ===
1. Giao diện danh sách bài tập lớn dạng thẻ (card) hiển thị những thông tin cụ thể nào để giảng viên theo dõi tiến độ?
2. Những thao tác quản lý nhanh nào được tích hợp trực tiếp trên mỗi thẻ bài tập?
3. Việc hiển thị số lượng file liên quan và số sinh viên tham gia trên card có ý nghĩa gì trong việc quản lý?
4. Thiết kế đặt các nút thao tác sát cạnh thẻ bài tập nhằm mục đích cải thiện yếu tố gì cho người dùng?','173bd3d8-94d1-4f54-93c4-94f5510e3c72'::uuid,NULL,NULL,246,353,'2026-03-21 13:35:13.368497+07'),
	 ('1e6cf5eb-99a6-42d3-97c4-6243a90b43d7'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,246,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
B.2.2.2
Danh sách bài tập lớn - Danh sách
Hình B.21: Danh sách bài tập lớn dạng list
Đặc điểm chính:
• Bảng liệt kê tên bài tập lớn, hạn, sốsinh viên hoàn thành và trạng thái (Đang diễn
ra/Hết hạn) giúp tổng hợp nhanh tiến độ.
• Bộlọc tựđộng và nút tạo mới giúp mởrộng nhanh danh sách bài tập lớn mà không
rời màn hình tổng quan.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 228/254

=== SUMMARY ===
Đoạn văn bản mô tả giao diện danh sách bài tập lớn dưới dạng bảng liệt kê trong hệ thống quản lý. Giao diện này cung cấp cái nhìn tổng quan về tiến độ thông qua các thông tin như tên bài tập, hạn nộp, số lượng sinh viên hoàn thành và trạng thái. Ngoài ra, tính năng bộ lọc tự động và nút tạo mới giúp giảng viên quản lý và mở rộng danh sách bài tập một cách thuận tiện ngay tại màn hình chính.

=== REVIEW QUESTIONS ===
1. Giao diện danh sách bài tập lớn dạng list hiển thị những thông tin chi tiết nào cho mỗi bài tập?
2. Làm thế nào người dùng có thể theo dõi nhanh tiến độ thực hiện của sinh viên thông qua bảng này?
3. Các trạng thái hiển thị của bài tập lớn trong danh sách bao gồm những gì?
4. Tính năng bộ lọc tự động và nút tạo mới hỗ trợ như thế nào trong việc quản lý khối lượng công việc?','843b9dc1-457d-4187-90ae-8144d690f77b'::uuid,NULL,NULL,247,331,'2026-03-21 13:35:13.368497+07'),
	 ('be8154b8-49dd-4798-a272-5b269b1cfba9'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,247,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
B.2.2.3
Chi tiết bài tập lớn
Hình B.22: Chi tiết bài tập lớn
Đặc điểm chính:
• Hiển thịtài liệu đính kèm trong bài, biểu tượng đểupload file mẫu và khu vực ghi
chú cho giáo viên.
• Trường nhận xét và điểm sốdành cho từng sinh viên giúp cập nhật phản hồi trực
tiếp sau khi chấm.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 229/254

=== SUMMARY ===
Đoạn văn bản này mô tả các tính năng chính của giao diện chi tiết bài tập lớn dành cho giáo viên. Hệ thống cho phép hiển thị tài liệu đính kèm, tải lên file mẫu và cung cấp khu vực ghi chú riêng. Đặc biệt, giáo viên có thể trực tiếp nhập điểm và nhận xét cho từng sinh viên ngay trên giao diện này, giúp tối ưu hóa quy trình chấm bài và phản hồi kết quả học tập một cách nhanh chóng và hiệu quả.

=== REVIEW QUESTIONS ===
1. Giao diện chi tiết bài tập lớn hỗ trợ giáo viên quản lý các loại tài liệu nào?
2. Mục đích của việc thiết kế khu vực ghi chú riêng cho giáo viên trong giao diện này là gì?
3. Giáo viên thực hiện việc phản hồi kết quả chấm bài cho sinh viên thông qua những công cụ nào?
4. Việc tích hợp trường nhận xét và điểm số trực tiếp trong giao diện bài tập mang lại lợi ích gì cho giáo viên?','2d27e42b-c4ea-4610-be9d-9b190f73d9c4'::uuid,NULL,NULL,248,320,'2026-03-21 13:35:13.368497+07'),
	 ('0c17dcc6-4479-4b54-9fb8-edb42dc1d672'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,248,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
B.2.2.4
Chi tiết bài tập lớn của sinh viên
Hình B.23: Chi tiết bài tập lớn của sinh viên
Đặc điểm chính:
• Giao diện cho phép xem từng file sinh viên gửi, nhập điểm cụthể, thêm nhận xét
và lưu lại đểcập nhật điểm sốchung.
• Danh sách file được hiển thịdạng thumbnail kèm menu thao tác giúp quản lý phiên
bản nộp bài.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 230/254

=== SUMMARY ===
Nội dung này giới thiệu giao diện quản lý chi tiết bài tập lớn của từng sinh viên. Hệ thống cho phép giảng viên xem các tệp tin đã nộp dưới dạng hình thu nhỏ (thumbnail), thực hiện chấm điểm, đưa ra nhận xét trực tiếp và quản lý các phiên bản bài nộp. Đây là công cụ hỗ trợ giảng viên theo dõi tiến độ và đánh giá kết quả học tập của sinh viên một cách chi tiết và có hệ thống.

=== REVIEW QUESTIONS ===
1. Giao diện chi tiết bài tập lớn cung cấp những tính năng nào để giảng viên đánh giá bài nộp của sinh viên?
2. Lợi ích của việc hiển thị danh sách file dưới dạng thumbnail kèm menu thao tác là gì?
3. Giảng viên cần thực hiện thao tác gì để cập nhật điểm số chung sau khi đã xem và nhận xét bài làm?
4. Làm thế nào để giảng viên có thể quản lý các phiên bản nộp bài khác nhau của cùng một sinh viên trong giao diện này?','69d7b438-dd81-4e9d-8448-498d0a3652f2'::uuid,NULL,NULL,249,334,'2026-03-21 13:35:13.368497+07'),
	 ('93266a66-92a5-4352-b1f1-6a353b63c61c'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,249,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
B.2.2.5
Danh sách sinh viên làm quiz
Hình B.24: Danh sách sinh viên làm quiz
Đặc điểm chính:
• Bảng quản lý hiển thị sinh viên, mã số, trạng thái nộp, điểm số và thời gian hoàn
thành, hỗ trợ theo dõi cá nhân hóa.
• Nút "Chi tiết"mở nhanh hồ sơ điểm quiz, giúp giáo viên tra cứu lịch sử nộp.
Báo cáo đồ án chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 231/254

=== SUMMARY ===
Phần này mô tả giao diện quản lý danh sách sinh viên thực hiện bài kiểm tra (quiz). Hệ thống cung cấp bảng dữ liệu chi tiết bao gồm thông tin định danh, trạng thái nộp bài, điểm số và thời gian hoàn thành để hỗ trợ giáo viên theo dõi sát sao tiến độ của từng cá nhân. Ngoài ra, tính năng truy cập nhanh qua nút ''Chi tiết'' cho phép giáo viên xem lại toàn bộ hồ sơ điểm và lịch sử nộp bài của sinh viên một cách thuận tiện.

=== REVIEW QUESTIONS ===
1. Bảng quản lý danh sách sinh viên làm quiz cung cấp những thông tin cụ thể nào về quá trình làm bài?
2. Lợi ích của việc hiển thị thời gian hoàn thành và trạng thái nộp bài đối với giáo viên là gì?
3. Chức năng của nút ''Chi tiết'' trong giao diện này được sử dụng để làm gì?
4. Làm thế nào giáo viên có thể tra cứu lịch sử nộp bài của một sinh viên cụ thể?','94f1f374-f579-47b2-a6d5-6f839d9f0b2a'::uuid,NULL,NULL,250,323,'2026-03-21 13:35:13.368497+07'),
	 ('94b5dfa7-fd27-4d5a-bb38-737c9d74f102'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,250,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
B.2.2.6
Diễn đàn lớp học
Hình B.25: Diễn đàn lớp học
Đặc điểm chính:
• Sidebar quản lý thread với trạng thái New/Top/Hot giúp giáo viên theo dõi mức độ
tương tác.
• Danh sách bài viết cho phép thao tác theo dõi/bỏtheo dõi, và mỗi bài có nút menu
đểmởrộng tùy chọn (theo dõi, bật thông báo).
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 232/254

=== SUMMARY ===
Nội dung này trình bày về giao diện và chức năng của Diễn đàn lớp học trong hệ thống quản lý học tập. Các tính năng cốt lõi bao gồm thanh Sidebar phân loại luồng thảo luận theo trạng thái (Mới, Nổi bật, Nóng) giúp giảng viên đánh giá mức độ tương tác. Ngoài ra, hệ thống hỗ trợ người dùng quản lý bài viết linh hoạt thông qua các tùy chọn theo dõi, bỏ theo dõi và tùy chỉnh thông báo qua menu mở rộng, tạo điều kiện thuận lợi cho việc theo dõi thảo luận.

=== REVIEW QUESTIONS ===
1. Thanh sidebar của diễn đàn lớp học hỗ trợ quản lý các luồng thảo luận theo những trạng thái nào?
2. Lợi ích của việc hiển thị trạng thái New/Top/Hot đối với giáo viên là gì?
3. Người dùng có thể thực hiện những thao tác quản lý nào đối với danh sách bài viết?
4. Nút menu mở rộng trên mỗi bài viết cung cấp các tùy chọn bổ sung nào cho người dùng?','ace37d7e-f076-422e-b7de-1e619cd22b95'::uuid,NULL,NULL,251,329,'2026-03-21 13:35:13.368497+07');
INSERT INTO coaching_chatbot.lecture_knowledge_chunks (lecture_knowledge_chunks_id,lecture_knowledge_id,chunk_index,chunk_content,qdrant_point_id,start_time_seconds,end_time_seconds,page_number,token_count,created_at) VALUES
	 ('137bec78-057d-4e84-b503-d232bfd37b2c'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,251,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
B.2.2.7
Popup theo dõi diễn đàn
Hình B.26: Popup theo dõi diễn đàn
Đặc điểm chính:
• Popup cung cấp bộlọc theo chủđề/đã theo dõi đểbật/tắt thông báo mà không cần
rời trang hiện tại.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 233/254

=== SUMMARY ===
Đoạn văn này mô tả tính năng ''Popup theo dõi diễn đàn'' trong hệ thống quản lý lớp học. Tính năng này cung cấp các bộ lọc theo chủ đề hoặc trạng thái đã theo dõi, cho phép người dùng quản lý việc bật/tắt thông báo một cách nhanh chóng. Điểm mạnh của thiết kế này là cho phép thực hiện các thao tác trực tiếp trên popup mà không cần tải lại hoặc rời khỏi trang hiện tại, giúp tối ưu hóa trải nghiệm quản lý tương tác trên diễn đàn.

=== REVIEW QUESTIONS ===
1. Mục đích chính của Popup theo dõi diễn đàn là gì?
2. Người dùng có thể thực hiện bộ lọc dựa trên những tiêu chí nào trong popup này?
3. Ưu điểm của việc quản lý thông báo thông qua popup so với việc chuyển trang là gì?
4. Tính năng bật/tắt thông báo trong popup này giúp ích gì cho người dùng (ví dụ: giáo viên)?','b469c2f5-a885-4b98-a18f-073e3487e6af'::uuid,NULL,NULL,252,287,'2026-03-21 13:35:13.368497+07'),
	 ('58e70aff-61d9-439e-9832-2ff18b2d6a16'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,252,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
B.2.2.8
Topic diễn đàn cụthể
Hình B.27: Topic diễn đàn cụthể
Đặc điểm chính:
• Giao diện hiển thịluồng trao đổi chi tiết, kèm hình đại diện, thời gian, tag, biểu
tượng xem và bình luận.
• Tính năng thêm phản hồi và gợi ý theo dõi topic trên thanh điều hướng trái thuận
tiện xửlý thảo luận.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 234/254

=== SUMMARY ===
Phần này giới thiệu về giao diện chi tiết của một chủ đề thảo luận trong diễn đàn lớp học. Hệ thống hiển thị luồng trao đổi trực quan với đầy đủ thông tin như hình đại diện, thời gian, thẻ phân loại và các biểu tượng tương tác (lượt xem, bình luận). Ngoài ra, giao diện còn tích hợp các tiện ích giúp người dùng dễ dàng phản hồi và theo dõi chủ đề thông qua thanh điều hướng, tối ưu hóa quá trình quản lý và tham gia thảo luận.

=== REVIEW QUESTIONS ===
1. Giao diện topic diễn đàn cụ thể cung cấp những thông tin chi tiết nào đi kèm với luồng trao đổi?
2. Người dùng có thể thực hiện những thao tác tương tác nào trực tiếp trên giao diện này?
3. Tính năng gợi ý theo dõi topic được bố trí ở đâu và có tác dụng gì?
4. Các yếu tố như hình đại diện, thời gian và tag hỗ trợ gì cho giáo viên và sinh viên khi sử dụng diễn đàn?','7532b872-0bb1-47c1-9d8a-09d0eaa59d35'::uuid,NULL,NULL,253,327,'2026-03-21 13:35:13.368497+07'),
	 ('bc46adb0-2522-4d90-abf1-af0f74ccec71'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,253,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
B.2.2.9
Thêm topic diễn đàn
Hình B.28: Thêm topic diễn đàn
Đặc điểm chính:
• Form tạo topic có trường chọn danh mục, tiêu đề, nội dung và thao tác đính kèm
ảnh hỗtrợgiáo viên khởi tạo chủđềchi tiết.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 235/254

=== SUMMARY ===
Đoạn văn bản giới thiệu tính năng ''Thêm topic diễn đàn'' trong hệ thống quản lý học tập. Tính năng này cung cấp một biểu mẫu chi tiết cho phép giáo viên khởi tạo các chủ đề thảo luận mới bằng cách chọn danh mục, đặt tiêu đề, soạn thảo nội dung và đính kèm hình ảnh minh họa. Mục tiêu là giúp giáo viên dễ dàng tạo ra các luồng trao đổi phong phú và cụ thể để hỗ trợ sinh viên trong quá trình học tập.

=== REVIEW QUESTIONS ===
1. Biểu mẫu (form) tạo topic diễn đàn bao gồm những trường thông tin chính nào?
2. Tính năng đính kèm ảnh trong form tạo topic có tác dụng gì đối với giáo viên?
3. Tính năng này được thiết kế để hỗ trợ đối tượng người dùng nào trong việc khởi tạo chủ đề?
4. Làm thế nào để giáo viên có thể phân loại chủ đề ngay khi tạo mới trên diễn đàn?','a209ce14-a48d-418b-8746-a2940b67f15e'::uuid,NULL,NULL,254,289,'2026-03-21 13:35:13.368497+07'),
	 ('1adba2c8-ece8-406f-bdce-533fa2de35bf'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,254,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
B.2.3
Thời khóa biểu
B.2.3.1
Lịch tuần biểu
Hình B.29: Lịch tuần biểu chưa đồng bộ
Đặc điểm chính:
• Lưới tuần biểu kết hợp lịch mini và danh sách sựkiện giúp giáo viên lập kếhoạch
buổi học và buổi hướng dẫn.
• Nút đồng bộvới Calendar và popover sựkiện (phòng học, giảng viên, trạng thái) hỗ
trợcập nhật liên tục.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 236/254

=== SUMMARY ===
Phần nội dung này giới thiệu về giao diện Thời khóa biểu, trọng tâm là Lịch tuần biểu dành cho giáo viên. Hệ thống tích hợp lưới tuần biểu cùng lịch mini và danh sách sự kiện để tối ưu hóa việc lập kế hoạch giảng dạy và hướng dẫn. Các tính năng quan trọng bao gồm khả năng đồng bộ hóa với Calendar và các cửa sổ hiển thị thông tin chi tiết (popover) về phòng học, giảng viên và trạng thái, giúp người dùng cập nhật thông tin lịch trình một cách nhanh chóng và chính xác.

=== REVIEW QUESTIONS ===
1. Lưới tuần biểu được kết hợp với những thành phần nào để hỗ trợ giáo viên lập kế hoạch?
2. Tính năng đồng bộ hóa của lịch tuần biểu hỗ trợ kết nối với ứng dụng nào?
3. Những thông tin chi tiết nào được hiển thị trong popover sự kiện?
4. Mục đích của việc tích hợp danh sách sự kiện và lịch mini vào giao diện tuần biểu là gì?','fc8f359e-0e4c-45b2-bc6e-7e77a0df1ac3'::uuid,NULL,NULL,255,333,'2026-03-21 13:35:13.368497+07'),
	 ('4d1b4d44-53ac-4524-b27a-4c9209763f8b'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,255,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
B.2.3.2
Lịch học kỳ
Hình B.30: Lịch học kỳ
Đặc điểm chính:
• Bảng lịch học kỳđềxuất học kỳ, tên môn, tín chỉ, ca học, thời gian và phòng học
đểtheo dõi toàn diện lịch giảng dạy.
• Bộlọc học kỳ, danh sách sựkiện tuần và các trạng thái hoàn thành giúp theo dõi
việc phụtrách các lớp.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 237/254

=== SUMMARY ===
Đoạn văn bản mô tả chức năng ''Lịch học kỳ'' trong hệ thống quản lý học tập dành cho giáo viên. Chức năng này cung cấp cái nhìn tổng quan về lịch giảng dạy bao gồm học kỳ, tên môn, tín chỉ, ca học và phòng học. Ngoài ra, hệ thống còn tích hợp bộ lọc học kỳ, danh sách sự kiện tuần và trạng thái hoàn thành giúp giảng viên theo dõi và quản lý việc phụ trách các lớp học một cách khoa học và toàn diện.

=== REVIEW QUESTIONS ===
1. Chức năng ''Lịch học kỳ'' cung cấp những thông tin chi tiết nào để hỗ trợ giảng viên theo dõi lịch giảng dạy?
2. Lợi ích của việc tích hợp bộ lọc học kỳ trong giao diện quản lý lịch học là gì?
3. Những yếu tố nào giúp giáo viên theo dõi trạng thái và tiến độ phụ trách các lớp học?
4. Danh sách sự kiện tuần hỗ trợ như thế nào trong việc quản lý lịch học kỳ?','84cecf8e-4c8e-492c-ae0f-820ed8bf84a2'::uuid,NULL,NULL,256,315,'2026-03-21 13:35:13.370006+07'),
	 ('daedbf29-21b3-4343-aa30-456653e8d16f'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,260,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
B.2.5
Cấu hình điểm số
B.2.5.1
Cấu hình điểm số
Hình B.35: Cấu hình điểm số
Đặc điểm chính:
• Bảng cấu hình nêu tên điểm/thứtựvà nhập khối lượng điểm cho từng hạng mục
(bài tập, thi, tổng) trước khi tạo khóa học.
B.3
Giao diện admin
Phần phụlục này trình bày các giao diện người dùng đại trà và tiêu chuẩn của hệ
thống LMS HCMUT dành cho admin. Các giao diện này bao gồm các chức năng cơ bản
và phổbiến trong các hệthống quản lý học tập hiện đại, được thiết kếđểđảm bảo tính
nhất quán và dễsửdụng.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 242/254

=== SUMMARY ===
Đoạn văn bản này giới thiệu về chức năng cấu hình điểm số và tổng quan giao diện dành cho quản trị viên (Admin) trên hệ thống LMS HCMUT. Người dùng có thể thiết lập trọng số điểm cho bài tập, bài thi và tổng điểm trước khi tạo khóa học. Giao diện Admin được thiết kế theo tiêu chuẩn hiện đại, đảm bảo tính nhất quán và dễ dàng thực hiện các chức năng quản lý học tập cơ bản.

=== REVIEW QUESTIONS ===
1. Người dùng cần thực hiện việc cấu hình điểm số vào thời điểm nào trong quy trình tạo khóa học?
2. Các hạng mục điểm số nào có thể được nhập khối lượng điểm trong bảng cấu hình?
3. Mục tiêu thiết kế chính của giao diện Admin trong hệ thống LMS HCMUT là gì?
4. Phần phụ lục B.3 trình bày nội dung gì liên quan đến các giao diện người dùng của hệ thống?','499af237-f055-41ba-8a8d-8c9e2d90ce38'::uuid,NULL,NULL,261,362,'2026-03-21 13:35:13.371031+07'),
	 ('f37f4b98-90ec-45ac-8f36-8b3524262b5a'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,261,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
B.3.1
Quản lý người dùng
B.3.1.1
Popup thêm mới người dùng và chỉnh sửa người dùng
Hình B.36: Giao diện thêm mới người dùng
Hình B.37: Giao diện chỉnh sửa người dùng
• Form nhập nhanh: Họtên, Email, Tên đăng nhập, Vai trò, Mật khẩu.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 243/254

=== SUMMARY ===
Đoạn văn bản này giới thiệu về chức năng quản lý người dùng trong giao diện admin của hệ thống LMS. Cụ thể, tài liệu mô tả các popup dùng để thêm mới và chỉnh sửa thông tin người dùng. Biểu mẫu này được thiết kế để nhập dữ liệu nhanh chóng, bao gồm các thông tin cơ bản như họ tên, email, tên đăng nhập, vai trò và mật khẩu, giúp admin quản lý danh sách người dùng một cách hiệu quả.

=== REVIEW QUESTIONS ===
1. Chức năng chính được mô tả trong mục B.3.1 là gì?
2. Biểu mẫu nhập nhanh người dùng bao gồm những trường thông tin nào?
3. Mục B.3.1.1 đề cập đến hai loại giao diện popup nào?
4. Giao diện quản lý người dùng này được thiết kế dành cho đối tượng nào trong hệ thống?','3a4b86be-0a0a-403b-aa06-045fbb56a5aa'::uuid,NULL,NULL,262,276,'2026-03-21 13:35:13.371031+07'),
	 ('cd64bf84-675f-47c9-8762-d793427e70bf'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,262,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
• Có tuỳchọn tạo mật khẩu tựđộng đểAdmin thao tác nhanh hơn.
• Có tuỳchọn gửi email chào mừng cho người dùng mới.
• Nút Hủy / Lưu rõ ràng, kèm nút đóng (X).
• Giao diện chỉnh sửa tương tựgiao diện thêm mới, nhưng có thêm tuỳchọn Đặt lại
mật khẩu.
• Nút Hủy / Lưu giúp thao tác nhanh, tránh nhầm.
B.3.1.2
Import người dùng
Hình B.38: Giao diện import người dùng
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 244/254

=== SUMMARY ===
Nội dung tập trung vào các tính năng quản lý người dùng dành cho Admin trong hệ thống LMS. Các chức năng chính bao gồm tạo mật khẩu tự động, gửi email chào mừng, và giao diện chỉnh sửa người dùng tích hợp nút đặt lại mật khẩu. Hệ thống chú trọng vào trải nghiệm người dùng với các nút điều hướng rõ ràng (Lưu, Hủy, Đóng) và cung cấp phương thức nhập liệu hàng loạt thông qua tính năng Import người dùng để tối ưu hóa quy trình quản trị.

=== REVIEW QUESTIONS ===
1. Những tùy chọn nào giúp Admin tiết kiệm thời gian khi tạo tài khoản người dùng mới?
2. Điểm khác biệt tiêu biểu giữa giao diện chỉnh sửa người dùng và giao diện thêm mới là gì?
3. Hệ thống hỗ trợ các nút chức năng nào để đảm bảo thao tác quản trị chính xác và tránh nhầm lẫn?
4. Chức năng được đề cập trong mục B.3.1.2 dùng để giải quyết vấn đề gì trong quản lý người dùng?','9eff11a3-f892-4d7b-929e-bf4e03d64667'::uuid,NULL,NULL,263,349,'2026-03-21 13:35:13.371031+07'),
	 ('d6ea2b3b-4e4e-423f-80ef-4da0b240d135'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,263,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Hình B.39: Giao diện import người dùng với file đã tải lên
Đặc điểm chính:
• Quy trình 3 bước rõ ràng: tải file mẫu   upload file dữliệu   xem trước & xác
nhận.
• Sau khi kiểm tra, hệthống báo ngay: tạo mới bao nhiêu, cập nhật bao nhiêu, lỗi
dòng nào.
• Có lựa chọn xửlý trùng:
• Bỏqua nếu đã tồn tại
• Cập nhật nếu đã tồn tại
• Nút Import đểthực hiện và Hủy đểthoát.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 245/254

=== SUMMARY ===
Đoạn văn bản mô tả chi tiết giao diện và quy trình 3 bước để import người dùng từ file dữ liệu vào hệ thống. Quy trình bao gồm việc tải file mẫu, tải lên dữ liệu thực tế và xem trước để xác nhận. Hệ thống hỗ trợ kiểm tra lỗi logic, báo cáo số lượng dòng tạo mới hoặc cập nhật, đồng thời cho phép người dùng linh hoạt lựa chọn cách xử lý khi dữ liệu bị trùng lặp (bỏ qua hoặc ghi đè).

=== REVIEW QUESTIONS ===
1. Quy trình import người dùng trong hệ thống gồm những bước cụ thể nào?
2. Hệ thống sẽ hiển thị những thông tin gì sau khi thực hiện kiểm tra file dữ liệu tải lên?
3. Khi gặp dữ liệu người dùng đã tồn tại (trùng lặp), người quản trị có những phương án xử lý nào?
4. Mục đích của nút ''Import'' và nút ''Hủy'' trong giao diện này là gì?','ca37edbb-f788-487d-b7ca-1552af1424a0'::uuid,NULL,NULL,264,326,'2026-03-21 13:35:13.371031+07'),
	 ('c7783d0b-b973-46d0-a055-d9439cb16ff8'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,265,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Hình B.41: Giao diện popup chọn giảng viên
Đặc điểm chính:
• Có ô tìm kiếm theo tên đểtìm giảng viên nhanh.
• Lọc theo Khoa (dropdown + danh sách khoa dạng nút chọn) giúp thu hẹp kết quả.
• Danh sách giảng viên hiển thịdạng thẻ(avatar, tên, khoa) và chọn bằng radio (chọn
1 người).
• Có phân trang đểduyệt danh sách dài.
• Nút Lưu và tiếp tục đểxác nhận lựa chọn.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 247/254

=== SUMMARY ===
Đoạn văn mô tả chi tiết các đặc điểm của giao diện popup chọn giảng viên trong hệ thống quản lý đào tạo. Giao diện tập trung vào trải nghiệm người dùng với các tính năng tìm kiếm theo tên, bộ lọc khoa linh hoạt và hiển thị thông tin dạng thẻ trực quan. Hệ thống giới hạn lựa chọn duy nhất một giảng viên thông qua nút radio, đồng thời hỗ trợ phân trang và nút xác nhận quy trình, giúp việc quản trị danh sách giảng viên lớn trở nên hiệu quả và chính xác.

=== REVIEW QUESTIONS ===
1. Giao diện popup chọn giảng viên cung cấp những công cụ nào để người dùng tìm kiếm và thu hẹp kết quả?
2. Danh sách giảng viên được hiển thị dưới hình thức nào và bao gồm những thông tin cơ bản gì?
3. Tại sao giao diện lại sử dụng nút chọn dạng radio cho danh sách giảng viên thay vì checkbox?
4. Tính năng phân trang trong giao diện này có vai trò gì đối với trải nghiệm người dùng?','3310b1c3-1464-4ff3-8643-105366d28dd0'::uuid,NULL,NULL,266,356,'2026-03-21 13:35:13.371031+07');
INSERT INTO coaching_chatbot.lecture_knowledge_chunks (lecture_knowledge_chunks_id,lecture_knowledge_id,chunk_index,chunk_content,qdrant_point_id,start_time_seconds,end_time_seconds,page_number,token_count,created_at) VALUES
	 ('8c449428-eac8-4d13-bb78-a32c4618634f'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,266,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
B.3.3
Quản lý thông báo
B.3.3.1
Danh sách thông báo
Hình B.42: Giao diện danh sách thông báo
Đặc điểm chính:
• Có nút Thêm mới đểtạo thông báo nhanh.
• Hỗtrợtìm kiếm và lọc theo vai trò / trạng thái đểxem đúng nhóm cần quản lý.
• Bảng hiển thịrõ các thông tin quan trọng: tiêu đề, người gửi, đối tượng nhận, kênh
gửi, trạng thái, thời gian gửi/lên lịch.
• Kênh gửi được thểhiện bằng icon (thông báo hệthống / email) nên nhìn là hiểu.
• Có phân trang và chọn sốdòng mỗi trang, phù hợp khi danh sách dài.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 248/254

=== SUMMARY ===
Đoạn văn bản mô tả giao diện quản lý danh sách thông báo trong hệ thống quản lý học tập. Các tính năng chính bao gồm tạo mới thông báo, tìm kiếm và lọc theo vai trò hoặc trạng thái. Bảng dữ liệu hiển thị chi tiết về tiêu đề, người gửi, người nhận, kênh gửi (dưới dạng biểu tượng), trạng thái và thời gian. Ngoài ra, giao diện còn hỗ trợ phân trang và tùy chỉnh số lượng dòng hiển thị để tối ưu hóa trải nghiệm người dùng khi quản lý danh sách lớn.

=== REVIEW QUESTIONS ===
1. Những tiêu chí nào có thể được sử dụng để tìm kiếm và lọc danh sách thông báo trong giao diện này?
2. Bảng hiển thị danh sách thông báo cung cấp những thông tin quan trọng nào cho người quản trị?
3. Làm thế nào để người dùng nhận biết nhanh phương thức gửi thông báo (hệ thống hay email) trên giao diện?
4. Tính năng nào được thiết kế để hỗ trợ người dùng khi số lượng thông báo trong danh sách trở nên quá dài?','a460003f-0359-4fdc-a0cd-2c4572826c5b'::uuid,NULL,NULL,267,396,'2026-03-21 13:35:13.371031+07'),
	 ('974c2823-e430-4974-80cd-dc95eaa94198'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,267,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
B.3.3.2
Tạo mới thông báo
Hình B.43: Giao diện tạo mới thông báo
Đặc điểm chính:
• Thiết kếtheo từng bước rõ ràng: chọn người nhận   soạn nội dung   chọn kênh
gửi   chọn thời điểm gửi.
• Chọn người nhận linh hoạt: tất cả, theo sinh viên/giảng viên, hoặc lọc theo ngành
học / khóa học.
• Soạn nội dung gồm tiêu đề(có giới hạn ký tự) và nội dung; có thêm mục mẫu có
sẵn đểsoạn nhanh.
• Chọn kênh gửi: thông báo trên hệthống và/hoặc gửi email.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 249/254

=== SUMMARY ===
Đoạn văn bản mô tả chi tiết quy trình và các tính năng của giao diện ''Tạo mới thông báo'' trong hệ thống quản lý. Quy trình được thiết kế theo các bước logic từ chọn đối tượng nhận (sinh viên, giảng viên, ngành học), soạn thảo nội dung (hỗ trợ mẫu có sẵn), đến việc lựa chọn kênh gửi (hệ thống, email) và thiết lập thời gian. Thiết kế này giúp người quản trị thực hiện việc thông báo một cách khoa học, chính xác và nhanh chóng.

=== REVIEW QUESTIONS ===
1. Quy trình tạo mới một thông báo bao gồm những bước cụ thể nào?
2. Hệ thống cung cấp những tùy chọn linh hoạt nào để lọc đối tượng nhận thông báo?
3. Trong phần soạn thảo nội dung, người dùng có thể làm gì để tăng tốc độ soạn thông báo?
4. Người quản trị có thể gửi thông báo qua những kênh truyền thông nào?','09570018-b060-4d4f-9893-891ed5aa533d'::uuid,NULL,NULL,268,350,'2026-03-21 13:35:13.371031+07'),
	 ('c451d9b7-ddd3-4f19-8801-8eef004a6960'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,268,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
• Tuỳchọn gửi ngay hoặc lên lịch, kèm chọn ngày gửi.
• Có nút Lưu nháp và Gửi đểkiểm soát tốt trước khi phát hành.
B.3.4
Quản trịhệthống
B.3.4.1
Nhật ký hệthống (Danh sách log)
Hình B.44: Giao diện nhật ký hệthống
Đặc điểm chính:
• Hiển thịlịch sửthao tác đểtheo dõi và truy vết khi có vấn đề.
• Có ô tìm kiếm nhanh theo tên/email/tên đăng nhập.
• Bộlọc theo thời gian và loại hành động, giúp khoanh vùng log cần xem.
• Nút Tải xuống đểxuất dữliệu phục vụbáo cáo/đối soát.
• Bảng log thểhiện rõ: Thời gian – Người dùng – Đối tượng – Hành động – Địa chỉ
IP, có phân trang.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 250/254

=== SUMMARY ===
Đoạn văn bản mô tả chức năng quản trị nhật ký hệ thống (log) trong một báo cáo đồ án chuyên ngành Công nghệ thông tin. Nội dung tập trung vào các tính năng như theo dõi lịch sử thao tác, truy vết sự cố, tìm kiếm nhanh, lọc dữ liệu và xuất báo cáo. Hệ thống log lưu trữ chi tiết về thời gian, người thực hiện, đối tượng bị tác động, hành động cụ thể và địa chỉ IP để phục vụ công tác quản lý và đối soát.

=== REVIEW QUESTIONS ===
1. Mục đích chính của việc hiển thị lịch sử thao tác trong nhật ký hệ thống là gì?
2. Người quản trị có thể tìm kiếm nhanh các bản ghi log dựa trên những thông tin nào?
3. Bảng dữ liệu nhật ký hệ thống bao gồm những trường thông tin cụ thể nào để hỗ trợ truy vết?
4. Tính năng ''Tải xuống'' trong giao diện nhật ký hệ thống phục vụ cho các hoạt động nghiệp vụ nào?','31b02ec9-8abf-4573-84ec-077e6d702066'::uuid,NULL,NULL,269,390,'2026-03-21 13:35:13.371031+07'),
	 ('815d30c2-64c9-482e-897f-64de6cb37a7b'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,269,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
Hình B.45: Giao diện popup chi tiết nhật ký hệthống
Đặc điểm chính:
• Hiển thịđầy đủthông tin của một bản ghi: thời gian, người thực hiện, hành động,
IP, đối tượng bịtác động.
• Có phần so sánh dữliệu trước / sau khi thay đổi (dạng JSON) đểnhìn rõ nội dung
đã chỉnh.
• Nút đóng (X) và thao tác Hủy / Lưu giúp kiểm soát thao tác (nếu có chỉnh sửa/ghi
nhận thêm).
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 251/254

=== SUMMARY ===
Đoạn văn mô tả các đặc điểm chính của giao diện popup chi tiết nhật ký hệ thống trong một báo cáo đồ án chuyên ngành. Giao diện này cho phép người dùng xem toàn diện thông tin bản ghi như thời gian, người thực hiện, IP và đối tượng tác động. Điểm quan trọng là khả năng so sánh dữ liệu trước và sau khi thay đổi dưới dạng JSON cùng các nút điều hướng để kiểm soát thao tác chỉnh sửa hoặc ghi nhận.

=== REVIEW QUESTIONS ===
1. Giao diện popup chi tiết nhật ký hệ thống hiển thị những thông tin cụ thể nào của một bản ghi?
2. Định dạng dữ liệu nào được sử dụng để so sánh nội dung trước và sau khi thay đổi?
3. Các nút điều hướng (Đóng, Hủy, Lưu) có vai trò gì trong giao diện popup này?
4. Mục đích của việc hiển thị địa chỉ IP và đối tượng bị tác động trong nhật ký hệ thống là gì?','8e01ee4e-cda2-493b-9c41-d8611cd99531'::uuid,NULL,NULL,270,335,'2026-03-21 13:35:13.372031+07'),
	 ('cc86f2d6-6eb8-49c4-b015-8d274a4af037'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,270,'=== ORIGINAL CONTENT ===
Trường Đại Học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa Học & KỹThuật Máy Tính
Tài liệu tham khảo
[1] Coursera. Trang chủcoursera. [Online]. Truy cập tại: https://www.coursera.org
(Lần truy cập cuối: 03/12/2025).
[2] Udemy. Trang chủudemy. [Online]. Truy cập tại: https://www.udemy.com (Lần
truy cập cuối: 09/12/2025).
[3] LinkedIn Learning. Trang chủlinkedin learning. [Online]. Truy cập tại:
https://www.linkedin.com/learning/ (Lần truy cập cuối: 05/12/2025).
[4] P. Lewis, E. Perez, A. Piktus, F. Petroni, V. Karpukhin, N. Goyal, H. K¨uttler,
M. Lewis, W. Yih, T. Rockt¨aschel, S. Riedel, and D. Kiela, “Retrieval-augmented
generation for knowledge-intensive nlp tasks,” arXiv preprint arXiv:2005.11401,
2021.
[5] Capterra. Linkedin learning reviews and ratings 2025. [Online]. Truy cập
tại: https://www.capterra.com/p/161208/LinkedIn-Learning/ (Lần truy cập cuối:
02/12/2025).
[6] G2 Crowd, Inc. Linkedin learning reviews, ratings & features 2025. [Online]. Truy
cập tại: https://www.g2.com/products/linkedin-linkedin-learning/reviews (Lần truy
cập cuối: 12/12/2025).
[7] 200Lab. Kiến trúc phân lớp (layered architecture) trong phát triển phần mềm.
[Online]. Truy cập tại: https://200lab.io/blog/kien-truc-phan-lop/ (Lần truy cập
cuối: 11/12/2025).
[8] M. Richards and N. Ford, Fundamentals of Software Architecture: An Engineering
Approach.
O’Reilly Media, 2020.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 252/254

=== SUMMARY ===
Đoạn văn bản cung cấp danh mục tài liệu tham khảo cho báo cáo đồ án chuyên ngành (CO4029) tại Trường Đại học Bách khoa TP.HCM. Các nguồn tài liệu bao gồm các nền tảng học tập trực tuyến phổ biến (Coursera, Udemy, LinkedIn Learning), nghiên cứu khoa học về mô hình RAG (Retrieval-Augmented Generation), các trang đánh giá phần mềm uy tín và tài liệu chuyên sâu về kiến trúc phần mềm, cụ thể là kiến trúc phân lớp. Đây là những cơ sở lý thuyết và thực tiễn quan trọng cho lĩnh vực Kỹ thuật Máy tính.

=== REVIEW QUESTIONS ===
1. Những nền tảng học tập trực tuyến nào đã được trích dẫn trong danh mục tài liệu tham khảo?
2. Bài báo khoa học ở mục [4] tập trung nghiên cứu về công nghệ gì trong lĩnh vực xử lý ngôn ngữ tự nhiên (NLP)?
3. Tài liệu số [7] và [8] cung cấp kiến thức về khía cạnh nào trong phát triển phần mềm?
4. Dựa vào thông tin cuối trang, danh mục tài liệu này thuộc về mã môn học nào và dành cho học kỳ nào?','bae0f88a-3b18-43d7-a845-f06ce38782fa'::uuid,NULL,NULL,271,601,'2026-03-21 13:35:13.372031+07'),
	 ('93b35516-ba34-4716-a9f3-a47b1f34e079'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,271,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
[9] GeeksforGeeks. Pipeline architecture in software engineering. [Online]. Truy cập
tại: https://www.geeksforgeeks.org/pipeline-architecture-in-software-engineering/
(Lần truy cập cuối: 13/12/2025).
[10] Microsoft
Azure.
Event-driven
architecture.
[Online].
Truy
cập
tại: https://learn.microsoft.com/en-us/azure/architecture/guide/architecture-styles/
event-driven (Lần truy cập cuối: 04/12/2025).
[11] IBM Cloud Education. Microservices architecture. [Online]. Truy cập tại:
https://www.ibm.com/cloud/learn/microservices (Lần truy cập cuối: 07/12/2025).
[12] S. Newman, Building Microservices: Designing Fine-Grained Systems.
O’Reilly
Media, 2015.
[13] A. S. Tanenbaum and M. V. Steen, Distributed Systems: Principles and Paradigms,
2nd ed.
Prentice Hall, 2007.
[14] J. Woodrow and C. Piech, “Soft grades: A calibrated and accurate method for
course-grade estimation that expresses uncertainty,” in LAK25: The 15th Interna-
tional Learning Analytics and Knowledge Conference (LAK 2025), Dublin, Ireland,
2025.
[15] W. N. van Wieringen. (2023) Lecture notes on ridge regression. [Online]. Truy cập
tại: https://arxiv.org/pdf/1509.09169 (Lần truy cập cuối: 10/12/2025).
[16] O*NET Resource Center. The O*NET content model. [Online]. Truy cập tại:
https://www.onetcenter.org/content.html (Lần truy cập cuối: 01/12/2025).
[17] Google AI for Developers. (2025) Embeddings | gemini api. [Online]. Truy
cập tại: https://ai.google.dev/gemini-api/docs/embeddings (Lần truy cập cuối:
08/12/2025).
[18] Google Cloud. (2025) Get text embeddings | generative ai on vertex ai.
[Online]. Truy cập tại: https://docs.cloud.google.com/vertex-ai/generative-ai/docs/
embeddings/get-text-embeddings (Lần truy cập cuối: 14/12/2025).
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 253/254

=== SUMMARY ===
Phần văn bản này là danh mục tài liệu tham khảo cho một báo cáo đồ án chuyên ngành thuộc Khoa Khoa học và Kỹ thuật Máy tính, Trường Đại học Bách khoa TP.HCM. Các tài liệu bao gồm các chủ đề về kiến trúc phần mềm (Pipeline, Event-driven, Microservices), hệ thống phân tán, các phương pháp hồi quy, mô hình nội dung O*NET và các công nghệ AI tiên tiến như văn bản nhúng (embeddings) từ Google API và Vertex AI.

=== REVIEW QUESTIONS ===
1. Danh mục tài liệu này đề cập đến những phong cách kiến trúc phần mềm nào?
2. Tài liệu số [17] và [18] tập trung vào công nghệ cụ thể nào của Google AI?
3. Cuốn sách của tác giả S. Newman (tài liệu [12]) thảo luận về chủ đề gì trong kỹ thuật phần mềm?
4. Dựa trên thông tin cuối trang, báo cáo này thuộc mã học phần nào và dành cho học kỳ mấy?','3729537c-cdad-40b5-ba60-94734a3d5956'::uuid,NULL,NULL,272,672,'2026-03-21 13:35:13.372031+07'),
	 ('5c4c6041-93ea-4d53-85c9-218a4b32c36c'::uuid,'febe6123-a925-44cf-a109-aefc7cf61420'::uuid,272,'=== ORIGINAL CONTENT ===
Trường Đại học Bách khoa - Đại học Quốc gia TP.HCM
Khoa Khoa học & Kỹthuật Máy tính
[19] Qdrant. Similarity search (nearest neighbors search). [Online]. Truy cập tại: https:
//qdrant.tech/documentation/concepts/search/ (Lần truy cập cuối: 09/12/2025).
[20] W. Xesquevixos, Software Architecture with Spring: Design Scalable and High-
Performance Java Applications with Spring.
Packt Publishing, 2024.
Báo cáo đồán chuyên ngành (CO4029) - HK-251 2025 - 2026
Trang 254/254

=== SUMMARY ===
Phân đoạn văn bản này cung cấp danh mục tài liệu tham khảo và thông tin định danh cho một báo cáo đồ án chuyên ngành tại Trường Đại học Bách khoa - ĐHQG TP.HCM. Nội dung tập trung vào hai nguồn tài liệu chính: công nghệ tìm kiếm tương tự (similarity search) với Qdrant và kiến trúc phần mềm sử dụng Spring Framework để phát triển ứng dụng Java hiệu năng cao. Đây là trang cuối của báo cáo thuộc học phần CO4029 trong học kỳ 251 (năm học 2025-2026).

=== REVIEW QUESTIONS ===
1. Tài liệu tham khảo số [19] đề cập đến khái niệm kỹ thuật nào trong hệ thống Qdrant?
2. Cuốn sách của tác giả W. Xesquevixos tập trung vào mục tiêu thiết kế ứng dụng Java như thế nào?
3. Văn bản này thuộc về học phần nào và được thực hiện tại khoa nào của Trường Đại học Bách khoa?
4. Thông tin về thời gian truy cập tài liệu trực tuyến trong mục [19] cho biết điều gì về tính cập nhật của báo cáo?','8a78ff6f-2115-4e6e-81d5-383e2956ff7f'::uuid,NULL,NULL,273,347,'2026-03-21 13:35:13.372031+07');
