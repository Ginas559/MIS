package vn.iotstar.coolenglish.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.servlet.http.Part;
import vn.iotstar.coolenglish.config.CloudinaryConfig;
import vn.iotstar.coolenglish.service.IImageUploadService;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Adapter Pattern implementation
 * Chuyển đổi Cloudinary SDK để thích hợp với interface IImageUploadService
 */
public class CloudinaryImageUploadAdapter implements IImageUploadService {

    private final Cloudinary cloudinary;
    private static final String UPLOAD_FOLDER = "coolenglish";

    public CloudinaryImageUploadAdapter() {
        this.cloudinary = CloudinaryConfig.getInstance();
    }

    @Override
    public String uploadImage(Part filePart) throws IOException {
        if (filePart == null || filePart.getSize() == 0) {
            throw new IllegalArgumentException("File upload không được để trống");
        }

        validateFileType(filePart);
        byte[] payload = toUploadPayload(filePart);

        @SuppressWarnings("unchecked")
        Map<String, Object> result = cloudinary.uploader().upload(
            payload,
            ObjectUtils.asMap(
                "folder", UPLOAD_FOLDER,
                "resource_type", "auto",
                "use_filename", true,
                "unique_filename", true
            )
        );

        return (String) result.get("secure_url");
    }

    @Override
    public String uploadImage(Part filePart, String publicId) throws IOException {
        if (filePart == null || filePart.getSize() == 0) {
            throw new IllegalArgumentException("File upload không được để trống");
        }

        if (publicId == null || publicId.trim().isEmpty()) {
            throw new IllegalArgumentException("Public ID không được để trống");
        }

        validateFileType(filePart);
        byte[] payload = toUploadPayload(filePart);

        @SuppressWarnings("unchecked")
        Map<String, Object> result = cloudinary.uploader().upload(
            payload,
            ObjectUtils.asMap(
                "folder", UPLOAD_FOLDER,
                "public_id", publicId,
                "resource_type", "auto",
                "overwrite", true,
                "unique_filename", false
            )
        );

        return (String) result.get("secure_url");
    }

    @Override
    public boolean deleteImage(String publicId) {
        if (publicId == null || publicId.trim().isEmpty()) {
            return false;
        }

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> result = cloudinary.uploader().destroy(
                UPLOAD_FOLDER + "/" + publicId,
                ObjectUtils.emptyMap()
            );
            
            String resultStatus = (String) result.get("result");
            return "ok".equals(resultStatus);
        } catch (Exception e) {
            return false;
        }
    }

    private byte[] toUploadPayload(Part filePart) throws IOException {
        try (InputStream inputStream = filePart.getInputStream()) {
            return inputStream.readAllBytes();
        }
    }

    /**
     * Validate loại file được upload
     */
    private void validateFileType(Part filePart) {
        String contentType = filePart.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Chỉ hỗ trợ upload ảnh");
        }

        long maxSize = 5 * 1024 * 1024; // 5MB
        if (filePart.getSize() > maxSize) {
            throw new IllegalArgumentException("Dung lượng ảnh không vượt quá 5MB");
        }
    }
}
