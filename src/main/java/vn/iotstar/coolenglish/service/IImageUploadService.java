package vn.iotstar.coolenglish.service;

import jakarta.servlet.http.Part;
import java.io.IOException;

/**
 * Target Interface cho Adapter Pattern
 * Định nghĩa các phương thức chung để upload ảnh
 */
public interface IImageUploadService {
    
    /**
     * Upload ảnh lên cloud storage
     * @param filePart File từ form upload
     * @return URL của ảnh sau khi upload
     * @throws IOException nếu có lỗi khi đọc file
     */
    String uploadImage(Part filePart) throws IOException;
    
    /**
     * Upload ảnh và đặt public ID
     * @param filePart File từ form upload
     * @param publicId ID công khai để quản lý ảnh
     * @return URL của ảnh sau khi upload
     * @throws IOException nếu có lỗi khi đọc file
     */
    String uploadImage(Part filePart, String publicId) throws IOException;
    
    /**
     * Xóa ảnh từ cloud storage
     * @param publicId ID công khai của ảnh
     * @return true nếu xóa thành công
     */
    boolean deleteImage(String publicId);
}

