package com.hcmut.lms.coursemanagement.application.service;

import com.hcmut.lms.coursemanagement.application.dto.request.VideoTranscriptRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.VideoTranscriptResponse;

import java.util.List;
import java.util.UUID;

/**
 * Service để hỗ trợ tách transcript thành các đoạn theo thời gian
 * Giúp quản lý và tìm kiếm transcript một cách hiệu quả
 */
public interface VideoTranscriptSegmentService {

    /**
     * Tách một transcript dài thành nhiều đoạn nhỏ
     * @param videoLectureId ID của video lecture
     * @param fullTranscript Nội dung transcript đầy đủ
     * @param segmentDurationSeconds Độ dài của mỗi đoạn (tính bằng giây)
     * @param totalVideoDurationSeconds Tổng thời lượng video (tính bằng giây)
     * @return Danh sách các request để tạo transcript segments
     */
    List<VideoTranscriptRequest> createTranscriptSegments(
            UUID videoLectureId,
            String fullTranscript,
            Integer segmentDurationSeconds,
            Integer totalVideoDurationSeconds
    );

    /**
     * Lấy tất cả transcript segments của một video, sắp xếp theo thứ tự
     * @param videoLectureId ID của video lecture
     * @return Danh sách các segments sắp xếp theo segment index
     */
    List<VideoTranscriptResponse> getTranscriptSegments(UUID videoLectureId);

    /**
     * Tìm transcript segments trong một khoảng thời gian
     * @param videoLectureId ID của video lecture
     * @param startTimeSeconds Thời gian bắt đầu (giây)
     * @param endTimeSeconds Thời gian kết thúc (giây)
     * @return Danh sách segments trong khoảng thời gian đó
     */
    List<VideoTranscriptResponse> getTranscriptSegmentsInRange(
            UUID videoLectureId,
            Integer startTimeSeconds,
            Integer endTimeSeconds
    );

    /**
     * Ghép tất cả segments lại thành một transcript hoàn chỉnh
     * @param videoLectureId ID của video lecture
     * @return Transcript đầy đủ
     */
    String mergeTranscriptSegments(UUID videoLectureId);

    /**
     * Xóa tất cả segments của một video lecture
     * @param videoLectureId ID của video lecture
     */
    void deleteTranscriptSegments(UUID videoLectureId);
}
