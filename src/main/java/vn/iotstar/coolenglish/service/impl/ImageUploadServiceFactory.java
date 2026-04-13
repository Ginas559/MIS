package vn.iotstar.coolenglish.service.impl;

import vn.iotstar.coolenglish.service.IImageUploadService;

/**
 * Factory Method Pattern
 * Quản lý việc tạo các service upload ảnh
 * Tập trung cấu hình và khởi tạo các service
 */
public class ImageUploadServiceFactory {
    
    private ImageUploadServiceFactory() {
        // Private constructor để tránh instantiation
    }

    /**
     * Tạo service upload ảnh dựa trên loại service
     * @param serviceType Loại service (CLOUDINARY, AWS_S3, GOOGLE_CLOUD, etc.)
     * @return IImageUploadService implementation
     */
    public static IImageUploadService createService(String serviceType) {
        if (serviceType == null || serviceType.trim().isEmpty()) {
            throw new IllegalArgumentException("Service type không được để trống");
        }

        return switch (serviceType.toUpperCase()) {
            case "CLOUDINARY" -> new CloudinaryImageUploadAdapter();
            // Các service khác có thể được thêm vào ở đây
            // case "AWS_S3" -> new AWSS3ImageUploadAdapter();
            // case "GOOGLE_CLOUD" -> new GoogleCloudImageUploadAdapter();
            default -> throw new IllegalArgumentException("Service type không được hỗ trợ: " + serviceType);
        };
    }

    /**
     * Tạo service upload ảnh mặc định (Cloudinary)
     * @return IImageUploadService implementation
     */
    public static IImageUploadService createDefaultService() {
        return createService("CLOUDINARY");
    }
}

