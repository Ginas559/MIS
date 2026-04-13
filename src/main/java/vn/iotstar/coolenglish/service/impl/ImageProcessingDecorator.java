package vn.iotstar.coolenglish.service.impl;

import jakarta.servlet.http.Part;
import vn.iotstar.coolenglish.service.IImageUploadService;

import java.io.IOException;

/**
 * Decorator Pattern
 * Thêm tính năng xử lý ảnh (resize, watermark, etc.) vào service upload
 * Có thể tắt bật linh hoạt mà không sửa code chính
 */
public class ImageProcessingDecorator implements IImageUploadService {
    
    protected IImageUploadService wrappedService;
    protected ImageProcessingConfig config;

    public ImageProcessingDecorator(IImageUploadService wrappedService) {
        this.wrappedService = wrappedService;
        this.config = new ImageProcessingConfig();
    }

    public ImageProcessingDecorator(IImageUploadService wrappedService, ImageProcessingConfig config) {
        this.wrappedService = wrappedService;
        this.config = config;
    }

    @Override
    public String uploadImage(Part filePart) throws IOException {
        return wrappedService.uploadImage(filePart);
    }

    @Override
    public String uploadImage(Part filePart, String publicId) throws IOException {
        return wrappedService.uploadImage(filePart, publicId);
    }

    @Override
    public boolean deleteImage(String publicId) {
        return wrappedService.deleteImage(publicId);
    }

    /**
     * Configuration class cho image processing
     */
    public static class ImageProcessingConfig {
        private boolean enableResize = true;
        private int resizeWidth = 200;
        private int resizeHeight = 200;
        private boolean enableWatermark = false;
        private String watermarkText = "CoolEnglish";
        private boolean enableCompression = true;
        private int compressionQuality = 80;

        // Getters and Setters
        public boolean isEnableResize() {
            return enableResize;
        }

        public void setEnableResize(boolean enableResize) {
            this.enableResize = enableResize;
        }

        public int getResizeWidth() {
            return resizeWidth;
        }

        public void setResizeWidth(int resizeWidth) {
            this.resizeWidth = resizeWidth;
        }

        public int getResizeHeight() {
            return resizeHeight;
        }

        public void setResizeHeight(int resizeHeight) {
            this.resizeHeight = resizeHeight;
        }

        public boolean isEnableWatermark() {
            return enableWatermark;
        }

        public void setEnableWatermark(boolean enableWatermark) {
            this.enableWatermark = enableWatermark;
        }

        public String getWatermarkText() {
            return watermarkText;
        }

        public void setWatermarkText(String watermarkText) {
            this.watermarkText = watermarkText;
        }

        public boolean isEnableCompression() {
            return enableCompression;
        }

        public void setEnableCompression(boolean enableCompression) {
            this.enableCompression = enableCompression;
        }

        public int getCompressionQuality() {
            return compressionQuality;
        }

        public void setCompressionQuality(int compressionQuality) {
            this.compressionQuality = compressionQuality;
        }
    }
}

